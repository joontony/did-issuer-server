package org.snubi.did.issuerserver.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Getter
@RequiredArgsConstructor
public abstract class HttpService {
    private final String url;
    private final String auth;
    private final String tokenType = "Bearer";
    private final String charset = "UTF-8";
    private final String type = "application/json";

    protected HttpURLConnection createConnection(String requestMethod) throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", type);
        connection.setRequestMethod(requestMethod);
        connection.setDoOutput(true);

        if (auth != null && !auth.isBlank()) {
            connection.setRequestProperty("Authorization", tokenType + " " + auth);
        }

        return connection;
    }

    protected String sendRequest(HttpURLConnection connection, String json) {

        try {
            connection.connect();

            if (json != null && !json.isEmpty()) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(json.getBytes(charset));
                outputStream.flush();
                outputStream.close();
            }

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));

            StringBuilder stringBuilder = new StringBuilder();

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}