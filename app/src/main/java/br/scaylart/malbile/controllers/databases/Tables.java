package br.scaylart.malbile.controllers.databases;

public class Tables {
    public static final String COLUMN_ID = "_id";

    public static final String RELATION_TYPE_ALTERNATIVE = "0";
    public static final String RELATION_TYPE_CHARACTER = "1";
    public static final String RELATION_TYPE_SIDE_STORY = "2";
    public static final String RELATION_TYPE_SPIN_OFF = "3";
    public static final String RELATION_TYPE_SUMMARY = "4";
    public static final String RELATION_TYPE_ADAPTATION = "5";
    public static final String RELATION_TYPE_RELATED = "6";
    public static final String RELATION_TYPE_PREQUEL = "7";
    public static final String RELATION_TYPE_SEQUEL = "8";
    public static final String RELATION_TYPE_PARENT_STORY = "9";
    public static final String RELATION_TYPE_OTHER = "10";

    public static final String TITLE_TYPE_JAPANESE = "0";
    public static final String TITLE_TYPE_ENGLISH = "1";
    public static final String TITLE_TYPE_SYNONYM = "2";

    public static class Profiles {
        public static String TABLE_NAME = "profiles";
        public static String USERNAME = "username";
        public static String AVATAR_URL = "avatar_url";
        public static String ABOUT = "about";
        public static String BIRTHDAY = "bithrday";
        public static String LOCATION = "location";
        public static String WEBSITE = "website";
        public static String COMMENTS = "comments";
        public static String FORUM_POSTS = "forum_posts";
        public static String LAST_ONLINE = "last_online";
        public static String JOIN_DATE = "join_date";
        public static String ACCESS_RANK = "access_rank";
        public static String GENDER = "gender";
        public static String ANIME_LIST_VIEW = "anime_list_view";
        public static String ANIME_TIME_DAYS = "anime_time_days";
        public static String ANIME_WATCHING = "anime_watching";
        public static String ANIME_COMPLETED = "anime_completed";
        public static String ANIME_HOLD = "anime_hold";
        public static String ANIME_DROPPED = "anime_dropped";
        public static String ANIME_PLANNED = "anime_planned";
        public static String ANIME_TOTAL_ENTRIES = "anime_total_entries";
        public static String MANGA_LIST_VIEW = "manga_list_view";
        public static String MANGA_TIME_DAYS = "manga_time_days";
        public static String MANGA_READING = "manga_reading";
        public static String MANGA_COMPLETED = "manga_completed";
        public static String MANGA_HOLD = "manga_hold";
        public static String MANGA_DROPPED = "manga_dropped";
        public static String MANGA_PLANNED = "manga_planned";
        public static String MANGA_TOTAL_ENTRIES = "manga_total_entries";
        public static String ANIME_COMPATIBILITY = "anime_compatibility";
        public static String MANGA_COMPATIBILITY = "manga_compatibility";
        public static String ANIME_COMPATIBILITY_VALUE = "anime_compatibility_value";
        public static String MANGA_COMPATIBILITY_VALUE = "manga_compatibility_value";
    }

    public static class Friends {
        public static String TABLE_NAME = "friends";
        public static String PROFILE_ID = "profile_id";
        public static String FRIEND_ID = "friend_id";
    }

    public static class Animes {
        public static String TABLE_NAME = "animes";
        public static String TITLE = "title";
        public static String IMAGE_URL = "image_url";
        public static String TYPE = "record_type";
        public static String EPISODES = "episodes";
        public static String STATUS = "record_status";
        public static String START_DATE = "start_date";
        public static String END_DATE = "end_date";
        public static String SYNOPSIS = "synopsis";
        public static String CLASSIFICATION = "classification";
        public static String RANK = "rank";
        public static String MEMBERS_SCORE = "members_score";
        public static String MEMBERS_COUNT = "members_count";
        public static String FAVORITED_COUNT = "favorited_count";
        public static String LAS_UPDATE = "last_update";
    }

