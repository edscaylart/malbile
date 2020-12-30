package br.scaylart.malbile.reader.model;

import br.scaylart.malbile.models.GlobalParcelable;
import lombok.Getter;
import lombok.Setter;

public class DownloadPage extends GlobalParcelable {
    @Getter @Setter private int id;
    @Getter @Setter private String url;
    @Getter @Setter private String parentUrl;
    @Getter @Setter private String directory;
    @Getter @Setter private String title;
    @Getter @Setter private int flag;
}
