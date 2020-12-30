package br.scaylart.malbile.models.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import br.scaylart.malbile.models.Profile;
import br.scaylart.malbile.models.ProfileAnimeStats;
import br.scaylart.malbile.models.ProfileDetails;
import br.scaylart.malbile.models.ProfileMangaStats;
import br.scaylart.malbile.models.User;

public class UserParser {
    public static User parse(String htmlContent, String username, boolean isUserMBL) throws IOException {
        User user = new User();

        Document contents = Jsoup.parse(htmlContent);

        Profile profile = new Profile();

        user.setUsername(username);

        Element left_side = contents.select("div.user-profile").first();

        profile.setAvatarUrl(left_side.select("div.user-image > img").first().attr("src"));

        /*boolean isFriend = true;
        for (Element href : left_side.select("a[href]")) {
            if (href.attr("href").contains("/myfriends.php?go=add"))
                isFriend = false;
        }
        profile.setFriend(isFriend);*/

        Elements user_details = left_side.select("ul.user-status");

        Elements stats_content = contents.select("div.container-right > div#statistics").select("div.stats");

        Element anime_stat = stats_content.select("div.anime").get(0);
        Element manga_stat = stats_content.select("div.manga").get(0);


        ProfileDetails profileDetails = parseDetail(user_details);

        /*
        if (!isUserMBL)
            profileDetails = parseCompatibility(anime_stat, manga_stat, profileDetails);
        */

        profile.setDetails(profileDetails);
        profile.setAnimeStats(parseAnimeStats(anime_stat));
        profile.setMangaStats(parseMangaStats(manga_stat));

        user.setProfile(profile);
        return user;
    }

    private static ProfileDetails parseDetail(Elements content) {
        ProfileDetails details = new ProfileDetails();

        for (Element element : content.select("li")) {
            Elements stats = element.select("span");

            if(stats != null && stats.size() > 1) {
                switch (stats.get(0).text().toString().toLowerCase().trim()) {
                    case "last online":
                        details.setLastOnline(stats.get(1).text());
                        break;
                    case "gender":
                        details.setGender(stats.get(1).text());
                        break;
                    case "birthday":
                        details.setBirthday(stats.get(1).text());
                        break;
                    case "location":
                        details.setLocation(stats.get(1).text());
                        break;
                    case "website":
                        details.setWebsite(stats.get(1).text());
                        break;
                    case "joined":
                        details.setJoinDate(stats.get(1).text());
                        break;
                    case "access rank":
                        details.setAccessRank(stats.get(1).text());
                        break;
                    case "anime list views":
                        details.setAnimeListView(stats.get(1).text() != null ? Integer.parseInt(stats.get(1).text().replace(",", "")) : 0);
                        break;
                    case "manga list views":
                        details.setMangaListView(stats.get(1).text() != null ? Integer.parseInt(stats.get(1).text().replace(",", "")) : 0);
                        break;
                    case "comments":
                        details.setComments(stats.get(1).text() != null ? Integer.parseInt(stats.get(1).text().replace(",", "")) : 0);
                        break;
                    case "forum posts":
                        int forumPost = 0;
                        if (stats.get(1).text() != null) {
                            String post = stats.get(1).text().trim();
                            forumPost = Integer.parseInt(post);
                        }
                        details.setForumPosts(forumPost);
                        break;
                }
            }
            //details.put(stats.get(0).text(), stats.get(1).text());
        }
        return details;
    }

    private static ProfileAnimeStats parseAnimeStats(Element content) {
        ProfileAnimeStats anime_stat = new ProfileAnimeStats();

        Element timeDays = content.select("div.stat-score").select("span:matchesOwn(Days:)").first();
        anime_stat.setTimeDays(Double.parseDouble(timeDays.parent().text().replace("Days:", "").trim()));

        for (Element element : content.select("ul.stats-status").select("li")) {
            switch (element.select("a").first().text().trim().toLowerCase()) {
                case "watching":
                    anime_stat.setWatching(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
                case "completed":
                    anime_stat.setCompleted(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
                case "on hold":
                    anime_stat.setOnHold(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
                case "dropped":
                    anime_stat.setDropped(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
                case "plan to watch":
                    anime_stat.setPlanToWatch(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
            }
        }

        Element totalEntries = content.select("ul.stats-data").select("span:matchesOwn(Total Entries)").first();
        anime_stat.setTotalEntries(Integer.parseInt(totalEntries.parent().text().replace("Total Entries", "").trim()));

        return anime_stat;
    }

    private static ProfileDetails parseCompatibility(Element contentAnime, Element contentManga, ProfileDetails details) {
        Elements elementAnime = contentAnime.select("div.spaceit > div");
        details.setAnimeCompatibility(elementAnime.get(0).text().toString().trim().replace(" :(", ""));
        details.setAnimeCompatibilityValue(contentAnime.select("div.spaceit").get(0).select("div").get(1).select("div").get(3).text().toString().trim().replace("%", ""));

        Elements elementManga = contentManga.select("div.spaceit > div");
        details.setMangaCompatibility(elementManga.get(0).text().toString().trim().replace(" :(", ""));
        details.setMangaCompatibilityValue(contentManga.select("div.spaceit").get(0).select("div").get(1).select("div").get(3).text().toString().trim().replace("%", ""));

        return details;
    }

    private static ProfileMangaStats parseMangaStats(Element content) {
        ProfileMangaStats manga_stat = new ProfileMangaStats();

        Element timeDays = content.select("div.stat-score").select("span:matchesOwn(Days:)").first();
        manga_stat.setTimeDays(Double.parseDouble(timeDays.parent().text().replace("Days:", "").trim()));

        for (Element element : content.select("ul.stats-status").select("li")) {
            switch (element.select("a").first().text().trim().toLowerCase()) {
                case "reading":
                    manga_stat.setReading(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
                case "completed":
                    manga_stat.setCompleted(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
                case "on hold":
                    manga_stat.setOnHold(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
                case "dropped":
                    manga_stat.setDropped(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
                case "plan to read":
                    manga_stat.setPlanToRead(Integer.parseInt(element.select("span").first().text().trim()));
                    break;
            }
        }

        Element totalEntries = content.select("ul.stats-data").select("span:matchesOwn(Total Entries)").first();
        manga_stat.setTotalEntries(Integer.parseInt(totalEntries.parent().text().replace("Total Entries", "").trim()));

        return manga_stat;
    }
}
