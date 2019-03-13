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


import Attributes.EMPTY;
import ChannelLogLevel.DEBUG;
import ChannelLogLevel.ERROR;
import ChannelLogLevel.INFO;
import Code.UNAVAILABLE;
import GrpcAttributes.ATTR_LB_ADDR_AUTHORITY;
import GrpcAttributes.NAME_RESOLVER_SERVICE_CONFIG;
import LoadBalancer.ATTR_LOAD_BALANCING_CONFIG;
import Status.RESOURCE_EXHAUSTED;
import io.grpc.Attributes;
import io.grpc.ChannelLogger;
import io.grpc.ConnectivityState;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.LoadBalancer.Helper;
import io.grpc.LoadBalancer.Subchannel;
import io.grpc.LoadBalancer.SubchannelPicker;
import io.grpc.LoadBalancerProvider;
import io.grpc.LoadBalancerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.SynchronizationContext;
import io.grpc.grpclb.GrpclbLoadBalancerProvider;
import io.grpc.internal.AutoConfiguredLoadBalancerFactory.AutoConfiguredLoadBalancer;
import io.grpc.internal.AutoConfiguredLoadBalancerFactory.PolicyException;
import io.grpc.internal.AutoConfiguredLoadBalancerFactory.PolicySelection;
import io.grpc.util.ForwardingLoadBalancerHelper;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static GrpcUtil.DEFAULT_LB_POLICY;


/**
 * Unit tests for {@link AutoConfiguredLoadBalancerFactory}.
 */
@RunWith(JUnit4.class)
public class AutoConfiguredLoadBalancerFactoryTest {
    private static final LoadBalancerRegistry defaultRegistry = LoadBalancerRegistry.getDefaultRegistry();

    private final AutoConfiguredLoadBalancerFactory lbf = new AutoConfiguredLoadBalancerFactory(DEFAULT_LB_POLICY);

    private final ChannelLogger channelLogger = Mockito.mock(ChannelLogger.class);

    private final LoadBalancer testLbBalancer = Mockito.mock(LoadBalancer.class);

    private final LoadBalancer testLbBalancer2 = Mockito.mock(LoadBalancer.class);

    private final LoadBalancerProvider testLbBalancerProvider = Mockito.mock(LoadBalancerProvider.class, AdditionalAnswers.delegatesTo(new AutoConfiguredLoadBalancerFactoryTest.FakeLoadBalancerProvider("test_lb", testLbBalancer)));

    private final LoadBalancerProvider testLbBalancerProvider2 = Mockito.mock(LoadBalancerProvider.class, AdditionalAnswers.delegatesTo(new AutoConfiguredLoadBalancerFactoryTest.FakeLoadBalancerProvider("test_lb2", testLbBalancer2)));

