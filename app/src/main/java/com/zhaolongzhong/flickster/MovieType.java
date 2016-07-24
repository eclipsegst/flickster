package com.zhaolongzhong.flickster;

public enum MovieType {

    POPULAR("POPULAR", "popular"),
    TOP_RATED("TOP RATED", "top_rated"),
    UPCOMING("UPCOMING", "upcoming"),
    NOW_PLAYING("NOW_PLAYING", "now_playing");

    private String name;
    private String value;

    MovieType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName(){
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public static MovieType instanceFromName(String name) {
        for (MovieType value : values()) {
            if (name.equals(value.name)) {
                return value;
            }
        }

        return POPULAR;
    }
}
