package com.example.bakingtime.models;

import java.io.Serializable;

public class Step{

    private int id;
    private String description;
    private String shortDescription;
    private String videoURL;
    private String thumbnailURL;

    public Step(int id, String shortDescription, String videoURL, String thumbnailURL, String description) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