    @Test
    public void newLoadBalancer_isAuto() {
        LoadBalancer lb = lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper());
        assertThat(lb).isInstanceOf(AutoConfiguredLoadBalancer.class);
    }

    @Test
    public void defaultIsPickFirst() {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        assertThat(lb.getDelegateProvider()).isInstanceOf(PickFirstLoadBalancerProvider.class);
        assertThat(lb.getDelegate().getClass().getName()).contains("PickFirst");
    }

    @Test
    public void defaultIsConfigurable() {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (new AutoConfiguredLoadBalancerFactory("test_lb").newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        assertThat(lb.getDelegateProvider()).isSameAs(testLbBalancerProvider);
        assertThat(lb.getDelegate()).isSameAs(testLbBalancer);
    }

    @Test
    public void forwardsCalls() {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        final AtomicInteger calls = new AtomicInteger();
        AutoConfiguredLoadBalancerFactoryTest.TestLoadBalancer testlb = new AutoConfiguredLoadBalancerFactoryTest.TestLoadBalancer() {
            @Override
            public void handleNameResolutionError(Status error) {
                calls.getAndSet(1);
            }

            @Override
            public void handleSubchannelState(Subchannel subchannel, ConnectivityStateInfo stateInfo) {
                calls.getAndSet(2);
            }

            @Override
            public void shutdown() {
                calls.getAndSet(3);
            }
        };
        lb.setDelegate(testlb);
        lb.handleNameResolutionError(RESOURCE_EXHAUSTED);
        assertThat(calls.getAndSet(0)).isEqualTo(1);
        lb.handleSubchannelState(null, null);
        assertThat(calls.getAndSet(0)).isEqualTo(2);
        lb.shutdown();
        assertThat(calls.getAndSet(0)).isEqualTo(3);
    }

    @Test
    public void handleResolvedAddressGroups_keepOldBalancer() {
        final List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        Helper helper = new AutoConfiguredLoadBalancerFactoryTest.TestHelper() {
            @Override
            public Subchannel createSubchannel(List<EquivalentAddressGroup> addrs, Attributes attrs) {
                assertThat(addrs).isEqualTo(servers);
                return new AutoConfiguredLoadBalancerFactoryTest.TestSubchannel(addrs, attrs);
            }
        };
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(helper)));
        LoadBalancer oldDelegate = lb.getDelegate();
        lb.handleResolvedAddressGroups(servers, EMPTY);
        assertThat(lb.getDelegate()).isSameAs(oldDelegate);
    }

    @Test
    public void handleResolvedAddressGroups_shutsDownOldBalancer() {
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("loadBalancingPolicy", "round_robin");
        Attributes serviceConfigAttrs = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        final List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        Helper helper = new AutoConfiguredLoadBalancerFactoryTest.TestHelper() {
            @Override
            public Subchannel createSubchannel(List<EquivalentAddressGroup> addrs, Attributes attrs) {
                assertThat(addrs).isEqualTo(servers);
                return new AutoConfiguredLoadBalancerFactoryTest.TestSubchannel(addrs, attrs);
            }
        };
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(helper)));
        final AtomicBoolean shutdown = new AtomicBoolean();
        AutoConfiguredLoadBalancerFactoryTest.TestLoadBalancer testlb = new AutoConfiguredLoadBalancerFactoryTest.TestLoadBalancer() {
            @Override
            public void handleNameResolutionError(Status error) {
                // noop
            }

            @Override
            public void handleSubchannelState(Subchannel subchannel, ConnectivityStateInfo stateInfo) {
                // noop
            }

            @Override
            public void shutdown() {
                shutdown.set(true);
            }
        };
        lb.setDelegate(testlb);
        lb.handleResolvedAddressGroups(servers, serviceConfigAttrs);
        assertThat(lb.getDelegateProvider().getClass().getName()).isEqualTo("io.grpc.util.SecretRoundRobinLoadBalancerProvider$Provider");
        Assert.assertTrue(shutdown.get());
    }

    @Test
    public void handleResolvedAddressGroups_propagateLbConfigToDelegate() throws Exception {
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"test_lb\": { \"setting1\": \"high\" } } ] }");
        Attributes serviceConfigAttrs = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        final List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        Helper helper = new AutoConfiguredLoadBalancerFactoryTest.TestHelper();
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(helper)));
        lb.handleResolvedAddressGroups(servers, serviceConfigAttrs);
        Mockito.verify(testLbBalancerProvider).newLoadBalancer(ArgumentMatchers.same(helper));
        assertThat(lb.getDelegate()).isSameAs(testLbBalancer);
        ArgumentCaptor<Attributes> attrsCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(testLbBalancer).handleResolvedAddressGroups(ArgumentMatchers.eq(servers), attrsCaptor.capture());
        assertThat(attrsCaptor.getValue().get(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG)).isEqualTo(Collections.singletonMap("setting1", "high"));
        Mockito.verify(testLbBalancer, Mockito.atLeast(0)).canHandleEmptyAddressListFromNameResolution();
        Mockito.verifyNoMoreInteractions(testLbBalancer);
        serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"test_lb\": { \"setting1\": \"low\" } } ] }");
        serviceConfigAttrs = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        lb.handleResolvedAddressGroups(servers, serviceConfigAttrs);
        Mockito.verify(testLbBalancer, Mockito.times(2)).handleResolvedAddressGroups(ArgumentMatchers.eq(servers), attrsCaptor.capture());
        // But the balancer config is changed.
        assertThat(attrsCaptor.getValue().get(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG)).isEqualTo(Collections.singletonMap("setting1", "low"));
        // Service config didn't change policy, thus the delegateLb is not swapped
        Mockito.verifyNoMoreInteractions(testLbBalancer);
        Mockito.verify(testLbBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
    }

    @Test
    public void handleResolvedAddressGroups_propagateOnlyBackendAddrsToDelegate() throws Exception {
        // This case only happens when grpclb is missing.  We will use a local registry
        LoadBalancerRegistry registry = new LoadBalancerRegistry();
        registry.register(new PickFirstLoadBalancerProvider());
        registry.register(new AutoConfiguredLoadBalancerFactoryTest.FakeLoadBalancerProvider("round_robin", testLbBalancer));
        final List<EquivalentAddressGroup> servers = Arrays.asList(new EquivalentAddressGroup(new SocketAddress() {}), new EquivalentAddressGroup(new SocketAddress() {}, Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "ok").build()));
        Helper helper = new AutoConfiguredLoadBalancerFactoryTest.TestHelper();
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (new AutoConfiguredLoadBalancerFactory(registry, DEFAULT_LB_POLICY).newLoadBalancer(helper)));
        lb.handleResolvedAddressGroups(servers, EMPTY);
        assertThat(lb.getDelegate()).isSameAs(testLbBalancer);
        Mockito.verify(testLbBalancer).handleResolvedAddressGroups(ArgumentMatchers.eq(Collections.singletonList(servers.get(0))), ArgumentMatchers.any(Attributes.class));
    }

    @Test
    public void handleResolvedAddressGroups_delegateDoNotAcceptEmptyAddressList_nothing() throws Exception {
        Helper helper = new AutoConfiguredLoadBalancerFactoryTest.TestHelper();
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(helper)));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"test_lb\": { \"setting1\": \"high\" } } ] }");
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build());
        assertThat(lb.getDelegate()).isSameAs(testLbBalancer);
        assertThat(testLbBalancer.canHandleEmptyAddressListFromNameResolution()).isFalse();
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(testLbBalancer).handleNameResolutionError(statusCaptor.capture());
        Status error = statusCaptor.getValue();
        assertThat(error.getCode()).isEqualTo(UNAVAILABLE);
        assertThat(error.getDescription()).startsWith("Name resolver returned no usable address");
    }

    @Test
    public void handleResolvedAddressGroups_delegateAcceptsEmptyAddressList() throws Exception {
        Helper helper = new AutoConfiguredLoadBalancerFactoryTest.TestHelper();
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(helper)));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"test_lb2\": { \"setting1\": \"high\" } } ] }");
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build());
        assertThat(lb.getDelegate()).isSameAs(testLbBalancer2);
        assertThat(testLbBalancer2.canHandleEmptyAddressListFromNameResolution()).isTrue();
        ArgumentCaptor<Attributes> attrsCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(testLbBalancer2).handleResolvedAddressGroups(ArgumentMatchers.eq(Collections.<EquivalentAddressGroup>emptyList()), attrsCaptor.capture());
        Map<String, ?> lbConfig = attrsCaptor.getValue().get(ATTR_LOAD_BALANCING_CONFIG);
        assertThat(lbConfig).isEqualTo(Collections.<String, Object>singletonMap("setting1", "high"));
        assertThat(attrsCaptor.getValue().get(NAME_RESOLVER_SERVICE_CONFIG)).isSameAs(serviceConfig);
    }

    @Test
    public void decideLoadBalancerProvider_noBalancerAddresses_noServiceConfig_pickFirst() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = null;
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider).isInstanceOf(PickFirstLoadBalancerProvider.class);
        assertThat(selection.serverList).isEqualTo(servers);
        assertThat(selection.config).isNull();
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @Test
    public void decideLoadBalancerProvider_noBalancerAddresses_noServiceConfig_customDefault() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (new AutoConfiguredLoadBalancerFactory("test_lb").newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = null;
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider).isSameAs(testLbBalancerProvider);
        assertThat(selection.serverList).isEqualTo(servers);
        assertThat(selection.config).isNull();
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @Test
    public void decideLoadBalancerProvider_oneBalancer_noServiceConfig_grpclb() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = null;
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}, Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "ok").build()));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider).isInstanceOf(GrpclbLoadBalancerProvider.class);
        assertThat(selection.serverList).isEqualTo(servers);
        assertThat(selection.config).isNull();
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @Test
    public void decideLoadBalancerProvider_serviceConfigLbPolicy() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("loadBalancingPolicy", "round_robin");
        List<EquivalentAddressGroup> servers = Arrays.asList(new EquivalentAddressGroup(new SocketAddress() {}, Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "ok").build()), new EquivalentAddressGroup(new SocketAddress() {}));
        List<EquivalentAddressGroup> backends = Arrays.asList(servers.get(1));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider.getClass().getName()).isEqualTo("io.grpc.util.SecretRoundRobinLoadBalancerProvider$Provider");
        assertThat(selection.serverList).isEqualTo(backends);
        assertThat(selection.config).isEqualTo(Collections.<String, Object>emptyMap());
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void decideLoadBalancerProvider_serviceConfigLbConfig() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"round_robin\": {} } ] }");
        List<EquivalentAddressGroup> servers = Arrays.asList(new EquivalentAddressGroup(new SocketAddress() {}, Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "ok").build()), new EquivalentAddressGroup(new SocketAddress() {}));
        List<EquivalentAddressGroup> backends = Arrays.asList(servers.get(1));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider.getClass().getName()).isEqualTo("io.grpc.util.SecretRoundRobinLoadBalancerProvider$Provider");
        assertThat(selection.serverList).isEqualTo(backends);
        assertThat(selection.config).isEqualTo(Collections.<String, Object>emptyMap());
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @Test
    public void decideLoadBalancerProvider_grpclbConfigPropagated() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig(("{\"loadBalancingConfig\": [" + ("{\"grpclb\": {\"childPolicy\": [ {\"pick_first\": {} } ] } }" + "] }")));
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}, Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "ok").build()));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider).isInstanceOf(GrpclbLoadBalancerProvider.class);
        assertThat(selection.serverList).isEqualTo(servers);
        assertThat(selection.config).isEqualTo(AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"childPolicy\": [ {\"pick_first\": {} } ] }"));
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @Test
    public void decideLoadBalancerProvider_policyUnavailButGrpclbAddressPresent() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig(("{\"loadBalancingConfig\": [" + ("{\"unavail\": {} }" + "] }")));
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}, Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "ok").build()));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider).isInstanceOf(GrpclbLoadBalancerProvider.class);
        assertThat(selection.serverList).isEqualTo(servers);
        assertThat(selection.config).isNull();
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @Test
    public void decideLoadBalancerProvider_grpclbProviderNotFound_fallbackToRoundRobin() throws Exception {
        LoadBalancerRegistry registry = new LoadBalancerRegistry();
        registry.register(new PickFirstLoadBalancerProvider());
        LoadBalancerProvider fakeRoundRobinProvider = new AutoConfiguredLoadBalancerFactoryTest.FakeLoadBalancerProvider("round_robin", testLbBalancer);
        registry.register(fakeRoundRobinProvider);
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"grpclb\": {} } ] }");
        List<EquivalentAddressGroup> servers = Arrays.asList(new EquivalentAddressGroup(new SocketAddress() {}, Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "ok").build()), new EquivalentAddressGroup(new SocketAddress() {}));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider).isSameAs(fakeRoundRobinProvider);
        assertThat(selection.config).isNull();
        Mockito.verify(channelLogger).log(ArgumentMatchers.eq(ERROR), ArgumentMatchers.startsWith("Found balancer addresses but grpclb runtime is missing"));
        // Called for the second time, the warning is only logged once
        selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider).isSameAs(fakeRoundRobinProvider);
        // Balancer addresses are filtered out in the server list passed to round_robin
        assertThat(selection.serverList).containsExactly(servers.get(1));
        assertThat(selection.config).isNull();
        Mockito.verifyNoMoreInteractions(channelLogger);
    }

    @Test
    public void decideLoadBalancerProvider_grpclbProviderNotFound_noBackendAddress() throws Exception {
        LoadBalancerRegistry registry = new LoadBalancerRegistry();
        registry.register(new PickFirstLoadBalancerProvider());
        registry.register(new AutoConfiguredLoadBalancerFactoryTest.FakeLoadBalancerProvider("round_robin", testLbBalancer));
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"grpclb\": {} } ] }");
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}, Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "ok").build()));
        try {
            lb.decideLoadBalancerProvider(servers, serviceConfig);
            Assert.fail("Should throw");
        } catch (PolicyException e) {
            assertThat(e).hasMessageThat().isEqualTo("Received ONLY balancer addresses but grpclb runtime is missing");
        }
    }

    @Test
    public void decideLoadBalancerProvider_serviceConfigLbPolicyOverridesDefault() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("loadBalancingPolicy", "round_robin");
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider.getClass().getName()).isEqualTo("io.grpc.util.SecretRoundRobinLoadBalancerProvider$Provider");
        assertThat(selection.config).isEqualTo(Collections.emptyMap());
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @Test
    public void decideLoadBalancerProvider_serviceConfigLbConfigOverridesDefault() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"round_robin\": {\"setting1\": \"high\"} } ] }");
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider.getClass().getName()).isEqualTo("io.grpc.util.SecretRoundRobinLoadBalancerProvider$Provider");
        assertThat(selection.serverList).isEqualTo(servers);
        assertThat(selection.config).isEqualTo(Collections.singletonMap("setting1", "high"));
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @Test
    public void decideLoadBalancerProvider_serviceConfigLbPolicyFailsOnUnknown() {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("loadBalancingPolicy", "MAGIC_BALANCER");
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        try {
            lb.decideLoadBalancerProvider(servers, serviceConfig);
            Assert.fail();
        } catch (PolicyException e) {
            assertThat(e).hasMessageThat().isEqualTo("None of [magic_balancer] specified by Service Config are available.");
        }
    }

    @Test
    public void decideLoadBalancerProvider_serviceConfigLbConfigFailsOnUnknown() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"magic_balancer\": {} } ] }");
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        try {
            lb.decideLoadBalancerProvider(servers, serviceConfig);
            Assert.fail();
        } catch (PolicyException e) {
            assertThat(e).hasMessageThat().isEqualTo("None of [magic_balancer] specified by Service Config are available.");
        }
    }

    @Test
    public void decideLoadBalancerProvider_serviceConfigLbConfigSkipUnknown() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        Map<String, ?> serviceConfig = AutoConfiguredLoadBalancerFactoryTest.parseConfig("{\"loadBalancingConfig\": [ {\"magic_balancer\": {} }, {\"round_robin\": {} } ] }");
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, serviceConfig);
        assertThat(selection.provider.getClass().getName()).isEqualTo("io.grpc.util.SecretRoundRobinLoadBalancerProvider$Provider");
        assertThat(selection.serverList).isEqualTo(servers);
        assertThat(selection.config).isEqualTo(Collections.emptyMap());
        Mockito.verify(channelLogger).log(ArgumentMatchers.eq(DEBUG), ArgumentMatchers.eq("{0} specified by Service Config are not available"), ArgumentMatchers.eq(new LinkedHashSet(Arrays.asList("magic_balancer"))));
    }

    @Test
    public void decideLoadBalancerProvider_serviceConfigHasZeroLbConfig() throws Exception {
        AutoConfiguredLoadBalancer lb = ((AutoConfiguredLoadBalancer) (lbf.newLoadBalancer(new AutoConfiguredLoadBalancerFactoryTest.TestHelper())));
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        PolicySelection selection = lb.decideLoadBalancerProvider(servers, Collections.<String, Object>emptyMap());
        assertThat(selection.provider).isInstanceOf(PickFirstLoadBalancerProvider.class);
        assertThat(selection.serverList).isEqualTo(servers);
        assertThat(selection.config).isNull();
        Mockito.verifyZeroInteractions(channelLogger);
    }

    @Test
    public void channelTracing_lbPolicyChanged() {
        final FakeClock clock = new FakeClock();
        List<EquivalentAddressGroup> servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}));
        Helper helper = new AutoConfiguredLoadBalancerFactoryTest.TestHelper() {
            @Override
            public Subchannel createSubchannel(List<EquivalentAddressGroup> addrs, Attributes attrs) {
                return new AutoConfiguredLoadBalancerFactoryTest.TestSubchannel(addrs, attrs);
            }

            @Override
            public ManagedChannel createOobChannel(EquivalentAddressGroup eag, String authority) {
                return Mockito.mock(ManagedChannel.class, Mockito.RETURNS_DEEP_STUBS);
            }

            @Override
            public String getAuthority() {
                return "fake_authority";
            }

            @Override
            public SynchronizationContext getSynchronizationContext() {
                return new SynchronizationContext(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        throw new AssertionError(e);
                    }
                });
            }

            @Override
            public ScheduledExecutorService getScheduledExecutorService() {
                return clock.getScheduledExecutorService();
            }
        };
        LoadBalancer lb = new AutoConfiguredLoadBalancerFactory(DEFAULT_LB_POLICY).newLoadBalancer(helper);
        lb.handleResolvedAddressGroups(servers, EMPTY);
        Mockito.verifyNoMoreInteractions(channelLogger);
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("loadBalancingPolicy", "round_robin");
        lb.handleResolvedAddressGroups(servers, Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build());
        Mockito.verify(channelLogger).log(ArgumentMatchers.eq(INFO), ArgumentMatchers.eq("Load balancer changed from {0} to {1}"), ArgumentMatchers.eq("PickFirstLoadBalancer"), ArgumentMatchers.eq("RoundRobinLoadBalancer"));
        Mockito.verify(channelLogger).log(ArgumentMatchers.eq(DEBUG), ArgumentMatchers.eq("Load-balancing config: {0}"), ArgumentMatchers.eq(Collections.emptyMap()));
        Mockito.verifyNoMoreInteractions(channelLogger);
        serviceConfig.put("loadBalancingPolicy", "round_robin");
        lb.handleResolvedAddressGroups(servers, Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build());
        Mockito.verify(channelLogger, Mockito.times(2)).log(ArgumentMatchers.eq(DEBUG), ArgumentMatchers.eq("Load-balancing config: {0}"), ArgumentMatchers.eq(Collections.emptyMap()));
        Mockito.verifyNoMoreInteractions(channelLogger);
        servers = Collections.singletonList(new EquivalentAddressGroup(new SocketAddress() {}, Attributes.newBuilder().set(ATTR_LB_ADDR_AUTHORITY, "ok").build()));
        lb.handleResolvedAddressGroups(servers, EMPTY);
        Mockito.verify(channelLogger).log(ArgumentMatchers.eq(INFO), ArgumentMatchers.eq("Load balancer changed from {0} to {1}"), ArgumentMatchers.eq("RoundRobinLoadBalancer"), ArgumentMatchers.eq("GrpclbLoadBalancer"));
        Mockito.verifyNoMoreInteractions(channelLogger);
    }

    public static class ForwardingLoadBalancer extends LoadBalancer {
        private final LoadBalancer delegate;

        public ForwardingLoadBalancer(LoadBalancer delegate) {
            this.delegate = delegate;
        }

        protected LoadBalancer delegate() {
            return delegate;
        }

        @Override
        public void handleResolvedAddressGroups(List<EquivalentAddressGroup> servers, Attributes attributes) {
            delegate().handleResolvedAddressGroups(servers, attributes);
        }

        @Override
        public void handleNameResolutionError(Status error) {
            delegate().handleNameResolutionError(error);
        }

        @Override
        public void handleSubchannelState(Subchannel subchannel, ConnectivityStateInfo stateInfo) {
            delegate().handleSubchannelState(subchannel, stateInfo);
        }

        @Override
        public void shutdown() {
            delegate().shutdown();
        }
    }

    private static class TestLoadBalancer extends AutoConfiguredLoadBalancerFactoryTest.ForwardingLoadBalancer {
        TestLoadBalancer() {
            super(null);
        }
    }

    private class TestHelper extends ForwardingLoadBalancerHelper {
        @Override
        protected Helper delegate() {
            return null;
        }

        @Override
        public ChannelLogger getChannelLogger() {
            return channelLogger;
        }

        @Override
        public void updateBalancingState(ConnectivityState newState, SubchannelPicker newPicker) {
            // noop
        }
    }

    private static class TestSubchannel extends Subchannel {
        TestSubchannel(List<EquivalentAddressGroup> addrs, Attributes attrs) {
            this.addrs = addrs;
            this.attrs = attrs;
        }

        final List<EquivalentAddressGroup> addrs;

        final Attributes attrs;

        @Override
        public void shutdown() {
        }

        @Override
        public void requestConnection() {
        }

        @Override
        public List<EquivalentAddressGroup> getAllAddresses() {
            return addrs;
        }

        @Override
        public Attributes getAttributes() {
            return attrs;
        }
    }

    private static final class FakeLoadBalancerProvider extends LoadBalancerProvider {
        private final String policyName;

        private final LoadBalancer balancer;

        FakeLoadBalancerProvider(String policyName, LoadBalancer balancer) {
            this.policyName = policyName;
            this.balancer = balancer;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public int getPriority() {
            return 5;
        }

        @Override
        public String getPolicyName() {
            return policyName;
        }

        @Override
        public LoadBalancer newLoadBalancer(Helper helper) {
            return balancer;
        }
    }
}

