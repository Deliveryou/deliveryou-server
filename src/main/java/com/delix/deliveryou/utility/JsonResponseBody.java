package com.delix.deliveryou.utility;

import com.delix.deliveryou.exception.InvalidJsonBodyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class JsonResponseBody {

    JsonResponseBody() {
    }

    public static String build(Object ...values) {
        if (values.length % 2 != 0)
            throw new InvalidJsonBodyException("Json body must be a set of key - value pair");

        Map map = new HashMap();
        for (int i = 0; i < values.length; i += 2)
            map.put(values[i], values[i + 1]);

        PromiseProducer<String> producer = new PromiseProducer<>();

        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String body = producer.produce(
                () -> objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map)
                , (exception) -> {
                    exception.printStackTrace();
                    return "{\n}";
                });

        return body;
    }
}
