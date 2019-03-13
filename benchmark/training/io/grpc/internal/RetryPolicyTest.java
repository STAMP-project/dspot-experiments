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


import Code.RESOURCE_EXHAUSTED;
import Code.UNAVAILABLE;
import MethodDescriptor.Builder;
import RetryPolicy.DEFAULT;
import com.google.common.collect.ImmutableSet;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.internal.RetriableStream.Throttle;
import io.grpc.testing.TestMethodDescriptors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for RetryPolicy.
 */
@RunWith(JUnit4.class)
public class RetryPolicyTest {
    @Test
    public void getRetryPolicies() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(RetryPolicyTest.class.getResourceAsStream("/io/grpc/internal/test_retry_service_config.json"), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            } 
            Object serviceConfigObj = JsonParser.parse(sb.toString());
            Assert.assertTrue((serviceConfigObj instanceof Map));
            @SuppressWarnings("unchecked")
            Map<String, ?> serviceConfig = ((Map<String, ?>) (serviceConfigObj));
            ServiceConfigInterceptor serviceConfigInterceptor = /* retryEnabled = */
            /* maxRetryAttemptsLimit = */
            /* maxHedgedAttemptsLimit = */
            new ServiceConfigInterceptor(true, 4, 3);
            serviceConfigInterceptor.handleUpdate(serviceConfig);
            Builder<Void, Void> builder = TestMethodDescriptors.voidMethod().toBuilder();
            MethodDescriptor<Void, Void> method = builder.setFullMethodName("not/exist").build();
            Assert.assertEquals(DEFAULT, serviceConfigInterceptor.getRetryPolicyFromConfig(method));
            method = builder.setFullMethodName("not_exist/Foo1").build();
            Assert.assertEquals(DEFAULT, serviceConfigInterceptor.getRetryPolicyFromConfig(method));
            method = builder.setFullMethodName("SimpleService1/not_exist").build();
            Assert.assertEquals(new RetryPolicy(3, TimeUnit.MILLISECONDS.toNanos(2100), TimeUnit.MILLISECONDS.toNanos(2200), Double.parseDouble("3"), ImmutableSet.of(UNAVAILABLE, RESOURCE_EXHAUSTED)), serviceConfigInterceptor.getRetryPolicyFromConfig(method));
            method = builder.setFullMethodName("SimpleService1/Foo1").build();
            Assert.assertEquals(new RetryPolicy(4, TimeUnit.MILLISECONDS.toNanos(100), TimeUnit.MILLISECONDS.toNanos(1000), Double.parseDouble("2"), ImmutableSet.of(UNAVAILABLE)), serviceConfigInterceptor.getRetryPolicyFromConfig(method));
            method = builder.setFullMethodName("SimpleService2/not_exist").build();
            Assert.assertEquals(DEFAULT, serviceConfigInterceptor.getRetryPolicyFromConfig(method));
            method = builder.setFullMethodName("SimpleService2/Foo2").build();
            Assert.assertEquals(new RetryPolicy(4, TimeUnit.MILLISECONDS.toNanos(100), TimeUnit.MILLISECONDS.toNanos(1000), Double.parseDouble("2"), ImmutableSet.of(UNAVAILABLE)), serviceConfigInterceptor.getRetryPolicyFromConfig(method));
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void getRetryPolicies_retryDisabled() throws Exception {
        Channel channel = Mockito.mock(Channel.class);
        ArgumentCaptor<CallOptions> callOptionsCap = ArgumentCaptor.forClass(CallOptions.class);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(RetryPolicyTest.class.getResourceAsStream("/io/grpc/internal/test_retry_service_config.json"), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            } 
            Object serviceConfigObj = JsonParser.parse(sb.toString());
            Assert.assertTrue((serviceConfigObj instanceof Map));
            @SuppressWarnings("unchecked")
            Map<String, ?> serviceConfig = ((Map<String, ?>) (serviceConfigObj));
            ServiceConfigInterceptor serviceConfigInterceptor = /* retryEnabled = */
            /* maxRetryAttemptsLimit = */
            /* maxHedgedAttemptsLimit = */
            new ServiceConfigInterceptor(false, 4, 3);
            serviceConfigInterceptor.handleUpdate(serviceConfig);
            Builder<Void, Void> builder = TestMethodDescriptors.voidMethod().toBuilder();
            MethodDescriptor<Void, Void> method = builder.setFullMethodName("SimpleService1/Foo1").build();
            serviceConfigInterceptor.interceptCall(method, CallOptions.DEFAULT, channel);
            Mockito.verify(channel).newCall(ArgumentMatchers.eq(method), callOptionsCap.capture());
            assertThat(callOptionsCap.getValue().getOption(ServiceConfigInterceptor.RETRY_POLICY_KEY)).isNull();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Test
    public void getThrottle() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(RetryPolicyTest.class.getResourceAsStream("/io/grpc/internal/test_retry_service_config.json"), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            } 
            Object serviceConfigObj = JsonParser.parse(sb.toString());
            Assert.assertTrue((serviceConfigObj instanceof Map));
            @SuppressWarnings("unchecked")
            Map<String, ?> serviceConfig = ((Map<String, ?>) (serviceConfigObj));
            Throttle throttle = ServiceConfigUtil.getThrottlePolicy(serviceConfig);
            Assert.assertEquals(new Throttle(10.0F, 0.1F), throttle);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}

