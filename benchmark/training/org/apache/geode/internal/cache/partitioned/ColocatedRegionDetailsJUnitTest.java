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
package org.apache.geode.internal.cache.partitioned;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import org.junit.Assert;
import org.junit.Test;


public class ColocatedRegionDetailsJUnitTest {
    @Test
    public void testColocatedRegionDetailsConstructor() {
        ColocatedRegionDetails crd = new ColocatedRegionDetails("host", "member name", "parent region", "child region");
        Assert.assertNotNull(crd);
        Assert.assertEquals("host", crd.getHost());
        Assert.assertEquals("member name", crd.getMember());
        Assert.assertEquals("parent region", crd.getParent());
        Assert.assertEquals("child region", crd.getChild());
    }

    @Test
    public void testColocatedRegion0ArgConstructor() {
        ColocatedRegionDetails crd = new ColocatedRegionDetails();
        Assert.assertNotNull(crd);
        Assert.assertNull(crd.getHost());
        Assert.assertNull(crd.getMember());
        Assert.assertNull(crd.getParent());
        Assert.assertNull(crd.getChild());
    }

    @Test
    public void testConstructingWithNulls() {
        ColocatedRegionDetails crd1 = new ColocatedRegionDetails(null, "member name", "parent region", "child region");
        ColocatedRegionDetails crd2 = new ColocatedRegionDetails("host", null, "parent region", "child region");
        ColocatedRegionDetails crd3 = new ColocatedRegionDetails("host", "member name", null, "child region");
        ColocatedRegionDetails crd4 = new ColocatedRegionDetails("host", "member name", "parent region", null);
        Assert.assertNotNull(crd1);
        Assert.assertNotNull(crd2);
        Assert.assertNotNull(crd3);
        Assert.assertNotNull(crd4);
    }

    @Test
    public void testSerialization() throws Exception {
        ColocatedRegionDetails crd = new ColocatedRegionDetails("host", "member name", "parent region", "child region");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        crd.toData(out);
        ColocatedRegionDetails crdIn = new ColocatedRegionDetails();
        crdIn.fromData(new DataInputStream(new ByteArrayInputStream(baos.toByteArray())));
        Assert.assertEquals(crd, crdIn);
    }

    @Test
    public void testSerializationOfEmptyColocatedRegionDetails() throws Exception {
        ColocatedRegionDetails crd = new ColocatedRegionDetails();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        crd.toData(out);
        ColocatedRegionDetails crdIn = new ColocatedRegionDetails();
        crdIn.fromData(new DataInputStream(new ByteArrayInputStream(baos.toByteArray())));
        Assert.assertEquals(crd, crdIn);
    }

    @Test
    public void testHostNotEquals() {
        ColocatedRegionDetails crd1 = new ColocatedRegionDetails();
        ColocatedRegionDetails crd2 = new ColocatedRegionDetails("host1", "member name", "parent region", "child region");
        ColocatedRegionDetails crd3 = new ColocatedRegionDetails("host2", "member name", "parent region", "child region");
        Assert.assertNotEquals(crd1, crd2);
        Assert.assertNotEquals(crd2, crd3);
        Assert.assertNotEquals(crd3, crd2);
    }

    @Test
    public void testMemberNotEquals() {
        ColocatedRegionDetails crd1 = new ColocatedRegionDetails("host", null, "parent region", "child region");
        ColocatedRegionDetails crd2 = new ColocatedRegionDetails("host", "member1", "parent region", "child region");
        ColocatedRegionDetails crd3 = new ColocatedRegionDetails("host", "member2", "parent region", "child region");
        Assert.assertNotEquals(crd1, crd2);
        Assert.assertNotEquals(crd2, crd3);
        Assert.assertNotEquals(crd3, crd2);
    }

    @Test
    public void testParentNotEquals() {
        ColocatedRegionDetails crd1 = new ColocatedRegionDetails("host", "member1", null, "child region");
        ColocatedRegionDetails crd2 = new ColocatedRegionDetails("host", "member1", "parent1", "child region");
        ColocatedRegionDetails crd3 = new ColocatedRegionDetails("host", "member1", "parent2", "child region");
        Assert.assertNotEquals(crd1, crd2);
        Assert.assertNotEquals(crd2, crd3);
        Assert.assertNotEquals(crd3, crd2);
    }

    @Test
    public void testChildNotEquals() {
        ColocatedRegionDetails crd1 = new ColocatedRegionDetails("host", "member1", "parent region", null);
        ColocatedRegionDetails crd2 = new ColocatedRegionDetails("host", "member1", "parent region", "child1");
        ColocatedRegionDetails crd3 = new ColocatedRegionDetails("host", "member1", "parent region", "child2");
        Assert.assertNotEquals(crd1, crd2);
        Assert.assertNotEquals(crd2, crd3);
        Assert.assertNotEquals(crd3, crd2);
    }

    @Test
    public void testClassInequality() {
        ColocatedRegionDetails crd1 = new ColocatedRegionDetails("host", "member1", "parent region", null);
        String crd2 = crd1.toString();
        Assert.assertNotEquals(crd1, crd2);
        Assert.assertNotEquals(crd2, crd1);
    }

    @Test
    public void nullColocatedRegionDetailsEqualsTests() {
        ColocatedRegionDetails crd1 = null;
        ColocatedRegionDetails crd2 = new ColocatedRegionDetails("host", "member1", "parent region", "child1");
        Assert.assertEquals(crd1, crd1);
        Assert.assertEquals(crd2, crd2);
        Assert.assertNotEquals(crd1, crd2);
        Assert.assertNotEquals(crd2, crd1);
    }

    @Test
    public void testToString() {
        ColocatedRegionDetails crd = new ColocatedRegionDetails("host1", "member name", "parent region", "child region");
        Assert.assertEquals("[host:host1, member:member name, parent:parent region, child:child region]", crd.toString());
    }

    @Test
    public void testToStringOfEmptyColocatedRegionDetails() {
        ColocatedRegionDetails crd = new ColocatedRegionDetails();
        Assert.assertEquals("[,,,]", crd.toString());
    }

    @Test
    public void testHashCode() {
        ColocatedRegionDetails crd1 = new ColocatedRegionDetails();
        ColocatedRegionDetails crd2 = new ColocatedRegionDetails("host1", "member name", "parent region", "child region");
        ColocatedRegionDetails crd3 = new ColocatedRegionDetails("host2", "member name", "parent region", "child region");
        Assert.assertNotEquals(crd1.hashCode(), crd2.hashCode());
        Assert.assertNotEquals(crd1.hashCode(), crd3.hashCode());
        Assert.assertNotEquals(crd2.hashCode(), crd3.hashCode());
        Assert.assertEquals(923521, crd1.hashCode());
        Assert.assertEquals(2077348855, crd2.hashCode());
        Assert.assertEquals(2077378646, crd3.hashCode());
    }
}

