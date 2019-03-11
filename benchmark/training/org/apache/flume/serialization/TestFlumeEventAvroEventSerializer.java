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
package org.apache.flume.serialization;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Test;


public class TestFlumeEventAvroEventSerializer {
    private static final File TESTFILE = new File("src/test/resources/FlumeEventAvroEvent.avro");

    @Test
    public void testAvroSerializer() throws FileNotFoundException, IOException {
        createAvroFile(TestFlumeEventAvroEventSerializer.TESTFILE, null);
        validateAvroFile(TestFlumeEventAvroEventSerializer.TESTFILE);
        FileUtils.forceDelete(TestFlumeEventAvroEventSerializer.TESTFILE);
    }

    @Test
    public void testAvroSerializerNullCompression() throws FileNotFoundException, IOException {
        createAvroFile(TestFlumeEventAvroEventSerializer.TESTFILE, "null");
        validateAvroFile(TestFlumeEventAvroEventSerializer.TESTFILE);
        FileUtils.forceDelete(TestFlumeEventAvroEventSerializer.TESTFILE);
    }

    @Test
    public void testAvroSerializerDeflateCompression() throws FileNotFoundException, IOException {
        createAvroFile(TestFlumeEventAvroEventSerializer.TESTFILE, "deflate");
        validateAvroFile(TestFlumeEventAvroEventSerializer.TESTFILE);
        FileUtils.forceDelete(TestFlumeEventAvroEventSerializer.TESTFILE);
    }

    @Test
    public void testAvroSerializerSnappyCompression() throws FileNotFoundException, IOException {
        // Snappy currently broken on Mac in OpenJDK 7 per FLUME-2012
        Assume.assumeTrue(((!("Mac OS X".equals(System.getProperty("os.name")))) || (!(System.getProperty("java.version").startsWith("1.7.")))));
        createAvroFile(TestFlumeEventAvroEventSerializer.TESTFILE, "snappy");
        validateAvroFile(TestFlumeEventAvroEventSerializer.TESTFILE);
        FileUtils.forceDelete(TestFlumeEventAvroEventSerializer.TESTFILE);
    }
}

