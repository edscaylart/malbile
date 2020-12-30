package br.scaylart.malbile.reader.model;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import br.scaylart.malbile.controllers.databases.Tables;
import br.scaylart.malbile.models.GlobalParcelable;
import lombok.Getter;
import lombok.Setter;

public class RecentChapter extends GlobalParcelable {
    @Getter @Setter private int id;
    @Getter @Setter private String url;
    @Getter @Setter private String parentUrl;
    @Getter @Setter private String title;
    @Getter @Setter private String thumbnailUrl;

    @Getter @Setter private int pageNumber;
    @Getter @Setter private boolean offline;
    @Getter @Setter private long date;

    public static RecentChapter fromCursor(Cursor c) {
        RecentChapter result = new RecentChapter();
        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setId(c.getInt(columnNames.indexOf(Tables.COLUMN_ID)));
        result.setUrl(c.getString(columnNames.indexOf(Tables.MangaRecentChapters.URL)));
        result.setParentUrl(c.getString(columnNames.indexOf(Tables.MangaRecentChapters.PARENT_URL)));
        result.setOffline(c.getInt(columnNames.indexOf(Tables.MangaRecentChapters.OFFLINE)) == 1);
        result.setDate(c.getLong(columnNames.indexOf(Tables.MangaRecentChapters.CHAPTER_DATE)));
        result.setPageNumber(c.getInt(columnNames.indexOf(Tables.MangaRecentChapters.PAGE_NUMBER)));
        result.setTitle(c.getString(columnNames.indexOf(Tables.MangaRecentChapters.TITLE)));
        result.setThumbnailUrl(c.getString(columnNames.indexOf(Tables.MangaRecentChapters.THUMBNAIL_URL)));

        return result;
    }
}
