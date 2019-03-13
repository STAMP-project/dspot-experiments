/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.ant.bnd.resource.bundle;


import Constants.PROVIDE_CAPABILITY;
import Constants.REQUIRE_CAPABILITY;
import ResourceBundleLoaderAnalyzerPlugin.LIFERAY_RESOURCE_BUNDLE;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Jar;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static ResourceBundleLoaderAnalyzerPlugin.LIFERAY_RESOURCE_BUNDLE;


/**
 *
 *
 * @author Gregory Amerson
 */
public class ResourceBundleLoaderAnalyzerPluginTest {
    @Test
    public void testAggregateResourceBundlesInstructionEmpty() throws Exception {
        ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin = new ResourceBundleLoaderAnalyzerPlugin();
        InputStream inputStream = ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream("dependencies/resources.test1.jar");
        try (Jar jar = new Jar("dot", inputStream);Analyzer analyzer = new Analyzer()) {
            analyzer.setBundleSymbolicName("resources.test");
            analyzer.setJar(jar);
            analyzer.setProperty("-liferay-aggregate-resource-bundles", "");
            Assert.assertTrue(resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));
            Parameters requireCapabilityHeaders = new Parameters(analyzer.getProperty(REQUIRE_CAPABILITY));
            Collection<Attrs> requires = requireCapabilityHeaders.values();
            Assert.assertTrue(requires.toString(), requires.isEmpty());
            Parameters provideCapabilityHeaders = new Parameters(analyzer.getProperty(PROVIDE_CAPABILITY));
            List<Map.Entry<String, Attrs>> provides = new java.util.ArrayList(provideCapabilityHeaders.entrySet());
            Assert.assertEquals(provides.toString(), 1, provides.size());
            Map.Entry<String, Attrs> entry = provides.get(0);
            Assert.assertEquals(LIFERAY_RESOURCE_BUNDLE, entry.getKey());
            Attrs attrs = entry.getValue();
            Assert.assertEquals(2, attrs.size());
            Assert.assertEquals("resources.test", attrs.get("bundle.symbolic.name"));
            Assert.assertEquals("content.Language", attrs.get("resource.bundle.base.name"));
        }
    }

    @Test
    public void testAggregateResourceBundlesInstructionMultiple() throws Exception {
        ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin = new ResourceBundleLoaderAnalyzerPlugin();
        InputStream inputStream = ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream("dependencies/resources.test1.jar");
        try (Jar jar = new Jar("dot", inputStream);Analyzer analyzer = new Analyzer()) {
            analyzer.setBundleSymbolicName("resources.test");
            analyzer.setJar(jar);
            analyzer.setProperty("-liferay-aggregate-resource-bundles", "resources.lang1,resources.lang2,resources.lang3");
            Assert.assertTrue(resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));
            Parameters requireCapabilityHeaders = new Parameters(analyzer.getProperty(REQUIRE_CAPABILITY));
            Collection<Attrs> requires = requireCapabilityHeaders.values();
            Assert.assertEquals(requires.toString(), 3, requires.size());
            String provideCapabilityProperty = analyzer.getProperty(PROVIDE_CAPABILITY);
            Assert.assertFalse(provideCapabilityProperty.contains("service.ranking:"));
            Parameters provideCapabilityHeaders = new Parameters(provideCapabilityProperty);
            List<Map.Entry<String, Attrs>> provides = new java.util.ArrayList(provideCapabilityHeaders.entrySet());
            Assert.assertEquals(provides.toString(), 2, provides.size());
            Map.Entry<String, Attrs> aggregateEntry = provides.get(0);
            Assert.assertEquals(LIFERAY_RESOURCE_BUNDLE, aggregateEntry.getKey());
            Attrs aggregateEntryAttrs = aggregateEntry.getValue();
            Assert.assertEquals(6, aggregateEntryAttrs.size());
            Assert.assertEquals("true", aggregateEntryAttrs.get("aggregate"));
            StringBuilder sb = new StringBuilder();
            sb.append("(&(bundle.symbolic.name=resources.test)(!(aggregate=true))),");
            sb.append("(bundle.symbolic.name=resources.lang1),");
            sb.append("(bundle.symbolic.name=resources.lang2),");
            sb.append("(bundle.symbolic.name=resources.lang3)");
            Assert.assertEquals(sb.toString(), aggregateEntryAttrs.get("resource.bundle.aggregate"));
            Assert.assertEquals("resources.test", aggregateEntryAttrs.get("bundle.symbolic.name"));
            Assert.assertEquals("content.Language", aggregateEntryAttrs.get("resource.bundle.base.name"));
            Assert.assertEquals("resources.test", aggregateEntryAttrs.get("servlet.context.name"));
            Map.Entry<String, Attrs> liferayResourceBundleEntry = provides.get(1);
            Assert.assertEquals(((LIFERAY_RESOURCE_BUNDLE) + "~"), liferayResourceBundleEntry.getKey());
            Attrs liferayResourceBundleAttrs = liferayResourceBundleEntry.getValue();
            Assert.assertEquals(2, liferayResourceBundleAttrs.size());
            Assert.assertEquals("resources.test", liferayResourceBundleAttrs.get("bundle.symbolic.name"));
            Assert.assertEquals("content.Language", liferayResourceBundleAttrs.get("resource.bundle.base.name"));
        }
    }

    @Test
    public void testAggregateResourceBundlesInstructionWebContextPath() throws Exception {
        ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin = new ResourceBundleLoaderAnalyzerPlugin();
        InputStream inputStream = ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream("dependencies/blade.language.web.jar");
        try (Jar jar = new Jar("dot", inputStream);Analyzer analyzer = new Analyzer()) {
            analyzer.setBundleSymbolicName("blade.language.web");
            analyzer.setJar(jar);
            analyzer.setProperty("-liferay-aggregate-resource-bundles", "blade.language");
            analyzer.setProperty("Web-ContextPath", "/blade-language-web");
            Assert.assertTrue(resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));
            Parameters provideCapabilityHeaders = new Parameters(analyzer.getProperty(PROVIDE_CAPABILITY));
            List<Map.Entry<String, Attrs>> provides = new java.util.ArrayList(provideCapabilityHeaders.entrySet());
            Map.Entry<String, Attrs> entry = provides.get(0);
            Attrs attrs = entry.getValue();
            Assert.assertEquals("blade-language-web", attrs.get("servlet.context.name"));
        }
    }

    @Test
    public void testProvideLiferayResourceBundleCapabilityAdded() throws Exception {
        ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin = new ResourceBundleLoaderAnalyzerPlugin();
        InputStream inputStream = ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream("dependencies/resources.test1.jar");
        try (Jar jar = new Jar("dot", inputStream);Analyzer analyzer = new Analyzer()) {
            analyzer.setJar(jar);
            Assert.assertTrue(resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));
            Parameters provideCapabilityHeaders = new Parameters(analyzer.getProperty(PROVIDE_CAPABILITY));
            Attrs attrs = provideCapabilityHeaders.get(LIFERAY_RESOURCE_BUNDLE);
            Assert.assertNotNull(attrs);
            Assert.assertTrue(attrs.containsKey("bundle.symbolic.name"));
            Assert.assertTrue(attrs.containsKey("resource.bundle.base.name"));
        }
    }

    @Test
    public void testProvideLiferayResourceBundleCapabilityNotAdded() throws Exception {
        ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin = new ResourceBundleLoaderAnalyzerPlugin();
        InputStream inputStream = ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream("dependencies/resources.test2.jar");
        try (Jar jar = new Jar("dot", inputStream);Analyzer analyzer = new Analyzer()) {
            analyzer.setJar(jar);
            Assert.assertFalse(resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));
            Parameters provideCapabilityHeaders = new Parameters(analyzer.getProperty(PROVIDE_CAPABILITY));
            Attrs attrs = provideCapabilityHeaders.get(LIFERAY_RESOURCE_BUNDLE);
            Assert.assertNull(attrs);
        }
    }
}

