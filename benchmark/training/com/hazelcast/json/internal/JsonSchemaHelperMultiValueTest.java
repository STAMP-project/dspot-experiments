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


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.serialization.impl.NavigableJsonInputAdapter;
import com.hazelcast.json.HazelcastJson;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class JsonSchemaHelperMultiValueTest extends AbstractJsonSchemaTest {
    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    @Test
    public void testAnyPattern() throws IOException {
        JsonObject object = Json.object().add("array", Json.array().add(1).add(2).add(3));
        NavigableJsonInputAdapter input = toAdapter(HazelcastJson.fromString(object.toString()));
        JsonSchemaNode description = JsonSchemaHelper.createSchema(createParserFromInput(input));
        JsonPattern pattern = JsonSchemaHelper.createPattern(input, description, splitPath("array[any]"));
        Assert.assertEquals(1, pattern.depth());
        Assert.assertEquals(0, pattern.get(0));
        Assert.assertTrue(pattern.hasAny());
    }

    @Test
    public void testAnyPattern_partsAfterAnyIsOmitted() throws IOException {
        JsonObject object = Json.object().add("array", Json.array().add(1).add(2).add(3));
        NavigableJsonInputAdapter input = toAdapter(HazelcastJson.fromString(object.toString()));
        JsonSchemaNode description = JsonSchemaHelper.createSchema(createParserFromInput(input));
        JsonPattern pattern = JsonSchemaHelper.createPattern(input, description, splitPath("array[any].a"));
        Assert.assertEquals(1, pattern.depth());
        Assert.assertEquals(0, pattern.get(0));
        Assert.assertTrue(pattern.hasAny());
    }

    @Test
    public void testAnyPattern_whenFirstItem() throws IOException {
        JsonArray object = Json.array().add(1).add(2).add(3);
        NavigableJsonInputAdapter input = toAdapter(HazelcastJson.fromString(object.toString()));
        JsonSchemaNode description = JsonSchemaHelper.createSchema(createParserFromInput(input));
        JsonPattern pattern = JsonSchemaHelper.createPattern(input, description, splitPath("[any]"));
        Assert.assertEquals(0, pattern.depth());
        Assert.assertTrue(pattern.hasAny());
    }

    @Test
    public void testAnyPattern__whenFirstItem_partsAfterAnyIsOmitted() throws IOException {
        JsonArray object = Json.array().add(1).add(2).add(3);
        NavigableJsonInputAdapter input = toAdapter(HazelcastJson.fromString(object.toString()));
        JsonSchemaNode description = JsonSchemaHelper.createSchema(createParserFromInput(input));
        JsonPattern pattern = JsonSchemaHelper.createPattern(input, description, splitPath("[any].abc.de"));
        Assert.assertEquals(0, pattern.depth());
        Assert.assertTrue(pattern.hasAny());
    }

    @Test
    public void testAnyPattern_whenNotArrayOrObject_returnsNull() throws IOException {
        JsonObject object = Json.object().add("scalarValue", 4);
        NavigableJsonInputAdapter input = toAdapter(HazelcastJson.fromString(object.toString()));
        JsonSchemaNode description = JsonSchemaHelper.createSchema(createParserFromInput(input));
        JsonPattern pattern = JsonSchemaHelper.createPattern(input, description, splitPath("scalarValue[any]"));
        Assert.assertNull(pattern);
    }
}

