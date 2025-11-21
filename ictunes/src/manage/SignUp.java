package manage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class SignUp extends JFrame {

    public SignUp() {
        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridLayout grid = new GridLayout(5, 2);
        grid.setVgap(5);

        Container c = getContentPane();
        c.setLayout(grid);
        c.add(new JLabel("이름"));
        c.add(new JTextField(""));
        c.add(new JLabel("전화번호"));
        c.add(new JTextField(""));
        c.add(new JLabel("아이디"));
        c.add(new JTextField(""));
        c.add(new JLabel("패스워드"));
        c.add(new JTextField(""));

        JButton Button = new JButton("완료");
        c.add(Button);
        c.add(new JLabel(""));

        Button.addActionListener(new MyActionListener());

        setSize(300, 200);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();
            if (b.getText().equals("완료")) {
                JOptionPane.showMessageDialog(SignUp.this, "회원가입이 완료되었습니다!");
            }
        }
    }

    public static void main(String[] args) {
        new SignUp();
    }
}
