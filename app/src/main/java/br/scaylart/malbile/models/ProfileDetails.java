package br.scaylart.malbile.models;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import br.scaylart.malbile.controllers.databases.Tables;
import lombok.Getter;
import lombok.Setter;

public class ProfileDetails extends GlobalParcelable {
    /* User's detail */
    @Getter @Setter private String lastOnline;
    @Getter @Setter private String gender;
    @Getter @Setter private String birthday;
    @Getter @Setter private String location;
    @Getter @Setter private String website;
    @Getter @Setter private String joinDate;
    @Getter @Setter private String accessRank;
    @Getter @Setter private int animeListView;
    @Getter @Setter private int mangaListView;
    @Getter @Setter private int comments;
    @Getter @Setter private int forumPosts;
    /* Compatibility */
    @Getter @Setter private String animeCompatibility;
    @Getter @Setter private String animeCompatibilityValue;
    @Getter @Setter private String mangaCompatibility;
    @Getter @Setter private String mangaCompatibilityValue;

    public static ProfileDetails fromCursor(Cursor c) {
        return fromCursor(c, false);
    }

    public static ProfileDetails fromCursor(Cursor c, boolean friendDetails) {
        ProfileDetails result = new ProfileDetails();

        List<String> columnNames = Arrays.asList(c.getColumnNames());

        result.setLastOnline(c.getString(columnNames.indexOf(Tables.Profiles.LAST_ONLINE)));
        if (!friendDetails) {
            result.setBirthday(c.getString(columnNames.indexOf(Tables.Profiles.BIRTHDAY)));
            result.setLocation(c.getString(columnNames.indexOf(Tables.Profiles.LOCATION)));
            result.setWebsite(c.getString(columnNames.indexOf(Tables.Profiles.WEBSITE)));
            result.setComments(c.getInt(columnNames.indexOf(Tables.Profiles.COMMENTS)));
            result.setForumPosts(c.getInt(columnNames.indexOf(Tables.Profiles.FORUM_POSTS)));
            result.setGender(c.getString(columnNames.indexOf(Tables.Profiles.GENDER)));
            result.setJoinDate(c.getString(columnNames.indexOf(Tables.Profiles.JOIN_DATE)));
            result.setAccessRank(c.getString(columnNames.indexOf(Tables.Profiles.ACCESS_RANK)));
            result.setAnimeListView(c.getInt(columnNames.indexOf(Tables.Profiles.ANIME_LIST_VIEW)));
            result.setMangaListView(c.getInt(columnNames.indexOf(Tables.Profiles.MANGA_LIST_VIEW)));
            result.setAnimeCompatibility(c.getString(columnNames.indexOf(Tables.Profiles.ANIME_COMPATIBILITY)));
            result.setMangaCompatibility(c.getString(columnNames.indexOf(Tables.Profiles.MANGA_COMPATIBILITY)));
            result.setAnimeCompatibilityValue(c.getString(columnNames.indexOf(Tables.Profiles.ANIME_COMPATIBILITY_VALUE)));
            result.setMangaCompatibilityValue(c.getString(columnNames.indexOf(Tables.Profiles.MANGA_COMPATIBILITY_VALUE)));
        }
        return result;
    }

    public int getGenderInt() {
        String[] gender = {
                "Female",
                "Male"
        };
        return Arrays.asList(gender).indexOf(getGender());
    }
}
