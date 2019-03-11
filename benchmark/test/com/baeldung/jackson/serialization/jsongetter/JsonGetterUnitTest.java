package com.baeldung.jackson.serialization.jsongetter;


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
public class JsonGetterUnitTest {
    @Test
    public void whenSerializingUsingJsonGetter_thenCorrect() throws JsonProcessingException {
        // arrange
        Author author = new Author("Alex", "Theedom");
        // act
        String result = new ObjectMapper().writeValueAsString(author);
        // assert
        assertThat(JsonPath.from(result).getList("publications")).isNotNull();
        assertThat(JsonPath.from(result).getList("items")).isNull();
        /* {
        "firstName": "Alex",
        "lastName": "Theedom",
        "publications": []
        }
         */
    }
}

