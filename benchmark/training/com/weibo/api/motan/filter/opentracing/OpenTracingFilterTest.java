/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * import io.opentracing.impl.BraveTracer;
 */
package com.weibo.api.motan.filter.opentracing;


import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.DefaultRequest;
import com.weibo.api.motan.rpc.DefaultResponse;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Referer;
import com.weibo.api.motan.rpc.Response;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @unknown UT
 * @author zhanglei
 * @unknown Dec 9, 2016
 */
// private void checkBraveTrace() {
// if (tracer instanceof BraveTracer) {
// assertTrue(request.getAttachments().containsKey("X-B3-TraceId"));
// assertTrue(request.getAttachments().containsKey("X-B3-SpanId"));
// assertTrue(request.getAttachments().containsKey("X-B3-Sampled"));
// }
// }
public class OpenTracingFilterTest {
    Filter OTFilter;

    Tracer tracer;

    Referer<HelloService> refer;

    Provider<HelloService> provider;

    DefaultRequest request;

    DefaultResponse response;

    @Test
    public void testRefererFilter() {
        Response res = OTFilter.filter(refer, request);
        Assert.assertEquals(response, res);
        checkMockTracer();
        // brave test must run with jdk1.8
        // tracer = new BraveTracer();// use bravetracer
        // res = OTFilter.filter(refer, request);
        // assertEquals(response, res);
        // checkBraveTrace();
    }

    @Test
    public void testProviderFilter() {
        Response res = OTFilter.filter(provider, request);
        Assert.assertEquals(response, res);
        checkMockTracer();
    }

    @Test
    public void testException() {
        response.setException(new RuntimeException("in test"));
        Response res = OTFilter.filter(refer, request);
        Assert.assertEquals(response, res);
        if ((tracer) instanceof MockTracer) {
            MockSpan span = finishedSpans().get(0);
            Assert.assertEquals(span.logEntries().size(), 1);
            Assert.assertTrue("request fail.in test".equals(span.logEntries().get(0).fields().get("event")));
        }
    }
}

