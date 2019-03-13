/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.io;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link PrintingOutputFormat}.
 */
public class PrintingOutputFormatTest {
    private final PrintStream originalSystemOut = System.out;

    private final PrintStream originalSystemErr = System.err;

    private final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

    private final ByteArrayOutputStream arrayErrorStream = new ByteArrayOutputStream();

    private final String line = System.lineSeparator();

    @Test
    public void testPrintOutputFormatStdOut() throws Exception {
        PrintingOutputFormat<String> printSink = new PrintingOutputFormat();
        printSink.open(0, 1);
        printSink.writeRecord("hello world!");
        Assert.assertEquals("Print to System.out", printSink.toString());
        Assert.assertEquals(("hello world!" + (line)), arrayOutputStream.toString());
        printSink.close();
    }

    @Test
    public void testPrintOutputFormatStdErr() throws Exception {
        PrintingOutputFormat<String> printSink = new PrintingOutputFormat(true);
        printSink.open(0, 1);
        printSink.writeRecord("hello world!");
        Assert.assertEquals("Print to System.err", printSink.toString());
        Assert.assertEquals(("hello world!" + (line)), arrayErrorStream.toString());
        printSink.close();
    }

    @Test
    public void testPrintOutputFormatWithPrefix() throws Exception {
        PrintingOutputFormat<String> printSink = new PrintingOutputFormat();
        printSink.open(1, 2);
        printSink.writeRecord("hello world!");
        Assert.assertEquals("Print to System.out", printSink.toString());
        Assert.assertEquals(("2> hello world!" + (line)), arrayOutputStream.toString());
        printSink.close();
    }

    @Test
    public void testPrintOutputFormatWithIdentifierAndPrefix() throws Exception {
        PrintingOutputFormat<String> printSink = new PrintingOutputFormat("mySink", false);
        printSink.open(1, 2);
        printSink.writeRecord("hello world!");
        Assert.assertEquals("Print to System.out", printSink.toString());
        Assert.assertEquals(("mySink:2> hello world!" + (line)), arrayOutputStream.toString());
        printSink.close();
    }

    @Test
    public void testPrintOutputFormatWithIdentifierButNoPrefix() throws Exception {
        PrintingOutputFormat<String> printSink = new PrintingOutputFormat("mySink", false);
        printSink.open(0, 1);
        printSink.writeRecord("hello world!");
        Assert.assertEquals("Print to System.out", printSink.toString());
        Assert.assertEquals(("mySink> hello world!" + (line)), arrayOutputStream.toString());
        printSink.close();
    }
}

