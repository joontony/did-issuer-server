package org.snubi.did.issuerserver.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JsonConverterTest {

    @Test
    void removeTageTest() {
        try {
            String json = "{\"회원태그\": [{\"수술예정\": true},{\"진료\": true},{\"코로나\": false}]}";
            String tagToRemove = "진료";

            String result = JsonConverter.removeTag(json, tagToRemove);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
