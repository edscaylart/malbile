package br.scaylart.malbile.controllers.databases;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.scaylart.malbile.MalbileApplication;

public class ApplicationSQLiteOpenHelper extends SQLiteOpenHelper {
    private static ApplicationSQLiteOpenHelper sInstance;

    protected static final String DATABASE_NAME = "MALbile.db";
    private static final int DATABASE_VERSION = 2;

    public ApplicationSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized ApplicationSQLiteOpenHelper getInstance() {
        if (sInstance == null) {
            sInstance = new ApplicationSQLiteOpenHelper(MalbileApplication.getInstance());
        }

        return sInstance;
    }

    private static final String CREATE_TABLE_PROFILE = "CREATE TABLE "
            + Tables.Profiles.TABLE_NAME + " ( "
            + Tables.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Tables.Profiles.USERNAME + " VARCHAR UNIQUE, "
            + Tables.Profiles.AVATAR_URL + " VARCHAR, "
            + Tables.Profiles.ABOUT + " VARCHAR, "
            + Tables.Profiles.BIRTHDAY + " VARCHAR, "
            + Tables.Profiles.LOCATION + " VARCHAR, "
            + Tables.Profiles.WEBSITE + " VARCHAR, "
            + Tables.Profiles.COMMENTS + " VARCHAR, "
            + Tables.Profiles.FORUM_POSTS + " VARCHAR, "
            + Tables.Profiles.LAST_ONLINE + " VARCHAR, "
            + Tables.Profiles.JOIN_DATE + " VARCHAR, "
            + Tables.Profiles.ACCESS_RANK + " VARCHAR, "
            + Tables.Profiles.GENDER + " VARCHAR, "
            + Tables.Profiles.ANIME_LIST_VIEW + " VARCHAR, "
            + Tables.Profiles.MANGA_LIST_VIEW + " VARCHAR, "
            + Tables.Profiles.ANIME_TIME_DAYS + " VARCHAR, "
            + Tables.Profiles.ANIME_WATCHING + " VARCHAR, "
            + Tables.Profiles.ANIME_COMPLETED + " VARCHAR, "
            + Tables.Profiles.ANIME_HOLD + " VARCHAR, "
            + Tables.Profiles.ANIME_DROPPED + " VARCHAR, "
            + Tables.Profiles.ANIME_PLANNED + " VARCHAR, "
            + Tables.Profiles.ANIME_TOTAL_ENTRIES + " VARCHAR, "
            + Tables.Profiles.MANGA_TIME_DAYS + " VARCHAR, "
            + Tables.Profiles.MANGA_READING + " VARCHAR, "
            + Tables.Profiles.MANGA_COMPLETED + " VARCHAR, "
            + Tables.Profiles.MANGA_HOLD + " VARCHAR, "
            + Tables.Profiles.MANGA_DROPPED + " VARCHAR, "
            + Tables.Profiles.MANGA_PLANNED + " VARCHAR, "
            + Tables.Profiles.MANGA_TOTAL_ENTRIES + " VARCHAR, "
            + Tables.Profiles.ANIME_COMPATIBILITY + " VARCHAR, "
            + Tables.Profiles.MANGA_COMPATIBILITY + " VARCHAR, "
            + Tables.Profiles.ANIME_COMPATIBILITY_VALUE + " VARCHAR, "
            + Tables.Profiles.MANGA_COMPATIBILITY_VALUE + " VARCHAR "
            + " );";

    private static final String CREATE_TABLE_FRIENDLIST = "CREATE TABLE "
            + Tables.Friends.TABLE_NAME + " ( "
            + Tables.Friends.PROFILE_ID + " INTEGER NOT NULL REFERENCES " + Tables.Profiles.TABLE_NAME + " (" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.Friends.FRIEND_ID + " INTEGER NOT NULL, "
            + "PRIMARY KEY(" + Tables.Friends.PROFILE_ID + ", " + Tables.Friends.FRIEND_ID + ") "
            + ");";

