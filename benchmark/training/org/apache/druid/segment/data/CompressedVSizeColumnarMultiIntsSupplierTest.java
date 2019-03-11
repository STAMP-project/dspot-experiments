/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.data;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Iterator;
import java.util.List;
import org.apache.druid.java.util.common.io.Closer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class CompressedVSizeColumnarMultiIntsSupplierTest {
    private Closer closer;

    protected List<int[]> vals;

    protected WritableSupplier<ColumnarMultiInts> columnarMultiIntsSupplier;

    @Test
    public void testSanity() {
        assertSame(vals, columnarMultiIntsSupplier.get());
    }

    @Test
    public void testSerde() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        columnarMultiIntsSupplier.writeTo(Channels.newChannel(baos), null);
        final byte[] bytes = baos.toByteArray();
        Assert.assertEquals(columnarMultiIntsSupplier.getSerializedSize(), bytes.length);
        WritableSupplier<ColumnarMultiInts> deserializedColumnarMultiInts = fromByteBuffer(ByteBuffer.wrap(bytes));
        assertSame(vals, deserializedColumnarMultiInts.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInvalidElementInRow() {
        columnarMultiIntsSupplier.get().get(3).get(15);
    }

    @Test
    public void testIterators() {
        Iterator<IndexedInts> iterator = columnarMultiIntsSupplier.get().iterator();
        int row = 0;
        while (iterator.hasNext()) {
            final int[] ints = vals.get(row);
            final IndexedInts vSizeIndexedInts = iterator.next();
            Assert.assertEquals(ints.length, vSizeIndexedInts.size());
            for (int i = 0, size = vSizeIndexedInts.size(); i < size; i++) {
                Assert.assertEquals(ints[i], vSizeIndexedInts.get(i));
            }
            row++;
        } 
    }
}

