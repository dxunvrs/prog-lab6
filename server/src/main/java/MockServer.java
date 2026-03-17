import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commands.CommandDef;
import commands.CommandType;
import network.Request;
import network.RequestType;
import network.Response;
import network.ResponseType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class MockServer {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Map<String, CommandDef> commands = new HashMap<>();
        // commands.put("test", new CommandDef("test", "test - команда от сервера", 0, CommandType.NO_ARGS));
        commands.put("add", new CommandDef("add", "add - тестовое добавление", 0, CommandType.OBJECT_ARG));

        int port = 1234;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Мок сервера запущен. Ожидание...");

            byte[] buffer = new byte[65535];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                Request request = mapper.readValue(packet.getData(), Request.class);

                System.out.println("Новый запрос");
                System.out.println("От: " + packet.getSocketAddress());
                System.out.println("JSON: " + new String(packet.getData()));

                Response response;
                if (request.getType() == RequestType.SYNC) {
                    response = new Response(ResponseType.SYNC_DATA, "Синхронизация команд");
                    response.setSyncData(commands);
                } else if (request.getType() == RequestType.SERVER_COMMAND) {
                    response = new Response(ResponseType.OK, "Старания были не напрасны, команда типо выполнилась");
                } else {
                    response = new Response(ResponseType.ERROR, "Неизвестный тип запроса");
                }

                byte[] responseData = mapper.writeValueAsBytes(response);

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
