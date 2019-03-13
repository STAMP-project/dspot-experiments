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
package org.springframework.context.support;


import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.lang.Nullable;


/**
 *
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 */
public class ConversionServiceFactoryBeanTests {
    @Test
    public void createDefaultConversionService() {
        ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
        factory.afterPropertiesSet();
        ConversionService service = factory.getObject();
        Assert.assertTrue(service.canConvert(String.class, Integer.class));
    }

    @Test
    public void createDefaultConversionServiceWithSupplements() {
        ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
        Set<Object> converters = new HashSet<>();
        converters.add(new org.springframework.core.convert.converter.Converter<String, ConversionServiceFactoryBeanTests.Foo>() {
            @Override
            public ConversionServiceFactoryBeanTests.Foo convert(String source) {
                return new ConversionServiceFactoryBeanTests.Foo();
            }
        });
        converters.add(new org.springframework.core.convert.converter.ConverterFactory<String, ConversionServiceFactoryBeanTests.Bar>() {
            @Override
            public <T extends ConversionServiceFactoryBeanTests.Bar> org.springframework.core.convert.converter.Converter<String, T> getConverter(Class<T> targetType) {
                return new org.springframework.core.convert.converter.Converter<String, T>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public T convert(String source) {
                        return ((T) (new ConversionServiceFactoryBeanTests.Bar()));
                    }
                };
            }
        });
        converters.add(new GenericConverter() {
            @Override
            public Set<ConvertiblePair> getConvertibleTypes() {
                return Collections.singleton(new ConvertiblePair(String.class, ConversionServiceFactoryBeanTests.Baz.class));
            }

            @Override
            @Nullable
            public Object convert(@Nullable
            Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
                return new ConversionServiceFactoryBeanTests.Baz();
            }
        });
        factory.setConverters(converters);
        factory.afterPropertiesSet();
        ConversionService service = factory.getObject();
        Assert.assertTrue(service.canConvert(String.class, Integer.class));
        Assert.assertTrue(service.canConvert(String.class, ConversionServiceFactoryBeanTests.Foo.class));
        Assert.assertTrue(service.canConvert(String.class, ConversionServiceFactoryBeanTests.Bar.class));
        Assert.assertTrue(service.canConvert(String.class, ConversionServiceFactoryBeanTests.Baz.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDefaultConversionServiceWithInvalidSupplements() {
        ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
        Set<Object> converters = new HashSet<>();
        converters.add("bogus");
        factory.setConverters(converters);
        factory.afterPropertiesSet();
    }

    @Test
    public void conversionServiceInApplicationContext() {
        doTestConversionServiceInApplicationContext("conversionService.xml", ClassPathResource.class);
    }

    @Test
    public void conversionServiceInApplicationContextWithResourceOverriding() {
        doTestConversionServiceInApplicationContext("conversionServiceWithResourceOverriding.xml", FileSystemResource.class);
    }

    public static class Foo {}

    public static class Bar {}

    public static class Baz {}

    public static class ComplexConstructorArgument {
        public ComplexConstructorArgument(Map<String, Class<?>> map) {
            Assert.assertTrue((!(map.isEmpty())));
            Assert.assertThat(map.keySet().iterator().next(), instanceOf(String.class));
            Assert.assertThat(map.values().iterator().next(), instanceOf(Class.class));
        }
    }
}

