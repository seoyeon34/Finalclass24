package gui;

import audio.MusicFileNotFoundException;
import audio.MusicPlaybackException;
import audio.WAVAudioFile;
import main.ApplicationMain;
import model.Music;
import player.AppMusicPlayer;
import search.SearchCriteria;
import search.SearchManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicPlayerGUI extends JFrame {

    private AppMusicPlayer appMusicPlayer;
    private SearchManager searchManager;
    private List<Music> allMusicDatabase;

    private JPanel backgroundPanel;
    private ImageIcon backgroundImage;

    private JTabbedPane tabbedPane;
    
    private ImagePanel mainPlaybackPanel; 
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
    private JButton removeSelectedButton;
    private JButton clearPlaylistButton;

    private JSlider volumeSlider; 
    private JLabel volumeLabel;   

    private DefaultListModel<WAVAudioFile> playlistListModel;
    private JList<WAVAudioFile> playlistJList;

    private ImagePanel searchTabPanel; 
    private JTextField searchInputField;
    private JComboBox<SearchCriteria> searchTypeComboBox;
    private JButton searchExecuteButton;
    private DefaultTableModel searchResultTableModel;
    private JTable searchResultTable;
    private JLabel searchStatusLabel;
    private JButton addSelectedToPlaylistButton; 

    private ImagePanel likedSongsTabPanel; 
    private DefaultListModel<WAVAudioFile> likedSongsListModel;
    private JList<WAVAudioFile> likedSongsJList;
    private JLabel noLikedSongsLabel;
    private JButton likedPlayButton;
    private JButton likedAddButton;
    private JButton createPlaylistFromLikedButton; 

    private ImagePanel recommendedSongsTabPanel; 
    private DefaultListModel<WAVAudioFile> recommendedSongsListModel;
    private JList<WAVAudioFile> recommendedSongsJList;
    private JLabel noRecommendedSongsLabel;
    private JButton refreshRecommendButton;
    private JButton recommendedPlayButton;
    private JButton recommendedAddButton;

    private Timer playbackTimer;
    private volatile boolean isSliderAdjusting = false; 

    private final Dimension tabButtonSize = new Dimension(150, 40);
    
    class ImagePanel extends JPanel {
        private ImageIcon imageIcon;

        public ImagePanel(String imagePath) {
            this(imagePath, new BorderLayout());
        }
        
        public ImagePanel(String imagePath, LayoutManager layout) {
            super(layout);
            try {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    imageIcon = new ImageIcon(imgFile.getAbsolutePath());
                } else {
                    System.err.println("배경 이미지 파일을 찾을 수 없습니다: " + imagePath);
                    imageIcon = null;
                }
            } catch (Exception e) {
                System.err.println("배경 이미지 로드 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
                imageIcon = null;
            }
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imageIcon != null) {
                g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public MusicPlayerGUI() {
        super("ICTunes 음악 스트리밍");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);

        playlist.AllMusicList.initializeMusicList();
        allMusicDatabase = playlist.AllMusicList.getAllMusic();

        searchManager = new SearchManager();
        appMusicPlayer = new AppMusicPlayer(this, allMusicDatabase); 
        
        loadBackgroundImage();

        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.BLACK);
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
                if(playbackTimer != null) playbackTimer.stop();
                appMusicPlayer.closeAllAudioFiles();
                System.exit(0);
            }
        });

        try {
            appMusicPlayer.setPlaylist(allMusicDatabase);
            updatePlaylistList();
        } catch(MusicPlaybackException e){
            JOptionPane.showMessageDialog(this, "초기 음악 목록 로드 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }

        updatePlayerUI();
        setVisible(true);
    }

    private void loadBackgroundImage() {
        try {
            File bgFile = new File(ApplicationMain.BASE_RESOURCE_PATH + "images/background.jpg");
            if(bgFile.exists()){
                ImageIcon bgIcon = new ImageIcon(bgFile.getAbsolutePath());
                Image scaled = bgIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                backgroundImage = new ImageIcon(scaled);
            } else {
                backgroundImage = null;
            }
        } catch(Exception e){
            backgroundImage = null;
        }
    }

    private void createComponents(){
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("맑은 고딕", Font.BOLD,14));
        tabbedPane.setBackground(new Color(30, 30, 30, 200)); 
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setOpaque(true);
        backgroundPanel.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
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

        createMainPlaybackTab();
        createSearchTab();
        createLikedSongsTab();
        createRecommendedSongsTab();

        tabbedPane.addTab("▶ 재생 / 목록", mainPlaybackPanel);
        tabbedPane.addTab("검색", searchTabPanel);
        tabbedPane.addTab("좋아요 음악", likedSongsTabPanel);
        tabbedPane.addTab("추천 음악", recommendedSongsTabPanel);
    }
    
    private void createMainPlaybackTab(){
        mainPlaybackPanel = new ImagePanel(ApplicationMain.BASE_RESOURCE_PATH + "images/tab_background.jpg", new BorderLayout(10,10));
        mainPlaybackPanel.setBorder(new EmptyBorder(20,20,20,20));

        coverImageLabel = new JLabel();
        coverImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        coverImageLabel.setPreferredSize(new Dimension(250,250));
        coverImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setCoverImage(ApplicationMain.BASE_RESOURCE_PATH + "images/default_cover.jpg");

        JPanel songInfoPanel = new JPanel();
        songInfoPanel.setOpaque(false);
        songInfoPanel.setLayout(new BoxLayout(songInfoPanel, BoxLayout.Y_AXIS));
        songInfoPanel.setBorder(new EmptyBorder(10,0,0,0));

        currentSongTitleLabel=new JLabel("현재 재생곡: -");
        currentSongTitleLabel.setFont(new Font("맑은 고딕",Font.BOLD,22));
        currentSongTitleLabel.setForeground(Color.WHITE);
        currentSongTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        currentSongArtistLabel=new JLabel("가수: -");
        currentSongArtistLabel.setFont(new Font("맑은 고딕",Font.PLAIN,18));
        currentSongArtistLabel.setForeground(Color.LIGHT_GRAY);
        currentSongArtistLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        songInfoPanel.add(currentSongTitleLabel);
        songInfoPanel.add(currentSongArtistLabel);

        JPanel topPanel=new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(coverImageLabel, BorderLayout.CENTER);
        topPanel.add(songInfoPanel, BorderLayout.SOUTH);

        mainPlaybackPanel.add(topPanel,BorderLayout.NORTH);

        JPanel playbackControlsWrapperPanel = new JPanel();
        playbackControlsWrapperPanel.setLayout(new BoxLayout(playbackControlsWrapperPanel, BoxLayout.Y_AXIS));
        playbackControlsWrapperPanel.setOpaque(false);

        // ==== 바(슬라이더) & 타임 패널 ====
        progressBar=new JSlider(0,100);
        progressBar.setOpaque(false);
        progressBar.setForeground(Color.WHITE);
        progressBar.setPreferredSize(new Dimension(800,40)); 
        progressBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isSliderAdjusting = true;
                playbackTimer.stop();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isSliderAdjusting = false;
                if (appMusicPlayer.getCurrentAudioFile() != null) {
                    try{
                        long totalLength = appMusicPlayer.getCurrentAudioFile().getDuration();
                        long newPosition = (long)(totalLength * (progressBar.getValue() / 100.0));
                        appMusicPlayer.getCurrentAudioFile().setPosition(newPosition);
                        if(!appMusicPlayer.isPlaying() && appMusicPlayer.isPaused()){
                            appMusicPlayer.play();
                        }
                    }catch (MusicPlaybackException ex){
                        displayErrorMessage("재생 위치 변경 오류","재생 위치를 변경할 수 없습니다: "+ex.getMessage());
                    }
                }
                playbackTimer.start();
            }
        });

        timeLabel=new JLabel("00:00 / 00:00");
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel progressPanel=new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        progressPanel.add(progressBar,BorderLayout.CENTER);
        progressPanel.add(timeLabel,BorderLayout.SOUTH);

        
        // ==== 볼륨 슬라이더 ====
        volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, 50); 
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.setPreferredSize(new Dimension(20, 80)); 
        volumeSlider.setMinimumSize(new Dimension(20, 80)); 
        volumeSlider.setMaximumSize(new Dimension(20, 80)); 
        volumeSlider.setPaintTicks(false); 
        volumeSlider.setPaintLabels(false); 
        volumeSlider.setToolTipText("음량 조절");
        volumeSlider.addChangeListener(e -> {
            if (!volumeSlider.getValueIsAdjusting()) { 
                float volume = volumeSlider.getValue() / 100.0f;
                appMusicPlayer.setVolume(volume);
                volumeLabel.setText(volumeSlider.getValue() + "%");
                updatePlayerUI(); 
            } else {
                volumeLabel.setText(volumeSlider.getValue() + "%"); 
            }
        });

        volumeLabel = new JLabel("50%", SwingConstants.CENTER); 
        volumeLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setPreferredSize(new Dimension(50, 20)); 
        volumeLabel.setMinimumSize(new Dimension(50, 20));
        volumeLabel.setMaximumSize(new Dimension(50, 20));

        JPanel volumeControlPanel = new JPanel(); 
        volumeControlPanel.setOpaque(false);
        volumeControlPanel.setLayout(new BoxLayout(volumeControlPanel, BoxLayout.Y_AXIS)); 
        volumeControlPanel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        volumeControlPanel.add(volumeSlider);
        volumeControlPanel.add(volumeLabel);
        volumeControlPanel.setPreferredSize(new Dimension(50, 100)); 
        volumeControlPanel.setMinimumSize(new Dimension(50, 100));
        volumeControlPanel.setMaximumSize(new Dimension(50, 100));


        // ==== 바(슬라이더) + 볼륨패널 ====
        JPanel progressAndVolumePanel = new JPanel();
        progressAndVolumePanel.setOpaque(false);
        progressAndVolumePanel.setLayout(new BoxLayout(progressAndVolumePanel, BoxLayout.X_AXIS));
        progressAndVolumePanel.add(progressPanel); 
        progressAndVolumePanel.add(Box.createRigidArea(new Dimension(10, 0))); 
        progressAndVolumePanel.add(volumeControlPanel); 
        progressAndVolumePanel.add(Box.createHorizontalGlue()); 


        JPanel controlButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlButtonPanel.setBackground(new Color(0, 0, 0, 150));
        controlButtonPanel.setOpaque(true);
        
        Dimension commonButtonSize = new Dimension(60,40);
        Dimension removeButtonSize = new Dimension(100, 40);

        
        // 모든 버튼 객체 생성 및 할당
        prevButton = createStyledIconButton("◀◀", new Color(50, 50, 50));
        playButton = createStyledIconButton("▶", new Color(0, 150, 0));
        pauseButton = createStyledIconButton("❚❚", new Color(200, 150, 0));
        stopButton = createStyledIconButton("■", new Color(150, 0, 0));
        nextButton = createStyledIconButton("▶▶", new Color(50, 50, 50));
        shuffleButton = createStyledToggleButton("셔플", new Color(0, 100, 150)); 
        repeatButton = createStyledToggleButton("반복", new Color(0, 100, 150));
        likeButton = createStyledToggleButton("♡", new Color(180, 0, 0)); 
        removeSelectedButton = createStyledButton("선택 곡 제거", new Color(150, 50, 0));
        clearPlaylistButton = createStyledButton("모든 곡 삭제", new Color(150, 0, 0));
        
        // 생성된 버튼 크기 설정
        prevButton.setPreferredSize(commonButtonSize);
        playButton.setPreferredSize(commonButtonSize);
        pauseButton.setPreferredSize(commonButtonSize);
        stopButton.setPreferredSize(commonButtonSize);
        nextButton.setPreferredSize(commonButtonSize);
        shuffleButton.setPreferredSize(commonButtonSize);
        repeatButton.setPreferredSize(commonButtonSize);
        likeButton.setPreferredSize(commonButtonSize);
        removeSelectedButton.setPreferredSize(removeButtonSize);
        clearPlaylistButton.setPreferredSize(removeButtonSize);

        controlButtonPanel.add(prevButton);
        controlButtonPanel.add(playButton);
        controlButtonPanel.add(pauseButton);
        controlButtonPanel.add(stopButton);
        controlButtonPanel.add(nextButton);
        controlButtonPanel.add(shuffleButton);
        controlButtonPanel.add(repeatButton);
        controlButtonPanel.add(likeButton);
        controlButtonPanel.add(removeSelectedButton);
        controlButtonPanel.add(clearPlaylistButton);

        playbackControlsWrapperPanel.add(progressAndVolumePanel); 
        playbackControlsWrapperPanel.add(controlButtonPanel);

        mainPlaybackPanel.add(playbackControlsWrapperPanel, BorderLayout.CENTER);

        playlistListModel=new DefaultListModel<>();
        playlistJList=new JList<>(playlistListModel);
        playlistJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        playlistJList.setFont(new Font("맑은 고딕",Font.PLAIN,16));
        playlistJList.setBackground(new Color(0,0,0,150));
        playlistJList.setForeground(Color.WHITE);
        playlistJList.setSelectionBackground(new Color(50,50,50,180));
        playlistJList.setSelectionForeground(Color.CYAN);
        playlistJList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,Object value,int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                if(value instanceof WAVAudioFile){
                    WAVAudioFile wavFile=(WAVAudioFile)value;
                    setText(wavFile.getTitle()+" - "+wavFile.getArtist()+(wavFile.isLiked()?" ♥":""));
                }
                
                
                if (index == appMusicPlayer.getCurrentSongIndex() && appMusicPlayer.isPlaying()) {
                    setForeground(Color.CYAN);
                } else if (isSelected) {
                    setBackground(new Color(50, 50, 50, 180));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(new Color(0, 0, 0, 0));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });

        JScrollPane playlistScrollPane=new JScrollPane(playlistJList);
        playlistScrollPane.setOpaque(false);
        playlistScrollPane.getViewport().setOpaque(false);
        playlistScrollPane.setPreferredSize(new Dimension(900,150));
        mainPlaybackPanel.add(playlistScrollPane,BorderLayout.PAGE_END);
    }

    private void createSearchTab() {
        searchTabPanel = new ImagePanel(ApplicationMain.BASE_RESOURCE_PATH + "images/tab_background.jpg", new BorderLayout(10,10));
        searchTabPanel.setBorder(new EmptyBorder(20,20,20,20));

        JPanel searchInputPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        searchInputPanel.setOpaque(false);

        searchInputField=new JTextField(25);
        searchInputField.setFont(new Font("맑은 고딕",Font.PLAIN,14));
        searchInputField.setPreferredSize(new Dimension(250,30));

        searchTypeComboBox=new JComboBox<>(SearchCriteria.values());
        searchTypeComboBox.setFont(new Font("맑은 고딕",Font.PLAIN,14));
        searchTypeComboBox.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,Object value,int index, boolean isSelected, boolean cellHasFocus){
                super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus); 
                if(value instanceof SearchCriteria){
                    setText(((SearchCriteria)value).getDisplayName());
                }
                setBackground(new Color(0,0,0,100));
                setForeground(Color.WHITE);
                return this;
            }
        });

        searchExecuteButton=createStyledButton("검색",new Color(0,120,180));
        searchExecuteButton.setPreferredSize(new Dimension(80,30));
        
        addSelectedToPlaylistButton = createStyledButton("선택한 곡 플레이리스트 추가", new Color(0, 100, 0)); 
        addSelectedToPlaylistButton.setPreferredSize(tabButtonSize);

        searchInputPanel.add(new JLabel("검색어:"));
        searchInputPanel.add(searchInputField);
        searchInputPanel.add(new JLabel("유형:"));
        searchInputPanel.add(searchTypeComboBox);
        searchInputPanel.add(searchExecuteButton);
        searchInputPanel.add(addSelectedToPlaylistButton); 

        searchTabPanel.add(searchInputPanel,BorderLayout.NORTH);

        String[] columnNames={"제목","가수","장르","발매년도","앨범"};
        searchResultTableModel=new DefaultTableModel(columnNames,0){
            @Override
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };

        searchResultTable=new JTable(searchResultTableModel);
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultTable.setFont(new Font("맑은 고딕",Font.PLAIN,14));
        searchResultTable.setBackground(new Color(0,0,0,150));
        searchResultTable.setForeground(Color.WHITE);
        searchResultTable.setSelectionBackground(new Color(50,50,50,180));
        searchResultTable.setSelectionForeground(Color.CYAN);
        searchResultTable.setRowHeight(25);

        searchResultTable.getTableHeader().setFont(new Font("맑은 고딕",Font.BOLD,14));
        searchResultTable.getTableHeader().setBackground(new Color(30,30,30,150));
        searchResultTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setOpaque(false);
        for(int i=0;i<searchResultTable.getColumnCount();i++){
            searchResultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        searchResultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(new Color(50, 50, 50, 180));
                    setForeground(Color.CYAN);
                } else {
                    setBackground(new Color(0, 0, 0, 0));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });


        JScrollPane searchScrollPane=new JScrollPane(searchResultTable);
        searchScrollPane.setOpaque(false);
        searchScrollPane.getViewport().setOpaque(false);
        searchTabPanel.add(searchScrollPane,BorderLayout.CENTER);

        searchStatusLabel=new JLabel("검색 결과를 표시합니다.",SwingConstants.CENTER);
        searchStatusLabel.setFont(new Font("맑은 고딕",Font.PLAIN,14));
        searchStatusLabel.setForeground(Color.LIGHT_GRAY);
        searchTabPanel.add(searchStatusLabel,BorderLayout.SOUTH);
    }

    private void createLikedSongsTab(){
        likedSongsTabPanel = new ImagePanel(ApplicationMain.BASE_RESOURCE_PATH + "images/tab_background.jpg", new BorderLayout(10,10));
        likedSongsTabPanel.setBorder(new EmptyBorder(20,20,20,20));

        noLikedSongsLabel=new JLabel("좋아요 한 곡이 없습니다.",SwingConstants.CENTER);
        noLikedSongsLabel.setFont(new Font("맑은 고딕",Font.BOLD,16));
        noLikedSongsLabel.setForeground(Color.GRAY);
        likedSongsTabPanel.add(noLikedSongsLabel,BorderLayout.NORTH);

        likedSongsListModel=new DefaultListModel<>();
        likedSongsJList=new JList<>(likedSongsListModel);
        likedSongsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        likedSongsJList.setFont(new Font("맑은 고딕",Font.PLAIN,16));
        likedSongsJList.setBackground(new Color(0,0,0,150));
        likedSongsJList.setForeground(Color.WHITE);
        likedSongsJList.setSelectionBackground(new Color(50,50,50,180));
        likedSongsJList.setSelectionForeground(Color.RED);
        likedSongsJList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus){ 
                super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                if(value instanceof WAVAudioFile){
                    WAVAudioFile wavFile=(WAVAudioFile)value;
                    setText(wavFile.getTitle()+" - "+wavFile.getArtist());
                }
                if(isSelected){
                    setBackground(new Color(50,50,50,180));
                    setForeground(Color.RED);
                } else {
                    setBackground(new Color(0,0,0,0));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });

        JScrollPane likedScrollPane=new JScrollPane(likedSongsJList);
        likedScrollPane.setOpaque(false);
        likedScrollPane.getViewport().setOpaque(false);
        likedSongsTabPanel.add(likedScrollPane,BorderLayout.CENTER);

        JPanel likedButtonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        likedButtonPanel.setBackground(new Color(0, 0, 0, 150));
        likedButtonPanel.setOpaque(true); 

        likedPlayButton=createStyledButton("선택한 곡 재생",new Color(0,150,0));
        likedAddButton=createStyledButton("선택한 곡 재생목록 추가",new Color(50,50,50));
        createPlaylistFromLikedButton = createStyledButton("좋아요한 곡으로 플레이리스트 생성", new Color(0, 100, 150)); 

        likedPlayButton.setPreferredSize(tabButtonSize);
        likedAddButton.setPreferredSize(tabButtonSize);
        createPlaylistFromLikedButton.setPreferredSize(new Dimension(250, 40)); 

        likedButtonPanel.add(likedPlayButton);
        likedButtonPanel.add(likedAddButton);
        likedButtonPanel.add(createPlaylistFromLikedButton); 
        likedSongsTabPanel.add(likedButtonPanel,BorderLayout.SOUTH);
    }

    private void createRecommendedSongsTab() {
        recommendedSongsTabPanel = new ImagePanel(ApplicationMain.BASE_RESOURCE_PATH + "images/tab_background.jpg", new BorderLayout(10,10));
        recommendedSongsTabPanel.setBorder(new EmptyBorder(20,20,20,20));

        JPanel topPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        topPanel.setOpaque(false);
        JLabel recommendTitleLabel=new JLabel("현재 곡 기반 추천 음악");
        recommendTitleLabel.setFont(new Font("맑은 고딕",Font.BOLD,16));
        recommendTitleLabel.setForeground(Color.WHITE);
        topPanel.add(recommendTitleLabel);
        refreshRecommendButton=createStyledButton("새로고침",new Color(80,80,80));
        refreshRecommendButton.setPreferredSize(new Dimension(100,30));
        topPanel.add(refreshRecommendButton);
        recommendedSongsTabPanel.add(topPanel,BorderLayout.NORTH);

        noRecommendedSongsLabel=new JLabel("추천 음악이 없습니다. 다른 곡을 재생해보세요.",SwingConstants.CENTER);
        noRecommendedSongsLabel.setFont(new Font("맑은 고딕",Font.BOLD,16));
        noRecommendedSongsLabel.setForeground(Color.GRAY);
        recommendedSongsTabPanel.add(noRecommendedSongsLabel,BorderLayout.CENTER);

        recommendedSongsListModel=new DefaultListModel<>();
        recommendedSongsJList=new JList<>(recommendedSongsListModel);
        recommendedSongsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recommendedSongsJList.setFont(new Font("맑은 고딕",Font.PLAIN,16));
        recommendedSongsJList.setBackground(new Color(0,0,0,150));
        recommendedSongsJList.setForeground(Color.WHITE);
        recommendedSongsJList.setSelectionBackground(new Color(50,50,50,180));
        recommendedSongsJList.setSelectionForeground(Color.GREEN);

        recommendedSongsJList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean isSelected,boolean cellHasFocus){ 
                super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                if(value instanceof WAVAudioFile){
                    WAVAudioFile wavFile=(WAVAudioFile)value;
                    setText(wavFile.getTitle()+" - "+wavFile.getArtist()+" ("+wavFile.getGenre()+")");
                }
                if(isSelected){
                    setBackground(new Color(50,50,50,180));
                    setForeground(Color.GREEN);
                }else{
                    setBackground(new Color(0,0,0,0));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });

        JScrollPane recommendedScrollPane=new JScrollPane(recommendedSongsJList);
        recommendedScrollPane.setOpaque(false);
        recommendedScrollPane.getViewport().setOpaque(false);
        recommendedSongsTabPanel.add(recommendedScrollPane,BorderLayout.CENTER);

        JPanel recommendButtonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        recommendButtonPanel.setBackground(new Color(0, 0, 0, 150));
        recommendButtonPanel.setOpaque(true);

        recommendedPlayButton=createStyledButton("선택한 곡 재생",new Color(0,150,0));
        recommendedAddButton=createStyledButton("선택한 곡 재생목록 추가",new Color(50,50,50));

        recommendedPlayButton.setPreferredSize(tabButtonSize);
        recommendedAddButton.setPreferredSize(tabButtonSize);
        
        recommendButtonPanel.add(recommendedPlayButton);
        recommendButtonPanel.add(recommendedAddButton);
        recommendedSongsTabPanel.add(recommendButtonPanel,BorderLayout.SOUTH);
    }

    //오류 모음
    private void addEventListeners(){
        playButton.addActionListener(e -> {
            try{
                appMusicPlayer.play();
                updatePlayerUI();
            }catch (MusicPlaybackException ex){
                displayErrorMessage("재생 오류",ex.getMessage());
            }
        });

        pauseButton.addActionListener(e -> {
            try{
                appMusicPlayer.pause();
                updatePlayerUI();
            }catch (MusicPlaybackException ex){
                displayErrorMessage("일시정지 오류",ex.getMessage());
            }
        });

        stopButton.addActionListener(e -> {
            try{
                appMusicPlayer.stop();
                updatePlayerUI();
            }catch (MusicPlaybackException ex){
                displayErrorMessage("정지 오류",ex.getMessage());
            }
        });

        prevButton.addActionListener(e -> {
            try{
                appMusicPlayer.previous();
                updatePlayerUI();
            }catch (MusicPlaybackException ex){
                displayErrorMessage("이전 곡 오류",ex.getMessage());
            }
        });

        nextButton.addActionListener(e -> {
            try{
                appMusicPlayer.next();
                updatePlayerUI();
            }catch (MusicPlaybackException ex){
                displayErrorMessage("다음 곡 오류",ex.getMessage());
            }
        });

        // 셔플 & 반복
        shuffleButton.addActionListener(e -> {
            appMusicPlayer.toggleShuffle();
            updatePlayerUI();
            displayInfoMessage("셔플 모드","셔플 모드: "+(appMusicPlayer.isShuffleMode()?"ON":"OFF"));
        });

        repeatButton.addActionListener(e -> {
            appMusicPlayer.toggleRepeat();
            updatePlayerUI();
            displayInfoMessage("반복 모드","반복 모드: "+(appMusicPlayer.isRepeatMode()?"ON":"OFF"));
        });

        
        
        likeButton.addActionListener(e -> {
            WAVAudioFile current=appMusicPlayer.getCurrentAudioFile();
            if(current!=null){
                appMusicPlayer.toggleLike(current);
                updatePlayerUI(); 
                updateLikedSongsList(); 
            }else{
                displayInfoMessage("좋아요","현재 재생 중인 곡이 없습니다.");
                likeButton.setSelected(false);
            }
        });
        
        removeSelectedButton.addActionListener(e -> {
            List<WAVAudioFile> selectedSongs = playlistJList.getSelectedValuesList();
            if (selectedSongs.isEmpty()) {
                displayInfoMessage("재생 목록 제거", "제거할 곡을 선택해주세요.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    selectedSongs.size() + "개의 곡을 재생 목록에서 제거하시겠습니까?", "선택 곡 제거 확인",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) { 
                try {
                    for (WAVAudioFile song : selectedSongs) {
                        appMusicPlayer.removeSong(song);
                    }
                    updatePlaylistList();
                    updatePlayerUI();
                    displayInfoMessage("재생 목록 제거", selectedSongs.size() + "개의 곡을 제거했습니다.");
                } catch (MusicPlaybackException ex) {
                    displayErrorMessage("재생 목록 제거 오류", "선택한 곡을 제거할 수 없습니다: " + ex.getMessage());
                }
            }
        });

        clearPlaylistButton.addActionListener(e -> {
            if (appMusicPlayer.getPlaylist().isEmpty()) {
                displayInfoMessage("재생 목록 제거", "재생 목록이 이미 비어있습니다.");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                    "정말 모든 곡을 재생 목록에서 제거하시겠습니까?", "모든 곡 삭제 확인",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                
            	try {
                    appMusicPlayer.clearPlaylist();
                    updatePlaylistList();
                    updatePlayerUI();
                    displayInfoMessage("재생 목록 제거", "모든 곡을 제거했습니다.");
                } catch (Exception ex) { 
                    displayErrorMessage("재생 목록 제거 오류", "모든 곡을 제거할 수 없습니다: " + ex.getMessage());
                }
            }
        });


        playlistJList.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    int index=playlistJList.locationToIndex(e.getPoint());
                    if(index!=-1){
                        try{
                            appMusicPlayer.play(index);
                            updatePlayerUI();
                        }catch (MusicPlaybackException ex){
                            displayErrorMessage("재생 오류",ex.getMessage());
                        }
                    }
                }
            }
        });

        coverImageLabel.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                WAVAudioFile current=appMusicPlayer.getCurrentAudioFile();
                if(current!=null){
                    new LyricsDisplayDialog(MusicPlayerGUI.this,current.getTitle(),current.getArtist());
                }else{
                    displayInfoMessage("가사","현재 재생 중인 곡이 없습니다.");
                }
            }
        });

        searchExecuteButton.addActionListener(e -> performSearch());
        searchInputField.addActionListener(e -> performSearch());
        
        
        // 검색 탭
        addSelectedToPlaylistButton.addActionListener(e -> {
            int selectedRow = searchResultTable.getSelectedRow();
            if (selectedRow == -1) {
                displayInfoMessage("플레이리스트 추가", "플레이리스트에 추가할 곡을 선택해주세요.");
                return;
            }

            String title = (String) searchResultTableModel.getValueAt(selectedRow, 0);
            String artist = (String) searchResultTableModel.getValueAt(selectedRow, 1);
            Music selectedMusic = allMusicDatabase.stream()
                    .filter(m -> m.getTitle().equals(title) && m.getArtist().equals(artist))
                    .findFirst()
                    .orElse(null);

            if (selectedMusic != null) {
                try {
                    WAVAudioFile wavFile = new WAVAudioFile(selectedMusic);
                    appMusicPlayer.addSong(wavFile); 
                    updatePlaylistList();
                    displayInfoMessage("플레이리스트 추가", "'" + selectedMusic.getTitle() + "'을(를) 플레이리스트에 추가했습니다.");
                } catch (MusicPlaybackException ex) {
                    displayErrorMessage("플레이리스트 추가 오류", "곡을 추가할 수 없습니다: " + ex.getMessage());
                } catch (MusicFileNotFoundException ex) {
                    displayErrorMessage("파일 로드 오류", "음악 파일을 로드할 수 없습니다: " + ex.getMessage());
                }
            }
        });

        searchResultTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    int selectedRow=searchResultTable.getSelectedRow();
                    if(selectedRow!=-1){
                        String title=(String)searchResultTableModel.getValueAt(selectedRow,0);
                        String artist=(String)searchResultTableModel.getValueAt(selectedRow,1);
                        Music selectedMusic=allMusicDatabase.stream()
                                .filter(m->m.getTitle().equals(title)&&m.getArtist().equals(artist))
                                .findFirst()
                                .orElse(null);
                        if(selectedMusic!=null){
                            try{
                                WAVAudioFile wavFile=new WAVAudioFile(selectedMusic);
                                try{
                                    if(!appMusicPlayer.getPlaylist().contains(wavFile)){
                                        appMusicPlayer.addSong(wavFile);
                                    }
                                    appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(wavFile));
                                    updatePlaylistList();
                                    updatePlayerUI();
                                    tabbedPane.setSelectedIndex(0);
                                    displayInfoMessage("음악 재생",selectedMusic.getTitle()+"을(를) 재생합니다.");
                                }catch (MusicPlaybackException ex){
                                    displayErrorMessage("재생/추가 오류",ex.getMessage());
                                }
                            }catch (MusicFileNotFoundException | MusicPlaybackException ex){
                                displayErrorMessage("재생 오류","선택한 음악을 로드하거나 재생할 수 없습니다: "+ex.getMessage());
                            }
                        }
                    }
                }
            }
        });

        likedPlayButton.addActionListener(e -> {
            int selectedIndex=likedSongsJList.getSelectedIndex();
            if(selectedIndex!=-1){
                Object selectedValue=likedSongsListModel.getElementAt(selectedIndex);
                if(selectedValue instanceof WAVAudioFile){
                    WAVAudioFile selectedLikedSong=(WAVAudioFile)selectedValue;
                    try{
                        WAVAudioFile currentPlaylistVersion = findOrCreateWAVAudioFile(selectedLikedSong.getMusicData());
                        
                        if(!appMusicPlayer.getPlaylist().contains(currentPlaylistVersion)){
                            appMusicPlayer.addSong(currentPlaylistVersion);
                        }
                        appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(currentPlaylistVersion));
                        updatePlaylistList();
                        updatePlayerUI();
                        tabbedPane.setSelectedIndex(0);
                    }catch (MusicPlaybackException ex){
                        displayErrorMessage("재생 오류",ex.getMessage());
                    }
                }else{
                    displayInfoMessage("알림","선택된 항목은 재생할 수 없습니다.");
                }
            }else{
                displayInfoMessage("알림","재생할 곡을 선택해주세요.");
            }
        });

        likedAddButton.addActionListener(e -> {
            int selectedIndex=likedSongsJList.getSelectedIndex();
            if(selectedIndex!=-1){
                Object selectedValue=likedSongsListModel.getElementAt(selectedIndex);
                if(selectedValue instanceof WAVAudioFile){
                    WAVAudioFile selectedLikedSong=(WAVAudioFile)selectedValue;
                    try{
                        WAVAudioFile currentPlaylistVersion = findOrCreateWAVAudioFile(selectedLikedSong.getMusicData());

                        if(!appMusicPlayer.getPlaylist().contains(currentPlaylistVersion)){
                            appMusicPlayer.addSong(currentPlaylistVersion);
                            updatePlaylistList();
                            displayInfoMessage("재생 목록 추가",selectedLikedSong.getTitle()+"을(를) 재생 목록에 추가했습니다.");
                        }else{
                            displayInfoMessage("알림",selectedLikedSong.getTitle()+"은(는) 이미 재생 목록에 있습니다.");
                        }
                    }catch (MusicPlaybackException ex){
                        displayErrorMessage("재생 목록 추가 오류",ex.getMessage());
                    }
                }else{
                    displayInfoMessage("알림","선택된 항목은 재생 목록에 추가할 수 없습니다.");
                }
            }else{
                displayInfoMessage("알림","재생 목록에 추가할 곡을 선택해주세요.");
            }
        });
        
        createPlaylistFromLikedButton.addActionListener(e -> {
            List<WAVAudioFile> likedSongs = appMusicPlayer.getLikedSongs();
            if (likedSongs.isEmpty()) {
                displayInfoMessage("플레이리스트 생성", "좋아요한 곡이 없습니다.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "현재 플레이리스트를 삭제하고 좋아요한 곡들로 새로운 플레이리스트를 만드시겠습니까?",
                    "플레이리스트 생성 확인", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    appMusicPlayer.stop(); 
                    appMusicPlayer.clearPlaylist(); 
                    
                    List<Music> likedMusicData = new ArrayList<>();
                    for(WAVAudioFile wavFile : likedSongs) {
                        likedMusicData.add(wavFile.getMusicData()); 
                    }
                    appMusicPlayer.setPlaylist(likedMusicData); 
                    
                    updatePlaylistList(); 
                    updatePlayerUI(); 
                    tabbedPane.setSelectedIndex(0); 

                    if (!appMusicPlayer.getPlaylist().isEmpty()) {
                        appMusicPlayer.play(0); 
                        displayInfoMessage("플레이리스트 생성", "좋아요한 곡들로 플레이리스트를 구성하고 재생을 시작합니다.");
                    } else {
                        displayInfoMessage("플레이리스트 생성", "좋아요한 곡들로 플레이리스트를 구성했습니다.");
                    }

                } catch (MusicPlaybackException ex) {
                    displayErrorMessage("플레이리스트 생성 오류", "좋아요한 곡으로 플레이리스트를 생성할 수 없습니다: " + ex.getMessage());
                }
            }
        });


        refreshRecommendButton.addActionListener(e -> updateRecommendedSongsList());

        recommendedPlayButton.addActionListener(e -> {
            int selectedIndex=recommendedSongsJList.getSelectedIndex();
            if(selectedIndex!=-1){
                Object selectedValue=recommendedSongsListModel.getElementAt(selectedIndex);
                if(selectedValue instanceof WAVAudioFile){
                    WAVAudioFile selectedRecommendedSong=(WAVAudioFile)selectedValue;
                    try{
                        WAVAudioFile currentPlaylistVersion = findOrCreateWAVAudioFile(selectedRecommendedSong.getMusicData());

                        if(!appMusicPlayer.getPlaylist().contains(currentPlaylistVersion)){
                            appMusicPlayer.addSong(currentPlaylistVersion);
                        }
                        appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(currentPlaylistVersion));
                        updatePlaylistList();
                        updatePlayerUI();
                        tabbedPane.setSelectedIndex(0);
                    }catch (MusicPlaybackException ex){
                        displayErrorMessage("재생 오류",ex.getMessage());
                    }
                }else{
                    displayInfoMessage("알림","선택된 항목은 재생할 수 없습니다.");
                }
            }else{
                displayInfoMessage("알림","재생할 곡을 선택해주세요.");
            }
        });

        recommendedAddButton.addActionListener(e -> {
            int selectedIndex=recommendedSongsJList.getSelectedIndex();
            if(selectedIndex!=-1){
                Object selectedValue=recommendedSongsListModel.getElementAt(selectedIndex);
                if(selectedValue instanceof WAVAudioFile){
                    WAVAudioFile selectedRecommendedSong=(WAVAudioFile)selectedValue;
                    try{
                        WAVAudioFile currentPlaylistVersion = findOrCreateWAVAudioFile(selectedRecommendedSong.getMusicData());

                        if(!appMusicPlayer.getPlaylist().contains(currentPlaylistVersion)){
                            appMusicPlayer.addSong(currentPlaylistVersion);
                            updatePlaylistList();
                            displayInfoMessage("재생 목록 추가",selectedRecommendedSong.getTitle()+"을(를) 재생 목록에 추가했습니다.");
                        }else{
                            displayInfoMessage("알림",selectedRecommendedSong.getTitle()+"은(는) 이미 재생 목록에 있습니다.");
                        }
                    }catch (MusicPlaybackException ex){
                        displayErrorMessage("재생 목록 추가 오류",ex.getMessage());
                    }
                }else{
                    displayInfoMessage("알림","선택된 항목은 재생 목록에 추가할 수 없습니다.");
                }
            }else{
                displayInfoMessage("알림","재생 목록에 추가할 곡을 선택해주세요.");
            }
        });

        recommendedSongsJList.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    int index=recommendedSongsJList.locationToIndex(e.getPoint());
                    if(index!=-1){
                        Object selectedValue=recommendedSongsListModel.getElementAt(index);
                        if(selectedValue instanceof WAVAudioFile){
                            WAVAudioFile selectedRecommendedSong=(WAVAudioFile)selectedValue;
                            try{
                                WAVAudioFile currentPlaylistVersion = findOrCreateWAVAudioFile(selectedRecommendedSong.getMusicData());

                                if(!appMusicPlayer.getPlaylist().contains(currentPlaylistVersion)){
                                    appMusicPlayer.addSong(currentPlaylistVersion);
                                }
                                appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(currentPlaylistVersion));
                                updatePlaylistList();
                                updatePlayerUI();
                                tabbedPane.setSelectedIndex(0);
                            }catch (MusicPlaybackException ex){
                                displayErrorMessage("재생 오류",ex.getMessage());
                            }
                        }
                    }
                }
            }
        });

        likedSongsJList.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    int index=likedSongsJList.locationToIndex(e.getPoint());
                    if(index!=-1){
                        Object selectedValue=likedSongsListModel.getElementAt(index);
                        if(selectedValue instanceof WAVAudioFile){
                            WAVAudioFile selectedLikedSong=(WAVAudioFile)selectedValue;
                            try{
                                WAVAudioFile currentPlaylistVersion = findOrCreateWAVAudioFile(selectedLikedSong.getMusicData());
                                
                                if(!appMusicPlayer.getPlaylist().contains(currentPlaylistVersion)){
                                    appMusicPlayer.addSong(currentPlaylistVersion);
                                }
                                appMusicPlayer.play(appMusicPlayer.getPlaylist().indexOf(currentPlaylistVersion));
                                updatePlaylistList();
                                updatePlayerUI();
                                tabbedPane.setSelectedIndex(0);
                            }catch (MusicPlaybackException ex){
                                displayErrorMessage("재생 오류",ex.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }

    private WAVAudioFile findOrCreateWAVAudioFile(Music music) {
    	
        // 이미 플레이리스트에 해당 데이터를 가진 WAVAudioFile가 있는지 확인
        for (WAVAudioFile existingFile : appMusicPlayer.getPlaylist()) {
            if (existingFile.getMusicData().equals(music)) { 
                return existingFile;
            }
        }
        
        // 플레이리스트에 없으면 새로 생성 및 반환 
        try {
            WAVAudioFile newFile = new WAVAudioFile(music);
            newFile.addLineListener(appMusicPlayer); 
            newFile.setVolume(appMusicPlayer.getVolume()); 
            return newFile;
        } catch (MusicFileNotFoundException | MusicPlaybackException e) {
            displayErrorMessage("파일 로드 오류", "음악 파일을 로드할 수 없습니다: " + e.getMessage());
            return null; 
        }
    }

    private void performSearch(){
        String keyword=searchInputField.getText().trim();
        SearchCriteria searchType=(SearchCriteria)searchTypeComboBox.getSelectedItem();

        List<Music> results=new java.util.ArrayList<>();

        if(keyword.isEmpty() && searchType!=SearchCriteria.ALL){
            displayInfoMessage("검색 알림","검색어를 입력해주세요. ('통합' 검색은 검색어 없이 전체 리스트 조회)");
            searchResultTableModel.setRowCount(0);
            searchStatusLabel.setText("검색 결과: 0개");
            return;
        }

        List<Music> currentSearchScope=allMusicDatabase; 

        try{
            switch(searchType){
                case TITLE:
                    results=searchManager.searchByTitle(keyword,currentSearchScope);
                    break;
                case ARTIST:
                    results=searchManager.searchByArtist(keyword,currentSearchScope);
                    break;
                case GENRE:
                    results=searchManager.searchByGenre(keyword,currentSearchScope);
                    break;
                case RELEASE_YEAR:
                    try{
                        int year=Integer.parseInt(keyword);
                        results=searchManager.searchByReleaseYear(year,currentSearchScope);
                    }catch(NumberFormatException ex){
                        displayErrorMessage("입력 오류","발매년도 검색은 숫자만 입력할 수 있습니다.");
                        return;
                    }
                    break;
                case ALL:
                    if(keyword.isEmpty()){
                        results=currentSearchScope;
                    }else{
                        results=searchManager.searchAll(keyword,currentSearchScope);
                    }
                    break;
            }
        }catch(Exception ex){
            displayErrorMessage("검색 오류","검색 중 오류가 발생했습니다: "+ex.getMessage());
        }
        updateSearchResultsTable(results);
    }

    private void initPlaybackTimer(){
        playbackTimer=new Timer(100,e->{
            if (isSliderAdjusting) { 
                return;
            }

            if(appMusicPlayer.isPlaying()){
                WAVAudioFile current=appMusicPlayer.getCurrentAudioFile();
                if(current!=null){
                    long currentPos=current.getCurrentPosition();
                    long totalLength=current.getDuration();

                    if(totalLength>0){
                        int progress=(int)((double)currentPos/totalLength*100);
                        progressBar.setValue(progress); 
                    }else{
                        progressBar.setValue(0);
                    }

                    timeLabel.setText(formatTime(currentPos)+" / "+formatTime(totalLength));
                }
            }else{
                if(!appMusicPlayer.isPaused()){
                    progressBar.setValue(0);
                    timeLabel.setText("00:00 / 00:00");
                }
            }
        });
        playbackTimer.start();
    }

    public void updatePlayerUI(){
        WAVAudioFile current=appMusicPlayer.getCurrentAudioFile();

        if(current==null){
            currentSongTitleLabel.setText("현재 재생곡: -");
            currentSongArtistLabel.setText("가수: -");
            setCoverImage(ApplicationMain.BASE_RESOURCE_PATH+"images/default_cover.jpg");
            progressBar.setValue(0);
            timeLabel.setText("00:00 / 00:00");
            
            playButton.setEnabled(true); 
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            likeButton.setEnabled(false);
            likeButton.setText("♡");
            likeButton.setSelected(false);
            volumeSlider.setValue((int)(appMusicPlayer.getVolume() * 100)); 
            volumeLabel.setText((int)(appMusicPlayer.getVolume() * 100) + "%");
        } else {
            currentSongTitleLabel.setText(current.getTitle());
            currentSongArtistLabel.setText(current.getArtist());
            setCoverImage(current.getCoverPath()); 
            
           
            playButton.setEnabled(!appMusicPlayer.isPlaying()); 
            pauseButton.setEnabled(appMusicPlayer.isPlaying()); 
            stopButton.setEnabled(appMusicPlayer.isPlaying() || appMusicPlayer.isPaused()); 

            
            prevButton.setEnabled(true);
            nextButton.setEnabled(true);
            likeButton.setEnabled(true);
            likeButton.setSelected(current.isLiked());
            likeButton.setText(current.isLiked() ? "♥" : "♡");

            
            volumeSlider.setValue((int)(appMusicPlayer.getVolume() * 100)); 
            volumeLabel.setText((int)(appMusicPlayer.getVolume() * 100) + "%");

            
            if (!isSliderAdjusting) { 
                long currentPos = current.getCurrentPosition();
                long totalLength = current.getDuration();
                if (totalLength > 0) {
                    int progress = (int) ((double) currentPos / totalLength * 100);
                    progressBar.setValue(progress);
                } else {
                    progressBar.setValue(0);
                }
                timeLabel.setText(formatTime(currentPos)+" / "+formatTime(totalLength));
            }
        }

        shuffleButton.setSelected(appMusicPlayer.isShuffleMode());
        repeatButton.setSelected(appMusicPlayer.isRepeatMode());

        
        int currentIdx = appMusicPlayer.getCurrentSongIndex();
        if (currentIdx != -1 && currentIdx < playlistListModel.getSize()) { 
            playlistJList.setSelectedIndex(currentIdx);
            playlistJList.ensureIndexIsVisible(currentIdx); 
        } else {
            playlistJList.clearSelection(); 
        }

        
        playlistJList.repaint();

        if(tabbedPane.getSelectedIndex()==2)updateLikedSongsList();
        if(tabbedPane.getSelectedIndex()==3)updateRecommendedSongsList();
    }

    public void updatePlaylistList(){ 
        playlistListModel.clear();
        for(WAVAudioFile audioFile:appMusicPlayer.getPlaylist()){
            playlistListModel.addElement(audioFile);
        }
        
        
        playlistJList.revalidate(); 
        playlistJList.repaint();    
        if(appMusicPlayer.getPlaylist().isEmpty() && appMusicPlayer.getCurrentAudioFile()==null)updatePlayerUI();
    }

    private void updateSearchResultsTable(List<Music> results){
        searchResultTableModel.setRowCount(0);

        if(results.isEmpty()){
            searchStatusLabel.setText("검색 결과: 0개");
        }else{
            for(Music music:results){
                searchResultTableModel.addRow(new Object[]{
                        music.getTitle(),
                        music.getArtist(),
                        music.getGenre(),
                        music.getReleaseYear(),
                        music.getAlbum()
                });
            }
            searchStatusLabel.setText("총 "+results.size()+"개의 음악이 검색되었습니다.");
        }
    }

    private void updateLikedSongsList(){
        likedSongsListModel.clear();
        List<WAVAudioFile> likedSongs=appMusicPlayer.getLikedSongs();
        if(likedSongs.isEmpty()){
            likedSongsJList.setVisible(false);
            noLikedSongsLabel.setVisible(true);
        }else{
            likedSongsJList.setVisible(true);
            noLikedSongsLabel.setVisible(false);
            for(WAVAudioFile audioFile:likedSongs)likedSongsListModel.addElement(audioFile);
        }
    }

    private void updateRecommendedSongsList(){
        recommendedSongsListModel.clear();
        List<WAVAudioFile> recommended=appMusicPlayer.getRecommendedSongs();
        if(recommended.isEmpty()){
            recommendedSongsJList.setVisible(false);
            noRecommendedSongsLabel.setVisible(true);
        }else{
            recommendedSongsJList.setVisible(true);
            noRecommendedSongsLabel.setVisible(false);
            for(WAVAudioFile audioFile:recommended)recommendedSongsListModel.addElement(audioFile);
        }
    }

    public void displayErrorMessage(String title,String message){
        JOptionPane.showMessageDialog(this,message,title,JOptionPane.ERROR_MESSAGE);
    }

    public void displayInfoMessage(String title,String message){
        JOptionPane.showMessageDialog(this,message,title,JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatTime(long microseconds){
        long seconds=TimeUnit.MICROSECONDS.toSeconds(microseconds);
        long minutes=seconds/60;
        seconds%=60;
        return String.format("%02d:%02d",minutes,seconds);
    }

    
    private JButton createStyledButton(String text,Color background){
        JButton button=new JButton(text);
        button.setFont(new Font("맑은 고딕",Font.BOLD,12));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,1));
        
        
        button.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        button.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
        return button;
    }

    
    
    private JButton createStyledIconButton(String text,Color background){
        JButton button=new JButton(text);
        button.setFont(new Font("맑은 고딕",Font.BOLD,18));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,1));
        button.setPreferredSize(new Dimension(60,40));
        
        
        button.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        button.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
        return button;
    }

    
    private JToggleButton createStyledToggleButton(String text,Color activeBackground){
        JToggleButton toggle=new JToggleButton(text);
        
        toggle.setFont(new Font("맑은 고딕",Font.PLAIN,16));
        toggle.setBackground(new Color(50,50,50));
        toggle.setForeground(Color.WHITE);
        toggle.setFocusPainted(false);
        toggle.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,1));
        toggle.setPreferredSize(new Dimension(60,40));
        
        
        toggle.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        toggle.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
        toggle.addChangeListener(e -> {
            if(toggle.isSelected())toggle.setBackground(activeBackground);
            else toggle.setBackground(new Color(50,50,50));
        });
        return toggle;
    }

    private ImageIcon getScaledCoverIcon(String path,int width,int height){
        try{
            File imageFile=new File(path);
            if(!imageFile.exists()){
                System.err.println("이미지 파일을 찾을 수 없습니다: "+path);
                return getScaledDefaultCover(width,height);
            }
            ImageIcon originalIcon=new ImageIcon(imageFile.getAbsolutePath());
            if(originalIcon.getImage()==null||originalIcon.getIconWidth()<=0){
                System.err.println("이미지 로드 실패: "+path);
                return getScaledDefaultCover(width,height);
            }
            Image scaledImage=originalIcon.getImage().getScaledInstance(width,height,Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }catch(Exception e){
            System.err.println("이미지 스케일링 또는 로드 중 오류: "+path+" - "+e.getMessage());
            e.printStackTrace();
            return getScaledDefaultCover(width,height);
        }
    }

    private ImageIcon getScaledDefaultCover(int width,int height){
        try{
            File defaultCoverFile=new File(ApplicationMain.BASE_RESOURCE_PATH+"images/default_cover.jpg");
            if(defaultCoverFile.exists()){
                ImageIcon defaultIcon=new ImageIcon(defaultCoverFile.getAbsolutePath());
                Image defaultScaledImage=defaultIcon.getImage().getScaledInstance(width,height,Image.SCALE_SMOOTH);
                return new ImageIcon(defaultScaledImage);
            }else{
                System.err.println("기본 커버 이미지가 존재하지 않음: "+defaultCoverFile.getAbsolutePath());
            }
        }catch(Exception e){
            System.err.println("기본 커버 이미지 로드 예외 발생: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void setCoverImage(String path){
        try{
            File coverFile=new File(path);
            if(coverFile.exists()){
                ImageIcon icon=new ImageIcon(coverFile.getAbsolutePath());
                Image scaled=icon.getImage().getScaledInstance(
                    coverImageLabel.getPreferredSize().width,
                    coverImageLabel.getPreferredSize().height,
                    Image.SCALE_SMOOTH);
                coverImageLabel.setIcon(new ImageIcon(scaled));
                System.out.println("커버 이미지 로드 성공: "+path);
            }else{
                System.err.println("커버 이미지 못 찾음: "+path);
                coverImageLabel.setIcon(null);
            }
        }catch(Exception e){
            System.err.println("커버 이미지 로드 중 오류: "+e.getMessage());
            e.printStackTrace();
        }
    }
}