package com.example.movies_stage2.models;

public class Trailer {

    private String name;
    private String videoKey;

    public Trailer(String name, String videoKey) {
        this.name = name;
        this.videoKey = videoKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }
}
