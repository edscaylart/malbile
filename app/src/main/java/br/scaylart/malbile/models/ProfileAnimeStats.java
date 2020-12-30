package br.scaylart.malbile.models;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import br.scaylart.malbile.controllers.databases.Tables;
import lombok.Getter;
import lombok.Setter;

public class ProfileAnimeStats extends GlobalParcelable {
    @Getter @Setter private int completed;
    @Getter @Setter private int dropped;
    @Getter @Setter private int onHold;
    @Getter @Setter private int planToWatch;
    @Getter @Setter private Double timeDays;
    @Getter @Setter private int totalEntries;
    @Getter @Setter private int watching;

    public static ProfileAnimeStats fromCursor(Cursor c) {
        ProfileAnimeStats result = new ProfileAnimeStats();

        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setTimeDays(c.getDouble(columnNames.indexOf(Tables.Profiles.ANIME_TIME_DAYS)));
        result.setWatching(c.getInt(columnNames.indexOf(Tables.Profiles.ANIME_WATCHING)));
        result.setCompleted(c.getInt(columnNames.indexOf(Tables.Profiles.ANIME_COMPLETED)));
        result.setDropped(c.getInt(columnNames.indexOf(Tables.Profiles.ANIME_DROPPED)));
        result.setOnHold(c.getInt(columnNames.indexOf(Tables.Profiles.ANIME_HOLD)));
        result.setPlanToWatch(c.getInt(columnNames.indexOf(Tables.Profiles.ANIME_PLANNED)));
        result.setTotalEntries(c.getInt(columnNames.indexOf(Tables.Profiles.ANIME_TOTAL_ENTRIES)));

        return result;
    }
}
