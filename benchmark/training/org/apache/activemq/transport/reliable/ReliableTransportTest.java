/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.reliable;


import junit.framework.TestCase;
import org.apache.activemq.transport.StubTransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class ReliableTransportTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(ReliableTransportTest.class);

    protected ReliableTransport transport;

    protected StubTransportListener listener = new StubTransportListener();

    protected ReplayStrategy replayStrategy;

    public void testValidSequenceOfPackets() throws Exception {
        int[] sequenceNumbers = new int[]{ 1, 2, 3, 4, 5, 6, 7 };
        sendStreamOfCommands(sequenceNumbers, true);
    }

    public void testValidWrapAroundPackets() throws Exception {
        int[] sequenceNumbers = new int[10];
        int value = (Integer.MAX_VALUE) - 3;
        transport.setExpectedCounter(value);
        for (int i = 0; i < 10; i++) {
            ReliableTransportTest.LOG.info(((("command: " + i) + " = ") + value));
            sequenceNumbers[i] = value++;
        }
        sendStreamOfCommands(sequenceNumbers, true);
    }

    public void testDuplicatePacketsDropped() throws Exception {
        int[] sequenceNumbers = new int[]{ 1, 2, 2, 3, 4, 5, 6, 7 };
        sendStreamOfCommands(sequenceNumbers, true, 7);
    }

    public void testOldDuplicatePacketsDropped() throws Exception {
        int[] sequenceNumbers = new int[]{ 1, 2, 3, 4, 5, 2, 6, 7 };
        sendStreamOfCommands(sequenceNumbers, true, 7);
    }

    public void testOldDuplicatePacketsDroppedUsingNegativeCounters() throws Exception {
        int[] sequenceNumbers = new int[]{ -3, -1, -3, -2, -1, 0, 1, -1, 3, 2, 0, 2, 4 };
        transport.setExpectedCounter((-3));
        sendStreamOfCommands(sequenceNumbers, true, 8);
    }

    public void testWrongOrderOfPackets() throws Exception {
        int[] sequenceNumbers = new int[]{ 4, 3, 1, 5, 2, 7, 6, 8, 10, 9 };
        sendStreamOfCommands(sequenceNumbers, true);
    }

    public void testMissingPacketsFails() throws Exception {
        int[] sequenceNumbers = new int[]{ 1, 2, 4, 5, 6, 7, 8, 9, 10 };
        sendStreamOfCommands(sequenceNumbers, false);
    }
}

