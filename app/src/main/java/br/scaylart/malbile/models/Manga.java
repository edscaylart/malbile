package br.scaylart.malbile.models;

import android.database.Cursor;

import com.google.gson.Gson;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.scaylart.malbile.controllers.databases.Tables;
import lombok.Getter;
import lombok.Setter;

@Root(name = "manga", strict = false)
public class Manga extends BaseRecord {
    @Element(name = "series_chapters", required = false)
    @Setter @Getter private int chapters; //Number of chapters. null is returned if the number of chapters is unknown.

    @Element(name = "series_volumes", required = false)
    @Setter @Getter private int volumes; //Number of volumes. null is returned if the number of volumes is unknown.

    @Element(name = "my_read_chapters", required = false)
    @Getter private int chaptersRead; //Number of chapters already read by the user.

    @Element(name = "my_read_volumes", required = false)
    @Getter private int volumesRead; //Number of volumes already read by the user.

    @Element(name = "my_rereadingg", required = false)
    @Getter private int rereadingValue; // is Rereading

    @Element(name = "my_rereading_chap", required = false)
    @Getter private int rereadingCount; // number chap rereading

    @Element(name = "my_start_date", required = false)
    @Getter private String listStartDate; // Use's start date

    @Element(name = "my_finish_date", required = false)
    @Getter private String listFinishDate; // Use's finish date

    @Element(name = "my_status", required = false)
    @Getter private int myStatus; //User's watched status of the manga.
    // Manga This is a string that is one of: reading, completed, on-hold, dropped, plan to read.

    @Element(name = "my_score", required = false)
    @Getter private int score; //User's score for the manga, from 1 to 10.

    @Setter @Getter private ArrayList<RelatedRecord> relatedManga; //A list of related manga.
    @Setter @Getter private ArrayList<RelatedRecord> animeAdaptations; //A list of anime adaptations of this manga (or conversely, anime from which this manga is adapted). Not available in /mangalist requests.

    @Getter private boolean rereading;

    public static Manga fromCursor(Cursor c) {
        Manga result = new Manga();
        result.setFromCursor(true);
        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setId(c.getInt(columnNames.indexOf(Tables.COLUMN_ID)));
        result.setTitle(c.getString(columnNames.indexOf(Tables.Mangas.TITLE)));
        result.setType(c.getInt(columnNames.indexOf(Tables.Mangas.TYPE)));
        result.setStatus(c.getInt(columnNames.indexOf(Tables.Mangas.STATUS)));
        result.setVolumes(c.getInt(columnNames.indexOf(Tables.Mangas.VOLUMES)));
        result.setChapters(c.getInt(columnNames.indexOf(Tables.Mangas.CHAPTERS)));
        result.setMembersScore(c.getString(columnNames.indexOf(Tables.Mangas.MEMBERS_SCORE)));
        result.setSynopsis(c.getString(columnNames.indexOf(Tables.Mangas.SYNOPSIS)));
        result.setImageUrl(c.getString(columnNames.indexOf(Tables.Mangas.IMAGE_URL)));
        result.setStartDate(c.getString(columnNames.indexOf(Tables.Mangas.START_DATE)));
        result.setEndDate(c.getString(columnNames.indexOf(Tables.Mangas.END_DATE)));
        result.setMembersCount(c.getInt(columnNames.indexOf(Tables.Mangas.MEMBERS_COUNT)));
        result.setFavoritedCount(c.getInt(columnNames.indexOf(Tables.Mangas.FAVORITED_COUNT)));
        //result.setPopularityRank(c.getInt(columnNames.indexOf("popularityRank")));
        result.setRank(c.getString(columnNames.indexOf(Tables.Mangas.RANK)));
        //result.setListedId(c.getInt(columnNames.indexOf("listedId")));
        //result.setPriority(c.getInt(columnNames.indexOf("priority")), false);
        //result.setChapDownloaded(c.getInt(columnNames.indexOf("downloaded")), false);
        //result.setPersonalComments(c.getString(columnNames.indexOf("comments")), false);

        result.setMyStatus(c.getInt(columnNames.indexOf(Tables.MangaLists.STATUS)), false);
        result.setVolumesRead(c.getInt(columnNames.indexOf(Tables.MangaLists.VOLUMES)), false);
        result.setChaptersRead(c.getInt(columnNames.indexOf(Tables.MangaLists.CHAPTERS)), false);
        result.setListStartDate(c.getString(columnNames.indexOf(Tables.MangaLists.START_DATE)), false);
        result.setListFinishDate(c.getString(columnNames.indexOf(Tables.MangaLists.END_DATE)), false);
        result.setScore(c.getInt(columnNames.indexOf(Tables.MangaLists.SCORE)));
        result.setRereadingValue(c.getInt(columnNames.indexOf(Tables.MangaLists.REREADING_VALUE)), false);
        result.setRereading(c.getInt(columnNames.indexOf(Tables.MangaLists.REREADING)) > 0, false);
        result.setRereadingCount(c.getInt(columnNames.indexOf(Tables.MangaLists.REREADING_COUNT)), false);

        if (!c.isNull(columnNames.indexOf(Tables.MangaLists.DIRTY))) {
            result.setDirty(new Gson().fromJson(c.getString(columnNames.indexOf(Tables.MangaLists.DIRTY)), ArrayList.class));
        } else {
            result.setDirty(null);
        }

        Date lastUpdateDate;
        try {
            long lastUpdate = c.getLong(columnNames.indexOf(Tables.MangaLists.LAST_UPDATE));
            lastUpdateDate = new java.util.Date(lastUpdate * 1000);// new Date(lastUpdate);
        } catch (Exception e) { // database entry was null
            lastUpdateDate = null;
        }
        result.setLastUpdate(c.getLong(columnNames.indexOf(Tables.MangaLists.LAST_UPDATE)));
        result.setLastDateUpdate(lastUpdateDate);
        return result;
    }

