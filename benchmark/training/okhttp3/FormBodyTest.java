/**
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3;


import java.nio.charset.Charset;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Test;


public final class FormBodyTest {
    @Test
    public void urlEncoding() throws Exception {
        FormBody body = new FormBody.Builder().add("a+=& b", "c+=& d").add("space, the", "final frontier").add("%25", "%25").build();
        Assert.assertEquals(3, body.size());
        Assert.assertEquals("a%2B%3D%26%20b", body.encodedName(0));
        Assert.assertEquals("space%2C%20the", body.encodedName(1));
        Assert.assertEquals("%2525", body.encodedName(2));
        Assert.assertEquals("a+=& b", body.name(0));
        Assert.assertEquals("space, the", body.name(1));
        Assert.assertEquals("%25", body.name(2));
        Assert.assertEquals("c%2B%3D%26%20d", body.encodedValue(0));
        Assert.assertEquals("final%20frontier", body.encodedValue(1));
        Assert.assertEquals("%2525", body.encodedValue(2));
        Assert.assertEquals("c+=& d", body.value(0));
        Assert.assertEquals("final frontier", body.value(1));
        Assert.assertEquals("%25", body.value(2));
        Assert.assertEquals("application/x-www-form-urlencoded", body.contentType().toString());
        String expected = "a%2B%3D%26%20b=c%2B%3D%26%20d&space%2C%20the=final%20frontier&%2525=%2525";
        Assert.assertEquals(expected.length(), body.contentLength());
        Buffer out = new Buffer();
        body.writeTo(out);
        Assert.assertEquals(expected, out.readUtf8());
    }

    @Test
    public void addEncoded() throws Exception {
        FormBody body = new FormBody.Builder().addEncoded("a+=& b", "c+=& d").addEncoded("e+=& f", "g+=& h").addEncoded("%25", "%25").build();
        String expected = "a+%3D%26%20b=c+%3D%26%20d&e+%3D%26%20f=g+%3D%26%20h&%25=%25";
        Buffer out = new Buffer();
        body.writeTo(out);
        Assert.assertEquals(expected, out.readUtf8());
    }

    @Test
    public void encodedPair() throws Exception {
        FormBody body = new FormBody.Builder().add("sim", "ple").build();
        String expected = "sim=ple";
        Assert.assertEquals(expected.length(), body.contentLength());
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        Assert.assertEquals(expected, buffer.readUtf8());
    }

    @Test
    public void encodeMultiplePairs() throws Exception {
        FormBody body = new FormBody.Builder().add("sim", "ple").add("hey", "there").add("help", "me").build();
        String expected = "sim=ple&hey=there&help=me";
        Assert.assertEquals(expected.length(), body.contentLength());
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        Assert.assertEquals(expected, buffer.readUtf8());
    }

    @Test
    public void buildEmptyForm() throws Exception {
        FormBody body = new FormBody.Builder().build();
        String expected = "";
        Assert.assertEquals(expected.length(), body.contentLength());
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        Assert.assertEquals(expected, buffer.readUtf8());
    }

    @Test
    public void characterEncoding() throws Exception {
        Assert.assertEquals("%00", formEncode(0));// Browsers convert '\u0000' to '%EF%BF%BD'.

        Assert.assertEquals("%01", formEncode(1));
        Assert.assertEquals("%02", formEncode(2));
        Assert.assertEquals("%03", formEncode(3));
        Assert.assertEquals("%04", formEncode(4));
        Assert.assertEquals("%05", formEncode(5));
        Assert.assertEquals("%06", formEncode(6));
        Assert.assertEquals("%07", formEncode(7));
        Assert.assertEquals("%08", formEncode(8));
        Assert.assertEquals("%09", formEncode(9));
        Assert.assertEquals("%0A", formEncode(10));// Browsers convert '\n' to '\r\n'

        Assert.assertEquals("%0B", formEncode(11));
        Assert.assertEquals("%0C", formEncode(12));
        Assert.assertEquals("%0D", formEncode(13));// Browsers convert '\r' to '\r\n'

        Assert.assertEquals("%0E", formEncode(14));
        Assert.assertEquals("%0F", formEncode(15));
        Assert.assertEquals("%10", formEncode(16));
        Assert.assertEquals("%11", formEncode(17));
        Assert.assertEquals("%12", formEncode(18));
        Assert.assertEquals("%13", formEncode(19));
        Assert.assertEquals("%14", formEncode(20));
        Assert.assertEquals("%15", formEncode(21));
        Assert.assertEquals("%16", formEncode(22));
        Assert.assertEquals("%17", formEncode(23));
        Assert.assertEquals("%18", formEncode(24));
        Assert.assertEquals("%19", formEncode(25));
        Assert.assertEquals("%1A", formEncode(26));
        Assert.assertEquals("%1B", formEncode(27));
        Assert.assertEquals("%1C", formEncode(28));
        Assert.assertEquals("%1D", formEncode(29));
        Assert.assertEquals("%1E", formEncode(30));
        Assert.assertEquals("%1F", formEncode(31));
        Assert.assertEquals("%20", formEncode(32));// Browsers use '+' for space.

        Assert.assertEquals("%21", formEncode(33));
        Assert.assertEquals("%22", formEncode(34));
        Assert.assertEquals("%23", formEncode(35));
        Assert.assertEquals("%24", formEncode(36));
        Assert.assertEquals("%25", formEncode(37));
        Assert.assertEquals("%26", formEncode(38));
        Assert.assertEquals("%27", formEncode(39));
        Assert.assertEquals("%28", formEncode(40));
        Assert.assertEquals("%29", formEncode(41));
        Assert.assertEquals("*", formEncode(42));
        Assert.assertEquals("%2B", formEncode(43));
        Assert.assertEquals("%2C", formEncode(44));
        Assert.assertEquals("-", formEncode(45));
        Assert.assertEquals(".", formEncode(46));
        Assert.assertEquals("%2F", formEncode(47));
        Assert.assertEquals("0", formEncode(48));
        Assert.assertEquals("9", formEncode(57));
        Assert.assertEquals("%3A", formEncode(58));
        Assert.assertEquals("%3B", formEncode(59));
        Assert.assertEquals("%3C", formEncode(60));
        Assert.assertEquals("%3D", formEncode(61));
        Assert.assertEquals("%3E", formEncode(62));
        Assert.assertEquals("%3F", formEncode(63));
        Assert.assertEquals("%40", formEncode(64));
        Assert.assertEquals("A", formEncode(65));
        Assert.assertEquals("Z", formEncode(90));
        Assert.assertEquals("%5B", formEncode(91));
        Assert.assertEquals("%5C", formEncode(92));
        Assert.assertEquals("%5D", formEncode(93));
        Assert.assertEquals("%5E", formEncode(94));
        Assert.assertEquals("_", formEncode(95));
        Assert.assertEquals("%60", formEncode(96));
        Assert.assertEquals("a", formEncode(97));
        Assert.assertEquals("z", formEncode(122));
        Assert.assertEquals("%7B", formEncode(123));
        Assert.assertEquals("%7C", formEncode(124));
        Assert.assertEquals("%7D", formEncode(125));
        Assert.assertEquals("%7E", formEncode(126));
        Assert.assertEquals("%7F", formEncode(127));
        Assert.assertEquals("%C2%80", formEncode(128));
        Assert.assertEquals("%C3%BF", formEncode(255));
    }

    @Test
    public void manualCharset() throws Exception {
        FormBody body = new FormBody.Builder(Charset.forName("ISO-8859-1")).add("name", "Nicol?s").build();
        String expected = "name=Nicol%E1s";
        Assert.assertEquals(expected.length(), body.contentLength());
        Buffer out = new Buffer();
        body.writeTo(out);
        Assert.assertEquals(expected, out.readUtf8());
    }
}

