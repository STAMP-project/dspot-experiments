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
package org.apache.beam.sdk.nexmark.queries.sql;


import java.util.List;
import org.apache.beam.sdk.nexmark.model.AuctionPrice;
import org.apache.beam.sdk.nexmark.model.Bid;
import org.apache.beam.sdk.nexmark.model.Event;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;


/**
 * Unit tests for {@link SqlQuery2}.
 */
public class SqlQuery2Test {
    private static final List<Bid> BIDS = ImmutableList.of(SqlQuery2Test.newBid(1L), SqlQuery2Test.newBid(2L), SqlQuery2Test.newBid(3L), SqlQuery2Test.newBid(4L), SqlQuery2Test.newBid(5L), SqlQuery2Test.newBid(6L), SqlQuery2Test.newBid(7L), SqlQuery2Test.newBid(8L));

    private static final List<Event> BIDS_EVENTS = ImmutableList.of(new Event(SqlQuery2Test.BIDS.get(0)), new Event(SqlQuery2Test.BIDS.get(1)), new Event(SqlQuery2Test.BIDS.get(2)), new Event(SqlQuery2Test.BIDS.get(3)), new Event(SqlQuery2Test.BIDS.get(4)), new Event(SqlQuery2Test.BIDS.get(5)), new Event(SqlQuery2Test.BIDS.get(6)), new Event(SqlQuery2Test.BIDS.get(7)));

    private static final List<AuctionPrice> BIDS_EVEN = ImmutableList.of(SqlQuery2Test.newAuctionPrice(SqlQuery2Test.BIDS.get(1)), SqlQuery2Test.newAuctionPrice(SqlQuery2Test.BIDS.get(3)), SqlQuery2Test.newAuctionPrice(SqlQuery2Test.BIDS.get(5)), SqlQuery2Test.newAuctionPrice(SqlQuery2Test.BIDS.get(7)));

    private static final List<AuctionPrice> BIDS_EVERY_THIRD = ImmutableList.of(SqlQuery2Test.newAuctionPrice(SqlQuery2Test.BIDS.get(2)), SqlQuery2Test.newAuctionPrice(SqlQuery2Test.BIDS.get(5)));

    @Rule
    public TestPipeline testPipeline = TestPipeline.create();

    @Test
    public void testSkipsEverySecondElement() throws Exception {
        PCollection<Event> bids = testPipeline.apply(Create.of(SqlQuery2Test.BIDS_EVENTS));
        PAssert.that(bids.apply(new SqlQuery2(2))).containsInAnyOrder(SqlQuery2Test.BIDS_EVEN);
        testPipeline.run();
    }

    @Test
    public void testSkipsEveryThirdElement() throws Exception {
        PCollection<Event> bids = testPipeline.apply(Create.of(SqlQuery2Test.BIDS_EVENTS));
        PAssert.that(bids.apply(new SqlQuery2(3))).containsInAnyOrder(SqlQuery2Test.BIDS_EVERY_THIRD);
        testPipeline.run();
    }
}