    public static class AnimeLists {
        public static String TABLE_NAME = "animelists";
        public static String PROFILE_ID = "profile_id";
        public static String ANIME_ID = "anime_id";
        public static String STATUS = "watch_status";
        public static String EPISODES = "watch_episodes";
        public static String SCORE = "watch_score";
        public static String START_DATE = "watch_start";
        public static String END_DATE = "watch_finish";
        public static String REWATCHING = "rewatching";
        public static String REWATCHING_VALUE = "rewatching_value";
        public static String REWATCHING_COUNT = "rewatching_count";
        public static String FAVORITE = "favorite";
        public static String DIRTY = "dirty";
        public static String LAST_UPDATE = "my_last_update";

        public static String getFields(String pfx) {
            if (pfx == null)
                pfx = "";
            String fields = pfx + STATUS + "," + pfx + EPISODES + "," + pfx + SCORE + "," + pfx + START_DATE + ","
                    + pfx + END_DATE + "," + pfx + REWATCHING + "," + pfx + REWATCHING_VALUE + "," + pfx + REWATCHING_COUNT + ","
                    + pfx + FAVORITE + "," + pfx + DIRTY + "," + pfx + LAST_UPDATE;
            return fields;
        }
    }

    public static class Mangas {
        public static String TABLE_NAME = "mangas";
        public static String TITLE = "title";
        public static String IMAGE_URL = "image_url";
        public static String TYPE = "record_type";
        public static String CHAPTERS = "chapters";
        public static String VOLUMES = "volumes";
        public static String STATUS = "record_status";
        public static String START_DATE = "start_date";
        public static String END_DATE = "end_date";
        public static String SYNOPSIS = "synopsis";
        public static String CLASSIFICATION = "classification";
        public static String RANK = "rank";
        public static String MEMBERS_SCORE = "members_score";
        public static String MEMBERS_COUNT = "members_count";
        public static String FAVORITED_COUNT = "favorited_count";
        public static String LAST_UPDATE = "last_update";
    }

    public static class MangaLists {
        public static String TABLE_NAME = "mangalists";
        public static String PROFILE_ID = "profile_id";
        public static String MANGA_ID = "manga_id";
        public static String STATUS = "read_status";
        public static String CHAPTERS = "read_chapters";
        public static String VOLUMES = "read_volumes";
        public static String SCORE = "read_score";
        public static String START_DATE = "read_start";
        public static String END_DATE = "read_finish";
        public static String REREADING = "rereading";
        public static String REREADING_VALUE = "rereading_value";
        public static String REREADING_COUNT = "rereading_count";
        public static String FAVORITE = "favorite";
        public static String DIRTY = "dirty";
        public static String LAST_UPDATE = "last_update";

        public static String getFields(String pfx) {
            if (pfx == null)
                pfx = "";
            String fields = pfx + STATUS + "," + pfx + CHAPTERS + "," + pfx + VOLUMES + "," + pfx + SCORE + ","
                    + pfx + START_DATE + "," + pfx + END_DATE + "," + pfx + REREADING + "," + pfx + REREADING_VALUE
                    + "," + pfx + REREADING_COUNT + "," + pfx + FAVORITE + "," + pfx + DIRTY + "," + pfx + LAST_UPDATE;
            return fields;
        }
    }

    public static class Procuders {
        public static String TABLE_NAME = "producers";
        public static String NAME = "record_name";
        public static String URL = "url";
    }

    public static class AnimeProducers {
        public static String TABLE_NAME = "anime_producers";
        public static String ANIME_ID = "anime_id";
        public static String PRODUCER_ID = "producer_id";
    }

    public static class Genres {
        public static String TABLE_NAME = "genres";
        public static String NAME = "record_name";
        public static String URL = "url";
    }

    public static class AnimeGenres {
        public static String TABLE_NAME = "anime_geners";
        public static String ANIME_ID = "anime_id";
        public static String GENRE_ID = "genre_id";
    }

    public static class MangaGenres {
        public static String TABLE_NAME = "manga_geners";
        public static String MANGA_ID = "manga_id";
        public static String GENRE_ID = "genre_id";
    }

    public static class AnimeAnimes {
        public static String TABLE_NAME = "rel_anime_animes";
        public static String ANIME_ID = "anime_id";
        public static String RELATED_ID = "related_id";
        public static String TYPE = "relation_type";
    }

