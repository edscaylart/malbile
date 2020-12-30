package br.scaylart.malbile.controllers;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.scaylart.malbile.controllers.databases.DBManager;
import br.scaylart.malbile.controllers.databases.Tables;
import br.scaylart.malbile.controllers.factories.DefaultFactory;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.AnimeList;
import br.scaylart.malbile.models.BaseRecord;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.models.MangaList;
import br.scaylart.malbile.models.Message;
import br.scaylart.malbile.models.User;
import br.scaylart.malbile.reader.model.MangaEden;
import br.scaylart.malbile.reader.model.RecentChapter;
import br.scaylart.malbile.utils.SearchUtils;
import br.scaylart.malbile.utils.wrappers.LibraryWrapper;
import br.scaylart.malbile.utils.wrappers.ReaderWrapper;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.utils.wrappers.SearchCatalogueWrapper;
import rx.Observable;
import rx.Subscriber;

public class QueryManager {
    private QueryManager() {
        throw new AssertionError();
    }

    public static Observable<AnimeList> queryAnimeLibraryFromDataBase(final LibraryWrapper request, final String username) {
        return Observable.create(new Observable.OnSubscribe<AnimeList>() {
            @Override
            public void call(Subscriber<? super AnimeList> subscriber) {
                try {
                    DBManager dbManager = new DBManager();
                    AnimeList animeList = new AnimeList(dbManager.getAnimeList(MalbileManager.getStatusFromEnum(request.getListStatus()), username));

                    subscriber.onNext(animeList);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<AnimeList> queryStoreAnimeLibrary(final AnimeList animeList, final String username) {
        return Observable.create(new Observable.OnSubscribe<AnimeList>() {
            @Override
            public void call(Subscriber<? super AnimeList> subscriber) {
                try {
                    DBManager dbManager = new DBManager();

                    dbManager.saveAnimeList(animeList.getAnimes(), username);
                    dbManager.cleanUpAnimeTable();

                    subscriber.onNext(animeList);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Anime> queryGetAnime(final int id, final String username) {
        return Observable.create(new Observable.OnSubscribe<Anime>() {
            @Override
            public void call(Subscriber<? super Anime> subscriber) {
                try {
                    DBManager dbManager = new DBManager();
                    subscriber.onNext(dbManager.getAnime(id, username));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<MangaList> queryMangaLibraryFromDataBase(final LibraryWrapper request, final String username) {
        return Observable.create(new Observable.OnSubscribe<MangaList>() {
            @Override
            public void call(Subscriber<? super MangaList> subscriber) {
                try {
                    DBManager dbManager = new DBManager();
                    MangaList mangaList = new MangaList(dbManager.getMangaList(MalbileManager.getStatusFromEnum(request.getListStatus()), username));

                    subscriber.onNext(mangaList);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<MangaList> queryStoreMangaLibrary(final MangaList mangaList, final String username) {
        return Observable.create(new Observable.OnSubscribe<MangaList>() {
            @Override
            public void call(Subscriber<? super MangaList> subscriber) {
                try {
                    DBManager dbManager = new DBManager();

                    dbManager.saveMangaList(mangaList.getMangas(), username);
                    dbManager.cleanUpAnimeTable();

                    subscriber.onNext(mangaList);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Manga> queryGetManga(final int id, final String username) {
        return Observable.create(new Observable.OnSubscribe<Manga>() {
            @Override
            public void call(Subscriber<? super Manga> subscriber) {
                try {
                    DBManager dbManager = new DBManager();
                    subscriber.onNext(dbManager.getManga(id, username));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<User> queryStoreUserPofile(final User user, final boolean fullData) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                try {
                    DBManager dbManager = new DBManager();
                    if (fullData)
                        dbManager.saveProfile(user);
                    else
                        dbManager.saveUser(user);

                    subscriber.onNext(user);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<User>> queryStoreFriendsPofile(final ArrayList<User> users, final String username) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<User>>() {
            @Override
            public void call(Subscriber<? super ArrayList<User>> subscriber) {
                try {
                    DBManager dbManager = new DBManager();
                    dbManager.saveFriendList(users, username);

                    subscriber.onNext(users);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<Message>> queryStoreMessages(final ArrayList<Message> messages) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Message>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Message>> subscriber) {
                try {
                    DBManager dbManager = new DBManager();
                    dbManager.saveMessageList(messages);

                    subscriber.onNext(messages);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<BaseRecord>> querySimilarLibrary(final RequestWrapper request) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<BaseRecord>>() {
            @Override
            public void call(Subscriber<? super ArrayList<BaseRecord>> subscriber) {
                try {
                    DBManager dbManager = new DBManager();
                    ArrayList<BaseRecord> records = new ArrayList<>();
                    if (request.getListType().equals(BaseService.ListType.ANIME)) {
                        ArrayList<Anime> animeArrayList = dbManager.getSimilarAnimeList();
                        for (Anime anime : animeArrayList) {
                            BaseRecord rec = new BaseRecord();
                            rec.setId(anime.getId());
                            rec.setImageUrl(anime.getImageUrl());
                            rec.setTitle(anime.getTitle());
                            records.add(rec);
                        }
                    } else {
                        ArrayList<Manga> mangaArrayList = dbManager.getSimilarMangaList();
                        for (Manga manga : mangaArrayList) {
                            BaseRecord rec = new BaseRecord();
                            rec.setId(manga.getId());
                            rec.setImageUrl(manga.getImageUrl());
                            rec.setTitle(manga.getTitle());
                            records.add(rec);
                        }
                    }

                    subscriber.onNext(records);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<ArrayList<MangaEden>> queryCatalogueMangasFromPreferenceSource(final SearchCatalogueWrapper searchCatalogueWrapper) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<MangaEden>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MangaEden>> subscriber) {
                try {
                    Cursor cursor;
                    ArrayList<MangaEden> result = null;
                    DBManager dbManager = new DBManager();

                    StringBuilder mangaSelection = new StringBuilder();
                    List<String> mangaSelectionArgs = new ArrayList<>();
                    String mangaOrderBy = null;
                    String mangaLimit = null;

                    mangaSelection.append(Tables.MangasReader.TITLE).append(" != ?");
                    mangaSelectionArgs.add(String.valueOf(DefaultFactory.Manga.DEFAULT_NAME));
                    mangaSelection.append(" AND ").append(Tables.MangasReader.RANK).append(" != ?");
                    mangaSelectionArgs.add(String.valueOf(DefaultFactory.Manga.DEFAULT_RANK));

                    if (searchCatalogueWrapper != null) {
                        for (String currentGenre : searchCatalogueWrapper.getGenresArgs()) {
                            mangaSelection.append(" AND ").append(Tables.MangasReader.GENRE).append(" LIKE ?");
                            mangaSelectionArgs.add("%" + currentGenre + "%");
                        }

                        if (searchCatalogueWrapper.getNameArgs() != null) {
                            mangaSelection.append(" AND ").append(Tables.MangasReader.TITLE).append(" LIKE ?");
                            mangaSelectionArgs.add("%" + searchCatalogueWrapper.getNameArgs() + "%");
                        }
                        if (searchCatalogueWrapper.getStatusArgs() != null && !searchCatalogueWrapper.getStatusArgs().equals(SearchUtils.STATUS_ALL)) {
                            mangaSelection.append(" AND ").append(Tables.MangasReader.COMPLETED).append(" = ?");
                            mangaSelectionArgs.add(searchCatalogueWrapper.getStatusArgs());
                        }
                        if (searchCatalogueWrapper.getOrderByArgs() != null) {
                            mangaOrderBy = searchCatalogueWrapper.getOrderByArgs() + " ASC";
                        }
                        if (searchCatalogueWrapper.getOffsetArgs() > -1) {
                            mangaLimit = String.valueOf(searchCatalogueWrapper.getOffsetArgs()) + "," + String.valueOf(SearchUtils.LIMIT_COUNT);
                        }
                    }

                    cursor = dbManager.getDBRead().query(Tables.MangasReader.TABLE_NAME,
                            null,
                            mangaSelection.toString(),
                            mangaSelectionArgs.toArray(new String[mangaSelectionArgs.size()]),
                            null, null,
                            mangaOrderBy,
                            mangaLimit);
                    //new String[]{Tables.MangasReader.TITLE,Tables.MangasReader.IMAGE_URL, Tables.MangasReader.URL},

                    if (cursor.moveToFirst()) {
                        result = new ArrayList<>();
                        do {
                            result.add(MangaEden.fromCursor(cursor));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Cursor> queryMangaFromRequest(final ReaderWrapper request) {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                try {
                    DBManager dbManager = new DBManager();

                    List<String> selectionArgs = new ArrayList<>();

                    selectionArgs.add(request.getUrl());

                    Cursor mangaCursor = dbManager.getDBRead().query(Tables.MangasReader.TABLE_NAME,
                            null,
                            (Tables.MangasReader.URL + " = ?"),
                            selectionArgs.toArray(new String[selectionArgs.size()]),
                            null, null, null,
                            "1");

                    subscriber.onNext(mangaCursor);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Cursor> queryChaptersOfMangaFromRequest(final ReaderWrapper request, final boolean isAscending) {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                try {
                    DBManager dbManager = new DBManager();

                    StringBuilder selection = new StringBuilder();
                    List<String> selectionArgs = new ArrayList<>();

                    selection.append(Tables.MangaChapters.PARENT_URL).append(" = ?");
                    selectionArgs.add(request.getUrl());

                    String orderBy;
                    if (isAscending) {
                        orderBy = Tables.MangaChapters.NUMBER + " ASC";
                    } else {
                        orderBy = Tables.MangaChapters.NUMBER + " DESC";
                    }

                    Cursor chaptersOfMangaCursor = dbManager.getDBRead().query(Tables.MangaChapters.TABLE_NAME,
                            null,
                            selection.toString(),
                            selectionArgs.toArray(new String[selectionArgs.size()]),
                            null, null,
                            orderBy);

                    subscriber.onNext(chaptersOfMangaCursor);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Cursor> queryRecentChaptersOfMangaFromRequest(final ReaderWrapper request, final boolean isOffline) {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                try {
                    DBManager dbManager = new DBManager();

                    StringBuilder selection = new StringBuilder();
                    List<String> selectionArgs = new ArrayList<>();

                    selection.append(Tables.MangaRecentChapters.PARENT_URL).append(" = ?");
                    selectionArgs.add(request.getUrl());

                    if (isOffline) {
                        selection.append(" AND ").append(Tables.MangaRecentChapters.OFFLINE).append(" = ?");
                        selectionArgs.add(String.valueOf(1));
                    } else {
                        selection.append(" AND ").append(Tables.MangaRecentChapters.OFFLINE).append(" = ?");
                        selectionArgs.add(String.valueOf(0));
                    }

                    Cursor recentChapter = dbManager.getDBRead().query(Tables.MangaRecentChapters.TABLE_NAME,
                            null,
                            selection.toString(),
                            selectionArgs.toArray(new String[selectionArgs.size()]),
                            null, null, null);

                    subscriber.onNext(recentChapter);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Cursor> queryRecentChapterFromRequest(final ReaderWrapper request, final boolean isOffline) {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                try {
                    DBManager dbManager = new DBManager();

                    StringBuilder selection = new StringBuilder();
                    List<String> selectionArgs = new ArrayList<>();

                    selection.append(Tables.MangaRecentChapters.URL).append(" = ?");
                    selectionArgs.add(request.getUrl());

                    if (isOffline) {
                        selection.append(" AND ").append(Tables.MangaRecentChapters.OFFLINE).append(" = ?");
                        selectionArgs.add(String.valueOf(1));
                    } else {
                        selection.append(" AND ").append(Tables.MangaRecentChapters.OFFLINE).append(" = ?");
                        selectionArgs.add(String.valueOf(0));
                    }

                    Cursor recentChapter = dbManager.getDBRead().query(Tables.MangaRecentChapters.TABLE_NAME,
                            null,
                            selection.toString(),
                            selectionArgs.toArray(new String[selectionArgs.size()]),
                            null, null, null,
                            "1");

                    subscriber.onNext(recentChapter);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Cursor> queryChapterFromRequest(final ReaderWrapper request) {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                try {
                    DBManager dbManager = new DBManager();

                    List<String> selectionArgs = new ArrayList<>();

                    selectionArgs.add(request.getUrl());

                    Cursor chapterCursor = dbManager.getDBRead().query(Tables.MangaChapters.TABLE_NAME,
                            null,
                            (Tables.MangaChapters.URL + " = ?"),
                            selectionArgs.toArray(new String[selectionArgs.size()]),
                            null, null, null,
                            "1");

                    subscriber.onNext(chapterCursor);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Observable<Cursor> queryAdjacentChapterFromRequestAndNumber(final ReaderWrapper request, final int adjacentNumber) {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                try {
                    DBManager dbManager = new DBManager();

                    StringBuilder selection = new StringBuilder();
                    List<String> selectionArgs = new ArrayList<>();

                    selection.append(Tables.MangaChapters.PARENT_URL).append(" = ?");
                    selectionArgs.add(request.getUrl());
                    selection.append(" AND ").append(Tables.MangaChapters.NUMBER).append(" = ?");
                    selectionArgs.add(String.valueOf(adjacentNumber));

                    Cursor adjacentChapter = dbManager.getDBRead().query(Tables.MangaChapters.TABLE_NAME,
                            null,
                            selection.toString(),
                            selectionArgs.toArray(new String[selectionArgs.size()]),
                            null, null, null,
                            "1");

                    subscriber.onNext(adjacentChapter);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static void putRecentChapterToDatabase(RecentChapter recentChapter) {
        DBManager dbManager = new DBManager();
        dbManager.saveRecentChapter(recentChapter);
    }

}
