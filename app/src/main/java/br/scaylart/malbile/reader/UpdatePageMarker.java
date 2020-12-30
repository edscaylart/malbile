package br.scaylart.malbile.reader;

import br.scaylart.malbile.models.GlobalParcelable;
import lombok.Getter;
import lombok.Setter;

public class UpdatePageMarker extends GlobalParcelable{
    @Getter @Setter private String nextPageUrl;
    @Getter @Setter private int lastMangaPosition;

    public UpdatePageMarker(String nextPageUrl, int lastMangaPosition) {
        this.nextPageUrl = nextPageUrl;
        this.lastMangaPosition = lastMangaPosition;
    }

    public void appendUpdatePageMarker(UpdatePageMarker newUpdatePageMarker) {
        this.nextPageUrl = newUpdatePageMarker.nextPageUrl;
        this.lastMangaPosition += newUpdatePageMarker.lastMangaPosition;
    }
}
