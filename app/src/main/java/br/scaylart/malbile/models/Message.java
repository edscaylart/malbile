package br.scaylart.malbile.models;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import br.scaylart.malbile.controllers.databases.Tables;
import lombok.Getter;
import lombok.Setter;

public class Message extends GlobalParcelable {
    @Getter @Setter @SerializedName("id") private int id;
    @Getter @Setter @SerializedName("username") private String username;
    @Getter @Setter @SerializedName("time") private String dateMsg;
    @Getter @Setter @SerializedName("subject") private String title;
    @Getter @Setter @SerializedName("preview") private String shortMessage;
    @Getter @Setter @SerializedName("message") private String fullMessage;
    @Getter @Setter @SerializedName("read") private boolean read;
    @Getter @Setter @SerializedName("action_id") private int readId;
    @Getter @Setter @SerializedName("thread_id") private int replyId;

    public static Message fromCursor(Cursor c) {
        Message result = new Message();
        List<String> columnNames = Arrays.asList(c.getColumnNames());
        result.setId(c.getInt(columnNames.indexOf(Tables.COLUMN_ID)));
        result.setUsername(c.getString(columnNames.indexOf(Tables.Messages.USERNAME)));
        result.setDateMsg(c.getString(columnNames.indexOf(Tables.Messages.DATE_MSG)));
        result.setTitle(c.getString(columnNames.indexOf(Tables.Messages.TITLE)));
        result.setShortMessage(c.getString(columnNames.indexOf(Tables.Messages.SHORT_MESSAGE)));
        result.setFullMessage(c.getString(columnNames.indexOf(Tables.Messages.FULL_MESSAGE)));
        result.setRead(c.getInt(columnNames.indexOf(Tables.Messages.READ)) == 1);
        result.setReadId(c.getInt(columnNames.indexOf(Tables.Messages.READ_ID)));
        result.setReplyId(c.getInt(columnNames.indexOf(Tables.Messages.REPLY_ID)));

        return result;
    }
}
