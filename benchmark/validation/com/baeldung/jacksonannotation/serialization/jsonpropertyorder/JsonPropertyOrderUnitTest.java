package com.baeldung.jacksonannotation.serialization.jsonpropertyorder;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Source code github.com/eugenp/tutorials
 *
 * @author Alex Theedom www.baeldung.com
 * @version 1.0
 */
public class JsonPropertyOrderUnitTest {
    @Test
    public void whenSerializingUsingJsonPropertyOrder_thenCorrect() throws JsonProcessingException {
        // arrange
        Author author = new Author("Alex", "Theedom");
        author.setzIndex("z123");
        author.setAlphaIndex("z123");
        // act
        String result = new ObjectMapper().writeValueAsString(author);
        // assert
        MatcherAssert.assertThat(result, JsonSchemaValidator.matchesJsonSchemaInClasspath("author-jsonpropertyorder-schema.json"));
        // NOTE: property order is not enforced by the JSON specification.
        /* {
        "items": [],
        "firstName": "Alex",
        "lastName": "Theedom",
        "id": "31ca2af9-df0a-4d49-a74c-86c0a3f944a2",
        "alphaIndex": "z123",
        "zIndex": "z123"
        }
         */
    }
}

