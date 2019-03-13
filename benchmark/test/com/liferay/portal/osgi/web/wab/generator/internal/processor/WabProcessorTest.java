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
package com.liferay.portal.osgi.web.wab.generator.internal.processor;


import Attrs.LIST_STRING;
import Constants.CDIANNOTATIONS;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.bnd.version.Version;
import aQute.lib.filter.Filter;
import com.liferay.portal.kernel.deploy.auto.context.AutoDeploymentContext;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Raymond Aug?
 */
public class WabProcessorTest {
    @Test
    public void testClassicThemeWab() throws Exception {
        File file = getFile("/classic-theme.autodeployed.war");
        try (Jar jar = new Jar(file)) {
            Assert.assertNull(jar.getBsn());
            Map<String, Resource> resources = jar.getResources();
            Assert.assertEquals(resources.toString(), 1244, resources.size());
        }
        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("Bundle-Version", new String[]{ "7.0.0.8" });
        parameters.put("Web-ContextPath", new String[]{ "/classic-theme" });
        WabProcessor wabProcessor = new WabProcessorTest.TestWabProcessor(file, parameters);
        File processedFile = wabProcessor.getProcessedFile();
        Assert.assertNotNull(processedFile);
        try (Jar jar = new Jar(processedFile)) {
            Map<String, Map<String, Resource>> directories = jar.getDirectories();
            Map<String, Resource> resources = jar.getResources();
            // Check to see that the right number of resources are in the WAB
            Assert.assertEquals(resources.toString(), 1240, resources.size());
            // Check if the basic metadata is correct
            Assert.assertEquals("classic-theme", jar.getBsn());
            Assert.assertEquals("7.0.0.8", jar.getVersion());
            // Assert that the Bundle-ClassPath is properly formed to our
            // conventions
            Domain domain = Domain.domain(jar.getManifest());
            Parameters bundleClassPath = domain.getBundleClassPath();
            Assert.assertEquals(4, bundleClassPath.size());
            Assert.assertTrue(bundleClassPath.containsKey("ext/WEB-INF/classes"));
            for (String bundleClassPathEntry : bundleClassPath.keySet()) {
                if (bundleClassPathEntry.equals("ext/WEB-INF/classes")) {
                    Assert.assertNull(resources.get(bundleClassPathEntry));
                } else
                    if (bundleClassPathEntry.equals("WEB-INF/classes")) {
                        Assert.assertNull(resources.get(bundleClassPathEntry));
                        Assert.assertTrue(directories.containsKey(bundleClassPathEntry));
                    } else {
                        // Check that all the libraries on the Bundle-ClassPath
                        // exist in the WAB
                        Assert.assertNotNull(resources.get(bundleClassPathEntry));
                    }

            }
            Parameters importedPackages = domain.getImportPackage();
            // Check basic servlet and jsp packages are imported
            Assert.assertTrue(importedPackages.containsKey("javax.servlet"));
            Assert.assertTrue(importedPackages.containsKey("javax.servlet.http"));
            // Check if packages declared in portal property
            // module.framework.web.generator.default.servlet.packages are
            // included
            Assert.assertTrue(importedPackages.containsKey("com.liferay.portal.model"));
            Assert.assertTrue(importedPackages.containsKey("com.liferay.portal.service"));
            Assert.assertTrue(importedPackages.containsKey("com.liferay.portal.servlet.filters.aggregate"));
            Assert.assertTrue(importedPackages.containsKey("com.liferay.portal.osgi.web.servlet.jsp.compiler"));
            Assert.assertTrue(importedPackages.containsKey("com.liferay.portal.spring.context"));
            Assert.assertTrue(importedPackages.containsKey("com.liferay.portal.util"));
            Assert.assertTrue(importedPackages.containsKey("com.liferay.portlet"));
            Assert.assertTrue(importedPackages.containsKey("com.sun.el"));
            Assert.assertTrue(importedPackages.containsKey("org.apache.commons.chain.generic"));
            Assert.assertTrue(importedPackages.containsKey("org.apache.naming.java"));
            // Check if packages only referenced in web.xml are imported
            Assert.assertTrue(importedPackages.containsKey("com.liferay.portal.kernel.servlet.filters.invoker"));
            Assert.assertTrue(importedPackages.containsKey("com.liferay.portal.webserver"));
        }
    }

