package org.snubi.did.issuerserver.key;

import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.snubi.did.issuerserver.converter.HexConverter;
import org.snubi.did.issuerserver.exception.CustomException;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class RsaCipher {
    public static String encryptWithPrivateKey(String jsonData, String privateKeyStr) {
        try {
            // Base64로 인코딩된 개인 키를 PrivateKey 객체로 변환합니다.
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // JSON 데이터로부터 SHA-256 해시 값을 생성합니다.
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedJsonData = md.digest(jsonData.getBytes(StandardCharsets.UTF_8));
            String sha256hex = HexConverter.bytesToHex(hashedJsonData);

            // RSA 개인 키를 사용하여 해시 값을 암호화합니다.
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            byte[] encryptedHashValue = cipher.doFinal(sha256hex.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedHashValue);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException("[jsonData, privateKey로 RSA 암호화 실패]", ErrorCode.ENCRYPT_ERROR);  // 필요에 따라 예외 처리를 합니다
        }
    }

    public static String decryptWithPublicKey(String encryptedHashValue, String publicKeyStr) {
        try {
            // Base64로 인코딩된 공개 키를 PublicKey 객체로 변환합니다.
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // 암호화된 해시 값을 복호화합니다.
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

            byte[] encryptedValueBytes = Base64.getDecoder().decode(encryptedHashValue);
            byte[] decryptedValueBytes = cipher.doFinal(encryptedValueBytes);

            //return Base64.getEncoder().encodeToString(decryptedHashValueBytes);
            return new String(decryptedValueBytes);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException("[jsonData, publicKey로 RSA 복호화 실패]", ErrorCode.DECRYPT_ERROR);
        }
    }
}


