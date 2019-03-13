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
package com.hazelcast.client.impl;


import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
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
    private static final MemberVersion VERSION = MemberVersion.of("1.3.2");

    private static Address address;

    @Test
    public void testConstructor() {
        MemberImpl member = new MemberImpl();
        Assert.assertNull(member.getAddress());
        Assert.assertFalse(member.localMember());
        Assert.assertFalse(member.isLiteMember());
    }

    @Test
    public void testConstructor_withAddress() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertFalse(member.isLiteMember());
    }

    @Test
    public void testConstructor_withAddressAndUUid() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION, "uuid2342");
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertEquals("uuid2342", member.getUuid());
        Assert.assertFalse(member.isLiteMember());
    }

    @Test
    public void testConstructor_withAttributes() throws Exception {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("stringKey", "value");
        attributes.put("booleanKeyTrue", true);
        attributes.put("booleanKeyFalse", false);
        attributes.put("byteKey", Byte.MAX_VALUE);
        attributes.put("shortKey", Short.MAX_VALUE);
        attributes.put("intKey", Integer.MAX_VALUE);
        attributes.put("longKey", Long.MAX_VALUE);
        attributes.put("floatKey", Float.MAX_VALUE);
        attributes.put("doubleKey", Double.MAX_VALUE);
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION, "uuid2342", attributes, true);
        MemberImplTest.assertBasicMemberImplFields(member);
        Assert.assertEquals("uuid2342", member.getUuid());
        Assert.assertTrue(member.isLiteMember());
        Assert.assertEquals("value", member.getStringAttribute("stringKey"));
        Assert.assertNull(member.getBooleanAttribute("booleanKey"));
        Boolean booleanValueTrue = member.getBooleanAttribute("booleanKeyTrue");
        Assert.assertNotNull(booleanValueTrue);
        Assert.assertTrue(booleanValueTrue);
        Boolean booleanValueFalse = member.getBooleanAttribute("booleanKeyFalse");
        Assert.assertNotNull(booleanValueFalse);
        Assert.assertFalse(booleanValueFalse);
        Byte byteValue = member.getByteAttribute("byteKey");
        Assert.assertNotNull(byteValue);
        Assert.assertEquals(Byte.MAX_VALUE, byteValue.byteValue());
        Short shortValue = member.getShortAttribute("shortKey");
        Assert.assertNotNull(shortValue);
        Assert.assertEquals(Short.MAX_VALUE, shortValue.shortValue());
        Integer intValue = member.getIntAttribute("intKey");
        Assert.assertNotNull(intValue);
        Assert.assertEquals(Integer.MAX_VALUE, intValue.intValue());
        Long longValue = member.getLongAttribute("longKey");
        Assert.assertNotNull(longValue);
        Assert.assertEquals(Long.MAX_VALUE, longValue.longValue());
        Float floatValue = member.getFloatAttribute("floatKey");
        Assert.assertNotNull(floatValue);
        Assert.assertEquals(Float.MAX_VALUE, floatValue, 1.0E-6);
        Double doubleValue = member.getDoubleAttribute("doubleKey");
        Assert.assertNotNull(doubleValue);
        Assert.assertEquals(Double.MAX_VALUE, doubleValue, 1.0E-6);
    }

    @Test
    public void testConstructor_withMemberImpl() {
        MemberImpl member = new MemberImpl(new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION));
        MemberImplTest.assertBasicMemberImplFields(member);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetStringAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        member.setStringAttribute("stringKey", "stringValue");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetBooleanAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        member.setBooleanAttribute("booleanKeyTrue", true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetByteAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        member.setByteAttribute("byteKey", Byte.MAX_VALUE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetShortAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        member.setShortAttribute("shortKey", Short.MAX_VALUE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetIntAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        member.setIntAttribute("intKey", Integer.MAX_VALUE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetLongAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        member.setLongAttribute("longKey", Long.MAX_VALUE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetFloatAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        member.setFloatAttribute("floatKey", Float.MAX_VALUE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetDoubleAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        member.setDoubleAttribute("doubleKey", Double.MAX_VALUE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAttribute() {
        MemberImpl member = new MemberImpl(MemberImplTest.address, MemberImplTest.VERSION);
        member.removeAttribute("removeKey");
    }
}

