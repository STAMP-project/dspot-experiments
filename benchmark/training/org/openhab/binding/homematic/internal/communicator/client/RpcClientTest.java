/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.homematic.internal.communicator.client;


import HmParamsetType.MASTER;
import HmParamsetType.VALUES;
import java.io.IOException;
import org.eclipse.smarthome.test.java.JavaTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openhab.binding.homematic.internal.communicator.message.RpcRequest;
import org.openhab.binding.homematic.internal.communicator.message.XmlRpcRequest;
import org.openhab.binding.homematic.internal.model.HmChannel;
import org.openhab.binding.homematic.test.util.DimmerHelper;
import org.openhab.binding.homematic.test.util.RpcClientMockImpl;


public class RpcClientTest extends JavaTest {
    private RpcClientMockImpl rpcClient;

    @Test
    public void valuesParamsetDescriptionIsLoadedForChannel() throws IOException {
        HmChannel channel = DimmerHelper.createDimmerHmChannel();
        rpcClient.addChannelDatapoints(channel, VALUES);
        MatcherAssert.assertThat(rpcClient.numberOfCalls.get(RpcClientMockImpl.GET_PARAMSET_DESCRIPTION_NAME), CoreMatchers.is(1));
    }

    @Test
    public void masterParamsetDescriptionIsLoadedForDummyChannel() throws IOException {
        HmChannel channel = DimmerHelper.createDimmerDummyChannel();
        rpcClient.addChannelDatapoints(channel, MASTER);
        MatcherAssert.assertThat(rpcClient.numberOfCalls.get(RpcClientMockImpl.GET_PARAMSET_DESCRIPTION_NAME), CoreMatchers.is(1));
    }

    @Test
    public void valuesParamsetDescriptionIsNotLoadedForDummyChannel() throws IOException {
        HmChannel channel = DimmerHelper.createDimmerDummyChannel();
        rpcClient.addChannelDatapoints(channel, VALUES);
        MatcherAssert.assertThat(rpcClient.numberOfCalls.get(RpcClientMockImpl.GET_PARAMSET_DESCRIPTION_NAME), CoreMatchers.is(0));
    }

    @Test
    public void valuesParamsetIsLoadedForChannel() throws IOException {
        HmChannel channel = DimmerHelper.createDimmerHmChannel();
        rpcClient.setChannelDatapointValues(channel, VALUES);
        MatcherAssert.assertThat(rpcClient.numberOfCalls.get(RpcClientMockImpl.GET_PARAMSET_NAME), CoreMatchers.is(1));
    }

    @Test
    public void masterParamsetIsLoadedForDummyChannel() throws IOException {
        HmChannel channel = DimmerHelper.createDimmerDummyChannel();
        rpcClient.setChannelDatapointValues(channel, MASTER);
        MatcherAssert.assertThat(rpcClient.numberOfCalls.get(RpcClientMockImpl.GET_PARAMSET_NAME), CoreMatchers.is(1));
    }

    @Test
    public void valuesParamsetIsNotLoadedForDummyChannel() throws IOException {
        HmChannel channel = DimmerHelper.createDimmerDummyChannel();
        rpcClient.setChannelDatapointValues(channel, VALUES);
        MatcherAssert.assertThat(rpcClient.numberOfCalls.get(RpcClientMockImpl.GET_PARAMSET_NAME), CoreMatchers.is(0));
    }

    @Test
    public void burstRxModeIsConfiguredAsParameterOnRequest() throws IOException {
        RpcRequest<String> request = new XmlRpcRequest("setValue");
        rpcClient.configureRxMode(request, RX_BURST_MODE);
        MatcherAssert.assertThat(request.createMessage(), CoreMatchers.containsString(String.format("<value>%s</value>", RX_BURST_MODE)));
    }

    @Test
    public void wakeupRxModeIsConfiguredAsParameterOnRequest() throws IOException {
        RpcRequest<String> request = new XmlRpcRequest("setValue");
        rpcClient.configureRxMode(request, RX_WAKEUP_MODE);
        MatcherAssert.assertThat(request.createMessage(), CoreMatchers.containsString(String.format("<value>%s</value>", RX_WAKEUP_MODE)));
    }

    @Test
    public void rxModeIsNotConfiguredAsParameterOnRequestForNull() throws IOException {
        RpcRequest<String> request = new XmlRpcRequest("setValue");
        rpcClient.configureRxMode(request, null);
        MatcherAssert.assertThat(request.createMessage(), CoreMatchers.not(CoreMatchers.containsString("<value>")));
    }

    @Test
    public void rxModeIsNotConfiguredAsParameterOnRequestForInvalidString() throws IOException {
        RpcRequest<String> request = new XmlRpcRequest("setValue");
        rpcClient.configureRxMode(request, "SUPER_RX_MODE");
        MatcherAssert.assertThat(request.createMessage(), CoreMatchers.not(CoreMatchers.containsString("<value>")));
    }
}

