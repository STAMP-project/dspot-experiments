/**
 * Copyright 2019 The gRPC Authors
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
package io.grpc.xds;


import ConnectivityState.READY;
import Status.UNAVAILABLE;
import io.envoyproxy.envoy.api.v2.DiscoveryRequest;
import io.envoyproxy.envoy.api.v2.DiscoveryResponse;
import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.LoadBalancer.Helper;
import io.grpc.LoadBalancer.Subchannel;
import io.grpc.LoadBalancerProvider;
import io.grpc.LoadBalancerRegistry;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.SynchronizationContext;
import io.grpc.internal.FakeClock;
import io.grpc.internal.JsonParser;
import io.grpc.internal.ServiceConfigUtil;
import io.grpc.internal.ServiceConfigUtil.LbConfig;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.xds.XdsLbState.SubchannelStore;
import io.grpc.xds.XdsLbState.SubchannelStoreImpl;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link XdsLoadBalancer}.
 */
@RunWith(JUnit4.class)
public class XdsLoadBalancerTest {
    @Rule
    public final GrpcCleanupRule cleanupRule = new GrpcCleanupRule();

    @Mock
    private Helper helper;

    @Mock
    private LoadBalancer fakeBalancer1;

    @Mock
    private LoadBalancer fakeBalancer2;

    private XdsLoadBalancer lb;

    private final FakeClock fakeClock = new FakeClock();

    private final StreamRecorder<DiscoveryRequest> streamRecorder = StreamRecorder.create();

    private final LoadBalancerRegistry lbRegistry = new LoadBalancerRegistry();

    private final LoadBalancerProvider lbProvider1 = new LoadBalancerProvider() {
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
            return "supported_1";
        }

