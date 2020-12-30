package br.scaylart.malbile.reader;

import android.content.ContentValues;
import android.database.Cursor;

import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.scaylart.malbile.controllers.caches.CacheProvider;
import br.scaylart.malbile.controllers.databases.DBManager;
import br.scaylart.malbile.controllers.databases.Tables;
import br.scaylart.malbile.controllers.factories.DefaultFactory;
import br.scaylart.malbile.controllers.networks.MangaService;
import br.scaylart.malbile.controllers.networks.ReaderService;
import br.scaylart.malbile.reader.model.Chapter;
import br.scaylart.malbile.reader.model.MangaEden;
import br.scaylart.malbile.reader.model.mangaeden.MangaListJson;
import br.scaylart.malbile.utils.wrappers.ReaderWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class English_MangaEden implements Source {
    public static final String NAME = "MangaEden (EN)";
    public static final String BASE_URL = "www.mangaeden.com";

    private static final String INITIAL_UPDATE_URL = "http://www.mangaeden.com/ajax/news/1/0";

    @Override
    public Observable<String> getName() {
        return Observable.just(NAME);
    }

    @Override
    public Observable<String> getBaseUrl() {
        return Observable.just(BASE_URL);
    }

    @Override
    public Observable<String> getInitialUpdateUrl() {
        return Observable.just(INITIAL_UPDATE_URL);
    }

    @Override
    public Observable<List<String>> getGenres() {
        List<String> genres = new ArrayList<>();

        return Observable.just(genres);
    }

    @Override
    public Observable<UpdatePageMarker> pullLatestUpdatesFromNetwork(final UpdatePageMarker newUpdate) {
        return MangaService.getPermanentInstance()
                .getResponse(newUpdate.getNextPageUrl())
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return MangaService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<UpdatePageMarker>>() {
                    @Override
                    public Observable<UpdatePageMarker> call(String s) {
                        return Observable.just(parseHtmlToLatestUpdates(newUpdate, s));
                    }
                });
    }

    private UpdatePageMarker parseHtmlToLatestUpdates(UpdatePageMarker newUpdate, String unparsedHtml) {
        Document parsedDocument = Jsoup.parse(unparsedHtml);

        List<MangaEden> updatedMangaList = scrapeUpdateMangasFromParsedDocument(parsedDocument);
        updateLibraryInDatabase(updatedMangaList);

        String nextPageUrl = findNextUrlFromUpdatePageMarker(newUpdate);
        int lastMangaPosition = updatedMangaList.size();

        return new UpdatePageMarker(nextPageUrl, lastMangaPosition);
    }

    private List<MangaEden> scrapeUpdateMangasFromParsedDocument(Document parsedDocument) {
        List<MangaEden> updatedMangaList = new ArrayList<MangaEden>();

        Elements updatedHtmlBlocks = parsedDocument.select("body > li");
        for (Element currentHtmlBlock : updatedHtmlBlocks) {
            MangaEden currentlyUpdatedManga = constructMangaFromHtmlBlock(currentHtmlBlock);

            updatedMangaList.add(currentlyUpdatedManga);
        }

        return updatedMangaList;
    }

    private MangaEden constructMangaFromHtmlBlock(Element htmlBlock) {
        MangaEden mangaFromHtmlBlock = DefaultFactory.Manga.constructDefault();

        Element urlElement = htmlBlock.select("div.newsManga").first();
        Element nameElement = htmlBlock.select("div.manga_tooltop_header > a").first();
        Element updateElement = htmlBlock.select("div.chapterDate").first();

        if (urlElement != null) {
            String fieldUrl = "https://www.mangaeden.com/api/manga/" + urlElement.id().substring(0, 24) + "/";
            mangaFromHtmlBlock.setUrl(fieldUrl);
        }
        if (nameElement != null) {
            String fieldName = nameElement.text();
            mangaFromHtmlBlock.setTitle(fieldName);
        }
        if (updateElement != null) {
            long fieldUpdate = parseUpdateFromElement(updateElement);
            mangaFromHtmlBlock.setUpdated(fieldUpdate);
        }

        int updateCount = htmlBlock.select("div.chapterDate").size();
        mangaFromHtmlBlock.setUpdateCount(updateCount);

        return mangaFromHtmlBlock;
    }

    private long parseUpdateFromElement(Element updateElement) {
        String updatedDateAsString = updateElement.text();

        if (updatedDateAsString.contains("Today")) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            try {
                Date withoutDay = new SimpleDateFormat("h:mm a", Locale.ENGLISH).parse(updatedDateAsString.replace("Today", ""));
                return today.getTimeInMillis() + withoutDay.getTime();
            } catch (ParseException e) {
                return today.getTimeInMillis();
            }
        } else if (updatedDateAsString.contains("Yesterday")) {
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            yesterday.set(Calendar.MILLISECOND, 0);

            try {
                Date withoutDay = new SimpleDateFormat("h:mm a", Locale.ENGLISH).parse(updatedDateAsString.replace("Yesterday", ""));
                return yesterday.getTimeInMillis() + withoutDay.getTime();
            } catch (ParseException e) {
                return yesterday.getTimeInMillis();
            }
        } else {
            try {
                Date specificDate = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).parse(updatedDateAsString);

                return specificDate.getTime();
            } catch (ParseException e) {
                // Do Nothing.
            }
        }

        return DefaultFactory.Manga.DEFAULT_UPDATED;
    }

    private void updateLibraryInDatabase(List<MangaEden> mangaList) {
        DBManager dbManager = new DBManager();
        dbManager.saveMangReaderList(mangaList);
    }

    private String findNextUrlFromUpdatePageMarker(UpdatePageMarker newUpdate) {
        String requestUrl = newUpdate.getNextPageUrl();

        if (!requestUrl.equals(DefaultFactory.UpdatePageMarker.DEFAULT_NEXT_PAGE_URL)) {
            String currentPageNumber = requestUrl.substring(0, requestUrl.lastIndexOf("/0")).replaceAll("[^\\d]", "");

            int newPageNumber = Integer.parseInt(currentPageNumber) + 1;
            return "http://www.mangaeden.com/ajax/news/" + newPageNumber + "/0";
        }

        return DefaultFactory.UpdatePageMarker.DEFAULT_NEXT_PAGE_URL;
    }

    @Override
    public Observable<MangaEden> pullMangaFromNetwork(final ReaderWrapper request) {
        return MangaService.getPermanentInstance()
                .getResponse(request.getUrl())
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return MangaService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<MangaEden>>() {
                    @Override
                    public Observable<MangaEden> call(final String unparsedJson) {
                        return Observable.create(new Observable.OnSubscribe<MangaEden>() {
                            @Override
                            public void call(Subscriber<? super MangaEden> subscriber) {
                                try {
                                    subscriber.onNext(parseJsonToManga(request, unparsedJson));
                                    subscriber.onCompleted();
                                } catch (Throwable e) {
                                    subscriber.onError(e);
                                }
                            }
                        });
                    }
                });
    }

    private MangaEden parseJsonToManga(ReaderWrapper request, String unparsedJson) throws JSONException {
        JSONObject parsedJsonObject = new JSONObject(unparsedJson);

        String fieldGenre = "";
        JSONArray genreArrayNodes = parsedJsonObject.getJSONArray("categories");
        for (int index = 0; index < genreArrayNodes.length(); index++) {
            if (index != genreArrayNodes.length() - 1) {
                fieldGenre += genreArrayNodes.getString(index) + ", ";
            } else {
                fieldGenre += genreArrayNodes.getString(index);
            }
        }

        DBManager dbManager = new DBManager();

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<String>();

        selection.append(Tables.MangasReader.URL + " = ?");
        selectionArgs.add(request.getUrl());

        Cursor cursor = dbManager.getDBRead().query(Tables.MangasReader.TABLE_NAME, null,
                selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]),
                null, null, null, "1");

        MangaEden newManga = null;

        if (cursor.moveToFirst()) {
            do {
                newManga =  MangaEden.fromCursor(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();

        newManga.setArtist(parsedJsonObject.getString("artist"));
        newManga.setAuthor(parsedJsonObject.getString("author"));
        newManga.setDescription(parsedJsonObject.getString("description").trim());
        newManga.setGenre(fieldGenre);
        newManga.setCompleted(parsedJsonObject.getInt("status") == 2);
        newManga.setImageUrl("https://cdn.mangaeden.com/mangasimg/" + parsedJsonObject.getString("image"));
        newManga.setInitialized(true);

        long id = 0;

        if (newManga != null) {
            ContentValues cv = new ContentValues();

            cv.put(Tables.MangasReader.ARTIST, newManga.getArtist());
            cv.put(Tables.MangasReader.AUTHOR, newManga.getAuthor());
            cv.put(Tables.MangasReader.DESCRIPTION, newManga.getDescription());
            cv.put(Tables.MangasReader.GENRE, newManga.getGenre());
            cv.put(Tables.MangasReader.COMPLETED, newManga.isCompleted());
            cv.put(Tables.MangasReader.IMAGE_URL, newManga.getImageUrl());
            cv.put(Tables.MangasReader.INITIALIZED, newManga.isInitialized() ? 1 : 0);

            id = dbManager.getDBWrite().update(Tables.MangasReader.TABLE_NAME, cv, " _id = ? ", new String[] {Integer.toString(newManga.getId())});
        }

        return newManga;
    }

    @Override
    public Observable<List<Chapter>> pullChaptersFromNetwork(final ReaderWrapper request) {
        return MangaService.getPermanentInstance()
                .getResponse(request.getUrl())
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return MangaService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<List<Chapter>>>() {
                    @Override
                    public Observable<List<Chapter>> call(final String unParsedJson) {
                        return Observable.create(new Observable.OnSubscribe<List<Chapter>>() {
                            @Override
                            public void call(Subscriber<? super List<Chapter>> subscriber) {
                                try {
                                    subscriber.onNext(parseJsonToChapters(request, unParsedJson));
                                    subscriber.onCompleted();
                                } catch (Throwable e) {
                                    subscriber.onError(e);
                                }
                            }
                        });
                    }
                });
    }

    private List<Chapter> parseJsonToChapters(ReaderWrapper request, String unParsedJson) throws JSONException{
        JSONObject parsedJsonObject = new JSONObject(unParsedJson);

        List<Chapter> chapterList = scrapeChaptersFromParsedJson(parsedJsonObject);
        chapterList = setSourceForChapterList(chapterList);
        chapterList = setParentUrlForChapterList(chapterList, request.getUrl());
        chapterList = setNumberForChapterList(chapterList);

        saveChaptersToDatabase(chapterList, request.getUrl());

        return chapterList;
    }

    private List<Chapter> scrapeChaptersFromParsedJson(JSONObject parsedJsonObject) throws JSONException {
        List<Chapter> chapterList = new ArrayList<Chapter>();

        String mangaName = parsedJsonObject.getString("title");
        JSONArray chapterArrayNodes = parsedJsonObject.getJSONArray("chapters");
        for (int index = 0; index < chapterArrayNodes.length(); index++) {
            JSONArray currentChapterArray = chapterArrayNodes.getJSONArray(index);

            Chapter currentChapter = constructChapterFromJSONArray(currentChapterArray, mangaName);

            chapterList.add(currentChapter);
        }

        return chapterList;
    }

    private Chapter constructChapterFromJSONArray(JSONArray chapterNode, String mangaName) throws JSONException {
        Chapter newChapter = DefaultFactory.Chapter.constructDefault();

        newChapter.setUrl("https://www.mangaeden.com/api/chapter/" + chapterNode.getString(3) + "/");
        newChapter.setTitle(mangaName + " " + chapterNode.getDouble(0));
        newChapter.setDate(chapterNode.getLong(1) * 1000);

        return newChapter;
    }

    private List<Chapter> setSourceForChapterList(List<Chapter> chapterList) {
        /*for (Chapter currentChapter : chapterList) {
            currentChapter.setSource(NAME);
        }*/

        return chapterList;
    }

    private List<Chapter> setParentUrlForChapterList(List<Chapter> chapterList, String parentUrl) {
        for (Chapter currentChapter : chapterList) {
            currentChapter.setParentUrl(parentUrl);
        }

        return chapterList;
    }

    private List<Chapter> setNumberForChapterList(List<Chapter> chapterList) {
        Collections.reverse(chapterList);
        for (int index = 0; index < chapterList.size(); index++) {
            chapterList.get(index).setNumber(index + 1);
        }

        return chapterList;
    }

    private void saveChaptersToDatabase(List<Chapter> chapterList, String parentUrl) {
        DBManager dbManager = new DBManager();

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<String>();

        selection.append(Tables.MangaChapters.PARENT_URL + " = ?");
        selectionArgs.add(parentUrl);

        dbManager.getDBWrite().beginTransaction();
        try {
            dbManager.getDBWrite().delete(Tables.MangaChapters.TABLE_NAME,
                    selection.toString(),
                    selectionArgs.toArray(new String[selectionArgs.size()]));

            long id = 0;

            for (Chapter currentChapter : chapterList) {
                ContentValues cv = new ContentValues();

                cv.put(Tables.MangaChapters.CHAPTER_DATE, currentChapter.getDate());
                cv.put(Tables.MangaChapters.NUMBER, currentChapter.getNumber());
                cv.put(Tables.MangaChapters.PARENT_URL, currentChapter.getParentUrl());
                cv.put(Tables.MangaChapters.TITLE, currentChapter.getTitle());
                cv.put(Tables.MangaChapters.URL, currentChapter.getUrl());

                dbManager.getDBWrite().insert(Tables.MangaChapters.TABLE_NAME, null, cv);
            }

            dbManager.getDBWrite().setTransactionSuccessful();
        } finally {
            dbManager.getDBWrite().endTransaction();
        }
    }

    @Override
    public Observable<String> pullImageUrlsFromNetwork(final ReaderWrapper request) {
        return MangaService.getPermanentInstance()
                .getResponse(request.getUrl())
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return MangaService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<List<String>>>() {
                    @Override
                    public Observable<List<String>> call(final String unParsedJson) {
                        return Observable.create(new Observable.OnSubscribe<List<String>>() {
                            @Override
                            public void call(Subscriber<? super List<String>> subscriber) {
                                try {
                                    subscriber.onNext(parseJsonToImageUrls(unParsedJson));
                                    subscriber.onCompleted();
                                } catch (Throwable e) {
                                    subscriber.onError(e);
                                }
                            }
                        });
                    }
                })
                .doOnNext(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> imageUrls) {
                        CacheProvider.getInstance().putImageUrlsToDiskCache(request.getUrl(), imageUrls);
                    }
                })
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> imageUrls) {
                        return Observable.from(imageUrls.toArray(new String[imageUrls.size()]));
                    }
                });
    }

    private List<String> parseJsonToImageUrls(String unParsedJson) throws JSONException{
        JSONObject parsedJson = new JSONObject(unParsedJson);

        List<String> imageUrlList = new ArrayList<String>();

        JSONArray imageArrayNodes = parsedJson.getJSONArray("images");
        for (int index = 0; index < imageArrayNodes.length(); index++) {
            JSONArray currentImageNode = imageArrayNodes.getJSONArray(index);

            imageUrlList.add("https://cdn.mangaeden.com/mangasimg/" + currentImageNode.getString(1));
        }
        Collections.reverse(imageUrlList);

        return imageUrlList;
    }

    private static final String INITIAL_DATABASE_URL = "http://www.mangaeden.com/api/list/0/";

    public Observable<ArrayList<MangaEden>> recursivelyConstructDatabase() {
        return MangaService.getTemporaryInstance()
                .getResponse("http://www.mangaeden.com/api/list/0/")
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return MangaService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<ArrayList<MangaEden>>>() {
                    @Override
                    public Observable<ArrayList<MangaEden>> call(final String unParsedJson) {
                        return Observable.create(new Observable.OnSubscribe<ArrayList<MangaEden>>() {
                            @Override
                            public void call(Subscriber<? super ArrayList<MangaEden>> subscriber) {
                                try {
                                    subscriber.onNext(null);//parseEnglish_MangaEden(unParsedJson));
                                    subscriber.onCompleted();
                                } catch (Throwable e) {
                                    subscriber.onError(e);
                                }
                            }
                        });
                    }
                });
    }
