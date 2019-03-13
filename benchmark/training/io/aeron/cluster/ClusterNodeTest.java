/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.cluster;


import io.aeron.cluster.client.AeronCluster;
import io.aeron.cluster.client.EgressListener;
import io.aeron.cluster.service.ClusteredServiceContainer;
import org.agrona.ExpandableArrayBuffer;
import org.agrona.collections.MutableInteger;
import org.junit.Assert;
import org.junit.Test;


public class ClusterNodeTest {
    private static final long MAX_CATALOG_ENTRIES = 1024;

    private ClusteredMediaDriver clusteredMediaDriver;

    private ClusteredServiceContainer container;

    private AeronCluster aeronCluster;

    @Test
    public void shouldConnectAndSendKeepAlive() {
        container = launchEchoService();
        aeronCluster = connectToCluster(null);
        Assert.assertTrue(aeronCluster.sendKeepAlive());
    }

    @Test(timeout = 10000)
    public void shouldEchoMessageViaService() {
        final ExpandableArrayBuffer msgBuffer = new ExpandableArrayBuffer();
        final String msg = "Hello World!";
        msgBuffer.putStringWithoutLengthAscii(0, msg);
        final MutableInteger messageCount = new MutableInteger();
        final EgressListener listener = ( clusterSessionId, timestamp, buffer, offset, length, header) -> {
            assertThat(buffer.getStringWithoutLengthAscii(offset, length), is(msg));
            messageCount.value += 1;
        };
        container = launchEchoService();
        aeronCluster = connectToCluster(listener);
        while ((aeronCluster.offer(msgBuffer, 0, msg.length())) < 0) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        while ((messageCount.get()) == 0) {
            if ((aeronCluster.pollEgress()) <= 0) {
                TestUtil.checkInterruptedStatus();
                Thread.yield();
            }
        } 
    }

    @Test(timeout = 10000)
    public void shouldScheduleEventInService() {
        final ExpandableArrayBuffer msgBuffer = new ExpandableArrayBuffer();
        final String msg = "Hello World!";
        msgBuffer.putStringWithoutLengthAscii(0, msg);
        final MutableInteger messageCount = new MutableInteger();
        final EgressListener listener = ( clusterSessionId, timestamp, buffer, offset, length, header) -> {
            assertThat(buffer.getStringWithoutLengthAscii(offset, length), is((msg + "-scheduled")));
            messageCount.value += 1;
        };
        container = launchTimedService();
        aeronCluster = connectToCluster(listener);
        while ((aeronCluster.offer(msgBuffer, 0, msg.length())) < 0) {
            TestUtil.checkInterruptedStatus();
            Thread.yield();
        } 
        while ((messageCount.get()) == 0) {
            if ((aeronCluster.pollEgress()) <= 0) {
                TestUtil.checkInterruptedStatus();
                Thread.yield();
            }
        } 
    }
}

