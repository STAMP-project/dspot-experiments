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
package org.apache.hadoop.hbase.security.visibility;


import com.google.protobuf.ByteString;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos.RegionActionResult;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos.NameBytesPair;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.ListLabelsResponse;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.VisibilityLabelsResponse;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static VisibilityUtils.SYSTEM_LABEL;


@Category({ SecurityTests.class, MediumTests.class })
public class TestVisibilityLabelsWithDefaultVisLabelService extends TestVisibilityLabels {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVisibilityLabelsWithDefaultVisLabelService.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestVisibilityLabelsWithDefaultVisLabelService.class);

    @Test
    public void testAddLabels() throws Throwable {
        PrivilegedExceptionAction<VisibilityLabelsResponse> action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
            @Override
            public VisibilityLabelsResponse run() throws Exception {
                String[] labels = new String[]{ "L1", TestVisibilityLabels.SECRET, "L2", "invalid~", "L3" };
                VisibilityLabelsResponse response = null;
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                    response = VisibilityClient.addLabels(conn, labels);
                } catch (Throwable e) {
                    throw new IOException(e);
                }
                List<RegionActionResult> resultList = response.getResultList();
                Assert.assertEquals(5, resultList.size());
                Assert.assertTrue(resultList.get(0).getException().getValue().isEmpty());
                Assert.assertEquals("org.apache.hadoop.hbase.DoNotRetryIOException", resultList.get(1).getException().getName());
                Assert.assertTrue(Bytes.toString(resultList.get(1).getException().getValue().toByteArray()).contains(("org.apache.hadoop.hbase.security.visibility.LabelAlreadyExistsException: " + "Label 'secret' already exists")));
                Assert.assertTrue(resultList.get(2).getException().getValue().isEmpty());
                Assert.assertTrue(resultList.get(3).getException().getValue().isEmpty());
                Assert.assertTrue(resultList.get(4).getException().getValue().isEmpty());
                return null;
            }
        };
        TestVisibilityLabels.SUPERUSER.runAs(action);
    }

    @Test
    public void testAddVisibilityLabelsOnRSRestart() throws Exception {
        List<RegionServerThread> regionServerThreads = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().getRegionServerThreads();
        for (RegionServerThread rsThread : regionServerThreads) {
            rsThread.getRegionServer().abort("Aborting ");
        }
        // Start one new RS
        RegionServerThread rs = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().startRegionServer();
        waitForLabelsRegionAvailability(rs.getRegionServer());
        final AtomicBoolean vcInitialized = new AtomicBoolean(true);
        do {
            PrivilegedExceptionAction<VisibilityLabelsResponse> action = new PrivilegedExceptionAction<VisibilityLabelsResponse>() {
                @Override
                public VisibilityLabelsResponse run() throws Exception {
                    String[] labels = new String[]{ TestVisibilityLabels.SECRET, TestVisibilityLabels.CONFIDENTIAL, TestVisibilityLabels.PRIVATE, "ABC", "XYZ" };
                    try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                        VisibilityLabelsResponse resp = VisibilityClient.addLabels(conn, labels);
                        List<RegionActionResult> results = resp.getResultList();
                        if (results.get(0).hasException()) {
                            NameBytesPair pair = results.get(0).getException();
                            Throwable t = ProtobufUtil.toException(pair);
                            TestVisibilityLabelsWithDefaultVisLabelService.LOG.debug("Got exception writing labels", t);
                            if (t instanceof VisibilityControllerNotReadyException) {
                                vcInitialized.set(false);
                                TestVisibilityLabelsWithDefaultVisLabelService.LOG.warn("VisibilityController was not yet initialized");
                                Threads.sleep(10);
                            } else {
                                vcInitialized.set(true);
                            }
                        } else
                            TestVisibilityLabelsWithDefaultVisLabelService.LOG.debug(("new labels added: " + resp));

                    } catch (Throwable t) {
                        throw new IOException(t);
                    }
                    return null;
                }
            };
            TestVisibilityLabels.SUPERUSER.runAs(action);
        } while (!(vcInitialized.get()) );
        // Scan the visibility label
        Scan s = new Scan();
        s.setAuthorizations(new Authorizations(SYSTEM_LABEL));
        int i = 0;
        try (Table ht = TestVisibilityLabels.TEST_UTIL.getConnection().getTable(VisibilityConstants.LABELS_TABLE_NAME);ResultScanner scanner = ht.getScanner(s)) {
            while (true) {
                Result next = scanner.next();
                if (next == null) {
                    break;
                }
                i++;
            } 
        }
        // One label is the "system" label.
        Assert.assertEquals("The count should be 13", 13, i);
    }

    @Test
    public void testListLabels() throws Throwable {
        PrivilegedExceptionAction<ListLabelsResponse> action = new PrivilegedExceptionAction<ListLabelsResponse>() {
            @Override
            public ListLabelsResponse run() throws Exception {
                ListLabelsResponse response = null;
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                    response = VisibilityClient.listLabels(conn, null);
                } catch (Throwable e) {
                    throw new IOException(e);
                }
                // The addLabels() in setup added:
                // { SECRET, TOPSECRET, CONFIDENTIAL, PUBLIC, PRIVATE, COPYRIGHT, ACCENT,
                // UNICODE_VIS_TAG, UC1, UC2 };
                // The previous tests added 2 more labels: ABC, XYZ
                // The 'system' label is excluded.
                List<ByteString> labels = response.getLabelList();
                Assert.assertEquals(12, labels.size());
                Assert.assertTrue(labels.contains(ByteString.copyFrom(Bytes.toBytes(TestVisibilityLabels.SECRET))));
                Assert.assertTrue(labels.contains(ByteString.copyFrom(Bytes.toBytes(TestVisibilityLabels.TOPSECRET))));
                Assert.assertTrue(labels.contains(ByteString.copyFrom(Bytes.toBytes(TestVisibilityLabels.CONFIDENTIAL))));
                Assert.assertTrue(labels.contains(ByteString.copyFrom(Bytes.toBytes("ABC"))));
                Assert.assertTrue(labels.contains(ByteString.copyFrom(Bytes.toBytes("XYZ"))));
                Assert.assertFalse(labels.contains(ByteString.copyFrom(Bytes.toBytes(VisibilityUtils.SYSTEM_LABEL))));
                return null;
            }
        };
        TestVisibilityLabels.SUPERUSER.runAs(action);
    }

    @Test
    public void testListLabelsWithRegEx() throws Throwable {
        PrivilegedExceptionAction<ListLabelsResponse> action = new PrivilegedExceptionAction<ListLabelsResponse>() {
            @Override
            public ListLabelsResponse run() throws Exception {
                ListLabelsResponse response = null;
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                    response = VisibilityClient.listLabels(conn, ".*secret");
                } catch (Throwable e) {
                    throw new IOException(e);
                }
                // Only return the labels that end with 'secret'
                List<ByteString> labels = response.getLabelList();
                Assert.assertEquals(2, labels.size());
                Assert.assertTrue(labels.contains(ByteString.copyFrom(Bytes.toBytes(TestVisibilityLabels.SECRET))));
                Assert.assertTrue(labels.contains(ByteString.copyFrom(Bytes.toBytes(TestVisibilityLabels.TOPSECRET))));
                return null;
            }
        };
        TestVisibilityLabels.SUPERUSER.runAs(action);
    }

    @Test
    public void testVisibilityLabelsOnWALReplay() throws Exception {
        final TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((("(" + (TestVisibilityLabels.SECRET)) + "|") + (TestVisibilityLabels.CONFIDENTIAL)) + ")"), TestVisibilityLabels.PRIVATE)) {
            List<RegionServerThread> regionServerThreads = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().getRegionServerThreads();
            for (RegionServerThread rsThread : regionServerThreads) {
                rsThread.getRegionServer().abort("Aborting ");
            }
            // Start one new RS
            RegionServerThread rs = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().startRegionServer();
            waitForLabelsRegionAvailability(rs.getRegionServer());
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
        }
    }
}

