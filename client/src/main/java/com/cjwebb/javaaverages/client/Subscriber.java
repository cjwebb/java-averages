package com.cjwebb.javaaverages.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class Subscriber {
    private final DatagramChannel channel;
    private final InetSocketAddress serverAddress;

    public Subscriber(String host, int port) throws IOException {
        channel = DatagramChannel.open();
        serverAddress = new InetSocketAddress(host, port);
    }

    // todo - subscribe message should be structured.
    public void subscribe(String message, MessageHandler handler) throws IOException {
        // send initial subscription messages
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        channel.send(buffer, serverAddress);

        // Receive response
        buffer = ByteBuffer.allocate(1024);
        while (!Thread.currentThread().isInterrupted()) {
            channel.receive(buffer);
            buffer.flip();
            handler.onMessage(StandardCharsets.UTF_8.decode(buffer).toString());
            buffer.flip();
        }
    }

    public void close() throws IOException {
        channel.close();
    }
}
