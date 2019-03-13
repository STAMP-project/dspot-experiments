/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.internal;


import CallOptions.DEFAULT;
import MethodType.UNARY;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.Deadline;
import io.grpc.MethodDescriptor;
import io.grpc.internal.ManagedChannelServiceConfig.MethodInfo;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ServiceConfigInterceptor}.
 */
@RunWith(JUnit4.class)
public class ServiceConfigInterceptorTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Mock
    private Channel channel;

    @Captor
    private ArgumentCaptor<CallOptions> callOptionsCap;

    private final ServiceConfigInterceptor interceptor = /* retryEnabled = */
    /* maxRetryAttemptsLimit = */
    /* maxHedgedAttemptsLimit = */
    new ServiceConfigInterceptor(true, 5, 6);

    private final String fullMethodName = MethodDescriptor.generateFullMethodName("service", "method");

    private final MethodDescriptor<Void, Void> methodDescriptor = MethodDescriptor.newBuilder(new ServiceConfigInterceptorTest.NoopMarshaller(), new ServiceConfigInterceptorTest.NoopMarshaller()).setType(UNARY).setFullMethodName(fullMethodName).build();

    private static final class JsonObj extends HashMap<String, Object> {
        private JsonObj(Object... kv) {
            for (int i = 0; i < (kv.length); i += 2) {
                put(((String) (kv[i])), kv[(i + 1)]);
            }
        }
    }

    private static final class JsonList extends ArrayList<Object> {
        private JsonList(Object... values) {
            addAll(Arrays.asList(values));
        }
    }

    @Test
    public void withWaitForReady() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "waitForReady", true);
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        interceptor.interceptCall(methodDescriptor, DEFAULT.withoutWaitForReady(), channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().isWaitForReady()).isTrue();
    }

    @Test
    public void handleUpdateNotCalledBeforeInterceptCall() {
        interceptor.interceptCall(methodDescriptor, DEFAULT.withoutWaitForReady(), channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().isWaitForReady()).isFalse();
        assertThat(callOptionsCap.getValue().getOption(ServiceConfigInterceptor.RETRY_POLICY_KEY).get()).isEqualTo(RetryPolicy.DEFAULT);
        assertThat(callOptionsCap.getValue().getOption(ServiceConfigInterceptor.HEDGING_POLICY_KEY).get()).isEqualTo(HedgingPolicy.DEFAULT);
    }

    @Test
    public void withMaxRequestSize() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "maxRequestMessageBytes", 1.0);
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        interceptor.interceptCall(methodDescriptor, DEFAULT, channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().getMaxOutboundMessageSize()).isEqualTo(1);
    }

    @Test
    public void withMaxRequestSize_pickSmallerExisting() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "maxRequestMessageBytes", 10.0);
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        interceptor.interceptCall(methodDescriptor, DEFAULT.withMaxOutboundMessageSize(5), channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().getMaxOutboundMessageSize()).isEqualTo(5);
    }

    @Test
    public void withMaxRequestSize_pickSmallerNew() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "maxRequestMessageBytes", 5.0);
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        interceptor.interceptCall(methodDescriptor, DEFAULT.withMaxOutboundMessageSize(10), channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().getMaxOutboundMessageSize()).isEqualTo(5);
    }

    @Test
    public void withMaxResponseSize() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "maxResponseMessageBytes", 1.0);
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        interceptor.interceptCall(methodDescriptor, DEFAULT, channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().getMaxInboundMessageSize()).isEqualTo(1);
    }

    @Test
    public void withMaxResponseSize_pickSmallerExisting() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "maxResponseMessageBytes", 5.0);
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        interceptor.interceptCall(methodDescriptor, DEFAULT.withMaxInboundMessageSize(10), channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().getMaxInboundMessageSize()).isEqualTo(5);
    }

    @Test
    public void withMaxResponseSize_pickSmallerNew() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "maxResponseMessageBytes", 10.0);
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        interceptor.interceptCall(methodDescriptor, DEFAULT.withMaxInboundMessageSize(5), channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().getMaxInboundMessageSize()).isEqualTo(5);
    }

    @Test
    public void withoutWaitForReady() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "waitForReady", false);
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        interceptor.interceptCall(methodDescriptor, DEFAULT.withWaitForReady(), channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().isWaitForReady()).isFalse();
    }

    @Test
    public void fullMethodMatched() {
        // Put in service that matches, but has no deadline.  It should be lower priority
        ServiceConfigInterceptorTest.JsonObj name1 = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig1 = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name1));
        ServiceConfigInterceptorTest.JsonObj name2 = new ServiceConfigInterceptorTest.JsonObj("service", "service", "method", "method");
        ServiceConfigInterceptorTest.JsonObj methodConfig2 = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name2), "timeout", "1s");
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig1, methodConfig2));
        interceptor.handleUpdate(serviceConfig);
        interceptor.interceptCall(methodDescriptor, DEFAULT, channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().getDeadline()).isNotNull();
    }

    @Test
    public void nearerDeadlineKept_existing() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "timeout", "100000s");
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        Deadline existingDeadline = Deadline.after(1000, TimeUnit.NANOSECONDS);
        interceptor.interceptCall(methodDescriptor, DEFAULT.withDeadline(existingDeadline), channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().getDeadline()).isEqualTo(existingDeadline);
    }

    @Test
    public void nearerDeadlineKept_new() {
        // TODO(carl-mastrangelo): the deadlines are very large because they change over time.
        // This should be fixed, and is tracked in https://github.com/grpc/grpc-java/issues/2531
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "timeout", "1s");
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        Deadline existingDeadline = Deadline.after(1234567890, TimeUnit.NANOSECONDS);
        interceptor.interceptCall(methodDescriptor, DEFAULT.withDeadline(existingDeadline), channel);
        Mockito.verify(channel).newCall(ArgumentMatchers.eq(methodDescriptor), callOptionsCap.capture());
        assertThat(callOptionsCap.getValue().getDeadline()).isNotEqualTo(existingDeadline);
    }

    @Test
    public void handleUpdate_failsOnMissingServiceName() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("method", "method");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name));
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("missing service");
        interceptor.handleUpdate(serviceConfig);
    }

    @Test
    public void handleUpdate_failsOnDuplicateMethod() {
        ServiceConfigInterceptorTest.JsonObj name1 = new ServiceConfigInterceptorTest.JsonObj("service", "service", "method", "method");
        ServiceConfigInterceptorTest.JsonObj name2 = new ServiceConfigInterceptorTest.JsonObj("service", "service", "method", "method");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name1, name2));
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Duplicate method");
        interceptor.handleUpdate(serviceConfig);
    }

    @Test
    public void handleUpdate_failsOnEmptyName() {
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj();
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("no names in method config");
        interceptor.handleUpdate(serviceConfig);
    }

    @Test
    public void handleUpdate_failsOnDuplicateService() {
        ServiceConfigInterceptorTest.JsonObj name1 = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj name2 = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name1, name2));
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Duplicate service");
        interceptor.handleUpdate(serviceConfig);
    }

    @Test
    public void handleUpdate_failsOnDuplicateServiceMultipleConfig() {
        ServiceConfigInterceptorTest.JsonObj name1 = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj name2 = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig1 = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name1));
        ServiceConfigInterceptorTest.JsonObj methodConfig2 = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name2));
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig1, methodConfig2));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Duplicate service");
        interceptor.handleUpdate(serviceConfig);
    }

    @Test
    public void handleUpdate_replaceExistingConfig() {
        ServiceConfigInterceptorTest.JsonObj name1 = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig1 = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name1));
        ServiceConfigInterceptorTest.JsonObj serviceConfig1 = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig1));
        ServiceConfigInterceptorTest.JsonObj name2 = new ServiceConfigInterceptorTest.JsonObj("service", "service", "method", "method");
        ServiceConfigInterceptorTest.JsonObj methodConfig2 = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name2));
        ServiceConfigInterceptorTest.JsonObj serviceConfig2 = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig2));
        interceptor.handleUpdate(serviceConfig1);
        assertThat(interceptor.managedChannelServiceConfig.get().getServiceMap()).isNotEmpty();
        assertThat(interceptor.managedChannelServiceConfig.get().getServiceMethodMap()).isEmpty();
        interceptor.handleUpdate(serviceConfig2);
        assertThat(interceptor.managedChannelServiceConfig.get().getServiceMap()).isEmpty();
        assertThat(interceptor.managedChannelServiceConfig.get().getServiceMethodMap()).isNotEmpty();
    }

    @Test
    public void handleUpdate_matchNames() {
        ServiceConfigInterceptorTest.JsonObj name1 = new ServiceConfigInterceptorTest.JsonObj("service", "service2");
        ServiceConfigInterceptorTest.JsonObj name2 = new ServiceConfigInterceptorTest.JsonObj("service", "service", "method", "method");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name1, name2));
        ServiceConfigInterceptorTest.JsonObj serviceConfig = new ServiceConfigInterceptorTest.JsonObj("methodConfig", new ServiceConfigInterceptorTest.JsonList(methodConfig));
        interceptor.handleUpdate(serviceConfig);
        assertThat(interceptor.managedChannelServiceConfig.get().getServiceMethodMap()).containsExactly(methodDescriptor.getFullMethodName(), new MethodInfo(methodConfig, false, 1, 1));
        assertThat(interceptor.managedChannelServiceConfig.get().getServiceMap()).containsExactly("service2", new MethodInfo(methodConfig, false, 1, 1));
    }

    @Test
    public void methodInfo_validateDeadline() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "timeout", "10000000000000000s");
        thrown.expectMessage("Duration value is out of range");
        new MethodInfo(methodConfig, false, 1, 1);
    }

    @Test
    public void methodInfo_saturateDeadline() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "timeout", "315576000000s");
        MethodInfo info = new MethodInfo(methodConfig, false, 1, 1);
        assertThat(info.timeoutNanos).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void methodInfo_badMaxRequestSize() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "maxRequestMessageBytes", (-1.0));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("exceeds bounds");
        new MethodInfo(methodConfig, false, 1, 1);
    }

    @Test
    public void methodInfo_badMaxResponseSize() {
        ServiceConfigInterceptorTest.JsonObj name = new ServiceConfigInterceptorTest.JsonObj("service", "service");
        ServiceConfigInterceptorTest.JsonObj methodConfig = new ServiceConfigInterceptorTest.JsonObj("name", new ServiceConfigInterceptorTest.JsonList(name), "maxResponseMessageBytes", (-1.0));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("exceeds bounds");
        new MethodInfo(methodConfig, false, 1, 1);
    }

    private static final class NoopMarshaller implements MethodDescriptor.Marshaller<Void> {
        @Override
        public InputStream stream(Void value) {
            return null;
        }

        @Override
        public Void parse(InputStream stream) {
            return null;
        }
    }
}

