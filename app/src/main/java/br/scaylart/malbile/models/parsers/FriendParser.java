package br.scaylart.malbile.models.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import br.scaylart.malbile.models.Profile;
import br.scaylart.malbile.models.ProfileDetails;
import br.scaylart.malbile.models.User;

public class FriendParser {
    public static ArrayList<User> parse(String htmlContent) throws IOException
    {
        ArrayList<User> users = new ArrayList<>();

        Document contents = Jsoup.parse(htmlContent);

        for (Element friendHolder : contents.select("div.friendHolder")) {
            User friend = new User();
            Profile profile = new Profile();
            ProfileDetails details = new ProfileDetails();
            Elements divFriends = friendHolder.select("div.friendBlock").first()
                    .select("div");
            Element picSurround = divFriends.get(0).select("img").first();
            profile.setAvatarUrl(picSurround.attr("src").toString());
            friend.setUsername(divFriends.get(2).text());
            details.setLastOnline(divFriends.get(3).text());

            profile.setDetails(details);
            friend.setProfile(profile);

            users.add(friend);
        }

        return users;
    }
}
