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
package org.apache.hadoop.hbase.client;


import RegionInfoDisplay.HIDDEN_END_KEY;
import RegionInfoDisplay.HIDDEN_START_KEY;
import RegionState.State.OPEN;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.RegionState;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ MasterTests.class, SmallTests.class })
public class TestRegionInfoDisplay {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionInfoDisplay.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testRegionDetailsForDisplay() throws IOException {
        byte[] startKey = new byte[]{ 1, 1, 2, 3 };
        byte[] endKey = new byte[]{ 1, 1, 2, 4 };
        Configuration conf = new Configuration();
        conf.setBoolean("hbase.display.keys", false);
        RegionInfo ri = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(startKey).setEndKey(endKey).build();
        checkEquality(ri, conf);
        // check HRIs with non-default replicaId
        ri = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(startKey).setEndKey(endKey).setSplit(false).setRegionId(System.currentTimeMillis()).setReplicaId(1).build();
        checkEquality(ri, conf);
        Assert.assertArrayEquals(HIDDEN_END_KEY, RegionInfoDisplay.getEndKeyForDisplay(ri, conf));
        Assert.assertArrayEquals(HIDDEN_START_KEY, RegionInfoDisplay.getStartKeyForDisplay(ri, conf));
        RegionState state = RegionState.createForTesting(convert(ri), OPEN);
        String descriptiveNameForDisplay = RegionInfoDisplay.getDescriptiveNameFromRegionStateForDisplay(state, conf);
        String originalDescriptive = state.toDescriptiveString();
        checkDescriptiveNameEquality(descriptiveNameForDisplay, originalDescriptive, startKey);
        conf.setBoolean("hbase.display.keys", true);
        Assert.assertArrayEquals(endKey, RegionInfoDisplay.getEndKeyForDisplay(ri, conf));
        Assert.assertArrayEquals(startKey, RegionInfoDisplay.getStartKeyForDisplay(ri, conf));
        Assert.assertEquals(originalDescriptive, RegionInfoDisplay.getDescriptiveNameFromRegionStateForDisplay(state, conf));
    }
}

