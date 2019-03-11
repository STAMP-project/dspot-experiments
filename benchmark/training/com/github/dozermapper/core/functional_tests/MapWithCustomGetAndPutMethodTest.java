/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.loader.api.TypeDefinition;
import java.util.HashMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.number.IsCloseTo;
import org.junit.Assert;
import org.junit.Test;


public class MapWithCustomGetAndPutMethodTest extends AbstractFunctionalTest {
    @Test
    public void testDefaultMapBehaviour_UseDefaultGetAndPutMethod() {
        Mapper defaultMapper = DozerBeanMapperBuilder.buildDefault();
        // Map to Object, should use "get"
        MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut input1 = MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.createInput();
        MapWithCustomGetAndPutMethodTest.ValueContainer output1 = defaultMapper.map(input1, MapWithCustomGetAndPutMethodTest.ValueContainer.class);
        Assert.assertThat(output1.getEmptyString(), CoreMatchers.is(""));
        Assert.assertThat(output1.getStringValue(), CoreMatchers.is("String"));
        Assert.assertThat(output1.getIntValue(), CoreMatchers.is(123));
        Assert.assertThat(output1.getDoubleValue(), CoreMatchers.is(IsCloseTo.closeTo(123.456, 1.0E-5)));
        // Object to Map, should use "put"
        MapWithCustomGetAndPutMethodTest.ValueContainer input2 = MapWithCustomGetAndPutMethodTest.ValueContainer.createInput();
        MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut output2 = defaultMapper.map(input2, MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class);
        Assert.assertThat(output2.get("emptyString"), CoreMatchers.is(""));
        Assert.assertThat(output2.get("stringValue"), CoreMatchers.is("Different String"));
        Assert.assertThat(output2.get("intValue"), CoreMatchers.is((-987)));
        Assert.assertThat(output2.get("doubleValue"), CoreMatchers.is((-987.654)));
    }

