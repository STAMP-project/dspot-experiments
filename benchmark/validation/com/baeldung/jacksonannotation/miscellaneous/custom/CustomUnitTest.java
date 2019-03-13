package com.baeldung.jacksonannotation.miscellaneous.custom;


import Course.Medium.ONLINE;
import com.baeldung.jacksonannotation.domain.Author;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import org.junit.Test;


/**
 * Source code github.com/eugenp/tutorials
 *
 * @author Alex Theedom www.baeldung.com
 * @version 1.0
 */
public class CustomUnitTest {
    @Test
    public void whenSerializingUsingCustom_thenCorrect() throws JsonProcessingException {
        // arrange
        Course course = new Course("Spring Security", new Author("Eugen", "Paraschiv"));
        course.setMedium(ONLINE);
        // act
        String result = new ObjectMapper().writeValueAsString(course);
        // assert
        assertThat(JsonPath.from(result).getString("title")).isEqualTo("Spring Security");
        /* {
        "title": "Spring Security",
        "price": 0,
        "id": "7dfd4db9-1175-432f-a53b-687423f7bb9b",
        "duration": 0,
        "authors": [
        {
        "id": "da0738f6-033c-4974-8d87-92820e5ccf27",
        "firstName": "Eugen",
        "lastName": "Paraschiv",
        "items": []
        }
        ],
        "medium": "ONLINE"
        }
         */
    }
}

