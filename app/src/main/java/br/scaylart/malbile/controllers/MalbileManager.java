package br.scaylart.malbile.controllers;

import android.content.ContentValues;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.squareup.okhttp.Response;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.scaylart.malbile.MalbileApplication;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.caches.CacheProvider;
import br.scaylart.malbile.controllers.databases.DBManager;
import br.scaylart.malbile.controllers.databases.Tables;
import br.scaylart.malbile.controllers.factories.SourceFactory;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.controllers.networks.ClientService;
import br.scaylart.malbile.controllers.networks.PostService;
import br.scaylart.malbile.controllers.networks.ReaderService;
import br.scaylart.malbile.controllers.networks.RestApiService;
import br.scaylart.malbile.controllers.networks.RestService;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.AnimeList;
import br.scaylart.malbile.models.BaseRecord;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.models.MangaList;
import br.scaylart.malbile.models.Message;
import br.scaylart.malbile.models.Recommendation;
import br.scaylart.malbile.models.Review;
import br.scaylart.malbile.models.SearchAnime;
import br.scaylart.malbile.models.SearchEntry;
import br.scaylart.malbile.models.User;
import br.scaylart.malbile.models.parsers.AddedUpcomingParser;
import br.scaylart.malbile.models.parsers.AnimeParser;
import br.scaylart.malbile.models.parsers.FriendParser;
import br.scaylart.malbile.models.parsers.MangaParser;
import br.scaylart.malbile.models.parsers.MessageParser;
import br.scaylart.malbile.models.parsers.RecommendationParser;
import br.scaylart.malbile.models.parsers.ReviewParser;
import br.scaylart.malbile.models.parsers.TopPopularParser;
import br.scaylart.malbile.models.parsers.UserParser;
import br.scaylart.malbile.reader.UpdatePageMarker;
import br.scaylart.malbile.reader.model.Chapter;
import br.scaylart.malbile.reader.model.MangaEden;
import br.scaylart.malbile.reader.model.mangaeden.MangaListJson;
import br.scaylart.malbile.utils.DiskUtils;
import br.scaylart.malbile.utils.PreferenceUtils;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.ReaderWrapper;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MalbileManager {
    private MalbileManager() {
        throw new AssertionError();
    }

    public enum ListStatus {
        BLANK,
        PROGRESS,
        PLANNED,
        ONHOLD,
        COMPLETED,
        DROPPED
    }

    public enum TaskJob {
        LIBRARY,
        JUSTADDED,
        MOSTPOPULAR,
        UPCOMING,
        TOPRATED,
        SEARCH
    }

    public static String getStatusFromEnum(ListStatus listStatus) {
        switch (listStatus) {
            case PROGRESS:
                return BaseRecord.STATUS_INPROGRESS;
            case COMPLETED:
                return BaseRecord.STATUS_COMPLETED;
            case ONHOLD:
                return BaseRecord.STATUS_ONHOLD;
            case DROPPED:
                return BaseRecord.STATUS_DROPPED;
            case PLANNED:
                return BaseRecord.STATUS_PLANNED;
            default:
                return "";
        }
    }

    public static Observable<String> getNameFromPreferenceSource() {
        return SourceFactory.constructSourceFromPreferences().getName();
    }

    public static Observable<String> getBaseUrlFromPreferenceSource() {
        return SourceFactory.constructSourceFromPreferences().getBaseUrl();
    }

    public static Observable<String> getInitialUpdateUrlFromPreferenceSource() {
        return SourceFactory.constructSourceFromPreferences().getInitialUpdateUrl();
    }

    public static Observable<List<String>> getGenresFromPreferenceSource() {
        return SourceFactory.constructSourceFromPreferences().getGenres();
    }

    public static Observable<UpdatePageMarker> pullLatestUpdatesFromNetwork(final UpdatePageMarker newUpdate) {
        return SourceFactory.constructSourceFromPreferences().pullLatestUpdatesFromNetwork(newUpdate);
    }

    public static Observable<MangaEden> pullMangaFromNetwork(final ReaderWrapper request) {
        return SourceFactory.constructSourceFromName(request.getSource()).pullMangaFromNetwork(request);
    }

    public static Observable<List<Chapter>> pullChaptersFromNetwork(final ReaderWrapper request) {
        return SourceFactory.constructSourceFromName(request.getSource()).pullChaptersFromNetwork(request);
    }

    public static Observable<String> pullImageUrlsFromNetwork(final ReaderWrapper request) {
        return MalbileManager.getImageUrlsFromDiskCache(request.getUrl())
                .onBackpressureBuffer()
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends String>>() {
                    @Override
                    public Observable<? extends String> call(Throwable throwable) {
                        return SourceFactory.constructSourceFromName(request.getSource()).pullImageUrlsFromNetwork(request);
                    }
                });
    }

    public static Observable<String> recursivelyConstructDatabase(final ReaderWrapper request) {
        return SourceFactory.constructSourceFromName(request.getSource()).recursivelyConstructDatabase(request.getUrl());
    }

    public static Observable<User> isAuthenticated(final String user, final String pass) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                try {
                    RestService service = new RestService(user, pass);
                    subscriber.onNext(service.verifyAuthentication());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<String> getTokenFromLoginPage() {
        return ClientService.getTemporaryInstance()
                .getResponse(ClientService.BASE_HOST + "/login.php")
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        try {
                            Document contents = Jsoup.parse(s);
                            String token = "";
                            Elements metalinks = contents.select("meta[name=csrf_token]");
                            for (Element metaTag : metalinks) {
                                if (metaTag.attr("name").equals("csrf_token")) {
                                    token = metaTag.attr("content");
                                }
                            }
                            PreferenceUtils.setTokenAcess(token);

                            return Observable.just(token);
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });
    }

    public static Observable<ArrayList<Message>> downloadMessagesFromNetwork(final String username) {
        /*return ClientService.getTemporaryInstance()
                .getResponse(ClientService.BASE_HOST + "/mymessages.php")
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<ArrayList<Message>>>() {
                    @Override
                    public Observable<ArrayList<Message>> call(String s) {
                        try {
                            return Observable.just(MessageParser.parse(s));
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });*/
        return Observable.create(new Observable.OnSubscribe<ArrayList<Message>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Message>> subscriber) {
                try {
                    RestApiService service = new RestApiService();
                    subscriber.onNext(service.getUserMessages());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Boolean> postMessageToUser(final String username, final List<NameValuePair> formDataValues) {
        return PostService.getTemporaryInstance()
                .executePost(PostService.BASE_HOST + "/mymessages.php?go=send&toname=" + username, formDataValues, PreferenceUtils.getCookie())
                .flatMap(new Func1<HttpResponse, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(HttpResponse response) {
                        return Observable.just(response.getStatusLine().getStatusCode() == 200);
                    }
                });
    }

    public static Observable<Boolean> postMessageToUser(final String username, final String subject, final String msg) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    RestApiService service = new RestApiService();
                    subscriber.onNext(service.sendMessage(username, subject, msg));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<User>> downloadFriendDataFromNetwork(final String username) {
        return ClientService.getTemporaryInstance()
                .getResponse(ClientService.BASE_HOST + "/profile/" + username + "/friends")
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<ArrayList<User>>>() {
                    @Override
                    public Observable<ArrayList<User>> call(String s) {
                        try {
                            return Observable.just(FriendParser.parse(s));
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });
    }

    public static Observable<User> downloadUserDataFromNetwork(final String username, final boolean isUserMBL) {
        return ClientService.getTemporaryInstance()
                .getResponse(ClientService.BASE_HOST + "/profile/" + username)
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<User>>() {
                    @Override
                    public Observable<User> call(String s) {
                        try {
                            return Observable.just(UserParser.parse(s, username, isUserMBL));
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });
    }

    public static Observable<AnimeList> getAnimeLibrary() {
        return Observable.create(new Observable.OnSubscribe<AnimeList>() {
            @Override
            public void call(Subscriber<? super AnimeList> subscriber) {
                try {
                    RestService service = new RestService();
                    subscriber.onNext(service.getAnimeLibrary());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<AnimeList> getAnimeLibrary(final String username) {
        return Observable.create(new Observable.OnSubscribe<AnimeList>() {
            @Override
            public void call(Subscriber<? super AnimeList> subscriber) {
                try {
                    RestService service = new RestService();
                    subscriber.onNext(service.getAnimeLibrary(username));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Anime> downloadAnimeFromNetwork(final int id, final Anime record, final String username) {
        return ClientService.getTemporaryInstance()
                .getResponse(ClientService.BASE_HOST + "/anime/" + String.valueOf(id))
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<Anime>>() {
                    @Override
                    public Observable<Anime> call(String s) {
                        try {
                            Anime anime = AnimeParser.parse(s, record);

                            DBManager dbManager = new DBManager();
                            dbManager.saveAnime(anime, false, username);

                            return Observable.just(anime);
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });
    }

    public static Observable<MangaList> getMangaLibrary() {
        return Observable.create(new Observable.OnSubscribe<MangaList>() {
            @Override
            public void call(Subscriber<? super MangaList> subscriber) {
                try {
                    RestService service = new RestService();
                    subscriber.onNext(service.getMangaLibrary());
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<MangaList> getMangaLibrary(final String username) {
        return Observable.create(new Observable.OnSubscribe<MangaList>() {
            @Override
            public void call(Subscriber<? super MangaList> subscriber) {
                try {
                    RestService service = new RestService();
                    subscriber.onNext(service.getMangaLibrary(username));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Manga> downloadMangaFromNetwork(final int id, final Manga record, final String username) {
        return ClientService.getTemporaryInstance()
                .getResponse(ClientService.BASE_HOST + "/manga/" + String.valueOf(id))
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<Manga>>() {
                    @Override
                    public Observable<Manga> call(String s) {
                        try {
                            Manga manga = MangaParser.parse(s, record);

                            DBManager dbManager = new DBManager();
                            dbManager.saveManga(manga, false, username);

                            return Observable.just(manga);
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });
    }

    public static Observable<ArrayList<Review>> downloadReviewFromNetwork(final String type, final String id, final String title, int page) {
        return ClientService.getTemporaryInstance()
                .getResponse(ClientService.BASE_HOST + "/" + type + "/" + id + "/" + title + "/reviews&p=" + String.valueOf(page))
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<ArrayList<Review>>>() {
                    @Override
                    public Observable<ArrayList<Review>> call(String s) {
                        try {
                            return Observable.just(ReviewParser.parse(s));
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });
    }

    public static Observable<Boolean> postReview(final RequestWrapper request, final List<NameValuePair> formDataValues) {
        String type = request.getListType().equals(BaseService.ListType.ANIME) ? "seriesid" : "mid";
        return PostService.getTemporaryInstance()
                .executePost(PostService.BASE_HOST + "/myreviews.php?" + type + "=" + String.valueOf(request.getId()) + "&go=write",
                        formDataValues,
                        PreferenceUtils.getCookie())
                .flatMap(new Func1<HttpResponse, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(HttpResponse response) {
                        return Observable.just(response.getStatusLine().getStatusCode() == 200);
                    }
                });
    }

    public static Observable<Boolean> postReview(final RequestWrapper request, final HashMap<String, String> fieldMap) {
        final String type = request.getListType().equals(BaseService.ListType.ANIME) ? "anime" : "manga";
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    RestApiService service = new RestApiService();
                    subscriber.onNext(service.postReviews(type, fieldMap));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<Recommendation>> downloadUserRecsFromNetwork(final String type, final String id, final String title) {
        return ClientService.getTemporaryInstance()
                .getResponse(ClientService.BASE_HOST + "/" + type + "/" + id + "/" + title + "/userrecs")
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<ArrayList<Recommendation>>>() {
                    @Override
                    public Observable<ArrayList<Recommendation>> call(String s) {
                        try {
                            return Observable.just(RecommendationParser.parse(s));
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });
    }

    public static Observable<Boolean> postRecommendation(final RequestWrapper request, final List<NameValuePair> formDataValues) {
        String type = request.getListType().equals(BaseService.ListType.ANIME) ? "aid" : "mid";
        return PostService.getTemporaryInstance()
                .executePost(PostService.BASE_HOST + "/myrecommendations.php?go=make&" + type + "=" + String.valueOf(request.getId()),
                        formDataValues,
                        PreferenceUtils.getCookie())
                .flatMap(new Func1<HttpResponse, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(HttpResponse response) {
                        return Observable.just(response.getStatusLine().getStatusCode() == 200);
                    }
                });
    }

    public static Observable<Boolean> postRecommendation(final RequestWrapper request, final HashMap<String, String> fieldMap) {
        final String type = request.getListType().equals(BaseService.ListType.ANIME) ? "anime" : "manga";
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    fieldMap.put("id", String.valueOf(request.getId()));

                    RestApiService service = new RestApiService();
                    subscriber.onNext(service.postRecommendations(type, fieldMap));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<Anime>> downloadAnimeListOfTaskjob(final TaskJob taskJob, int page) {
        String url = ClientService.BASE_HOST;
        switch (taskJob) {
            case MOSTPOPULAR:
                url += "/topanime.php?type=bypopularity&limit=" + String.valueOf(page * 50);
                break;
            case TOPRATED:
                url += "/topanime.php&limit=" + String.valueOf(page * 50);
                break;
            case JUSTADDED:
                url += "/anime.php?o=9&c[0]=a&c[1]=d&cv=2&w=1&show=" + String.valueOf(page * 50);
                break;
            case UPCOMING:
                url += "/anime.php?sd=21&sm=3&sy=2015&em=0&ed=0&ey=0&o=2&w=&c[0]=a&c[1]=d&cv=1&show=" + String.valueOf(page * 50);
                break;
        }
        return ClientService.getTemporaryInstance().getResponse(url)
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<ArrayList<Anime>>>() {
                    @Override
                    public Observable<ArrayList<Anime>> call(String s) {
                        try {
                            switch (taskJob) {
                                case MOSTPOPULAR:
                                case TOPRATED:
                                    ArrayList<BaseRecord> topRated = TopPopularParser.parse(s, "anime");
                                    ArrayList<Anime> animes = new ArrayList<>();
                                    for (BaseRecord record : topRated) {
                                        animes.add((Anime) record);
                                    }
                                    return Observable.just(animes);
                                case JUSTADDED:
                                case UPCOMING:
                                    ArrayList<BaseRecord> justAdded = AddedUpcomingParser.parse(s, "anime");
                                    ArrayList<Anime> animesJd = new ArrayList<>();
                                    for (BaseRecord record : justAdded) {
                                        animesJd.add((Anime) record);
                                    }
                                    return Observable.just(animesJd);
                                default:
                                    return null;
                            }
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });
    }

    public static Observable<ArrayList<Manga>> downloadMangaListOfTaskjob(final TaskJob taskJob, int page) {
        String url = ClientService.BASE_HOST;
        switch (taskJob) {
            case MOSTPOPULAR:
                url += "/topmanga.php?type=bypopularity&limit=" + String.valueOf(page * 50);
                break;
            case TOPRATED:
                url += "/topmanga.php&limit=" + String.valueOf(page * 50);
                break;
            case JUSTADDED:
                url += "/manga.php?o=9&c[0]=a&c[1]=d&cv=2&show=" + String.valueOf(page * 50);
                break;
            case UPCOMING:
                url += "/manga.php?sd=21&sm=3&sy=2015&em=0&ed=0&ey=0&o=2&w=1&c[0]=d&c[1]=a&cv=1&show=" + String.valueOf(page * 50);
                break;
        }
        return ClientService.getTemporaryInstance().getResponse(url)
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return ClientService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<ArrayList<Manga>>>() {
                    @Override
                    public Observable<ArrayList<Manga>> call(String s) {
                        try {
                            switch (taskJob) {
                                case MOSTPOPULAR:
                                case TOPRATED:
                                    ArrayList<BaseRecord> topRated = TopPopularParser.parse(s, "manga");
                                    ArrayList<Manga> mangas = new ArrayList<>();
                                    for (BaseRecord record : topRated) {
                                        mangas.add((Manga) record);
                                    }
                                    return Observable.just(mangas);
                                case JUSTADDED:
                                case UPCOMING:
                                    ArrayList<BaseRecord> justAdded = AddedUpcomingParser.parse(s, "manga");
                                    ArrayList<Manga> mangasJd = new ArrayList<>();
                                    for (BaseRecord record : justAdded) {
                                        mangasJd.add((Manga) record);
                                    }
                                    return Observable.just(mangasJd);
                                default:
                                    return null;
                            }
                        } catch (Throwable e) {
                            return null;
                        }
                    }
                });
    }

    public static Observable<Boolean> addOrUpdateAnime(final Anime anime) {
        String url = PostService.BASE_HOST;
        final int statusCode = anime.getCreateFlag() ? 201 : 200;

        if (anime.getCreateFlag()) {
            url += "/api/animelist/add/" + Integer.toString(anime.getId()) + ".xml";

        } else {
            url += "/api/animelist/update/" + Integer.toString(anime.getId()) + ".xml";

            if (!anime.isDirty())
                return Observable.just(false);
        }

        List<NameValuePair> formDataValues = new ArrayList<>(2);
        formDataValues.add(new BasicNameValuePair("data", anime.getXML()));

        return PostService.getTemporaryInstance()
                .executePost(url, formDataValues, null)
                .flatMap(new Func1<HttpResponse, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(HttpResponse httpResponse) {
                        return Observable.just(httpResponse.getStatusLine().getStatusCode() == statusCode);
                    }
                })
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        if (aBoolean) {
                            anime.clearDirty();

                            DBManager dbManager = new DBManager();
                            dbManager.saveAnime(anime, false, AccountService.getUsername());
                        }
                        return Observable.just(aBoolean);
                    }
                });
    }

    public static Observable<Boolean> deleteAnimeFromList(final Anime anime) {
        String url = PostService.BASE_HOST;
        final int statusCode = 200;

        if (anime.getDeleteFlag()) {
            url += "/api/animelist/delete/" + Integer.toString(anime.getId()) + ".xml";
        } else {
            return Observable.just(false);
        }

        List<NameValuePair> formDataValues = new ArrayList<>(2);
        formDataValues.add(new BasicNameValuePair("data", anime.getXML()));

        return PostService.getTemporaryInstance()
                .executePost(url, formDataValues, null)
                .flatMap(new Func1<HttpResponse, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(HttpResponse httpResponse) {
                        return Observable.just(httpResponse.getStatusLine().getStatusCode() == statusCode);
                    }
                })
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        if (aBoolean) {
                            DBManager dbManager = new DBManager();
                            dbManager.deleteAnimeFromAnimelist(anime.getId(), AccountService.getUsername());
                        }
                        return Observable.just(aBoolean);
                    }
                });
    }

    public static Observable<Boolean> addOrUpdateManga(final Manga manga) {
        String url = PostService.BASE_HOST;
        final int statusCode = manga.getCreateFlag() ? 201 : 200;

        if (manga.getCreateFlag()) {
            url += "/api/mangalist/add/" + Integer.toString(manga.getId()) + ".xml";

        } else {
            url += "/api/mangalist/update/" + Integer.toString(manga.getId()) + ".xml";

            if (!manga.isDirty())
                return Observable.just(false);
        }

        List<NameValuePair> formDataValues = new ArrayList<>(2);
        formDataValues.add(new BasicNameValuePair("data", manga.getXML()));

        return PostService.getTemporaryInstance()
                .executePost(url, formDataValues, null)
                .flatMap(new Func1<HttpResponse, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(HttpResponse httpResponse) {
                        return Observable.just(httpResponse.getStatusLine().getStatusCode() == statusCode);
                    }
                })
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        if (aBoolean) {
                            manga.clearDirty();

                            DBManager dbManager = new DBManager();
                            dbManager.saveManga(manga, false, AccountService.getUsername());
                        }
                        return Observable.just(aBoolean);
                    }
                });
    }

    public static Observable<Boolean> deleteMangaFromList(final Manga manga) {
        String url = PostService.BASE_HOST;
        final int statusCode = 200;

        if (manga.getDeleteFlag()) {
            url += "/api/mangalist/delete/" + Integer.toString(manga.getId()) + ".xml";
        } else {
            return Observable.just(false);
        }

        List<NameValuePair> formDataValues = new ArrayList<>(2);
        formDataValues.add(new BasicNameValuePair("data", manga.getXML()));

        return PostService.getTemporaryInstance()
                .executePost(url, formDataValues, null)
                .flatMap(new Func1<HttpResponse, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(HttpResponse httpResponse) {
                        return Observable.just(httpResponse.getStatusLine().getStatusCode() == statusCode);
                    }
                })
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        if (aBoolean) {
                            DBManager dbManager = new DBManager();
                            dbManager.deleteMangaFromMangalist(manga.getId(), AccountService.getUsername());
                        }
                        return Observable.just(aBoolean);
                    }
                });
    }

    public static Observable<ArrayList<Anime>> searchAnimeFromNetwork(final String query) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Anime>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Anime>> subscriber) {
                try {
                    RestService service = new RestService();
                    subscriber.onNext(service.searchAnime(query));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<Manga>> searchMangaFromNetwork(final String query) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Manga>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Manga>> subscriber) {
                try {
                    RestService service = new RestService();
                    subscriber.onNext(service.searchManga(query));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<GlideDrawable> cacheFromImagesOfSize(final List<String> imageUrls) {
        return Observable.create(new Observable.OnSubscribe<GlideDrawable>() {
            @Override
            public void call(Subscriber<? super GlideDrawable> subscriber) {
                try {
                    for (String imageUrl : imageUrls) {
                        if (!subscriber.isUnsubscribed()) {
                            FutureTarget<GlideDrawable> cacheFuture = Glide.with(MalbileApplication.getInstance())
                                    .load(imageUrl)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                            subscriber.onNext(cacheFuture.get(ClientService.READ_TIMEOUT, TimeUnit.SECONDS));
                        }
                    }
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public static Observable<Boolean> clearImageCache() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean isSuccessful = true;

                    File imageCacheDirectory = Glide.getPhotoCacheDir(MalbileApplication.getInstance());
                    if (imageCacheDirectory.isDirectory()) {
                        for (File cachedFile : imageCacheDirectory.listFiles()) {
                            if (!cachedFile.delete()) {
                                isSuccessful = false;
                            }
                        }
                    } else {
                        isSuccessful = false;
                    }

                    File urlCacheDirectory = CacheProvider.getInstance().getCacheDir();
                    if (urlCacheDirectory.isDirectory()) {
                        for (File cachedFile : urlCacheDirectory.listFiles()) {
                            if (!cachedFile.delete()) {
                                isSuccessful = false;
                            }
                        }
                    } else {
                        isSuccessful = false;
                    }

                    subscriber.onNext(isSuccessful);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static Observable<String> getImageUrlsFromDiskCache(final String chapterUrl) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String[] imageUrls = CacheProvider.getInstance().getImageUrlsFromDiskCache(chapterUrl);

                    for (String imageUrl : imageUrls) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(imageUrl);
                        }
                    }
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static Observable<File> saveInputStreamToDirectory(final InputStream inputStream, final String directory, final String name) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    subscriber.onNext(DiskUtils.saveInputStreamToDirectory(inputStream, directory, name));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<MangaEden>> downloadMagaEdenList() {
        return Observable.create(new Observable.OnSubscribe<ArrayList<MangaEden>>() {
            @Override
            public void call(final Subscriber<? super ArrayList<MangaEden>> subscriber) {
                try {
                    ReaderService service = new ReaderService();
                    MangaListJson mangaJson = service.getMangaList();

                    ArrayList<MangaEden> mangaList = new ArrayList<>();

                    for (int index = 0; index < mangaJson.getMangas().size(); index++) {

                        MangaEden newManga = new MangaEden();
                        //newManga.setSource(NAME);
                        newManga.setUrl("https://www.mangaeden.com/api/manga/" + mangaJson.getMangas().get(index).getId() + "/");
                        newManga.setTitle(mangaJson.getMangas().get(index).getTitle());
                        newManga.setImageUrl("https://cdn.mangaeden.com/mangasimg/" + mangaJson.getMangas().get(index).getImage());
                        newManga.setCompleted(mangaJson.getMangas().get(index).getStatus() == 2);
                        newManga.setRank(mangaJson.getMangas().get(index).getHits()); // Hits.

                        mangaList.add(newManga);
                    }

                    Collections.sort(mangaList, new Comparator<MangaEden>() {
                        @Override
                        public int compare(MangaEden lhs, MangaEden rhs) {
                            if (lhs.getRank() < rhs.getRank()) {
                                return 1;
                            } else if (lhs.getRank() == rhs.getRank()) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                    });

                    for (int index = 0; index < mangaList.size(); index++) {
                        mangaList.get(index).setRank(index + 1);
                    }

                    DBManager dbManager = new DBManager();

                    dbManager.getDBWrite().beginTransaction();
                    try {
                        for (MangaEden currentManga : mangaList) {
                            ContentValues cv = new ContentValues();

                            cv.put(Tables.MangasReader.URL, currentManga.getUrl());
                            cv.put(Tables.MangasReader.TITLE, currentManga.getTitle());
                            cv.put(Tables.MangasReader.IMAGE_URL, currentManga.getImageUrl());
                            cv.put(Tables.MangasReader.COMPLETED, currentManga.isCompleted());
                            cv.put(Tables.MangasReader.RANK, currentManga.getRank());

                            dbManager.getDBWrite().replace(Tables.MangaChapters.TABLE_NAME, null, cv);
                        }

                        dbManager.getDBWrite().setTransactionSuccessful();
                    } finally {
                        dbManager.getDBWrite().endTransaction();
                    }

                    subscriber.onNext(mangaList);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
