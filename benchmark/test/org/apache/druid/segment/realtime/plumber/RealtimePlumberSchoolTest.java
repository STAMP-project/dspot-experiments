/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.realtime.plumber;


import com.google.common.base.Suppliers;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.segment.indexing.DataSchema;
import org.apache.druid.segment.indexing.RealtimeTuningConfig;
import org.apache.druid.segment.indexing.TuningConfigs;
import org.apache.druid.segment.loading.DataSegmentPusher;
import org.apache.druid.segment.realtime.FireDepartmentMetrics;
import org.apache.druid.segment.realtime.SegmentPublisher;
import org.apache.druid.segment.writeout.SegmentWriteOutMediumFactory;
import org.apache.druid.server.coordination.DataSegmentAnnouncer;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class RealtimePlumberSchoolTest {
    private final RejectionPolicyFactory rejectionPolicy;

    private final SegmentWriteOutMediumFactory segmentWriteOutMediumFactory;

    private RealtimePlumber plumber;

    private RealtimePlumberSchool realtimePlumberSchool;

    private DataSegmentAnnouncer announcer;

    private SegmentPublisher segmentPublisher;

    private DataSegmentPusher dataSegmentPusher;

    private SegmentHandoffNotifier handoffNotifier;

    private SegmentHandoffNotifierFactory handoffNotifierFactory;

    private ServiceEmitter emitter;

    private RealtimeTuningConfig tuningConfig;

    private DataSchema schema;

    private DataSchema schema2;

    private FireDepartmentMetrics metrics;

    private File tmpDir;

    public RealtimePlumberSchoolTest(RejectionPolicyFactory rejectionPolicy, SegmentWriteOutMediumFactory segmentWriteOutMediumFactory) {
        this.rejectionPolicy = rejectionPolicy;
        this.segmentWriteOutMediumFactory = segmentWriteOutMediumFactory;
    }

    @Test(timeout = 60000L)
    public void testPersist() throws Exception {
        testPersist(null);
    }

    @Test(timeout = 60000L)
    public void testPersistWithCommitMetadata() throws Exception {
        final Object commitMetadata = "dummyCommitMetadata";
        testPersist(commitMetadata);
        plumber = ((RealtimePlumber) (realtimePlumberSchool.findPlumber(schema, tuningConfig, metrics)));
        Assert.assertEquals(commitMetadata, plumber.startJob());
    }

    @Test(timeout = 60000L)
    public void testPersistFails() throws Exception {
        Sink sink = new Sink(Intervals.utc(0, TimeUnit.HOURS.toMillis(1)), schema, tuningConfig.getShardSpec(), DateTimes.of("2014-12-01T12:34:56.789").toString(), tuningConfig.getMaxRowsInMemory(), TuningConfigs.getMaxBytesInMemoryOrDefault(tuningConfig.getMaxBytesInMemory()), tuningConfig.isReportParseExceptions(), tuningConfig.getDedupColumn());
        plumber.getSinks().put(0L, sink);
        plumber.startJob();
        final InputRow row = EasyMock.createNiceMock(InputRow.class);
        EasyMock.expect(row.getTimestampFromEpoch()).andReturn(0L);
        EasyMock.expect(row.getDimensions()).andReturn(new ArrayList<String>());
        EasyMock.replay(row);
        plumber.add(row, Suppliers.ofInstance(Committers.nil()));
        final CountDownLatch doneSignal = new CountDownLatch(1);
        plumber.persist(Committers.supplierFromRunnable(new Runnable() {
            @Override
            public void run() {
                doneSignal.countDown();
                throw new RuntimeException();
            }
        }).get());
        doneSignal.await();
        // Exception may need time to propagate
        while ((metrics.failedPersists()) < 1) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(1, metrics.failedPersists());
    }

    @Test(timeout = 60000L)
    public void testPersistHydrantGaps() throws Exception {
        final Object commitMetadata = "dummyCommitMetadata";
        testPersistHydrantGapsHelper(commitMetadata);
    }

    @Test(timeout = 60000L)
    public void testDimOrderInheritance() throws Exception {
        final Object commitMetadata = "dummyCommitMetadata";
        testDimOrderInheritanceHelper(commitMetadata);
    }
}

