package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commands.CommandDef;
import commands.CommandType;
import network.Request;
import network.RequestType;
import network.Response;
import network.ResponseType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConnectionManager implements AutoCloseable {
    private final int port;
    private DatagramChannel channel;
    private Selector selector;
    private final ObjectMapper mapper;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(65535);

    private boolean isWorking = true;

    public ConnectionManager(int port) {
        this.port = port;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public void start() throws IOException {
        selector = Selector.open();
        channel = DatagramChannel.open();

        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));

        channel.register(selector, SelectionKey.OP_READ);

        System.out.println("Сервер запущен на порту " + port);

        while (isWorking) {
            if (selector.select() == 0) continue;

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid()) continue;

                if (key.isReadable()) {
                    read(key);
                }
            }
        }
    }

    private void read(SelectionKey key) {
        DatagramChannel chan = (DatagramChannel) key.channel();
        try {
            readBuffer.clear();
            SocketAddress clientAddress = chan.receive(readBuffer);

            if (clientAddress == null) return;

            readBuffer.flip();
            byte[] data = new byte[readBuffer.remaining()];
            readBuffer.get(data);

            Request request = mapper.readValue(data, Request.class);
            System.out.println("Новый запрос");
            System.out.println("От: " + clientAddress);
            System.out.println("JSON: " + new String(data));


            Map<String, CommandDef> commands = new HashMap<>();
            // commands.put("test", new CommandDef("test", "test - команда от сервера", 0, CommandType.NO_ARGS));
            commands.put("add", new CommandDef("add", "add - тестовое добавление", 0, CommandType.OBJECT_ARG));

            Response response;
            if (request.getType() == RequestType.SYNC) {
                response = new Response(ResponseType.SYNC_DATA, "Синхронизация команд");
                response.setSyncData(commands);
            } else if (request.getType() == RequestType.SERVER_COMMAND) {
                response = new Response(ResponseType.OK, "Старания были не напрасны, команда типо выполнилась");
            } else {
                response = new Response(ResponseType.ERROR, "Неизвестный тип запроса");
            }

            send(clientAddress, response);

        } catch (IOException e) {
            System.out.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }

    private void send(SocketAddress clientAddress, Response response) throws IOException {
        byte[] data = mapper.writeValueAsBytes(response);
        ByteBuffer buffer = ByteBuffer.wrap(data);

        channel.send(buffer, clientAddress);
    }

    public void stop() {
        isWorking = false;
        selector.wakeup();
    }

    @Override
    public void close() {
        try {
            if (selector != null && channel.isOpen()) selector.close();
            if (channel != null && channel.isOpen()) channel.close();
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }
}
