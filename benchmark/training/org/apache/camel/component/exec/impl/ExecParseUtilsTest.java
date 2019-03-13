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
package org.apache.camel.component.exec.impl;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ExecParseUtils}
 */
public class ExecParseUtilsTest {
    @Test
    public void testSingleQuoted() {
        Assert.assertTrue(ExecParseUtils.isSingleQuoted("\"c:\\program files\\test\""));
    }

    @Test
    public void testSingleQuoted2() {
        Assert.assertTrue(ExecParseUtils.isSingleQuoted("\"with space\""));
    }

    @Test
    public void testSingleQuotedNegative() {
        Assert.assertFalse(ExecParseUtils.isSingleQuoted("arg 0"));
    }

    @Test
    public void testSingleQuotedNegative2() {
        Assert.assertFalse(ExecParseUtils.isSingleQuoted("\" \" space not allowed between quotes \""));
    }

    @Test
    public void testSingleQuotedNegative3() {
        Assert.assertFalse(ExecParseUtils.isSingleQuoted("\"\"double quoted is not single quoted\"\""));
    }

    @Test
    public void testEmptySingleQuotedNegative() {
        Assert.assertFalse(ExecParseUtils.isSingleQuoted("\"\""));
    }

    @Test
    public void testEmptySingleQuotedNegative2() {
        Assert.assertFalse(ExecParseUtils.isSingleQuoted("\""));
    }

    @Test
    public void testDoubleQuoted() {
        Assert.assertTrue(ExecParseUtils.isDoubleQuoted("\"\"c:\\program files\\test\\\"\""));
    }

    @Test
    public void testEmptyDoubleQuotedNegative() {
        Assert.assertFalse(ExecParseUtils.isDoubleQuoted("\"\"\"\""));
    }

    @Test
    public void testWhiteSpaceSeparatedArgs() {
        List<String> args = ExecParseUtils.splitToWhiteSpaceSeparatedTokens("arg0 arg1 arg2");
        Assert.assertEquals("arg0", args.get(0));
        Assert.assertEquals("arg1", args.get(1));
        Assert.assertEquals("arg2", args.get(2));
    }

    @Test
    public void testWhiteSpaceQuoted() {
        List<String> args = ExecParseUtils.splitToWhiteSpaceSeparatedTokens("\"arg 0\"");
        Assert.assertEquals("arg 0", args.get(0));
    }

    @Test
    public void testTwoQuotings() {
        List<String> args = ExecParseUtils.splitToWhiteSpaceSeparatedTokens("\"arg 0\" \"arg 1\"");
        Assert.assertEquals("arg 0", args.get(0));
        Assert.assertEquals("arg 1", args.get(1));
    }

    @Test
    public void testWhitespaceSeparatedArgsWithSpaces() {
        List<String> args = ExecParseUtils.splitToWhiteSpaceSeparatedTokens("\"arg 0 \"   arg1 \"arg 2\"");
        Assert.assertEquals("arg 0 ", args.get(0));
        Assert.assertEquals("arg1", args.get(1));
        Assert.assertEquals("arg 2", args.get(2));
    }

    @Test
    public void testDoubleQuote() {
        List<String> args = ExecParseUtils.splitToWhiteSpaceSeparatedTokens("\"\"arg0\"\"");
        Assert.assertEquals("\"arg0\"", args.get(0));
    }

    @Test
    public void testDoubleQuoteAndSpace() {
        List<String> args = ExecParseUtils.splitToWhiteSpaceSeparatedTokens("\"\"arg0\"\" arg1");
        Assert.assertEquals("\"arg0\"", args.get(0));
        Assert.assertEquals("arg1", args.get(1));
    }

    @Test
    public void testTwoDoubleQuotes() {
        List<String> args = ExecParseUtils.splitToWhiteSpaceSeparatedTokens("\"\"arg0\"\" \"\"arg1\"\"");
        Assert.assertEquals("\"arg0\"", args.get(0));
        Assert.assertEquals("\"arg1\"", args.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWhiteSpaceSeparatedArgsNotClosed() {
        ExecParseUtils.splitToWhiteSpaceSeparatedTokens("arg 0 \" arg1 \"arg 2\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidQuotes() {
        ExecParseUtils.splitToWhiteSpaceSeparatedTokens("\"\"arg 0 \" arg1 \"arg 2\"");
    }
}