    @Test
    public void testFatCDIWabOptsOutOfOSGiCDIIntegration() throws Exception {
        File file = getFile("/jsf.cdi.applicant.portlet.war");
        WabProcessor wabProcessor = new WabProcessorTest.TestWabProcessor(file, Collections.singletonMap("Web-ContextPath", new String[]{ "/jsf-cdi-applicant-portlet" }));
        File processedFile = wabProcessor.getProcessedFile();
        Assert.assertNotNull(processedFile);
        try (Jar jar = new Jar(processedFile)) {
            // Check if the basic metadata is correct
            Assert.assertEquals("jsf-cdi-applicant-portlet", jar.getBsn());
            Assert.assertEquals("4.1.2", jar.getVersion());
            // Does this WAR have a beans.xml file that would trigger
            // OSGi CDI Integration analysis?
            Resource beansXMLFile = jar.getResource("WEB-INF/beans.xml");
            Assert.assertNotNull(beansXMLFile);
            // Did the beans.xml file have a discovery mode of none?
            try (InputStream inputStream = beansXMLFile.openInputStream()) {
                Document document = SAXReaderUtil.read(inputStream);
                Node beanDiscoveryMode = document.selectSingleNode("/beans/@bean-discovery-mode");
                String value = beanDiscoveryMode.getStringValue();
                Assert.assertNotEquals("none", value);
            }
            // Now that we've established CDI discovery would kick
            // in, check to see if the WAB opted-out of integration by
            // having the "-cdiannotations" instruction set to the empty
            // value in liferay-plugin-package.properties.
            Resource packageProperties = jar.getResource("WEB-INF/liferay-plugin-package.properties");
            Properties properties = new Properties();
            try (InputStream inputStream = packageProperties.openInputStream()) {
                properties.load(inputStream);
            }
            Assert.assertEquals("", properties.getProperty(CDIANNOTATIONS));
            // Finally, make sure no requirement on the OSGi CDI
            // Integration extender was added to the manifest
            Domain domain = Domain.domain(jar.getManifest());
            Parameters requirements = domain.getRequireCapability();
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("osgi.extender", "osgi.cdi");
            arguments.put("version", new Version(1));
            for (Attrs attrs : requirements.values()) {
                String filterString = attrs.get("filter:");
                if (filterString == null) {
                    continue;
                }
                Filter filter = new Filter(filterString);
                Assert.assertFalse(filter.matchMap(arguments));
            }
        }
    }

    @Test
    public void testSkinnyCDIWabGainsOSGiCDIIntegration() throws Exception {
        File file = getFile("/PortletV3AnnotatedDemo.war");
        WabProcessor wabProcessor = new WabProcessorTest.TestWabProcessor(file, Collections.singletonMap("Web-ContextPath", new String[]{ "/portlet-V3-annotated-demo" }));
        File processedFile = wabProcessor.getProcessedFile();
        Assert.assertNotNull(processedFile);
        try (Jar jar = new Jar(processedFile)) {
            // Check if the basic metadata is correct
            Assert.assertEquals("portlet-V3-annotated-demo", jar.getBsn());
            Assert.assertEquals("1.0.0", jar.getVersion());
            // Does this WAR have a beans.xml file that would trigger
            // OSGi CDI Integration analysis?
            Resource beansXMLFile = jar.getResource("WEB-INF/beans.xml");
            Assert.assertNotNull(beansXMLFile);
            // Did the beans.xml file have a discovery mode of none?
            try (InputStream inputStream = beansXMLFile.openInputStream()) {
                Document document = SAXReaderUtil.read(inputStream);
                Node beanDiscoveryMode = document.selectSingleNode("/beans/@bean-discovery-mode");
                String value = beanDiscoveryMode.getStringValue();
                Assert.assertNotEquals("none", value);
            }
            // Now that we've established CDI discovery would kick
            // in, check to see if the WAB opted-out of integration by
            // having the "-cdiannotations" instruction set to the empty
            // value in liferay-plugin-package.properties.
            Resource packageProperties = jar.getResource("WEB-INF/liferay-plugin-package.properties");
            Properties properties = new Properties();
            try (InputStream inputStream = packageProperties.openInputStream()) {
                properties.load(inputStream);
            }
            Assert.assertFalse(properties.containsKey(CDIANNOTATIONS));
            // Finally, make sure the requirement on the OSGi CDI
            // Integration extender was added to the manifest
            Domain domain = Domain.domain(jar.getManifest());
            Parameters requirements = domain.getRequireCapability();
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("osgi.extender", "osgi.cdi");
            arguments.put("version", new Version(1));
            Map.Entry<String, Attrs> entry = findRequirement(requirements, "osgi.extender", arguments);
            Assert.assertNotNull(entry);
            // Assert the expected number of beans were discovered
            Attrs attrs = entry.getValue();
            List<String> beans = attrs.getTyped(LIST_STRING, "beans");
            Assert.assertEquals(beans.toString(), 23, beans.size());
            // Make sure other CDI requirements were added
            // The bean portlet extension
            Assert.assertNotNull(findRequirement(requirements, "osgi.cdi.extension", Collections.singletonMap("osgi.cdi.extension", "com.liferay.bean.portlet.cdi.extension")));
            // The http extension
            Assert.assertNotNull(findRequirement(requirements, "osgi.cdi.extension", Collections.singletonMap("osgi.cdi.extension", "aries.cdi.http")));
            // The EL extension
            Assert.assertNotNull(findRequirement(requirements, "osgi.cdi.extension", Collections.singletonMap("osgi.cdi.extension", "aries.cdi.el.jsp")));
        }
    }

