import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClient {
    public static String getLogonID() {
        JFrame frame = new JFrame("YS톡 로그인");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.getContentPane().setLayout(null);
        frame.setLocationRelativeTo(null); // 창을 가운데로 정렬
        frame.getContentPane().setBackground(new Color(186, 206, 224));

        // 이미지를 표시할 JLabel 생성
   //     ImageIcon originalIcon = new ImageIcon("Image/talk.png"); // 이미지 파일 경로로 수정
        ClassLoader classLoader = ChatClient.class.getClassLoader();
        ImageIcon originalIcon = new ImageIcon(classLoader.getResource("Image/talk.png"));

       
        Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        int labelWidth = scaledIcon.getIconWidth();
        int labelHeight = scaledIcon.getIconHeight();
        int labelX = (frame.getWidth() - labelWidth) / 2;
        int labelY = 50;
        imageLabel.setBounds(50, 140, 300, 200);
        frame.getContentPane().add(imageLabel);

        JLabel chatName = new JLabel("YS Open Chat");
        chatName.setFont(new Font("Serif", Font.BOLD, 24));
        chatName.setBounds(109, 70, 169, 56);
        frame.getContentPane().add(chatName);

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(50, 350, 80, 30);
        lblId.setFont(new Font("Serif", Font.BOLD, 17));
        frame.getContentPane().add(lblId);

        HintTextField textField = new HintTextField("ID를 입력하시오");
        textField.setBounds(109, 350, 224, 30);
        textField.setFont(new Font("Serif", Font.BOLD, 17));
        frame.getContentPane().add(textField);

        JButton button = new JButton("Login");
        button.setBackground(new Color(254, 219, 0));
        button.setBounds(50, 418, 300, 50);
        button.setFont(new Font("Serif", Font.BOLD, 19));
        frame.getContentPane().add(button);

        final String[] logonID = {""}; // final 배열로 변경

        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputID = textField.getText().trim(); // 공백 제거
                inputID = inputID.replaceAll(" ", "");
                if (!inputID.isEmpty() && !inputID.equals(textField.getHint())) { // 공백이 아니고 힌트 메시지와 다른지 검사
                    if (inputID.length() <= 10) {
                        logonID[0] = inputID;
                        frame.dispose(); // 로그인 화면 닫기
                    } else {
                        JOptionPane.showMessageDialog(frame, "ID는 10글자 이내로 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                        textField.setText("");
                    }
                    ;
                } else {
                    JOptionPane.showMessageDialog(frame, "ID를 다시 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    textField.setText("");
                }
            }
        };

        button.addActionListener(loginAction);
        textField.addActionListener(loginAction); // 엔터 키 처리

        // 포커스 이동 설정
        frame.setFocusTraversalPolicyProvider(true);
        frame.setFocusTraversalPolicy(new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                return null;
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                return null;
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return null;
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return null;
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return button; // 버튼을 기본 포커스 컴포넌트로 설정
            }
        });

        frame.setVisible(true);

        while (logonID[0].equals("")) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return logonID[0];
    }

    public static void main(String args[]) {
        String id = getLogonID();
        try {
            if (args.length == 0) {
                ClientThread thread = new ClientThread();
                thread.start();
                thread.requestLogon(id);
            } else if (args.length == 1) {
                ClientThread thread = new ClientThread(args[0]);
                thread.start();
                thread.requestLogon(id);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // 힌트 텍스트 필드 클래스
    static class HintTextField extends JTextField implements FocusListener {
        private String hint;
        private boolean showingHint;

        HintTextField(String hint) {
            super();
            this.hint = hint;
            this.showingHint = true;
            setForeground(Color.GRAY);
            addFocusListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (showingHint && (getText().isEmpty() || getText().equals(hint)) && !isFocusOwner()) {
                Insets insets = getInsets();
                FontMetrics fm = g.getFontMetrics();
                int x = insets.left;
                int y = insets.top + (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g.setColor(Color.GRAY);
                g.drawString(hint, x, y);
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (showingHint) {
                setText("");
                setForeground(Color.BLACK);
                showingHint = false;
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (getText().isEmpty()) {
                setForeground(Color.GRAY);
                showingHint = true;
            }
        }

        public String getHint() {
            return hint;
        }
    }
}

