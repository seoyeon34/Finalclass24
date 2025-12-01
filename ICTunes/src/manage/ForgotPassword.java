package manage;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent;   
import java.util.Optional;

/*
 * 비밀번호 찾기 기능을 제공하는 다이얼로그.
 */

public class ForgotPassword extends JDialog {

    private UserManager userManager;
    private JFrame parentFrame; 
    
    private JTextField inputField; 
    private JButton searchButton;
    private JLabel statusLabel; 
    
    private JPanel newPasswordPanel; 
    private JPasswordField newPasswordField;
    private JPasswordField newPasswordFieldConfirm; 
    private JButton resetPasswordButton;
    
    private User currentUserForReset; 

    public ForgotPassword(JFrame parent, UserManager userManager) {
        super(parent, "비밀번호 찾기", true); 
        this.parentFrame = parent; 
        this.userManager = userManager;
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10)); 

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) { 
                if (parentFrame != null) {
                    parentFrame.setVisible(true); 
                }
            }
        });

        
        JPanel topSearchPanel = new JPanel();
        topSearchPanel.setLayout(new BoxLayout(topSearchPanel, BoxLayout.Y_AXIS));
        topSearchPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        
        JPanel idInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        idInputPanel.add(new JLabel("아이디 입력:"));
        inputField = new JTextField(15);
        idInputPanel.add(inputField);
        topSearchPanel.add(idInputPanel);
        topSearchPanel.add(Box.createVerticalStrut(10)); 

        
        searchButton = new JButton("사용자 확인");
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        topSearchPanel.add(searchButton);
        topSearchPanel.add(Box.createVerticalStrut(10)); 


        statusLabel = new JLabel("아이디를 입력하고 '사용자 확인' 버튼을 눌러주세요.");
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topSearchPanel.add(statusLabel);
        
        add(topSearchPanel, BorderLayout.NORTH);

        newPasswordPanel = new JPanel();
        newPasswordPanel.setLayout(new BoxLayout(newPasswordPanel, BoxLayout.Y_AXIS));
        newPasswordPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        newPasswordPanel.setVisible(false); 


        JPanel newPassPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newPassPanel.add(new JLabel("새 비밀번호:"));
        newPasswordField = new JPasswordField(15);
        newPassPanel.add(newPasswordField);
        newPasswordPanel.add(newPassPanel);
        newPasswordPanel.add(Box.createVerticalStrut(5));


        JPanel confirmPassPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        confirmPassPanel.add(new JLabel("새 비밀번호 확인:"));
        newPasswordFieldConfirm = new JPasswordField(15); 
        confirmPassPanel.add(newPasswordFieldConfirm);
        newPasswordPanel.add(confirmPassPanel);
        newPasswordPanel.add(Box.createVerticalStrut(10));


        resetPasswordButton = new JButton("비밀번호 재설정");
        resetPasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newPasswordPanel.add(resetPasswordButton);
        

        add(newPasswordPanel, BorderLayout.CENTER); 


        inputField.addActionListener(e -> performUserSearch());
        searchButton.addActionListener(e -> performUserSearch());
        newPasswordField.addActionListener(e -> resetUserPassword());
        newPasswordFieldConfirm.addActionListener(e -> resetUserPassword());
        resetPasswordButton.addActionListener(e -> resetUserPassword());
    }


    private void performUserSearch() {
        String query = inputField.getText().trim();
        if (query.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("아이디를 입력해주세요.");
            return;
        }

        Optional<User> foundUser = userManager.findUserById(query);
        
        if (foundUser.isPresent()) {
            currentUserForReset = foundUser.get();
            statusLabel.setForeground(Color.GREEN);
            statusLabel.setText(currentUserForReset.getUserId() + " 님 확인되었습니다. 새 비밀번호를 입력하세요.");
            newPasswordPanel.setVisible(true);
            newPasswordField.setText("");
            newPasswordFieldConfirm.setText(""); 
            newPasswordField.requestFocusInWindow();
        } else {
            currentUserForReset = null;
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("해당 아이디와 일치하는 사용자를 찾을 수 없습니다.");
            newPasswordPanel.setVisible(false);
        }
    }


    private void resetUserPassword() {
        if (currentUserForReset == null) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("먼저 아이디를 확인해주세요.");
            return;
        }

        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(newPasswordFieldConfirm.getPassword()); 

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("새 비밀번호와 확인 비밀번호를 모두 입력해주세요.");
            return;
        }
        
        if (newPass.length() < 8) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("비밀번호는 최소 8자 이상 입력해야 합니다.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            return;
        }

        if (newPass.equals(currentUserForReset.getPassword())) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("현재 사용중인 비밀번호와 동일합니다."); 
            return;
        }

        if (userManager.resetPassword(currentUserForReset.getUserId(), newPass)) {
            statusLabel.setForeground(Color.BLUE);
            statusLabel.setText("비밀번호가 성공적으로 재설정되었습니다.");
            JOptionPane.showMessageDialog(this, "비밀번호가 성공적으로 재설정되었습니다. 로그인 화면으로 돌아갑니다.");
            dispose(); 
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("비밀번호 재설정에 실패했습니다.");
        }
    }

    public void showDialog() {

        inputField.setText("");
        newPasswordField.setText("");
        newPasswordFieldConfirm.setText("");
        newPasswordPanel.setVisible(false);
        statusLabel.setText("아이디를 입력하고 '사용자 확인' 버튼을 눌러주세요.");
        inputField.requestFocusInWindow();
        setVisible(true); 
    }
}
