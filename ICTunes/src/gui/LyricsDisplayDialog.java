package gui;

import function.Lyrics;

import javax.swing.*;
import java.awt.*;

/*
 * 음악 가사를 표시하는 팝업 다이얼로그.
 */

public class LyricsDisplayDialog extends JDialog {

    public LyricsDisplayDialog(JFrame parent, String songTitle, String artist) {
        super(parent, songTitle + " - 가사", true);
        
        setSize(400, 600);
        setLocationRelativeTo(parent);

        // 가사 내용 가져오기
        String lyricsText = Lyrics.getLyricsByTitleAndArtist(songTitle, artist);
        if (lyricsText == null || lyricsText.isEmpty()) {
            lyricsText = "가사를 찾을 수 없습니다.";
        }

        JTextArea lyricsArea = new JTextArea(lyricsText);
        lyricsArea.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        lyricsArea.setEditable(false);
        lyricsArea.setLineWrap(true);
        lyricsArea.setWrapStyleWord(true);
        lyricsArea.setBackground(new Color(30, 30, 30));
        lyricsArea.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(lyricsArea);
        
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 20));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}