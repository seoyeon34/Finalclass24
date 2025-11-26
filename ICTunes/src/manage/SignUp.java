package manage;

import main.ApplicationMain; 
import model.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;   
import java.io.FileWriter;
import java.io.IOException;


public class SignUp extends JFrame {

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField idField;
    private JPasswordField pwField;

    // GUI 설정
    public SignUp() {
        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GridLayout grid = new GridLayout(6, 2);
        grid.setVgap(5);

        Container c = getContentPane();
        c.setLayout(grid);

        c.add(new JLabel("이름"));
        nameField = new JTextField("");
        c.add(nameField);
        
        c.add(new JLabel("전화번호"));
        phoneField = new JTextField("");
        c.add(phoneField);

        c.add(new JLabel("아이디"));
        idField = new JTextField("");
        c.add(idField);

        c.add(new JLabel("패스워드"));
        pwField = new JPasswordField("");
        c.add(pwField);

        JButton completeBtn = new JButton("회원가입 완료");
        c.add(completeBtn);
        c.add(new JLabel(""));

        JButton loginBtn = new JButton("로그인");
        c.add(loginBtn);
        c.add(new JLabel(""));

        completeBtn.addActionListener(new MyActionListener());
        loginBtn.addActionListener(new MyActionListener());

        setSize(300, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            // 회원가입 완료 버튼 눌렀을 때
            if (b.getText().equals("회원가입 완료")) {
                String id = idField.getText().trim();
                String pw = new String(pwField.getPassword());
                String phone = phoneField.getText().trim();
                String name = nameField.getText().trim();

                if (id.isEmpty() || pw.isEmpty() || phone.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(SignUp.this, "모든 필드를 입력하세요 (아이디, 비밀번호, 전화번호, 이름).", "회원가입 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (!phone.matches("\\d+")) {
                    JOptionPane.showMessageDialog(SignUp.this, "전화번호는 숫자만 입력해주세요.", "회원가입 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String resourcesPath = ApplicationMain.BASE_RESOURCE_PATH;
                File userDir = new File(resourcesPath);
                
                System.out.println("[SignUp] users.txt 저장 시도. 기본 리소스 경로: " + resourcesPath);
                System.out.println("[SignUp] users.txt 파일이 저장될 디렉토리: " + userDir.getAbsolutePath());

                // users.txt 파일이 저장될 디렉토리가 없으면 생성
                if (!userDir.exists()) {
                    System.out.println("[SignUp] 디렉토리 존재하지 않음. 생성 시도...");
                    boolean created = userDir.mkdirs();
                    if (created) {
                        System.out.println("[SignUp] 리소스 디렉토리 생성 성공: " + userDir.getAbsolutePath());
                    } else {
                        System.err.println("[SignUp] 리소스 디렉토리 생성 실패: " + userDir.getAbsolutePath());
                        JOptionPane.showMessageDialog(SignUp.this, "파일 저장 경로 디렉토리 생성에 실패했습니다. 권한을 확인하세요.", "회원가입 오류", JOptionPane.ERROR_MESSAGE);
                        return; // 디렉토리 생성 실패 시 회원가입 진행 중단
                    }
                } else {
                    System.out.println("[SignUp] 리소스 디렉토리가 이미 존재합니다: " + userDir.getAbsolutePath());
                }

                // 디렉토리에 쓰기 가능한지 확인
                if (!userDir.canWrite()) {
                    System.err.println("[SignUp] 리소스 디렉토리에 쓰기 권한이 없습니다: " + userDir.getAbsolutePath());
                    JOptionPane.showMessageDialog(SignUp.this, "파일 저장 경로에 쓰기 권한이 없습니다. 권한을 확인하거나 다른 경로를 사용하세요.", "회원가입 오류", JOptionPane.ERROR_MESSAGE);
                    return; // 쓰기 권한이 없으면 중단
                } else {
                    System.out.println("[SignUp] 리소스 디렉토리에 쓰기 권한이 있습니다.");
                }

                String filePath = resourcesPath + "users.txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    System.out.println("[SignUp] users.txt 파일 쓰기 시작: " + filePath);
                    writer.write(id + "," + pw + "," + phone + "," + name);
                    writer.flush(); // 버퍼 비우기 (파일에 즉시 기록)
                    System.out.println("[SignUp] users.txt 파일 쓰기 완료. ");
                    JOptionPane.showMessageDialog(SignUp.this, "회원가입이 완료되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 회원가입 창 닫기
                } catch (IOException ex) {
                    System.err.println("[SignUp] 사용자 정보 저장 중 IOException 발생:");
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(SignUp.this, "사용자 정보 저장 중 오류 발생: " + ex.getMessage() + "\n콘솔을 확인하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }

            // 로그인 버튼 누르면 로그인 창 띄우기
            if (b.getText().equals("로그인")) {
                new Login();
                dispose(); // 현재 회원가입 창 닫기
            }
        }
    }
}