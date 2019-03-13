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
package org.apache.hadoop.hbase;


import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientTests.class, SmallTests.class })
public class TestRegionLocations {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionLocations.class);

    ServerName sn0 = ServerName.valueOf("host0", 10, 10);

    ServerName sn1 = ServerName.valueOf("host1", 10, 10);

    ServerName sn2 = ServerName.valueOf("host2", 10, 10);

    ServerName sn3 = ServerName.valueOf("host3", 10, 10);

    HRegionInfo info0 = hri(0);

    HRegionInfo info1 = hri(1);

    HRegionInfo info2 = hri(2);

    HRegionInfo info9 = hri(9);

    long regionId1 = 1000;

    long regionId2 = 2000;

    @Test
    public void testSizeMethods() {
        RegionLocations list = new RegionLocations();
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(0, list.size());
        Assert.assertEquals(0, list.numNonNullElements());
        list = hrll(((HRegionLocation) (null)));
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(0, list.numNonNullElements());
        HRegionInfo info0 = hri(0);
        list = hrll(hrl(info0, null));
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(0, list.numNonNullElements());
        HRegionInfo info9 = hri(9);
        list = hrll(hrl(info9, null));
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(10, list.size());
        Assert.assertEquals(0, list.numNonNullElements());
        list = hrll(hrl(info0, null), hrl(info9, null));
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(10, list.size());
        Assert.assertEquals(0, list.numNonNullElements());
    }

    @Test
    public void testRemoveByServer() {
        RegionLocations list;
        // test remove from empty list
        list = new RegionLocations();
        Assert.assertTrue((list == (list.removeByServer(sn0))));
        // test remove from single element list
        list = hrll(hrl(info0, sn0));
        Assert.assertTrue((list == (list.removeByServer(sn1))));
        list = list.removeByServer(sn0);
        Assert.assertEquals(0, list.numNonNullElements());
        // test remove from multi element list
        list = hrll(hrl(info0, sn0), hrl(info1, sn1), hrl(info2, sn2), hrl(info9, sn2));
        Assert.assertTrue((list == (list.removeByServer(sn3))));// no region is mapped to sn3

        list = list.removeByServer(sn0);
        Assert.assertNull(list.getRegionLocation(0));
        Assert.assertEquals(sn1, list.getRegionLocation(1).getServerName());
        Assert.assertEquals(sn2, list.getRegionLocation(2).getServerName());
        Assert.assertNull(list.getRegionLocation(5));
        Assert.assertEquals(sn2, list.getRegionLocation(9).getServerName());
        // test multi-element remove from multi element list
        list = hrll(hrl(info0, sn1), hrl(info1, sn1), hrl(info2, sn0), hrl(info9, sn0));
        list = list.removeByServer(sn0);
        Assert.assertEquals(sn1, list.getRegionLocation(0).getServerName());
        Assert.assertEquals(sn1, list.getRegionLocation(1).getServerName());
        Assert.assertNull(list.getRegionLocation(2));
        Assert.assertNull(list.getRegionLocation(5));
        Assert.assertNull(list.getRegionLocation(9));
    }

    @Test
    public void testRemove() {
        RegionLocations list;
        // test remove from empty list
        list = new RegionLocations();
        Assert.assertTrue((list == (list.remove(hrl(info0, sn0)))));
        // test remove from single element list
        list = hrll(hrl(info0, sn0));
        Assert.assertTrue((list == (list.remove(hrl(info0, sn1)))));
        list = list.remove(hrl(info0, sn0));
        Assert.assertTrue(list.isEmpty());
        // test remove from multi element list
        list = hrll(hrl(info0, sn0), hrl(info1, sn1), hrl(info2, sn2), hrl(info9, sn2));
        Assert.assertTrue((list == (list.remove(hrl(info1, sn3)))));// no region is mapped to sn3

        list = list.remove(hrl(info0, sn0));
        Assert.assertNull(list.getRegionLocation(0));
        Assert.assertEquals(sn1, list.getRegionLocation(1).getServerName());
        Assert.assertEquals(sn2, list.getRegionLocation(2).getServerName());
        Assert.assertNull(list.getRegionLocation(5));
        Assert.assertEquals(sn2, list.getRegionLocation(9).getServerName());
        list = list.remove(hrl(info9, sn2));
        Assert.assertNull(list.getRegionLocation(0));
        Assert.assertEquals(sn1, list.getRegionLocation(1).getServerName());
        Assert.assertEquals(sn2, list.getRegionLocation(2).getServerName());
        Assert.assertNull(list.getRegionLocation(5));
        Assert.assertNull(list.getRegionLocation(9));
        // test multi-element remove from multi element list
        list = hrll(hrl(info0, sn1), hrl(info1, sn1), hrl(info2, sn0), hrl(info9, sn0));
        list = list.remove(hrl(info9, sn0));
        Assert.assertEquals(sn1, list.getRegionLocation(0).getServerName());
        Assert.assertEquals(sn1, list.getRegionLocation(1).getServerName());
        Assert.assertEquals(sn0, list.getRegionLocation(2).getServerName());
        Assert.assertNull(list.getRegionLocation(5));
        Assert.assertNull(list.getRegionLocation(9));
    }

    @Test
    public void testUpdateLocation() {
        RegionLocations list;
        // test add to empty list
        list = new RegionLocations();
        list = list.updateLocation(hrl(info0, sn1), false, false);
        Assert.assertEquals(sn1, list.getRegionLocation(0).getServerName());
        // test add to non-empty list
        list = list.updateLocation(hrl(info9, sn3, 10), false, false);
        Assert.assertEquals(sn3, list.getRegionLocation(9).getServerName());
        Assert.assertEquals(10, list.size());
        list = list.updateLocation(hrl(info2, sn2, 10), false, false);
        Assert.assertEquals(sn2, list.getRegionLocation(2).getServerName());
        Assert.assertEquals(10, list.size());
        // test update greater SeqNum
        list = list.updateLocation(hrl(info2, sn3, 11), false, false);
        Assert.assertEquals(sn3, list.getRegionLocation(2).getServerName());
        Assert.assertEquals(sn3, list.getRegionLocation(9).getServerName());
        // test update equal SeqNum
        list = list.updateLocation(hrl(info2, sn1, 11), false, false);// should not update

        Assert.assertEquals(sn3, list.getRegionLocation(2).getServerName());
        Assert.assertEquals(sn3, list.getRegionLocation(9).getServerName());
        list = list.updateLocation(hrl(info2, sn1, 11), true, false);// should update

        Assert.assertEquals(sn1, list.getRegionLocation(2).getServerName());
        Assert.assertEquals(sn3, list.getRegionLocation(9).getServerName());
        // test force update
        list = list.updateLocation(hrl(info2, sn2, 9), false, true);// should update

        Assert.assertEquals(sn2, list.getRegionLocation(2).getServerName());
        Assert.assertEquals(sn3, list.getRegionLocation(9).getServerName());
    }

    @Test
    public void testMergeLocations() {
        RegionLocations list1;
        RegionLocations list2;
        // test merge empty lists
        list1 = new RegionLocations();
        list2 = new RegionLocations();
        Assert.assertTrue((list1 == (list1.mergeLocations(list2))));
        // test merge non-empty and empty
        list2 = hrll(hrl(info0, sn0));
        list1 = list1.mergeLocations(list2);
        Assert.assertEquals(sn0, list1.getRegionLocation(0).getServerName());
        // test merge empty and non empty
        list1 = hrll();
        list1 = list2.mergeLocations(list1);
        Assert.assertEquals(sn0, list1.getRegionLocation(0).getServerName());
        // test merge non intersecting
        list1 = hrll(hrl(info0, sn0), hrl(info1, sn1));
        list2 = hrll(hrl(info2, sn2));
        list1 = list2.mergeLocations(list1);
        Assert.assertEquals(sn0, list1.getRegionLocation(0).getServerName());
        Assert.assertEquals(sn1, list1.getRegionLocation(1).getServerName());
        Assert.assertEquals(2, list1.size());// the size is taken from the argument list to merge

        // do the other way merge as well
        list1 = hrll(hrl(info0, sn0), hrl(info1, sn1));
        list2 = hrll(hrl(info2, sn2));
        list1 = list1.mergeLocations(list2);
        Assert.assertEquals(sn0, list1.getRegionLocation(0).getServerName());
        Assert.assertEquals(sn1, list1.getRegionLocation(1).getServerName());
        Assert.assertEquals(sn2, list1.getRegionLocation(2).getServerName());
        // test intersecting lists same seqNum
        list1 = hrll(hrl(info0, sn0), hrl(info1, sn1));
        list2 = hrll(hrl(info0, sn2), hrl(info1, sn2), hrl(info9, sn3));
        list1 = list2.mergeLocations(list1);// list1 should override

        Assert.assertEquals(2, list1.size());
        Assert.assertEquals(sn0, list1.getRegionLocation(0).getServerName());
        Assert.assertEquals(sn1, list1.getRegionLocation(1).getServerName());
        // do the other way
        list1 = hrll(hrl(info0, sn0), hrl(info1, sn1));
        list2 = hrll(hrl(info0, sn2), hrl(info1, sn2), hrl(info9, sn3));
        list1 = list1.mergeLocations(list2);// list2 should override

        Assert.assertEquals(10, list1.size());
        Assert.assertEquals(sn2, list1.getRegionLocation(0).getServerName());
        Assert.assertEquals(sn2, list1.getRegionLocation(1).getServerName());
        Assert.assertEquals(sn3, list1.getRegionLocation(9).getServerName());
        // test intersecting lists different seqNum
        list1 = hrll(hrl(info0, sn0, 10), hrl(info1, sn1, 10));
        list2 = hrll(hrl(info0, sn2, 11), hrl(info1, sn2, 11), hrl(info9, sn3, 11));
        list1 = list1.mergeLocations(list2);// list2 should override because of seqNum

        Assert.assertEquals(10, list1.size());
        Assert.assertEquals(sn2, list1.getRegionLocation(0).getServerName());
        Assert.assertEquals(sn2, list1.getRegionLocation(1).getServerName());
        Assert.assertEquals(sn3, list1.getRegionLocation(9).getServerName());
        // do the other way
        list1 = hrll(hrl(info0, sn0, 10), hrl(info1, sn1, 10));
        list2 = hrll(hrl(info0, sn2, 11), hrl(info1, sn2, 11), hrl(info9, sn3, 11));
        list1 = list1.mergeLocations(list2);// list2 should override

        Assert.assertEquals(10, list1.size());
        Assert.assertEquals(sn2, list1.getRegionLocation(0).getServerName());
        Assert.assertEquals(sn2, list1.getRegionLocation(1).getServerName());
        Assert.assertEquals(sn3, list1.getRegionLocation(9).getServerName());
    }

    @Test
    public void testMergeLocationsWithDifferentRegionId() {
        RegionLocations list1;
        RegionLocations list2;
        // test merging two lists. But the list2 contains region replicas with a different region id
        HRegionInfo info0 = hri(regionId1, 0);
        HRegionInfo info1 = hri(regionId1, 1);
        HRegionInfo info2 = hri(regionId2, 2);
        list1 = hrll(hrl(info2, sn1));
        list2 = hrll(hrl(info0, sn2), hrl(info1, sn2));
        list1 = list2.mergeLocations(list1);
        Assert.assertNull(list1.getRegionLocation(0));
        Assert.assertNull(list1.getRegionLocation(1));
        Assert.assertNotNull(list1.getRegionLocation(2));
        Assert.assertEquals(sn1, list1.getRegionLocation(2).getServerName());
        Assert.assertEquals(3, list1.size());
        // try the other way merge
        list1 = hrll(hrl(info2, sn1));
        list2 = hrll(hrl(info0, sn2), hrl(info1, sn2));
        list2 = list1.mergeLocations(list2);
        Assert.assertNotNull(list2.getRegionLocation(0));
        Assert.assertNotNull(list2.getRegionLocation(1));
        Assert.assertNull(list2.getRegionLocation(2));
    }

    @Test
    public void testUpdateLocationWithDifferentRegionId() {
        RegionLocations list;
        HRegionInfo info0 = hri(regionId1, 0);
        HRegionInfo info1 = hri(regionId2, 1);
        HRegionInfo info2 = hri(regionId1, 2);
        list = new RegionLocations(hrl(info0, sn1), hrl(info2, sn1));
        list = list.updateLocation(hrl(info1, sn2), false, true);// force update

        // the other locations should be removed now
        Assert.assertNull(list.getRegionLocation(0));
        Assert.assertNotNull(list.getRegionLocation(1));
        Assert.assertNull(list.getRegionLocation(2));
        Assert.assertEquals(sn2, list.getRegionLocation(1).getServerName());
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testConstructWithNullElements() {
        // RegionLocations can contain null elements as well. These null elements can
        RegionLocations list = new RegionLocations(((HRegionLocation) (null)));
        Assert.assertTrue(list.isEmpty());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(0, list.numNonNullElements());
        list = new RegionLocations(null, hrl(info1, sn0));
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(1, list.numNonNullElements());
        list = new RegionLocations(hrl(info0, sn0), null);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(1, list.numNonNullElements());
        list = new RegionLocations(null, hrl(info2, sn0), null, hrl(info9, sn0));
        Assert.assertEquals(10, list.size());
        Assert.assertEquals(2, list.numNonNullElements());
        list = new RegionLocations(null, hrl(info2, sn0), null, hrl(info9, sn0), null);
        Assert.assertEquals(11, list.size());
        Assert.assertEquals(2, list.numNonNullElements());
        list = new RegionLocations(null, hrl(info2, sn0), null, hrl(info9, sn0), null, null);
        Assert.assertEquals(12, list.size());
        Assert.assertEquals(2, list.numNonNullElements());
    }
}

