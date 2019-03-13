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


import ChangeRequest.Status;
import RecordSet.Type;
import com.google.api.services.dns.model.Change;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ChangeRequestInfoTest {
    private static final String GENERATED_ID = "cr-id-1";

    private static final Long START_TIME_MILLIS = 12334567890L;

    private static final Status STATUS = Status.PENDING;

    private static final String NAME1 = "dns1";

    private static final Type TYPE1 = Type.A;

    private static final String NAME2 = "dns2";

    private static final Type TYPE2 = Type.AAAA;

    private static final String NAME3 = "dns3";

    private static final Type TYPE3 = Type.MX;

    private static final RecordSet RECORD1 = RecordSet.newBuilder(ChangeRequestInfoTest.NAME1, ChangeRequestInfoTest.TYPE1).build();

    private static final RecordSet RECORD2 = RecordSet.newBuilder(ChangeRequestInfoTest.NAME2, ChangeRequestInfoTest.TYPE2).build();

    private static final RecordSet RECORD3 = RecordSet.newBuilder(ChangeRequestInfoTest.NAME3, ChangeRequestInfoTest.TYPE3).build();

    private static final List<RecordSet> ADDITIONS = ImmutableList.of(ChangeRequestInfoTest.RECORD1, ChangeRequestInfoTest.RECORD2);

    private static final List<RecordSet> DELETIONS = ImmutableList.of(ChangeRequestInfoTest.RECORD3);

    private static final ChangeRequestInfo CHANGE = ChangeRequest.newBuilder().add(ChangeRequestInfoTest.RECORD1).add(ChangeRequestInfoTest.RECORD2).delete(ChangeRequestInfoTest.RECORD3).setStartTime(ChangeRequestInfoTest.START_TIME_MILLIS).setStatus(ChangeRequestInfoTest.STATUS).setGeneratedId(ChangeRequestInfoTest.GENERATED_ID).build();

    @Test
    public void testEmptyBuilder() {
        ChangeRequestInfo cr = ChangeRequest.newBuilder().build();
        Assert.assertNotNull(cr.getDeletions());
        Assert.assertTrue(cr.getDeletions().isEmpty());
        Assert.assertNotNull(cr.getAdditions());
        Assert.assertTrue(cr.getAdditions().isEmpty());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(ChangeRequestInfoTest.GENERATED_ID, ChangeRequestInfoTest.CHANGE.getGeneratedId());
        Assert.assertEquals(ChangeRequestInfoTest.STATUS, ChangeRequestInfoTest.CHANGE.status());
        Assert.assertEquals(ChangeRequestInfoTest.START_TIME_MILLIS, ChangeRequestInfoTest.CHANGE.getStartTimeMillis());
        Assert.assertEquals(ChangeRequestInfoTest.ADDITIONS, ChangeRequestInfoTest.CHANGE.getAdditions());
        Assert.assertEquals(ChangeRequestInfoTest.DELETIONS, ChangeRequestInfoTest.CHANGE.getDeletions());
        List<RecordSet> recordList = ImmutableList.of(ChangeRequestInfoTest.RECORD1);
        ChangeRequestInfo another = ChangeRequestInfoTest.CHANGE.toBuilder().setAdditions(recordList).build();
        Assert.assertEquals(recordList, another.getAdditions());
        Assert.assertEquals(ChangeRequestInfoTest.CHANGE.getDeletions(), another.getDeletions());
        another = ChangeRequestInfoTest.CHANGE.toBuilder().setDeletions(recordList).build();
        Assert.assertEquals(recordList, another.getDeletions());
        Assert.assertEquals(ChangeRequestInfoTest.CHANGE.getAdditions(), another.getAdditions());
    }

    @Test
    public void testEqualsAndNotEquals() {
        ChangeRequestInfo clone = ChangeRequestInfoTest.CHANGE.toBuilder().build();
        Assert.assertEquals(ChangeRequestInfoTest.CHANGE, clone);
        clone = ChangeRequest.fromPb(ChangeRequestInfoTest.CHANGE.toPb());
        Assert.assertEquals(ChangeRequestInfoTest.CHANGE, clone);
        clone = ChangeRequestInfoTest.CHANGE.toBuilder().setGeneratedId("some-other-id").build();
        Assert.assertNotEquals(ChangeRequestInfoTest.CHANGE, clone);
        clone = ChangeRequestInfoTest.CHANGE.toBuilder().setStartTime(((ChangeRequestInfoTest.CHANGE.getStartTimeMillis()) + 1)).build();
        Assert.assertNotEquals(ChangeRequestInfoTest.CHANGE, clone);
        clone = ChangeRequestInfoTest.CHANGE.toBuilder().add(ChangeRequestInfoTest.RECORD3).build();
        Assert.assertNotEquals(ChangeRequestInfoTest.CHANGE, clone);
        clone = ChangeRequestInfoTest.CHANGE.toBuilder().delete(ChangeRequestInfoTest.RECORD1).build();
        Assert.assertNotEquals(ChangeRequestInfoTest.CHANGE, clone);
        ChangeRequestInfo empty = ChangeRequest.newBuilder().build();
        Assert.assertNotEquals(ChangeRequestInfoTest.CHANGE, empty);
        Assert.assertEquals(empty, ChangeRequest.newBuilder().build());
    }

    @Test
    public void testSameHashCodeOnEquals() {
        ChangeRequestInfo clone = ChangeRequestInfoTest.CHANGE.toBuilder().build();
        Assert.assertEquals(ChangeRequestInfoTest.CHANGE, clone);
        Assert.assertEquals(ChangeRequestInfoTest.CHANGE.hashCode(), clone.hashCode());
        ChangeRequestInfo empty = ChangeRequest.newBuilder().build();
        Assert.assertEquals(empty.hashCode(), ChangeRequest.newBuilder().build().hashCode());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertEquals(ChangeRequestInfoTest.CHANGE, ChangeRequest.fromPb(ChangeRequestInfoTest.CHANGE.toPb()));
        ChangeRequestInfo partial = ChangeRequest.newBuilder().build();
        Assert.assertEquals(partial, ChangeRequest.fromPb(partial.toPb()));
        partial = ChangeRequest.newBuilder().setGeneratedId(ChangeRequestInfoTest.GENERATED_ID).build();
        Assert.assertEquals(partial, ChangeRequest.fromPb(partial.toPb()));
        partial = ChangeRequest.newBuilder().add(ChangeRequestInfoTest.RECORD1).build();
        Assert.assertEquals(partial, ChangeRequest.fromPb(partial.toPb()));
        partial = ChangeRequest.newBuilder().delete(ChangeRequestInfoTest.RECORD1).build();
        Assert.assertEquals(partial, ChangeRequest.fromPb(partial.toPb()));
        partial = ChangeRequest.newBuilder().setAdditions(ChangeRequestInfoTest.ADDITIONS).build();
        Assert.assertEquals(partial, ChangeRequest.fromPb(partial.toPb()));
        partial = ChangeRequest.newBuilder().setDeletions(ChangeRequestInfoTest.DELETIONS).build();
        Assert.assertEquals(partial, ChangeRequest.fromPb(partial.toPb()));
        partial = ChangeRequest.newBuilder().setStartTime(ChangeRequestInfoTest.START_TIME_MILLIS).build();
        Assert.assertEquals(partial, ChangeRequest.fromPb(partial.toPb()));
        partial = ChangeRequest.newBuilder().setStatus(ChangeRequestInfoTest.STATUS).build();
        Assert.assertEquals(partial, ChangeRequest.fromPb(partial.toPb()));
    }

    @Test
    public void testToBuilder() {
        Assert.assertEquals(ChangeRequestInfoTest.CHANGE, ChangeRequestInfoTest.CHANGE.toBuilder().build());
        ChangeRequestInfo partial = ChangeRequest.newBuilder().build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ChangeRequest.newBuilder().setGeneratedId(ChangeRequestInfoTest.GENERATED_ID).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ChangeRequest.newBuilder().add(ChangeRequestInfoTest.RECORD1).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ChangeRequest.newBuilder().delete(ChangeRequestInfoTest.RECORD1).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ChangeRequest.newBuilder().setAdditions(ChangeRequestInfoTest.ADDITIONS).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ChangeRequest.newBuilder().setDeletions(ChangeRequestInfoTest.DELETIONS).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ChangeRequest.newBuilder().setStartTime(ChangeRequestInfoTest.START_TIME_MILLIS).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ChangeRequest.newBuilder().setStatus(ChangeRequestInfoTest.STATUS).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
    }

    @Test
    public void testClearAdditions() {
        ChangeRequestInfo clone = ChangeRequestInfoTest.CHANGE.toBuilder().clearAdditions().build();
        Assert.assertTrue(clone.getAdditions().isEmpty());
        Assert.assertFalse(clone.getDeletions().isEmpty());
    }

    @Test
    public void testAddAddition() {
        try {
            ChangeRequestInfoTest.CHANGE.toBuilder().add(null);
            Assert.fail("Should not be able to add null RecordSet.");
        } catch (NullPointerException e) {
            // expected
        }
        ChangeRequestInfo clone = ChangeRequestInfoTest.CHANGE.toBuilder().add(ChangeRequestInfoTest.RECORD1).build();
        Assert.assertEquals(((ChangeRequestInfoTest.CHANGE.getAdditions().size()) + 1), clone.getAdditions().size());
    }

    @Test
    public void testAddDeletion() {
        try {
            ChangeRequestInfoTest.CHANGE.toBuilder().delete(null);
            Assert.fail("Should not be able to delete null RecordSet.");
        } catch (NullPointerException e) {
            // expected
        }
        ChangeRequestInfo clone = ChangeRequestInfoTest.CHANGE.toBuilder().delete(ChangeRequestInfoTest.RECORD1).build();
        Assert.assertEquals(((ChangeRequestInfoTest.CHANGE.getDeletions().size()) + 1), clone.getDeletions().size());
    }

    @Test
    public void testClearDeletions() {
        ChangeRequestInfo clone = ChangeRequestInfoTest.CHANGE.toBuilder().clearDeletions().build();
        Assert.assertTrue(clone.getDeletions().isEmpty());
        Assert.assertFalse(clone.getAdditions().isEmpty());
    }

    @Test
    public void testRemoveAddition() {
        ChangeRequestInfo clone = ChangeRequestInfoTest.CHANGE.toBuilder().removeAddition(ChangeRequestInfoTest.RECORD1).build();
        Assert.assertTrue(clone.getAdditions().contains(ChangeRequestInfoTest.RECORD2));
        Assert.assertFalse(clone.getAdditions().contains(ChangeRequestInfoTest.RECORD1));
        Assert.assertTrue(clone.getDeletions().contains(ChangeRequestInfoTest.RECORD3));
        clone = ChangeRequestInfoTest.CHANGE.toBuilder().removeAddition(ChangeRequestInfoTest.RECORD2).removeAddition(ChangeRequestInfoTest.RECORD1).build();
        Assert.assertFalse(clone.getAdditions().contains(ChangeRequestInfoTest.RECORD2));
        Assert.assertFalse(clone.getAdditions().contains(ChangeRequestInfoTest.RECORD1));
        Assert.assertTrue(clone.getAdditions().isEmpty());
        Assert.assertTrue(clone.getDeletions().contains(ChangeRequestInfoTest.RECORD3));
    }

    @Test
    public void testRemoveDeletion() {
        ChangeRequestInfo clone = ChangeRequestInfoTest.CHANGE.toBuilder().removeDeletion(ChangeRequestInfoTest.RECORD3).build();
        Assert.assertTrue(clone.getDeletions().isEmpty());
    }

    @Test
    public void testDateParsing() {
        String startTime = "2016-01-26T18:33:43.512Z";// obtained from service

        Change change = ChangeRequestInfoTest.CHANGE.toPb().setStartTime(startTime);
        ChangeRequestInfo converted = ChangeRequest.fromPb(change);
        Assert.assertNotNull(converted.getStartTimeMillis());
        Assert.assertEquals(change, converted.toPb());
        Assert.assertEquals(change.getStartTime(), converted.toPb().getStartTime());
    }
}

