package manage;

import model.User; 

import java.awt.*;
import java.awt.event.*; 
import javax.swing.*;

public class SignUp extends JDialog { 

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField idField;
    private JPasswordField pwField;
    private UserManager userManager; 
    private JFrame parentFrame;

    public SignUp(JFrame parent, UserManager userManager) {
        super(parent, "회원가입", true);
        this.parentFrame = parent; 
        this.userManager = userManager; 

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent); 

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            }
        });

        
        GridBagLayout layout = new GridBagLayout();
        Container c = getContentPane();
        c.setLayout(layout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 

        Dimension fieldSize = new Dimension(180, 28);

       
        KeyListener blockKorean = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                    Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO ||
                    Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.HANGUL_JAMO) {
                    e.consume(); 
                    JOptionPane.showMessageDialog(SignUp.this,
                        "아이디/비밀번호에는 한글을 입력할 수 없습니다.",
                        "입력 오류",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        };


        // 이름 라벨
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("이름"), gbc);

        // 이름 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        nameField = new JTextField("");
        nameField.setPreferredSize(fieldSize);
        c.add(nameField, gbc);

        // 전화번호 라벨
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("전화번호"), gbc);

        // 전화번호 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        phoneField = new JTextField("");
        phoneField.setPreferredSize(fieldSize);
        c.add(phoneField, gbc);

        // 아이디 라벨
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("아이디"), gbc);

        // 아이디 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        idField = new JTextField("");
        idField.setPreferredSize(fieldSize);
        c.add(idField, gbc);
        idField.addKeyListener(blockKorean); 

        // 패스워드 라벨
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("비밀번호"), gbc);

        // 패스워드 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pwField = new JPasswordField("");
        pwField.setPreferredSize(fieldSize);
        c.add(pwField, gbc);
        pwField.addKeyListener(blockKorean); 


        // 회원가입 완료 버튼
        JButton completeBtn = new JButton("회원가입 완료");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.CENTER;
        c.add(completeBtn, gbc);

        // 로그인 버튼 (MyActionListener에서 처리)
        JButton loginBtn = new JButton("로그인"); 
        gbc.gridy = 5;
        c.add(loginBtn, gbc);


        // Listener 연결
        completeBtn.addActionListener(new MyActionListener());
        loginBtn.addActionListener(new MyActionListener());

        pack(); 
        setResizable(false); 
    }
    
    
    public void showDialog() {
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            // 회원가입 완료 버튼 눌렀을 때
            if (b.getText().equals("회원가입 완료")) {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String id = idField.getText().trim();
                String pw = new String(pwField.getPassword());

                if (name.isEmpty() || phone.isEmpty() || id.isEmpty() || pw.isEmpty()) {
                    JOptionPane.showMessageDialog(SignUp.this, "모든 필드를 입력해주세요.", "회원가입 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (pw.length() < 8) {
                    JOptionPane.showMessageDialog(SignUp.this,
                        "비밀번호는 최소 8자 이상 입력해야 합니다.",
                        "비밀번호 오류",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!phone.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$")) { 
                    JOptionPane.showMessageDialog(SignUp.this,
                        "올바른 전화번호 형식을 입력해주세요 (예: 010-1234-5678).",
                        "회원가입 오류",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                User newUser = new User(id, pw, name, "no_email_provided", phone); 
                
                if (userManager.registerUser(newUser)) { 
                    JOptionPane.showMessageDialog(SignUp.this, "회원가입 성공! 로그인 화면으로 돌아갑니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); 
                } else {
                    JOptionPane.showMessageDialog(SignUp.this, "이미 존재하는 아이디입니다. 다른 아이디를 사용해주세요.", "회원가입 오류", JOptionPane.WARNING_MESSAGE);
                }
            }
            
            else if (b.getText().equals("로그인")) {
                dispose(); 
            }
        }
    }
}