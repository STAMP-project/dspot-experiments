package io.dropwizard.jackson;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class JacksonDeserializationOfBigNumbersToInstantTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    @Test
    public void testDoesNotAttemptToDeserializeExtremelBigNumbers() throws Exception {
        JacksonDeserializationOfBigNumbersToInstantTest.Event event = objectMapper.readValue("{\"id\": 42, \"createdAt\": 1e1000000000}", JacksonDeserializationOfBigNumbersToInstantTest.Event.class);
        Assertions.assertTimeout(Duration.ofSeconds(5L), () -> assertThat(event.getCreatedAt()).isEqualTo(Instant.ofEpochMilli(0)));
    }

    @Test
    public void testCanDeserializeZero() throws Exception {
        JacksonDeserializationOfBigNumbersToInstantTest.Event event = objectMapper.readValue("{\"id\": 42, \"createdAt\": 0}", JacksonDeserializationOfBigNumbersToInstantTest.Event.class);
        assertThat(event.getCreatedAt()).isEqualTo(Instant.ofEpochMilli(0));
    }

    @Test
    public void testCanDeserializeNormalTimestamp() throws Exception {
        JacksonDeserializationOfBigNumbersToInstantTest.Event event = objectMapper.readValue("{\"id\": 42, \"createdAt\": 1538326357}", JacksonDeserializationOfBigNumbersToInstantTest.Event.class);
        assertThat(event.getCreatedAt()).isEqualTo(Instant.ofEpochMilli(1538326357000L));
    }

    @Test
    public void testCanDeserializeNormalTimestampWithNanoseconds() throws Exception {
        JacksonDeserializationOfBigNumbersToInstantTest.Event event = objectMapper.readValue("{\"id\": 42, \"createdAt\": 1538326357.298509112}", JacksonDeserializationOfBigNumbersToInstantTest.Event.class);
        assertThat(event.getCreatedAt()).isEqualTo(Instant.ofEpochSecond(1538326357, 298509112L));
    }

    @Test
    public void testCanDeserializeMinInstant() throws Exception {
        JacksonDeserializationOfBigNumbersToInstantTest.Event event = objectMapper.readValue("{\"id\": 42, \"createdAt\": -31557014167219200}", JacksonDeserializationOfBigNumbersToInstantTest.Event.class);
        assertThat(event.getCreatedAt()).isEqualTo(Instant.MIN);
    }

    @Test
    public void testCanDeserializeMaxInstant() throws Exception {
        JacksonDeserializationOfBigNumbersToInstantTest.Event event = objectMapper.readValue("{\"id\": 42, \"createdAt\": 31556889864403199.999999999}", JacksonDeserializationOfBigNumbersToInstantTest.Event.class);
        assertThat(event.getCreatedAt()).isEqualTo(Instant.MAX);
    }

    @Test
    public void testCanNotDeserializeValueMoreThanMaxInstant() {
        assertThatExceptionOfType(JsonMappingException.class).isThrownBy(() -> objectMapper.readValue("{\"id\": 42, \"createdAt\": 31556889864403200}", .class));
    }

    @Test
    public void testCanNotDeserializeValueLessThanMaxInstant() {
        assertThatExceptionOfType(JsonMappingException.class).isThrownBy(() -> objectMapper.readValue("{\"id\": 42, \"createdAt\": -31557014167219201}", .class));
    }

    static class Event {
        private int id;

        @Nullable
        private Instant createdAt;

        @JsonProperty
        int getId() {
            return id;
        }

        @JsonProperty
        void setId(int id) {
            this.id = id;
        }

        @JsonProperty
        @Nullable
        Instant getCreatedAt() {
            return createdAt;
        }

        @JsonProperty
        void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }
    }
}

