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
package org.openhab.binding.mqtt.generic;


import DataTypeEnum.boolean_;
import DataTypeEnum.float_;
import MqttConnectionState.CONNECTED;
import OnOffType.OFF;
import OnOffType.ON;
import ReadyState.ready;
import UnDefType.UNDEF;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.eclipse.smarthome.io.transport.mqtt.MqttConnectionObserver;
import org.eclipse.smarthome.io.transport.mqtt.MqttConnectionState;
import org.eclipse.smarthome.io.transport.mqtt.MqttService;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.Device;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.DeviceAttributes;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.DeviceCallback;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.Node;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.NodeAttributes;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.Property;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.PropertyAttributes;
import org.openhab.binding.mqtt.generic.internal.convention.homie300.PropertyHelper;
import org.openhab.binding.mqtt.generic.internal.generic.ChannelState;
import org.openhab.binding.mqtt.generic.internal.handler.HomieThingHandler;
import org.openhab.binding.mqtt.generic.internal.handler.ThingChannelConstants;
import org.openhab.binding.mqtt.generic.internal.tools.ChildMap;
import org.openhab.binding.mqtt.generic.internal.tools.WaitForTopicValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A full implementation test, that starts the embedded MQTT broker and publishes a homie device tree.
 *
 * @author David Graeff - Initial contribution
 */
public class HomieImplementationTests extends JavaOSGiTest {
    final Logger logger = LoggerFactory.getLogger(HomieImplementationTests.class);

    private MqttService mqttService;

    private MqttBrokerConnection embeddedConnection;

    private MqttBrokerConnection connection;

    private int registeredTopics = 100;

    // The handler is not tested here, so just mock the callback
    @Mock
    DeviceCallback callback;

    // A handler mock is required to verify that channel value changes have been received
    @Mock
    HomieThingHandler handler;

    ScheduledExecutorService scheduler;

    /**
     * Create an observer that fails the test as soon as the broker client connection changes its connection state
     * to something else then CONNECTED.
     */
    MqttConnectionObserver failIfChange = new MqttConnectionObserver() {
        @Override
        public void connectionStateChanged(@NonNull
        MqttConnectionState state, @Nullable
        Throwable error) {
            Assert.assertThat(state, CoreMatchers.is(CONNECTED));
        }
    };

    private final String baseTopic = "homie";

    private final String deviceID = ThingChannelConstants.testHomieThing.getId();

    private final String deviceTopic = ((baseTopic) + "/") + (deviceID);

    String propertyTestTopic;

