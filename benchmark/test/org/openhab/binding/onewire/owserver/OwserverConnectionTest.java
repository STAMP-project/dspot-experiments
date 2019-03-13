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
package org.openhab.binding.onewire.owserver;


import OnOffType.OFF;
import OnOffType.ON;
import OwserverConnectionState.FAILED;
import OwserverConnectionState.OPENED;
import java.util.List;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.test.java.JavaTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.onewire.internal.OwException;
import org.openhab.binding.onewire.internal.OwPageBuffer;
import org.openhab.binding.onewire.internal.SensorId;
import org.openhab.binding.onewire.internal.handler.OwserverBridgeHandler;
import org.openhab.binding.onewire.internal.owserver.OwserverConnection;
import org.openhab.binding.onewire.test.OwserverTestServer;


/**
 * Tests cases for {@link OwserverConnection}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class OwserverConnectionTest extends JavaTest {
    private final String TEST_HOST = "127.0.0.1";

    OwserverTestServer testServer;

    OwserverConnection owserverConnection;

    @Mock
    OwserverBridgeHandler bridgeHandler;

    private int testPort;

    @Test
    public void successfullConnectionReportedToBridgeHandler() {
        owserverConnection.start();
        Mockito.verify(bridgeHandler).reportConnectionState(OPENED);
    }

    @Test
    public void failedConnectionReportedToBridgeHandler() {
        owserverConnection.setPort(1);
        owserverConnection.start();
        Mockito.verify(bridgeHandler, Mockito.timeout(100)).reportConnectionState(FAILED);
    }

    @Test
    public void testGetDirectory() {
        owserverConnection.start();
        try {
            List<SensorId> directory = owserverConnection.getDirectory("/");
            Assert.assertEquals(3, directory.size());
            Assert.assertEquals(new SensorId("/00.0123456789ab"), directory.get(0));
            Assert.assertEquals(new SensorId("/00.0123456789ac"), directory.get(1));
            Assert.assertEquals(new SensorId("/00.0123456789ad"), directory.get(2));
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void testCheckPresence() {
        owserverConnection.start();
        try {
            State presence = owserverConnection.checkPresence("present");
            Assert.assertEquals(ON, presence);
            presence = owserverConnection.checkPresence("notpresent");
            Assert.assertEquals(OFF, presence);
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void testReadDecimalType() {
        owserverConnection.start();
        try {
            DecimalType number = ((DecimalType) (owserverConnection.readDecimalType("testsensor/decimal")));
            Assert.assertEquals(17.4, number.doubleValue(), 0.01);
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void testReadDecimalTypeArray() {
        owserverConnection.start();
        try {
            List<State> numbers = owserverConnection.readDecimalTypeArray("testsensor/decimalarray");
            Assert.assertEquals(3834, intValue());
            Assert.assertEquals(0, intValue());
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void testGetPages() {
        owserverConnection.start();
        try {
            OwPageBuffer pageBuffer = owserverConnection.readPages("testsensor");
            Assert.assertEquals(31, pageBuffer.getByte(5, 7));
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }

    @Test
    public void testWriteDecimalType() {
        owserverConnection.start();
        try {
            owserverConnection.writeDecimalType("testsensor/decimal", new DecimalType(2009));
            Mockito.verify(bridgeHandler, Mockito.never()).reportConnectionState(FAILED);
        } catch (OwException e) {
            Assert.fail("caught unexpected OwException");
        }
    }
}

