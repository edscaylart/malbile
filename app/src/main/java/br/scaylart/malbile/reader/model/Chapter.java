package br.scaylart.malbile.reader.model;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import br.scaylart.malbile.controllers.databases.Tables;
import br.scaylart.malbile.models.GlobalParcelable;
import lombok.Getter;
import lombok.Setter;

public class Chapter extends GlobalParcelable {
    public static final String TAG = Chapter.class.getSimpleName();

    public static final String PARCELABLE_KEY = TAG + ":" + "ParcelableKey";

    @Getter @Setter private int id;
    @Getter @Setter private String url;
    @Getter @Setter private String parentUrl;
    @Getter @Setter private String title;
    @Getter @Setter private boolean newChapter;
    @Getter @Setter private long date;

    @Getter @Setter private int number;

    public static Chapter fromCursor(Cursor c) {
        Chapter result = new Chapter();
        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setId(c.getInt(columnNames.indexOf(Tables.COLUMN_ID)));
        result.setUrl(c.getString(columnNames.indexOf(Tables.MangaChapters.URL)));
        result.setParentUrl(c.getString(columnNames.indexOf(Tables.MangaChapters.PARENT_URL)));
        result.setNewChapter(c.getInt(columnNames.indexOf(Tables.MangaChapters.NEW)) == 1);
        result.setDate(c.getLong(columnNames.indexOf(Tables.MangaChapters.CHAPTER_DATE)));
        result.setNumber(c.getInt(columnNames.indexOf(Tables.MangaChapters.NUMBER)));
        result.setTitle(c.getString(columnNames.indexOf(Tables.MangaChapters.TITLE)));

        return result;
    }
}
