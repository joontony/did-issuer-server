package org.snubi.did.issuerserver.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CustomConfigTest {

    @Test
    @DisplayName("CustomConfig의 각 필드에 데이터가 잘 담겨야 한다.")
    public void CustomConfig() {
        log.info("strTokenPrefix : " + CustomConfig.strTokenPrefix);
        Assertions.assertNotNull(CustomConfig.strTokenPrefix);

        log.info("strResponseAuthHeader : " + CustomConfig.strResponseAuthHeader);
        Assertions.assertNotNull(CustomConfig.strResponseAuthHeader);

        log.info("strResponseAuthClaimIssue : " + CustomConfig.strResponseAuthClaimIssue);
        Assertions.assertNotNull(CustomConfig.strResponseAuthClaimIssue);

        log.info("strSecrete : " + CustomConfig.strSecrete);
        Assertions.assertNotNull(CustomConfig.strSecrete);

        log.info("strKubernetesServerUrl : " + CustomConfig.strKubernetesServerUrl);
        Assertions.assertNotNull(CustomConfig.strKubernetesServerUrl);

        log.info("strKubernetesServerIp : " + CustomConfig.strKubernetesServerIp);
        Assertions.assertNotNull(CustomConfig.strKubernetesServerIp);

        log.info("context : " + CustomConfig.context);
        Assertions.assertNotNull(CustomConfig.context);

        log.info("avChainType : " + CustomConfig.credentialId);
        Assertions.assertNotNull(CustomConfig.credentialId);

        log.info("avChainType : " + CustomConfig.avChainType);
        Assertions.assertNotNull(CustomConfig.avChainType);

        log.info("clubType : " + CustomConfig.clubType);
        Assertions.assertNotNull(CustomConfig.clubType);

        log.info("proofType : " + CustomConfig.proofType);
        Assertions.assertNotNull(CustomConfig.proofType);

        log.info("avChainVerificationMethod : " + CustomConfig.avChainVerificationMethod);
        Assertions.assertNotNull(CustomConfig.avChainVerificationMethod);

        log.info("proofPurpose : " + CustomConfig.proofPurpose);
        Assertions.assertNotNull(CustomConfig.proofPurpose);
    }
}