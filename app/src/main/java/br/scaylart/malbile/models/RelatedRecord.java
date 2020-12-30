package br.scaylart.malbile.models;

import br.scaylart.malbile.controllers.networks.BaseService.ListType;

public class RelatedRecord extends GlobalParcelable {
    private int anime_id = 0;
    private int manga_id = 0;

    private String title;
    private String url;

    public RelatedRecord() {}

    public void setId(int id, ListType type) {
        this.anime_id = type.equals(ListType.ANIME) ? id : 0;
        this.manga_id = type.equals(ListType.MANGA) ? id : 0;
    }

    public int getId() {
        return (anime_id > 0) ? anime_id : manga_id;
    }

    public ListType getType() {
        if (anime_id > 0)
            return ListType.ANIME;
        if (manga_id > 0)
            return ListType.MANGA;
        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
