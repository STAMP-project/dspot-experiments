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
package org.ehcache.clustered.common.internal.store.operations;


import java.nio.ByteBuffer;
import java.util.Date;
import org.ehcache.spi.serialization.Serializer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class LazyValueHolderTest {
    @Mock
    private Serializer<Date> serializer;

    @Test
    public void testGetValueDecodeOnlyOnce() throws Exception {
        Date date = Mockito.mock(Date.class);
        ByteBuffer buffer = Mockito.mock(ByteBuffer.class);
        Mockito.doReturn(date).when(serializer).read(buffer);
        LazyValueHolder<Date> valueHolder = new LazyValueHolder(buffer, serializer);
        Mockito.verify(serializer, Mockito.never()).read(buffer);// Encoded value not deserialized on creation itself

        valueHolder.getValue();
        Mockito.verify(serializer).read(buffer);// Deserialization happens on the first invocation of getValue()

        valueHolder.getValue();
        Mockito.verify(serializer).read(buffer);// Deserialization does not repeat on subsequent getValue() calls

    }

    @Test
    public void testEncodeEncodesOnlyOnce() throws Exception {
        Date date = Mockito.mock(Date.class);
        ByteBuffer buffer = Mockito.mock(ByteBuffer.class);
        Mockito.doReturn(buffer).when(serializer).serialize(date);
        LazyValueHolder<Date> valueHolder = new LazyValueHolder(date);
        Mockito.verify(serializer, Mockito.never()).serialize(date);// Value not serialized on creation itself

        valueHolder.encode(serializer);
        Mockito.verify(serializer).serialize(date);// Serialization happens on the first invocation of encode()

        valueHolder.encode(serializer);
        Mockito.verify(serializer).serialize(date);// Serialization does not repeat on subsequent encode() calls

    }

    @Test
    public void testEncodeDoesNotEncodeAlreadyEncodedValue() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(0);
        LazyValueHolder<Date> valueHolder = new LazyValueHolder(buffer, serializer);
        ByteBuffer encoded = valueHolder.encode(serializer);
        Assert.assertThat(encoded.array(), Matchers.sameInstance(buffer.array()));// buffer should be a dupicate to preserve positional parameters

        Mockito.verify(serializer, Mockito.never()).serialize(ArgumentMatchers.any(Date.class));// Value not serialized as the serialized form was available on creation itself

    }
}

