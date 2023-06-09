import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ImageSender {

    public static void main(String[] args) {
        String serverAddress = "서버 IP 주소";
        int serverPort = 2777;
        String imagePath = "전송할 이미지 파일 경로";

        try {
            // 서버에 소켓 연결
            Socket socket = new Socket(serverAddress, serverPort);

            // 이미지 파일을 읽어서 소켓을 통해 서버로 전송
            File imageFile = new File(imagePath);
            byte[] buffer = new byte[(int) imageFile.length()];
            FileInputStream fis = new FileInputStream(imageFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(buffer, 0, buffer.length);

            OutputStream os = socket.getOutputStream();
            os.write(buffer, 0, buffer.length);
            os.flush();

            // 소켓과 파일 관련 리소스 정리
            bis.close();
            fis.close();
            socket.close();

            System.out.println("이미지 전송 완료");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}