    public static class AnimeMangas {
        public static String TABLE_NAME = "rel_anime_mangas";
        public static String ANIME_ID = "anime_id";
        public static String RELATED_ID = "related_id";
        public static String TYPE = "relation_type";
    }

    public static class MangaMangas {
        public static String TABLE_NAME = "rel_manga_mangas";
        public static String MANGA_ID = "manga_id";
        public static String RELATED_ID = "related_id";
        public static String TYPE = "relation_type";
    }

    public static class MangaAnimes {
        public static String TABLE_NAME = "rel_manga_animeS";
        public static String MANGA_ID = "manga_id";
        public static String RELATED_ID = "related_id";
        public static String TYPE = "relation_type";
    }

    public static class AnimeOtherTitles {
        public static String TABLE_NAME = "anime_othertitles";
        public static String ANIME_ID = "anime_id";
        public static String TYPE = "title_type";
        public static String TITLE = "title";
    }

    public static class MangaOtherTitles {
        public static String TABLE_NAME = "manga_othertitles";
        public static String MANGA_ID = "manga_id";
        public static String TYPE = "title_type";
        public static String TITLE = "title";
    }

    public static class Messages {
        public static String TABLE_NAME = "user_messages";
        public static String USERNAME = "username";
        public static String DATE_MSG = "date_msg";
        public static String TITLE = "title";
        public static String SHORT_MESSAGE = "short_message";
        public static String FULL_MESSAGE = "full_message";
        public static String READ = "read";
        public static String READ_ID = "read_id";
        public static String REPLY_ID = "reply_id";
    }

    public static class MangasReader {
        public static String TABLE_NAME = "mangas_reader";
        public static String TITLE = "title";
        public static String IMAGE_URL = "image_url";
        public static String DESCRIPTION = "description";
        public static String LAST_CHAPTER_DATE = "last_chapter_date";
        public static String GENRE = "genre";
        public static String AUTHOR = "author";
        public static String ARTIST = "artist";
        public static String URL = "url";
        public static String COMPLETED = "completed";
        public static String RANK = "rank";
        public static String UPDATED = "updated";
        public static String UPDATE_COUNT = "update_count";
        public static String INITIALIZED = "initialized";
    }

    public static class MangasReaderMangas {
        public static String TABLE_NAME = "mangas_reader_mangas";
        public static String READER_ID = "reader_id";
        public static String MANGA_ID = "manga_id";
    }

    public static class MangaChapters {
        public static String TABLE_NAME = "manga_chapters";
        public static String URL = "url";
        public static String PARENT_URL = "parent_url";
        public static String NUMBER = "chapter_number";
        public static String CHAPTER_DATE = "chapter_date";
        public static String NEW = "new_chapter";
        public static String TITLE = "title";
    }

    public static class MangaRecentChapters {
        public static String TABLE_NAME = "manga_recent_chapters";
        public static String URL = "url";
        public static String PARENT_URL = "parent_url";
        public static String PAGE_NUMBER = "page_number";
        public static String CHAPTER_DATE = "chapter_date";
        public static String THUMBNAIL_URL = "thumbnail_url";
        public static String TITLE = "title";
        public static String OFFLINE = "offline";
    }

    public static class DownloadManga {
        public static String TABLE_NAME = "download_manga";
        public static String TITLE = "title";
        public static String IMAGE_URL = "image_url";
        public static String DESCRIPTION = "description";
        public static String LAST_CHAPTER_DATE = "last_chapter_date";
        public static String GENRE = "genre";
        public static String AUTHOR = "author";
        public static String ARTIST = "artist";
        public static String URL = "url";
        public static String COMPLETED = "completed";
    }

    public static class DownloadChapters {
        public static String TABLE_NAME = "download_chapters";
        public static String URL = "url";
        public static String PARENT_URL = "parent_url";
        public static String TITLE = "title";
        public static String DIRECTORY = "directory";
        public static String CURRENT_PAGE = "current_page";
        public static String TOTAL_PAGES = "total_pages";
        public static String FLAG = "flag";
    }

    public static class DownloadPages {
        public static String TABLE_NAME = "download_pages";
        public static String URL = "url";
        public static String PARENT_URL = "parent_url";
        public static String DIRECTORY = "directory";
        public static String FLAG = "flag";
    }
}
