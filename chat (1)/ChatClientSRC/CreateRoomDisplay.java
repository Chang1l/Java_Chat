import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class CreateRoomDisplay extends JDialog implements ActionListener, ItemListener
{
  private ClientThread client;
  private String roomName, str_password;
  private int roomMaxUser, isRock;

  private JFrame main;
  private Container c;
  private JTextField tf;
  private JPanel radioPanel;
  private JPanel radioPanel_1;
  private JRadioButton radio1, radio2, radio3, radio4, rock, unrock;
  private JPasswordField password;
  private JButton ok, cancle;
  private JLabel label_1;
  private JLabel label_2;
  private JLabel label_3;

  public CreateRoomDisplay(JFrame frame, ClientThread client){
    super(frame, true);
    getContentPane().setBackground(new Color(186, 206, 224));
    setBackground(new Color(255, 255, 255));
    main = frame;
    setTitle("´ëÈ­¹æ °³¼³");
    this.client = client;
    isRock = 0;
    roomMaxUser = 2;
    str_password = "0";

    c = getContentPane();
    c.setLayout(null);

    JLabel label;
    label = new JLabel("¹æÁ¦¸ñ");
    label.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    label.setBounds(10, 10, 100, 20);
    label.setForeground(Color.BLACK);
    c.add(label);

    tf = new JTextField();
    tf.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    tf.setBounds(10, 40, 280, 30);
    c.add(tf);

    label_1 = new JLabel("ÃÖ´ëÀÎ¿ø");
    label_1.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    label_1.setForeground(Color.BLACK);
    label_1.setBounds(10, 80, 100, 20);
    label_1.setBackground(new Color(186, 206, 224));
    c.add(label_1);

    radioPanel = new JPanel();
    radioPanel.setBackground(new Color(186, 206, 224));
    radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
    ButtonGroup group = new ButtonGroup();
    radio1 = new JRadioButton("2¸í");
    radio1.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    radio1.setBackground(new Color(186, 206, 224));
    radio1.setSelected(true);
    radio1.addItemListener(this);
    group.add(radio1);
    radio2 = new JRadioButton("3¸í");
    radio2.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    radio2.setBackground(new Color(186, 206, 224));
    radio2.addItemListener(this);
    group.add(radio2);
    radio3 = new JRadioButton("4¸í");
    radio3.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    radio3.setBackground(new Color(186, 206, 224));
    radio3.addItemListener(this);
    group.add(radio3);
    radio4 = new JRadioButton("5¸í");
    radio4.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    radio4.setBackground(new Color(186, 206, 224));
    radio4.addItemListener(this);
    group.add(radio4);
    radioPanel.add(radio1);
    radioPanel.add(radio2);
    radioPanel.add(radio3);
    radioPanel.add(radio4);
    radioPanel.setBounds(10, 110, 280, 30);
    c.add(radioPanel);

    label_2 = new JLabel("°ø°³¿©ºÎ");
    label_2.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    label_2.setForeground(Color.BLACK);
    label_2.setBounds(10, 150, 100, 20);
    c.add(label_2);

    radioPanel_1 = new JPanel();
    radioPanel_1.setBackground(new Color(186, 206, 224));
    radioPanel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
    group = new ButtonGroup();
    unrock = new JRadioButton("°ø°³");
    unrock.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    unrock.setBackground(new Color(186, 206, 224));
    unrock.setSelected(true);
    unrock.addItemListener(this);
    group.add(unrock);
    rock = new JRadioButton("ºñ°ø°³");
    rock.setBackground(new Color(186, 206, 224));
    rock.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    rock.addItemListener(this);
    group.add(rock);
    radioPanel_1.add(unrock);
    radioPanel_1.add(rock);
    radioPanel_1.setBounds(10, 180, 280, 30);
    c.add(radioPanel_1);

    label_3 = new JLabel("ºñ¹Ð¹øÈ£");
    label_3.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    label_3.setForeground(Color.BLACK);
    label_3.setBounds(10, 220, 100, 20);
    c.add(label_3);

    password = new JPasswordField();
    password.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    password.setBounds(10, 250, 280, 30);
    password.setEditable(false);
    c.add(password);

    ok = new JButton("È® ÀÎ");
    ok.setBackground(new Color(254, 229, 0));
    ok.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    ok.setForeground(new Color(0, 0, 0));
    ok.setBounds(61, 300, 80, 30);
    ok.addActionListener(this);
    c.add(ok);

    cancle = new JButton("Ãë ¼Ò");
    cancle.setBackground(new Color(255, 255, 255));
    cancle.setFont(new Font("³ª´®°íµñ", Font.PLAIN, 17));
    cancle.setForeground(new Color(0, 0, 0));
    cancle.setBounds(156, 300, 80, 30);
    cancle.addActionListener(this);
    c.add(cancle);

    Dimension dim = getToolkit().getScreenSize();
    setSize(330, 400);
    setLocation(dim.width/2 - getWidth()/2,
            dim.height/2 - getHeight()/2);
    show();

    addWindowListener(
            new WindowAdapter() {
              public void windowActivated(WindowEvent e) {
                tf.requestFocusInWindow();
              }
            }
    );

    addWindowListener(
            new WindowAdapter(){
              public void windowClosing(WindowEvent e){
                dispose();
              }
            }
    );
  }

  public void itemStateChanged(ItemEvent ie){
    if (ie.getSource() == unrock){
      isRock = 0;
      str_password = "0";
      password.setText("");
      password.setEditable(false);
    } else if (ie.getSource() == rock) {
      isRock = 1;
      password.setEditable(true);
    } else if (ie.getSource() == radio1) {
      roomMaxUser = 2;
    } else if (ie.getSource() == radio2) {
      roomMaxUser = 3;
    } else if (ie.getSource() == radio3) {
      roomMaxUser = 4;
    } else if (ie.getSource() == radio4) {
      roomMaxUser = 5;
    }
  }

  public void actionPerformed(ActionEvent ae){
    if(ae.getSource() == ok){
      if(tf.getText().equals("")){
        JOptionPane.showMessageDialog(main, "¹æÁ¦¸ñÀ» ÀÔ·ÂÇÏ¼¼¿ä",
                "´ëÈ­¹æ °³¼³.", JOptionPane.ERROR_MESSAGE);
      } else {
        roomName = tf.getText();
        if(isRock == 1){
          str_password = password.getText();
        }
        if(isRock ==1 && str_password.equals("")){
          JOptionPane.showMessageDialog(main, "ºñ¹Ð¹øÈ£¸¦ ÀÔ·ÂÇÏ¼¼¿ä",
                  "´ëÈ­¹æ °³¼³.", JOptionPane.ERROR_MESSAGE);
        } else {
          client.requestCreateRoom(roomName, roomMaxUser,
                  isRock, str_password);
          dispose();
        }
      }
    } else {
      dispose();
    }
  }
}
