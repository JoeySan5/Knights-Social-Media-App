package edu.lehigh.cse216.knights.backend;

import java.security.SecureRandom;
import java.util.Random;

/**
 * SessionKeyGenerator is a class that generates a random string of characters
 * to be used as a session key for a user.
 */
public class SessionKeyGenerator {
    /**
     * The characters that are used to generate the random string
     */
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    /**
     * The random number generator
     */
    private static final Random random = new SecureRandom();

    /**
     * Generates a random string of characters of a given length
     * @param length The length of the string to generate
     * @return The generated string
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
    
}