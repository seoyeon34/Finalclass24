package function;

public class Music {
    private String title;
    private String albumCoverPath;
    private String lyrics;

    public Music(String title, String albumCoverPath, String lyrics) {
        this.title = title;
        this.albumCoverPath = albumCoverPath;
        this.lyrics = lyrics;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbumCoverPath() {
        return albumCoverPath;
    }

    public String getLyrics() {
        return lyrics;
    }
}
