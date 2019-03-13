/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.filter;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


public class GeoServerBasicAuthenticationFilterTest {
    public static final String USERNAME = "admin:";

    public static final String PASSWORD = "geoserver";

    private static final int NTHREADS = 8;

    private String expected;

    private GeoServerBasicAuthenticationFilter authenticationFilter;

    @Test
    public void testMultiThreadGetCacheKey() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(GeoServerBasicAuthenticationFilterTest.NTHREADS);
        List<Future<Boolean>> list = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < 600; i++) {
            Callable<Boolean> worker = new GeoServerBasicAuthenticationFilterTest.AuthenticationCallable(authenticationFilter);
            Future<Boolean> submit = executor.submit(worker);
            list.add(submit);
        }
        for (Future<Boolean> future : list) {
            future.get();
        }
        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();
    }

    private class AuthenticationCallable implements Callable<Boolean> {
        private GeoServerBasicAuthenticationFilter authenticationFilter;

        private AuthenticationCallable(GeoServerBasicAuthenticationFilter authenticationFilter) {
            this.authenticationFilter = authenticationFilter;
        }

        @Override
        public Boolean call() throws Exception {
            MockHttpServletRequest request = createRequest();
            String result = authenticationFilter.getCacheKey(request);
            Assert.assertEquals(expected, result);
            return true;
        }
    }
}

