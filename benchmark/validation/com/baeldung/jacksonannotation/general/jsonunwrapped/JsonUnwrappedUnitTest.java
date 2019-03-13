package com.baeldung.jacksonannotation.general.jsonunwrapped;


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
public class JsonUnwrappedUnitTest {
    @Test
    public void whenSerializingUsingJsonUnwrapped_thenCorrect() throws JsonProcessingException {
        // arrange
        Order.Type preorderType = new Order.Type();
        preorderType.id = 10;
        preorderType.name = "pre-order";
        Order order = new Order(preorderType);
        // act
        String result = new ObjectMapper().writeValueAsString(order);
        // assert
        assertThat(JsonPath.from(result).getInt("id")).isEqualTo(10);
        assertThat(JsonPath.from(result).getString("name")).isEqualTo("pre-order");
        /* {
        "id": 10,
        "name": "pre-order"
        }
         */
    }
}

