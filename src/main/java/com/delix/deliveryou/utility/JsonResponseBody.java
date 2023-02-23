package com.delix.deliveryou.utility;

import com.delix.deliveryou.exception.InvalidJsonBodyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class JsonResponseBody {
    private JsonResponseBody() {
    }
    public static String build(Object ...values) {
        if (values.length % 2 != 0)
            throw new InvalidJsonBodyException("Json body must be a set of key - value pair");

        Map map = new HashMap();
        for (int i = 0; i < values.length; i += 2)
            map.put(values[i], values[i + 1]);

        PromiseProducer<String> producer = new PromiseProducer<>();

        String body = producer.produce(
                () -> new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map)
                , (exception) -> "{\n}");

        return body;
    }
}
