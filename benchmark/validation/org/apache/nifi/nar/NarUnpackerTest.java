/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.nar;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;


public class NarUnpackerTest {
    @Test
    public void testUnpackNars() {
        NiFiProperties properties = loadSpecifiedProperties("/NarUnpacker/conf/nifi.properties", Collections.EMPTY_MAP);
        Assert.assertEquals("./target/NarUnpacker/lib/", properties.getProperty("nifi.nar.library.directory"));
        Assert.assertEquals("./target/NarUnpacker/lib2/", properties.getProperty("nifi.nar.library.directory.alt"));
        final ExtensionMapping extensionMapping = NarUnpacker.unpackNars(properties, SystemBundle.create(properties));
        Assert.assertEquals(2, extensionMapping.getAllExtensionNames().size());
        Assert.assertTrue(extensionMapping.getAllExtensionNames().keySet().contains("org.apache.nifi.processors.dummy.one"));
        Assert.assertTrue(extensionMapping.getAllExtensionNames().keySet().contains("org.apache.nifi.processors.dummy.two"));
        final File extensionsWorkingDir = properties.getExtensionsWorkingDirectory();
        File[] extensionFiles = extensionsWorkingDir.listFiles();
        Set<String> expectedNars = new HashSet<>();
        expectedNars.add("dummy-one.nar-unpacked");
        expectedNars.add("dummy-two.nar-unpacked");
        expectedNars.add("nifi-jetty-bundle.nar-unpacked");
        Assert.assertEquals(expectedNars.size(), extensionFiles.length);
        for (File extensionFile : extensionFiles) {
            Assert.assertTrue(expectedNars.contains(extensionFile.getName()));
        }
    }

    @Test
    public void testUnpackNarsFromEmptyDir() throws IOException {
        final File emptyDir = new File("./target/empty/dir");
        emptyDir.delete();
        emptyDir.deleteOnExit();
        Assert.assertTrue(emptyDir.mkdirs());
        final Map<String, String> others = new HashMap<>();
        others.put("nifi.nar.library.directory.alt", emptyDir.toString());
        NiFiProperties properties = loadSpecifiedProperties("/NarUnpacker/conf/nifi.properties", others);
        final ExtensionMapping extensionMapping = NarUnpacker.unpackNars(properties, SystemBundle.create(properties));
        Assert.assertEquals(1, extensionMapping.getAllExtensionNames().size());
        Assert.assertTrue(extensionMapping.getAllExtensionNames().keySet().contains("org.apache.nifi.processors.dummy.one"));
        final File extensionsWorkingDir = properties.getExtensionsWorkingDirectory();
        File[] extensionFiles = extensionsWorkingDir.listFiles();
        Assert.assertEquals(2, extensionFiles.length);
        final Optional<File> foundDummyOne = Stream.of(extensionFiles).filter(( f) -> f.getName().equals("dummy-one.nar-unpacked")).findFirst();
        Assert.assertTrue(foundDummyOne.isPresent());
    }

    @Test
    public void testUnpackNarsFromNonExistantDir() {
        final File nonExistantDir = new File("./target/this/dir/should/not/exist/");
        nonExistantDir.delete();
        nonExistantDir.deleteOnExit();
        final Map<String, String> others = new HashMap<>();
        others.put("nifi.nar.library.directory.alt", nonExistantDir.toString());
        NiFiProperties properties = loadSpecifiedProperties("/NarUnpacker/conf/nifi.properties", others);
        final ExtensionMapping extensionMapping = NarUnpacker.unpackNars(properties, SystemBundle.create(properties));
        Assert.assertTrue(extensionMapping.getAllExtensionNames().keySet().contains("org.apache.nifi.processors.dummy.one"));
        Assert.assertEquals(1, extensionMapping.getAllExtensionNames().size());
        final File extensionsWorkingDir = properties.getExtensionsWorkingDirectory();
        File[] extensionFiles = extensionsWorkingDir.listFiles();
        Assert.assertEquals(2, extensionFiles.length);
        final Optional<File> foundDummyOne = Stream.of(extensionFiles).filter(( f) -> f.getName().equals("dummy-one.nar-unpacked")).findFirst();
        Assert.assertTrue(foundDummyOne.isPresent());
    }

    @Test
    public void testUnpackNarsFromNonDir() throws IOException {
        final File nonDir = new File("./target/file.txt");
        nonDir.createNewFile();
        nonDir.deleteOnExit();
        final Map<String, String> others = new HashMap<>();
        others.put("nifi.nar.library.directory.alt", nonDir.toString());
        NiFiProperties properties = loadSpecifiedProperties("/NarUnpacker/conf/nifi.properties", others);
        final ExtensionMapping extensionMapping = NarUnpacker.unpackNars(properties, SystemBundle.create(properties));
        Assert.assertNull(extensionMapping);
    }
}

