/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.jersey2.server;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 *
 *
 * @author Michael Weirauch
 */
public class MetricsRequestEventListenerTimedTest extends JerseyTest {
    static {
        Logger.getLogger("org.glassfish.jersey").setLevel(Level.OFF);
    }

    private static final String METRIC_NAME = "http.server.requests";

    private MeterRegistry registry;

    private CountDownLatch longTaskRequestStartedLatch;

    private CountDownLatch longTaskRequestReleaseLatch;

    @Test
    public void resourcesAndNotFoundsAreNotAutoTimed() {
        target("not-timed").request().get();
        target("not-found").request().get();
        assertThat(registry.find(MetricsRequestEventListenerTimedTest.METRIC_NAME).tags(MetricsRequestEventListenerTimedTest.tagsFrom("/not-timed", 200)).timer()).isNull();
        assertThat(registry.find(MetricsRequestEventListenerTimedTest.METRIC_NAME).tags(MetricsRequestEventListenerTimedTest.tagsFrom("NOT_FOUND", 404)).timer()).isNull();
    }

    @Test
    public void resourcesWithAnnotationAreTimed() {
        target("timed").request().get();
        target("multi-timed").request().get();
        assertThat(registry.get(MetricsRequestEventListenerTimedTest.METRIC_NAME).tags(MetricsRequestEventListenerTimedTest.tagsFrom("/timed", 200)).timer().count()).isEqualTo(1);
        assertThat(registry.get("multi1").tags(MetricsRequestEventListenerTimedTest.tagsFrom("/multi-timed", 200)).timer().count()).isEqualTo(1);
        assertThat(registry.get("multi2").tags(MetricsRequestEventListenerTimedTest.tagsFrom("/multi-timed", 200)).timer().count()).isEqualTo(1);
    }

    @Test
    public void longTaskTimerSupported() throws InterruptedException, ExecutionException {
        final Future<Response> future = target("long-timed").request().async().get();
        /* Wait until the request has arrived at the server side. (Async client
        processing might be slower in triggering the request resulting in the
        assertions below to fail. Thread.sleep() is not an option, so resort
        to CountDownLatch.)
         */
        longTaskRequestStartedLatch.await();
        // the request is not timed, yet
        assertThat(registry.find(MetricsRequestEventListenerTimedTest.METRIC_NAME).tags(MetricsRequestEventListenerTimedTest.tagsFrom("/timed", 200)).timer()).isNull();
        // the long running task is timed
        assertThat(registry.get("long.task.in.request").tags(Tags.of("method", "GET", "uri", "/long-timed")).longTaskTimer().activeTasks()).isEqualTo(1);
        // finish the long running request
        longTaskRequestReleaseLatch.countDown();
        future.get();
        // the request is timed after the long running request completed
        assertThat(registry.get(MetricsRequestEventListenerTimedTest.METRIC_NAME).tags(MetricsRequestEventListenerTimedTest.tagsFrom("/long-timed", 200)).timer().count()).isEqualTo(1);
    }

    @Test
    public void unnamedLongTaskTimerIsNotSupported() {
        assertThatExceptionOfType(ProcessingException.class).isThrownBy(() -> target("long-timed-unnamed").request().get()).withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void classLevelAnnotationIsInherited() {
        target("/class/inherited").request().get();
        assertThat(registry.get(MetricsRequestEventListenerTimedTest.METRIC_NAME).tags(Tags.concat(MetricsRequestEventListenerTimedTest.tagsFrom("/class/inherited", 200), Tags.of("on", "class"))).timer().count()).isEqualTo(1);
    }

    @Test
    public void methodLevelAnnotationOverridesClassLevel() {
        target("/class/on-method").request().get();
        assertThat(registry.get(MetricsRequestEventListenerTimedTest.METRIC_NAME).tags(Tags.concat(MetricsRequestEventListenerTimedTest.tagsFrom("/class/on-method", 200), Tags.of("on", "method"))).timer().count()).isEqualTo(1);
        // class level annotation is not picked up
        assertThat(registry.getMeters()).hasSize(1);
    }
}

