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
package org.openhab.binding.mqtt.generic.internal.handler;


import OnOffType.ON;
import RefreshType.REFRESH;
import java.util.concurrent.CompletableFuture;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.mqtt.generic.internal.generic.ChannelConfig;
import org.openhab.binding.mqtt.generic.internal.generic.ChannelConfigBuilder;
import org.openhab.binding.mqtt.generic.internal.generic.ChannelState;
import org.openhab.binding.mqtt.generic.internal.values.OnOffValue;
import org.openhab.binding.mqtt.generic.internal.values.TextValue;
import org.openhab.binding.mqtt.generic.internal.values.ValueFactory;
import org.openhab.binding.mqtt.handler.AbstractBrokerHandler;


/**
 * Tests cases for {@link GenericThingHandler}.
 *
 * @author David Graeff - Initial contribution
 */
public class GenericThingHandlerTests {
    @Mock
    private ThingHandlerCallback callback;

    @Mock
    private Thing thing;

    @Mock
    private AbstractBrokerHandler bridgeHandler;

    @Mock
    private MqttBrokerConnection connection;

    private GenericThingHandler thingHandler;

    @Test(expected = IllegalArgumentException.class)
    public void initializeWithUnknownThingUID() {
        ChannelConfig config = ThingChannelConstants.textConfiguration().as(ChannelConfig.class);
        thingHandler.createChannelState(config, new org.eclipse.smarthome.core.thing.ChannelUID(ThingChannelConstants.testGenericThing, "test"), ValueFactory.createValueState(config, ThingChannelConstants.unknownChannel.getId()));
    }

    @Test
    public void initialize() {
        thingHandler.initialize();
        Mockito.verify(thingHandler).bridgeStatusChanged(ArgumentMatchers.any());
        Mockito.verify(thingHandler).start(ArgumentMatchers.any());
        Assert.assertThat(thingHandler.connection, CoreMatchers.is(connection));
        ChannelState channelConfig = thingHandler.channelStateByChannelUID.get(ThingChannelConstants.textChannelUID);
        Assert.assertThat(channelConfig.getStateTopic(), CoreMatchers.is("test/state"));
        Assert.assertThat(channelConfig.getCommandTopic(), CoreMatchers.is("test/command"));
        Mockito.verify(connection).subscribe(ArgumentMatchers.eq(channelConfig.getStateTopic()), ArgumentMatchers.eq(channelConfig));
        Mockito.verify(callback).statusUpdated(ArgumentMatchers.eq(thing), ArgumentMatchers.argThat(( arg) -> (arg.getStatus().equals(ThingStatus.ONLINE)) && (arg.getStatusDetail().equals(ThingStatusDetail.NONE))));
    }

    @Test
    public void handleCommandRefresh() {
        ChannelState channelConfig = Mockito.mock(ChannelState.class);
        Mockito.doReturn(CompletableFuture.completedFuture(true)).when(channelConfig).start(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(CompletableFuture.completedFuture(true)).when(channelConfig).stop();
        Mockito.doReturn(channelConfig).when(thingHandler).createChannelState(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        thingHandler.initialize();
        TextValue value = Mockito.spy(new TextValue());
        Mockito.doReturn(value).when(channelConfig).getCache();
        thingHandler.connection = connection;
        thingHandler.handleCommand(ThingChannelConstants.textChannelUID, REFRESH);
        Mockito.verify(value).getChannelState();
    }

    @Test
    public void handleCommandUpdateString() {
        TextValue value = Mockito.spy(new TextValue());
        ChannelState channelConfig = Mockito.spy(new ChannelState(ChannelConfigBuilder.create("stateTopic", "commandTopic").build(), ThingChannelConstants.textChannelUID, value, thingHandler));
        Mockito.doReturn(channelConfig).when(thingHandler).createChannelState(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        thingHandler.initialize();
        thingHandler.connection = connection;
        StringType updateValue = new StringType("UPDATE");
        thingHandler.handleCommand(ThingChannelConstants.textChannelUID, updateValue);
        Mockito.verify(value).update(ArgumentMatchers.eq(updateValue));
        Assert.assertThat(channelConfig.getCache().getChannelState().toString(), CoreMatchers.is("UPDATE"));
    }

    @Test
    public void handleCommandUpdateBoolean() {
        OnOffValue value = Mockito.spy(new OnOffValue("ON", "OFF"));
        ChannelState channelConfig = Mockito.spy(new ChannelState(ChannelConfigBuilder.create("stateTopic", "commandTopic").build(), ThingChannelConstants.textChannelUID, value, thingHandler));
        Mockito.doReturn(channelConfig).when(thingHandler).createChannelState(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        thingHandler.initialize();
        thingHandler.connection = connection;
        StringType updateValue = new StringType("ON");
        thingHandler.handleCommand(ThingChannelConstants.textChannelUID, updateValue);
        Mockito.verify(value).update(ArgumentMatchers.eq(updateValue));
        Assert.assertThat(channelConfig.getCache().getChannelState(), CoreMatchers.is(ON));
    }

    @Test
    public void processMessage() {
        TextValue textValue = new TextValue();
        ChannelState channelConfig = Mockito.spy(new ChannelState(ChannelConfigBuilder.create("test/state", "test/state/set").build(), ThingChannelConstants.textChannelUID, textValue, thingHandler));
        Mockito.doReturn(channelConfig).when(thingHandler).createChannelState(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        thingHandler.initialize();
        byte[] payload = "UPDATE".getBytes();
        // Test process message
        channelConfig.processMessage("test/state", payload);
        Mockito.verify(callback).statusUpdated(ArgumentMatchers.eq(thing), ArgumentMatchers.argThat(( arg) -> arg.getStatus().equals(ThingStatus.ONLINE)));
        Mockito.verify(callback).stateUpdated(ArgumentMatchers.eq(ThingChannelConstants.textChannelUID), ArgumentMatchers.argThat(( arg) -> "UPDATE".equals(arg.toString())));
        Assert.assertThat(textValue.getChannelState().toString(), CoreMatchers.is("UPDATE"));
    }
}

