/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.json.internal;


import com.fasterxml.jackson.core.JsonParser;
import com.hazelcast.internal.json.Json;
import java.io.IOException;
import org.junit.Test;


public abstract class AbstractJsonSchemaCreateTest {
    @Test
    public void testOneFirstLevelAttribute() throws IOException {
        String jsonString = Json.object().add("name", "aName").toString();
        JsonParser parser = createParserFromString(jsonString);
        JsonSchemaNode description = JsonSchemaHelper.createSchema(parser);
        validate(description, "0", 1, 8);
    }

    @Test
    public void testTwoFirstLevelAttributes() throws IOException {
        String jsonString = Json.object().add("name", "aName").add("age", 4).toString();
        JsonParser parser = createParserFromString(jsonString);
        JsonSchemaStructNode description = ((JsonSchemaStructNode) (JsonSchemaHelper.createSchema(parser)));
        validate(description, "0", 1, 8);
        validate(description, "1", 16, 22);
    }

    @Test
    public void testThreeFirstLevelAttributes() throws IOException {
        String jsonString = Json.object().add("name", "aName").add("age", 4).add("location", "ankara").toString();
        JsonParser parser = createParserFromString(jsonString);
        JsonSchemaStructNode description = ((JsonSchemaStructNode) (JsonSchemaHelper.createSchema(parser)));
        validate(description, "0", 1, 8);
        validate(description, "1", 16, 22);
        validate(description, "2", 24, 35);
    }

    @Test
    public void testOneFirstLevelTwoInnerAttributes() throws IOException {
        String jsonString = Json.object().add("name", Json.object().add("firstName", "fName").add("surname", "sname")).toString();
        JsonParser parser = createParserFromString(jsonString);
        JsonSchemaStructNode description = ((JsonSchemaStructNode) (JsonSchemaHelper.createSchema(parser)));
        validate(description, "0.0", 9, 21);
        validate(description, "0.1", 29, 39);
    }

    @Test
    public void testTwoFirstLevelOneInnerAttributesEach() throws IOException {
        String jsonString = Json.object().add("name", Json.object().add("firstName", "fName")).add("address", Json.object().add("addressId", 4)).toString();
        JsonParser parser = createParserFromString(jsonString);
        JsonSchemaStructNode description = ((JsonSchemaStructNode) (JsonSchemaHelper.createSchema(parser)));
        validate(description, "0.0", 9, 21);
        validate(description, "1.0", 41, 53);
    }

    @Test
    public void testFourNestedLevelsEachHavingAValue() throws IOException {
        String jsonString = Json.object().add("firstObject", Json.object().add("secondObject", Json.object().add("thirdLevelTerminalString", "terminalvalue").add("thirdObject", Json.object().add("fourthTerminalValue", true))).add("secondLevelTerminalNumber", 43534324)).add("firstLevelTerminalNumber", 53).toString();
        JsonParser parser = createParserFromString(jsonString);
        JsonSchemaStructNode description = ((JsonSchemaStructNode) (JsonSchemaHelper.createSchema(parser)));
        validate(description, "1", 157, 184);
        validate(description, "0.1", 119, 147);
        validate(description, "0.0.0", 32, 59);
        validate(description, "0.0.1.0", 90, 112);
    }

    @Test
    public void testFourNestedLevels() throws IOException {
        String jsonString = Json.object().add("firstObject", Json.object().add("secondObject", Json.object().add("thirdObject", Json.object().add("fourthObject", true)))).toString();
        JsonParser parser = createParserFromString(jsonString);
        JsonSchemaNode description = JsonSchemaHelper.createSchema(parser);
        validate(description, "0.0.0.0", 47, 62);
    }

    @Test
    public void testSimpleArray() throws IOException {
        String jsonString = Json.array(new int[]{ 1, 2, 3 }).toString();
        JsonParser parser = createParserFromString(jsonString);
        JsonSchemaNode description = JsonSchemaHelper.createSchema(parser);
        validate(description, "0", (-1), 1);
        validate(description, "1", (-1), 3);
        validate(description, "2", (-1), 5);
    }
}

