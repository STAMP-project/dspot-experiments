/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.header.internals;


import java.io.IOException;
import java.util.Iterator;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.junit.Assert;
import org.junit.Test;


public class RecordHeadersTest {
    @Test
    public void testAdd() {
        Headers headers = new RecordHeaders();
        headers.add(new RecordHeader("key", "value".getBytes()));
        Header header = headers.iterator().next();
        RecordHeadersTest.assertHeader("key", "value", header);
        headers.add(new RecordHeader("key2", "value2".getBytes()));
        RecordHeadersTest.assertHeader("key2", "value2", headers.lastHeader("key2"));
        Assert.assertEquals(2, getCount(headers));
    }

    @Test
    public void testRemove() {
        Headers headers = new RecordHeaders();
        headers.add(new RecordHeader("key", "value".getBytes()));
        Assert.assertTrue(headers.iterator().hasNext());
        headers.remove("key");
        Assert.assertFalse(headers.iterator().hasNext());
    }

    @Test
    public void testAddRemoveInterleaved() {
        Headers headers = new RecordHeaders();
        headers.add(new RecordHeader("key", "value".getBytes()));
        headers.add(new RecordHeader("key2", "value2".getBytes()));
        Assert.assertTrue(headers.iterator().hasNext());
        headers.remove("key");
        Assert.assertEquals(1, getCount(headers));
        headers.add(new RecordHeader("key3", "value3".getBytes()));
        Assert.assertNull(headers.lastHeader("key"));
        RecordHeadersTest.assertHeader("key2", "value2", headers.lastHeader("key2"));
        RecordHeadersTest.assertHeader("key3", "value3", headers.lastHeader("key3"));
        Assert.assertEquals(2, getCount(headers));
        headers.remove("key2");
        Assert.assertNull(headers.lastHeader("key"));
        Assert.assertNull(headers.lastHeader("key2"));
        RecordHeadersTest.assertHeader("key3", "value3", headers.lastHeader("key3"));
        Assert.assertEquals(1, getCount(headers));
        headers.add(new RecordHeader("key3", "value4".getBytes()));
        RecordHeadersTest.assertHeader("key3", "value4", headers.lastHeader("key3"));
        Assert.assertEquals(2, getCount(headers));
        headers.add(new RecordHeader("key", "valueNew".getBytes()));
        Assert.assertEquals(3, getCount(headers));
        RecordHeadersTest.assertHeader("key", "valueNew", headers.lastHeader("key"));
        headers.remove("key3");
        Assert.assertEquals(1, getCount(headers));
        Assert.assertNull(headers.lastHeader("key2"));
        headers.remove("key");
        Assert.assertFalse(headers.iterator().hasNext());
    }

    @Test
    public void testLastHeader() {
        Headers headers = new RecordHeaders();
        headers.add(new RecordHeader("key", "value".getBytes()));
        headers.add(new RecordHeader("key", "value2".getBytes()));
        headers.add(new RecordHeader("key", "value3".getBytes()));
        RecordHeadersTest.assertHeader("key", "value3", headers.lastHeader("key"));
        Assert.assertEquals(3, getCount(headers));
    }

    @Test
    public void testReadOnly() throws IOException {
        RecordHeaders headers = new RecordHeaders();
        headers.add(new RecordHeader("key", "value".getBytes()));
        Iterator<Header> headerIteratorBeforeClose = headers.iterator();
        headers.setReadOnly();
        try {
            headers.add(new RecordHeader("key", "value".getBytes()));
            Assert.fail("IllegalStateException expected as headers are closed");
        } catch (IllegalStateException ise) {
            // expected
        }
        try {
            headers.remove("key");
            Assert.fail("IllegalStateException expected as headers are closed");
        } catch (IllegalStateException ise) {
            // expected
        }
        try {
            Iterator<Header> headerIterator = headers.iterator();
            headerIterator.next();
            headerIterator.remove();
            Assert.fail("IllegalStateException expected as headers are closed");
        } catch (IllegalStateException ise) {
            // expected
        }
        try {
            headerIteratorBeforeClose.next();
            headerIteratorBeforeClose.remove();
            Assert.fail("IllegalStateException expected as headers are closed");
        } catch (IllegalStateException ise) {
            // expected
        }
    }

    @Test
    public void testHeaders() throws IOException {
        RecordHeaders headers = new RecordHeaders();
        headers.add(new RecordHeader("key", "value".getBytes()));
        headers.add(new RecordHeader("key1", "key1value".getBytes()));
        headers.add(new RecordHeader("key", "value2".getBytes()));
        headers.add(new RecordHeader("key2", "key2value".getBytes()));
        Iterator<Header> keyHeaders = headers.headers("key").iterator();
        RecordHeadersTest.assertHeader("key", "value", keyHeaders.next());
        RecordHeadersTest.assertHeader("key", "value2", keyHeaders.next());
        Assert.assertFalse(keyHeaders.hasNext());
        keyHeaders = headers.headers("key1").iterator();
        RecordHeadersTest.assertHeader("key1", "key1value", keyHeaders.next());
        Assert.assertFalse(keyHeaders.hasNext());
        keyHeaders = headers.headers("key2").iterator();
        RecordHeadersTest.assertHeader("key2", "key2value", keyHeaders.next());
        Assert.assertFalse(keyHeaders.hasNext());
    }

    @Test
    public void testNew() throws IOException {
        RecordHeaders headers = new RecordHeaders();
        headers.add(new RecordHeader("key", "value".getBytes()));
        headers.setReadOnly();
        RecordHeaders newHeaders = new RecordHeaders(headers);
        newHeaders.add(new RecordHeader("key", "value2".getBytes()));
        // Ensure existing headers are not modified
        RecordHeadersTest.assertHeader("key", "value", headers.lastHeader("key"));
        Assert.assertEquals(1, getCount(headers));
        // Ensure new headers are modified
        RecordHeadersTest.assertHeader("key", "value2", newHeaders.lastHeader("key"));
        Assert.assertEquals(2, getCount(newHeaders));
    }
}

