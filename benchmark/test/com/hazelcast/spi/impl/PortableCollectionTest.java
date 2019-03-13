/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl;


import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PortableCollectionTest {
    @Test
    public void testReadPortable() throws IOException {
        final int expectedSize = 1;
        final byte[] payload = "12345678".getBytes();
        PortableReader reader = Mockito.mock(PortableReader.class);
        ObjectDataInput input = Mockito.mock(ObjectDataInput.class);
        Mockito.when(reader.readBoolean("l")).thenReturn(true);
        Mockito.when(reader.readInt("s")).thenReturn(1);
        Mockito.when(reader.getRawDataInput()).thenReturn(input);
        Mockito.when(input.readData()).thenReturn(new Packet(payload));
        PortableCollection collection = new PortableCollection();
        collection.readPortable(reader);
        Collection<Data> actual = collection.getCollection();
        Assert.assertThat(actual.size(), Is.is(expectedSize));
        Assert.assertThat(actual, Matchers.hasItem(new Packet(payload)));
        Mockito.verify(reader).readBoolean("l");
        Mockito.verify(reader).readInt("s");
        Mockito.verify(input).readData();
    }

    @Test
    public void testWritePortable() throws IOException {
        final byte[] payload = "12345678".getBytes();
        Data data = new Packet(payload);
        PortableWriter portableWriter = Mockito.mock(PortableWriter.class);
        ObjectDataOutput out = Mockito.mock(ObjectDataOutput.class);
        Collection<Data> coll = new ArrayList<Data>();
        coll.add(data);
        Mockito.when(portableWriter.getRawDataOutput()).thenReturn(out);
        PortableCollection collection = new PortableCollection(coll);
        collection.writePortable(portableWriter);
        Mockito.verify(portableWriter).writeBoolean("l", true);
        Mockito.verify(portableWriter).writeInt("s", coll.size());
        Mockito.verify(out).writeData(data);
    }
}

