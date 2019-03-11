/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.conf;


import Source.DEFAULT;
import Source.RUNTIME;
import Source.SYSTEM_PROPERTY;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the {@link AlluxioProperties} class.
 */
public class AlluxioPropertiesTest {
    private AlluxioProperties mProperties = new AlluxioProperties();

    private PropertyKey mKeyWithValue;

    private PropertyKey mKeyWithoutValue;

    @Test
    public void get() {
        Assert.assertEquals("value", mProperties.get(mKeyWithValue));
        Assert.assertEquals(null, mProperties.get(mKeyWithoutValue));
        mProperties.put(mKeyWithoutValue, "newValue1", RUNTIME);
        Assert.assertEquals("newValue1", mProperties.get(mKeyWithoutValue));
    }

    @Test
    public void clear() {
        mProperties.put(mKeyWithValue, "ignored1", RUNTIME);
        mProperties.put(mKeyWithoutValue, "ignored2", RUNTIME);
        mProperties.clear();
        Assert.assertEquals(null, mProperties.get(mKeyWithoutValue));
        Assert.assertEquals("value", mProperties.get(mKeyWithValue));
    }

    @Test
    public void put() {
        mProperties.put(mKeyWithValue, "value1", SYSTEM_PROPERTY);
        mProperties.put(mKeyWithoutValue, "value2", SYSTEM_PROPERTY);
        Assert.assertEquals("value1", mProperties.get(mKeyWithValue));
        Assert.assertEquals("value2", mProperties.get(mKeyWithoutValue));
        mProperties.put(mKeyWithValue, "valueLowerPriority", Source.siteProperty(""));
        Assert.assertEquals("value1", mProperties.get(mKeyWithValue));
        mProperties.put(mKeyWithValue, "valueSamePriority", SYSTEM_PROPERTY);
        Assert.assertEquals("valueSamePriority", mProperties.get(mKeyWithValue));
        mProperties.put(mKeyWithValue, "valueHigherPriority", RUNTIME);
        Assert.assertEquals("valueHigherPriority", mProperties.get(mKeyWithValue));
    }

    @Test
    public void remove() {
        mProperties.remove(mKeyWithValue);
        Assert.assertEquals(mKeyWithValue.getDefaultValue(), mProperties.get(mKeyWithValue));
    }

    @Test
    public void isSet() {
        Assert.assertTrue(mProperties.isSet(mKeyWithValue));
        Assert.assertFalse(mProperties.isSet(mKeyWithoutValue));
        mProperties.remove(mKeyWithValue);
        mProperties.put(mKeyWithoutValue, "value", RUNTIME);
        Assert.assertTrue(mProperties.isSet(mKeyWithValue));
        Assert.assertTrue(mProperties.isSet(mKeyWithoutValue));
    }

    @Test
    public void entrySet() {
        Set<Map.Entry<? extends PropertyKey, String>> expected = PropertyKey.defaultKeys().stream().map(( key) -> Maps.immutableEntry(key, key.getDefaultValue())).collect(Collectors.toSet());
        Assert.assertThat(mProperties.entrySet(), Is.is(expected));
        mProperties.put(mKeyWithValue, "value", RUNTIME);
        expected.add(Maps.immutableEntry(mKeyWithValue, "value"));
        Assert.assertThat(mProperties.entrySet(), Is.is(expected));
    }

    @Test
    public void keySet() {
        Set<PropertyKey> expected = new java.util.HashSet(PropertyKey.defaultKeys());
        Assert.assertThat(mProperties.keySet(), Is.is(expected));
        PropertyKey newKey = new PropertyKey.Builder("keySetNew").build();
        mProperties.put(newKey, "value", RUNTIME);
        expected.add(newKey);
        Assert.assertThat(mProperties.keySet(), Is.is(expected));
    }

    @Test
    public void forEach() {
        Set<PropertyKey> expected = new java.util.HashSet(PropertyKey.defaultKeys());
        Set<PropertyKey> actual = Sets.newHashSet();
        mProperties.forEach(( key, value) -> actual.add(key));
        Assert.assertThat(actual, Is.is(expected));
        PropertyKey newKey = new PropertyKey.Builder("forEachNew").build();
        mProperties.put(newKey, "value", RUNTIME);
        Set<PropertyKey> actual2 = Sets.newHashSet();
        mProperties.forEach(( key, value) -> actual2.add(key));
        expected.add(newKey);
        Assert.assertThat(actual2, Is.is(expected));
    }

    @Test
    public void setGetSource() {
        mProperties.put(mKeyWithValue, "valueIgnored", RUNTIME);
        Assert.assertEquals(RUNTIME, mProperties.getSource(mKeyWithValue));
        Assert.assertEquals(DEFAULT, mProperties.getSource(mKeyWithoutValue));
    }

    @Test
    public void merge() {
        PropertyKey newKey = new PropertyKey.Builder("mergeNew").setDefaultValue("value3").build();
        Properties sysProp = new Properties();
        sysProp.put(mKeyWithValue, "value1");
        sysProp.put(mKeyWithoutValue, "value2");
        mProperties.merge(sysProp, SYSTEM_PROPERTY);
        Assert.assertEquals(SYSTEM_PROPERTY, mProperties.getSource(mKeyWithValue));
        Assert.assertEquals(SYSTEM_PROPERTY, mProperties.getSource(mKeyWithoutValue));
        Assert.assertEquals(DEFAULT, mProperties.getSource(newKey));
        Assert.assertEquals("value1", mProperties.get(mKeyWithValue));
        Assert.assertEquals("value2", mProperties.get(mKeyWithoutValue));
        Assert.assertEquals("value3", mProperties.get(newKey));
    }
}

