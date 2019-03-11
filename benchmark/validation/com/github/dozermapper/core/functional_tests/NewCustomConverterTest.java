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


import com.github.dozermapper.core.DozerConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.vo.SimpleObj;
import java.util.Collection;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


public class NewCustomConverterTest extends AbstractFunctionalTest {
    private Mapper mapper;

    @Test
    public void test_DirectMapping() {
        SimpleObj source = new SimpleObj();
        source.setField1("yes");
        SimpleObj destination = mapper.map(source, SimpleObj.class);
        Assert.assertTrue(destination.getField7());
    }

    @Test
    public void test_ParametrizedMapping() {
        SimpleObj source = new SimpleObj();
        source.setField1("*");
        source.setField7(Boolean.TRUE);
        SimpleObj destination = mapper.map(source, SimpleObj.class);
        Assert.assertNull("yes", destination.getField1());
    }

    @Test
    public void test_NoParameterConfigured() {
        Assert.assertEquals("yes", mapper.map(Boolean.TRUE, String.class));
        Assert.assertEquals("no", mapper.map(Boolean.FALSE, String.class));
        Assert.assertEquals(Boolean.TRUE, mapper.map("yes", Boolean.class));
        Assert.assertEquals(Boolean.FALSE, mapper.map("no", Boolean.class));
    }

    @Test
    public void test_Autoboxing() {
        HashSet<String> strings = new HashSet<>();
        strings.add("A");
        strings.add("B");
        NewCustomConverterTest.IntContainer result = mapper.map(strings, NewCustomConverterTest.IntContainer.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getValue());
    }

    public static class IntContainer {
        int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class IntConverter extends DozerConverter<Integer, Collection> {
        public IntConverter() {
            super(Integer.class, Collection.class);
        }

        @Override
        public Collection convertTo(Integer source, Collection destination) {
            throw new IllegalStateException();
        }

        @Override
        public Integer convertFrom(Collection source, Integer destination) {
            return source.size();
        }
    }
}

