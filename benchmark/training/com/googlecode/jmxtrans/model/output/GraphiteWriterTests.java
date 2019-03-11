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
import com.googlecode.jmxtrans.model.Query;
import com.googlecode.jmxtrans.model.QueryFixtures;
import com.googlecode.jmxtrans.model.Result;
import com.googlecode.jmxtrans.model.ResultFixtures;
import com.googlecode.jmxtrans.model.Server;
import com.googlecode.jmxtrans.model.ServerFixtures;
import com.googlecode.jmxtrans.model.ValidationException;
import com.googlecode.jmxtrans.test.RequiresIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


@Category(RequiresIO.class)
public class GraphiteWriterTests {
    @Test(expected = NullPointerException.class)
    public void hostIsRequired() throws ValidationException {
        try {
            GraphiteWriter.builder().setPort(123).build();
        } catch (NullPointerException npe) {
            assertThat(npe).hasMessage("Host cannot be null.");
            throw npe;
        }
    }

    @Test(expected = NullPointerException.class)
    public void portIsRequired() throws ValidationException {
        try {
            GraphiteWriter.builder().setHost("localhost").build();
        } catch (NullPointerException npe) {
            assertThat(npe).hasMessage("Port cannot be null.");
            throw npe;
        }
    }

    @Test
    public void writeSingleResult() throws Exception {
        // check that Graphite format is respected
        assertThat(GraphiteWriterTests.getOutput(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.numericResult())).startsWith("servers.host_example_net_4321.MemoryAlias.ObjectPendingFinalizationCount 10");
    }

    @Test
    public void useObjDomainWorks() throws Exception {
        // check that Graphite format is respected
        assertThat(GraphiteWriterTests.getOutput(ServerFixtures.dummyServer(), QueryFixtures.queryUsingDomainAsKey(), ResultFixtures.numericResult())).startsWith("servers.host_example_net_4321.MemoryAlias.ObjectPendingFinalizationCount 10 0");
    }

    @Test
    public void allowDottedWorks() throws Exception {
        // check that Graphite format is respected
        assertThat(GraphiteWriterTests.getOutput(ServerFixtures.dummyServer(), QueryFixtures.queryAllowingDottedKeys(), ResultFixtures.numericResult())).startsWith("servers.host_example_net_4321.MemoryAlias.ObjectPendingFinalizationCount 10 0");
    }

    @Test
    public void stringNumericValue() throws Exception {
        // check that Graphite format is respected
        assertThat(GraphiteWriterTests.getOutput(ServerFixtures.dummyServer(), QueryFixtures.queryAllowingDottedKeys(), ResultFixtures.stringResult("10"))).startsWith("servers.host_example_net_4321.MemoryAlias.NonHeapMemoryUsage.ObjectPendingFinalizationCount 10 0");
    }

    @Test
    public void invalidNumbersFiltered() throws Exception {
        assertThat(GraphiteWriterTests.getOutput(ServerFixtures.dummyServer(), QueryFixtures.queryAllowingDottedKeys(), ResultFixtures.numericResult(Double.NEGATIVE_INFINITY))).isEmpty();
        assertThat(GraphiteWriterTests.getOutput(ServerFixtures.dummyServer(), QueryFixtures.queryAllowingDottedKeys(), ResultFixtures.stringResult(String.valueOf(Double.NEGATIVE_INFINITY)))).isEmpty();
    }

    @Test
    public void useAllTypeNamesWorks() throws Exception {
        // Set useAllTypeNames to true
        String typeName = "typeName,typeNameKey1=typeNameValue1,typeNameKey2=typeNameValue2";
        String typeNameReordered = "typeNameKey2=typeNameValue2,typeName,typeNameKey1=typeNameValue1";
        // check that Graphite format is respected
        assertThat(GraphiteWriterTests.getOutput(ServerFixtures.dummyServer(), QueryFixtures.queryWithAllTypeNames(), ResultFixtures.numericResultWithTypenames(typeName))).startsWith("servers.host_example_net_4321.ObjectPendingFinalizationCount.typeNameValue1_typeNameValue2.ObjectPendingFinalizationCount 10 0");
        assertThat(GraphiteWriterTests.getOutput(ServerFixtures.dummyServer(), QueryFixtures.queryWithAllTypeNames(), ResultFixtures.numericResultWithTypenames(typeNameReordered))).startsWith("servers.host_example_net_4321.ObjectPendingFinalizationCount.typeNameValue2_typeNameValue1.ObjectPendingFinalizationCount 10 0");
    }

