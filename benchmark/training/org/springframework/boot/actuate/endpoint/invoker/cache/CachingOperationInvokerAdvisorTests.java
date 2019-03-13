/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.endpoint.invoker.cache;


import OperationType.READ;
import OperationType.WRITE;
import java.util.function.Function;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvoker;
import org.springframework.boot.actuate.endpoint.invoke.OperationParameters;
import org.springframework.lang.Nullable;


/**
 * Tests for {@link CachingOperationInvokerAdvisor}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class CachingOperationInvokerAdvisorTests {
    @Mock
    private OperationInvoker invoker;

    @Mock
    private Function<EndpointId, Long> timeToLive;

    private CachingOperationInvokerAdvisor advisor;

    @Test
    public void applyWhenOperationIsNotReadShouldNotAddAdvise() {
        OperationParameters parameters = getParameters("get");
        OperationInvoker advised = this.advisor.apply(EndpointId.of("foo"), WRITE, parameters, this.invoker);
        assertThat(advised).isSameAs(this.invoker);
    }

    @Test
    public void applyWhenHasAtLeaseOneMandatoryParameterShouldNotAddAdvise() {
        OperationParameters parameters = getParameters("getWithParameters", String.class, String.class);
        OperationInvoker advised = this.advisor.apply(EndpointId.of("foo"), READ, parameters, this.invoker);
        assertThat(advised).isSameAs(this.invoker);
    }

    @Test
    public void applyWhenTimeToLiveReturnsNullShouldNotAddAdvise() {
        OperationParameters parameters = getParameters("get");
        BDDMockito.given(this.timeToLive.apply(ArgumentMatchers.any())).willReturn(null);
        OperationInvoker advised = this.advisor.apply(EndpointId.of("foo"), READ, parameters, this.invoker);
        assertThat(advised).isSameAs(this.invoker);
        Mockito.verify(this.timeToLive).apply(EndpointId.of("foo"));
    }

    @Test
    public void applyWhenTimeToLiveIsZeroShouldNotAddAdvise() {
        OperationParameters parameters = getParameters("get");
        BDDMockito.given(this.timeToLive.apply(ArgumentMatchers.any())).willReturn(0L);
        OperationInvoker advised = this.advisor.apply(EndpointId.of("foo"), READ, parameters, this.invoker);
        assertThat(advised).isSameAs(this.invoker);
        Mockito.verify(this.timeToLive).apply(EndpointId.of("foo"));
    }

    @Test
    public void applyShouldAddCacheAdvise() {
        OperationParameters parameters = getParameters("get");
        BDDMockito.given(this.timeToLive.apply(ArgumentMatchers.any())).willReturn(100L);
        assertAdviseIsApplied(parameters);
    }

    @Test
    public void applyWithAllOptionalParametersShouldAddAdvise() {
        OperationParameters parameters = getParameters("getWithAllOptionalParameters", String.class, String.class);
        BDDMockito.given(this.timeToLive.apply(ArgumentMatchers.any())).willReturn(100L);
        assertAdviseIsApplied(parameters);
    }

    @Test
    public void applyWithSecurityContextShouldAddAdvise() {
        OperationParameters parameters = getParameters("getWithSecurityContext", SecurityContext.class, String.class);
        BDDMockito.given(this.timeToLive.apply(ArgumentMatchers.any())).willReturn(100L);
        assertAdviseIsApplied(parameters);
    }

    public static class TestOperations {
        public String get() {
            return "";
        }

        public String getWithParameters(@Nullable
        String foo, String bar) {
            return "";
        }

        public String getWithAllOptionalParameters(@Nullable
        String foo, @Nullable
        String bar) {
            return "";
        }

        public String getWithSecurityContext(SecurityContext securityContext, @Nullable
        String bar) {
            return "";
        }
    }
}

