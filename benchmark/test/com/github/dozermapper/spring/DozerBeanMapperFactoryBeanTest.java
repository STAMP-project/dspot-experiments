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
package com.github.dozermapper.spring;


import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MapperModelContext;
import com.github.dozermapper.core.events.EventListener;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;


public class DozerBeanMapperFactoryBeanTest {
    private DozerBeanMapperFactoryBean factory;

    private Resource mockResource;

    private ApplicationContext mockContext;

    @Test
    public void testOk() throws Exception {
        URL url = this.getClass().getClassLoader().getResource("mappings/mappingSpring.xml");
        Mockito.when(mockResource.getURL()).thenReturn(url);
        factory.setCustomConverters(Collections.emptyList());
        factory.setCustomConvertersWithId(Collections.emptyMap());
        factory.setEventListeners(Collections.emptyList());
        factory.setFactories(Collections.emptyMap());
        factory.setMappingFiles(new Resource[]{ mockResource });
        factory.setMappingBuilders(Collections.emptyList());
        factory.setCustomFieldMapper(null);
        factory.afterPropertiesSet();
        Assert.assertEquals(Mapper.class, factory.getObjectType());
        Assert.assertTrue(factory.isSingleton());
        Mapper mapper = factory.getObject();
        Assert.assertNotNull(mapper);
        MapperModelContext mapperModelContext = mapper.getMapperModelContext();
        Assert.assertNotNull(mapperModelContext);
        List<?> files = mapperModelContext.getMappingFiles();
        Assert.assertEquals(1, files.size());
        Assert.assertEquals(("file:" + (url.getFile())), files.iterator().next());
    }

    @Test
    public void testEmpty() {
        factory.afterPropertiesSet();
    }

    @Test
    public void shouldInjectBeans() {
        HashMap<String, CustomConverter> converterHashMap = new HashMap<>();
        converterHashMap.put("a", Mockito.mock(CustomConverter.class));
        HashMap<String, BeanFactory> beanFactoryMap = new HashMap<>();
        beanFactoryMap.put("a", Mockito.mock(BeanFactory.class));
        HashMap<String, EventListener> eventListenerMap = new HashMap<>();
        eventListenerMap.put("a", Mockito.mock(EventListener.class));
        Mockito.when(mockContext.getBeansOfType(CustomConverter.class)).thenReturn(converterHashMap);
        Mockito.when(mockContext.getBeansOfType(BeanFactory.class)).thenReturn(beanFactoryMap);
        Mockito.when(mockContext.getBeansOfType(EventListener.class)).thenReturn(eventListenerMap);
        factory.afterPropertiesSet();
        Mapper mapper = factory.getObject();
        Assert.assertNotNull(mapper);
        MapperModelContext mapperModelContext = mapper.getMapperModelContext();
        Assert.assertNotNull(mapperModelContext);
        Assert.assertEquals(1, mapperModelContext.getCustomConverters().size());
        Assert.assertEquals(1, mapperModelContext.getCustomConverters().size());
        Assert.assertEquals(1, mapperModelContext.getCustomConvertersWithId().size());
        Assert.assertEquals(1, mapperModelContext.getEventListeners().size());
    }
}

