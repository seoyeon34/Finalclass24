package playlist;

import java.util.ArrayList;

public class AllMusicList {

    public static ArrayList<Music> allMusic = new ArrayList<>();

    public static void showMusic() {
    	
        // 코드 여러번 호출했을 때 중복되는 거 막을려고 초기화 기능
        allMusic.clear();

        // 발라드
        allMusic.add(new Music("응급실", "izi", "발라드", 2005));
        allMusic.add(new Music("한번 더 이별", "이창섭", "발라드", 2024));
        allMusic.add(new Music("이별하러 가는 길", "임한별", "발라드", 2018));

        // 인디
        allMusic.add(new Music("Yours", "데이먼스 이어", "인디", 2019));
        allMusic.add(new Music("나무", "카더가든", "인디", 2019));
        allMusic.add(new Music("좋은 밤 좋은 꿈", "너드커넥션", "인디", 2020));

        // 힙합
        allMusic.add(new Music("죽일놈", "다이나믹 듀오", "힙합", 2009));
        allMusic.add(new Music("Aqua man", "빈지노", "힙합", 2012));
        allMusic.add(new Music("D", "딘", "힙합", 2016));

        // pop
        allMusic.add(new Music("All I want for Christmas is you", "Mariah Carey", "pop", 1994));
        allMusic.add(new Music("When I was your man", "Bruno Mars", "pop", 2012));
        allMusic.add(new Music("Off my face", "Justin Bieber", "pop", 2021));

        // kpop
        allMusic.add(new Music("Golden", "K demon hunters", "kpop", 2025));
        allMusic.add(new Music("Blue valentine", "Nmixx", "kpop", 2025));
        allMusic.add(new Music("Drama", "aespa", "kpop", 2023));
    }

    public static void showAllMusic() {
        System.out.println("\n 전체 음악 목록 ");
        int number = 1;

        for (Music m : allMusic) {
            System.out.println(number + ". " + m.toString());
            number++;
        }
    }
}