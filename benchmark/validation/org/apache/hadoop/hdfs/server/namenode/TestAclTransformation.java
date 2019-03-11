/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.namenode;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.hdfs.protocol.AclException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests operations that modify ACLs.  All tests in this suite have been
 * cross-validated against Linux setfacl/getfacl to check for consistency of the
 * HDFS implementation.
 */
public class TestAclTransformation {
    private static final List<AclEntry> ACL_SPEC_TOO_LARGE;

    private static final List<AclEntry> ACL_SPEC_DEFAULT_TOO_LARGE;

    static {
        ACL_SPEC_TOO_LARGE = Lists.newArrayListWithCapacity(33);
        ACL_SPEC_DEFAULT_TOO_LARGE = Lists.newArrayListWithCapacity(33);
        for (int i = 0; i < 33; ++i) {
            TestAclTransformation.ACL_SPEC_TOO_LARGE.add(AclTestHelpers.aclEntry(ACCESS, USER, ("user" + i), ALL));
            TestAclTransformation.ACL_SPEC_DEFAULT_TOO_LARGE.add(AclTestHelpers.aclEntry(DEFAULT, USER, ("user" + i), ALL));
        }
    }

    @Test
    public void testFilterAclEntriesByAclSpec() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "execs", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "diana"), AclTestHelpers.aclEntry(ACCESS, GROUP, "sales"));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "execs", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        Assert.assertEquals(expected, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test
    public void testFilterAclEntriesByAclSpecUnchanged() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "clark"), AclTestHelpers.aclEntry(ACCESS, GROUP, "execs"));
        Assert.assertEquals(existing, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test
    public void testFilterAclEntriesByAclSpecAccessMaskCalculated() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "diana"));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        Assert.assertEquals(expected, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test
    public void testFilterAclEntriesByAclSpecDefaultMaskCalculated() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "diana"));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test
    public void testFilterAclEntriesByAclSpecDefaultMaskPreserved() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "diana"));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test
    public void testFilterAclEntriesByAclSpecAccessMaskPreserved() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "diana"));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test
    public void testFilterAclEntriesByAclSpecAutomaticDefaultUser() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test
    public void testFilterAclEntriesByAclSpecAutomaticDefaultGroup() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, GROUP));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test
    public void testFilterAclEntriesByAclSpecAutomaticDefaultOther() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, OTHER));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, READ)).build();
        Assert.assertEquals(expected, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test
    public void testFilterAclEntriesByAclSpecEmptyAclSpec() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, READ)).build();
        List<AclEntry> aclSpec = Lists.newArrayList();
        Assert.assertEquals(existing, filterAclEntriesByAclSpec(existing, aclSpec));
    }

    @Test(expected = AclException.class)
    public void testFilterAclEntriesByAclSpecRemoveAccessMaskRequired() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, MASK));
        filterAclEntriesByAclSpec(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testFilterAclEntriesByAclSpecRemoveDefaultMaskRequired() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, MASK));
        filterAclEntriesByAclSpec(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testFilterAclEntriesByAclSpecInputTooLarge() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        filterAclEntriesByAclSpec(existing, TestAclTransformation.ACL_SPEC_TOO_LARGE);
    }

    @Test(expected = AclException.class)
    public void testFilterDefaultAclEntriesByAclSpecInputTooLarge() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        filterAclEntriesByAclSpec(existing, TestAclTransformation.ACL_SPEC_DEFAULT_TOO_LARGE);
    }

    @Test
    public void testFilterDefaultAclEntries() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, "sales", READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, READ_EXECUTE)).build();
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        Assert.assertEquals(expected, filterDefaultAclEntries(existing));
    }

    @Test
    public void testFilterDefaultAclEntriesUnchanged() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        Assert.assertEquals(existing, filterDefaultAclEntries(existing));
    }

    @Test
    public void testMergeAclEntries() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesUnchanged() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, "sales", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL), AclTestHelpers.aclEntry(ACCESS, MASK, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, "sales", ALL), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        Assert.assertEquals(existing, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesMultipleNewBeforeExisting() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, USER, "clark", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_EXECUTE));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, USER, "clark", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesAccessMaskCalculated() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesDefaultMaskCalculated() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_EXECUTE));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesDefaultMaskPreserved() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "diana", FsAction.READ_EXECUTE));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesAccessMaskPreserved() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_EXECUTE));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesAutomaticDefaultUser() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, READ));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, READ)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesAutomaticDefaultGroup() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, READ));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, READ)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesAutomaticDefaultOther() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesProvidedAccessMask() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, MASK, ALL));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesProvidedDefaultMask() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, mergeAclEntries(existing, aclSpec));
    }

    @Test
    public void testMergeAclEntriesEmptyAclSpec() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, READ)).build();
        List<AclEntry> aclSpec = Lists.newArrayList();
        Assert.assertEquals(existing, mergeAclEntries(existing, aclSpec));
    }

    @Test(expected = AclException.class)
    public void testMergeAclEntriesInputTooLarge() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        mergeAclEntries(existing, TestAclTransformation.ACL_SPEC_TOO_LARGE);
    }

    @Test(expected = AclException.class)
    public void testMergeAclDefaultEntriesInputTooLarge() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        mergeAclEntries(existing, TestAclTransformation.ACL_SPEC_DEFAULT_TOO_LARGE);
    }

    @Test(expected = AclException.class)
    public void testMergeAclEntriesResultTooLarge() throws AclException {
        ImmutableList.Builder<AclEntry> aclBuilder = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL));
        for (int i = 1; i <= 28; ++i) {
            aclBuilder.add(AclTestHelpers.aclEntry(ACCESS, USER, ("user" + i), READ));
        }
        aclBuilder.add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        List<AclEntry> existing = aclBuilder.build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ));
        mergeAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testMergeAclDefaultEntriesResultTooLarge() throws AclException {
        ImmutableList.Builder<AclEntry> aclBuilder = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL));
        for (int i = 1; i <= 28; ++i) {
            aclBuilder.add(AclTestHelpers.aclEntry(DEFAULT, USER, ("user" + i), READ));
        }
        aclBuilder.add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        List<AclEntry> existing = aclBuilder.build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ));
        mergeAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testMergeAclEntriesDuplicateEntries() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, "clark", READ), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE));
        mergeAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testMergeAclEntriesNamedMask() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, MASK, "bruce", READ_EXECUTE));
        mergeAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testMergeAclEntriesNamedOther() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, OTHER, "bruce", READ_EXECUTE));
        mergeAclEntries(existing, aclSpec);
    }

    @Test
    public void testReplaceAclEntries() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL), AclTestHelpers.aclEntry(ACCESS, MASK, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, "sales", ALL), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, "sales", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, replaceAclEntries(existing, aclSpec));
    }

    @Test
    public void testReplaceAclEntriesUnchanged() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, "sales", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL), AclTestHelpers.aclEntry(ACCESS, MASK, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, "sales", ALL), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        Assert.assertEquals(existing, replaceAclEntries(existing, aclSpec));
    }

    @Test
    public void testReplaceAclEntriesAccessMaskCalculated() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, READ));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        Assert.assertEquals(expected, replaceAclEntries(existing, aclSpec));
    }

    @Test
    public void testReplaceAclEntriesDefaultMaskCalculated() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, READ), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ), AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, GROUP, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, READ));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, READ)).build();
        Assert.assertEquals(expected, replaceAclEntries(existing, aclSpec));
    }

    @Test
    public void testReplaceAclEntriesDefaultMaskPreserved() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, READ));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, ALL)).add(AclTestHelpers.aclEntry(ACCESS, MASK, ALL)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, replaceAclEntries(existing, aclSpec));
    }

    @Test
    public void testReplaceAclEntriesAccessMaskPreserved() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, MASK, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, READ)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, replaceAclEntries(existing, aclSpec));
    }

    @Test
    public void testReplaceAclEntriesAutomaticDefaultUser() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, OTHER, READ));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, READ)).build();
        Assert.assertEquals(expected, replaceAclEntries(existing, aclSpec));
    }

    @Test
    public void testReplaceAclEntriesAutomaticDefaultGroup() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ), AclTestHelpers.aclEntry(DEFAULT, MASK, READ), AclTestHelpers.aclEntry(DEFAULT, OTHER, READ));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, READ)).build();
        Assert.assertEquals(expected, replaceAclEntries(existing, aclSpec));
    }

    @Test
    public void testReplaceAclEntriesAutomaticDefaultOther() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_WRITE));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ_WRITE)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, replaceAclEntries(existing, aclSpec));
    }

    @Test
    public void testReplaceAclEntriesOnlyDefaults() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ));
        List<AclEntry> expected = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, MASK, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        Assert.assertEquals(expected, replaceAclEntries(existing, aclSpec));
    }

    @Test(expected = AclException.class)
    public void testReplaceAclEntriesInputTooLarge() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        replaceAclEntries(existing, TestAclTransformation.ACL_SPEC_TOO_LARGE);
    }

    @Test(expected = AclException.class)
    public void testReplaceAclDefaultEntriesInputTooLarge() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(DEFAULT, USER, ALL)).add(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ)).add(AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE)).build();
        replaceAclEntries(existing, TestAclTransformation.ACL_SPEC_DEFAULT_TOO_LARGE);
    }

    @Test(expected = AclException.class)
    public void testReplaceAclEntriesResultTooLarge() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayListWithCapacity(32);
        aclSpec.add(AclTestHelpers.aclEntry(ACCESS, USER, ALL));
        for (int i = 1; i <= 29; ++i) {
            aclSpec.add(AclTestHelpers.aclEntry(ACCESS, USER, ("user" + i), READ));
        }
        aclSpec.add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ));
        aclSpec.add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        // The ACL spec now has 32 entries.  Automatic mask calculation will push it
        // over the limit to 33.
        replaceAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testReplaceAclEntriesDuplicateEntries() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, "clark", READ), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        replaceAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testReplaceAclEntriesNamedMask() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(ACCESS, MASK, "bruce", READ_EXECUTE));
        replaceAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testReplaceAclEntriesNamedOther() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(ACCESS, OTHER, "bruce", READ_EXECUTE));
        replaceAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testReplaceAclEntriesMissingUser() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL), AclTestHelpers.aclEntry(ACCESS, MASK, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        replaceAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testReplaceAclEntriesMissingGroup() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL), AclTestHelpers.aclEntry(ACCESS, MASK, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        replaceAclEntries(existing, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testReplaceAclEntriesMissingOther() throws AclException {
        List<AclEntry> existing = new ImmutableList.Builder<AclEntry>().add(AclTestHelpers.aclEntry(ACCESS, USER, ALL)).add(AclTestHelpers.aclEntry(ACCESS, GROUP, READ)).add(AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)).build();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, "sales", ALL), AclTestHelpers.aclEntry(ACCESS, MASK, ALL));
        replaceAclEntries(existing, aclSpec);
    }
}

