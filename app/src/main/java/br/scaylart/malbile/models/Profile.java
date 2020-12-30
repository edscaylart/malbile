package br.scaylart.malbile.models;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import br.scaylart.malbile.controllers.databases.Tables;
import lombok.Getter;
import lombok.Setter;

public class Profile extends GlobalParcelable {
    @Getter @Setter private int id;
    @Getter @Setter private String avatarUrl;
    @Getter @Setter private boolean friend;

    @Getter @Setter private ProfileDetails details;
    @Getter @Setter private ProfileAnimeStats animeStats;
    @Getter @Setter private ProfileMangaStats mangaStats;

    public static Profile fromCursor(Cursor c) {
        Profile result = new Profile();

        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setAvatarUrl(c.getString(columnNames.indexOf(Tables.Profiles.AVATAR_URL)));
        result.setDetails(ProfileDetails.fromCursor(c));
        result.setAnimeStats(ProfileAnimeStats.fromCursor(c));
        result.setMangaStats(ProfileMangaStats.fromCursor(c));
        return result;
    }
}
