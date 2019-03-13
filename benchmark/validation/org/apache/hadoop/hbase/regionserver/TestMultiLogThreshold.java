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
package org.apache.hadoop.hbase.regionserver;


import RSRpcServices.LogDelegate;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests logging of large batch commands via Multi. Tests are fast, but uses a mini-cluster (to test
 * via "Multi" commands) so classified as MediumTests
 */
@Category(MediumTests.class)
public class TestMultiLogThreshold {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMultiLogThreshold.class);

    private static RSRpcServices SERVICES;

    private static HBaseTestingUtility TEST_UTIL;

    private static Configuration CONF;

    private static final byte[] TEST_FAM = Bytes.toBytes("fam");

    private static LogDelegate LD;

    private static HRegionServer RS;

    private static int THRESHOLD;

    private enum ActionType {

        REGION_ACTIONS,
        ACTIONS;}

    @Test
    public void testMultiLogThresholdRegionActions() throws IOException, ServiceException {
        sendMultiRequest(((TestMultiLogThreshold.THRESHOLD) + 1), TestMultiLogThreshold.ActionType.REGION_ACTIONS);
        Mockito.verify(TestMultiLogThreshold.LD, Mockito.times(1)).logBatchWarning(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testMultiNoLogThresholdRegionActions() throws IOException, ServiceException {
        sendMultiRequest(TestMultiLogThreshold.THRESHOLD, TestMultiLogThreshold.ActionType.REGION_ACTIONS);
        Mockito.verify(TestMultiLogThreshold.LD, Mockito.never()).logBatchWarning(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testMultiLogThresholdActions() throws IOException, ServiceException {
        sendMultiRequest(((TestMultiLogThreshold.THRESHOLD) + 1), TestMultiLogThreshold.ActionType.ACTIONS);
        Mockito.verify(TestMultiLogThreshold.LD, Mockito.times(1)).logBatchWarning(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testMultiNoLogThresholdAction() throws IOException, ServiceException {
        sendMultiRequest(TestMultiLogThreshold.THRESHOLD, TestMultiLogThreshold.ActionType.ACTIONS);
        Mockito.verify(TestMultiLogThreshold.LD, Mockito.never()).logBatchWarning(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }
}

