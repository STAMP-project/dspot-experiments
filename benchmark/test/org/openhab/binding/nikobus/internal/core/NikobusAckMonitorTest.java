/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.nikobus.internal.core;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhab.binding.nikobus.internal.NikobusBinding;


/**
 *
 *
 * @author Davy Vanherbergen
 * @since 1.3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class NikobusAckMonitorTest {
    @Mock
    private NikobusCommandReceiver receiver;

    @Mock
    private NikobusBinding binding;

    private NikobusCommandSender sender;

    @Test
    public void canDetectAck() {
        sender = new NikobusCommandSender(null) {
            @Override
            public void sendCommand(NikobusCommand cmd) {
                cmd.incrementSentCount();
            }
        };
        testForAck("mytest", "ADFADFMYTE", false);
        testForAck("mytest", "MYTESTTT", true);
        testForAck("mytest", "ADFADFMYTE", false);
        testForAck("mytest1", "MYTEST1", true);
    }
}

