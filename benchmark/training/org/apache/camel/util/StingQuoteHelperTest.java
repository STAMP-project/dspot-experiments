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
package org.apache.camel.util;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class StingQuoteHelperTest extends Assert {
    @Test
    public void testSplitSafeQuote() throws Exception {
        Assert.assertEquals(null, StringQuoteHelper.splitSafeQuote(null, ','));
        String[] out = StringQuoteHelper.splitSafeQuote("", ',');
        Assert.assertEquals(1, out.length);
        Assert.assertEquals("", out[0]);
        out = StringQuoteHelper.splitSafeQuote("   ", ',');
        Assert.assertEquals(1, out.length);
        Assert.assertEquals("", out[0]);
        out = StringQuoteHelper.splitSafeQuote("   ", ',', false);
        Assert.assertEquals(1, out.length);
        Assert.assertEquals("   ", out[0]);
        out = StringQuoteHelper.splitSafeQuote("Camel", ',');
        Assert.assertEquals(1, out.length);
        Assert.assertEquals("Camel", out[0]);
        out = StringQuoteHelper.splitSafeQuote("Hello Camel", ',');
        Assert.assertEquals(1, out.length);
        Assert.assertEquals("Hello Camel", out[0]);
        out = StringQuoteHelper.splitSafeQuote("Hello,Camel", ',');
        Assert.assertEquals(2, out.length);
        Assert.assertEquals("Hello", out[0]);
        Assert.assertEquals("Camel", out[1]);
        out = StringQuoteHelper.splitSafeQuote("Hello,Camel,Bye,World", ',');
        Assert.assertEquals(4, out.length);
        Assert.assertEquals("Hello", out[0]);
        Assert.assertEquals("Camel", out[1]);
        Assert.assertEquals("Bye", out[2]);
        Assert.assertEquals("World", out[3]);
        out = StringQuoteHelper.splitSafeQuote("'Hello,Camel','Bye,World'", ',');
        Assert.assertEquals(2, out.length);
        Assert.assertEquals("Hello,Camel", out[0]);
        Assert.assertEquals("Bye,World", out[1]);
        out = StringQuoteHelper.splitSafeQuote("\'Hello,Camel\',\"Bye,World\"", ',');
        Assert.assertEquals(2, out.length);
        Assert.assertEquals("Hello,Camel", out[0]);
        Assert.assertEquals("Bye,World", out[1]);
        out = StringQuoteHelper.splitSafeQuote("\"Hello,Camel\",\'Bye,World\'", ',');
        Assert.assertEquals(2, out.length);
        Assert.assertEquals("Hello,Camel", out[0]);
        Assert.assertEquals("Bye,World", out[1]);
        out = StringQuoteHelper.splitSafeQuote("\"Hello,Camel\",\"Bye,World\"", ',');
        Assert.assertEquals(2, out.length);
        Assert.assertEquals("Hello,Camel", out[0]);
        Assert.assertEquals("Bye,World", out[1]);
        out = StringQuoteHelper.splitSafeQuote("'Hello Camel', 'Bye World'", ',');
        Assert.assertEquals(2, out.length);
        Assert.assertEquals("Hello Camel", out[0]);
        Assert.assertEquals("Bye World", out[1]);
        out = StringQuoteHelper.splitSafeQuote("'Hello Camel', ' Bye World'", ',', false);
        Assert.assertEquals(2, out.length);
        Assert.assertEquals("Hello Camel", out[0]);
        Assert.assertEquals(" Bye World", out[1]);
        out = StringQuoteHelper.splitSafeQuote("'http:', ' '", ',', false);
        Assert.assertEquals(2, out.length);
        Assert.assertEquals("http:", out[0]);
        Assert.assertEquals(" ", out[1]);
        out = StringQuoteHelper.splitSafeQuote("'http:', ''", ',', false);
        Assert.assertEquals(2, out.length);
        Assert.assertEquals("http:", out[0]);
        Assert.assertEquals("", out[1]);
        out = StringQuoteHelper.splitSafeQuote("'Hello Camel', 5, true", ',', false);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("Hello Camel", out[0]);
        Assert.assertEquals("5", out[1]);
        Assert.assertEquals("true", out[2]);
        out = StringQuoteHelper.splitSafeQuote("'Hello Camel',5,true", ',', false);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("Hello Camel", out[0]);
        Assert.assertEquals("5", out[1]);
        Assert.assertEquals("true", out[2]);
        out = StringQuoteHelper.splitSafeQuote("   'Hello Camel',  5   ,  true   ", ',', false);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("Hello Camel", out[0]);
        Assert.assertEquals("5", out[1]);
        Assert.assertEquals("true", out[2]);
        out = StringQuoteHelper.splitSafeQuote("*, '', 'arg3'", ',', false);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("*", out[0]);
        Assert.assertEquals("", out[1]);
        Assert.assertEquals("arg3", out[2]);
    }

    @Test
    public void testLastIsQuote() throws Exception {
        String[] out = StringQuoteHelper.splitSafeQuote(" ${body}, 5, 'Hello World'", ',', true);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("${body}", out[0]);
        Assert.assertEquals("5", out[1]);
        Assert.assertEquals("Hello World", out[2]);
        out = StringQuoteHelper.splitSafeQuote(" ${body}, 5, \"Hello World\"", ',', true);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("${body}", out[0]);
        Assert.assertEquals("5", out[1]);
        Assert.assertEquals("Hello World", out[2]);
    }

    @Test
    public void testSingleInDoubleQuote() throws Exception {
        String[] out = StringQuoteHelper.splitSafeQuote("\"Hello O\'Connor\", 5, \'foo bar\'", ',', true);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("Hello O'Connor", out[0]);
        Assert.assertEquals("5", out[1]);
        Assert.assertEquals("foo bar", out[2]);
        out = StringQuoteHelper.splitSafeQuote("\"Hello O\'Connor O\'Bannon\", 5, \'foo bar\'", ',', true);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("Hello O'Connor O'Bannon", out[0]);
        Assert.assertEquals("5", out[1]);
        Assert.assertEquals("foo bar", out[2]);
    }

    @Test
    public void testDoubleInSingleQuote() throws Exception {
        String[] out = StringQuoteHelper.splitSafeQuote("\'Hello O\"Connor\', 5, \'foo bar\'", ',', true);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("Hello O\"Connor", out[0]);
        Assert.assertEquals("5", out[1]);
        Assert.assertEquals("foo bar", out[2]);
        out = StringQuoteHelper.splitSafeQuote("\'Hello O\"Connor O\"Bannon\', 5, \'foo bar\'", ',', true);
        Assert.assertEquals(3, out.length);
        Assert.assertEquals("Hello O\"Connor O\"Bannon", out[0]);
        Assert.assertEquals("5", out[1]);
        Assert.assertEquals("foo bar", out[2]);
    }
}

