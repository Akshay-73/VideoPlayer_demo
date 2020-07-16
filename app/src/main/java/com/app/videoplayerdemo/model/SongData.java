package com.app.videoplayerdemo.model;

public class SongData {

    private String songName;
    private String songLink;

    public SongData(String songLink) {
        this.songLink = songLink;
    }

    public SongData(String songName, String songLink) {
        this.songName = songName;
        this.songLink = songLink;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }
}
