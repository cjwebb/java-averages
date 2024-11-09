package com.cjwebb.javaaverages.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Publisher implements AutoCloseable {
    private final DatagramChannel channel;
    private final InetSocketAddress serverAddress;

    public Publisher(String host, int port) throws IOException {
        channel = DatagramChannel.open();
        serverAddress = new InetSocketAddress(host, port);
    }

    public void publish(String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        channel.send(buffer, serverAddress);
    }

    public void close() throws IOException {
        channel.close();
    }
}
