package mx.jhcue.poc.kafkaevents.utils;

import lombok.experimental.UtilityClass;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class HeadersUtils {

    public static final String PREFIX = "x-kafka-";

    public static Headers mapHttpHeadersToHeaders(HttpHeaders httpHeaders) {
        Headers headers = new RecordHeaders();
        httpHeaders.keySet().stream()
                .filter(headerName -> headerName.toLowerCase().startsWith(PREFIX))
                .forEach(headerName -> {
                    var values = httpHeaders.get(headerName);
                    var key = headerName.substring(PREFIX.length());
                    values.forEach(value -> headers.add(key, value.getBytes(StandardCharsets.UTF_8)));
                });
        return headers;
    }

    public static MultiValueMap<String, String> makeReadable(Headers headers) {
        MultiValueMap<String, String> readableHeaders = new LinkedMultiValueMap<>();
        headers.forEach(header -> readableHeaders.add(header.key(), new String(header.value(), StandardCharsets.UTF_8)));
        return readableHeaders;
    }
}
