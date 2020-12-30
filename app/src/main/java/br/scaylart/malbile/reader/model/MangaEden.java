package br.scaylart.malbile.reader.model;

import android.database.Cursor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.scaylart.malbile.controllers.databases.Tables;
import br.scaylart.malbile.models.GlobalParcelable;
import lombok.Getter;
import lombok.Setter;

public class MangaEden extends GlobalParcelable {
    @Getter @Setter private int id;
    @Getter @Setter private String url;
    @Getter @Setter private String artist;
    @Getter @Setter private String author;
    @Getter @Setter private String description;
    @Getter @Setter private String genre;
    @Getter @Setter private String title;
    @Getter @Setter private boolean completed;
    @Getter @Setter private String imageUrl;

    @Getter @Setter private int rank;
    @Getter @Setter private long updated;
    @Getter @Setter private int updateCount;

    @Getter @Setter private boolean initialized;

    public static MangaEden fromCursor(Cursor c) {
        MangaEden result = new MangaEden();
        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setId(c.getInt(columnNames.indexOf(Tables.COLUMN_ID)));
        result.setArtist(c.getString(columnNames.indexOf(Tables.MangasReader.ARTIST)));
        result.setAuthor(c.getString(columnNames.indexOf(Tables.MangasReader.AUTHOR)));
        result.setCompleted(c.getInt(columnNames.indexOf(Tables.MangasReader.COMPLETED)) == 2);
        result.setDescription(c.getString(columnNames.indexOf(Tables.MangasReader.DESCRIPTION)));
        result.setGenre(c.getString(columnNames.indexOf(Tables.MangasReader.GENRE)));
        result.setImageUrl(c.getString(columnNames.indexOf(Tables.MangasReader.IMAGE_URL)));
        result.setInitialized(c.getInt(columnNames.indexOf(Tables.MangasReader.INITIALIZED)) == 1);
       // result.setd(c.getString(columnNames.indexOf(Tables.MangasReader.LAST_CHAPTER_DATE)));
        result.setRank(c.getInt(columnNames.indexOf(Tables.MangasReader.RANK)));
        result.setTitle(c.getString(columnNames.indexOf(Tables.MangasReader.TITLE)));
        result.setUpdateCount(c.getInt(columnNames.indexOf(Tables.MangasReader.UPDATE_COUNT)));
        result.setUpdated(c.getInt(columnNames.indexOf(Tables.MangasReader.UPDATED)));
        result.setUrl(c.getString(columnNames.indexOf(Tables.MangasReader.URL)));

        return result;
    }
}
