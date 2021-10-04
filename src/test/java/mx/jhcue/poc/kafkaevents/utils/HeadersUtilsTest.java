package mx.jhcue.poc.kafkaevents.utils;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HeadersUtilsTest {

    @ParameterizedTest(name = "mapHttpHeadersToHeaders {index}: {0}")
    @MethodSource("mapHttpHeadersToHeadersSource")
    void mapHttpHeadersToHeaders(String name, HttpHeaders httpHeaders, Headers expected) {
        var actual = HeadersUtils.mapHttpHeadersToHeaders(httpHeaders);
        assertEquals(actual, expected);
    }

    @ParameterizedTest(name = "makeReadable {index}: {0}")
    @MethodSource("makeReadableSource")
    void makeReadable(String name, Headers headers, HttpHeaders expected) {
        var actual = HeadersUtils.makeReadable(headers);
        assertAll(
                () -> assertEquals(actual, expected)
        );
    }

    private static Stream<Arguments> mapHttpHeadersToHeadersSource() {
        return Stream.of(
                Arguments.of("Standard headers",
                        makeHttpStandardHeadesForKafka(),
                        makeStandardHeaders()
                )
        );
    }

    private static Stream<Arguments> makeReadableSource() {
        return Stream.of(
                Arguments.of("Standard headers",
                        makeStandardHeaders(),
                        makeHttpStandardHeadesForKafka()
                )
        );
    }

    private static Headers makeStandardHeaders() {
        Headers headers = new RecordHeaders();
        headers.add(KafkaHeaders.CORRELATION_ID, "test-correlation_id".getBytes(StandardCharsets.UTF_8));
        return headers;
    }

    private static MultiValueMap<String, String> makeHttpStandardHeadesForKafka() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(KafkaHeaders.CORRELATION_ID, "test-correlation_id");
        return headers;
    }

}