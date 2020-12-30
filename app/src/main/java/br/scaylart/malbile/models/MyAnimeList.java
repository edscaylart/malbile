package br.scaylart.malbile.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "myanimelist", strict = false)
public class MyAnimeList {
    @ElementList(name = "manga", inline = true, required = false)
    public ArrayList<Manga> mangas;

    @ElementList(name = "anime", inline = true, required = false)
    public ArrayList<Anime> animes;
}

