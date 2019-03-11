/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.guice;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Properties;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import org.apache.druid.TestObjectMapper;
import org.junit.Assert;
import org.junit.Test;


public class JsonConfiguratorTest {
    private static final String PROP_PREFIX = "test.property.prefix.";

    private final ObjectMapper mapper = new TestObjectMapper();

    private final Properties properties = new Properties();

    final Validator validator = new Validator() {
        @Override
        public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
            return ImmutableSet.of();
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
            return ImmutableSet.of();
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
            return ImmutableSet.of();
        }

        @Override
        public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            return null;
        }

        @Override
        public ExecutableValidator forExecutables() {
            return null;
        }
    };

    @Test
    public void testTest() {
        Assert.assertEquals(new MappableObject("p1", ImmutableList.of("p2"), "p2"), new MappableObject("p1", ImmutableList.of("p2"), "p2"));
        Assert.assertEquals(new MappableObject("p1", null, null), new MappableObject("p1", ImmutableList.of(), null));
    }

    @Test
    public void testsimpleConfigurate() {
        final JsonConfigurator configurator = new JsonConfigurator(mapper, validator);
        properties.setProperty(((JsonConfiguratorTest.PROP_PREFIX) + "prop1"), "prop1");
        properties.setProperty(((JsonConfiguratorTest.PROP_PREFIX) + "prop1List"), "[\"prop2\"]");
        final MappableObject obj = configurator.configurate(properties, JsonConfiguratorTest.PROP_PREFIX, MappableObject.class);
        Assert.assertEquals("prop1", obj.prop1);
        Assert.assertEquals(ImmutableList.of("prop2"), obj.prop1List);
    }

    @Test
    public void testMissingConfigList() {
        final JsonConfigurator configurator = new JsonConfigurator(mapper, validator);
        properties.setProperty(((JsonConfiguratorTest.PROP_PREFIX) + "prop1"), "prop1");
        final MappableObject obj = configurator.configurate(properties, JsonConfiguratorTest.PROP_PREFIX, MappableObject.class);
        Assert.assertEquals("prop1", obj.prop1);
        Assert.assertEquals(ImmutableList.of(), obj.prop1List);
    }

    @Test
    public void testMissingConfig() {
        final JsonConfigurator configurator = new JsonConfigurator(mapper, validator);
        properties.setProperty(((JsonConfiguratorTest.PROP_PREFIX) + "prop1List"), "[\"prop2\"]");
        final MappableObject obj = configurator.configurate(properties, JsonConfiguratorTest.PROP_PREFIX, MappableObject.class);
        Assert.assertNull(obj.prop1);
        Assert.assertEquals(ImmutableList.of("prop2"), obj.prop1List);
    }

    @Test
    public void testQuotedConfig() {
        final JsonConfigurator configurator = new JsonConfigurator(mapper, validator);
        properties.setProperty(((JsonConfiguratorTest.PROP_PREFIX) + "prop1"), "testing \"prop1\"");
        final MappableObject obj = configurator.configurate(properties, JsonConfiguratorTest.PROP_PREFIX, MappableObject.class);
        Assert.assertEquals("testing \"prop1\"", obj.prop1);
        Assert.assertEquals(ImmutableList.of(), obj.prop1List);
    }

    @Test
    public void testPropertyWithDot() {
        final JsonConfigurator configurator = new JsonConfigurator(mapper, validator);
        properties.setProperty(((JsonConfiguratorTest.PROP_PREFIX) + "prop2.prop.2"), "testing");
        properties.setProperty(((JsonConfiguratorTest.PROP_PREFIX) + "prop1"), "prop1");
        final MappableObject obj = configurator.configurate(properties, JsonConfiguratorTest.PROP_PREFIX, MappableObject.class);
        Assert.assertEquals("testing", obj.prop2);
        Assert.assertEquals(ImmutableList.of(), obj.prop1List);
        Assert.assertEquals("prop1", obj.prop1);
    }
}

