package com.liferay.mobile.screens.viewsets.defaultviews.comment.display;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.comment.display.CommentDisplayScreenlet;
import com.liferay.mobile.screens.comment.display.view.CommentDisplayViewModel;
import com.liferay.mobile.screens.models.CommentEntry;
import com.liferay.mobile.screens.userportrait.UserPortraitScreenlet;

/**
 * @author Alejandro Hernández
 */
public class CommentDisplayView extends FrameLayout implements CommentDisplayViewModel, View.OnClickListener {

	public CommentDisplayView(Context context) {
		super(context);
	}

	public CommentDisplayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CommentDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public CommentDisplayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void showFinishOperation(String actionName) {
		loadView();
	}

	@Override
	public void showFinishOperation(String loadCommentAction, CommentEntry commentEntry) {
		this.commentEntry = commentEntry;
		loadView();
	}

	@Override
	public void showStartOperation(String actionName) {
		progressBar.setVisibility(VISIBLE);
		contentGroup.setVisibility(GONE);
	}

	private void loadView() {
		progressBar.setVisibility(GONE);
		contentGroup.setVisibility(VISIBLE);

		if (commentEntry != null) {
			changeButtonBackgrounds(commentEntry);
			reloadButtons();
			loadCommentInView(commentEntry);
		}
	}

	private void loadCommentInView(CommentEntry commentEntry) {
		userPortraitScreenlet.setUserId(commentEntry.getUserId());
		//FIXME !
		//userPortraitScreenlet.load();
		userNameTextView.setText(commentEntry.getUserName());
		createDateTextView.setText(commentEntry.getCreateDateAsTimeSpan());
		editedTextView.setVisibility(commentEntry.getModifiedDate() != commentEntry.getCreateDate() ? VISIBLE : GONE);
		bodyTextView.setText(Html.fromHtml(commentEntry.getBody()).toString().replaceAll("\n", "").trim());
	}

	private void changeButtonBackgrounds(CommentEntry commentEntry) {

		boolean editable = commentEntry.isEditable();

		if (editable) {
			editImageButton.setVisibility(VISIBLE);
			editImageButton.setOnClickListener(this);
			deleteImageButton.setVisibility(VISIBLE);
			deleteImageButton.setOnClickListener(this);
		} else {
			editImageButton.setVisibility(GONE);
			deleteImageButton.setVisibility(GONE);
		}
	}

	private void reloadButtons() {

		if (editing && viewSwitcher.getCurrentView() != editBodyEditText) {
			viewSwitcher.showNext();
		} else if (!editing && viewSwitcher.getCurrentView() != bodyTextView) {
			viewSwitcher.showPrevious();
		}

		boolean inAction = editing || deleting;
		editImageButton.setImageResource(inAction ? R.drawable.default_ok : R.drawable.default_comment_edit);
		changeButtonBackgroundDrawable(editImageButton,
			inAction ? R.drawable.default_button_selector_green : R.drawable.default_button_selector);

		deleteImageButton.setImageResource(inAction ? R.drawable.default_cancel : R.drawable.default_comment_delete);
		changeButtonBackgroundDrawable(deleteImageButton,
			inAction ? R.drawable.default_button_selector_red : R.drawable.default_button_selector);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.comment_first_action) {
			if (editing) {
				String editedText = editBodyEditText.getText().toString();
				if (!editedText.equals(bodyTextView.getText())) {
					getScreenlet().performUserAction(CommentDisplayScreenlet.UPDATE_COMMENT_ACTION, editedText);
				}
			} else if (deleting) {
				getScreenlet().performUserAction(CommentDisplayScreenlet.DELETE_COMMENT_ACTION);
			} else {
				editBodyEditText.setText(bodyTextView.getText());
			}
			editing = !editing && !deleting;
			deleting = false;
			reloadButtons();
		} else if (v.getId() == R.id.comment_second_action) {
			deleting = !editing && !deleting;
			editing = false;
			reloadButtons();
		}
	}

	@Override
	public void showFailedOperation(String actionName, Exception e) {
		progressBar.setVisibility(GONE);
		contentGroup.setVisibility(VISIBLE);
	}

	private void changeButtonBackgroundDrawable(ImageButton button, int drawable) {
		Drawable drawableCompat = ContextCompat.getDrawable(getContext(), drawable);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			button.setBackground(drawableCompat);
		} else {
			button.setBackgroundDrawable(drawableCompat);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		userNameTextView = (TextView) findViewById(R.id.comment_user_name);
		bodyTextView = (TextView) findViewById(R.id.comment_body);
		userPortraitScreenlet = (UserPortraitScreenlet) findViewById(R.id.comment_user_portrait);
		createDateTextView = (TextView) findViewById(R.id.comment_create_date);
		editedTextView = (TextView) findViewById(R.id.comment_edited);
		editImageButton = (ImageButton) findViewById(R.id.comment_first_action);
		deleteImageButton = (ImageButton) findViewById(R.id.comment_second_action);
		editBodyEditText = (EditText) findViewById(R.id.comment_edit_body);
		viewSwitcher = (ViewSwitcher) findViewById(R.id.comment_view_switcher);
		progressBar = (ProgressBar) findViewById(R.id.liferay_progress);
		contentGroup = (ViewGroup) findViewById(R.id.comment_display_content);
	}

	@Override
	public BaseScreenlet getScreenlet() {
		return screenlet;
	}

	@Override
	public void setScreenlet(BaseScreenlet screenlet) {
		this.screenlet = screenlet;
	}

	private TextView userNameTextView;
	private TextView createDateTextView;
	private TextView bodyTextView;
	private TextView editedTextView;
	private UserPortraitScreenlet userPortraitScreenlet;
	private EditText editBodyEditText;

	private ImageButton editImageButton;
	private ImageButton deleteImageButton;
	private ViewSwitcher viewSwitcher;
	private boolean deleting;
	private boolean editing;

	private BaseScreenlet screenlet;
	private ViewGroup contentGroup;
	private ProgressBar progressBar;
	private CommentEntry commentEntry;
}
