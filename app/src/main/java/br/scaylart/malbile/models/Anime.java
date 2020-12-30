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

@Root(name = "anime", strict = false)
public class Anime extends BaseRecord {
    @Element(name = "series_episodes", required = false)
    @Setter @Getter private int episodes; //Number of episodes. null is returned if the number of episodes is unknown.

    @Element(name = "my_watched_episodes", required = false)
    @Getter private int watchedEpisodes; //Number of episodes already watched by the user.

    @Element(name = "my_rewatching", required = false)
    @Getter private int rewatchingValue; // is rewatching

    @Element(name = "my_rewatching_ep", required = false)
    @Getter private int rewatchingCount; // number ep rewatching

    @Element(name = "my_start_date", required = false)
    @Getter private String listStartDate; // Use's start date

    @Element(name = "my_finish_date", required = false)
    @Getter private String listFinishDate; // Use's finish date

    @Element(name = "my_status", required = false)
    @Getter private int myStatus; //User's watched status of the anime.
    // Anime This is a string that is one of: watching, completed, on-hold, dropped, plan to watch.

    @Element(name = "my_score", required = false)
    @Getter private int score; //User's score for the anime, from 1 to 10.

    @Setter @Getter private String classification; //Classification or rating of this anime. Like: R - 17+ (violence & profanity), PG - Children. Not available in XML requests.

    @Setter @Getter private ArrayList<RelatedRecord> mangaAdaptations; //A list of manga adaptations of this anime (or conversely, manga from which this anime is adapted). Not available in XML requests.
    @Setter @Getter private ArrayList<RelatedRecord> prequels; //A list of anime prequels of this anime. Not available in XML requests.
    @Setter @Getter private ArrayList<RelatedRecord> sequels; //A list of anime sequels of this anime. Not available in XML requests.
    @Setter @Getter private ArrayList<RelatedRecord> spinOffs; //A list of spin-offs of this anime. Not available in XML requests.
    @Setter @Getter private RelatedRecord parentStory; //A list of summaries of this anime. Not available in XML requests.

    @Getter private boolean rewatching;

    public static Anime fromCursor(Cursor c) {
        Anime result = new Anime();
        result.setFromCursor(true);
        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setId(c.getInt(columnNames.indexOf(Tables.COLUMN_ID)));
        result.setTitle(c.getString(columnNames.indexOf(Tables.Animes.TITLE)));
        result.setType(c.getInt(columnNames.indexOf(Tables.Animes.TYPE)));
        result.setStatus(c.getInt(columnNames.indexOf(Tables.Animes.STATUS)));
        result.setEpisodes(c.getInt(columnNames.indexOf(Tables.Animes.EPISODES)));
        //result.setStorage(c.getInt(columnNames.indexOf("storage")), false);
        //result.setStorageValue(c.getInt(columnNames.indexOf("storageValue")), false);
        result.setMembersScore(c.getString(columnNames.indexOf(Tables.Animes.MEMBERS_SCORE)));
        result.setSynopsis(c.getString(columnNames.indexOf(Tables.Animes.SYNOPSIS)));
        result.setImageUrl(c.getString(columnNames.indexOf(Tables.Animes.IMAGE_URL)));
        result.setClassification(c.getString(columnNames.indexOf(Tables.Animes.CLASSIFICATION)));
        result.setMembersCount(c.getInt(columnNames.indexOf(Tables.Animes.MEMBERS_COUNT)));
        result.setFavoritedCount(c.getInt(columnNames.indexOf(Tables.Animes.FAVORITED_COUNT)));
        //result.setPopularityRank(c.getInt(columnNames.indexOf("popularityRank")));
        //result.setFansubGroup(c.getString(columnNames.indexOf("fansub")), false);
        //result.setPriority(c.getInt(columnNames.indexOf("priority")), false);
        //result.setEpsDownloaded(c.getInt(columnNames.indexOf("downloaded")), false);
        //result.setPersonalComments(c.getString(columnNames.indexOf("comments")), false);
        result.setStartDate(c.getString(columnNames.indexOf(Tables.Animes.START_DATE)));
        result.setEndDate(c.getString(columnNames.indexOf(Tables.Animes.END_DATE)));
        result.setRank(c.getString(columnNames.indexOf(Tables.Animes.RANK)));
        //result.setListedId(c.getInt(columnNames.indexOf("listedId")));

        result.setRewatching(c.getInt(columnNames.indexOf(Tables.AnimeLists.REWATCHING)) > 0);
        result.setScore(c.getInt(columnNames.indexOf(Tables.AnimeLists.SCORE)), false);
        result.setListStartDate(c.getString(columnNames.indexOf(Tables.AnimeLists.START_DATE)), false);
        result.setListFinishDate(c.getString(columnNames.indexOf(Tables.AnimeLists.END_DATE)), false);
        result.setRewatchingCount(c.getInt(columnNames.indexOf(Tables.AnimeLists.REWATCHING_COUNT)), false);
        result.setRewatchingValue(c.getInt(columnNames.indexOf(Tables.AnimeLists.REWATCHING_VALUE)), false);
        result.setMyStatus(c.getInt(columnNames.indexOf(Tables.AnimeLists.STATUS)), false);
        result.setWatchedEpisodes(c.getInt(columnNames.indexOf(Tables.AnimeLists.EPISODES)), false);
        Date lastUpdateDate;
        try {
            long lastUpdate = c.getLong(columnNames.indexOf(Tables.AnimeLists.LAST_UPDATE));
            lastUpdateDate = new java.util.Date(lastUpdate * 1000);//new Date(lastUpdate);
        } catch (Exception e) { // database entry was null
            lastUpdateDate = null;
        }
        result.setLastUpdate(c.getLong(columnNames.indexOf(Tables.AnimeLists.LAST_UPDATE)));
        result.setLastDateUpdate(lastUpdateDate);
        if (!c.isNull(columnNames.indexOf(Tables.AnimeLists.DIRTY))) {
            result.setDirty(new Gson().fromJson(c.getString(columnNames.indexOf(Tables.AnimeLists.DIRTY)), ArrayList.class));
        } else {
            result.setDirty(null);
        }

        return result;
    }

