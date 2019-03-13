/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfCommandOk;
import org.openhab.binding.lightwaverf.internal.command.LightwaveRfSetHeatingTemperatureCommand;


public class LightwaveRFSenderThreadTest {
    @Mock
    DatagramSocket mockSocket;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Test
    public void test() throws Exception {
        LightwaveRfSetHeatingTemperatureCommand tempCommand = new LightwaveRfSetHeatingTemperatureCommand("768,!R3DhF*tP18.0");
        final LightwaveRfCommandOk okCommand = new LightwaveRfCommandOk("768,OK");
        final LightwaveRFSenderThread senderThread = new LightwaveRFSenderThread(mockSocket, "192.168.1.1", 8000, 120000);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                senderThread.okMessageReceived(okCommand);
            }
        }, 1000, TimeUnit.MILLISECONDS);
        senderThread.sendLightwaveCommand(tempCommand);
        senderThread.run();
        Mockito.verify(mockSocket, Mockito.times(1)).send(ArgumentMatchers.any(DatagramPacket.class));
    }
}

