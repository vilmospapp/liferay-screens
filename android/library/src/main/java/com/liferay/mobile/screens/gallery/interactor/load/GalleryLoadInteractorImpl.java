package com.liferay.mobile.screens.gallery.interactor.load;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.liferay.mobile.android.service.JSONObjectWrapper;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.v7.dlapp.DLAppService;
import com.liferay.mobile.screens.base.list.interactor.BaseListCallback;
import com.liferay.mobile.screens.base.list.interactor.BaseListEvent;
import com.liferay.mobile.screens.base.list.interactor.BaseListInteractor;
import com.liferay.mobile.screens.cache.OfflinePolicy;
import com.liferay.mobile.screens.cache.tablecache.TableCache;
import com.liferay.mobile.screens.gallery.interactor.GalleryInteractorListener;
import com.liferay.mobile.screens.gallery.model.ImageEntry;
import com.liferay.mobile.screens.util.JSONUtil;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.liferay.mobile.screens.cache.DefaultCachedType.IMAGE_LIST;
import static com.liferay.mobile.screens.cache.DefaultCachedType.IMAGE_LIST_COUNT;

/**
 * @author Víctor Galán Grande
 */
public class GalleryLoadInteractorImpl extends BaseListInteractor<ImageEntry, GalleryInteractorListener>
	implements GalleryLoadInteractor {

	public GalleryLoadInteractorImpl(int targetScreenletId, OfflinePolicy offlinePolicy) {
		super(targetScreenletId, offlinePolicy);
	}

	@Override
	public void loadRows(long groupId, long folderId, String[] mimeTypes, int startRow, int endRow, Locale locale,
		String obcClassName) throws Exception {

		this.groupId = groupId;
		this.folderId = folderId;
		this.mimeTypes = getMimeTypes(mimeTypes);

		validate(groupId, folderId);

		processWithCache(startRow, endRow, locale, obcClassName);
	}

	@NonNull
	@Override
	protected ImageEntry getElement(TableCache tableCache) throws JSONException {
		return new ImageEntry(JSONUtil.toMap(new JSONObject(tableCache.getContent())));
	}

	@Override
	protected String getContent(ImageEntry imageEntry) {
		return new JSONObject(imageEntry.getValues()).toString();
	}

	@Override
	protected BaseListCallback<ImageEntry> getCallback(Pair<Integer, Integer> rowsRange, Locale locale) {
		return new GalleryLoadCallback(getTargetScreenletId(), rowsRange, locale);
	}

	@Override
	protected void getPageRowsRequest(Session session, int startRow, int endRow, Locale locale, JSONObjectWrapper obc)
		throws Exception {

		new DLAppService(session).getFileEntries(groupId, folderId, mimeTypes, startRow, endRow, obc);
	}

	@Override
	protected void getPageRowCountRequest(Session session) throws Exception {
		new DLAppService(session).getFileEntriesCount(groupId, folderId, mimeTypes);
	}

	@Override
	protected boolean cached(Object... args) throws Exception {

		int startRow = (int) args[0];
		int endRow = (int) args[1];
		Locale locale = (Locale) args[2];

		String id = String.valueOf(folderId);

		return recoverRows(id, IMAGE_LIST, IMAGE_LIST_COUNT, groupId, null, locale, startRow, endRow);
	}

	@Override
	protected void storeToCache(BaseListEvent event, Object... args) {

		String id = String.valueOf(folderId);

		storeRows(id, IMAGE_LIST, IMAGE_LIST_COUNT, groupId, null, event);
	}

	protected void validate(long groupId, long folderId) {
		if (groupId <= 0) {
			throw new IllegalArgumentException("groupId cannot be 0 or negative");
		}

		if (folderId < 0) {
			throw new IllegalArgumentException("groupId cannot be negative");
		}
	}

	private JSONArray getMimeTypes(String[] mimeTypes) {
		if (mimeTypes == null) {
			return DEFAULT_MIME_TYPES;
		}
		if (mimeTypes.length == 1 && mimeTypes[0].isEmpty()) {
			return DEFAULT_MIME_TYPES;
		}

		JSONArray jsonMimeTypes = new JSONArray();

		for (String mimeType : mimeTypes) {
			jsonMimeTypes.put(mimeType);
		}

		return jsonMimeTypes;
	}

	private static final JSONArray DEFAULT_MIME_TYPES = new JSONArray()
		.put("image/png")
		.put("image/jpeg")
		.put("image/gif");

	private long groupId;
	private long folderId;
	private JSONArray mimeTypes;
}
