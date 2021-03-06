/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.userportrait.interactor.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.screens.auth.login.connector.UserConnector;
import com.liferay.mobile.screens.base.interactor.BaseCachedRemoteInteractor;
import com.liferay.mobile.screens.cache.Cache;
import com.liferay.mobile.screens.cache.DefaultCachedType;
import com.liferay.mobile.screens.cache.OfflinePolicy;
import com.liferay.mobile.screens.cache.sql.CacheSQL;
import com.liferay.mobile.screens.cache.userportrait.UserPortraitCache;
import com.liferay.mobile.screens.context.LiferayScreensContext;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.userportrait.interactor.UserPortraitInteractorListener;
import com.liferay.mobile.screens.userportrait.interactor.UserPortraitUriBuilder;
import com.liferay.mobile.screens.util.ServiceProvider;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Javier Gamarra
 * @author Jose Manuel Navarro
 */
public class UserPortraitLoadInteractorImpl
	extends BaseCachedRemoteInteractor<UserPortraitInteractorListener, UserPortraitLoadEvent>
	implements UserPortraitLoadInteractor, Target {

	public UserPortraitLoadInteractorImpl(int screenletId, OfflinePolicy offlinePolicy) {
		super(screenletId, offlinePolicy);
	}

	@Override
	public void load(boolean male, long portraitId, String uuid) {
		validate(uuid);

		UserPortraitUriBuilder userPortraitUriBuilder = new UserPortraitUriBuilder();
		Uri uri = userPortraitUriBuilder.getUserPortraitUri(LiferayServerContext.getServer(),
			male, portraitId, uuid);

		Context context = LiferayScreensContext.getContext();
		Downloader downloader = new OkHttpDownloader(userPortraitUriBuilder.getUserPortraitClient(context));
		Picasso picasso = new Picasso.Builder(context).downloader(downloader).build();
		RequestCreator requestCreator = picasso.load(uri);

		if (OfflinePolicy.REMOTE_ONLY.equals(getOfflinePolicy())) {
			requestCreator = requestCreator
				.memoryPolicy(MemoryPolicy.NO_CACHE)
				.networkPolicy(NetworkPolicy.NO_CACHE);
		}

		requestCreator.into(this);
	}

	@Override
	public void load(final long userId) throws Exception {

		validate(userId);

		processWithCache(userId);
	}

	public void onEvent(UserPortraitLoadEvent event) {
		if (!isValidEvent(event)) {
			return;
		}

		if (event.isFailed()) {
			onEventWithCache(event, event.getUserId());
		}
		else {
			JSONObject userAttributes = event.getJSONObject();
			try {
				long portraitId = userAttributes.getLong("portraitId");
				Long userId = userAttributes.getLong("userId");
				String uuid = userAttributes.getString("uuid");

				if (hasToStoreToCache()) {
					storeToCache(event, userId, portraitId, uuid);
				}
				load(true, portraitId, uuid);
			}
			catch (Exception e) {
				notifyError(event);
			}

		}
	}

	@Override
	public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
		if (getListener() != null) {
			getListener().onEndUserPortraitLoadRequest(bitmap);
		}
	}

	@Override
	public void onBitmapFailed(Drawable errorDrawable) {
		if (getListener() != null) {
			getListener().onUserPortraitLoadFailure(new IOException("Portrait cannot be loaded"));
		}
	}

	@Override
	public void onPrepareLoad(Drawable placeHolderDrawable) {
	}

	@Override
	protected void online(Object[] args) throws Exception {

		long userId = (long) args[0];

		getUserService(userId).getUserById(userId);
	}

	@Override
	protected boolean cached(Object[] args) {

		long userId = (long) args[0];

		Cache cache = CacheSQL.getInstance();
		UserPortraitCache userPortraitCache = (UserPortraitCache) cache.getById(
			DefaultCachedType.USER_PORTRAIT, String.valueOf(userId));

		if (userPortraitCache != null) {
			load(userPortraitCache.isMale(), userPortraitCache.getPortraitId(), userPortraitCache.getUuid());
			return true;
		}
		return false;
	}

	@Override
	protected void storeToCache(UserPortraitLoadEvent event, Object... args) {

		long userId = (long) args[0];
		long portraitId = (long) args[1];
		String uuid = (String) args[2];

		CacheSQL.getInstance().set(new UserPortraitCache(userId, true, portraitId, uuid));
	}

	@Override
	protected void notifyError(UserPortraitLoadEvent event) {
		getListener().onUserPortraitLoadFailure(event.getException());
	}

	protected UserConnector getUserService(long userId) {
		Session session = SessionContext.createSessionFromCurrentSession();
		session.setCallback(new UserPortraitLoadCallback(getTargetScreenletId(), userId));

		return ServiceProvider.getInstance().getUserConnector(session);
	}

	private void validate(String uuid) {
		if (getListener() == null) {
			throw new IllegalArgumentException("Listener cannot be empty");
		}
		if (uuid == null || uuid.isEmpty()) {
			throw new IllegalArgumentException("userId cannot be empty");
		}
	}

	private void validate(long userId) {
		if (userId == 0) {
			throw new IllegalArgumentException("userId cannot be empty");
		}
	}

}