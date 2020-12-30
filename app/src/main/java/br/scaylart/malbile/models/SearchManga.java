package br.scaylart.malbile.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "manga", strict = false)
public class SearchManga {
    @ElementList(name = "entry", inline = true, required = false)
    public ArrayList<SearchEntry> entry;
}
