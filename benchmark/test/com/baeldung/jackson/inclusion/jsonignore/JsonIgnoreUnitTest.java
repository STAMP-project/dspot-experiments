package com.baeldung.jackson.inclusion.jsonignore;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.junit.Test;


/**
 * Source code github.com/readlearncode
 *
 * @author Alex Theedom www.readlearncode.com
 * @version 1.0
 */
public class JsonIgnoreUnitTest {
    @Test
    public void whenSerializingUsingJsonIgnore_thenCorrect() throws JsonProcessingException {
        // arrange
        Author author = new Author("Alex", "Theedom");
        // act
        String result = new ObjectMapper().writeValueAsString(author);
        // assert
        assertThat(JsonPath.from(result).getString("firstName")).isEqualTo("Alex");
        assertThat(JsonPath.from(result).getString("lastName")).isEqualTo("Theedom");
        assertThat(JsonPath.from(result).getString("id")).isNull();
        /* {
        "firstName": "Alex",
        "lastName": "Theedom",
        "items": []
        }
         */
    }
}

