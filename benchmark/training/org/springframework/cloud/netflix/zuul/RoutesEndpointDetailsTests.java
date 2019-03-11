/**
 * Copyright 2013-2019 the original author or authors.
 *
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
 */
package org.springframework.cloud.netflix.zuul;


import RoutesEndpoint.FORMAT_DETAILS;
import RoutesEndpoint.RouteDetails;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;


/**
 *
 *
 * @author Ryan Baxter
 * @author Gregor Zurowski
 */
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class RoutesEndpointDetailsTests {
    private RouteLocator locator;

    private RoutesEndpoint endpoint;

    @Mock
    private ApplicationEventPublisher publisher;

    @Test
    public void reset() throws Exception {
        this.endpoint.setApplicationEventPublisher(publisher);
        Map<String, String> result = new HashMap<>();
        for (Route r : locator.getRoutes()) {
            result.put(r.getFullPath(), r.getLocation());
        }
        assertThat(endpoint.reset()).isEqualTo(result);
        Mockito.verify(endpoint, Mockito.times(1)).invoke();
        Mockito.verify(publisher, Mockito.times(1)).publishEvent(ArgumentMatchers.isA(RoutesRefreshedEvent.class));
    }

    @Test
    public void routeDetails() throws Exception {
        Map<String, RoutesEndpoint.RouteDetails> results = new HashMap<>();
        for (Route route : locator.getRoutes()) {
            results.put(route.getFullPath(), new RoutesEndpoint.RouteDetails(route));
        }
        assertThat(this.endpoint.invokeRouteDetails(FORMAT_DETAILS)).isEqualTo(results);
        Mockito.verify(endpoint, Mockito.times(1)).invokeRouteDetails();
    }
}

