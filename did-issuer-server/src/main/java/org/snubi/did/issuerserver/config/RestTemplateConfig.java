package org.snubi.did.issuerserver.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.snubi.did.issuerserver.restTemplate.HttpClientInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketException;
import java.net.URI;
import java.time.Duration;
@EnableRetry
@Configuration
@Slf4j
public class RestTemplateConfig {

    @Bean
    HttpClient httpClient() {
        return HttpClientBuilder.create()
                .setMaxConnTotal(100) // 클라이언트가 풀링하는 HTTP 연결의 최대 수를 설정
                .setMaxConnPerRoute(100) // // 하나의 라우트(특정 호스트로 가는 경로)에 대해 생성될 수 있는 동시 연결 수의 최대치를 설정합니다. 예를 들어, www.example.com와 www.anotherexample.com으로 요청을 보내는 경우 각각 다른 라우트로 간주되며 각각에 대해 별도로 제한이 적용
                .build();
    }

    @Bean
    RestTemplate restTemplate(HttpClient httpClient, RestTemplateBuilder builder) {

        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setHttpClient(httpClient);

        /**
         * Attempted read from closed stream 에러 방지
         * requestBody는 Stream이므로 소비가 되면 사라지는 (byte array가 비워진다) 특징이 있다.
         *
         * 이렇게 되면 인터셉터에서 이 requestBody를 읽어버리면 Presentation Layer에서 요청의 requestBody를 읽을 수 없게 되기 때문에 문제가 된다.
         */
        BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory = new BufferingClientHttpRequestFactory(
                httpComponentsClientHttpRequestFactory);

        RestTemplate restTemplate = new RestTemplate(bufferingClientHttpRequestFactory) {
            @Override
//            @Retryable(value = {ClientAbortException.class, SocketException.class}, backoff = @Backoff(delay = 1000))
            public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
                                                  Class<T> responseType) throws RestClientException {

                ResponseEntity<T> exchange = null;
                try {
                    log.info("retry 요청 URI : " + url);
                    log.info("retry 요청 method : " + method.name());
                    exchange = super.exchange(url, method, requestEntity, responseType);
                } catch (Exception e) {
                    logger.info("Exception : ", e);
                }
                return exchange;
            }

            @Recover
            public <T> ResponseEntity<String> exchangeRecover(RestClientException e) {
                return  ResponseEntity.badRequest().body("서버와의 연결에 실패했씁니다. <3회의 요청 전부 실패>");
            }
        };


        return builder
                .requestFactory(() -> bufferingClientHttpRequestFactory) // 파라미터 없이 결과값으로 request Factory 반환
                .setConnectTimeout(Duration.ofMillis(20000)) //  HTTP 요청을 시작하고 서버와의 TCP 핸드셰이크를 완료하는 데 허용되는 시간을 설정
//                .setReadTimeout(Duration.ofSeconds(100)) //  HTTP 요청이 서버에 도착한 후, 응답을 받아오는데 걸리는 시간을 설정
                .interceptors(new HttpClientInterceptor()) // HTTP 요청 및 응답을 로깅
                .build();
    }
}
