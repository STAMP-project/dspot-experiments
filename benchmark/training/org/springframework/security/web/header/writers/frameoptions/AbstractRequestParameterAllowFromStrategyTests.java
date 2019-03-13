/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.security.web.header.writers.frameoptions;


import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 *
 *
 * @author Rob Winch
 */
public class AbstractRequestParameterAllowFromStrategyTests {
    private MockHttpServletRequest request;

    @Test
    public void nullAllowFromParameterValue() {
        AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub strategy = new AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub(true);
        assertThat(strategy.getAllowFromValue(request)).isEqualTo("DENY");
    }

    @Test
    public void emptyAllowFromParameterValue() {
        request.setParameter("x-frames-allow-from", "");
        AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub strategy = new AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub(true);
        assertThat(strategy.getAllowFromValue(request)).isEqualTo("DENY");
    }

    @Test
    public void emptyAllowFromCustomParameterValue() {
        String customParam = "custom";
        request.setParameter(customParam, "");
        AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub strategy = new AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub(true);
        setAllowFromParameterName(customParam);
        assertThat(strategy.getAllowFromValue(request)).isEqualTo("DENY");
    }

    @Test
    public void allowFromParameterValueAllowed() {
        String value = "https://example.com";
        request.setParameter("x-frames-allow-from", value);
        AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub strategy = new AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub(true);
        assertThat(strategy.getAllowFromValue(request)).isEqualTo(value);
    }

    @Test
    public void allowFromParameterValueDenied() {
        String value = "https://example.com";
        request.setParameter("x-frames-allow-from", value);
        AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub strategy = new AbstractRequestParameterAllowFromStrategyTests.RequestParameterAllowFromStrategyStub(false);
        assertThat(strategy.getAllowFromValue(request)).isEqualTo("DENY");
    }

    private static class RequestParameterAllowFromStrategyStub extends AbstractRequestParameterAllowFromStrategy {
        private boolean match;

        RequestParameterAllowFromStrategyStub(boolean match) {
            this.match = match;
        }

        @Override
        protected boolean allowed(String allowFromOrigin) {
            return match;
        }
    }
}

