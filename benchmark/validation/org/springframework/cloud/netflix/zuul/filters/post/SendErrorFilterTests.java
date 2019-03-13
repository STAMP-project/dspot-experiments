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
package org.springframework.cloud.netflix.zuul.filters.post;


import HttpStatus.NOT_FOUND;
import com.netflix.zuul.context.RequestContext;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Spencer Gibb
 */
public class SendErrorFilterTests {
    @Test
    public void runsNormally() {
        SendErrorFilter filter = createSendErrorFilter(new MockHttpServletRequest());
        assertThat(filter.shouldFilter()).as("shouldFilter returned false").isTrue();
        filter.run();
    }

    @Test
    public void noRequestDispatcher() {
        SendErrorFilter filter = createSendErrorFilter(Mockito.mock(HttpServletRequest.class));
        assertThat(filter.shouldFilter()).as("shouldFilter returned false").isTrue();
        filter.run();
    }

    @Test
    public void doesNotRunTwice() {
        SendErrorFilter filter = createSendErrorFilter(new MockHttpServletRequest());
        assertThat(filter.shouldFilter()).as("shouldFilter returned false").isTrue();
        filter.run();
        assertThat(filter.shouldFilter()).as("shouldFilter returned true").isFalse();
    }

    @Test
    public void setResponseCode() {
        SendErrorFilter filter = createSendErrorFilter(new MockHttpServletRequest());
        filter.run();
        RequestContext ctx = RequestContext.getCurrentContext();
        int resCode = ctx.getResponse().getStatus();
        int ctxCode = ctx.getResponseStatusCode();
        assertThat(resCode).as(("invalid response code: " + resCode)).isEqualTo(NOT_FOUND.value());
        assertThat(ctxCode).as(("invalid response code in RequestContext: " + ctxCode)).isEqualTo(resCode);
    }
}

