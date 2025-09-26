package com.example.demo.Config;
import java.util.concurrent.atomic.AtomicInteger;

public class VoteID {

    private static final AtomicInteger counter = new AtomicInteger(0);

        public static void initialize(int lastNumber) {
            counter.set(lastNumber);
        }

        public static String generateSessionId() {
            int number = counter.incrementAndGet();
            return String.format("VS%04d", number);
        }
    }

