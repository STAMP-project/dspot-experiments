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


import ComponentSwitch.switchChannelID;
import MqttConnectionState.CONNECTED;
import OnOffType.ON;
import UnDefType.UNDEF;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.types.State;
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
import org.openhab.binding.mqtt.generic.internal.convention.homeassistant.AbstractComponent;
import org.openhab.binding.mqtt.generic.internal.convention.homeassistant.DiscoverComponents;
import org.openhab.binding.mqtt.generic.internal.convention.homeassistant.DiscoverComponents.ComponentDiscovered;
import org.openhab.binding.mqtt.generic.internal.convention.homeassistant.HaID;
import org.openhab.binding.mqtt.generic.internal.generic.ChannelStateUpdateListener;
import org.openhab.binding.mqtt.generic.internal.generic.MqttChannelTypeProvider;
import org.openhab.binding.mqtt.generic.internal.handler.ThingChannelConstants;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A full implementation test, that starts the embedded MQTT broker and publishes a homeassistant MQTT discovery device
 * tree.
 *
 * @author David Graeff - Initial contribution
 */
public class HomeAssistantMQTTImplementationTests extends JavaOSGiTest {
    final Logger logger = LoggerFactory.getLogger(HomeAssistantMQTTImplementationTests.class);

    private MqttService mqttService;

    private MqttBrokerConnection embeddedConnection;

    private MqttBrokerConnection connection;

    private int registeredTopics = 100;

    Throwable failure = null;

    @Mock
    ChannelStateUpdateListener channelStateUpdateListener;

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

    private String testObjectTopic;

    @Test
    public void reconnectTest() throws InterruptedException, ExecutionException, TimeoutException, ConfigurationException {
        connection.removeConnectionObserver(failIfChange);
        connection.stop().get(2000, TimeUnit.MILLISECONDS);
        connection = new MqttBrokerConnection(embeddedConnection.getHost(), embeddedConnection.getPort(), embeddedConnection.isSecure(), "ha_mqtt");
        connection.start().get(2000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void retrieveAllTopics() throws InterruptedException, ExecutionException, TimeoutException {
        CountDownLatch c = new CountDownLatch(registeredTopics);
        connection.subscribe((("homeassistant/+/+/" + (ThingChannelConstants.testHomeAssistantThing.getId())) + "/#"), ( topic, payload) -> c.countDown()).get(200, TimeUnit.MILLISECONDS);
        Assert.assertTrue((("Connection " + (connection.getClientId())) + " not retrieving all topics"), c.await(200, TimeUnit.MILLISECONDS));
    }

    @Test
    public void parseHATree() throws InterruptedException, ExecutionException, TimeoutException {
        MqttChannelTypeProvider channelTypeProvider = Mockito.mock(MqttChannelTypeProvider.class);
        final Map<String, AbstractComponent<?>> haComponents = new HashMap<String, AbstractComponent<?>>();
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(4);
        DiscoverComponents discover = Mockito.spy(new DiscoverComponents(ThingChannelConstants.testHomeAssistantThing, scheduler, channelStateUpdateListener, new Gson()));
        // The DiscoverComponents object calls ComponentDiscovered callbacks.
        // In the following implementation we add the found component to the `haComponents` map
        // and add the types to the channelTypeProvider, like in the real Thing handler.
        final CountDownLatch latch = new CountDownLatch(1);
        ComponentDiscovered cd = ( haID, c) -> {
            haComponents.put(haID.getChannelGroupID(), c);
            c.addChannelTypes(channelTypeProvider);
            channelTypeProvider.setChannelGroupType(c.groupTypeUID(), c.type());
            latch.countDown();
        };
        // Start the discovery for 100ms. Forced timeout after 300ms.
        HaID haID = new HaID(testObjectTopic);
        CompletableFuture<Void> future = discover.startDiscovery(connection, 100, haID, cd).thenRun(() -> {
        }).exceptionally(( e) -> {
            failure = e;
            return null;
        });
        Assert.assertTrue(latch.await(300, TimeUnit.MILLISECONDS));
        future.get(100, TimeUnit.MILLISECONDS);
        // No failure expected and one discoverd result
        Assert.assertNull(failure);
        Assert.assertThat(haComponents.size(), CoreMatchers.is(1));
        // For the switch component we should have one channel group type and one channel type
        Mockito.verify(channelTypeProvider, Mockito.times(1)).setChannelGroupType(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(channelTypeProvider, Mockito.times(1)).setChannelType(ArgumentMatchers.any(), ArgumentMatchers.any());
        // We expect a switch component with an OnOff channel with the initial value UNDEF:
        State value = haComponents.get(haID.getChannelGroupID()).channelTypes().get(switchChannelID).channelState.getCache().getChannelState();
        Assert.assertThat(value, CoreMatchers.is(UNDEF));
        haComponents.values().stream().map(( e) -> e.start(connection, scheduler, 100)).reduce(CompletableFuture.completedFuture(null), ( a, v) -> a.thenCompose(( b) -> v)).exceptionally(( e) -> {
            failure = e;
            return null;
        }).get();
        // We should have received the retained value, while subscribing to the channels MQTT state topic.
        Mockito.verify(channelStateUpdateListener, Mockito.times(1)).updateChannelState(ArgumentMatchers.any(), ArgumentMatchers.any());
        // Value should be ON now.
        value = haComponents.get(haID.getChannelGroupID()).channelTypes().get(switchChannelID).channelState.getCache().getChannelState();
        Assert.assertThat(value, CoreMatchers.is(ON));
    }
}

