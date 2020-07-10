package com.example.movies_stage2.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favoriteMovie")
public class Movie {

    @PrimaryKey(autoGenerate = false)
    private int id;
    private String title;
    private String releaseDate;
    private String imagePath;
    private int voteAverage;
    private String overview;

    public Movie() {
    }

    public Movie(int id, String title, String releaseDate, String imagePath, int voteAverage, String overview) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.imagePath = imagePath;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
