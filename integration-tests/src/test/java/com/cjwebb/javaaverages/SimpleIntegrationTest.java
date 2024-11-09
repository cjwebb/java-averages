package com.cjwebb.javaaverages;

import com.cjwebb.javaaverages.client.Publisher;
import com.cjwebb.javaaverages.client.Subscriber;
import com.cjwebb.javaaverages.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class SimpleIntegrationTest {
    protected static final Logger logger = LogManager.getLogger();

    private static final String host = "localhost";
    private static final int port = 8080;

    private static Server server;
    private static ExecutorService serverExecutor;
    private Publisher publisher;
    private Subscriber subscriber1;
    private Subscriber subscriber2;

    @BeforeAll
    static void startServer() {
        serverExecutor = Executors.newSingleThreadExecutor();
        server = new Server();
        serverExecutor.submit(() -> {
            try {
                server.start(port);
            } catch (IOException e) {
                logger.error(e);
            }
        });

        // Give the server time to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    @AfterAll
    static void stopServer() {
        serverExecutor.shutdownNow();
    }

    @BeforeEach
    void setUp() throws IOException {
        publisher = new Publisher(host, port);
        subscriber1 = new Subscriber(host, port);
        subscriber2 = new Subscriber(host, port);
    }

    @AfterEach
    void tearDown() throws IOException {
        publisher.close();
        subscriber1.close();
        subscriber2.close();
    }

    @Test
    void testPublishSubscribe() throws IOException, InterruptedException {
        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            // Create message receivers
            MessageReceiver receiver1 = new MessageReceiver(subscriber1, "SUBSCRIBE|values:max");
            MessageReceiver receiver2 = new MessageReceiver(subscriber2, "SUBSCRIBE|values:min");
            var r1 = executorService.submit(receiver1);
            var r2 = executorService.submit(receiver2);
            Thread.sleep(100);

            try {
                publisher.publish("PUBLISH|values|a=100");
                publisher.publish("PUBLISH|values|a=200");

                // Wait for messages to be received
                await().atMost(2, TimeUnit.SECONDS).until(() -> receiver1.hasMessages() && receiver2.hasMessages());

                // Verify received messages
                logger.info(receiver1.getMessages());
                assertEquals(receiver1.lastMessage(), "MESSAGE|values|a:max=100");
                assertEquals(receiver2.lastMessage(), "MESSAGE|values|a:min=100");
            } finally {
                r1.cancel(true);
                r2.cancel(true);
            }
        }
    }


    private static class MessageReceiver implements Runnable {

        private final CopyOnWriteArrayList<String> messages = new CopyOnWriteArrayList<>();
        private final Subscriber subscriber;
        private final String subscription;

        public MessageReceiver(Subscriber subscriber, String subscription) {
            this.subscriber = subscriber;
            this.subscription = subscription;
        }

        public CopyOnWriteArrayList<String> getMessages() {
            return messages;
        }

        public boolean hasMessages() {
            return !messages.isEmpty();
        }

        public String lastMessage() {
            return messages.getLast();
        }

        public void onMessage(String message) {
            if (message != null && !message.isEmpty()) {
                messages.add(message);
                logger.info("MessageReceiver|{}", message);
            }
        }

        @Override
        public void run() {
            try {
                subscriber.subscribe(subscription, this::onMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}