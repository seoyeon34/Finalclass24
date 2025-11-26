package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * 사용자에게 WAV 오디오 파일을 선택할 수 있는 다이얼로그를 제공하는 유틸리티 클래스입니다.
 */
public class FileSelector {
    /**
     * 파일 선택 다이얼로그를 표시하고 사용자가 선택한 WAV 오디오 파일의 경로를 반환합니다.
     * @param parentFrame 파일 선택 다이얼로그의 부모 프레임 (null이면 화면 중앙에 표시)
     * @return 선택된 WAV 파일의 절대 경로, 사용자가 취소했으면 null
     */
    public static String selectAudioFile(JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("WAV 오디오 파일 선택");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // 파일만 선택 가능

        // IDE에서 개발 중일 때 기본 경로 설정 예시
        // 프로젝트 루트에 있는 'resources' 폴더를 기본으로 열도록 할 수 있습니다.
        // File defaultDir = new File(System.getProperty("user.dir") + File.separator + "resources" + File.separator + "music");
        // if (defaultDir.exists() && defaultDir.isDirectory()) {
        //     fileChooser.setCurrentDirectory(defaultDir);
        // }

        // WAV 파일만 필터링하도록 설정합니다.
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "WAV 오디오 파일 (*.wav)", "wav"); 
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false); // "모든 파일" 옵션 비활성화

        int result = fileChooser.showOpenDialog(parentFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}