    @Test
    public void booleanAsNumberWorks() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GenericKeyedObjectPool<InetSocketAddress, Socket> pool = Mockito.mock(GenericKeyedObjectPool.class);
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(pool.borrowObject(Matchers.any(InetSocketAddress.class))).thenReturn(socket);
        Mockito.when(socket.getOutputStream()).thenReturn(out);
        GraphiteWriter writer = GraphiteWriter.builder().setHost("localhost").setPort(123).setBooleanAsNumber(true).build();
        writer.setPool(pool);
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.singleTrueResult());
        // check that the booleanAsNumber property was picked up from the JSON
        assertThat(out.toString()).startsWith("servers.host_example_net_4321.VerboseMemory.Verbose 1 0");
    }

    @Test
    public void checkEmptyTypeNamesAreIgnored() throws Exception {
        Server server = ServerFixtures.serverWithNoQuery();
        // Set useObjDomain to true
        Query query = Query.builder().setUseObjDomainAsKey(true).setAllowDottedKeys(true).setObj("\"yammer.metrics\":name=\"uniqueName\",type=\"\"").build();
        Result result = new Result(System.currentTimeMillis(), "Attribute", "com.yammer.metrics.reporting.JmxReporter$Counter", "yammer.metrics", null, "name=\"uniqueName\",type=\"\"", ImmutableList.<String>of(), 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArrayList<String> typeNames = new ArrayList<String>();
        typeNames.add("name");
        typeNames.add("type");
        GraphiteWriter writer = GraphiteWriterTests.getGraphiteWriter(out, typeNames);
        writer.doWrite(server, query, ImmutableList.of(result));
        // check that the empty type "type" is ignored when allowDottedKeys is true
        assertThat(out.toString()).startsWith("servers.host_example_net_4321.yammer.metrics.uniqueName.Attribute 0 ");
        // check that this also works when literal " characters aren't included in the JMX ObjectName
        query = Query.builder().setUseObjDomainAsKey(true).setAllowDottedKeys(true).setObj("yammer.metrics:name=uniqueName,type=").build();
        out = new ByteArrayOutputStream();
        writer = GraphiteWriterTests.getGraphiteWriter(out, typeNames);
        writer.doWrite(server, query, ImmutableList.of(result));
        assertThat(out.toString()).startsWith("servers.host_example_net_4321.yammer.metrics.uniqueName.Attribute 0 ");
        // check that the empty type "type" is ignored when allowDottedKeys is false
        query = Query.builder().setUseObjDomainAsKey(true).setAllowDottedKeys(false).setObj("\"yammer.metrics\":name=\"uniqueName\",type=\"\"").build();
        out = new ByteArrayOutputStream();
        writer = GraphiteWriterTests.getGraphiteWriter(out, typeNames);
        writer.doWrite(server, query, ImmutableList.of(result));
        assertThat(out.toString()).startsWith("servers.host_example_net_4321.yammer_metrics.uniqueName.Attribute 0 ");
    }

    @Test
    public void socketInvalidatedWhenError() throws Exception {
        GenericKeyedObjectPool<InetSocketAddress, Socket> pool = Mockito.mock(GenericKeyedObjectPool.class);
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(pool.borrowObject(Matchers.any(InetSocketAddress.class))).thenReturn(socket);
        GraphiteWriterTests.UnflushableByteArrayOutputStream out = new GraphiteWriterTests.UnflushableByteArrayOutputStream();
        Mockito.when(socket.getOutputStream()).thenReturn(out);
        GraphiteWriter writer = GraphiteWriter.builder().setHost("localhost").setPort(2003).build();
        writer.setPool(pool);
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
        Mockito.verify(pool).invalidateObject(Matchers.any(InetSocketAddress.class), Matchers.eq(socket));
        Mockito.verify(pool, Mockito.never()).returnObject(Matchers.any(InetSocketAddress.class), Matchers.eq(socket));
    }

    private static class UnflushableByteArrayOutputStream extends ByteArrayOutputStream {
        @Override
        public void flush() throws IOException {
            throw new IOException();
        }
    }
}

