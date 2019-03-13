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
package org.apache.beam.sdk.io.kinesis;


import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.model.Shard;
import java.util.List;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.joda.time.Instant;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests StartingPointShardsFinder.
 */
public class StartingPointShardsFinderTest {
    private static final String STREAM_NAME = "streamName";

    private SimplifiedKinesisClient kinesis = Mockito.mock(SimplifiedKinesisClient.class);

    /* This test operates on shards hierarchy prepared upfront.
    Following diagram depicts shard split and merge operations:

                0002------------------------------+
               /                                   \
    0000------+                                     0009
               \                                   /
                0003------+             0007------+
                           \           /
                            0006------+
                           /           \
                0004------+             0008------+
               /                                   \
    0001------+                                     0010
               \                                   /
                0005------------------------------+
     */
    private final Shard shard00 = createClosedShard("0000");

    private final Shard shard01 = createClosedShard("0001");

    private final Shard shard02 = createClosedShard("0002").withParentShardId("0000");

    private final Shard shard03 = createClosedShard("0003").withParentShardId("0000");

    private final Shard shard04 = createClosedShard("0004").withParentShardId("0001");

    private final Shard shard05 = createClosedShard("0005").withParentShardId("0001");

    private final Shard shard06 = createClosedShard("0006").withParentShardId("0003").withAdjacentParentShardId("0004");

    private final Shard shard07 = createClosedShard("0007").withParentShardId("0006");

    private final Shard shard08 = createClosedShard("0008").withParentShardId("0006");

    private final Shard shard09 = createOpenShard("0009").withParentShardId("0002").withAdjacentParentShardId("0007");

    private final Shard shard10 = createOpenShard("0010").withParentShardId("0008").withAdjacentParentShardId("0005");

    private final List<Shard> allShards = ImmutableList.of(shard00, shard01, shard02, shard03, shard04, shard05, shard06, shard07, shard08, shard09, shard10);

    private StartingPointShardsFinder underTest = new StartingPointShardsFinder();

    @Test
    public void shouldFindFirstShardsWhenAllShardsAreValid() throws Exception {
        // given
        Instant timestampAtTheBeginning = new Instant();
        StartingPoint startingPointAtTheBeginning = new StartingPoint(timestampAtTheBeginning);
        for (Shard shard : allShards) {
            activeAtTimestamp(shard, timestampAtTheBeginning);
        }
        BDDMockito.given(kinesis.listShards(StartingPointShardsFinderTest.STREAM_NAME)).willReturn(allShards);
        // when
        Iterable<Shard> shardsAtStartingPoint = underTest.findShardsAtStartingPoint(kinesis, StartingPointShardsFinderTest.STREAM_NAME, startingPointAtTheBeginning);
        // then
        assertThat(shardsAtStartingPoint).containsExactlyInAnyOrder(shard00, shard01);
    }

    @Test
    public void shouldFind3StartingShardsInTheMiddle() throws Exception {
        // given
        Instant timestampAfterShards3And4Merge = new Instant();
        StartingPoint startingPointAfterFirstSplitsAndMerge = new StartingPoint(timestampAfterShards3And4Merge);
        expiredAtTimestamp(shard00, timestampAfterShards3And4Merge);
        expiredAtTimestamp(shard01, timestampAfterShards3And4Merge);
        activeAtTimestamp(shard02, timestampAfterShards3And4Merge);
        expiredAtTimestamp(shard03, timestampAfterShards3And4Merge);
        expiredAtTimestamp(shard04, timestampAfterShards3And4Merge);
        activeAtTimestamp(shard05, timestampAfterShards3And4Merge);
        activeAtTimestamp(shard06, timestampAfterShards3And4Merge);
        activeAtTimestamp(shard07, timestampAfterShards3And4Merge);
        activeAtTimestamp(shard08, timestampAfterShards3And4Merge);
        activeAtTimestamp(shard09, timestampAfterShards3And4Merge);
        activeAtTimestamp(shard10, timestampAfterShards3And4Merge);
        BDDMockito.given(kinesis.listShards(StartingPointShardsFinderTest.STREAM_NAME)).willReturn(allShards);
        // when
        Iterable<Shard> shardsAtStartingPoint = underTest.findShardsAtStartingPoint(kinesis, StartingPointShardsFinderTest.STREAM_NAME, startingPointAfterFirstSplitsAndMerge);
        // then
        assertThat(shardsAtStartingPoint).containsExactlyInAnyOrder(shard02, shard05, shard06);
    }

