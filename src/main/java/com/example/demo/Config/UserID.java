package com.example.demo.Config;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class UserID implements IdentifierGenerator {

    private static final String PREFIX = "USR";
    private static final int LENGTH = 6;
    private static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        int id = counter.getAndIncrement();
        return PREFIX + String.format("%0" + LENGTH + "d", id); // e.g. USR000001
    }
}
