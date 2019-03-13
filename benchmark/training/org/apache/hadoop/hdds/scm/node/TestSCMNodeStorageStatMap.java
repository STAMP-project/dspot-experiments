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
package org.apache.hadoop.hdds.scm.node;


import SCMNodeStorageStatMap.ReportStatus.ALL_IS_WELL;
import SCMNodeStorageStatMap.ReportStatus.FAILED_AND_OUT_OF_SPACE_STORAGE;
import SCMNodeStorageStatMap.ReportStatus.STORAGE_OUT_OF_SPACE;
import SCMNodeStorageStatMap.UtilizationThreshold.CRITICAL;
import SCMNodeStorageStatMap.UtilizationThreshold.NORMAL;
import SCMNodeStorageStatMap.UtilizationThreshold.WARN;
import StorageLocationReport.Builder;
import StorageType.DISK;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.NodeReportProto;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.StorageReportProto;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.exceptions.SCMException;
import org.apache.hadoop.ozone.OzoneConsts;
import org.apache.hadoop.ozone.container.common.impl.StorageLocationReport;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test Node Storage Map.
 */
public class TestSCMNodeStorageStatMap {
    private static final int DATANODE_COUNT = 100;

    private final long capacity = 10L * (OzoneConsts.GB);

    private final long used = 2L * (OzoneConsts.GB);

    private final long remaining = (capacity) - (used);

    private static OzoneConfiguration conf = new OzoneConfiguration();

    private final Map<UUID, Set<StorageLocationReport>> testData = new ConcurrentHashMap<>();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testIsKnownDatanode() throws SCMException {
        SCMNodeStorageStatMap map = new SCMNodeStorageStatMap(TestSCMNodeStorageStatMap.conf);
        UUID knownNode = getFirstKey();
        UUID unknownNode = UUID.randomUUID();
        Set<StorageLocationReport> report = testData.get(knownNode);
        map.insertNewDatanode(knownNode, report);
        Assert.assertTrue("Not able to detect a known node", map.isKnownDatanode(knownNode));
        Assert.assertFalse("Unknown node detected", map.isKnownDatanode(unknownNode));
    }

    @Test
    public void testInsertNewDatanode() throws SCMException {
        SCMNodeStorageStatMap map = new SCMNodeStorageStatMap(TestSCMNodeStorageStatMap.conf);
        UUID knownNode = getFirstKey();
        Set<StorageLocationReport> report = testData.get(knownNode);
        map.insertNewDatanode(knownNode, report);
        Assert.assertEquals(map.getStorageVolumes(knownNode), testData.get(knownNode));
        thrown.expect(SCMException.class);
        thrown.expectMessage("already exists");
        map.insertNewDatanode(knownNode, report);
    }

    @Test
    public void testUpdateUnknownDatanode() throws SCMException {
        SCMNodeStorageStatMap map = new SCMNodeStorageStatMap(TestSCMNodeStorageStatMap.conf);
        UUID unknownNode = UUID.randomUUID();
        String path = GenericTestUtils.getTempPath((((TestSCMNodeStorageStatMap.class.getSimpleName()) + "-") + (unknownNode.toString())));
        Set<StorageLocationReport> reportSet = new HashSet<>();
        StorageLocationReport.Builder builder = StorageLocationReport.newBuilder();
        builder.setStorageType(DISK).setId(unknownNode.toString()).setStorageLocation(path).setScmUsed(used).setRemaining(remaining).setCapacity(capacity).setFailed(false);
        reportSet.add(builder.build());
        thrown.expect(SCMException.class);
        thrown.expectMessage("No such datanode");
        map.updateDatanodeMap(unknownNode, reportSet);
    }

    @Test
    public void testProcessNodeReportCheckOneNode() throws IOException {
        UUID key = getFirstKey();
        List<StorageReportProto> reportList = new ArrayList<>();
        Set<StorageLocationReport> reportSet = testData.get(key);
        SCMNodeStorageStatMap map = new SCMNodeStorageStatMap(TestSCMNodeStorageStatMap.conf);
        map.insertNewDatanode(key, reportSet);
        Assert.assertTrue(map.isKnownDatanode(key));
        UUID storageId = UUID.randomUUID();
        String path = GenericTestUtils.getRandomizedTempPath().concat(("/" + storageId));
        StorageLocationReport report = reportSet.iterator().next();
        long reportCapacity = report.getCapacity();
        long reportScmUsed = report.getScmUsed();
        long reportRemaining = report.getRemaining();
        StorageReportProto storageReport = TestUtils.createStorageReport(storageId, path, reportCapacity, reportScmUsed, reportRemaining, null);
        StorageReportResult result = map.processNodeReport(key, TestUtils.createNodeReport(storageReport));
        Assert.assertEquals(ALL_IS_WELL, result.getStatus());
        StorageContainerDatanodeProtocolProtos.NodeReportProto.Builder nrb = org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.NodeReportProto.newBuilder();
        StorageReportProto srb = reportSet.iterator().next().getProtoBufMessage();
        reportList.add(srb);
        result = map.processNodeReport(key, TestUtils.createNodeReport(reportList));
        Assert.assertEquals(ALL_IS_WELL, result.getStatus());
        reportList.add(TestUtils.createStorageReport(UUID.randomUUID(), path, reportCapacity, reportCapacity, 0, null));
        result = map.processNodeReport(key, TestUtils.createNodeReport(reportList));
        Assert.assertEquals(STORAGE_OUT_OF_SPACE, result.getStatus());
        // Mark a disk failed
        StorageReportProto srb2 = StorageReportProto.newBuilder().setStorageUuid(UUID.randomUUID().toString()).setStorageLocation(srb.getStorageLocation()).setScmUsed(reportCapacity).setCapacity(reportCapacity).setRemaining(0).setFailed(true).build();
        reportList.add(srb2);
        nrb.addAllStorageReport(reportList);
        result = map.processNodeReport(key, nrb.addStorageReport(srb).build());
        Assert.assertEquals(FAILED_AND_OUT_OF_SPACE_STORAGE, result.getStatus());
    }

