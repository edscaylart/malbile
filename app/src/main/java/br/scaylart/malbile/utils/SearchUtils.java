package br.scaylart.malbile.utils;

import br.scaylart.malbile.controllers.databases.Tables;

public class SearchUtils {
    public static final long TIMEOUT = 500;

    public static final String STATUS_ALL = "ALL";
    public static final String STATUS_COMPLETED = "1";
    public static final String STATUS_ONGOING = "0";

    public static final String ORDER_BY_NAME = Tables.MangasReader.TITLE;
    public static final String ORDER_BY_RANK = Tables.MangasReader.RANK;

    public static final int LIMIT_COUNT = 1000;

    private SearchUtils() {
        throw new AssertionError();
    }
}
