package org.snubi.did.issuerserver.signature;

import org.snubi.did.issuerserver.converter.HexConverter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

    /**
     * 입력 받은 문자열을 SHA-256으로 암호화하여 해시 값을 반환함
     *
     * @return 해시값
     */
    public static String encrypt(String jsonData) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedJsonData = md.digest(jsonData.getBytes(StandardCharsets.UTF_8));

        return HexConverter.bytesToHex(hashedJsonData);
    }
}