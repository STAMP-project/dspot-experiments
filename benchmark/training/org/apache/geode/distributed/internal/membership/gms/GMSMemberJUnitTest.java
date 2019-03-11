/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.distributed.internal.membership.gms;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import org.apache.geode.distributed.internal.membership.MemberAttributes;
import org.apache.geode.internal.Version;
import org.apache.geode.test.junit.categories.SecurityTest;
import org.jgroups.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SecurityTest.class })
public class GMSMemberJUnitTest {
    @Test
    public void testEqualsNotSameType() {
        GMSMember member = new GMSMember();
        Assert.assertFalse(member.equals("Not a GMSMember"));
    }

    @Test
    public void testEqualsIsSame() {
        GMSMember member = new GMSMember();
        Assert.assertTrue(member.equals(member));
    }

    @Test
    public void testCompareToIsSame() {
        GMSMember member = new GMSMember();
        UUID uuid = new UUID(0, 0);
        member.setUUID(uuid);
        Assert.assertEquals(0, member.compareTo(member));
    }

    @Test
    public void testCompareToInetAddressIsLongerThan() {
        GMSMember member1 = createGMSMember(new byte[]{ 1, 1, 1, 1, 1 }, 1, 1, 1);
        GMSMember member2 = createGMSMember(new byte[]{ 1, 1, 1, 1 }, 1, 1, 1);
        Assert.assertEquals(1, member1.compareTo(member2));
    }

    @Test
    public void testShallowMemberEquals() {
        GMSMember member1 = createGMSMember(new byte[]{ 1, 1, 1, 1, 1 }, 1, 1, 1);
        GMSMember member2 = new GMSMember(member1.getInetAddress(), member1.getPort(), member1.getVersionOrdinal(), member1.getUuidMSBs(), member1.getUuidLSBs(), member1.getVmViewId());
        Assert.assertEquals(0, member1.compareTo(member2));
    }

    @Test
    public void testShallowMemberNotEquals() {
        GMSMember member1 = createGMSMember(new byte[]{ 1, 1, 1, 1, 1 }, 1, 1, 1);
        GMSMember member2 = new GMSMember(member1.getInetAddress(), member1.getPort(), member1.getVersionOrdinal(), member1.getUuidMSBs(), member1.getUuidLSBs(), 100);
        Assert.assertEquals(false, member1.equals(member2));
    }

    @Test
    public void testCompareToInetAddressIsShorterThan() {
        GMSMember member1 = createGMSMember(new byte[]{ 1, 1, 1, 1 }, 1, 1, 1);
        GMSMember member2 = createGMSMember(new byte[]{ 1, 1, 1, 1, 1 }, 1, 1, 1);
        Assert.assertEquals((-1), member1.compareTo(member2));
    }

    @Test
    public void testCompareToInetAddressIsGreater() {
        GMSMember member1 = createGMSMember(new byte[]{ 1, 2, 1, 1, 1 }, 1, 1, 1);
        GMSMember member2 = createGMSMember(new byte[]{ 1, 1, 1, 1, 1 }, 1, 1, 1);
        Assert.assertEquals(1, member1.compareTo(member2));
    }

    @Test
    public void testCompareToInetAddressIsLessThan() {
        GMSMember member1 = createGMSMember(new byte[]{ 1, 1, 1, 1, 1 }, 1, 1, 1);
        GMSMember member2 = createGMSMember(new byte[]{ 1, 2, 1, 1, 1 }, 1, 1, 1);
        Assert.assertEquals((-1), member1.compareTo(member2));
    }

    @Test
    public void testCompareToMyViewIdLarger() {
        GMSMember member1 = createGMSMember(new byte[]{ 1 }, 2, 1, 1);
        GMSMember member2 = createGMSMember(new byte[]{ 1 }, 1, 1, 1);
        Assert.assertEquals(1, member1.compareTo(member2));
    }

