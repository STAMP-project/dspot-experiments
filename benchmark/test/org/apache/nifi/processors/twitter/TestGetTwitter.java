/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.twitter;


import GetTwitter.ACCESS_TOKEN;
import GetTwitter.ACCESS_TOKEN_SECRET;
import GetTwitter.CONSUMER_KEY;
import GetTwitter.CONSUMER_SECRET;
import GetTwitter.ENDPOINT;
import GetTwitter.ENDPOINT_FILTER;
import GetTwitter.LOCATIONS;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class TestGetTwitter {
    @Test
    public void testLocationValidatorWithValidLocations() {
        final TestRunner runner = TestRunners.newTestRunner(GetTwitter.class);
        runner.setProperty(ENDPOINT, ENDPOINT_FILTER);
        runner.setProperty(CONSUMER_KEY, "consumerKey");
        runner.setProperty(CONSUMER_SECRET, "consumerSecret");
        runner.setProperty(ACCESS_TOKEN, "accessToken");
        runner.setProperty(ACCESS_TOKEN_SECRET, "accessTokenSecret");
        runner.setProperty(LOCATIONS, "-122.75,36.8,-121.75,37.8,-74,40,-73,41");
        runner.assertValid();
    }

    @Test
    public void testLocationValidatorWithEqualLatitudes() {
        final TestRunner runner = TestRunners.newTestRunner(GetTwitter.class);
        runner.setProperty(ENDPOINT, ENDPOINT_FILTER);
        runner.setProperty(CONSUMER_KEY, "consumerKey");
        runner.setProperty(CONSUMER_SECRET, "consumerSecret");
        runner.setProperty(ACCESS_TOKEN, "accessToken");
        runner.setProperty(ACCESS_TOKEN_SECRET, "accessTokenSecret");
        runner.setProperty(LOCATIONS, "-122.75,36.8,-121.75,37.8,-74,40,-73,40");
        runner.assertNotValid();
    }

    @Test
    public void testLocationValidatorWithEqualLongitudes() {
        final TestRunner runner = TestRunners.newTestRunner(GetTwitter.class);
        runner.setProperty(ENDPOINT, ENDPOINT_FILTER);
        runner.setProperty(CONSUMER_KEY, "consumerKey");
        runner.setProperty(CONSUMER_SECRET, "consumerSecret");
        runner.setProperty(ACCESS_TOKEN, "accessToken");
        runner.setProperty(ACCESS_TOKEN_SECRET, "accessTokenSecret");
        runner.setProperty(LOCATIONS, "-122.75,36.8,-121.75,37.8,-74,40,-74,41");
        runner.assertNotValid();
    }

    @Test
    public void testLocationValidatorWithSWLatGreaterThanNELat() {
        final TestRunner runner = TestRunners.newTestRunner(GetTwitter.class);
        runner.setProperty(ENDPOINT, ENDPOINT_FILTER);
        runner.setProperty(CONSUMER_KEY, "consumerKey");
        runner.setProperty(CONSUMER_SECRET, "consumerSecret");
        runner.setProperty(ACCESS_TOKEN, "accessToken");
        runner.setProperty(ACCESS_TOKEN_SECRET, "accessTokenSecret");
        runner.setProperty(LOCATIONS, "-122.75,36.8,-121.75,37.8,-74,40,-73,39");
        runner.assertNotValid();
    }

    @Test
    public void testLocationValidatorWithSWLonGreaterThanNELon() {
        final TestRunner runner = TestRunners.newTestRunner(GetTwitter.class);
        runner.setProperty(ENDPOINT, ENDPOINT_FILTER);
        runner.setProperty(CONSUMER_KEY, "consumerKey");
        runner.setProperty(CONSUMER_SECRET, "consumerSecret");
        runner.setProperty(ACCESS_TOKEN, "accessToken");
        runner.setProperty(ACCESS_TOKEN_SECRET, "accessTokenSecret");
        runner.setProperty(LOCATIONS, "-122.75,36.8,-121.75,37.8,-74,40,-75,41");
        runner.assertNotValid();
    }
}

