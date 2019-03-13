package com.baeldung.jacksonannotation.serialization.jsonrootname;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.path.json.JsonPath;
import org.junit.Test;


/**
 * Source code github.com/eugenp/tutorials
 *
 * @author Alex Theedom www.baeldung.com
 * @version 1.0
 */
public class JsonRootNameUnitTest {
    @Test
    public void whenSerializingUsingJsonRootName_thenCorrect() throws JsonProcessingException {
        // arrange
        Author author = new Author("Alex", "Theedom");
        // act
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        String result = mapper.writeValueAsString(author);
        // assert
        assertThat(JsonPath.from(result).getString("writer.firstName")).isEqualTo("Alex");
        assertThat(JsonPath.from(result).getString("author.firstName")).isNull();
        /* {
        "writer": {
        "id": "0f50dca6-3dd7-4801-a334-fd1614276389",
        "firstName": "Alex",
        "lastName": "Theedom",
        "items": []
        }
        }
         */
    }
}

