/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.keycloak.test.broker.oidc.mappers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;


/**
 * Unit test for {@link org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper}
 *
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class AbstractJsonUserAttributeMapperTest {
    private static ObjectMapper mapper = new ObjectMapper();

    private static JsonNode baseNode;

    @Test
    public void getJsonValue_invalidPath() throws IOException {
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "."));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), ".."));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "...value1"));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), ".value1"));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value1."));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "[]"));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "[value1"));
    }

    @Test
    public void getJsonValue_simpleValues() throws JsonProcessingException, IOException {
        // unknown field returns null
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_unknown"));
        // we check value is trimmed also!
        Assert.assertEquals("v1", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value1"));
        // test for KEYCLOAK-4202 bug (null value handling)
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_null"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_empty"));
        Assert.assertEquals("true", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_b"));
        Assert.assertEquals("454", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_i"));
    }

    @Test
    public void getJsonValue_nestedSimpleValues() throws JsonProcessingException, IOException {
        // null if path points to JSON object
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1.nest2"));
        // unknown field returns null
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1.value_unknown"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1.nest2.value_unknown"));
        // we check value is trimmed also!
        Assert.assertEquals("fgh", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1.value1"));
        // test for KEYCLOAK-4202 bug (null value handling)
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1.value_null"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1.value_empty"));
        Assert.assertEquals("false", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1.nest2.value_b"));
        Assert.assertEquals("43", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1.nest2.value_i"));
        // null if invalid nested path
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1."));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nest1.nest2."));
    }

    @Test
    public void getJsonValue_simpleArray() throws JsonProcessingException, IOException {
        // array field itself returns null if no index is provided
        Assert.assertEquals(Arrays.asList("a1", "a2"), AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_array"));
        // outside index returns null
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_array[2]"));
        // corect index
        Assert.assertEquals("a1", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_array[0]"));
        Assert.assertEquals("a2", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_array[1]"));
        // incorrect array constructs
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_array[]"));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_array]"));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_array["));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_array[a]"));
        Assert.assertNull(AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "value_array[-2]"));
    }

    @Test
    public void getJsonValue_nestedArrayWithObjects() throws JsonProcessingException, IOException {
        Assert.assertEquals("vala1", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a[0].av1"));
        Assert.assertEquals("vala2", AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a[1].av1"));
        // different path erros or nonexisting indexes or fields return null
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a[2].av1"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a[0]"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a[0].av_unknown"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a[].av1"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a.av1"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a].av1"));
        Assert.assertEquals(null, AbstractJsonUserAttributeMapper.getJsonValue(getJsonNode(), "nesta.a[.av1"));
    }
}

