package br.scaylart.malbile.reader.model;

import br.scaylart.malbile.models.GlobalParcelable;
import lombok.Getter;
import lombok.Setter;

public class DownloadManga extends GlobalParcelable {
    @Getter @Setter private int id;
    @Getter @Setter private String url;
    @Getter @Setter private String artist;
    @Getter @Setter private String author;
    @Getter @Setter private String description;
    @Getter @Setter private String genre;
    @Getter @Setter private String title;
    @Getter @Setter private boolean completed;
    @Getter @Setter private String imageUrl;
}
