package br.scaylart.malbile.models.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import br.scaylart.malbile.models.Message;

public class MessageParser {
    public static ArrayList<Message> parse(String htmlContent) throws IOException {
        ArrayList<Message> messages = new ArrayList<Message>();

        Document contents = Jsoup.parse(htmlContent);

        for (Element messageEl : contents.select("div.message")) {
            Message msg = new Message();

            msg.setId(Integer.parseInt(messageEl.attr("id").replace("message_","").trim()));
            msg.setRead(messageEl.attr("class").contains("row_not_highlighted"));
            msg.setUsername(messageEl.select("div.mym_user").select("a").text().trim());
            msg.setDateMsg(messageEl.select("div.mym_user").select("small").text().trim());
            msg.setReadId(Integer.parseInt(messageEl.select("div.mym_subject").select("a").first().attr("href").replace("?go=read&id=", "").trim()));
            msg.setTitle(messageEl.select("div.mym_subject").select("a").first().text().trim());
            msg.setShortMessage(messageEl.select("div.mym_subject").select("a.lightLink").first().text().trim());

            String actReply = messageEl.select("div.mym_actions").select("a").first().attr("href")
                    .replace("?go=send&replyid=" + String.valueOf(msg.getId()) + "&threadid=", "")
                    .replace("&toname=" + msg.getUsername(), "");

            msg.setReplyId(Integer.parseInt(actReply));

            messages.add(msg);
        }

        return messages;
    }
}
