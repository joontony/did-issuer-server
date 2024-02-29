package org.snubi.did.issuerserver.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

/**
 * Dto -> JSON 데이터 변환시 Getter 메서드 필요!!
 */

public class JsonConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String ObjectToJson(Object object) throws JsonProcessingException {

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper.writeValueAsString(object);
    }

    public static JsonNode getJsonNode(String data) throws JsonProcessingException {
        return objectMapper.readTree(data);
    }

    public static String removeKeyValue(String json, String key) throws JsonProcessingException {
        ObjectNode objectNode = (ObjectNode) objectMapper.readTree(json);
        objectNode.remove(key);

        return objectMapper.writeValueAsString(objectNode);
    }

    public static String sortJsonByKey(String json) throws JsonProcessingException {

        if (json == null) return null;
        Map<String, String> map = objectMapper.readValue(json, new TypeReference<>(){});
        TreeMap<String, String> sorted = new TreeMap<>(map);

        return objectMapper.writeValueAsString(sorted);
    }

    public static List<String> filterKeysByBoolean(JsonNode jsonNode, boolean filterValue) throws JsonProcessingException {

        List<String> filteredKeys = new ArrayList<>();

        if (jsonNode.isArray()) {
            for (JsonNode node : jsonNode) {
                Iterator<String> fieldNames = node.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode fieldValue = node.get(fieldName);

                    if (fieldValue.isBoolean() && fieldValue.asBoolean() == filterValue) {
                        filteredKeys.add(fieldName);
                    }
                }
            }
        }

        return filteredKeys;
    }
    public static List<String> removeBooleanValue(String json) throws JsonProcessingException {

        Map<String, Object> jsonMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        List<String> trueKeys = new ArrayList<>();

        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List) {
                List<Map<String, Boolean>> tagList = (List<Map<String, Boolean>>) value;
                for (Map<String, Boolean> tag : tagList) {
                    for (Map.Entry<String, Boolean> tagEntry : tag.entrySet()) {
                        if (tagEntry.getValue()) {  // 값이 true인 경우
                            trueKeys.add(tagEntry.getKey());
                        }
                    }
                }
            }
        }

        return trueKeys;
    }

    public static List<String> removeBooleanValueForMultipleJson(String json) throws JsonProcessingException {
        String[] jsonObjects = json.split("}");
        List<String> keyList = new ArrayList<>();

        for (String jsonObject : jsonObjects) {
            // JSON 객체로 변환
            String jsonObjectWithBrace = jsonObject + "}";
            JsonNode jsonNode = objectMapper.readTree(jsonObjectWithBrace);

            // "true"인 경우 해당 키 값을 리스트에 추가
            if (jsonNode.isObject() && jsonNode.size() == 1) {
                String key = jsonNode.fieldNames().next();
                JsonNode value = jsonNode.get(key);
                if (value.isBoolean() && value.booleanValue()) {
                    keyList.add(key);
                }
            }
        }

        return keyList;
    }

    public static String removeTag(String json, String tag) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);

        // "회원태그" 배열을 가져옵니다.
        ArrayNode tagArray = (ArrayNode) rootNode.get("회원태그");

        // 배열에서 진료 키와 관련된 값을 제거합니다.
        for (int i = tagArray.size() - 1; i >= 0; i--) {
            ObjectNode tagObject = (ObjectNode) tagArray.get(i);
            if (tagObject.has(tag)) {
                tagObject.remove(tag);
            }

            // 빈 객체인 경우 배열에서 제거합니다.
            if (tagObject.isEmpty()) {
                tagArray.remove(i);
            }
        }

        return objectMapper.writeValueAsString(rootNode);
    }

    public static String addTag(String json, String tag, boolean value) throws Exception {
        JsonNode rootNode = objectMapper.readTree(json);

        // "회원태그" 배열을 가져옵니다.
        ArrayNode tagArray = (ArrayNode) rootNode.get("회원태그");

        // if) 배열에서 해당 태그가 이미 존재하면서 해당 태그의 value가 false라면 지웁니다.
        // else if) 배열에서 해당 태그가 이미 존재하는지 확인하고 있다면 null을 리턴합니다.
        for (JsonNode tagNode : tagArray) {
            ObjectNode tagObject = (ObjectNode) tagNode;
            if (tagObject.has(tag) && !tagObject.get(tag).asBoolean()) {
                tagObject.remove(tag);
            } else if (tagObject.has(tag)) {
                return null;
            }
        }

        // 해당 태그가 존재하지 않는 경우나 위에서 False라서 지운 경우 새로운 객체(tag, true)를 추가합니다.
        ObjectNode newTagObject = objectMapper.createObjectNode();
        newTagObject.put(tag, value);
        tagArray.add(newTagObject);

        return objectMapper.writeValueAsString(rootNode);
    }
}