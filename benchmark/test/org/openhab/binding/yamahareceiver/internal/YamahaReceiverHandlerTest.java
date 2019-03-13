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
package org.openhab.binding.yamahareceiver.internal;


import ThingStatus.OFFLINE;
import ThingStatus.ONLINE;
import java.util.List;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.yamahareceiver.internal.config.YamahaBridgeConfig;
import org.openhab.binding.yamahareceiver.internal.handler.YamahaBridgeHandler;
import org.openhab.binding.yamahareceiver.internal.protocol.DeviceInformation;
import org.openhab.binding.yamahareceiver.internal.protocol.ProtocolFactory;
import org.openhab.binding.yamahareceiver.internal.protocol.SystemControl;
import org.openhab.binding.yamahareceiver.internal.protocol.xml.AbstractXMLProtocolTest;


/**
 * Test cases for {@link YamahaBridgeHandler}. The tests provide mocks for supporting entities using Mockito.
 *
 * @author Tomasz Maruszak - Initial contribution
 */
public class YamahaReceiverHandlerTest extends AbstractXMLProtocolTest {
    private YamahaBridgeHandler subject;

    @Mock
    private YamahaBridgeConfig bridgeConfig;

    @Mock
    private Configuration configuration;

    @Mock
    private ProtocolFactory protocolFactory;

    @Mock
    private DeviceInformation deviceInformation;

    @Mock
    private SystemControl systemControl;

    @Mock
    private ThingHandlerCallback callback;

    @Mock
    private Bridge bridge;

    @Test
    public void afterInitializeBridgeShouldBeOnline() throws InterruptedException {
        // when
        subject.initialize();
        // internally there is an timer, let's allow it to execute
        Thread.sleep(200);
        // then
        ArgumentCaptor<ThingStatusInfo> statusInfoCaptor = ArgumentCaptor.forClass(ThingStatusInfo.class);
        Mockito.verify(callback, Mockito.atLeastOnce()).statusUpdated(ArgumentMatchers.same(bridge), statusInfoCaptor.capture());
        List<ThingStatusInfo> thingStatusInfo = statusInfoCaptor.getAllValues();
        // the first one will be OFFLINE
        Assert.assertThat(thingStatusInfo.get(0).getStatus(), CoreMatchers.is(CoreMatchers.equalTo(OFFLINE)));
        // depending on the internal timer, several status updates and especially the last one will be ONLINE
        Assert.assertThat(thingStatusInfo.get(((thingStatusInfo.size()) - 1)).getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
    }
}

