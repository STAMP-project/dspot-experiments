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
package org.apache.beam.sdk.extensions.sql.jdbc;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.io.gcp.pubsub.TestPubsub;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsIn;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * BeamSqlLine integration tests.
 */
public class BeamSqlLineIT implements Serializable {
    @Rule
    public transient TestPubsub eventsTopic = TestPubsub.create();

    private static String project;

    private static String createPubsubTableStatement;

    private static String setProject;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ExecutorService pool;

    @Test
    public void testSelectFromPubsub() throws Exception {
        String[] args = BeamSqlLineTestingUtils.buildArgs(String.format(BeamSqlLineIT.createPubsubTableStatement, eventsTopic.topicPath()), BeamSqlLineIT.setProject, ("SELECT event_timestamp, taxi_rides.payload.ride_status, taxi_rides.payload.latitude, " + "taxi_rides.payload.longitude from taxi_rides LIMIT 3;"));
        Future<List<List<String>>> expectedResult = runQueryInBackground(args);
        eventsTopic.checkIfAnySubscriptionExists(BeamSqlLineIT.project, Duration.standardMinutes(1));
        List<PubsubMessage> messages = ImmutableList.of(message(convertTimestampToMillis("2018-07-01 21:25:20"), taxiRideJSON("id1", 1, 40.702, (-74.001), 1000, 10, "enroute", 2)), message(convertTimestampToMillis("2018-07-01 21:26:06"), taxiRideJSON("id2", 2, 40.703, (-74.002), 1000, 10, "enroute", 4)), message(convertTimestampToMillis("2018-07-02 13:26:06"), taxiRideJSON("id3", 3, 30.0, (-72.32324), 2000, 20, "enroute", 7)));
        eventsTopic.publish(messages);
        Assert.assertThat(Arrays.asList(Arrays.asList("2018-07-01 21:25:20", "enroute", "40.702", "-74.001"), Arrays.asList("2018-07-01 21:26:06", "enroute", "40.703", "-74.002"), Arrays.asList("2018-07-02 13:26:06", "enroute", "30.0", "-72.32324")), CoreMatchers.everyItem(IsIn.isOneOf(expectedResult.get(30, TimeUnit.SECONDS).toArray())));
    }

    @Test
    public void testFilterForSouthManhattan() throws Exception {
        String[] args = BeamSqlLineTestingUtils.buildArgs(String.format(BeamSqlLineIT.createPubsubTableStatement, eventsTopic.topicPath()), BeamSqlLineIT.setProject, ("SELECT event_timestamp, taxi_rides.payload.ride_status, \n" + (((("taxi_rides.payload.latitude, taxi_rides.payload.longitude from taxi_rides\n" + "       WHERE taxi_rides.payload.longitude > -74.747\n") + "         AND taxi_rides.payload.longitude < -73.969\n") + "         AND taxi_rides.payload.latitude > 40.699\n") + "         AND taxi_rides.payload.latitude < 40.720 LIMIT 2;")));
        Future<List<List<String>>> expectedResult = runQueryInBackground(args);
        eventsTopic.checkIfAnySubscriptionExists(BeamSqlLineIT.project, Duration.standardMinutes(1));
        List<PubsubMessage> messages = ImmutableList.of(message(convertTimestampToMillis("2018-07-01 21:25:20"), taxiRideJSON("id1", 1, 40.701, (-74.001), 1000, 10, "enroute", 2)), message(convertTimestampToMillis("2018-07-01 21:26:06"), taxiRideJSON("id2", 2, 40.702, (-74.002), 1000, 10, "enroute", 4)), message(convertTimestampToMillis("2018-07-02 13:26:06"), taxiRideJSON("id3", 3, 30, (-72.32324), 2000, 20, "enroute", 7)), message(convertTimestampToMillis("2018-07-02 14:28:22"), taxiRideJSON("id4", 4, 34, (-73.32324), 2000, 20, "enroute", 8)));
        eventsTopic.publish(messages);
        Assert.assertThat(Arrays.asList(Arrays.asList("2018-07-01 21:25:20", "enroute", "40.701", "-74.001"), Arrays.asList("2018-07-01 21:26:06", "enroute", "40.702", "-74.002")), CoreMatchers.everyItem(IsIn.isOneOf(expectedResult.get(30, TimeUnit.SECONDS).toArray())));
    }
}

