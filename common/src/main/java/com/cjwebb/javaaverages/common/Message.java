package com.cjwebb.javaaverages.common;

/**
 * Protocol in two parts: publish and subscribe.
 * <p>
 * To start with, let's use strings (or java.lang.CharSequence)
 * Publish: PUBLISH|key|value
 * Subscribe: SUBSCRIBE|key|command args
 * <p>
 * Publish is much easier to visualise.
 * PUBLISH|thing|100.1
 * <p>
 * Subscribe is harder, as we need to define commands that clients can subscribe for.
 * In a way, streaming SQL would be nice here... but let's start simple.
 * <p>
 * SUBSCRIBE|select min thing, avg thing, max thing from thing.
 */
public class Message {
    private final String action;
    private final String topic;
    private final String[] functions;

    public Message(String action, String topic, String[] functions) {
        this.action = action;
        this.topic = topic;
        this.functions = functions;
    }

    public String getAction() {
        return action;
    }

    public String getTopic() {
        return topic;
    }

    public String[] getFunction() {
        return functions;
    }

    @Override
    public String toString() {
        return String.format("%s|%s:%s",
                action,
                topic,
                String.join(",", functions)
        );
    }

    public static Message parse(String input) {
        String[] mainParts = input.split("\\|");
        if (mainParts.length != 2) {
            throw new IllegalArgumentException("Invalid message format: missing '|' separator");
        }

        String action = mainParts[0];
        String[] topicParts = mainParts[1].split(":");

        if (topicParts.length != 2) {
            throw new IllegalArgumentException("Invalid message format: missing ':' separator");
        }

        String[] functions = topicParts[1].split(",");
        return new Message(action, topicParts[0], functions);
    }
}
