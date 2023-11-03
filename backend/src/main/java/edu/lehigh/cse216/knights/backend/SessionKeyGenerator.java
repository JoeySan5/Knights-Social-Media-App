package edu.lehigh.cse216.knights.backend;

import java.security.SecureRandom;
import java.util.Random;

public class SessionKeyGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int keyLength = 12; // 원하는 랜덤 문자열의 길이를 설정하세요.
        String sessionKey = generateRandomString(keyLength);
        System.out.println("Generated Session Key: " + sessionKey);
    }
}