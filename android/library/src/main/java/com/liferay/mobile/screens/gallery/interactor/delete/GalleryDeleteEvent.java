package com.liferay.mobile.screens.gallery.interactor.delete;

import com.liferay.mobile.screens.base.interactor.BasicEvent;

/**
 * @author Víctor Galán Grande
 */
public class GalleryDeleteEvent extends BasicEvent {

	public GalleryDeleteEvent(int targetScreenletId, long imageEntryId) {
		super(targetScreenletId);

		this.imageEntryId = imageEntryId;
	}

	public GalleryDeleteEvent(int targetScreenletId, Exception exception) {
		super(targetScreenletId, exception);
	}

	public long getImageEntryId() {
		return imageEntryId;
	}

	private long imageEntryId;
}
