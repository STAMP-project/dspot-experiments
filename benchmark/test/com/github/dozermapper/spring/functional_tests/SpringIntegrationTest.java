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
package com.github.dozermapper.spring.functional_tests;


import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MapperModelContext;
import com.github.dozermapper.core.events.EventListener;
import com.github.dozermapper.spring.functional_tests.support.EventTestListener;
import com.github.dozermapper.spring.functional_tests.support.InjectedCustomConverter;
import com.github.dozermapper.spring.vo.Destination;
import com.github.dozermapper.spring.vo.Source;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringIntegrationTest {
    private ClassPathXmlApplicationContext context;

    @Test
    public void testCreationByFactory() {
        Mapper mapper = context.getBean("byFactory", Mapper.class);
        Assert.assertNotNull(mapper);
        Assert.assertNotNull(mapper.getMappingMetadata());
    }

    @Test
    public void testSpringNoMappingFilesSpecified() {
        // Mapper can be used without specifying any mapping files.
        // Fields that have the same name will be mapped automatically.
        Mapper implicitMapper = context.getBean("implicitMapper", Mapper.class);
        Assert.assertNotNull(implicitMapper);
        assertBasicMapping(implicitMapper);
    }

    @Test
    public void testInjectConverter() {
        Mapper mapper = context.getBean("mapperWithConverter", Mapper.class);
        Assert.assertNotNull(mapper);
        Assert.assertNotNull(mapper.getMappingMetadata());
        MapperModelContext mapperModelContext = mapper.getMapperModelContext();
        Assert.assertNotNull(mapperModelContext);
        List<CustomConverter> customConverters = mapperModelContext.getCustomConverters();
        Assert.assertEquals(1, customConverters.size());
        InjectedCustomConverter converter = context.getBean(InjectedCustomConverter.class);
        converter.setInjectedName("inject");
        assertBasicMapping(mapper);
    }

    @Test
    public void testEventListeners() {
        Mapper eventMapper = context.getBean("mapperWithEventListener", Mapper.class);
        EventTestListener listener = context.getBean(EventTestListener.class);
        Assert.assertNotNull(eventMapper);
        Assert.assertNotNull(listener);
        MapperModelContext mapperModelContext = eventMapper.getMapperModelContext();
        Assert.assertNotNull(mapperModelContext);
        Assert.assertNotNull(mapperModelContext.getEventListeners());
        Assert.assertEquals(1, mapperModelContext.getEventListeners().size());
        EventListener eventListener = mapperModelContext.getEventListeners().get(0);
        Assert.assertTrue((eventListener instanceof EventTestListener));
        Assert.assertEquals(0, listener.getInvocationCount());
        assertBasicMapping(eventMapper);
        Assert.assertEquals(4, listener.getInvocationCount());
    }

    @Test
    public void testBeanMappingBuilder() {
        Mapper mapper = context.getBean("factoryWithMappingBuilder", Mapper.class);
        Source source = new Source();
        source.setName("John");
        source.setId(2L);
        Destination destination = mapper.map(source, Destination.class);
        Assert.assertEquals("John", destination.getValue());
        Assert.assertEquals(2L, destination.getId());
    }
}

