package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/*
 * 사용자에게 WAV 오디오 파일을 선택할 수 있는 다이얼로그를 제공하는 클래스.
 */

public class FileSelector {
    
    public static String selectAudioFile(JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("WAV 오디오 파일 선택");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // 파일만 선택 가능

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "WAV 오디오 파일 (*.wav)", "wav"); 
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false); // 모든 파일 옵션 비활성화

        int result = fileChooser.showOpenDialog(parentFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}