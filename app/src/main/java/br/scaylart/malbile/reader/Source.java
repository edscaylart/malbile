package br.scaylart.malbile.reader;

import java.util.ArrayList;
import java.util.List;

import br.scaylart.malbile.reader.model.Chapter;
import br.scaylart.malbile.reader.model.MangaEden;
import br.scaylart.malbile.utils.wrappers.ReaderWrapper;
import rx.Observable;

public interface Source {
    Observable<String> getName();

    Observable<String> getBaseUrl();

    Observable<String> getInitialUpdateUrl();

    Observable<List<String>> getGenres();

    Observable<UpdatePageMarker> pullLatestUpdatesFromNetwork(UpdatePageMarker newUpdate);

    Observable<MangaEden> pullMangaFromNetwork(ReaderWrapper request);

    Observable<List<Chapter>> pullChaptersFromNetwork(ReaderWrapper request);

    Observable<String> pullImageUrlsFromNetwork(ReaderWrapper request);

    Observable<ArrayList<MangaEden>> recursivelyConstructDatabase();

    Observable<String> recursivelyConstructDatabase(final String url);
}