    @Test
    public void testCompareToTheirViewIdLarger() {
        GMSMember member1 = createGMSMember(new byte[]{ 1 }, 1, 1, 1);
        GMSMember member2 = createGMSMember(new byte[]{ 1 }, 2, 1, 1);
        Assert.assertEquals((-1), member1.compareTo(member2));
    }

    @Test
    public void testCompareToMyMSBLarger() {
        GMSMember member1 = createGMSMember(new byte[]{ 1 }, 1, 2, 1);
        GMSMember member2 = createGMSMember(new byte[]{ 1 }, 1, 1, 1);
        Assert.assertEquals(1, member1.compareTo(member2));
    }

    @Test
    public void testCompareToTheirMSBLarger() {
        GMSMember member1 = createGMSMember(new byte[]{ 1 }, 1, 1, 1);
        GMSMember member2 = createGMSMember(new byte[]{ 1 }, 1, 2, 1);
        Assert.assertEquals((-1), member1.compareTo(member2));
    }

    @Test
    public void testCompareToMyLSBLarger() {
        GMSMember member1 = createGMSMember(new byte[]{ 1 }, 1, 1, 2);
        GMSMember member2 = createGMSMember(new byte[]{ 1 }, 1, 1, 1);
        Assert.assertEquals(1, member1.compareTo(member2));
    }

    @Test
    public void testCompareToTheirLSBLarger() {
        GMSMember member1 = createGMSMember(new byte[]{ 1 }, 1, 1, 1);
        GMSMember member2 = createGMSMember(new byte[]{ 1 }, 1, 1, 2);
        Assert.assertEquals((-1), member1.compareTo(member2));
    }

    /**
     * Makes sure a NPE is not thrown
     */
    @Test
    public void testNoNPEWhenSetAttributesWithNull() {
        GMSMember member = new GMSMember();
        member.setAttributes(null);
        MemberAttributes attrs = member.getAttributes();
        MemberAttributes invalid = MemberAttributes.INVALID;
        Assert.assertEquals(attrs.getVmKind(), invalid.getVmKind());
        Assert.assertEquals(attrs.getPort(), invalid.getPort());
        Assert.assertEquals(attrs.getVmViewId(), invalid.getVmViewId());
        Assert.assertEquals(attrs.getName(), invalid.getName());
    }

    @Test
    public void testGetUUIDReturnsNullWhenUUIDIs0() {
        GMSMember member = new GMSMember();
        UUID uuid = new UUID(0, 0);
        member.setUUID(uuid);
        Assert.assertNull(member.getUUID());
    }

    @Test
    public void testGetUUID() {
        GMSMember member = new GMSMember();
        UUID uuid = new UUID(1, 1);
        member.setUUID(uuid);
        Assert.assertNotNull(member.getUUID());
    }

    /**
     * <p>
     * GEODE-2875 - adds vmKind to on-wire form of GMSMember.writeEssentialData
     * </p>
     * <p>
     * This must be backward-compatible with Geode 1.0 (Version.GFE_90)
     * </p>
     */
    @Test
    public void testGMSMemberBackwardCompatibility() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MemberAttributes attributes = new MemberAttributes(10, 20, 1, 2, "member", null, null);
        GMSMember member = new GMSMember();
        member.setAttributes(attributes);
        DataOutput dataOutput = new DataOutputStream(baos);
        member.writeEssentialData(dataOutput);
        // vmKind should be transmitted to a member with the current version
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput dataInput = new DataInputStream(bais);
        GMSMember newMember = new GMSMember();
        newMember.readEssentialData(dataInput);
        Assert.assertEquals(1, newMember.getVmKind());
        // vmKind should not be transmitted to a member with version GFE_90 or earlier
        dataOutput = new org.apache.geode.internal.HeapDataOutputStream(Version.GFE_90);
        member.writeEssentialData(dataOutput);
        bais = new ByteArrayInputStream(baos.toByteArray());
        dataInput = new org.apache.geode.internal.VersionedDataInputStream(new DataInputStream(bais), Version.GFE_90);
        newMember = new GMSMember();
        newMember.readEssentialData(dataInput);
        Assert.assertEquals(0, newMember.getVmKind());
    }
}

