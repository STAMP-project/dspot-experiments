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
package com.google.cloud.dns.it;


import ChangeRequest.Status.DONE;
import ChangeRequest.Status.PENDING;
import ChangeRequestField.ADDITIONS;
import ChangeRequestField.DELETIONS;
import ChangeRequestField.ID;
import ChangeRequestField.START_TIME;
import ChangeRequestField.STATUS;
import Dns.ChangeRequestListOption;
import Dns.ChangeRequestOption;
import Dns.ProjectOption;
import Dns.RecordSetListOption;
import Dns.SortingOrder.ASCENDING;
import Dns.SortingOrder.DESCENDING;
import Dns.ZoneListOption;
import Dns.ZoneOption;
import ProjectField.PROJECT_ID;
import ProjectField.PROJECT_NUMBER;
import ProjectField.QUOTA;
import RecordSet.Type;
import RecordSet.Type.A;
import RecordSet.Type.AAAA;
import RecordSet.Type.NS;
import RecordSet.Type.SOA;
import RecordSetField.DNS_RECORDS;
import RecordSetField.TTL;
import RecordSetField.TYPE;
import ZoneField.CREATION_TIME;
import ZoneField.DESCRIPTION;
import ZoneField.DNS_NAME;
import ZoneField.NAME;
import ZoneField.NAME_SERVERS;
import ZoneField.NAME_SERVER_SET;
import ZoneField.ZONE_ID;
import com.google.api.gax.paging.Page;
import com.google.cloud.dns.ChangeRequest;
import com.google.cloud.dns.ChangeRequestInfo;
import com.google.cloud.dns.Dns;
import com.google.cloud.dns.DnsBatch;
import com.google.cloud.dns.DnsBatchResult;
import com.google.cloud.dns.DnsException;
import com.google.cloud.dns.DnsOptions;
import com.google.cloud.dns.ProjectInfo;
import com.google.cloud.dns.RecordSet;
import com.google.cloud.dns.Zone;
import com.google.cloud.dns.ZoneInfo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ITDnsTest {
    private static final String PREFIX = "gcldjvit-";

    private static final Dns DNS = DnsOptions.getDefaultInstance().getService();

    private static final String ZONE_NAME1 = ((ITDnsTest.PREFIX) + (UUID.randomUUID())).substring(0, 32);

    private static final String ZONE_NAME_EMPTY_DESCRIPTION = ((ITDnsTest.PREFIX) + (UUID.randomUUID())).substring(0, 32);

    private static final String ZONE_NAME_TOO_LONG = (ITDnsTest.ZONE_NAME1) + (UUID.randomUUID());

    private static final String ZONE_DESCRIPTION1 = "first zone";

    private static final String ZONE_DNS_NAME1 = (ITDnsTest.ZONE_NAME1) + ".com.";

    private static final String ZONE_DNS_EMPTY_DESCRIPTION = (ITDnsTest.ZONE_NAME_EMPTY_DESCRIPTION) + ".com.";

    private static final String ZONE_DNS_NAME_NO_PERIOD = (ITDnsTest.ZONE_NAME1) + ".com";

    private static final ZoneInfo ZONE1 = ZoneInfo.of(ITDnsTest.ZONE_NAME1, ITDnsTest.ZONE_DNS_EMPTY_DESCRIPTION, ITDnsTest.ZONE_DESCRIPTION1);

    private static final ZoneInfo ZONE_EMPTY_DESCRIPTION = ZoneInfo.of(ITDnsTest.ZONE_NAME_EMPTY_DESCRIPTION, ITDnsTest.ZONE_DNS_NAME1, ITDnsTest.ZONE_DESCRIPTION1);

    private static final ZoneInfo ZONE_NAME_ERROR = ZoneInfo.of(ITDnsTest.ZONE_NAME_TOO_LONG, ITDnsTest.ZONE_DNS_NAME1, ITDnsTest.ZONE_DESCRIPTION1);

    private static final ZoneInfo ZONE_DNS_NO_PERIOD = ZoneInfo.of(ITDnsTest.ZONE_NAME1, ITDnsTest.ZONE_DNS_NAME_NO_PERIOD, ITDnsTest.ZONE_DESCRIPTION1);

    private static final RecordSet A_RECORD_ZONE1 = RecordSet.newBuilder(("www." + (ITDnsTest.ZONE1.getDnsName())), A).setRecords(ImmutableList.of("123.123.55.1")).setTtl(25, TimeUnit.SECONDS).build();

    private static final RecordSet AAAA_RECORD_ZONE1 = RecordSet.newBuilder(("www." + (ITDnsTest.ZONE1.getDnsName())), AAAA).setRecords(ImmutableList.of("ed:ed:12:aa:36:3:3:105")).setTtl(25, TimeUnit.SECONDS).build();

    private static final ChangeRequestInfo CHANGE_ADD_ZONE1 = ChangeRequest.newBuilder().add(ITDnsTest.A_RECORD_ZONE1).add(ITDnsTest.AAAA_RECORD_ZONE1).build();

    private static final ChangeRequestInfo CHANGE_DELETE_ZONE1 = ChangeRequest.newBuilder().delete(ITDnsTest.A_RECORD_ZONE1).delete(ITDnsTest.AAAA_RECORD_ZONE1).build();

    private static final List<String> ZONE_NAMES = ImmutableList.of(ITDnsTest.ZONE_NAME1, ITDnsTest.ZONE_NAME_EMPTY_DESCRIPTION);

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testCreateValidZone() {
        try {
            Zone created = ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertEquals(ITDnsTest.ZONE1.getDnsName(), created.getDnsName());
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());
            Assert.assertNotNull(created.getCreationTimeMillis());
            Assert.assertNotNull(created.getNameServers());
            Assert.assertNull(created.getNameServerSet());
            Assert.assertNotNull(created.getGeneratedId());
            Zone retrieved = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName());
            Assert.assertEquals(created, retrieved);
            created = ITDnsTest.DNS.create(ITDnsTest.ZONE_EMPTY_DESCRIPTION);
            Assert.assertEquals(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getDescription(), created.getDescription());
            Assert.assertEquals(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getDnsName(), created.getDnsName());
            Assert.assertEquals(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getName(), created.getName());
            Assert.assertNotNull(created.getCreationTimeMillis());
            Assert.assertNotNull(created.getNameServers());
            Assert.assertNull(created.getNameServerSet());
            Assert.assertNotNull(created.getGeneratedId());
            retrieved = ITDnsTest.DNS.getZone(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getName());
            Assert.assertEquals(created, retrieved);
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
            ITDnsTest.DNS.delete(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getName());
        }
    }

    @Test
    public void testCreateZoneWithErrors() {
        try {
            try {
                ITDnsTest.DNS.create(ITDnsTest.ZONE_NAME_ERROR);
                Assert.fail("Zone name is too long. The service returns an error.");
            } catch (DnsException ex) {
                // expected
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                ITDnsTest.DNS.create(ITDnsTest.ZONE_DNS_NO_PERIOD);
                Assert.fail("Zone name is missing a period. The service returns an error.");
            } catch (DnsException ex) {
                // expected
                Assert.assertFalse(ex.isRetryable());
            }
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE_NAME_ERROR.getName());
            ITDnsTest.DNS.delete(ITDnsTest.ZONE_DNS_NO_PERIOD.getName());
        }
    }

    @Test
    public void testCreateZoneWithOptions() {
        try {
            Zone created = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(CREATION_TIME));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNotNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDescription());
            Assert.assertNull(created.getDnsName());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            created = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(DESCRIPTION));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertNull(created.getDnsName());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            created = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(DNS_NAME));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertEquals(ITDnsTest.ZONE1.getDnsName(), created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            created = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(NAME));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            created = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(NAME_SERVER_SET));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());// we did not set it

            Assert.assertNull(created.getGeneratedId());
            created.delete();
            created = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(NAME_SERVERS));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertFalse(created.getNameServers().isEmpty());
            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            created = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(ZONE_ID));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertNotNull(created.getNameServers());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNotNull(created.getGeneratedId());
            created.delete();
            // combination of multiple things
            created = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(ZONE_ID, NAME_SERVERS, NAME_SERVER_SET, DESCRIPTION));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertFalse(created.getNameServers().isEmpty());
            Assert.assertNull(created.getNameServerSet());// we did not set it

            Assert.assertNotNull(created.getGeneratedId());
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
        }
    }

    @Test
    public void testGetZone() {
        try {
            ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(NAME));
            Zone created = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(CREATION_TIME));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNotNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDescription());
            Assert.assertNull(created.getDnsName());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(DESCRIPTION));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertNull(created.getDnsName());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(DNS_NAME));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertEquals(ITDnsTest.ZONE1.getDnsName(), created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(NAME));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(NAME_SERVER_SET));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());// we did not set it

            Assert.assertNull(created.getGeneratedId());
            created = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(NAME_SERVERS));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertFalse(created.getNameServers().isEmpty());
            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(ZONE_ID));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertNotNull(created.getNameServers());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNotNull(created.getGeneratedId());
            // combination of multiple things
            created = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(ZONE_ID, NAME_SERVERS, NAME_SERVER_SET, DESCRIPTION));
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertFalse(created.getNameServers().isEmpty());
            Assert.assertNull(created.getNameServerSet());// we did not set it

            Assert.assertNotNull(created.getGeneratedId());
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
        }
    }

    @Test
    public void testListZones() {
        try {
            List<Zone> zones = ITDnsTest.filter(ITDnsTest.DNS.listZones().iterateAll().iterator());
            Assert.assertEquals(0, zones.size());
            // some zones exists
            Zone created = ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            zones = ITDnsTest.filter(ITDnsTest.DNS.listZones().iterateAll().iterator());
            Assert.assertEquals(created, zones.get(0));
            Assert.assertEquals(1, zones.size());
            created = ITDnsTest.DNS.create(ITDnsTest.ZONE_EMPTY_DESCRIPTION);
            zones = ITDnsTest.filter(ITDnsTest.DNS.listZones().iterateAll().iterator());
            Assert.assertEquals(2, zones.size());
            Assert.assertTrue(zones.contains(created));
            // error in options
            try {
                ITDnsTest.DNS.listZones(ZoneListOption.pageSize(0));
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                ITDnsTest.DNS.listZones(ZoneListOption.pageSize((-1)));
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            // ok size
            zones = ITDnsTest.filter(ITDnsTest.DNS.listZones(ZoneListOption.pageSize(1000)).iterateAll().iterator());
            Assert.assertEquals(2, zones.size());// we still have only 2 zones

            // dns name problems
            try {
                ITDnsTest.DNS.listZones(ZoneListOption.dnsName("aaaaa"));
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            // ok name
            zones = ITDnsTest.filter(ITDnsTest.DNS.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName())).iterateAll().iterator());
            Assert.assertEquals(1, zones.size());
            // field options
            Iterator<Zone> zoneIterator = ITDnsTest.DNS.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(ZONE_ID)).iterateAll().iterator();
            Zone zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNotNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = ITDnsTest.DNS.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(CREATION_TIME)).iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNotNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = ITDnsTest.DNS.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(DNS_NAME)).iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNotNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = ITDnsTest.DNS.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(DESCRIPTION)).iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNotNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = ITDnsTest.DNS.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(NAME_SERVERS)).iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertFalse(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = ITDnsTest.DNS.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(NAME_SERVER_SET)).iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());// we cannot set it using google-cloud

            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            // several combined
            zones = ITDnsTest.filter(ITDnsTest.DNS.listZones(ZoneListOption.fields(ZONE_ID, DESCRIPTION), ZoneListOption.pageSize(1)).iterateAll().iterator());
            Assert.assertEquals(2, zones.size());
            for (Zone current : zones) {
                Assert.assertNull(current.getCreationTimeMillis());
                Assert.assertNotNull(current.getName());
                Assert.assertNull(current.getDnsName());
                Assert.assertNotNull(current.getDescription());
                Assert.assertNull(current.getNameServerSet());
                Assert.assertTrue(zone.getNameServers().isEmpty());
                Assert.assertNotNull(current.getGeneratedId());
            }
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
            ITDnsTest.DNS.delete(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getName());
        }
    }

    @Test
    public void testDeleteZone() {
        try {
            Zone created = ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            Assert.assertEquals(created, ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName()));
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
            Assert.assertNull(ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName()));
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
        }
    }

    @Test
    public void testCreateChange() {
        try {
            ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(NAME));
            ChangeRequest created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1);
            Assert.assertEquals(ITDnsTest.CHANGE_ADD_ZONE1.getAdditions(), created.getAdditions());
            Assert.assertNotNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertTrue(ImmutableList.of(PENDING, DONE).contains(created.status()));
            ITDnsTest.assertEqChangesIgnoreStatus(created, ITDnsTest.DNS.getChangeRequest(ITDnsTest.ZONE1.getName(), "1"));
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(created);
            // with options
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(ID));
            Assert.assertTrue(created.getAdditions().isEmpty());
            Assert.assertNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNull(created.status());
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(STATUS));
            Assert.assertTrue(created.getAdditions().isEmpty());
            Assert.assertNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNotNull(created.status());
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(START_TIME));
            Assert.assertTrue(created.getAdditions().isEmpty());
            Assert.assertNotNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNull(created.status());
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(ADDITIONS));
            Assert.assertEquals(ITDnsTest.CHANGE_ADD_ZONE1.getAdditions(), created.getAdditions());
            Assert.assertNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNull(created.status());
            // finishes with delete otherwise we cannot delete the zone
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1, ChangeRequestOption.fields(DELETIONS));
            ITDnsTest.waitForChangeToComplete(created);
            Assert.assertEquals(ITDnsTest.CHANGE_DELETE_ZONE1.getDeletions(), created.getDeletions());
            Assert.assertNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getAdditions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNull(created.status());
            ITDnsTest.waitForChangeToComplete(created);
        } finally {
            ITDnsTest.clear();
        }
    }

    @Test
    public void testInvalidChangeRequest() {
        Zone zone = ITDnsTest.DNS.create(ITDnsTest.ZONE1);
        RecordSet validA = RecordSet.newBuilder(("subdomain." + (zone.getDnsName())), A).setRecords(ImmutableList.of("0.255.1.5")).build();
        boolean recordAdded = false;
        try {
            ChangeRequestInfo validChange = ChangeRequest.newBuilder().add(validA).build();
            zone.applyChangeRequest(validChange);
            recordAdded = true;
            try {
                zone.applyChangeRequest(validChange);
                Assert.fail("Created a record set which already exists.");
            } catch (DnsException ex) {
                // expected
                Assert.assertFalse(ex.isRetryable());
                Assert.assertEquals(409, ex.getCode());
            }
            // delete with field mismatch
            RecordSet mismatch = validA.toBuilder().setTtl(20, TimeUnit.SECONDS).build();
            ChangeRequestInfo deletion = ChangeRequest.newBuilder().delete(mismatch).build();
            try {
                zone.applyChangeRequest(deletion);
                Assert.fail("Deleted a record set without a complete match.");
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(412, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            // delete and add SOA
            Iterator<RecordSet> recordSetIterator = zone.listRecordSets().iterateAll().iterator();
            LinkedList<RecordSet> deletions = new LinkedList<>();
            LinkedList<RecordSet> additions = new LinkedList<>();
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                if ((recordSet.getType()) == (Type.SOA)) {
                    deletions.add(recordSet);
                    // the subdomain is necessary to get 400 instead of 412
                    RecordSet copy = recordSet.toBuilder().setName(("x." + (recordSet.getName()))).build();
                    additions.add(copy);
                    break;
                }
            } 
            deletion = deletion.toBuilder().setDeletions(deletions).build();
            ChangeRequestInfo addition = ChangeRequest.newBuilder().setAdditions(additions).build();
            try {
                zone.applyChangeRequest(deletion);
                Assert.fail("Deleted SOA.");
            } catch (DnsException ex) {
                // expected
                Assert.assertFalse(ex.isRetryable());
                Assert.assertEquals(400, ex.getCode());
            }
            try {
                zone.applyChangeRequest(addition);
                Assert.fail("Added second SOA.");
            } catch (DnsException ex) {
                // expected
                Assert.assertFalse(ex.isRetryable());
                Assert.assertEquals(400, ex.getCode());
            }
        } finally {
            if (recordAdded) {
                ChangeRequestInfo deletion = ChangeRequest.newBuilder().delete(validA).build();
                ChangeRequest request = zone.applyChangeRequest(deletion);
                ITDnsTest.waitForChangeToComplete(zone.getName(), request.getGeneratedId());
            }
            zone.delete();
        }
    }

    @Test
    public void testListChanges() {
        try {
            // no such zone exists
            try {
                ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName());
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(404, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            // zone exists but has no changes
            ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            ImmutableList<ChangeRequest> changes = ImmutableList.copyOf(ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName()).iterateAll());
            Assert.assertEquals(1, changes.size());// default change creating SOA and NS

            // zone has changes
            ChangeRequest change = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1);
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
            change = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
            change = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1);
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
            change = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
            changes = ImmutableList.copyOf(ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName()).iterateAll());
            Assert.assertEquals(5, changes.size());
            // error in options
            try {
                ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.pageSize(0));
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.pageSize((-1)));
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            // sorting order
            ImmutableList<ChangeRequest> ascending = ImmutableList.copyOf(ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING)).iterateAll());
            ImmutableList<ChangeRequest> descending = ImmutableList.copyOf(ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(DESCENDING)).iterateAll());
            int size = 5;
            Assert.assertEquals(size, descending.size());
            Assert.assertEquals(size, ascending.size());
            for (int i = 0; i < size; i++) {
                Assert.assertEquals(descending.get(i), ascending.get(((size - i) - 1)));
            }
            // field options
            changes = ImmutableList.copyOf(ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(ADDITIONS)).iterateAll());
            change = changes.get(1);
            Assert.assertEquals(ITDnsTest.CHANGE_ADD_ZONE1.getAdditions(), change.getAdditions());
            Assert.assertTrue(change.getDeletions().isEmpty());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNull(change.getStartTimeMillis());
            Assert.assertNull(change.status());
            changes = ImmutableList.copyOf(ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(DELETIONS)).iterateAll());
            change = changes.get(2);
            Assert.assertTrue(change.getAdditions().isEmpty());
            Assert.assertNotNull(change.getDeletions());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNull(change.getStartTimeMillis());
            Assert.assertNull(change.status());
            changes = ImmutableList.copyOf(ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(ID)).iterateAll());
            change = changes.get(1);
            Assert.assertTrue(change.getAdditions().isEmpty());
            Assert.assertTrue(change.getDeletions().isEmpty());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNull(change.getStartTimeMillis());
            Assert.assertNull(change.status());
            changes = ImmutableList.copyOf(ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(START_TIME)).iterateAll());
            change = changes.get(1);
            Assert.assertTrue(change.getAdditions().isEmpty());
            Assert.assertTrue(change.getDeletions().isEmpty());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNotNull(change.getStartTimeMillis());
            Assert.assertNull(change.status());
            changes = ImmutableList.copyOf(ITDnsTest.DNS.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(STATUS)).iterateAll());
            change = changes.get(1);
            Assert.assertTrue(change.getAdditions().isEmpty());
            Assert.assertTrue(change.getDeletions().isEmpty());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNull(change.getStartTimeMillis());
            Assert.assertEquals(DONE, change.status());
        } finally {
            ITDnsTest.clear();
        }
    }

    @Test
    public void testGetChange() {
        try {
            Zone zone = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(NAME));
            ChangeRequest created = zone.applyChangeRequest(ITDnsTest.CHANGE_ADD_ZONE1);
            ChangeRequest retrieved = ITDnsTest.DNS.getChangeRequest(zone.getName(), created.getGeneratedId());
            ITDnsTest.assertEqChangesIgnoreStatus(created, retrieved);
            ITDnsTest.waitForChangeToComplete(zone.getName(), created.getGeneratedId());
            zone.applyChangeRequest(ITDnsTest.CHANGE_DELETE_ZONE1);
            // with options
            created = zone.applyChangeRequest(ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(ID));
            retrieved = ITDnsTest.DNS.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(ID));
            ITDnsTest.assertEqChangesIgnoreStatus(created, retrieved);
            ITDnsTest.waitForChangeToComplete(zone.getName(), created.getGeneratedId());
            zone.applyChangeRequest(ITDnsTest.CHANGE_DELETE_ZONE1);
            created = zone.applyChangeRequest(ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(STATUS));
            retrieved = ITDnsTest.DNS.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(STATUS));
            ITDnsTest.assertEqChangesIgnoreStatus(created, retrieved);
            ITDnsTest.waitForChangeToComplete(zone.getName(), created.getGeneratedId());
            zone.applyChangeRequest(ITDnsTest.CHANGE_DELETE_ZONE1);
            created = zone.applyChangeRequest(ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(START_TIME));
            retrieved = ITDnsTest.DNS.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(START_TIME));
            ITDnsTest.assertEqChangesIgnoreStatus(created, retrieved);
            ITDnsTest.waitForChangeToComplete(zone.getName(), created.getGeneratedId());
            zone.applyChangeRequest(ITDnsTest.CHANGE_DELETE_ZONE1);
            created = zone.applyChangeRequest(ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(ADDITIONS));
            retrieved = ITDnsTest.DNS.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(ADDITIONS));
            ITDnsTest.assertEqChangesIgnoreStatus(created, retrieved);
            ITDnsTest.waitForChangeToComplete(zone.getName(), created.getGeneratedId());
            // finishes with delete otherwise we cannot delete the zone
            created = zone.applyChangeRequest(ITDnsTest.CHANGE_DELETE_ZONE1, ChangeRequestOption.fields(DELETIONS));
            retrieved = ITDnsTest.DNS.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(DELETIONS));
            ITDnsTest.assertEqChangesIgnoreStatus(created, retrieved);
            ITDnsTest.waitForChangeToComplete(zone.getName(), created.getGeneratedId());
        } finally {
            ITDnsTest.clear();
        }
    }

    @Test
    public void testGetProject() {
        // fetches all fields
        ProjectInfo project = ITDnsTest.DNS.getProject();
        Assert.assertNotNull(project.getQuota());
        // options
        project = ITDnsTest.DNS.getProject(ProjectOption.fields(QUOTA));
        Assert.assertNotNull(project.getQuota());
        project = ITDnsTest.DNS.getProject(ProjectOption.fields(PROJECT_ID));
        Assert.assertNull(project.getQuota());
        project = ITDnsTest.DNS.getProject(ProjectOption.fields(PROJECT_NUMBER));
        Assert.assertNull(project.getQuota());
        project = ITDnsTest.DNS.getProject(ProjectOption.fields(PROJECT_NUMBER, QUOTA, PROJECT_ID));
        Assert.assertNotNull(project.getQuota());
    }

    @Test
    public void testListDnsRecords() {
        try {
            Zone zone = ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            ImmutableList<RecordSet> recordSets = ImmutableList.copyOf(ITDnsTest.DNS.listRecordSets(zone.getName()).iterateAll());
            Assert.assertEquals(2, recordSets.size());
            ImmutableList<RecordSet.Type> defaultRecords = ImmutableList.of(NS, SOA);
            for (RecordSet recordSet : recordSets) {
                Assert.assertTrue(defaultRecords.contains(recordSet.getType()));
            }
            // field options
            Iterator<RecordSet> recordSetIterator = ITDnsTest.DNS.listRecordSets(zone.getName(), RecordSetListOption.fields(TTL)).iterateAll().iterator();
            int counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(recordSets.get(counter).getTtl(), recordSet.getTtl());
                Assert.assertEquals(recordSets.get(counter).getName(), recordSet.getName());
                Assert.assertEquals(recordSets.get(counter).getType(), recordSet.getType());
                Assert.assertTrue(recordSet.getRecords().isEmpty());
                counter++;
            } 
            Assert.assertEquals(2, counter);
            recordSetIterator = ITDnsTest.DNS.listRecordSets(zone.getName(), RecordSetListOption.fields(RecordSetField.NAME)).iterateAll().iterator();
            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(recordSets.get(counter).getName(), recordSet.getName());
                Assert.assertEquals(recordSets.get(counter).getType(), recordSet.getType());
                Assert.assertTrue(recordSet.getRecords().isEmpty());
                Assert.assertNull(recordSet.getTtl());
                counter++;
            } 
            Assert.assertEquals(2, counter);
            recordSetIterator = ITDnsTest.DNS.listRecordSets(zone.getName(), RecordSetListOption.fields(DNS_RECORDS)).iterateAll().iterator();
            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(recordSets.get(counter).getRecords(), recordSet.getRecords());
                Assert.assertEquals(recordSets.get(counter).getName(), recordSet.getName());
                Assert.assertEquals(recordSets.get(counter).getType(), recordSet.getType());
                Assert.assertNull(recordSet.getTtl());
                counter++;
            } 
            Assert.assertEquals(2, counter);
            recordSetIterator = ITDnsTest.DNS.listRecordSets(zone.getName(), RecordSetListOption.fields(TYPE), RecordSetListOption.pageSize(1)).iterateAll().iterator();// also test paging

            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(recordSets.get(counter).getType(), recordSet.getType());
                Assert.assertEquals(recordSets.get(counter).getName(), recordSet.getName());
                Assert.assertTrue(recordSet.getRecords().isEmpty());
                Assert.assertNull(recordSet.getTtl());
                counter++;
            } 
            Assert.assertEquals(2, counter);
            // test page size
            Page<RecordSet> recordSetPage = ITDnsTest.DNS.listRecordSets(zone.getName(), RecordSetListOption.fields(TYPE), RecordSetListOption.pageSize(1));
            Assert.assertEquals(1, ImmutableList.copyOf(recordSetPage.getValues().iterator()).size());
            // test name filter
            ChangeRequest change = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1);
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
            recordSetIterator = ITDnsTest.DNS.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.dnsName(ITDnsTest.A_RECORD_ZONE1.getName())).iterateAll().iterator();
            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertTrue(ImmutableList.of(ITDnsTest.A_RECORD_ZONE1.getType(), ITDnsTest.AAAA_RECORD_ZONE1.getType()).contains(recordSet.getType()));
                counter++;
            } 
            Assert.assertEquals(2, counter);
            // test type filter
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
            recordSetIterator = ITDnsTest.DNS.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.dnsName(ITDnsTest.A_RECORD_ZONE1.getName()), RecordSetListOption.type(ITDnsTest.A_RECORD_ZONE1.getType())).iterateAll().iterator();
            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(ITDnsTest.A_RECORD_ZONE1, recordSet);
                counter++;
            } 
            Assert.assertEquals(1, counter);
            change = zone.applyChangeRequest(ITDnsTest.CHANGE_DELETE_ZONE1);
            // check wrong arguments
            try {
                // name is not set
                ITDnsTest.DNS.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.type(ITDnsTest.A_RECORD_ZONE1.getType()));
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                ITDnsTest.DNS.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.pageSize(0));
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                ITDnsTest.DNS.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.pageSize((-1)));
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
        } finally {
            ITDnsTest.clear();
        }
    }

    @Test
    public void testListZonesBatch() {
        try {
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Page<Zone>> result = batch.listZones();
            batch.submit();
            List<Zone> zones = ITDnsTest.filter(result.get().iterateAll().iterator());
            Assert.assertEquals(0, zones.size());
            // some zones exists
            Zone firstZone = ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            batch = ITDnsTest.DNS.batch();
            result = batch.listZones();
            batch.submit();
            zones = ITDnsTest.filter(result.get().iterateAll().iterator());
            Assert.assertEquals(1, zones.size());
            Assert.assertEquals(firstZone, zones.get(0));
            Zone created = ITDnsTest.DNS.create(ITDnsTest.ZONE_EMPTY_DESCRIPTION);
            batch = ITDnsTest.DNS.batch();
            result = batch.listZones();
            DnsBatchResult<Page<Zone>> zeroSizeError = batch.listZones(ZoneListOption.pageSize(0));
            DnsBatchResult<Page<Zone>> negativeSizeError = batch.listZones(ZoneListOption.pageSize((-1)));
            DnsBatchResult<Page<Zone>> okSize = batch.listZones(ZoneListOption.pageSize(1));
            DnsBatchResult<Page<Zone>> nameError = batch.listZones(ZoneListOption.dnsName("aaaaa"));
            DnsBatchResult<Page<Zone>> okName = batch.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()));
            DnsBatchResult<Page<Zone>> idResult = batch.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(ZONE_ID));
            DnsBatchResult<Page<Zone>> timeResult = batch.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(CREATION_TIME));
            DnsBatchResult<Page<Zone>> dnsNameResult = batch.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(DNS_NAME));
            DnsBatchResult<Page<Zone>> descriptionResult = batch.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(DESCRIPTION));
            DnsBatchResult<Page<Zone>> nameServersResult = batch.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(NAME_SERVERS));
            DnsBatchResult<Page<Zone>> nameServerSetResult = batch.listZones(ZoneListOption.dnsName(ITDnsTest.ZONE1.getDnsName()), ZoneListOption.fields(NAME_SERVER_SET));
            DnsBatchResult<Page<Zone>> combinationResult = batch.listZones(ZoneListOption.fields(ZONE_ID, DESCRIPTION), ZoneListOption.pageSize(1));
            batch.submit();
            zones = ITDnsTest.filter(result.get().iterateAll().iterator());
            Assert.assertEquals(2, zones.size());
            Assert.assertTrue(zones.contains(firstZone));
            Assert.assertTrue(zones.contains(created));
            // error in options
            try {
                zeroSizeError.get();
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                negativeSizeError.get();
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            // ok size
            Assert.assertEquals(1, Iterables.size(okSize.get().getValues()));
            // dns name problems
            try {
                nameError.get();
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            // ok name
            zones = ITDnsTest.filter(okName.get().iterateAll().iterator());
            Assert.assertEquals(1, zones.size());
            // field options
            Iterator<Zone> zoneIterator = idResult.get().iterateAll().iterator();
            Zone zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNotNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = timeResult.get().iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNotNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = dnsNameResult.get().iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNotNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = descriptionResult.get().iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNotNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = nameServersResult.get().iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());
            Assert.assertFalse(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            zoneIterator = nameServerSetResult.get().iterateAll().iterator();
            zone = zoneIterator.next();
            Assert.assertNull(zone.getCreationTimeMillis());
            Assert.assertNotNull(zone.getName());
            Assert.assertNull(zone.getDnsName());
            Assert.assertNull(zone.getDescription());
            Assert.assertNull(zone.getNameServerSet());// we cannot set it using google-cloud

            Assert.assertTrue(zone.getNameServers().isEmpty());
            Assert.assertNull(zone.getGeneratedId());
            Assert.assertFalse(zoneIterator.hasNext());
            // several combined
            zones = ITDnsTest.filter(combinationResult.get().iterateAll().iterator());
            Assert.assertEquals(2, zones.size());
            for (Zone current : zones) {
                Assert.assertNull(current.getCreationTimeMillis());
                Assert.assertNotNull(current.getName());
                Assert.assertNull(current.getDnsName());
                Assert.assertNotNull(current.getDescription());
                Assert.assertNull(current.getNameServerSet());
                Assert.assertTrue(zone.getNameServers().isEmpty());
                Assert.assertNotNull(current.getGeneratedId());
            }
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
            ITDnsTest.DNS.delete(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getName());
        }
    }

    @Test
    public void testCreateValidZoneBatch() {
        try {
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Zone> completeZoneResult = batch.createZone(ITDnsTest.ZONE1);
            DnsBatchResult<Zone> partialZoneResult = batch.createZone(ITDnsTest.ZONE_EMPTY_DESCRIPTION);
            batch.submit();
            Zone created = completeZoneResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertEquals(ITDnsTest.ZONE1.getDnsName(), created.getDnsName());
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());
            Assert.assertNotNull(created.getCreationTimeMillis());
            Assert.assertNotNull(created.getNameServers());
            Assert.assertNull(created.getNameServerSet());
            Assert.assertNotNull(created.getGeneratedId());
            Zone retrieved = ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName());
            Assert.assertEquals(created, retrieved);
            created = partialZoneResult.get();
            Assert.assertEquals(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getDescription(), created.getDescription());
            Assert.assertEquals(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getDnsName(), created.getDnsName());
            Assert.assertEquals(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getName(), created.getName());
            Assert.assertNotNull(created.getCreationTimeMillis());
            Assert.assertNotNull(created.getNameServers());
            Assert.assertNull(created.getNameServerSet());
            Assert.assertNotNull(created.getGeneratedId());
            retrieved = ITDnsTest.DNS.getZone(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getName());
            Assert.assertEquals(created, retrieved);
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
            ITDnsTest.DNS.delete(ITDnsTest.ZONE_EMPTY_DESCRIPTION.getName());
        }
    }

    @Test
    public void testCreateZoneWithErrorsBatch() {
        try {
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Zone> nameErrorResult = batch.createZone(ITDnsTest.ZONE_NAME_ERROR);
            DnsBatchResult<Zone> noPeriodResult = batch.createZone(ITDnsTest.ZONE_DNS_NO_PERIOD);
            batch.submit();
            try {
                nameErrorResult.get();
                Assert.fail("Zone name is too long. The service returns an error.");
            } catch (DnsException ex) {
                // expected
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                noPeriodResult.get();
                Assert.fail("Zone name is missing a period. The service returns an error.");
            } catch (DnsException ex) {
                // expected
                Assert.assertFalse(ex.isRetryable());
            }
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE_NAME_ERROR.getName());
            ITDnsTest.DNS.delete(ITDnsTest.ZONE_DNS_NO_PERIOD.getName());
        }
    }

    @Test
    public void testCreateZoneWithOptionsBatch() {
        try {
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Zone> batchResult = batch.createZone(ITDnsTest.ZONE1, ZoneOption.fields(CREATION_TIME));
            batch.submit();
            Zone created = batchResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNotNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDescription());
            Assert.assertNull(created.getDnsName());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            batch = ITDnsTest.DNS.batch();
            batchResult = batch.createZone(ITDnsTest.ZONE1, ZoneOption.fields(DESCRIPTION));
            batch.submit();
            created = batchResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertNull(created.getDnsName());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            batch = ITDnsTest.DNS.batch();
            batchResult = batch.createZone(ITDnsTest.ZONE1, ZoneOption.fields(DNS_NAME));
            batch.submit();
            created = batchResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertEquals(ITDnsTest.ZONE1.getDnsName(), created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            batch = ITDnsTest.DNS.batch();
            batchResult = batch.createZone(ITDnsTest.ZONE1, ZoneOption.fields(NAME));
            batch.submit();
            created = batchResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            batch = ITDnsTest.DNS.batch();
            batchResult = batch.createZone(ITDnsTest.ZONE1, ZoneOption.fields(NAME_SERVER_SET));
            batch.submit();
            created = batchResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());// we did not set it

            Assert.assertNull(created.getGeneratedId());
            created.delete();
            batch = ITDnsTest.DNS.batch();
            batchResult = batch.createZone(ITDnsTest.ZONE1, ZoneOption.fields(NAME_SERVERS));
            batch.submit();
            created = batchResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertFalse(created.getNameServers().isEmpty());
            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created.delete();
            batch = ITDnsTest.DNS.batch();
            batchResult = batch.createZone(ITDnsTest.ZONE1, ZoneOption.fields(ZONE_ID));
            batch.submit();
            created = batchResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertNotNull(created.getNameServers());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNotNull(created.getGeneratedId());
            created.delete();
            batch = ITDnsTest.DNS.batch();
            batchResult = batch.createZone(ITDnsTest.ZONE1, ZoneOption.fields(ZONE_ID, NAME_SERVERS, NAME_SERVER_SET, DESCRIPTION));
            batch.submit();
            // combination of multiple things
            created = batchResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertFalse(created.getNameServers().isEmpty());
            Assert.assertNull(created.getNameServerSet());// we did not set it

            Assert.assertNotNull(created.getGeneratedId());
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
        }
    }

    @Test
    public void testGetZoneBatch() {
        try {
            ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(NAME));
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Zone> timeResult = batch.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(CREATION_TIME));
            DnsBatchResult<Zone> descriptionResult = batch.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(DESCRIPTION));
            DnsBatchResult<Zone> dnsNameResult = batch.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(DNS_NAME));
            DnsBatchResult<Zone> nameResult = batch.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(NAME));
            DnsBatchResult<Zone> nameServerSetResult = batch.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(NAME_SERVER_SET));
            DnsBatchResult<Zone> nameServersResult = batch.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(NAME_SERVERS));
            DnsBatchResult<Zone> idResult = batch.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(ZONE_ID));
            DnsBatchResult<Zone> combinationResult = batch.getZone(ITDnsTest.ZONE1.getName(), ZoneOption.fields(ZONE_ID, NAME_SERVERS, NAME_SERVER_SET, DESCRIPTION));
            batch.submit();
            Zone created = timeResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNotNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDescription());
            Assert.assertNull(created.getDnsName());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = descriptionResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertNull(created.getDnsName());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = dnsNameResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertEquals(ITDnsTest.ZONE1.getDnsName(), created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = nameResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = nameServerSetResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNull(created.getNameServerSet());// we did not set it

            Assert.assertNull(created.getGeneratedId());
            created = nameServersResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertFalse(created.getNameServers().isEmpty());
            Assert.assertNull(created.getNameServerSet());
            Assert.assertNull(created.getGeneratedId());
            created = idResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertNull(created.getDescription());
            Assert.assertNotNull(created.getNameServers());
            Assert.assertTrue(created.getNameServers().isEmpty());// never returns null

            Assert.assertNotNull(created.getGeneratedId());
            // combination of multiple things
            created = combinationResult.get();
            Assert.assertEquals(ITDnsTest.ZONE1.getName(), created.getName());// always returned

            Assert.assertNull(created.getCreationTimeMillis());
            Assert.assertNull(created.getDnsName());
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), created.getDescription());
            Assert.assertFalse(created.getNameServers().isEmpty());
            Assert.assertNull(created.getNameServerSet());// we did not set it

            Assert.assertNotNull(created.getGeneratedId());
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
        }
    }

    @Test
    public void testDeleteZoneBatch() {
        try {
            Zone created = ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            Assert.assertEquals(created, ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName()));
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Boolean> result = batch.deleteZone(ITDnsTest.ZONE1.getName());
            batch.submit();
            Assert.assertNull(ITDnsTest.DNS.getZone(ITDnsTest.ZONE1.getName()));
            Assert.assertTrue(result.get());
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
        }
    }

    @Test
    public void testGetProjectBatch() {
        // fetches all fields
        DnsBatch batch = ITDnsTest.DNS.batch();
        DnsBatchResult<ProjectInfo> result = batch.getProject();
        DnsBatchResult<ProjectInfo> resultQuota = batch.getProject(ProjectOption.fields(QUOTA));
        DnsBatchResult<ProjectInfo> resultId = batch.getProject(ProjectOption.fields(PROJECT_ID));
        DnsBatchResult<ProjectInfo> resultNumber = batch.getProject(ProjectOption.fields(PROJECT_NUMBER));
        DnsBatchResult<ProjectInfo> resultCombination = batch.getProject(ProjectOption.fields(PROJECT_NUMBER, QUOTA, PROJECT_ID));
        batch.submit();
        Assert.assertNotNull(result.get().getQuota());
        Assert.assertNotNull(resultQuota.get().getQuota());
        Assert.assertNull(resultId.get().getQuota());
        Assert.assertNull(resultNumber.get().getQuota());
        Assert.assertNotNull(resultCombination.get().getQuota());
    }

    @Test
    public void testCreateChangeBatch() {
        try {
            ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(NAME));
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<ChangeRequest> result = batch.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1);
            batch.submit();
            ChangeRequest created = result.get();
            Assert.assertEquals(ITDnsTest.CHANGE_ADD_ZONE1.getAdditions(), created.getAdditions());
            Assert.assertNotNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertTrue(ImmutableList.of(PENDING, DONE).contains(created.status()));
            ITDnsTest.assertEqChangesIgnoreStatus(created, ITDnsTest.DNS.getChangeRequest(ITDnsTest.ZONE1.getName(), "1"));
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(created);
            // with options
            batch = ITDnsTest.DNS.batch();
            result = batch.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(ID));
            batch.submit();
            created = result.get();
            Assert.assertTrue(created.getAdditions().isEmpty());
            Assert.assertNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNull(created.status());
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(created);
            batch = ITDnsTest.DNS.batch();
            result = batch.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(STATUS));
            batch.submit();
            created = result.get();
            Assert.assertTrue(created.getAdditions().isEmpty());
            Assert.assertNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNotNull(created.status());
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(created);
            batch = ITDnsTest.DNS.batch();
            result = batch.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(START_TIME));
            batch.submit();
            created = result.get();
            Assert.assertTrue(created.getAdditions().isEmpty());
            Assert.assertNotNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNull(created.status());
            ITDnsTest.waitForChangeToComplete(created);
            created = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(created);
            batch = ITDnsTest.DNS.batch();
            result = batch.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1, ChangeRequestOption.fields(ADDITIONS));
            batch.submit();
            created = result.get();
            Assert.assertEquals(ITDnsTest.CHANGE_ADD_ZONE1.getAdditions(), created.getAdditions());
            Assert.assertNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getDeletions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNull(created.status());
            // finishes with delete otherwise we cannot delete the zone
            ITDnsTest.waitForChangeToComplete(created);
            batch = ITDnsTest.DNS.batch();
            result = batch.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1, ChangeRequestOption.fields(DELETIONS));
            batch.submit();
            created = result.get();
            ITDnsTest.waitForChangeToComplete(created);
            Assert.assertEquals(ITDnsTest.CHANGE_DELETE_ZONE1.getDeletions(), created.getDeletions());
            Assert.assertNull(created.getStartTimeMillis());
            Assert.assertTrue(created.getAdditions().isEmpty());
            Assert.assertNotNull(created.getGeneratedId());
            Assert.assertNull(created.status());
            ITDnsTest.waitForChangeToComplete(created);
        } finally {
            ITDnsTest.clear();
        }
    }

    @Test
    public void testGetChangeBatch() {
        try {
            Zone zone = ITDnsTest.DNS.create(ITDnsTest.ZONE1, ZoneOption.fields(NAME));
            ChangeRequest created = zone.applyChangeRequest(ITDnsTest.CHANGE_ADD_ZONE1);
            ITDnsTest.waitForChangeToComplete(zone.getName(), created.getGeneratedId());
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<ChangeRequest> completeResult = batch.getChangeRequest(zone.getName(), created.getGeneratedId());
            DnsBatchResult<ChangeRequest> idResult = batch.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(ID));
            DnsBatchResult<ChangeRequest> statusResult = batch.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(STATUS));
            DnsBatchResult<ChangeRequest> timeResult = batch.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(START_TIME));
            DnsBatchResult<ChangeRequest> additionsResult = batch.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(ADDITIONS));
            batch.submit();
            ITDnsTest.assertEqChangesIgnoreStatus(created, completeResult.get());
            // with options
            ChangeRequest retrieved = idResult.get();
            Assert.assertEquals(created.getGeneratedId(), retrieved.getGeneratedId());
            Assert.assertEquals(0, retrieved.getAdditions().size());
            Assert.assertEquals(0, retrieved.getDeletions().size());
            Assert.assertNull(retrieved.getStartTimeMillis());
            Assert.assertNull(retrieved.status());
            retrieved = statusResult.get();
            Assert.assertEquals(created.getGeneratedId(), retrieved.getGeneratedId());
            Assert.assertEquals(0, retrieved.getAdditions().size());
            Assert.assertEquals(0, retrieved.getDeletions().size());
            Assert.assertNull(retrieved.getStartTimeMillis());
            Assert.assertEquals(ChangeRequestInfo.Status.DONE, retrieved.status());
            retrieved = timeResult.get();
            Assert.assertEquals(created.getGeneratedId(), retrieved.getGeneratedId());
            Assert.assertEquals(0, retrieved.getAdditions().size());
            Assert.assertEquals(0, retrieved.getDeletions().size());
            Assert.assertEquals(created.getStartTimeMillis(), retrieved.getStartTimeMillis());
            Assert.assertNull(retrieved.status());
            retrieved = additionsResult.get();
            Assert.assertEquals(created.getGeneratedId(), retrieved.getGeneratedId());
            Assert.assertEquals(2, retrieved.getAdditions().size());
            Assert.assertTrue(retrieved.getAdditions().contains(ITDnsTest.A_RECORD_ZONE1));
            Assert.assertTrue(retrieved.getAdditions().contains(ITDnsTest.AAAA_RECORD_ZONE1));
            Assert.assertEquals(0, retrieved.getDeletions().size());
            Assert.assertNull(retrieved.getStartTimeMillis());
            Assert.assertNull(retrieved.status());
            // finishes with delete otherwise we cannot delete the zone
            created = zone.applyChangeRequest(ITDnsTest.CHANGE_DELETE_ZONE1, ChangeRequestOption.fields(DELETIONS));
            batch = ITDnsTest.DNS.batch();
            DnsBatchResult<ChangeRequest> deletionsResult = batch.getChangeRequest(zone.getName(), created.getGeneratedId(), ChangeRequestOption.fields(DELETIONS));
            batch.submit();
            retrieved = deletionsResult.get();
            Assert.assertEquals(created.getGeneratedId(), retrieved.getGeneratedId());
            Assert.assertEquals(0, retrieved.getAdditions().size());
            Assert.assertEquals(2, retrieved.getDeletions().size());
            Assert.assertTrue(retrieved.getDeletions().contains(ITDnsTest.AAAA_RECORD_ZONE1));
            Assert.assertTrue(retrieved.getDeletions().contains(ITDnsTest.A_RECORD_ZONE1));
            Assert.assertNull(retrieved.getStartTimeMillis());
            Assert.assertNull(retrieved.status());
            ITDnsTest.waitForChangeToComplete(zone.getName(), created.getGeneratedId());
        } finally {
            ITDnsTest.clear();
        }
    }

    @Test
    public void testListChangesBatch() {
        try {
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Page<ChangeRequest>> result = batch.listChangeRequests(ITDnsTest.ZONE1.getName());
            batch.submit();
            try {
                result.get();
                Assert.fail("Zone does not exist yet");
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(404, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            // zone exists but has no changes
            ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            batch = ITDnsTest.DNS.batch();
            result = batch.listChangeRequests(ITDnsTest.ZONE1.getName());
            batch.submit();
            // default change creating SOA and NS
            Assert.assertEquals(1, Iterables.size(result.get().getValues()));
            // zone has changes
            ChangeRequest change = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1);
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
            change = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_DELETE_ZONE1);
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
            batch = ITDnsTest.DNS.batch();
            result = batch.listChangeRequests(ITDnsTest.ZONE1.getName());
            DnsBatchResult<Page<ChangeRequest>> errorPageSize = batch.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.pageSize(0));
            DnsBatchResult<Page<ChangeRequest>> errorPageNegative = batch.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.pageSize((-1)));
            DnsBatchResult<Page<ChangeRequest>> resultAscending = batch.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING));
            DnsBatchResult<Page<ChangeRequest>> resultDescending = batch.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(DESCENDING));
            DnsBatchResult<Page<ChangeRequest>> resultAdditions = batch.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(ADDITIONS));
            DnsBatchResult<Page<ChangeRequest>> resultDeletions = batch.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(DELETIONS));
            DnsBatchResult<Page<ChangeRequest>> resultId = batch.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(ID));
            DnsBatchResult<Page<ChangeRequest>> resultTime = batch.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(START_TIME));
            DnsBatchResult<Page<ChangeRequest>> resultStatus = batch.listChangeRequests(ITDnsTest.ZONE1.getName(), ChangeRequestListOption.sortOrder(ASCENDING), ChangeRequestListOption.fields(STATUS));
            batch.submit();
            Assert.assertEquals(3, Iterables.size(result.get().getValues()));
            // error in options
            try {
                errorPageSize.get();
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                errorPageNegative.get();
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            // sorting order
            ImmutableList<ChangeRequest> ascending = ImmutableList.copyOf(resultAscending.get().iterateAll());
            ImmutableList<ChangeRequest> descending = ImmutableList.copyOf(resultDescending.get().iterateAll());
            int size = 3;
            Assert.assertEquals(size, descending.size());
            Assert.assertEquals(size, ascending.size());
            for (int i = 0; i < size; i++) {
                Assert.assertEquals(descending.get(i), ascending.get(((size - i) - 1)));
            }
            // field options
            change = Iterables.get(resultAdditions.get().getValues(), 1);
            Assert.assertEquals(ITDnsTest.CHANGE_ADD_ZONE1.getAdditions(), change.getAdditions());
            Assert.assertTrue(change.getDeletions().isEmpty());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNull(change.getStartTimeMillis());
            Assert.assertNull(change.status());
            change = Iterables.get(resultDeletions.get().getValues(), 2);
            Assert.assertTrue(change.getAdditions().isEmpty());
            Assert.assertNotNull(change.getDeletions());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNull(change.getStartTimeMillis());
            Assert.assertNull(change.status());
            change = Iterables.get(resultId.get().getValues(), 1);
            Assert.assertTrue(change.getAdditions().isEmpty());
            Assert.assertTrue(change.getDeletions().isEmpty());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNull(change.getStartTimeMillis());
            Assert.assertNull(change.status());
            change = Iterables.get(resultTime.get().getValues(), 1);
            Assert.assertTrue(change.getAdditions().isEmpty());
            Assert.assertTrue(change.getDeletions().isEmpty());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNotNull(change.getStartTimeMillis());
            Assert.assertNull(change.status());
            change = Iterables.get(resultStatus.get().getValues(), 1);
            Assert.assertTrue(change.getAdditions().isEmpty());
            Assert.assertTrue(change.getDeletions().isEmpty());
            Assert.assertNotNull(change.getGeneratedId());
            Assert.assertNull(change.getStartTimeMillis());
            Assert.assertEquals(DONE, change.status());
        } finally {
            ITDnsTest.clear();
        }
    }

    @Test
    public void testListDnsRecordSetsBatch() {
        try {
            Zone zone = ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Page<RecordSet>> result = batch.listRecordSets(zone.getName());
            batch.submit();
            ImmutableList<RecordSet> recordSets = ImmutableList.copyOf(result.get().iterateAll());
            Assert.assertEquals(2, recordSets.size());
            ImmutableList<RecordSet.Type> defaultRecords = ImmutableList.of(NS, SOA);
            for (RecordSet recordSet : recordSets) {
                Assert.assertTrue(defaultRecords.contains(recordSet.getType()));
            }
            // field options
            batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Page<RecordSet>> ttlResult = batch.listRecordSets(zone.getName(), RecordSetListOption.fields(TTL));
            DnsBatchResult<Page<RecordSet>> nameResult = batch.listRecordSets(zone.getName(), RecordSetListOption.fields(RecordSetField.NAME));
            DnsBatchResult<Page<RecordSet>> recordsResult = batch.listRecordSets(zone.getName(), RecordSetListOption.fields(DNS_RECORDS));
            DnsBatchResult<Page<RecordSet>> pageSizeResult = batch.listRecordSets(zone.getName(), RecordSetListOption.fields(TYPE), RecordSetListOption.pageSize(1));
            batch.submit();
            Iterator<RecordSet> recordSetIterator = ttlResult.get().iterateAll().iterator();
            int counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(recordSets.get(counter).getTtl(), recordSet.getTtl());
                Assert.assertEquals(recordSets.get(counter).getName(), recordSet.getName());
                Assert.assertEquals(recordSets.get(counter).getType(), recordSet.getType());
                Assert.assertTrue(recordSet.getRecords().isEmpty());
                counter++;
            } 
            Assert.assertEquals(2, counter);
            recordSetIterator = nameResult.get().iterateAll().iterator();
            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(recordSets.get(counter).getName(), recordSet.getName());
                Assert.assertEquals(recordSets.get(counter).getType(), recordSet.getType());
                Assert.assertTrue(recordSet.getRecords().isEmpty());
                Assert.assertNull(recordSet.getTtl());
                counter++;
            } 
            Assert.assertEquals(2, counter);
            recordSetIterator = recordsResult.get().iterateAll().iterator();
            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(recordSets.get(counter).getRecords(), recordSet.getRecords());
                Assert.assertEquals(recordSets.get(counter).getName(), recordSet.getName());
                Assert.assertEquals(recordSets.get(counter).getType(), recordSet.getType());
                Assert.assertNull(recordSet.getTtl());
                counter++;
            } 
            Assert.assertEquals(2, counter);
            recordSetIterator = pageSizeResult.get().iterateAll().iterator();// also test paging

            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(recordSets.get(counter).getType(), recordSet.getType());
                Assert.assertEquals(recordSets.get(counter).getName(), recordSet.getName());
                Assert.assertTrue(recordSet.getRecords().isEmpty());
                Assert.assertNull(recordSet.getTtl());
                counter++;
            } 
            Assert.assertEquals(2, counter);
            // test page size
            Page<RecordSet> recordSetPage = pageSizeResult.get();
            Assert.assertEquals(1, ImmutableList.copyOf(recordSetPage.getValues().iterator()).size());
            // test name filter
            ChangeRequest change = ITDnsTest.DNS.applyChangeRequest(ITDnsTest.ZONE1.getName(), ITDnsTest.CHANGE_ADD_ZONE1);
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
            batch = ITDnsTest.DNS.batch();
            result = batch.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.dnsName(ITDnsTest.A_RECORD_ZONE1.getName()));
            batch.submit();
            recordSetIterator = result.get().iterateAll().iterator();
            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertTrue(ImmutableList.of(ITDnsTest.A_RECORD_ZONE1.getType(), ITDnsTest.AAAA_RECORD_ZONE1.getType()).contains(recordSet.getType()));
                counter++;
            } 
            Assert.assertEquals(2, counter);
            // test type filter
            batch = ITDnsTest.DNS.batch();
            result = batch.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.dnsName(ITDnsTest.A_RECORD_ZONE1.getName()), RecordSetListOption.type(ITDnsTest.A_RECORD_ZONE1.getType()));
            batch.submit();
            recordSetIterator = result.get().iterateAll().iterator();
            counter = 0;
            while (recordSetIterator.hasNext()) {
                RecordSet recordSet = recordSetIterator.next();
                Assert.assertEquals(ITDnsTest.A_RECORD_ZONE1, recordSet);
                counter++;
            } 
            Assert.assertEquals(1, counter);
            batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Page<RecordSet>> noNameError = batch.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.type(ITDnsTest.A_RECORD_ZONE1.getType()));
            DnsBatchResult<Page<RecordSet>> zeroSizeError = batch.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.pageSize(0));
            DnsBatchResult<Page<RecordSet>> negativeSizeError = batch.listRecordSets(ITDnsTest.ZONE1.getName(), RecordSetListOption.pageSize((-1)));
            batch.submit();
            // check wrong arguments
            try {
                // name is not set
                noNameError.get();
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                zeroSizeError.get();
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            try {
                negativeSizeError.get();
                Assert.fail();
            } catch (DnsException ex) {
                // expected
                Assert.assertEquals(400, ex.getCode());
                Assert.assertFalse(ex.isRetryable());
            }
            ITDnsTest.waitForChangeToComplete(ITDnsTest.ZONE1.getName(), change.getGeneratedId());
        } finally {
            ITDnsTest.clear();
        }
    }

    @Test
    public void testBatchCombined() {
        // only testing that the combination is possible
        // the results are validated in the other test methods
        try {
            ITDnsTest.DNS.create(ITDnsTest.ZONE1);
            DnsBatch batch = ITDnsTest.DNS.batch();
            DnsBatchResult<Zone> zoneResult = batch.getZone(ITDnsTest.ZONE_NAME1);
            DnsBatchResult<ChangeRequest> changeRequestResult = batch.getChangeRequest(ITDnsTest.ZONE_NAME1, "0");
            DnsBatchResult<Page<RecordSet>> pageResult = batch.listRecordSets(ITDnsTest.ZONE_NAME1);
            DnsBatchResult<ProjectInfo> projectResult = batch.getProject();
            Assert.assertFalse(zoneResult.completed());
            try {
                zoneResult.get();
                Assert.fail("this should be submitted first");
            } catch (IllegalStateException ex) {
                // expected
            }
            batch.submit();
            Assert.assertNotNull(zoneResult.get().getCreationTimeMillis());
            Assert.assertEquals(ITDnsTest.ZONE1.getDnsName(), zoneResult.get().getDnsName());
            Assert.assertEquals(ITDnsTest.ZONE1.getDescription(), zoneResult.get().getDescription());
            Assert.assertFalse(zoneResult.get().getNameServers().isEmpty());
            Assert.assertNull(zoneResult.get().getNameServerSet());// we did not set it

            Assert.assertNotNull(zoneResult.get().getGeneratedId());
            Assert.assertNotNull(projectResult.get().getQuota());
            Assert.assertEquals(2, Iterables.size(pageResult.get().getValues()));
            Assert.assertNotNull(changeRequestResult.get());
        } finally {
            ITDnsTest.DNS.delete(ITDnsTest.ZONE1.getName());
        }
    }
}

