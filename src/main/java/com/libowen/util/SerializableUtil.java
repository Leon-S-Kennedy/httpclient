package com.libowen.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libowen.model.RequestFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * @Author: libw1
 * @Date: 2024/04/02/16:51
 * @Description:
 */
@Slf4j
public class SerializableUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T jsonFileToObject(String filePath, Class<T> valueType) {
        T result=null;
        File file = new File(filePath);
        if(file.exists()&&file.isFile()&&file.getName().endsWith(".json")){
            try {
                result = objectMapper.readValue(file, valueType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            log.info("{}该文件不是.json文件",filePath);
        }
        return result;
    }

    public static String toJsonString(Object obj) {
        String jsonString =null;
        try {
            jsonString = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;

    }

    public static String StringGetPretty(String responseEntity) {
        String prettyString=null;
        try {
            Object obj = objectMapper.readValue(responseEntity, Object.class);
            prettyString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return prettyString;
    }
}
