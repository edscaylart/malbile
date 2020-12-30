package br.scaylart.malbile.models;


import android.text.TextUtils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementUnion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

public class BaseRecord extends GlobalParcelable {
    public static final String STATUS_INPROGRESS = "1"; // watching / reading
    public static final String STATUS_COMPLETED = "2";//"completed";
    public static final String STATUS_ONHOLD = "3";//"on-hold";
    public static final String STATUS_DROPPED = "4";//"dropped";
    public static final String STATUS_PLANNED = "6"; // plant o watch/read

    @ElementUnion({
            @Element(name = "series_animedb_id", required = false),
            @Element(name = "series_mangadb_id", required = false)
    })
    @Setter @Getter private int id;

    @Element(name = "series_title", required = false)
    @Setter @Getter private String title; //The anime/manga title.

    @Element(name = "series_image", required = false)
    @Setter private String imageUrl; //URL to an image for this anime/manga.

    @Element(name = "series_type", required = false)
    @Setter @Getter private int type; //Type of anime/manga.
    // Type of manga. Possible values: Manga, Novel, One Shot, Doujin, Manwha, Manhua, OEL ("OEL manga" refers to "Original English-Language manga").
    // Type of anime. Possible values: TV, Movie, OVA, ONA, Special, Music.

    @Element(name = "series_status", required = false)
    @Setter @Getter private int status; //Airing status of this anime/manga.
    // Anime Possible values: finished airing, currently airing, not yet aired.
    // Manga Possible values: finished, publishing, not yet published.

    @Element(name = "series_start", required = false)
    @Setter @Getter private String startDate; //Beginning date from which this anime/manga was/will be aired.

    @Element(name = "series_end", required = false)
    @Setter @Getter private String endDate; //Ending air date of this anime/manga.

    @Element(name = "my_last_updated", required = false)
    @Setter @Getter private long lastUpdate; // Last update in epoch time

    @Setter @Getter private Date lastDateUpdate; // Last update date

    @Element(name = "my_id", required = false)
    @Setter @Getter private String myMalId; // User's anime/manga ID.

    @Setter @Getter private ArrayList<RelatedRecord> genres; //A list of genres for this anime/manga, e.g. ["Action", "Comedy", "Shounen"]. Not available in XML requests.
    @Setter @Getter private String synopsis; //Text describing the anime/manga. Not available in XML requests.
    @Setter @Getter private String rank; //Global rank of this anime/manga. Not available in XML requests.
    @Setter @Getter private String membersScore; //Weighted score members of MyAnimeList have given to this anime. Not available in XML requests.
    @Setter @Getter private int membersCount; //Number of members who have this anime on their list. Not available in XML requests.
    @Setter @Getter private int favoritedCount; //Number of members who have this anime marked as one of their favorites. Not available in XML requests.

    @Setter @Getter private ArrayList<RelatedRecord> sideStories; //A list of manga side stories of this manga. Not available in XML requests.
    @Setter @Getter private ArrayList<RelatedRecord> alternativeVersions; //A list of alternative versions of this anime/manga.
    @Setter @Getter private HashMap<String, ArrayList<String>> otherTitles; //A hash/dictionary containing other titles this anime/manga has.

    @Setter private ArrayList<String> dirty;

    @Setter private boolean createFlag;
    @Setter private boolean deleteFlag;

    @Setter @Getter private transient boolean fromCursor = false;

    public BaseRecord() {}

    public void addDirtyField(String field) {
        if (dirty == null) {
            dirty = new ArrayList<String>();
        }
        if (!dirty.contains((field))) {
            dirty.add(field);
        }
    }

    public boolean isDirty() {
        return dirty != null && !dirty.isEmpty();
    }

    public void clearDirty() {
        dirty = null;
    }

    public boolean getCreateFlag() {
        return createFlag;
    }

    public boolean getDeleteFlag() {
        return deleteFlag;
    }

    private ArrayList<String> getOtherTitlesByLanguage(String lang) {
        if (otherTitles == null) {
            return null;
        }
        return otherTitles.get(lang);
    }

    public ArrayList<String> getOtherTitlesJapanese() {
        return getOtherTitlesByLanguage("japanese");
    }

    public ArrayList<String> getOtherTitlesEnglish() {
        return getOtherTitlesByLanguage("english");
    }

    public ArrayList<String> getOtherTitlesSynonyms() {
        return getOtherTitlesByLanguage("synonyms");
    }

    public ArrayList<String> getDirty() {
        return dirty;
    }

    public String getImageUrl() {
        // if not loaded from cursor the image might point to an thumbnail
        if (fromCursor)
            return imageUrl;
        else
            return imageUrl.replaceFirst("t.jpg$", ".jpg");
    }

    /*
     * some reflection magic used to get dirty values easier
     */
    public Class getPropertyType(String property) {
        try {
            Field field = getField(this.getClass(), property);
            return field.getType();
        } catch (Exception e) {
            return null;
        }
    }

    private Field getField(Class<?> c, String property) {
        try {
            return c.getDeclaredField(property);
        } catch (Exception e) {
            if (c.getSuperclass() != null) {
                return getField(c.getSuperclass(), property);
            } else {
                return null;
            }
        }
    }

    protected Object getPropertyValue(String property) {
        try {
            Field field = getField(this.getClass(), property);
            if (field != null) {
                field.setAccessible(true);
                return field.get(this);
            }
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public Integer getIntegerPropertyValue(String property) {
        Object value = getPropertyValue(property);
        if (value != null) {
            return (Integer) value;
        }
        return null;
    }

    public String getStringPropertyValue(String property) {
        Object value = getPropertyValue(property);
        if (value != null) {
            return (String) value;
        }
        return null;
    }

    public String getArrayPropertyValue(String property) {
        ArrayList<String> array = (ArrayList<String>) getPropertyValue(property);
        Object value = array != null ? TextUtils.join(",", array) : "";
        return (String) value;
    }

    public String nullCheck(String string) {
        // TODO alterar
        return isEmpty(string) ? "" : string;
    }

    public boolean isEmpty(String string) {
        return ((string == null || string.equals("") || string.equals("0-00-00")));
    }
}
