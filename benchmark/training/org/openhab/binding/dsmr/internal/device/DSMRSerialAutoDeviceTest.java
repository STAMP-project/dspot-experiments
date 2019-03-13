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
package org.openhab.binding.dsmr.internal.device;


import DSMRBindingConstants.DSMR_PORT_NAME;
import DSMRConnectorErrorEvent.DONT_EXISTS;
import DSMRConnectorErrorEvent.IN_USE;
import DeviceState.DISCOVER_SETTINGS;
import DeviceState.ERROR;
import DeviceState.NORMAL;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.io.transport.serial.PortInUseException;
import org.eclipse.smarthome.io.transport.serial.SerialPort;
import org.eclipse.smarthome.io.transport.serial.SerialPortEvent;
import org.eclipse.smarthome.io.transport.serial.SerialPortEventListener;
import org.eclipse.smarthome.io.transport.serial.SerialPortIdentifier;
import org.eclipse.smarthome.io.transport.serial.SerialPortManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openhab.binding.dsmr.internal.TelegramReaderUtil;
import org.openhab.binding.dsmr.internal.device.connector.DSMRConnectorErrorEvent;
import org.openhab.binding.dsmr.internal.device.p1telegram.P1Telegram;


/**
 * Test class for {@link DSMRSerialAutoDevice}.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
public class DSMRSerialAutoDeviceTest {
    private static final String DUMMY_PORTNAME = "/dev/dummy-serial";

    private static final String TELEGRAM_NAME = "dsmr_50";

    @Mock
    private SerialPortIdentifier mockIdentifier;

    @Mock
    private ScheduledExecutorService scheduler;

    @Mock
    private SerialPort mockSerialPort;

    private SerialPortManager serialPortManager = new SerialPortManager() {
        @Override
        public SerialPortIdentifier getIdentifier(String name) {
            Assert.assertEquals("Expect the passed serial port name", DSMRSerialAutoDeviceTest.DUMMY_PORTNAME, name);
            return mockIdentifier;
        }

        @Override
        @NonNull
        public Stream<@NonNull
        SerialPortIdentifier> getIdentifiers() {
            return Stream.empty();
        }
    };

    private SerialPortEventListener serialPortEventListener;

    @Test
    public void testHandlingDataAndRestart() throws IOException, PortInUseException {
        mockValidSerialPort();
        AtomicReference<P1Telegram> telegramRef = new AtomicReference<>(null);
        DSMREventListener listener = new DSMREventListener() {
            @Override
            public void handleTelegramReceived(@NonNull
            P1Telegram telegram) {
                telegramRef.set(telegram);
            }

            @Override
            public void handleErrorEvent(@NonNull
            DSMRConnectorErrorEvent connectorErrorEvent) {
                Assert.fail(("No handleErrorEvent Expected" + connectorErrorEvent));
            }
        };
        try (InputStream inputStream = new ByteArrayInputStream(TelegramReaderUtil.readRawTelegram(DSMRSerialAutoDeviceTest.TELEGRAM_NAME))) {
            Mockito.when(mockSerialPort.getInputStream()).thenReturn(inputStream);
            DSMRSerialAutoDevice device = new DSMRSerialAutoDevice(serialPortManager, DSMRSerialAutoDeviceTest.DUMMY_PORTNAME, listener, scheduler, 1);
            device.start();
            Assert.assertSame("Expect to be starting discovery state", DISCOVER_SETTINGS, device.getState());
            serialPortEventListener.serialEvent(new DSMRSerialAutoDeviceTest.MockSerialPortEvent(mockSerialPort, SerialPortEvent.BI, false, true));
            Assert.assertSame("Expect to be still in discovery state", DISCOVER_SETTINGS, device.getState());
            serialPortEventListener.serialEvent(new DSMRSerialAutoDeviceTest.MockSerialPortEvent(mockSerialPort, SerialPortEvent.DATA_AVAILABLE, false, true));
            Assert.assertSame("Expect to be in normal state", NORMAL, device.getState());
            device.restart();
            Assert.assertSame("Expect not to start rediscovery when in normal state", NORMAL, device.getState());
            device.stop();
        }
        Assert.assertNotNull("Expected to have read a telegram", telegramRef.get());
    }

    @Test
    public void testHandleError() throws IOException, PortInUseException {
        AtomicReference<DSMRConnectorErrorEvent> eventRef = new AtomicReference<>(null);
        DSMREventListener listener = new DSMREventListener() {
            @Override
            public void handleTelegramReceived(@NonNull
            P1Telegram telegram) {
                Assert.fail(("No telegram expected:" + telegram));
            }

            @Override
            public void handleErrorEvent(@NonNull
            DSMRConnectorErrorEvent connectorErrorEvent) {
                eventRef.set(connectorErrorEvent);
            }
        };
        try (InputStream inputStream = new ByteArrayInputStream(new byte[]{  })) {
            Mockito.when(mockSerialPort.getInputStream()).thenReturn(inputStream);
            // Trigger device to go into error stage with port in use.
            Mockito.doAnswer(( a) -> {
                throw new PortInUseException();
            }).when(mockIdentifier).open(ArgumentMatchers.eq(DSMR_PORT_NAME), ArgumentMatchers.anyInt());
            DSMRSerialAutoDevice device = new DSMRSerialAutoDevice(serialPortManager, DSMRSerialAutoDeviceTest.DUMMY_PORTNAME, listener, scheduler, 1);
            device.start();
            Assert.assertSame("Expected an error", IN_USE, eventRef.get());
            Assert.assertSame("Expect to be in error state", ERROR, device.getState());
            // Trigger device to restart
            mockValidSerialPort();
            device.restart();
            Assert.assertSame("Expect to be starting discovery state", DISCOVER_SETTINGS, device.getState());
            // Trigger device to go into error stage with port doesn't exist.
            mockIdentifier = null;
            device = new DSMRSerialAutoDevice(serialPortManager, DSMRSerialAutoDeviceTest.DUMMY_PORTNAME, listener, scheduler, 1);
            device.start();
            Assert.assertSame("Expected an error", DONT_EXISTS, eventRef.get());
            Assert.assertSame("Expect to be in error state", ERROR, device.getState());
        }
    }

    /**
     * Mock class implementing {@link SerialPortEvent}.
     */
    private static class MockSerialPortEvent implements SerialPortEvent {
        private final int eventType;

        private final boolean newValue;

        public MockSerialPortEvent(SerialPort mockSerialPort, int eventType, boolean oldValue, boolean newValue) {
            this.eventType = eventType;
            this.newValue = newValue;
        }

        @Override
        public int getEventType() {
            return eventType;
        }

        @Override
        public boolean getNewValue() {
            return newValue;
        }
    }
}

