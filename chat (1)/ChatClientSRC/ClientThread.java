import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class ClientThread extends Thread {
    private WaitRoomDisplay ct_waitRoom;
    private ChatRoomDisplay ct_chatRoom;
    private Socket ct_sock;
    private DataInputStream ct_in;
    private DataOutputStream ct_out;
    private StringBuffer ct_buffer;
    private Thread thisThread;
    private String ct_logonID;
    private int ct_roomNumber;
    private static MessageBox msgBox, logonbox, fileTransBox;
    private static final String SEPARATOR = "|";
    private static final String DELIMETER = "'";
    private static final String DELIMETER2 = "=";


    private static final int REQ_LOGON = 1001;
    private static final int REQ_CREATEROOM = 1011;
    private static final int REQ_ENTERROOM = 1021;
    private static final int REQ_QUITROOM = 1031;
    private static final int REQ_LOGOUT = 1041;
    private static final int REQ_SENDWORD = 1051;
    private static final int REQ_SENDWORDTO = 1052;
    private static final int REQ_COERCEOUT = 1053;
    private static final int REQ_SENDFILE = 1061;
    private static final int REQ_EMOT = 1071;

    private static final int YES_LOGON = 2001;
    private static final int NO_LOGON = 2002;
    private static final int YES_CREATEROOM = 2011;
    private static final int NO_CREATEROOM = 2012;
    private static final int YES_ENTERROOM = 2021;
    private static final int NO_ENTERROOM = 2022;
    private static final int YES_QUITROOM = 2031;
    private static final int YES_LOGOUT = 2041;
    private static final int YES_SENDWORD = 2051;
    private static final int YES_SENDWORDTO = 2052;
    private static final int NO_SENDWORDTO = 2053;
    private static final int YES_COERCEOUT = 2054;
    private static final int YES_SENDFILE = 2061;
    private static final int NO_SENDFILE = 2062;
    private static final int YES_EMOT = 2071;
    private static final int MDY_WAITUSER = 2003;
    private static final int MDY_WAITINFO = 2013;
    private static final int MDY_ROOMUSER = 2023;
    private static final int ERR_ALREADYUSER = 3001;
    private static final int ERR_SERVERFULL = 3002;
    private static final int ERR_ROOMSFULL = 3011;
    private static final int ERR_ROOMERFULL = 3021;
    private static final int ERR_PASSWORD = 3022;
    private static final int ERR_REJECTION = 3031;
    private static final int ERR_NOUSER = 3032;

    public ClientThread() {
        ct_waitRoom = new WaitRoomDisplay(this);
        ct_chatRoom = null;
        try {
            ct_sock = new Socket(InetAddress.getLocalHost(), 2777);
            ct_in = new DataInputStream(ct_sock.getInputStream());
            ct_out = new DataOutputStream(ct_sock.getOutputStream());
            ct_buffer = new StringBuffer(4096);
            thisThread = this;
        } catch (IOException e) {
            MessageBoxLess msgout = new MessageBoxLess(ct_waitRoom, "연결에러",
                    "서버에 접속할 수 없습니다.");
            msgout.show();
        }
    }

    public ClientThread(String hostaddr) {
        ct_waitRoom = new WaitRoomDisplay(this);
        ct_chatRoom = null;
        try {
            ct_sock = new Socket(hostaddr, 2777);
            ct_in = new DataInputStream(ct_sock.getInputStream());
            ct_out = new DataOutputStream(ct_sock.getOutputStream());
            ct_buffer = new StringBuffer(4096);
            thisThread = this;
        } catch (IOException e) {
            MessageBoxLess msgout = new MessageBoxLess(ct_waitRoom, "연결에러",
                    "서버에 접속할 수 없습니다.");
            msgout.show();
        }
    }

    public String getLoginID(){
        return ct_logonID;
    }

    public void run() {
        try {
            Thread currThread = Thread.currentThread();
            while (currThread == thisThread) {
                String recvData = ct_in.readUTF();
                StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
                int command = Integer.parseInt(st.nextToken());
                switch (command) {
                    case YES_LOGON: {
                        logonbox.dispose();

                        ct_roomNumber = 0;
                        try {
                            StringTokenizer st1 = new StringTokenizer(st.nextToken(), DELIMETER);
                            Vector roomInfo = new Vector();
                            while (st1.hasMoreTokens()) {
                                String temp = st1.nextToken();
                                if (!temp.equals("empty")) {
                                    roomInfo.addElement(temp);
                                }
                            }
                            ct_waitRoom.roomInfo.setListData(roomInfo);
                            ct_waitRoom.setTitle("대기실 - "+ct_logonID);
                            ct_waitRoom.message.requestFocusInWindow();
                        } catch (NoSuchElementException e) {
                            ct_waitRoom.message.requestFocusInWindow();
                        }
                        break;
                    }
                    case NO_LOGON: {
                        String id;
                        int errCode = Integer.parseInt(st.nextToken());
                        if (errCode == ERR_ALREADYUSER) {
                            logonbox.dispose();
                            JOptionPane.showMessageDialog(ct_waitRoom, "이미 다른 사용자가 있습니다.",
                                    "로그온", JOptionPane.ERROR_MESSAGE);
                            id = ChatClient.getLogonID();
                            requestLogon(id);
                        } else if (errCode == ERR_SERVERFULL) {
                            logonbox.dispose();
                            JOptionPane.showMessageDialog(ct_waitRoom, "대기방이 만원입니다.",
                                    "로그온", JOptionPane.ERROR_MESSAGE);
                            id = ChatClient.getLogonID();
                            requestLogon(id);
                        }
                        break;
                    }
                    case MDY_WAITUSER: {
                        StringTokenizer st1 = new StringTokenizer(st.nextToken(), DELIMETER);
                        Vector user = new Vector();
                        while (st1.hasMoreTokens()) {
                            user.addElement(st1.nextToken());
                        }
                        ct_waitRoom.waiterInfo.setListData(user);
                        ct_waitRoom.message.requestFocusInWindow();
                        break;
                    }
                    case YES_CREATEROOM: {
                        ct_roomNumber = Integer.parseInt(st.nextToken());
                        ct_waitRoom.hide();
                        if (ct_chatRoom == null) {
                            ct_chatRoom = new ChatRoomDisplay(this);
                            ct_chatRoom.isAdmin = true;
                        } else {
                            ct_chatRoom.show();
                            ct_chatRoom.isAdmin = true;
                            ct_chatRoom.resetComponents();
                        }
                        break;
                    }
                    case NO_CREATEROOM: {
                        int errCode = Integer.parseInt(st.nextToken());
                        if (errCode == ERR_ROOMSFULL) {
                            msgBox = new MessageBox(ct_waitRoom, "채팅방개설",
                                    "더 이상 채팅방을 개설 할 수 없습니다.");
                            msgBox.show();
                        }
                        break;
                    }
                    case MDY_WAITINFO: {
                        StringTokenizer st1 = new StringTokenizer(st.nextToken(), DELIMETER);
                        StringTokenizer st2 = new StringTokenizer(st.nextToken(), DELIMETER);

                        Vector rooms = new Vector();
                        Vector users = new Vector();
                        while (st1.hasMoreTokens()) {
                            String temp = st1.nextToken();
                            if (!temp.equals("empty")) {
                                rooms.addElement(temp);
                            }
                        }
                        ct_waitRoom.roomInfo.setListData(rooms);

                        while (st2.hasMoreTokens()) {
                            users.addElement(st2.nextToken());
                        }

                        ct_waitRoom.waiterInfo.setListData(users);
                        ct_waitRoom.message.requestFocusInWindow();

                        break;
                    }
                    case YES_ENTERROOM: {
                        ct_roomNumber = Integer.parseInt(st.nextToken());
                        String id = st.nextToken();
                        ct_waitRoom.hide();
                        if (ct_chatRoom == null) {
                            ct_chatRoom = new ChatRoomDisplay(this);
                        } else {
                            ct_chatRoom.show();
                            ct_chatRoom.resetComponents();
                        }
                        break;
                    }
                    case NO_ENTERROOM: {
                        int errCode = Integer.parseInt(st.nextToken());
                        if (errCode == ERR_ROOMERFULL) {
                            msgBox = new MessageBox(ct_waitRoom, "대화방입장",
                                    "대화방이 만원입니다.");
                            msgBox.show();
                        } else if (errCode == ERR_PASSWORD) {
                            msgBox = new MessageBox(ct_waitRoom, "대화방입장",
                                    "비밀번호가 틀립니다.");
                            msgBox.show();
                        }
                        break;
                    }
                    case MDY_ROOMUSER: {
                        String id = st.nextToken();
                        int code = Integer.parseInt(st.nextToken());

                        StringTokenizer st1 = new StringTokenizer(st.nextToken(), DELIMETER);
                        Vector<String> user = new Vector<>();
                        while (st1.hasMoreTokens()) {
                            user.addElement(st1.nextToken());
                        }
                        ct_chatRoom.setTitle("채팅방 - " + ct_logonID);
                        ct_chatRoom.roomerInfo.setListData(user);

                        String message;
                        if (code == 1) {
                            message = "### " + id + "님이 입장하셨습니다. ###\n";
                        } else if (code == 2) {
                            message = "### " + id + "님이 강제퇴장 되었습니다. ###\n";
                        } else {
                            message = "### " + id + "님이 퇴장하셨습니다. ###\n";
                        }

                        try {
                            ct_chatRoom.messages.getDocument().insertString(ct_chatRoom.messages.getDocument().getLength(), message, null);
                            ct_chatRoom.message.requestFocusInWindow();
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }

                        break;
                    }
                    case YES_QUITROOM: {
                        String id = st.nextToken();
                        if (ct_chatRoom.isAdmin) ct_chatRoom.isAdmin = false;
                        ct_chatRoom.hide();
                        ct_waitRoom.show();
                        ct_waitRoom.resetComponents();
                        ct_roomNumber = 0;
                        break;
                    }
                    case YES_LOGOUT: {
                        ct_waitRoom.dispose();
                        if (ct_chatRoom != null) {
                            ct_chatRoom.dispose();
                        }
                        release();
                        break;
                    }
                    case YES_SENDWORD: {
                        String id = st.nextToken();
                        int roomNumber = Integer.parseInt(st.nextToken());
                        try {
                            String data = st.nextToken();
                            String time = st.nextToken();
                            if (roomNumber == 0) {
                                String value = "[" + time + "] " + id + " : " + data + "\n";
                                StyledDocument doc = ct_waitRoom.messages.getStyledDocument();
                                doc.insertString(doc.getLength(), value, null);
                                if (id.equals(ct_logonID)) {
                                    ct_waitRoom.message.setText("");
                                    ct_waitRoom.message.requestFocusInWindow();
                                }
                                ct_waitRoom.message.requestFocusInWindow();
                            } else {
                                StyledDocument doc = ct_chatRoom.messages.getStyledDocument();
                                SimpleAttributeSet messageStyle = new SimpleAttributeSet();
                                doc.insertString(doc.getLength(), "[" + time + "] " + id + " : ", null);
                                doc.insertString(doc.getLength(), data + "\n", messageStyle);
                                if (id.equals(ct_logonID)) {
                                    ct_chatRoom.message.setText("");
                                }
                                ct_chatRoom.message.requestFocusInWindow();
                            }
                        } catch (NoSuchElementException | BadLocationException e) {
                            if (roomNumber == 0) ct_waitRoom.message.requestFocusInWindow();
                            else ct_chatRoom.message.requestFocusInWindow();
                        }
                        break;
                    }
                    case YES_SENDWORDTO: {
                        String id = st.nextToken();
                        String idTo = st.nextToken();
                        int roomNumber = Integer.parseInt(st.nextToken());
                        try {
                            String data = st.nextToken();
                            String time = st.nextToken();
                            if (roomNumber == 0) {
                                if (id.equals(ct_logonID)) {
                                    ct_waitRoom.message.setText("");
                                    StyledDocument doc = ct_waitRoom.messages.getStyledDocument();
                                    SimpleAttributeSet messageStyle = new SimpleAttributeSet();
                                    messageStyle.addAttribute(StyleConstants.Foreground, Color.MAGENTA);
                                    doc.insertString(doc.getLength(), "[" + time + "] " +"귓속말 " + id + "-> "+ idTo + " : ", messageStyle);
                                    doc.insertString(doc.getLength(), data + "\n", messageStyle);

                                } else {
                                    ct_waitRoom.message.setText("");
                                    StyledDocument doc = ct_waitRoom.messages.getStyledDocument();
                                    SimpleAttributeSet messageStyle = new SimpleAttributeSet();
                                    messageStyle.addAttribute(StyleConstants.Foreground, Color.MAGENTA);
                                    doc.insertString(doc.getLength(), "[" + time + "] " +"귓속말 " + id + "-> "+ idTo + " : ", messageStyle);
                                    doc.insertString(doc.getLength(), data + "\n", messageStyle);
                                }
                                ct_waitRoom.message.requestFocusInWindow();
                            } else {

                                if (id.equals(ct_logonID)) {
                                    ct_chatRoom.message.setText("");
                                    StyledDocument doc = ct_chatRoom.messages.getStyledDocument();
                                    SimpleAttributeSet messageStyle = new SimpleAttributeSet();
                                    messageStyle.addAttribute(StyleConstants.Foreground, Color.MAGENTA);

                                    //doc.insertString(doc.getLength(), "[" + time + "] 귓속말<to:" + idTo + "> : ", null);
                                    doc.insertString(doc.getLength(), "[" + time + "] " +"귓속말 " + id + "-> "+ idTo + " : ", messageStyle);
                                    doc.insertString(doc.getLength(), data + "\n", messageStyle);
                                } else {
                                    StyledDocument doc = ct_chatRoom.messages.getStyledDocument();
                                    SimpleAttributeSet messageStyle = new SimpleAttributeSet();
                                    messageStyle.addAttribute(StyleConstants.Foreground, Color.MAGENTA);

                                    //doc.insertString(doc.getLength(), "[" + time + "] 귓속말<from:" + id + "> : ", null);
                                    doc.insertString(doc.getLength(), "[" + time + "] " +"귓속말 " + id + "-> "+ idTo + " : ", messageStyle);
                                    doc.insertString(doc.getLength(), data + "\n", messageStyle);
                                }
                                ct_chatRoom.message.requestFocusInWindow();
                            }
                        } catch (NoSuchElementException  | BadLocationException e) {
                            if (roomNumber == 0) ct_waitRoom.message.requestFocusInWindow();
                            else ct_chatRoom.message.requestFocusInWindow();
                        }
                        break;
                    }
                    case NO_SENDWORDTO: {
                        String id = st.nextToken();
                        int roomNumber = Integer.parseInt(st.nextToken());
                        String message = "";
                        if (roomNumber == 0) {
                            message = "대기실에 " + id + "님이 존재하지 않습니다.";
                            JOptionPane.showMessageDialog(ct_waitRoom, message,
                                    "귓속말 에러", JOptionPane.ERROR_MESSAGE);
                        } else {
                            message = "이 대화방에 " + id + "님이 존재하지 않습니다.";
                            JOptionPane.showMessageDialog(ct_chatRoom, message,
                                    "귓속말 에러", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    }
                    case REQ_SENDFILE: {
                        String id = st.nextToken();
                        int roomNumber = Integer.parseInt(st.nextToken());
                        String message = id + "로 부터 파일전송을 수락하시겠습니까?";
                        int value = JOptionPane.showConfirmDialog(ct_chatRoom, message,
                                "파일수신", JOptionPane.YES_NO_OPTION);
                        if (value == 1) {
                            try {
                                ct_buffer.setLength(0);
                                ct_buffer.append(NO_SENDFILE);
                                ct_buffer.append(SEPARATOR);
                                ct_buffer.append(ct_logonID);
                                ct_buffer.append(SEPARATOR);
                                ct_buffer.append(roomNumber);
                                ct_buffer.append(SEPARATOR);
                                ct_buffer.append(id);
                                send(ct_buffer.toString());
                            } catch (IOException e) {
                                System.out.println(e);
                            }
                        } else {
                            StringTokenizer addr = new StringTokenizer(InetAddress.getLocalHost().toString(), "/");
                            String hostname = "";
                            String hostaddr = "";

                            hostname = addr.nextToken();
                            try {
                                hostaddr = addr.nextToken();
                            } catch (NoSuchElementException err) {
                                hostaddr = hostname;
                            }

                            try {
                                ct_buffer.setLength(0);
                                ct_buffer.append(YES_SENDFILE);
                                ct_buffer.append(SEPARATOR);
                                ct_buffer.append(ct_logonID);
                                ct_buffer.append(SEPARATOR);
                                ct_buffer.append(roomNumber);
                                ct_buffer.append(SEPARATOR);
                                ct_buffer.append(id);
                                ct_buffer.append(SEPARATOR);
                                ct_buffer.append(hostaddr);
                                send(ct_buffer.toString());
                            } catch (IOException e) {
                                System.out.println(e);
                            }
                            // 파일 수신 서버실행.
                            new ReciveFile();
                        }
                        break;
                    }
                    case NO_SENDFILE: {
                        int code = Integer.parseInt(st.nextToken());
                        String id = st.nextToken();
                        fileTransBox.dispose();

                        if (code == ERR_REJECTION) {
                            String message = id + "님이 파일수신을 거부하였습니다.";
                            JOptionPane.showMessageDialog(ct_chatRoom, message,
                                    "파일전송", JOptionPane.ERROR_MESSAGE);
                            break;
                        } else if (code == ERR_NOUSER) {
                            String message = id + "님은 이 방에 존재하지 않습니다.";
                            JOptionPane.showMessageDialog(ct_chatRoom, message,
                                    "파일전송", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    }
                    case YES_SENDFILE: {
                        String id = st.nextToken();
                        String addr = st.nextToken();

                        fileTransBox.dispose();
                        // 파일 송신 클라이언트 실행.
                        new SendFile(addr);
                        break;
                    }
                    case YES_COERCEOUT: {
                        ct_chatRoom.hide();
                        ct_waitRoom.show();
                        ct_waitRoom.resetComponents();
                        ct_roomNumber = 0;
                        StyledDocument doc = ct_waitRoom.messages.getStyledDocument();
                        doc.insertString(doc.getLength(),"### 방장에 의해 강제퇴장 되었습니다. ###\n",null);
                        break;
                    }
                    //추가
                    case YES_EMOT: {
                        String id = st.nextToken();
                        String imgpath = st.nextToken();
                        int roomNumber = Integer.parseInt(st.nextToken());
                        String time = st.nextToken();
                        try {
                            SimpleAttributeSet messageStyle = new SimpleAttributeSet();
                            if (roomNumber == 0) {
                                // ct_waitRoom에 대한 처리
                            } else {
                                ct_chatRoom.message.setText("");
                                // 이미지 아이콘을 생성하여 스타일에 추가
                                ImageIcon imageIcon = new ImageIcon(imgpath);
                                StyleConstants.setIcon(messageStyle, imageIcon);
                                StyledDocument doc = ct_chatRoom.messages.getStyledDocument();

                                // id와 time 출력
                                doc.insertString(doc.getLength(),  "[" + time + "]"+ id +": \n", null);

                                // 이미지를 추가하고 줄바꿈을 삽입
                                doc.insertString(doc.getLength(), " ", messageStyle);
                                doc.insertString(doc.getLength(), "\n", null);

                                ct_chatRoom.message.requestFocusInWindow();
                            }
                        } catch (BadLocationException e) {
                            if (roomNumber != 0) {
                                ct_chatRoom.message.requestFocusInWindow();
                            }
                        }
                        break;
                    }

                }
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            System.out.println(e);
            release();
        } catch (IOException e) {
            System.out.println(e);
            release();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestLogon(String id) {
        try {
            logonbox = new MessageBox(ct_waitRoom, "로그온", "서버에 로그온 중입니다.");
            logonbox.show();
            ct_logonID = id;
            ct_buffer.setLength(0);
            System.out.println(1);
            ct_buffer.append(REQ_LOGON);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(id);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void requestCreateRoom(String roomName, int roomMaxUser,
                                  int isRock, String password) {
        try {
            ct_buffer.setLength(0);
            System.out.println(2);
            ct_buffer.append(REQ_CREATEROOM);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_logonID);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(roomName);
            ct_buffer.append(DELIMETER);
            ct_buffer.append(roomMaxUser);
            ct_buffer.append(DELIMETER);
            ct_buffer.append(isRock);
            ct_buffer.append(DELIMETER);
            ct_buffer.append(password);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void requestEnterRoom(int roomNumber, String password) {
        try {
            ct_buffer.setLength(0);
            System.out.println(3);
            ct_buffer.append(REQ_ENTERROOM);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_logonID);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(roomNumber);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(password);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void requestQuitRoom() {
        try {
            ct_buffer.setLength(0);
            System.out.println(4);
            ct_buffer.append(REQ_QUITROOM);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_logonID);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_roomNumber);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void requestLogout() {
        try {
            ct_buffer.setLength(0);
            System.out.println(5);
            ct_buffer.append(REQ_LOGOUT);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_logonID);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void requestSendWord(String data) {
        try {
            ct_buffer.setLength(0);
            System.out.println(6);
            ct_buffer.append(REQ_SENDWORD);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_logonID);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_roomNumber);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(data);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void requestSendWordTo(String data, String idTo) {
        try {
            ct_buffer.setLength(0);
            System.out.println(7);
            ct_buffer.append(REQ_SENDWORDTO);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_logonID);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_roomNumber);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(idTo);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(data);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void requestCoerceOut(String idTo) {
        try {
            ct_buffer.setLength(0);
            System.out.println(8);
            ct_buffer.append(REQ_COERCEOUT);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_roomNumber);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(idTo);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void requestSendFile(String idTo) {
        fileTransBox = new MessageBox(ct_chatRoom, "파일전송", "상대방의 승인을 기다립니다.");
        fileTransBox.show();
        try {
            ct_buffer.setLength(0);
            System.out.println(9);
            ct_buffer.append(REQ_SENDFILE);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_logonID);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_roomNumber);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(idTo);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    //이모티콘 프로토콜 메소드추가
    public void requestEmoticon(String imgpath) {

        try {
            ct_buffer.setLength(0);
            System.out.println(10);
            ct_buffer.append(REQ_EMOT);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_logonID);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(ct_roomNumber);
            ct_buffer.append(SEPARATOR);
            ct_buffer.append(imgpath);
            send(ct_buffer.toString());
        } catch (IOException e) {
            System.out.println(e);
        }
    }



    private void send(String sendData) throws IOException {
        ct_out.writeUTF(sendData);
        ct_out.flush();
    }

    public void release() {
        if (thisThread != null) {
            thisThread = null;
        }
        try {
            if (ct_out != null) {
                ct_out.close();
            }
        } catch (IOException e) {
        } finally {
            ct_out = null;
        }
        try {
            if (ct_in != null) {
                ct_in.close();
            }
        } catch (IOException e) {
        } finally {
            ct_in = null;
        }
        try {
            if (ct_sock != null) {
                ct_sock.close();
            }
        } catch (IOException e) {
        } finally {
            ct_sock = null;
        }
        System.exit(0);
    }
}

