package com.baeldung.jsonjava;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class JSONArrayGetValueByKeyUnitTest {
    private static final JSONArrayGetValueByKey obj = new JSONArrayGetValueByKey();

    @Test
    public void givenJSONArrayAndAKey_thenReturnAllValuesForGivenKey() {
        String jsonStr = "[" + (((((((((((((((" {" + " \"name\": \"John\",") + " \"city\": \"chicago\",") + " \"age\": \"22\" ") + "},") + " { ") + "\"name\": \"Gary\",") + " \"city\": \"florida\",") + " \"age\": \"35\" ") + "},") + " { ") + "\"name\": \"Selena\",") + " \"city\": \"vegas\",") + " \"age\": \"18\" ") + "} ") + "]");
        List<String> actualValues = JSONArrayGetValueByKeyUnitTest.obj.getValuesByKeyInJSONArray(jsonStr, "name");
        Assert.assertThat(actualValues, CoreMatchers.equalTo(Arrays.asList("John", "Gary", "Selena")));
    }

    @Test
    public void givenJSONArrayAndAKey_whenUsingJava8Syntax_thenReturnAllValuesForGivenKey() {
        String jsonStr = "[" + (((((((((((((((" {" + " \"name\": \"John\",") + " \"city\": \"chicago\",") + " \"age\": \"22\" ") + "},") + " { ") + "\"name\": \"Gary\",") + " \"city\": \"florida\",") + " \"age\": \"35\" ") + "},") + " { ") + "\"name\": \"Selena\",") + " \"city\": \"vegas\",") + " \"age\": \"18\" ") + "} ") + "]");
        List<String> actualValues = JSONArrayGetValueByKeyUnitTest.obj.getValuesByKeyInJSONArrayUsingJava8(jsonStr, "name");
        Assert.assertThat(actualValues, CoreMatchers.equalTo(Arrays.asList("John", "Gary", "Selena")));
    }
}

