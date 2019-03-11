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
package org.apache.avro;


import java.io.File;
import java.io.IOException;
import org.apache.avro.io.DatumReader;
import org.junit.Test;


public class DataFileInteropTest {
    private static final File DATAFILE_DIR = new File(System.getProperty("test.dir", "/tmp"));

    @Test
    public void testGeneratedGeneric() throws IOException {
        System.out.println("Reading with generic:");
        DataFileInteropTest.DatumReaderProvider<Object> provider = GenericDatumReader::new;
        readFiles(provider);
    }

    @Test
    public void testGeneratedSpecific() throws IOException {
        System.out.println("Reading with specific:");
        DataFileInteropTest.DatumReaderProvider<Interop> provider = SpecificDatumReader::new;
        readFiles(provider);
    }

    interface DatumReaderProvider<T extends Object> {
        public DatumReader<T> get();
    }
}