    @Test
    public void testMapWithCustomMethods_UseSpecifiedMethods() {
        Mapper customMapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(MapWithCustomGetAndPutMethodTest.ValueContainer.class, new TypeDefinition(MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class).mapMethods("customGet", "customPut"));
            }
        }).build();
        // Map to Object, should use "customGet"
        MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut input1 = MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.createInput();
        MapWithCustomGetAndPutMethodTest.ValueContainer output1 = customMapper.map(input1, MapWithCustomGetAndPutMethodTest.ValueContainer.class);
        Assert.assertThat(output1.getEmptyString(), CoreMatchers.is("Map contains EMPTY"));
        Assert.assertThat(output1.getStringValue(), CoreMatchers.is("String"));
        Assert.assertThat(output1.getIntValue(), CoreMatchers.is(123));
        Assert.assertThat(output1.getDoubleValue(), CoreMatchers.is(IsCloseTo.closeTo(123.456, 1.0E-5)));
        // Object to Map, should use "customPut"
        MapWithCustomGetAndPutMethodTest.ValueContainer input = MapWithCustomGetAndPutMethodTest.ValueContainer.createInput();
        MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut output = customMapper.map(input, MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class);
        Assert.assertThat(output.get("emptyString"), CoreMatchers.is("Tried to insert EMPTY"));
        Assert.assertThat(output.get("stringValue"), CoreMatchers.is("Different String"));
        Assert.assertThat(output.get("intValue"), CoreMatchers.is(987));// only the Int should be absolute

        Assert.assertThat(output.get("doubleValue"), CoreMatchers.is((-987.654)));
    }

    @Test
    public void testMapWithNullGetAndPutMethods_FallbackToDefaultMethods() {
        Mapper nullMapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(MapWithCustomGetAndPutMethodTest.ValueContainer.class, new TypeDefinition(MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class).mapMethods(null, null));
            }
        }).build();
        // Map to Object, should use "get"
        MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut input1 = MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.createInput();
        MapWithCustomGetAndPutMethodTest.ValueContainer output1 = nullMapper.map(input1, MapWithCustomGetAndPutMethodTest.ValueContainer.class);
        Assert.assertThat(output1.getEmptyString(), CoreMatchers.is(""));
        Assert.assertThat(output1.getStringValue(), CoreMatchers.is("String"));
        Assert.assertThat(output1.getIntValue(), CoreMatchers.is(123));
        Assert.assertThat(output1.getDoubleValue(), CoreMatchers.is(IsCloseTo.closeTo(123.456, 1.0E-5)));
        // Object to Map, should use "put"
        MapWithCustomGetAndPutMethodTest.ValueContainer input2 = MapWithCustomGetAndPutMethodTest.ValueContainer.createInput();
        MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut output2 = nullMapper.map(input2, MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class);
        Assert.assertThat(output2.get("emptyString"), CoreMatchers.is(""));
        Assert.assertThat(output2.get("stringValue"), CoreMatchers.is("Different String"));
        Assert.assertThat(output2.get("intValue"), CoreMatchers.is((-987)));
        Assert.assertThat(output2.get("doubleValue"), CoreMatchers.is((-987.654)));
    }

    @Test
    public void testMapWithEmptyGetAndPutMethods_FallbackToDefaultMethods() {
        Mapper emptyMapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(MapWithCustomGetAndPutMethodTest.ValueContainer.class, new TypeDefinition(MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class).mapMethods("", ""));
            }
        }).build();
        // Map to Object, should use "get"
        MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut input1 = MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.createInput();
        MapWithCustomGetAndPutMethodTest.ValueContainer output1 = emptyMapper.map(input1, MapWithCustomGetAndPutMethodTest.ValueContainer.class);
        Assert.assertThat(output1.getEmptyString(), CoreMatchers.is(""));
        Assert.assertThat(output1.getStringValue(), CoreMatchers.is("String"));
        Assert.assertThat(output1.getIntValue(), CoreMatchers.is(123));
        Assert.assertThat(output1.getDoubleValue(), CoreMatchers.is(IsCloseTo.closeTo(123.456, 1.0E-5)));
        // Object to Map, should use "put"
        MapWithCustomGetAndPutMethodTest.ValueContainer input2 = MapWithCustomGetAndPutMethodTest.ValueContainer.createInput();
        MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut output2 = emptyMapper.map(input2, MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class);
        Assert.assertThat(output2.get("emptyString"), CoreMatchers.is(""));
        Assert.assertThat(output2.get("stringValue"), CoreMatchers.is("Different String"));
        Assert.assertThat(output2.get("intValue"), CoreMatchers.is((-987)));
        Assert.assertThat(output2.get("doubleValue"), CoreMatchers.is((-987.654)));
    }

    /**
     * This is simply to make sure we are trying to be 'too intelligent' and still throwing exceptions
     * if non-existent methods are configured.
     */
    @Test(expected = MappingException.class)
    public void testMapWithInvalidGetMethod_ThrowsMappingException() {
        Mapper invalidMapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(MapWithCustomGetAndPutMethodTest.ValueContainer.class, new TypeDefinition(MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class).mapMethods("invalidGetMethod", "invalidPutMethod"));
            }
        }).build();
        // Map to Object, will try to  use "invalidGetMethod"
        MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut input = MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.createInput();
        invalidMapper.map(input, MapWithCustomGetAndPutMethodTest.ValueContainer.class);
        Assert.fail("Expected MappingException");
    }

    /**
     * This is simply to make sure we are trying to be 'too intelligent' and still throwing exceptions
     * if non-existent methods are configured.
     */
    @Test(expected = MappingException.class)
    public void testMapWithInvalidPutMethod_ThrowsMappingException() {
        Mapper invalidMapper = DozerBeanMapperBuilder.create().withMappingBuilder(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(MapWithCustomGetAndPutMethodTest.ValueContainer.class, new TypeDefinition(MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class).mapMethods("invalidGetMethod", "invalidPutMethod"));
            }
        }).build();
        // Object to Map, will try to  use "invalidPutMethod"
        MapWithCustomGetAndPutMethodTest.ValueContainer input = MapWithCustomGetAndPutMethodTest.ValueContainer.createInput();
        invalidMapper.map(input, MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut.class);
        Assert.fail("Expected MappingException");
    }

    protected static class MapWithCustomGetAndPut extends HashMap<String, Object> {
        private static final long serialVersionUID = 3085080961637343951L;

        public Object customGet(Object key) {
            Object value = super.get(key);
            if (value.equals("")) {
                return "Map contains EMPTY";
            } else {
                return value;
            }
        }

        public Object customPut(String key, Object value) {
            Object valueToInsert = value;
            if (value.equals("")) {
                valueToInsert = "Tried to insert EMPTY";
            } else
                if (value instanceof Integer) {
                    // for testing purposes, we convert integers to absolute value
                    valueToInsert = Math.abs(((Integer) (value)));
                }

            super.put(key, valueToInsert);
            return valueToInsert;
        }

        public static MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut createInput() {
            MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut result = new MapWithCustomGetAndPutMethodTest.MapWithCustomGetAndPut();
            result.put("emptyString", "");
            result.put("stringValue", "String");
            result.put("intValue", 123);
            result.put("doubleValue", 123.456);
            return result;
        }
    }

    protected static class ValueContainer {
        private String emptyString;

        private String stringValue;

        private int intValue;

        private double doubleValue;

        public String getEmptyString() {
            return emptyString;
        }

        public void setEmptyString(String nullString) {
            this.emptyString = nullString;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public static MapWithCustomGetAndPutMethodTest.ValueContainer createInput() {
            MapWithCustomGetAndPutMethodTest.ValueContainer result = new MapWithCustomGetAndPutMethodTest.ValueContainer();
            result.emptyString = "";
            result.stringValue = "Different String";
            result.intValue = -987;
            result.doubleValue = -987.654;
            return result;
        }
    }
}

