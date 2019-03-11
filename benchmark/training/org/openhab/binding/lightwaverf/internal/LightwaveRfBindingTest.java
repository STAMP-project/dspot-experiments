/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal;


import LightwaveRfItemDirection.IN_AND_OUT;
import LightwaveRfType.HEATING_BATTERY;
import LightwaveRfType.HEATING_CURRENT_TEMP;
import LightwaveRfType.HEATING_SET_TEMP;
import LightwaveRfType.SWITCH;
import LightwaveRfType.VERSION;
import OnOffType.ON;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.lightwaverf.LightwaveRfBindingProvider;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRFCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfHeatInfoRequest;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfHeatingInfoResponse;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfOnOffCommand;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfVersionMessage;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;


public class LightwaveRfBindingTest {
    @Mock
    EventPublisher mockEventPublisher;

    @Mock
    LightwaveRfBindingProvider mockBindingProvider;

    @Mock
    LightwaverfConvertor mockLightwaveRfConvertor;

    @Mock
    LightwaveRFCommand mockLightwaveRfCommand;

    @Mock
    LightwaveRfWifiLink mockWifiLink;

    @Mock
    LightwaveRfVersionMessage mockVersionMessage;

    @Mock
    LightwaveRfHeatingInfoResponse mockHeatInfoResponse;

    @Mock
    LightwaveRfHeatInfoRequest mockHeatInfoRequest;

    @Test
    public void testInternalReceiveCommand() {
        Mockito.when(mockBindingProvider.getRoomId("MySwitch")).thenReturn("2");
        Mockito.when(mockBindingProvider.getDeviceId("MySwitch")).thenReturn("3");
        Mockito.when(mockBindingProvider.getTypeForItemName("MySwitch")).thenReturn(SWITCH);
        Mockito.when(mockBindingProvider.getDirection("MySwitch")).thenReturn(IN_AND_OUT);
        Mockito.when(mockLightwaveRfConvertor.convertToLightwaveRfMessage("2", "3", SWITCH, ON)).thenReturn(mockLightwaveRfCommand);
        LightwaveRfBinding binding = new LightwaveRfBinding();
        binding.addBindingProvider(mockBindingProvider);
        binding.setWifiLink(mockWifiLink);
        binding.setLightwaveRfConvertor(mockLightwaveRfConvertor);
        binding.internalReceiveCommand("MySwitch", ON);
        Mockito.verify(mockWifiLink).sendLightwaveCommand(mockLightwaveRfCommand);
    }

    @Test
    public void testInternalReceiveUpdate() {
        Mockito.when(mockBindingProvider.getRoomId("MySwitch")).thenReturn("2");
        Mockito.when(mockBindingProvider.getDeviceId("MySwitch")).thenReturn("3");
        Mockito.when(mockBindingProvider.getTypeForItemName("MySwitch")).thenReturn(SWITCH);
        Mockito.when(mockBindingProvider.getDirection("MySwitch")).thenReturn(IN_AND_OUT);
        Mockito.when(mockLightwaveRfConvertor.convertToLightwaveRfMessage("2", "3", SWITCH, ON)).thenReturn(mockLightwaveRfCommand);
        LightwaveRfBinding binding = new LightwaveRfBinding();
        binding.addBindingProvider(mockBindingProvider);
        binding.setWifiLink(mockWifiLink);
        binding.setLightwaveRfConvertor(mockLightwaveRfConvertor);
        binding.internalReceiveUpdate("MySwitch", ON);
        Mockito.verify(mockWifiLink).sendLightwaveCommand(mockLightwaveRfCommand);
    }

