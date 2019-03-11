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
import com.googlecode.jmxtrans.model.OutputWriter;
import com.googlecode.jmxtrans.model.QueryFixtures;
import com.googlecode.jmxtrans.model.ResultFixtures;
import com.googlecode.jmxtrans.model.ServerFixtures;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StdOutWriterTest {
    private PrintStream originalStdOut;

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1000);

    private PrintStream dummyStdOut = new PrintStream(byteArrayOutputStream);

    @Test
    public void defaultConfig() throws Exception {
        StdOutWriter writerFactory = new StdOutWriter(ImmutableList.<String>of(), false, false, null, Collections.<String, Object>emptyMap());
        OutputWriter writer = writerFactory.create();
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
        assertThat(byteArrayOutputStream.toString()).contains("Result(");
        assertThat(byteArrayOutputStream.toString()).contains("typeName=type=Memory");
    }

    @Test
    public void skipResult() throws Exception {
        StdOutWriter writerFactory = new StdOutWriter(ImmutableList.<String>of(), false, false, Mockito.mock(ResultSerializer.class), Collections.<String, Object>emptyMap());
        OutputWriter writer = writerFactory.create();
        writer.doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
        assertThat(byteArrayOutputStream.toString()).doesNotContain("Result(");
        assertThat(byteArrayOutputStream.toString()).doesNotContain("typeName=type=Memory");
    }
}

