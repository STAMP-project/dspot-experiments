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


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.avro.file.DataFileReader;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("restriction")
public class TestDataFileReader {
    // regression test for bug AVRO-2286
    @Test
    public void testForLeakingFileDescriptors() throws IOException {
        Path emptyFile = Files.createTempFile("empty", ".avro");
        Files.deleteIfExists(emptyFile);
        Files.createFile(emptyFile);
        long openFilesBeforeOperation = getNumberOfOpenFileDescriptors();
        try (DataFileReader<Object> reader = new DataFileReader(emptyFile.toFile(), new org.apache.avro.generic.GenericDatumReader())) {
            Assert.fail("Reading on empty file is supposed to fail.");
        } catch (IOException e) {
            // everything going as supposed to
        }
        Files.delete(emptyFile);
        Assert.assertEquals("File descriptor leaked from new DataFileReader()", openFilesBeforeOperation, getNumberOfOpenFileDescriptors());
    }
}

