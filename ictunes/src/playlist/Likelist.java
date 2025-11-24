package playlist;

import java.util.*;
import function.Like;
import playlist.*;

public class Likelist {
	public static ArrayList<String> Likemusic = new ArrayList<String>();
	ArrayList<Music> allMusic;
	ArrayList<Integer> Like;
	
	// title-index matching  
	public String indexTitle(int n) {
		String title;
		AllMusicList.showMusic();
		Music input = allMusic.get(4*n+n);	// AllMusicList 기준 title 배열 index = 4*n + n
		
		return title = input.getTitle();
	}
	
	public Likelist() {
		String liketitle;
		
		// like arraylist에서 l=1이면 Likemusic에 추가 
		for(int index = 0; index < Like.size(); index++) {
			if(Like.get(index) == 1) {
				liketitle = indexTitle(index);
				Likemusic.add(liketitle);
			}
		} 
		showLike();	// 좋아요 목록 출력 
	}
	
	public void showLike() {
		for(int i = 0; i < Likemusic.size(); i++) {
		System.out.println((i+1) + ". " + Likemusic.toString()); }
	}
}
