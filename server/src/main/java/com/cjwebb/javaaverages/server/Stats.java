package com.cjwebb.javaaverages.server;


import java.util.concurrent.atomic.LongAdder;

public class Stats {
    private final LongAdder sum = new LongAdder();
    private final LongAdder count = new LongAdder();
    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;

    public void addValue(long value) {
        sum.add(value);
        count.increment();
        min = Math.min(min, value);
        max = Math.max(max, value);
    }

    public double getMean() {
        return count.sum() > 0 ? sum.doubleValue() / count.sum() : 0.0;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }
}
