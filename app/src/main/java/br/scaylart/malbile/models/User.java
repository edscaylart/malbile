package br.scaylart.malbile.models;

import android.database.Cursor;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.scaylart.malbile.controllers.databases.Tables;
import lombok.Getter;
import lombok.Setter;

@Root(name = "user", strict = false)
public class User extends GlobalParcelable {
    @Element(name = "id", required = false)
    @Getter @Setter private int id;
    @Element(name = "username", required = false)
    @Getter @Setter private String username;
    @Getter @Setter private Profile profile;

    public static User fromCursor(Cursor c) {
        User result = new User();
        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setId(c.getInt(columnNames.indexOf(Tables.COLUMN_ID)));
        result.setUsername(c.getString(columnNames.indexOf(Tables.Profiles.USERNAME)));

        result.setProfile(Profile.fromCursor(c));
        return result;
    }

    public static boolean isDeveloperRecord(String name) {
        String[] developers = {
                "scaylart"
        };
        return Arrays.asList(developers).contains(name.toLowerCase(Locale.US));
    }
}
