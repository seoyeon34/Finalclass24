package function;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent;

public class Music extends JFrame{
    
    private static boolean isPlaying = true; 
    
    private static final String[] TITLES_SIMULATION = {
        "응급실", "한번 더 이별", "이별하러 가는 길", "Yours", "나무", "좋은 밤 좋은 꿈",
        "죽일놈", "Aqua man", "D", "All I want for Christmas is you", "When I was your man", "Off my face",
        "Golden", "Blue valentine", "Drama"
    };
    private static final String[] ARTISTS_SIMULATION = {
        "izi", "이창섭", "임한별", "데이먼스 이어", "카더가든", "너드커넥션",
        "다이나믹 듀오", "빈지노", "딘", "Mariah Carey", "Bruno Mars", "Justin Bieber",
        "K-pop demon hunters", "Nmixx", "aespa"
    };
    private static final String[] COVER_PATHS_SIMULATION = {
            "/Users/songseolin/Desktop/앨범 커버/응급실.jpeg", 
            "/Users/songseolin/Desktop/앨범 커버/한번 더 이별.jpg", 
            "/Users/songseolin/Desktop/앨범 커버/이별하러 가는 길.jpg", 
            "/Users/songseolin/Desktop/앨범 커버/yours.jpg",
            "/Users/songseolin/Desktop/앨범 커버/나무.jpg", 
            "/Users/songseolin/Desktop/앨범 커버/좋은 밤 좋은 꿈.jpg",
            "/Users/songseolin/Desktop/앨범 커버/죽일 놈.jpg",
            "/Users/songseolin/Desktop/앨범 커버/Aqua Man.jpg",
            "/Users/songseolin/Desktop/앨범 커버/D.jpg",
            "/Users/songseolin/Desktop/앨범 커버/All I want for christmas is you.jpg",
            "/Users/songseolin/Desktop/앨범 커버/When I was your man.jpg", 
            "/Users/songseolin/Desktop/앨범 커버/off my face.jpg",
            "/Users/songseolin/Desktop/앨범 커버/Golden.jpg",
            "/Users/songseolin/Desktop/앨범 커버/Blue Valentine.jpg",
            "/Users/songseolin/Desktop/앨범 커버/Drama.jpg"
        };
    
    private static int currentTrackIndex = 0; 
    private static final int TOTAL_TRACKS = TITLES_SIMULATION.length; 
    
    private static String getCurrentTitle() { return TITLES_SIMULATION[currentTrackIndex]; }
    private static String getCurrentArtist() { return ARTISTS_SIMULATION[currentTrackIndex]; }
    
    private static String getLyricsContent() { 
        return Lyrics.getLyricsByIndex(currentTrackIndex); 
    }

    private static String getCurrentCoverPath() { 
        return COVER_PATHS_SIMULATION[currentTrackIndex]; 
    }
	private Cover cover = new Cover();
	
	public Music() {
		setTitle(getCurrentTitle()); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel Panel = new JPanel(new BorderLayout());
        Panel.add(cover, BorderLayout.CENTER);
        
        JPanel controlArea = new JPanel();
        controlArea.setLayout(new BoxLayout(controlArea, BoxLayout.Y_AXIS));
        
        cover.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LyricsDisplay(getCurrentTitle()); 
            }
        });
        
        JLabel title = new JLabel(getCurrentTitle(), SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setPreferredSize(new Dimension(250, 25));
        title.setMaximumSize(new Dimension(250, 25));

        JLabel artist = new JLabel(getCurrentArtist(), SwingConstants.LEFT);
        artist.setPreferredSize(new Dimension(250, 20));
        artist.setMaximumSize(new Dimension(250, 20));

        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        artist.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        labelPanel.add(title);
        labelPanel.add(artist); 
        
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        
        centerContainer.add(cover);  
        centerContainer.add(labelPanel); 
        
        Panel.add(centerContainer, BorderLayout.CENTER);
        
        JSlider progressBar = new JSlider(0, 100, 0); 
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5)); 
        
        JButton prevBtn = new JButton("◀◀");
        JButton playPauseBtn = new JButton("▶");
        JButton nextBtn = new JButton("▶▶");
        
        if (isPlaying) {
            playPauseBtn.setText("||");
        } else {
            playPauseBtn.setText("▶");
        }
        
        playPauseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPlaying = !isPlaying;
                
                if (isPlaying) {
                    playPauseBtn.setText("||");
                } else {
                    playPauseBtn.setText("▶");
                }
            }
        });
        
        prevBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTrackIndex = (currentTrackIndex - 1 + TOTAL_TRACKS) % TOTAL_TRACKS;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dispose(); 
                        new Music(); 
                    }
                });
            }
        });

        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTrackIndex = (currentTrackIndex + 1) % TOTAL_TRACKS;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dispose();
                        new Music();
                    }
                });
            }
        });
        
        button.add(prevBtn);
        button.add(playPauseBtn);
        button.add(nextBtn);
        
        controlArea.add(progressBar);
        controlArea.add(button);
        
        Panel.add(controlArea, BorderLayout.SOUTH);
        
        setContentPane(Panel); 
        
        setSize(300, 400); 
        setVisible(true);
	}
	
	class Cover extends JPanel{
		private ImageIcon icon = new ImageIcon(getCurrentCoverPath());
		private Image img = icon.getImage();
        
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 50, 10, 200, 200, this);
		}
	}
    
	private class LyricsDisplay extends JFrame {
        public LyricsDisplay(String songTitle) {
            setTitle(songTitle + " - 가사");
            setSize(300, 600);
            
            String lyricsText = getLyricsContent(); 

            JTextArea lyricsArea = new JTextArea(lyricsText);
            lyricsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            lyricsArea.setEditable(false);
            
            JScrollPane scrollPane = new JScrollPane(lyricsArea);
            add(scrollPane, BorderLayout.CENTER);
            
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setVisible(true);
        }
	}
    
	public static void main(String[] args) {
		new Music();
	}
}