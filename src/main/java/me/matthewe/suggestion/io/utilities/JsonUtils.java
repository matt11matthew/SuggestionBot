package me.matthewe.suggestion.io.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Matthew E on 12/10/2023 at 1:27 PM for the project TicketBot
 */
public class JsonUtils {
    public static <T> T loadJsonObjectFromFile(File file, Class<T> clazz)  {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated //NO LONGER NEEDED
    public static void testJsonObject(Object object) {

        ObjectMapper objectMapper = new ObjectMapper();

        // Serialize the object to JSON
        String json = null;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(json);

    }

}
