/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.hystrix;


import HystrixCommand.Setter;
import HystrixCommandGroupKey.Factory;
import com.alipay.sofa.rpc.api.future.SofaResponseFuture;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.exception.SofaTimeOutException;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.filter.FilterInvoker;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.test.HelloService;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import java.util.Collections;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href=mailto:scienjus@gmail.com>ScienJus</a>
 */
public class HystrixFilterAsyncTest extends ActivelyDestroyTest {
    private static final int HYSTRIX_DEFAULT_TIMEOUT = 1000;

    @Test
    public void testSuccess() throws InterruptedException {
        ProviderConfig<HelloService> providerConfig = defaultServer(0);
        providerConfig.export();
        ConsumerConfig<HelloService> consumerConfig = defaultClient();
        HelloService helloService = consumerConfig.refer();
        try {
            helloService.sayHello("abc", 24);
            Future future = SofaResponseFuture.getFuture();
            Assert.assertFalse(future.isDone());
            Assert.assertFalse(future.isCancelled());
            String result = ((String) (SofaResponseFuture.getResponse(10000, true)));
            Assert.assertTrue(future.isDone());
            Assert.assertEquals("hello abc from server! age: 24", result);
            Assert.assertEquals(1, ((InvokeCounterHelloService) (providerConfig.getRef())).getExecuteCount());
        } finally {
            providerConfig.unExport();
            consumerConfig.unRefer();
        }
    }

