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
import org.apache.beam.sdk.nexmark.NexmarkConfiguration;
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
 * Unit tests for {@link SqlQuery7}.
 */
public class SqlQuery7Test {
    private static final NexmarkConfiguration config = new NexmarkConfiguration();

    private static final List<Bid> BIDS = ImmutableList.of(SqlQuery7Test.newBid(4L, 3L, 2L, 1L), SqlQuery7Test.newBid(1L, 2L, 3L, 2L), SqlQuery7Test.newBid(2L, 2L, 3L, 2L), SqlQuery7Test.newBid(2L, 2L, 4L, 3L), SqlQuery7Test.newBid(2L, 2L, 5L, 3L));

    private static final List<Event> BIDS_EVENTS = ImmutableList.of(new Event(SqlQuery7Test.BIDS.get(0)), new Event(SqlQuery7Test.BIDS.get(1)), new Event(SqlQuery7Test.BIDS.get(2)), new Event(SqlQuery7Test.BIDS.get(3)), new Event(SqlQuery7Test.BIDS.get(4)));

    public static final List<Bid> RESULTS = ImmutableList.of(SqlQuery7Test.BIDS.get(0), SqlQuery7Test.BIDS.get(1), SqlQuery7Test.BIDS.get(2), SqlQuery7Test.BIDS.get(4));

    @Rule
    public TestPipeline testPipeline = TestPipeline.create();

    @Test
    public void testBids() throws Exception {
        PCollection<Event> bids = testPipeline.apply(Create.of(SqlQuery7Test.BIDS_EVENTS));
        PAssert.that(bids.apply(new SqlQuery7(SqlQuery7Test.config))).containsInAnyOrder(SqlQuery7Test.RESULTS);
        testPipeline.run();
    }
}

