package manage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
1
public class Login {

    public static void main(String[] args) {

        JFrame frame = new JFrame("로그인");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel label = new JLabel("로그인 하시겠습니까? ");
        label.setBounds(70, 10, 200, 25);
        frame.add(label);

        JButton yesButton = new JButton("Y");
        yesButton.setBounds(50, 50, 80, 30);
        frame.add(yesButton);

        JButton noButton = new JButton("N");
        noButton.setBounds(150, 50, 80, 30);
        frame.add(noButton);

        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "로그인을 진행합니다.");
            }
        });

        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "로그인을 취소합니다.");
            }
        });

        frame.setVisible(true);
    }
}
