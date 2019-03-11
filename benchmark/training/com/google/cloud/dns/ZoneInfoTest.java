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


import com.google.api.services.dns.model.ManagedZone;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ZoneInfoTest {
    private static final String NAME = "mz-example.com";

    private static final String GENERATED_ID = "123456";

    private static final Long CREATION_TIME_MILLIS = 1123468321321L;

    private static final String DNS_NAME = "example.com.";

    private static final String DESCRIPTION = "description for the zone";

    private static final String NAME_SERVER_SET = "some set";

    private static final String NS1 = "name server 1";

    private static final String NS2 = "name server 2";

    private static final String NS3 = "name server 3";

    private static final List<String> NAME_SERVERS = ImmutableList.of(ZoneInfoTest.NS1, ZoneInfoTest.NS2, ZoneInfoTest.NS3);

    private static final ZoneInfo INFO = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION).toBuilder().setCreationTimeMillis(ZoneInfoTest.CREATION_TIME_MILLIS).setGeneratedId(ZoneInfoTest.GENERATED_ID).setNameServerSet(ZoneInfoTest.NAME_SERVER_SET).setNameServers(ZoneInfoTest.NAME_SERVERS).build();

    @Test
    public void testOf() {
        ZoneInfo partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION);
        Assert.assertTrue(partial.getNameServers().isEmpty());
        Assert.assertEquals(ZoneInfoTest.NAME, partial.getName());
        Assert.assertNull(partial.getGeneratedId());
        Assert.assertNull(partial.getCreationTimeMillis());
        Assert.assertNull(partial.getNameServerSet());
        Assert.assertEquals(ZoneInfoTest.DESCRIPTION, partial.getDescription());
        Assert.assertEquals(ZoneInfoTest.DNS_NAME, partial.getDnsName());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(3, ZoneInfoTest.INFO.getNameServers().size());
        Assert.assertEquals(ZoneInfoTest.NS1, ZoneInfoTest.INFO.getNameServers().get(0));
        Assert.assertEquals(ZoneInfoTest.NS2, ZoneInfoTest.INFO.getNameServers().get(1));
        Assert.assertEquals(ZoneInfoTest.NS3, ZoneInfoTest.INFO.getNameServers().get(2));
        Assert.assertEquals(ZoneInfoTest.NAME, ZoneInfoTest.INFO.getName());
        Assert.assertEquals(ZoneInfoTest.GENERATED_ID, ZoneInfoTest.INFO.getGeneratedId());
        Assert.assertEquals(ZoneInfoTest.CREATION_TIME_MILLIS, ZoneInfoTest.INFO.getCreationTimeMillis());
        Assert.assertEquals(ZoneInfoTest.NAME_SERVER_SET, ZoneInfoTest.INFO.getNameServerSet());
        Assert.assertEquals(ZoneInfoTest.DESCRIPTION, ZoneInfoTest.INFO.getDescription());
        Assert.assertEquals(ZoneInfoTest.DNS_NAME, ZoneInfoTest.INFO.getDnsName());
    }

    @Test
    public void testEqualsAndNotEquals() {
        ZoneInfo clone = ZoneInfoTest.INFO.toBuilder().build();
        Assert.assertEquals(ZoneInfoTest.INFO, clone);
        List<String> moreServers = Lists.newLinkedList(ZoneInfoTest.NAME_SERVERS);
        moreServers.add(ZoneInfoTest.NS1);
        clone = ZoneInfoTest.INFO.toBuilder().setNameServers(moreServers).build();
        Assert.assertNotEquals(ZoneInfoTest.INFO, clone);
        String differentName = "totally different name";
        clone = ZoneInfoTest.INFO.toBuilder().setName(differentName).build();
        Assert.assertNotEquals(ZoneInfoTest.INFO, clone);
        clone = ZoneInfoTest.INFO.toBuilder().setCreationTimeMillis(((ZoneInfoTest.INFO.getCreationTimeMillis()) + 1)).build();
        Assert.assertNotEquals(ZoneInfoTest.INFO, clone);
        clone = ZoneInfoTest.INFO.toBuilder().setDescription(((ZoneInfoTest.INFO.getDescription()) + "aaaa")).build();
        Assert.assertNotEquals(ZoneInfoTest.INFO, clone);
        clone = ZoneInfoTest.INFO.toBuilder().setDnsName(differentName).build();
        Assert.assertNotEquals(ZoneInfoTest.INFO, clone);
        clone = ZoneInfoTest.INFO.toBuilder().setGeneratedId(((ZoneInfoTest.INFO.getGeneratedId()) + "1111")).build();
        Assert.assertNotEquals(ZoneInfoTest.INFO, clone);
        clone = ZoneInfoTest.INFO.toBuilder().setNameServerSet(((ZoneInfoTest.INFO.getNameServerSet()) + "salt")).build();
        Assert.assertNotEquals(ZoneInfoTest.INFO, clone);
    }

    @Test
    public void testSameHashCodeOnEquals() {
        int hash = ZoneInfoTest.INFO.hashCode();
        ZoneInfo clone = ZoneInfoTest.INFO.toBuilder().build();
        Assert.assertEquals(clone.hashCode(), hash);
    }

    @Test
    public void testToBuilder() {
        Assert.assertEquals(ZoneInfoTest.INFO, ZoneInfoTest.INFO.toBuilder().build());
        ZoneInfo partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION);
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION).toBuilder().setGeneratedId(ZoneInfoTest.GENERATED_ID).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION).toBuilder().setCreationTimeMillis(ZoneInfoTest.CREATION_TIME_MILLIS).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        List<String> nameServers = new LinkedList<>();
        nameServers.add(ZoneInfoTest.NS1);
        partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION).toBuilder().setNameServers(nameServers).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
        partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION).toBuilder().setNameServerSet(ZoneInfoTest.NAME_SERVER_SET).build();
        Assert.assertEquals(partial, partial.toBuilder().build());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertEquals(ZoneInfoTest.INFO, ZoneInfo.fromPb(ZoneInfoTest.INFO.toPb()));
        ZoneInfo partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION);
        Assert.assertEquals(partial, ZoneInfo.fromPb(partial.toPb()));
        partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION).toBuilder().setGeneratedId(ZoneInfoTest.GENERATED_ID).build();
        Assert.assertEquals(partial, ZoneInfo.fromPb(partial.toPb()));
        partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION).toBuilder().setCreationTimeMillis(ZoneInfoTest.CREATION_TIME_MILLIS).build();
        Assert.assertEquals(partial, ZoneInfo.fromPb(partial.toPb()));
        List<String> nameServers = new LinkedList<>();
        nameServers.add(ZoneInfoTest.NS1);
        partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION).toBuilder().setNameServers(nameServers).build();
        Assert.assertEquals(partial, ZoneInfo.fromPb(partial.toPb()));
        partial = ZoneInfo.of(ZoneInfoTest.NAME, ZoneInfoTest.DNS_NAME, ZoneInfoTest.DESCRIPTION).toBuilder().setNameServerSet(ZoneInfoTest.NAME_SERVER_SET).build();
        Assert.assertEquals(partial, ZoneInfo.fromPb(partial.toPb()));
    }

    @Test
    public void testEmptyNameServers() {
        ZoneInfo clone = ZoneInfoTest.INFO.toBuilder().setNameServers(new LinkedList<String>()).build();
        Assert.assertTrue(clone.getNameServers().isEmpty());
        clone.toPb();// test that this is allowed

    }

    @Test
    public void testDateParsing() {
        ManagedZone pb = ZoneInfoTest.INFO.toPb();
        pb.setCreationTime("2016-01-19T18:00:12.854Z");// a real value obtained from Google Cloud DNS

        ZoneInfo mz = ZoneInfo.fromPb(pb);// parses the string timestamp to millis

        ManagedZone pbClone = mz.toPb();// converts it back to string

        Assert.assertEquals(pb, pbClone);
        Assert.assertEquals(pb.getCreationTime(), pbClone.getCreationTime());
    }
}

