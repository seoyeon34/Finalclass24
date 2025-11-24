package function;

import java.util.ArrayList;

import playlist.*;

public class Like {
	// integer like arraylist 설정해서, allMusic 노래 index 순서대로 좋아요 저장
	public static ArrayList<Integer> Like = new ArrayList<>();
	Music m;	
	int index;
	
	// Like array 처음 초기화 -> main에서 한번 실행하기
	private void initialize() {
		for(int index = 0; index < 15; index++) {
			Like.add(index, 0); }
	}
	
	// title 입력받으면, 노래 index matching하기 
	public int titleIndex(String title) {
		m.title = title;
		
		if(m.title == "응급실") {
			return index = 0;
		} else if(m.title == "한번 더 이별") {
			return index = 1;
		} else if(m.title == "이별하러 가는 길") {
			return index = 2;
		} 
		else if(m.title == "Yours") {
			return index = 3;
		} else if(m.title == "나무") {
			return index = 4;
		} else if(m.title == "좋은 밤 좋은 꿈") {
			return index = 5;
		}
		else if(m.title == "죽일놈") {
			return index = 6;
		} else if(m.title == "Aqua man") {
			return index = 7;
		} else if(m.title == "D") {
			return index = 8;
		}
		else if(m.title == "All I want for Christmas is you") {
			return index = 9;
		} else if(m.title == "When I was your man") {
			return index = 10;
		} else if(m.title == "Off my face") {
			return index = 11;
		}
		else if(m.title == "Golden") {
			return index = 12;
		} else if(m.title == "Blue valentine") {
			return index = 13;
		} else if(m.title == "Drama") {
			return index = 14;
		}
		return index;
	}

	// Like arraylist에 좋아요된 노래 index대로 저장 
	/* ArrayList<Integer> Like
	1 0 0 0 0 0 1 0 0 0 0 1 0 0 0
	응급실, 죽일놈, Off my face
	좋아요 노래로 저장
	*/
	public Like(String title, int l) {
		int index = titleIndex(title);
		
		showLike();
		
		if(l == 1) {
			Like.add(index, 1); 	// Like array가 l=1이면 좋아요 저장 
		} else return;
		}
	
	// 좋아요 저장 출력
	public void showLike() {
		System.out.println("(좋아요)한 노래입니다.");
	}
}
