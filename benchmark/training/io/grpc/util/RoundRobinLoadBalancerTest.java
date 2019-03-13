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
package io.grpc.util;


import Attributes.EMPTY;
import Attributes.Key;
import ConnectivityState.SHUTDOWN;
import GrpcAttributes.NAME_RESOLVER_SERVICE_CONFIG;
import Metadata.ASCII_STRING_MARSHALLER;
import Status.NOT_FOUND;
import Status.OK;
import Status.UNAVAILABLE;
import Status.UNKNOWN;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.grpc.Attributes;
import io.grpc.ConnectivityState;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.LoadBalancer.Helper;
import io.grpc.LoadBalancer.PickResult;
import io.grpc.LoadBalancer.PickSubchannelArgs;
import io.grpc.LoadBalancer.Subchannel;
import io.grpc.LoadBalancer.SubchannelPicker;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.util.RoundRobinLoadBalancer.EmptyPicker;
import io.grpc.util.RoundRobinLoadBalancer.ReadyPicker;
import io.grpc.util.RoundRobinLoadBalancer.Ref;
import io.grpc.util.RoundRobinLoadBalancer.StickinessState;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Unit test for {@link RoundRobinLoadBalancer}.
 */
@RunWith(JUnit4.class)
public class RoundRobinLoadBalancerTest {
    private static final Attributes.Key<String> MAJOR_KEY = Key.create("major-key");

    private RoundRobinLoadBalancer loadBalancer;

    private final List<EquivalentAddressGroup> servers = Lists.newArrayList();

    private final Map<List<EquivalentAddressGroup>, Subchannel> subchannels = Maps.newLinkedHashMap();

    private final Attributes affinity = Attributes.newBuilder().set(io.grpc.util.MAJOR_KEY, "I got the keys").build();

    @Captor
    private ArgumentCaptor<SubchannelPicker> pickerCaptor;

    @Captor
    private ArgumentCaptor<ConnectivityState> stateCaptor;

    @Captor
    private ArgumentCaptor<List<EquivalentAddressGroup>> eagListCaptor;

    @Mock
    private Helper mockHelper;

    // This LoadBalancer doesn't use any of the arg fields, as verified in tearDown().
    @Mock
    private PickSubchannelArgs mockArgs;

