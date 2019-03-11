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
package io.restassured.module.mockmvc;


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Test;


public class AttributeTest {
    @Test
    public void can_send_attribute_using_attribute_name_and_value() {
        RestAssuredMockMvc.given().attribute("testAttribute", "John Doe").when().get("/attribute").then().statusCode(200).body("testAttribute", Matchers.equalTo("John Doe"));
    }

    @Test
    public void can_send_attributes_using_map() {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("testAttribute1", "value1");
        attributes.put("testAttribute2", "value2");
        RestAssuredMockMvc.given().attributes(attributes).when().get("/attribute").then().statusCode(200).body("testAttribute1", Matchers.equalTo("value1")).body("testAttribute2", Matchers.equalTo("value2"));
    }
}

