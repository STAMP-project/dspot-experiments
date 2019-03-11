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
package org.apache.nifi.documentation.html;


import RequiredPermission.READ_FILESYSTEM;
import SystemResource.CPU;
import SystemResource.DISK;
import SystemResource.MEMORY;
import SystemResourceConsideration.DEFAULT_DESCRIPTION;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.documentation.DocumentationWriter;
import org.apache.nifi.documentation.example.DeprecatedProcessor;
import org.apache.nifi.documentation.example.FullyDocumentedProcessor;
import org.apache.nifi.documentation.example.NakedProcessor;
import org.apache.nifi.documentation.example.ProcessorWithLogger;
import org.apache.nifi.init.ProcessorInitializer;
import org.apache.nifi.nar.ExtensionManager;
import org.apache.nifi.nar.StandardExtensionDiscoveringManager;
import org.junit.Assert;
import org.junit.Test;


public class ProcessorDocumentationWriterTest {
    @Test
    public void testFullyDocumentedProcessor() throws IOException {
        ExtensionManager extensionManager = new StandardExtensionDiscoveringManager();
        FullyDocumentedProcessor processor = new FullyDocumentedProcessor();
        ProcessorInitializer initializer = new ProcessorInitializer(extensionManager);
        initializer.initialize(processor);
        DocumentationWriter writer = new HtmlProcessorDocumentationWriter(extensionManager);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.write(processor, baos, false);
        initializer.teardown(processor);
        String results = new String(baos.toByteArray());
        XmlValidator.assertXmlValid(results);
        XmlValidator.assertContains(results, FullyDocumentedProcessor.DIRECTORY.getDisplayName());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.DIRECTORY.getDescription());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.OPTIONAL_PROPERTY.getDisplayName());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.OPTIONAL_PROPERTY.getDescription());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.POLLING_INTERVAL.getDisplayName());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.POLLING_INTERVAL.getDescription());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.POLLING_INTERVAL.getDefaultValue());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.RECURSE.getDisplayName());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.RECURSE.getDescription());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.REL_SUCCESS.getName());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.REL_SUCCESS.getDescription());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.REL_FAILURE.getName());
        XmlValidator.assertContains(results, FullyDocumentedProcessor.REL_FAILURE.getDescription());
        XmlValidator.assertContains(results, "Controller Service API: ");
        XmlValidator.assertContains(results, "SampleService");
        XmlValidator.assertContains(results, "CLUSTER, LOCAL");
        XmlValidator.assertContains(results, "state management description");
        XmlValidator.assertContains(results, "processor restriction description");
        XmlValidator.assertContains(results, READ_FILESYSTEM.getPermissionLabel());
        XmlValidator.assertContains(results, "Requires read filesystem permission");
        XmlValidator.assertNotContains(results, "iconSecure.png");
        XmlValidator.assertContains(results, FullyDocumentedProcessor.class.getAnnotation(CapabilityDescription.class).value());
        XmlValidator.assertNotContains(results, "This component has no required or optional properties.");
        XmlValidator.assertNotContains(results, "No description provided.");
        XmlValidator.assertNotContains(results, "No tags provided.");
        XmlValidator.assertNotContains(results, "Additional Details...");
        // check expression language scope
        XmlValidator.assertContains(results, "Supports Expression Language: true (will be evaluated using variable registry only)");
        XmlValidator.assertContains(results, "Supports Expression Language: true (undefined scope)");
        // verify dynamic properties
        XmlValidator.assertContains(results, "Routes FlowFiles to relationships based on XPath");
        // input requirement
        XmlValidator.assertContains(results, "This component does not allow an incoming relationship.");
        // verify system resource considerations
        XmlValidator.assertContains(results, CPU.name());
        XmlValidator.assertContains(results, DEFAULT_DESCRIPTION);
        XmlValidator.assertContains(results, DISK.name());
        XmlValidator.assertContains(results, "Customized disk usage description");
        XmlValidator.assertContains(results, MEMORY.name());
        XmlValidator.assertContains(results, "Not Specified");
        // verify the right OnRemoved and OnShutdown methods were called
        Assert.assertEquals(0, processor.getOnRemovedArgs());
        Assert.assertEquals(0, processor.getOnRemovedNoArgs());
        Assert.assertEquals(1, processor.getOnShutdownArgs());
        Assert.assertEquals(1, processor.getOnShutdownNoArgs());
    }

    @Test
    public void testNakedProcessor() throws IOException {
        ExtensionManager extensionManager = new StandardExtensionDiscoveringManager();
        NakedProcessor processor = new NakedProcessor();
        ProcessorInitializer initializer = new ProcessorInitializer(extensionManager);
        initializer.initialize(processor);
        DocumentationWriter writer = new HtmlProcessorDocumentationWriter(extensionManager);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.write(processor, baos, false);
        initializer.teardown(processor);
        String results = new String(baos.toByteArray());
        XmlValidator.assertXmlValid(results);
        // no description
        XmlValidator.assertContains(results, "No description provided.");
        // no tags
        XmlValidator.assertContains(results, "No tags provided.");
        // properties
        XmlValidator.assertContains(results, "This component has no required or optional properties.");
        // relationships
        XmlValidator.assertContains(results, "This processor has no relationships.");
        // state management
        XmlValidator.assertContains(results, "This component does not store state.");
        // state management
        XmlValidator.assertContains(results, "This component is not restricted.");
        // input requirement
        XmlValidator.assertNotContains(results, "Input requirement:");
    }

    @Test
    public void testProcessorWithLoggerInitialization() throws IOException {
        ExtensionManager extensionManager = new StandardExtensionDiscoveringManager();
        ProcessorWithLogger processor = new ProcessorWithLogger();
        ProcessorInitializer initializer = new ProcessorInitializer(extensionManager);
        initializer.initialize(processor);
        DocumentationWriter writer = new HtmlProcessorDocumentationWriter(extensionManager);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.write(processor, baos, false);
        initializer.teardown(processor);
        String results = new String(baos.toByteArray());
        XmlValidator.assertXmlValid(results);
    }

    @Test
    public void testDeprecatedProcessor() throws IOException {
        ExtensionManager extensionManager = new StandardExtensionDiscoveringManager();
        DeprecatedProcessor processor = new DeprecatedProcessor();
        ProcessorInitializer initializer = new ProcessorInitializer(extensionManager);
        initializer.initialize(processor);
        DocumentationWriter writer = new HtmlProcessorDocumentationWriter(extensionManager);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.write(processor, baos, false);
        initializer.teardown(processor);
        String results = new String(baos.toByteArray());
        XmlValidator.assertXmlValid(results);
        XmlValidator.assertContains(results, DeprecatedProcessor.DIRECTORY.getDisplayName());
        XmlValidator.assertContains(results, DeprecatedProcessor.DIRECTORY.getDescription());
        XmlValidator.assertContains(results, DeprecatedProcessor.OPTIONAL_PROPERTY.getDisplayName());
        XmlValidator.assertContains(results, DeprecatedProcessor.OPTIONAL_PROPERTY.getDescription());
        XmlValidator.assertContains(results, DeprecatedProcessor.POLLING_INTERVAL.getDisplayName());
        XmlValidator.assertContains(results, DeprecatedProcessor.POLLING_INTERVAL.getDescription());
        XmlValidator.assertContains(results, DeprecatedProcessor.POLLING_INTERVAL.getDefaultValue());
        XmlValidator.assertContains(results, DeprecatedProcessor.RECURSE.getDisplayName());
        XmlValidator.assertContains(results, DeprecatedProcessor.RECURSE.getDescription());
        XmlValidator.assertContains(results, DeprecatedProcessor.REL_SUCCESS.getName());
        XmlValidator.assertContains(results, DeprecatedProcessor.REL_SUCCESS.getDescription());
        XmlValidator.assertContains(results, DeprecatedProcessor.REL_FAILURE.getName());
        XmlValidator.assertContains(results, DeprecatedProcessor.REL_FAILURE.getDescription());
        XmlValidator.assertContains(results, "Controller Service API: ");
        XmlValidator.assertContains(results, "SampleService");
        XmlValidator.assertContains(results, "CLUSTER, LOCAL");
        XmlValidator.assertContains(results, "state management description");
        XmlValidator.assertContains(results, "processor restriction description");
        XmlValidator.assertNotContains(results, "iconSecure.png");
        XmlValidator.assertContains(results, DeprecatedProcessor.class.getAnnotation(CapabilityDescription.class).value());
        // Check for the existence of deprecation notice
        XmlValidator.assertContains(results, "Deprecation notice: ");
        // assertContains(results, DeprecatedProcessor.class.getAnnotation(DeprecationNotice.class.));
        XmlValidator.assertNotContains(results, "This component has no required or optional properties.");
        XmlValidator.assertNotContains(results, "No description provided.");
        XmlValidator.assertNotContains(results, "No tags provided.");
        XmlValidator.assertNotContains(results, "Additional Details...");
        // verify the right OnRemoved and OnShutdown methods were called
        Assert.assertEquals(0, processor.getOnRemovedArgs());
        Assert.assertEquals(0, processor.getOnRemovedNoArgs());
        Assert.assertEquals(1, processor.getOnShutdownArgs());
        Assert.assertEquals(1, processor.getOnShutdownNoArgs());
    }
}

