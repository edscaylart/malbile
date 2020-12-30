package br.scaylart.malbile.controllers.factories;

import br.scaylart.malbile.reader.English_MangaEden;
import br.scaylart.malbile.reader.Source;
import br.scaylart.malbile.reader.English_MangaReader;
import br.scaylart.malbile.utils.PreferenceUtils;

public class SourceFactory {
    private SourceFactory() {
        throw new AssertionError();
    }

    public static Source checkNames(String sourceName) {
        Source currentSource;

        if (sourceName.equalsIgnoreCase(English_MangaReader.NAME)) {
            currentSource = new English_MangaReader();
        } else {
            currentSource = new English_MangaEden();
        }

        return currentSource;
    }

    public static Source constructSourceFromPreferences() {
        String sourceName = PreferenceUtils.getSource();
        return checkNames(sourceName);
    }

    public static Source constructSourceFromName(String sourceName) {
        return checkNames(sourceName);
    }

    public static Source constructSourceFromUrl(String url) {
        Source currentSource;

        if (url.contains(English_MangaReader.BASE_URL)) {
            currentSource = new English_MangaReader();
        } else {
            currentSource = new English_MangaEden();
        }

        return currentSource;
    }
}
