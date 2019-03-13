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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.NotFoundException;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 * Tests for {@link MetricsApplicationEventListener}.
 *
 * @author Michael Weirauch
 * @author Johnny Lim
 */
public class MetricsRequestEventListenerTest extends JerseyTest {
    static {
        Logger.getLogger("org.glassfish.jersey").setLevel(Level.OFF);
    }

    private static final String METRIC_NAME = "http.server.requests";

    private MeterRegistry registry;

    @Test
    public void resourcesAreTimed() {
        target("/").request().get();
        target("hello").request().get();
        target("hello/").request().get();
        target("hello/peter").request().get();
        target("sub-resource/sub-hello/peter").request().get();
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("root", "200", "SUCCESS", null)).timer().count()).isEqualTo(1);
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("/hello", "200", "SUCCESS", null)).timer().count()).isEqualTo(2);
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("/hello/{name}", "200", "SUCCESS", null)).timer().count()).isEqualTo(1);
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("/sub-resource/sub-hello/{name}", "200", "SUCCESS", null)).timer().count()).isEqualTo(1);
        // assert we are not auto-timing long task @Timed
        assertThat(registry.getMeters()).hasSize(4);
    }

    @Test
    public void notFoundIsAccumulatedUnderSameUri() {
        try {
            target("not-found").request().get();
        } catch (NotFoundException ignored) {
        }
        try {
            target("throws-not-found-exception").request().get();
        } catch (NotFoundException ignored) {
        }
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("NOT_FOUND", "404", "CLIENT_ERROR", null)).timer().count()).isEqualTo(2);
    }

    @Test
    public void redirectsAreAccumulatedUnderSameUri() {
        target("redirect/302").request().get();
        target("redirect/307").request().get();
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("REDIRECTION", "302", "REDIRECTION", null)).timer().count()).isEqualTo(1);
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("REDIRECTION", "307", "REDIRECTION", null)).timer().count()).isEqualTo(1);
    }

    @Test
    public void exceptionsAreMappedCorrectly() {
        try {
            target("throws-exception").request().get();
        } catch (Exception ignored) {
        }
        try {
            target("throws-webapplication-exception").request().get();
        } catch (Exception ignored) {
        }
        try {
            target("throws-mappable-exception").request().get();
        } catch (Exception ignored) {
        }
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("/throws-exception", "500", "SERVER_ERROR", "IllegalArgumentException")).timer().count()).isEqualTo(1);
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("/throws-webapplication-exception", "401", "CLIENT_ERROR", "NotAuthorizedException")).timer().count()).isEqualTo(1);
        assertThat(registry.get(MetricsRequestEventListenerTest.METRIC_NAME).tags(MetricsRequestEventListenerTest.tagsFrom("/throws-mappable-exception", "410", "CLIENT_ERROR", "ResourceGoneException")).timer().count()).isEqualTo(1);
    }
}