    public String getProgress() {
        String total = (getChapters() > 0) ? String.valueOf(getChapters()) : "?";
        String read = String.valueOf(getChaptersRead());

        return read + " / " + total;
    }

    public String getVolumeProgress() {
        String total = (getVolumes() > 0) ? String.valueOf(getVolumes()) : "?";
        String read = String.valueOf(getVolumesRead());

        return read + " / " + total;
    }

    public void setChaptersRead(int chaptersRead) {
        setChaptersRead(chaptersRead, true);
    }

    public void setChaptersRead(int chaptersRead, boolean markDirty) {
        this.chaptersRead = chaptersRead;
        if (markDirty) {
            addDirtyField(Tables.MangaLists.CHAPTERS);
        }
    }

    public void setVolumesRead(int volumesRead) {
        setVolumesRead(volumesRead, true);
    }

    public void setVolumesRead(int volumesRead, boolean markDirty) {
        this.volumesRead = volumesRead;
        if (markDirty) {
            addDirtyField(Tables.MangaLists.VOLUMES);
        }
    }

    public void setRereadingValue(int rereadingValue) {
        setRereadingValue(rereadingValue, true);
    }

    public void setRereadingValue(int rereadingValue, boolean markDirty) {
        this.rereadingValue = rereadingValue + 1;
        if (markDirty) {
            addDirtyField(Tables.MangaLists.REREADING_VALUE);
        }
    }

    public void setRereadingCount(int rereadingCount) {
        setRereadingCount(rereadingCount, true);
    }

    public void setRereadingCount(int rereadingCount, boolean markDirty) {
        this.rereadingCount = rereadingCount;
        if (markDirty) {
            addDirtyField(Tables.MangaLists.REREADING_COUNT);
        }
    }

    public void setRereading(boolean rereading) {
        setRereading(rereading, true);
    }

    public void setRereading(boolean rereading, boolean markDirty) {
        this.rereading = rereading;
        if (markDirty) {
            addDirtyField(Tables.MangaLists.REREADING);
        }
    }

    public void setListStartDate(String listStartDate) {
        setListStartDate(listStartDate, true);
    }

    public void setListStartDate(String listStartDate, boolean markDirty) {
        this.listStartDate = listStartDate;
        if (markDirty) {
            addDirtyField(Tables.MangaLists.START_DATE);
        }
    }

    public void setListFinishDate(String listFinishDate) {
        setListFinishDate(listFinishDate, true);
    }

    public void setListFinishDate(String listFinishDate, boolean markDirty) {
        this.listFinishDate = listFinishDate;
        if (markDirty) {
            addDirtyField(Tables.MangaLists.END_DATE);
        }
    }

    public void setMyStatus(int listStatus) {
        setMyStatus(listStatus, true);
    }

    public void setMyStatus(int listStatus, boolean markDirty) {
        this.myStatus = listStatus;
        if (markDirty) {
            addDirtyField(Tables.MangaLists.STATUS);
        }
    }

    public void setScore(int score) {
        setScore(score, true);
    }

    public void setScore(int score, boolean markDirty) {
        this.score = score;
        if (markDirty) {
            addDirtyField(Tables.MangaLists.SCORE);
        }
    }

    public void setStatusByDesc(String status) {
        switch (status.toLowerCase().trim()) {
            case "publishing":
                this.setStatus(1);
                break;
            case "finished":
                this.setStatus(2);
                break;
            case "not yet published":
                this.setStatus(3);
                break;
        }
    }

    public void setTypeByDesc(String type) {
        switch (type.toLowerCase().trim()) {
            case "manga":
                this.setType(1);
                break;
            case "novel":
                this.setType(2);
                break;
            case "one shot":
                this.setType(3);
                break;
            case "doujin":
                this.setType(4);
                break;
            case "manhwa":
                this.setType(5);
                break;
            case "manhua":
                this.setType(6);
                break;
            case "oel":
                this.setType(7);
                break;
        }
    }

    public int getListStatusIndex() {
        switch (getMyStatus()) {
            case 1:
                return 4; // reading
            case 2:
                return 0; // completed
            case 3:
                return 1; // onhold
            case 4:
                return 2; // dropped
            case 6:
                return 6; // plan to read
            default:
                return 0;
        }
    }

    public String getXML() {
        String XMLdata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<entry>" +
                "<chapter>" + Integer.toString(getChaptersRead()) + "</chapter>" +
                "<volume>" + Integer.toString(getVolumesRead()) + "</volume>" +
                "<status>" + Integer.toString(getMyStatus()) + "</status>" +
                "<score>" + Integer.toString(getScore()) + "</score>" +
                "<times_reread>" + Integer.toString(getRereadingCount()) + "</times_reread>" +
                "<date_start>" + nullCheck(getListStartDate()) + "</date_start>" +
                "<date_finish>" + nullCheck(getListFinishDate()) + "</date_finish>" +
                "<enable_rereading>" + Integer.toString(isRereading() ? 1 : 0) + "</enable_rereading>" +
                "</entry>";

        return XMLdata;
    }
}
