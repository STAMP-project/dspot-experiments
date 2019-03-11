/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.eureka;


import EurekaClientIdentity.DEFAULT_CLIENT_NAME;
import EurekaMonitors.RATE_LIMITED;
import EurekaMonitors.RATE_LIMITED_CANDIDATES;
import EurekaServerIdentity.DEFAULT_SERVER_NAME;
import HttpServletResponse.SC_SERVICE_UNAVAILABLE;
import com.netflix.config.ConfigurationManager;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @author Tomasz Bak
 */
@RunWith(MockitoJUnitRunner.class)
public class RateLimitingFilterTest {
    private static final String FULL_FETCH = "base/apps";

    private static final String DELTA_FETCH = "base/apps/delta";

    private static final String APP_FETCH = "base/apps/myAppId";

    private static final String CUSTOM_CLIENT = "CustomClient";

    private static final String PYTHON_CLIENT = "PythonClient";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RateLimitingFilter filter;

    @Test
    public void testPrivilegedClientAlwaysServed() throws Exception {
        whenRequest(RateLimitingFilterTest.FULL_FETCH, RateLimitingFilterTest.PYTHON_CLIENT);
        filter.doFilter(request, response, filterChain);
        whenRequest(RateLimitingFilterTest.DELTA_FETCH, DEFAULT_CLIENT_NAME);
        filter.doFilter(request, response, filterChain);
        whenRequest(RateLimitingFilterTest.APP_FETCH, DEFAULT_SERVER_NAME);
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain, Mockito.times(3)).doFilter(request, response);
        Mockito.verify(response, Mockito.never()).setStatus(SC_SERVICE_UNAVAILABLE);
    }

    @Test
    public void testStandardClientsThrottlingEnforceable() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("eureka.rateLimiter.throttleStandardClients", true);
        // Custom clients will go up to the window limit
        whenRequest(RateLimitingFilterTest.FULL_FETCH, DEFAULT_CLIENT_NAME);
        filter.doFilter(request, response, filterChain);
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain, Mockito.times(2)).doFilter(request, response);
        // Now we hit the limit
        long rateLimiterCounter = RATE_LIMITED.getCount();
        filter.doFilter(request, response, filterChain);
        Assert.assertEquals("Expected rate limiter counter increase", (rateLimiterCounter + 1), RATE_LIMITED.getCount());
        Mockito.verify(response, Mockito.times(1)).setStatus(SC_SERVICE_UNAVAILABLE);
    }

    @Test
    public void testCustomClientShedding() throws Exception {
        // Custom clients will go up to the window limit
        whenRequest(RateLimitingFilterTest.FULL_FETCH, RateLimitingFilterTest.CUSTOM_CLIENT);
        filter.doFilter(request, response, filterChain);
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain, Mockito.times(2)).doFilter(request, response);
        // Now we hit the limit
        long rateLimiterCounter = RATE_LIMITED.getCount();
        filter.doFilter(request, response, filterChain);
        Assert.assertEquals("Expected rate limiter counter increase", (rateLimiterCounter + 1), RATE_LIMITED.getCount());
        Mockito.verify(response, Mockito.times(1)).setStatus(SC_SERVICE_UNAVAILABLE);
    }

    @Test
    public void testCustomClientThrottlingCandidatesCounter() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty("eureka.rateLimiter.enabled", false);
        // Custom clients will go up to the window limit
        whenRequest(RateLimitingFilterTest.FULL_FETCH, RateLimitingFilterTest.CUSTOM_CLIENT);
        filter.doFilter(request, response, filterChain);
        filter.doFilter(request, response, filterChain);
        Mockito.verify(filterChain, Mockito.times(2)).doFilter(request, response);
        // Now we hit the limit
        long rateLimiterCounter = RATE_LIMITED_CANDIDATES.getCount();
        filter.doFilter(request, response, filterChain);
        Assert.assertEquals("Expected rate limiter counter increase", (rateLimiterCounter + 1), RATE_LIMITED_CANDIDATES.getCount());
        // We just test the counter
        Mockito.verify(response, Mockito.times(0)).setStatus(SC_SERVICE_UNAVAILABLE);
    }
}

