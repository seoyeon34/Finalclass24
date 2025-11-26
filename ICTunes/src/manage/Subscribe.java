//아직 구현 못함. 아직 플레이리스트 부분을 제대로 만들질 못해서 우선 남겨둠. 추후 수정 예정

package manage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class Subscribe extends JFrame {

    private CardLayout cardLayout; // 패널 전환을 위한 CardLayout
    private JPanel mainPanel;      // CardLayout이 적용될 메인 패널
    private int price = 0;         // 선택된 구독 요금
    private String type = "";      // 선택된 구독 유형 (월간/연간)

    private JLabel priceLabel;     // 가격을 표시할 라벨
    private JLabel titleLabel;     // 구독 유형 제목을 표시할 라벨
    
    public Subscribe() {
        // GUI 기본 설정
        setTitle("ICTunes 정기구독");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 이 창만 닫히도록 설정

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout); // CardLayout을 사용하는 메인 패널 초기화

        // 구독 선택 화면과 구독 정보/결제 화면을 메인 패널에 추가
        mainPanel.add(createSelectPanel(), "SELECT");
        mainPanel.add(createDetailPanel(), "DETAIL");

        add(mainPanel);

        // 창 크기 및 가시성 설정
        pack(); // 컴포넌트 크기에 맞춰 창 크기 자동 조절
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setVisible(true);
    }

    
    private JPanel createSelectPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("정기구독 서비스", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        panel.add(title);

        JButton monthBtn = createStyledButton("월간 구독 (9,900원/월)");
        JButton yearBtn = createStyledButton("연간 구독 (99,000원/년)");
        JButton noSubscribeBtn = createStyledButton("나중에 구독할게요");
        JButton closeBtn = createStyledButton("닫기");

        panel.add(monthBtn);
        panel.add(yearBtn);
        panel.add(noSubscribeBtn);
        panel.add(closeBtn);

        // 월간 구독 버튼 액션 리스너
        monthBtn.addActionListener(e -> {
            type = "월간";
            price = 9900;
            updateDetailPanel(); // 상세 패널 정보 업데이트
            cardLayout.show(mainPanel, "DETAIL");
        });

        // 연간 구독 버튼 액션 리스너
        yearBtn.addActionListener(e -> {
            type = "연간";
            price = 99000;
            updateDetailPanel(); // 상세 패널 정보 업데이트
            cardLayout.show(mainPanel, "DETAIL"); // 상세 패널로 전환
        });

        // "나중에 구독할게요" 버튼 액션 리스너
        noSubscribeBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(Subscribe.this, "구독하지 않고 기본 서비스로 이용합니다.", "구독 취소", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // 창 닫기
           
        });

        // 닫기 버튼 액션 리스너
        closeBtn.addActionListener(e -> {
            dispose(); // 창 닫기
        });

        return panel;
    }

   
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        panel.add(titleLabel);

        // 구독 시 제공되는 기능들을 나열합니다.
        panel.add(new JLabel("- 광고 없이 음악 감상", SwingConstants.CENTER));
        panel.add(new JLabel("- 무제한 스트리밍 및 다운로드", SwingConstants.CENTER));
        panel.add(new JLabel("- 고음질 음원 제공", SwingConstants.CENTER));
        
        priceLabel = new JLabel("", SwingConstants.CENTER);
        priceLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        panel.add(priceLabel);

        JButton payBtn = createStyledButton("결제하기");
        panel.add(payBtn);

        // 결제 버튼 액션 리스너
        payBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                Subscribe.this,
                price + "원을 결제하시겠습니까?", // 콤마 포맷팅은 필요 시 추가
                "결제 확인",
                JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(Subscribe.this, "결제가 완료되었습니다. ICTunes 정기구독 서비스에 오신 것을 환영합니다!", "결제 완료", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 구독 창 닫기
                
            }
        });
        
        return panel;
    }

    private void updateDetailPanel() {
        titleLabel.setText(type + " 정기구독");
        priceLabel.setText("가격: " + String.format("%,d원", price)); // 가격을 콤마 포맷으로 표시
    }
    
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
}