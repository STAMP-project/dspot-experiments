/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.util;


import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class TrackingBytesArrayTest {
    private TrackingBytesArray data;

    @Test
    public void testAddArraySize() throws Exception {
        Assert.assertEquals(0, data.length());
        data.copyFrom(new BytesArray("one"));
        Assert.assertEquals(3, data.length());
        data.copyFrom(new BytesArray("two"));
        Assert.assertEquals(6, data.length());
        data.copyFrom(new BytesArray("three"));
        Assert.assertEquals(11, data.length());
    }

    @Test
    public void testAddRefSize() throws Exception {
        BytesRef ref = new BytesRef();
        ref.add(new BytesArray("one"));
        ref.add(new BytesArray("three"));
        data.copyFrom(ref);
        Assert.assertEquals(8, data.length());
    }

    @Test
    public void testRemoveSize() throws Exception {
        Assert.assertEquals(0, data.length());
        data.copyFrom(new BytesArray("a"));
        data.copyFrom(new BytesArray("bb"));
        data.copyFrom(new BytesArray("ccc"));
        data.copyFrom(new BytesArray("dddd"));
        Assert.assertEquals(10, data.length());
        data.remove(1);
        Assert.assertEquals(8, data.length());
        data.remove(1);
        Assert.assertEquals(5, data.length());
    }

    @Test
    public void testWriteAfterAdding() throws Exception {
        data.copyFrom(new BytesArray("a"));
        data.copyFrom(new BytesArray("bb"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        data.writeTo(out);
        Assert.assertEquals("abb", out.toString());
    }

    @Test
    public void testWriteAfterRemoving() throws Exception {
        data.copyFrom(new BytesArray("a"));
        data.copyFrom(new BytesArray("bb"));
        data.copyFrom(new BytesArray("ccc"));
        data.remove(1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        data.writeTo(out);
        Assert.assertEquals("accc", out.toString());
    }

    @Test
    public void testPopData() throws Exception {
        Assert.assertEquals(0, data.length());
        data.copyFrom(new BytesArray("a"));
        data.copyFrom(new BytesArray("bb"));
        data.copyFrom(new BytesArray("ccc"));
        data.copyFrom(new BytesArray("dddd"));
        Assert.assertEquals(10, data.length());
        BytesArray entry = data.pop();
        Assert.assertEquals(9, data.length());
        Assert.assertEquals(1, entry.length());
        entry = data.pop();
        Assert.assertEquals(7, data.length());
        Assert.assertEquals(2, entry.length());
    }
}

