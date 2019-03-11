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


import com.google.common.collect.ImmutableMap;
import com.googlecode.jmxtrans.model.Query;
import com.googlecode.jmxtrans.model.QueryFixtures;
import com.googlecode.jmxtrans.model.Result;
import com.googlecode.jmxtrans.model.ResultFixtures;
import com.googlecode.jmxtrans.model.ServerFixtures;
import com.googlecode.jmxtrans.test.RequiresIO;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;


/**
 * Tests for {@link OpenTSDBWriter}.
 */
@Category(RequiresIO.class)
public class OpenTSDBWriterTests {
    protected OpenTSDBWriter writer;

    protected Query mockQuery;

    protected Result mockResult;

    protected Socket mockSocket;

    protected DataOutputStream mockOut;

    protected InputStreamReader mockInStreamRdr;

    protected BufferedReader mockBufRdr;

    protected Logger mockLog;

    protected ImmutableMap<String, Object> testValues;

    @Test(expected = NullPointerException.class)
    public void exceptionThrownIfHostIsNotDefined() throws Exception {
        OpenTSDBWriter.builder().setPort(4242).build();
    }

    @Test(expected = NullPointerException.class)
    public void exceptionThrownIfPortIsNotDefined() throws Exception {
        OpenTSDBWriter.builder().setHost("localhost").build();
    }

    @Test
    public void socketInvalidatedWhenError() throws Exception {
        GenericKeyedObjectPool<InetSocketAddress, Socket> pool = Mockito.mock(GenericKeyedObjectPool.class);
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(pool.borrowObject(Matchers.any(InetSocketAddress.class))).thenReturn(socket);
        OpenTSDBWriterTests.UnflushableByteArrayOutputStream out = new OpenTSDBWriterTests.UnflushableByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(out);
        OpenTSDBWriter writer = OpenTSDBWriter.builder().setHost("localhost").setPort(4243).build();
        writer.setPool(pool);
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
        Mockito.verify(pool).invalidateObject(Matchers.any(InetSocketAddress.class), Matchers.eq(socket));
        Mockito.verify(pool, Mockito.never()).returnObject(Matchers.any(InetSocketAddress.class), Matchers.eq(socket));
    }

    @Test
    public void writeSingleResult() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OpenTSDBWriter writer = OpenTSDBWriterTests.getOpenTSDBWriter(out);
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.singleNumericResult());
        // check that OpenTSDB format is respected
        // hostname is added by default
        assertThat(out.toString()).startsWith("put MemoryAlias.ObjectPendingFinalizationCount ").contains("host=").endsWith("\n");
    }

    private static class UnflushableByteArrayOutputStream extends ByteArrayOutputStream {
        @Override
        public void flush() throws IOException {
            throw new IOException();
        }
    }
}

