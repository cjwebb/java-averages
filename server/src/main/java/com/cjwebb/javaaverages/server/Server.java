package com.cjwebb.javaaverages.server;

import com.cjwebb.javaaverages.common.Message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    protected static final Logger logger = LogManager.getLogger();

    private DatagramChannel channel;
    private Selector selector;

    private final Map<String, Set<InetSocketAddress>> subscriptions = new ConcurrentHashMap<>();

    public void start(int port) throws IOException {
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));

        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        logger.info("Server started on port {}", port);

        processMessages();
    }

    private void processMessages() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        while (true) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isReadable()) {
                    DatagramChannel ch = (DatagramChannel) key.channel();
                    buffer.clear();
                    InetSocketAddress clientAddress = (InetSocketAddress) ch.receive(buffer);

                    buffer.flip();
                    String message = StandardCharsets.UTF_8.decode(buffer).toString();
                    handleMessage(message, clientAddress);
                }
            }
        }
    }

    private void handleMessage(String message, InetSocketAddress clientAddress) throws IOException {
        String[] parts = message.split("\\|");
        String command = parts[0].toUpperCase();

        logger.info(message);

        switch (command) {
            case "SUBSCRIBE":
                handleSubscription(message, clientAddress);
                break;
            case "PUBLISH":
                handlePublication(parts, clientAddress);
                break;
            default:
                sendResponse(clientAddress, "ERROR|Unknown command");
        }
    }

    private void handleSubscription(String message, InetSocketAddress clientAddress) throws IOException {
        try {
            Message msg = Message.parse(message);

            // TODO - store which stats functions are required.

            subscriptions.computeIfAbsent(msg.getTopic(), k -> ConcurrentHashMap.newKeySet()).add(clientAddress);
            sendResponse(clientAddress, "SUCCESS|Subscribed to " + msg.getTopic());
        } catch (IllegalArgumentException e) {
            sendResponse(clientAddress, "ERROR|" + e.getMessage());
        }
    }

    private void handlePublication(String[] parts, InetSocketAddress clientAddress) throws IOException {
        if (parts.length < 3) {
            sendResponse(clientAddress, "ERROR|Invalid publication format");
            return;
        }

        String topic = parts[1];
        String content = parts[2];

        Set<InetSocketAddress> subscribers = subscriptions.getOrDefault(topic, Collections.emptySet());
        for (InetSocketAddress subscriber : subscribers) {

            // TODO - return stats functions

            sendResponse(subscriber, "MESSAGE|" + topic + "|" + content);
        }

        sendResponse(clientAddress, "SUCCESS|Published to " + subscribers.size() + " subscribers");
    }

    private void sendResponse(InetSocketAddress address, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        channel.send(buffer, address);
        logger.info("send response {}", message);
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        new Server().start(port);
    }
}