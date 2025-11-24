package manage;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SignUp extends JFrame {

    public static String ID = "";
    public static String PW = "";
    public static String PHONE = "";

    JTextField nameField;
    JTextField phoneField;
    JTextField idField;
    JTextField pwField;

    //GUI 설정
    public SignUp() {
        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridLayout grid = new GridLayout(6, 2);
        grid.setVgap(5);

        Container c = getContentPane();
        c.setLayout(grid);

        //이름 입력 필드, 라벨 추가 
        c.add(new JLabel("이름"));
        nameField = new JTextField("");
        c.add(nameField);

        //전화번호 입력 필드, 라벨 추가 
        c.add(new JLabel("전화번호"));
        phoneField = new JTextField("");
        c.add(phoneField);

        //아이디 입력 필드, 라벨 추가 
        c.add(new JLabel("아이디"));
        idField = new JTextField("");
        c.add(idField);

        //패스워드 입력 필드, 라벨 추가 
        c.add(new JLabel("패스워드"));
        pwField = new JTextField("");
        c.add(pwField);

        //회원가입 완료 버튼 
        JButton completeBtn = new JButton("회원가입 완료");
        c.add(completeBtn);
        c.add(new JLabel(""));

        //로그인 버튼
        JButton loginBtn = new JButton("로그인");
        c.add(loginBtn);
        c.add(new JLabel(""));

        //Listener 연결
        completeBtn.addActionListener(new MyActionListener());
        loginBtn.addActionListener(new MyActionListener());

        setSize(300, 250);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            //회원가입 완료 버튼 눌럿을 때
            if (b.getText().equals("회원가입 완료")) {
                ID = idField.getText();
                PW = pwField.getText();
                PHONE = phoneField.getText();

                //정보 다 입력했는지 확인
                if (ID.equals("") || PW.equals("") || PHONE.equals("")) {
                    JOptionPane.showMessageDialog(SignUp.this, "정보를 모두 입력하세요.");
                    return;
                }

                JOptionPane.showMessageDialog(SignUp.this, "회원가입이 완료되었습니다.");
            }

            //로그인 버튼 누르면 로그인 창 띄우기
            if (b.getText().equals("로그인")) {
                new Login();
            }
        }
    }

    public static void main(String[] args) {
        new SignUp();
    }
}
