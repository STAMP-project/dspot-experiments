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
import com.github.dozermapper.core.DozerConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MapperAware;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class CustomConverterMapperAwareTest extends AbstractFunctionalTest {
    @Test
    public void test_convert_withInjectedMapper() {
        List<CustomConverterMapperAwareTest.BeanA> list = new ArrayList<>();
        CustomConverterMapperAwareTest.BeanA b1 = new CustomConverterMapperAwareTest.BeanA("1");
        CustomConverterMapperAwareTest.BeanA b2 = new CustomConverterMapperAwareTest.BeanA("2");
        CustomConverterMapperAwareTest.BeanA b3 = new CustomConverterMapperAwareTest.BeanA("3");
        list.add(b1);
        list.add(b2);
        list.add(b3);
        Map map = mapper.map(list, HashMap.class);
        Assert.assertNotNull(map);
        Assert.assertEquals(3, map.size());
        Assert.assertTrue(map.keySet().contains(b1));
        Assert.assertTrue(map.keySet().contains(b2));
        Assert.assertTrue(map.keySet().contains(b3));
        Assert.assertEquals(b1.getA(), ((CustomConverterMapperAwareTest.BeanB) (map.get(b1))).getA().toString());
        Assert.assertEquals(b2.getA(), ((CustomConverterMapperAwareTest.BeanB) (map.get(b2))).getA().toString());
        Assert.assertEquals(b3.getA(), ((CustomConverterMapperAwareTest.BeanB) (map.get(b3))).getA().toString());
    }

    @Test
    public void test_convert_withSubclassedConverterInstance() {
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("mappings/customConverterMapperAware.xml").withCustomConverter(new CustomConverterMapperAwareTest.Converter() {
            @Override
            public Map convertTo(List source, Map destination) {
                return new HashMap() {
                    {
                        put("foo", "bar");
                    }
                };
            }
        }).build();
        HashMap result = mapper.map(new ArrayList<String>(), HashMap.class);
        Assert.assertEquals("bar", result.get("foo"));
    }

    @Test
    public void test_stackOverflow() {
        CustomConverterMapperAwareTest.BeanA a = new CustomConverterMapperAwareTest.BeanA();
        CustomConverterMapperAwareTest.BeanB b = new CustomConverterMapperAwareTest.BeanB();
        a.setBeanB(b);
        b.setBeanA(a);
        CustomConverterMapperAwareTest.Container container = new CustomConverterMapperAwareTest.Container();
        container.setBeanA(a);
        container.setBeanB(b);
        CustomConverterMapperAwareTest.Container result = mapper.map(container, CustomConverterMapperAwareTest.Container.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getBeanA());
        Assert.assertNotNull(result.getBeanB());
    }

    public static class Converter extends DozerConverter<List, Map> implements MapperAware {
        private Mapper mapper;

        public Converter() {
            super(List.class, Map.class);
        }

        public Map convertTo(List source, Map destination) {
            Map result = new HashMap();
            for (Object item : source) {
                CustomConverterMapperAwareTest.BeanB mappedItem = mapper.map(item, CustomConverterMapperAwareTest.BeanB.class);
                result.put(item, mappedItem);
            }
            return result;
        }

        public List convertFrom(Map source, List destination) {
            return destination;
        }

        public void setMapper(Mapper mapper) {
            this.mapper = mapper;
        }
    }

    public static class ConverterRecursion extends DozerConverter<CustomConverterMapperAwareTest.BeanA, CustomConverterMapperAwareTest.BeanA> implements MapperAware {
        private Mapper mapper;

        public ConverterRecursion() {
            super(CustomConverterMapperAwareTest.BeanA.class, CustomConverterMapperAwareTest.BeanA.class);
        }

        public void setMapper(Mapper mapper) {
            this.mapper = mapper;
        }

        public CustomConverterMapperAwareTest.BeanA convertTo(CustomConverterMapperAwareTest.BeanA source, CustomConverterMapperAwareTest.BeanA destination) {
            return mapper.map(source, CustomConverterMapperAwareTest.BeanA.class);
        }

        public CustomConverterMapperAwareTest.BeanA convertFrom(CustomConverterMapperAwareTest.BeanA source, CustomConverterMapperAwareTest.BeanA destination) {
            return mapper.map(source, CustomConverterMapperAwareTest.BeanA.class);
        }
    }

    public static class Container {
        CustomConverterMapperAwareTest.BeanA beanA;

        CustomConverterMapperAwareTest.BeanB beanB;

        public CustomConverterMapperAwareTest.BeanA getBeanA() {
            return beanA;
        }

        public void setBeanA(CustomConverterMapperAwareTest.BeanA beanA) {
            this.beanA = beanA;
        }

        public CustomConverterMapperAwareTest.BeanB getBeanB() {
            return beanB;
        }

        public void setBeanB(CustomConverterMapperAwareTest.BeanB beanB) {
            this.beanB = beanB;
        }
    }

    public static class BeanA {
        public BeanA() {
        }

        public BeanA(String a) {
            this.a = a;
        }

        private CustomConverterMapperAwareTest.BeanB beanB;

        private String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public CustomConverterMapperAwareTest.BeanB getBeanB() {
            return beanB;
        }

        public void setBeanB(CustomConverterMapperAwareTest.BeanB beanB) {
            this.beanB = beanB;
        }
    }

    public static class BeanB {
        private CustomConverterMapperAwareTest.BeanA beanA;

        private Integer a;

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public CustomConverterMapperAwareTest.BeanA getBeanA() {
            return beanA;
        }

        public void setBeanA(CustomConverterMapperAwareTest.BeanA beanA) {
            this.beanA = beanA;
        }
    }
}

