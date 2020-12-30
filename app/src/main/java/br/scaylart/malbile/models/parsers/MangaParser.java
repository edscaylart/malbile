package br.scaylart.malbile.models.parsers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.models.RelatedRecord;

public class MangaParser {
    public static Manga parse(String htmlContent, Manga record) throws IOException {
        Manga manga;
        if (record == null) {
            manga = new Manga();
        } else {
            manga = record;
        }

        Document contents = Jsoup.parse(htmlContent);

        // Manga ID.
        // Example:
        // <input type="hidden" name="mid" value="25">
        manga.setId(Integer.parseInt(contents.select("input[name=mid]").first().attr("value")));

        // Title and rank.
        // Example:
        // <h1>
        //      <div style="float: right; font-size: 13px;">Ranked #3</div>Fullmetal Alchemist
        //      <span style="font-weight: normal;"><small>(Manga)</small></span>
        // </h1>
        manga.setRank(contents.select("div#contentWrapper").select("span:matchesOwn(Ranked #)").first().text().replace("Ranked #", "").trim());

        Element el_title = contents.select("h1.h1").first();
        manga.setTitle(el_title.select("span[itemprop=name]").text());

        Element left_column = contents.select("div#content table tbody tr td.borderClass").first();

        // Title Image
        // Example:
        // <a href="/manga/25/Fullmetal_Alchemist/pic&pid=27600"><img src="http://cdn.myanimelist.net/images/manga/1/27600.jpg" alt="Fullmetal Alchemist" align="center"></a>
        manga.setImageUrl(left_column.select("div a img").first().attr("src"));

        // Alternative Titles section.
        // Example:
        // <h2>Alternative Titles</h2>
        // <div class="spaceit_pad"><span class="dark_text">English:</span> Fullmetal Alchemist</div>
        // <div class="spaceit_pad"><span class="dark_text">Synonyms:</span> Full Metal Alchemist, Hagane no Renkinjutsushi, FMA, Fullmetal Alchemist Gaiden</div>
        // <div class="spaceit_pad"><span class="dark_text">Japanese:</span> 鋼の錬金術師</div>

        HashMap<String, ArrayList<String>> other_titles = new HashMap<String, ArrayList<String>>();

        // English:
        Element el_english = left_column.select("span:matchesOwn(English:)").first();
        if (el_english != null) {
            List others = Arrays.asList(el_english.parent().text().replace("English:", "").trim().split(", "));
            other_titles.put("english", new ArrayList(others));
        }
        // Synonyms:
        Element el_synonyms = left_column.select("span:matchesOwn(Synonyms:)").first();
        if (el_synonyms != null) {
            List others = Arrays.asList(el_synonyms.parent().text().replace("Synonyms:", "").trim().split(", "));
            other_titles.put("synonyms", new ArrayList(others));
        }
        // Japanese:
        Element el_japanese = left_column.select("span:matchesOwn(Japanese:)").first();
        if (el_japanese != null) {
            List others = Arrays.asList(el_japanese.parent().text().replace("Japanese:", "").trim().split(", "));
            other_titles.put("japanese", new ArrayList(others));
        }
        manga.setOtherTitles(other_titles);

        // Information section.
        // Example:
        // <h2>Information</h2>
        // <div><span class="dark_text">Type:</span> Manga</div>
        // <div class="spaceit"><span class="dark_text">Volumes:</span> 27 </div>
        // <div><span class="dark_text">Chapters:</span> 109 </div>
        // <div class="spaceit"><span class="dark_text">Status:</span> Finished</div>
        // <div><span class="dark_text">Published:</span> Jul  12, 2001 to Jun  11, 2010</div>
        // <div class="spaceit"><span class="dark_text">Genres:</span>
        //      <a href="/manga.php?genre[]=1">Action</a>,
        //      <a href="/manga.php?genre[]=2">Adventure</a>,
        //      <a href="/manga.php?genre[]=4">Comedy</a>,
        //      <a href="/manga.php?genre[]=8">Drama</a>,
        //      <a href="/manga.php?genre[]=27">Shounen</a>,
        //      <a href="/manga.php?genre[]=38">Military</a>
        // </div>
        // <div><span class="dark_text">Authors:</span>
        //      <a href="/people/1874/Hiromu_Arakawa">Arakawa, Hiromu</a> (Story & Art)
        // </div>

        // Type:
        Element el_type = left_column.select("span:matchesOwn(Type:)").first();
        if (el_type != null) {
            manga.setTypeByDesc(el_type.parent().text().replace("Type:", "").trim());
        }

        // Volumes:
        Element el_volumes = left_column.select("span:matchesOwn(Volumes:)").first();
        if (el_volumes != null) {
            String volume = el_volumes.parent().text().replace("Volumes:", "").trim();
            manga.setVolumes(volume.toLowerCase().equals("unknown") ? 0 : Integer.parseInt(volume));
        }

        // Chapters:
        Element el_chapters = left_column.select("span:matchesOwn(Chapters:)").first();
        if (el_chapters != null) {
            String chapters = el_chapters.parent().text().replace("Chapters:", "").trim();
            manga.setChapters(chapters.toLowerCase().equals("unknown") ? 0 : Integer.parseInt(chapters));
        }

        // Status:
        Element el_status = left_column.select("span:matchesOwn(Status:)").first();
        if (el_status != null) {
            manga.setStatusByDesc(el_status.parent().text().replace("Status:", "").trim());
        }

        //Published:
        Element el_published = left_column.select("span:matchesOwn(Published:)").first();
        if (el_published != null) {
            String htmlAired = el_published.parent().text();
            if (htmlAired.contains("to")) {
                String[] aired = el_published.parent().text().replace("Published:", "").split(" to ");
                manga.setStartDate(aired[0].trim());
                manga.setEndDate(aired[1].trim());
            }else {
                manga.setStartDate(el_published.parent().text().replace("Published:", "").trim());
            }
        }

        // Genres:
        Element el_genres = left_column.select("span:matchesOwn(Genres:)").first();
        if (el_genres != null) {
            ArrayList<RelatedRecord> relGenres = new ArrayList<>();
            for (Element genres : el_genres.parent().select("a")) {
                RelatedRecord g = new RelatedRecord();
                g.setId(manga.getId(), BaseService.ListType.MANGA);
                g.setUrl(genres.attr("href").toString());
                g.setTitle(genres.text());
                relGenres.add(g);
            }
            if (relGenres.size() > 0)
                manga.setGenres(relGenres);//el_genres.parent().text().replace("Genres:", "").trim());
        }

        // Statistics
        // Example:
        // <h2>Statistics</h2>
        // <div>
        //      <span class="dark_text">Score:</span> 9.10<sup><small>1</small></sup>
        //      <small>(scored by 50174 users)</small>
        // </div>
        // <div class="spaceit"><span class="dark_text">Ranked:</span> #3<sup><small>2</small></sup></div>
        // <div><span class="dark_text">Popularity:</span> #7</div>
        // <div class="spaceit"><span class="dark_text">Members:</span> 78,835</div>
        // <div><span class="dark_text">Favorites:</span> 13,517</div>

        // Score:
        Element el_score = left_column.select("span:matchesOwn(Score:)").first();
        if (el_score != null) {
            String sScore = el_score.parent().text().replace("Score:", "").trim();
            manga.setMembersScore(sScore.substring(0, sScore.indexOf("(")).trim());
        }

        // Members:
        Element el_members = left_column.select("span:matchesOwn(Members:)").first();
        if (el_members != null) {
            manga.setMembersCount(Integer.parseInt(el_members.parent().text().replace("Members:", "").trim().replace(",", "")));
        }

        // Favorites:
        Element el_favorites = left_column.select("span:matchesOwn(Favorites:)").first();
        if (el_favorites != null) {
            manga.setFavoritedCount(Integer.parseInt(el_favorites.parent().text().replace("Favorites:", "").trim().replace(",", "")));
        }

        // -
        // Extract from sections on the right column: Synopsis, Related Manga
        // Recommendations.
        // -

        // Synopsis
        // Example:
        // <td valign="top">
        // <h2>Synopsis</h2>
        // The rules of alchemy state that to gain something, one must lose something of equal value.
        // Alchemy is the process of taking apart and reconstructing an object into a different entity,
        // with the rules of alchemy to govern this procedure. However, there exists an object that can
        // bring any alchemist above these rules, the object known as the Philosopher's Stone.
        // The young Edward Elric is a particularly talented alchemist who through an accident years
        // back lost his younger brother Alphonse and one of his legs. Sacrificing one of his arms as well,
        // he used alchemy to bind his brother's soul to a suit of armor. This lead to the beginning of their
        // journey to restore their bodies, in search for the legendary Philosopher's Stone.
        // </td>

        Element el_synopsis = contents.select("h2:matchesOwn(Synopsis)").first();
        el_synopsis.parent().removeClass("border_top");
        if (el_synopsis != null) {
            el_synopsis.parent().html(el_synopsis.parent().html().replace(el_synopsis.text(), "").replace("<h2></h2>", ""));
            manga.setSynopsis(el_synopsis.parent().text().replace("Edit Synopsis",""));
        }

        // Related Manga
        // Example:
        // <td>
        //      <br />
        //      <h2>Related Manga</h2>
        //      Side story: <a href="/manga/4658/Fullmetal_Alchemist">Fullmetal Alchemist</a><br>
        //      Alternative version: <a href="/manga/32409/Full_Metal_Alchemist:_The_Prototype">Full Metal Alchemist: The Prototype</a><br>
        //      Adaptation: <a href="/anime/121/Fullmetal_Alchemist">Fullmetal Alchemist</a>,
        //                  <a href="/anime/5114/Fullmetal_Alchemist:_Brotherhood">Fullmetal Alchemist: Brotherhood</a>
        //      <br>
/*
        Element related = contents.select("h2:matchesOwn(Related Manga)").first();
        if (related != null) {
            // Adaptation
            Matcher matcherAdaptation = pregMatch("Adaptation\\: ?(<a .+?)\\<br", related.parent().html());
            if (matcherAdaptation.find()) {
                List adaptations = new ArrayList();

                for (Element el : Jsoup.parse(matcherAdaptation.group(1)).select("a")) {
                    adaptations.add(new String[]{el.text(), el.attr("href")});
                }
                //manga.setAnime_adaptations(adaptations);
            }
            // Alternative version
            Matcher matcherAlternative = pregMatch("Alternative version\\: ?(<a .+?)\\<br", related.parent().html());
            if (matcherAlternative.find()) {
                List alternatives = new ArrayList();

                for (Element el : Jsoup.parse(matcherAlternative.group(1)).select("a")) {
                    alternatives.add(new String[]{el.text(), el.attr("href")});
                }
                //manga.setAlternative_versions(alternatives);
            }
            // Side story
            Matcher matcherSide = pregMatch("Side story\\: ?(<a .+?)\\<br", related.parent().html());
            if (matcherSide.find()) {
                List side_stories = new ArrayList();

                for (Element el : Jsoup.parse(matcherSide.group(1)).select("a")) {
                    side_stories.add(new String[]{el.text(), el.attr("href")});
                }
                //manga.setSide_stories(side_stories);
            }
        }
*/
        return manga;
    }

    public static Matcher pregMatch(String expression, String content) {
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(content);
        return matcher;
    }
}
