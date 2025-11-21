package playlist;

import java.util.ArrayList;
import java.util.*;

public class Playlist {
	public static ArrayList<Music> AddMusicList = new ArrayList<Music>();
	
	// 재생목록 추가 기능
	public void Addplaymusic(int add, int m) {	// add 추가 승인, m 노래 번호 
		AllMusicList s = null;          
		ArrayList<Music> allmusic = s.allMusic;
		
		// 추가(yes or no)
		if(add == 1) {	// yes=1
			AddMusicList.add(allmusic.get(m));
		}
	}
	
	// 추가한 재생목록 출력 
	public static void showAddMusic() {
		System.out.println("\n 재생 목록 ");
		
		Iterator<Music> i = AddMusicList.iterator();
		while(i.hasNext()) {
			Music n = i.next();
			System.out.println(n.getTitle());
		}
		
	}
	
}
