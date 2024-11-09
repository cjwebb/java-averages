package com.cjwebb.javaaverages.playground.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        try(SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress("localhost", 9000));

            // Warm up the JVMs
            System.out.println("Warming up...");
            var warmUpMessages = 1_000_000;
            sendMessages(socketChannel, warmUpMessages);

            // Run the test
            System.out.println("Starting test...");
            var numberOfMessages = 1_000_000;
            long elapsedNanos = sendMessages(socketChannel, numberOfMessages);

            // Show some results
            System.out.println("Test finished.");
            System.out.println("Elapsed millis: " + elapsedNanos / 1000000);
            System.out.println("Mean micros: "    + elapsedNanos / 1000 / numberOfMessages);
        }
    }

    private static long sendMessages(SocketChannel socketChannel, int numberOfMessages) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.allocate(4);
        ByteBuffer readBuffer = ByteBuffer.allocate(4);

        long startTime = System.nanoTime();

        for (int i = 1; i <= numberOfMessages; i++) {
            writeBuffer.clear();
            writeBuffer.putInt(i);
            writeBuffer.flip();
            while(writeBuffer.hasRemaining()) {
                socketChannel.write(writeBuffer);
            }

            readBuffer.clear();
            while (readBuffer.hasRemaining()) {
                socketChannel.read(readBuffer);
            }
            readBuffer.flip();
            readBuffer.getInt();
        }

        return System.nanoTime() - startTime;
    }
}
