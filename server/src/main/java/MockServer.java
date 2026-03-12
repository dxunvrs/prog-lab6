import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class MockServer {
    public static void main(String[] args) {
        int port = 1234;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Мок сервера запущен. Ожидание...");

            byte[] buffer = new byte[65535];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String receivedJson = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                System.out.println("Новый запрос");
                System.out.println("От: " + packet.getSocketAddress());
                System.out.println("JSON: " + receivedJson);

                String responseJson = "{\"message\":\"JSON получен\",\"success\":true}";
                byte[] responseData = responseJson.getBytes(StandardCharsets.UTF_8);

                DatagramPacket responsePacket = new DatagramPacket(
                        responseData, responseData.length, packet.getSocketAddress()
                );
                socket.send(responsePacket);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
