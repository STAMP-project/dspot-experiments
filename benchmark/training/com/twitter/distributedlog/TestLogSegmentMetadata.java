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
package com.twitter.distributedlog;


import DistributedLogConstants.LOCAL_REGION_ID;
import TruncationStatus.PARTIALLY_TRUNCATED;
import TruncationStatus.TRUNCATED;
import com.google.common.base.Charsets;
import com.twitter.distributedlog.LogSegmentMetadata.LogSegmentMetadataBuilder;
import com.twitter.distributedlog.exceptions.UnsupportedMetadataVersionException;
import com.twitter.distributedlog.util.FutureUtils;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test {@link LogSegmentMetadata}
 */
public class TestLogSegmentMetadata extends ZooKeeperClusterTestCase {
    static final Logger LOG = LoggerFactory.getLogger(TestLogSegmentMetadata.class);

    static final int TEST_REGION_ID = 15 - 1;

    private ZooKeeperClient zkc;

    @Test(timeout = 60000)
    public void testReadMetadata() throws Exception {
        LogSegmentMetadata metadata1 = setRegionId(TestLogSegmentMetadata.TEST_REGION_ID).build();
        metadata1.write(zkc);
        LogSegmentMetadata read1 = FutureUtils.result(LogSegmentMetadata.read(zkc, "/metadata1"));
        Assert.assertEquals(metadata1, read1);
        Assert.assertEquals(TestLogSegmentMetadata.TEST_REGION_ID, read1.getRegionId());
    }

    @Test(timeout = 60000)
    public void testReadMetadataCrossVersion() throws Exception {
        LogSegmentMetadata metadata1 = new LogSegmentMetadataBuilder("/metadata2", 1, 1000, 1).setRegionId(TestLogSegmentMetadata.TEST_REGION_ID).build();
        metadata1.write(zkc);
        // synchronous read
        LogSegmentMetadata read1 = FutureUtils.result(LogSegmentMetadata.read(zkc, "/metadata2", true));
        Assert.assertEquals(read1.getLedgerId(), metadata1.getLedgerId());
        Assert.assertEquals(read1.getFirstTxId(), metadata1.getFirstTxId());
        Assert.assertEquals(read1.getLastTxId(), metadata1.getLastTxId());
        Assert.assertEquals(read1.getLogSegmentSequenceNumber(), metadata1.getLogSegmentSequenceNumber());
        Assert.assertEquals(LOCAL_REGION_ID, read1.getRegionId());
    }

    @Test(timeout = 60000)
    public void testReadMetadataCrossVersionFailure() throws Exception {
        LogSegmentMetadata metadata1 = new LogSegmentMetadataBuilder("/metadata-failure", 1, 1000, 1).setRegionId(TestLogSegmentMetadata.TEST_REGION_ID).build();
        metadata1.write(zkc);
        // synchronous read
        try {
            LogSegmentMetadata read1 = FutureUtils.result(LogSegmentMetadata.read(zkc, "/metadata-failure"));
            Assert.fail("The previous statement should throw an exception");
        } catch (UnsupportedMetadataVersionException e) {
            // Expected
        }
    }

    @Test(timeout = 60000)
    public void testMutateTruncationStatus() {
        LogSegmentMetadata metadata = setRegionId(0).setLogSegmentSequenceNo(1L).build();
        metadata = metadata.completeLogSegment("/completed-metadata", 1000L, 1000, 1000L, 0L, 0L);
        LogSegmentMetadata partiallyTruncatedSegment = metadata.mutator().setTruncationStatus(PARTIALLY_TRUNCATED).setMinActiveDLSN(new DLSN(1L, 500L, 0L)).build();
        LogSegmentMetadata fullyTruncatedSegment = partiallyTruncatedSegment.mutator().setTruncationStatus(TRUNCATED).build();
        Assert.assertEquals(new DLSN(1L, 500L, 0L), fullyTruncatedSegment.getMinActiveDLSN());
    }

    @Test(timeout = 60000)
    public void testParseInvalidMetadata() throws Exception {
        try {
            LogSegmentMetadata.parseData("/metadata1", new byte[0], false);
            Assert.fail("Should fail to parse invalid metadata");
        } catch (IOException ioe) {
            // expected
        }
    }

    @Test(timeout = 60000)
    public void testReadLogSegmentWithSequenceId() throws Exception {
        LogSegmentMetadata metadata = setRegionId(0).setLogSegmentSequenceNo(1L).setStartSequenceId(999L).build();
        // write inprogress log segment with v5
        String data = metadata.getFinalisedData();
        LogSegmentMetadata parsedMetadata = LogSegmentMetadata.parseData("/metadatav5", data.getBytes(Charsets.UTF_8), false);
        Assert.assertEquals(999L, parsedMetadata.getStartSequenceId());
        LogSegmentMetadata metadatav4 = setRegionId(0).setLogSegmentSequenceNo(1L).setStartSequenceId(999L).build();
        String datav4 = metadatav4.getFinalisedData();
        LogSegmentMetadata parsedMetadatav4 = LogSegmentMetadata.parseData("/metadatav4", datav4.getBytes(Charsets.UTF_8), false);
        Assert.assertTrue(((parsedMetadatav4.getStartSequenceId()) < 0));
    }
}

