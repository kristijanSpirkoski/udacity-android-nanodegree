package com.example.movies_stage1;

public class Movie {

    private String title;
    private String releaseDate;
    private String imagePath;
    private int voteAverage;
    private String overview;

    public Movie() {
    }

    public Movie(String title, String releaseDate, String imagePath, int voteAverage, String overview) {
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
}
