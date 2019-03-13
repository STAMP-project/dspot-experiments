/**
 * Copyright 2014 Google Inc. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.google.maps;


import DistanceMatrixElementStatus.OK;
import RouteRestriction.TOLLS;
import TrafficModel.PESSIMISTIC;
import TravelMode.BICYCLING;
import TravelMode.DRIVING;
import TravelMode.TRANSIT;
import Unit.IMPERIAL;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MediumTests.class)
public class DistanceMatrixApiTest {
    private final String getDistanceMatrixWithBasicStringParams;

    public DistanceMatrixApiTest() {
        getDistanceMatrixWithBasicStringParams = TestUtils.retrieveBody("GetDistanceMatrixWithBasicStringParams.json");
    }

    @Test
    public void testLatLngOriginDestinations() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            DistanceMatrixApi.newRequest(sc.context).origins(new LatLng((-31.9522), 115.8589), new LatLng((-37.8136), 144.9631)).destinations(new LatLng((-25.344677), 131.036692), new LatLng((-13.092297), 132.394057)).awaitIgnoreError();
            sc.assertParamValue("-31.95220000,115.85890000|-37.81360000,144.96310000", "origins");
            sc.assertParamValue("-25.34467700,131.03669200|-13.09229700,132.39405700", "destinations");
        }
    }

    @Test
    public void testGetDistanceMatrixWithBasicStringParams() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(getDistanceMatrixWithBasicStringParams)) {
            String[] origins = new String[]{ "Perth, Australia", "Sydney, Australia", "Melbourne, Australia", "Adelaide, Australia", "Brisbane, Australia", "Darwin, Australia", "Hobart, Australia", "Canberra, Australia" };
            String[] destinations = new String[]{ "Uluru, Australia", "Kakadu, Australia", "Blue Mountains, Australia", "Bungle Bungles, Australia", "The Pinnacles, Australia" };
            DistanceMatrix matrix = DistanceMatrixApi.getDistanceMatrix(sc.context, origins, destinations).await();
            Assert.assertNotNull(matrix.toString());
            Assert.assertNotNull(Arrays.toString(matrix.rows));
            // Rows length will match the number of origin elements, regardless of whether they're
            // routable.
            Assert.assertEquals(8, matrix.rows.length);
            Assert.assertEquals(5, matrix.rows[0].elements.length);
            Assert.assertEquals(OK, matrix.rows[0].elements[0].status);
            Assert.assertEquals("Perth WA, Australia", matrix.originAddresses[0]);
            Assert.assertEquals("Sydney NSW, Australia", matrix.originAddresses[1]);
            Assert.assertEquals("Melbourne VIC, Australia", matrix.originAddresses[2]);
            Assert.assertEquals("Adelaide SA, Australia", matrix.originAddresses[3]);
            Assert.assertEquals("Brisbane QLD, Australia", matrix.originAddresses[4]);
            Assert.assertEquals("Darwin NT, Australia", matrix.originAddresses[5]);
            Assert.assertEquals("Hobart TAS 7000, Australia", matrix.originAddresses[6]);
            Assert.assertEquals("Canberra ACT 2601, Australia", matrix.originAddresses[7]);
            Assert.assertEquals("Uluru, Petermann NT 0872, Australia", matrix.destinationAddresses[0]);
            Assert.assertEquals("Kakadu NT 0822, Australia", matrix.destinationAddresses[1]);
            Assert.assertEquals("Blue Mountains, New South Wales, Australia", matrix.destinationAddresses[2]);
            Assert.assertEquals("Purnululu National Park, Western Australia 6770, Australia", matrix.destinationAddresses[3]);
            Assert.assertEquals("Pinnacles Drive, Cervantes WA 6511, Australia", matrix.destinationAddresses[4]);
            sc.assertParamValue(StringUtils.join(origins, "|"), "origins");
            sc.assertParamValue(StringUtils.join(destinations, "|"), "destinations");
        }
    }

    @Test
    public void testNewRequestWithAllPossibleParams() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            String[] origins = new String[]{ "Perth, Australia", "Sydney, Australia", "Melbourne, Australia", "Adelaide, Australia", "Brisbane, Australia", "Darwin, Australia", "Hobart, Australia", "Canberra, Australia" };
            String[] destinations = new String[]{ "Uluru, Australia", "Kakadu, Australia", "Blue Mountains, Australia", "Bungle Bungles, Australia", "The Pinnacles, Australia" };
            // this is ignored when an API key is used
            DistanceMatrixApi.newRequest(sc.context).origins(origins).destinations(destinations).mode(DRIVING).language("en-AU").avoid(TOLLS).units(IMPERIAL).departureTime(Instant.now().plus(Duration.ofMinutes(2))).await();
            sc.assertParamValue(StringUtils.join(origins, "|"), "origins");
            sc.assertParamValue(StringUtils.join(destinations, "|"), "destinations");
            sc.assertParamValue(DRIVING.toUrlValue(), "mode");
            sc.assertParamValue("en-AU", "language");
            sc.assertParamValue(TOLLS.toUrlValue(), "avoid");
            sc.assertParamValue(IMPERIAL.toUrlValue(), "units");
        }
    }

    /**
     * Test the language parameter.
     *
     * <p>Sample request: <a
     * href="http://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&mode=bicycling&language=fr-FR">
     * origins: Vancouver BC|Seattle, destinations: San Francisco|Victoria BC, mode: bicycling,
     * language: french</a>.
     */
    @Test
    public void testLanguageParameter() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            String[] origins = new String[]{ "Vancouver BC", "Seattle" };
            String[] destinations = new String[]{ "San Francisco", "Victoria BC" };
            DistanceMatrixApi.newRequest(sc.context).origins(origins).destinations(destinations).mode(BICYCLING).language("fr-FR").await();
            sc.assertParamValue(StringUtils.join(origins, "|"), "origins");
            sc.assertParamValue(StringUtils.join(destinations, "|"), "destinations");
            sc.assertParamValue(BICYCLING.toUrlValue(), "mode");
            sc.assertParamValue("fr-FR", "language");
        }
    }

    /**
     * Test transit without arrival or departure times specified.
     */
    @Test
    public void testTransitWithoutSpecifyingTime() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            String[] origins = new String[]{ "Fisherman's Wharf, San Francisco", "Union Square, San Francisco" };
            String[] destinations = new String[]{ "Mikkeller Bar, San Francisco", "Moscone Center, San Francisco" };
            DistanceMatrixApi.newRequest(sc.context).origins(origins).destinations(destinations).mode(TRANSIT).await();
            sc.assertParamValue(StringUtils.join(origins, "|"), "origins");
            sc.assertParamValue(StringUtils.join(destinations, "|"), "destinations");
            sc.assertParamValue(TRANSIT.toUrlValue(), "mode");
        }
    }

    /**
     * Test duration in traffic with traffic model set.
     */
    @Test
    public void testDurationInTrafficWithTrafficModel() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            final long ONE_HOUR_MILLIS = (60 * 60) * 1000;
            DistanceMatrixApi.newRequest(sc.context).origins("Fisherman's Wharf, San Francisco").destinations("San Francisco International Airport, San Francisco, CA").mode(DRIVING).trafficModel(PESSIMISTIC).departureTime(Instant.ofEpochMilli(((System.currentTimeMillis()) + ONE_HOUR_MILLIS))).await();
            sc.assertParamValue("Fisherman's Wharf, San Francisco", "origins");
            sc.assertParamValue("San Francisco International Airport, San Francisco, CA", "destinations");
            sc.assertParamValue(DRIVING.toUrlValue(), "mode");
            sc.assertParamValue(PESSIMISTIC.toUrlValue(), "traffic_model");
        }
    }
}

