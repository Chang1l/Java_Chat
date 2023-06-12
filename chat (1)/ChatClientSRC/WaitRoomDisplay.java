import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

class WaitRoomDisplay extends JFrame implements ActionListener, KeyListener,
        MouseListener, ChangeListener {
    private ClientThread cc_thread;
    private int roomNumber;
    private String password, select;
    private boolean isRock, isSelected;

    private JLabel rooms, waiter, label;
    public JList roomInfo, waiterInfo;
    private JButton create, join, sendword;
    private Font font;
    private JViewport view;
    private JScrollPane jsp3;
    public JTextPane messages;
    public JTextField message;
    private JPanel p_1;
    private JPanel p_2;

    public WaitRoomDisplay(ClientThread thread) {
        super("대기실");
        getContentPane().setBackground(new Color(186, 206, 224));

        getContentPane().setFont(new Font("굴림", Font.PLAIN, 17));

        cc_thread = thread;
        roomNumber = 0;
        password = "0";
        isRock = false;
        isSelected = false;
        font = new Font("SanSerif", Font.PLAIN, 12);


        Container c = getContentPane();
        c.setLayout(null);

        rooms = new JLabel("대화방");

        JPanel p = new JPanel();
        p.setBackground(new Color(186, 206, 224));
        p.setLayout(null);
        p.setBounds(5, 10, 460, 215);
        p.setFont(font);
        p.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED), "대화방 목록"));

        label = new JLabel("번 호");
        label.setBounds(15, 25, 45, 25);
        label.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        p.add(label);

        label = new JLabel("제 목");
        label.setBounds(60, 25, 210, 25);
        label.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        p.add(label);

        label = new JLabel("현재/최대");
        label.setBounds(265, 25, 60, 25);
        label.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        p.add(label);

        label = new JLabel("공개여부");
        label.setBounds(325, 25, 60, 25);
        label.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        p.add(label);

        label = new JLabel("개 설 자");
        label.setBounds(385, 25, 60, 25);
        label.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        p.add(label);

        roomInfo = new JList();
        roomInfo.setFont(new Font("Dialog", Font.PLAIN, 17));
        WaitListCellRenderer renderer = new WaitListCellRenderer();
        JScrollPane jsp1 = new JScrollPane(roomInfo);
        roomInfo.addMouseListener(this);
        renderer.setDefaultTab(20);
        renderer.setTabs(new int[]{40, 265, 285, 315, 375, 430});
        roomInfo.setCellRenderer(renderer);
        jsp1.setBounds(15, 49, 430, 155);
        p.add(jsp1);

        c.add(p);


        p_1 = new JPanel();
        p_1.setBackground(new Color(186, 206, 224));
        p_1.setLayout(null);
        p_1.setBounds(464, 230, 150, 200);
        p_1.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED), "대기자"));

        waiterInfo = new JList();
        waiterInfo.setFont(new Font("Dialog", Font.PLAIN, 17));
        JScrollPane jsp2 = new JScrollPane(waiterInfo);
        jsp2.setBounds(15, 25, 115, 165);
        p_1.add(jsp2);

        c.add(p_1);

        p_2 = new JPanel();
        p_2.setBackground(new Color(186, 206, 224));
        p_2.setLayout(null);
        p_2.setBounds(5, 230, 460, 200);
        p_2.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED), "채팅창"));

        view = new JViewport();
        messages = new JTextPane();
        messages.setEditable(false);
        messages.setFont(font);
        view.add(messages);
        view.addChangeListener(this);
        jsp3 = new JScrollPane(view);
        jsp3.setBounds(15, 25, 430, 135);
        view.addChangeListener(this);
        p_2.add(jsp3);

        view = (JViewport) jsp3.getViewport().getView();
        view.addChangeListener(this);

        message = new JTextField();
        message.setFont(font);
        message.setBounds(15, 170, 430, 20);
        message.addKeyListener(this);
        message.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
        p_2.add(message);

        c.add(p_2);

        ClassLoader classLoader = ChatClient.class.getClassLoader();
        ImageIcon createIcon = new ImageIcon(classLoader.getResource("Image/createchat.png")); // 이미지 파일 경로로 수정
        Image image = createIcon.getImage();
        Image resizedImage = image.getScaledInstance(55, 55, Image.SCALE_SMOOTH);//이미지 사이즈 변경
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        create = new JButton(resizedIcon);
        create.setBounds(510, 20, 55, 55);
        create.setBorderPainted(false);
        create.setOpaque(false);
        create.setContentAreaFilled(false);
        create.addActionListener(this);
        c.add(create);


        ImageIcon joinIcon = new ImageIcon(classLoader.getResource("Image/joinchat.png")); // 이미지 파일 경로로 수정
        image = joinIcon.getImage();
        resizedImage = image.getScaledInstance(55, 55, Image.SCALE_SMOOTH);//이미지 사이즈 변경
        resizedIcon = new ImageIcon(resizedImage);
        join = new JButton(resizedIcon);
        join.setBounds(510, 90, 55, 55);
        join.setBorderPainted(false);
        join.setOpaque(false);
        join.setContentAreaFilled(false);
        join.addActionListener(this);
        c.add(join);


        ImageIcon sendwordIcon = new ImageIcon(classLoader.getResource("Image/ear.png")); // 이미지 파일 경로로 수정
        image = sendwordIcon.getImage();
        resizedImage = image.getScaledInstance(55, 55, Image.SCALE_SMOOTH);//이미지 사이즈 변경
        resizedIcon = new ImageIcon(resizedImage);
        sendword = new JButton(resizedIcon);
        sendword.setBorderPainted(false);
        sendword.setOpaque(false);
        sendword.setContentAreaFilled(false);
        sendword.setBounds(510, 160, 55, 55);
        sendword.addActionListener(this);
        c.add(sendword);

        Dimension dim = getToolkit().getScreenSize();
        setSize(640, 488);
        setLocation(dim.width / 2 - getWidth() / 2,
                dim.height / 2 - getHeight() / 2);
        show();

        addWindowListener(
                new WindowAdapter() {
                    public void windowActivated(WindowEvent e) {
                        message.requestFocusInWindow();
                    }
                }
        );

        addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        cc_thread.requestLogout();
                    }
                }
        );

    }

    public void resetComponents() {
        messages.setText("");
        message.setText("");
        roomNumber = 0;
        password = "0";
        isRock = false;
        isSelected = false;
        message.requestFocusInWindow();
    }

    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
            String words = message.getText();
            String data;
            String idTo;
            if (words.startsWith("/w")) {
                StringTokenizer st = new StringTokenizer(words, " ");
                String command = st.nextToken();
                idTo = st.nextToken();
                data = st.nextToken();
                cc_thread.requestSendWordTo(data, idTo);
                message.setText("");
            } else {
                cc_thread.requestSendWord(words);
                message.requestFocusInWindow();
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        try {
            isSelected = true;
            String select = String.valueOf(((JList) e.getSource()).getSelectedValue());
            setSelectedRoomInfo(select);
        } catch (Exception err) {
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == create) {
            CreateRoomDisplay createRoom = new CreateRoomDisplay(this, cc_thread);
        } else if (ae.getSource() == join) {
            if (!isSelected) {
                JOptionPane.showMessageDialog(this, "입장할 방을 선택하세요.",
                        "대화방 입장.", JOptionPane.ERROR_MESSAGE);
            } else if (isRock && password.equals("0")) {
                if ((password = JOptionPane.showInputDialog("비밀번호를 입력하세요.")) != null) {
                    if (!password.equals("")) {
                        cc_thread.requestEnterRoom(roomNumber, password);
                        password = "0";
                    } else {
                        password = "0";
                        cc_thread.requestEnterRoom(roomNumber, password);
                    }
                } else {
                    password = "0";
                }
            } else {
                cc_thread.requestEnterRoom(roomNumber, password);
            }
        }
        // 대기방 액션 귓속말 아이콘 버튼 클릭 시 컨트롤
        else if (ae.getSource() == sendword) {
            String idTo, data;
            while (true) {
                idTo = JOptionPane.showInputDialog("아이디를 입력하세요.");
                idTo = idTo.trim();
                idTo = idTo.replaceAll(" ","");
                if (!idTo.equals("")) {
                    break;
                }
            }
          while (true) {
            data = JOptionPane.showInputDialog("메세지를 입력하세요.");
            data = data.trim();
            data = data.replaceAll(" ","");
            if (!data.equals("")) {
              System.out.println(idTo);
              System.out.println(data);
              System.out.println(cc_thread.getLoginID());
              if(!idTo.equals(cc_thread.getLoginID())){
                cc_thread.requestSendWordTo(data, idTo);
                break;
              }
              else {
                JOptionPane.showMessageDialog(this, "자기 자신에게 메세지를 보낼 수 없습니다!", "귓속말", JOptionPane.ERROR_MESSAGE);
                break;
              }
            }
          }
        }
    }

    private void setSelectedRoomInfo(String select) {
        StringTokenizer st = new StringTokenizer(select, "=");
        roomNumber = Integer.parseInt(st.nextToken());
        String roomName = st.nextToken();
        int maxUser = Integer.parseInt(st.nextToken());
        int user = Integer.parseInt(st.nextToken());
        isRock = st.nextToken().equals("비공개") ? true : false;
    }

    public void stateChanged(ChangeEvent e) {
        jsp3.getVerticalScrollBar().setValue((jsp3.getVerticalScrollBar().getValue() + 20));
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
