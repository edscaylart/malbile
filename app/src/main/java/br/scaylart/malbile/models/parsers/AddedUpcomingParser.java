package br.scaylart.malbile.models.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import java.io.IOException;

import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.BaseRecord;
import br.scaylart.malbile.models.Manga;

public class AddedUpcomingParser {
    public static ArrayList<BaseRecord> parse(String htmlContent, String type) throws IOException {
        ArrayList<BaseRecord> records = new ArrayList<BaseRecord>();

        Document contents = Jsoup.parse(htmlContent);

        boolean menuBar = true;

        Elements mediaItens = contents.select("div#content > table > tbody > tr");

        for (Element item : mediaItens) {
            if (menuBar)
                menuBar = false;
            else
                records.add(type.equals("anime") ? parseAnime(item) : parseManga(item));
        }

        return records;
    }

    private static Anime parseAnime(Element item) {
        Anime record = new Anime();

        record.setId(Integer.parseInt(item.select("a").first().attr("id").replace("#sarea", "")));
        record.setTitle(item.select("strong").first().text());
        record.setImageUrl(item.select("img").first().attr("src").replace("t.jpg", ".jpg"));
        record.setTypeByDesc(item.select("td").get(2).text().trim());

        String initialDate = item.select("td").get(3).text().trim();

        if (!initialDate.equals("-")) {
            String[] startDate = initialDate.split("-");

            if (startDate[2].length() == 2) {
                startDate[2] = fixShortYear(startDate[2]);
            }

            if (!startDate[0].equals("?"))
                record.setStartDate(startDate[2] + "-" + startDate[0] + "-"  + startDate[1]);
        }

        return record;
    }

    private static Manga parseManga(Element item) {
        Manga record = new Manga();

        record.setId(Integer.parseInt(item.select("a").first().attr("id").replace("#sarea", "")));
        record.setTitle(item.select("strong").first().text());
        record.setImageUrl(item.select("img").first().attr("src").replace("t.jpg", ".jpg"));
        record.setTypeByDesc(item.select("td").get(2).text().trim());

        String initialDate = item.select("td").get(3).text().trim();

        if (!initialDate.equals("-")) {
            String[] startDate = initialDate.split("-");

            if (startDate[2].length() == 2) {
                startDate[2] = fixShortYear(startDate[2]);
            }

            if (!startDate[0].equals("?"))
                record.setStartDate(startDate[2] + startDate[0] + startDate[1]);
        }

        return record;
    }

    private static String fixShortYear(String year) {
        if (Integer.parseInt(year) >= 30)
            return "19" + year;
        else
            return "20" + year;
    }
}
