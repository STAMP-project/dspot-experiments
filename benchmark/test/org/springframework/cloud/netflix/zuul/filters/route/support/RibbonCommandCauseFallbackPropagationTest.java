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


import HttpStatus.BAD_GATEWAY;
import HttpStatus.CONFLICT;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;
import HystrixCommandProperties.Setter;
import com.netflix.client.AbstractLoadBalancerAwareClient;
import com.netflix.client.ClientException;
import com.netflix.client.ClientRequest;
import com.netflix.client.IResponse;
import com.netflix.client.RequestSpecificRetryHandler;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.http.HttpResponse;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import java.util.UUID;
import org.junit.Test;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;


/**
 *
 *
 * @author Dominik Mostek
 */
public class RibbonCommandCauseFallbackPropagationTest {
    private RibbonCommandContext context;

    @Test
    public void providerIsCalledInCaseOfException() throws Exception {
        FallbackProvider provider = new RibbonCommandCauseFallbackPropagationTest.TestFallbackProvider(RibbonCommandCauseFallbackPropagationTest.getClientHttpResponse(INTERNAL_SERVER_ERROR));
        RuntimeException exception = new RuntimeException("Failed!");
        RibbonCommandCauseFallbackPropagationTest.TestRibbonCommand testCommand = new RibbonCommandCauseFallbackPropagationTest.TestRibbonCommand(new RibbonCommandCauseFallbackPropagationTest.TestClient(exception), provider, context);
        ClientHttpResponse response = execute();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void causeIsProvidedForNewInterface() throws Exception {
        RibbonCommandCauseFallbackPropagationTest.TestFallbackProvider provider = RibbonCommandCauseFallbackPropagationTest.TestFallbackProvider.withResponse(NOT_FOUND);
        RuntimeException exception = new RuntimeException("Failed!");
        RibbonCommandCauseFallbackPropagationTest.TestRibbonCommand testCommand = new RibbonCommandCauseFallbackPropagationTest.TestRibbonCommand(new RibbonCommandCauseFallbackPropagationTest.TestClient(exception), provider, context);
        ClientHttpResponse response = execute();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        Throwable cause = provider.getCause();
        assertThat(cause.getClass()).isEqualTo(exception.getClass());
        assertThat(cause.getMessage()).isEqualTo(exception.getMessage());
    }

    @Test
    public void executionExceptionIsUsedInsteadWhenFailedExceptionIsNull() throws Exception {
        RibbonCommandCauseFallbackPropagationTest.TestFallbackProvider provider = RibbonCommandCauseFallbackPropagationTest.TestFallbackProvider.withResponse(BAD_GATEWAY);
        final RuntimeException exception = new RuntimeException("Failed!");
        RibbonCommandCauseFallbackPropagationTest.TestRibbonCommand testCommand = new RibbonCommandCauseFallbackPropagationTest.TestRibbonCommand(new RibbonCommandCauseFallbackPropagationTest.TestClient(exception), provider, context) {
            @Override
            public Throwable getFailedExecutionException() {
                return null;
            }

            @Override
            public Throwable getExecutionException() {
                return exception;
            }
        };
        ClientHttpResponse response = execute();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(BAD_GATEWAY);
    }

    @Test
    public void timeoutExceptionIsPropagated() throws Exception {
        RibbonCommandCauseFallbackPropagationTest.TestFallbackProvider provider = RibbonCommandCauseFallbackPropagationTest.TestFallbackProvider.withResponse(CONFLICT);
        RuntimeException exception = new RuntimeException("Failed!");
        RibbonCommandCauseFallbackPropagationTest.TestRibbonCommand testCommand = new RibbonCommandCauseFallbackPropagationTest.TestRibbonCommand(new RibbonCommandCauseFallbackPropagationTest.TestClient(exception), provider, 1, context) {
            @Override
            protected ClientRequest createRequest() throws Exception {
                Thread.sleep(5);
                return super.createRequest();
            }
        };
        ClientHttpResponse response = execute();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
        assertThat(provider.getCause()).isNotNull();
        assertThat(provider.getCause().getClass()).isEqualTo(HystrixTimeoutException.class);
    }

    public static class TestFallbackProvider implements FallbackProvider {
        private final ClientHttpResponse response;

        private Throwable cause;

        public TestFallbackProvider(final ClientHttpResponse response) {
            this.response = response;
        }

        @Override
        public ClientHttpResponse fallbackResponse(String route, final Throwable cause) {
            this.cause = cause;
            return response;
        }

        @Override
        public String getRoute() {
            return "test-route";
        }

        public Throwable getCause() {
            return cause;
        }

        public static RibbonCommandCauseFallbackPropagationTest.TestFallbackProvider withResponse(final HttpStatus status) {
            return new RibbonCommandCauseFallbackPropagationTest.TestFallbackProvider(RibbonCommandCauseFallbackPropagationTest.getClientHttpResponse(status));
        }
    }

    @SuppressWarnings("rawtypes")
    public static class TestClient extends AbstractLoadBalancerAwareClient {
        private final RuntimeException exception;

        public TestClient(RuntimeException exception) {
            super(null);
            this.exception = exception;
        }

        @Override
        public IResponse executeWithLoadBalancer(final ClientRequest request, final IClientConfig requestConfig) throws ClientException {
            throw exception;
        }

        @Override
        public RequestSpecificRetryHandler getRequestSpecificRetryHandler(final ClientRequest clientRequest, final IClientConfig iClientConfig) {
            return null;
        }

        @Override
        public IResponse execute(final ClientRequest clientRequest, final IClientConfig iClientConfig) throws Exception {
            return null;
        }
    }

    public static class TestRibbonCommand extends AbstractRibbonCommand<AbstractLoadBalancerAwareClient<ClientRequest, HttpResponse>, ClientRequest, HttpResponse> {
        public TestRibbonCommand(AbstractLoadBalancerAwareClient<ClientRequest, HttpResponse> client, FallbackProvider fallbackProvider, RibbonCommandContext context) {
            this(client, new ZuulProperties(), fallbackProvider, context);
        }

        public TestRibbonCommand(AbstractLoadBalancerAwareClient<ClientRequest, HttpResponse> client, ZuulProperties zuulProperties, FallbackProvider fallbackProvider, RibbonCommandContext context) {
            super(("testCommand" + (UUID.randomUUID())), client, context, zuulProperties, fallbackProvider);
        }

        public TestRibbonCommand(AbstractLoadBalancerAwareClient<ClientRequest, HttpResponse> client, FallbackProvider fallbackProvider, int timeout, RibbonCommandContext context) {
            // different name is used because of properties caching
            super(getSetter(("testCommand" + (UUID.randomUUID())), new ZuulProperties(), new DefaultClientConfigImpl()).andCommandPropertiesDefaults(RibbonCommandCauseFallbackPropagationTest.TestRibbonCommand.defauts(timeout)), client, context, fallbackProvider, null);
        }

        private static Setter defauts(final int timeout) {
            return HystrixCommandProperties.Setter().withExecutionTimeoutEnabled(true).withExecutionIsolationStrategy(THREAD).withExecutionTimeoutInMilliseconds(timeout);
        }

        @Override
        protected ClientRequest createRequest() throws Exception {
            return null;
        }
    }
}

