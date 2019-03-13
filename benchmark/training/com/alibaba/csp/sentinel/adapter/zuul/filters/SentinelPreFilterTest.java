/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.csp.sentinel.adapter.zuul.filters;


import com.alibaba.csp.sentinel.adapter.zuul.fallback.DefaultRequestOriginParser;
import com.alibaba.csp.sentinel.adapter.zuul.fallback.RequestOriginParser;
import com.alibaba.csp.sentinel.adapter.zuul.fallback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.zuul.properties.SentinelZuulProperties;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.netflix.zuul.context.RequestContext;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author tiger
 */
public class SentinelPreFilterTest {
    private String SERVICE_ID = "servicea";

    private String URI = "/servicea/test";

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private UrlCleaner urlCleaner;

    private final RequestOriginParser requestOriginParser = new DefaultRequestOriginParser();

    @Test
    public void testFilterType() throws Exception {
        SentinelZuulProperties properties = new SentinelZuulProperties();
        SentinelPreFilter sentinelPreFilter = new SentinelPreFilter(properties, urlCleaner, requestOriginParser);
        Assert.assertEquals(sentinelPreFilter.filterType(), PRE_TYPE);
    }

    @Test
    public void testRun() throws Exception {
        RequestContext ctx = RequestContext.getCurrentContext();
        SentinelZuulProperties properties = new SentinelZuulProperties();
        SentinelPreFilter sentinelPreFilter = new SentinelPreFilter(properties, urlCleaner, requestOriginParser);
        BDDMockito.given(urlCleaner.clean(URI)).willReturn(URI);
        sentinelPreFilter.run();
        Assert.assertNull(ctx.getRouteHost());
        Assert.assertEquals(ctx.get(SERVICE_ID_KEY), SERVICE_ID);
    }

    @Test
    public void testServiceFallBackRun() throws Exception {
        RequestContext ctx = RequestContext.getCurrentContext();
        SentinelZuulProperties properties = new SentinelZuulProperties();
        properties.setEnabled(true);
        SentinelPreFilter sentinelPreFilter = new SentinelPreFilter(properties, urlCleaner, requestOriginParser);
        BDDMockito.given(urlCleaner.clean(URI)).willAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new FlowException("flow ex");
            }
        });
        sentinelPreFilter.run();
        Assert.assertNull(ctx.getRouteHost());
        Assert.assertNull(ctx.get(SERVICE_ID_KEY));
    }
}

