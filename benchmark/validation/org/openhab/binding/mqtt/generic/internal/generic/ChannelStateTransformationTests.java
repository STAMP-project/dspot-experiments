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
package org.openhab.binding.mqtt.generic.internal.generic;


import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.transform.TransformationService;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.eclipse.smarthome.io.transport.mqtt.MqttException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.mqtt.generic.internal.handler.GenericThingHandler;
import org.openhab.binding.mqtt.generic.internal.handler.ThingChannelConstants;
import org.openhab.binding.mqtt.handler.AbstractBrokerHandler;


/**
 * Tests cases for {@link ThingHandler} to test the json transformation.
 *
 * @author David Graeff - Initial contribution
 */
public class ChannelStateTransformationTests {
    @Mock
    private TransformationService jsonPathService;

    @Mock
    private TransformationServiceProvider transformationServiceProvider;

    @Mock
    private ThingHandlerCallback callback;

    @Mock
    private Thing thing;

    @Mock
    private AbstractBrokerHandler bridgeHandler;

    @Mock
    private MqttBrokerConnection connection;

    private GenericThingHandler thingHandler;

    @SuppressWarnings("null")
    @Test
    public void initialize() throws MqttException {
        Mockito.when(thing.getChannels()).thenReturn(ThingChannelConstants.thingChannelListWithJson);
        thingHandler.initialize();
        ChannelState channelConfig = thingHandler.getChannelState(ThingChannelConstants.textChannelUID);
        Assert.assertThat(channelConfig.transformationsIn.get(0).pattern, CoreMatchers.is(ThingChannelConstants.jsonPathPattern));
    }

    @SuppressWarnings("null")
    @Test
    public void processMessageWithJSONPath() throws Exception {
        Mockito.when(jsonPathService.transform(ThingChannelConstants.jsonPathPattern, ThingChannelConstants.jsonPathJSON)).thenReturn("23.2");
        thingHandler.initialize();
        ChannelState channelConfig = thingHandler.getChannelState(ThingChannelConstants.textChannelUID);
        channelConfig.setChannelStateUpdateListener(thingHandler);
        ChannelStateTransformation transformation = channelConfig.transformationsIn.get(0);
        byte[] payload = ThingChannelConstants.jsonPathJSON.getBytes();
        Assert.assertThat(transformation.pattern, CoreMatchers.is(ThingChannelConstants.jsonPathPattern));
        // Test process message
        channelConfig.processMessage(channelConfig.getStateTopic(), payload);
        Mockito.verify(callback).stateUpdated(ArgumentMatchers.eq(ThingChannelConstants.textChannelUID), ArgumentMatchers.argThat(( arg) -> "23.2".equals(arg.toString())));
        Assert.assertThat(channelConfig.getCache().getChannelState().toString(), CoreMatchers.is("23.2"));
    }
}