    public String getProgress() {
        String total = (getEpisodes() > 0) ? String.valueOf(getEpisodes()) : "?";
        String watch = String.valueOf(getWatchedEpisodes());

        return watch + " / " + total;
    }

    public void setWatchedEpisodes(int watchedEpisodes) {
        setWatchedEpisodes(watchedEpisodes, true);
    }

    public void setWatchedEpisodes(int watchedEpisodes, boolean markDirty) {
        this.watchedEpisodes = watchedEpisodes;
        if (markDirty) {
            addDirtyField(Tables.AnimeLists.EPISODES);
        }
    }

    public void setRewatchingCount(int rewatchingCount) {
        setRewatchingCount(rewatchingCount, true);
    }

    public void setRewatchingCount(int rewatchingCount, boolean markDirty) {
        this.rewatchingCount = rewatchingCount;
        if (markDirty) {
            addDirtyField(Tables.AnimeLists.REWATCHING_COUNT);
        }
    }

    public void setRewatchingValue(int rewatchingValue) {
        setRewatchingValue(rewatchingValue, true);
    }

    public void setRewatchingValue(int rewatchingValue, boolean markDirty) {
        this.rewatchingValue = rewatchingValue;
        if (markDirty) {
            addDirtyField(Tables.AnimeLists.REWATCHING_VALUE);
        }
    }

    public void setRewatching(boolean rewatching) {
        setRewatching(rewatching, true);
    }

    public void setRewatching(boolean rewatching, boolean markDirty) {
        this.rewatching = rewatching;
        if (markDirty) {
            addDirtyField(Tables.AnimeLists.REWATCHING);
        }
    }

    public void setListStartDate(String listStartDate) {
        setListStartDate(listStartDate, true);
    }

    public void setListStartDate(String listStartDate, boolean markDirty) {
        this.listStartDate = listStartDate;
        if (markDirty) {
            addDirtyField(Tables.AnimeLists.START_DATE);
        }
    }

    public void setListFinishDate(String listFinishDate) {
        setListFinishDate(listFinishDate, true);
    }

    public void setListFinishDate(String listFinishDate, boolean markDirty) {
        this.listFinishDate = listFinishDate;
        if (markDirty) {
            addDirtyField(Tables.AnimeLists.END_DATE);
        }
    }

    public void setMyStatus(int listStatus) {
        setMyStatus(listStatus, true);
    }

    public void setMyStatus(int listStatus, boolean markDirty) {
        this.myStatus = listStatus;
        if (markDirty) {
            addDirtyField(Tables.AnimeLists.STATUS);
        }
    }

    public void setScore(int score) {
        setScore(score, true);
    }

    public void setScore(int score, boolean markDirty) {
        this.score = score;
        if (markDirty) {
            addDirtyField(Tables.AnimeLists.SCORE);
        }
    }

    public void setStatusByDesc(String status) {
        switch (status.toLowerCase().trim()) {
            case "currently airing":
                this.setStatus(1);
                break;
            case "finished airing":
                this.setStatus(2);
                break;
            case "not yet aired":
                this.setStatus(3);
                break;
        }
    }

    public void setTypeByDesc(String type) {
        switch (type.toLowerCase().trim()) {
            case "tv":
                this.setType(1);
                break;
            case "ova":
                this.setType(2);
                break;
            case "movie":
                this.setType(3);
                break;
            case "special":
                this.setType(4);
                break;
            case "ona":
                this.setType(5);
                break;
            case "music":
                this.setType(6);
                break;
        }
    }

    public int getMyStatusIndex() {
        switch (getMyStatus()) {
            case 1:
                return 3; // watching
            case 2:
                return 0; // completed
            case 3:
                return 1; // onhold
            case 4:
                return 2; // dropped
            case 6:
                return 5; // plan to watch
            default:
                return 0;
        }
    }

    public String getXML() {
        String XMLdata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<entry>" +
                "<episode>" + Integer.toString(getWatchedEpisodes()) + "</episode>" +
                "<status>" + Integer.toString(getMyStatus()) + "</status>" +
                "<score>" + Integer.toString(getScore()) + "</score>" +
                "<times_rewatched>" + Integer.toString(getRewatchingCount()) + "</times_rewatched>" +
                "<date_start>" + nullCheck(getListStartDate()) + "</date_start>" +
                "<date_finish>" + nullCheck(getListFinishDate()) + "</date_finish>" +
                "<enable_rewatching>" + Integer.toString(isRewatching() ? 1 : 0) + "</enable_rewatching>" +
                "</entry>";

        return XMLdata;
    }
}
