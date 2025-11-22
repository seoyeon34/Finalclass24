package function;

import java.util.ArrayList;
package function;

import java.util.ArrayList;

import playlist.*;

public class Like {
	// Like Array에 AllMusicList 순서대로 좋아요 목록 새로 만듦 
	// allMusic Array에 곡마다 index=4에 Like allMusic.add로 시도했는데 실패 
	public static ArrayList<Music> Like = new ArrayList<>();
	Music m;
	
	public Like(String title, int l) {
		m.title = title;
		showLike();
		
		if(l == 1) {
			if(m.title == "응급실") {
				Like.add(0, new Music(1));
			} else if(m.title == "한번 더 이별") {
				Like.add(1, new Music(1));
			} else if(m.title == "이별하러 가는 길") {
				Like.add(2, new Music(1));
			} 
			else if(m.title == "Yours") {
				Like.add(3, new Music(1));
			} else if(m.title == "나무") {
				Like.add(4, new Music(1));
			} else if(m.title == "좋은 밤 좋은 꿈") {
				Like.add(5, new Music(1));
			}
			else if(m.title == "죽일놈") {
				Like.add(6, new Music(1));
			} else if(m.title == "Aqua man") {
				Like.add(7, new Music(1));
			} else if(m.title == "D") {
				Like.add(8, new Music(1));
			}
			else if(m.title == "All I want for Christmas is you") {
				Like.add(9, new Music(1));
			} else if(m.title == "When I was your man") {
				Like.add(10, new Music(1));
			} else if(m.title == "Off my face") {
				Like.add(11, new Music(1));
			}
			else if(m.title == "Golden") {
				Like.add(12, new Music(1));
			} else if(m.title == "Blue valentine") {
				Like.add(13, new Music(1));
			} else if(m.title == "Drama") {
				Like.add(14, new Music(1));
			}
			
		} else return;
			
		}
	public void showLike() {
		System.out.println("(좋아요)한 노래입니다.");
	}
}

import playlist.AllMusicList;
import playlist.Music;

public class Like {
	AllMusicList s = null;
	ArrayList<Music> allmusic = s.allMusic;
	
	public void Like(int l) {
	
		if(l == 1) {
			
		} else {
			
		}	
		
	}
}