    @Test
    public void testThatEmbeddedLibsAreHandledProperly() throws Exception {
        File file = getFile("/tck-V3URLTests.wab.war");
        WabProcessor wabProcessor = new WabProcessorTest.TestWabProcessor(file, Collections.singletonMap("Web-ContextPath", new String[]{ "/portlet-V3-annotated-demo" }));
        File processedFile = wabProcessor.getProcessedFile();
        Assert.assertNotNull(processedFile);
        try (Jar jar = new Jar(processedFile)) {
            // Check if the basic metadata is correct
            Assert.assertEquals("portlet-V3-annotated-demo", jar.getBsn());
            Assert.assertEquals("1.0.0", jar.getVersion());
            // Does this WAR have a beans.xml file that would trigger
            // OSGi CDI Integration analysis?
            Resource beansXMLFile = jar.getResource("WEB-INF/beans.xml");
            Assert.assertNotNull(beansXMLFile);
            // Did the beans.xml file have a discovery mode of none?
            try (InputStream inputStream = beansXMLFile.openInputStream()) {
                Document document = SAXReaderUtil.read(inputStream);
                Node beanDiscoveryMode = document.selectSingleNode("/beans/@bean-discovery-mode");
                String value = beanDiscoveryMode.getStringValue();
                Assert.assertNotEquals("none", value);
            }
            // Finally, make sure the requirement on the OSGi CDI
            // Integration extender was added to the manifest
            Domain domain = Domain.domain(jar.getManifest());
            Parameters requirements = domain.getRequireCapability();
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("osgi.extender", "osgi.cdi");
            arguments.put("version", new Version(1));
            Map.Entry<String, Attrs> entry = findRequirement(requirements, "osgi.extender", arguments);
            Assert.assertNotNull(entry);
            // Assert the expected number of beans were discovered
            Attrs attrs = entry.getValue();
            List<String> beans = attrs.getTyped(LIST_STRING, "beans");
            Assert.assertEquals(beans.toString(), 5, beans.size());
            List<String> expectedList = Arrays.asList("javax.portlet.tck.portlets.URLTests_ActionURL", "javax.portlet.tck.portlets.URLTests_BaseURL", "javax.portlet.tck.portlets.URLTests_RenderURL", "javax.portlet.tck.portlets.URLTests_ResourceURL", "javax.portlet.tck.util.ModuleTestCaseDetails");
            Assert.assertEquals(expectedList.toString(), expectedList, beans);
            // Make sure other CDI requirements were added
            // The bean portlet extension
            Assert.assertNotNull(findRequirement(requirements, "osgi.cdi.extension", Collections.singletonMap("osgi.cdi.extension", "com.liferay.bean.portlet.cdi.extension")));
            // The http extension
            Assert.assertNotNull(findRequirement(requirements, "osgi.cdi.extension", Collections.singletonMap("osgi.cdi.extension", "aries.cdi.http")));
            // The EL extension
            Assert.assertNotNull(findRequirement(requirements, "osgi.cdi.extension", Collections.singletonMap("osgi.cdi.extension", "aries.cdi.el.jsp")));
        }
    }

    private static class TestWabProcessor extends WabProcessor {
        @Override
        protected void executeAutoDeployers(AutoDeploymentContext autoDeploymentContext) {
            try {
                File deployDir = autoDeploymentContext.getDeployDir();
                File parent = deployDir.getParentFile();
                Stream<Path> pathsStream = Files.walk(parent.toPath());
                pathsStream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                parent.mkdirs();
                File newFile = new File(parent, _file.getName());
                Files.copy(_file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        private TestWabProcessor(File file, Map<String, String[]> parameters) {
            super(WabProcessorTest.TestWabProcessor.class.getClassLoader(), file, parameters);
            _file = file;
        }

        private final File _file;
    }
}

