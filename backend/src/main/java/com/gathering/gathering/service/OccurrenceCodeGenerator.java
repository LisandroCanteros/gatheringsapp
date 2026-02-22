package com.gathering.gathering.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OccurrenceCodeGenerator {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 8;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }
        return builder.toString();
    }
}
