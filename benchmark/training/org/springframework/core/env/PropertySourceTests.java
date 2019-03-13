/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.env;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link PropertySource} implementations.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class PropertySourceTests {
    @Test
    @SuppressWarnings("serial")
    public void equals() {
        Map<String, Object> map1 = new HashMap<String, Object>() {
            {
                put("a", "b");
            }
        };
        Map<String, Object> map2 = new HashMap<String, Object>() {
            {
                put("c", "d");
            }
        };
        Properties props1 = new Properties() {
            {
                setProperty("a", "b");
            }
        };
        Properties props2 = new Properties() {
            {
                setProperty("c", "d");
            }
        };
        MapPropertySource mps = new MapPropertySource("mps", map1);
        Assert.assertThat(mps, CoreMatchers.equalTo(mps));
        Assert.assertThat(new MapPropertySource("x", map1).equals(new MapPropertySource("x", map1)), CoreMatchers.is(true));
        Assert.assertThat(new MapPropertySource("x", map1).equals(new MapPropertySource("x", map2)), CoreMatchers.is(true));
        Assert.assertThat(new MapPropertySource("x", map1).equals(new PropertiesPropertySource("x", props1)), CoreMatchers.is(true));
        Assert.assertThat(new MapPropertySource("x", map1).equals(new PropertiesPropertySource("x", props2)), CoreMatchers.is(true));
        Assert.assertThat(new MapPropertySource("x", map1).equals(new Object()), CoreMatchers.is(false));
        Assert.assertThat(new MapPropertySource("x", map1).equals("x"), CoreMatchers.is(false));
        Assert.assertThat(new MapPropertySource("x", map1).equals(new MapPropertySource("y", map1)), CoreMatchers.is(false));
        Assert.assertThat(new MapPropertySource("x", map1).equals(new MapPropertySource("y", map2)), CoreMatchers.is(false));
        Assert.assertThat(new MapPropertySource("x", map1).equals(new PropertiesPropertySource("y", props1)), CoreMatchers.is(false));
        Assert.assertThat(new MapPropertySource("x", map1).equals(new PropertiesPropertySource("y", props2)), CoreMatchers.is(false));
    }

    @Test
    @SuppressWarnings("serial")
    public void collectionsOperations() {
        Map<String, Object> map1 = new HashMap<String, Object>() {
            {
                put("a", "b");
            }
        };
        Map<String, Object> map2 = new HashMap<String, Object>() {
            {
                put("c", "d");
            }
        };
        PropertySource<?> ps1 = new MapPropertySource("ps1", map1);
        ps1.getSource();
        List<PropertySource<?>> propertySources = new ArrayList<>();
        Assert.assertThat(propertySources.add(ps1), CoreMatchers.equalTo(true));
        Assert.assertThat(propertySources.contains(ps1), CoreMatchers.is(true));
        Assert.assertThat(propertySources.contains(PropertySource.named("ps1")), CoreMatchers.is(true));
        PropertySource<?> ps1replacement = new MapPropertySource("ps1", map2);// notice - different map

        Assert.assertThat(propertySources.add(ps1replacement), CoreMatchers.is(true));// true because linkedlist allows duplicates

        Assert.assertThat(propertySources.size(), CoreMatchers.is(2));
        Assert.assertThat(propertySources.remove(PropertySource.named("ps1")), CoreMatchers.is(true));
        Assert.assertThat(propertySources.size(), CoreMatchers.is(1));
        Assert.assertThat(propertySources.remove(PropertySource.named("ps1")), CoreMatchers.is(true));
        Assert.assertThat(propertySources.size(), CoreMatchers.is(0));
        PropertySource<?> ps2 = new MapPropertySource("ps2", map2);
        propertySources.add(ps1);
        propertySources.add(ps2);
        Assert.assertThat(propertySources.indexOf(PropertySource.named("ps1")), CoreMatchers.is(0));
        Assert.assertThat(propertySources.indexOf(PropertySource.named("ps2")), CoreMatchers.is(1));
        propertySources.clear();
    }
}

