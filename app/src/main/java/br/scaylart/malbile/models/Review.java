package br.scaylart.malbile.models;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class Review extends GlobalParcelable {
    @Setter @Getter private String data;
    @Setter @Getter private String username;
    @Setter @Getter private String thumbnailUrl;
    @Setter @Getter private String commentaryLess;
    @Setter @Getter private String commentaryFull;
    @Setter @Getter private String foundHelpful;
    @Setter @Getter private String foundHelpfulTotal;
    @Setter @Getter private ArrayList<ReviewRating> reviewRatings;
}