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
package org.apache.flink.runtime.rest.handler.legacy.files;


import org.apache.flink.runtime.rest.handler.util.MimeTypes;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the MIME types map.
 */
public class MimeTypesTest {
    @Test
    public void testCompleteness() {
        try {
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("txt"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("htm"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("html"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("css"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("js"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("json"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("png"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("jpg"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("jpeg"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("gif"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("woff"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("woff2"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("otf"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("ttf"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForExtension("eot"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testFileNameExtraction() {
        try {
            Assert.assertNotNull(MimeTypes.getMimeTypeForFileName("test.txt"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForFileName("t.txt"));
            Assert.assertNotNull(MimeTypes.getMimeTypeForFileName("first.second.third.txt"));
            Assert.assertNull(MimeTypes.getMimeTypeForFileName(".txt"));
            Assert.assertNull(MimeTypes.getMimeTypeForFileName("txt"));
            Assert.assertNull(MimeTypes.getMimeTypeForFileName("test."));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

