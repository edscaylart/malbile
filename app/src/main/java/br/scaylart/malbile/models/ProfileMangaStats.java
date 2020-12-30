package br.scaylart.malbile.models;

import android.database.Cursor;

import java.util.Arrays;
import java.util.List;

import br.scaylart.malbile.controllers.databases.Tables;
import lombok.Getter;
import lombok.Setter;

public class ProfileMangaStats extends GlobalParcelable {
    @Getter @Setter private int completed;
    @Getter @Setter private int dropped;
    @Getter @Setter private int onHold;
    @Getter @Setter private int planToRead;
    @Getter @Setter private Double timeDays;
    @Getter @Setter private int totalEntries;
    @Getter @Setter private int reading;

    public static ProfileMangaStats fromCursor(Cursor c) {
        ProfileMangaStats result = new ProfileMangaStats();

        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setTimeDays(c.getDouble(columnNames.indexOf(Tables.Profiles.MANGA_TIME_DAYS)));
        result.setReading(c.getInt(columnNames.indexOf(Tables.Profiles.MANGA_READING)));
        result.setCompleted(c.getInt(columnNames.indexOf(Tables.Profiles.MANGA_COMPLETED)));
        result.setDropped(c.getInt(columnNames.indexOf(Tables.Profiles.MANGA_DROPPED)));
        result.setOnHold(c.getInt(columnNames.indexOf(Tables.Profiles.MANGA_HOLD)));
        result.setPlanToRead(c.getInt(columnNames.indexOf(Tables.Profiles.MANGA_PLANNED)));
        result.setTotalEntries(c.getInt(columnNames.indexOf(Tables.Profiles.MANGA_TOTAL_ENTRIES)));

        return result;
    }
}