    private static final String CREATE_TABLE_ANIME = "CREATE TABLE "
            + Tables.Animes.TABLE_NAME + " ( "
            + Tables.COLUMN_ID + " INTEGER PRIMARY KEY, "
            + Tables.Animes.TITLE + " VARCHAR, "
            + Tables.Animes.IMAGE_URL + " VARCHAR, "
            + Tables.Animes.TYPE + " INTEGER, "
            + Tables.Animes.EPISODES + " INTEGER, "
            + Tables.Animes.STATUS + " INTEGER, "
            + Tables.Animes.START_DATE + " VARCHAR, "
            + Tables.Animes.END_DATE + " VARCHAR, "
            + Tables.Animes.SYNOPSIS + " VARCHAR, "
            + Tables.Animes.CLASSIFICATION + " VARCHAR, "
            + Tables.Animes.RANK + " INTEGER, "
            + Tables.Animes.MEMBERS_SCORE + " INTEGER, "
            + Tables.Animes.MEMBERS_COUNT + " INTEGER, "
            + Tables.Animes.FAVORITED_COUNT + " INTEGER, "
            + Tables.Animes.LAS_UPDATE + " INTEGER NOT NULL DEFAULT 407570400 "
            + " );";

    private static final String CREATE_TABLE_ANIMELIST = "CREATE TABLE "
            + Tables.AnimeLists.TABLE_NAME + " ( "
            + Tables.AnimeLists.PROFILE_ID + " INTEGER NOT NULL REFERENCES " + Tables.Profiles.TABLE_NAME + " (" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.AnimeLists.ANIME_ID + " INTEGER NOT NULL REFERENCES " + Tables.Animes.TABLE_NAME + " (" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.AnimeLists.STATUS + " INTEGER, "
            + Tables.AnimeLists.EPISODES + " INTEGER, "
            + Tables.AnimeLists.SCORE + " INTEGER, "
            + Tables.AnimeLists.START_DATE + " VARCHAR, "
            + Tables.AnimeLists.END_DATE + " VARCHAR, "
            + Tables.AnimeLists.REWATCHING + " INTEGER, "
            + Tables.AnimeLists.REWATCHING_VALUE + " INTEGER, "
            + Tables.AnimeLists.REWATCHING_COUNT + " INTEGER, "
            + Tables.AnimeLists.FAVORITE + " INTEGER NOT NULL DEFAULT 0, "
            + Tables.AnimeLists.DIRTY + " varchar DEFAULT NULL, "
            + Tables.AnimeLists.LAST_UPDATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now')), "
            + "PRIMARY KEY(" + Tables.Friends.PROFILE_ID + ", " + Tables.AnimeLists.ANIME_ID + ") "
            + " );";

    private static final String CREATE_TABLE_MANGA = "CREATE TABLE "
            + Tables.Mangas.TABLE_NAME + " ( "
            + Tables.COLUMN_ID + " INTEGER PRIMARY KEY, "
            + Tables.Mangas.TITLE + " VARCHAR, "
            + Tables.Mangas.IMAGE_URL + " VARCHAR, "
            + Tables.Mangas.TYPE + " INTEGER, "
            + Tables.Mangas.CHAPTERS + " INTEGER, "
            + Tables.Mangas.VOLUMES + " INTEGER, "
            + Tables.Mangas.STATUS + " INTEGER, "
            + Tables.Mangas.START_DATE + " VARCHAR, "
            + Tables.Mangas.END_DATE + " VARCHAR, "
            + Tables.Mangas.SYNOPSIS + " VARCHAR, "
            + Tables.Mangas.CLASSIFICATION + " VARCHAR, "
            + Tables.Mangas.RANK + " INTEGER, "
            + Tables.Mangas.MEMBERS_SCORE + " INTEGER, "
            + Tables.Mangas.MEMBERS_COUNT + " INTEGER, "
            + Tables.Mangas.FAVORITED_COUNT + " INTEGER, "
            + Tables.Mangas.LAST_UPDATE + " INTEGER NOT NULL DEFAULT 407570400 "
            + " );";

