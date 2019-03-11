package com.baeldung.jacksonannotation.general.jsonfilter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.restassured.path.json.JsonPath;
import org.junit.Test;


/**
 * Source code github.com/eugenp/tutorials
 *
 * @author Alex Theedom www.baeldung.com
 * @version 1.0
 */
public class JsonFilterUnitTest {
    @Test
    public void whenSerializingUsingJsonFilter_thenCorrect() throws JsonProcessingException {
        // arrange
        Author author = new Author("Alex", "Theedom");
        FilterProvider filters = new SimpleFilterProvider().addFilter("authorFilter", SimpleBeanPropertyFilter.filterOutAllExcept("lastName"));
        // act
        String result = new ObjectMapper().writer(filters).writeValueAsString(author);
        // assert
        assertThat(JsonPath.from(result).getList("items")).isNull();
        /* {
        "lastName": "Theedom"
        }
         */
    }
}