/*
    @Override
    public Observable<ArrayList<MangaEden>> recursivelyConstructDatabase() {
        return Observable.create(new Observable.OnSubscribe<ArrayList<MangaEden>>() {
            @Override
            public void call(Subscriber<? super ArrayList<MangaEden>> subscriber) {
                try {
                    ReaderService service = new ReaderService();
                    subscriber.onNext(parseEnglish_MangaEden(service.getMangaList()));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }
    */
/*
    @Override
    public Observable<ArrayList<MangaEden>> recursivelyConstructDatabase() {
        return Observable.create(new Observable.OnSubscribe<ArrayList<MangaEden>>() {
            @Override
            public void call(final Subscriber<? super ArrayList<MangaEden>> subscriber) {
                try {
                    ReaderService service = new ReaderService();
                    MangaListJson listJson = service.getMangaList();
                    subscriber.onNext(parseEnglish_MangaEden(listJson));
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }
*/
    @Override
    public Observable<String> recursivelyConstructDatabase(String url) {
        return MangaService.getTemporaryInstance()
                .getResponse("https://www.mangaeden.com/api/list/0/")
                .flatMap(new Func1<Response, Observable<String>>() {
                    @Override
                    public Observable<String> call(Response response) {
                        return MangaService.mapResponseToString(response);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(final String unParsedJson) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                try {
                                    subscriber.onNext(parseEnglish_MangaEden(unParsedJson));
                                    subscriber.onCompleted();
                                } catch (Throwable e) {
                                    subscriber.onError(e);
                                }
                            }
                        });
                    }
                });
    }
