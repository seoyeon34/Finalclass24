package gui;

import audio.MusicFileNotFoundException;
import audio.MusicPlaybackException;
import audio.WAVAudioFile;
import model.Music;
import player.AppMusicPlayer;
import search.SearchCriteria;
import search.SearchManager;
import main.ApplicationMain; // BASE_RESOURCE_PATH를 가져오기 위함

import javax.sound.sampled.LineEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * ICTunes 음악 스트리밍 애플리케이션의 메인 GUI 클래스
 * 음악 재생, 목록 관리, 검색, 좋아요, 추천 등의 기능을 통합적으로 제공
 */

public class MusicPlayerGUI extends JFrame {

    // --- 핵심 로직 인스턴스 ---
    private AppMusicPlayer appMusicPlayer;
    private SearchManager searchManager;
    private List<Music> allMusicDatabase;

    // --- GUI 컴포넌트 ---
    private JPanel backgroundPanel;
    private ImageIcon backgroundImage;
    
    private JTabbedPane tabbedPane;

    // --- 1. '재생 / 재생목록' 탭 (Main Playback & Playlist) ---
    private JPanel mainPlaybackPanel;
    private JLabel coverImageLabel;
    private JLabel currentSongTitleLabel;
    private JLabel currentSongArtistLabel;
    private JSlider progressBar;
    private JLabel timeLabel;
    
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton prevButton;
    private JButton nextButton;
    private JToggleButton shuffleButton;
    private JToggleButton repeatButton;
    private JToggleButton likeButton;
    private JButton addFileButton;

    private DefaultListModel<WAVAudioFile> playlistListModel;
    private JList<WAVAudioFile> playlistJList;

    // --- 2. '검색' 탭 ---
    private JPanel searchTabPanel;
    private JTextField searchInputField;
    private JComboBox<SearchCriteria> searchTypeComboBox;
    private JButton searchExecuteButton;
    private DefaultTableModel searchResultTableModel;
    private JTable searchResultTable;
    private JLabel searchStatusLabel;

    // --- 3. '좋아요 음악' 탭 ---
    private JPanel likedSongsTabPanel;
    private DefaultListModel<WAVAudioFile> likedSongsListModel;
    private JList<WAVAudioFile> likedSongsJList;
    private JLabel noLikedSongsLabel;
    private JButton likedPlayButton; // 추가: 좋아요 탭 내 선택 재생 버튼
    private JButton likedAddButton;  // 추가: 좋아요 탭 내 재생목록 추가 버튼

    // --- 4. '추천 음악' 탭 ---
    private JPanel recommendedSongsTabPanel;
    private DefaultListModel<WAVAudioFile> recommendedSongsListModel;
    private JList<WAVAudioFile> recommendedSongsJList;
    private JButton refreshRecommendButton;
    private JLabel noRecommendedSongsLabel; // 비어있는 목록 메시지 레이블
    private JButton recommendedPlayButton; // 추가: 추천 탭 내 선택 재생 버튼
    private JButton recommendedAddButton;  // 추가: 추천 탭 내 재생목록 추가 버튼

    // --- 타이머 ---
    private Timer playbackTimer;
    
