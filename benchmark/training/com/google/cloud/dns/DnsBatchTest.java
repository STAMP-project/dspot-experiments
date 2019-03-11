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
import DnsRpc.Option;
import GoogleJsonError.ErrorInfo;
import RecordSet.Type.AAAA;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.gax.paging.Page;
import com.google.api.services.dns.model.Change;
import com.google.api.services.dns.model.ChangesListResponse;
import com.google.api.services.dns.model.ManagedZone;
import com.google.api.services.dns.model.ManagedZonesListResponse;
import com.google.api.services.dns.model.Project;
import com.google.api.services.dns.model.ResourceRecordSet;
import com.google.api.services.dns.model.ResourceRecordSetsListResponse;
import com.google.cloud.dns.spi.v1.DnsRpc;
import com.google.cloud.dns.spi.v1.RpcBatch;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class DnsBatchTest {
    private static final String ZONE_NAME = "somezonename";

    private static final String DNS_NAME = "example.com.";

    private static final String DESCRIPTION = "desc";

    private static final Integer MAX_SIZE = 20;

    private static final String PAGE_TOKEN = "some token";

    private static final ZoneInfo ZONE_INFO = ZoneInfo.of(DnsBatchTest.ZONE_NAME, DnsBatchTest.DNS_NAME, DnsBatchTest.DESCRIPTION);

    private static final ZoneOption ZONE_FIELDS = ZoneOption.fields(CREATION_TIME);

    private static final ProjectOption PROJECT_FIELDS = ProjectOption.fields(QUOTA);

    private static final ZoneListOption[] ZONE_LIST_OPTIONS = new ZoneListOption[]{ ZoneListOption.pageSize(DnsBatchTest.MAX_SIZE), ZoneListOption.pageToken(DnsBatchTest.PAGE_TOKEN), ZoneListOption.fields(Dns.ZoneField.DESCRIPTION), ZoneListOption.dnsName(DnsBatchTest.DNS_NAME) };

    private static final ProjectInfo PROJECT_INFO = ProjectInfo.newBuilder().build();

    private static final RecordSetListOption[] RECORD_SET_LIST_OPTIONS = new RecordSetListOption[]{ RecordSetListOption.pageSize(DnsBatchTest.MAX_SIZE), RecordSetListOption.pageToken(DnsBatchTest.PAGE_TOKEN), RecordSetListOption.fields(TTL), RecordSetListOption.dnsName(DnsBatchTest.DNS_NAME), RecordSetListOption.type(AAAA) };

    private static final RecordSet RECORD_SET = RecordSet.newBuilder("Something", AAAA).build();

    private static final ChangeRequestInfo CHANGE_REQUEST_PARTIAL = ChangeRequestInfo.newBuilder().add(DnsBatchTest.RECORD_SET).build();

    private static final String CHANGE_ID = "some change id";

    private static final ChangeRequestInfo CHANGE_REQUEST_COMPLETE = ChangeRequestInfo.newBuilder().add(DnsBatchTest.RECORD_SET).setStartTime(123L).setStatus(PENDING).setGeneratedId(DnsBatchTest.CHANGE_ID).build();

    private static final ChangeRequestListOption[] CHANGE_LIST_OPTIONS = new ChangeRequestListOption[]{ ChangeRequestListOption.pageSize(DnsBatchTest.MAX_SIZE), ChangeRequestListOption.pageToken(DnsBatchTest.PAGE_TOKEN), ChangeRequestListOption.fields(STATUS), ChangeRequestListOption.sortOrder(ASCENDING) };

    private static final ChangeRequestOption CHANGE_GET_FIELDS = ChangeRequestOption.fields(STATUS);

    private static final List<ResourceRecordSet> RECORD_SET_LIST = ImmutableList.of(DnsBatchTest.RECORD_SET.toPb(), DnsBatchTest.RECORD_SET.toPb(), DnsBatchTest.RECORD_SET.toPb(), DnsBatchTest.RECORD_SET.toPb());

    private static final List<Change> CHANGE_LIST = ImmutableList.of(DnsBatchTest.CHANGE_REQUEST_COMPLETE.toPb(), DnsBatchTest.CHANGE_REQUEST_COMPLETE.toPb(), DnsBatchTest.CHANGE_REQUEST_COMPLETE.toPb());

    private static final List<ManagedZone> ZONE_LIST = ImmutableList.of(DnsBatchTest.ZONE_INFO.toPb(), DnsBatchTest.ZONE_INFO.toPb());

    private static final GoogleJsonError GOOGLE_JSON_ERROR = new GoogleJsonError();

    private DnsOptions optionsMock;

    private DnsRpc dnsRpcMock;

    private RpcBatch batchMock;

    private DnsBatch dnsBatch;

    private final Dns dns = EasyMock.createStrictMock(Dns.class);

    @Test
    public void testConstructor() {
        Assert.assertSame(batchMock, dnsBatch.getBatch());
        Assert.assertSame(optionsMock, dnsBatch.getOptions());
        Assert.assertSame(dnsRpcMock, dnsBatch.getDnsRpc());
    }

    @Test
    public void testListZones() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<ManagedZonesListResponse>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addListZones(EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<Page<Zone>> batchResult = dnsBatch.listZones();
        Assert.assertEquals(0, capturedOptions.getValue().size());
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        RpcBatch.Callback<ManagedZonesListResponse> capturedCallback = callback.getValue();
        capturedCallback.onFailure(DnsBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a DnsException on error.");
        } catch (DnsException ex) {
            // expected
        }
    }

    @Test
    public void testListZonesWithOptions() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<ManagedZonesListResponse>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addListZones(EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<Page<Zone>> batchResult = dnsBatch.listZones(DnsBatchTest.ZONE_LIST_OPTIONS);
        Assert.assertNotNull(callback.getValue());
        Integer size = ((Integer) (capturedOptions.getValue().get(DnsBatchTest.ZONE_LIST_OPTIONS[0].getRpcOption())));
        Assert.assertEquals(DnsBatchTest.MAX_SIZE, size);
        String selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.ZONE_LIST_OPTIONS[1].getRpcOption())));
        Assert.assertEquals(DnsBatchTest.PAGE_TOKEN, selector);
        selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.ZONE_LIST_OPTIONS[2].getRpcOption())));
        Assert.assertTrue(selector.contains(Dns.ZoneField.DESCRIPTION.getSelector()));
        Assert.assertTrue(selector.contains(NAME.getSelector()));
        selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.ZONE_LIST_OPTIONS[3].getRpcOption())));
        Assert.assertEquals(DnsBatchTest.DNS_NAME, selector);
        // check the callback
        ManagedZonesListResponse response = new ManagedZonesListResponse().setManagedZones(DnsBatchTest.ZONE_LIST).setNextPageToken(DnsBatchTest.PAGE_TOKEN);
        RpcBatch.Callback<ManagedZonesListResponse> capturedCallback = callback.getValue();
        EasyMock.verify(optionsMock);
        EasyMock.reset(optionsMock);
        EasyMock.expect(optionsMock.getService()).andReturn(dns).times(DnsBatchTest.ZONE_LIST.size());
        EasyMock.replay(optionsMock);
        capturedCallback.onSuccess(response);
        Page<Zone> page = batchResult.get();
        Assert.assertEquals(DnsBatchTest.PAGE_TOKEN, page.getNextPageToken());
        Iterator<Zone> iterator = page.getValues().iterator();
        int resultSize = 0;
        EasyMock.verify(dns);
        EasyMock.reset(dns);
        EasyMock.expect(dns.getOptions()).andReturn(optionsMock).times(((DnsBatchTest.ZONE_LIST.size()) + 1));
        EasyMock.replay(dns);
        Zone zoneInfoFunctional = new Zone(dns, new ZoneInfo.BuilderImpl(DnsBatchTest.ZONE_INFO));
        while (iterator.hasNext()) {
            Assert.assertEquals(zoneInfoFunctional, iterator.next());
            resultSize++;
        } 
        Assert.assertEquals(DnsBatchTest.ZONE_LIST.size(), resultSize);
    }

    @Test
    public void testCreateZone() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<ManagedZone>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        Capture<ManagedZone> capturedZone = Capture.newInstance();
        batchMock.addCreateZone(EasyMock.capture(capturedZone), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<Zone> batchResult = dnsBatch.createZone(DnsBatchTest.ZONE_INFO);
        Assert.assertEquals(0, capturedOptions.getValue().size());
        Assert.assertEquals(DnsBatchTest.ZONE_INFO.toPb(), capturedZone.getValue());
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        // testing error here, success is tested with options
        RpcBatch.Callback<ManagedZone> capturedCallback = callback.getValue();
        GoogleJsonError error = new GoogleJsonError();
        error.setCode(404);
        capturedCallback.onFailure(error);
        try {
            batchResult.get();
            Assert.fail("Should throw a DnsException on error.");
        } catch (DnsException ex) {
            // expected
        }
    }

    @Test
    public void testCreateZoneWithOptions() {
        EasyMock.reset(dns, batchMock, optionsMock);
        EasyMock.expect(dns.getOptions()).andReturn(optionsMock);
        EasyMock.expect(optionsMock.getService()).andReturn(dns);
        Capture<RpcBatch.Callback<ManagedZone>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        Capture<ManagedZone> capturedZone = Capture.newInstance();
        batchMock.addCreateZone(EasyMock.capture(capturedZone), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(dns, batchMock, optionsMock);
        DnsBatchResult<Zone> batchResult = dnsBatch.createZone(DnsBatchTest.ZONE_INFO, DnsBatchTest.ZONE_FIELDS);
        Assert.assertEquals(DnsBatchTest.ZONE_INFO.toPb(), capturedZone.getValue());
        Assert.assertNotNull(callback.getValue());
        String selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.ZONE_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains(CREATION_TIME.getSelector()));
        Assert.assertTrue(selector.contains(NAME.getSelector()));
        RpcBatch.Callback<ManagedZone> capturedCallback = callback.getValue();
        capturedCallback.onSuccess(DnsBatchTest.ZONE_INFO.toPb());
        Assert.assertEquals(DnsBatchTest.ZONE_INFO.toPb(), batchResult.get().toPb());
    }

    @Test
    public void testGetZone() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<ManagedZone>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addGetZone(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<Zone> batchResult = dnsBatch.getZone(DnsBatchTest.ZONE_NAME);
        Assert.assertEquals(0, capturedOptions.getValue().size());
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        // testing error here, success is tested with options
        RpcBatch.Callback<ManagedZone> capturedCallback = callback.getValue();
        capturedCallback.onFailure(DnsBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a DnsException on error.");
        } catch (DnsException ex) {
            // expected
        }
    }

    @Test
    public void testGetZoneNotFound() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<ManagedZone>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addGetZone(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<Zone> batchResult = dnsBatch.getZone(DnsBatchTest.ZONE_NAME);
        Assert.assertEquals(0, capturedOptions.getValue().size());
        GoogleJsonError error = new GoogleJsonError();
        error.setCode(404);
        RpcBatch.Callback<ManagedZone> capturedCallback = callback.getValue();
        capturedCallback.onFailure(error);
        Assert.assertNull(batchResult.get());
    }

    @Test
    public void testGetZoneWithOptions() {
        EasyMock.reset(dns, batchMock, optionsMock);
        EasyMock.expect(dns.getOptions()).andReturn(optionsMock);
        Capture<RpcBatch.Callback<ManagedZone>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addGetZone(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.expect(optionsMock.getService()).andReturn(dns);
        EasyMock.replay(dns, batchMock, optionsMock);
        DnsBatchResult<Zone> batchResult = dnsBatch.getZone(DnsBatchTest.ZONE_NAME, DnsBatchTest.ZONE_FIELDS);
        Assert.assertNotNull(callback.getValue());
        String selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.ZONE_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains(CREATION_TIME.getSelector()));
        Assert.assertTrue(selector.contains(NAME.getSelector()));
        RpcBatch.Callback<ManagedZone> capturedCallback = callback.getValue();
        capturedCallback.onSuccess(DnsBatchTest.ZONE_INFO.toPb());
        Assert.assertEquals(DnsBatchTest.ZONE_INFO.toPb(), batchResult.get().toPb());
    }

    @Test
    public void testDeleteZone() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<Void>> callback = Capture.newInstance();
        batchMock.addDeleteZone(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.capture(callback));
        EasyMock.replay(batchMock);
        DnsBatchResult<Boolean> batchResult = dnsBatch.deleteZone(DnsBatchTest.ZONE_NAME);
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        // testing error here, success is tested with options
        RpcBatch.Callback<Void> capturedCallback = callback.getValue();
        capturedCallback.onFailure(DnsBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a DnsException on error.");
        } catch (DnsException ex) {
            // expected
        }
    }

    @Test
    public void testDeleteZoneOnSuccess() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<Void>> callback = Capture.newInstance();
        batchMock.addDeleteZone(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.capture(callback));
        EasyMock.replay(batchMock);
        DnsBatchResult<Boolean> batchResult = dnsBatch.deleteZone(DnsBatchTest.ZONE_NAME);
        Assert.assertNotNull(callback.getValue());
        RpcBatch.Callback<Void> capturedCallback = callback.getValue();
        Void result = null;
        capturedCallback.onSuccess(result);
        Assert.assertTrue(batchResult.get());
    }

    @Test
    public void testGetProject() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<Project>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addGetProject(EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<ProjectInfo> batchResult = dnsBatch.getProject();
        Assert.assertEquals(0, capturedOptions.getValue().size());
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        // testing error here, success is tested with options
        RpcBatch.Callback<Project> capturedCallback = callback.getValue();
        capturedCallback.onFailure(DnsBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a DnsException on error.");
        } catch (DnsException ex) {
            // expected
        }
    }

    @Test
    public void testGetProjectWithOptions() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<Project>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addGetProject(EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<ProjectInfo> batchResult = dnsBatch.getProject(DnsBatchTest.PROJECT_FIELDS);
        Assert.assertNotNull(callback.getValue());
        String selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.PROJECT_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains(QUOTA.getSelector()));
        Assert.assertTrue(selector.contains(PROJECT_ID.getSelector()));
        RpcBatch.Callback<Project> capturedCallback = callback.getValue();
        capturedCallback.onSuccess(DnsBatchTest.PROJECT_INFO.toPb());
        Assert.assertEquals(DnsBatchTest.PROJECT_INFO, batchResult.get());
    }

    @Test
    public void testListRecordSets() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<ResourceRecordSetsListResponse>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addListRecordSets(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<Page<RecordSet>> batchResult = dnsBatch.listRecordSets(DnsBatchTest.ZONE_NAME);
        Assert.assertEquals(0, capturedOptions.getValue().size());
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        RpcBatch.Callback<ResourceRecordSetsListResponse> capturedCallback = callback.getValue();
        capturedCallback.onFailure(DnsBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a DnsException on error.");
        } catch (DnsException ex) {
            // expected
        }
    }

    @Test
    public void testListRecordSetsWithOptions() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<ResourceRecordSetsListResponse>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addListRecordSets(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<Page<RecordSet>> batchResult = dnsBatch.listRecordSets(DnsBatchTest.ZONE_NAME, DnsBatchTest.RECORD_SET_LIST_OPTIONS);
        Assert.assertNotNull(callback.getValue());
        Integer size = ((Integer) (capturedOptions.getValue().get(DnsBatchTest.RECORD_SET_LIST_OPTIONS[0].getRpcOption())));
        Assert.assertEquals(DnsBatchTest.MAX_SIZE, size);
        String selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.RECORD_SET_LIST_OPTIONS[1].getRpcOption())));
        Assert.assertEquals(DnsBatchTest.PAGE_TOKEN, selector);
        selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.RECORD_SET_LIST_OPTIONS[2].getRpcOption())));
        Assert.assertTrue(selector.contains(Dns.RecordSetField.NAME.getSelector()));
        Assert.assertTrue(selector.contains(TTL.getSelector()));
        selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.RECORD_SET_LIST_OPTIONS[3].getRpcOption())));
        Assert.assertEquals(DnsBatchTest.RECORD_SET_LIST_OPTIONS[3].getValue(), selector);
        String type = ((String) (capturedOptions.getValue().get(DnsBatchTest.RECORD_SET_LIST_OPTIONS[4].getRpcOption())));
        Assert.assertEquals(DnsBatchTest.RECORD_SET_LIST_OPTIONS[4].getValue(), type);
        RpcBatch.Callback<ResourceRecordSetsListResponse> capturedCallback = callback.getValue();
        ResourceRecordSetsListResponse response = new ResourceRecordSetsListResponse().setRrsets(DnsBatchTest.RECORD_SET_LIST).setNextPageToken(DnsBatchTest.PAGE_TOKEN);
        capturedCallback.onSuccess(response);
        Page<RecordSet> page = batchResult.get();
        Assert.assertEquals(DnsBatchTest.PAGE_TOKEN, page.getNextPageToken());
        Iterator<RecordSet> iterator = page.getValues().iterator();
        int resultSize = 0;
        while (iterator.hasNext()) {
            Assert.assertEquals(DnsBatchTest.RECORD_SET, iterator.next());
            resultSize++;
        } 
        Assert.assertEquals(DnsBatchTest.RECORD_SET_LIST.size(), resultSize);
    }

    @Test
    public void testListChangeRequests() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<ChangesListResponse>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addListChangeRequests(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<Page<ChangeRequest>> batchResult = dnsBatch.listChangeRequests(DnsBatchTest.ZONE_NAME);
        Assert.assertNotNull(callback.getValue());
        Assert.assertEquals(0, capturedOptions.getValue().size());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        RpcBatch.Callback<ChangesListResponse> capturedCallback = callback.getValue();
        capturedCallback.onFailure(DnsBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a DnsException on error.");
        } catch (DnsException ex) {
            // expected
        }
    }

    @Test
    public void testListChangeRequestsWithOptions() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<ChangesListResponse>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addListChangeRequests(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<Page<ChangeRequest>> batchResult = dnsBatch.listChangeRequests(DnsBatchTest.ZONE_NAME, DnsBatchTest.CHANGE_LIST_OPTIONS);
        Assert.assertNotNull(callback.getValue());
        Integer size = ((Integer) (capturedOptions.getValue().get(DnsBatchTest.CHANGE_LIST_OPTIONS[0].getRpcOption())));
        Assert.assertEquals(DnsBatchTest.MAX_SIZE, size);
        String selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.CHANGE_LIST_OPTIONS[1].getRpcOption())));
        Assert.assertEquals(DnsBatchTest.PAGE_TOKEN, selector);
        selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.CHANGE_LIST_OPTIONS[2].getRpcOption())));
        Assert.assertTrue(selector.contains(STATUS.getSelector()));
        Assert.assertTrue(selector.contains(ID.getSelector()));
        selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.CHANGE_LIST_OPTIONS[3].getRpcOption())));
        Assert.assertTrue(selector.contains(ASCENDING.selector()));
        // check the callback
        ChangesListResponse response = new ChangesListResponse().setChanges(DnsBatchTest.CHANGE_LIST).setNextPageToken(DnsBatchTest.PAGE_TOKEN);
        RpcBatch.Callback<ChangesListResponse> capturedCallback = callback.getValue();
        EasyMock.verify(optionsMock);
        EasyMock.reset(optionsMock);
        EasyMock.expect(optionsMock.getService()).andReturn(dns);
        EasyMock.replay(optionsMock);
        capturedCallback.onSuccess(response);
        Page<ChangeRequest> page = batchResult.get();
        Assert.assertEquals(DnsBatchTest.PAGE_TOKEN, page.getNextPageToken());
        Iterator<ChangeRequest> iterator = page.getValues().iterator();
        int resultSize = 0;
        EasyMock.verify(dns);
        EasyMock.reset(dns);
        EasyMock.expect(dns.getOptions()).andReturn(optionsMock).times(DnsBatchTest.CHANGE_LIST.size());
        EasyMock.replay(dns);
        while (iterator.hasNext()) {
            Assert.assertEquals(DnsBatchTest.CHANGE_REQUEST_COMPLETE.toPb(), iterator.next().toPb());
            resultSize++;
        } 
        Assert.assertEquals(DnsBatchTest.CHANGE_LIST.size(), resultSize);
    }

    @Test
    public void testGetChangeRequest() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<Change>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addGetChangeRequest(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.eq(DnsBatchTest.CHANGE_REQUEST_COMPLETE.getGeneratedId()), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<ChangeRequest> batchResult = dnsBatch.getChangeRequest(DnsBatchTest.ZONE_NAME, DnsBatchTest.CHANGE_REQUEST_COMPLETE.getGeneratedId());
        Assert.assertEquals(0, capturedOptions.getValue().size());
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        // testing error here, success is tested with options
        RpcBatch.Callback<Change> capturedCallback = callback.getValue();
        capturedCallback.onFailure(DnsBatchTest.GOOGLE_JSON_ERROR);
        try {
            batchResult.get();
            Assert.fail("Should throw a DnsException on error.");
        } catch (DnsException ex) {
            // expected
        }
    }

    @Test
    public void testGetChangeRequestNotFound() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<Change>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addGetChangeRequest(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.eq(DnsBatchTest.CHANGE_REQUEST_COMPLETE.getGeneratedId()), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<ChangeRequest> batchResult = dnsBatch.getChangeRequest(DnsBatchTest.ZONE_NAME, DnsBatchTest.CHANGE_REQUEST_COMPLETE.getGeneratedId());
        Assert.assertEquals(0, capturedOptions.getValue().size());
        RpcBatch.Callback<Change> capturedCallback = callback.getValue();
        GoogleJsonError error = new GoogleJsonError();
        GoogleJsonError.ErrorInfo errorInfo = new GoogleJsonError.ErrorInfo();
        errorInfo.setReason("reason");
        errorInfo.setLocation("entity.parameters.changeId");
        error.setCode(404);
        error.setErrors(ImmutableList.of(errorInfo));
        capturedCallback.onFailure(error);
        Assert.assertNull(batchResult.get());
    }

    @Test
    public void testGetChangeRequestWithOptions() {
        EasyMock.reset(dns, batchMock, optionsMock);
        EasyMock.expect(dns.getOptions()).andReturn(optionsMock);
        EasyMock.expect(optionsMock.getService()).andReturn(dns);
        Capture<RpcBatch.Callback<Change>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addGetChangeRequest(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.eq(DnsBatchTest.CHANGE_REQUEST_COMPLETE.getGeneratedId()), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(dns, batchMock, optionsMock);
        DnsBatchResult<ChangeRequest> batchResult = dnsBatch.getChangeRequest(DnsBatchTest.ZONE_NAME, DnsBatchTest.CHANGE_REQUEST_COMPLETE.getGeneratedId(), DnsBatchTest.CHANGE_GET_FIELDS);
        Assert.assertNotNull(callback.getValue());
        String selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.CHANGE_GET_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains(STATUS.getSelector()));
        Assert.assertTrue(selector.contains(ID.getSelector()));
        RpcBatch.Callback<Change> capturedCallback = callback.getValue();
        capturedCallback.onSuccess(DnsBatchTest.CHANGE_REQUEST_COMPLETE.toPb());
        Assert.assertEquals(DnsBatchTest.CHANGE_REQUEST_COMPLETE.toPb(), batchResult.get().toPb());
    }

    @Test
    public void testApplyChangeRequest() {
        EasyMock.reset(batchMock);
        Capture<RpcBatch.Callback<Change>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addApplyChangeRequest(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.eq(DnsBatchTest.CHANGE_REQUEST_PARTIAL.toPb()), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.replay(batchMock);
        DnsBatchResult<ChangeRequest> batchResult = dnsBatch.applyChangeRequest(DnsBatchTest.ZONE_INFO.getName(), DnsBatchTest.CHANGE_REQUEST_PARTIAL);
        Assert.assertEquals(0, capturedOptions.getValue().size());
        Assert.assertNotNull(callback.getValue());
        try {
            batchResult.get();
            Assert.fail("No result available yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        // testing error here, success is tested with options
        RpcBatch.Callback<Change> capturedCallback = callback.getValue();
        GoogleJsonError error = new GoogleJsonError();
        error.setCode(404);
        capturedCallback.onFailure(error);
        try {
            batchResult.get();
            Assert.fail("Should throw a DnsException on error.");
        } catch (DnsException ex) {
            // expected
        }
    }

    @Test
    public void testApplyChangeRequestWithOptions() {
        EasyMock.reset(dns, batchMock, optionsMock);
        EasyMock.expect(dns.getOptions()).andReturn(optionsMock);
        Capture<RpcBatch.Callback<Change>> callback = Capture.newInstance();
        Capture<Map<DnsRpc.Option, Object>> capturedOptions = Capture.newInstance();
        batchMock.addApplyChangeRequest(EasyMock.eq(DnsBatchTest.ZONE_NAME), EasyMock.eq(DnsBatchTest.CHANGE_REQUEST_PARTIAL.toPb()), EasyMock.capture(callback), EasyMock.capture(capturedOptions));
        EasyMock.expect(optionsMock.getService()).andReturn(dns);
        EasyMock.replay(dns, batchMock, optionsMock);
        DnsBatchResult<ChangeRequest> batchResult = dnsBatch.applyChangeRequest(DnsBatchTest.ZONE_INFO.getName(), DnsBatchTest.CHANGE_REQUEST_PARTIAL, DnsBatchTest.CHANGE_GET_FIELDS);
        String selector = ((String) (capturedOptions.getValue().get(DnsBatchTest.CHANGE_GET_FIELDS.getRpcOption())));
        Assert.assertTrue(selector.contains(STATUS.getSelector()));
        Assert.assertTrue(selector.contains(ID.getSelector()));
        RpcBatch.Callback<Change> capturedCallback = callback.getValue();
        capturedCallback.onSuccess(DnsBatchTest.CHANGE_REQUEST_COMPLETE.toPb());
        Assert.assertEquals(DnsBatchTest.CHANGE_REQUEST_COMPLETE.toPb(), batchResult.get().toPb());
    }
}

