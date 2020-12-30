package br.scaylart.malbile.models;

import lombok.Getter;
import lombok.Setter;

public class Recommendation extends GlobalParcelable  {
    @Setter @Getter private int id;
    @Setter @Getter private String title;
    @Setter @Getter private String thumbnailUrl;
    @Setter @Getter private String recommendedBy;
    @Setter @Getter private String commentary;
}
