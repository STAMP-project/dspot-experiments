package com.baeldung.jacksonannotation.inclusion.jsonignoretype;


import Order.Type;
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
public class JsonIgnoreTypeUnitTest {
    @Test
    public void whenSerializingUsingJsonIgnoreType_thenCorrect() throws JsonProcessingException {
        // arrange
        Order.Type type = new Order.Type();
        type.id = 10;
        type.name = "Pre-order";
        Order order = new Order(type);
        // act
        String result = new ObjectMapper().writeValueAsString(order);
        // assert
        assertThat(JsonPath.from(result).getString("id")).isNotNull();
        assertThat(JsonPath.from(result).getString("type")).isNull();
        /* {"id":"ac2428da-523e-443c-a18a-4ea4d2791fea"} */
    }
}

