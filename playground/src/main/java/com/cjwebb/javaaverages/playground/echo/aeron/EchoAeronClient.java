package com.cjwebb.javaaverages.playground.echo.aeron;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

public class EchoAeronClient {
    private static final String CHANNEL_1 = "aeron:udp?endpoint=localhost:40123";
    private static final String CHANNEL_2 = "aeron:udp?endpoint=localhost:40124";
    private static final int STREAM_ID = 10;

    public static void main(String[] args) {
        try (//MediaDriver driver = MediaDriver.launch();
             Aeron aeron = Aeron.connect();
             Publication publication = aeron.addPublication(CHANNEL_1, STREAM_ID);
             Subscription subscription = aeron.addSubscription(CHANNEL_2, STREAM_ID)) {

            final UnsafeBuffer buffer = new UnsafeBuffer(new byte[Integer.BYTES]);
            final IdleStrategy idle = new SleepingIdleStrategy();

            // Warm up the JVMs
            System.out.println("Warming up...");
            var warmUpMessages = 1_000_000;
            sendMessages(publication, subscription, buffer, idle, warmUpMessages);

            // Run the test
            System.out.println("Starting test...");
            var numberOfMessages = 1_000_000;
            long elapsedNanos = sendMessages(publication, subscription, buffer, idle, numberOfMessages);


            // Show some results
            System.out.println("Test finished.");
            System.out.println("Elapsed millis: " + elapsedNanos / 1000000);
            System.out.println("Mean micros: "    + elapsedNanos / 1000 / numberOfMessages);
        }
    }

    private static long sendMessages(Publication publication,
                                     Subscription subscription,
                                     UnsafeBuffer buffer,
                                     IdleStrategy idle,
                                     int numberOfMessages) {

        long startTime = System.nanoTime();

        for (int i = 1; i <= numberOfMessages; i++) {
            buffer.putInt(0, i);
            while (publication.offer(buffer) < 0) {
                idle.idle();
            }

            FragmentHandler handler = (buffer1, offset, length, header) -> {};

            while (subscription.poll(handler, 1) <= 0) {
                idle.idle();
            }
        }

        return System.nanoTime() - startTime;
    }
}