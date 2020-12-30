package br.scaylart.malbile.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "anime", strict = false)
public class SearchAnime {
    @ElementList(name = "entry", inline = true, required = false)
    public ArrayList<SearchEntry> entry;
}
