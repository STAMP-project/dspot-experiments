/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;


import HttpResponseStatus.OK;
import io.netty.util.AsciiString;
import org.junit.Assert;
import org.junit.Test;


public class HttpResponseStatusTest {
    @Test
    public void parseLineStringJustCode() {
        Assert.assertSame(OK, HttpResponseStatus.parseLine("200"));
    }

    @Test
    public void parseLineStringCodeAndPhrase() {
        Assert.assertSame(OK, HttpResponseStatus.parseLine("200 OK"));
    }

    @Test
    public void parseLineStringCustomCode() {
        HttpResponseStatus customStatus = HttpResponseStatus.parseLine("612");
        Assert.assertEquals(612, customStatus.code());
    }

    @Test
    public void parseLineStringCustomCodeAndPhrase() {
        HttpResponseStatus customStatus = HttpResponseStatus.parseLine("612 FOO");
        Assert.assertEquals(612, customStatus.code());
        Assert.assertEquals("FOO", customStatus.reasonPhrase());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseLineStringMalformedCode() {
        HttpResponseStatus.parseLine("200a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseLineStringMalformedCodeWithPhrase() {
        HttpResponseStatus.parseLine("200a foo");
    }

    @Test
    public void parseLineAsciiStringJustCode() {
        Assert.assertSame(OK, HttpResponseStatus.parseLine(new AsciiString("200")));
    }

    @Test
    public void parseLineAsciiStringCodeAndPhrase() {
        Assert.assertSame(OK, HttpResponseStatus.parseLine(new AsciiString("200 OK")));
    }

    @Test
    public void parseLineAsciiStringCustomCode() {
        HttpResponseStatus customStatus = HttpResponseStatus.parseLine(new AsciiString("612"));
        Assert.assertEquals(612, customStatus.code());
    }

    @Test
    public void parseLineAsciiStringCustomCodeAndPhrase() {
        HttpResponseStatus customStatus = HttpResponseStatus.parseLine(new AsciiString("612 FOO"));
        Assert.assertEquals(612, customStatus.code());
        Assert.assertEquals("FOO", customStatus.reasonPhrase());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseLineAsciiStringMalformedCode() {
        HttpResponseStatus.parseLine(new AsciiString("200a"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseLineAsciiStringMalformedCodeWithPhrase() {
        HttpResponseStatus.parseLine(new AsciiString("200a foo"));
    }
}

