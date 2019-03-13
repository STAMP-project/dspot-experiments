/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.yaml.engine;


import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.shardingsphere.core.yaml.engine.fixture.DefaultYamlRepresenterFixture;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DefaultYamlRepresenterTest {
    @Test
    public void assertToYamlWithNull() {
        DefaultYamlRepresenterFixture actual = new DefaultYamlRepresenterFixture();
        Assert.assertThat(dumpAsMap(actual), CoreMatchers.is("{}\n"));
    }

    @Test
    public void assertToYamlWithEmpty() {
        DefaultYamlRepresenterFixture actual = new DefaultYamlRepresenterFixture();
        setValue("");
        actual.setCollection(Collections.<String>emptyList());
        setMap(Collections.<String, String>emptyMap());
        Assert.assertThat(dumpAsMap(actual), CoreMatchers.is("value: \'\'\n"));
    }

    @Test
    public void assertToYamlWithValue() {
        DefaultYamlRepresenterFixture actual = new DefaultYamlRepresenterFixture();
        setValue("value");
        setCollection(Arrays.asList("value1", "value2"));
        Map<String, String> map = new LinkedHashMap<>(2, 1);
        map.put("key1", "value1");
        map.put("key2", "value2");
        setMap(map);
        String expected = new org.yaml.snakeyaml.Yaml(new DefaultYamlRepresenter()).dumpAsMap(actual);
        Assert.assertThat(expected, CoreMatchers.containsString("collection:\n- value1\n- value2\n"));
        Assert.assertThat(expected, CoreMatchers.containsString("map:\n  key1: value1\n  key2: value2\n"));
        Assert.assertThat(expected, CoreMatchers.containsString("value: value\n"));
    }
}

