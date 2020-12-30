package br.scaylart.malbile.controllers.networks;

import java.util.ArrayList;

import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.networks.interfaces.MalApi;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.AnimeList;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.models.MangaList;
import br.scaylart.malbile.models.MyAnimeList;
import br.scaylart.malbile.models.SearchAnime;
import br.scaylart.malbile.models.SearchEntry;
import br.scaylart.malbile.models.SearchManga;
import br.scaylart.malbile.models.User;

public class RestService extends BaseService {
    private MalApi service;

    private String username;

    public RestService() {
        username = AccountService.getUsername();
        setupRESTService(username, AccountService.getPassword());
    }

    public RestService(String username, String password) {
        this.username = username;
        setupRESTService(username, password);

    }

    private void setupRESTService(String username, String password) {
        service = ServiceGenerator.createService(MalApi.class,
                BASE_HOST,
                USER_AGENT,
                "xml",
                username,
                password,
                null);
    }

    public User verifyAuthentication() {
        return service.verifyCredentials();
    }

    public AnimeList getAnimeLibrary() {
        return new AnimeList(service.getLibrary(username, "all", "anime").animes);
    }

    public AnimeList getAnimeLibrary(String username) {
        return new AnimeList(service.getLibrary(username, "all", "anime").animes);
    }

    public MangaList getMangaLibrary() {
        return new MangaList(service.getLibrary(username, "all", "manga").mangas);
    }

    public MangaList getMangaLibrary(String username) {
        return new MangaList(service.getLibrary(username, "all", "manga").mangas);
    }

    public ArrayList<Anime> searchAnime(String query) {
        SearchAnime search = service.searchAnime(query);
        ArrayList<Anime> animes = new ArrayList<Anime>();
        for (SearchEntry entry : search.entry) {
            animes.add(entry.createAnime());
        }
        return animes;
    }

    public ArrayList<Manga> searchManga(String query) {
        SearchManga search = service.searchManga(query);
        ArrayList<Manga> mangas = new ArrayList<Manga>();
        for (SearchEntry entry : search.entry) {
            mangas.add(entry.createManga());
        }
        return mangas;
    }
}
