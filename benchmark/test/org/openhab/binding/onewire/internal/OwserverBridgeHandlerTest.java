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
package org.openhab.binding.onewire.internal;


import OwserverConnectionState.FAILED;
import OwserverConnectionState.OPENED;
import ThingStatus.OFFLINE;
import ThingStatus.ONLINE;
import ThingStatus.UNKNOWN;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.test.java.JavaTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openhab.binding.onewire.internal.handler.OwserverBridgeHandler;
import org.openhab.binding.onewire.internal.owserver.OwserverConnection;


/**
 * Tests cases for {@link OwserverBridgeHandler}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class OwserverBridgeHandlerTest extends JavaTest {
    private static final String TEST_HOST = "foo.bar";

    private static final int TEST_PORT = 4711;

    Map<String, Object> bridgeProperties = new HashMap<>();

    @Mock
    OwserverConnection owserverConnection;

    Bridge bridge;

    @Mock
    ThingHandlerCallback thingHandlerCallback;

    OwserverBridgeHandler bridgeHandler;

    @Test
    public void testInitializationStartsConnectionWithOptions() {
        bridgeHandler.initialize();
        Mockito.verify(owserverConnection).setHost(OwserverBridgeHandlerTest.TEST_HOST);
        Mockito.verify(owserverConnection).setPort(OwserverBridgeHandlerTest.TEST_PORT);
        Mockito.verify(owserverConnection, Mockito.timeout(5000)).start();
    }

    @Test
    public void testInitializationReportsRefreshableOnSuccessfullConnection() {
        Mockito.doAnswer(( answer) -> {
            bridgeHandler.reportConnectionState(OPENED);
            return null;
        }).when(owserverConnection).start();
        bridgeHandler.initialize();
        ArgumentCaptor<ThingStatusInfo> statusCaptor = ArgumentCaptor.forClass(ThingStatusInfo.class);
        waitForAssert(() -> {
            verify(thingHandlerCallback, times(2)).statusUpdated(eq(bridge), statusCaptor.capture());
        });
        Assert.assertThat(statusCaptor.getAllValues().get(0).getStatus(), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(statusCaptor.getAllValues().get(1).getStatus(), CoreMatchers.is(ONLINE));
        waitForAssert(() -> assertTrue(bridgeHandler.isRefreshable()));
    }

    @Test
    public void testInitializationReportsNotRefreshableOnFailedConnection() {
        Mockito.doAnswer(( answer) -> {
            bridgeHandler.reportConnectionState(FAILED);
            return null;
        }).when(owserverConnection).start();
        bridgeHandler.initialize();
        ArgumentCaptor<ThingStatusInfo> statusCaptor = ArgumentCaptor.forClass(ThingStatusInfo.class);
        waitForAssert(() -> {
            verify(thingHandlerCallback, times(2)).statusUpdated(eq(bridge), statusCaptor.capture());
        });
        Assert.assertThat(statusCaptor.getAllValues().get(0).getStatus(), CoreMatchers.is(UNKNOWN));
        Assert.assertThat(statusCaptor.getAllValues().get(1).getStatus(), CoreMatchers.is(OFFLINE));
        waitForAssert(() -> assertFalse(bridgeHandler.isRefreshable()));
    }
}

