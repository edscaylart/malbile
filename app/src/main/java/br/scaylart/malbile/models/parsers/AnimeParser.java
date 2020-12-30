package br.scaylart.malbile.models.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.RelatedRecord;

public class AnimeParser {
    public static Anime parse(String htmlContent, Anime record) throws IOException {
        Anime anime;
        if (record == null) {
            anime = new Anime();
        } else {
            anime = record;
        }

        Document contents = Jsoup.parse(htmlContent);

        // Anime ID.
        // Example:
        // <input type="hidden" name="aid" value="5114">
        anime.setId(Integer.parseInt(contents.select("input[name=aid]").first().attr("value")));

        // Title and rank.
        // Example:
        // <h1><div style="float: right; font-size: 13px;">Ranked #1</div>Fullmetal Alchemist: Brotherhood</h1>
        anime.setRank(contents.select("div#contentWrapper").select("span:matchesOwn(Ranked #)").first().text().replace("Ranked #", "").trim());
        anime.setTitle(contents.select("h1.h1").first().text().trim());

        Element left_column = contents.select("div#content table tbody tr td.borderClass").first();

        // Title Image
        // Example:
        // <a href="/anime/5114/Fullmetal_Alchemist:_Brotherhood/pic&pid=47421"><img src="http://cdn.myanimelist.net/images/anime/5/47421.jpg" alt="Fullmetal Alchemist: Brotherhood" align="center"></a>
        anime.setImageUrl(left_column.select("div a img").first().attr("src"));


        // Alternative Titles section.
        // Example:
        // <h2>Alternative Titles</h2>
        // <div class="spaceit_pad"><span class="dark_text">English:</span> Fullmetal Alchemist: Brotherhood</div>
        // <div class="spaceit_pad"><span class="dark_text">Synonyms:</span> Hagane no Renkinjutsushi (2009), Fullmetal Alchemist (2009), FMA</div>
        // <div class="spaceit_pad"><span class="dark_text">Japanese:</span> ??????</div><br />

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
        anime.setOtherTitles(other_titles);

        // Information section.
        // Example:
        // <h2>Information</h2>
        // <div><span class="dark_text">Type:</span> TV</div>
        // <div class="spaceit"><span class="dark_text">Episodes:</span> 64 </div>
        // <div><span class="dark_text">Status:</span> Finished Airing</div>
        // <div class="spaceit"><span class="dark_text">Aired:</span> Apr  5, 2009 to Jul  4, 2010</div>
        // <div>
        //   <span class="dark_text">Producers:</span>
        //   <a href="/anime.php?p=4">Bones</a>,
        //   <a href="/anime.php?p=17">Aniplex</a>,
        //   <a href="/anime.php?p=58">Square Enix</a>,
        //   <a href="/anime.php?p=102">FUNimation Entertainment</a><sup><small>L</small></sup>,
        //   <a href="/anime.php?p=143">Mainichi Broadcasting</a>,
        //   <a href="/anime.php?p=1155">Studio Moriken</a>
        // </div>
        // <div class="spaceit">
        //   <span class="dark_text">Genres:</span>
        //   <a href="/anime.php?genre[]=1">Action</a>,
        //   <a href="/anime.php?genre[]=2">Adventure</a>,
        //   <a href="/anime.php?genre[]=8">Drama</a>,
        //   <a href="/anime.php?genre[]=10">Fantasy</a>,
        //   <a href="/anime.php?genre[]=16">Magic</a>,
        //   <a href="/anime.php?genre[]=27">Shounen</a>,
        //   <a href="/anime.php?genre[]=38">Military</a>
        // </div>
        // <div><span class="dark_text">Duration:</span>24 min. per episode</div>
        // <div class="spaceit"><span class="dark_text">Rating:</span>R - 17+ (violence & profanity)</div>

        //Type:
        Element el_type = left_column.select("span:matchesOwn(Type:)").first();
        if (el_type != null) {
            anime.setTypeByDesc(el_type.parent().text().replace("Type:", "").trim());
        }

        //Episodes:
        Element el_episodes = left_column.select("span:matchesOwn(Episodes:)").first();
        if (el_episodes != null) {
            String episode = el_episodes.parent().text().replace("Episodes:", "").trim();
            anime.setEpisodes(episode.toLowerCase().equals("unknown") ? 0 : Integer.parseInt(episode));
        }

        //Status:
        Element el_status = left_column.select("span:matchesOwn(Status:)").first();
        if (el_status != null) {
            anime.setStatusByDesc(el_status.parent().text().replace("Status:", "").trim());
        }

        //Aired:
        Element el_aired = left_column.select("span:matchesOwn(Aired:)").first();
        if (el_aired != null) {
            String htmlAired = el_aired.parent().text();
            if (htmlAired.contains("to")) {
                String[] aired = el_aired.parent().text().replace("Aired:", "").split(" to ");
                anime.setStartDate(aired[0].trim());
                anime.setEndDate(aired[1].trim());
            } else {
                anime.setStartDate(el_aired.parent().text().replace("Aired:", "").trim());
            }
        }

        // Genres:
        Element el_genres = left_column.select("span:matchesOwn(Genres:)").first();
        if (el_genres != null) {
            ArrayList<RelatedRecord> relGenres = new ArrayList<>();
            for (Element genres : el_genres.parent().select("a")) {
                RelatedRecord g = new RelatedRecord();
                g.setId(anime.getId(), BaseService.ListType.ANIME);
                g.setUrl(genres.attr("href").split(".net")[1]);
                g.setTitle(genres.text());
                relGenres.add(g);
            }
            if (relGenres.size() > 0)
                anime.setGenres(relGenres);//el_genres.parent().text().replace("Genres:", "").trim());
        }

        // Classification:
        Element el_rating = left_column.select("span:matchesOwn(Rating:)").first();
        if (el_rating != null) {
            anime.setClassification(el_rating.parent().text().replace("Rating:", "").trim());
        }

        // Statistics
        // Example:
        // <h2>Statistics</h2>
        // <div>
        //   <span class="dark_text">Score:</span> 9.24<sup><small>1</small></sup>
        //   <small>(scored by 250809 users)</small>
        // </div>
        // <div class="spaceit"><span class="dark_text">Ranked:</span> #1<sup><small>2</small></sup></div>
        // <div><span class="dark_text">Popularity:</span> #5</div>
        // <div class="spaceit"><span class="dark_text">Members:</span> 405,870</div>
        // <div><span class="dark_text">Favorites:</span> 41,350</div>

        // Score:
        Element el_score = left_column.select("span:matchesOwn(Score:)").first();
        if (el_score != null) {
            String sScore = el_score.parent().text().replace("Score:", "").trim();
            anime.setMembersScore(sScore.substring(0, sScore.indexOf("(")).trim());
        }

        // Members:
        Element el_members = left_column.select("span:matchesOwn(Members:)").first();
        if (el_members != null) {
            anime.setMembersCount(Integer.parseInt(el_members.parent().text().replace("Members:", "").trim().replace(",", "")));
        }

        // Favorites:
        Element el_favorites = left_column.select("span:matchesOwn(Favorites:)").first();
        if (el_favorites != null) {
            anime.setFavoritedCount(Integer.parseInt(el_favorites.parent().text().replace("Favorites:", "").trim().replace(",", "")));
        }

        // -
        // Extract from sections on the right column: Synopsis, Related Anime
        // Recommendations.
        // -

        //List a = new ArrayList<>();
        //a.add(new String[]{"desc","url"});
        // Synopsis
        // Example:
        // <td valign="top">
        // <h2>Synopsis</h2>
        // In this world there exist alchemists, people who study and perform the art of alchemical transmutation—to manipulate objects and transform one object into another. They are bounded by the basic law of alchemy: in order to gain something you have to sacrifice something of the same value.<br />
        // <br />The main character is the famous alchemist Edward Elric—also known as the Fullmetal Alchemist—who almost lost his little brother, Alphonse, in an alchemical accident. Edward managed to attach his brother&apos;s soul to a large suit of armor. While he did manage to save his brother&apos;s life, he paid the terrible price of his limbs.<br />
        // <br />To get back what they&apos;ve lost, the brothers embark on a journey to find the Philosopher&apos;s Stone that is said to amplify the powers of an alchemist enormously; however, on the way, they start uncovering a conspiracy that could endanger the entire nation, and they realize the misfortunes brought upon by the Philosopher&apos;s Stone.<br />
        // <br />Fullmetal Alchemist: Brotherhood is a re-telling of the story from the manga&apos;s point of view.
        // </td>

        Element el_synopsis = contents.select("h2:matchesOwn(Synopsis)").first();
        if (el_synopsis != null) {
            el_synopsis.html(el_synopsis.parent().html().replace(el_synopsis.html(), ""));
            anime.setSynopsis(el_synopsis.text().replace("Edit Synopsis",""));
        }

        // Related Anime
        // Example:
        // <td>
        //   <br />
        //   <h2>Related Anime</h2>
        //   Adaptation: <a href="/manga/25/Fullmetal_Alchemist">Fullmetal Alchemist</a><br />
        //   Alternative version: <a href="/anime/121/Fullmetal_Alchemist">Fullmetal Alchemist</a><br />
        //   Side story: <a href="/anime/6421/Fullmetal_Alchemist:_Brotherhood_Specials">Fullmetal Alchemist: Brotherhood Specials</a>,
        //               <a href="/anime/9135/Fullmetal_Alchemist:_Milos_no_Seinaru_Hoshi">Fullmetal Alchemist: Milos no Seinaru Hoshi</a><br />
        //   Spin-off: <a href="/anime/7902/Fullmetal_Alchemist:_Brotherhood_-_4-Koma_Theater">Fullmetal Alchemist: Brotherhood - 4-Koma Theater</a><br />
    /*
        Element related = contents.select("h2:matchesOwn(Related Anime)").first();
        if (related != null) {
            // Adaptation
            Matcher matcherAdaptation = pregMatch("Adaptation\\: ?(<a .+?)\\<br", related.parent().html());
            if (matcherAdaptation.find()) {
                List adaptations = new ArrayList();

                for (Element el : Jsoup.parse(matcherAdaptation.group(1)).select("a")) {
                    adaptations.add(new String[]{el.text(), el.attr("href")});
                }
                //anime.setMangaAdaptations(adaptations);
            }
            // Alternative version
            Matcher matcherAlternative = pregMatch("Alternative version\\: ?(<a .+?)\\<br", related.parent().html());
            if (matcherAlternative.find()) {
                List alternatives = new ArrayList();

                for (Element el : Jsoup.parse(matcherAlternative.group(1)).select("a")) {
                    alternatives.add(new String[]{el.text(), el.attr("href")});
                }
                //anime.setAlternativeVersions(alternatives);
            }
            // Prequel
            Matcher matcherPrequel = pregMatch("Prequel\\: ?(<a .+?)\\<br", related.parent().html());
            if (matcherPrequel.find()) {
                List prequels = new ArrayList();

                for (Element el : Jsoup.parse(matcherPrequel.group(1)).select("a")) {
                    prequels.add(new String[]{el.text(), el.attr("href")});
                }
                //anime.setPrequels(prequels);
            }
            // Sequel
            Matcher matcherSequel = pregMatch("Sequel\\: ?(<a .+?)\\<br", related.parent().html());
            if (matcherSequel.find()) {
                List sequels = new ArrayList();

                for (Element el : Jsoup.parse(matcherSequel.group(1)).select("a")) {
                    sequels.add(new String[]{el.text(), el.attr("href")});
                }
                //anime.setSequels(sequels);
            }
            // Side story
            Matcher matcherSide = pregMatch("Side story\\: ?(<a .+?)\\<br", related.parent().html());
            if (matcherSide.find()) {
                List side_stories = new ArrayList();

                for (Element el : Jsoup.parse(matcherSide.group(1)).select("a")) {
                    side_stories.add(new String[]{el.text(), el.attr("href")});
                }
                //anime.setSideStories(side_stories);
            }
            // Spin-off
            Matcher matcherSpinOff = pregMatch("Spin\\-off\\: ?(<a .+?)\\<br", related.parent().html());
            if (matcherSpinOff.find()) {
                List spin_offs = new ArrayList();

                for (Element el : Jsoup.parse(matcherSpinOff.group(1)).select("a")) {
                    spin_offs.add(new String[]{el.text(), el.attr("href")});
                }
                //anime.setSpinOffs(spin_offs);
            }
        }
        */

        return anime;
    }

    public static Matcher pregMatch(String expression, String content) {
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(content);
        return matcher;
    }
}
