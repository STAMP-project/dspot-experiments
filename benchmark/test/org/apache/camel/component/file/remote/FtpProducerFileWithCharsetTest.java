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
package org.apache.camel.component.file.remote;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.camel.converter.IOConverter;
import org.apache.camel.util.IOHelper;
import org.junit.Test;


public class FtpProducerFileWithCharsetTest extends FtpServerTestSupport {
    private String payload = "\u00e6\u00f8\u00e5 \u00a9";

    @Test
    public void testProducerWithCharset() throws Exception {
        sendFile(getFtpUrl(), payload, "charset/iso.txt");
        File file = new File(((FtpServerTestSupport.FTP_ROOT_DIR) + "/upload/charset/iso.txt"));
        assertTrue("The uploaded file should exists", file.exists());
        String fileContent = new String(IOConverter.toByteArray(file), "iso-8859-1");
        assertEquals(fileContent, payload);
        // Lets also test byte wise
        InputStream fis = IOHelper.buffered(new FileInputStream(file));
        byte[] buffer = new byte[100];
        int len = fis.read(buffer);
        assertTrue(("Should read data: " + len), (len != (-1)));
        byte[] data = new byte[len];
        System.arraycopy(buffer, 0, data, 0, len);
        fis.close();
        // data should be in iso, where the danish ae is -26, oe is -8 aa is -27
        // and copyright is -87
        assertEquals(5, data.length);
        assertEquals((-26), data[0]);
        assertEquals((-8), data[1]);
        assertEquals((-27), data[2]);
        assertEquals(32, data[3]);
        assertEquals((-87), data[4]);
    }
}

