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


import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.RawType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.openhab.binding.mqtt.generic.internal.convention.homeassistant.DiscoverComponents.ComponentDiscovered;
import org.openhab.binding.mqtt.generic.internal.values.ColorValue;
import org.openhab.binding.mqtt.generic.internal.values.DateTimeValue;
import org.openhab.binding.mqtt.generic.internal.values.ImageValue;
import org.openhab.binding.mqtt.generic.internal.values.LocationValue;
import org.openhab.binding.mqtt.generic.internal.values.NumberValue;
import org.openhab.binding.mqtt.generic.internal.values.PercentageValue;
import org.openhab.binding.mqtt.generic.internal.values.TextValue;


/**
 * Tests the {@link ChannelState} class.
 *
 * @author David Graeff - Initial contribution
 */
public class ChannelStateTests {
    @Mock
    MqttBrokerConnection connection;

    @Mock
    ComponentDiscovered discovered;

    @Mock
    ChannelStateUpdateListener channelStateUpdateListener;

    @Mock
    ChannelUID channelUID;

    @Spy
    TextValue textValue;

    ScheduledExecutorService scheduler;

    ChannelConfig config = ChannelConfigBuilder.create("state", "command").build();

    @Test
    public void noInteractionTimeoutTest() throws InterruptedException, ExecutionException, TimeoutException {
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, textValue, channelStateUpdateListener));
        c.start(connection, scheduler, 50).get(100, TimeUnit.MILLISECONDS);
        Mockito.verify(connection).subscribe(ArgumentMatchers.eq("state"), ArgumentMatchers.eq(c));
        c.stop().get();
        Mockito.verify(connection).unsubscribe(ArgumentMatchers.eq("state"), ArgumentMatchers.eq(c));
    }

    @Test
    public void publishFormatTest() throws InterruptedException, ExecutionException, TimeoutException {
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, textValue, channelStateUpdateListener));
        c.start(connection, scheduler, 0).get(50, TimeUnit.MILLISECONDS);
        Mockito.verify(connection).subscribe(ArgumentMatchers.eq("state"), ArgumentMatchers.eq(c));
        c.publishValue(new StringType("UPDATE")).get();
        Mockito.verify(connection).publish(ArgumentMatchers.eq("command"), ArgumentMatchers.argThat(( p) -> Arrays.equals(p, "UPDATE".getBytes())), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        c.config.formatBeforePublish = "prefix%s";
        c.publishValue(new StringType("UPDATE")).get();
        Mockito.verify(connection).publish(ArgumentMatchers.eq("command"), ArgumentMatchers.argThat(( p) -> Arrays.equals(p, "prefixUPDATE".getBytes())), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        c.config.formatBeforePublish = "%1$s-%1$s";
        c.publishValue(new StringType("UPDATE")).get();
        Mockito.verify(connection).publish(ArgumentMatchers.eq("command"), ArgumentMatchers.argThat(( p) -> Arrays.equals(p, "UPDATE-UPDATE".getBytes())), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(false));
        c.config.formatBeforePublish = "%s";
        c.config.retained = true;
        c.publishValue(new StringType("UPDATE")).get();
        Mockito.verify(connection).publish(ArgumentMatchers.eq("command"), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(true));
        c.stop().get();
        Mockito.verify(connection).unsubscribe(ArgumentMatchers.eq("state"), ArgumentMatchers.eq(c));
    }

    @Test
    public void receiveWildcardTest() throws InterruptedException, ExecutionException, TimeoutException {
        ChannelState c = Mockito.spy(new ChannelState(ChannelConfigBuilder.create("state/+/topic", "command").build(), channelUID, textValue, channelStateUpdateListener));
        CompletableFuture<@Nullable
        Void> future = c.start(connection, scheduler, 100);
        c.processMessage("state/bla/topic", "A TEST".getBytes());
        future.get(300, TimeUnit.MILLISECONDS);
        Assert.assertThat(textValue.getChannelState().toString(), CoreMatchers.is("A TEST"));
        Mockito.verify(channelStateUpdateListener).updateChannelState(ArgumentMatchers.eq(channelUID), ArgumentMatchers.any());
    }

    @Test
    public void receiveStringTest() throws InterruptedException, ExecutionException, TimeoutException {
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, textValue, channelStateUpdateListener));
        CompletableFuture<@Nullable
        Void> future = c.start(connection, scheduler, 100);
        c.processMessage("state", "A TEST".getBytes());
        future.get(300, TimeUnit.MILLISECONDS);
        Assert.assertThat(textValue.getChannelState().toString(), CoreMatchers.is("A TEST"));
        Mockito.verify(channelStateUpdateListener).updateChannelState(ArgumentMatchers.eq(channelUID), ArgumentMatchers.any());
    }

    @Test
    public void receiveDecimalTest() throws InterruptedException, ExecutionException, TimeoutException {
        NumberValue value = new NumberValue(null, null, new BigDecimal(10));
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, value, channelStateUpdateListener));
        c.start(connection, Mockito.mock(ScheduledExecutorService.class), 100);
        c.processMessage("state", "15".getBytes());
        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("15"));
        c.processMessage("state", "INCREASE".getBytes());
        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("25"));
        c.processMessage("state", "DECREASE".getBytes());
        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("15"));
        Mockito.verify(channelStateUpdateListener, Mockito.times(3)).updateChannelState(ArgumentMatchers.eq(channelUID), ArgumentMatchers.any());
    }

    @Test
    public void receiveDecimalFractionalTest() throws InterruptedException, ExecutionException, TimeoutException {
        NumberValue value = new NumberValue(null, null, new BigDecimal(10.5));
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, value, channelStateUpdateListener));
        c.start(connection, Mockito.mock(ScheduledExecutorService.class), 100);
        c.processMessage("state", "5.5".getBytes());
        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("5.5"));
        c.processMessage("state", "INCREASE".getBytes());
        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("16.0"));
    }

    @Test
    public void receivePercentageTest() throws InterruptedException, ExecutionException, TimeoutException {
        PercentageValue value = new PercentageValue(new BigDecimal((-100)), new BigDecimal(100), new BigDecimal(10), null, null);
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, value, channelStateUpdateListener));
        c.start(connection, Mockito.mock(ScheduledExecutorService.class), 100);
        c.processMessage("state", "-100".getBytes());// 0%

        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("0"));
        c.processMessage("state", "100".getBytes());// 100%

        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("100"));
        c.processMessage("state", "0".getBytes());// 50%

        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("50"));
        c.processMessage("state", "INCREASE".getBytes());
        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("60"));
    }

    @Test
    public void receiveRGBColorTest() throws InterruptedException, ExecutionException, TimeoutException {
        ColorValue value = new ColorValue(true, "FON", "FOFF", 10);
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, value, channelStateUpdateListener));
        c.start(connection, Mockito.mock(ScheduledExecutorService.class), 100);
        c.processMessage("state", "ON".getBytes());// Normal on state

        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("0,0,10"));
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is("25,25,25"));
        c.processMessage("state", "FOFF".getBytes());// Custom off state

        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("0,0,0"));
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is("0,0,0"));
        c.processMessage("state", "10".getBytes());// Brightness only

        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("0,0,10"));
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is("25,25,25"));
        HSBType t = HSBType.fromRGB(12, 18, 231);
        c.processMessage("state", "12,18,231".getBytes());
        Assert.assertThat(value.getChannelState(), CoreMatchers.is(t));// HSB

        // rgb -> hsv -> rgb is quite lossy
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is("13,20,225"));
    }

    @Test
    public void receiveHSBColorTest() throws InterruptedException, ExecutionException, TimeoutException {
        ColorValue value = new ColorValue(false, "FON", "FOFF", 10);
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, value, channelStateUpdateListener));
        c.start(connection, Mockito.mock(ScheduledExecutorService.class), 100);
        c.processMessage("state", "ON".getBytes());// Normal on state

        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("0,0,10"));
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is("0,0,10"));
        c.processMessage("state", "FOFF".getBytes());// Custom off state

        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("0,0,0"));
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is("0,0,0"));
        c.processMessage("state", "10".getBytes());// Brightness only

        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("0,0,10"));
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is("0,0,10"));
        c.processMessage("state", "12,18,100".getBytes());
        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("12,18,100"));
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is("12,18,100"));
    }

    @Test
    public void receiveLocationTest() throws InterruptedException, ExecutionException, TimeoutException {
        LocationValue value = new LocationValue();
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, value, channelStateUpdateListener));
        c.start(connection, Mockito.mock(ScheduledExecutorService.class), 100);
        c.processMessage("state", "46.833974, 7.108433".getBytes());
        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is("46.833974,7.108433"));
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is("46.833974,7.108433"));
    }

    @Test
    public void receiveDateTimeTest() throws InterruptedException, ExecutionException, TimeoutException {
        DateTimeValue value = new DateTimeValue();
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, value, channelStateUpdateListener));
        c.start(connection, Mockito.mock(ScheduledExecutorService.class), 100);
        ZonedDateTime zd = ZonedDateTime.now();
        String datetime = zd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        c.processMessage("state", datetime.getBytes());
        Assert.assertThat(value.getChannelState().toString(), CoreMatchers.is((datetime + "+0100")));
        Assert.assertThat(value.getMQTTpublishValue().toString(), CoreMatchers.is(datetime));
    }

    @Test
    public void receiveImageTest() throws InterruptedException, ExecutionException, TimeoutException {
        ImageValue value = new ImageValue();
        ChannelState c = Mockito.spy(new ChannelState(config, channelUID, value, channelStateUpdateListener));
        c.start(connection, Mockito.mock(ScheduledExecutorService.class), 100);
        byte[] payload = new byte[]{ ((byte) (255)), ((byte) (216)), 1, 2, ((byte) (255)), ((byte) (217)) };
        c.processMessage("state", payload);
        Assert.assertThat(value.getChannelState(), CoreMatchers.is(CoreMatchers.instanceOf(RawType.class)));
        Assert.assertThat(getMimeType(), CoreMatchers.is("image/jpeg"));
    }
}

