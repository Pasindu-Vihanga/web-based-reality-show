package com.example.demo.Config;

import java.util.concurrent.atomic.AtomicInteger;
public class EpisodeID {

    private static final AtomicInteger counter = new AtomicInteger(0);

        /** Initialize counter from DB (highest existing episodeId) */
        public static void initialize(int lastNumber) {
            counter.set(lastNumber);
        }

        /** Generate new ID like EP0001, EP0002, ... */
        public static String generateEpisodeId() {
            int number = counter.incrementAndGet();
            return String.format("EP%04d", number);
        }
}