    @Test
    public void testProcessMultipleNodeReports() throws SCMException {
        SCMNodeStorageStatMap map = new SCMNodeStorageStatMap(TestSCMNodeStorageStatMap.conf);
        int counter = 1;
        // Insert all testData into the SCMNodeStorageStatMap Map.
        for (Map.Entry<UUID, Set<StorageLocationReport>> keyEntry : testData.entrySet()) {
            map.insertNewDatanode(keyEntry.getKey(), keyEntry.getValue());
        }
        Assert.assertEquals(((TestSCMNodeStorageStatMap.DATANODE_COUNT) * (capacity)), map.getTotalCapacity());
        Assert.assertEquals(((TestSCMNodeStorageStatMap.DATANODE_COUNT) * (remaining)), map.getTotalFreeSpace());
        Assert.assertEquals(((TestSCMNodeStorageStatMap.DATANODE_COUNT) * (used)), map.getTotalSpaceUsed());
        // upadate 1/4th of the datanode to be full
        for (Map.Entry<UUID, Set<StorageLocationReport>> keyEntry : testData.entrySet()) {
            Set<StorageLocationReport> reportSet = new HashSet<>();
            String path = GenericTestUtils.getTempPath((((TestSCMNodeStorageStatMap.class.getSimpleName()) + "-") + (keyEntry.getKey().toString())));
            StorageLocationReport.Builder builder = StorageLocationReport.newBuilder();
            builder.setStorageType(DISK).setId(keyEntry.getKey().toString()).setStorageLocation(path).setScmUsed(capacity).setRemaining(0).setCapacity(capacity).setFailed(false);
            reportSet.add(builder.build());
            map.updateDatanodeMap(keyEntry.getKey(), reportSet);
            counter++;
            if (counter > ((TestSCMNodeStorageStatMap.DATANODE_COUNT) / 4)) {
                break;
            }
        }
        Assert.assertEquals(((TestSCMNodeStorageStatMap.DATANODE_COUNT) / 4), map.getDatanodeList(CRITICAL).size());
        Assert.assertEquals(0, map.getDatanodeList(WARN).size());
        Assert.assertEquals((0.75 * (TestSCMNodeStorageStatMap.DATANODE_COUNT)), map.getDatanodeList(NORMAL).size(), 0);
        Assert.assertEquals(((TestSCMNodeStorageStatMap.DATANODE_COUNT) * (capacity)), map.getTotalCapacity(), 0);
        Assert.assertEquals(((0.75 * (TestSCMNodeStorageStatMap.DATANODE_COUNT)) * (remaining)), map.getTotalFreeSpace(), 0);
        Assert.assertEquals((((0.75 * (TestSCMNodeStorageStatMap.DATANODE_COUNT)) * (used)) + ((0.25 * (TestSCMNodeStorageStatMap.DATANODE_COUNT)) * (capacity))), map.getTotalSpaceUsed(), 0);
        counter = 1;
        // Remove 1/4 of the DataNodes from the Map
        for (Map.Entry<UUID, Set<StorageLocationReport>> keyEntry : testData.entrySet()) {
            map.removeDatanode(keyEntry.getKey());
            counter++;
            if (counter > ((TestSCMNodeStorageStatMap.DATANODE_COUNT) / 4)) {
                break;
            }
        }
        Assert.assertEquals(0, map.getDatanodeList(CRITICAL).size());
        Assert.assertEquals(0, map.getDatanodeList(WARN).size());
        Assert.assertEquals((0.75 * (TestSCMNodeStorageStatMap.DATANODE_COUNT)), map.getDatanodeList(NORMAL).size(), 0);
        Assert.assertEquals(((0.75 * (TestSCMNodeStorageStatMap.DATANODE_COUNT)) * (capacity)), map.getTotalCapacity(), 0);
        Assert.assertEquals(((0.75 * (TestSCMNodeStorageStatMap.DATANODE_COUNT)) * (remaining)), map.getTotalFreeSpace(), 0);
        Assert.assertEquals(((0.75 * (TestSCMNodeStorageStatMap.DATANODE_COUNT)) * (used)), map.getTotalSpaceUsed(), 0);
    }
}

