package playlist;

public class Music {

	private String title;
	private String artist;
	private String genre;
	private int year;
	public int like;
	
	public Music(String title, String artist, String genre, int year) {
		this.title = title;
		this.artist = artist;
		this.genre = genre;
		this.year = year;	
	}
	
	public String getTitle() {
		return title;
	}
	public String getArtist() {
		return artist;
	
	}
	public String getGenre() {
		return genre;
	}
	public int getYear() {
		return year;
	}
	
	public String toSting() {
		return "[" + genre + "]" + title + "-" + artist + "(" + year + ")"; 
	}
	
}
