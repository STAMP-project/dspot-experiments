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
import Dns.ChangeRequestField.ID;
import Dns.ChangeRequestField.STATUS;
import Dns.ChangeRequestListOption;
import Dns.ChangeRequestOption;
import Dns.ProjectField.PROJECT_ID;
import Dns.ProjectField.QUOTA;
import Dns.ProjectOption;
import Dns.RecordSetField.TTL;
import Dns.RecordSetListOption;
import Dns.SortingOrder.ASCENDING;
import Dns.ZoneField.CREATION_TIME;
import Dns.ZoneField.DESCRIPTION;
import Dns.ZoneField.NAME;
import Dns.ZoneListOption;
import Dns.ZoneOption;
import DnsRpc.ListResult;
import DnsRpc.Option;
import RecordSet.Type.AAAA;
import com.google.api.core.ApiClock;
import com.google.api.gax.paging.Page;
import com.google.api.services.dns.model.Change;
import com.google.api.services.dns.model.ManagedZone;
import com.google.api.services.dns.model.ResourceRecordSet;
import com.google.cloud.dns.spi.DnsRpcFactory;
import com.google.cloud.dns.spi.v1.DnsRpc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Map;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class DnsImplTest {
    // Dns entities
    private static final String ZONE_NAME = "some zone name";

    private static final String DNS_NAME = "example.com.";

    private static final String DESCRIPTION = "desc";

    private static final String CHANGE_ID = "some change id";

    private static final RecordSet DNS_RECORD1 = RecordSet.newBuilder("Something", AAAA).build();

    private static final RecordSet DNS_RECORD2 = RecordSet.newBuilder("Different", AAAA).build();

    private static final Integer MAX_SIZE = 20;

    private static final String PAGE_TOKEN = "some token";

    private static final ZoneInfo ZONE_INFO = ZoneInfo.of(DnsImplTest.ZONE_NAME, DnsImplTest.DNS_NAME, DnsImplTest.DESCRIPTION);

    private static final ProjectInfo PROJECT_INFO = ProjectInfo.newBuilder().build();

    private static final ChangeRequestInfo CHANGE_REQUEST_PARTIAL = ChangeRequestInfo.newBuilder().add(DnsImplTest.DNS_RECORD1).build();

    private static final ChangeRequestInfo CHANGE_REQUEST_COMPLETE = ChangeRequestInfo.newBuilder().add(DnsImplTest.DNS_RECORD1).setStartTime(123L).setStatus(PENDING).setGeneratedId(DnsImplTest.CHANGE_ID).build();

    // Result lists
    private static final DnsRpc.ListResult<Change> LIST_RESULT_OF_PB_CHANGES = ListResult.of("cursor", ImmutableList.of(DnsImplTest.CHANGE_REQUEST_COMPLETE.toPb(), DnsImplTest.CHANGE_REQUEST_PARTIAL.toPb()));

    private static final DnsRpc.ListResult<ManagedZone> LIST_RESULT_OF_PB_ZONES = ListResult.of("cursor", ImmutableList.of(DnsImplTest.ZONE_INFO.toPb()));

    private static final DnsRpc.ListResult<ResourceRecordSet> LIST_OF_PB_DNS_RECORDS = ListResult.of("cursor", ImmutableList.of(DnsImplTest.DNS_RECORD1.toPb(), DnsImplTest.DNS_RECORD2.toPb()));

    // Field options
    private static final ZoneOption ZONE_FIELDS = ZoneOption.fields(CREATION_TIME);

    private static final ProjectOption PROJECT_FIELDS = ProjectOption.fields(QUOTA);

    private static final ChangeRequestOption CHANGE_GET_FIELDS = ChangeRequestOption.fields(STATUS);

    // Listing options
    private static final ZoneListOption[] ZONE_LIST_OPTIONS = new ZoneListOption[]{ ZoneListOption.pageSize(DnsImplTest.MAX_SIZE), ZoneListOption.pageToken(DnsImplTest.PAGE_TOKEN), ZoneListOption.fields(Dns.ZoneField.DESCRIPTION), ZoneListOption.dnsName(DnsImplTest.DNS_NAME) };

    private static final ChangeRequestListOption[] CHANGE_LIST_OPTIONS = new ChangeRequestListOption[]{ ChangeRequestListOption.pageSize(DnsImplTest.MAX_SIZE), ChangeRequestListOption.pageToken(DnsImplTest.PAGE_TOKEN), ChangeRequestListOption.fields(STATUS), ChangeRequestListOption.sortOrder(ASCENDING) };

    private static final RecordSetListOption[] RECORD_SET_LIST_OPTIONS = new RecordSetListOption[]{ RecordSetListOption.pageSize(DnsImplTest.MAX_SIZE), RecordSetListOption.pageToken(DnsImplTest.PAGE_TOKEN), RecordSetListOption.fields(TTL), RecordSetListOption.dnsName(DnsImplTest.DNS_NAME), RecordSetListOption.type(AAAA) };

    // Other
    private static final Map<DnsRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

    private static final ApiClock TIME_SOURCE = new ApiClock() {
        @Override
        public long nanoTime() {
            return 42000000000L;
        }

        @Override
        public long millisTime() {
            return 42000L;
        }
    };

    private DnsOptions options;

    private DnsRpcFactory rpcFactoryMock;

    private DnsRpc dnsRpcMock;

    private Dns dns;

    @Test
    public void testCreateZone() {
        EasyMock.expect(dnsRpcMock.create(DnsImplTest.ZONE_INFO.toPb(), DnsImplTest.EMPTY_RPC_OPTIONS)).andReturn(DnsImplTest.ZONE_INFO.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Zone zone = dns.create(DnsImplTest.ZONE_INFO);
        Assert.assertEquals(new Zone(dns, new ZoneInfo.BuilderImpl(DnsImplTest.ZONE_INFO)), zone);
    }

    @Test
    public void testCreateZoneWithOptions() {
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(dnsRpcMock.create(EasyMock.eq(DnsImplTest.ZONE_INFO.toPb()), EasyMock.capture(capturedOptions))).andReturn(DnsImplTest.ZONE_INFO.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Zone zone = dns.create(DnsImplTest.ZONE_INFO, DnsImplTest.ZONE_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(DnsImplTest.ZONE_FIELDS.getRpcOption())));
        Assert.assertEquals(new Zone(dns, new ZoneInfo.BuilderImpl(DnsImplTest.ZONE_INFO)), zone);
        Assert.assertTrue(selector.contains(CREATION_TIME.getSelector()));
        Assert.assertTrue(selector.contains(NAME.getSelector()));
    }

    @Test
    public void testGetZone() {
        EasyMock.expect(dnsRpcMock.getZone(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.EMPTY_RPC_OPTIONS)).andReturn(DnsImplTest.ZONE_INFO.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Zone zone = dns.getZone(DnsImplTest.ZONE_INFO.getName());
        Assert.assertEquals(new Zone(dns, new ZoneInfo.BuilderImpl(DnsImplTest.ZONE_INFO)), zone);
    }

    @Test
    public void testGetZoneWithOptions() {
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(dnsRpcMock.getZone(EasyMock.eq(DnsImplTest.ZONE_INFO.getName()), EasyMock.capture(capturedOptions))).andReturn(DnsImplTest.ZONE_INFO.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Zone zone = dns.getZone(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.ZONE_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(DnsImplTest.ZONE_FIELDS.getRpcOption())));
        Assert.assertEquals(new Zone(dns, new ZoneInfo.BuilderImpl(DnsImplTest.ZONE_INFO)), zone);
        Assert.assertTrue(selector.contains(CREATION_TIME.getSelector()));
        Assert.assertTrue(selector.contains(NAME.getSelector()));
    }

    @Test
    public void testDeleteZone() {
        EasyMock.expect(dnsRpcMock.deleteZone(DnsImplTest.ZONE_INFO.getName())).andReturn(true);
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Assert.assertTrue(dns.delete(DnsImplTest.ZONE_INFO.getName()));
    }

    @Test
    public void testGetProject() {
        EasyMock.expect(dnsRpcMock.getProject(DnsImplTest.EMPTY_RPC_OPTIONS)).andReturn(DnsImplTest.PROJECT_INFO.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        ProjectInfo projectInfo = dns.getProject();
        Assert.assertEquals(DnsImplTest.PROJECT_INFO, projectInfo);
    }

    @Test
    public void testProjectGetWithOptions() {
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(dnsRpcMock.getProject(EasyMock.capture(capturedOptions))).andReturn(DnsImplTest.PROJECT_INFO.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        ProjectInfo projectInfo = dns.getProject(DnsImplTest.PROJECT_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(DnsImplTest.PROJECT_FIELDS.getRpcOption())));
        Assert.assertEquals(DnsImplTest.PROJECT_INFO, projectInfo);
        Assert.assertTrue(selector.contains(QUOTA.getSelector()));
        Assert.assertTrue(selector.contains(PROJECT_ID.getSelector()));
    }

    @Test
    public void testGetChangeRequest() {
        EasyMock.expect(dnsRpcMock.getChangeRequest(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.CHANGE_REQUEST_COMPLETE.getGeneratedId(), DnsImplTest.EMPTY_RPC_OPTIONS)).andReturn(DnsImplTest.CHANGE_REQUEST_COMPLETE.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        ChangeRequest changeRequest = dns.getChangeRequest(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.CHANGE_REQUEST_COMPLETE.getGeneratedId());
        Assert.assertEquals(new ChangeRequest(dns, DnsImplTest.ZONE_INFO.getName(), new ChangeRequestInfo.BuilderImpl(DnsImplTest.CHANGE_REQUEST_COMPLETE)), changeRequest);
    }

    @Test
    public void testGetChangeRequestWithOptions() {
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(dnsRpcMock.getChangeRequest(EasyMock.eq(DnsImplTest.ZONE_INFO.getName()), EasyMock.eq(DnsImplTest.CHANGE_REQUEST_COMPLETE.getGeneratedId()), EasyMock.capture(capturedOptions))).andReturn(DnsImplTest.CHANGE_REQUEST_COMPLETE.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        ChangeRequest changeRequest = dns.getChangeRequest(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.CHANGE_REQUEST_COMPLETE.getGeneratedId(), DnsImplTest.CHANGE_GET_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(DnsImplTest.CHANGE_GET_FIELDS.getRpcOption())));
        Assert.assertEquals(new ChangeRequest(dns, DnsImplTest.ZONE_INFO.getName(), new ChangeRequestInfo.BuilderImpl(DnsImplTest.CHANGE_REQUEST_COMPLETE)), changeRequest);
        Assert.assertTrue(selector.contains(STATUS.getSelector()));
        Assert.assertTrue(selector.contains(ID.getSelector()));
    }

    @Test
    public void testApplyChangeRequest() {
        EasyMock.expect(dnsRpcMock.applyChangeRequest(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.CHANGE_REQUEST_PARTIAL.toPb(), DnsImplTest.EMPTY_RPC_OPTIONS)).andReturn(DnsImplTest.CHANGE_REQUEST_COMPLETE.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        ChangeRequest changeRequest = dns.applyChangeRequest(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.CHANGE_REQUEST_PARTIAL);
        Assert.assertEquals(new ChangeRequest(dns, DnsImplTest.ZONE_INFO.getName(), new ChangeRequestInfo.BuilderImpl(DnsImplTest.CHANGE_REQUEST_COMPLETE)), changeRequest);
    }

    @Test
    public void testApplyChangeRequestWithOptions() {
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(dnsRpcMock.applyChangeRequest(EasyMock.eq(DnsImplTest.ZONE_INFO.getName()), EasyMock.eq(DnsImplTest.CHANGE_REQUEST_PARTIAL.toPb()), EasyMock.capture(capturedOptions))).andReturn(DnsImplTest.CHANGE_REQUEST_COMPLETE.toPb());
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        ChangeRequest changeRequest = dns.applyChangeRequest(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.CHANGE_REQUEST_PARTIAL, DnsImplTest.CHANGE_GET_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(DnsImplTest.CHANGE_GET_FIELDS.getRpcOption())));
        Assert.assertEquals(new ChangeRequest(dns, DnsImplTest.ZONE_INFO.getName(), new ChangeRequestInfo.BuilderImpl(DnsImplTest.CHANGE_REQUEST_COMPLETE)), changeRequest);
        Assert.assertTrue(selector.contains(STATUS.getSelector()));
        Assert.assertTrue(selector.contains(ID.getSelector()));
    }

    // lists
    @Test
    public void testListChangeRequests() {
        EasyMock.expect(dnsRpcMock.listChangeRequests(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.EMPTY_RPC_OPTIONS)).andReturn(com.google.cloud.dns.LIST_RESULT_OF_PB_CHANGES);
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Page<ChangeRequest> changeRequestPage = dns.listChangeRequests(DnsImplTest.ZONE_INFO.getName());
        Assert.assertTrue(Lists.newArrayList(changeRequestPage.getValues()).contains(new ChangeRequest(dns, DnsImplTest.ZONE_INFO.getName(), new ChangeRequestInfo.BuilderImpl(DnsImplTest.CHANGE_REQUEST_COMPLETE))));
        Assert.assertTrue(Lists.newArrayList(changeRequestPage.getValues()).contains(new ChangeRequest(dns, DnsImplTest.ZONE_INFO.getName(), new ChangeRequestInfo.BuilderImpl(DnsImplTest.CHANGE_REQUEST_PARTIAL))));
        Assert.assertEquals(2, Lists.newArrayList(changeRequestPage.getValues()).size());
    }

    @Test
    public void testListChangeRequestsWithOptions() {
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(dnsRpcMock.listChangeRequests(EasyMock.eq(DnsImplTest.ZONE_NAME), EasyMock.capture(capturedOptions))).andReturn(com.google.cloud.dns.LIST_RESULT_OF_PB_CHANGES);
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Page<ChangeRequest> changeRequestPage = dns.listChangeRequests(DnsImplTest.ZONE_NAME, DnsImplTest.CHANGE_LIST_OPTIONS);
        Assert.assertTrue(Lists.newArrayList(changeRequestPage.getValues()).contains(new ChangeRequest(dns, DnsImplTest.ZONE_INFO.getName(), new ChangeRequestInfo.BuilderImpl(DnsImplTest.CHANGE_REQUEST_COMPLETE))));
        Assert.assertTrue(Lists.newArrayList(changeRequestPage.getValues()).contains(new ChangeRequest(dns, DnsImplTest.ZONE_INFO.getName(), new ChangeRequestInfo.BuilderImpl(DnsImplTest.CHANGE_REQUEST_PARTIAL))));
        Assert.assertEquals(2, Lists.newArrayList(changeRequestPage.getValues()).size());
        Integer size = ((Integer) (capturedOptions.getValue().get(DnsImplTest.CHANGE_LIST_OPTIONS[0].getRpcOption())));
        Assert.assertEquals(DnsImplTest.MAX_SIZE, size);
        String selector = ((String) (capturedOptions.getValue().get(DnsImplTest.CHANGE_LIST_OPTIONS[1].getRpcOption())));
        Assert.assertEquals(DnsImplTest.PAGE_TOKEN, selector);
        selector = ((String) (capturedOptions.getValue().get(DnsImplTest.CHANGE_LIST_OPTIONS[2].getRpcOption())));
        Assert.assertTrue(selector.contains(STATUS.getSelector()));
        Assert.assertTrue(selector.contains(ID.getSelector()));
        selector = ((String) (capturedOptions.getValue().get(DnsImplTest.CHANGE_LIST_OPTIONS[3].getRpcOption())));
        Assert.assertTrue(selector.contains(ASCENDING.selector()));
    }

    @Test
    public void testListZones() {
        EasyMock.expect(dnsRpcMock.listZones(DnsImplTest.EMPTY_RPC_OPTIONS)).andReturn(com.google.cloud.dns.LIST_RESULT_OF_PB_ZONES);
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Page<Zone> zonePage = dns.listZones();
        Assert.assertEquals(1, Lists.newArrayList(zonePage.getValues()).size());
        Assert.assertEquals(new Zone(dns, new ZoneInfo.BuilderImpl(DnsImplTest.ZONE_INFO)), Lists.newArrayList(zonePage.getValues()).get(0));
    }

    @Test
    public void testListZonesWithOptions() {
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(dnsRpcMock.listZones(EasyMock.capture(capturedOptions))).andReturn(com.google.cloud.dns.LIST_RESULT_OF_PB_ZONES);
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Page<Zone> zonePage = dns.listZones(DnsImplTest.ZONE_LIST_OPTIONS);
        Assert.assertEquals(1, Lists.newArrayList(zonePage.getValues()).size());
        Assert.assertEquals(new Zone(dns, new ZoneInfo.BuilderImpl(DnsImplTest.ZONE_INFO)), Lists.newArrayList(zonePage.getValues()).get(0));
        Integer size = ((Integer) (capturedOptions.getValue().get(DnsImplTest.ZONE_LIST_OPTIONS[0].getRpcOption())));
        Assert.assertEquals(DnsImplTest.MAX_SIZE, size);
        String selector = ((String) (capturedOptions.getValue().get(DnsImplTest.ZONE_LIST_OPTIONS[1].getRpcOption())));
        Assert.assertEquals(DnsImplTest.PAGE_TOKEN, selector);
        selector = ((String) (capturedOptions.getValue().get(DnsImplTest.ZONE_LIST_OPTIONS[2].getRpcOption())));
        Assert.assertTrue(selector.contains(Dns.ZoneField.DESCRIPTION.getSelector()));
        Assert.assertTrue(selector.contains(NAME.getSelector()));
        selector = ((String) (capturedOptions.getValue().get(DnsImplTest.ZONE_LIST_OPTIONS[3].getRpcOption())));
        Assert.assertEquals(DnsImplTest.DNS_NAME, selector);
    }

    @Test
    public void testListRecordSets() {
        EasyMock.expect(dnsRpcMock.listRecordSets(DnsImplTest.ZONE_INFO.getName(), DnsImplTest.EMPTY_RPC_OPTIONS)).andReturn(com.google.cloud.dns.LIST_OF_PB_DNS_RECORDS);
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Page<RecordSet> dnsPage = dns.listRecordSets(DnsImplTest.ZONE_INFO.getName());
        Assert.assertEquals(2, Lists.newArrayList(dnsPage.getValues()).size());
        Assert.assertTrue(Lists.newArrayList(dnsPage.getValues()).contains(DnsImplTest.DNS_RECORD1));
        Assert.assertTrue(Lists.newArrayList(dnsPage.getValues()).contains(DnsImplTest.DNS_RECORD2));
    }

    @Test
    public void testListRecordSetsWithOptions() {
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        EasyMock.expect(dnsRpcMock.listRecordSets(EasyMock.eq(DnsImplTest.ZONE_NAME), EasyMock.capture(capturedOptions))).andReturn(com.google.cloud.dns.LIST_OF_PB_DNS_RECORDS);
        EasyMock.replay(dnsRpcMock);
        dns = options.getService();// creates DnsImpl

        Page<RecordSet> dnsPage = dns.listRecordSets(DnsImplTest.ZONE_NAME, DnsImplTest.RECORD_SET_LIST_OPTIONS);
        Assert.assertEquals(2, Lists.newArrayList(dnsPage.getValues()).size());
        Assert.assertTrue(Lists.newArrayList(dnsPage.getValues()).contains(DnsImplTest.DNS_RECORD1));
        Assert.assertTrue(Lists.newArrayList(dnsPage.getValues()).contains(DnsImplTest.DNS_RECORD2));
        Integer size = ((Integer) (capturedOptions.getValue().get(DnsImplTest.RECORD_SET_LIST_OPTIONS[0].getRpcOption())));
        Assert.assertEquals(DnsImplTest.MAX_SIZE, size);
        String selector = ((String) (capturedOptions.getValue().get(DnsImplTest.RECORD_SET_LIST_OPTIONS[1].getRpcOption())));
        Assert.assertEquals(DnsImplTest.PAGE_TOKEN, selector);
        selector = ((String) (capturedOptions.getValue().get(DnsImplTest.RECORD_SET_LIST_OPTIONS[2].getRpcOption())));
        Assert.assertTrue(selector.contains(Dns.RecordSetField.NAME.getSelector()));
        Assert.assertTrue(selector.contains(TTL.getSelector()));
        selector = ((String) (capturedOptions.getValue().get(DnsImplTest.RECORD_SET_LIST_OPTIONS[3].getRpcOption())));
        Assert.assertEquals(DnsImplTest.RECORD_SET_LIST_OPTIONS[3].getValue(), selector);
        String type = ((String) (capturedOptions.getValue().get(DnsImplTest.RECORD_SET_LIST_OPTIONS[4].getRpcOption())));
        Assert.assertEquals(DnsImplTest.RECORD_SET_LIST_OPTIONS[4].getValue(), type);
    }
}

