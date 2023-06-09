import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ImageSender {

    public static void main(String[] args) {
        String serverAddress = "���� IP �ּ�";
        int serverPort = 2777;
        String imagePath = "������ �̹��� ���� ���";

        try {
            // ������ ���� ����
            Socket socket = new Socket(serverAddress, serverPort);

            // �̹��� ������ �о ������ ���� ������ ����
            File imageFile = new File(imagePath);
            byte[] buffer = new byte[(int) imageFile.length()];
            FileInputStream fis = new FileInputStream(imageFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(buffer, 0, buffer.length);

            OutputStream os = socket.getOutputStream();
            os.write(buffer, 0, buffer.length);
            os.flush();

            // ���ϰ� ���� ���� ���ҽ� ����
            bis.close();
            fis.close();
            socket.close();

            System.out.println("�̹��� ���� �Ϸ�");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}