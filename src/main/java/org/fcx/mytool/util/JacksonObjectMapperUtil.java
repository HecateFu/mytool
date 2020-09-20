package org.fcx.mytool.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JacksonObjectMapperUtil {
    private static ObjectMapper objectMapper;
    @Autowired
    public JacksonObjectMapperUtil(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    public static ObjectMapper getJsonMapper () {
        return objectMapper;
    }
}
