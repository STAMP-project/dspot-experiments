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
package com.hazelcast.spi.discovery.impl;


import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.ValidationException;
import com.hazelcast.config.properties.ValueValidator;
import com.hazelcast.core.TypeConverter;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DiscoveryServicePropertiesUtilTest {
    private static final String PROPERTY_KEY_1 = "property1";

    private static final String PROPERTY_VALUE_1 = "propertyValue1";

    private static final PropertyDefinition PROPERTY_DEFINITION_1 = new com.hazelcast.config.properties.SimplePropertyDefinition(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1, PropertyTypeConverter.STRING);

    @Test
    public void correctProperties() {
        // given
        Map<String, Comparable> properties = Collections.singletonMap(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1, ((Comparable) (DiscoveryServicePropertiesUtilTest.PROPERTY_VALUE_1)));
        Collection<PropertyDefinition> propertyDefinitions = Collections.singletonList(DiscoveryServicePropertiesUtilTest.PROPERTY_DEFINITION_1);
        // when
        Map<String, Comparable> result = DiscoveryServicePropertiesUtil.prepareProperties(properties, propertyDefinitions);
        // then
        Assert.assertEquals(DiscoveryServicePropertiesUtilTest.PROPERTY_VALUE_1, result.get(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1));
    }

    @Test(expected = InvalidConfigurationException.class)
    public void unsatisfiedRequiredProperty() {
        // given
        Map<String, Comparable> properties = Collections.emptyMap();
        Collection<PropertyDefinition> propertyDefinitions = Collections.singletonList(DiscoveryServicePropertiesUtilTest.PROPERTY_DEFINITION_1);
        // when
        DiscoveryServicePropertiesUtil.prepareProperties(properties, propertyDefinitions);
        // then
        // throw exception
    }

    @Test
    public void unsatisfiedOptionalProperty() {
        // given
        Map<String, Comparable> properties = Collections.emptyMap();
        Collection<PropertyDefinition> propertyDefinitions = Collections.singletonList(((PropertyDefinition) (new com.hazelcast.config.properties.SimplePropertyDefinition(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1, true, PropertyTypeConverter.STRING))));
        // when
        Map<String, Comparable> result = DiscoveryServicePropertiesUtil.prepareProperties(properties, propertyDefinitions);
        // then
        Assert.assertTrue(result.isEmpty());
    }

    @Test(expected = ValidationException.class)
    public void invalidProperty() {
        // given
        Map<String, Comparable> properties = Collections.singletonMap(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1, ((Comparable) (DiscoveryServicePropertiesUtilTest.PROPERTY_VALUE_1)));
        ValueValidator<String> valueValidator = Mockito.mock(ValueValidator.class);
        BDDMockito.willThrow(new ValidationException("Invalid property")).given(valueValidator).validate(DiscoveryServicePropertiesUtilTest.PROPERTY_VALUE_1);
        Collection<PropertyDefinition> propertyDefinitions = Collections.singletonList(((PropertyDefinition) (new com.hazelcast.config.properties.SimplePropertyDefinition(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1, false, PropertyTypeConverter.STRING, new DiscoveryServicePropertiesUtilTest.DummyValidator()))));
        // when
        DiscoveryServicePropertiesUtil.prepareProperties(properties, propertyDefinitions);
        // then
        // throw exception
    }

    @Test(expected = InvalidConfigurationException.class)
    public void unknownProperty() {
        // given
        Map<String, Comparable> properties = Collections.singletonMap(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1, ((Comparable) (DiscoveryServicePropertiesUtilTest.PROPERTY_VALUE_1)));
        Collection<PropertyDefinition> propertyDefinitions = Collections.emptyList();
        // when
        DiscoveryServicePropertiesUtil.prepareProperties(properties, propertyDefinitions);
        // then
        // throw exception
    }

    @Test
    public void nullProperty() {
        // given
        Map<String, Comparable> properties = Collections.singletonMap(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1, null);
        TypeConverter typeConverter = new TypeConverter() {
            @Override
            public Comparable convert(Comparable value) {
                return value == null ? "hazel" : "cast";
            }
        };
        Collection<PropertyDefinition> propertyDefinitions = Collections.singletonList(((PropertyDefinition) (new com.hazelcast.config.properties.SimplePropertyDefinition(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1, true, typeConverter))));
        // when
        Map<String, Comparable> result = DiscoveryServicePropertiesUtil.prepareProperties(properties, propertyDefinitions);
        // then
        Assert.assertEquals("hazel", result.get(DiscoveryServicePropertiesUtilTest.PROPERTY_KEY_1));
    }

    private static class DummyValidator implements ValueValidator<String> {
        @Override
        public void validate(String value) throws ValidationException {
            throw new ValidationException("Invalid property");
        }
    }
}

