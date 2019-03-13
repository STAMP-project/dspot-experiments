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


import Attributes.EMPTY;
import Attributes.Key;
import Status.NOT_FOUND;
import Status.OK;
import Status.UNAVAILABLE;
import com.google.common.collect.Lists;
import io.grpc.Attributes;
import io.grpc.ConnectivityState;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer.Helper;
import io.grpc.LoadBalancer.PickResult;
import io.grpc.LoadBalancer.PickSubchannelArgs;
import io.grpc.LoadBalancer.Subchannel;
import io.grpc.LoadBalancer.SubchannelPicker;
import io.grpc.Status;
import java.net.SocketAddress;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Unit test for {@link PickFirstLoadBalancer}.
 */
@RunWith(JUnit4.class)
public class PickFirstLoadBalancerTest {
    private PickFirstLoadBalancer loadBalancer;

    private List<EquivalentAddressGroup> servers = Lists.newArrayList();

    private List<SocketAddress> socketAddresses = Lists.newArrayList();

    private static final Attributes.Key<String> FOO = Key.create("foo");

    private Attributes affinity = Attributes.newBuilder().set(io.grpc.internal.FOO, "bar").build();

    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Captor
    private ArgumentCaptor<SubchannelPicker> pickerCaptor;

    @Captor
    private ArgumentCaptor<Attributes> attrsCaptor;

    @Mock
    private Helper mockHelper;

    @Mock
    private Subchannel mockSubchannel;

    // This LoadBalancer doesn't use any of the arg fields, as verified in tearDown().
    @Mock
    private PickSubchannelArgs mockArgs;

