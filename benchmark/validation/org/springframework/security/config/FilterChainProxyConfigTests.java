/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
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
package org.springframework.security.config;


import java.util.List;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;


/**
 * Tests {@link FilterChainProxy}.
 *
 * @author Carlos Sanchez
 * @author Ben Alex
 */
public class FilterChainProxyConfigTests {
    private ClassPathXmlApplicationContext appCtx;

    @Test
    public void normalOperation() throws Exception {
        FilterChainProxy filterChainProxy = appCtx.getBean("filterChain", FilterChainProxy.class);
        doNormalOperation(filterChainProxy);
    }

    @Test
    public void normalOperationWithNewConfig() throws Exception {
        FilterChainProxy filterChainProxy = appCtx.getBean("newFilterChainProxy", FilterChainProxy.class);
        filterChainProxy.setFirewall(new DefaultHttpFirewall());
        checkPathAndFilterOrder(filterChainProxy);
        doNormalOperation(filterChainProxy);
    }

    @Test
    public void normalOperationWithNewConfigRegex() throws Exception {
        FilterChainProxy filterChainProxy = appCtx.getBean("newFilterChainProxyRegex", FilterChainProxy.class);
        filterChainProxy.setFirewall(new DefaultHttpFirewall());
        checkPathAndFilterOrder(filterChainProxy);
        doNormalOperation(filterChainProxy);
    }

    @Test
    public void normalOperationWithNewConfigNonNamespace() throws Exception {
        FilterChainProxy filterChainProxy = appCtx.getBean("newFilterChainProxyNonNamespace", FilterChainProxy.class);
        filterChainProxy.setFirewall(new DefaultHttpFirewall());
        checkPathAndFilterOrder(filterChainProxy);
        doNormalOperation(filterChainProxy);
    }

    @Test
    public void pathWithNoMatchHasNoFilters() throws Exception {
        FilterChainProxy filterChainProxy = appCtx.getBean("newFilterChainProxyNoDefaultPath", FilterChainProxy.class);
        assertThat(filterChainProxy.getFilters("/nomatch")).isNull();
    }

    // SEC-1235
    @Test
    public void mixingPatternsAndPlaceholdersDoesntCauseOrderingIssues() throws Exception {
        FilterChainProxy fcp = appCtx.getBean("sec1235FilterChainProxy", FilterChainProxy.class);
        List<SecurityFilterChain> chains = fcp.getFilterChains();
        assertThat(getPattern(chains.get(0))).isEqualTo("/login*");
        assertThat(getPattern(chains.get(1))).isEqualTo("/logout");
        assertThat(((getRequestMatcher()) instanceof AnyRequestMatcher)).isTrue();
    }
}