    @Test
    public void retrieveAllTopics() throws InterruptedException, ExecutionException, TimeoutException {
        CountDownLatch c = new CountDownLatch(registeredTopics);
        connection.subscribe(((deviceTopic) + "/#"), ( topic, payload) -> c.countDown()).get(200, TimeUnit.MILLISECONDS);
        Assert.assertTrue((("Connection " + (connection.getClientId())) + " not retrieving all topics"), c.await(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void retrieveOneAttribute() throws InterruptedException, ExecutionException {
        WaitForTopicValue watcher = new WaitForTopicValue(connection, ((deviceTopic) + "/$homie"));
        Assert.assertThat(watcher.waitForTopicValue(100), CoreMatchers.is("3.0"));
    }

    @SuppressWarnings("null")
    @Test
    public void retrieveAttributes() throws InterruptedException, ExecutionException {
        Assert.assertThat(connection.hasSubscribers(), CoreMatchers.is(false));
        Node node = new Node(deviceTopic, "testnode", ThingChannelConstants.testHomieThing, callback, new NodeAttributes());
        Property property = Mockito.spy(new Property(((deviceTopic) + "/testnode"), node, "temperature", callback, new PropertyAttributes()));
        // Create a scheduler
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(4);
        property.subscribe(connection, scheduler, 100).get();
        Assert.assertThat(property.attributes.settable, CoreMatchers.is(true));
        Assert.assertThat(property.attributes.retained, CoreMatchers.is(true));
        Assert.assertThat(property.attributes.name, CoreMatchers.is("Testprop"));
        Assert.assertThat(property.attributes.unit, CoreMatchers.is("?C"));
        Assert.assertThat(property.attributes.datatype, CoreMatchers.is(float_));
        Assert.assertThat(property.attributes.format, CoreMatchers.is("-100:100"));
        Mockito.verify(property).attributesReceived();
        // Receive property value
        ChannelState channelState = Mockito.spy(property.getChannelState());
        PropertyHelper.setChannelState(property, channelState);
        property.startChannel(connection, scheduler, 200).get();
        Mockito.verify(channelState).start(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.verify(channelState).processMessage(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(callback).updateChannelState(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertThat(property.getChannelState().getCache().getChannelState(), CoreMatchers.is(new DecimalType(10)));
        property.stop().get();
        Assert.assertThat(connection.hasSubscribers(), CoreMatchers.is(false));
    }

    @SuppressWarnings("null")
    @Test
    public void parseHomieTree() throws InterruptedException, ExecutionException, TimeoutException {
        // Create a Homie Device object. Because spied Nodes are required for call verification,
        // the full Device constructor need to be used and a ChildMap object need to be created manually.
        ChildMap<Node> nodeMap = new ChildMap();
        Device device = Mockito.spy(new Device(ThingChannelConstants.testHomieThing, callback, new DeviceAttributes(), nodeMap));
        // Intercept creating a node in initialize()->start() and inject a spy'ed node.
        Mockito.doAnswer(this::createSpyNode).when(device).createNode(ArgumentMatchers.any());
        // initialize the device, subscribe and wait.
        device.initialize(baseTopic, deviceID, Collections.emptyList());
        device.subscribe(connection, scheduler, 200).get();
        Assert.assertThat(device.isInitialized(), CoreMatchers.is(true));
        // Check device attributes
        Assert.assertThat(device.attributes.homie, CoreMatchers.is("3.0"));
        Assert.assertThat(device.attributes.name, CoreMatchers.is("Name"));
        Assert.assertThat(device.attributes.state, CoreMatchers.is(ready));
        Assert.assertThat(device.attributes.nodes.length, CoreMatchers.is(1));
        Mockito.verify(device, Mockito.times(4)).attributeChanged(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        Mockito.verify(callback).readyStateChanged(ArgumentMatchers.eq(ready));
        Mockito.verify(device).attributesReceived(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        // Expect 1 node
        Assert.assertThat(device.nodes.size(), CoreMatchers.is(1));
        // Check node and node attributes
        Node node = device.nodes.get("testnode");
        Mockito.verify(node).subscribe(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.verify(node).attributesReceived(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Mockito.verify(node.attributes).subscribeAndReceive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        Assert.assertThat(node.attributes.type, CoreMatchers.is("Type"));
        Assert.assertThat(node.attributes.name, CoreMatchers.is("Testnode"));
        // Expect 2 property
        Assert.assertThat(node.properties.size(), CoreMatchers.is(3));
        // Check property and property attributes
        Property property = node.properties.get("temperature");
        Assert.assertThat(property.attributes.settable, CoreMatchers.is(true));
        Assert.assertThat(property.attributes.retained, CoreMatchers.is(true));
        Assert.assertThat(property.attributes.name, CoreMatchers.is("Testprop"));
        Assert.assertThat(property.attributes.unit, CoreMatchers.is("?C"));
        Assert.assertThat(property.attributes.datatype, CoreMatchers.is(float_));
        Assert.assertThat(property.attributes.format, CoreMatchers.is("-100:100"));
        Mockito.verify(property).attributesReceived();
        Assert.assertNotNull(property.getChannelState());
        Assert.assertThat(property.getType().getState().getMinimum().intValue(), CoreMatchers.is((-100)));
        Assert.assertThat(property.getType().getState().getMaximum().intValue(), CoreMatchers.is(100));
        // Check property and property attributes
        Property propertyBell = node.properties.get("doorbell");
        Mockito.verify(propertyBell).attributesReceived();
        Assert.assertThat(propertyBell.attributes.settable, CoreMatchers.is(false));
        Assert.assertThat(propertyBell.attributes.retained, CoreMatchers.is(false));
        Assert.assertThat(propertyBell.attributes.name, CoreMatchers.is("Doorbell"));
        Assert.assertThat(propertyBell.attributes.datatype, CoreMatchers.is(boolean_));
        // The device->node->property tree is ready. Now subscribe to property values.
        device.startChannels(connection, scheduler, 50, handler).get();
        Assert.assertThat(propertyBell.getChannelState().isStateful(), CoreMatchers.is(false));
        Assert.assertThat(propertyBell.getChannelState().getCache().getChannelState(), CoreMatchers.is(UNDEF));
        Assert.assertThat(property.getChannelState().getCache().getChannelState(), CoreMatchers.is(new DecimalType(10)));
        property = node.properties.get("testRetain");
        WaitForTopicValue watcher = new WaitForTopicValue(embeddedConnection, ((propertyTestTopic) + "/set"));
        // Watch the topic. Publish a retain=false value to MQTT
        property.getChannelState().publishValue(OFF).get();
        Assert.assertThat(watcher.waitForTopicValue(50), CoreMatchers.is("false"));
        // Publish a retain=false value to MQTT.
        property.getChannelState().publishValue(ON).get();
        // This test is flaky if the MQTT broker does not get a time to "forget" this non-retained value
        Thread.sleep(50);
        // No value is expected to be retained on this MQTT topic
        watcher = new WaitForTopicValue(embeddedConnection, ((propertyTestTopic) + "/set"));
        Assert.assertNull(watcher.waitForTopicValue(50));
    }
}

