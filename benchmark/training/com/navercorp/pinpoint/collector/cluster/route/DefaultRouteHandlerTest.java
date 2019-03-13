/**
 * Copyright 2016 NAVER Corp.
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
package com.navercorp.pinpoint.collector.cluster.route;


import TRouteResult.NOT_FOUND;
import com.navercorp.pinpoint.collector.cluster.ClusterPointRepository;
import com.navercorp.pinpoint.collector.cluster.route.filter.RouteFilter;
import com.navercorp.pinpoint.thrift.dto.command.TCommandTransferResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class DefaultRouteHandlerTest {
    private int requestId = 0;

    @Test
    public void testName() throws Exception {
        DefaultRouteHandler routeHandler = new DefaultRouteHandler(new ClusterPointRepository(), new DefaultRouteFilterChain(), new DefaultRouteFilterChain());
        DefaultRouteHandlerTest.CountFilter requestFilter = new DefaultRouteHandlerTest.CountFilter();
        routeHandler.addRequestFilter(requestFilter);
        TCommandTransferResponse response = routeHandler.onRoute(createRequestEvent());
        Assert.assertEquals(1, requestFilter.getCallCount());
        DefaultRouteHandlerTest.CountFilter responseFilter = new DefaultRouteHandlerTest.CountFilter();
        routeHandler.addResponseFilter(responseFilter);
        response = routeHandler.onRoute(createRequestEvent());
        Assert.assertEquals(2, requestFilter.getCallCount());
        Assert.assertEquals(1, responseFilter.getCallCount());
        Assert.assertEquals(NOT_FOUND, response.getRouteResult());
    }

    static class CountFilter implements RouteFilter {
        private int callCount = 0;

        @Override
        public void doEvent(RouteEvent event) {
            (callCount)++;
        }

        public int getCallCount() {
            return callCount;
        }
    }
}

