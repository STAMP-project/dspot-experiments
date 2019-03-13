package io.dropwizard.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import org.junit.jupiter.api.Test;


public class ParanamerModuleTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void deserializePersonWithoutAnnotations() throws IOException {
        final ObjectReader reader = mapper.readerFor(Person.class);
        final Person person = reader.readValue("{ \"name\": \"Foo\", \"surname\": \"Bar\" }");
        assertThat(person.getName()).isEqualTo("Foo");
        assertThat(person.getSurname()).isEqualTo("Bar");
    }

    @Test
    public void serializePersonWithoutAnnotations() throws IOException {
        final ObjectWriter reader = mapper.writerFor(Person.class);
        final String person = reader.writeValueAsString(new Person("Foo", "Bar"));
        assertThat(person).contains("\"name\":\"Foo\"").contains("\"surname\":\"Bar\"");
    }
}

