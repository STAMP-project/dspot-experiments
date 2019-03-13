/**
 * Copyright 2016 The gRPC Authors
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


import AbstractManagedChannelImplBuilder.IDLE_MODE_DEFAULT_TIMEOUT_MILLIS;
import AbstractManagedChannelImplBuilder.IDLE_MODE_MAX_TIMEOUT_DAYS;
import AbstractManagedChannelImplBuilder.IDLE_MODE_MIN_TIMEOUT_MILLIS;
import CensusStatsModule.StatsClientInterceptor;
import CensusTracingModule.TracingClientInterceptor;
import ManagedChannelImpl.IDLE_TIMEOUT_MILLIS_DISABLE;
import NameResolver.Factory;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.LoadBalancer;
import io.grpc.MethodDescriptor;
import io.grpc.NameResolver;
import io.grpc.internal.testing.StatsTestUtils.FakeStatsRecorder;
import io.grpc.internal.testing.StatsTestUtils.FakeTagContextBinarySerializer;
import io.grpc.internal.testing.StatsTestUtils.FakeTagger;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Executor;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static GrpcUtil.STOPWATCH_SUPPLIER;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * Unit tests for {@link AbstractManagedChannelImplBuilder}.
 */
@RunWith(JUnit4.class)
public class AbstractManagedChannelImplBuilderTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private static final ClientInterceptor DUMMY_USER_INTERCEPTOR = new ClientInterceptor() {
        @Override
        public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
            return next.newCall(method, callOptions);
        }
    };

    private AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("fake");

    private AbstractManagedChannelImplBuilderTest.Builder directAddressBuilder = new AbstractManagedChannelImplBuilderTest.Builder(new SocketAddress() {}, "fake");

    @Test
    public void executor_default() {
        Assert.assertNotNull(builder.executorPool);
    }

    @Test
    public void executor_normal() {
        Executor executor = Mockito.mock(Executor.class);
        Assert.assertEquals(builder, builder.executor(executor));
        Assert.assertEquals(executor, builder.executorPool.getObject());
    }

    @Test
    public void executor_null() {
        ObjectPool<? extends Executor> defaultValue = builder.executorPool;
        builder.executor(Mockito.mock(Executor.class));
        Assert.assertEquals(builder, executor(null));
        Assert.assertEquals(defaultValue, builder.executorPool);
    }

    @Test
    public void directExecutor() {
        Assert.assertEquals(builder, builder.directExecutor());
        Assert.assertEquals(MoreExecutors.directExecutor(), builder.executorPool.getObject());
    }

    @Test
    public void nameResolverFactory_default() {
        Assert.assertNotNull(getNameResolverFactory());
    }

    @Test
    public void nameResolverFactory_normal() {
        NameResolver.Factory nameResolverFactory = Mockito.mock(Factory.class);
        Assert.assertEquals(builder, builder.nameResolverFactory(nameResolverFactory));
        Assert.assertEquals(nameResolverFactory, getNameResolverFactory());
    }

    @Test
    public void nameResolverFactory_null() {
        NameResolver.Factory defaultValue = builder.getNameResolverFactory();
        nameResolverFactory(Mockito.mock(Factory.class));
        Assert.assertEquals(builder, builder.nameResolverFactory(null));
        Assert.assertEquals(defaultValue, getNameResolverFactory());
    }

    @Test(expected = IllegalStateException.class)
    public void nameResolverFactory_notAllowedWithDirectAddress() {
        nameResolverFactory(Mockito.mock(Factory.class));
    }

    @Test
    public void loadBalancerFactory_default() {
        Assert.assertNull(builder.loadBalancerFactory);
    }

    @Test
    @Deprecated
    public void loadBalancerFactory_normal() {
        LoadBalancer.Factory loadBalancerFactory = Mockito.mock(LoadBalancer.Factory.class);
        Assert.assertEquals(builder, builder.loadBalancerFactory(loadBalancerFactory));
        Assert.assertEquals(loadBalancerFactory, builder.loadBalancerFactory);
    }

    @Test
    @Deprecated
    public void loadBalancerFactory_null() {
        LoadBalancer.Factory defaultValue = builder.loadBalancerFactory;
        loadBalancerFactory(Mockito.mock(LoadBalancer.Factory.class));
        Assert.assertEquals(builder, builder.loadBalancerFactory(null));
        Assert.assertEquals(defaultValue, builder.loadBalancerFactory);
    }

    @Test(expected = IllegalStateException.class)
    @Deprecated
    public void loadBalancerFactory_notAllowedWithDirectAddress() {
        loadBalancerFactory(Mockito.mock(LoadBalancer.Factory.class));
    }

    @Test
    public void defaultLoadBalancingPolicy_default() {
        Assert.assertEquals("pick_first", builder.defaultLbPolicy);
    }

    @Test
    public void defaultLoadBalancingPolicy_normal() {
        Assert.assertEquals(builder, defaultLoadBalancingPolicy("magic_balancer"));
        Assert.assertEquals("magic_balancer", builder.defaultLbPolicy);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultLoadBalancingPolicy_null() {
        builder.defaultLoadBalancingPolicy(null);
    }

    @Test(expected = IllegalStateException.class)
    public void defaultLoadBalancingPolicy_notAllowedWithDirectAddress() {
        defaultLoadBalancingPolicy("magic_balancer");
    }

    @Test
    public void fullStreamDecompression_default() {
        TestCase.assertFalse(builder.fullStreamDecompression);
    }

    @Test
    public void fullStreamDecompression_enabled() {
        Assert.assertEquals(builder, enableFullStreamDecompression());
        Assert.assertTrue(builder.fullStreamDecompression);
    }

    @Test
    public void decompressorRegistry_default() {
        Assert.assertNotNull(builder.decompressorRegistry);
    }

    @Test
    public void decompressorRegistry_normal() {
        DecompressorRegistry decompressorRegistry = DecompressorRegistry.emptyInstance();
        Assert.assertNotEquals(decompressorRegistry, builder.decompressorRegistry);
        Assert.assertEquals(builder, builder.decompressorRegistry(decompressorRegistry));
        Assert.assertEquals(decompressorRegistry, builder.decompressorRegistry);
    }

    @Test
    public void decompressorRegistry_null() {
        DecompressorRegistry defaultValue = builder.decompressorRegistry;
        Assert.assertEquals(builder, builder.decompressorRegistry(DecompressorRegistry.emptyInstance()));
        Assert.assertNotEquals(defaultValue, builder.decompressorRegistry);
        decompressorRegistry(null);
        Assert.assertEquals(defaultValue, builder.decompressorRegistry);
    }

    @Test
    public void compressorRegistry_default() {
        Assert.assertNotNull(builder.compressorRegistry);
    }

    @Test
    public void compressorRegistry_normal() {
        CompressorRegistry compressorRegistry = CompressorRegistry.newEmptyInstance();
        Assert.assertNotEquals(compressorRegistry, builder.compressorRegistry);
        Assert.assertEquals(builder, builder.compressorRegistry(compressorRegistry));
        Assert.assertEquals(compressorRegistry, builder.compressorRegistry);
    }

    @Test
    public void compressorRegistry_null() {
        CompressorRegistry defaultValue = builder.compressorRegistry;
        builder.compressorRegistry(CompressorRegistry.newEmptyInstance());
        Assert.assertNotEquals(defaultValue, builder.compressorRegistry);
        Assert.assertEquals(builder, compressorRegistry(null));
        Assert.assertEquals(defaultValue, builder.compressorRegistry);
    }

    @Test
    public void userAgent_default() {
        Assert.assertNull(builder.userAgent);
    }

    @Test
    public void userAgent_normal() {
        String userAgent = "user-agent/1";
        Assert.assertEquals(builder, builder.userAgent(userAgent));
        Assert.assertEquals(userAgent, builder.userAgent);
    }

    @Test
    public void userAgent_null() {
        Assert.assertEquals(builder, userAgent(null));
        Assert.assertNull(builder.userAgent);
        builder.userAgent("user-agent/1");
        userAgent(null);
        Assert.assertNull(builder.userAgent);
    }

    @Test
    public void overrideAuthority_default() {
        Assert.assertNull(builder.authorityOverride);
    }

    @Test
    public void overrideAuthority_normal() {
        String overrideAuthority = "best-authority";
        Assert.assertEquals(builder, overrideAuthority(overrideAuthority));
        Assert.assertEquals(overrideAuthority, builder.authorityOverride);
    }

    @Test(expected = NullPointerException.class)
    public void overrideAuthority_null() {
        builder.overrideAuthority(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void overrideAuthority_invalid() {
        overrideAuthority("not_allowed");
    }

    @Test
    public void overrideAuthority_getNameResolverFactory() {
        AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("target");
        Assert.assertNull(builder.authorityOverride);
        TestCase.assertFalse(((getNameResolverFactory()) instanceof OverrideAuthorityNameResolverFactory));
        overrideAuthority("google.com");
        Assert.assertTrue(((getNameResolverFactory()) instanceof OverrideAuthorityNameResolverFactory));
    }

    @Test
    public void makeTargetStringForDirectAddress_scopedIpv6() throws Exception {
        InetSocketAddress address = new InetSocketAddress("0:0:0:0:0:0:0:0%0", 10005);
        Assert.assertEquals("/0:0:0:0:0:0:0:0%0:10005", address.toString());
        String target = AbstractManagedChannelImplBuilder.makeTargetStringForDirectAddress(address);
        URI uri = new URI(target);
        Assert.assertEquals("directaddress:////0:0:0:0:0:0:0:0%250:10005", target);
        Assert.assertEquals(target, uri.toString());
    }

    @Test
    public void getEffectiveInterceptors_default() {
        builder.intercept(AbstractManagedChannelImplBuilderTest.DUMMY_USER_INTERCEPTOR);
        List<ClientInterceptor> effectiveInterceptors = getEffectiveInterceptors();
        Assert.assertEquals(3, effectiveInterceptors.size());
        assertThat(effectiveInterceptors.get(0)).isInstanceOf(TracingClientInterceptor.class);
        assertThat(effectiveInterceptors.get(1)).isInstanceOf(StatsClientInterceptor.class);
        assertThat(effectiveInterceptors.get(2)).isSameAs(AbstractManagedChannelImplBuilderTest.DUMMY_USER_INTERCEPTOR);
    }

    @Test
    public void getEffectiveInterceptors_disableStats() {
        builder.intercept(AbstractManagedChannelImplBuilderTest.DUMMY_USER_INTERCEPTOR);
        setStatsEnabled(false);
        List<ClientInterceptor> effectiveInterceptors = getEffectiveInterceptors();
        Assert.assertEquals(2, effectiveInterceptors.size());
        assertThat(effectiveInterceptors.get(0)).isInstanceOf(TracingClientInterceptor.class);
        assertThat(effectiveInterceptors.get(1)).isSameAs(AbstractManagedChannelImplBuilderTest.DUMMY_USER_INTERCEPTOR);
    }

    @Test
    public void getEffectiveInterceptors_disableTracing() {
        builder.intercept(AbstractManagedChannelImplBuilderTest.DUMMY_USER_INTERCEPTOR);
        setTracingEnabled(false);
        List<ClientInterceptor> effectiveInterceptors = getEffectiveInterceptors();
        Assert.assertEquals(2, effectiveInterceptors.size());
        assertThat(effectiveInterceptors.get(0)).isInstanceOf(StatsClientInterceptor.class);
        assertThat(effectiveInterceptors.get(1)).isSameAs(AbstractManagedChannelImplBuilderTest.DUMMY_USER_INTERCEPTOR);
    }

    @Test
    public void getEffectiveInterceptors_disableBoth() {
        builder.intercept(AbstractManagedChannelImplBuilderTest.DUMMY_USER_INTERCEPTOR);
        setStatsEnabled(false);
        setTracingEnabled(false);
        List<ClientInterceptor> effectiveInterceptors = getEffectiveInterceptors();
        assertThat(effectiveInterceptors).containsExactly(AbstractManagedChannelImplBuilderTest.DUMMY_USER_INTERCEPTOR);
    }

    @Test
    public void idleTimeout() {
        AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("target");
        Assert.assertEquals(IDLE_MODE_DEFAULT_TIMEOUT_MILLIS, getIdleTimeoutMillis());
        builder.idleTimeout(Long.MAX_VALUE, DAYS);
        Assert.assertEquals(IDLE_TIMEOUT_MILLIS_DISABLE, getIdleTimeoutMillis());
        builder.idleTimeout(IDLE_MODE_MAX_TIMEOUT_DAYS, DAYS);
        Assert.assertEquals(IDLE_TIMEOUT_MILLIS_DISABLE, getIdleTimeoutMillis());
        try {
            builder.idleTimeout(0, SECONDS);
            Assert.fail("Should throw");
        } catch (IllegalArgumentException e) {
            // expected
        }
        builder.idleTimeout(1, NANOSECONDS);
        Assert.assertEquals(IDLE_MODE_MIN_TIMEOUT_MILLIS, getIdleTimeoutMillis());
        builder.idleTimeout(30, SECONDS);
        Assert.assertEquals(SECONDS.toMillis(30), getIdleTimeoutMillis());
    }

    @Test
    public void maxRetryAttempts() {
        AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("target");
        Assert.assertEquals(5, builder.maxRetryAttempts);
        builder.maxRetryAttempts(3);
        Assert.assertEquals(3, builder.maxRetryAttempts);
    }

    @Test
    public void maxHedgedAttempts() {
        AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("target");
        Assert.assertEquals(5, builder.maxHedgedAttempts);
        builder.maxHedgedAttempts(3);
        Assert.assertEquals(3, builder.maxHedgedAttempts);
    }

    @Test
    public void retryBufferSize() {
        AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("target");
        Assert.assertEquals((1L << 24), builder.retryBufferSize);
        builder.retryBufferSize(3456L);
        Assert.assertEquals(3456L, builder.retryBufferSize);
    }

    @Test
    public void perRpcBufferLimit() {
        AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("target");
        Assert.assertEquals((1L << 20), builder.perRpcBufferLimit);
        builder.perRpcBufferLimit(3456L);
        Assert.assertEquals(3456L, builder.perRpcBufferLimit);
    }

    @Test
    public void retryBufferSizeInvalidArg() {
        AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("target");
        thrown.expect(IllegalArgumentException.class);
        builder.retryBufferSize(0L);
    }

    @Test
    public void perRpcBufferLimitInvalidArg() {
        AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("target");
        thrown.expect(IllegalArgumentException.class);
        builder.perRpcBufferLimit(0L);
    }

    @Test
    public void disableRetry() {
        AbstractManagedChannelImplBuilderTest.Builder builder = new AbstractManagedChannelImplBuilderTest.Builder("target");
        enableRetry();
        Assert.assertTrue(builder.retryEnabled);
        builder.disableRetry();
        TestCase.assertFalse(builder.retryEnabled);
        enableRetry();
        Assert.assertTrue(builder.retryEnabled);
        builder.disableRetry();
        TestCase.assertFalse(builder.retryEnabled);
    }

    static class Builder extends AbstractManagedChannelImplBuilder<AbstractManagedChannelImplBuilderTest.Builder> {
        Builder(String target) {
            super(target);
            overrideCensusStatsModule(new CensusStatsModule(new FakeTagger(), new FakeTagContextBinarySerializer(), new FakeStatsRecorder(), STOPWATCH_SUPPLIER, true, true, true, true));
        }

        Builder(SocketAddress directServerAddress, String authority) {
            super(directServerAddress, authority);
            overrideCensusStatsModule(new CensusStatsModule(new FakeTagger(), new FakeTagContextBinarySerializer(), new FakeStatsRecorder(), STOPWATCH_SUPPLIER, true, true, true, true));
        }

        @Override
        protected ClientTransportFactory buildTransportFactory() {
            throw new UnsupportedOperationException();
        }
    }
}

