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
import org.apache.beam.sdk.nexmark.model.Auction;
import org.apache.beam.sdk.nexmark.model.Event;
import org.apache.beam.sdk.nexmark.model.NameCityStateId;
import org.apache.beam.sdk.nexmark.model.Person;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;


/**
 * Unit tests for {@link SqlQuery3}.
 */
public class SqlQuery3Test {
    private static final List<Person> PEOPLE = // matches query
    // matches query
    // matches query
    ImmutableList.of(SqlQuery3Test.newPerson(0L, "WA"), SqlQuery3Test.newPerson(1L, "CA"), SqlQuery3Test.newPerson(2L, "OR"), SqlQuery3Test.newPerson(3L, "ID"), SqlQuery3Test.newPerson(4L, "NY"));

    private static final List<Auction> AUCTIONS = // matches query
    // matches query
    // matches query
    // matches query
    ImmutableList.of(SqlQuery3Test.newAuction(0L, 0L, 5L), SqlQuery3Test.newAuction(1L, 1L, 10L), SqlQuery3Test.newAuction(2L, 2L, 5L), SqlQuery3Test.newAuction(3L, 3L, 10L), SqlQuery3Test.newAuction(4L, 4L, 5L), SqlQuery3Test.newAuction(5L, 0L, 5L), SqlQuery3Test.newAuction(6L, 1L, 10L), SqlQuery3Test.newAuction(7L, 2L, 5L), SqlQuery3Test.newAuction(8L, 3L, 10L), SqlQuery3Test.newAuction(9L, 4L, 5L));

    private static final List<Event> PEOPLE_AND_AUCTIONS_EVENTS = ImmutableList.of(new Event(SqlQuery3Test.PEOPLE.get(0)), new Event(SqlQuery3Test.AUCTIONS.get(0)), new Event(SqlQuery3Test.PEOPLE.get(1)), new Event(SqlQuery3Test.AUCTIONS.get(1)), new Event(SqlQuery3Test.PEOPLE.get(2)), new Event(SqlQuery3Test.AUCTIONS.get(2)), new Event(SqlQuery3Test.PEOPLE.get(3)), new Event(SqlQuery3Test.AUCTIONS.get(3)), new Event(SqlQuery3Test.AUCTIONS.get(4)), new Event(SqlQuery3Test.AUCTIONS.get(5)), new Event(SqlQuery3Test.AUCTIONS.get(6)), new Event(SqlQuery3Test.PEOPLE.get(4)), new Event(SqlQuery3Test.AUCTIONS.get(2)), new Event(SqlQuery3Test.AUCTIONS.get(7)), new Event(SqlQuery3Test.AUCTIONS.get(8)), new Event(SqlQuery3Test.AUCTIONS.get(9)));

    public static final List<NameCityStateId> RESULTS = ImmutableList.of(new NameCityStateId("name_1", "city_1", "CA", 1L), new NameCityStateId("name_3", "city_3", "ID", 3L), new NameCityStateId("name_1", "city_1", "CA", 6L), new NameCityStateId("name_3", "city_3", "ID", 8L));

    @Rule
    public TestPipeline testPipeline = TestPipeline.create();

    @Test
    public void testJoinsPeopleWithAuctions() throws Exception {
        PCollection<Event> events = testPipeline.apply(Create.of(SqlQuery3Test.PEOPLE_AND_AUCTIONS_EVENTS));
        PAssert.that(events.apply(new SqlQuery3(new NexmarkConfiguration()))).containsInAnyOrder(SqlQuery3Test.RESULTS);
        testPipeline.run();
    }
}

