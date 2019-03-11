/**
 * Copyright 2017-2019 the original author or authors.
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
package org.springframework.cloud.netflix.zuul.filters.route.support;


import HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE;
import HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;
import com.netflix.client.AbstractLoadBalancerAwareClient;
import com.netflix.client.ClientRequest;
import com.netflix.client.http.HttpResponse;
import org.junit.Test;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;


/**
 *
 *
 * @author Yongsung Yoon
 */
public class RibbonCommandHystrixThreadPoolKeyTests {
    private ZuulProperties zuulProperties;

    @Test
    public void testDefaultHystrixThreadPoolKey() throws Exception {
        zuulProperties.setRibbonIsolationStrategy(THREAD);
        RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand ribbonCommand1 = new RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand("testCommand1", zuulProperties);
        RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand ribbonCommand2 = new RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand("testCommand2", zuulProperties);
        // CommandGroupKey should be used as ThreadPoolKey as default.
        assertThat(getThreadPoolKey().name()).isEqualTo(getCommandGroup().name());
        assertThat(getThreadPoolKey().name()).isEqualTo(getCommandGroup().name());
    }

    @Test
    public void testUseSeparateThreadPools() throws Exception {
        zuulProperties.setRibbonIsolationStrategy(THREAD);
        zuulProperties.getThreadPool().setUseSeparateThreadPools(true);
        RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand ribbonCommand1 = new RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand("testCommand1", zuulProperties);
        RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand ribbonCommand2 = new RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand("testCommand2", zuulProperties);
        assertThat(getThreadPoolKey().name()).isEqualTo("testCommand1");
        assertThat(getThreadPoolKey().name()).isEqualTo("testCommand2");
    }

    @Test
    public void testThreadPoolKeyPrefix() throws Exception {
        final String prefix = "zuulgw-";
        zuulProperties.setRibbonIsolationStrategy(THREAD);
        zuulProperties.getThreadPool().setUseSeparateThreadPools(true);
        zuulProperties.getThreadPool().setThreadPoolKeyPrefix(prefix);
        RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand ribbonCommand1 = new RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand("testCommand1", zuulProperties);
        RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand ribbonCommand2 = new RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand("testCommand2", zuulProperties);
        assertThat(getThreadPoolKey().name()).isEqualTo((prefix + "testCommand1"));
        assertThat(getThreadPoolKey().name()).isEqualTo((prefix + "testCommand2"));
    }

    @Test
    public void testNoSideEffectOnSemaphoreIsolation() throws Exception {
        final String prefix = "zuulgw-";
        zuulProperties.setRibbonIsolationStrategy(SEMAPHORE);
        zuulProperties.getThreadPool().setUseSeparateThreadPools(true);
        zuulProperties.getThreadPool().setThreadPoolKeyPrefix(prefix);
        RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand ribbonCommand1 = new RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand("testCommand1", zuulProperties);
        RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand ribbonCommand2 = new RibbonCommandHystrixThreadPoolKeyTests.TestRibbonCommand("testCommand2", zuulProperties);
        // There should be no side effect on semaphore isolation
        assertThat(getThreadPoolKey().name()).isEqualTo(getCommandGroup().name());
        assertThat(getThreadPoolKey().name()).isEqualTo(getCommandGroup().name());
    }

    public static class TestRibbonCommand extends AbstractRibbonCommand<AbstractLoadBalancerAwareClient<ClientRequest, HttpResponse>, ClientRequest, HttpResponse> {
        public TestRibbonCommand(String commandKey, ZuulProperties zuulProperties) {
            super(commandKey, null, null, zuulProperties);
        }

        @Override
        protected ClientRequest createRequest() throws Exception {
            return null;
        }
    }
}

