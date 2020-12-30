package br.scaylart.malbile.models.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import br.scaylart.malbile.models.Review;
import br.scaylart.malbile.models.ReviewRating;

public class ReviewParser {
    public static ArrayList<Review> parse(String htmlContent) throws IOException {
        ArrayList<Review> records = new ArrayList<Review>();

        Document contents = Jsoup.parse(htmlContent);

        // Element elMoreReviews = contents.select("span:matchesOwn(More Reviews)").first();
        // if (elMoreReviews != null) {
        //     nextPage++;
        // }

        for (Element elReviews : contents.select("div#horiznav_nav").first().parent().select("div.borderDark")) {
            Review review = new Review();
            review.setThumbnailUrl(elReviews.select("img").first().attr("src"));

            Element elTd = elReviews.select("div.borderLight.reviewDetails > table > tbody > tr > td").get(1);

            review.setUsername(elTd.select("a").first().text());
            review.setFoundHelpful(elTd.select("div.lightLink.spaceit").select("strong").get(0).text());
            //review.setFoundHelpfulTotal(elTd.select("div.lightLink.spaceit").select("strong").get(1).text());

            Element elTd2 = elReviews.select("div.borderLight.reviewDetails > table > tbody > tr > td").get(2);

            review.setData(elTd2.select("div").first().text());

            Element elReadability = elReviews.select("div.spaceit.textReadability").first();
            Element elTableRating = elReadability.select("table").first();

            ArrayList<ReviewRating> reviewRatings = new ArrayList<ReviewRating>();
            for (Element elLineaTr : elTableRating.select("tr")) {
                ReviewRating rating = new ReviewRating();
                rating.setDescription(elLineaTr.select("td").get(0).text());
                rating.setRating(elLineaTr.select("td").get(1).text());
                reviewRatings.add(rating);
            }
            review.setReviewRatings(reviewRatings);

            elReadability.select("table").remove();
            elReadability.select("a").remove();

            review.setCommentaryFull(elReadability.text());

            elReadability.select("span").remove();

            review.setCommentaryLess(elReadability.text());

            records.add(review);
        }

        return records;
    }
}
