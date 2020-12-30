package br.scaylart.malbile.controllers.networks;

import br.scaylart.malbile.controllers.networks.interfaces.MangaReaderApi;
import br.scaylart.malbile.reader.model.mangaeden.MangaListJson;

public class ReaderService {
    public static final String BASE_HOST = "https://www.mangaeden.com/api";
    public static final String USER_AGENT =  "api-indiv-0A82E1C01531EA0C7E8349A8B82803BA";

    private MangaReaderApi service;

    public ReaderService() {
        setupRESTService();
    }

    private void setupRESTService() {
        service = ServiceGenerator.createService(MangaReaderApi.class,
                BASE_HOST,
                USER_AGENT,
                "json");
    }

    public MangaListJson getMangaList() {
        return service.getMangaList();
    }

}
