package br.scaylart.malbile.models;

import lombok.Getter;
import lombok.Setter;

public class ReviewRating extends GlobalParcelable  {
    @Setter @Getter private String description;
    @Setter @Getter private String rating;
}