    @Test
    public void pickAfterResolved() throws Exception {
        loadBalancer.handleResolvedAddressGroups(servers, affinity);
        Mockito.verify(mockHelper).createSubchannel(ArgumentMatchers.eq(servers), attrsCaptor.capture());
        Mockito.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        Mockito.verify(mockSubchannel).requestConnection();
        Assert.assertEquals(pickerCaptor.getValue().pickSubchannel(mockArgs), pickerCaptor.getValue().pickSubchannel(mockArgs));
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void pickAfterResolvedAndUnchanged() throws Exception {
        loadBalancer.handleResolvedAddressGroups(servers, affinity);
        Mockito.verify(mockSubchannel).requestConnection();
        loadBalancer.handleResolvedAddressGroups(servers, affinity);
        Mockito.verifyNoMoreInteractions(mockSubchannel);
        Mockito.verify(mockHelper).createSubchannel(ArgumentMatchers.anyListOf(EquivalentAddressGroup.class), ArgumentMatchers.any(Attributes.class));
        Mockito.verify(mockHelper).updateBalancingState(ArgumentMatchers.isA(ConnectivityState.class), ArgumentMatchers.isA(SubchannelPicker.class));
        // Updating the subchannel addresses is unnecessary, but doesn't hurt anything
        Mockito.verify(mockHelper).updateSubchannelAddresses(ArgumentMatchers.eq(mockSubchannel), ArgumentMatchers.anyListOf(EquivalentAddressGroup.class));
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void pickAfterResolvedAndChanged() throws Exception {
        SocketAddress socketAddr = new PickFirstLoadBalancerTest.FakeSocketAddress("newserver");
        List<EquivalentAddressGroup> newServers = Lists.newArrayList(new EquivalentAddressGroup(socketAddr));
        InOrder inOrder = Mockito.inOrder(mockHelper);
        loadBalancer.handleResolvedAddressGroups(servers, affinity);
        inOrder.verify(mockHelper).createSubchannel(ArgumentMatchers.eq(servers), ArgumentMatchers.any(Attributes.class));
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        Mockito.verify(mockSubchannel).requestConnection();
        Assert.assertEquals(mockSubchannel, pickerCaptor.getValue().pickSubchannel(mockArgs).getSubchannel());
        loadBalancer.handleResolvedAddressGroups(newServers, affinity);
        inOrder.verify(mockHelper).updateSubchannelAddresses(ArgumentMatchers.eq(mockSubchannel), ArgumentMatchers.eq(newServers));
        Mockito.verifyNoMoreInteractions(mockSubchannel);
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void stateChangeBeforeResolution() throws Exception {
        loadBalancer.handleSubchannelState(mockSubchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void pickAfterStateChangeAfterResolution() throws Exception {
        loadBalancer.handleResolvedAddressGroups(servers, affinity);
        Mockito.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        Subchannel subchannel = pickerCaptor.getValue().pickSubchannel(mockArgs).getSubchannel();
        Mockito.reset(mockHelper);
        InOrder inOrder = Mockito.inOrder(mockHelper);
        Status error = UNAVAILABLE.withDescription("boom!");
        loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forTransientFailure(error));
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.TRANSIENT_FAILURE), pickerCaptor.capture());
        Assert.assertEquals(error, pickerCaptor.getValue().pickSubchannel(mockArgs).getStatus());
        loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.IDLE));
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.IDLE), pickerCaptor.capture());
        Assert.assertEquals(OK, pickerCaptor.getValue().pickSubchannel(mockArgs).getStatus());
        loadBalancer.handleSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        Assert.assertEquals(subchannel, pickerCaptor.getValue().pickSubchannel(mockArgs).getSubchannel());
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void nameResolutionError() throws Exception {
        Status error = NOT_FOUND.withDescription("nameResolutionError");
        loadBalancer.handleNameResolutionError(error);
        Mockito.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.TRANSIENT_FAILURE), pickerCaptor.capture());
        PickResult pickResult = pickerCaptor.getValue().pickSubchannel(mockArgs);
        Assert.assertEquals(null, pickResult.getSubchannel());
        Assert.assertEquals(error, pickResult.getStatus());
        Mockito.verify(mockSubchannel, Mockito.never()).requestConnection();
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void nameResolutionSuccessAfterError() throws Exception {
        InOrder inOrder = Mockito.inOrder(mockHelper);
        loadBalancer.handleNameResolutionError(NOT_FOUND.withDescription("nameResolutionError"));
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.any(ConnectivityState.class), ArgumentMatchers.any(SubchannelPicker.class));
        Mockito.verify(mockSubchannel, Mockito.never()).requestConnection();
        loadBalancer.handleResolvedAddressGroups(servers, affinity);
        inOrder.verify(mockHelper).createSubchannel(ArgumentMatchers.eq(servers), ArgumentMatchers.eq(EMPTY));
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        Mockito.verify(mockSubchannel).requestConnection();
        Assert.assertEquals(mockSubchannel, pickerCaptor.getValue().pickSubchannel(mockArgs).getSubchannel());
        Assert.assertEquals(pickerCaptor.getValue().pickSubchannel(mockArgs), pickerCaptor.getValue().pickSubchannel(mockArgs));
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void nameResolutionErrorWithStateChanges() throws Exception {
        InOrder inOrder = Mockito.inOrder(mockHelper);
        loadBalancer.handleSubchannelState(mockSubchannel, ConnectivityStateInfo.forTransientFailure(UNAVAILABLE));
        Status error = NOT_FOUND.withDescription("nameResolutionError");
        loadBalancer.handleNameResolutionError(error);
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.TRANSIENT_FAILURE), pickerCaptor.capture());
        PickResult pickResult = pickerCaptor.getValue().pickSubchannel(mockArgs);
        Assert.assertEquals(null, pickResult.getSubchannel());
        Assert.assertEquals(error, pickResult.getStatus());
        loadBalancer.handleSubchannelState(mockSubchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        Status error2 = NOT_FOUND.withDescription("nameResolutionError2");
        loadBalancer.handleNameResolutionError(error2);
        inOrder.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.TRANSIENT_FAILURE), pickerCaptor.capture());
        pickResult = pickerCaptor.getValue().pickSubchannel(mockArgs);
        Assert.assertEquals(null, pickResult.getSubchannel());
        Assert.assertEquals(error2, pickResult.getStatus());
        Mockito.verifyNoMoreInteractions(mockHelper);
    }

    @Test
    public void requestConnection() {
        loadBalancer.handleResolvedAddressGroups(servers, affinity);
        loadBalancer.handleSubchannelState(mockSubchannel, ConnectivityStateInfo.forNonError(ConnectivityState.IDLE));
        Mockito.verify(mockHelper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.IDLE), pickerCaptor.capture());
        SubchannelPicker picker = pickerCaptor.getValue();
        Mockito.verify(mockSubchannel).requestConnection();
        picker.requestConnection();
        Mockito.verify(mockSubchannel, Mockito.times(2)).requestConnection();
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

