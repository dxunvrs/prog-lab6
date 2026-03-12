package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import network.Request;
import network.Response;

import java.io.IOException;
import java.net.*;

public class ConnectionManager {
    private final String host;
    private final int port;
    private final DatagramSocket socket;
    private final ObjectMapper mapper;

    private static final int MAX_ATTEMPTS = 5;
    private static final int TIMEOUT = 3000;
    private static final int PACKET_SIZE = 65535;

    public ConnectionManager(String host, int port) throws SocketException {
        this.host = host;
        this.port = port;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(TIMEOUT);
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public Response sendAndReceive(Request request) {
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            try {
                byte[] sendData = mapper.writeValueAsBytes(request);
                InetAddress address = InetAddress.getByName(host);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);

                socket.send(sendPacket);

                byte[] receiveBuffer = new byte[PACKET_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                return mapper.readValue(receivePacket.getData(), Response.class);
            } catch (SocketTimeoutException e) {
                attempts++;
                System.out.println("Сервер не отвечает");
                if (attempts < MAX_ATTEMPTS) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {}
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Пока");
                break;
            }
        }

        return new Response("Не удалось получить ответ", false);
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
