package org.snubi.did.issuerserver.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.converter.JsonConverter;
import org.springframework.scheduling.annotation.Async;

import java.net.HttpURLConnection;

@Slf4j
public class HttpPostService extends HttpService {
    private HttpPostService(String url, String auth) {
        super(url, auth);
    }

    public static HttpPostService createHttpPost(String url, String auth) {
        return new HttpPostService(url, auth);
    }

    public String post(Object object) throws JsonProcessingException, InterruptedException {

        Thread.sleep(3000);
        log.info("2");
        String json;
        if (object instanceof String) {
            json = (String) object;
        } else {
            json = JsonConverter.ObjectToJson(object);
        }

        try {
            if ((getUrl() == null) || getUrl().isBlank()) {
                throw new Exception("접속 URL이 지정되지 않았습니다.");
            }

            HttpURLConnection postConnection = createConnection("POST");

            return sendRequest(postConnection, json);
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}

