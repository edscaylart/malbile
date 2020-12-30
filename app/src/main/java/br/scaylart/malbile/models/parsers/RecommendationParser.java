package br.scaylart.malbile.models.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import br.scaylart.malbile.models.Recommendation;

public class RecommendationParser {
    public static ArrayList<Recommendation> parse(String htmlContent) throws IOException {
        ArrayList<Recommendation> records = new ArrayList<Recommendation>();

        Document contents = Jsoup.parse(htmlContent);

        //for (Element elRec : contents.select("div#horiznav_nav").first().parent().select("div > div.borderClass")) {
        for (Element elRec : contents.select("div#horiznav_nav").first().parent().children()) {
            if (elRec.tagName().equals("div") && elRec.className().equals("borderClass")) {
                Recommendation recommendation = new Recommendation();

                recommendation.setThumbnailUrl(elRec.select("img").first().attr("src").replace("t.jpg", ".jpg"));

                Element elTd = elRec.select("> table > tbody > tr > td").get(1);

                String id = elTd.select("div > a > strong").first().parent().attr("href").replace("/anime/", "").replace("/manga/", "").split("/")[0];

                recommendation.setId(Integer.parseInt(id));
                recommendation.setTitle(elTd.select("div > a > strong").first().text());
                recommendation.setCommentary(elTd.select("div.borderClass > div.spaceit_pad").first().text());
                recommendation.setRecommendedBy(elTd.select("div.borderClass > div.spaceit_pad > a").first().text());

                records.add(recommendation);
            }
        }

        return records;
    }
}
