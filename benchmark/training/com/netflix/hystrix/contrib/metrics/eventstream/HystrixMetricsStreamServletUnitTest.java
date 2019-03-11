/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.contrib.metrics.eventstream;


import HystrixDashboardStream.DashboardData;
import com.netflix.hystrix.metric.consumer.HystrixDashboardStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import rx.Observable;


public class HystrixMetricsStreamServletUnitTest {
    @Mock
    HttpServletRequest mockReq;

    @Mock
    HttpServletResponse mockResp;

    @Mock
    DashboardData mockDashboard;

    @Mock
    PrintWriter mockPrintWriter;

    HystrixMetricsStreamServlet servlet;

    private final Observable<HystrixDashboardStream.DashboardData> streamOfOnNexts = Observable.interval(100, TimeUnit.MILLISECONDS).map(new rx.functions.Func1<Long, HystrixDashboardStream.DashboardData>() {
        @Override
        public DashboardData call(Long timestamp) {
            return mockDashboard;
        }
    });

    @Test
    public void shutdownServletShouldRejectRequests() throws IOException, ServletException {
        servlet = new HystrixMetricsStreamServlet(streamOfOnNexts, 10);
        try {
            servlet.init();
        } catch (ServletException ex) {
        }
        servlet.shutdown();
        servlet.service(mockReq, mockResp);
        Mockito.verify(mockResp).sendError(503, "Service has been shut down.");
    }
}

