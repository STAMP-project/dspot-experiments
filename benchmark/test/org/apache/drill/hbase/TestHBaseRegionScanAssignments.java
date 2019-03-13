/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.hbase;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import org.apache.drill.categories.HbaseStorageTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;
import org.apache.drill.exec.store.hbase.HBaseGroupScan;
import org.apache.drill.exec.store.hbase.HBaseScanSpec;
import org.apache.drill.shaded.guava.com.google.common.collect.Lists;
import org.apache.drill.shaded.guava.com.google.common.collect.Maps;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SlowTest.class, HbaseStorageTest.class })
public class TestHBaseRegionScanAssignments extends BaseHBaseTest {
    static final String HOST_A = "A";

    static final String HOST_B = "B";

    static final String HOST_C = "C";

    static final String HOST_D = "D";

    static final String HOST_E = "E";

    static final String HOST_F = "F";

    static final String HOST_G = "G";

    static final String HOST_H = "H";

    static final String HOST_I = "I";

    static final String HOST_J = "J";

    static final String HOST_K = "K";

    static final String HOST_L = "L";

    static final String HOST_M = "M";

    static final String HOST_X = "X";

    static final String PORT_AND_STARTTIME = ",60020,1400265190186";

    static final ServerName SERVER_A = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_A) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final ServerName SERVER_B = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_B) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final ServerName SERVER_C = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_C) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final ServerName SERVER_D = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_D) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final ServerName SERVER_E = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_E) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final ServerName SERVER_F = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_F) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final ServerName SERVER_G = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_G) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final ServerName SERVER_H = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_H) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final ServerName SERVER_I = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_I) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final ServerName SERVER_X = ServerName.valueOf(((TestHBaseRegionScanAssignments.HOST_X) + (TestHBaseRegionScanAssignments.PORT_AND_STARTTIME)));

    static final byte[][] splits = new byte[][]{ new byte[]{  }, "10".getBytes(), "15".getBytes(), "20".getBytes(), "25".getBytes(), "30".getBytes(), "35".getBytes(), "40".getBytes(), "45".getBytes(), "50".getBytes(), "55".getBytes(), "60".getBytes(), "65".getBytes(), "70".getBytes(), "75".getBytes(), "80".getBytes(), "85".getBytes(), "90".getBytes(), "95".getBytes() };

    static final String TABLE_NAME_STR = "TestTable";

    static final TableName TABLE_NAME = TableName.valueOf(TestHBaseRegionScanAssignments.TABLE_NAME_STR);

    @Test
    public void testHBaseGroupScanAssignmentMix() throws Exception {
        NavigableMap<HRegionInfo, ServerName> regionsToScan = Maps.newTreeMap();
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[1]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[1], TestHBaseRegionScanAssignments.splits[2]), TestHBaseRegionScanAssignments.SERVER_B);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[2], TestHBaseRegionScanAssignments.splits[3]), TestHBaseRegionScanAssignments.SERVER_B);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[3], TestHBaseRegionScanAssignments.splits[4]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[4], TestHBaseRegionScanAssignments.splits[5]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[5], TestHBaseRegionScanAssignments.splits[6]), TestHBaseRegionScanAssignments.SERVER_D);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[6], TestHBaseRegionScanAssignments.splits[7]), TestHBaseRegionScanAssignments.SERVER_C);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[7], TestHBaseRegionScanAssignments.splits[0]), TestHBaseRegionScanAssignments.SERVER_D);
        final List<DrillbitEndpoint> endpoints = Lists.newArrayList();
        final DrillbitEndpoint DB_A = DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_A).setControlPort(1234).build();
        endpoints.add(DB_A);
        endpoints.add(DB_A);
        final DrillbitEndpoint DB_B = DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_B).setControlPort(1234).build();
        endpoints.add(DB_B);
        final DrillbitEndpoint DB_D = DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_D).setControlPort(1234).build();
        endpoints.add(DB_D);
        final DrillbitEndpoint DB_X = DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_X).setControlPort(1234).build();
        endpoints.add(DB_X);
        HBaseGroupScan scan = new HBaseGroupScan();
        scan.setRegionsToScan(regionsToScan);
        scan.setHBaseScanSpec(new HBaseScanSpec(TestHBaseRegionScanAssignments.TABLE_NAME_STR, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[0], null));
        scan.applyAssignments(endpoints);
        int i = 0;
        Assert.assertEquals(2, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'A'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'A'

        Assert.assertEquals(2, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'B'

        Assert.assertEquals(2, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'D'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'X'

        testParallelizationWidth(scan, i);
    }

    @Test
    public void testHBaseGroupScanAssignmentSomeAfinedWithOrphans() throws Exception {
        NavigableMap<HRegionInfo, ServerName> regionsToScan = Maps.newTreeMap();
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[1]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[1], TestHBaseRegionScanAssignments.splits[2]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[2], TestHBaseRegionScanAssignments.splits[3]), TestHBaseRegionScanAssignments.SERVER_B);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[3], TestHBaseRegionScanAssignments.splits[4]), TestHBaseRegionScanAssignments.SERVER_B);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[4], TestHBaseRegionScanAssignments.splits[5]), TestHBaseRegionScanAssignments.SERVER_C);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[5], TestHBaseRegionScanAssignments.splits[6]), TestHBaseRegionScanAssignments.SERVER_C);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[6], TestHBaseRegionScanAssignments.splits[7]), TestHBaseRegionScanAssignments.SERVER_D);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[7], TestHBaseRegionScanAssignments.splits[8]), TestHBaseRegionScanAssignments.SERVER_D);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[8], TestHBaseRegionScanAssignments.splits[9]), TestHBaseRegionScanAssignments.SERVER_E);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[9], TestHBaseRegionScanAssignments.splits[10]), TestHBaseRegionScanAssignments.SERVER_E);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[10], TestHBaseRegionScanAssignments.splits[11]), TestHBaseRegionScanAssignments.SERVER_F);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[11], TestHBaseRegionScanAssignments.splits[12]), TestHBaseRegionScanAssignments.SERVER_F);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[12], TestHBaseRegionScanAssignments.splits[13]), TestHBaseRegionScanAssignments.SERVER_G);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[13], TestHBaseRegionScanAssignments.splits[14]), TestHBaseRegionScanAssignments.SERVER_G);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[14], TestHBaseRegionScanAssignments.splits[15]), TestHBaseRegionScanAssignments.SERVER_H);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[15], TestHBaseRegionScanAssignments.splits[16]), TestHBaseRegionScanAssignments.SERVER_H);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[16], TestHBaseRegionScanAssignments.splits[17]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[17], TestHBaseRegionScanAssignments.splits[0]), TestHBaseRegionScanAssignments.SERVER_A);
        final List<DrillbitEndpoint> endpoints = Lists.newArrayList();
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_A).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_B).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_C).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_D).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_E).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_F).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_G).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_H).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_I).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_J).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_K).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_L).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_M).setControlPort(1234).build());
        HBaseGroupScan scan = new HBaseGroupScan();
        scan.setRegionsToScan(regionsToScan);
        scan.setHBaseScanSpec(new HBaseScanSpec(TestHBaseRegionScanAssignments.TABLE_NAME_STR, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[0], null));
        scan.applyAssignments(endpoints);
        LinkedList<Integer> sizes = Lists.newLinkedList();
        Collections.addAll(sizes, 1, 1, 1, 1, 1, 1, 1, 1);
        Collections.addAll(sizes, 2, 2, 2, 2, 2);
        for (int i = 0; i < (endpoints.size()); i++) {
            Assert.assertTrue(sizes.remove(((Integer) (scan.getSpecificScan(i).getRegionScanSpecList().size()))));
        }
        Assert.assertEquals(0, sizes.size());
        testParallelizationWidth(scan, endpoints.size());
    }

    @Test
    public void testHBaseGroupScanAssignmentOneEach() throws Exception {
        NavigableMap<HRegionInfo, ServerName> regionsToScan = Maps.newTreeMap();
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[1]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[1], TestHBaseRegionScanAssignments.splits[2]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[2], TestHBaseRegionScanAssignments.splits[3]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[3], TestHBaseRegionScanAssignments.splits[4]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[4], TestHBaseRegionScanAssignments.splits[5]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[5], TestHBaseRegionScanAssignments.splits[6]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[6], TestHBaseRegionScanAssignments.splits[7]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[7], TestHBaseRegionScanAssignments.splits[8]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[8], TestHBaseRegionScanAssignments.splits[0]), TestHBaseRegionScanAssignments.SERVER_A);
        final List<DrillbitEndpoint> endpoints = Lists.newArrayList();
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_A).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_A).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_B).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_C).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_D).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_E).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_F).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_G).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_H).setControlPort(1234).build());
        HBaseGroupScan scan = new HBaseGroupScan();
        scan.setRegionsToScan(regionsToScan);
        scan.setHBaseScanSpec(new HBaseScanSpec(TestHBaseRegionScanAssignments.TABLE_NAME_STR, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[0], null));
        scan.applyAssignments(endpoints);
        int i = 0;
        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'A'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'A'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'B'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'C'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'D'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'E'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'F'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'G'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'H'

        testParallelizationWidth(scan, i);
    }

    @Test
    public void testHBaseGroupScanAssignmentNoAfinity() throws Exception {
        NavigableMap<HRegionInfo, ServerName> regionsToScan = Maps.newTreeMap();
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[1]), TestHBaseRegionScanAssignments.SERVER_X);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[1], TestHBaseRegionScanAssignments.splits[2]), TestHBaseRegionScanAssignments.SERVER_X);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[2], TestHBaseRegionScanAssignments.splits[3]), TestHBaseRegionScanAssignments.SERVER_X);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[3], TestHBaseRegionScanAssignments.splits[4]), TestHBaseRegionScanAssignments.SERVER_X);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[4], TestHBaseRegionScanAssignments.splits[5]), TestHBaseRegionScanAssignments.SERVER_X);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[5], TestHBaseRegionScanAssignments.splits[6]), TestHBaseRegionScanAssignments.SERVER_X);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[6], TestHBaseRegionScanAssignments.splits[7]), TestHBaseRegionScanAssignments.SERVER_X);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[7], TestHBaseRegionScanAssignments.splits[0]), TestHBaseRegionScanAssignments.SERVER_X);
        final List<DrillbitEndpoint> endpoints = Lists.newArrayList();
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_A).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_B).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_C).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_D).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_E).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_F).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_G).setControlPort(1234).build());
        endpoints.add(DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_H).setControlPort(1234).build());
        HBaseGroupScan scan = new HBaseGroupScan();
        scan.setRegionsToScan(regionsToScan);
        scan.setHBaseScanSpec(new HBaseScanSpec(TestHBaseRegionScanAssignments.TABLE_NAME_STR, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[0], null));
        scan.applyAssignments(endpoints);
        int i = 0;
        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'A'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'B'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'C'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'D'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'E'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'F'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'G'

        Assert.assertEquals(1, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'H'

        testParallelizationWidth(scan, i);
    }

    @Test
    public void testHBaseGroupScanAssignmentAllPreferred() throws Exception {
        NavigableMap<HRegionInfo, ServerName> regionsToScan = Maps.newTreeMap();
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[1]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[1], TestHBaseRegionScanAssignments.splits[2]), TestHBaseRegionScanAssignments.SERVER_A);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[2], TestHBaseRegionScanAssignments.splits[3]), TestHBaseRegionScanAssignments.SERVER_B);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[3], TestHBaseRegionScanAssignments.splits[4]), TestHBaseRegionScanAssignments.SERVER_B);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[4], TestHBaseRegionScanAssignments.splits[5]), TestHBaseRegionScanAssignments.SERVER_C);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[5], TestHBaseRegionScanAssignments.splits[6]), TestHBaseRegionScanAssignments.SERVER_C);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[6], TestHBaseRegionScanAssignments.splits[7]), TestHBaseRegionScanAssignments.SERVER_D);
        regionsToScan.put(new HRegionInfo(TestHBaseRegionScanAssignments.TABLE_NAME, TestHBaseRegionScanAssignments.splits[7], TestHBaseRegionScanAssignments.splits[0]), TestHBaseRegionScanAssignments.SERVER_D);
        final List<DrillbitEndpoint> endpoints = Lists.newArrayList();
        final DrillbitEndpoint DB_A = DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_A).setControlPort(1234).build();
        endpoints.add(DB_A);
        final DrillbitEndpoint DB_B = DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_B).setControlPort(1234).build();
        endpoints.add(DB_B);
        final DrillbitEndpoint DB_D = DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_C).setControlPort(1234).build();
        endpoints.add(DB_D);
        final DrillbitEndpoint DB_X = DrillbitEndpoint.newBuilder().setAddress(TestHBaseRegionScanAssignments.HOST_D).setControlPort(1234).build();
        endpoints.add(DB_X);
        HBaseGroupScan scan = new HBaseGroupScan();
        scan.setRegionsToScan(regionsToScan);
        scan.setHBaseScanSpec(new HBaseScanSpec(TestHBaseRegionScanAssignments.TABLE_NAME_STR, TestHBaseRegionScanAssignments.splits[0], TestHBaseRegionScanAssignments.splits[0], null));
        scan.applyAssignments(endpoints);
        int i = 0;
        Assert.assertEquals(2, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'A'

        Assert.assertEquals(2, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'B'

        Assert.assertEquals(2, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'C'

        Assert.assertEquals(2, scan.getSpecificScan((i++)).getRegionScanSpecList().size());// 'D'

        testParallelizationWidth(scan, i);
    }
}