    private static final String CREATE_TABLE_MANGALIST = "CREATE TABLE "
            + Tables.MangaLists.TABLE_NAME + " ( "
            + Tables.MangaLists.PROFILE_ID + " INTEGER NOT NULL REFERENCES " + Tables.Profiles.TABLE_NAME + " (" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.MangaLists.MANGA_ID + " INTEGER NOT NULL REFERENCES " + Tables.Mangas.TABLE_NAME + " (" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.MangaLists.STATUS + " INTEGER, "
            + Tables.MangaLists.CHAPTERS + " INTEGER, "
            + Tables.MangaLists.VOLUMES + " INTEGER, "
            + Tables.MangaLists.SCORE + " INTEGER, "
            + Tables.MangaLists.START_DATE + " VARCHAR, "
            + Tables.MangaLists.END_DATE + " VARCHAR, "
            + Tables.MangaLists.REREADING + " INTEGER, "
            + Tables.MangaLists.REREADING_VALUE + " INTEGER, "
            + Tables.MangaLists.REREADING_COUNT + " INTEGER, "
            + Tables.MangaLists.FAVORITE + " INTEGER NOT NULL DEFAULT 0, "
            + Tables.MangaLists.DIRTY + " varchar DEFAULT NULL, "
            + Tables.MangaLists.LAST_UPDATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now')), "
            + "PRIMARY KEY(profile_id, manga_id) "
            + " );";

    private static final String CREATE_TABLE_PRODUCER = "CREATE TABLE "
            + Tables.Procuders.TABLE_NAME + " ( "
            + Tables.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Tables.Procuders.NAME + " VARCHAR UNIQUE, "
            + Tables.Procuders.URL + " VARCHAR "
            + ");";

    private static final String CREATE_TABLE_ANIME_PRODUCER = "CREATE TABLE "
            + Tables.AnimeProducers.TABLE_NAME + " ( "
            + Tables.AnimeProducers.ANIME_ID + " INTEGER NOT NULL REFERENCES " + Tables.Animes.TABLE_NAME + " (" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.AnimeProducers.PRODUCER_ID + " INTEGER NOT NULL REFERENCES " + Tables.Procuders.TABLE_NAME + " (" + Tables.COLUMN_ID + ") ON DELETE CASCADE "
            + ");";

    private static final String CREATE_TABLE_GENRES = "CREATE TABLE "
            + Tables.Genres.TABLE_NAME + "("
            + Tables.COLUMN_ID + " integer primary key autoincrement, "
            + Tables.Genres.NAME + " VARCHAR UNIQUE, "
            + Tables.Genres.URL + " VARCHAR "
            + ")";

    private static final String CREATE_TABLE_ANIME_GENRES = "CREATE TABLE "
            + Tables.AnimeGenres.TABLE_NAME + "("
            + Tables.AnimeGenres.ANIME_ID + " integer NOT NULL REFERENCES " + Tables.Animes.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.AnimeGenres.GENRE_ID + " integer NOT NULL REFERENCES " + Tables.Genres.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + "PRIMARY KEY(" + Tables.AnimeGenres.ANIME_ID + ", " + Tables.AnimeGenres.GENRE_ID + ")"
            + ")";

    private static final String CREATE_TABLE_MANGA_GENRES = "CREATE TABLE "
            + Tables.MangaGenres.TABLE_NAME + "("
            + Tables.MangaGenres.MANGA_ID + " integer NOT NULL REFERENCES " + Tables.Mangas.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.MangaGenres.GENRE_ID + " integer NOT NULL REFERENCES " + Tables.Genres.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + "PRIMARY KEY(" + Tables.MangaGenres.MANGA_ID + ", " + Tables.MangaGenres.GENRE_ID + ")"
            + ")";

    /*
     * Anime-/Manga-relation tables
     *
     * Structure for these tables is
     * - anime id
     * - related id
     * - relation type (side story, summary, alternative version etc.), see RELATION_TYPE-constants
     *   below
     */

    private static final String CREATE_TABLE_ANIME_ANIME_RELATIONS = "CREATE TABLE "
            + Tables.AnimeAnimes.TABLE_NAME + "("
            + Tables.AnimeAnimes.ANIME_ID + " integer NOT NULL REFERENCES " + Tables.Animes.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.AnimeAnimes.RELATED_ID + " integer NOT NULL REFERENCES " + Tables.Animes.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.AnimeAnimes.TYPE + " integer NOT NULL, "
            + "PRIMARY KEY(" + Tables.AnimeAnimes.ANIME_ID + ", " + Tables.AnimeAnimes.RELATED_ID + ")"
            + ")";

