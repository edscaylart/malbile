package br.scaylart.malbile.controllers.factories;

import java.util.ArrayList;

import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.utils.DownloadUtils;
import br.scaylart.malbile.utils.SearchUtils;

public class DefaultFactory {
    private DefaultFactory() {
        throw new AssertionError();
    }

    public static final class Chapter {
        public static final String DEFAULT_URL = "No Url";
        public static final String DEFAULT_PARENT_URL = "No Parent Url";

        public static final String DEFAULT_NAME = "No Name";
        public static final boolean DEFAULT_NEW = false;
        public static final long DEFAULT_DATE = 0;

        public static final int DEFAULT_NUMBER = 0;

        private Chapter() {
            throw new AssertionError();
        }

        public static br.scaylart.malbile.reader.model.Chapter constructDefault() {
            br.scaylart.malbile.reader.model.Chapter newInstance = new br.scaylart.malbile.reader.model.Chapter();

            newInstance.setUrl(DEFAULT_URL);
            newInstance.setParentUrl(DEFAULT_PARENT_URL);

            newInstance.setTitle(DEFAULT_NAME);
            newInstance.setNewChapter(DEFAULT_NEW);
            newInstance.setDate(DEFAULT_DATE);

            newInstance.setNumber(DEFAULT_NUMBER);

            return newInstance;
        }
    }

    public static final class RecentChapter {
        public static final String DEFAULT_URL = "No Url";
        public static final String DEFAULT_PARENT_URL = "No Parent Url";

        public static final String DEFAULT_NAME = "No Name";
        public static final String DEFAULT_THUMBNAIL_URL = "No Thumbnail Url";

        public static final long DEFAULT_DATE = 0;
        public static final int DEFAULT_PAGE_NUMBER = 0;

        public static final boolean DEFAULT_OFFLINE = false;

        private RecentChapter() {
            throw new AssertionError();
        }

        public static br.scaylart.malbile.reader.model.RecentChapter constructDefault() {
            br.scaylart.malbile.reader.model.RecentChapter newInstance = new br.scaylart.malbile.reader.model.RecentChapter();

            newInstance.setUrl(DEFAULT_URL);
            newInstance.setParentUrl(DEFAULT_PARENT_URL);

            newInstance.setTitle(DEFAULT_NAME);
            newInstance.setThumbnailUrl(DEFAULT_THUMBNAIL_URL);

            newInstance.setDate(DEFAULT_DATE);
            newInstance.setPageNumber(DEFAULT_PAGE_NUMBER);

            newInstance.setOffline(DEFAULT_OFFLINE);

            return newInstance;
        }
    }

    public static final class DownloadChapter {
        public static final String DEFAULT_SOURCE = "No Source";
        public static final String DEFAULT_URL = "No Url";
        public static final String DEFAULT_PARENT_URL = "No Parent Url";

        public static final String DEFAULT_NAME = "No Name";

        public static final String DEFAULT_DIRECTORY = "No Directory";

        public static final int DEFAULT_CURRENT_PAGE = 0;
        public static final int DEFAULT_TOTAL_PAGES = 0;
        public static final int DEFAULT_FLAG = DownloadUtils.FLAG_FAILED;

        private DownloadChapter() {
            throw new AssertionError();
        }

        public static br.scaylart.malbile.reader.model.DownloadChapter constructDefault() {
            br.scaylart.malbile.reader.model.DownloadChapter newInstance = new br.scaylart.malbile.reader.model.DownloadChapter();

            newInstance.setUrl(DEFAULT_URL);
            newInstance.setParentUrl(DEFAULT_PARENT_URL);

            newInstance.setTitle(DEFAULT_NAME);

            newInstance.setDirectory(DEFAULT_DIRECTORY);

            newInstance.setCurrentPage(DEFAULT_CURRENT_PAGE);
            newInstance.setTotalPage(DEFAULT_TOTAL_PAGES);
            newInstance.setFlag(DEFAULT_FLAG);

            return newInstance;
        }
    }

    public static final class DownloadManga {
        public static final String DEFAULT_URL = "No Url";

        public static final String DEFAULT_ARTIST = "No Artist";
        public static final String DEFAULT_AUTHOR = "No Author";
        public static final String DEFAULT_DESCRIPTION = "No Description";
        public static final String DEFAULT_GENRE = "No Genre";
        public static final String DEFAULT_NAME = "No Name";
        public static final boolean DEFAULT_COMPLETED = false;
        public static final String DEFAULT_THUMBNAIL_URL = "No Thumbnail Url";

        private DownloadManga() {
            throw new AssertionError();
        }

