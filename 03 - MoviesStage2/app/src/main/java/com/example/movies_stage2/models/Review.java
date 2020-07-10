package com.example.movies_stage2.models;

public class Review {

    private String author;
    private String contents;

    public Review(String author, String contents) {
        this.author = author;
        this.contents = contents;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
