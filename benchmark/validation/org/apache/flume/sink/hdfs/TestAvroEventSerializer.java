/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.sink.hdfs;


import java.io.File;
import java.io.IOException;
import org.junit.Test;


public class TestAvroEventSerializer {
    private File file;

    @Test
    public void testNoCompression() throws IOException {
        createAvroFile(file, null, false, false);
        validateAvroFile(file);
    }

    @Test
    public void testNullCompression() throws IOException {
        createAvroFile(file, "null", false, false);
        validateAvroFile(file);
    }

    @Test
    public void testDeflateCompression() throws IOException {
        createAvroFile(file, "deflate", false, false);
        validateAvroFile(file);
    }

    @Test
    public void testSnappyCompression() throws IOException {
        createAvroFile(file, "snappy", false, false);
        validateAvroFile(file);
    }

    @Test
    public void testSchemaUrl() throws IOException {
        createAvroFile(file, null, true, false);
        validateAvroFile(file);
    }

    @Test
    public void testStaticSchemaUrl() throws IOException {
        createAvroFile(file, null, false, true);
        validateAvroFile(file);
    }

    @Test
    public void testBothUrls() throws IOException {
        createAvroFile(file, null, true, true);
        validateAvroFile(file);
    }
}

