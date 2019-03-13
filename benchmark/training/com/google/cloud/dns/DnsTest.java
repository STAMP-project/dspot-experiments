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


import Dns.ChangeRequestField.ID;
import Dns.ChangeRequestField.START_TIME;
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
import Dns.ZoneListOption;
import Dns.ZoneOption;
import DnsRpc.Option.DNS_NAME;
import DnsRpc.Option.DNS_TYPE;
import DnsRpc.Option.FIELDS;
import DnsRpc.Option.NAME;
import DnsRpc.Option.PAGE_SIZE;
import DnsRpc.Option.PAGE_TOKEN;
import DnsRpc.Option.SORTING_ORDER;
import RecordSet.Type;
import org.junit.Assert;
import org.junit.Test;


public class DnsTest {
    private static final Integer PAGE_SIZE = 20;

    private static final String PAGE_TOKEN = "page token";

    private static final String DNS_NAME = "www.example.com.";

    @Test
    public void testRecordSetListOption() {
        // dns name
        String dnsName = "some name";
        Dns.RecordSetListOption recordSetListOption = RecordSetListOption.dnsName(dnsName);
        Assert.assertEquals(dnsName, recordSetListOption.getValue());
        Assert.assertEquals(NAME, recordSetListOption.getRpcOption());
        // page token
        recordSetListOption = RecordSetListOption.pageToken(DnsTest.PAGE_TOKEN);
        Assert.assertEquals(DnsTest.PAGE_TOKEN, recordSetListOption.getValue());
        Assert.assertEquals(DnsRpc.Option.PAGE_TOKEN, recordSetListOption.getRpcOption());
        // page size
        recordSetListOption = RecordSetListOption.pageSize(DnsTest.PAGE_SIZE);
        Assert.assertEquals(DnsTest.PAGE_SIZE, recordSetListOption.getValue());
        Assert.assertEquals(DnsRpc.Option.PAGE_SIZE, recordSetListOption.getRpcOption());
        // record type
        RecordSet.Type recordType = Type.AAAA;
        recordSetListOption = RecordSetListOption.type(recordType);
        Assert.assertEquals(recordType.name(), recordSetListOption.getValue());
        Assert.assertEquals(DNS_TYPE, recordSetListOption.getRpcOption());
        // fields
        recordSetListOption = RecordSetListOption.fields(Dns.RecordSetField.NAME, TTL);
        Assert.assertEquals(FIELDS, recordSetListOption.getRpcOption());
        Assert.assertTrue(((recordSetListOption.getValue()) instanceof String));
        Assert.assertTrue(((String) (recordSetListOption.getValue())).contains(Dns.RecordSetField.NAME.getSelector()));
        Assert.assertTrue(((String) (recordSetListOption.getValue())).contains(TTL.getSelector()));
        Assert.assertTrue(((String) (recordSetListOption.getValue())).contains(Dns.RecordSetField.NAME.getSelector()));
    }

    @Test
    public void testZoneOption() {
        Dns.ZoneOption fields = ZoneOption.fields(CREATION_TIME, DESCRIPTION);
        Assert.assertEquals(FIELDS, fields.getRpcOption());
        Assert.assertTrue(((fields.getValue()) instanceof String));
        Assert.assertTrue(((String) (fields.getValue())).contains(CREATION_TIME.getSelector()));
        Assert.assertTrue(((String) (fields.getValue())).contains(DESCRIPTION.getSelector()));
    }

    @Test
    public void testZoneList() {
        // fields
        Dns.ZoneListOption fields = ZoneListOption.fields(CREATION_TIME, DESCRIPTION);
        Assert.assertEquals(FIELDS, fields.getRpcOption());
        Assert.assertTrue(((fields.getValue()) instanceof String));
        Assert.assertTrue(((String) (fields.getValue())).contains(CREATION_TIME.getSelector()));
        Assert.assertTrue(((String) (fields.getValue())).contains(DESCRIPTION.getSelector()));
        Assert.assertTrue(((String) (fields.getValue())).contains(Dns.ZoneField.NAME.getSelector()));
        // page token
        Dns.ZoneListOption option = ZoneListOption.pageToken(DnsTest.PAGE_TOKEN);
        Assert.assertEquals(DnsTest.PAGE_TOKEN, option.getValue());
        Assert.assertEquals(DnsRpc.Option.PAGE_TOKEN, option.getRpcOption());
        // page size
        option = ZoneListOption.pageSize(DnsTest.PAGE_SIZE);
        Assert.assertEquals(DnsTest.PAGE_SIZE, option.getValue());
        Assert.assertEquals(DnsRpc.Option.PAGE_SIZE, option.getRpcOption());
        // dnsName filter
        option = ZoneListOption.dnsName(DnsTest.DNS_NAME);
        Assert.assertEquals(DnsTest.DNS_NAME, option.getValue());
        Assert.assertEquals(DnsRpc.Option.DNS_NAME, option.getRpcOption());
    }

    @Test
    public void testProjectGetOption() {
        // fields
        Dns.ProjectOption fields = ProjectOption.fields(QUOTA);
        Assert.assertEquals(FIELDS, fields.getRpcOption());
        Assert.assertTrue(((fields.getValue()) instanceof String));
        Assert.assertTrue(((String) (fields.getValue())).contains(QUOTA.getSelector()));
        Assert.assertTrue(((String) (fields.getValue())).contains(PROJECT_ID.getSelector()));
    }

    @Test
    public void testChangeRequestOption() {
        // fields
        Dns.ChangeRequestOption fields = ChangeRequestOption.fields(START_TIME, STATUS);
        Assert.assertEquals(FIELDS, fields.getRpcOption());
        Assert.assertTrue(((fields.getValue()) instanceof String));
        Assert.assertTrue(((String) (fields.getValue())).contains(START_TIME.getSelector()));
        Assert.assertTrue(((String) (fields.getValue())).contains(STATUS.getSelector()));
        Assert.assertTrue(((String) (fields.getValue())).contains(ID.getSelector()));
    }

    @Test
    public void testChangeRequestListOption() {
        // fields
        Dns.ChangeRequestListOption fields = ChangeRequestListOption.fields(START_TIME, STATUS);
        Assert.assertEquals(FIELDS, fields.getRpcOption());
        Assert.assertTrue(((fields.getValue()) instanceof String));
        Assert.assertTrue(((String) (fields.getValue())).contains(START_TIME.getSelector()));
        Assert.assertTrue(((String) (fields.getValue())).contains(STATUS.getSelector()));
        Assert.assertTrue(((String) (fields.getValue())).contains(ID.getSelector()));
        // page token
        Dns.ChangeRequestListOption option = ChangeRequestListOption.pageToken(DnsTest.PAGE_TOKEN);
        Assert.assertEquals(DnsTest.PAGE_TOKEN, option.getValue());
        Assert.assertEquals(DnsRpc.Option.PAGE_TOKEN, option.getRpcOption());
        // page size
        option = ChangeRequestListOption.pageSize(DnsTest.PAGE_SIZE);
        Assert.assertEquals(DnsTest.PAGE_SIZE, option.getValue());
        Assert.assertEquals(DnsRpc.Option.PAGE_SIZE, option.getRpcOption());
        // sort order
        option = ChangeRequestListOption.sortOrder(ASCENDING);
        Assert.assertEquals(SORTING_ORDER, option.getRpcOption());
        Assert.assertEquals(ASCENDING.selector(), option.getValue());
    }
}