/*
    private ArrayList<MangaEden> parseEnglish_MangaEden(MangaListJson mangaJson) throws JSONException {
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

        return mangaList;
    }
    */

    private String parseEnglish_MangaEden(String unParsedJson) throws JSONException {
        JSONObject parsedJson = new JSONObject(unParsedJson);

        ArrayList<MangaEden> mangaList = new ArrayList<MangaEden>();
        JSONArray mangaArrayNodes = parsedJson.getJSONArray("manga");
        for (int index = 0; index < mangaArrayNodes.length(); index++) {
            JSONObject currentMangaNode = mangaArrayNodes.getJSONObject(index);

            MangaEden newManga = new MangaEden();
            //newManga.setSource(NAME);
            newManga.setUrl("https://www.mangaeden.com/api/manga/" + currentMangaNode.getString("i") + "/");
            newManga.setTitle(currentMangaNode.getString("t"));
            newManga.setImageUrl("https://cdn.mangaeden.com/mangasimg/" + currentMangaNode.getString("im"));
            newManga.setCompleted(currentMangaNode.getInt("s") == 2);
            newManga.setRank(currentMangaNode.getInt("h")); // Hits.

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

        dbManager.getDBWrite().delete(Tables.MangasReader.TABLE_NAME, null, null);

        dbManager.getDBWrite().beginTransaction();
        try {
            for (MangaEden currentManga : mangaList) {
                ContentValues cv = new ContentValues();

                cv.put(Tables.MangasReader.URL, currentManga.getUrl());
                cv.put(Tables.MangasReader.TITLE, currentManga.getTitle());
                cv.put(Tables.MangasReader.IMAGE_URL, currentManga.getImageUrl());
                cv.put(Tables.MangasReader.COMPLETED, currentManga.isCompleted());
                cv.put(Tables.MangasReader.RANK, currentManga.getRank());

                dbManager.getDBWrite().insert(Tables.MangasReader.TABLE_NAME, null, cv);
            }

            dbManager.getDBWrite().setTransactionSuccessful();
        } finally {
            dbManager.getDBWrite().endTransaction();
        }

        return null;
    }
}
