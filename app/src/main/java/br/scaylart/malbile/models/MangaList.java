package br.scaylart.malbile.models;


import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class MangaList {
    @Setter @Getter private ArrayList<Manga> mangas;

    public MangaList() {}

    public MangaList(ArrayList<Manga> mangas) {
        this.mangas = mangas;
    }
}
