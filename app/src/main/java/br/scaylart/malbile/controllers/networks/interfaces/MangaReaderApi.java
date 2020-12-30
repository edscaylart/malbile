package br.scaylart.malbile.controllers.networks.interfaces;

import br.scaylart.malbile.reader.model.mangaeden.MangaListJson;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MangaReaderApi {
    @GET("/list/0/")
    MangaListJson getMangaList();

    @GET("/manga/{id}/")
    MangaListJson getMangaChapters(@Path("id") String id);

    @GET("/chapter/{id}/ ")
    MangaListJson getChapterPages(@Path("id") String id);
}
