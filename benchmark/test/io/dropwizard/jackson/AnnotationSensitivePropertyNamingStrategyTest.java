package io.dropwizard.jackson;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;


public class AnnotationSensitivePropertyNamingStrategyTest {
    public static class RegularExample {
        @JsonProperty
        @Nullable
        String firstName;

        // Jackson
        @SuppressWarnings({ "UnusedDeclaration", "unused" })
        private RegularExample() {
        }

        public RegularExample(String firstName) {
            this.firstName = firstName;
        }
    }

    @JsonSnakeCase
    public static class SnakeCaseExample {
        @JsonProperty
        @Nullable
        String firstName;

        // Jackson
        @SuppressWarnings({ "UnusedDeclaration", "unused" })
        private SnakeCaseExample() {
        }

        public SnakeCaseExample(String firstName) {
            this.firstName = firstName;
        }
    }

    private final PropertyNamingStrategy strategy = new AnnotationSensitivePropertyNamingStrategy();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void serializesRegularProperties() throws Exception {
        assertThat(mapper.writeValueAsString(new AnnotationSensitivePropertyNamingStrategyTest.RegularExample("woo"))).isEqualTo("{\"firstName\":\"woo\"}");
    }

    @Test
    public void serializesSnakeCaseProperties() throws Exception {
        assertThat(mapper.writeValueAsString(new AnnotationSensitivePropertyNamingStrategyTest.SnakeCaseExample("woo"))).isEqualTo("{\"first_name\":\"woo\"}");
    }

    @Test
    public void deserializesRegularProperties() throws Exception {
        assertThat(mapper.readValue("{\"firstName\":\"woo\"}", AnnotationSensitivePropertyNamingStrategyTest.RegularExample.class).firstName).isEqualTo("woo");
    }

    @Test
    public void deserializesSnakeCaseProperties() throws Exception {
        assertThat(mapper.readValue("{\"first_name\":\"woo\"}", AnnotationSensitivePropertyNamingStrategyTest.SnakeCaseExample.class).firstName).isEqualTo("woo");
    }
}

