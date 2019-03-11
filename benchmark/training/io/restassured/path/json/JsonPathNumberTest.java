/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.path.json;


import JsonPathConfig.NumberReturnType;
import JsonPathConfig.NumberReturnType.BIG_INTEGER;
import JsonPathConfig.NumberReturnType.FLOAT_AND_DOUBLE;
import io.restassured.path.json.config.JsonPathConfig;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JsonPathNumberTest {
    /**
     * An expected input which is less than Integer.MAX_VALUE
     */
    private static final String EXPECTED_INTEGER = "15303030";

    /**
     * An expected input which is greater than Integer.MAX_VALUE and less than Long.MAX_VALUE
     */
    private static final String EXPECTED_LONG = "13000000000";

    private static final String ORDER_NUMBER_JSON = (((("{\n" + ("\n" + "    \"orderNumber\":")) + (JsonPathNumberTest.EXPECTED_INTEGER)) + " \n") + "\n") + "}";

    private static final String LIGHT_YEARS_TO_COSMIC_HORIZON_JSON = (((("{\n" + ("\n" + "    \"lightYearsToCosmicHorizon\":")) + (JsonPathNumberTest.EXPECTED_LONG)) + " \n") + "\n") + "}";

    private static final String PRICE_JSON = "{\n" + ((("\n" + "    \"price\":12.1 \n") + "\n") + "}");

    @Test
    public void json_path_returns_big_decimal_for_json_numbers_when_configured_accordingly() {
        // Given
        final JsonPath jsonPath = new JsonPath(JsonPathNumberTest.PRICE_JSON).using(new JsonPathConfig(NumberReturnType.BIG_DECIMAL));
        // When
        BigDecimal price = jsonPath.get("price");
        // Then
        Assert.assertThat(price, Matchers.equalTo(BigDecimal.valueOf(12.1)));
    }

    @Test
    public void json_path_returns_float_for_json_numbers_when_configured_accordingly() {
        // Given
        final JsonPath jsonPath = new JsonPath(JsonPathNumberTest.PRICE_JSON).using(new JsonPathConfig().numberReturnType(FLOAT_AND_DOUBLE));
        // When
        float price = ((Float) (jsonPath.get("price")));
        // Then
        Assert.assertThat(price, Matchers.equalTo(12.1F));
    }

    @Test
    public void json_path_returns_big_integer_for_json_integer_numbers_when_configured_accordingly() {
        // Given
        final JsonPath jsonPath = new JsonPath(JsonPathNumberTest.ORDER_NUMBER_JSON).using(new JsonPathConfig().numberReturnType(BIG_INTEGER));
        // When
        BigInteger orderNumber = jsonPath.get("orderNumber");
        // Then
        Assert.assertThat(orderNumber, Matchers.equalTo(new BigInteger(JsonPathNumberTest.EXPECTED_INTEGER)));
    }

    @Test
    public void json_path_returns_big_integer_for_json_long_numbers_when_configured_accordingly() {
        // Given
        final JsonPath jsonPath = new JsonPath(JsonPathNumberTest.LIGHT_YEARS_TO_COSMIC_HORIZON_JSON).using(new JsonPathConfig().numberReturnType(BIG_INTEGER));
        // When
        BigInteger orderNumber = jsonPath.get("lightYearsToCosmicHorizon");
        // Then
        Assert.assertThat(orderNumber, Matchers.equalTo(new BigInteger(JsonPathNumberTest.EXPECTED_LONG)));
    }
}

