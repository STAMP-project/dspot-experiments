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


import org.junit.Assert;
import org.junit.Test;


public class ImmutableMappingTest extends AbstractFunctionalTest {
    private static final String TEST_STRING = "testString";

    private static final Integer TEST_INTEGER = 13;

    @Test
    public void shouldMapImmutableTypes() {
        ImmutableMappingTest.Immutable source = new ImmutableMappingTest.Immutable(ImmutableMappingTest.TEST_STRING);
        ImmutableMappingTest.Immutable result = mapper.map(source, ImmutableMappingTest.Immutable.class);
        Assert.assertEquals(ImmutableMappingTest.TEST_STRING, result.getValue());
    }

    @Test
    public void shouldMapImmutableTypesWithInheritance() {
        ImmutableMappingTest.ImmutableChild source = new ImmutableMappingTest.ImmutableChild(ImmutableMappingTest.TEST_STRING, ImmutableMappingTest.TEST_INTEGER);
        ImmutableMappingTest.ImmutableChild result = mapper.map(source, ImmutableMappingTest.ImmutableChild.class);
        Assert.assertEquals(ImmutableMappingTest.TEST_STRING, result.getValue());
        Assert.assertEquals(ImmutableMappingTest.TEST_INTEGER, result.getChildValue());
    }

    @Test
    public void shouldMapFromMutableToImmutable() {
        ImmutableMappingTest.MutableClass source = new ImmutableMappingTest.MutableClass(ImmutableMappingTest.TEST_STRING, ImmutableMappingTest.TEST_INTEGER);
        ImmutableMappingTest.Immutable result = mapper.map(source, ImmutableMappingTest.Immutable.class);
        Assert.assertEquals(ImmutableMappingTest.TEST_STRING, result.getValue());
    }

    @Test
    public void shouldMapFromImmutableToMutable() {
        ImmutableMappingTest.Immutable source = new ImmutableMappingTest.Immutable(ImmutableMappingTest.TEST_STRING);
        ImmutableMappingTest.MutableClass result = mapper.map(source, ImmutableMappingTest.MutableClass.class);
        Assert.assertEquals(ImmutableMappingTest.TEST_STRING, result.getStringVar());
        Assert.assertEquals(null, result.getIntegerVar());
    }

    @Test
    public void shouldInitializeUnmappedFields() {
        ImmutableMappingTest.MutableClass source = new ImmutableMappingTest.MutableClass(ImmutableMappingTest.TEST_STRING, ImmutableMappingTest.TEST_INTEGER);
        ImmutableMappingTest.ImmutableChild result = mapper.map(source, ImmutableMappingTest.ImmutableChild.class);
        Assert.assertEquals(ImmutableMappingTest.TEST_STRING, result.getValue());
        Assert.assertEquals(null, result.getChildValue());
    }

    public static class Immutable {
        private final String value;

        public Immutable(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class ImmutableChild extends ImmutableMappingTest.Immutable {
        private final Integer childValue;

        public ImmutableChild(String value, Integer childValue) {
            super(value);
            this.childValue = childValue;
        }

        public Integer getChildValue() {
            return childValue;
        }
    }

    public static class MutableClass {
        private String stringVar;

        private Integer integerVar;

        public MutableClass() {
        }

        public MutableClass(String stringVar, Integer integerVar) {
            this.stringVar = stringVar;
            this.integerVar = integerVar;
        }

        public String getStringVar() {
            return stringVar;
        }

        public void setStringVar(String stringVar) {
            this.stringVar = stringVar;
        }

        public Integer getIntegerVar() {
            return integerVar;
        }

        public void setIntegerVar(Integer integerVar) {
            this.integerVar = integerVar;
        }
    }
}

