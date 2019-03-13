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
package org.springframework.cloud.netflix.zuul.filters.route;


import com.netflix.zuul.context.RequestContext;
import org.junit.Test;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.http.client.ClientHttpResponse;


/**
 *
 *
 * @author Spencer Gibb
 * @author Yongsung Yoon
 * @author Gang Li
 */
public class RibbonRoutingFilterTests {
    private RequestContext requestContext;

    private RibbonRoutingFilter filter;

    @Test
    public void useServlet31Works() {
        assertThat(filter.isUseServlet31()).isTrue();
    }

    @Test
    public void testLoadBalancerKeyToRibbonCommandContext() throws Exception {
        final String testKey = "testLoadBalancerKey";
        requestContext.set(LOAD_BALANCER_KEY, testKey);
        RibbonCommandContext commandContext = filter.buildCommandContext(requestContext);
        assertThat(commandContext.getLoadBalancerKey()).isEqualTo(testKey);
    }

    @Test
    public void testNullLoadBalancerKeyToRibbonCommandContext() throws Exception {
        requestContext.set(LOAD_BALANCER_KEY, null);
        RibbonCommandContext commandContext = filter.buildCommandContext(requestContext);
        assertThat(commandContext.getLoadBalancerKey()).isNull();
    }

    @Test
    public void testSetResponseWithNonHttpStatusCode() throws Exception {
        ClientHttpResponse response = this.createClientHttpResponseWithNonStatus();
        this.filter.setResponse(response);
        assertThat(517).isEqualTo(this.requestContext.get("responseStatusCode"));
    }

    @Test
    public void testSetResponseWithHttpStatusCode() throws Exception {
        ClientHttpResponse response = this.createClientHttpResponse();
        this.filter.setResponse(response);
        assertThat(200).isEqualTo(this.requestContext.get("responseStatusCode"));
    }
}

