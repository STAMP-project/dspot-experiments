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


import ChannelKind.STATE;
import MqttBindingConstants.HOMIE_PROPERTY_VERSION;
import RefreshType.REFRESH;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.TypeParser;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.DeviceAttributes.ReadyState;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.Node;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.NodeAttributes;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.Property;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.PropertyAttributes;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.PropertyAttributes.DataTypeEnum;
import org.openhab.binding.mqtt.generic.internal.generic.ChannelState;
import org.openhab.binding.mqtt.generic.internal.generic.ChannelStateHelper;
import org.openhab.binding.mqtt.generic.internal.generic.MqttChannelTypeProvider;
import org.openhab.binding.mqtt.generic.internal.values.Value;
import org.openhab.binding.mqtt.handler.AbstractBrokerHandler;


/**
 * Tests cases for {@link HomieThingHandler}.
 *
 * @author David Graeff - Initial contribution
 */
public class HomieThingHandlerTests {
    @Mock
    private ThingHandlerCallback callback;

    private Thing thing;

    @Mock
    private AbstractBrokerHandler bridgeHandler;

    @Mock
    private MqttBrokerConnection connection;

    @Mock
    private ScheduledExecutorService scheduler;

    @Mock
    private ScheduledFuture<?> scheduledFuture;

    private HomieThingHandler thingHandler;

    private final MqttChannelTypeProvider channelTypeProvider = new MqttChannelTypeProvider();

    private final String deviceID = ThingChannelConstants.testHomieThing.getId();

    private final String deviceTopic = "homie/" + (deviceID);

    // A completed future is returned for a subscribe call to the attributes
    CompletableFuture<@Nullable
    Void> future = CompletableFuture.completedFuture(null);

    @Test
    public void initialize() {
        Assert.assertThat(thingHandler.device.isInitialized(), CoreMatchers.is(false));
        // // A completed future is returned for a subscribe call to the attributes
        Mockito.doReturn(future).when(thingHandler.device.attributes).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(future).when(thingHandler.device.attributes).unsubscribe();
        // Prevent a call to accept, that would update our thing.
        Mockito.doNothing().when(thingHandler).accept(ArgumentMatchers.any());
        // Pretend that a device state change arrived.
        thingHandler.device.attributes.state = ReadyState.ready;
        Mockito.verify(callback, Mockito.times(0)).statusUpdated(ArgumentMatchers.eq(thing), ArgumentMatchers.any());
        thingHandler.initialize();
        // Expect a call to the bridge status changed, the start, the propertiesChanged method
        Mockito.verify(thingHandler).bridgeStatusChanged(ArgumentMatchers.any());
        Mockito.verify(thingHandler).start(ArgumentMatchers.any());
        Mockito.verify(thingHandler).readyStateChanged(ArgumentMatchers.any());
        Mockito.verify(thingHandler.device.attributes).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.argThat(( arg) -> deviceTopic.equals(arg)), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Assert.assertThat(thingHandler.device.isInitialized(), CoreMatchers.is(true));
        Mockito.verify(callback).statusUpdated(ArgumentMatchers.eq(thing), ArgumentMatchers.argThat(( arg) -> (arg.getStatus().equals(ThingStatus.ONLINE)) && (arg.getStatusDetail().equals(ThingStatusDetail.NONE))));
    }

    @Test
    public void initializeGeneralTimeout() throws InterruptedException {
        // A non completed future is returned for a subscribe call to the attributes
        Mockito.doReturn(future).when(thingHandler.device.attributes).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(future).when(thingHandler.device.attributes).unsubscribe();
        // Prevent a call to accept, that would update our thing.
        Mockito.doNothing().when(thingHandler).accept(ArgumentMatchers.any());
        thingHandler.initialize();
        Mockito.verify(callback).statusUpdated(ArgumentMatchers.eq(thing), ArgumentMatchers.argThat(( arg) -> (arg.getStatus().equals(ThingStatus.OFFLINE)) && (arg.getStatusDetail().equals(ThingStatusDetail.COMMUNICATION_ERROR))));
    }

    @Test
    public void initializeNoStateReceived() throws InterruptedException {
        // A completed future is returned for a subscribe call to the attributes
        Mockito.doReturn(future).when(thingHandler.device.attributes).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(future).when(thingHandler.device.attributes).unsubscribe();
        // Prevent a call to accept, that would update our thing.
        Mockito.doNothing().when(thingHandler).accept(ArgumentMatchers.any());
        thingHandler.initialize();
        Assert.assertThat(thingHandler.device.isInitialized(), CoreMatchers.is(true));
        Mockito.verify(callback).statusUpdated(ArgumentMatchers.eq(thing), ArgumentMatchers.argThat(( arg) -> (arg.getStatus().equals(ThingStatus.OFFLINE)) && (arg.getStatusDetail().equals(ThingStatusDetail.GONE))));
    }

