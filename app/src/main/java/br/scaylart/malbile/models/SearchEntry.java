package br.scaylart.malbile.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import lombok.Getter;
import lombok.Setter;

@Root(name = "entry", strict = false)
public class SearchEntry {
    @Element(name = "id", required = false)
    @Getter @Setter private int id;

    @Element(name = "title", required = false)
    @Getter @Setter private String title;

    @Element(name = "episodes", required = false)
    @Getter @Setter private int episodes;

    @Element(name = "chapters", required = false)
    @Getter @Setter private int chapters;

    @Element(name = "volumes", required = false)
    @Getter @Setter private int volumes;

    @Element(name = "score", required = false)
    @Getter @Setter private String score;

    @Element(name = "type", required = false)
    @Getter @Setter private String type;

    @Element(name = "status", required = false)
    @Getter @Setter private String status;

    @Element(name = "start_date", required = false)
    @Getter @Setter private String start_date;

    @Element(name = "end_date", required = false)
    @Getter @Setter private String end_date;

    @Element(name = "synopsis", required = false)
    @Getter @Setter private String synopsis;

    @Element(name = "image", required = false)
    @Getter @Setter private String image;

    public Anime createAnime() {
        Anime anime = new Anime();
        anime.setId(getId());
        anime.setTitle(getTitle());
        anime.setEpisodes(getEpisodes());
        anime.setMembersScore(getScore());
        anime.setTypeByDesc(getType());
        anime.setStatusByDesc(getStatus());
        anime.setStartDate(getStart_date());
        anime.setEndDate(getEnd_date());
        //anime.setSynopsis(getSynopsis());
        anime.setImageUrl(getImage());
        return anime;
    }

    public Manga createManga() {
        Manga manga = new Manga();
        manga.setId(getId());
        manga.setTitle(getTitle());
        manga.setChapters(getChapters());
        manga.setVolumes(getVolumes());
        manga.setMembersScore(getScore());
        manga.setTypeByDesc(getType());
        manga.setStatusByDesc(getStatus());
        manga.setStartDate(getStart_date());
        manga.setEndDate(getEnd_date());
        //anime.setSynopsis(getSynopsis());
        manga.setImageUrl(getImage());
        return manga;
    }
}
