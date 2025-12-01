package main;

import manage.Login;

import javax.swing.SwingUtilities;

/*
 * ICTunes 음악 스트리밍 애플리케이션의 메인 진입점 클래스
 * 사용자 로그인/회원가입 기능을 먼저 제공하고, 로그인 성공 시 메인 음악 플레이어 GUI를 실행.
 */
public class ApplicationMain{
	
    public static final String BASE_RESOURCE_PATH = "E:/Folder/"; // <<=== 이 부분을 제공한 음악 및 사진(Folder)의 위치로 변경

    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(() -> {
            System.out.println("ICTunes 애플리케이션을 시작합니다. 로그인 화면을 표시합니다.");
            new Login();
        });
    }
}