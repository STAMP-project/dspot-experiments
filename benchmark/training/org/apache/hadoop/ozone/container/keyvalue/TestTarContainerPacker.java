/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.container.keyvalue;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.container.common.interfaces.ContainerPacker;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the tar/untar for a given container.
 */
public class TestTarContainerPacker {
    private static final String TEST_DB_FILE_NAME = "test1";

    private static final String TEST_DB_FILE_CONTENT = "test1";

    private static final String TEST_CHUNK_FILE_NAME = "chunk1";

    private static final String TEST_CHUNK_FILE_CONTENT = "This is a chunk";

    private static final String TEST_DESCRIPTOR_FILE_CONTENT = "descriptor";

    private ContainerPacker packer = new TarContainerPacker();

    private static final Path SOURCE_CONTAINER_ROOT = Paths.get("target/test/data/packer-source-dir");

    private static final Path DEST_CONTAINER_ROOT = Paths.get("target/test/data/packer-dest-dir");

    @Test
    public void pack() throws IOException, CompressorException {
        // GIVEN
        OzoneConfiguration conf = new OzoneConfiguration();
        KeyValueContainerData sourceContainerData = createContainer(1L, TestTarContainerPacker.SOURCE_CONTAINER_ROOT, conf);
        KeyValueContainer sourceContainer = new KeyValueContainer(sourceContainerData, conf);
        // sample db file in the metadata directory
        try (FileWriter writer = new FileWriter(sourceContainerData.getDbFile().toPath().resolve(TestTarContainerPacker.TEST_DB_FILE_NAME).toFile())) {
            IOUtils.write(TestTarContainerPacker.TEST_DB_FILE_CONTENT, writer);
        }
        // sample chunk file in the chunk directory
        try (FileWriter writer = new FileWriter(Paths.get(sourceContainerData.getChunksPath()).resolve(TestTarContainerPacker.TEST_CHUNK_FILE_NAME).toFile())) {
            IOUtils.write(TestTarContainerPacker.TEST_CHUNK_FILE_CONTENT, writer);
        }
        // sample container descriptor file
        try (FileWriter writer = new FileWriter(sourceContainer.getContainerFile())) {
            IOUtils.write(TestTarContainerPacker.TEST_DESCRIPTOR_FILE_CONTENT, writer);
        }
        Path targetFile = TestTarContainerPacker.SOURCE_CONTAINER_ROOT.getParent().resolve("container.tar.gz");
        // WHEN: pack it
        try (FileOutputStream output = new FileOutputStream(targetFile.toFile())) {
            packer.pack(sourceContainer, output);
        }
        // THEN: check the result
        try (FileInputStream input = new FileInputStream(targetFile.toFile())) {
            CompressorInputStream uncompressed = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.GZIP, input);
            TarArchiveInputStream tarStream = new TarArchiveInputStream(uncompressed);
            TarArchiveEntry entry;
            Map<String, TarArchiveEntry> entries = new HashMap<>();
            while ((entry = tarStream.getNextTarEntry()) != null) {
                entries.put(entry.getName(), entry);
            } 
            Assert.assertTrue(entries.containsKey("container.yaml"));
        }
        // read the container descriptor only
        try (FileInputStream input = new FileInputStream(targetFile.toFile())) {
            String containerYaml = new String(packer.unpackContainerDescriptor(input), Charset.forName(StandardCharsets.UTF_8.name()));
            Assert.assertEquals(TestTarContainerPacker.TEST_DESCRIPTOR_FILE_CONTENT, containerYaml);
        }
        KeyValueContainerData destinationContainerData = createContainer(2L, TestTarContainerPacker.DEST_CONTAINER_ROOT, conf);
        KeyValueContainer destinationContainer = new KeyValueContainer(destinationContainerData, conf);
        String descriptor = "";
        // unpackContainerData
        try (FileInputStream input = new FileInputStream(targetFile.toFile())) {
            descriptor = new String(packer.unpackContainerData(destinationContainer, input), Charset.forName(StandardCharsets.UTF_8.name()));
        }
        assertExampleMetadataDbIsGood(destinationContainerData.getDbFile().toPath());
        assertExampleChunkFileIsGood(Paths.get(destinationContainerData.getChunksPath()));
        Assert.assertFalse(("Descriptor file should not been exctarcted by the " + "unpackContainerData Call"), destinationContainer.getContainerFile().exists());
        Assert.assertEquals(TestTarContainerPacker.TEST_DESCRIPTOR_FILE_CONTENT, descriptor);
    }
}

