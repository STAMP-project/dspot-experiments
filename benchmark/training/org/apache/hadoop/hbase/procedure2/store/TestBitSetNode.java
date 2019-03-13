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
package org.apache.hadoop.hbase.procedure2.store;


import DeleteState.NO;
import DeleteState.YES;
import Procedure.NO_PROC_ID;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, SmallTests.class })
public class TestBitSetNode {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBitSetNode.class);

    @Test
    public void testGetActiveMaxMinProcId() {
        BitSetNode node = new BitSetNode(5L, false);
        Assert.assertEquals(5L, node.getActiveMinProcId());
        Assert.assertEquals(5L, node.getActiveMaxProcId());
        node.insertOrUpdate(10L);
        Assert.assertEquals(5L, node.getActiveMinProcId());
        Assert.assertEquals(10L, node.getActiveMaxProcId());
        node.insertOrUpdate(1L);
        Assert.assertEquals(1L, node.getActiveMinProcId());
        Assert.assertEquals(10L, node.getActiveMaxProcId());
        node.delete(10L);
        Assert.assertEquals(1L, node.getActiveMinProcId());
        Assert.assertEquals(5L, node.getActiveMaxProcId());
        node.delete(1L);
        Assert.assertEquals(5L, node.getActiveMinProcId());
        Assert.assertEquals(5L, node.getActiveMaxProcId());
        node.delete(5L);
        Assert.assertEquals(NO_PROC_ID, node.getActiveMinProcId());
        Assert.assertEquals(NO_PROC_ID, node.getActiveMaxProcId());
    }

    @Test
    public void testGrow() {
        BitSetNode node = new BitSetNode(1000, false);
        // contains, do not need to grow but should not fail
        Assert.assertTrue(node.canGrow(1024));
        Assert.assertTrue(node.canGrow(900));
        Assert.assertTrue(node.canGrow(1100));
        Assert.assertFalse(node.canGrow(100));
        Assert.assertFalse(node.canGrow(10000));
        // grow to right
        node.grow(1100);
        Assert.assertTrue(node.contains(1100));
        Assert.assertTrue(node.isModified(1000));
        // grow to left
        node.grow(900);
        Assert.assertTrue(node.contains(900));
        Assert.assertTrue(node.isModified(1000));
        for (long i = node.getStart(); i <= (node.getEnd()); i++) {
            if (i != 1000) {
                Assert.assertEquals(YES, node.isDeleted(i));
            } else {
                Assert.assertEquals(NO, node.isDeleted(i));
            }
        }
    }

    @Test
    public void testMerge() {
        BitSetNode node = new BitSetNode(1000, false);
        Assert.assertTrue(node.canMerge(new BitSetNode(1200, false)));
        Assert.assertFalse(node.canMerge(new BitSetNode(10000, false)));
        BitSetNode rightNode = new BitSetNode(1200, false);
        node.merge(rightNode);
        Assert.assertTrue(node.isModified(1000));
        Assert.assertTrue(node.isModified(1200));
        for (long i = node.getStart(); i <= (node.getEnd()); i++) {
            if ((i != 1000) && (i != 1200)) {
                Assert.assertEquals(YES, node.isDeleted(i));
            } else {
                Assert.assertEquals(NO, node.isDeleted(i));
            }
        }
    }
}

