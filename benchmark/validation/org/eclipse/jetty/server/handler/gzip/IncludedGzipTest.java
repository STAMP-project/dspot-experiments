/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.server.handler.gzip;


import GzipHandler.DEFLATE;
import GzipHandler.GZIP;
import HttpServletResponse.SC_OK;
import HttpTester.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.servlet.ServletTester;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.IO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static GzipHandler.GZIP;


@ExtendWith(WorkDirExtension.class)
public class IncludedGzipTest {
    public WorkDir testdir;

    private static String __content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In quis felis nunc. " + (((((((((("Quisque suscipit mauris et ante auctor ornare rhoncus lacus aliquet. Pellentesque " + "habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. ") + "Vestibulum sit amet felis augue, vel convallis dolor. Cras accumsan vehicula diam ") + "at faucibus. Etiam in urna turpis, sed congue mi. Morbi et lorem eros. Donec vulputate ") + "velit in risus suscipit lobortis. Aliquam id urna orci, nec sollicitudin ipsum. ") + "Cras a orci turpis. Donec suscipit vulputate cursus. Mauris nunc tellus, fermentum ") + "eu auctor ut, mollis at diam. Quisque porttitor ultrices metus, vitae tincidunt massa ") + "sollicitudin a. Vivamus porttitor libero eget purus hendrerit cursus. Integer aliquam ") + "consequat mauris quis luctus. Cras enim nibh, dignissim eu faucibus ac, mollis nec neque. ") + "Aliquam purus mauris, consectetur nec convallis lacinia, porta sed ante. Suspendisse ") + "et cursus magna. Donec orci enim, molestie a lobortis eu, imperdiet vitae neque.");

    private ServletTester tester;

    private String compressionType;

    public IncludedGzipTest() {
        this.compressionType = GZIP;
    }

    @Test
    public void testGzip() throws Exception {
        // generated and parsed test
        ByteBuffer request = BufferUtil.toBuffer((((("GET /context/file.txt HTTP/1.0\r\n" + ("Host: tester\r\n" + "Accept-Encoding: ")) + (compressionType)) + "\r\n") + "\r\n"));
        HttpTester.Response response = HttpTester.parseResponse(tester.getResponses(request));
        Assertions.assertEquals(SC_OK, response.getStatus());
        Assertions.assertEquals(compressionType, response.get("Content-Encoding"));
        InputStream testIn = null;
        ByteArrayInputStream compressedResponseStream = new ByteArrayInputStream(response.getContentBytes());
        if (compressionType.equals(GZIP)) {
            testIn = new GZIPInputStream(compressedResponseStream);
        } else
            if (compressionType.equals(DEFLATE)) {
                testIn = new InflaterInputStream(compressedResponseStream, new Inflater(true));
            }

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        IO.copy(testIn, testOut);
        Assertions.assertEquals(IncludedGzipTest.__content, testOut.toString("ISO8859_1"));
    }
}