    private static final String CREATE_TABLE_ANIME_MANGA_RELATIONS = "CREATE TABLE "
            + Tables.AnimeMangas.TABLE_NAME + "("
            + Tables.AnimeMangas.ANIME_ID + " integer NOT NULL REFERENCES " + Tables.Animes.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.AnimeMangas.RELATED_ID + " integer NOT NULL REFERENCES " + Tables.Mangas.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.AnimeMangas.TYPE + " integer NOT NULL, " // can currently only be RELATION_TYPE_ADAPTATION
            + "PRIMARY KEY(" + Tables.AnimeMangas.ANIME_ID + ", " + Tables.AnimeMangas.RELATED_ID + ")"
            + ")";

    private static final String CREATE_TABLE_MANGA_MANGA_RELATIONS = "CREATE TABLE "
            + Tables.MangaMangas.TABLE_NAME + "("
            + Tables.MangaMangas.MANGA_ID + " integer NOT NULL REFERENCES " + Tables.Mangas.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.MangaMangas.RELATED_ID + " integer NOT NULL REFERENCES " + Tables.Mangas.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.MangaMangas.TYPE + " integer NOT NULL, "
            + "PRIMARY KEY(" + Tables.MangaMangas.MANGA_ID + ", " + Tables.MangaMangas.RELATED_ID + ")"
            + ")";

    private static final String CREATE_TABLE_MANGA_ANIME_RELATIONS = "CREATE TABLE "
            + Tables.MangaAnimes.TABLE_NAME + "("
            + Tables.MangaAnimes.MANGA_ID + " integer NOT NULL REFERENCES " + Tables.Mangas.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.MangaAnimes.RELATED_ID + " integer NOT NULL REFERENCES " + Tables.Animes.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.MangaAnimes.TYPE + " integer NOT NULL, " // can currently only be RELATION_TYPE_ADAPTATION
            + "PRIMARY KEY(" + Tables.MangaAnimes.MANGA_ID + ", " + Tables.MangaAnimes.RELATED_ID + ")"
            + ")";

    private static final String CREATE_TABLE_ANIME_OTHER_TITLES = "CREATE TABLE "
            + Tables.AnimeOtherTitles.TABLE_NAME + "("
            + Tables.AnimeOtherTitles.ANIME_ID + " integer NOT NULL REFERENCES " + Tables.Animes.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.AnimeOtherTitles.TYPE + " integer NOT NULL, "
            + Tables.AnimeOtherTitles.TITLE + " varchar NOT NULL, "
            + "PRIMARY KEY(" + Tables.AnimeOtherTitles.ANIME_ID + ", " + Tables.AnimeOtherTitles.TYPE + ", " + Tables.AnimeOtherTitles.TITLE + ")"
            + ")";

    private static final String CREATE_TABLE_MANGA_OTHER_TITLES = "CREATE TABLE "
            + Tables.MangaOtherTitles.TABLE_NAME + "("
            + Tables.MangaOtherTitles.MANGA_ID + " integer NOT NULL REFERENCES " + Tables.Mangas.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.MangaOtherTitles.TYPE + " integer NOT NULL, "
            + Tables.MangaOtherTitles.TITLE + " varchar NOT NULL, "
            + "PRIMARY KEY(" + Tables.MangaOtherTitles.MANGA_ID + ", " + Tables.MangaOtherTitles.TYPE + ", " + Tables.MangaOtherTitles.TITLE + ")"
            + ")";

    /**
     * MESSAGES
     */
    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE "
            + Tables.Messages.TABLE_NAME + " ( "
            + Tables.COLUMN_ID + " INTEGER PRIMARY KEY, "
            + Tables.Messages.USERNAME + " VARCHAR, "
            + Tables.Messages.DATE_MSG + " VARCHAR, "
            + Tables.Messages.TITLE + " VARCHAR, "
            + Tables.Messages.SHORT_MESSAGE + " VARCHAR, "
            + Tables.Messages.FULL_MESSAGE + " VARCHAR, "
            + Tables.Messages.READ + " INTEGER, "
            + Tables.Messages.READ_ID + " INTEGER, "
            + Tables.Messages.REPLY_ID + " INTEGER "
            + " );";

