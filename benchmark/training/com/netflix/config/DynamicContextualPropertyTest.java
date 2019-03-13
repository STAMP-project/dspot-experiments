/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.config.DynamicContextualProperty.Value;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


public class DynamicContextualPropertyTest {
    @Test
    public void testPropertyChange() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        List<Value<Integer>> values = Lists.newArrayList();
        Value<Integer> value = new Value<Integer>();
        Map<String, Collection<String>> dimension = Maps.newHashMap();
        dimension.put("d1", Lists.newArrayList("v1", "v2"));
        value.setDimensions(dimension);
        value.setValue(5);
        values.add(value);
        Value<Integer> value2 = new Value<Integer>();
        Map<String, Collection<String>> dimension2 = Maps.newHashMap();
        dimension2.put("d1", Lists.newArrayList("v3"));
        dimension2.put("d2", Lists.newArrayList("x1"));
        value2.setDimensions(dimension2);
        value2.setValue(10);
        values.add(value2);
        value = new Value<Integer>();
        value.setValue(2);
        values.add(value);
        String json = mapper.writeValueAsString(values);
        System.out.println(("serialized json: " + json));
        ConfigurationManager.getConfigInstance().setProperty("d1", "v1");
        ConfigurationManager.getConfigInstance().setProperty("contextualProp", json);
        DynamicContextualProperty<Integer> prop = new DynamicContextualProperty<Integer>("contextualProp", 0);
        // d1=v1
        Assert.assertEquals(5, prop.getValue().intValue());
        // d1=v2
        ConfigurationManager.getConfigInstance().setProperty("d1", "v2");
        Assert.assertEquals(5, prop.getValue().intValue());
        // d1=v3
        ConfigurationManager.getConfigInstance().setProperty("d1", "v3");
        Assert.assertEquals(2, prop.getValue().intValue());
        // d1=v3, d2 = x1
        ConfigurationManager.getConfigInstance().setProperty("d2", "x1");
        Assert.assertEquals(10, prop.getValue().intValue());
        // d1=v1, d2 = x1
        ConfigurationManager.getConfigInstance().setProperty("d1", "v1");
        Assert.assertEquals(5, prop.getValue().intValue());
        values.remove(0);
        json = mapper.writeValueAsString(values);
        ConfigurationManager.getConfigInstance().setProperty("contextualProp", json);
        Assert.assertEquals(2, prop.getValue().intValue());
        ConfigurationManager.getConfigInstance().clearProperty("contextualProp");
        Assert.assertEquals(0, prop.getValue().intValue());
    }

    @Test
    public void testInvalidJson() {
        String invalidJson = "invalidJson";
        ConfigurationManager.getConfigInstance().setProperty("testInvalid", invalidJson);
        DynamicContextualProperty<Integer> prop = new DynamicContextualProperty<Integer>("key1", 0);
        try {
            new DynamicContextualProperty<Integer>("testInvalid", 0);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        ConfigurationManager.getConfigInstance().setProperty("key1", invalidJson);
        // should not throw exception and just return default
        Assert.assertEquals(0, prop.getValue().intValue());
    }

    @Test
    public void testSingleTextValue() {
        ConfigurationManager.getConfigInstance().setProperty("key2", "5");
        DynamicContextualProperty<Integer> prop = null;
        try {
            prop = new DynamicContextualProperty<Integer>("key2", 0);
            Assert.assertEquals(5, prop.getValue().intValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception");
        }
        ConfigurationManager.getConfigInstance().setProperty("key2", "10");
        Assert.assertEquals(10, prop.getValue().intValue());
        ConfigurationManager.getConfigInstance().setProperty("key2", "Invalid");
        Assert.assertEquals(0, prop.getValue().intValue());
        String json = "[{\"value\":2}]";
        ConfigurationManager.getConfigInstance().setProperty("key2", json);
        Assert.assertEquals(2, prop.getValue().intValue());
    }

    @Test
    public void testCallback() {
        String json = "[{\"value\":5,\"if\":{\"d1\":[\"v1\",\"v2\"]}, \"comment\": \"some comment\"},{\"value\":10,\"if\":{\"d1\":[\"v3\"],\"d2\":[\"x1\"]}, \"runtimeEval\": true},{\"value\":2}]";
        ConfigurationManager.getConfigInstance().setProperty("d1", "v2");
        final AtomicReference<Integer> ref = new AtomicReference<Integer>();
        DynamicContextualProperty<Integer> prop = new DynamicContextualProperty<Integer>("propWithCallback", 0) {
            @Override
            protected void propertyChanged(Integer newVal) {
                ref.set(newVal);
            }
        };
        Assert.assertEquals(0, prop.getValue().intValue());
        ConfigurationManager.getConfigInstance().setProperty("propWithCallback", json);
        Assert.assertEquals(5, ref.get().intValue());
        Assert.assertEquals(5, prop.getValue().intValue());
        Assert.assertEquals("some comment", prop.values.get(0).getComment());
        Assert.assertTrue(prop.values.get(1).isRuntimeEval());
        Assert.assertFalse(prop.values.get(0).isRuntimeEval());
        // set the property as a single value integer
        ConfigurationManager.getConfigInstance().setProperty("propWithCallback", "7");
        Assert.assertEquals(7, ref.get().intValue());
        Assert.assertEquals(7, prop.getValue().intValue());
    }
}

