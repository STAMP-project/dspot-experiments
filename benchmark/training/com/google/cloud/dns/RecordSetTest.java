/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.dns;


import RecordSet.Type;
import RecordSet.Type.TXT;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class RecordSetTest {
    private static final String NAME = "example.com.";

    private static final Integer TTL = 3600;

    private static final TimeUnit UNIT = TimeUnit.HOURS;

    private static final Integer UNIT_TTL = 1;

    private static final Type TYPE = Type.AAAA;

    private static final RecordSet RECORD_SET = RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).setTtl(RecordSetTest.UNIT_TTL, RecordSetTest.UNIT).build();

    @Test
    public void testDefaultDnsRecord() {
        RecordSet recordSet = RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).build();
        Assert.assertEquals(0, recordSet.getRecords().size());
        Assert.assertEquals(RecordSetTest.TYPE, recordSet.getType());
        Assert.assertEquals(RecordSetTest.NAME, recordSet.getName());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(RecordSetTest.NAME, RecordSetTest.RECORD_SET.getName());
        Assert.assertEquals(RecordSetTest.TTL, RecordSetTest.RECORD_SET.getTtl());
        Assert.assertEquals(RecordSetTest.TYPE, RecordSetTest.RECORD_SET.getType());
        Assert.assertEquals(0, RecordSetTest.RECORD_SET.getRecords().size());
        // verify that one can add records to the record set
        String testingRecord = "Testing recordSet";
        String anotherTestingRecord = "Another recordSet 123";
        RecordSet anotherRecord = RecordSetTest.RECORD_SET.toBuilder().addRecord(testingRecord).addRecord(anotherTestingRecord).build();
        Assert.assertEquals(2, anotherRecord.getRecords().size());
        Assert.assertTrue(anotherRecord.getRecords().contains(testingRecord));
        Assert.assertTrue(anotherRecord.getRecords().contains(anotherTestingRecord));
    }

    @Test
    public void testValidTtl() {
        try {
            RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).setTtl((-1), TimeUnit.SECONDS);
            Assert.fail("A negative value is not acceptable for ttl.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).setTtl(0, TimeUnit.SECONDS);
        RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).setTtl(Integer.MAX_VALUE, TimeUnit.SECONDS);
        try {
            RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).setTtl(Integer.MAX_VALUE, TimeUnit.HOURS);
            Assert.fail("This value is too large for int.");
        } catch (IllegalArgumentException e) {
            // expected
        }
        RecordSet record = RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).setTtl(RecordSetTest.UNIT_TTL, RecordSetTest.UNIT).build();
        Assert.assertEquals(RecordSetTest.TTL, record.getTtl());
    }

    @Test
    public void testEqualsAndNotEquals() {
        RecordSet clone = RecordSetTest.RECORD_SET.toBuilder().build();
        Assert.assertEquals(RecordSetTest.RECORD_SET, clone);
        clone = RecordSetTest.RECORD_SET.toBuilder().addRecord("another recordSet").build();
        Assert.assertNotEquals(RecordSetTest.RECORD_SET, clone);
        String differentName = "totally different name";
        clone = RecordSetTest.RECORD_SET.toBuilder().setName(differentName).build();
        Assert.assertNotEquals(RecordSetTest.RECORD_SET, clone);
        clone = RecordSetTest.RECORD_SET.toBuilder().setTtl(((RecordSetTest.RECORD_SET.getTtl()) + 1), TimeUnit.SECONDS).build();
        Assert.assertNotEquals(RecordSetTest.RECORD_SET, clone);
        clone = RecordSetTest.RECORD_SET.toBuilder().setType(TXT).build();
        Assert.assertNotEquals(RecordSetTest.RECORD_SET, clone);
    }

    @Test
    public void testSameHashCodeOnEquals() {
        int hash = RecordSetTest.RECORD_SET.hashCode();
        RecordSet clone = RecordSetTest.RECORD_SET.toBuilder().build();
        Assert.assertEquals(clone.hashCode(), hash);
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertEquals(RecordSetTest.RECORD_SET, RecordSet.fromPb(RecordSetTest.RECORD_SET.toPb()));
        RecordSet partial = RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).build();
        Assert.assertEquals(partial, RecordSet.fromPb(partial.toPb()));
        partial = RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).addRecord("test").build();
        Assert.assertEquals(partial, RecordSet.fromPb(partial.toPb()));
        partial = RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).setTtl(15, TimeUnit.SECONDS).build();
        Assert.assertEquals(partial, RecordSet.fromPb(partial.toPb()));
    }

    @Test
    public void testToBuilder() {
        Assert.assertEquals(RecordSetTest.RECORD_SET, RecordSetTest.RECORD_SET.toBuilder().build());
        RecordSet partial = RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).addRecord("test").build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = RecordSet.newBuilder(RecordSetTest.NAME, RecordSetTest.TYPE).setTtl(15, TimeUnit.SECONDS).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
    }

    @Test
    public void clearRecordSet() {
        // make sure that we are starting not empty
        RecordSet clone = RecordSetTest.RECORD_SET.toBuilder().addRecord("record").addRecord("another").build();
        Assert.assertNotEquals(0, clone.getRecords().size());
        clone = clone.toBuilder().clearRecords().build();
        Assert.assertEquals(0, clone.getRecords().size());
        clone.toPb();// verify that pb allows it

    }

    @Test
    public void removeFromRecordSet() {
        String recordString = "record";
        // make sure that we are starting not empty
        RecordSet clone = RecordSetTest.RECORD_SET.toBuilder().addRecord(recordString).build();
        Assert.assertNotEquals(0, clone.getRecords().size());
        clone = clone.toBuilder().removeRecord(recordString).build();
        Assert.assertEquals(0, clone.getRecords().size());
    }
}

