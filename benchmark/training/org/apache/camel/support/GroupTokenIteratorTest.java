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
package org.apache.camel.support;


import Exchange.CHARSET_NAME;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.TestSupport;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.Scanner;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class GroupTokenIteratorTest extends TestSupport {
    private CamelContext context;

    private Exchange exchange;

    @Test
    public void testGroupIterator() throws Exception {
        String s = "ABC\nDEF\nGHI\nJKL\nMNO\nPQR\nSTU\nVW";
        Scanner scanner = new Scanner(s, "\n");
        GroupTokenIterator gi = new GroupTokenIterator(exchange, scanner, "\n", 3, false);
        Assert.assertTrue(gi.hasNext());
        Assert.assertEquals("ABC\nDEF\nGHI", gi.next());
        Assert.assertEquals("JKL\nMNO\nPQR", gi.next());
        Assert.assertEquals("STU\nVW", gi.next());
        Assert.assertFalse(gi.hasNext());
        IOHelper.close(gi);
    }

    @Test
    public void testGroupIteratorSkipFirst() throws Exception {
        String s = "##comment\nABC\nDEF\nGHI\nJKL\nMNO\nPQR\nSTU\nVW";
        Scanner scanner = new Scanner(s, "\n");
        GroupTokenIterator gi = new GroupTokenIterator(exchange, scanner, "\n", 3, true);
        Assert.assertTrue(gi.hasNext());
        Assert.assertEquals("ABC\nDEF\nGHI", gi.next());
        Assert.assertEquals("JKL\nMNO\nPQR", gi.next());
        Assert.assertEquals("STU\nVW", gi.next());
        Assert.assertFalse(gi.hasNext());
        IOHelper.close(gi);
    }

    @Test
    public void testGroupIteratorWithDifferentEncodingFromDefault() throws Exception {
        if ((Charset.defaultCharset()) == (StandardCharsets.UTF_8)) {
            // can't think of test case where having default charset set to UTF-8 is affected
            return;
        }
        byte[] buf = "\u00a31\n\u00a32\n".getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream in = new ByteArrayInputStream(buf);
        Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.displayName(), "\n");
        exchange.setProperty(CHARSET_NAME, StandardCharsets.UTF_8.displayName());
        GroupTokenIterator gi = new GroupTokenIterator(exchange, scanner, "\n", 1, false);
        Assert.assertTrue(gi.hasNext());
        Assert.assertEquals("\u00a31", gi.next());
        Assert.assertEquals("\u00a32", gi.next());
        Assert.assertFalse(gi.hasNext());
        IOHelper.close(gi);
    }
}

