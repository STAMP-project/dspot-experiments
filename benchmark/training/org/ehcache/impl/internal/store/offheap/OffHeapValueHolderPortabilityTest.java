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
package org.ehcache.impl.internal.store.offheap;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import org.ehcache.impl.internal.store.offheap.portability.OffHeapValueHolderPortability;
import org.ehcache.impl.serialization.StringSerializer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terracotta.offheapstore.storage.portability.WriteContext;


public class OffHeapValueHolderPortabilityTest {
    private OffHeapValueHolderPortability<String> valueHolderPortability;

    private OffHeapValueHolder<String> originalValue;

    @Test
    public void testEncodeDecode() throws IOException {
        ByteBuffer encoded = valueHolderPortability.encode(originalValue);
        // uncomment to perform backward compatibility tests
        // passThroughAFile(encoded);
        OffHeapValueHolder<String> decoded = valueHolderPortability.decode(encoded);
        Assert.assertThat(originalValue, CoreMatchers.equalTo(decoded));
    }

    @Test
    public void testDecodingAPreviousVersionWithTheHits() {
        StringSerializer serializer = new StringSerializer();
        ByteBuffer serialized = serializer.serialize("test");
        long time = System.currentTimeMillis();
        ByteBuffer byteBuffer = ByteBuffer.allocate(((serialized.remaining()) + 40));
        byteBuffer.putLong(123L);// id

        byteBuffer.putLong(time);// creation time

        byteBuffer.putLong((time + 1));// last access time

        byteBuffer.putLong((time + 2));// expiration time

        byteBuffer.putLong(100L);// hits

        byteBuffer.put(serialized);// the held value

        byteBuffer.flip();
        OffHeapValueHolder<String> decoded = valueHolderPortability.decode(byteBuffer);
        Assert.assertThat(decoded.getId(), CoreMatchers.equalTo(123L));
        Assert.assertThat(decoded.creationTime(), CoreMatchers.equalTo(time));
        Assert.assertThat(decoded.lastAccessTime(), CoreMatchers.equalTo((time + 1)));
        Assert.assertThat(decoded.expirationTime(), CoreMatchers.equalTo((time + 2)));
        Assert.assertThat(decoded.get(), CoreMatchers.equalTo("test"));
    }

    @Test
    public void testWriteBackSupport() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ByteBuffer encoded = valueHolderPortability.encode(originalValue);
        WriteContext writeContext = Mockito.mock(WriteContext.class);
        OffHeapValueHolder<String> decoded = valueHolderPortability.decode(encoded, writeContext);
        decoded.setExpirationTime(4L);
        decoded.setLastAccessTime(6L);
        decoded.writeBack();
        Mockito.verify(writeContext).setLong(OffHeapValueHolderPortability.ACCESS_TIME_OFFSET, 6L);
        Mockito.verify(writeContext).setLong(OffHeapValueHolderPortability.EXPIRE_TIME_OFFSET, 4L);
    }
}

