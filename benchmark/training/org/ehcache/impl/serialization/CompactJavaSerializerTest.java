/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.serialization;


import java.io.ObjectStreamClass;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.ehcache.spi.persistence.StateHolder;
import org.ehcache.spi.persistence.StateRepository;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class CompactJavaSerializerTest {
    @Test
    public void testStateHolderFailureRereadBehavior() throws ClassNotFoundException {
        StateHolder<Integer, ObjectStreamClass> stateMap = Mockito.spy(new TransientStateHolder<>());
        StateRepository stateRepository = Mockito.mock(StateRepository.class);
        Mockito.when(stateRepository.getPersistentStateHolder(ArgumentMatchers.eq("CompactJavaSerializer-ObjectStreamClassIndex"), ArgumentMatchers.eq(Integer.class), ArgumentMatchers.eq(ObjectStreamClass.class), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stateMap);
        AtomicBoolean failing = new AtomicBoolean();
        Answer<Object> optionalFailure = ( invocation) -> {
            try {
                return invocation.callRealMethod();
            } finally {
                if (failing.get()) {
                    throw new RuntimeException();
                }
            }
        };
        Mockito.doAnswer(optionalFailure).when(stateMap).entrySet();
        Mockito.doAnswer(optionalFailure).when(stateMap).get(ArgumentMatchers.any());
        Mockito.doAnswer(optionalFailure).when(stateMap).putIfAbsent(ArgumentMatchers.any(), ArgumentMatchers.any());
        CompactJavaSerializer<Date> serializerA = new CompactJavaSerializer<Date>(getClass().getClassLoader());
        serializerA.init(stateRepository);
        Date object = new Date();
        failing.set(true);
        try {
            serializerA.serialize(object);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            // expected
        }
        failing.set(false);
        ByteBuffer serialized = serializerA.serialize(object);
        Assert.assertThat(serializerA.read(serialized), Is.is(object));
        Assert.assertThat(stateMap.entrySet(), hasSize(1));
        CompactJavaSerializer<Date> serializerB = new CompactJavaSerializer<Date>(getClass().getClassLoader());
        serializerB.init(stateRepository);
        Assert.assertThat(serializerB.read(serialized), Is.is(object));
    }
}

