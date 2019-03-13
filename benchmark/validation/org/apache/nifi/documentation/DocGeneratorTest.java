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
package org.apache.nifi.documentation;


import NiFiProperties.COMPONENT_DOCS_DIRECTORY;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.nifi.bundle.Bundle;
import org.apache.nifi.bundle.BundleCoordinate;
import org.apache.nifi.nar.ExtensionDiscoveringManager;
import org.apache.nifi.nar.ExtensionMapping;
import org.apache.nifi.nar.NarClassLoadersHolder;
import org.apache.nifi.nar.NarUnpacker;
import org.apache.nifi.nar.StandardExtensionDiscoveringManager;
import org.apache.nifi.nar.SystemBundle;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DocGeneratorTest {
    @Test
    public void testProcessorLoadsNarResources() throws IOException, ClassNotFoundException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
        NiFiProperties properties = loadSpecifiedProperties("/conf/nifi.properties", COMPONENT_DOCS_DIRECTORY, temporaryFolder.getRoot().getAbsolutePath());
        final Bundle systemBundle = SystemBundle.create(properties);
        final ExtensionMapping mapping = NarUnpacker.unpackNars(properties, systemBundle);
        NarClassLoadersHolder.getInstance().init(properties.getFrameworkWorkingDirectory(), properties.getExtensionsWorkingDirectory());
        final ExtensionDiscoveringManager extensionManager = new StandardExtensionDiscoveringManager();
        extensionManager.discoverExtensions(systemBundle, NarClassLoadersHolder.getInstance().getBundles());
        DocGenerator.generate(properties, extensionManager, mapping);
        final String extensionClassName = "org.apache.nifi.processors.WriteResourceToStream";
        final BundleCoordinate coordinate = mapping.getProcessorNames().get(extensionClassName).stream().findFirst().get();
        final String path = ((((((coordinate.getGroup()) + "/") + (coordinate.getId())) + "/") + (coordinate.getVersion())) + "/") + extensionClassName;
        File processorDirectory = new File(temporaryFolder.getRoot(), path);
        File indexHtml = new File(processorDirectory, "index.html");
        Assert.assertTrue((indexHtml + " should have been generated"), indexHtml.exists());
        String generatedHtml = FileUtils.readFileToString(indexHtml, Charset.defaultCharset());
        Assert.assertNotNull(generatedHtml);
        Assert.assertTrue(generatedHtml.contains("This example processor loads a resource from the nar and writes it to the FlowFile content"));
        Assert.assertTrue(generatedHtml.contains("files that were successfully processed"));
        Assert.assertTrue(generatedHtml.contains("files that were not successfully processed"));
        Assert.assertTrue(generatedHtml.contains("resources"));
    }
}