    @Test
    public void pickAfterResolved() throws Exception {
        final Subchannel readySubchannel = subchannels.values().iterator().next();
        loadBalancer.handleResolvedAddressGroups(servers, affinity);
        loadBalancer.handleSubchannelState(readySubchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        Mockito.verify(mockHelper, Mockito.times(3)).createSubchannel(eagListCaptor.capture(), ArgumentMatchers.any(Attributes.class));
        assertThat(eagListCaptor.getAllValues()).containsAllIn(subchannels.keySet());
        for (Subchannel subchannel : subchannels.values()) {
            Mockito.verify(subchannel).requestConnection();
            Mockito.verify(subchannel, Mockito.never()).shutdown();
        }
        Mockito.verify(mockHelper, Mockito.times(2)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        Assert.assertEquals(ConnectivityState.CONNECTING, stateCaptor.getAllValues().get(0));
        Assert.assertEquals(ConnectivityState.READY, stateCaptor.getAllValues().get(1));
        assertThat(RoundRobinLoadBalancerTest.getList(pickerCaptor.getValue())).containsExactly(readySubchannel);
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void pickAfterResolvedUpdatedHosts() throws Exception {
        Subchannel removedSubchannel = Mockito.mock(Subchannel.class);
        Subchannel oldSubchannel = Mockito.mock(Subchannel.class);
        Subchannel newSubchannel = Mockito.mock(Subchannel.class);
        RoundRobinLoadBalancerTest.FakeSocketAddress removedAddr = new RoundRobinLoadBalancerTest.FakeSocketAddress("removed");
        RoundRobinLoadBalancerTest.FakeSocketAddress oldAddr = new RoundRobinLoadBalancerTest.FakeSocketAddress("old");
        RoundRobinLoadBalancerTest.FakeSocketAddress newAddr = new RoundRobinLoadBalancerTest.FakeSocketAddress("new");
        List<Subchannel> allSubchannels = Lists.newArrayList(removedSubchannel, oldSubchannel, newSubchannel);
        List<RoundRobinLoadBalancerTest.FakeSocketAddress> allAddrs = Lists.newArrayList(removedAddr, oldAddr, newAddr);
        for (int i = 0; i < (allSubchannels.size()); i++) {
            Subchannel subchannel = allSubchannels.get(i);
            List<EquivalentAddressGroup> eagList = Arrays.asList(new EquivalentAddressGroup(allAddrs.get(i)));
            Mockito.when(subchannel.getAttributes()).thenReturn(Attributes.newBuilder().set(RoundRobinLoadBalancer.STATE_INFO, new Ref(ConnectivityStateInfo.forNonError(ConnectivityState.READY))).build());
            Mockito.when(subchannel.getAllAddresses()).thenReturn(eagList);
        }
        final Map<List<EquivalentAddressGroup>, Subchannel> subchannels2 = Maps.newHashMap();
        subchannels2.put(Arrays.asList(new EquivalentAddressGroup(removedAddr)), removedSubchannel);
        subchannels2.put(Arrays.asList(new EquivalentAddressGroup(oldAddr)), oldSubchannel);
        List<EquivalentAddressGroup> currentServers = Lists.newArrayList(new EquivalentAddressGroup(removedAddr), new EquivalentAddressGroup(oldAddr));
        Mockito.doAnswer(new Answer<Subchannel>() {
            @Override
            public Subchannel answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return subchannels2.get(args[0]);
            }
        }).when(mockHelper).createSubchannel(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Attributes.class));
        loadBalancer.handleResolvedAddressGroups(currentServers, affinity);
        InOrder inOrder = Mockito.inOrder(mockHelper);
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        SubchannelPicker picker = pickerCaptor.getValue();
        assertThat(RoundRobinLoadBalancerTest.getList(picker)).containsExactly(removedSubchannel, oldSubchannel);
        Mockito.verify(removedSubchannel, Mockito.times(1)).requestConnection();
        Mockito.verify(oldSubchannel, Mockito.times(1)).requestConnection();
        assertThat(loadBalancer.getSubchannels()).containsExactly(removedSubchannel, oldSubchannel);
        subchannels2.clear();
        subchannels2.put(Arrays.asList(new EquivalentAddressGroup(oldAddr)), oldSubchannel);
        subchannels2.put(Arrays.asList(new EquivalentAddressGroup(newAddr)), newSubchannel);
        List<EquivalentAddressGroup> latestServers = Lists.newArrayList(new EquivalentAddressGroup(oldAddr), new EquivalentAddressGroup(newAddr));
        loadBalancer.handleResolvedAddressGroups(latestServers, affinity);
        Mockito.verify(newSubchannel, Mockito.times(1)).requestConnection();
        Mockito.verify(removedSubchannel, Mockito.times(1)).shutdown();
        loadBalancer.handleSubchannelState(removedSubchannel, ConnectivityStateInfo.forNonError(ConnectivityState.SHUTDOWN));
        assertThat(loadBalancer.getSubchannels()).containsExactly(oldSubchannel, newSubchannel);
        Mockito.verify(mockHelper, Mockito.times(3)).createSubchannel(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Attributes.class));
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        picker = pickerCaptor.getValue();
        assertThat(RoundRobinLoadBalancerTest.getList(picker)).containsExactly(oldSubchannel, newSubchannel);
        // test going from non-empty to empty
        loadBalancer.handleResolvedAddressGroups(Collections.<EquivalentAddressGroup>emptyList(), affinity);
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.TRANSIENT_FAILURE), pickerCaptor.capture());
        Assert.assertEquals(PickResult.withNoResult(), pickerCaptor.getValue().pickSubchannel(mockArgs));
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void pickAfterStateChange() throws Exception {
        InOrder inOrder = Mockito.inOrder(mockHelper);
        loadBalancer.handleResolvedAddressGroups(servers, EMPTY);
        Subchannel subchannel = loadBalancer.getSubchannels().iterator().next();
        Ref<ConnectivityStateInfo> subchannelStateInfo = subchannel.getAttributes().get(RoundRobinLoadBalancer.STATE_INFO);
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), ArgumentMatchers.isA(EmptyPicker.class));
        assertThat(subchannelStateInfo.value).isEqualTo(ConnectivityStateInfo.forNonError(ConnectivityState.IDLE));
        loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        assertThat(pickerCaptor.getValue()).isInstanceOf(ReadyPicker.class);
        assertThat(subchannelStateInfo.value).isEqualTo(ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        Status error = UNKNOWN.withDescription("\u00af\\_(\u30c4)_//\u00af");
        loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forTransientFailure(error));
        assertThat(subchannelStateInfo.value).isEqualTo(ConnectivityStateInfo.forTransientFailure(error));
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        assertThat(pickerCaptor.getValue()).isInstanceOf(EmptyPicker.class);
        loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.IDLE));
        assertThat(subchannelStateInfo.value).isEqualTo(ConnectivityStateInfo.forNonError(ConnectivityState.IDLE));
        Mockito.verify(subchannel, Mockito.times(2)).requestConnection();
        Mockito.verify(mockHelper, Mockito.times(3)).createSubchannel(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Attributes.class));
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void pickerRoundRobin() throws Exception {
        Subchannel subchannel = Mockito.mock(Subchannel.class);
        Subchannel subchannel1 = Mockito.mock(Subchannel.class);
        Subchannel subchannel2 = Mockito.mock(Subchannel.class);
        ReadyPicker picker = /* startIndex */
        /* stickinessState */
        new ReadyPicker(Collections.unmodifiableList(Lists.newArrayList(subchannel, subchannel1, subchannel2)), 0, null);
        assertThat(picker.getList()).containsExactly(subchannel, subchannel1, subchannel2);
        Assert.assertEquals(subchannel, picker.pickSubchannel(mockArgs).getSubchannel());
        Assert.assertEquals(subchannel1, picker.pickSubchannel(mockArgs).getSubchannel());
        Assert.assertEquals(subchannel2, picker.pickSubchannel(mockArgs).getSubchannel());
        Assert.assertEquals(subchannel, picker.pickSubchannel(mockArgs).getSubchannel());
    }

    @Test
    public void pickerEmptyList() throws Exception {
        SubchannelPicker picker = new EmptyPicker(Status.UNKNOWN);
        Assert.assertEquals(null, picker.pickSubchannel(mockArgs).getSubchannel());
        Assert.assertEquals(UNKNOWN, picker.pickSubchannel(mockArgs).getStatus());
    }

    @Test
    public void nameResolutionErrorWithNoChannels() throws Exception {
        Status error = NOT_FOUND.withDescription("nameResolutionError");
        loadBalancer.handleNameResolutionError(error);
        Mockito.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.TRANSIENT_FAILURE), pickerCaptor.capture());
        LoadBalancer.PickResult pickResult = pickerCaptor.getValue().pickSubchannel(mockArgs);
        Assert.assertNull(pickResult.getSubchannel());
        Assert.assertEquals(error, pickResult.getStatus());
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void nameResolutionErrorWithActiveChannels() throws Exception {
        final Subchannel readySubchannel = subchannels.values().iterator().next();
        loadBalancer.handleResolvedAddressGroups(servers, affinity);
        loadBalancer.handleSubchannelState(readySubchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        loadBalancer.handleNameResolutionError(NOT_FOUND.withDescription("nameResolutionError"));
        Mockito.verify(mockHelper, Mockito.times(3)).createSubchannel(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Attributes.class));
        Mockito.verify(mockHelper, Mockito.times(3)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        Iterator<ConnectivityState> stateIterator = stateCaptor.getAllValues().iterator();
        Assert.assertEquals(ConnectivityState.CONNECTING, stateIterator.next());
        Assert.assertEquals(ConnectivityState.READY, stateIterator.next());
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, stateIterator.next());
        LoadBalancer.PickResult pickResult = pickerCaptor.getValue().pickSubchannel(mockArgs);
        Assert.assertEquals(readySubchannel, pickResult.getSubchannel());
        Assert.assertEquals(OK.getCode(), pickResult.getStatus().getCode());
        LoadBalancer.PickResult pickResult2 = pickerCaptor.getValue().pickSubchannel(mockArgs);
        Assert.assertEquals(readySubchannel, pickResult2.getSubchannel());
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void subchannelStateIsolation() throws Exception {
        Iterator<Subchannel> subchannelIterator = subchannels.values().iterator();
        Subchannel sc1 = subchannelIterator.next();
        Subchannel sc2 = subchannelIterator.next();
        Subchannel sc3 = subchannelIterator.next();
        loadBalancer.handleResolvedAddressGroups(servers, EMPTY);
        Mockito.verify(sc1, Mockito.times(1)).requestConnection();
        Mockito.verify(sc2, Mockito.times(1)).requestConnection();
        Mockito.verify(sc3, Mockito.times(1)).requestConnection();
        loadBalancer.handleSubchannelState(sc1, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        loadBalancer.handleSubchannelState(sc2, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        loadBalancer.handleSubchannelState(sc3, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        loadBalancer.handleSubchannelState(sc2, ConnectivityStateInfo.forNonError(ConnectivityState.IDLE));
        loadBalancer.handleSubchannelState(sc3, ConnectivityStateInfo.forTransientFailure(UNAVAILABLE));
        Mockito.verify(mockHelper, Mockito.times(6)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        Iterator<ConnectivityState> stateIterator = stateCaptor.getAllValues().iterator();
        Iterator<SubchannelPicker> pickers = pickerCaptor.getAllValues().iterator();
        // The picker is incrementally updated as subchannels become READY
        Assert.assertEquals(ConnectivityState.CONNECTING, stateIterator.next());
        assertThat(pickers.next()).isInstanceOf(EmptyPicker.class);
        Assert.assertEquals(ConnectivityState.READY, stateIterator.next());
        assertThat(RoundRobinLoadBalancerTest.getList(pickers.next())).containsExactly(sc1);
        Assert.assertEquals(ConnectivityState.READY, stateIterator.next());
        assertThat(RoundRobinLoadBalancerTest.getList(pickers.next())).containsExactly(sc1, sc2);
        Assert.assertEquals(ConnectivityState.READY, stateIterator.next());
        assertThat(RoundRobinLoadBalancerTest.getList(pickers.next())).containsExactly(sc1, sc2, sc3);
        // The IDLE subchannel is dropped from the picker, but a reconnection is requested
        Assert.assertEquals(ConnectivityState.READY, stateIterator.next());
        assertThat(RoundRobinLoadBalancerTest.getList(pickers.next())).containsExactly(sc1, sc3);
        Mockito.verify(sc2, Mockito.times(2)).requestConnection();
        // The failing subchannel is dropped from the picker, with no requested reconnect
        Assert.assertEquals(ConnectivityState.READY, stateIterator.next());
        assertThat(RoundRobinLoadBalancerTest.getList(pickers.next())).containsExactly(sc1);
        Mockito.verify(sc3, Mockito.times(1)).requestConnection();
        assertThat(stateIterator.hasNext()).isFalse();
        assertThat(pickers.hasNext()).isFalse();
    }

    @Test
    public void noStickinessEnabled_withStickyHeader() {
        loadBalancer.handleResolvedAddressGroups(servers, EMPTY);
        for (Subchannel subchannel : subchannels.values()) {
            loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        }
        Mockito.verify(mockHelper, Mockito.times(4)).updateBalancingState(ArgumentMatchers.any(ConnectivityState.class), pickerCaptor.capture());
        SubchannelPicker picker = pickerCaptor.getValue();
        io.grpc.Metadata.Key<String> stickinessKey = io.grpc.Metadata.Key.of("my-sticky-key", ASCII_STRING_MARSHALLER);
        Metadata headerWithStickinessValue = new Metadata();
        headerWithStickinessValue.put(stickinessKey, "my-sticky-value");
        Mockito.doReturn(headerWithStickinessValue).when(mockArgs).getHeaders();
        List<Subchannel> allSubchannels = RoundRobinLoadBalancerTest.getList(picker);
        Subchannel sc1 = picker.pickSubchannel(mockArgs).getSubchannel();
        Subchannel sc2 = picker.pickSubchannel(mockArgs).getSubchannel();
        Subchannel sc3 = picker.pickSubchannel(mockArgs).getSubchannel();
        Subchannel sc4 = picker.pickSubchannel(mockArgs).getSubchannel();
        Assert.assertEquals(nextSubchannel(sc1, allSubchannels), sc2);
        Assert.assertEquals(nextSubchannel(sc2, allSubchannels), sc3);
        Assert.assertEquals(nextSubchannel(sc3, allSubchannels), sc1);
        Assert.assertEquals(sc4, sc1);
        Assert.assertNull(loadBalancer.getStickinessMapForTest());
    }

    @Test
    public void stickinessEnabled_withoutStickyHeader() {
        Map<String, Object> serviceConfig = new HashMap<>();
        serviceConfig.put("stickinessMetadataKey", "my-sticky-key");
        Attributes attributes = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        loadBalancer.handleResolvedAddressGroups(servers, attributes);
        for (Subchannel subchannel : subchannels.values()) {
            loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        }
        Mockito.verify(mockHelper, Mockito.times(4)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        SubchannelPicker picker = pickerCaptor.getValue();
        Mockito.doReturn(new Metadata()).when(mockArgs).getHeaders();
        List<Subchannel> allSubchannels = RoundRobinLoadBalancerTest.getList(picker);
        Subchannel sc1 = picker.pickSubchannel(mockArgs).getSubchannel();
        Subchannel sc2 = picker.pickSubchannel(mockArgs).getSubchannel();
        Subchannel sc3 = picker.pickSubchannel(mockArgs).getSubchannel();
        Subchannel sc4 = picker.pickSubchannel(mockArgs).getSubchannel();
        Assert.assertEquals(nextSubchannel(sc1, allSubchannels), sc2);
        Assert.assertEquals(nextSubchannel(sc2, allSubchannels), sc3);
        Assert.assertEquals(nextSubchannel(sc3, allSubchannels), sc1);
        Assert.assertEquals(sc4, sc1);
        Mockito.verify(mockArgs, Mockito.times(4)).getHeaders();
        Assert.assertNotNull(loadBalancer.getStickinessMapForTest());
        assertThat(loadBalancer.getStickinessMapForTest()).isEmpty();
    }

    @Test
    public void stickinessEnabled_withStickyHeader() {
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("stickinessMetadataKey", "my-sticky-key");
        Attributes attributes = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        loadBalancer.handleResolvedAddressGroups(servers, attributes);
        for (Subchannel subchannel : subchannels.values()) {
            loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        }
        Mockito.verify(mockHelper, Mockito.times(4)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        SubchannelPicker picker = pickerCaptor.getValue();
        io.grpc.Metadata.Key<String> stickinessKey = io.grpc.Metadata.Key.of("my-sticky-key", ASCII_STRING_MARSHALLER);
        Metadata headerWithStickinessValue = new Metadata();
        headerWithStickinessValue.put(stickinessKey, "my-sticky-value");
        Mockito.doReturn(headerWithStickinessValue).when(mockArgs).getHeaders();
        Subchannel sc1 = picker.pickSubchannel(mockArgs).getSubchannel();
        Assert.assertEquals(sc1, picker.pickSubchannel(mockArgs).getSubchannel());
        Assert.assertEquals(sc1, picker.pickSubchannel(mockArgs).getSubchannel());
        Assert.assertEquals(sc1, picker.pickSubchannel(mockArgs).getSubchannel());
        Assert.assertEquals(sc1, picker.pickSubchannel(mockArgs).getSubchannel());
        Mockito.verify(mockArgs, Mockito.atLeast(4)).getHeaders();
        Assert.assertNotNull(loadBalancer.getStickinessMapForTest());
        assertThat(loadBalancer.getStickinessMapForTest()).hasSize(1);
    }

    @Test
    public void stickinessEnabled_withDifferentStickyHeaders() {
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("stickinessMetadataKey", "my-sticky-key");
        Attributes attributes = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        loadBalancer.handleResolvedAddressGroups(servers, attributes);
        for (Subchannel subchannel : subchannels.values()) {
            loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        }
        Mockito.verify(mockHelper, Mockito.times(4)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        SubchannelPicker picker = pickerCaptor.getValue();
        io.grpc.Metadata.Key<String> stickinessKey = io.grpc.Metadata.Key.of("my-sticky-key", ASCII_STRING_MARSHALLER);
        Metadata headerWithStickinessValue1 = new Metadata();
        headerWithStickinessValue1.put(stickinessKey, "my-sticky-value");
        Metadata headerWithStickinessValue2 = new Metadata();
        headerWithStickinessValue2.put(stickinessKey, "my-sticky-value2");
        List<Subchannel> allSubchannels = RoundRobinLoadBalancerTest.getList(picker);
        Mockito.doReturn(headerWithStickinessValue1).when(mockArgs).getHeaders();
        Subchannel sc1a = picker.pickSubchannel(mockArgs).getSubchannel();
        Mockito.doReturn(headerWithStickinessValue2).when(mockArgs).getHeaders();
        Subchannel sc2a = picker.pickSubchannel(mockArgs).getSubchannel();
        Mockito.doReturn(headerWithStickinessValue1).when(mockArgs).getHeaders();
        Subchannel sc1b = picker.pickSubchannel(mockArgs).getSubchannel();
        Mockito.doReturn(headerWithStickinessValue2).when(mockArgs).getHeaders();
        Subchannel sc2b = picker.pickSubchannel(mockArgs).getSubchannel();
        Assert.assertEquals(sc1a, sc1b);
        Assert.assertEquals(sc2a, sc2b);
        Assert.assertEquals(nextSubchannel(sc1a, allSubchannels), sc2a);
        Assert.assertEquals(nextSubchannel(sc1b, allSubchannels), sc2b);
        Mockito.verify(mockArgs, Mockito.atLeast(4)).getHeaders();
        Assert.assertNotNull(loadBalancer.getStickinessMapForTest());
        assertThat(loadBalancer.getStickinessMapForTest()).hasSize(2);
    }

    @Test
    public void stickiness_goToTransientFailure_pick_backToReady() {
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("stickinessMetadataKey", "my-sticky-key");
        Attributes attributes = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        loadBalancer.handleResolvedAddressGroups(servers, attributes);
        for (Subchannel subchannel : subchannels.values()) {
            loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        }
        Mockito.verify(mockHelper, Mockito.times(4)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        SubchannelPicker picker = pickerCaptor.getValue();
        io.grpc.Metadata.Key<String> stickinessKey = io.grpc.Metadata.Key.of("my-sticky-key", ASCII_STRING_MARSHALLER);
        Metadata headerWithStickinessValue = new Metadata();
        headerWithStickinessValue.put(stickinessKey, "my-sticky-value");
        Mockito.doReturn(headerWithStickinessValue).when(mockArgs).getHeaders();
        // first pick
        Subchannel sc1 = picker.pickSubchannel(mockArgs).getSubchannel();
        // go to transient failure
        loadBalancer.handleSubchannelState(sc1, ConnectivityStateInfo.forTransientFailure(UNAVAILABLE));
        Mockito.verify(mockHelper, Mockito.times(5)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        picker = pickerCaptor.getValue();
        // second pick
        Subchannel sc2 = picker.pickSubchannel(mockArgs).getSubchannel();
        // go back to ready
        loadBalancer.handleSubchannelState(sc1, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        Mockito.verify(mockHelper, Mockito.times(6)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        picker = pickerCaptor.getValue();
        // third pick
        Subchannel sc3 = picker.pickSubchannel(mockArgs).getSubchannel();
        Assert.assertEquals(sc2, sc3);
        Mockito.verify(mockArgs, Mockito.atLeast(3)).getHeaders();
        Assert.assertNotNull(loadBalancer.getStickinessMapForTest());
        assertThat(loadBalancer.getStickinessMapForTest()).hasSize(1);
    }

    @Test
    public void stickiness_goToTransientFailure_backToReady_pick() {
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("stickinessMetadataKey", "my-sticky-key");
        Attributes attributes = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        loadBalancer.handleResolvedAddressGroups(servers, attributes);
        for (Subchannel subchannel : subchannels.values()) {
            loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        }
        Mockito.verify(mockHelper, Mockito.times(4)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        SubchannelPicker picker = pickerCaptor.getValue();
        io.grpc.Metadata.Key<String> stickinessKey = io.grpc.Metadata.Key.of("my-sticky-key", ASCII_STRING_MARSHALLER);
        Metadata headerWithStickinessValue1 = new Metadata();
        headerWithStickinessValue1.put(stickinessKey, "my-sticky-value");
        Mockito.doReturn(headerWithStickinessValue1).when(mockArgs).getHeaders();
        // first pick
        Subchannel sc1 = picker.pickSubchannel(mockArgs).getSubchannel();
        // go to transient failure
        loadBalancer.handleSubchannelState(sc1, ConnectivityStateInfo.forTransientFailure(UNAVAILABLE));
        Metadata headerWithStickinessValue2 = new Metadata();
        headerWithStickinessValue2.put(stickinessKey, "my-sticky-value2");
        Mockito.doReturn(headerWithStickinessValue2).when(mockArgs).getHeaders();
        Mockito.verify(mockHelper, Mockito.times(5)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        picker = pickerCaptor.getValue();
        // second pick with a different stickiness value
        @SuppressWarnings("unused")
        Subchannel sc2 = picker.pickSubchannel(mockArgs).getSubchannel();
        // go back to ready
        loadBalancer.handleSubchannelState(sc1, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        Mockito.doReturn(headerWithStickinessValue1).when(mockArgs).getHeaders();
        Mockito.verify(mockHelper, Mockito.times(6)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        picker = pickerCaptor.getValue();
        // third pick with my-sticky-value1
        Subchannel sc3 = picker.pickSubchannel(mockArgs).getSubchannel();
        Assert.assertEquals(sc1, sc3);
        Mockito.verify(mockArgs, Mockito.atLeast(3)).getHeaders();
        Assert.assertNotNull(loadBalancer.getStickinessMapForTest());
        assertThat(loadBalancer.getStickinessMapForTest()).hasSize(2);
    }

    @Test
    public void stickiness_oneSubchannelShutdown() {
        Map<String, String> serviceConfig = new HashMap<>();
        serviceConfig.put("stickinessMetadataKey", "my-sticky-key");
        Attributes attributes = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        loadBalancer.handleResolvedAddressGroups(servers, attributes);
        for (Subchannel subchannel : subchannels.values()) {
            loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        }
        Mockito.verify(mockHelper, Mockito.times(4)).updateBalancingState(stateCaptor.capture(), pickerCaptor.capture());
        SubchannelPicker picker = pickerCaptor.getValue();
        io.grpc.Metadata.Key<String> stickinessKey = io.grpc.Metadata.Key.of("my-sticky-key", ASCII_STRING_MARSHALLER);
        Metadata headerWithStickinessValue = new Metadata();
        headerWithStickinessValue.put(stickinessKey, "my-sticky-value");
        Mockito.doReturn(headerWithStickinessValue).when(mockArgs).getHeaders();
        List<Subchannel> allSubchannels = Lists.newArrayList(RoundRobinLoadBalancerTest.getList(picker));
        Subchannel sc1 = picker.pickSubchannel(mockArgs).getSubchannel();
        // shutdown channel directly
        loadBalancer.handleSubchannelState(sc1, ConnectivityStateInfo.forNonError(SHUTDOWN));
        Assert.assertNull(loadBalancer.getStickinessMapForTest().get("my-sticky-value").value);
        Assert.assertEquals(nextSubchannel(sc1, allSubchannels), picker.pickSubchannel(mockArgs).getSubchannel());
        assertThat(loadBalancer.getStickinessMapForTest()).hasSize(1);
        Mockito.verify(mockArgs, Mockito.atLeast(2)).getHeaders();
        Subchannel sc2 = picker.pickSubchannel(mockArgs).getSubchannel();
        Assert.assertEquals(sc2, loadBalancer.getStickinessMapForTest().get("my-sticky-value").value);
        // shutdown channel via name resolver change
        List<EquivalentAddressGroup> newServers = new java.util.ArrayList(servers);
        newServers.remove(sc2.getAddresses());
        loadBalancer.handleResolvedAddressGroups(newServers, attributes);
        Mockito.verify(sc2, Mockito.times(1)).shutdown();
        loadBalancer.handleSubchannelState(sc2, ConnectivityStateInfo.forNonError(ConnectivityState.SHUTDOWN));
        Assert.assertNull(loadBalancer.getStickinessMapForTest().get("my-sticky-value").value);
        Assert.assertEquals(nextSubchannel(sc2, allSubchannels), picker.pickSubchannel(mockArgs).getSubchannel());
        assertThat(loadBalancer.getStickinessMapForTest()).hasSize(1);
        Mockito.verify(mockArgs, Mockito.atLeast(2)).getHeaders();
    }

    @Test
    public void stickiness_resolveTwice_metadataKeyChanged() {
        Map<String, String> serviceConfig1 = new HashMap<>();
        serviceConfig1.put("stickinessMetadataKey", "my-sticky-key1");
        Attributes attributes1 = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig1).build();
        loadBalancer.handleResolvedAddressGroups(servers, attributes1);
        Map<String, ?> stickinessMap1 = loadBalancer.getStickinessMapForTest();
        Map<String, String> serviceConfig2 = new HashMap<>();
        serviceConfig2.put("stickinessMetadataKey", "my-sticky-key2");
        Attributes attributes2 = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig2).build();
        loadBalancer.handleResolvedAddressGroups(servers, attributes2);
        Map<String, ?> stickinessMap2 = loadBalancer.getStickinessMapForTest();
        Assert.assertNotSame(stickinessMap1, stickinessMap2);
    }

    @Test
    public void stickiness_resolveTwice_metadataKeyUnChanged() {
        Map<String, String> serviceConfig1 = new HashMap<>();
        serviceConfig1.put("stickinessMetadataKey", "my-sticky-key1");
        Attributes attributes1 = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig1).build();
        loadBalancer.handleResolvedAddressGroups(servers, attributes1);
        Map<String, ?> stickinessMap1 = loadBalancer.getStickinessMapForTest();
        loadBalancer.handleResolvedAddressGroups(servers, attributes1);
        Map<String, ?> stickinessMap2 = loadBalancer.getStickinessMapForTest();
        Assert.assertSame(stickinessMap1, stickinessMap2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void readyPicker_emptyList() {
        // ready picker list must be non-empty
        new ReadyPicker(Collections.<Subchannel>emptyList(), 0, null);
    }

    @Test
    public void internalPickerComparisons() {
        EmptyPicker emptyOk1 = new EmptyPicker(Status.OK);
        EmptyPicker emptyOk2 = new EmptyPicker(OK.withDescription("different OK"));
        EmptyPicker emptyErr = new EmptyPicker(UNKNOWN.withDescription("\u00af\\_(\u30c4)_//\u00af"));
        Iterator<Subchannel> subchannelIterator = subchannels.values().iterator();
        Subchannel sc1 = subchannelIterator.next();
        Subchannel sc2 = subchannelIterator.next();
        StickinessState stickinessState = new StickinessState("stick-key");
        ReadyPicker ready1 = new ReadyPicker(Arrays.asList(sc1, sc2), 0, null);
        ReadyPicker ready2 = new ReadyPicker(Arrays.asList(sc1), 0, null);
        ReadyPicker ready3 = new ReadyPicker(Arrays.asList(sc2, sc1), 1, null);
        ReadyPicker ready4 = new ReadyPicker(Arrays.asList(sc1, sc2), 1, stickinessState);
        ReadyPicker ready5 = new ReadyPicker(Arrays.asList(sc2, sc1), 0, stickinessState);
        Assert.assertTrue(emptyOk1.isEquivalentTo(emptyOk2));
        Assert.assertFalse(emptyOk1.isEquivalentTo(emptyErr));
        Assert.assertFalse(ready1.isEquivalentTo(ready2));
        Assert.assertTrue(ready1.isEquivalentTo(ready3));
        Assert.assertFalse(ready3.isEquivalentTo(ready4));
        Assert.assertTrue(ready4.isEquivalentTo(ready5));
        Assert.assertFalse(emptyOk1.isEquivalentTo(ready1));
        Assert.assertFalse(ready1.isEquivalentTo(emptyOk1));
    }

    private static class FakeSocketAddress extends SocketAddress {
        final String name;

        FakeSocketAddress(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "FakeSocketAddress-" + (name);
        }
    }
}