        public static br.scaylart.malbile.reader.model.DownloadManga constructDefault() {
            br.scaylart.malbile.reader.model.DownloadManga newInstance = new br.scaylart.malbile.reader.model.DownloadManga();

            newInstance.setUrl(DEFAULT_URL);

            newInstance.setArtist(DEFAULT_ARTIST);
            newInstance.setAuthor(DEFAULT_AUTHOR);
            newInstance.setDescription(DEFAULT_DESCRIPTION);
            newInstance.setGenre(DEFAULT_GENRE);
            newInstance.setTitle(DEFAULT_NAME);
            newInstance.setCompleted(DEFAULT_COMPLETED);
            newInstance.setImageUrl(DEFAULT_THUMBNAIL_URL);

            return newInstance;
        }
    }

    public static final class DownloadPage {
        public static final String DEFAULT_URL = "No Url";
        public static final String DEFAULT_PARENT_URL = "No Parent Url";

        public static final String DEFAULT_DIRECTORY = "No Directory";

        public static final String DEFAULT_NAME = "No Name";

        public static final int DEFAULT_FLAG = DownloadUtils.FLAG_FAILED;

        private DownloadPage() {
            throw new AssertionError();
        }

        public static br.scaylart.malbile.reader.model.DownloadPage constructDefault() {
            br.scaylart.malbile.reader.model.DownloadPage newInstance = new br.scaylart.malbile.reader.model.DownloadPage();

            newInstance.setUrl(DEFAULT_URL);
            newInstance.setParentUrl(DEFAULT_PARENT_URL);

            newInstance.setDirectory(DEFAULT_DIRECTORY);

            newInstance.setTitle(DEFAULT_NAME);

            newInstance.setFlag(DEFAULT_FLAG);

            return newInstance;
        }
    }

    public static final class Manga {
        public static final String DEFAULT_URL = "No Url";

        public static final String DEFAULT_ARTIST = "No Artist";
        public static final String DEFAULT_AUTHOR = "No Author";
        public static final String DEFAULT_DESCRIPTION = "No Description";
        public static final String DEFAULT_GENRE = "No Genre";
        public static final String DEFAULT_NAME = "No Name";
        public static final boolean DEFAULT_COMPLETED = false;
        public static final String DEFAULT_THUMBNAIL_URL = "No Thumbnail Url";

        public static final int DEFAULT_RANK = 0;
        public static final long DEFAULT_UPDATED = 0;
        public static final int DEFAULT_UPDATE_COUNT = 0;

        public static final boolean DEFAULT_INITIALIZED = false;

        private Manga() {
            throw new AssertionError();
        }

        public static br.scaylart.malbile.reader.model.MangaEden constructDefault() {
            br.scaylart.malbile.reader.model.MangaEden newInstance = new br.scaylart.malbile.reader.model.MangaEden();

            newInstance.setUrl(DEFAULT_URL);

            newInstance.setArtist(DEFAULT_ARTIST);
            newInstance.setAuthor(DEFAULT_AUTHOR);
            newInstance.setDescription(DEFAULT_DESCRIPTION);
            newInstance.setGenre(DEFAULT_GENRE);
            newInstance.setTitle(DEFAULT_NAME);
            newInstance.setCompleted(DEFAULT_COMPLETED);
            newInstance.setImageUrl(DEFAULT_THUMBNAIL_URL);

            newInstance.setRank(DEFAULT_RANK);
            newInstance.setUpdated(DEFAULT_UPDATED);
            newInstance.setUpdateCount(DEFAULT_UPDATE_COUNT);

            newInstance.setInitialized(DEFAULT_INITIALIZED);

            return newInstance;
        }
    }

    public static final class SearchCatalogueWrapper {
        public static final String DEFAULT_NAME = null;
        public static final String DEFAULT_STATUS = SearchUtils.STATUS_ALL;
        public static final String DEFAULT_ORDER_BY = SearchUtils.ORDER_BY_RANK;
        public static final int DEFAULT_OFFSET = 0;

        private SearchCatalogueWrapper() {
            throw new AssertionError();
        }

        public static br.scaylart.malbile.utils.wrappers.SearchCatalogueWrapper constructDefault() {
            br.scaylart.malbile.utils.wrappers.SearchCatalogueWrapper newInstance = new br.scaylart.malbile.utils.wrappers.SearchCatalogueWrapper();

            newInstance.setNameArgs(DEFAULT_NAME);
            newInstance.setStatusArgs(DEFAULT_STATUS);
            newInstance.setOrderByArgs(DEFAULT_ORDER_BY);
            newInstance.setGenresArgs(new ArrayList<String>());
            newInstance.setOffsetArgs(DEFAULT_OFFSET);

            return newInstance;
        }
    }

    public static final class UpdatePageMarker {
        public static final String DEFAULT_NEXT_PAGE_URL = "No Next Page Url";
        public static final int DEFAULT_LAST_MANGA_POSITION = 0;

        private UpdatePageMarker() {
            throw new AssertionError();
        }

        public static br.scaylart.malbile.reader.UpdatePageMarker constructDefault() {
            return new br.scaylart.malbile.reader.UpdatePageMarker(MalbileManager.getInitialUpdateUrlFromPreferenceSource().toBlocking().single(), DEFAULT_LAST_MANGA_POSITION);
        }
    }
}
