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
package org.openhab.binding.nest.handler;


import ThingStatus.UNKNOWN;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.nest.internal.config.NestBridgeConfiguration;
import org.openhab.binding.nest.internal.handler.NestRedirectUrlSupplier;


/**
 * Tests cases for {@link NestBridgeHandler}.
 *
 * @author David Bennett - Initial contribution
 */
public class NestBridgeHandlerTest {
    private ThingHandler handler;

    @Mock
    private ThingHandlerCallback callback;

    @Mock
    private Bridge bridge;

    @Mock
    private Configuration configuration;

    @Mock
    private NestRedirectUrlSupplier redirectUrlSupplier;

    @SuppressWarnings("null")
    @Test
    public void initializeShouldCallTheCallback() {
        Mockito.when(bridge.getConfiguration()).thenReturn(configuration);
        NestBridgeConfiguration bridgeConfig = new NestBridgeConfiguration();
        Mockito.when(configuration.as(ArgumentMatchers.eq(NestBridgeConfiguration.class))).thenReturn(bridgeConfig);
        bridgeConfig.accessToken = "my token";
        // we expect the handler#initialize method to call the callback during execution and
        // pass it the thing and a ThingStatusInfo object containing the ThingStatus of the thing.
        handler.initialize();
        // the argument captor will capture the argument of type ThingStatusInfo given to the
        // callback#statusUpdated method.
        ArgumentCaptor<ThingStatusInfo> statusInfoCaptor = ArgumentCaptor.forClass(ThingStatusInfo.class);
        // verify the interaction with the callback and capture the ThingStatusInfo argument:
        Mockito.verify(callback).statusUpdated(ArgumentMatchers.eq(bridge), statusInfoCaptor.capture());
        // assert that the ThingStatusInfo given to the callback was build with the UNKNOWN status:
        ThingStatusInfo thingStatusInfo = statusInfoCaptor.getValue();
        Assert.assertThat(thingStatusInfo.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(UNKNOWN)));
    }
}