    /**
     * MangaEden manga List / Chapters / Pages
     */
    private static final String CREATE_TABLE_EDEN_MANGALIST = "CREATE TABLE "
            + Tables.MangasReader.TABLE_NAME + "("
            + Tables.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + Tables.MangasReader.TITLE + " VARCHAR NOT NULL, "
            + Tables.MangasReader.IMAGE_URL + " VARCHAR, "
            + Tables.MangasReader.DESCRIPTION + " VARCHAR, "
            + Tables.MangasReader.LAST_CHAPTER_DATE + " VARCHAR, "
            + Tables.MangasReader.GENRE + " VARCHAR, "
            + Tables.MangasReader.AUTHOR + " VARCHAR, "
            + Tables.MangasReader.ARTIST + " VARCHAR, "
            + Tables.MangasReader.URL + " VARCHAR, "
            + Tables.MangasReader.COMPLETED + " INTEGER, "
            + Tables.MangasReader.RANK + " INTEGER, "
            + Tables.MangasReader.UPDATED + " INTEGER, "
            + Tables.MangasReader.UPDATE_COUNT + " INTEGER, "
            + Tables.MangasReader.INITIALIZED + " INTEGER "
            + ")";

    private static final String CREATE_TABLE_EDEN_MANGALIST_MANGA = "CREATE TABLE "
            + Tables.MangasReaderMangas.TABLE_NAME + "("
            + Tables.MangasReaderMangas.READER_ID + " integer NOT NULL REFERENCES " + Tables.MangasReader.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE, "
            + Tables.MangasReaderMangas.MANGA_ID + " integer NOT NULL REFERENCES " + Tables.Mangas.TABLE_NAME + "(" + Tables.COLUMN_ID + ") ON DELETE CASCADE "
            + ")";

    private static final String CREATE_TABLE_EDEN_MANGACHAPTER = "CREATE TABLE "
            + Tables.MangaChapters.TABLE_NAME + "("
            + Tables.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + Tables.MangaChapters.URL + " VARCHAR, "
            + Tables.MangaChapters.PARENT_URL + " VARCHAR, "
            + Tables.MangaChapters.NUMBER + " INTEGER, "
            + Tables.MangaChapters.CHAPTER_DATE + " INTEGER, "
            + Tables.MangaChapters.NEW + " INTEGER, "
            + Tables.MangaChapters.TITLE + " VARCHAR "
            + ")";

    private static final String CREATE_TABLE_EDEN_MANGA_RECENT_CHAPTER = "CREATE TABLE "
            + Tables.MangaRecentChapters.TABLE_NAME + "("
            + Tables.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + Tables.MangaRecentChapters.URL + " VARCHAR, "
            + Tables.MangaRecentChapters.PARENT_URL + " VARCHAR, "
            + Tables.MangaRecentChapters.PAGE_NUMBER + " INTEGER, "
            + Tables.MangaRecentChapters.CHAPTER_DATE + " INTEGER, "
            + Tables.MangaRecentChapters.THUMBNAIL_URL + " VARCHAR, "
            + Tables.MangaRecentChapters.TITLE + " VARCHAR, "
            + Tables.MangaRecentChapters.OFFLINE + " VARCHAR "
            + ")";

    private static final String CREATE_TABLE_DOWNLOAD_MANGA = "CREATE TABLE "
            + Tables.DownloadManga.TABLE_NAME + "("
            + Tables.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + Tables.DownloadManga.TITLE + " VARCHAR NOT NULL, "
            + Tables.DownloadManga.IMAGE_URL + " VARCHAR, "
            + Tables.DownloadManga.DESCRIPTION + " VARCHAR, "
            + Tables.DownloadManga.LAST_CHAPTER_DATE + " VARCHAR, "
            + Tables.DownloadManga.GENRE + " VARCHAR, "
            + Tables.DownloadManga.AUTHOR + " VARCHAR, "
            + Tables.DownloadManga.ARTIST + " VARCHAR, "
            + Tables.DownloadManga.URL + " VARCHAR, "
            + Tables.DownloadManga.COMPLETED + " INTEGER "
            + ")";

