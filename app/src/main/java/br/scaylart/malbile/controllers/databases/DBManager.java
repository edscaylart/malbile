package br.scaylart.malbile.controllers.databases;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.controllers.account.AccountService;
import br.scaylart.malbile.controllers.networks.BaseService.ListType;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.models.Message;
import br.scaylart.malbile.models.RelatedRecord;
import br.scaylart.malbile.models.User;
import br.scaylart.malbile.reader.model.MangaEden;
import br.scaylart.malbile.reader.model.RecentChapter;
import br.scaylart.malbile.utils.StringUtils;

public class DBManager {
    ApplicationSQLiteOpenHelper dbHelper;
    SQLiteDatabase dbRead;

    public DBManager() {
        if (dbHelper == null)
            dbHelper = ApplicationSQLiteOpenHelper.getInstance();
    }

    public synchronized SQLiteDatabase getDBWrite() {
        return dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDBRead() {
        if (dbRead == null)
            dbRead = dbHelper.getReadableDatabase();
        return dbRead;
    }

    public void saveAnimeList(ArrayList<Anime> list, String username) {
        Integer userId = getUserId(username);
        if (list != null && list.size() > 0 && userId != null) {
            try {
                getDBWrite().beginTransaction();
                for (Anime anime : list)
                    saveAnime(anime, true, userId);
                getDBWrite().setTransactionSuccessful();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } finally {
                getDBWrite().endTransaction();
            }
        }
    }

    public void saveAnime(Anime anime, boolean ignoreSynopsis, String username) {
        Integer userId;
        if (username.equals(""))
            userId = 0;
        else
            userId = getUserId(username);
        saveAnime(anime, ignoreSynopsis, userId);
    }

    public void saveAnime(Anime anime, boolean ignoreSynopsis, int userId) {
        ContentValues cv = new ContentValues();

        cv.put(Tables.COLUMN_ID, anime.getId());
        cv.put(Tables.Animes.TITLE, anime.getTitle());
        cv.put(Tables.Animes.TYPE, anime.getType());
        cv.put(Tables.Animes.IMAGE_URL, anime.getImageUrl());
        cv.put(Tables.Animes.STATUS, anime.getStatus());
        cv.put(Tables.Animes.EPISODES, anime.getEpisodes());

        if (!ignoreSynopsis) {
            cv.put(Tables.Animes.SYNOPSIS, anime.getSynopsis());
            cv.put(Tables.Animes.MEMBERS_SCORE, anime.getMembersScore());
            cv.put(Tables.Animes.CLASSIFICATION, anime.getClassification());
            cv.put(Tables.Animes.MEMBERS_COUNT, anime.getMembersCount());
            cv.put(Tables.Animes.FAVORITED_COUNT, anime.getFavoritedCount());
            //cv.put("popularityRank", anime.getPopularityRank());
            cv.put(Tables.Animes.RANK, anime.getRank());
            cv.put(Tables.Animes.START_DATE, anime.getStartDate());
            cv.put(Tables.Animes.END_DATE, anime.getEndDate());
        }

        int updateResult = getDBWrite().update(Tables.Animes.TABLE_NAME, cv, Tables.COLUMN_ID + " = ?", new String[]{Integer.toString(anime.getId())});
        if (updateResult == 0) {
            Long insertResult = getDBWrite().insert(Tables.Animes.TABLE_NAME, null, cv);
            if (insertResult > 0) {
                anime.setId(insertResult.intValue());
            }
        }

        if (anime.getId() > 0) {
            if (!ignoreSynopsis) {
                if (anime.getGenres() != null) {
                    getDBWrite().delete(Tables.AnimeGenres.TABLE_NAME,
                            Tables.AnimeGenres.ANIME_ID + " = ?",
                            new String[]{String.valueOf(anime.getId())});

                    // delete old relations
                    for (RelatedRecord genre : anime.getGenres()) {
                        Integer genreId = getGenreId(genre.getTitle());
                        if (genreId == null || genreId < 1) {
                            ContentValues gv = new ContentValues();
                            gv.put("record_name", genre.getTitle());
                            gv.put("url", genre.getUrl());
                            genreId = (int) getDBWrite().replace(Tables.Genres.TABLE_NAME, null, gv);
                        }
                        ContentValues gcv = new ContentValues();
                        gcv.put("anime_id", anime.getId());
                        gcv.put("genre_id", genreId);
                        getDBWrite().replace(Tables.AnimeGenres.TABLE_NAME, null, gcv);
                    }
                }

                saveAnimeToAnimeRelation(anime.getAlternativeVersions(), anime.getId(), Tables.RELATION_TYPE_ALTERNATIVE);
                saveAnimeToAnimeRelation(anime.getPrequels(), anime.getId(), Tables.RELATION_TYPE_PREQUEL);
                saveAnimeToAnimeRelation(anime.getSequels(), anime.getId(), Tables.RELATION_TYPE_SEQUEL);
                saveAnimeToAnimeRelation(anime.getSideStories(), anime.getId(), Tables.RELATION_TYPE_SIDE_STORY);
                saveAnimeToAnimeRelation(anime.getSpinOffs(), anime.getId(), Tables.RELATION_TYPE_SPIN_OFF);

                if (anime.getMangaAdaptations() != null) {
                    // delete old relations
                    getDBWrite().delete(Tables.AnimeMangas.TABLE_NAME,
                            Tables.AnimeMangas.ANIME_ID + " = ? AND " + Tables.AnimeMangas.RELATED_ID + " = ?",
                            new String[]{String.valueOf(anime.getId()), Tables.RELATION_TYPE_ADAPTATION});

                    for (RelatedRecord mangaStub : anime.getMangaAdaptations()) {
                        saveAnimeToMangaRelation(anime.getId(), mangaStub, Tables.RELATION_TYPE_ADAPTATION);
                    }
                }

                if (anime.getParentStory() != null) {
                    // delete old relations
                    getDBWrite().delete(Tables.AnimeMangas.TABLE_NAME,
                            Tables.AnimeMangas.ANIME_ID + " = ? AND " + Tables.AnimeMangas.RELATED_ID + " = ?",
                            new String[]{String.valueOf(anime.getId()), Tables.RELATION_TYPE_PARENT_STORY});
                    saveAnimeToAnimeRelation(anime.getId(), anime.getParentStory(), Tables.RELATION_TYPE_PARENT_STORY);
                }

                if (anime.getOtherTitles() != null) {
                    if (anime.getOtherTitlesEnglish() != null) {
                        // delete old relations
                        getDBWrite().delete(Tables.AnimeOtherTitles.TABLE_NAME,
                                Tables.AnimeOtherTitles.ANIME_ID + " = ? AND " + Tables.AnimeOtherTitles.TYPE + " = ?",
                                new String[]{String.valueOf(anime.getId()), Tables.TITLE_TYPE_ENGLISH});
                        for (String title : anime.getOtherTitlesEnglish()) {
                            saveAnimeOtherTitle(anime.getId(), title, Tables.TITLE_TYPE_ENGLISH);
                        }
                    }

                    if (anime.getOtherTitlesJapanese() != null) {
                        // delete old relations
                        getDBWrite().delete(Tables.AnimeOtherTitles.TABLE_NAME,
                                Tables.AnimeOtherTitles.ANIME_ID + " = ? AND " + Tables.AnimeOtherTitles.TYPE + " = ?",
                                new String[]{String.valueOf(anime.getId()), Tables.TITLE_TYPE_JAPANESE});
                        for (String title : anime.getOtherTitlesJapanese()) {
                            saveAnimeOtherTitle(anime.getId(), title, Tables.TITLE_TYPE_JAPANESE);
                        }
                    }

                    if (anime.getOtherTitlesSynonyms() != null) {
                        // delete old relations
                        getDBWrite().delete(Tables.AnimeOtherTitles.TABLE_NAME,
                                Tables.AnimeOtherTitles.ANIME_ID + " = ? AND " + Tables.AnimeOtherTitles.TYPE + " = ?",
                                new String[]{String.valueOf(anime.getId()), Tables.TITLE_TYPE_SYNONYM});
                        for (String title : anime.getOtherTitlesSynonyms()) {
                            saveAnimeOtherTitle(anime.getId(), title, Tables.TITLE_TYPE_SYNONYM);
                        }
                    }
                }

                // TODO implementar producers em animes e no Parser
                /*
                if (anime.getProducers() != null) {
                    // delete old relations
                    getDBWrite().delete(DBHelper.TABLE_ANIME_PRODUCER, "anime_id = ?", new String[]{String.valueOf(anime.getId())});
                    for (String producer : anime.getProducers()) {
                        Integer producerId = getProducerId(producer);
                        if (producerId != null) {
                            ContentValues gcv = new ContentValues();
                            gcv.put("anime_id", anime.getId());
                            gcv.put("producer_id", producerId);
                            getDBWrite().replace(DBHelper.TABLE_ANIME_PRODUCER, null, gcv);

                        }
                    }
                }
                */
            }

            if (userId > 0) {
                ContentValues alcv = new ContentValues();
                alcv.put(Tables.AnimeLists.PROFILE_ID, userId);
                alcv.put(Tables.AnimeLists.ANIME_ID, anime.getId());
                alcv.put(Tables.AnimeLists.STATUS, anime.getMyStatus());
                alcv.put(Tables.AnimeLists.SCORE, anime.getScore());
                alcv.put(Tables.AnimeLists.EPISODES, anime.getWatchedEpisodes());
                alcv.put(Tables.AnimeLists.START_DATE, anime.getListStartDate());
                alcv.put(Tables.AnimeLists.END_DATE, anime.getListFinishDate());
                alcv.put(Tables.AnimeLists.REWATCHING, (anime.isRewatching() ? 1 : 0));
                alcv.put(Tables.AnimeLists.REWATCHING_COUNT, anime.getRewatchingCount());
                alcv.put(Tables.AnimeLists.REWATCHING_VALUE, anime.getRewatchingValue());
                alcv.put(Tables.AnimeLists.DIRTY, anime.getDirty() != null ? new Gson().toJson(anime.getDirty()) : null);
                if (anime.getLastUpdate() != 0)
                    alcv.put(Tables.AnimeLists.LAST_UPDATE, anime.getLastUpdate());//anime.getLastDateUpdate().getTime());
                getDBWrite().replace(Tables.AnimeLists.TABLE_NAME, null, alcv);
            }
        }
    }

    public void saveAnimeToAnimeRelation(ArrayList<RelatedRecord> record, int id, String relationType) {
        if (record != null) {
            // delete old relations
            getDBWrite().delete(Tables.AnimeAnimes.TABLE_NAME,
                    Tables.AnimeAnimes.ANIME_ID + " = ? AND " + Tables.AnimeAnimes.TYPE + " = ?",
                    new String[]{String.valueOf(id), relationType});

            for (RelatedRecord related : record) {
                saveAnimeToAnimeRelation(id, related, relationType);
            }
        }
    }

    public Anime getAnime(Integer id, String username) {
        Anime result = null;

        Cursor cursor = getDBRead().rawQuery(
                "SELECT a.*, " + Tables.AnimeLists.getFields("al.") +
                        " FROM " + Tables.AnimeLists.TABLE_NAME + " al " +
                        " INNER JOIN " + Tables.Animes.TABLE_NAME + " a ON al." + Tables.AnimeLists.ANIME_ID + " = a." + Tables.COLUMN_ID +
                        " WHERE al.profile_id = ? AND a." + Tables.COLUMN_ID + " = ? ",
                new String[]{getUserId(username).toString(), id.toString()});
        if (cursor.moveToFirst()) {
            result = Anime.fromCursor(cursor);
            result.setGenres(getAnimeGenres(result.getId()));
            result.setAlternativeVersions(getAnimeToAnimeRelations(result.getId(), Tables.RELATION_TYPE_ALTERNATIVE));
            result.setPrequels(getAnimeToAnimeRelations(result.getId(), Tables.RELATION_TYPE_PREQUEL));
            result.setSequels(getAnimeToAnimeRelations(result.getId(), Tables.RELATION_TYPE_SEQUEL));
            result.setSideStories(getAnimeToAnimeRelations(result.getId(), Tables.RELATION_TYPE_SIDE_STORY));
            result.setSpinOffs(getAnimeToAnimeRelations(result.getId(), Tables.RELATION_TYPE_SPIN_OFF));
            result.setMangaAdaptations(getAnimeToMangaRelations(result.getId(), Tables.RELATION_TYPE_ADAPTATION));
            ArrayList<RelatedRecord> parentStory = getAnimeToAnimeRelations(result.getId(), Tables.RELATION_TYPE_PARENT_STORY);
            if (parentStory != null && parentStory.size() > 0) {
                result.setParentStory(parentStory.get(0));
            }
            HashMap<String, ArrayList<String>> otherTitles = new HashMap<>();
            otherTitles.put("english", getAnimeOtherTitles(result.getId(), Tables.TITLE_TYPE_ENGLISH));
            otherTitles.put("japanese", getAnimeOtherTitles(result.getId(), Tables.TITLE_TYPE_JAPANESE));
            otherTitles.put("synonyms", getAnimeOtherTitles(result.getId(), Tables.TITLE_TYPE_SYNONYM));
            result.setOtherTitles(otherTitles);
            // TODO implementar producers
            //result.setProducers(getAnimeProducers(result.getId()));
        }
        cursor.close();
        return result;
    }

    private boolean deleteAnime(Integer id) {
        return getDBWrite().delete(Tables.Animes.TABLE_NAME, Tables.COLUMN_ID + " = ?", new String[]{id.toString()}) == 1;
    }

    public boolean deleteAnimeFromAnimelist(Integer id, String username) {
        boolean result = false;
        Integer userId = getUserId(username);
        if (userId != 0) {
            result = getDBWrite().delete(Tables.AnimeLists.TABLE_NAME,
                    Tables.AnimeLists.PROFILE_ID + " = ? AND " + Tables.AnimeLists.ANIME_ID + " = ?",
                    new String[]{userId.toString(), id.toString()}) == 1;
            if (result) {
                boolean isUsed;
                /* check if this record is used for other relations and delete if it's not to keep the database
                 * still used relations can be:
                 * - animelist of other user
                 * - record is related to other anime or manga (e.g. as sequel or adaptation)
                 */
                // used in other animelist?
                isUsed = recordExists(Tables.AnimeLists.TABLE_NAME, Tables.AnimeLists.ANIME_ID, id.toString());
                if (!isUsed) { // no need to check more if its already used
                    // used as related record of other anime?
                    isUsed = recordExists(Tables.AnimeAnimes.TABLE_NAME, Tables.AnimeAnimes.RELATED_ID, id.toString());
                }
                if (!isUsed) { // no need to check more if its already used
                    // used as related record of an manga?
                    isUsed = recordExists(Tables.MangaAnimes.TABLE_NAME, Tables.MangaAnimes.RELATED_ID, id.toString());
                }
                if (!isUsed) {// its not used anymore, delete it
                    deleteAnime(id);
                }
            }
        }
        return result;
    }

    // delete all anime records without relations, because they're "dead" records
    public void cleanUpAnimeTable() {
        getDBWrite().rawQuery("DELETE FROM " + Tables.Animes.TABLE_NAME + " WHERE " +
                Tables.COLUMN_ID + " NOT IN (SELECT DISTINCT " + Tables.AnimeLists.ANIME_ID + " FROM " + Tables.AnimeLists.TABLE_NAME + ") AND " +
                Tables.COLUMN_ID + " NOT IN (SELECT DISTINCT " + Tables.AnimeAnimes.RELATED_ID + " FROM " + Tables.AnimeAnimes.TABLE_NAME + ") AND " +
                Tables.COLUMN_ID + " NOT IN (SELECT DISTINCT " + Tables.MangaAnimes.RELATED_ID + " FROM " + Tables.MangaAnimes.TABLE_NAME + ")", null);
    }

    public ArrayList<Anime> getAnimeList(String listStatus, String username) {
        if (listStatus == "")
            return getAnimeList(getUserId(username), "", false);
        else
            return getAnimeList(getUserId(username), listStatus, false);
    }

    public ArrayList<Anime> getDirtyAnimeList(String username) {
        return getAnimeList(getUserId(username), "", true);
    }

    private ArrayList<Anime> getAnimeList(int userId, String listStatus, boolean dirtyOnly) {
        ArrayList<Anime> result = null;
        Cursor cursor;
        try {
            ArrayList<String> selArgs = new ArrayList<>();
            selArgs.add(String.valueOf(userId));
            if (listStatus != "")
                selArgs.add(listStatus);

            cursor = getDBRead().rawQuery("SELECT a.*, " + Tables.AnimeLists.getFields("al.") +
                    " FROM " + Tables.AnimeLists.TABLE_NAME + " al " +
                    " INNER JOIN " + Tables.Animes.TABLE_NAME + " a ON al." + Tables.AnimeLists.ANIME_ID + " = a." + Tables.COLUMN_ID +
                    " WHERE al." + Tables.AnimeLists.PROFILE_ID + " = ? " +
                    (listStatus != "" ? " AND al." + Tables.AnimeLists.STATUS + " = ? " : "") +
                    (dirtyOnly ? " AND al." + Tables.AnimeLists.DIRTY + " IS NOT NULL " : "") +
                    " ORDER BY a." + Tables.Animes.TITLE + " COLLATE NOCASE", selArgs.toArray(new String[selArgs.size()]));

            if (cursor.moveToFirst()) {
                result = new ArrayList<>();
                do {
                    result.add(Anime.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public ArrayList<Anime> getSimilarAnimeList() {
        ArrayList<Anime> result = null;
        Cursor cursor;
        int userId = getUserId(AccountService.getUsername());
        try {
            ArrayList<String> selArgs = new ArrayList<>();
            selArgs.add(String.valueOf(userId));

            cursor = getDBRead().rawQuery("SELECT a.*, " + Tables.AnimeLists.getFields("al.") +
                    " FROM " + Tables.AnimeLists.TABLE_NAME + " al " +
                    " INNER JOIN " + Tables.Animes.TABLE_NAME + " a ON al." + Tables.AnimeLists.ANIME_ID + " = a." + Tables.COLUMN_ID +
                    " WHERE al." + Tables.AnimeLists.PROFILE_ID + " = ? " +
                    " AND al." + Tables.AnimeLists.STATUS + " IN (1,2,3,4) " +
                    " ORDER BY a." + Tables.Animes.TITLE + " COLLATE NOCASE", selArgs.toArray(new String[selArgs.size()]));

            if (cursor.moveToFirst()) {
                result = new ArrayList<>();
                do {
                    result.add(Anime.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public void saveMangaList(ArrayList<Manga> list, String username) {
        Integer userId = getUserId(username);
        if (list != null && list.size() > 0 && userId != null) {
            try {
                getDBWrite().beginTransaction();
                for (Manga manga : list)
                    saveManga(manga, true, userId);
                getDBWrite().setTransactionSuccessful();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } finally {
                getDBWrite().endTransaction();
            }
        }
    }

    public void saveManga(Manga manga, boolean ignoreSynopsis, String username) {
        Integer userId;
        if (username.equals(""))
            userId = 0;
        else
            userId = getUserId(username);
        saveManga(manga, ignoreSynopsis, userId);
    }

    public void saveManga(Manga manga, boolean ignoreSynopsis, int userId) {
        ContentValues cv = new ContentValues();

        cv.put(Tables.COLUMN_ID, manga.getId());
        cv.put(Tables.Mangas.TITLE, manga.getTitle());
        cv.put(Tables.Mangas.TYPE, manga.getType());
        cv.put(Tables.Mangas.IMAGE_URL, manga.getImageUrl());
        cv.put(Tables.Mangas.STATUS, manga.getStatus());
        cv.put(Tables.Mangas.VOLUMES, manga.getVolumes());
        cv.put(Tables.Mangas.CHAPTERS, manga.getChapters());

        if (!ignoreSynopsis) {
            cv.put(Tables.Mangas.SYNOPSIS, manga.getSynopsis());
            cv.put(Tables.Mangas.START_DATE, manga.getStartDate());
            cv.put(Tables.Mangas.END_DATE, manga.getEndDate());
            cv.put(Tables.Mangas.MEMBERS_COUNT, manga.getMembersCount());
            cv.put(Tables.Mangas.MEMBERS_SCORE, manga.getMembersScore());
            cv.put(Tables.Mangas.FAVORITED_COUNT, manga.getFavoritedCount());
            //cv.put("popularityRank", manga.getPopularityRank());
            cv.put(Tables.Mangas.RANK, manga.getRank());
            //cv.put("listedId", manga.getListedId());
        }

        // don't use replace it replaces synopsis with null even when we don't put it in the ContentValues
        int updateResult = getDBWrite().update(Tables.Mangas.TABLE_NAME, cv, Tables.COLUMN_ID + " = ?", new String[]{Integer.toString(manga.getId())});
        if (updateResult == 0) {
            Long insertResult = getDBWrite().insert(Tables.Mangas.TABLE_NAME, null, cv);
            if (insertResult > 0) {
                manga.setId(insertResult.intValue());
            }
        }

        if (manga.getId() > 0) { // save/update relations if saving was successful
            if (!ignoreSynopsis) { // only on DetailView!
                if (manga.getGenres() != null) {
                    getDBWrite().delete(Tables.MangaGenres.TABLE_NAME, Tables.MangaGenres.MANGA_ID + " = ?", new String[]{String.valueOf(manga.getId())});

                    // delete old relations
                    for (RelatedRecord genre : manga.getGenres()) {
                        Integer genreId = getGenreId(genre.getTitle());
                        if (genreId == null || genreId < 1) {
                            ContentValues gv = new ContentValues();
                            gv.put(Tables.Genres.NAME, genre.getTitle());
                            gv.put(Tables.Genres.URL, genre.getUrl());
                            genreId = (int) getDBWrite().replace(Tables.Genres.TABLE_NAME, null, gv);
                        }
                        ContentValues gcv = new ContentValues();
                        gcv.put(Tables.MangaGenres.MANGA_ID, manga.getId());
                        gcv.put(Tables.MangaGenres.GENRE_ID, genreId);
                        getDBWrite().replace(Tables.MangaGenres.TABLE_NAME, null, gcv);
                    }
                }

                if (manga.getAlternativeVersions() != null) {
                    // delete old relations
                    getDBWrite().delete(Tables.MangaMangas.TABLE_NAME,
                            Tables.MangaMangas.MANGA_ID + " = ? AND " + Tables.MangaMangas.TYPE + " = ?",
                            new String[]{String.valueOf(manga.getId()),
                                    Tables.RELATION_TYPE_ALTERNATIVE});

                    for (RelatedRecord mangaStub : manga.getAlternativeVersions()) {
                        saveMangaToMangaRelation(manga.getId(), mangaStub, Tables.RELATION_TYPE_ALTERNATIVE);
                    }
                }

                if (manga.getRelatedManga() != null) {
                    // delete old relations
                    getDBWrite().delete(Tables.MangaMangas.TABLE_NAME,
                            Tables.MangaMangas.MANGA_ID + " = ? AND " + Tables.MangaMangas.TYPE + " = ?",
                            new String[]{String.valueOf(manga.getId()),
                                    Tables.RELATION_TYPE_RELATED});

                    for (RelatedRecord mangaStub : manga.getRelatedManga()) {
                        saveMangaToMangaRelation(manga.getId(), mangaStub, Tables.RELATION_TYPE_RELATED);
                    }
                }

                if (manga.getAnimeAdaptations() != null) {
                    // delete old relations
                    getDBWrite().delete(Tables.MangaAnimes.TABLE_NAME,
                            Tables.MangaAnimes.MANGA_ID + " = ? AND " + Tables.MangaAnimes.TYPE + " = ?",
                            new String[]{String.valueOf(manga.getId()),
                                    Tables.RELATION_TYPE_ADAPTATION});

                    for (RelatedRecord animeStub : manga.getAnimeAdaptations()) {
                        saveMangaToAnimeRelation(manga.getId(), animeStub, Tables.RELATION_TYPE_ADAPTATION);
                    }
                }

                if (manga.getOtherTitles() != null) {
                    if (manga.getOtherTitlesEnglish() != null) {
                        // delete old relations
                        getDBWrite().delete(Tables.MangaOtherTitles.TABLE_NAME,
                                Tables.MangaOtherTitles.MANGA_ID + " = ? and " + Tables.MangaOtherTitles.TYPE + " = ?",
                                new String[]{String.valueOf(manga.getId()),
                                        Tables.TITLE_TYPE_ENGLISH});
                        for (String title : manga.getOtherTitlesEnglish()) {
                            saveMangaOtherTitle(manga.getId(), title, Tables.TITLE_TYPE_ENGLISH);
                        }
                    }

                    if (manga.getOtherTitlesJapanese() != null) {
                        // delete old relations
                        getDBWrite().delete(Tables.MangaOtherTitles.TABLE_NAME,
                                Tables.MangaOtherTitles.MANGA_ID + " = ? and " + Tables.MangaOtherTitles.TYPE + " = ?",
                                new String[]{String.valueOf(manga.getId()),
                                        Tables.TITLE_TYPE_JAPANESE});
                        for (String title : manga.getOtherTitlesJapanese()) {
                            saveMangaOtherTitle(manga.getId(), title, Tables.TITLE_TYPE_JAPANESE);
                        }
                    }

                    if (manga.getOtherTitlesSynonyms() != null) {
                        // delete old relations
                        getDBWrite().delete(Tables.MangaOtherTitles.TABLE_NAME,
                                Tables.MangaOtherTitles.MANGA_ID + " = ? and " + Tables.MangaOtherTitles.TYPE + " = ?",
                                new String[]{String.valueOf(manga.getId()),
                                        Tables.TITLE_TYPE_SYNONYM});
                        for (String title : manga.getOtherTitlesSynonyms()) {
                            saveMangaOtherTitle(manga.getId(), title, Tables.TITLE_TYPE_SYNONYM);
                        }
                    }
                }
            }

            // update mangalist if user id is provided
            if (userId > 0) {
                ContentValues mlcv = new ContentValues();
                mlcv.put(Tables.MangaLists.PROFILE_ID, userId);
                mlcv.put(Tables.MangaLists.MANGA_ID, manga.getId());
                mlcv.put(Tables.MangaLists.STATUS, manga.getMyStatus());
                mlcv.put(Tables.MangaLists.SCORE, manga.getScore());
                mlcv.put(Tables.MangaLists.VOLUMES, manga.getVolumesRead());
                mlcv.put(Tables.MangaLists.CHAPTERS, manga.getChaptersRead());
                mlcv.put(Tables.MangaLists.START_DATE, manga.getListStartDate());
                mlcv.put(Tables.MangaLists.END_DATE, manga.getListFinishDate());
                mlcv.put(Tables.MangaLists.REREADING, manga.isRereading());
                mlcv.put(Tables.MangaLists.REREADING_VALUE, manga.getRereadingValue());
                mlcv.put(Tables.MangaLists.REREADING_COUNT, manga.getRereadingCount());
                mlcv.put(Tables.MangaLists.DIRTY, manga.getDirty() != null ? new Gson().toJson(manga.getDirty()) : null);
                if (manga.getLastUpdate() != 0)
                    mlcv.put(Tables.MangaLists.LAST_UPDATE, manga.getLastUpdate());// manga.getLastDateUpdate().getTime());
                getDBWrite().replace(Tables.MangaLists.TABLE_NAME, null, mlcv);
            }
        }
    }

    public Manga getManga(Integer id, String username) {
        Manga result = null;
        Cursor cursor = getDBRead().rawQuery("SELECT m.*, " + Tables.MangaLists.getFields("ml.") +
                " FROM " + Tables.MangaLists.TABLE_NAME + " ml" +
                " INNER JOIN " + Tables.Mangas.TABLE_NAME + " m ON ml." + Tables.MangaLists.MANGA_ID + " = m." + Tables.COLUMN_ID +
                " WHERE ml." + Tables.MangaLists.PROFILE_ID + " = ? and m." + Tables.COLUMN_ID + " = ?", new String[]{getUserId(username).toString(), id.toString()});
        if (cursor.moveToFirst()) {
            result = Manga.fromCursor(cursor);
            result.setGenres(getMangaGenres(result.getId()));
            result.setAlternativeVersions(getMangaToMangaRelations(result.getId(), Tables.RELATION_TYPE_ALTERNATIVE));
            result.setRelatedManga(getMangaToMangaRelations(result.getId(), Tables.RELATION_TYPE_RELATED));
            result.setAnimeAdaptations(getMangaToAnimeRelations(result.getId(), Tables.RELATION_TYPE_ADAPTATION));
            HashMap<String, ArrayList<String>> otherTitles = new HashMap<>();
            otherTitles.put("english", getMangaOtherTitles(result.getId(), Tables.TITLE_TYPE_ENGLISH));
            otherTitles.put("japanese", getMangaOtherTitles(result.getId(), Tables.TITLE_TYPE_JAPANESE));
            otherTitles.put("synonyms", getMangaOtherTitles(result.getId(), Tables.TITLE_TYPE_SYNONYM));
            result.setOtherTitles(otherTitles);
        }
        cursor.close();
        return result;
    }

    private boolean deleteManga(Integer id) {
        return getDBWrite().delete(Tables.Mangas.TABLE_NAME, Tables.COLUMN_ID + " = ?", new String[]{id.toString()}) == 1;
    }

    public boolean deleteMangaFromMangalist(Integer id, String username) {
        boolean result = false;
        Integer userId = getUserId(username);
        if (userId != 0) {
            result = getDBWrite().delete(Tables.MangaLists.TABLE_NAME,
                    Tables.MangaLists.PROFILE_ID + " = ? AND " + Tables.MangaLists.MANGA_ID + " = ?",
                    new String[]{userId.toString(), id.toString()}) == 1;
            if (result) {
                boolean isUsed;
                /* check if this record is used for other relations and delete if it's not to keep the database
                 * still used relations can be:
                 * - mangalist of other user
                 * - record is related to other anime or manga (e.g. as sequel or adaptation)
                 */
                // used in other mangalist?
                isUsed = recordExists(Tables.MangaLists.TABLE_NAME, Tables.MangaLists.MANGA_ID, id.toString());
                if (!isUsed) { // no need to check more if its already used
                    // used as related record of other manga?
                    isUsed = recordExists(Tables.MangaMangas.TABLE_NAME, Tables.MangaMangas.RELATED_ID, id.toString());
                }
                if (!isUsed) { // no need to check more if its already used
                    // used as related record of an anime?
                    isUsed = recordExists(Tables.AnimeMangas.TABLE_NAME, Tables.AnimeMangas.RELATED_ID, id.toString());
                }

                if (!isUsed) {// its not used anymore, delete it
                    deleteManga(id);
                }
            }
        }
        return result;
    }

    // delete all manga records without relations, because they're "dead" records
    public void cleanUpMangaTable() {
        getDBWrite().rawQuery("DELETE FROM " + Tables.Mangas.TABLE_NAME + " WHERE " +
                Tables.COLUMN_ID + " NOT IN (SELECT DISTINCT " + Tables.MangaLists.MANGA_ID + " FROM " + Tables.MangaLists.TABLE_NAME + ") AND " +
                Tables.COLUMN_ID + " NOT IN (SELECT DISTINCT " + Tables.MangaMangas.RELATED_ID + " FROM " + Tables.MangaMangas.TABLE_NAME + ") AND " +
                Tables.COLUMN_ID + " NOT IN (SELECT DISTINCT " + Tables.AnimeMangas.RELATED_ID + " FROM " + Tables.AnimeMangas.TABLE_NAME + ")", null);
    }

    public ArrayList<Manga> getMangaList(String listStatus, String username) {
        if (listStatus == "")
            return getMangaList(getUserId(username), "", false);
        else
            return getMangaList(getUserId(username), listStatus, false);
    }

    public ArrayList<Manga> getDirtyMangaList(String username) {
        return getMangaList(getUserId(username), "", true);
    }

    private ArrayList<Manga> getMangaList(int userId, String listStatus, boolean dirtyOnly) {
        ArrayList<Manga> result = null;
        Cursor cursor;
        try {
            ArrayList<String> selArgs = new ArrayList<>();
            selArgs.add(String.valueOf(userId));
            if (listStatus != "") {
                selArgs.add(listStatus);
            }

            cursor = getDBRead().rawQuery("SELECT m.*, " + Tables.MangaLists.getFields("ml.") +
                    " FROM " + Tables.MangaLists.TABLE_NAME + " ml " +
                    " INNER JOIN " + Tables.Mangas.TABLE_NAME + " m ON ml." + Tables.MangaLists.MANGA_ID + " = m." + Tables.COLUMN_ID +
                    " WHERE ml." + Tables.MangaLists.PROFILE_ID + " = ? " + (listStatus != "" ? " AND ml." + Tables.MangaLists.STATUS + " = ? " : "") + (dirtyOnly ? " AND ml." + Tables.MangaLists.DIRTY + " <> \"\" " : "") +
                    " ORDER BY m." + Tables.Mangas.TITLE + " COLLATE NOCASE", selArgs.toArray(new String[selArgs.size()]));
            if (cursor.moveToFirst()) {
                result = new ArrayList<>();
                do {
                    result.add(Manga.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public ArrayList<Manga> getSimilarMangaList() {
        ArrayList<Manga> result = null;
        int userId = getUserId(AccountService.getUsername());
        Cursor cursor;
        try {
            ArrayList<String> selArgs = new ArrayList<>();
            selArgs.add(String.valueOf(userId));

            cursor = getDBRead().rawQuery("SELECT m.*, " + Tables.MangaLists.getFields("ml.") +
                    " FROM " + Tables.MangaLists.TABLE_NAME + " ml " +
                    " INNER JOIN " + Tables.Mangas.TABLE_NAME + " m ON ml." + Tables.MangaLists.MANGA_ID + " = m." + Tables.COLUMN_ID +
                    " WHERE ml." + Tables.MangaLists.PROFILE_ID + " = ? " +
                    " AND ml." + Tables.MangaLists.STATUS + " IN (1,2,3,4) " +
                    " ORDER BY m." + Tables.Mangas.TITLE + " COLLATE NOCASE", selArgs.toArray(new String[selArgs.size()]));
            if (cursor.moveToFirst()) {
                result = new ArrayList<>();
                do {
                    result.add(Manga.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public void saveUser(User profile) {
        ContentValues cv = new ContentValues();

        cv.put(Tables.Profiles.USERNAME, profile.getUsername());
        cv.put(Tables.Profiles.AVATAR_URL, profile.getProfile().getAvatarUrl());

        // don't use replace it alters the autoincrement _id field!
        int updateResult = getDBWrite().update(Tables.Profiles.TABLE_NAME, cv,
                Tables.Profiles.USERNAME + " = ?",
                new String[]{profile.getUsername()});
        if (updateResult > 0) {// updated row
            profile.setId(getUserId(profile.getUsername()));
        } else {
            Long insertResult = getDBWrite().insert(Tables.Profiles.TABLE_NAME, null, cv);
            profile.setId(insertResult.intValue());
        }
    }

    public void saveUser(User user, Boolean profile) {
        ContentValues cv = new ContentValues();

        cv.put(Tables.Profiles.USERNAME, user.getUsername());
        if (user.getProfile().getAvatarUrl().equals("http://cdn.myanimelist.net/images/questionmark_50.gif"))
            cv.put(Tables.Profiles.AVATAR_URL, "http://cdn.myanimelist.net/images/na.gif");
        else
            cv.put(Tables.Profiles.AVATAR_URL, user.getProfile().getAvatarUrl());
        if (user.getProfile().getDetails().getLastOnline() != null) {
            cv.put(Tables.Profiles.LAST_ONLINE, user.getProfile().getDetails().getLastOnline());
        } else
            cv.putNull(Tables.Profiles.LAST_ONLINE);

        if (profile) {
            if (user.getProfile().getDetails().getBirthday() != null) {
                // TODO implementar MALDateTools em utils
                String birthday = ""; //MALDateTools.parseMALDateToISO8601String(user.getProfile().getDetails().getBirthday());
                cv.put(Tables.Profiles.BIRTHDAY, birthday.equals("") ? user.getProfile().getDetails().getBirthday() : birthday);
            } else
                cv.putNull(Tables.Profiles.BIRTHDAY);
            cv.put(Tables.Profiles.LOCATION, user.getProfile().getDetails().getLocation());
            cv.put(Tables.Profiles.WEBSITE, user.getProfile().getDetails().getWebsite());
            cv.put(Tables.Profiles.COMMENTS, user.getProfile().getDetails().getComments());
            cv.put(Tables.Profiles.FORUM_POSTS, user.getProfile().getDetails().getForumPosts());
            cv.put(Tables.Profiles.GENDER, user.getProfile().getDetails().getGender());
            if (user.getProfile().getDetails().getJoinDate() != null) {
                String joindate = ""; //MALDateTools.parseMALDateToISO8601String(user.getProfile().getDetails().getJoinDate());
                cv.put(Tables.Profiles.JOIN_DATE, joindate.equals("") ? user.getProfile().getDetails().getJoinDate() : joindate);
            } else
                cv.putNull(Tables.Profiles.JOIN_DATE);
            cv.put(Tables.Profiles.ACCESS_RANK, user.getProfile().getDetails().getAccessRank());
            cv.put(Tables.Profiles.ANIME_LIST_VIEW, user.getProfile().getDetails().getAnimeListView());
            cv.put(Tables.Profiles.MANGA_LIST_VIEW, user.getProfile().getDetails().getMangaListView());

            cv.put(Tables.Profiles.ANIME_TIME_DAYS, user.getProfile().getAnimeStats().getTimeDays());
            cv.put(Tables.Profiles.ANIME_WATCHING, user.getProfile().getAnimeStats().getWatching());
            cv.put(Tables.Profiles.ANIME_COMPLETED, user.getProfile().getAnimeStats().getCompleted());
            cv.put(Tables.Profiles.ANIME_HOLD, user.getProfile().getAnimeStats().getOnHold());
            cv.put(Tables.Profiles.ANIME_DROPPED, user.getProfile().getAnimeStats().getDropped());
            cv.put(Tables.Profiles.ANIME_PLANNED, user.getProfile().getAnimeStats().getPlanToWatch());
            cv.put(Tables.Profiles.ANIME_TOTAL_ENTRIES, user.getProfile().getAnimeStats().getTotalEntries());

            cv.put(Tables.Profiles.MANGA_TIME_DAYS, user.getProfile().getMangaStats().getTimeDays());
            cv.put(Tables.Profiles.MANGA_READING, user.getProfile().getMangaStats().getReading());
            cv.put(Tables.Profiles.MANGA_COMPLETED, user.getProfile().getMangaStats().getCompleted());
            cv.put(Tables.Profiles.MANGA_HOLD, user.getProfile().getMangaStats().getOnHold());
            cv.put(Tables.Profiles.MANGA_DROPPED, user.getProfile().getMangaStats().getDropped());
            cv.put(Tables.Profiles.MANGA_PLANNED, user.getProfile().getMangaStats().getPlanToRead());
            cv.put(Tables.Profiles.MANGA_TOTAL_ENTRIES, user.getProfile().getMangaStats().getTotalEntries());

            cv.put(Tables.Profiles.ANIME_COMPATIBILITY, user.getProfile().getDetails().getAnimeCompatibility());
            cv.put(Tables.Profiles.MANGA_COMPATIBILITY, user.getProfile().getDetails().getMangaCompatibility());
            cv.put(Tables.Profiles.ANIME_COMPATIBILITY_VALUE, user.getProfile().getDetails().getAnimeCompatibilityValue());
            cv.put(Tables.Profiles.MANGA_COMPATIBILITY_VALUE, user.getProfile().getDetails().getMangaCompatibilityValue());
        }

        // don't use replace it alters the autoincrement _id field!
        int updateResult = getDBWrite().update(Tables.Profiles.TABLE_NAME, cv,
                Tables.Profiles.USERNAME + " = ?",
                new String[]{user.getUsername()});
        if (updateResult > 0) {// updated row
            user.setId(getUserId(user.getUsername()));
        } else {
            Long insertResult = getDBWrite().insert(Tables.Profiles.TABLE_NAME, null, cv);
            user.setId(insertResult.intValue());
        }
    }

    public void saveUserFriends(Integer userId, ArrayList<User> friends) {
        if (userId == null || friends == null) {
            return;
        }
        SQLiteDatabase db = getDBWrite();
        db.beginTransaction();
        try {
            db.delete(Tables.Friends.TABLE_NAME, Tables.Friends.PROFILE_ID + " = ?", new String[]{userId.toString()});
            for (User friend : friends) {
                ContentValues cv = new ContentValues();
                cv.put(Tables.Friends.PROFILE_ID, userId);
                cv.put(Tables.Friends.FRIEND_ID, friend.getId());
                db.insert(Tables.Friends.TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void saveProfile(User profile) {
        saveUser(profile, true);
    }

    public User getProfile(String name) {
        User result = null;
        Cursor cursor;
        try {
            cursor = getDBRead().query(Tables.Profiles.TABLE_NAME, null, Tables.Profiles.USERNAME + " = ?", new String[]{name}, null, null, null);
            if (cursor.moveToFirst())
                result = User.fromCursor(cursor);
            cursor.close();
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public ArrayList<User> getFriendList(String username) {
        ArrayList<User> friendlist = new ArrayList<>();
        Cursor cursor = getDBRead().rawQuery("SELECT p1.* FROM " + Tables.Profiles.TABLE_NAME + " AS p1" +                  // for result rows
                " INNER JOIN " + Tables.Profiles.TABLE_NAME + " AS p2" +                                                    // for getting user id to given name
                " INNER JOIN " + Tables.Friends.TABLE_NAME + " AS fl ON fl.profile_id = p2." + Tables.COLUMN_ID + // for user<>friend relation
                " WHERE p2." + Tables.Profiles.USERNAME + " = ? AND p1." + Tables.COLUMN_ID + " = fl." + Tables.Friends.FRIEND_ID +
                " ORDER BY p1." + Tables.Profiles.USERNAME + " COLLATE NOCASE", new String[]{username});
        if (cursor.moveToFirst()) {
            do {
                friendlist.add(User.fromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friendlist;
    }

    public void saveFriendList(ArrayList<User> friendlist, String username) {
        for (User friend : friendlist) {
            saveUser(friend, false);
        }

        Integer userId = getUserId(username);
        saveUserFriends(userId, friendlist);
    }

    private Integer getGenreId(String genre) {
        return getRecordId(Tables.Genres.TABLE_NAME, Tables.COLUMN_ID, Tables.Genres.NAME, genre);
    }

    private Integer getProducerId(String producer) {
        return getRecordId(Tables.Procuders.TABLE_NAME, Tables.COLUMN_ID, Tables.Procuders.NAME, producer);
    }

    private Integer getUserId(String username) {
        if (username == null || username.equals(""))
            return 0;
        Integer id = getRecordId(Tables.Profiles.TABLE_NAME, Tables.COLUMN_ID, Tables.Profiles.USERNAME, username);
        if (id == null) {
            id = 0;
        }
        return id;
    }

    private Integer getRecordId(String table, String idField, String searchField, String value) {
        Integer result = null;
        Cursor cursor = getDBRead().query(table, new String[]{idField}, searchField + " = ?", new String[]{value}, null, null, null);
        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }
        cursor.close();

        if (result == null) {
            ContentValues cv = new ContentValues();
            cv.put(searchField, value);
            Long addResult = getDBWrite().insert(table, null, cv);
            if (addResult > -1) {
                result = addResult.intValue();
            }
        }
        return result;
    }

    public ArrayList<RelatedRecord> getAnimeGenres(Integer animeId) {
        ArrayList<RelatedRecord> result = null;
        Cursor cursor = getDBRead().rawQuery("SELECT g.* FROM " + Tables.Genres.TABLE_NAME + " g " +
                        " INNER JOIN " + Tables.AnimeGenres.TABLE_NAME + " ag ON ag." + Tables.AnimeGenres.GENRE_ID + " = g." + Tables.COLUMN_ID +
                        " WHERE ag." + Tables.AnimeGenres.ANIME_ID + " = ? ORDER BY g." + Tables.Genres.NAME + " COLLATE NOCASE",
                new String[]{animeId.toString()});
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                RelatedRecord animeStub = new RelatedRecord();
                animeStub.setId(cursor.getInt(0), ListType.ANIME);
                animeStub.setTitle(cursor.getString(1));
                animeStub.setUrl(cursor.getString(2));
                result.add(animeStub);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<String> getAnimeProducers(Integer animeId) {
        ArrayList<String> result = null;
        Cursor cursor = getDBRead().rawQuery("SELECT p." + Tables.Procuders.NAME + " FROM " + Tables.Procuders.TABLE_NAME + " p " +
                        " INNER JOIN " + Tables.AnimeProducers.TABLE_NAME + " ap ON ap." + Tables.AnimeProducers.PRODUCER_ID + " = p." + Tables.COLUMN_ID +
                        " WHERE ap." + Tables.AnimeProducers.ANIME_ID + " = ? ORDER BY p." + Tables.Procuders.NAME + " COLLATE NOCASE",
                new String[]{animeId.toString()});

        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                result.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    public ArrayList<RelatedRecord> getMangaGenres(Integer mangaId) {
        ArrayList<RelatedRecord> result = null;
        Cursor cursor = getDBRead().rawQuery("SELECT g.* FROM " + Tables.Genres.TABLE_NAME + " g " +
                        " INNER JOIN " + Tables.MangaGenres.TABLE_NAME + " mg ON mg." + Tables.MangaGenres.GENRE_ID + " = g." + Tables.COLUMN_ID +
                        " WHERE mg." + Tables.MangaGenres.MANGA_ID + " = ? ORDER BY g." + Tables.Genres.NAME + " COLLATE NOCASE",
                new String[]{mangaId.toString()});
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                RelatedRecord mangaStub = new RelatedRecord();
                mangaStub.setId(cursor.getInt(0), ListType.MANGA);
                mangaStub.setTitle(cursor.getString(1));
                mangaStub.setUrl(cursor.getString(2));
                result.add(mangaStub);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    private boolean recordExists(String table, String searchField, String searchValue) {
        boolean result = false;
        Cursor cursor = getDBRead().query(table, null, searchField + " = ?", new String[]{searchValue}, null, null, null);
        if (cursor.moveToFirst()) {
            result = true;
        }
        cursor.close();
        return result;
    }

    private void saveAnimeToAnimeRelation(int animeId, RelatedRecord relatedAnime, String relationType) {
        if (relatedAnime.getId() == 0) {
            return;
        }
        boolean relatedRecordExists;
        if (!recordExists(Tables.Animes.TABLE_NAME, Tables.COLUMN_ID, String.valueOf(relatedAnime.getId()))) {
            ContentValues cv = new ContentValues();
            cv.put(Tables.COLUMN_ID, relatedAnime.getId());
            cv.put(Tables.Animes.TITLE, relatedAnime.getTitle());
            relatedRecordExists = getDBWrite().insert(Tables.Animes.TABLE_NAME, null, cv) > 0;
        } else {
            relatedRecordExists = true;
        }

        if (relatedRecordExists) {
            ContentValues cv = new ContentValues();
            cv.put(Tables.AnimeAnimes.ANIME_ID, animeId);
            cv.put(Tables.AnimeAnimes.RELATED_ID, relatedAnime.getId());
            cv.put(Tables.AnimeAnimes.TYPE, relationType);
            getDBWrite().replace(Tables.AnimeAnimes.TABLE_NAME, null, cv);
        }
    }

    private void saveAnimeToMangaRelation(int animeId, RelatedRecord relatedManga, String relationType) {
        if (relatedManga.getId() == 0) {
            return;
        }
        boolean relatedRecordExists;
        if (!recordExists(Tables.Mangas.TABLE_NAME, Tables.COLUMN_ID, String.valueOf(relatedManga.getId()))) {
            ContentValues cv = new ContentValues();
            cv.put(Tables.COLUMN_ID, relatedManga.getId());
            cv.put(Tables.Mangas.TITLE, relatedManga.getTitle());
            relatedRecordExists = getDBWrite().insert(Tables.Mangas.TABLE_NAME, null, cv) > 0;
        } else {
            relatedRecordExists = true;
        }

        if (relatedRecordExists) {
            ContentValues cv = new ContentValues();
            cv.put(Tables.AnimeMangas.ANIME_ID, animeId);
            cv.put(Tables.AnimeMangas.RELATED_ID, relatedManga.getId());
            cv.put(Tables.AnimeMangas.TYPE, relationType);
            getDBWrite().replace(Tables.AnimeMangas.TABLE_NAME, null, cv);
        }
    }

    private void saveMangaToMangaRelation(int mangaId, RelatedRecord relatedManga, String relationType) {
        if (relatedManga.getId() == 0) {
            return;
        }
        boolean relatedRecordExists;
        if (!recordExists(Tables.Mangas.TABLE_NAME, Tables.COLUMN_ID, String.valueOf(relatedManga.getId()))) {
            ContentValues cv = new ContentValues();
            cv.put(Tables.COLUMN_ID, relatedManga.getId());
            cv.put(Tables.Mangas.TITLE, relatedManga.getTitle());
            relatedRecordExists = getDBWrite().insert(Tables.Mangas.TABLE_NAME, null, cv) > 0;
        } else {
            relatedRecordExists = true;
        }

        if (relatedRecordExists) {
            ContentValues cv = new ContentValues();
            cv.put(Tables.MangaMangas.MANGA_ID, mangaId);
            cv.put(Tables.MangaMangas.RELATED_ID, relatedManga.getId());
            cv.put(Tables.MangaMangas.TYPE, relationType);
            getDBWrite().replace(Tables.MangaMangas.TABLE_NAME, null, cv);
        }
    }

    private void saveMangaToAnimeRelation(int mangaId, RelatedRecord relatedAnime, String relationType) {
        if (relatedAnime.getId() == 0) {
            return;
        }
        boolean relatedRecordExists;
        if (!recordExists(Tables.Animes.TABLE_NAME, Tables.COLUMN_ID, String.valueOf(relatedAnime.getId()))) {
            ContentValues cv = new ContentValues();
            cv.put(Tables.COLUMN_ID, relatedAnime.getId());
            cv.put(Tables.Animes.TITLE, relatedAnime.getTitle());
            relatedRecordExists = getDBWrite().insert(Tables.Animes.TABLE_NAME, null, cv) > 0;
        } else {
            relatedRecordExists = true;
        }

        if (relatedRecordExists) {
            ContentValues cv = new ContentValues();
            cv.put(Tables.MangaAnimes.MANGA_ID, mangaId);
            cv.put(Tables.MangaAnimes.RELATED_ID, relatedAnime.getId());
            cv.put(Tables.MangaAnimes.TYPE, relationType);
            getDBWrite().replace(Tables.MangaAnimes.TABLE_NAME, null, cv);
        }
    }

    private ArrayList<RelatedRecord> getAnimeToAnimeRelations(Integer animeId, String relationType) {
        ArrayList<RelatedRecord> result = null;
        Cursor cursor = getDBRead().rawQuery("SELECT a." + Tables.COLUMN_ID + ", a." + Tables.Animes.TITLE +
                        " FROM " + Tables.Animes.TABLE_NAME + " a " +
                        " INNER JOIN " + Tables.AnimeAnimes.TABLE_NAME + " ar ON a." + Tables.COLUMN_ID + " = ar." + Tables.AnimeAnimes.RELATED_ID +
                        " WHERE ar." + Tables.AnimeAnimes.ANIME_ID + " = ? AND ar." + Tables.AnimeAnimes.TYPE + " = ? ORDER BY a." + Tables.Animes.TITLE + " COLLATE NOCASE",
                new String[]{animeId.toString(), relationType});
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                RelatedRecord animeRelated = new RelatedRecord();
                animeRelated.setId(cursor.getInt(0), ListType.ANIME);
                animeRelated.setTitle(cursor.getString(1));
                result.add(animeRelated);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    private ArrayList<RelatedRecord> getAnimeToMangaRelations(Integer animeId, String relationType) {
        ArrayList<RelatedRecord> result = null;
        Cursor cursor = getDBRead().rawQuery("SELECT m." + Tables.COLUMN_ID + ", m." + Tables.Mangas.TITLE +
                        " FROM " + Tables.Mangas.TABLE_NAME + " m " +
                        " INNER JOIN " + Tables.AnimeMangas.TABLE_NAME + " ar ON m." + Tables.COLUMN_ID + " = ar." + Tables.AnimeMangas.RELATED_ID +
                        " WHERE ar." + Tables.AnimeMangas.ANIME_ID + " = ? AND ar." + Tables.AnimeMangas.TYPE + " = ? ORDER BY m." + Tables.Mangas.TITLE + " COLLATE NOCASE",
                new String[]{animeId.toString(), relationType});
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                RelatedRecord mangaRelated = new RelatedRecord();
                mangaRelated.setId(cursor.getInt(0), ListType.MANGA);
                mangaRelated.setTitle(cursor.getString(1));
                result.add(mangaRelated);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    private ArrayList<RelatedRecord> getMangaToMangaRelations(Integer mangaId, String relationType) {
        ArrayList<RelatedRecord> result = null;
        Cursor cursor = getDBRead().rawQuery("SELECT m." + Tables.COLUMN_ID + ", m." + Tables.Mangas.TITLE +
                        " FROM " + Tables.Mangas.TABLE_NAME + " m " +
                        " INNER JOIN " + Tables.MangaMangas.TABLE_NAME + " mr ON m." + Tables.COLUMN_ID + " = mr." + Tables.MangaMangas.RELATED_ID +
                        " WHERE mr." + Tables.MangaMangas.MANGA_ID + " = ? AND mr." + Tables.MangaMangas.TYPE + " = ? ORDER BY m." + Tables.Mangas.TITLE + " COLLATE NOCASE",
                new String[]{mangaId.toString(), relationType});
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                RelatedRecord mangaRelated = new RelatedRecord();
                mangaRelated.setId(cursor.getInt(0), ListType.MANGA);
                mangaRelated.setTitle(cursor.getString(1));
                result.add(mangaRelated);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    private ArrayList<RelatedRecord> getMangaToAnimeRelations(Integer mangaId, String relationType) {
        ArrayList<RelatedRecord> result = null;
        Cursor cursor = getDBRead().rawQuery("SELECT a." + Tables.COLUMN_ID + ", a." + Tables.Animes.TITLE +
                        " FROM " + Tables.Animes.TABLE_NAME + " a " +
                        " INNER JOIN " + Tables.MangaAnimes.TABLE_NAME + " mr ON a." + Tables.COLUMN_ID + " = mr." + Tables.MangaAnimes.RELATED_ID +
                        " WHERE mr." + Tables.MangaAnimes.MANGA_ID + " = ? AND mr." + Tables.MangaAnimes.TYPE + " = ? ORDER BY a." + Tables.Animes.TITLE + " COLLATE NOCASE",
                new String[]{mangaId.toString(), relationType});
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                RelatedRecord animeRelated = new RelatedRecord();
                animeRelated.setId(cursor.getInt(0), ListType.ANIME);
                animeRelated.setTitle(cursor.getString(1));
                result.add(animeRelated);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    private void saveAnimeOtherTitle(int animeId, String title, String titleType) {
        ContentValues cv = new ContentValues();
        cv.put("anime_id", animeId);
        cv.put("title_type", titleType);
        cv.put("title", title);
        getDBWrite().replace(Tables.AnimeOtherTitles.TABLE_NAME, null, cv);
    }

    private void saveMangaOtherTitle(int mangaId, String title, String titleType) {
        ContentValues cv = new ContentValues();
        cv.put("manga_id", mangaId);
        cv.put("title_type", titleType);
        cv.put("title", title);
        getDBWrite().replace(Tables.MangaOtherTitles.TABLE_NAME, null, cv);
    }

    private ArrayList<String> getAnimeOtherTitles(Integer animeId, String titleType) {
        ArrayList<String> result = null;
        Cursor cursor = getDBRead().query(Tables.AnimeOtherTitles.TABLE_NAME,
                new String[]{Tables.AnimeOtherTitles.TITLE},
                Tables.AnimeOtherTitles.ANIME_ID + " = ? AND " + Tables.AnimeOtherTitles.TYPE + " = ?",
                new String[]{animeId.toString(), titleType}, null, null,
                Tables.AnimeOtherTitles.TITLE + " COLLATE NOCASE");
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                result.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    private ArrayList<String> getMangaOtherTitles(Integer mangaId, String titleType) {
        ArrayList<String> result = null;
        Cursor cursor = getDBRead().query(Tables.MangaOtherTitles.TABLE_NAME,
                new String[]{Tables.MangaOtherTitles.TITLE},
                Tables.MangaOtherTitles.MANGA_ID + " = ? AND " + Tables.MangaOtherTitles.TYPE + " = ?",
                new String[]{mangaId.toString(), titleType}, null, null,
                Tables.MangaOtherTitles.TITLE + " COLLATE NOCASE");
        if (cursor.moveToFirst()) {
            result = new ArrayList<>();
            do {
                result.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }


    public void saveMangReaderList(List<MangaEden> mangaList) {
        try {
            getDBWrite().beginTransaction();

            for (MangaEden currentManga : mangaList) {
                StringBuilder selection = new StringBuilder();
                List<String> selectionArgs = new ArrayList<>();

                selection.append(Tables.MangasReader.URL).append(" = ?");
                selectionArgs.add(currentManga.getUrl());

                MangaEden existingManga = MangaEden.fromCursor(getDBRead().query(Tables.MangasReader.TABLE_NAME, null,
                        selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]),
                        null, null, null, "1"));

                if (existingManga != null) {
                    ContentValues cv = new ContentValues();

                    cv.put(Tables.MangasReader.UPDATED, currentManga.getUpdated());
                    cv.put(Tables.MangasReader.UPDATE_COUNT, currentManga.getUpdateCount());

                    existingManga.setUpdated(currentManga.getUpdated());
                    existingManga.setUpdateCount(currentManga.getUpdateCount());

                    getDBWrite().replace(Tables.MangasReader.TABLE_NAME, null, cv);
                }
            }

            getDBWrite().setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getDBWrite().endTransaction();
        }
    }

    /*
            public void saveMangaEdenList(MangaEden manga) {
                ContentValues cv = new ContentValues();

                cv.put("title", manga.getTitle());
                cv.put("image_url", manga.getImage());
                cv.put("last_chapter_date", manga.getLastChapterDate());
                cv.put("eden_id", manga.getId());

                getDBWrite().replace(dbHelper.TABLE_EDEN_MANGALIST, null, cv);
            }
    */
    public ArrayList<Message> getAllMessages() {
        ArrayList<Message> result = null;
        Cursor cursor;
        try {

            cursor = getDBRead().query(Tables.Messages.TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                result = new ArrayList<>();
                do {
                    result.add(Message.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public void saveMessageList(ArrayList<Message> messages) {
        try {
            getDBWrite().beginTransaction();
            for (Message msg : messages)
                saveMessage(msg);
            getDBWrite().setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        } finally {
            getDBWrite().endTransaction();
        }
    }

    public void saveMessage(Message msg) {
        ContentValues cv = new ContentValues();

        cv.put(Tables.COLUMN_ID, msg.getId());
        cv.put(Tables.Messages.TITLE, msg.getTitle());
        cv.put(Tables.Messages.USERNAME, msg.getUsername());
        cv.put(Tables.Messages.DATE_MSG, msg.getDateMsg());
        cv.put(Tables.Messages.SHORT_MESSAGE, msg.getShortMessage());
        cv.put(Tables.Messages.FULL_MESSAGE, msg.getFullMessage());
        cv.put(Tables.Messages.READ, msg.isRead() ? 1 : 0);
        cv.put(Tables.Messages.READ_ID, msg.getReadId());
        cv.put(Tables.Messages.REPLY_ID, msg.getReplyId());

        getDBWrite().replace(Tables.Messages.TABLE_NAME, null, cv);
    }

    public void saveRecentChapter(RecentChapter recentChapter) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(Tables.COLUMN_ID, recentChapter.getId());
            cv.put(Tables.MangaRecentChapters.URL, recentChapter.getUrl());
            cv.put(Tables.MangaRecentChapters.PARENT_URL, recentChapter.getParentUrl());
            cv.put(Tables.MangaRecentChapters.TITLE, recentChapter.getTitle());
            cv.put(Tables.MangaRecentChapters.THUMBNAIL_URL, recentChapter.getThumbnailUrl());
            cv.put(Tables.MangaRecentChapters.PAGE_NUMBER, recentChapter.getPageNumber());
            cv.put(Tables.MangaRecentChapters.OFFLINE, recentChapter.isOffline() ? 1 : 0);
            cv.put(Tables.MangaRecentChapters.CHAPTER_DATE, recentChapter.getDate());

            getDBWrite().replace(Tables.MangaRecentChapters.TABLE_NAME, null, cv);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }
}
