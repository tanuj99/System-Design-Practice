package hashing;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashFunction {

    private HashFunction() {
    }

    public static long hash(String key) {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));

            BigInteger number = new BigInteger(1, digest);

            return Math.abs(number.longValue());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}