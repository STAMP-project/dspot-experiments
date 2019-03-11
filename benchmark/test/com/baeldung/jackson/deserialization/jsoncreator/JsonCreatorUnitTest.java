package com.baeldung.jackson.deserialization.jsoncreator;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import java.io.IOException;
import org.junit.Test;


/**
 * Source code github.com/readlearncode
 *
 * @author Alex Theedom www.readlearncode.com
 * @version 1.0
 */
public class JsonCreatorUnitTest {
    @Test
    public void whenDeserializingUsingJsonCreator_thenCorrect() throws IOException {
        // arrange
        String authorJson = "{" + (("    \"christianName\": \"Alex\"," + "    \"surname\": \"Theedom\"") + "}");
        // act
        final Author author = new ObjectMapper().readerFor(Author.class).readValue(authorJson);
        // assert
        assertThat(JsonPath.from(authorJson).getString("christianName")).isEqualTo(author.getFirstName());
        assertThat(JsonPath.from(authorJson).getString("surname")).isEqualTo(author.getLastName());
        /* {
        "christianName": "Alex",
        "surname": "Theedom"
        }
         */
    }
}

