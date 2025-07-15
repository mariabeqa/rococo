package org.rococo.data;

import org.apache.commons.lang3.StringUtils;
import org.rococo.config.Config;

public enum DataBase {

    MUSEUM("jdbc:mysql://%s/rococo-museum"),
    PAINTING("jdbc:mysql://%s/rococo-painting"),
    ARTIST("jdbc:mysql://%s/rococo-artist");
    private final String url;

    DataBase(String url) {
        this.url = url;
    }

    private static final Config CFG = Config.getInstance();

    public String getUrl() {
        return String.format(url, CFG.databaseAddress());
    }

    public String getUrlForP6Spy() {
        return "jdbc:p6spy:" + StringUtils.substringAfter(getUrl(), "jdbc:");
    }
}
