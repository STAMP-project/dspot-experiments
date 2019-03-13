/**
 * -\-\-
 * Spotify Apollo Metrics Module
 * --
 * Copyright (C) 2013 - 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.metrics.semantic;


import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableSet;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.metrics.RequestMetrics;
import com.spotify.metrics.core.MetricId;
import com.spotify.metrics.core.SemanticMetricRegistry;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import okio.ByteString;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SemanticServiceMetricsTest {
    private SemanticMetricRegistry metricRegistry;

    private RequestMetrics requestMetrics;

    @Test
    public void shouldTrackFanout() throws Exception {
        requestMetrics.fanout(29);
        MatcherAssert.assertThat(metricRegistry.getMetrics(), Matchers.hasKey(Matchers.hasProperty("tags", Matchers.allOf(Matchers.hasEntry("service", "test-service"), Matchers.hasEntry("what", "request-fanout-factor"), Matchers.hasEntry("endpoint", "GET:/bar"), Matchers.hasEntry("unit", "request/request")))));
    }

    @Test
    public void shouldTrackRequestRate() throws Exception {
        requestMetrics.response(Response.forStatus(Status.FOUND));
        MatcherAssert.assertThat(metricRegistry.getMetrics(), Matchers.hasKey(Matchers.hasProperty("tags", Matchers.allOf(Matchers.hasEntry("service", "test-service"), Matchers.hasEntry("what", "endpoint-request-rate"), Matchers.hasEntry("endpoint", "GET:/bar"), Matchers.hasEntry("status-code", "302"), Matchers.hasEntry("unit", "request")))));
    }

    @Test
    public void shouldTrackRequestDuration() throws Exception {
        MatcherAssert.assertThat(metricRegistry.getMetrics(), Matchers.hasKey(Matchers.hasProperty("tags", Matchers.allOf(Matchers.hasEntry("service", "test-service"), Matchers.hasEntry("what", "endpoint-request-duration"), Matchers.hasEntry("endpoint", "GET:/bar")))));
    }

    @Test
    public void shouldCalculateRequestDurationOnResponse() throws Exception {
        requestMetrics.response(Response.ok());
        Collection<Timer> timers = metricRegistry.getTimers(( metricId, metric) -> metricId.getTags().get("what").equals("endpoint-request-duration")).values();
        MatcherAssert.assertThat(timers.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(timers.iterator().next().getCount(), CoreMatchers.is(1L));
    }

    @Test
    public void shouldCalculateRequestDurationOnDrop() throws Exception {
        requestMetrics.drop();
        Collection<Timer> timers = metricRegistry.getTimers(( metricId, metric) -> metricId.getTags().get("what").equals("endpoint-request-duration")).values();
        MatcherAssert.assertThat(timers.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(timers.iterator().next().getCount(), CoreMatchers.is(1L));
    }

    @Test
    public void shouldTrackOneMinErrorRatio() throws Exception {
        requestMetrics.response(Response.ok());
        MatcherAssert.assertThat(metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio")) && (metricId.getTags().get("stat").equals("1m"))).values(), Matchers.iterableWithSize(1));
    }

    @Test
    public void shouldTrackFiveMinErrorRatio() throws Exception {
        requestMetrics.response(Response.ok());
        MatcherAssert.assertThat(metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio")) && (metricId.getTags().get("stat").equals("5m"))).values(), Matchers.iterableWithSize(1));
    }

    @Test
    public void shouldTrackFifteenMinErrorRatio() throws Exception {
        requestMetrics.response(Response.ok());
        MatcherAssert.assertThat(metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio")) && (metricId.getTags().get("stat").equals("15m"))).values(), Matchers.iterableWithSize(1));
    }

    @Test
    public void shouldTrackOneMinErrorRatio4xx() throws Exception {
        requestMetrics.response(Response.ok());
        MatcherAssert.assertThat(metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio-4xx")) && (metricId.getTags().get("stat").equals("1m"))).values(), Matchers.iterableWithSize(1));
    }

    @Test
    public void shouldTrackFiveMinErrorRatio4xx() throws Exception {
        requestMetrics.response(Response.ok());
        MatcherAssert.assertThat(metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio-4xx")) && (metricId.getTags().get("stat").equals("5m"))).values(), Matchers.iterableWithSize(1));
    }

    @Test
    public void shouldTrackFifteenMinErrorRatio4xx() throws Exception {
        requestMetrics.response(Response.ok());
        MatcherAssert.assertThat(metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio-4xx")) && (metricId.getTags().get("stat").equals("15m"))).values(), Matchers.iterableWithSize(1));
    }

    @Test
    public void shouldTrackOneMinErrorRatio5xx() throws Exception {
        requestMetrics.response(Response.ok());
        MatcherAssert.assertThat(metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio-5xx")) && (metricId.getTags().get("stat").equals("1m"))).values(), Matchers.iterableWithSize(1));
    }

    @Test
    public void shouldTrackFiveMinErrorRatio5xx() throws Exception {
        requestMetrics.response(Response.ok());
        MatcherAssert.assertThat(metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio-5xx")) && (metricId.getTags().get("stat").equals("5m"))).values(), Matchers.iterableWithSize(1));
    }

    @Test
    public void shouldTrackFifteenMinErrorRatio5xx() throws Exception {
        requestMetrics.response(Response.ok());
        MatcherAssert.assertThat(metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio-5xx")) && (metricId.getTags().get("stat").equals("15m"))).values(), Matchers.iterableWithSize(1));
    }

    @Test
    public void shouldCalculateOneMinErrorRatio() throws Exception {
        requestMetrics.response(Response.ok());
        requestMetrics.response(Response.forStatus(Status.INTERNAL_SERVER_ERROR));
        // noinspection OptionalGetWithoutIsPresent
        // the test above will fail if there's not exactly 1 such element
        Gauge oneMin = metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio")) && (metricId.getTags().get("stat").equals("1m"))).values().stream().findFirst().get();
        // semantic metrics Meters take some time to update
        await().atMost(15, TimeUnit.SECONDS).until(() -> (((Double) (oneMin.getValue())) > 0.3) && (((Double) (oneMin.getValue())) < 0.7));
    }

    @Test
    public void shouldCalculateOneMinErrorRatio4xx() throws Exception {
        requestMetrics.response(Response.ok());
        requestMetrics.response(Response.forStatus(Status.BAD_REQUEST));
        // noinspection OptionalGetWithoutIsPresent
        // the test above will fail if there's not exactly 1 such element
        Gauge oneMin = metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio-4xx")) && (metricId.getTags().get("stat").equals("1m"))).values().stream().findFirst().get();
        // semantic metrics Meters take some time to update
        await().atMost(15, TimeUnit.SECONDS).until(() -> (((Double) (oneMin.getValue())) > 0.3) && (((Double) (oneMin.getValue())) < 0.7));
    }

    @Test
    public void shouldCalculateOneMinErrorRatio5xx() throws Exception {
        requestMetrics.response(Response.ok());
        requestMetrics.response(Response.forStatus(Status.INTERNAL_SERVER_ERROR));
        // noinspection OptionalGetWithoutIsPresent
        // the test above will fail if there's not exactly 1 such element
        Gauge oneMin = metricRegistry.getGauges(( metricId, metric) -> (metricId.getTags().get("what").equals("error-ratio-5xx")) && (metricId.getTags().get("stat").equals("1m"))).values().stream().findFirst().get();
        // semantic metrics Meters take some time to update
        await().atMost(15, TimeUnit.SECONDS).until(() -> (((Double) (oneMin.getValue())) > 0.3) && (((Double) (oneMin.getValue())) < 0.7));
    }

    @Test
    public void shouldCountDroppedRequests() throws Exception {
        requestMetrics.drop();
        Collection<Meter> meters = metricRegistry.getMeters(( metricId, metric) -> metricId.getTags().get("what").equals("dropped-request-rate")).values();
        MatcherAssert.assertThat(meters.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(meters.iterator().next().getCount(), CoreMatchers.is(1L));
    }

    @Test
    public void shouldCalculateResponseSizes() throws Exception {
        requestMetrics.response(Response.forPayload(ByteString.encodeUtf8("this has non-zero size")));
        Collection<Histogram> histograms = metricRegistry.getHistograms(( metricId, metric) -> (metricId.getTags().get("what").equals("response-payload-size")) && (metricId.getTags().get("unit").equals("B"))).values();
        MatcherAssert.assertThat(histograms, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(histograms.iterator().next().getCount(), CoreMatchers.is(1L));
    }

    @Test
    public void shouldCalculateRequestSizes() throws Exception {
        requestMetrics.incoming(Request.forUri("hm://foo").withPayload(ByteString.encodeUtf8("small, but nice")));
        Collection<Histogram> histograms = metricRegistry.getHistograms(( metricId, metric) -> (metricId.getTags().get("what").equals("request-payload-size")) && (metricId.getTags().get("unit").equals("B"))).values();
        MatcherAssert.assertThat(histograms, Matchers.iterableWithSize(1));
        MatcherAssert.assertThat(histograms.iterator().next().getCount(), CoreMatchers.is(1L));
    }

    @Test
    public void shouldSupportDisablingFanout() throws Exception {
        setupWithPredicate(( what) -> what != (What.REQUEST_FANOUT_FACTOR));
        requestMetrics.fanout(3240);
        assertNotInRegistry(What.REQUEST_FANOUT_FACTOR);
    }

    @Test
    public void shouldSupportDisablingRequestRate() throws Exception {
        setupWithPredicate(( what) -> what != (What.ENDPOINT_REQUEST_RATE));
        requestMetrics.response(Response.ok());
        assertNotInRegistry(What.ENDPOINT_REQUEST_RATE);
    }

    @Test
    public void shouldSupportDisablingRequestDuration() throws Exception {
        setupWithPredicate(( what) -> what != (What.ENDPOINT_REQUEST_DURATION));
        requestMetrics.response(Response.ok());
        assertNotInRegistry(What.ENDPOINT_REQUEST_DURATION);
    }

    @Test
    public void shouldSupportDisablingDropRate() throws Exception {
        setupWithPredicate(( what) -> what != (What.DROPPED_REQUEST_RATE));
        requestMetrics.drop();
        assertNotInRegistry(What.DROPPED_REQUEST_RATE);
    }

    @Test
    public void shouldSupportDisablingErrorRatio() throws Exception {
        setupWithPredicate(( what) -> what != (What.ERROR_RATIO));
        requestMetrics.response(Response.ok());
        assertNotInRegistry(What.ERROR_RATIO);
    }

    @Test
    public void shouldSupportDisablingRequestSize() throws Exception {
        setupWithPredicate(( what) -> what != (What.REQUEST_PAYLOAD_SIZE));
        requestMetrics.response(Response.forPayload(ByteString.encodeUtf8("flop")));
        assertNotInRegistry(What.REQUEST_PAYLOAD_SIZE);
    }

    @Test
    public void shouldSupportDisablingResponseSize() throws Exception {
        setupWithPredicate(( what) -> what != (What.RESPONSE_PAYLOAD_SIZE));
        requestMetrics.response(Response.forPayload(ByteString.encodeUtf8("flop")));
        assertNotInRegistry(What.RESPONSE_PAYLOAD_SIZE);
    }

    @Test
    public void shouldPrecreateMetersForDefinedStatusCodes() throws Exception {
        setupWith(( what) -> true, ImmutableSet.of(200, 503), DurationThresholdConfig.parseConfig(ConfigFactory.empty()));
        Map<MetricId, Meter> meters = metricRegistry.getMeters(( metricId, metric) -> metricId.getTags().get("what").equals("endpoint-request-rate"));
        MatcherAssert.assertThat(meters.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(meters.keySet(), Matchers.containsInAnyOrder(meterWithTag("status-code", "200"), meterWithTag("status-code", "503")));
    }

    @Test
    public void shouldTrackDurationThresholdIndividual() throws Exception {
        final Config config = ConfigFactory.parseString("endpoint-duration-goal./bar.GET = 200");
        setupWith(( what) -> true, Collections.emptySet(), DurationThresholdConfig.parseConfig(config));
        MatcherAssert.assertThat(metricRegistry.getMetrics(), Matchers.hasKey(Matchers.hasProperty("tags", Matchers.allOf(Matchers.hasEntry("service", "test-service"), Matchers.hasEntry("what", What.ENDPOINT_REQUEST_DURATION_THRESHOLD_RATE.tag()), Matchers.hasEntry("endpoint", "GET:/bar"), Matchers.hasEntry("threshold", "200")))));
    }

    @Test
    public void shouldTrackDurationThresholdGlobal() throws Exception {
        final Config config = ConfigFactory.parseString("endpoint-duration-goal.all-endpoints = 400");
        setupWith(( what) -> true, Collections.emptySet(), DurationThresholdConfig.parseConfig(config));
        MatcherAssert.assertThat(metricRegistry.getMetrics(), Matchers.hasKey(Matchers.hasProperty("tags", Matchers.allOf(Matchers.hasEntry("service", "test-service"), Matchers.hasEntry("what", What.ENDPOINT_REQUEST_DURATION_THRESHOLD_RATE.tag()), Matchers.hasEntry("endpoint", "GET:/bar"), Matchers.hasEntry("threshold", "400")))));
    }

    @Test
    public void shouldNotTrackDurationThresholdWrongEndpoint() throws Exception {
        final Config config = ConfigFactory.parseString("endpoint-duration-goal.fake-endpoint.GET = 400");
        setupWith(( what) -> true, Collections.emptySet(), DurationThresholdConfig.parseConfig(config));
        MatcherAssert.assertThat(metricRegistry.getMetrics(), Matchers.not(Matchers.hasKey(Matchers.hasProperty("tags", Matchers.allOf(Matchers.hasEntry("service", "test-service"), Matchers.hasEntry("what", What.ENDPOINT_REQUEST_DURATION_THRESHOLD_RATE.tag()), Matchers.hasEntry("endpoint", "GET:/bar"), Matchers.hasEntry("threshold", "400"))))));
    }

    @Test
    public void shouldNotTrackDurationThresholdNoConfig() throws Exception {
        MatcherAssert.assertThat(metricRegistry.getMetrics(), Matchers.not(Matchers.hasKey(Matchers.hasProperty("tags", Matchers.allOf(Matchers.hasEntry("service", "test-service"), Matchers.hasEntry("what", What.ENDPOINT_REQUEST_DURATION_THRESHOLD_RATE.tag()), Matchers.hasEntry("endpoint", "GET:/bar"), Matchers.hasEntry("threshold", "400"))))));
    }
}

