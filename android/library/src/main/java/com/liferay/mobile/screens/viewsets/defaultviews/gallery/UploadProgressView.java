package com.liferay.mobile.screens.viewsets.defaultviews.gallery;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.context.LiferayScreensContext;
import com.liferay.mobile.screens.gallery.interactor.upload.CancelUploadEvent;
import com.liferay.mobile.screens.util.EventBusUtil;
import com.liferay.mobile.screens.util.LiferayLogger;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Víctor Galán Grande
 */
public class UploadProgressView extends RelativeLayout implements View.OnClickListener {
	public UploadProgressView(Context context) {
		super(context);
	}

	public UploadProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UploadProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public UploadProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void addUpload(String imagePath) {
		uploadCount++;
		images.add(imagePath);

		LiferayLogger.d("Image added" + imagePath);
		if (uploadCount == 1) {
			setImage(images.poll());
		}

		updateText();
	}

	public void uploadCompleteOrError() {
		setProgress(0);
		uploadCount--;
		updateText();
		if (uploadCount >= 1) {
			LiferayLogger.d("seteo imagen" + images.size());
			setImage(images.poll());
		} else {
			hide();
		}
	}

	public void setProgress(int progress) {
		progressBar.setProgress(progress);
	}

	public void setImage(String path) {
		Picasso.with(getContext()).load(new File(path)).fit().into(image);
		LiferayLogger.d("Image setted" + path);
	}

	@Override
	public void onClick(View v) {
		hide();
		EventBusUtil.post(new CancelUploadEvent());
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		initialize();
	}

	public void hide() {
		uploadCount = 0;
		images.clear();

		setVisibility(GONE);
	}

	private void updateText() {
		if (uploadCount == 1) {
			setOneUploadText();
		} else {
			setSeveralUploadText();
		}
	}

	private void setOneUploadText() {
		String text = LiferayScreensContext.getContext().getString(R.string.gallery_uploading_one);
		uploadText.setText(text);
	}

	private void setSeveralUploadText() {
		String text = LiferayScreensContext.getContext().getString(R.string.gallery_uploading_several, uploadCount);
		uploadText.setText(text);
	}

	private void initialize() {
		progressBar = (ProgressBar) findViewById(R.id.progress_view_progressbar);
		image = (ImageView) findViewById(R.id.progress_view_image);
		uploadText = (TextView) findViewById(R.id.default_progress_view_text);

		findViewById(R.id.progress_view_cancel_button).setOnClickListener(this);
	}

	private Queue<String> images = new PriorityQueue<>();
	private int uploadCount = 0;

	private ProgressBar progressBar;
	private TextView uploadText;
	private ImageView image;
}
