package com.baeldung.jackson.serialization.jsonanygetter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import java.util.Map;
import org.junit.Test;


/**
 * Source code github.com/readlearncode
 *
 * @author Alex Theedom www.readlearncode.com
 * @version 1.0
 */
public class JsonAnyGetterUnitTest {
    @Test
    public void whenSerializingUsingJsonAnyGetter_thenCorrect() throws JsonProcessingException {
        // arrange
        Inventory inventory = new Inventory();
        Map<String, Float> countryDeliveryCost = inventory.getCountryDeliveryCost();
        inventory.setLocation("France");
        countryDeliveryCost.put("USA", 10.0F);
        countryDeliveryCost.put("UK", 15.0F);
        // act
        String result = new ObjectMapper().writeValueAsString(inventory);
        // assert
        assertThat(JsonPath.from(result).getString("location")).isEqualTo("France");
        assertThat(JsonPath.from(result).getFloat("USA")).isEqualTo(10.0F);
        assertThat(JsonPath.from(result).getFloat("UK")).isEqualTo(15.0F);
        /* {
        "location": "France",
        "USA": 10,
        "UK": 15
        }
         */
    }
}

