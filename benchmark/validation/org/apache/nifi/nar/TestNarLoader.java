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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.reporting.ReportingTask;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;


public class TestNarLoader {
    static final String WORK_DIR = "./target/work";

    static final String NAR_AUTOLOAD_DIR = "./target/extensions";

    static final String EXTENSIONS_DIR = "./src/test/resources/extensions";

    private NiFiProperties properties;

    private ExtensionMapping extensionMapping;

    private StandardNarLoader narLoader;

    private NarClassLoaders narClassLoaders;

    private ExtensionDiscoveringManager extensionManager;

    @Test
    public void testNarLoaderWhenAllAvailable() throws IOException {
        // Copy all NARs from src/test/resources/extensions to target/extensions
        final File extensionsDir = new File(TestNarLoader.EXTENSIONS_DIR);
        final Path narAutoLoadDir = Paths.get(TestNarLoader.NAR_AUTOLOAD_DIR);
        for (final File extensionFile : extensionsDir.listFiles()) {
            Files.copy(extensionFile.toPath(), narAutoLoadDir.resolve(extensionFile.getName()), StandardCopyOption.REPLACE_EXISTING);
        }
        final List<File> narFiles = Arrays.asList(narAutoLoadDir.toFile().listFiles());
        Assert.assertEquals(3, narFiles.size());
        final NarLoadResult narLoadResult = narLoader.load(narFiles);
        Assert.assertNotNull(narLoadResult);
        Assert.assertEquals(3, narLoadResult.getLoadedBundles().size());
        Assert.assertEquals(0, narLoadResult.getSkippedBundles().size());
        Assert.assertEquals(5, narClassLoaders.getBundles().size());
        Assert.assertEquals(1, extensionManager.getExtensions(Processor.class).size());
        Assert.assertEquals(1, extensionManager.getExtensions(ControllerService.class).size());
        Assert.assertEquals(0, extensionManager.getExtensions(ReportingTask.class).size());
    }

    @Test
    public void testNarLoaderWhenDependentNarsAreMissing() throws IOException {
        final File extensionsDir = new File(TestNarLoader.EXTENSIONS_DIR);
        final Path narAutoLoadDir = Paths.get(TestNarLoader.NAR_AUTOLOAD_DIR);
        // Copy processors NAR first which depends on service API NAR
        final File processorsNar = new File(extensionsDir, "nifi-example-processors-nar-1.0.nar");
        final File targetProcessorNar = new File(narAutoLoadDir.toFile(), processorsNar.getName());
        Files.copy(processorsNar.toPath(), targetProcessorNar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Attempt to load while only processor NAR is available
        final List<File> narFiles1 = Arrays.asList(targetProcessorNar);
        final NarLoadResult narLoadResult1 = narLoader.load(narFiles1);
        Assert.assertNotNull(narLoadResult1);
        Assert.assertEquals(0, narLoadResult1.getLoadedBundles().size());
        Assert.assertEquals(1, narLoadResult1.getSkippedBundles().size());
        // Copy the service impl which also depends on service API NAR
        final File serviceImplNar = new File(extensionsDir, "nifi-example-service-nar-1.1.nar");
        final File targetServiceImplNar = new File(narAutoLoadDir.toFile(), serviceImplNar.getName());
        Files.copy(serviceImplNar.toPath(), targetServiceImplNar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Attempt to load while processor and service impl NARs available
        final List<File> narFiles2 = Arrays.asList(targetServiceImplNar);
        final NarLoadResult narLoadResult2 = narLoader.load(narFiles2);
        Assert.assertNotNull(narLoadResult2);
        Assert.assertEquals(0, narLoadResult2.getLoadedBundles().size());
        Assert.assertEquals(2, narLoadResult2.getSkippedBundles().size());
        // Copy service API NAR
        final File serviceApiNar = new File(extensionsDir, "nifi-example-service-api-nar-1.0.nar");
        final File targetServiceApiNar = new File(narAutoLoadDir.toFile(), serviceApiNar.getName());
        Files.copy(serviceApiNar.toPath(), targetServiceApiNar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Attempt to load while all NARs available
        final List<File> narFiles3 = Arrays.asList(targetServiceApiNar);
        final NarLoadResult narLoadResult3 = narLoader.load(narFiles3);
        Assert.assertNotNull(narLoadResult3);
        Assert.assertEquals(3, narLoadResult3.getLoadedBundles().size());
        Assert.assertEquals(0, narLoadResult3.getSkippedBundles().size());
        Assert.assertEquals(5, narClassLoaders.getBundles().size());
        Assert.assertEquals(1, extensionManager.getExtensions(Processor.class).size());
        Assert.assertEquals(1, extensionManager.getExtensions(ControllerService.class).size());
        Assert.assertEquals(0, extensionManager.getExtensions(ReportingTask.class).size());
    }
}

