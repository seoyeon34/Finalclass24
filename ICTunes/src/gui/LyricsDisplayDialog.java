package gui;

import function.Lyrics; // Lyrics 데이터를 가져오기 위함

import javax.swing.*;
import java.awt.*;

/**
 * 음악 가사를 표시하는 팝업 다이얼로그입니다.
 * (기존 function.Music 내부 클래스에서 분리 및 개선)
 */
public class LyricsDisplayDialog extends JDialog {

    public LyricsDisplayDialog(JFrame parent, String songTitle, String artist) {
        super(parent, songTitle + " - 가사", true); // 모달 다이얼로그로 설정
        
        setSize(400, 600); // 다이얼로그 크기 설정
        setLocationRelativeTo(parent); // 부모 프레임 중앙에 위치

        // 가사 내용 가져오기
        String lyricsText = Lyrics.getLyricsByTitleAndArtist(songTitle, artist);
        if (lyricsText == null || lyricsText.isEmpty()) {
            lyricsText = "가사를 찾을 수 없습니다.";
        }

        JTextArea lyricsArea = new JTextArea(lyricsText);
        lyricsArea.setFont(new Font("맑은 고딕", Font.PLAIN, 16)); // 가독성 좋은 폰트와 크기
        lyricsArea.setEditable(false); // 가사 수정 불가
        lyricsArea.setLineWrap(true); // 줄바꿈 활성화
        lyricsArea.setWrapStyleWord(true); // 단어 단위 줄바꿈
        lyricsArea.setBackground(new Color(30, 30, 30)); // 어두운 배경
        lyricsArea.setForeground(Color.WHITE); // 밝은 글씨색

        JScrollPane scrollPane = new JScrollPane(lyricsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 여백 추가
        add(scrollPane, BorderLayout.CENTER);
        
        // 다이얼로그 닫기 버튼 추가
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 20)); // 버튼 패널 배경색
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}