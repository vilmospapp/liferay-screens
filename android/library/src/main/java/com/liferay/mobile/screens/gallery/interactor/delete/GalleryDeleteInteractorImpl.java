package com.liferay.mobile.screens.gallery.interactor.delete;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.v7.dlapp.DLAppService;
import com.liferay.mobile.screens.base.interactor.BaseRemoteInteractor;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.gallery.interactor.GalleryInteractorListener;

/**
 * @author Víctor Galán Grande
 */
public class GalleryDeleteInteractorImpl extends BaseRemoteInteractor<GalleryInteractorListener>
	implements GalleryDeleteInteractor {

	public GalleryDeleteInteractorImpl(int targetScreenletId) {
		super(targetScreenletId);
	}

	@Override
		_imageEntryId = imageEntryId;
		validate(imageEntryId);
		getDLAppService().deleteFileEntry(imageEntryId);
	public void deleteImageEntry(long imageEntryId) {
		try {
		} catch (Exception e) {
			getListener().onImageEntryDeleteFailure(e);
		}
	}

	public void onEvent(GalleryDeleteEvent event) {
		if (!isValidEvent(event)) {
			return;
		}

		if (event.isFailed()) {
			getListener().onImageEntryDeleteFailure(event.getException());
		} else {
			getListener().onImageEntryDeleted(event.getImageEntryId());
		}
	}

	private DLAppService getDLAppService() {
		Session session = SessionContext.createSessionFromCurrentSession();
		session.setCallback(new GalleryDeleteCallback(getTargetScreenletId(), _imageEntryId));

		return new DLAppService(session);
	}

	private void validate(long imageEntryId) {
		if (imageEntryId <= 0) {
			throw new IllegalArgumentException("Image entry Id must be greater than 0");
		}
	}

	private long _imageEntryId;
}
