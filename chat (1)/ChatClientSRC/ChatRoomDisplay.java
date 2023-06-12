import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class ChatRoomDisplay extends JFrame implements ActionListener, KeyListener, ListSelectionListener, ChangeListener {
    private ClientThread cr_thread;
    private String idTo;
    private boolean isSelected;
    public boolean isAdmin;

    private JLabel roomer;
    public JList roomerInfo;
    private JButton coerceOut, sendWord, sendFile, quitRoom, imgIcon;
    private Font font, font_imgBtn;
    private JViewport view;
    private JScrollPane jsp3;
    //public JTextArea messages;
    public JTextArea message;
    private JPanel p_1;
    public JTextPane messages;

    public ChatRoomDisplay(ClientThread thread) {
        super("��ȭ��");
        getContentPane().setBackground(new Color(186, 206, 224));

        cr_thread = thread;
        isSelected = false;
        isAdmin = false;
        font = new Font("SanSerif", Font.PLAIN, 12);
        font_imgBtn = new Font("SanSerif", Font.PLAIN, 7);

        Container c = getContentPane();
        c.setLayout(null);

        p_1 = new JPanel();
        p_1.setBackground(new Color(186, 206, 224));
        p_1.setLayout(null);
        p_1.setBounds(12, 194, 403, 447);
        p_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "ä��â"));

        view = new JViewport();
        messages = new JTextPane();
        messages.setBackground(new Color(186, 206, 224));
        messages.setFont(new Font("Dialog", Font.PLAIN, 17));
        messages.setEditable(false);
        view.add(messages);
        view.addChangeListener(this);
        jsp3 = new JScrollPane(view);
        jsp3.setBounds(15, 20, 380, 284);
        p_1.add(jsp3);

        // message = new JTextField();
        message = new JTextArea();
        message.setFont(new Font("Dialog", Font.PLAIN, 17));
        message.addKeyListener(this);
        message.setBounds(12, 310, 383, 80);
        message.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
        p_1.add(message);

        // ---------------------------------------------------------------------------
        //�̸�Ƽ��
        ClassLoader classLoader = ChatClient.class.getClassLoader();
        ImageIcon sendImageIcon = new ImageIcon(classLoader.getResource("Image/smile.png"));

       
       // ImageIcon sendImageIcon = new ImageIcon("Image/smile.png"); // �̹��� ���� ��η� ����
        Image image = sendImageIcon.getImage();
        Image resizedImage = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);//�̹��� ������ ����
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        JButton sendImage = new JButton(resizedIcon);
        sendImage.setBorderPainted(false);//�׵θ� �����
        sendImage.setOpaque(false);//��� �����ϰ�
        sendImage.setContentAreaFilled(false);//���� ä��� ������ �����ϰ�

        sendImage.setFont(new Font("Dialog", Font.PLAIN, 17));
        sendImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFrame frame = new JFrame("�̸�Ƽ��");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setSize(400, 500);
                frame.setLocationRelativeTo(null); // â�� ȭ�� ����� ��ġ

                // �̹��� �迭
                String[] imagePaths = new String[12];
                for (int i = 1; i <= 12; i++) {
                    imagePaths[i - 1] = "Image/emogi" + i + ".png";
                }

                JPanel panel = new JPanel();
                for (String imagePath : imagePaths) {
                    ImageIcon icon = new ImageIcon(classLoader.getResource(imagePath));
                    Image image = icon.getImage();
                    Image resizedImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    ImageIcon resizedIcon = new ImageIcon(resizedImage);
                    JLabel label = new JLabel(resizedIcon);
                    final String path = imagePath; // imagePath�� final ������ ����

                    label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            cr_thread.requestEmoticon(path); // final ������ path�� ���
                            frame.dispose(); // �̸�Ƽ�� ���� â �ݱ�
                        }
                    });
                    panel.add(label);
                }

                frame.add(panel);

                // â�� ǥ���մϴ�
                frame.setVisible(true);
            }
        });
        sendImage.setBounds(15, 400, 35, 35);
        sendImage.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        p_1.add(sendImage);
        // ---------------------------------------------------------------------------

        c.add(p_1);

        JPanel p = new JPanel();
        p.setBounds(12, 6, 395, 178);
        getContentPane().add(p);
        p.setBackground(new Color(186, 206, 224));
        p.setLayout(null);
        p.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "������"));
        JScrollPane jsp2 = new JScrollPane();
        jsp2.setBounds(15, 25, 110, 135);
        p.add(jsp2);

        roomerInfo = new JList();
        jsp2.setViewportView(roomerInfo);
        roomerInfo.setFont(new Font("Dialog", Font.PLAIN, 17));

        //���� ����
        ImageIcon coerceOutIcon = new ImageIcon(classLoader.getResource("Image/redcard.png"));
      //  ImageIcon coerceOutIcon = new ImageIcon("Image/redcard.png"); // �̹��� ���� ��η� ����
        image = coerceOutIcon.getImage();
        resizedImage = image.getScaledInstance(55, 55, Image.SCALE_SMOOTH);//�̹��� ������ ����
        resizedIcon = new ImageIcon(resizedImage);
        coerceOut = new JButton(resizedIcon);
        coerceOut.setBounds(187, 25, 55, 55);
        coerceOut.setBorderPainted(false);
        coerceOut.setOpaque(false);
        coerceOut.setContentAreaFilled(false);
        p.add(coerceOut);
        coerceOut.setFont(font);
        coerceOut.addActionListener(this);


        //��������
        ImageIcon sendFileIcon = new ImageIcon(classLoader.getResource("Image/folder.png"));
      //  ImageIcon sendFileIcon = new ImageIcon("Image/folder.png"); // �̹��� ���� ��η� ����
        image = sendFileIcon.getImage();
        resizedImage = image.getScaledInstance(55, 55, Image.SCALE_SMOOTH);//�̹��� ������ ����
        resizedIcon = new ImageIcon(resizedImage);
        sendFile = new JButton(resizedIcon);
        sendFile.setBounds(187, 110, 55, 55);
        sendFile.setBorderPainted(false);
        sendFile.setOpaque(false);
        sendFile.setContentAreaFilled(false);
        p.add(sendFile);
        sendFile.setFont(font);
        sendFile.addActionListener(this);


        //�Ӹ� ������
        ImageIcon sendWordIcon = new ImageIcon(classLoader.getResource("Image/ear.png"));
     //   ImageIcon sendWordIcon = new ImageIcon("Image/ear.png"); // �̹��� ���� ��η� ����
        image = sendWordIcon.getImage();
        resizedImage = image.getScaledInstance(55, 55, Image.SCALE_SMOOTH);//�̹��� ������ ����
        resizedIcon = new ImageIcon(resizedImage);
        sendWord = new JButton(resizedIcon);
        sendWord.setBounds(286, 25, 55, 55);
        sendWord.setBorderPainted(false);
        sendWord.setOpaque(false);
        sendWord.setContentAreaFilled(false);
        p.add(sendWord);
        sendWord.setFont(font);
        sendWord.addActionListener(this);

        //����ϱ�
        ImageIcon quitRoomIcon = new ImageIcon(classLoader.getResource("Image/quitroom.png"));
       // ImageIcon quitRoomIcon = new ImageIcon("Image/quitroom.png"); // �̹��� ���� ��η� ����
        image = quitRoomIcon.getImage();
        resizedImage = image.getScaledInstance(55, 55, Image.SCALE_SMOOTH);//�̹��� ������ ����
        resizedIcon = new ImageIcon(resizedImage);
        quitRoom = new JButton(resizedIcon);
        quitRoom.setBounds(286, 110, 55, 55);
        quitRoom.setBorderPainted(false);
        quitRoom.setOpaque(false);
        quitRoom.setContentAreaFilled(false);
        p.add(quitRoom);
        quitRoom.setFont(font);
        quitRoom.addActionListener(this);
        roomerInfo.addListSelectionListener(this);

        Dimension dim = getToolkit().getScreenSize();
        setSize(436, 688);
        setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);
        show();

        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                message.requestFocusInWindow();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cr_thread.requestQuitRoom();
            }
        });
    }

    public void resetComponents() {
        messages.setText("");
        message.setText("");
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
                cr_thread.requestSendWordTo(data, idTo);
                message.setText("");

            } else {
                cr_thread.requestSendWord(words);
                message.requestFocusInWindow();
            }
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        isSelected = true;
        idTo = String.valueOf(((JList) e.getSource()).getSelectedValue());
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == coerceOut) {
            if (!isAdmin) {//������ �ƴ� ���
                JOptionPane.showMessageDialog(this, "����� ������ �ƴմϴ�.", "��������", JOptionPane.ERROR_MESSAGE);
            } else if (!isSelected) {//�����ε� ������ ���� ���
                JOptionPane.showMessageDialog(this, "�������� ID�� �����ϼ���.", "��������", JOptionPane.ERROR_MESSAGE);
            } else {//�����̰� ������ �� ���
                cr_thread.requestCoerceOut(idTo);
                isSelected = false;
            }
        } else if (ae.getSource() == quitRoom) {
            cr_thread.requestQuitRoom();
        }

        // �׼� �ӼӸ� ������ Ŭ�� �� ����
        else if (ae.getSource() == sendWord) {
            String idTo, data;
            while (true){
                idTo = JOptionPane.showInputDialog("���̵� �Է��ϼ���.");
                idTo = idTo.trim();
                idTo = idTo.replaceAll(" ","");
                if(!idTo.equals("")){
                    break;
                }
            }
            while (true) {
                data = JOptionPane.showInputDialog("�޼����� �Է��ϼ���.");
                data = data.trim();
                data = data.replaceAll(" ","");
                if (!data.equals("")) {
                    System.out.println(idTo);
                    System.out.println(data);
                    System.out.println(cr_thread.getLoginID());
                    if(!idTo.equals(cr_thread.getLoginID())){
                        cr_thread.requestSendWordTo(data, idTo);
                        break;
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "�ڱ� �ڽſ��� �޼����� ���� �� �����ϴ�!", "�ӼӸ�", JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                }
            }
        }

        else if (ae.getSource() == sendFile) {
            String idTo;
            if ((idTo = JOptionPane.showInputDialog("���� ���̵� �Է��ϼ���.")) != null) {
                cr_thread.requestSendFile(idTo);
            }
        }
    }

    public void stateChanged(ChangeEvent e) {
        jsp3.getVerticalScrollBar().setValue((jsp3.getVerticalScrollBar().getValue() + 20));
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }


}
