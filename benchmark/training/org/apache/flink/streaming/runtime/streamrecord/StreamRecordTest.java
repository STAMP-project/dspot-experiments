/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.streamrecord;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link StreamRecord}.
 */
public class StreamRecordTest {
    @Test
    public void testWithNoTimestamp() {
        StreamRecord<String> record = new StreamRecord("test");
        Assert.assertTrue(record.isRecord());
        Assert.assertFalse(record.isWatermark());
        Assert.assertFalse(record.hasTimestamp());
        Assert.assertEquals("test", record.getValue());
        // try {
        // record.getTimestamp();
        // fail("should throw an exception");
        // } catch (IllegalStateException e) {
        // assertTrue(e.getMessage().contains("timestamp"));
        // }
        // for now, the "no timestamp case" returns Long.MIN_VALUE
        Assert.assertEquals(Long.MIN_VALUE, record.getTimestamp());
        Assert.assertNotNull(record.toString());
        Assert.assertTrue(((record.hashCode()) == (new StreamRecord("test").hashCode())));
        Assert.assertTrue(record.equals(new StreamRecord("test")));
        Assert.assertEquals(record, record.asRecord());
        try {
            record.asWatermark();
            Assert.fail("should throw an exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testWithTimestamp() {
        StreamRecord<String> record = new StreamRecord("foo", 42);
        Assert.assertTrue(record.isRecord());
        Assert.assertFalse(record.isWatermark());
        Assert.assertTrue(record.hasTimestamp());
        Assert.assertEquals(42L, record.getTimestamp());
        Assert.assertEquals("foo", record.getValue());
        Assert.assertNotNull(record.toString());
        Assert.assertTrue(((record.hashCode()) == (new StreamRecord("foo", 42).hashCode())));
        Assert.assertTrue(((record.hashCode()) != (new StreamRecord("foo").hashCode())));
        Assert.assertTrue(record.equals(new StreamRecord("foo", 42)));
        Assert.assertFalse(record.equals(new StreamRecord("foo")));
        Assert.assertEquals(record, record.asRecord());
        try {
            record.asWatermark();
            Assert.fail("should throw an exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testAllowedTimestampRange() {
        Assert.assertEquals(0L, new StreamRecord("test", 0).getTimestamp());
        Assert.assertEquals((-1L), new StreamRecord("test", (-1)).getTimestamp());
        Assert.assertEquals(1L, new StreamRecord("test", 1).getTimestamp());
        Assert.assertEquals(Long.MIN_VALUE, new StreamRecord("test", Long.MIN_VALUE).getTimestamp());
        Assert.assertEquals(Long.MAX_VALUE, new StreamRecord("test", Long.MAX_VALUE).getTimestamp());
    }

    @Test
    public void testReplacePreservesTimestamp() {
        StreamRecord<String> recNoTimestamp = new StreamRecord("o sole mio");
        StreamRecord<Integer> newRecNoTimestamp = recNoTimestamp.replace(17);
        Assert.assertFalse(newRecNoTimestamp.hasTimestamp());
        StreamRecord<String> recWithTimestamp = new StreamRecord("la dolce vita", 99);
        StreamRecord<Integer> newRecWithTimestamp = recWithTimestamp.replace(17);
        Assert.assertTrue(newRecWithTimestamp.hasTimestamp());
        Assert.assertEquals(99L, newRecWithTimestamp.getTimestamp());
    }

    @Test
    public void testReplaceWithTimestampOverridesTimestamp() {
        StreamRecord<String> record = new StreamRecord("la divina comedia");
        Assert.assertFalse(record.hasTimestamp());
        StreamRecord<Double> newRecord = record.replace(3.14, 123);
        Assert.assertTrue(newRecord.hasTimestamp());
        Assert.assertEquals(123L, newRecord.getTimestamp());
    }

    @Test
    public void testCopy() {
        StreamRecord<String> recNoTimestamp = new StreamRecord<String>("test");
        StreamRecord<String> recNoTimestampCopy = recNoTimestamp.copy("test");
        Assert.assertEquals(recNoTimestamp, recNoTimestampCopy);
        StreamRecord<String> recWithTimestamp = new StreamRecord<String>("test", 99);
        StreamRecord<String> recWithTimestampCopy = recWithTimestamp.copy("test");
        Assert.assertEquals(recWithTimestamp, recWithTimestampCopy);
    }

    @Test
    public void testCopyTo() {
        StreamRecord<String> recNoTimestamp = new StreamRecord<String>("test");
        StreamRecord<String> recNoTimestampCopy = new StreamRecord(null);
        recNoTimestamp.copyTo("test", recNoTimestampCopy);
        Assert.assertEquals(recNoTimestamp, recNoTimestampCopy);
        StreamRecord<String> recWithTimestamp = new StreamRecord<String>("test", 99);
        StreamRecord<String> recWithTimestampCopy = new StreamRecord(null);
        recWithTimestamp.copyTo("test", recWithTimestampCopy);
        Assert.assertEquals(recWithTimestamp, recWithTimestampCopy);
    }

    @Test
    public void testSetAndEraseTimestamps() {
        StreamRecord<String> rec = new StreamRecord<String>("hello");
        Assert.assertFalse(rec.hasTimestamp());
        rec.setTimestamp(13456L);
        Assert.assertTrue(rec.hasTimestamp());
        Assert.assertEquals(13456L, rec.getTimestamp());
        rec.eraseTimestamp();
        Assert.assertFalse(rec.hasTimestamp());
    }
}

