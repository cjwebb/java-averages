package com.cjwebb.javaperf.playground.echo.aeron;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

public class EchoAeronServer {
    private static final int STREAM_ID = 10;
    private static final String CHANNEL_1 = "aeron:udp?endpoint=localhost:40123";
    private static final String CHANNEL_2 = "aeron:udp?endpoint=localhost:40124";

    public static void main(String[] args) {
        try (MediaDriver driver = MediaDriver.launch();
             Aeron aeron = Aeron.connect();
             Subscription subscription = aeron.addSubscription(CHANNEL_1, STREAM_ID);
             Publication publication = aeron.addPublication(CHANNEL_2, STREAM_ID)) {

            final UnsafeBuffer buffer = new UnsafeBuffer(new byte[Integer.BYTES]);
            final IdleStrategy idle = new SleepingIdleStrategy();
            System.out.println("Server started, waiting for messages...");

            while (true) {
                subscription.poll((buffer1, offset, length, header) -> {
                    int receivedValue = buffer1.getInt(offset);
                    buffer.putInt(0, receivedValue);
                    while (publication.offer(buffer) < 0) {
                        idle.idle();
                    }
                }, 10);
            }
        }
    }
}