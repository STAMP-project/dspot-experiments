/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.model.output;


import com.google.common.collect.ImmutableList;
import com.googlecode.jmxtrans.exceptions.LifecycleException;
import com.googlecode.jmxtrans.model.Query;
import com.googlecode.jmxtrans.model.Result;
import com.googlecode.jmxtrans.model.ServerFixtures;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;


/**
 * Tests for {@link TCollectorUDPWriter}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ TCollectorUDPWriter.class, DatagramSocket.class })
public class TCollectorUDPWriterTests {
    protected TCollectorUDPWriter writer;

    protected Query mockQuery;

    protected Result mockResult;

    protected DatagramSocket mockDgSocket;

    protected Logger mockLog;

    @Test
    public void successfullySendMessageToTCollector() throws Exception {
        // Prepare
        ArgumentCaptor<DatagramPacket> packetCapture = ArgumentCaptor.forClass(DatagramPacket.class);
        // Execute
        this.writer.start();
        this.writer.doWrite(ServerFixtures.dummyServer(), this.mockQuery, ImmutableList.of(this.mockResult));
        this.writer.close();
        // Verifications
        Mockito.verify(this.mockDgSocket).send(packetCapture.capture());
        String sentString = new String(packetCapture.getValue().getData(), packetCapture.getValue().getOffset(), packetCapture.getValue().getLength());
        Assert.assertThat(sentString, Matchers.startsWith("X-DOMAIN.PKG.CLASS-X.X-ATT-X 0 120021"));
        Assert.assertThat(sentString, Matchers.not(Matchers.containsString("host=")));
    }

    /**
     * Test a socket exception when creating the DatagramSocket.
     */
    @Test
    public void testSocketException() throws Exception {
        // Prepare
        SocketException sockExc = new SocketException("X-SOCK-EXC-X");
        PowerMockito.whenNew(DatagramSocket.class).withNoArguments().thenThrow(sockExc);
        try {
            // Execute
            this.writer.start();
            Assert.fail("LifecycleException missing");
        } catch (LifecycleException lcExc) {
            // Verify
            Assert.assertSame(sockExc, lcExc.getCause());
            Mockito.verify(this.mockLog).error(ArgumentMatchers.contains("create a datagram socket"), ArgumentMatchers.eq(sockExc));
        }
    }

    @Test(expected = NullPointerException.class)
    public void exceptionIsThrownWhenHostIsNotDefined() throws Exception {
        TCollectorUDPWriter.builder().setPort(1234).build();
    }

    @Test(expected = NullPointerException.class)
    public void exceptionIsThrownWhenPortIsNotDefined() throws Exception {
        TCollectorUDPWriter.builder().setHost("localhost").build();
    }
}