        @Override
        public LoadBalancer newLoadBalancer(Helper helper) {
            return fakeBalancer1;
        }
    };

    private final LoadBalancerProvider lbProvider2 = new LoadBalancerProvider() {
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
            return "supported_2";
        }

        @Override
        public LoadBalancer newLoadBalancer(Helper helper) {
            return fakeBalancer2;
        }
    };

    private final LoadBalancerProvider roundRobin = new LoadBalancerProvider() {
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
            return "round_robin";
        }

        @Override
        public LoadBalancer newLoadBalancer(Helper helper) {
            return null;
        }
    };

    private final SynchronizationContext syncContext = new SynchronizationContext(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            throw new AssertionError(e);
        }
    });

    private final SubchannelStore fakeSubchannelStore = Mockito.mock(SubchannelStore.class, AdditionalAnswers.delegatesTo(new SubchannelStoreImpl()));

    private ManagedChannel oobChannel1;

    private ManagedChannel oobChannel2;

    private ManagedChannel oobChannel3;

    private StreamObserver<DiscoveryResponse> serverResponseWriter;

    @Test
    public void selectChildPolicy() throws Exception {
        String lbConfigRaw = "{\"xds_experimental\" : { " + (((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"unsupported_1\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}},") + "{\"supported_2\" : {\"key\" : \"val\"}}],") + "\"fallbackPolicy\" : [{\"lbPolicy3\" : {\"key\" : \"val\"}}, {\"lbPolicy4\" : {}}]") + "}}");
        LbConfig expectedChildPolicy = ServiceConfigUtil.unwrapLoadBalancingConfig(JsonParser.parse("{\"supported_1\" : {\"key\" : \"val\"}}"));
        LbConfig childPolicy = XdsLoadBalancer.selectChildPolicy(ServiceConfigUtil.unwrapLoadBalancingConfig(JsonParser.parse(lbConfigRaw)), lbRegistry);
        Assert.assertEquals(expectedChildPolicy, childPolicy);
    }

    @Test
    public void selectFallBackPolicy() throws Exception {
        String lbConfigRaw = "{\"xds_experimental\" : { " + (((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"lbPolicy3\" : {\"key\" : \"val\"}}, {\"lbPolicy4\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}},") + "{\"supported_2\" : {\"key\" : \"val\"}}]") + "}}");
        LbConfig expectedFallbackPolicy = ServiceConfigUtil.unwrapLoadBalancingConfig(JsonParser.parse("{\"supported_1\" : {\"key\" : \"val\"}}"));
        LbConfig fallbackPolicy = XdsLoadBalancer.selectFallbackPolicy(ServiceConfigUtil.unwrapLoadBalancingConfig(JsonParser.parse(lbConfigRaw)), lbRegistry);
        Assert.assertEquals(expectedFallbackPolicy, fallbackPolicy);
    }

    @Test
    public void selectFallBackPolicy_roundRobinIsDefault() throws Exception {
        String lbConfigRaw = "{\"xds_experimental\" : { " + (("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"lbPolicy3\" : {\"key\" : \"val\"}}, {\"lbPolicy4\" : {}}]") + "}}");
        LbConfig expectedFallbackPolicy = ServiceConfigUtil.unwrapLoadBalancingConfig(JsonParser.parse("{\"round_robin\" : {}}"));
        LbConfig fallbackPolicy = XdsLoadBalancer.selectFallbackPolicy(ServiceConfigUtil.unwrapLoadBalancingConfig(JsonParser.parse(lbConfigRaw)), lbRegistry);
        Assert.assertEquals(expectedFallbackPolicy, fallbackPolicy);
    }

    @Test
    public void canHandleEmptyAddressListFromNameResolution() {
        Assert.assertTrue(lb.canHandleEmptyAddressListFromNameResolution());
    }

    @Test
    public void resolverEvent_standardModeToStandardMode() throws Exception {
        String lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"unsupported\" : {\"key\" : \"val\"}}, {\"unsupported_2\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        Attributes attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        XdsLbState xdsLbState1 = lb.getXdsLbStateForTest();
        assertThat(xdsLbState1.childPolicy).isNull();
        Mockito.verify(helper).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        Mockito.verify(oobChannel1).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
        lbConfigRaw = "{\"xds_experimental\" : { " + (("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig2 = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig2).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        XdsLbState xdsLbState2 = lb.getXdsLbStateForTest();
        assertThat(xdsLbState2.childPolicy).isNull();
        assertThat(xdsLbState2).isSameAs(xdsLbState1);
        // verify oobChannel is unchanged
        Mockito.verify(helper).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        // verify ADS stream is unchanged
        Mockito.verify(oobChannel1).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
    }

    @Test
    public void resolverEvent_standardModeToCustomMode() throws Exception {
        String lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"unsupported\" : {\"key\" : \"val\"}}, {\"unsupported_2\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        Attributes attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        Mockito.verify(helper).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        Mockito.verify(oobChannel1).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
        lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"supported_1\" : {\"key\" : \"val\"}}, {\"unsupported_2\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig2 = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig2).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        assertThat(lb.getXdsLbStateForTest().childPolicy).isNotNull();
        // verify oobChannel is unchanged
        Mockito.verify(helper).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        // verify ADS stream is reset
        Mockito.verify(oobChannel1, Mockito.times(2)).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
    }

    @Test
    public void resolverEvent_customModeToStandardMode() throws Exception {
        String lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"supported_1\" : {\"key\" : \"val\"}}, {\"unsupported_2\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        Attributes attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        Mockito.verify(helper).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        Mockito.verify(oobChannel1).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
        assertThat(lb.getXdsLbStateForTest().childPolicy).isNotNull();
        lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"unsupported\" : {\"key\" : \"val\"}}, {\"unsupported_2\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig2 = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig2).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        assertThat(lb.getXdsLbStateForTest().childPolicy).isNull();
        // verify oobChannel is unchanged
        Mockito.verify(helper).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        // verify ADS stream is reset
        Mockito.verify(oobChannel1, Mockito.times(2)).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
    }

    @Test
    public void resolverEvent_customModeToCustomMode() throws Exception {
        String lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"supported_1\" : {\"key\" : \"val\"}}, {\"unsupported_2\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        Attributes attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        assertThat(lb.getXdsLbStateForTest().childPolicy).isNotNull();
        Mockito.verify(helper).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        Mockito.verify(oobChannel1).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
        lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"supported_2\" : {\"key\" : \"val\"}}, {\"unsupported_1\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig2 = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig2).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        assertThat(lb.getXdsLbStateForTest().childPolicy).isNotNull();
        // verify oobChannel is unchanged
        Mockito.verify(helper).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        // verify ADS stream is reset
        Mockito.verify(oobChannel1, Mockito.times(2)).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
    }

    @Test
    public void resolverEvent_balancerNameChange() throws Exception {
        String lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"unsupported\" : {\"key\" : \"val\"}}, {\"unsupported_2\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        Attributes attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        Mockito.verify(helper).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        Mockito.verify(oobChannel1).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
        lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8443\"," + "\"childPolicy\" : [{\"supported_1\" : {\"key\" : \"val\"}}, {\"unsupported_2\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig2 = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig2).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        assertThat(lb.getXdsLbStateForTest().childPolicy).isNotNull();
        // verify oobChannel is unchanged
        Mockito.verify(helper, Mockito.times(2)).createOobChannel(Matchers.<EquivalentAddressGroup>any(), ArgumentMatchers.anyString());
        Mockito.verify(oobChannel1).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
        Mockito.verify(oobChannel2).newCall(Matchers.<MethodDescriptor<?, ?>>any(), Matchers.<CallOptions>any());
        Mockito.verifyNoMoreInteractions(oobChannel3);
    }

    @Test
    public void fallback_AdsNotWorkingYetTimerExpired() throws Exception {
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), XdsLoadBalancerTest.standardModeWithFallback1Attributes());
        assertThat(fakeClock.forwardTime(10, TimeUnit.SECONDS)).isEqualTo(1);
        assertThat(fakeClock.getPendingTasks()).isEmpty();
        ArgumentCaptor<Attributes> captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(fakeBalancer1).handleResolvedAddressGroups(Matchers.<List<EquivalentAddressGroup>>any(), captor.capture());
        assertThat(captor.getValue().get(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG)).containsExactly("supported_1_option", "yes");
    }

    @Test
    public void fallback_AdsWorkingTimerCancelled() throws Exception {
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), XdsLoadBalancerTest.standardModeWithFallback1Attributes());
        serverResponseWriter.onNext(DiscoveryResponse.getDefaultInstance());
        assertThat(fakeClock.getPendingTasks()).isEmpty();
        Mockito.verify(fakeBalancer1, Mockito.never()).handleResolvedAddressGroups(Matchers.<List<EquivalentAddressGroup>>any(), Matchers.<Attributes>any());
    }

    @Test
    public void fallback_AdsErrorAndNoActiveSubchannel() throws Exception {
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), XdsLoadBalancerTest.standardModeWithFallback1Attributes());
        serverResponseWriter.onError(new Exception("fake error"));
        ArgumentCaptor<Attributes> captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(fakeBalancer1).handleResolvedAddressGroups(Matchers.<List<EquivalentAddressGroup>>any(), captor.capture());
        assertThat(captor.getValue().get(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG)).containsExactly("supported_1_option", "yes");
        assertThat(fakeClock.forwardTime(10, TimeUnit.SECONDS)).isEqualTo(1);
        assertThat(fakeClock.getPendingTasks()).isEmpty();
        // verify handleResolvedAddressGroups() is not called again
        Mockito.verify(fakeBalancer1).handleResolvedAddressGroups(Matchers.<List<EquivalentAddressGroup>>any(), Matchers.<Attributes>any());
    }

    @Test
    public void fallback_AdsErrorWithActiveSubchannel() throws Exception {
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), XdsLoadBalancerTest.standardModeWithFallback1Attributes());
        serverResponseWriter.onNext(DiscoveryResponse.getDefaultInstance());
        Mockito.doReturn(true).when(fakeSubchannelStore).hasReadyBackends();
        serverResponseWriter.onError(new Exception("fake error"));
        Mockito.verify(fakeBalancer1, Mockito.never()).handleResolvedAddressGroups(Matchers.<List<EquivalentAddressGroup>>any(), Matchers.<Attributes>any());
        Subchannel subchannel = new Subchannel() {
            @Override
            public void shutdown() {
            }

            @Override
            public void requestConnection() {
            }

            @Override
            public Attributes getAttributes() {
                return Attributes.newBuilder().set(XdsLoadBalancer.STATE_INFO, new java.util.concurrent.atomic.AtomicReference(ConnectivityStateInfo.forNonError(READY))).build();
            }
        };
        Mockito.doReturn(true).when(fakeSubchannelStore).hasSubchannel(subchannel);
        Mockito.doReturn(false).when(fakeSubchannelStore).hasReadyBackends();
        lb.handleSubchannelState(subchannel, ConnectivityStateInfo.forTransientFailure(UNAVAILABLE));
        ArgumentCaptor<Attributes> captor = ArgumentCaptor.forClass(Attributes.class);
        Mockito.verify(fakeBalancer1).handleResolvedAddressGroups(Matchers.<List<EquivalentAddressGroup>>any(), captor.capture());
        assertThat(captor.getValue().get(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG)).containsExactly("supported_1_option", "yes");
    }

    @Test
    public void shutdown_cleanupTimers() throws Exception {
        String lbConfigRaw = "{\"xds_experimental\" : { " + ((("\"balancerName\" : \"dns:///balancer.example.com:8080\"," + "\"childPolicy\" : [{\"unsupported\" : {\"key\" : \"val\"}}, {\"unsupported_2\" : {}}],") + "\"fallbackPolicy\" : [{\"unsupported\" : {}}, {\"supported_1\" : {\"key\" : \"val\"}}]") + "}}");
        @SuppressWarnings("unchecked")
        Map<String, ?> lbConfig = ((Map<String, ?>) (JsonParser.parse(lbConfigRaw)));
        Attributes attrs = Attributes.newBuilder().set(LoadBalancer.ATTR_LOAD_BALANCING_CONFIG, lbConfig).build();
        lb.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), attrs);
        assertThat(fakeClock.getPendingTasks()).isNotEmpty();
        lb.shutdown();
        assertThat(fakeClock.getPendingTasks()).isEmpty();
    }
}

