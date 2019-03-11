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
import com.googlecode.jmxtrans.model.QueryFixtures;
import com.googlecode.jmxtrans.model.ResultFixtures;
import com.googlecode.jmxtrans.model.ServerFixtures;
import com.googlecode.jmxtrans.model.output.support.WriterBasedOutputWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;


public class GraphiteWriter2Test {
    @Test
    public void correctFormatIsSentToGraphite() throws IOException {
        WriterBasedOutputWriter outputWriter = new GraphiteWriter2(ImmutableList.<String>of(), "servers");
        StringWriter writer = new StringWriter();
        outputWriter.write(writer, ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
        String graphiteLine = writer.toString();
        assertThat(graphiteLine).hasLineCount(1).startsWith("servers").contains("example_net_4321").endsWith(" 10 0\n");
    }

    @Test
    public void invalidNumbersFiltered() throws Exception {
        WriterBasedOutputWriter outputWriter = new GraphiteWriter2(ImmutableList.<String>of(), "servers");
        StringWriter writer = new StringWriter();
        outputWriter.write(writer, ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ImmutableList.of(ResultFixtures.numericResult(Double.NEGATIVE_INFINITY)));
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    public void invalidNumericStringFiltered() throws Exception {
        WriterBasedOutputWriter outputWriter = new GraphiteWriter2(ImmutableList.<String>of(), "servers");
        StringWriter writer = new StringWriter();
        outputWriter.write(writer, ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ImmutableList.of(ResultFixtures.stringResult(String.valueOf(Double.NEGATIVE_INFINITY))));
        assertThat(writer.toString()).isEmpty();
    }

    @Test
    public void stringNumericValue() throws Exception {
        WriterBasedOutputWriter outputWriter = new GraphiteWriter2(ImmutableList.<String>of(), "servers");
        StringWriter writer = new StringWriter();
        outputWriter.write(writer, ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ImmutableList.of(ResultFixtures.stringResult("10")));
        assertThat(writer.toString()).startsWith("servers.host_example_net_4321.MemoryAlias.NonHeapMemoryUsage_ObjectPendingFinalizationCount 10 0");
    }
}

