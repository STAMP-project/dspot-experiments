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


import ChangeRequest.Status.PENDING;
import Dns.ChangeRequestField.START_TIME;
import Dns.ChangeRequestField.STATUS;
import Dns.ChangeRequestOption;
import RecordSet.Type.A;
import RecordSet.Type.AAAA;
import RecordSet.Type.CNAME;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;


public class ChangeRequestTest {
    private static final String ZONE_NAME = "dns-zone-name";

    private static final ChangeRequestInfo CHANGE_REQUEST_INFO = ChangeRequest.newBuilder().add(RecordSet.newBuilder("name", A).build()).delete(RecordSet.newBuilder("othername", AAAA).build()).build();

    private static final DnsOptions OPTIONS = createStrictMock(DnsOptions.class);

    private Dns dns;

    private ChangeRequest changeRequest;

    private ChangeRequest changeRequestPending;

    private ChangeRequest changeRequestPartial;

    @Test
    public void testConstructor() {
        expect(dns.getOptions()).andReturn(ChangeRequestTest.OPTIONS);
        replay(dns);
        Assert.assertEquals(new ChangeRequest(dns, ChangeRequestTest.ZONE_NAME, new ChangeRequestInfo.BuilderImpl(ChangeRequestTest.CHANGE_REQUEST_INFO)), changeRequestPartial);
        Assert.assertNotNull(changeRequest.getDns());
        Assert.assertEquals(ChangeRequestTest.ZONE_NAME, changeRequest.getZone());
        Assert.assertSame(dns, changeRequestPartial.getDns());
        Assert.assertEquals(ChangeRequestTest.ZONE_NAME, changeRequestPartial.getZone());
    }

    @Test
    public void testFromPb() {
        expect(dns.getOptions()).andReturn(ChangeRequestTest.OPTIONS).times(2);
        replay(dns);
        Assert.assertEquals(changeRequest, ChangeRequest.fromPb(dns, ChangeRequestTest.ZONE_NAME, changeRequest.toPb()));
        Assert.assertEquals(changeRequestPartial, ChangeRequest.fromPb(dns, ChangeRequestTest.ZONE_NAME, changeRequestPartial.toPb()));
    }

    @Test
    public void testEqualsAndToBuilder() {
        expect(dns.getOptions()).andReturn(ChangeRequestTest.OPTIONS).times(2);
        replay(dns);
        ChangeRequest compare = changeRequest.toBuilder().build();
        Assert.assertEquals(changeRequest, compare);
        Assert.assertEquals(changeRequest.hashCode(), compare.hashCode());
        compare = changeRequestPartial.toBuilder().build();
        Assert.assertEquals(changeRequestPartial, compare);
        Assert.assertEquals(changeRequestPartial.hashCode(), compare.hashCode());
    }

    @Test
    public void testBuilder() {
        // one for each build() call because it invokes a constructor
        expect(dns.getOptions()).andReturn(ChangeRequestTest.OPTIONS).times(9);
        replay(dns);
        String id = (changeRequest.getGeneratedId()) + "aaa";
        Assert.assertEquals(id, changeRequest.toBuilder().setGeneratedId(id).build().getGeneratedId());
        ChangeRequest modified = changeRequest.toBuilder().setStatus(PENDING).build();
        Assert.assertEquals(PENDING, modified.status());
        modified = changeRequest.toBuilder().clearDeletions().build();
        Assert.assertTrue(modified.getDeletions().isEmpty());
        modified = changeRequest.toBuilder().clearAdditions().build();
        Assert.assertTrue(modified.getAdditions().isEmpty());
        modified = changeRequest.toBuilder().setAdditions(ImmutableList.<RecordSet>of()).build();
        Assert.assertTrue(modified.getAdditions().isEmpty());
        modified = changeRequest.toBuilder().setDeletions(ImmutableList.<RecordSet>of()).build();
        Assert.assertTrue(modified.getDeletions().isEmpty());
        RecordSet cname = RecordSet.newBuilder("last", CNAME).build();
        modified = changeRequest.toBuilder().add(cname).build();
        Assert.assertTrue(modified.getAdditions().contains(cname));
        modified = changeRequest.toBuilder().delete(cname).build();
        Assert.assertTrue(modified.getDeletions().contains(cname));
        modified = changeRequest.toBuilder().setStartTime(0L).build();
        Assert.assertEquals(Long.valueOf(0), modified.getStartTimeMillis());
    }

    @Test
    public void testApplyTo() {
        expect(dns.applyChangeRequest(ChangeRequestTest.ZONE_NAME, changeRequest)).andReturn(changeRequest);
        expect(dns.applyChangeRequest(ChangeRequestTest.ZONE_NAME, changeRequest, ChangeRequestOption.fields(START_TIME))).andReturn(changeRequest);
        replay(dns);
        Assert.assertSame(changeRequest, changeRequest.applyTo(ChangeRequestTest.ZONE_NAME));
        Assert.assertSame(changeRequest, changeRequest.applyTo(ChangeRequestTest.ZONE_NAME, ChangeRequestOption.fields(START_TIME)));
    }

    @Test
    public void testReload() {
        expect(dns.getChangeRequest(ChangeRequestTest.ZONE_NAME, changeRequest.getGeneratedId())).andReturn(changeRequest);
        expect(dns.getChangeRequest(ChangeRequestTest.ZONE_NAME, changeRequest.getGeneratedId(), ChangeRequestOption.fields(START_TIME))).andReturn(changeRequest);
        replay(dns);
        Assert.assertSame(changeRequest, changeRequest.reload());
        Assert.assertSame(changeRequest, changeRequest.reload(ChangeRequestOption.fields(START_TIME)));
    }

    @Test
    public void testIsDone() {
        replay(dns);
        Assert.assertTrue(changeRequest.isDone());
        verify(dns);
        reset(dns);
        expect(dns.getChangeRequest(ChangeRequestTest.ZONE_NAME, changeRequest.getGeneratedId(), ChangeRequestOption.fields(STATUS))).andReturn(changeRequest);
        replay(dns);
        Assert.assertTrue(changeRequestPending.isDone());
        verify(dns);
    }
}

