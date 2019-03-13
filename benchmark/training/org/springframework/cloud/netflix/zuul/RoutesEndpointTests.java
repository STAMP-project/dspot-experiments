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


import RoutesEndpoint.RouteDetails;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;


/**
 *
 *
 * @author Ryan Baxter
 * @author Gregor Zurowski
 */
public class RoutesEndpointTests {
    private RouteLocator locator;

    @Test
    public void testInvoke() {
        RoutesEndpoint endpoint = new RoutesEndpoint(locator);
        Map<String, String> result = new HashMap<String, String>();
        for (Route r : locator.getRoutes()) {
            result.put(r.getFullPath(), r.getLocation());
        }
        assertThat(endpoint.invoke()).isEqualTo(result);
    }

    @Test
    public void testInvokeRouteDetails() {
        RoutesEndpoint endpoint = new RoutesEndpoint(locator);
        Map<String, RoutesEndpoint.RouteDetails> results = new HashMap<>();
        for (Route route : locator.getRoutes()) {
            results.put(route.getFullPath(), new RoutesEndpoint.RouteDetails(route));
        }
        assertThat(endpoint.invokeRouteDetails()).isEqualTo(results);
    }
}

