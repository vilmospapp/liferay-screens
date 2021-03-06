package com.liferay.mobile.screens.blogs;

import com.liferay.mobile.screens.asset.list.AssetEntry;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sarai Díaz García
 */

public class BlogsEntry extends AssetEntry {

	public BlogsEntry(Map<String, Object> map) {
		super(map);
	}

	@Override
	public String getTitle() {
		return super.getTitle();
	}

	public Map<String, Object> getBlogsEntry() {
		return (Map<String, Object>) _values.get("blogsEntry");
	}

	public String getUserName() {
		return (String) getBlogsEntry().get("userName");
	}

	public String getDate() {
		long displayDate = Long.valueOf(getBlogsEntry().get("displayDate").toString());
		return dateToString(displayDate);
	}

	public long getCoverImage() {
		String cover = getBlogsEntry().get("coverImageFileEntryId").toString();
		return Long.valueOf(cover);
	}

	public long getUserId() {
		return Long.valueOf(getBlogsEntry().get("userId").toString());
	}

	public String getSubtitle() {
		return (String) getBlogsEntry().get("subtitle");
	}

	public String getContent() {
		return (String) getBlogsEntry().get("content");
	}

	private String dateToString(long displayDate) {
		SimpleDateFormat simpleDateFormat =
			(SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
		return simpleDateFormat.format(new Date(displayDate));
	}
}
