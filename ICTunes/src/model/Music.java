package model;


public class Music {
    private String title;      // 음악 제목
    private String artist;     // 가수 이름
    private String genre;      // 장르 (카테고리)
    private int releaseYear;   // 발매년도
    private String album;      // 앨범 이름
    private String filePath;   // 음악 파일 경로 (실제 재생에 사용될 WAV 파일 경로)
    private String coverPath;  // 앨범 커버 이미지 파일 경로
    private boolean liked;     // 좋아요 상태 (기본값 false)

    /**
     * Music 객체를 생성하는 생성자
     *
     * @param title       음악 제목
     * @param artist      가수 이름
     * @param genre       장르
     * @param releaseYear 발매년도
     * @param album       앨범 이름
     * @param filePath    음악 파일 경로 (WAV)
     * @param coverPath   앨범 커버 이미지 파일 경로
     */
    public Music(String title, String artist, String genre, int releaseYear, String album, String filePath, String coverPath) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.album = album;
        this.filePath = filePath;
        this.coverPath = coverPath;
        this.liked = false; // 처음 생성 시 좋아요 상태는 false로 초기화
    }

    // --- Getter 메서드들 ---
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getAlbum() {
        return album;
    }
    
    public String getFilePath() {
        return filePath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public boolean isLiked() {
        return liked;
    }

    // --- Setter 메서드 ---
    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    /**
     * Music 객체의 문자열 표현을 반환.
     * 검색 결과 등 화면에 표시할 때 사용될 수 있으며, 좋아요 상태도 표시.
     * @return Music 객체의 정보가 담긴 문자열
     */
    
    @Override
    public String toString() {
        String likeStatus = this.liked ? " ♥" : "";
        return String.format("%s - %s (%d) %s%s", title, artist, releaseYear, album, likeStatus);
    }
}