    // --- 생성자 ---
    public MusicPlayerGUI() {
        super("ICTunes 음악 스트리밍");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        playlist.AllMusicList.initializeMusicList(); // 가장 먼저 전체 음악 목록 초기화
        this.allMusicDatabase = playlist.AllMusicList.getAllMusic();

        this.searchManager = new SearchManager();
        this.appMusicPlayer = new AppMusicPlayer(this);
        
        // 배경 이미지 로딩 (여기 안되는데 왜 그러는건지 모르겠다 ㅜ)
        try {
            File bgFile = new File(ApplicationMain.BASE_RESOURCE_PATH + "images/background.jpg");
            if (bgFile.exists()) {
                backgroundImage = new ImageIcon(bgFile.getAbsolutePath());
               
                // 이미지 크기 조절
                Image img = backgroundImage.getImage().getScaledInstance(
                    this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
                backgroundImage = new ImageIcon(img);
            } else {
                System.err.println("배경 이미지 파일을 찾을 수 없습니다: " + bgFile.getAbsolutePath());
               
                throw new IOException("Background image file not found at " + bgFile.getAbsolutePath());
            }
        } catch (Exception e) { // IOException을 포함한 모든 예외를 잡음
            System.err.println("배경 이미지 로드 실패: " + e.getMessage() + "\n기본 배경으로 진행합니다.");
            backgroundImage = null; // 로드 실패 시 null로 설정하여 기본 JPanel 배경으로
        }

        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.BLACK); // 배경 이미지 없을 시 검정색 배경
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        createComponents();
        addEventListeners();
        initPlaybackTimer();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("애플리케이션 종료 요청. 리소스를 해제합니다.");
                if (playbackTimer != null) {
                    playbackTimer.stop();
                }
                appMusicPlayer.closeAllAudioFiles();
                System.exit(0);
            }
        });
        
        try {
            
            appMusicPlayer.setPlaylist(allMusicDatabase);
            updatePlaylistList(); // 재생 목록 UI 업데이트
        } catch (MusicFileNotFoundException | MusicPlaybackException e) {
            JOptionPane.showMessageDialog(this,
                    "초기 음악 목록 로드 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // 어떤 파일에서 문제 발생했는지 자세한 스택 출력
        }

        updatePlayerUI();
        setVisible(true);
    }
    
    private void createComponents() {
        tabbedPane = createStyledTabbedPane();
        backgroundPanel.add(tabbedPane, BorderLayout.CENTER);

        createMainPlaybackTab();
        createSearchTab();
        createLikedSongsTab();      // noLikedSongsLabel 추가
        createRecommendedSongsTab(); // noRecommendedSongsLabel 추가

        tabbedPane.addTab("▶ 재생 / 목록", createPanelWithBackground(mainPlaybackPanel));
        tabbedPane.addTab("검색", createPanelWithBackground(searchTabPanel));
        tabbedPane.addTab("좋아요 음악", createPanelWithBackground(likedSongsTabPanel));
        tabbedPane.addTab("추천 음악", createPanelWithBackground(recommendedSongsTabPanel));
    }

    private JPanel createPanelWithBackground(JPanel innerPanel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(innerPanel, BorderLayout.CENTER);
        innerPanel.setOpaque(false);
        return wrapper;
    }
    
    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tp = new JTabbedPane();
        tp.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        tp.setBackground(new Color(0, 0, 0, 150));
        tp.setForeground(Color.WHITE);
        tp.setOpaque(false);

        tp.addChangeListener(e -> {
            int selectedIndex = tp.getSelectedIndex();
            if (selectedIndex == 0) {
                updatePlayerUI();
                updatePlaylistList();
            } else if (selectedIndex == 1) {
                
            } else if (selectedIndex == 2) {
                updateLikedSongsList();
            } else if (selectedIndex == 3) {
                updateRecommendedSongsList();
            }
        });
        return tp;
    }

    private void createMainPlaybackTab() {
        mainPlaybackPanel = new JPanel(new BorderLayout(10, 10));
        mainPlaybackPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPlaybackPanel = new JPanel(new BorderLayout(0, 10));
        topPlaybackPanel.setOpaque(false);

        coverImageLabel = new JLabel();
        coverImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        coverImageLabel.setPreferredSize(new Dimension(250, 250));
        coverImageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 기본 커버 이미지 로딩: 절대 경로
        try {
            File defaultCoverFile = new File(ApplicationMain.BASE_RESOURCE_PATH + "images/default_cover.jpg");
            if (defaultCoverFile.exists()) {
                ImageIcon defaultCover = new ImageIcon(defaultCoverFile.getAbsolutePath());
                Image scaledDefaultCover = defaultCover.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                coverImageLabel.setIcon(new ImageIcon(scaledDefaultCover));
            } else {
                System.err.println("기본 커버 이미지 파일을 찾을 수 없습니다: " + defaultCoverFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("기본 커버 이미지 로드 실패: " + e.getMessage());
        }

        JPanel songInfoPanel = new JPanel();
        songInfoPanel.setLayout(new BoxLayout(songInfoPanel, BoxLayout.Y_AXIS));
        songInfoPanel.setOpaque(false);
        songInfoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        currentSongTitleLabel = new JLabel("현재 재생곡: -");
        currentSongTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        currentSongTitleLabel.setForeground(Color.WHITE);
        currentSongTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        currentSongArtistLabel = new JLabel("가수: -");
        currentSongArtistLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        currentSongArtistLabel.setForeground(Color.LIGHT_GRAY);
        currentSongArtistLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        songInfoPanel.add(currentSongTitleLabel);
        songInfoPanel.add(currentSongArtistLabel);

        topPlaybackPanel.add(coverImageLabel, BorderLayout.CENTER);
        topPlaybackPanel.add(songInfoPanel, BorderLayout.SOUTH);

        JPanel progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setOpaque(false);

        progressBar = new JSlider(0, 100, 0);
        progressBar.setOpaque(false);
        progressBar.setForeground(Color.WHITE);
        progressBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (appMusicPlayer.getCurrentAudioFile() != null) {
                    long totalLength = appMusicPlayer.getCurrentAudioFile().getDuration();
                    long newPosition = (long) (totalLength * (progressBar.getValue() / 100.0));
                    try {
                        appMusicPlayer.getCurrentAudioFile().setPosition(newPosition);
                        if (!appMusicPlayer.isPlaying() && appMusicPlayer.isPaused()) {
                            appMusicPlayer.play();
                        }
                    } catch (MusicPlaybackException ex) {
                        displayErrorMessage("재생 위치 변경 오류", "재생 위치를 변경할 수 없습니다: " + ex.getMessage());
                    }
                }
            }
        });
        
        timeLabel = new JLabel("00:00 / 00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        timeLabel.setForeground(Color.WHITE);

        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(timeLabel, BorderLayout.SOUTH);
        
        topPlaybackPanel.add(progressPanel, BorderLayout.NORTH);
        mainPlaybackPanel.add(topPlaybackPanel, BorderLayout.NORTH);

        JPanel controlButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlButtonPanel.setOpaque(false);

        playButton = createStyledIconButton("▶", new Color(0, 150, 0));
        pauseButton = createStyledIconButton("❚❚", new Color(200, 150, 0));
        stopButton = createStyledIconButton("■", new Color(150, 0, 0));
        prevButton = createStyledIconButton("◀◀", new Color(50, 50, 50));
        nextButton = createStyledIconButton("▶▶", new Color(50, 50, 50));
        shuffleButton = createStyledToggleButton("🔀", new Color(0, 100, 150));
        repeatButton = createStyledToggleButton("🔁", new Color(0, 100, 150));
        likeButton = createStyledToggleButton("♥", new Color(180, 0, 0));
        addFileButton = createStyledButton("파일 추가", new Color(50, 50, 50));
        
        controlButtonPanel.add(prevButton);
        controlButtonPanel.add(playButton);
        controlButtonPanel.add(pauseButton);
        controlButtonPanel.add(stopButton);
        controlButtonPanel.add(nextButton);
        controlButtonPanel.add(shuffleButton);
        controlButtonPanel.add(repeatButton);
        controlButtonPanel.add(likeButton);
        controlButtonPanel.add(addFileButton);

        mainPlaybackPanel.add(controlButtonPanel, BorderLayout.CENTER);

        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setOpaque(false);
        playlistPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                "현재 재생 목록",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("맑은 고딕", Font.BOLD, 14), Color.WHITE));

        playlistListModel = new DefaultListModel<>();
        playlistJList = new JList<>(playlistListModel);
        playlistJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playlistJList.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        playlistJList.setBackground(new Color(0, 0, 0, 150));
        playlistJList.setForeground(Color.WHITE);
        playlistJList.setSelectionBackground(new Color(50, 50, 50, 180));
        playlistJList.setSelectionForeground(Color.CYAN);
        playlistJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WAVAudioFile) {
                    WAVAudioFile wavFile = (WAVAudioFile) value;
                    setText(wavFile.getTitle() + " - " + wavFile.getArtist() + (wavFile.isLiked() ? " ♥" : ""));
                }
                if (isSelected) {
                    setBackground(new Color(50, 50, 50, 180));
                    setForeground(Color.CYAN);
                } else {
                    setBackground(new Color(0, 0, 0, 150));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });

        JScrollPane playlistScrollPane = new JScrollPane(playlistJList);
        playlistScrollPane.setOpaque(false);
        playlistScrollPane.getViewport().setOpaque(false);

        playlistPanel.add(playlistScrollPane, BorderLayout.CENTER);
        mainPlaybackPanel.add(playlistPanel, BorderLayout.SOUTH);
    }

    private void createSearchTab() {
        searchTabPanel = new JPanel(new BorderLayout(10, 10));
        searchTabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        searchTabPanel.setOpaque(false);

        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchInputPanel.setOpaque(false);

        searchInputField = new JTextField(25);
        searchInputField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchInputField.setPreferredSize(new Dimension(250, 30));

        searchTypeComboBox = new JComboBox<>(SearchCriteria.values());
        searchTypeComboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SearchCriteria) {
                    setText(((SearchCriteria) value).getDisplayName());
                }
                return this;
            }
        });

        searchExecuteButton = createStyledButton("검색", new Color(0, 120, 180));
        
        searchInputPanel.add(new JLabel("검색어:"));
        searchInputPanel.add(searchInputField);
        searchInputPanel.add(new JLabel("유형:"));
        searchInputPanel.add(searchTypeComboBox);
        searchInputPanel.add(searchExecuteButton);

        searchTabPanel.add(searchInputPanel, BorderLayout.NORTH);

        String[] columnNames = {"제목", "가수", "장르", "발매년도", "앨범"};
        searchResultTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        searchResultTable = new JTable(searchResultTableModel);
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultTable.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchResultTable.setBackground(new Color(0, 0, 0, 150));
        searchResultTable.setForeground(Color.WHITE);
        searchResultTable.setSelectionBackground(new Color(50, 50, 50, 180));
        searchResultTable.setSelectionForeground(Color.CYAN);
        searchResultTable.setRowHeight(25);
        
        searchResultTable.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 14));
        searchResultTable.getTableHeader().setBackground(new Color(30, 30, 30));
        searchResultTable.getTableHeader().setForeground(Color.WHITE);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i = 0; i < searchResultTable.getColumnCount(); i++) {
            searchResultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane searchScrollPane = new JScrollPane(searchResultTable);
        searchScrollPane.setOpaque(false);
        searchScrollPane.getViewport().setOpaque(false);
        searchTabPanel.add(searchScrollPane, BorderLayout.CENTER);

        searchStatusLabel = new JLabel("검색 결과를 표시합니다.", SwingConstants.CENTER);
        searchStatusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchStatusLabel.setForeground(Color.LIGHT_GRAY);
        searchTabPanel.add(searchStatusLabel, BorderLayout.SOUTH);
    }

    private void createLikedSongsTab() {
        likedSongsTabPanel = new JPanel(new BorderLayout(10, 10));
        likedSongsTabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        likedSongsTabPanel.setOpaque(false);

        // 비어있는 목록일 때 표시될 메시지 레이블
        noLikedSongsLabel = new JLabel("좋아요 한 곡이 없습니다.", SwingConstants.CENTER);
        noLikedSongsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        noLikedSongsLabel.setForeground(Color.GRAY);
        
        // noLikedSongsLabel은 updateLikedSongsList()에서 setVisible()로 제어됩니다.
        likedSongsTabPanel.add(noLikedSongsLabel, BorderLayout.CENTER); // CENTER에 배치

        likedSongsListModel = new DefaultListModel<>();
        likedSongsJList = new JList<>(likedSongsListModel);
        likedSongsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        likedSongsJList.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        likedSongsJList.setBackground(new Color(0, 0, 0, 150));
        likedSongsJList.setForeground(Color.WHITE);
        likedSongsJList.setSelectionBackground(new Color(50, 50, 50, 180));
        likedSongsJList.setSelectionForeground(Color.RED);

        likedSongsJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WAVAudioFile) {
                    WAVAudioFile wavFile = (WAVAudioFile) value;
                    setText(wavFile.getTitle() + " - " + wavFile.getArtist());
                }
                if (isSelected) {
                    setBackground(new Color(50, 50, 50, 180));
                    setForeground(Color.RED);
                } else {
                    setBackground(new Color(0, 0, 0, 150));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });

        JScrollPane likedScrollPane = new JScrollPane(likedSongsJList);
        likedScrollPane.setOpaque(false);
        likedScrollPane.getViewport().setOpaque(false);
        likedSongsTabPanel.add(likedScrollPane, BorderLayout.CENTER); // JList를 담는 스크롤팬도 CENTER에 배치

        JPanel likedButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        likedButtonPanel.setOpaque(false);
        likedPlayButton = createStyledButton("선택한 곡 재생", new Color(0, 150, 0)); // 필드 변수에 할당
        likedAddButton = createStyledButton("선택한 곡 재생목록 추가", new Color(50, 50, 50)); // 필드 변수에 할당
        likedButtonPanel.add(likedPlayButton);
        likedButtonPanel.add(likedAddButton);
        likedSongsTabPanel.add(likedButtonPanel, BorderLayout.SOUTH);
    }

    private void createRecommendedSongsTab() {
        recommendedSongsTabPanel = new JPanel(new BorderLayout(10, 10));
        recommendedSongsTabPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        recommendedSongsTabPanel.setOpaque(false);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        topPanel.setOpaque(false);
        JLabel recommendTitleLabel = new JLabel("현재 곡 기반 추천 음악");
        recommendTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        recommendTitleLabel.setForeground(Color.WHITE);
        topPanel.add(recommendTitleLabel);
        refreshRecommendButton = createStyledButton("새로고침", new Color(80, 80, 80));
        topPanel.add(refreshRecommendButton);
        recommendedSongsTabPanel.add(topPanel, BorderLayout.NORTH);

        // 비어있는 목록일 때 표시될 메시지 레이블
        noRecommendedSongsLabel = new JLabel("추천 음악이 없습니다. 다른 곡을 재생해보세요.", SwingConstants.CENTER);
        noRecommendedSongsLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        noRecommendedSongsLabel.setForeground(Color.GRAY);
        
        // noRecommendedSongsLabel은 updateRecommendedSongsList()에서 setVisible()로 제어됩니다.
        recommendedSongsTabPanel.add(noRecommendedSongsLabel, BorderLayout.CENTER); // CENTER에 배치

        recommendedSongsListModel = new DefaultListModel<>();
        recommendedSongsJList = new JList<>(recommendedSongsListModel);
        recommendedSongsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recommendedSongsJList.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        recommendedSongsJList.setBackground(new Color(0, 0, 0, 150));
        recommendedSongsJList.setForeground(Color.WHITE);
        recommendedSongsJList.setSelectionBackground(new Color(50, 50, 50, 180));
        recommendedSongsJList.setSelectionForeground(Color.GREEN);
        
        recommendedSongsJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof WAVAudioFile) {
                    WAVAudioFile wavFile = (WAVAudioFile) value;
                    setText(wavFile.getTitle() + " - " + wavFile.getArtist() + " (" + wavFile.getGenre() + ")");
                }
                if (isSelected) {
                    setBackground(new Color(50, 50, 50, 180));
                    setForeground(Color.GREEN);
                } else {
                    setBackground(new Color(0, 0, 0, 150));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });

        JScrollPane recommendedScrollPane = new JScrollPane(recommendedSongsJList);
        recommendedScrollPane.setOpaque(false);
        recommendedScrollPane.getViewport().setOpaque(false);
        recommendedSongsTabPanel.add(recommendedScrollPane, BorderLayout.CENTER);
        
        JPanel recommendButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        recommendButtonPanel.setOpaque(false);
        recommendedPlayButton = createStyledButton("선택한 곡 재생", new Color(0, 150, 0)); // 필드 변수에 할당
        recommendedAddButton = createStyledButton("선택한 곡 재생목록 추가", new Color(50, 50, 50)); // 필드 변수에 할당
        recommendButtonPanel.add(recommendedPlayButton);
        recommendButtonPanel.add(recommendedAddButton);
        recommendedSongsTabPanel.add(recommendButtonPanel, BorderLayout.SOUTH);
    }
    
    private void addEventListeners() {
        playButton.addActionListener(e -> {
            try {
                appMusicPlayer.play();
                updatePlayerUI();
            } catch (MusicPlaybackException ex) {
                displayErrorMessage("재생 오류", ex.getMessage());
            }
        });

        pauseButton.addActionListener(e -> {
            try {
                appMusicPlayer.pause();
                updatePlayerUI();
            } catch (MusicPlaybackException ex) {
                displayErrorMessage("일시정지 오류", ex.getMessage());
            }
        });

        stopButton.addActionListener(e -> {
            try {
                appMusicPlayer.stop();
                updatePlayerUI();
            } catch (MusicPlaybackException ex) {
                displayErrorMessage("정지 오류", ex.getMessage());
            }
        });

        prevButton.addActionListener(e -> {
            try {
                appMusicPlayer.previous();
                updatePlayerUI();
            } catch (MusicPlaybackException ex) {
                displayErrorMessage("이전 곡 오류", ex.getMessage());
            }
        });

        nextButton.addActionListener(e -> {
            try {
                appMusicPlayer.next();
                updatePlayerUI();
            } catch (MusicPlaybackException ex) {
                displayErrorMessage("다음 곡 오류", ex.getMessage());
            }
        });

        shuffleButton.addActionListener(e -> {
            appMusicPlayer.toggleShuffle();
            updatePlayerUI(); 
            displayInfoMessage("셔플 모드", "셔플 모드: " + (appMusicPlayer.isShuffleMode() ? "ON" : "OFF"));
        });

        repeatButton.addActionListener(e -> {
            appMusicPlayer.toggleRepeat();
            updatePlayerUI();
            displayInfoMessage("반복 모드", "반복 모드: " + (appMusicPlayer.isRepeatMode() ? "ON" : "OFF"));
        });

        likeButton.addActionListener(e -> {
            WAVAudioFile current = appMusicPlayer.getCurrentAudioFile();
            if (current != null) {
                appMusicPlayer.toggleLike(current);
                updatePlayerUI(); // 좋아요 버튼 상태 변경 반영
                updateLikedSongsList(); // 좋아요 목록 갱신
            } else {
                displayInfoMessage("좋아요", "현재 재생 중인 곡이 없습니다.");
                likeButton.setSelected(false);
            }
        });

        addFileButton.addActionListener(e -> {
            String filePath = FileSelector.selectAudioFile(this); // FileSelector는 항상 절대 경로를 반환.
            if (filePath != null) {
                try {
                    File selectedFile = new File(filePath);
                    String fileName = selectedFile.getName();
                    String title = fileName.substring(0, fileName.lastIndexOf('.'));
                    
                    // 동적으로 추가되는 음악 파일의 앨범 커버는 기본 커버를 사용 (절대 경로 지정)
                    Music newMusicData = new Music(title, "Unknown Artist", "Unknown Genre", 
                                                    2025, "Unknown Album", filePath, 
                                                    ApplicationMain.BASE_RESOURCE_PATH + "images/default_cover.jpg");

                    WAVAudioFile newWavFile = new WAVAudioFile(newMusicData);
                    appMusicPlayer.addSong(newWavFile);
                    updatePlaylistList();
                    displayInfoMessage("파일 추가", "'" + title + "' (WAV) 파일을 재생 목록에 추가했습니다.");
                } catch (MusicFileNotFoundException | MusicPlaybackException ex) {
                    displayErrorMessage("파일 추가 오류", "음악 파일을 로드할 수 없습니다: " + ex.getMessage());
                }
            }
        });

        playlistJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = playlistJList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        try {
                            appMusicPlayer.play(index);
                            updatePlayerUI();
                        } catch (MusicPlaybackException ex) {
                            displayErrorMessage("재생 오류", ex.getMessage());
                        }
                    }
                }
            }
        });

        coverImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WAVAudioFile current = appMusicPlayer.getCurrentAudioFile();
                if (current != null) {
                    new LyricsDisplayDialog(MusicPlayerGUI.this, current.getTitle(), current.getArtist());
                } else {
                    displayInfoMessage("가사", "현재 재생 중인 곡이 없습니다.");
                }
            }
        });

        searchExecuteButton.addActionListener(e -> performSearch());
        searchInputField.addActionListener(e -> performSearch());

        searchResultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = searchResultTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String title = (String) searchResultTableModel.getValueAt(selectedRow, 0);
                        String artist = (String) searchResultTableModel.getValueAt(selectedRow, 1);
                        
                        Music selectedMusic = allMusicDatabase.stream()
                                .filter(m -> m.getTitle().equals(title) && m.getArtist().equals(artist))
                                .findFirst()
                                .orElse(null);

                        if (selectedMusic != null) {
                            try {
                                WAVAudioFile wavFile = new WAVAudioFile(selectedMusic);
                                // 재생 목록에 이미 있는 곡인지 확인 후 추가 (중복 방지)
                                if (!appMusicPlayer.getPlaylist().contains(wavFile)) { 
                                    appMusicPlayer.addSong(wavFile);
                                }
                                appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(wavFile)); 
                                updatePlaylistList();
                                updatePlayerUI();
                                tabbedPane.setSelectedIndex(0);
                                displayInfoMessage("음악 재생", selectedMusic.getTitle() + "을(를) 재생합니다.");
                            } catch (MusicFileNotFoundException | MusicPlaybackException ex) {
                                displayErrorMessage("재생 오류", "선택한 음악을 재생할 수 없습니다: " + ex.getMessage());
                            }
                        }
                    }
                }
            }
        });

        // 좋아요 음악 탭 버튼 이벤트 리스너
        likedPlayButton.addActionListener(e -> {
            int selectedIndex = likedSongsJList.getSelectedIndex();
            if (selectedIndex != -1) {
                Object selectedValue = likedSongsListModel.getElementAt(selectedIndex);
                if (selectedValue instanceof WAVAudioFile) { // WAVAudioFile 객체일 때만 처리
                    try {
                        WAVAudioFile selectedLikedSong = (WAVAudioFile) selectedValue;
                        if (!appMusicPlayer.getPlaylist().contains(selectedLikedSong)) {
                             appMusicPlayer.addSong(selectedLikedSong);
                        }
                        appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(selectedLikedSong));
                        updatePlaylistList();
                        updatePlayerUI();
                        tabbedPane.setSelectedIndex(0);
                    } catch (MusicPlaybackException ex) {
                        displayErrorMessage("재생 오류", ex.getMessage());
                    }
                } else { // WAVAudioFile 객체가 아닌 경우 (이 상황은 없어야 하지만 방어적 코딩)
                     displayInfoMessage("알림", "선택된 항목은 재생할 수 없습니다.");
                }
            } else {
                displayInfoMessage("알림", "재생할 곡을 선택해주세요.");
            }
        });

        likedAddButton.addActionListener(e -> {
            int selectedIndex = likedSongsJList.getSelectedIndex();
            if (selectedIndex != -1) {
                Object selectedValue = likedSongsListModel.getElementAt(selectedIndex);
                 if (selectedValue instanceof WAVAudioFile) { // WAVAudioFile 객체일 때만 처리
                    WAVAudioFile selectedLikedSong = (WAVAudioFile) selectedValue;
                    if (!appMusicPlayer.getPlaylist().contains(selectedLikedSong)) {
                        appMusicPlayer.addSong(selectedLikedSong);
                        updatePlaylistList();
                        displayInfoMessage("재생 목록 추가", selectedLikedSong.getTitle() + "을(를) 재생 목록에 추가했습니다.");
                    } else {
                        displayInfoMessage("알림", selectedLikedSong.getTitle() + "은(는) 이미 재생 목록에 있습니다.");
                    }
                 } else { // WAVAudioFile 객체가 아닌 경우
                     displayInfoMessage("알림", "선택된 항목은 재생 목록에 추가할 수 없습니다.");
                 }
            } else {
                displayInfoMessage("알림", "재생 목록에 추가할 곡을 선택해주세요.");
            }
        });

        refreshRecommendButton.addActionListener(e -> updateRecommendedSongsList());
        
        // 추천 음악 탭 버튼 이벤트 리스너
        recommendedPlayButton.addActionListener(e -> {
            int selectedIndex = recommendedSongsJList.getSelectedIndex();
            if (selectedIndex != -1) {
                Object selectedValue = recommendedSongsListModel.getElementAt(selectedIndex);
                if (selectedValue instanceof WAVAudioFile) { // WAVAudioFile 객체일 때만 처리
                    try {
                        WAVAudioFile selectedRecommendedSong = (WAVAudioFile) selectedValue;
                        if (!appMusicPlayer.getPlaylist().contains(selectedRecommendedSong)) {
                            appMusicPlayer.addSong(selectedRecommendedSong);
                        }
                        appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(selectedRecommendedSong));
                        updatePlaylistList();
                        updatePlayerUI();
                        tabbedPane.setSelectedIndex(0);
                    } catch (MusicPlaybackException ex) {
                        displayErrorMessage("재생 오류", ex.getMessage());
                    }
                } else { // WAVAudioFile 객체가 아닌 경우
                    displayInfoMessage("알림", "선택된 항목은 재생할 수 없습니다.");
                }
            } else {
                displayInfoMessage("알림", "재생할 곡을 선택해주세요.");
            }
        });

        recommendedAddButton.addActionListener(e -> {
            int selectedIndex = recommendedSongsJList.getSelectedIndex();
            if (selectedIndex != -1) {
                Object selectedValue = recommendedSongsListModel.getElementAt(selectedIndex);
                if (selectedValue instanceof WAVAudioFile) { // WAVAudioFile 객체일 때만 처리
                    WAVAudioFile selectedRecommendedSong = (WAVAudioFile) selectedValue;
                    if (!appMusicPlayer.getPlaylist().contains(selectedRecommendedSong)) {
                        appMusicPlayer.addSong(selectedRecommendedSong);
                        updatePlaylistList();
                        displayInfoMessage("재생 목록 추가", selectedRecommendedSong.getTitle() + "을(를) 재생 목록에 추가했습니다.");
                    } else {
                        displayInfoMessage("알림", selectedRecommendedSong.getTitle() + "은(는) 이미 재생 목록에 있습니다.");
                    }
                } else { // WAVAudioFile 객체가 아닌 경우
                    displayInfoMessage("알림", "선택된 항목은 재생 목록에 추가할 수 없습니다.");
                }
            } else {
                displayInfoMessage("알림", "재생 목록에 추가할 곡을 선택해주세요.");
            }
        });

        recommendedSongsJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = recommendedSongsJList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Object selectedValue = recommendedSongsListModel.getElementAt(index);
                        if (selectedValue instanceof WAVAudioFile) {
                            try {
                                WAVAudioFile selectedRecommendedSong = (WAVAudioFile) selectedValue;
                                if (!appMusicPlayer.getPlaylist().contains(selectedRecommendedSong)) {
                                    appMusicPlayer.addSong(selectedRecommendedSong);
                                }
                                appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(selectedRecommendedSong));
                                updatePlaylistList();
                                updatePlayerUI();
                                tabbedPane.setSelectedIndex(0);
                            } catch (MusicPlaybackException ex) {
                                displayErrorMessage("재생 오류", ex.getMessage());
                            }
                        }
                    }
                }
            }
        });

        likedSongsJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = likedSongsJList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Object selectedValue = likedSongsListModel.getElementAt(index);
                        if (selectedValue instanceof WAVAudioFile) {
                            try {
                                WAVAudioFile selectedLikedSong = (WAVAudioFile) selectedValue;
                                if (!appMusicPlayer.getPlaylist().contains(selectedLikedSong)) {
                                    appMusicPlayer.addSong(selectedLikedSong);
                                }
                                appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(selectedLikedSong));
                                updatePlaylistList();
                                updatePlayerUI();
                                tabbedPane.setSelectedIndex(0);
                            } catch (MusicPlaybackException ex) {
                                displayErrorMessage("재생 오류", ex.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }

    private void performSearch() {
        String keyword = searchInputField.getText().trim();
        SearchCriteria searchType = (SearchCriteria) searchTypeComboBox.getSelectedItem();

        List<Music> results = new java.util.ArrayList<>();

        if (keyword.isEmpty() && searchType != SearchCriteria.ALL) {
            displayInfoMessage("검색 알림", "검색어를 입력해주세요. ('통합' 검색은 검색어 없이 전체 리스트 조회)");
            searchResultTableModel.setRowCount(0);
            searchStatusLabel.setText("검색 결과: 0개");
            return;
        }

        List<Music> currentSearchScope = allMusicDatabase; 

        try {
            switch (searchType) {
                case TITLE:
                    results = searchManager.searchByTitle(keyword, currentSearchScope);
                    break;
                case ARTIST:
                    results = searchManager.searchByArtist(keyword, currentSearchScope);
                    break;
                case GENRE:
                    results = searchManager.searchByGenre(keyword, currentSearchScope);
                    break;
                case RELEASE_YEAR:
                    try {
                        int year = Integer.parseInt(keyword);
                        results = searchManager.searchByReleaseYear(year, currentSearchScope);
                    } catch (NumberFormatException ex) {
                        displayErrorMessage("입력 오류", "발매년도 검색은 숫자만 입력할 수 있습니다.");
                        return;
                    }
                    break;
                case ALL:
                    if (keyword.isEmpty()) {
                        results = currentSearchScope;
                    } else {
                        results = searchManager.searchAll(keyword, currentSearchScope);
                    }
                    break;
            }
        } catch (Exception ex) {
            displayErrorMessage("검색 오류", "검색 중 오류가 발생했습니다: " + ex.getMessage());
        }
        updateSearchResultsTable(results);
    }
    
    private void initPlaybackTimer() {
        playbackTimer = new Timer(100, e -> {
            if (appMusicPlayer.isPlaying()) {
                WAVAudioFile current = appMusicPlayer.getCurrentAudioFile();
                if (current != null) {
                    long currentPos = current.getCurrentPosition();
                    long totalLength = current.getDuration();
                    
                    if (totalLength > 0) {
                        int progress = (int) ((double) currentPos / totalLength * 100);
                        progressBar.setValue(progress);
                    } else {
                        progressBar.setValue(0);
                    }
                    
                    timeLabel.setText(formatTime(currentPos) + " / " + formatTime(totalLength));
                }
            } else {
                if (!appMusicPlayer.isPaused()) {
                    progressBar.setValue(0);
                    timeLabel.setText("00:00 / 00:00");
                }
            }
        });
        playbackTimer.start();
    }

    public void updatePlayerUI() {
        WAVAudioFile current = appMusicPlayer.getCurrentAudioFile();

        if (current == null) {
            currentSongTitleLabel.setText("현재 재생곡: -");
            currentSongArtistLabel.setText("가수: -");
            coverImageLabel.setIcon(getScaledCoverIcon(ApplicationMain.BASE_RESOURCE_PATH + "images/default_cover.jpg", 250, 250));
            progressBar.setValue(0);
            timeLabel.setText("00:00 / 00:00");
            playButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            likeButton.setEnabled(false);
            likeButton.setSelected(false);
        } else {
            currentSongTitleLabel.setText(current.getTitle());
            currentSongArtistLabel.setText(current.getArtist());
            coverImageLabel.setIcon(getScaledCoverIcon(current.getCoverPath(), 250, 250));

            playButton.setEnabled(!appMusicPlayer.isPlaying() && !appMusicPlayer.isPaused());
            pauseButton.setEnabled(appMusicPlayer.isPlaying());
            stopButton.setEnabled(appMusicPlayer.isPlaying() || appMusicPlayer.isPaused());
            prevButton.setEnabled(true);
            nextButton.setEnabled(true);
            likeButton.setEnabled(true);

            likeButton.setSelected(current.isLiked());
        }

        shuffleButton.setSelected(appMusicPlayer.isShuffleMode());
        repeatButton.setSelected(appMusicPlayer.isRepeatMode());

        int currentIdx = -1;
        if (current != null) {
            currentIdx = appMusicPlayer.getPlaylist().indexOf(current);
        }
        if (currentIdx != -1) {
            playlistJList.setSelectedIndex(currentIdx);
            playlistJList.ensureIndexIsVisible(currentIdx);
        } else {
            playlistJList.clearSelection();
        }

        // 좋아요/추천 탭이 열려있다면 UI 업데이트 (일관성 유지)
        if (tabbedPane.getSelectedIndex() == 2) {
            updateLikedSongsList();
        }
        if (tabbedPane.getSelectedIndex() == 3) {
            updateRecommendedSongsList();
        }
    }

    private void updatePlaylistList() {
        playlistListModel.clear();
        for (WAVAudioFile audioFile : appMusicPlayer.getPlaylist()) {
            playlistListModel.addElement(audioFile);
        }
        if (appMusicPlayer.getPlaylist().isEmpty() && appMusicPlayer.getCurrentAudioFile() == null) {
            updatePlayerUI();
        }
    }

    private void updateSearchResultsTable(List<Music> results) {
        searchResultTableModel.setRowCount(0);

        if (results.isEmpty()) {
            searchStatusLabel.setText("검색 결과: 0개");
        } else {
            for (Music music : results) {
                searchResultTableModel.addRow(new Object[]{
                    music.getTitle(),
                    music.getArtist(),
                    music.getGenre(),
                    music.getReleaseYear(),
                    music.getAlbum()
                });
            }
            searchStatusLabel.setText("총 " + results.size() + "개의 음악이 검색되었습니다.");
        }
    }

    private void updateLikedSongsList() {
        likedSongsListModel.clear();
        List<WAVAudioFile> likedSongs = appMusicPlayer.getLikedSongs();
        if (likedSongs.isEmpty()) {
            likedSongsJList.setVisible(false);
            noLikedSongsLabel.setVisible(true);
        } else {
            likedSongsJList.setVisible(true);
            noLikedSongsLabel.setVisible(false);
            for (WAVAudioFile audioFile : likedSongs) {
                likedSongsListModel.addElement(audioFile);
            }
        }
    }

    private void updateRecommendedSongsList() {
        recommendedSongsListModel.clear();
        List<WAVAudioFile> recommended = appMusicPlayer.getRecommendedSongs();
        if (recommended.isEmpty()) {
            recommendedSongsJList.setVisible(false);
            noRecommendedSongsLabel.setVisible(true);
        } else {
            recommendedSongsJList.setVisible(true);
            noRecommendedSongsLabel.setVisible(false);
            for (WAVAudioFile audioFile : recommended) {
                recommendedSongsListModel.addElement(audioFile);
            }
        }
    }
    
    private String formatTime(long microseconds) {
        long seconds = TimeUnit.MICROSECONDS.toSeconds(microseconds);
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        return button;
    }

    private JButton createStyledIconButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        button.setPreferredSize(new Dimension(50, 40));
        return button;
    }

    private JToggleButton createStyledToggleButton(String text, Color activeBackground) {
        JToggleButton toggleButton = new JToggleButton(text);
        toggleButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        toggleButton.setBackground(new Color(50, 50, 50));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setFocusPainted(false);
        toggleButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        toggleButton.setPreferredSize(new Dimension(50, 40));

        toggleButton.addChangeListener(e -> {
            if (toggleButton.isSelected()) {
                toggleButton.setBackground(activeBackground);
            } else {
                toggleButton.setBackground(new Color(50, 50, 50));
            }
        });
        return toggleButton;
    }

    public void displayErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void displayInfoMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 주어진 절대 경로의 이미지를 스케일링하여 ImageIcon으로 반환.
     * 로딩 실패 시에는 기본 커버 이미지를 반환.
     * @param path 이미지 파일의 절대 경로
     * @param width 스케일링할 너비
     * @param height 스케일링할 높이
     * @return 스케일링된 ImageIcon, 로드 실패 시 기본 커버 이미지, 모든 시도 실패 시 null
     */
    private ImageIcon getScaledCoverIcon(String path, int width, int height) {
        try {
            File imageFile = new File(path);
            if (!imageFile.exists()) {
                System.err.println("이미지 파일을 찾을 수 없습니다 (절대 경로): " + path);
                return getScaledDefaultCover(width, height); // 기본 커버 이미지로 대체
            }
            
            ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
            
            if (originalIcon.getImage() == null || originalIcon.getIconWidth() <= 0) {
                 System.err.println("이미지 로드 실패 (원본 이미지 데이터 없음 또는 유효하지 않음, 절대 경로): " + path);
                 return getScaledDefaultCover(width, height); // 기본 커버 이미지로 대체
            }
            
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) { // 파일 접근 권한 등 다른 예외 발생 시
            System.err.println("이미지 스케일링 또는 로드 중 오류 발생 (절대 경로): " + path + " - " + e.getMessage());
            return getScaledDefaultCover(width, height); // 기본 커버 이미지로 대체
        }
    }

    /**
     * 기본 커버 이미지를 로드하고 스케일링하여 반환합니다.
     * @param width 스케일링할 너비
     * @param height 스케일링할 높이
     * @return 스케일링된 기본 커버 ImageIcon, 로드 실패 시 null
     */
    private ImageIcon getScaledDefaultCover(int width, int height) {
        try {
            File defaultCoverFile = new File(ApplicationMain.BASE_RESOURCE_PATH + "images/default_cover.jpg");
            if (defaultCoverFile.exists()) {
                ImageIcon defaultIcon = new ImageIcon(defaultCoverFile.getAbsolutePath());
                Image defaultScaledImage = defaultIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(defaultScaledImage);
            } else {
                System.err.println("기본 커버 이미지 파일도 찾을 수 없습니다: " + defaultCoverFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("기본 커버 이미지 로드 중 예외 발생: " + e.getMessage());
        }
        return null; // 모든 시도 실패 시 최종적으로 null 반환
    }
}