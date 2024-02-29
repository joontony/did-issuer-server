package org.snubi.did.issuerserver.restTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.snubi.did.issuerserver.config.CustomConfig;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.SocketException;

/**
 * @Transactional(propagation = Propagation.NESTED)는 스프링 프레임워크에서 사용되는 트랜잭션 속성 중 하나입니다. 이 속성은 중첩된 트랜잭션을 지원하는데 사용됩니다.
 * NESTED propagation은 현재 실행 중인 트랜잭션이 있는 경우 해당 트랜잭션 내에서 중첩된 트랜잭션을 시작합니다. 중첩된 트랜잭션은 독립적으로 커밋 또는 롤백될 수 있으며, 외부 트랜잭션의 커밋 또는 롤백에는 영향을 미치지 않습니다.
 * 중첩된 트랜잭션은 외부 트랜잭션의 일부로서 수행되며, 외부 트랜잭션의 롤백 시에도 중첩된 트랜잭션은 롤백됩니다. 하지만 중첩된 트랜잭션의 롤백이 외부 트랜잭션에 영향을 주지는 않습니다.
 * NESTED propagation은 데이터베이스에서 지원하는 SAVEPOINT를 사용하여 중첩된 트랜잭션을 관리합니다. 이를 통해 중첩된 트랜잭션 간에 롤백 및 커밋을 독립적으로 수행할 수 있습니다.
 * 이렇게 중첩된 트랜잭션을 사용하면 특정 동작을 중간에 롤백하거나 커밋할 수 있으며, 외부 트랜잭션에 영향을 주지 않고 독립적으로 제어할 수 있습니다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RestTemplateService {

    private final RestTemplate restTemplate;

    public String postToDidResolverServerSignatureCreate(HttpEntity<SendRequest.SignatureDto> signatureDtoHttpEntity) {
        return restTemplate.exchange(CustomConfig.didResolverServerSignatureCreate, HttpMethod.POST, signatureDtoHttpEntity, String.class).getBody();
    }

    public void postToDidSeverClubAfterCreateIssuer(HttpEntity<Response.CreateClubVcResponse> createClubVcResponseHttpEntity) {
        restTemplate.exchange(CustomConfig.didSeverClubAfterCreateIssuer, HttpMethod.POST, createClubVcResponseHttpEntity, String.class);
    }

    @Async
    public void postToDidSeverClubAfterExcelIssuer(HttpEntity<Response.AfterExcelSaveResponse> postEntity) {

        try {
            restTemplate.exchange(CustomConfig.didSeverClubAfterExcelIssuer, HttpMethod.POST, postEntity,
                    String.class).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Async
    public void postToDidSeverClubAfterMobileExcelIssuer(HttpEntity<Response.AfterExcelSaveResponse> postEntity) {

        try {
            restTemplate.exchange(CustomConfig.didSeverClubAfterMobileExcelIssuer, HttpMethod.POST, postEntity,
                    String.class).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
///club/after/mobile/excel/issuer
    @Async
    public void postToDidSeverClubAfterExcelReInvite(HttpEntity<Response.AfterExcelSaveResponse> postEntity) {

        try {
            restTemplate.exchange(CustomConfig.didSeverClubAfterExcelReInvite, HttpMethod.POST, postEntity,
                    String.class).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void postToDidServerClubQrCodeComplete(HttpEntity<Response.ScanPushResponse> scanPushResponseHttpEntity) {
        restTemplate.exchange(CustomConfig.didSeverClubQrcodeComplete, HttpMethod.POST, scanPushResponseHttpEntity, String.class);
    }

    // podUrl을 동적으로 사용하기 위해서는 (CustomConfig.didIssuerPodUrl + "/credential/club/signature") 이렇게 해야 한다.
    @Retryable(value = {ClientAbortException.class, SocketException.class}, backoff = @Backoff(delay = 1000), maxAttempts = 2)
    public String postToIssuerPodCredentialClubSignature(HttpEntity<SendRequest.ForSignatureJsonDto> forSignatureJsonDtoHttpEntity) {
        return restTemplate.exchange(CustomConfig.didIssuerPodUrl + "/credential/club/signature", HttpMethod.POST, forSignatureJsonDtoHttpEntity, String.class).getBody();
    }

    // podUrl을 동적으로 사용하기 위해서는 (CustomConfig.didIssuerPodUrl + "/presentation/verification/club") 이렇게 해야 한다.
    @Retryable(value = {ClientAbortException.class, SocketException.class}, backoff = @Backoff(delay = 1000), maxAttempts = 2)
    public String postToIssuerPodPresentationVerificationClub(HttpEntity<SendRequest.validationVpDto> validationVpDtoHttpEntity) {
        return restTemplate.exchange(CustomConfig.didIssuerPodUrl + "/presentation/verification/club", HttpMethod.POST, validationVpDtoHttpEntity, String.class).getBody();
    }

    // webSocket용
    public void sendToDidServerForEntityUpdate() {
        restTemplate.exchange(CustomConfig.didServerClubPersonalMessageEntityListener, HttpMethod.GET, null, String.class);
    }
}