    @Test
    public void testHystrixTimeout() {
        ProviderConfig<HelloService> providerConfig = defaultServer(2000);
        providerConfig.export();
        ConsumerConfig<HelloService> consumerConfig = defaultClient().setTimeout(10000);
        HelloService helloService = consumerConfig.refer();
        long start = System.currentTimeMillis();
        try {
            helloService.sayHello("abc", 24);
            Future future = SofaResponseFuture.getFuture();
            Assert.assertFalse(future.isDone());
            Assert.assertFalse(future.isCancelled());
            Assert.assertTrue((((System.currentTimeMillis()) - start) < (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
            SofaResponseFuture.getResponse(10000, true);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof SofaRpcException));
            Assert.assertTrue(((e.getCause()) instanceof HystrixRuntimeException));
            Assert.assertTrue((((System.currentTimeMillis()) - start) > (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
        } finally {
            providerConfig.unExport();
            consumerConfig.unRefer();
        }
    }

    @Test
    public void testHystrixFallback() throws InterruptedException {
        ProviderConfig<HelloService> providerConfig = defaultServer(2000);
        providerConfig.export();
        ConsumerConfig<HelloService> consumerConfig = defaultClient().setTimeout(10000);
        SofaHystrixConfig.registerFallback(consumerConfig, new HelloServiceFallback());
        HelloService helloService = consumerConfig.refer();
        try {
            long start = System.currentTimeMillis();
            helloService.sayHello("abc", 24);
            Future future = SofaResponseFuture.getFuture();
            Assert.assertFalse(future.isDone());
            Assert.assertFalse(future.isCancelled());
            Assert.assertTrue((((System.currentTimeMillis()) - start) < (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
            String result = ((String) (SofaResponseFuture.getResponse(10000, true)));
            Assert.assertTrue((((System.currentTimeMillis()) - start) > (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
            Assert.assertEquals("fallback abc from server! age: 24", result);
        } finally {
            providerConfig.unExport();
            consumerConfig.unRefer();
        }
    }

    @Test
    public void testHystrixFallbackFactory() throws InterruptedException {
        ProviderConfig<HelloService> providerConfig = defaultServer(2000);
        providerConfig.export();
        ConsumerConfig<HelloService> consumerConfig = defaultClient().setTimeout(10000);
        SofaHystrixConfig.registerFallbackFactory(consumerConfig, new HelloServiceFallbackFactory());
        HelloService helloService = consumerConfig.refer();
        try {
            long start = System.currentTimeMillis();
            helloService.sayHello("abc", 24);
            Future future = SofaResponseFuture.getFuture();
            Assert.assertFalse(future.isDone());
            Assert.assertFalse(future.isCancelled());
            Assert.assertTrue((((System.currentTimeMillis()) - start) < (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
            String result = ((String) (SofaResponseFuture.getResponse(10000, true)));
            Assert.assertTrue((((System.currentTimeMillis()) - start) > (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
            Assert.assertEquals("fallback abc from server! age: 24, error: com.netflix.hystrix.exception.HystrixTimeoutException", result);
        } finally {
            providerConfig.unExport();
            consumerConfig.unRefer();
        }
    }

    @Test
    public void testHystrixCircuitBreakerFallback() throws InterruptedException {
        // ??????
        SetterFactory setterFactory = new SetterFactory() {
            @Override
            public Setter createSetter(FilterInvoker invoker, SofaRequest request) {
                String groupKey = invoker.getConfig().getInterfaceId();
                String commandKey = (request.getMethodName()) + "_circuit_breaker_test";
                return Setter.withGroupKey(Factory.asKey(groupKey)).andCommandKey(HystrixCommandKey.Factory.asKey(commandKey)).andCommandPropertiesDefaults(HystrixCommandProperties.defaultSetter().withCircuitBreakerForceOpen(true));
            }
        };
        ProviderConfig<HelloService> providerConfig = defaultServer(0);
        providerConfig.export();
        ConsumerConfig<HelloService> consumerConfig = defaultClient().setTimeout(10000);
        SofaHystrixConfig.registerFallbackFactory(consumerConfig, new HelloServiceFallbackFactory());
        SofaHystrixConfig.registerSetterFactory(consumerConfig, setterFactory);
        HelloService helloService = consumerConfig.refer();
        try {
            for (int i = 0; i < 20; i++) {
                long start = System.currentTimeMillis();
                helloService.sayHello("abc", 24);
                String result = ((String) (SofaResponseFuture.getResponse(10000, true)));
                Assert.assertTrue((((System.currentTimeMillis()) - start) < (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
                Assert.assertEquals("fallback abc from server! age: 24, error: java.lang.RuntimeException", result);
                Assert.assertTrue((((System.currentTimeMillis()) - start) < (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
            }
            // ????????????????
            Assert.assertEquals(0, ((InvokeCounterHelloService) (providerConfig.getRef())).getExecuteCount());
        } finally {
            providerConfig.unExport();
            consumerConfig.unRefer();
        }
    }

    @Test
    public void testHystrixThreadPoolRejectedFallback() throws InterruptedException {
        // ??????
        SetterFactory setterFactory = new SetterFactory() {
            @Override
            public Setter createSetter(FilterInvoker invoker, SofaRequest request) {
                String groupKey = (invoker.getConfig().getInterfaceId()) + "thread_pool_rejected";
                String commandKey = (request.getMethodName()) + "thread_pool_rejected";
                return Setter.withGroupKey(Factory.asKey(groupKey)).andCommandKey(HystrixCommandKey.Factory.asKey(commandKey)).andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutEnabled(false)).andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(1));
            }
        };
        ProviderConfig<HelloService> providerConfig = defaultServer(2000);
        providerConfig.export();
        ConsumerConfig<HelloService> consumerConfig = defaultClient().setTimeout(10000);
        SofaHystrixConfig.registerFallbackFactory(consumerConfig, new HelloServiceFallbackFactory());
        SofaHystrixConfig.registerSetterFactory(consumerConfig, setterFactory);
        HelloService helloService = consumerConfig.refer();
        try {
            for (int i = 0; i < 20; i++) {
                long start = System.currentTimeMillis();
                helloService.sayHello("abc", 24);
                Future future = SofaResponseFuture.getFuture();
                // ?????????????????????????
                if (i > 0) {
                    Assert.assertTrue(future.isDone());
                    Assert.assertTrue((((System.currentTimeMillis()) - start) < (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
                    String result = ((String) (SofaResponseFuture.getResponse(10000, true)));
                    Assert.assertEquals("fallback abc from server! age: 24, error: java.util.concurrent.RejectedExecutionException", result);
                    Assert.assertTrue((((System.currentTimeMillis()) - start) < (HystrixFilterAsyncTest.HYSTRIX_DEFAULT_TIMEOUT)));
                }
            }
            // ????????????????????????
            Assert.assertEquals(1, ((InvokeCounterHelloService) (providerConfig.getRef())).getExecuteCount());
        } finally {
            providerConfig.unExport();
            consumerConfig.unRefer();
        }
    }

    @Test
    public void testHystrixLockNotRelease() {
        // ?? filter mock ??????
        ProviderConfig<HelloService> providerConfig = defaultServer(2000);
        providerConfig.export();
        ConsumerConfig<HelloService> consumerConfig = defaultClient().setFilterRef(Collections.<Filter>singletonList(new MockTimeoutFilter(2000))).setTimeout(10000);
        HelloService helloService = consumerConfig.refer();
        try {
            helloService.sayHello("abc", 24);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof SofaTimeOutException));
            Assert.assertEquals("Asynchronous execution timed out, please check Hystrix configuration. Events: [SofaAsyncHystrixEvent#EMIT]", e.getMessage());
        }
    }
}

