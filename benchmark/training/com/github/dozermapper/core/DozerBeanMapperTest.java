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
package com.github.dozermapper.core;


import com.github.dozermapper.core.events.EventListener;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.generics.deepindex.TestObjectPrime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DozerBeanMapperTest {
    private Mapper mapper;

    private static final int THREAD_COUNT = 10;

    private List<Throwable> exceptions;

    @Test
    public void shouldBeThreadSafe() throws Exception {
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("mappings/testDozerBeanMapping.xml").build();
        final CountDownLatch latch = new CountDownLatch(DozerBeanMapperTest.THREAD_COUNT);
        for (int i = 0; i < (DozerBeanMapperTest.THREAD_COUNT); i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        mapper.map(new TestObject(), TestObjectPrime.class);
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }
        latch.await();
        Assert.assertTrue(exceptions.isEmpty());
    }

    @Test
    public void shouldReturnImmutableResources() throws Exception {
        mapper.map("Hello", String.class);
        assertImmutable("mappingFiles", mapper);
        assertImmutable("customConverters", mapper);
        assertImmutable("customConvertersWithId", mapper);
        assertImmutable("eventListeners", mapper);
    }

    @Test
    public void shouldSetEventListeners() {
        EventListener listener = Mockito.mock(EventListener.class);
        Mapper beanMapper = DozerBeanMapperBuilder.create().withEventListener(listener).build();
        beanMapper.map(new Object(), new Object());
        Mockito.verify(listener).onMappingStarted(ArgumentMatchers.any());
        Mockito.verify(listener).onMappingFinished(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(listener);
    }
}

