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


import Dns.ChangeRequestField.START_TIME;
import Dns.ChangeRequestListOption;
import Dns.ChangeRequestOption;
import Dns.RecordSetListOption;
import Dns.ZoneField.CREATION_TIME;
import Dns.ZoneOption;
import Zone.Builder;
import com.google.api.gax.paging.Page;
import com.google.common.collect.ImmutableList;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;


public class ZoneTest {
    private static final String ZONE_NAME = "dns-zone-name";

    private static final String ZONE_ID = "123";

    private static final ZoneInfo ZONE_INFO = Zone.of(ZoneTest.ZONE_NAME, "example.com", "description").toBuilder().setGeneratedId(ZoneTest.ZONE_ID).setCreationTimeMillis(123478946464L).build();

    private static final ZoneInfo NO_ID_INFO = ZoneInfo.of(ZoneTest.ZONE_NAME, "another-example.com", "description").toBuilder().setCreationTimeMillis(893123464L).build();

    private static final ZoneOption ZONE_FIELD_OPTIONS = ZoneOption.fields(CREATION_TIME);

    private static final RecordSetListOption DNS_RECORD_OPTIONS = RecordSetListOption.dnsName("some-dns");

    private static final ChangeRequestOption CHANGE_REQUEST_FIELD_OPTIONS = ChangeRequestOption.fields(START_TIME);

    private static final ChangeRequestListOption CHANGE_REQUEST_LIST_OPTIONS = ChangeRequestListOption.fields(START_TIME);

    private static final ChangeRequestInfo CHANGE_REQUEST = ChangeRequestInfo.newBuilder().setGeneratedId("someid").build();

    private static final ChangeRequestInfo CHANGE_REQUEST_NO_ID = ChangeRequestInfo.newBuilder().build();

    private static final DnsException EXCEPTION = new DnsException((-1), "message", null);

    private static final DnsOptions OPTIONS = createStrictMock(DnsOptions.class);

    private Dns dns;

    private Zone zone;

    private Zone zoneNoId;

    private ChangeRequest changeRequestAfter;

    @Test
    public void testConstructor() {
        replay(dns);
        Assert.assertEquals(ZoneTest.ZONE_INFO.toPb(), zone.toPb());
        Assert.assertNotNull(zone.getDns());
        Assert.assertEquals(dns, zone.getDns());
    }

    @Test
    public void deleteByNameAndFound() {
        expect(dns.delete(ZoneTest.ZONE_NAME)).andReturn(true).times(2);
        replay(dns);
        boolean result = zone.delete();
        Assert.assertTrue(result);
        result = zoneNoId.delete();
        Assert.assertTrue(result);
    }

    @Test
    public void deleteByNameAndNotFound() {
        expect(dns.delete(ZoneTest.ZONE_NAME)).andReturn(false).times(2);
        replay(dns);
        boolean result = zoneNoId.delete();
        Assert.assertFalse(result);
        result = zone.delete();
        Assert.assertFalse(result);
    }

    @Test
    public void listDnsRecordsByNameAndFound() {
        @SuppressWarnings("unchecked")
        Page<RecordSet> pageMock = createStrictMock(Page.class);
        replay(pageMock);
        expect(dns.listRecordSets(ZoneTest.ZONE_NAME)).andReturn(pageMock).times(2);
        // again for options
        expect(dns.listRecordSets(ZoneTest.ZONE_NAME, ZoneTest.DNS_RECORD_OPTIONS)).andReturn(pageMock).times(2);
        replay(dns);
        Page<RecordSet> result = zone.listRecordSets();
        Assert.assertSame(pageMock, result);
        result = zoneNoId.listRecordSets();
        Assert.assertSame(pageMock, result);
        verify(pageMock);
        zone.listRecordSets(ZoneTest.DNS_RECORD_OPTIONS);// check options

        zoneNoId.listRecordSets(ZoneTest.DNS_RECORD_OPTIONS);// check options

    }

