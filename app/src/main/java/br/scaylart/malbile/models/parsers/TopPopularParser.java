package br.scaylart.malbile.models.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.BaseRecord;
import br.scaylart.malbile.models.Manga;

public class TopPopularParser {
    public static ArrayList<BaseRecord> parse(String htmlContent, String type) throws IOException {
        ArrayList<BaseRecord> records = new ArrayList<BaseRecord>();

        Document contents = Jsoup.parse(htmlContent);

        Elements mediaItens = contents.select("div#content div > table > tbody > tr");

        for (Element item : mediaItens) {
            records.add(type.equals("anime") ? parseAnime(item) : parseManga(item));
        }

        return records;
    }

    private static Anime parseAnime(Element item) {
        Anime record = new Anime();

        record.setRank(item.select("td.borderClass > span.lightLink").first().text().trim());

        record.setId(Integer.parseInt(item.select("a").first().attr("id").replace("#area", "")));
        record.setTitle(item.select("strong").first().text());
        record.setImageUrl(item.select("img").first().attr("src").replace("t.jpg", ".jpg"));
        record.setMembersCount(Integer.parseInt(item.select("div.spaceit_pad > span.lightLink").first().text().replace("members", "").trim().replace(",", "")));

        String[] details = item.select("div.spaceit_pad").first().text().replace(item.select("div.spaceit_pad > span").first().text(), "").split(", ");

        record.setTypeByDesc(details[0].trim());
        record.setEpisodes(details[1].contains("?") ? 0 : Integer.parseInt(details[1].replace("eps", "").trim()));
        record.setMembersScore(details[2].replace("scored", "").trim());

        return record;
    }

    private static Manga parseManga(Element item) {
        Manga record = new Manga();

        record.setRank(item.select("td.borderClass > span.lightLink").first().text().trim());

        record.setId(Integer.parseInt(item.select("a").first().attr("id").replace("#area", "")));
        record.setTitle(item.select("strong").first().text());
        record.setImageUrl(item.select("img").first().attr("src").replace("t.jpg", ".jpg"));
        record.setMembersCount(Integer.parseInt(item.select("div.spaceit_pad > span.lightLink").first().text().replace("members", "").trim().replace(",", "")));

        String[] details = item.select("div.spaceit_pad").first().text().replace(item.select("div.spaceit_pad > span").first().text(), "").split(", ");

        record.setVolumes(details[0].contains("?") ? 0 : Integer.parseInt(details[0].replace("volumes", "").trim()));
        record.setMembersScore(details[1].replace("scored", "").trim());

        return record;
    }
}