    @Test
    public void testRoomDeviceMessageRecevied() {
        Mockito.when(mockBindingProvider.getBindingItemsForRoomDevice("2", "3")).thenReturn(Arrays.asList("MySwitch"));
        Mockito.when(mockBindingProvider.getTypeForItemName("MySwitch")).thenReturn(SWITCH);
        Mockito.when(mockBindingProvider.getDirection("MySwitch")).thenReturn(IN_AND_OUT);
        LightwaveRfBinding binding = new LightwaveRfBinding();
        binding.addBindingProvider(mockBindingProvider);
        binding.setEventPublisher(mockEventPublisher);
        binding.roomDeviceMessageReceived(new LightwaveRfOnOffCommand(1, "2", "3", true));
        Mockito.verify(mockEventPublisher).postUpdate("MySwitch", ON);
    }

    @Test
    public void testRoomMessageRecevied() {
        Mockito.when(mockHeatInfoRequest.getState(HEATING_SET_TEMP)).thenReturn(new DecimalType(24.5));
        Mockito.when(mockHeatInfoRequest.getRoomId()).thenReturn("2");
        Mockito.when(mockBindingProvider.getBindingItemsForRoom("2")).thenReturn(Arrays.asList("Temp"));
        Mockito.when(mockBindingProvider.getTypeForItemName("Temp")).thenReturn(HEATING_SET_TEMP);
        Mockito.when(mockBindingProvider.getDirection("Temp")).thenReturn(IN_AND_OUT);
        LightwaveRfBinding binding = new LightwaveRfBinding();
        binding.addBindingProvider(mockBindingProvider);
        binding.setEventPublisher(mockEventPublisher);
        binding.roomMessageReceived(mockHeatInfoRequest);
        Mockito.verify(mockEventPublisher).postUpdate("Temp", new DecimalType(24.5));
    }

    @Test
    public void testSerialMessageRecevied() {
        Mockito.when(mockHeatInfoResponse.getState(HEATING_BATTERY)).thenReturn(new DecimalType(2.99));
        Mockito.when(mockHeatInfoResponse.getState(HEATING_CURRENT_TEMP)).thenReturn(new DecimalType(22.3));
        Mockito.when(mockHeatInfoResponse.getSerial()).thenReturn("655432");
        Mockito.when(mockBindingProvider.getBindingItemsForSerial("655432")).thenReturn(Arrays.asList("Battery", "Temp"));
        Mockito.when(mockBindingProvider.getTypeForItemName("Battery")).thenReturn(HEATING_BATTERY);
        Mockito.when(mockBindingProvider.getTypeForItemName("Temp")).thenReturn(HEATING_CURRENT_TEMP);
        Mockito.when(mockBindingProvider.getDirection("Battery")).thenReturn(IN_AND_OUT);
        Mockito.when(mockBindingProvider.getDirection("Temp")).thenReturn(IN_AND_OUT);
        LightwaveRfBinding binding = new LightwaveRfBinding();
        binding.addBindingProvider(mockBindingProvider);
        binding.setEventPublisher(mockEventPublisher);
        binding.serialMessageReceived(mockHeatInfoResponse);
        Mockito.verify(mockEventPublisher).postUpdate("Battery", new DecimalType(2.99));
        Mockito.verify(mockEventPublisher).postUpdate("Temp", new DecimalType(22.3));
    }

    @Test
    public void testVersionMessageRecevied() {
        Mockito.when(mockVersionMessage.getState(VERSION)).thenReturn(new StringType("2.91"));
        Mockito.when(mockBindingProvider.getBindingItemsForType(VERSION)).thenReturn(Arrays.asList("MyVersion"));
        Mockito.when(mockBindingProvider.getTypeForItemName("MyVersion")).thenReturn(VERSION);
        Mockito.when(mockBindingProvider.getDirection("MyVersion")).thenReturn(IN_AND_OUT);
        LightwaveRfBinding binding = new LightwaveRfBinding();
        binding.addBindingProvider(mockBindingProvider);
        binding.setEventPublisher(mockEventPublisher);
        binding.versionMessageReceived(mockVersionMessage);
        Mockito.verify(mockEventPublisher).postUpdate("MyVersion", new StringType("2.91"));
    }
}

