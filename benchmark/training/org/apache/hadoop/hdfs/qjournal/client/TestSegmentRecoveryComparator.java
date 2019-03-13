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
package org.apache.hadoop.hdfs.qjournal.client;


import java.util.Map;
import org.apache.hadoop.hdfs.qjournal.protocol.QJournalProtocolProtos.PrepareRecoveryResponseProto;
import org.apache.hadoop.hdfs.qjournal.protocol.QJournalProtocolProtos.SegmentStateProto;
import org.junit.Assert;
import org.junit.Test;


public class TestSegmentRecoveryComparator {
    @Test
    public void testComparisons() {
        Map.Entry<AsyncLogger, PrepareRecoveryResponseProto> INPROGRESS_1_3 = TestSegmentRecoveryComparator.makeEntry(PrepareRecoveryResponseProto.newBuilder().setSegmentState(SegmentStateProto.newBuilder().setStartTxId(1L).setEndTxId(3L).setIsInProgress(true)).setLastWriterEpoch(0L).build());
        Map.Entry<AsyncLogger, PrepareRecoveryResponseProto> INPROGRESS_1_4 = TestSegmentRecoveryComparator.makeEntry(PrepareRecoveryResponseProto.newBuilder().setSegmentState(SegmentStateProto.newBuilder().setStartTxId(1L).setEndTxId(4L).setIsInProgress(true)).setLastWriterEpoch(0L).build());
        Map.Entry<AsyncLogger, PrepareRecoveryResponseProto> INPROGRESS_1_4_ACCEPTED = TestSegmentRecoveryComparator.makeEntry(PrepareRecoveryResponseProto.newBuilder().setSegmentState(SegmentStateProto.newBuilder().setStartTxId(1L).setEndTxId(4L).setIsInProgress(true)).setLastWriterEpoch(0L).setAcceptedInEpoch(1L).build());
        Map.Entry<AsyncLogger, PrepareRecoveryResponseProto> FINALIZED_1_3 = TestSegmentRecoveryComparator.makeEntry(PrepareRecoveryResponseProto.newBuilder().setSegmentState(SegmentStateProto.newBuilder().setStartTxId(1L).setEndTxId(3L).setIsInProgress(false)).setLastWriterEpoch(0L).build());
        // Should compare equal to itself
        Assert.assertEquals(0, SegmentRecoveryComparator.INSTANCE.compare(INPROGRESS_1_3, INPROGRESS_1_3));
        // Longer log wins.
        Assert.assertEquals((-1), SegmentRecoveryComparator.INSTANCE.compare(INPROGRESS_1_3, INPROGRESS_1_4));
        Assert.assertEquals(1, SegmentRecoveryComparator.INSTANCE.compare(INPROGRESS_1_4, INPROGRESS_1_3));
        // Finalized log wins even over a longer in-progress
        Assert.assertEquals((-1), SegmentRecoveryComparator.INSTANCE.compare(INPROGRESS_1_4, FINALIZED_1_3));
        Assert.assertEquals(1, SegmentRecoveryComparator.INSTANCE.compare(FINALIZED_1_3, INPROGRESS_1_4));
        // Finalized log wins even if the in-progress one has an accepted
        // recovery proposal.
        Assert.assertEquals((-1), SegmentRecoveryComparator.INSTANCE.compare(INPROGRESS_1_4_ACCEPTED, FINALIZED_1_3));
        Assert.assertEquals(1, SegmentRecoveryComparator.INSTANCE.compare(FINALIZED_1_3, INPROGRESS_1_4_ACCEPTED));
    }
}

