package org.snubi.did.issuerserver.signature;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
@Slf4j
@SpringBootTest
class SHA256Test {

    public static class SHA256 {
        public String encrypt(String text) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());

            return bytesToHex(md.digest());
        }

        private String bytesToHex(byte[] bytes) {
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                log.info(String.valueOf(b));
                builder.append(String.format("%02x", b));
            }
            log.info("### builder.toString() ### : " + builder);
            return builder.toString();
        }
    }

    @Test
    public void sha256EncryptTest() throws NoSuchAlgorithmException {
        String a = "this is test.";
        String b = "this is test.";

        SHA256 sha256 = new SHA256();

        String encryptedA = sha256.encrypt(a);
        String encryptedB = sha256.encrypt(b);

        Assertions.assertEquals(encryptedA, encryptedB);

    }

}