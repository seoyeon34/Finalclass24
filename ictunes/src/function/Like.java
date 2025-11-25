import playlist.*;

public class Like {
	// Like Array에 AllMusicList 순서대로 좋아요 목록 새로 만듦 
	// integer like arraylist 설정해서, allMusic 노래 index 순서대로 좋아요 저장
public static ArrayList<Integer> Like = new ArrayList<>();
Music m;	
int index;

	// Like array 처음 초기화 -> main에서 한번 실행 
	// Like array 처음 초기화 -> main에서 한번 실행하기
private void initialize() {
for(int index = 0; index < 15; index++) {
Like.add(index, 0); }
}

	// title-index matching  
	// title 입력받으면, 노래 index matching하기 
public int titleIndex(String title) {
m.title = title;

@@ -57,7 +57,13 @@ else if(m.title == "Golden") {
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

@@ -68,6 +74,7 @@ public Like(String title, int l) {
} else return;
}

	// 좋아요 저장 출력
public void showLike() {
System.out.println("(좋아요)한 노래입니다.");
}
