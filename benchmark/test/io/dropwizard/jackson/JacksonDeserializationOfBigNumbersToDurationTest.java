package io.dropwizard.jackson;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class JacksonDeserializationOfBigNumbersToDurationTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();

    @Test
    public void testDoesNotAttemptToDeserializeExtremelyBigNumbers() throws Exception {
        JacksonDeserializationOfBigNumbersToDurationTest.Task task = objectMapper.readValue("{\"id\": 42, \"duration\": 1e1000000000}", JacksonDeserializationOfBigNumbersToDurationTest.Task.class);
        Assertions.assertTimeout(Duration.ofSeconds(5L), () -> assertThat(task.getDuration()).isEqualTo(Duration.ofSeconds(0)));
    }

    @Test
    public void testCanDeserializeZero() throws Exception {
        JacksonDeserializationOfBigNumbersToDurationTest.Task task = objectMapper.readValue("{\"id\": 42, \"duration\": 0}", JacksonDeserializationOfBigNumbersToDurationTest.Task.class);
        assertThat(task.getDuration()).isEqualTo(Duration.ofSeconds(0));
    }

    @Test
    public void testCanDeserializeNormalTimestamp() throws Exception {
        JacksonDeserializationOfBigNumbersToDurationTest.Task task = objectMapper.readValue("{\"id\": 42, \"duration\": 30}", JacksonDeserializationOfBigNumbersToDurationTest.Task.class);
        assertThat(task.getDuration()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    public void testCanDeserializeNormalTimestampWithNanoseconds() throws Exception {
        JacksonDeserializationOfBigNumbersToDurationTest.Task task = objectMapper.readValue("{\"id\": 42, \"duration\": 30.314400507}", JacksonDeserializationOfBigNumbersToDurationTest.Task.class);
        assertThat(task.getDuration()).isEqualTo(Duration.ofSeconds(30, 314400507L));
    }

    @Test
    public void testCanDeserializeFromString() throws Exception {
        JacksonDeserializationOfBigNumbersToDurationTest.Task task = objectMapper.readValue("{\"id\": 42, \"duration\": \"PT30S\"}", JacksonDeserializationOfBigNumbersToDurationTest.Task.class);
        assertThat(task.getDuration()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    public void testCanDeserializeMinDuration() throws Exception {
        JacksonDeserializationOfBigNumbersToDurationTest.Task task = objectMapper.readValue("{\"id\": 42, \"duration\": -9223372036854775808}", JacksonDeserializationOfBigNumbersToDurationTest.Task.class);
        assertThat(task.getDuration()).isEqualTo(Duration.ofSeconds(Long.MIN_VALUE));
    }

    @Test
    public void testCanDeserializeMaxDuration() throws Exception {
        JacksonDeserializationOfBigNumbersToDurationTest.Task task = objectMapper.readValue("{\"id\": 42, \"duration\": 9223372036854775807}", JacksonDeserializationOfBigNumbersToDurationTest.Task.class);
        assertThat(task.getDuration()).isEqualTo(Duration.ofSeconds(Long.MAX_VALUE));
    }

    @Test
    public void testCanNotDeserializeValueMoreThanMaxDuration() {
        assertThatExceptionOfType(JsonMappingException.class).isThrownBy(() -> objectMapper.readValue("{\"id\": 42, \"duration\": 9223372036854775808}", .class));
    }

    @Test
    public void testCanNotDeserializeValueLessThanMinDuration() {
        assertThatExceptionOfType(JsonMappingException.class).isThrownBy(() -> objectMapper.readValue("{\"id\": 42, \"duration\": -9223372036854775809}", .class));
    }

    static class Task {
        private int id;

        @Nullable
        private Duration duration;

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
        Duration getDuration() {
            return duration;
        }

        @JsonProperty
        void setDuration(Duration duration) {
            this.duration = duration;
        }
    }
}