    @SuppressWarnings("null")
    @Test
    public void handleCommandRefresh() {
        // Create mocked homie device tree with one node and one read-only property
        Node node = thingHandler.device.createNode("node", Mockito.spy(new NodeAttributes()));
        Mockito.doReturn(future).when(node.attributes).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(future).when(node.attributes).unsubscribe();
        node.attributes.name = "testnode";
        Property property = node.createProperty("property", Mockito.spy(new PropertyAttributes()));
        Mockito.doReturn(future).when(property.attributes).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(future).when(property.attributes).unsubscribe();
        property.attributes.name = "testprop";
        property.attributes.datatype = DataTypeEnum.string_;
        property.attributes.settable = false;
        property.attributesReceived();
        node.properties.put(property.propertyID, property);
        thingHandler.device.nodes.put(node.nodeID, node);
        thingHandler.connection = connection;
        thingHandler.handleCommand(property.channelUID, REFRESH);
        Mockito.verify(callback).stateUpdated(ArgumentMatchers.argThat(( arg) -> property.channelUID.equals(arg)), ArgumentMatchers.argThat(( arg) -> property.getChannelState().getCache().getChannelState().equals(arg)));
    }

    @SuppressWarnings("null")
    @Test
    public void handleCommandUpdate() {
        // Create mocked homie device tree with one node and one writable property
        Node node = thingHandler.device.createNode("node", Mockito.spy(new NodeAttributes()));
        Mockito.doReturn(future).when(node.attributes).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(future).when(node.attributes).unsubscribe();
        node.attributes.name = "testnode";
        Property property = node.createProperty("property", Mockito.spy(new PropertyAttributes()));
        Mockito.doReturn(future).when(property.attributes).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.doReturn(future).when(property.attributes).unsubscribe();
        property.attributes.name = "testprop";
        property.attributes.datatype = DataTypeEnum.string_;
        property.attributes.settable = true;
        property.attributesReceived();
        node.properties.put(property.propertyID, property);
        thingHandler.device.nodes.put(node.nodeID, node);
        ChannelState channelState = property.getChannelState();
        Assert.assertNotNull(channelState);
        ChannelStateHelper.setConnection(channelState, connection);// Pretend we called start()

        thingHandler.connection = connection;
        StringType updateValue = new StringType("UPDATE");
        thingHandler.handleCommand(property.channelUID, updateValue);
        Assert.assertThat(property.getChannelState().getCache().getChannelState().toString(), CoreMatchers.is("UPDATE"));
        Mockito.verify(connection, Mockito.times(1)).publish(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        // Check non writable property
        property.attributes.settable = false;
        property.attributesReceived();
        // Assign old value
        Value value = property.getChannelState().getCache();
        Command command = TypeParser.parseCommand(value.getSupportedCommandTypes(), "OLDVALUE");
        property.getChannelState().getCache().update(command);
        // Try to update with new value
        updateValue = new StringType("SOMETHINGNEW");
        thingHandler.handleCommand(property.channelUID, updateValue);
        // Expect old value and no MQTT publish
        Assert.assertThat(property.getChannelState().getCache().getChannelState().toString(), CoreMatchers.is("OLDVALUE"));
        Mockito.verify(connection, Mockito.times(1)).publish(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void propertiesChanged() throws InterruptedException, ExecutionException {
        thingHandler.device.initialize("homie", "device", new ArrayList<Channel>());
        thingHandler.connection = connection;
        // Create mocked homie device tree with one node and one property
        Mockito.doAnswer(this::createSubscriberAnswer).when(thingHandler.device.attributes).createSubscriber(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        thingHandler.device.attributes.state = ReadyState.ready;
        thingHandler.device.attributes.name = "device";
        thingHandler.device.attributes.homie = "3.0";
        thingHandler.device.attributes.nodes = new String[]{ "node" };
        // Intercept creating a node in initialize()->start() and inject a spy'ed node.
        Mockito.doAnswer(( i) -> createSpyNode("node", thingHandler.device)).when(thingHandler.device).createNode(ArgumentMatchers.any());
        Mockito.verify(thingHandler, Mockito.times(0)).nodeAddedOrChanged(ArgumentMatchers.any());
        Mockito.verify(thingHandler, Mockito.times(0)).propertyAddedOrChanged(ArgumentMatchers.any());
        thingHandler.initialize();
        Assert.assertThat(thingHandler.device.isInitialized(), CoreMatchers.is(true));
        Mockito.verify(thingHandler).propertyAddedOrChanged(ArgumentMatchers.any());
        Mockito.verify(thingHandler).nodeAddedOrChanged(ArgumentMatchers.any());
        Mockito.verify(thingHandler.device).subscribe(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.verify(thingHandler.device).attributesReceived(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Assert.assertNotNull(thingHandler.device.nodes.get("node").properties.get("property"));
        Assert.assertTrue(thingHandler.delayedProcessing.isArmed());
        // Simulate waiting for the delayed processor
        thingHandler.delayedProcessing.forceProcessNow();
        // Called for the updated property + for the new channels
        Mockito.verify(callback, Mockito.atLeast(2)).thingUpdated(ArgumentMatchers.any());
        final List<@NonNull
        Channel> channels = thingHandler.getThing().getChannels();
        Assert.assertThat(channels.size(), CoreMatchers.is(1));
        Assert.assertThat(channels.get(0).getLabel(), CoreMatchers.is("testprop"));
        Assert.assertThat(channels.get(0).getKind(), CoreMatchers.is(STATE));
        final Map<@NonNull
        String, @NonNull
        String> properties = thingHandler.getThing().getProperties();
        Assert.assertThat(properties.get(HOMIE_PROPERTY_VERSION), CoreMatchers.is("3.0"));
        Assert.assertThat(properties.size(), CoreMatchers.is(1));
    }
}

