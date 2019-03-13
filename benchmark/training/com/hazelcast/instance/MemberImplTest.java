/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.instance;


import EndpointQualifier.MEMBER;
import EndpointQualifier.REST;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.version.MemberVersion;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MemberImplTest extends HazelcastTestSupport {
    private static HazelcastInstanceImpl hazelcastInstance;

    private static Address address;

    @Test
    public void testConstructor_withLocalMember_isTrue() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertTrue(member.localMember());
    }

    @Test
    public void testConstructor_withLocalMember_isFalse() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), false);
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertFalse(member.localMember());
    }

    @Test
    public void testConstructor_withLiteMember_isTrue() {
        MemberImpl member = new MemberImpl.Builder(MemberImplTest.address).version(MemberVersion.of("3.8.0")).localMember(true).uuid(UuidUtil.newUnsecureUuidString()).liteMember(true).build();
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertTrue(member.localMember());
        Assert.assertTrue(member.isLiteMember());
    }

    @Test
    public void testConstructor_withLiteMember_isFalse() {
        MemberImpl member = new MemberImpl.Builder(MemberImplTest.address).version(MemberVersion.of("3.8.0")).localMember(true).uuid(UuidUtil.newUnsecureUuidString()).build();
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertTrue(member.localMember());
        Assert.assertFalse(member.isLiteMember());
    }

    @Test
    public void testConstructor_withHazelcastInstance() throws Exception {
        MemberImpl member = new MemberImpl.Builder(MemberImplTest.address).version(MemberVersion.of("3.8.0")).localMember(true).uuid("uuid2342").instance(MemberImplTest.hazelcastInstance).build();
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertTrue(member.localMember());
        Assert.assertEquals("uuid2342", member.getUuid());
    }

    @Test
    public void testConstructor_withAttributes() throws Exception {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("key1", "value");
        attributes.put("key2", 12345);
        MemberImpl member = new MemberImpl.Builder(MemberImplTest.address).version(MemberVersion.of("3.8.0")).localMember(true).uuid("uuid2342").attributes(attributes).instance(MemberImplTest.hazelcastInstance).build();
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertTrue(member.localMember());
        Assert.assertEquals("uuid2342", member.getUuid());
        Assert.assertEquals("value", member.getAttribute("key1"));
        Assert.assertEquals(12345, member.getAttribute("key2"));
        Assert.assertFalse(member.isLiteMember());
    }

    @Test
    public void testConstructor_withMemberImpl() {
        MemberImpl member = new MemberImpl(new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true));
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertTrue(member.localMember());
    }

    @Test
    public void testSetHazelcastInstance() throws Exception {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getLogger());
        member.setHazelcastInstance(MemberImplTest.hazelcastInstance);
        Assert.assertNotNull(member.getLogger());
    }

    @Test
    public void testStringAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getStringAttribute("stringKey"));
        member.setStringAttribute("stringKey", "stringValue");
        Assert.assertEquals("stringValue", member.getStringAttribute("stringKey"));
    }

    @Test
    public void testBooleanAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getBooleanAttribute("booleanKeyTrue"));
        Assert.assertNull(member.getBooleanAttribute("booleanKeyFalse"));
        member.setBooleanAttribute("booleanKeyTrue", true);
        Assert.assertTrue(member.getBooleanAttribute("booleanKeyTrue"));
        member.setBooleanAttribute("booleanKeyFalse", false);
        Assert.assertFalse(member.getBooleanAttribute("booleanKeyFalse"));
    }

    @Test
    public void testByteAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getByteAttribute("byteKey"));
        Byte value = Byte.MAX_VALUE;
        member.setByteAttribute("byteKey", value);
        Assert.assertEquals(value, member.getByteAttribute("byteKey"));
    }

    @Test
    public void testShortAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getShortAttribute("shortKey"));
        Short value = Short.MAX_VALUE;
        member.setShortAttribute("shortKey", value);
        Assert.assertEquals(value, member.getShortAttribute("shortKey"));
    }

    @Test
    public void testIntAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getIntAttribute("intKey"));
        Integer value = Integer.MAX_VALUE;
        member.setIntAttribute("intKey", value);
        Assert.assertEquals(value, member.getIntAttribute("intKey"));
    }

    @Test
    public void testLongAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getLongAttribute("longKey"));
        Long value = Long.MAX_VALUE;
        member.setLongAttribute("longKey", value);
        Assert.assertEquals(value, member.getLongAttribute("longKey"));
    }

    @Test
    public void testFloatAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getFloatAttribute("floatKey"));
        Float value = Float.MAX_VALUE;
        member.setFloatAttribute("floatKey", value);
        Assert.assertEquals(value, member.getFloatAttribute("floatKey"), 0.001);
    }

    @Test
    public void testDoubleAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getDoubleAttribute("doubleKey"));
        Double value = Double.MAX_VALUE;
        member.setDoubleAttribute("doubleKey", value);
        Assert.assertEquals(value, member.getDoubleAttribute("doubleKey"), 0.001);
    }

    @Test
    public void testRemoveAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), true);
        Assert.assertNull(member.getStringAttribute("removeKey"));
        member.setStringAttribute("removeKey", "removeValue");
        Assert.assertEquals("removeValue", member.getStringAttribute("removeKey"));
        member.removeAttribute("removeKey");
        Assert.assertNull(member.getStringAttribute("removeKey"));
    }

    @Test
    public void testRemoveAttribute_withHazelcastInstance() {
        MemberImpl member = new MemberImpl.Builder(MemberImplTest.address).version(MemberVersion.of("3.8.0")).localMember(true).uuid("uuid").instance(MemberImplTest.hazelcastInstance).build();
        member.removeAttribute("removeKeyWithInstance");
        Assert.assertNull(member.getStringAttribute("removeKeyWithInstance"));
    }

    @Test
    public void testSetAttribute_withHazelcastInstance() {
        MemberImpl member = new MemberImpl.Builder(MemberImplTest.address).version(MemberVersion.of("3.8.0")).localMember(true).uuid("uuid").instance(MemberImplTest.hazelcastInstance).build();
        member.setStringAttribute("setKeyWithInstance", "setValueWithInstance");
        Assert.assertEquals("setValueWithInstance", member.getStringAttribute("setKeyWithInstance"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAttribute_onRemoteMember() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), false);
        member.removeAttribute("remoteMemberRemove");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetAttribute_onRemoteMember() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.8.0"), false);
        member.setStringAttribute("remoteMemberSet", "wontWork");
    }

    @Test
    public void testSerialization_whenSingleAddress() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberVersion.of("3.12.0"), false);
        testSerialization(member);
    }

    @Test
    public void testSerialization_whenMultiAddress() throws Exception {
        Map<EndpointQualifier, Address> addressMap = new HashMap<EndpointQualifier, Address>();
        addressMap.put(MEMBER, MemberImplTest.address);
        addressMap.put(REST, new Address("127.0.0.1", 8080));
        MemberImpl member = new MemberImpl.Builder(addressMap).version(MemberVersion.of("3.12.0")).build();
        testSerialization(member);
    }
}

