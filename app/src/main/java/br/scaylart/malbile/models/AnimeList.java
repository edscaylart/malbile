package br.scaylart.malbile.models;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class AnimeList {
    @Setter @Getter private ArrayList<Anime> animes;

    public AnimeList() {}

    public AnimeList(ArrayList<Anime> animes) {
        this.animes = animes;
    }
}