    private static final String CREATE_TABLE_DOWNLOAD_CHAPTER = "CREATE TABLE "
            + Tables.DownloadChapters.TABLE_NAME + "("
            + Tables.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + Tables.DownloadChapters.TITLE + " VARCHAR NOT NULL, "
            + Tables.DownloadChapters.URL + " VARCHAR, "
            + Tables.DownloadChapters.PARENT_URL + " VARCHAR, "
            + Tables.DownloadChapters.DIRECTORY + " VARCHAR, "
            + Tables.DownloadChapters.CURRENT_PAGE + " INTEGER, "
            + Tables.DownloadChapters.TOTAL_PAGES + " INTEGER, "
            + Tables.DownloadChapters.FLAG + " INTEGER "
            + ")";

    private static final String CREATE_TABLE_DOWNLOAD_PAGE = "CREATE TABLE "
            + Tables.DownloadPages.TABLE_NAME + "("
            + Tables.COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + Tables.DownloadPages.URL + " VARCHAR, "
            + Tables.DownloadPages.PARENT_URL + " VARCHAR, "
            + Tables.DownloadPages.DIRECTORY + " VARCHAR, "
            + Tables.DownloadPages.FLAG + " INTEGER "
            + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ANIME);
        db.execSQL(CREATE_TABLE_MANGA);
        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_FRIENDLIST);
        db.execSQL(CREATE_TABLE_ANIMELIST);
        db.execSQL(CREATE_TABLE_MANGALIST);
        db.execSQL(CREATE_TABLE_ANIME_ANIME_RELATIONS);
        db.execSQL(CREATE_TABLE_ANIME_MANGA_RELATIONS);
        db.execSQL(CREATE_TABLE_MANGA_MANGA_RELATIONS);
        db.execSQL(CREATE_TABLE_MANGA_ANIME_RELATIONS);
        db.execSQL(CREATE_TABLE_GENRES);
        db.execSQL(CREATE_TABLE_ANIME_GENRES);
        db.execSQL(CREATE_TABLE_MANGA_GENRES);
        db.execSQL(CREATE_TABLE_ANIME_OTHER_TITLES);
        db.execSQL(CREATE_TABLE_MANGA_OTHER_TITLES);
        db.execSQL(CREATE_TABLE_PRODUCER);
        db.execSQL(CREATE_TABLE_ANIME_PRODUCER);
        db.execSQL(CREATE_TABLE_EDEN_MANGALIST);
        db.execSQL(CREATE_TABLE_EDEN_MANGALIST_MANGA);
        db.execSQL(CREATE_TABLE_EDEN_MANGACHAPTER);
        db.execSQL(CREATE_TABLE_EDEN_MANGA_RECENT_CHAPTER);
        db.execSQL(CREATE_TABLE_DOWNLOAD_MANGA);
        db.execSQL(CREATE_TABLE_DOWNLOAD_CHAPTER);
        db.execSQL(CREATE_TABLE_DOWNLOAD_PAGE);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    public void deleteDatabase(Context context) {
        sInstance = null;
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("drop table chapter_pages");
            db.execSQL("drop table " + Tables.MangaChapters.TABLE_NAME);
            db.execSQL("drop table " + Tables.MangasReaderMangas.TABLE_NAME);
            db.execSQL("drop table " + Tables.MangasReader.TABLE_NAME);

            db.execSQL(CREATE_TABLE_EDEN_MANGALIST);
            db.execSQL(CREATE_TABLE_EDEN_MANGALIST_MANGA);
            db.execSQL(CREATE_TABLE_EDEN_MANGACHAPTER);
            db.execSQL(CREATE_TABLE_EDEN_MANGA_RECENT_CHAPTER);

            db.execSQL(CREATE_TABLE_DOWNLOAD_MANGA);
            db.execSQL(CREATE_TABLE_DOWNLOAD_CHAPTER);
            db.execSQL(CREATE_TABLE_DOWNLOAD_PAGE);
        }
    }
}
