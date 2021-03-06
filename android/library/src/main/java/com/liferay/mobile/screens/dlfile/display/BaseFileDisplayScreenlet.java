package com.liferay.mobile.screens.dlfile.display;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.asset.display.AssetDisplayListener;
import com.liferay.mobile.screens.asset.display.interactor.AssetDisplayInteractorImpl;
import com.liferay.mobile.screens.asset.list.AssetEntry;
import com.liferay.mobile.screens.base.BaseScreenlet;
import com.liferay.mobile.screens.context.SessionContext;

/**
 * @author Sarai Díaz García
 */
public abstract class BaseFileDisplayScreenlet
	extends BaseScreenlet<BaseFileDisplayViewModel, AssetDisplayInteractorImpl> implements AssetDisplayListener {

	public static final String LOAD_ASSET_ACTION = "LOAD_ASSET_ACTION";

	public static final String STATE_ENTRY_ID = "STATE_ENTRY_ID";
	public static final String STATE_FILE_ENTRY = "STATE_FILE_ENTRY";

	public BaseFileDisplayScreenlet(Context context) {
		super(context);
	}

	public BaseFileDisplayScreenlet(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseFileDisplayScreenlet(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public BaseFileDisplayScreenlet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void load() {
		performUserAction(LOAD_ASSET_ACTION);
	}

	public void loadFile() {
		onRetrieveAssetSuccess(fileEntry);
	}

	@Override
	protected View createScreenletView(Context context, AttributeSet attributes) {
		TypedArray typedArray =
			context.getTheme().obtainStyledAttributes(attributes, R.styleable.AssetDisplayScreenlet, 0, 0);

		int layoutId = typedArray.getResourceId(R.styleable.AssetDisplayScreenlet_layoutId, getDefaultLayoutId());

		autoLoad = typedArray.getBoolean(R.styleable.AssetDisplayScreenlet_autoLoad, true);
		entryId = typedArray.getInt(R.styleable.AssetDisplayScreenlet_entryId, 0);

		className = typedArray.getString(R.styleable.AssetDisplayScreenlet_className);
		classPK = typedArray.getInt(R.styleable.AssetDisplayScreenlet_classPK, 0);

		View view = LayoutInflater.from(context).inflate(layoutId, null);

		typedArray.recycle();

		return view;
	}

	@Override
	public void onRetrieveAssetSuccess(AssetEntry assetEntry) {
		fileEntry = (FileEntry) assetEntry;
		getViewModel().showFinishOperation(fileEntry);

		if (listener != null) {
			listener.onRetrieveAssetSuccess(assetEntry);
		}
	}

	@Override
	public void onRetrieveAssetFailure(Exception e) {
		getViewModel().showFailedOperation(null, e);

		if (listener != null) {
			listener.onRetrieveAssetFailure(e);
		}
	}

	@Override
	protected AssetDisplayInteractorImpl createInteractor(String actionName) {
		return new AssetDisplayInteractorImpl(this.getScreenletId());
	}

	@Override
	protected void onUserAction(String userActionName, AssetDisplayInteractorImpl interactor, Object... args) {
		switch (userActionName) {
			case LOAD_ASSET_ACTION:
				if (entryId != 0) {
					interactor.getAssetEntry(entryId);
				} else {
					interactor.getAssetEntry(className, classPK);
				}
		}
	}

	@Override
	protected void onScreenletAttached() {
		super.onScreenletAttached();

		if (autoLoad) {
			autoLoad();
		}
	}

	protected void autoLoad() {
		if (SessionContext.isLoggedIn()) {
			try {
				if (fileEntry == null || (className != null && classPK != 0)) {
					load();
				} else {
					loadFile();
				}
			} catch (Exception e) {
				onRetrieveAssetFailure(e);
			}
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		Bundle state = new Bundle();
		state.putParcelable(STATE_SUPER, superState);
		state.putLong(STATE_ENTRY_ID, entryId);
		state.putParcelable(STATE_FILE_ENTRY, fileEntry);
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable inState) {
		Bundle state = (Bundle) inState;

		entryId = state.getLong(STATE_ENTRY_ID);
		fileEntry = state.getParcelable(STATE_FILE_ENTRY);

		Parcelable superState = state.getParcelable(STATE_SUPER);
		super.onRestoreInstanceState(superState);
	}

	public long getEntryId() {
		return entryId;
	}

	public void setEntryId(long entryId) {
		this.entryId = entryId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getClassPK() {
		return classPK;
	}

	public void setClassPK(long classPK) {
		this.classPK = classPK;
	}

	public void setListener(AssetDisplayListener listener) {
		this.listener = listener;
	}

	public void setFileEntry(FileEntry fileEntry) {
		this.fileEntry = fileEntry;
	}

	public void setAutoLoad(boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	protected boolean autoLoad;
	protected long entryId;
	protected long classPK;
	protected String className;
	protected AssetDisplayListener listener;
	protected FileEntry fileEntry;
}
