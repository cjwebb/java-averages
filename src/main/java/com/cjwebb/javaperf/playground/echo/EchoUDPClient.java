package com.cjwebb.javaperf.playground.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class EchoUDPClient {

    public static void main(String[] args) throws IOException {
        try(DatagramChannel c = DatagramChannel.open()) {
            c.connect(new InetSocketAddress("localhost", 9000));

            // Warm up the JVMs
            System.out.println("Warming up...");
            var warmUpMessages = 1_000_000;
            sendMessages(c, warmUpMessages);

            // Run the test
            System.out.println("Starting test...");
            var numberOfMessages = 1_000_000;
            long elapsedNanos = sendMessages(c, numberOfMessages);

            // Show some results
            System.out.println("Test finished.");
            System.out.println("Elapsed millis: " + elapsedNanos / 1000000);
            System.out.println("Mean micros: "    + elapsedNanos / 1000 / numberOfMessages);
        }
    }

    private static long sendMessages(DatagramChannel channel, int numberOfMessages) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.allocate(4);
        ByteBuffer readBuffer = ByteBuffer.allocate(4);

        long startTime = System.nanoTime();

        for (int i = 1; i <= numberOfMessages; i++) {
            writeBuffer.clear();
            writeBuffer.putInt(i);
            writeBuffer.flip();
            while(writeBuffer.hasRemaining()) {
                channel.write(writeBuffer);
            }

            readBuffer.clear();
            while (readBuffer.hasRemaining()) {
                channel.read(readBuffer);
            }
            readBuffer.flip();
            readBuffer.getInt();
        }

        return System.nanoTime() - startTime;
    }
}
