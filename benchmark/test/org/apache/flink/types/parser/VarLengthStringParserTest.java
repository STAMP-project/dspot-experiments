/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.types.parser;


import ConfigConstants.DEFAULT_CHARSET;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.flink.types.StringValue;
import org.apache.flink.types.Value;
import org.junit.Assert;
import org.junit.Test;


public class VarLengthStringParserTest {
    public StringValueParser parser = new StringValueParser();

    @Test
    public void testGetValue() {
        Value v = parser.createValue();
        Assert.assertTrue((v instanceof StringValue));
    }

    @Test
    public void testParseValidUnquotedStrings() {
        this.parser = new StringValueParser();
        // check valid strings with out whitespaces and trailing delimiter
        byte[] recBytes = "abcdefgh|i|jklmno|".getBytes(DEFAULT_CHARSET);
        StringValue s = new StringValue();
        int startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 9));
        Assert.assertTrue(s.getValue().equals("abcdefgh"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 11));
        Assert.assertTrue(s.getValue().equals("i"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 18));
        Assert.assertTrue(s.getValue().equals("jklmno"));
        // check single field not terminated
        recBytes = "abcde".getBytes(DEFAULT_CHARSET);
        startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 5));
        Assert.assertTrue(s.getValue().equals("abcde"));
        // check last field not terminated
        recBytes = "abcde|fg".getBytes(DEFAULT_CHARSET);
        startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 6));
        Assert.assertTrue(s.getValue().equals("abcde"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 8));
        Assert.assertTrue(s.getValue().equals("fg"));
    }

    @Test
    public void testParseValidQuotedStrings() {
        this.parser = new StringValueParser();
        this.parser.enableQuotedStringParsing(((byte) ('\"')));
        // check valid strings with out whitespaces and trailing delimiter
        byte[] recBytes = "\"abcdefgh\"|\"i\"|\"jklmno\"|".getBytes(DEFAULT_CHARSET);
        StringValue s = new StringValue();
        int startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 11));
        Assert.assertTrue(s.getValue().equals("abcdefgh"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 15));
        Assert.assertTrue(s.getValue().equals("i"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 24));
        Assert.assertTrue(s.getValue().equals("jklmno"));
        // check single field not terminated
        recBytes = "\"abcde\"".getBytes(DEFAULT_CHARSET);
        startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 7));
        Assert.assertTrue(s.getValue().equals("abcde"));
        // check last field not terminated
        recBytes = "\"abcde\"|\"fg\"".getBytes(DEFAULT_CHARSET);
        startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 8));
        Assert.assertTrue(s.getValue().equals("abcde"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 12));
        Assert.assertTrue(s.getValue().equals("fg"));
        // check delimiter in quotes
        recBytes = "\"abcde|fg\"|\"hij|kl|mn|op\"|".getBytes(DEFAULT_CHARSET);
        startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 11));
        Assert.assertTrue(s.getValue().equals("abcde|fg"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 26));
        Assert.assertTrue(s.getValue().equals("hij|kl|mn|op"));
        // check delimiter in quotes last field not terminated
        recBytes = "\"abcde|fg\"|\"hij|kl|mn|op\"".getBytes(DEFAULT_CHARSET);
        startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 11));
        Assert.assertTrue(s.getValue().equals("abcde|fg"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 25));
        Assert.assertTrue(s.getValue().equals("hij|kl|mn|op"));
    }

    @Test
    public void testParseValidMixedStrings() {
        this.parser = new StringValueParser();
        this.parser.enableQuotedStringParsing(((byte) ('@')));
        // check valid strings with out whitespaces and trailing delimiter
        byte[] recBytes = "@abcde|gh@|@i@|jklmnopq|@rs@|tuv".getBytes(DEFAULT_CHARSET);
        StringValue s = new StringValue();
        int startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 11));
        Assert.assertTrue(s.getValue().equals("abcde|gh"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 15));
        Assert.assertTrue(s.getValue().equals("i"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 24));
        Assert.assertTrue(s.getValue().equals("jklmnopq"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 29));
        Assert.assertTrue(s.getValue().equals("rs"));
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos == 32));
        Assert.assertTrue(s.getValue().equals("tuv"));
    }

    @Test
    public void testParseInvalidQuotedStrings() {
        this.parser = new StringValueParser();
        this.parser.enableQuotedStringParsing(((byte) ('\"')));
        // check valid strings with out whitespaces and trailing delimiter
        byte[] recBytes = "\"abcdefgh\"-|\"jklmno  ".getBytes(DEFAULT_CHARSET);
        StringValue s = new StringValue();
        int startPos = 0;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos < 0));
        startPos = 12;
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertTrue((startPos < 0));
    }

    @Test
    public void testParseValidMixedStringsWithCharset() {
        Charset charset = StandardCharsets.US_ASCII;
        this.parser = new StringValueParser();
        this.parser.enableQuotedStringParsing(((byte) ('@')));
        // check valid strings with out whitespaces and trailing delimiter
        byte[] recBytes = "@abcde|gh@|@i@|jklmnopq|@rs@|tuv".getBytes(DEFAULT_CHARSET);
        StringValue s = new StringValue();
        int startPos = 0;
        parser.setCharset(charset);
        startPos = parser.parseField(recBytes, startPos, recBytes.length, new byte[]{ '|' }, s);
        Assert.assertEquals(11, startPos);
        Assert.assertEquals("abcde|gh", s.getValue());
    }
}

