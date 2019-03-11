/**
 * Copyright 2013 The Netty Project
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
package io.netty.handler.codec.compression;


import CharsetUtil.UTF_8;
import EmptyArrays.EMPTY_BYTES;
import ZlibWrapper.GZIP;
import ZlibWrapper.NONE;
import ZlibWrapper.ZLIB;
import ZlibWrapper.ZLIB_OR_NONE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.internal.PlatformDependent;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public abstract class ZlibTest {
    private static final byte[] BYTES_SMALL = new byte[128];

    private static final byte[] BYTES_LARGE = new byte[1024 * 1024];

    private static final byte[] BYTES_LARGE2 = ((((((((((((((((((((((((((((((((((((("<!--?xml version=\"1.0\" encoding=\"ISO-8859-1\"?-->\n" + (((("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n") + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head>\n") + "    <title>Apache Tomcat</title>\n") + "</head>\n")) + '\n') + "<body>\n") + "<h1>It works !</h1>\n") + '\n') + "<p>If you're seeing this page via a web browser, it means you've setup Tomcat successfully.") + " Congratulations!</p>\n") + " \n") + "<p>This is the default Tomcat home page.") + " It can be found on the local filesystem at: <code>/var/lib/tomcat7/webapps/ROOT/index.html</code></p>\n") + '\n') + "<p>Tomcat7 veterans might be pleased to learn that this system instance of Tomcat is installed with") + " <code>CATALINA_HOME</code> in <code>/usr/share/tomcat7</code> and <code>CATALINA_BASE</code> in") + " <code>/var/lib/tomcat7</code>, following the rules from") + " <code>/usr/share/doc/tomcat7-common/RUNNING.txt.gz</code>.</p>\n") + '\n') + "<p>You might consider installing the following packages, if you haven\'t already done so:</p>\n") + '\n') + "<p><b>tomcat7-docs</b>: This package installs a web application that allows to browse the Tomcat 7") + " documentation locally. Once installed, you can access it by clicking <a href=\"docs/\">here</a>.</p>\n") + '\n') + "<p><b>tomcat7-examples</b>: This package installs a web application that allows to access the Tomcat") + " 7 Servlet and JSP examples. Once installed, you can access it by clicking") + " <a href=\"examples/\">here</a>.</p>\n") + '\n') + "<p><b>tomcat7-admin</b>: This package installs two web applications that can help managing this Tomcat") + " instance. Once installed, you can access the <a href=\"manager/html\">manager webapp</a> and") + " the <a href=\"host-manager/html\">host-manager webapp</a>.</p><p>\n") + '\n') + "</p><p>NOTE: For security reasons, using the manager webapp is restricted") + " to users with role \"manager\".") + " The host-manager webapp is restricted to users with role \"admin\". Users are ") + "defined in <code>/etc/tomcat7/tomcat-users.xml</code>.</p>\n") + '\n') + '\n') + '\n') + "</body></html>").getBytes(UTF_8);

    static {
        Random rand = PlatformDependent.threadLocalRandom();
        rand.nextBytes(ZlibTest.BYTES_SMALL);
        rand.nextBytes(ZlibTest.BYTES_LARGE);
    }

    @Test
    public void testGZIP2() throws Exception {
        byte[] bytes = "message".getBytes(UTF_8);
        ByteBuf data = Unpooled.wrappedBuffer(bytes);
        ByteBuf deflatedData = Unpooled.wrappedBuffer(ZlibTest.gzip(bytes));
        EmbeddedChannel chDecoderGZip = new EmbeddedChannel(createDecoder(GZIP));
        try {
            chDecoderGZip.writeInbound(deflatedData);
            Assert.assertTrue(chDecoderGZip.finish());
            ByteBuf buf = chDecoderGZip.readInbound();
            Assert.assertEquals(buf, data);
            Assert.assertNull(chDecoderGZip.readInbound());
            data.release();
            buf.release();
        } finally {
            ZlibTest.dispose(chDecoderGZip);
        }
    }

    @Test
    public void testZLIB() throws Exception {
        testCompressNone(ZLIB, ZLIB);
        testCompressSmall(ZLIB, ZLIB);
        testCompressLarge(ZLIB, ZLIB);
        testDecompressOnly(ZLIB, ZlibTest.deflate(ZlibTest.BYTES_LARGE2), ZlibTest.BYTES_LARGE2);
    }

    @Test
    public void testNONE() throws Exception {
        testCompressNone(NONE, NONE);
        testCompressSmall(NONE, NONE);
        testCompressLarge(NONE, NONE);
    }

    @Test
    public void testGZIP() throws Exception {
        testCompressNone(GZIP, GZIP);
        testCompressSmall(GZIP, GZIP);
        testCompressLarge(GZIP, GZIP);
        testDecompressOnly(GZIP, ZlibTest.gzip(ZlibTest.BYTES_LARGE2), ZlibTest.BYTES_LARGE2);
    }

    @Test
    public void testGZIPCompressOnly() throws Exception {
        testGZIPCompressOnly0(null);// Do not write anything; just finish the stream.

        testGZIPCompressOnly0(EMPTY_BYTES);// Write an empty array.

        testGZIPCompressOnly0(ZlibTest.BYTES_SMALL);
        testGZIPCompressOnly0(ZlibTest.BYTES_LARGE);
    }

    @Test
    public void testZLIB_OR_NONE() throws Exception {
        testCompressNone(NONE, ZLIB_OR_NONE);
        testCompressSmall(NONE, ZLIB_OR_NONE);
        testCompressLarge(NONE, ZLIB_OR_NONE);
    }

    @Test
    public void testZLIB_OR_NONE2() throws Exception {
        testCompressNone(ZLIB, ZLIB_OR_NONE);
        testCompressSmall(ZLIB, ZLIB_OR_NONE);
        testCompressLarge(ZLIB, ZLIB_OR_NONE);
    }

    @Test
    public void testZLIB_OR_NONE3() throws Exception {
        testCompressNone(GZIP, ZLIB_OR_NONE);
        testCompressSmall(GZIP, ZLIB_OR_NONE);
        testCompressLarge(GZIP, ZLIB_OR_NONE);
    }
}

