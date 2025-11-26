package playlist;

import model.Music;
import main.ApplicationMain; // BASE_RESOURCE_PATH를 가져오기 위함

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllMusicList {

    private static final List<Music> allMusic = new ArrayList<>();
    private static boolean initialized = false;

    public static void initializeMusicList() {
        if (initialized) {
            return;
        }

        System.out.println("전체 음악 목록 초기화 시작...");

        // BASE_RESOURCE_PATH를 기준으로 경로를 구성합니다.
        // 음악 파일: BASE_RESOURCE_PATH + "music/" + "음악파일명.wav"
        // 앨범 커버: BASE_RESOURCE_PATH + "covers/" + "앨범커버파일명.jpg"

        // ==== 발라드 ====
        allMusic.add(new Music("응급실", "izi", "발라드", 2005, "쾌걸춘향 OST",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/응급실.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/응급실.jpg"));
        allMusic.add(new Music("한번 더 이별", "이창섭", "발라드", 2025, "첫 사랑 엔딩 OST",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/한번_더_이별.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/한번_더_이별.jpg"));
        allMusic.add(new Music("이별하러 가는 길", "임한별", "발라드", 2018, "싱글",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/이별하러_가는_길.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/이별하러_가는_길.jpg"));

        // ==== 인디 ====
        allMusic.add(new Music("Yours", "데이먼스 이어", "인디", 2019, "Yours",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/Yours.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/Yours.jpg"));
        allMusic.add(new Music("나무", "카더가든", "인디", 2019, "나무",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/나무.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/나무.jpg"));
        allMusic.add(new Music("좋은 밤 좋은 꿈", "너드커넥션", "인디", 2020, "좋은 밤 좋은 꿈",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/좋은_밤_좋은_꿈.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/좋은_밤_좋은_꿈.jpg"));

        // ==== 힙합 ====
        allMusic.add(new Music("죽일놈", "다이나믹 듀오", "힙합", 2009, "Band Of Dynamic Brothers",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/죽일놈.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/죽일놈.jpg"));
        allMusic.add(new Music("Aqua man", "빈지노", "힙합", 2012, "24:2 6",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/Aqua_man.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/Aqua_man.jpg"));
        allMusic.add(new Music("D", "딘", "힙합", 2016, "130 mood : TRBL",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/D.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/D.jpg"));

        // ==== Pop ====
        allMusic.add(new Music("All I want for Christmas is you", "Mariah Carey", "Pop", 1994, "Merry Christmas",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/All_I_want_for_Christmas_is_you.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/All_I_want_for_Christmas_is_you.jpg"));
        allMusic.add(new Music("When I was your man", "Bruno Mars", "Pop", 2012, "Unorthodox Jukebox",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/When_I_was_your_man.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/When_I_was_your_man.jpg"));
        allMusic.add(new Music("Off my face", "Justin Bieber", "Pop", 2021, "Justice",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/Off_my_face.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/Off_my_face.jpg"));

        // ==== K-pop ====
        allMusic.add(new Music("Golden", "HUNTR/X", "K-pop", 2025, "K-Demon Hunters OST",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/Golden.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/Golden.jpg"));
        allMusic.add(new Music("Blue Valentine", "NMIXX", "K-pop", 2025, "Blue Valentine",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/Blue_Valentine.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/Blue_Valentine.jpg"));
        allMusic.add(new Music("Drama", "aespa", "K-pop", 2023, "Drama",
                ApplicationMain.BASE_RESOURCE_PATH + "musics/Drama.wav", ApplicationMain.BASE_RESOURCE_PATH + "covers/Drama.jpg"));
        
        initialized = true;
        System.out.println("전체 음악 목록 초기화 완료. 총 " + allMusic.size() + "곡.");
    }

    public static List<Music> getAllMusic() {
        if (!initialized) {
            initializeMusicList();
        }
        return Collections.unmodifiableList(allMusic);
    }
    
    public static void showAllMusic() {
        if (!initialized) {
            initializeMusicList();
        }
        System.out.println("\n--- 전체 음악 목록 ---");
        int number = 1;
        for (Music m : allMusic) {
            System.out.println(number + ". " + m.toString());
            number++;
        }
        System.out.println("--------------------");
    }
}