    @Test
    public void shouldFindLastShardWhenAllPreviousExpired() throws Exception {
        // given
        Instant timestampAtTheEnd = new Instant();
        StartingPoint startingPointAtTheEnd = new StartingPoint(timestampAtTheEnd);
        expiredAtTimestamp(shard00, timestampAtTheEnd);
        expiredAtTimestamp(shard01, timestampAtTheEnd);
        expiredAtTimestamp(shard02, timestampAtTheEnd);
        expiredAtTimestamp(shard03, timestampAtTheEnd);
        expiredAtTimestamp(shard04, timestampAtTheEnd);
        expiredAtTimestamp(shard05, timestampAtTheEnd);
        expiredAtTimestamp(shard06, timestampAtTheEnd);
        expiredAtTimestamp(shard07, timestampAtTheEnd);
        expiredAtTimestamp(shard08, timestampAtTheEnd);
        activeAtTimestamp(shard09, timestampAtTheEnd);
        activeAtTimestamp(shard10, timestampAtTheEnd);
        BDDMockito.given(kinesis.listShards(StartingPointShardsFinderTest.STREAM_NAME)).willReturn(allShards);
        // when
        Iterable<Shard> shardsAtStartingPoint = underTest.findShardsAtStartingPoint(kinesis, StartingPointShardsFinderTest.STREAM_NAME, startingPointAtTheEnd);
        // then
        assertThat(shardsAtStartingPoint).containsExactlyInAnyOrder(shard09, shard10);
    }

    @Test
    public void shouldFindLastShardsWhenLatestStartingPointRequested() throws Exception {
        // given
        StartingPoint latestStartingPoint = new StartingPoint(InitialPositionInStream.LATEST);
        BDDMockito.given(kinesis.listShards(StartingPointShardsFinderTest.STREAM_NAME)).willReturn(allShards);
        // when
        Iterable<Shard> shardsAtStartingPoint = underTest.findShardsAtStartingPoint(kinesis, StartingPointShardsFinderTest.STREAM_NAME, latestStartingPoint);
        // then
        assertThat(shardsAtStartingPoint).containsExactlyInAnyOrder(shard09, shard10);
    }

    @Test
    public void shouldFindEarliestShardsWhenTrimHorizonStartingPointRequested() throws Exception {
        // given
        StartingPoint trimHorizonStartingPoint = new StartingPoint(InitialPositionInStream.TRIM_HORIZON);
        BDDMockito.given(kinesis.listShards(StartingPointShardsFinderTest.STREAM_NAME)).willReturn(allShards);
        // when
        Iterable<Shard> shardsAtStartingPoint = underTest.findShardsAtStartingPoint(kinesis, StartingPointShardsFinderTest.STREAM_NAME, trimHorizonStartingPoint);
        // then
        assertThat(shardsAtStartingPoint).containsExactlyInAnyOrder(shard00, shard01);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenSuccessorsNotFoundForExpiredShard() throws Exception {
        // given
        StartingPoint latestStartingPoint = new StartingPoint(InitialPositionInStream.LATEST);
        Shard closedShard10 = createClosedShard("0010").withParentShardId("0008").withAdjacentParentShardId("0005");
        List<Shard> shards = ImmutableList.of(shard00, shard01, shard02, shard03, shard04, shard05, shard06, shard07, shard08, shard09, closedShard10);
        BDDMockito.given(kinesis.listShards(StartingPointShardsFinderTest.STREAM_NAME)).willReturn(shards);
        // when
        underTest.findShardsAtStartingPoint(kinesis, StartingPointShardsFinderTest.STREAM_NAME, latestStartingPoint);
    }
}