    @Test
    public void listDnsRecordsByNameAndNotFound() {
        expect(dns.listRecordSets(ZoneTest.ZONE_NAME)).andThrow(ZoneTest.EXCEPTION).times(2);
        // again for options
        expect(dns.listRecordSets(ZoneTest.ZONE_NAME, ZoneTest.DNS_RECORD_OPTIONS)).andThrow(ZoneTest.EXCEPTION).times(2);
        replay(dns);
        try {
            zoneNoId.listRecordSets();
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zone.listRecordSets();
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zoneNoId.listRecordSets(ZoneTest.DNS_RECORD_OPTIONS);// check options

            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zone.listRecordSets(ZoneTest.DNS_RECORD_OPTIONS);// check options

            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
    }

    @Test
    public void reloadByNameAndFound() {
        expect(dns.getZone(ZoneTest.ZONE_NAME)).andReturn(zone).times(2);
        // again for options
        expect(dns.getZone(ZoneTest.ZONE_NAME, ZoneTest.ZONE_FIELD_OPTIONS)).andReturn(zoneNoId);
        expect(dns.getZone(ZoneTest.ZONE_NAME, ZoneTest.ZONE_FIELD_OPTIONS)).andReturn(zone);
        replay(dns);
        Zone result = zoneNoId.reload();
        Assert.assertSame(zone.getDns(), result.getDns());
        Assert.assertEquals(zone, result);
        result = zone.reload();
        Assert.assertSame(zone.getDns(), result.getDns());
        Assert.assertEquals(zone, result);
        zoneNoId.reload(ZoneTest.ZONE_FIELD_OPTIONS);// check options

        zone.reload(ZoneTest.ZONE_FIELD_OPTIONS);// check options

    }

    @Test
    public void reloadByNameAndNotFound() {
        expect(dns.getZone(ZoneTest.ZONE_NAME)).andReturn(null).times(2);
        // again for options
        expect(dns.getZone(ZoneTest.ZONE_NAME, ZoneTest.ZONE_FIELD_OPTIONS)).andReturn(null).times(2);
        replay(dns);
        Zone result = zoneNoId.reload();
        Assert.assertNull(result);
        result = zone.reload();
        Assert.assertNull(result);
        zoneNoId.reload(ZoneTest.ZONE_FIELD_OPTIONS);// for options

        zone.reload(ZoneTest.ZONE_FIELD_OPTIONS);// for options

    }

    @Test
    public void applyChangeByNameAndFound() {
        expect(dns.applyChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST)).andReturn(changeRequestAfter);
        expect(dns.applyChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST)).andReturn(changeRequestAfter);
        // again for options
        expect(dns.applyChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS)).andReturn(changeRequestAfter);
        expect(dns.applyChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS)).andReturn(changeRequestAfter);
        replay(dns);
        ChangeRequest result = zoneNoId.applyChangeRequest(ZoneTest.CHANGE_REQUEST);
        Assert.assertEquals(changeRequestAfter, result);
        result = zone.applyChangeRequest(ZoneTest.CHANGE_REQUEST);
        Assert.assertEquals(changeRequestAfter, result);
        // check options
        result = zoneNoId.applyChangeRequest(ZoneTest.CHANGE_REQUEST, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
        Assert.assertEquals(changeRequestAfter, result);
        result = zone.applyChangeRequest(ZoneTest.CHANGE_REQUEST, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
        Assert.assertEquals(changeRequestAfter, result);
    }

    @Test
    public void applyChangeByNameAndNotFound() {
        // ID is not set
        expect(dns.applyChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST)).andThrow(ZoneTest.EXCEPTION).times(2);
        // again for options
        expect(dns.applyChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS)).andThrow(ZoneTest.EXCEPTION).times(2);
        replay(dns);
        try {
            zoneNoId.applyChangeRequest(ZoneTest.CHANGE_REQUEST);
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zone.applyChangeRequest(ZoneTest.CHANGE_REQUEST);
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        // check options
        try {
            zoneNoId.applyChangeRequest(ZoneTest.CHANGE_REQUEST, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zone.applyChangeRequest(ZoneTest.CHANGE_REQUEST, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
    }

    @Test
    public void applyNullChangeRequest() {
        replay(dns);// no calls expected

        try {
            zone.applyChangeRequest(null);
            Assert.fail("Cannot apply null ChangeRequest.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            zone.applyChangeRequest(null, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Cannot apply null ChangeRequest.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            zoneNoId.applyChangeRequest(null);
            Assert.fail("Cannot apply null ChangeRequest.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            zoneNoId.applyChangeRequest(null, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Cannot apply null ChangeRequest.");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void getChangeAndZoneFoundByName() {
        expect(dns.getChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST.getGeneratedId())).andReturn(changeRequestAfter).times(2);
        // again for options
        expect(dns.getChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS)).andReturn(changeRequestAfter).times(2);
        replay(dns);
        ChangeRequest result = zoneNoId.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId());
        Assert.assertEquals(changeRequestAfter, result);
        result = zone.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId());
        Assert.assertEquals(changeRequestAfter, result);
        // check options
        result = zoneNoId.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
        Assert.assertEquals(changeRequestAfter, result);
        result = zone.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
        Assert.assertEquals(changeRequestAfter, result);
    }

    @Test
    public void getChangeAndZoneNotFoundByName() {
        expect(dns.getChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST.getGeneratedId())).andThrow(ZoneTest.EXCEPTION).times(2);
        // again for options
        expect(dns.getChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS)).andThrow(ZoneTest.EXCEPTION).times(2);
        replay(dns);
        try {
            zoneNoId.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId());
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zone.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId());
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        // check options
        try {
            zoneNoId.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zone.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
    }

    @Test
    public void getChangedWhichDoesNotExistZoneFound() {
        expect(dns.getChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST.getGeneratedId())).andReturn(null).times(2);
        // again for options
        expect(dns.getChangeRequest(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS)).andReturn(null).times(2);
        replay(dns);
        Assert.assertNull(zoneNoId.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId()));
        Assert.assertNull(zone.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId()));
        Assert.assertNull(zoneNoId.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS));
        Assert.assertNull(zone.getChangeRequest(ZoneTest.CHANGE_REQUEST.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS));
    }

    @Test
    public void getNullChangeRequest() {
        replay(dns);// no calls expected

        try {
            zone.getChangeRequest(null);
            Assert.fail("Cannot get null ChangeRequest.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            zone.getChangeRequest(null, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Cannot get null ChangeRequest.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            zoneNoId.getChangeRequest(null);
            Assert.fail("Cannot get null ChangeRequest.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            zoneNoId.getChangeRequest(null, ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Cannot get null ChangeRequest.");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void getChangeRequestWithNoId() {
        replay(dns);// no calls expected

        try {
            zone.getChangeRequest(ZoneTest.CHANGE_REQUEST_NO_ID.getGeneratedId());
            Assert.fail("Cannot get ChangeRequest by null id.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            zone.getChangeRequest(ZoneTest.CHANGE_REQUEST_NO_ID.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Cannot get ChangeRequest by null id.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            zoneNoId.getChangeRequest(ZoneTest.CHANGE_REQUEST_NO_ID.getGeneratedId());
            Assert.fail("Cannot get ChangeRequest by null id.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            zoneNoId.getChangeRequest(ZoneTest.CHANGE_REQUEST_NO_ID.getGeneratedId(), ZoneTest.CHANGE_REQUEST_FIELD_OPTIONS);
            Assert.fail("Cannot get ChangeRequest by null id.");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void listChangeRequestsAndZoneFound() {
        @SuppressWarnings("unchecked")
        Page<ChangeRequest> pageMock = createStrictMock(Page.class);
        replay(pageMock);
        expect(dns.listChangeRequests(ZoneTest.ZONE_NAME)).andReturn(pageMock).times(2);
        // again for options
        expect(dns.listChangeRequests(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST_LIST_OPTIONS)).andReturn(pageMock).times(2);
        replay(dns);
        Page<ChangeRequest> result = zoneNoId.listChangeRequests();
        Assert.assertSame(pageMock, result);
        result = zone.listChangeRequests();
        Assert.assertSame(pageMock, result);
        verify(pageMock);
        zoneNoId.listChangeRequests(ZoneTest.CHANGE_REQUEST_LIST_OPTIONS);// check options

        zone.listChangeRequests(ZoneTest.CHANGE_REQUEST_LIST_OPTIONS);// check options

    }

    @Test
    public void listChangeRequestsAndZoneNotFound() {
        expect(dns.listChangeRequests(ZoneTest.ZONE_NAME)).andThrow(ZoneTest.EXCEPTION).times(2);
        // again for options
        expect(dns.listChangeRequests(ZoneTest.ZONE_NAME, ZoneTest.CHANGE_REQUEST_LIST_OPTIONS)).andThrow(ZoneTest.EXCEPTION).times(2);
        replay(dns);
        try {
            zoneNoId.listChangeRequests();
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zone.listChangeRequests();
            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zoneNoId.listChangeRequests(ZoneTest.CHANGE_REQUEST_LIST_OPTIONS);// check options

            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
        try {
            zone.listChangeRequests(ZoneTest.CHANGE_REQUEST_LIST_OPTIONS);// check options

            Assert.fail("Parent container not found, should throw an exception.");
        } catch (DnsException e) {
            // expected
        }
    }

    @Test
    public void testFromPb() {
        expect(dns.getOptions()).andReturn(ZoneTest.OPTIONS);
        replay(dns);
        Assert.assertEquals(Zone.fromPb(dns, zone.toPb()), zone);
    }

    @Test
    public void testEqualsAndToBuilder() {
        expect(dns.getOptions()).andReturn(ZoneTest.OPTIONS).times(2);
        replay(dns);
        Assert.assertEquals(zone, zone.toBuilder().build());
        Assert.assertEquals(zone.hashCode(), zone.toBuilder().build().hashCode());
    }

    @Test
    public void testBuilder() {
        // one for each build() call because it invokes a constructor
        expect(dns.getOptions()).andReturn(ZoneTest.OPTIONS).times(8);
        replay(dns);
        Assert.assertNotEquals(zone, zone.toBuilder().setGeneratedId(new BigInteger(zone.getGeneratedId()).add(BigInteger.ONE).toString()).build());
        Assert.assertNotEquals(zone, zone.toBuilder().setDnsName(((zone.getName()) + "aaaa")).build());
        Assert.assertNotEquals(zone, zone.toBuilder().setNameServerSet(((zone.getNameServerSet()) + "aaaa")).build());
        Assert.assertNotEquals(zone, zone.toBuilder().setNameServers(ImmutableList.of("nameserverpppp")).build());
        Assert.assertNotEquals(zone, zone.toBuilder().setDnsName(((zone.getDnsName()) + "aaaa")).build());
        Assert.assertNotEquals(zone, zone.toBuilder().setCreationTimeMillis(((zone.getCreationTimeMillis()) + 1)).build());
        Zone.Builder builder = zone.toBuilder();
        builder.setGeneratedId(ZoneTest.ZONE_ID).setDnsName("example.com").setCreationTimeMillis(123478946464L).build();
        Assert.assertEquals(zone, builder.build());
    }
}

