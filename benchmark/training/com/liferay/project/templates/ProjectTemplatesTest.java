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
package com.liferay.project.templates;


import WorkspaceUtil.WORKSPACE;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import com.liferay.maven.executor.MavenExecutor;
import com.liferay.project.templates.internal.util.FileUtil;
import com.liferay.project.templates.internal.util.ProjectTemplatesUtil;
import com.liferay.project.templates.internal.util.Validator;
import com.liferay.project.templates.util.FileTestUtil;
import com.liferay.project.templates.util.StringTestUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPathExpression;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *
 *
 * @author Lawrence Lee
 * @author Gregory Amerson
 * @author Andrea Di Giorgi
 */
public class ProjectTemplatesTest {
    @ClassRule
    public static final MavenExecutor mavenExecutor = new MavenExecutor();

    @ClassRule
    public static final TemporaryFolder testCaseTemporaryFolder = new TemporaryFolder();

    @Test
    public void testBuildTemplate() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle(null, "hello-world-portlet");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/resources/META-INF/resources/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/resources/META-INF/resources/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/hello/world/portlet/portlet/HelloWorldPortlet.java", "public class HelloWorldPortlet extends MVCPortlet {");
        File mavenProjectDir = _buildTemplateWithMaven("mvc-portlet", "hello-world-portlet", "com.test", "-DclassName=HelloWorld", "-Dpackage=hello.world.portlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateActivator() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("activator", "bar-activator");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_OSGI_CORE) + ", version: \"6.0.0\""));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/bar/activator/BarActivator.java", "public class BarActivator implements BundleActivator {");
        File mavenProjectDir = _buildTemplateWithMaven("activator", "bar-activator", "com.test", "-DclassName=BarActivator", "-Dpackage=bar.activator");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
        if (ProjectTemplatesTest._isBuildProjects()) {
            File jarFile = ProjectTemplatesTest._testExists(gradleProjectDir, "build/libs/bar.activator-1.0.0.jar");
            Domain domain = Domain.domain(jarFile);
            Parameters parameters = domain.getImportPackage();
            Assert.assertNotNull(parameters);
            Attrs attrs = parameters.get("org.osgi.framework");
            Assert.assertNotNull(attrs);
        }
    }

    @Test
    public void testBuildTemplateActivatorInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("activator", "bar-activator", "build/libs/bar.activator-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateActivatorWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("activator", "activator-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_OSGI_CORE) + "\n"));
    }

    @Test
    public void testBuildTemplateApi() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("api", "foo");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_OSGI_CORE) + ", version: \"6.0.0\""));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/api/Foo.java", "public interface Foo");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/foo/api/packageinfo", "1.0.0");
        File mavenProjectDir = _buildTemplateWithMaven("api", "foo", "com.test", "-DclassName=Foo", "-Dpackage=foo");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
        if (ProjectTemplatesTest._isBuildProjects()) {
            File jarFile = ProjectTemplatesTest._testExists(gradleProjectDir, "build/libs/foo-1.0.0.jar");
            Domain domain = Domain.domain(jarFile);
            Parameters parameters = domain.getExportPackage();
            Assert.assertNotNull(parameters);
            Assert.assertNotNull(parameters.toString(), parameters.get("foo.api"));
        }
    }

    @Test
    public void testBuildTemplateApiContainsCorrectAuthor() throws Exception {
        String author = "Test Author";
        File gradleProjectDir = _buildTemplateWithGradle("api", "author-test", "--author", author);
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/author/test/api/AuthorTest.java", ("@author " + author));
        File mavenProjectDir = _buildTemplateWithMaven("api", "author-test", "com.test", ("-Dauthor=" + author), "-DclassName=AuthorTest", "-Dpackage=author.test");
        ProjectTemplatesTest._testContains(mavenProjectDir, "src/main/java/author/test/api/AuthorTest.java", ("@author " + author));
    }

    @Test
    public void testBuildTemplateApiInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("api", "foo", "build/libs/foo-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateApiWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("api", "api-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_OSGI_CORE) + "\n"));
    }

    @Test
    public void testBuildTemplateContentDTDVersionLayoutTemplate70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("layout-template", "foo-bar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-layout-templates.xml", "liferay-layout-templates_7_0_0.dtd");
    }

    @Test
    public void testBuildTemplateContentDTDVersionLayoutTemplate71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("layout-template", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-layout-templates.xml", "liferay-layout-templates_7_1_0.dtd");
    }

    @Test
    public void testBuildTemplateContentDTDVersionServiceBuilder70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service-builder", "foo-bar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "foo-bar-service/service.xml", "liferay-service-builder_7_0_0.dtd");
    }

    @Test
    public void testBuildTemplateContentDTDVersionServiceBuilder71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service-builder", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "foo-bar-service/service.xml", "liferay-service-builder_7_1_0.dtd");
    }

    @Test
    public void testBuildTemplateContentDTDVersionSpringMVCPortlet70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("spring-mvc-portlet", "foo-bar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml", "liferay-display_7_0_0.dtd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml", "liferay-portlet-app_7_0_0.dtd");
    }

    @Test
    public void testBuildTemplateContentDTDVersionSpringMVCPortlet71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("spring-mvc-portlet", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml", "liferay-display_7_1_0.dtd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml", "liferay-portlet-app_7_1_0.dtd");
    }

    @Test
    public void testBuildTemplateContentDTDVersionWarHook70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-hook", "foo-bar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml", "liferay-hook_7_0_0.dtd");
    }

    @Test
    public void testBuildTemplateContentDTDVersionWarHook71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-hook", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml", "liferay-hook_7_1_0.dtd");
    }

    @Test
    public void testBuildTemplateContentDTDVersionWarMVCPortlet70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-mvc-portlet", "foo-bar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml", "liferay-display_7_0_0.dtd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml", "liferay-portlet-app_7_0_0.dtd");
    }

    @Test
    public void testBuildTemplateContentDTDVersionWarMVCPortlet71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-mvc-portlet", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml", "liferay-display_7_1_0.dtd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml", "liferay-portlet-app_7_1_0.dtd");
    }

    @Test
    public void testBuildTemplateContentTargetingReport() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-report", "foo-bar");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/bar/content/targeting/report/FooBarReport.java", "public class FooBarReport extends BaseJSPReport");
        File mavenProjectDir = _buildTemplateWithMaven("content-targeting-report", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateContentTargetingReport70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-report", "foo-bar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.3.0"));
        File mavenProjectDir = _buildTemplateWithMaven("content-targeting-report", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateContentTargetingReport71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-report", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("content-targeting-report", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar", "-DliferayVersion=7.1");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateContentTargetingReportInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("content-targeting-report", "foo-bar", "build/libs/foo.bar-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateContentTargetingReportWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-report", "report-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateContentTargetingRule() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-rule", "foo-bar");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/bar/content/targeting/rule/FooBarRule.java", "public class FooBarRule extends BaseJSPRule");
        File mavenProjectDir = _buildTemplateWithMaven("content-targeting-rule", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateContentTargetingRule70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-rule", "foo-bar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.3.0"));
        File mavenProjectDir = _buildTemplateWithMaven("content-targeting-rule", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateContentTargetingRule71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-rule", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("content-targeting-rule", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar", "-DliferayVersion=7.1");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateContentTargetingRuleInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("content-targeting-rule", "foo-bar", "build/libs/foo.bar-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateContentTargetingRuleWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-rule", "rule-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateContentTargetingTrackingAction() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-tracking-action", "foo-bar");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/foo/bar/content/targeting/tracking/action" + "/FooBarTrackingAction.java"), "public class FooBarTrackingAction extends BaseJSPTrackingAction");
        File mavenProjectDir = _buildTemplateWithMaven("content-targeting-tracking-action", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateContentTargetingTrackingAction70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-tracking-action", "foo-bar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.3.0"));
        File mavenProjectDir = _buildTemplateWithMaven("content-targeting-tracking-action", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateContentTargetingTrackingAction71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-tracking-action", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("content-targeting-tracking-action", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar", "-DliferayVersion=7.1");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateContentTargetingTrackingActionInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("content-targeting-tracking-action", "foo-bar", "build/libs/foo.bar-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateContentTargetingTrackingActionWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("content-targeting-tracking-action", "tracking-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateControlMenuEntry70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("control-menu-entry", "foo-bar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0\""));
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/foo/bar/control/menu" + "/FooBarProductNavigationControlMenuEntry.java"), "public class FooBarProductNavigationControlMenuEntry", "extends BaseProductNavigationControlMenuEntry", "implements ProductNavigationControlMenuEntry");
        File mavenProjectDir = _buildTemplateWithMaven("control-menu-entry", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateControlMenuEntry71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("control-menu-entry", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0\""));
        File mavenProjectDir = _buildTemplateWithMaven("control-menu-entry", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateControlMenuEntryInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("control-menu-entry", "foo-bar", "build/libs/foo.bar-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateControlMenuEntryWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("control-menu-entry", "entry-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateExt() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("modules-ext", "loginExt", "--original-module-name", "com.liferay.login.web", "--original-module-version", "1.0.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "buildscript {", "repositories {", ("originalModule group: \"com.liferay\", name: " + "\"com.liferay.login.web\", version: \"1.0.0\""), "apply plugin: \"com.liferay.osgi.ext.plugin\"");
        if (ProjectTemplatesTest._isBuildProjects()) {
            ProjectTemplatesTest._executeGradle(gradleProjectDir, ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD);
            File jarFile = ProjectTemplatesTest._testExists(gradleProjectDir, "build/libs/com.liferay.login.web-1.0.0.ext.jar");
            Domain domain = Domain.domain(jarFile);
            Map.Entry<String, Attrs> bundleSymbolicName = domain.getBundleSymbolicName();
            Assert.assertEquals(bundleSymbolicName.toString(), "com.liferay.login.web", bundleSymbolicName.getKey());
        }
    }

    @Test
    public void testBuildTemplateExtInWorkspace() throws Exception {
        File workspaceDir = _buildWorkspace();
        File workspaceProjectDir = ProjectTemplatesTest._buildTemplateWithGradle(new File(workspaceDir, "ext"), "modules-ext", "loginExt", "--original-module-name", "com.liferay.login.web", "--original-module-version", "1.0.0");
        ProjectTemplatesTest._testContains(workspaceProjectDir, "build.gradle", ("originalModule group: \"com.liferay\", name: " + "\"com.liferay.login.web\", version: \"1.0.0\""));
        ProjectTemplatesTest._testNotContains(workspaceProjectDir, "build.gradle", true, "^repositories \\{.*");
        ProjectTemplatesTest._executeGradle(workspaceDir, ":ext:loginExt:build");
        ProjectTemplatesTest._testExists(workspaceProjectDir, "build/libs/com.liferay.login.web-1.0.0.ext.jar");
    }

    @Test
    public void testBuildTemplateFMPortletWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("freemarker-portlet", "freemarker-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateFormField70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("form-field", "foobar", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Bundle-Name: foobar", "Web-ContextPath: /dynamic-data-foobar-form-field");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foobar/form/field/FoobarDDMFormFieldRenderer.java", "property = \"ddm.form.field.type.name=foobar\"", ("public class FoobarDDMFormFieldRenderer extends " + "BaseDDMFormFieldRenderer {"), "ddm.Foobar", "/META-INF/resources/foobar.soy");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foobar/form/field/FoobarDDMFormFieldType.java", "ddm.form.field.type.js.class.name=Liferay.DDM.Field.Foobar", "ddm.form.field.type.js.module=foobar-form-field", "ddm.form.field.type.label=foobar-label", "ddm.form.field.type.name=foobar", "public class FoobarDDMFormFieldType extends BaseDDMFormFieldType", "return \"foobar\";");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/config.js", "foobar-group", "'foobar-form-field': {", "path: 'foobar_field.js',", "'foobar-form-field-template': {");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/foobar.soy", "{namespace ddm}", "{template .Foobar autoescape", "<div class=\"form-group foobar-form-field\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/foobar_field.js", "'foobar-form-field',", "var FoobarField", "value: 'foobar-form-field'", "NAME: 'foobar-form-field'", "Liferay.namespace('DDM.Field').Foobar = FoobarField;");
        File mavenProjectDir = _buildTemplateWithMaven("form-field", "foobar", "com.test", "-DclassName=Foobar", "-Dpackage=foobar", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateFormField71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("form-field", "foobar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Bundle-Name: foobar", "Web-ContextPath: /dynamic-data-foobar-form-field");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, "package.json", "\"name\": \"dynamic-data-foobar-form-field\"", ",foobar_field.js &&");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foobar/form/field/FoobarDDMFormFieldRenderer.java", "property = \"ddm.form.field.type.name=foobar\"", ("public class FoobarDDMFormFieldRenderer extends " + "BaseDDMFormFieldRenderer {"), "DDMFoobar.render", "/META-INF/resources/foobar.soy");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foobar/form/field/FoobarDDMFormFieldType.java", "ddm.form.field.type.description=foobar-description", "ddm.form.field.type.js.class.name=Liferay.DDM.Field.Foobar", "ddm.form.field.type.js.module=foobar-form-field", "ddm.form.field.type.label=foobar-label", "ddm.form.field.type.name=foobar", "public class FoobarDDMFormFieldType extends BaseDDMFormFieldType", "return \"foobar\";");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/config.js", "field-foobar", "'foobar-form-field': {", "path: 'foobar_field.js',");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/foobar.soy", "{namespace DDMFoobar}", "variant=\"\'foobar\'\"", "foobar-form-field");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/foobar.es.js", "import templates from './foobar.soy';", "* Foobar Component", "class Foobar extends Component", "Soy.register(Foobar,", "!window.DDMFoobar", "window.DDMFoobar", "window.DDMFoobar.render = Foobar;", "export default Foobar;");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/foobar_field.js", "'foobar-form-field',", "var FoobarField", "value: 'foobar-form-field'", "NAME: 'foobar-form-field'", "Liferay.namespace('DDM.Field').Foobar = FoobarField;");
        File mavenProjectDir = _buildTemplateWithMaven("form-field", "foobar", "com.test", "-DclassName=Foobar", "-Dpackage=foobar", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateFormField71WithHyphen() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("form-field", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Bundle-Name: foo-bar", "Web-ContextPath: /dynamic-data-foo-bar-form-field");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, "package.json", "\"name\": \"dynamic-data-foo-bar-form-field\"", ",foo-bar_field.js &&");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/bar/form/field/FooBarDDMFormFieldRenderer.java", "property = \"ddm.form.field.type.name=fooBar\"", ("public class FooBarDDMFormFieldRenderer extends " + "BaseDDMFormFieldRenderer {"), "DDMFooBar.render", "/META-INF/resources/foo-bar.soy");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/bar/form/field/FooBarDDMFormFieldType.java", "ddm.form.field.type.description=foo-bar-description", "ddm.form.field.type.js.class.name=Liferay.DDM.Field.FooBar", "ddm.form.field.type.js.module=foo-bar-form-field", "ddm.form.field.type.label=foo-bar-label", "ddm.form.field.type.name=fooBar", "public class FooBarDDMFormFieldType extends BaseDDMFormFieldType", "return \"fooBar\";");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/config.js", "field-foo-bar", "'foo-bar-form-field': {", "path: 'foo-bar_field.js',");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/foo-bar.soy", "{namespace DDMFooBar}", "variant=\"\'fooBar\'\"", "foo-bar-form-field");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/foo-bar.es.js", "import templates from './foo-bar.soy';", "* FooBar Component", "class FooBar extends Component", "Soy.register(FooBar,", "!window.DDMFooBar", "window.DDMFooBar", "window.DDMFooBar.render = FooBar;", "export default FooBar;");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/foo-bar_field.js", "'foo-bar-form-field',", "var FooBarField", "value: 'foo-bar-form-field'", "NAME: 'foo-bar-form-field'", "Liferay.namespace('DDM.Field').FooBar = FooBarField;");
        File mavenProjectDir = _buildTemplateWithMaven("form-field", "foo-bar", "com.test", "-DclassName=FooBar", "-Dpackage=foo.bar", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateFormFieldInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("form-field", "foobar", "build/libs/foobar-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateFormFieldWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("form-field", "field-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateFragment() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("fragment", "loginhook", "--host-bundle-symbolic-name", "com.liferay.login.web", "--host-bundle-version", "1.0.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Bundle-SymbolicName: loginhook", "Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"");
        File mavenProjectDir = _buildTemplateWithMaven("fragment", "loginhook", "com.test", "-DhostBundleSymbolicName=com.liferay.login.web", "-DhostBundleVersion=1.0.0", "-Dpackage=loginhook");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
        if (ProjectTemplatesTest._isBuildProjects()) {
            File jarFile = ProjectTemplatesTest._testExists(gradleProjectDir, "build/libs/loginhook-1.0.0.jar");
            Domain domain = Domain.domain(jarFile);
            Map.Entry<String, Attrs> fragmentHost = domain.getFragmentHost();
            Assert.assertNotNull(fragmentHost);
            Assert.assertEquals(fragmentHost.toString(), "com.liferay.login.web", fragmentHost.getKey());
        }
    }

    @Test
    public void testBuildTemplateFreeMarkerPortlet70() throws Exception {
        File gradleProjectDir = _testBuildTemplatePortlet70("freemarker-portlet", "FreeMarkerPortlet", "templates/init.ftl", "templates/view.ftl");
        ProjectTemplatesTest._testStartsWith(gradleProjectDir, "src/main/resources/templates/view.ftl", ProjectTemplatesTest._FREEMARKER_PORTLET_VIEW_FTL_PREFIX);
    }

    @Test
    public void testBuildTemplateFreeMarkerPortlet71() throws Exception {
        _testBuildTemplatePortlet71("freemarker-portlet", "FreeMarkerPortlet", "templates/init.ftl", "templates/view.ftl");
    }

    @Test
    public void testBuildTemplateFreeMarkerPortletInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("freemarker-portlet", "foo", "build/libs/foo-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateFreeMarkerPortletWithPackage70() throws Exception {
        File gradleProjectDir = _testBuildTemplatePortletWithPackage70("freemarker-portlet", "FreeMarkerPortlet", "templates/init.ftl", "templates/view.ftl");
        ProjectTemplatesTest._testStartsWith(gradleProjectDir, "src/main/resources/templates/view.ftl", ProjectTemplatesTest._FREEMARKER_PORTLET_VIEW_FTL_PREFIX);
    }

    @Test
    public void testBuildTemplateFreeMarkerPortletWithPackage71() throws Exception {
        File gradleProjectDir = _testBuildTemplatePortletWithPackage71("freemarker-portlet", "FreeMarkerPortlet", "templates/init.ftl", "templates/view.ftl");
        ProjectTemplatesTest._testStartsWith(gradleProjectDir, "src/main/resources/templates/view.ftl", ProjectTemplatesTest._FREEMARKER_PORTLET_VIEW_FTL_PREFIX);
    }

    @Test
    public void testBuildTemplateFreeMarkerPortletWithPortletName70() throws Exception {
        File gradleProjectDir = _testBuildTemplatePortletWithPortletName70("freemarker-portlet", "FreeMarkerPortlet", "templates/init.ftl", "templates/view.ftl");
        ProjectTemplatesTest._testStartsWith(gradleProjectDir, "src/main/resources/templates/view.ftl", ProjectTemplatesTest._FREEMARKER_PORTLET_VIEW_FTL_PREFIX);
    }

    @Test
    public void testBuildTemplateFreeMarkerPortletWithPortletName71() throws Exception {
        File gradleProjectDir = _testBuildTemplatePortletWithPortletName71("freemarker-portlet", "FreeMarkerPortlet", "templates/init.ftl", "templates/view.ftl");
        ProjectTemplatesTest._testStartsWith(gradleProjectDir, "src/main/resources/templates/view.ftl", ProjectTemplatesTest._FREEMARKER_PORTLET_VIEW_FTL_PREFIX);
    }

    @Test
    public void testBuildTemplateFreeMarkerPortletWithPortletSuffix70() throws Exception {
        File gradleProjectDir = _testBuildTemplatePortletWithPortletSuffix70("freemarker-portlet", "FreeMarkerPortlet", "templates/init.ftl", "templates/view.ftl");
        ProjectTemplatesTest._testStartsWith(gradleProjectDir, "src/main/resources/templates/view.ftl", ProjectTemplatesTest._FREEMARKER_PORTLET_VIEW_FTL_PREFIX);
    }

    @Test
    public void testBuildTemplateFreeMarkerPortletWithPortletSuffix71() throws Exception {
        File gradleProjectDir = _testBuildTemplatePortletWithPortletSuffix71("freemarker-portlet", "FreeMarkerPortlet", "templates/init.ftl", "templates/view.ftl");
        ProjectTemplatesTest._testStartsWith(gradleProjectDir, "src/main/resources/templates/view.ftl", ProjectTemplatesTest._FREEMARKER_PORTLET_VIEW_FTL_PREFIX);
    }

    @Test
    public void testBuildTemplateInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace(null, "hello-world-portlet", "build/libs/hello.world.portlet-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateLayoutTemplate() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("layout-template", "foo");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/foo.png");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/foo.ftl", "class=\"foo\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-layout-templates.xml", "<layout-template id=\"foo\" name=\"foo\">", "<template-path>/foo.ftl</template-path>", "<thumbnail-path>/foo.png</thumbnail-path>");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties", "name=foo");
        ProjectTemplatesTest._testEquals(gradleProjectDir, "build.gradle", "apply plugin: \"war\"");
        File mavenProjectDir = _buildTemplateWithMaven("layout-template", "foo", "com.test");
        ProjectTemplatesTest._createNewFiles("src/main/resources/.gitkeep", gradleProjectDir, mavenProjectDir);
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildTemplateLiferayVersionInvalid62() throws Exception {
        _buildTemplateWithGradle("mvc-portlet", "test", "--liferayVersion", "6.2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildTemplateLiferayVersionInvalid70test() throws Exception {
        _buildTemplateWithGradle("mvc-portlet", "test", "--liferayVersion", "7.0test");
    }

    @Test
    public void testBuildTemplateLiferayVersionValid70() throws Exception {
        _buildTemplateWithGradle("mvc-portlet", "test", "--liferayVersion", "7.0");
    }

    @Test
    public void testBuildTemplateLiferayVersionValid712() throws Exception {
        _buildTemplateWithGradle("mvc-portlet", "test", "--liferayVersion", "7.1.2");
    }

    @Test
    public void testBuildTemplateModulesExtGradle() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("modules-ext", "foo-ext", "--original-module-name", "com.liferay.login.web", "--original-module-version", "2.0.4");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "originalModule group: \"com.liferay\", ", "name: \"com.liferay.login.web\", version: \"2.0.4\"");
        if (ProjectTemplatesTest._isBuildProjects()) {
            ProjectTemplatesTest._executeGradle(gradleProjectDir, ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD);
            File gradleOutputDir = new File(gradleProjectDir, "build/libs");
            Path gradleOutputPath = FileTestUtil.getFile(gradleOutputDir.toPath(), ProjectTemplatesTest._OUTPUT_FILENAME_GLOB_REGEX, 1);
            Assert.assertNotNull(gradleOutputPath);
            Assert.assertTrue(Files.exists(gradleOutputPath));
        }
    }

    @Test
    public void testBuildTemplateModulesExtMaven() throws Exception {
        String groupId = "com.test";
        String name = "foo-ext";
        String template = "modules-ext";
        List<String> completeArgs = new ArrayList<>();
        completeArgs.add("archetype:generate");
        completeArgs.add("--batch-mode");
        String archetypeArtifactId = "com.liferay.project.templates." + (template.replace('-', '.'));
        completeArgs.add(("-DarchetypeArtifactId=" + archetypeArtifactId));
        String projectTemplateVersion = ProjectTemplatesUtil.getArchetypeVersion(archetypeArtifactId);
        Assert.assertTrue("Unable to get project template version", Validator.isNotNull(projectTemplateVersion));
        completeArgs.add("-DarchetypeGroupId=com.liferay");
        completeArgs.add(("-DarchetypeVersion=" + projectTemplateVersion));
        completeArgs.add(("-DartifactId=" + name));
        completeArgs.add(("-Dauthor=" + (System.getProperty("user.name"))));
        completeArgs.add(("-DgroupId=" + groupId));
        completeArgs.add("-DliferayVersion=7.1");
        completeArgs.add("-DoriginalModuleName=com.liferay.login.web");
        completeArgs.add("-DoriginalModuleVersion=3.0.4");
        completeArgs.add("-DprojectType=standalone");
        completeArgs.add("-Dversion=1.0.0");
        File destinationDir = temporaryFolder.newFolder("maven");
        ProjectTemplatesTest._executeMaven(destinationDir, completeArgs.toArray(new String[0]));
        File projectDir = new File(destinationDir, name);
        ProjectTemplatesTest._testContains(projectDir, "build.gradle", "originalModule group: \"com.liferay\", ", "name: \"com.liferay.login.web\", version: \"3.0.4\"");
        ProjectTemplatesTest._testNotExists(projectDir, "pom.xml");
    }

    @Test
    public void testBuildTemplateMVCPortlet70() throws Exception {
        _testBuildTemplatePortlet70("mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp", "META-INF/resources/view.jsp");
    }

    @Test
    public void testBuildTemplateMVCPortlet71() throws Exception {
        _testBuildTemplatePortlet71("mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp", "META-INF/resources/view.jsp");
    }

    @Test
    public void testBuildTemplateMVCPortletInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("mvc-portlet", "foo", "build/libs/foo-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateMVCPortletWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("mvc-portlet", "mvc-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateMVCPortletWithPackage70() throws Exception {
        _testBuildTemplatePortletWithPackage70("mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp", "META-INF/resources/view.jsp");
    }

    @Test
    public void testBuildTemplateMVCPortletWithPackage71() throws Exception {
        _testBuildTemplatePortletWithPackage71("mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp", "META-INF/resources/view.jsp");
    }

    @Test
    public void testBuildTemplateMVCPortletWithPortletName70() throws Exception {
        _testBuildTemplatePortletWithPortletName70("mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp", "META-INF/resources/view.jsp");
    }

    @Test
    public void testBuildTemplateMVCPortletWithPortletName71() throws Exception {
        _testBuildTemplatePortletWithPortletName71("mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp", "META-INF/resources/view.jsp");
    }

    @Test
    public void testBuildTemplateMVCPortletWithPortletSuffix70() throws Exception {
        _testBuildTemplatePortletWithPortletSuffix70("mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp", "META-INF/resources/view.jsp");
    }

    @Test
    public void testBuildTemplateMVCPortletWithPortletSuffix71() throws Exception {
        _testBuildTemplatePortletWithPortletSuffix71("mvc-portlet", "MVCPortlet", "META-INF/resources/init.jsp", "META-INF/resources/view.jsp");
    }

    @Test
    public void testBuildTemplateNAPortletWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("npm-angular-portlet", "angular-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateNpmAngularPortlet70() throws Exception {
        _testBuildTemplateNpmAngular70("npm-angular-portlet", "foo", "foo", "Foo");
    }

    @Test
    public void testBuildTemplateNpmAngularPortlet71() throws Exception {
        _testBuildTemplateNpmAngular71("npm-angular-portlet", "foo", "foo", "Foo");
    }

    @Test
    public void testBuildTemplateNpmAngularPortletWithDashes70() throws Exception {
        _testBuildTemplateNpmAngular70("npm-angular-portlet", "foo-bar", "foo.bar", "FooBar");
    }

    @Test
    public void testBuildTemplateNpmAngularPortletWithDashes71() throws Exception {
        _testBuildTemplateNpmAngular71("npm-angular-portlet", "foo-bar", "foo.bar", "FooBar");
    }

    @Test
    public void testBuildTemplateNpmReactPortlet70() throws Exception {
        _testBuildTemplateNpm70("npm-react-portlet", "foo", "foo", "Foo");
    }

    @Test
    public void testBuildTemplateNpmReactPortlet71() throws Exception {
        _testBuildTemplateNpm71("npm-react-portlet", "foo", "foo", "Foo");
    }

    @Test
    public void testBuildTemplateNpmReactPortletWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("npm-react-portlet", "react-portlet-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateNpmReactPortletWithDashes70() throws Exception {
        _testBuildTemplateNpm70("npm-react-portlet", "foo-bar", "foo.bar", "FooBar");
    }

    @Test
    public void testBuildTemplateNpmReactPortletWithDashes71() throws Exception {
        _testBuildTemplateNpm71("npm-react-portlet", "foo-bar", "foo.bar", "FooBar");
    }

    @Test
    public void testBuildTemplateNpmVuejsPortlet70() throws Exception {
        _testBuildTemplateNpm70("npm-vuejs-portlet", "foo", "foo", "Foo");
    }

    @Test
    public void testBuildTemplateNpmVuejsPortlet71() throws Exception {
        _testBuildTemplateNpm71("npm-vuejs-portlet", "foo", "foo", "Foo");
    }

    @Test
    public void testBuildTemplateNpmVuejsPortletWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("npm-vuejs-portlet", "vuejs-portlet-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateNpmVuejsPortletWithDashes70() throws Exception {
        _testBuildTemplateNpm70("npm-vuejs-portlet", "foo-bar", "foo.bar", "FooBar");
    }

    @Test
    public void testBuildTemplateNpmVuejsPortletWithDashes71() throws Exception {
        _testBuildTemplateNpm71("npm-vuejs-portlet", "foo-bar", "foo.bar", "FooBar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildTemplateOnExistingDirectory() throws Exception {
        File destinationDir = temporaryFolder.newFolder("gradle");
        ProjectTemplatesTest._buildTemplateWithGradle(destinationDir, "activator", "dup-activator");
        ProjectTemplatesTest._buildTemplateWithGradle(destinationDir, "activator", "dup-activator");
    }

    @Test
    public void testBuildTemplatePanelApp70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("panel-app", "gradle.test", "--class-name", "Foo", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Export-Package: gradle.test.constants");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/gradle/test/application/list/FooPanelApp.java", "public class FooPanelApp extends BasePanelApp");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/gradle/test/constants/FooPortletKeys.java", "public class FooPortletKeys");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/gradle/test/portlet/FooPortlet.java", "javax.portlet.name=\" + FooPortletKeys.Foo", "public class FooPortlet extends MVCPortlet");
        File mavenProjectDir = _buildTemplateWithMaven("panel-app", "gradle.test", "com.test", "-DclassName=Foo", "-Dpackage=gradle.test", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePanelApp71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("panel-app", "gradle.test", "--class-name", "Foo", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("panel-app", "gradle.test", "com.test", "-DclassName=Foo", "-Dpackage=gradle.test", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePanelAppInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("panel-app", "gradle.test", "build/libs/gradle.test-1.0.0.jar");
    }

    @Test
    public void testBuildTemplatePanelAppWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("panel-app", "panel-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplatePorletProviderInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("portlet-provider", "provider.test", "build/libs/provider.test-1.0.0.jar");
    }

    @Test
    public void testBuildTemplatePortlet70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet", "foo.test", "--class-name", "Foo", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Export-Package: foo.test.constants");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/test/constants/FooPortletKeys.java", "public class FooPortletKeys");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/test/portlet/FooPortlet.java", "package foo.test.portlet;", "javax.portlet.display-name=Foo", "javax.portlet.name=\" + FooPortletKeys.Foo", "public class FooPortlet extends GenericPortlet {", "printWriter.print(\"Hello from Foo");
        File mavenProjectDir = _buildTemplateWithMaven("portlet", "foo.test", "com.test", "-DclassName=Foo", "-Dpackage=foo.test", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePortlet71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet", "foo.test", "--class-name", "Foo", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("portlet", "foo.test", "com.test", "-DclassName=Foo", "-Dpackage=foo.test", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePortletConfigurationIcon70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet-configuration-icon", "icontest", "--package-name", "blade.test", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/blade/test/portlet/configuration/icon" + "/IcontestPortletConfigurationIcon.java"), "public class IcontestPortletConfigurationIcon", "extends BasePortletConfigurationIcon");
        File mavenProjectDir = _buildTemplateWithMaven("portlet-configuration-icon", "icontest", "com.test", "-DclassName=Icontest", "-Dpackage=blade.test", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePortletConfigurationIcon71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet-configuration-icon", "icontest", "--package-name", "blade.test", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("portlet-configuration-icon", "icontest", "com.test", "-DclassName=Icontest", "-Dpackage=blade.test", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePortletConfigurationIconInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("portlet-configuration-icon", "blade.test", "build/libs/blade.test-1.0.0.jar");
    }

    @Test
    public void testBuildTemplatePortletConfigurationIconWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet-configuration-icon", "icon-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplatePortletInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("portlet", "foo.test", "build/libs/foo.test-1.0.0.jar");
    }

    @Test
    public void testBuildTemplatePortletProvider70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet-provider", "provider.test", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/provider/test/constants" + "/ProviderTestPortletKeys.java"), "package provider.test.constants;", "public class ProviderTestPortletKeys", "public static final String ProviderTest = \"providertest\";");
        File mavenProjectDir = _buildTemplateWithMaven("portlet-provider", "provider.test", "com.test", "-DclassName=ProviderTest", "-Dpackage=provider.test", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePortletProvider71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet-provider", "provider.test", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("portlet-provider", "provider.test", "com.test", "-DclassName=ProviderTest", "-Dpackage=provider.test", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePortletProviderWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet-provider", "provider-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplatePortletToolbarContributor70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet-toolbar-contributor", "toolbartest", "--package-name", "blade.test", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/blade/test/portlet/toolbar/contributor" + "/ToolbartestPortletToolbarContributor.java"), "public class ToolbartestPortletToolbarContributor", "implements PortletToolbarContributor");
        File mavenProjectDir = _buildTemplateWithMaven("portlet-toolbar-contributor", "toolbartest", "com.test", "-DclassName=Toolbartest", "-Dpackage=blade.test", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePortletToolbarContributor71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet-toolbar-contributor", "toolbartest", "--package-name", "blade.test", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("portlet-toolbar-contributor", "toolbartest", "com.test", "-DclassName=Toolbartest", "-Dpackage=blade.test", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplatePortletToolbarContributorInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("portlet-toolbar-contributor", "blade.test", "build/libs/blade.test-1.0.0.jar");
    }

    @Test
    public void testBuildTemplatePortletToolbarContributorWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet-toolbar-contributor", "contributor-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplatePortletWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet", "portlet-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplatePortletWithPortletName() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("portlet", "portlet");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/portlet/portlet/PortletPortlet.java", "package portlet.portlet;", "javax.portlet.display-name=Portlet", "public class PortletPortlet extends GenericPortlet {", "printWriter.print(\"Hello from Portlet");
        File mavenProjectDir = _buildTemplateWithMaven("portlet", "portlet", "com.test", "-DclassName=Portlet", "-Dpackage=portlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateRest70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("rest", "my-rest", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ("compileOnly group: \"javax.ws.rs\", name: \"javax.ws.rs-api\", " + "version: \"2.0.1\""));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/my/rest/application/MyRestApplication.java", "public class MyRestApplication extends Application");
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/resources/configuration" + ("/com.liferay.portal.remote.cxf.common.configuration." + "CXFEndpointPublisherConfiguration-cxf.properties")), "contextPath=/my-rest");
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/resources/configuration/com.liferay.portal.remote.rest." + ("extender.configuration.RestExtenderConfiguration-rest." + "properties")), "contextPaths=/my-rest", ("jaxRsApplicationFilterStrings=(component.name=" + "my.rest.application.MyRestApplication)"));
        File mavenProjectDir = _buildTemplateWithMaven("rest", "my-rest", "com.test", "-DclassName=MyRest", "-Dpackage=my.rest", "-DliferayVersion=7.0");
        ProjectTemplatesTest._testContains(mavenProjectDir, "src/main/java/my/rest/application/MyRestApplication.java", "public class MyRestApplication extends Application");
        ProjectTemplatesTest._testContains(mavenProjectDir, ("src/main/resources/configuration" + ("/com.liferay.portal.remote.cxf.common.configuration." + "CXFEndpointPublisherConfiguration-cxf.properties")), "contextPath=/my-rest");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateRest71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("rest", "my-rest", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ("compileOnly group: \"org.osgi\", name: " + "\"org.osgi.service.jaxrs\", version: \"1.0.0\""));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/my/rest/application/MyRestApplication.java", "public class MyRestApplication extends Application");
        ProjectTemplatesTest._testNotExists(gradleProjectDir, ("src/main/resources/configuration" + ("/com.liferay.portal.remote.cxf.common.configuration." + "CXFEndpointPublisherConfiguration-cxf.properties")));
        ProjectTemplatesTest._testNotExists(gradleProjectDir, ("src/main/resources/configuration/com.liferay.portal.remote.rest." + ("extender.configuration.RestExtenderConfiguration-rest." + "properties")));
        ProjectTemplatesTest._testNotExists(gradleProjectDir, "src/main/resources/configuration");
        File mavenProjectDir = _buildTemplateWithMaven("rest", "my-rest", "com.test", "-DclassName=MyRest", "-Dpackage=my.rest", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "src/main/java/my/rest/application/MyRestApplication.java", "public class MyRestApplication extends Application");
        ProjectTemplatesTest._testNotExists(mavenProjectDir, ("src/main/resources/configuration" + ("/com.liferay.portal.remote.cxf.common.configuration." + "CXFEndpointPublisherConfiguration-cxf.properties")));
        ProjectTemplatesTest._testNotExists(mavenProjectDir, "src/main/resources/configuration");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateRestInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("rest", "my-rest", "build/libs/my.rest-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateRestWithBOM70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("rest", "rest-dependency-management", "--dependency-management-enabled", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "compileOnly group: \"javax.ws.rs\", name: \"javax.ws.rs-api\"");
    }

    @Test
    public void testBuildTemplateRestWithBOM71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("rest", "rest-dependency-management", "--dependency-management-enabled", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "compileOnly group: \"javax.ws.rs\", name: \"javax.ws.rs-api\"\n", ("compileOnly group: \"org.osgi\", name: " + "\"org.osgi.service.jaxrs\", version: \"1.0.0\""));
    }

    @Test
    public void testBuildTemplateService70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service", "servicepreaction", "--class-name", "FooAction", "--service", "com.liferay.portal.kernel.events.LifecycleAction", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0"));
        ProjectTemplatesTest._writeServiceClass(gradleProjectDir);
        File mavenProjectDir = _buildTemplateWithMaven("service", "servicepreaction", "com.test", "-DclassName=FooAction", "-Dpackage=servicepreaction", "-DserviceClass=com.liferay.portal.kernel.events.LifecycleAction", "-DliferayVersion=7.0");
        ProjectTemplatesTest._writeServiceClass(mavenProjectDir);
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateService71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service", "servicepreaction", "--class-name", "FooAction", "--service", "com.liferay.portal.kernel.events.LifecycleAction", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        ProjectTemplatesTest._writeServiceClass(gradleProjectDir);
        File mavenProjectDir = _buildTemplateWithMaven("service", "servicepreaction", "com.test", "-DclassName=FooAction", "-Dpackage=servicepreaction", "-DserviceClass=com.liferay.portal.kernel.events.LifecycleAction", "-DliferayVersion=7.1");
        ProjectTemplatesTest._writeServiceClass(mavenProjectDir);
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateServiceBuilder70() throws Exception {
        String name = "guestbook";
        String packageName = "com.liferay.docs.guestbook";
        File gradleProjectDir = _buildTemplateWithGradle("service-builder", name, "--package-name", packageName, "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, (name + "-api/build.gradle"), ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, (name + "-service/build.gradle"), ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.6.0"));
        File mavenProjectDir = _buildTemplateWithMaven("service-builder", name, "com.test", ("-Dpackage=" + packageName), "-DliferayVersion=7.0");
        _testBuildTemplateServiceBuilder(gradleProjectDir, mavenProjectDir, gradleProjectDir, name, packageName, "");
    }

    @Test
    public void testBuildTemplateServiceBuilder71() throws Exception {
        String name = "guestbook";
        String packageName = "com.liferay.docs.guestbook";
        File gradleProjectDir = _buildTemplateWithGradle("service-builder", name, "--package-name", packageName, "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, (name + "-api/build.gradle"), ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, (name + "-service/build.gradle"), ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("service-builder", name, "com.test", ("-Dpackage=" + packageName), "-DliferayVersion=7.1");
        _testBuildTemplateServiceBuilder(gradleProjectDir, mavenProjectDir, gradleProjectDir, name, packageName, "");
    }

    @Test
    public void testBuildTemplateServiceBuilderCheckExports() throws Exception {
        String name = "guestbook";
        String packageName = "com.liferay.docs.guestbook";
        File gradleProjectDir = _buildTemplateWithGradle("service-builder", name, "--package-name", packageName, "--liferayVersion", "7.1");
        File gradleServiceXml = new File(new File(gradleProjectDir, (name + "-service")), "service.xml");
        Consumer<Document> consumer = ( document) -> {
            Element documentElement = document.getDocumentElement();
            documentElement.setAttribute("package-path", "com.liferay.test");
        };
        ProjectTemplatesTest._editXml(gradleServiceXml, consumer);
        File mavenProjectDir = _buildTemplateWithMaven("service-builder", name, "com.test", ("-Dpackage=" + packageName), "-DliferayVersion=7.1");
        File mavenServiceXml = new File(new File(mavenProjectDir, (name + "-service")), "service.xml");
        ProjectTemplatesTest._editXml(mavenServiceXml, consumer);
        ProjectTemplatesTest._testContains(gradleProjectDir, (name + "-api/bnd.bnd"), "Export-Package:\\", (packageName + ".exception,\\"), (packageName + ".model,\\"), (packageName + ".service,\\"), (packageName + ".service.persistence"));
        Optional<String> stdOutput = ProjectTemplatesTest._executeGradle(gradleProjectDir, false, true, ((name + "-service") + (ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD)));
        Assert.assertTrue(stdOutput.isPresent());
        String gradleOutput = stdOutput.get();
        Assert.assertTrue(("Expected gradle output to include build error. " + gradleOutput), gradleOutput.contains("Exporting an empty package"));
        String mavenOutput = ProjectTemplatesTest._executeMaven(mavenProjectDir, true, ProjectTemplatesTest._MAVEN_GOAL_PACKAGE);
        Assert.assertTrue(("Expected maven output to include build error. " + mavenOutput), mavenOutput.contains("Exporting an empty package"));
    }

    @Test
    public void testBuildTemplateServiceBuilderNestedPath70() throws Exception {
        File workspaceProjectDir = _buildTemplateWithGradle(WORKSPACE, "ws-nested-path");
        File destinationDir = new File(workspaceProjectDir, "modules/nested/path");
        Assert.assertTrue(destinationDir.mkdirs());
        File gradleProjectDir = ProjectTemplatesTest._buildTemplateWithGradle(destinationDir, "service-builder", "sample", "--package-name", "com.test.sample", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "sample-service/build.gradle", "compileOnly project(\":modules:nested:path:sample:sample-api\")");
        File mavenProjectDir = _buildTemplateWithMaven("service-builder", "sample", "com.test", "-Dpackage=com.test.sample", "-DliferayVersion=7.0");
        _testBuildTemplateServiceBuilder(gradleProjectDir, mavenProjectDir, workspaceProjectDir, "sample", "com.test.sample", ":modules:nested:path:sample");
    }

    @Test
    public void testBuildTemplateServiceBuilderNestedPath71() throws Exception {
        File workspaceProjectDir = _buildTemplateWithGradle(WORKSPACE, "ws-nested-path");
        File destinationDir = new File(workspaceProjectDir, "modules/nested/path");
        Assert.assertTrue(destinationDir.mkdirs());
        File gradleProjectDir = ProjectTemplatesTest._buildTemplateWithGradle(destinationDir, "service-builder", "sample", "--package-name", "com.test.sample", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "sample-service/build.gradle", "compileOnly project(\":modules:nested:path:sample:sample-api\")");
        File mavenProjectDir = _buildTemplateWithMaven("service-builder", "sample", "com.test", "-Dpackage=com.test.sample", "-DliferayVersion=7.1");
        _testBuildTemplateServiceBuilder(gradleProjectDir, mavenProjectDir, workspaceProjectDir, "sample", "com.test.sample", ":modules:nested:path:sample");
    }

    @Test
    public void testBuildTemplateServiceBuilderTargetPlatformEnabled70() throws Exception {
        File workspaceProjectDir = _buildTemplateWithGradle(WORKSPACE, "workspace");
        File gradleProperties = new File(workspaceProjectDir, "gradle.properties");
        Files.write(gradleProperties.toPath(), "\nliferay.workspace.target.platform.version=7.0.6".getBytes(), StandardOpenOption.APPEND);
        File modulesDir = new File(workspaceProjectDir, "modules");
        ProjectTemplatesTest._buildTemplateWithGradle(modulesDir, "service-builder", "foo", "--package-name", "test", "--liferayVersion", "7.0", "--dependency-management-enabled");
        ProjectTemplatesTest._executeGradle(workspaceProjectDir, (":modules:foo:foo-service" + (ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD_SERVICE)));
        ProjectTemplatesTest._executeGradle(workspaceProjectDir, ":modules:foo:foo-api:build");
        ProjectTemplatesTest._executeGradle(workspaceProjectDir, ":modules:foo:foo-service:build");
    }

    @Test
    public void testBuildTemplateServiceBuilderTargetPlatformEnabled71() throws Exception {
        File workspaceProjectDir = _buildTemplateWithGradle(WORKSPACE, "workspace");
        File gradleProperties = new File(workspaceProjectDir, "gradle.properties");
        Files.write(gradleProperties.toPath(), "\nliferay.workspace.target.platform.version=7.1.0".getBytes(), StandardOpenOption.APPEND);
        File modulesDir = new File(workspaceProjectDir, "modules");
        ProjectTemplatesTest._buildTemplateWithGradle(modulesDir, "service-builder", "foo", "--package-name", "test", "--liferayVersion", "7.1", "--dependency-management-enabled");
        ProjectTemplatesTest._executeGradle(workspaceProjectDir, (":modules:foo:foo-service" + (ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD_SERVICE)));
        ProjectTemplatesTest._executeGradle(workspaceProjectDir, ":modules:foo:foo-api:build");
        ProjectTemplatesTest._executeGradle(workspaceProjectDir, ":modules:foo:foo-service:build");
    }

    @Test
    public void testBuildTemplateServiceBuilderWithDashes70() throws Exception {
        String name = "backend-integration";
        String packageName = "com.liferay.docs.guestbook";
        File gradleProjectDir = _buildTemplateWithGradle("service-builder", name, "--package-name", packageName, "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, (name + "-api/build.gradle"), ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, (name + "-service/build.gradle"), ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.6.0"));
        File mavenProjectDir = _buildTemplateWithMaven("service-builder", name, "com.test", ("-Dpackage=" + packageName), "-DliferayVersion=7.0");
        _testBuildTemplateServiceBuilder(gradleProjectDir, mavenProjectDir, gradleProjectDir, name, packageName, "");
    }

    @Test
    public void testBuildTemplateServiceBuilderWithDashes71() throws Exception {
        String name = "backend-integration";
        String packageName = "com.liferay.docs.guestbook";
        File gradleProjectDir = _buildTemplateWithGradle("service-builder", name, "--package-name", packageName, "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, (name + "-api/build.gradle"), ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        ProjectTemplatesTest._testContains(gradleProjectDir, (name + "-service/build.gradle"), ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("service-builder", name, "com.test", ("-Dpackage=" + packageName), "-DliferayVersion=7.1");
        _testBuildTemplateServiceBuilder(gradleProjectDir, mavenProjectDir, gradleProjectDir, name, packageName, "");
    }

    @Test
    public void testBuildTemplateServiceInWorkspace() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service", "servicepreaction", "--class-name", "FooAction", "--service", "com.liferay.portal.kernel.events.LifecycleAction");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "buildscript {", "repositories {");
        ProjectTemplatesTest._writeServiceClass(gradleProjectDir);
        File workspaceDir = _buildWorkspace();
        File modulesDir = new File(workspaceDir, "modules");
        File workspaceProjectDir = ProjectTemplatesTest._buildTemplateWithGradle(modulesDir, "service", "servicepreaction", "--class-name", "FooAction", "--service", "com.liferay.portal.kernel.events.LifecycleAction");
        ProjectTemplatesTest._testNotContains(workspaceProjectDir, "build.gradle", true, "^repositories \\{.*");
        ProjectTemplatesTest._writeServiceClass(workspaceProjectDir);
        ProjectTemplatesTest._executeGradle(gradleProjectDir, ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD);
        ProjectTemplatesTest._testExists(gradleProjectDir, "build/libs/servicepreaction-1.0.0.jar");
        ProjectTemplatesTest._executeGradle(workspaceDir, ":modules:servicepreaction:build");
        ProjectTemplatesTest._testExists(workspaceProjectDir, "build/libs/servicepreaction-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateServiceWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service", "service-dependency-management", "--service", "com.liferay.portal.kernel.events.LifecycleAction", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateServiceWrapper70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service-wrapper", "serviceoverride", "--service", "com.liferay.portal.kernel.service.UserLocalServiceWrapper", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0\""), "apply plugin: \"com.liferay.plugin\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/serviceoverride/Serviceoverride.java", "package serviceoverride;", "import com.liferay.portal.kernel.service.UserLocalServiceWrapper;", "service = ServiceWrapper.class", "public class Serviceoverride extends UserLocalServiceWrapper {", "public Serviceoverride() {");
        File mavenProjectDir = _buildTemplateWithMaven("service-wrapper", "serviceoverride", "com.test", "-DclassName=Serviceoverride", "-Dpackage=serviceoverride", ("-DserviceWrapperClass=" + "com.liferay.portal.kernel.service.UserLocalServiceWrapper"), "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateServiceWrapper71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service-wrapper", "serviceoverride", "--service", "com.liferay.portal.kernel.service.UserLocalServiceWrapper", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("service-wrapper", "serviceoverride", "com.test", "-DclassName=Serviceoverride", "-Dpackage=serviceoverride", ("-DserviceWrapperClass=" + "com.liferay.portal.kernel.service.UserLocalServiceWrapper"), "-DliferayVersion=7.1");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateServiceWrapperInWorkspace() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service-wrapper", "serviceoverride", "--service", "com.liferay.portal.kernel.service.UserLocalServiceWrapper");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "buildscript {", "repositories {");
        File workspaceDir = _buildWorkspace();
        File modulesDir = new File(workspaceDir, "modules");
        File workspaceProjectDir = ProjectTemplatesTest._buildTemplateWithGradle(modulesDir, "service-wrapper", "serviceoverride", "--service", "com.liferay.portal.kernel.service.UserLocalServiceWrapper");
        ProjectTemplatesTest._testNotContains(workspaceProjectDir, "build.gradle", true, "^repositories \\{.*");
        ProjectTemplatesTest._executeGradle(gradleProjectDir, ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD);
        ProjectTemplatesTest._testExists(gradleProjectDir, "build/libs/serviceoverride-1.0.0.jar");
        ProjectTemplatesTest._executeGradle(workspaceDir, ":modules:serviceoverride:build");
        ProjectTemplatesTest._testExists(workspaceProjectDir, "build/libs/serviceoverride-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateServiceWrapperWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("service-wrapper", "wrapper-dependency-management", "--service", "com.liferay.portal.kernel.service.UserLocalServiceWrapper", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateSimulationPanelEntry70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("simulation-panel-entry", "simulator", "--package-name", "test.simulator", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.3.0\""), "apply plugin: \"com.liferay.plugin\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/test/simulator/application/list" + "/SimulatorSimulationPanelApp.java"), "public class SimulatorSimulationPanelApp", "extends BaseJSPPanelApp");
        File mavenProjectDir = _buildTemplateWithMaven("simulation-panel-entry", "simulator", "com.test", "-DclassName=Simulator", "-Dpackage=test.simulator", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateSimulationPanelEntry71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("simulation-panel-entry", "simulator", "--package-name", "test.simulator", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("simulation-panel-entry", "simulator", "com.test", "-DclassName=Simulator", "-Dpackage=test.simulator", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateSimulationPanelEntryInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("simulation-panel-entry", "test.simulator", "build/libs/test.simulator-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateSimulationPanelEntryWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("simulation-panel-entry", "simulator-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateSocialBookmark71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("social-bookmark", "foo", "--package-name", "com.liferay.test", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testExists(gradleProjectDir, "build.gradle");
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/com/liferay/test/social/bookmark" + "/FooSocialBookmark.java"), "public class FooSocialBookmark implements SocialBookmark");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/META-INF/resources/page.jsp", "<clay:link");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/resources/content/Language.properties", "foo=Foo");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, ("src/main/java/com/liferay/test/social/bookmark" + "/FooSocialBookmark.java"), "private ResourceBundleLoader");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, ("src/main/java/com/liferay/test/social/bookmark" + "/FooSocialBookmark.java"), "protected ResourceBundleLoader");
        File mavenProjectDir = _buildTemplateWithMaven("social-bookmark", "foo", "com.test", "-DclassName=Foo", "-Dpackage=com.liferay.test", "-DliferayVersion=7.1");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateSoyPortletCustomClass70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("soy-portlet", "foo", "--class-name", "MySoyPortlet", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/portlet/MySoyPortletPortlet.java", "public class MySoyPortletPortlet extends SoyPortlet {");
    }

    @Test
    public void testBuildTemplateSoyPortletCustomClass71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("soy-portlet", "foo", "--class-name", "MySPR", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/portlet/MySPRSoyPortletRegister.java", ("public class MySPRSoyPortletRegister implements " + "SoyPortletRegister {"));
    }

    @Test
    public void testBuildTemplateSoyPortletWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("soy-portlet", "soy-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateSpringMVCPortlet70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("spring-mvc-portlet", "foo", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/WEB-INF/jsp/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/WEB-INF/jsp/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.6.0\""));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/foo/portlet/FooPortletViewController.java", "public class FooPortletViewController {");
        File mavenProjectDir = _buildTemplateWithMaven("spring-mvc-portlet", "foo", "com.test", "-DclassName=Foo", "-Dpackage=foo", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
        if (ProjectTemplatesTest._isBuildProjects()) {
            ProjectTemplatesTest._testSpringMVCOutputs(gradleProjectDir);
        }
    }

    @Test
    public void testBuildTemplateSpringMVCPortlet71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("spring-mvc-portlet", "foo", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("spring-mvc-portlet", "foo", "com.test", "-DclassName=Foo", "-Dpackage=foo", "-DliferayVersion=7.1");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
        if (ProjectTemplatesTest._isBuildProjects()) {
            ProjectTemplatesTest._testSpringMVCOutputs(gradleProjectDir);
        }
    }

    @Test
    public void testBuildTemplateSpringMVCPortletInWorkspace() throws Exception {
        _testBuildTemplateProjectWarInWorkspace("spring-mvc-portlet", "foo", "foo");
    }

    @Test
    public void testBuildTemplateSpringMvcPortletWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("spring-mvc-portlet", "spring-mvc-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateSpringMVCPortletWithPackage() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("spring-mvc-portlet", "foo", "--package-name", "com.liferay.test");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/WEB-INF/jsp/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/WEB-INF/jsp/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/com/liferay/test/portlet" + "/FooPortletViewController.java"), "public class FooPortletViewController {");
        File mavenProjectDir = _buildTemplateWithMaven("spring-mvc-portlet", "foo", "com.test", "-DclassName=Foo", "-Dpackage=com.liferay.test");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateSpringMVCPortletWithPortletName() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("spring-mvc-portlet", "portlet");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/WEB-INF/jsp/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/WEB-INF/jsp/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/portlet/portlet/PortletPortletViewController.java", "public class PortletPortletViewController {");
        File mavenProjectDir = _buildTemplateWithMaven("spring-mvc-portlet", "portlet", "com.test", "-DclassName=Portlet", "-Dpackage=portlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateSpringMVCPortletWithPortletSuffix() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("spring-mvc-portlet", "portlet-portlet");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/WEB-INF/jsp/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/WEB-INF/jsp/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/portlet/portlet/portlet" + "/PortletPortletViewController.java"), "public class PortletPortletViewController {");
        File mavenProjectDir = _buildTemplateWithMaven("spring-mvc-portlet", "portlet-portlet", "com.test", "-DclassName=Portlet", "-Dpackage=portlet.portlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateTemplateContextContributor70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("template-context-contributor", "blade-test", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0\""), "apply plugin: \"com.liferay.plugin\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, ("src/main/java/blade/test/context/contributor" + "/BladeTestTemplateContextContributor.java"), "public class BladeTestTemplateContextContributor", "implements TemplateContextContributor");
        File mavenProjectDir = _buildTemplateWithMaven("template-context-contributor", "blade-test", "com.test", "-DclassName=BladeTest", "-Dpackage=blade.test", "-DliferayVersion=7.0");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateTemplateContextContributor71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("template-context-contributor", "blade-test", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testExists(gradleProjectDir, "bnd.bnd");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("template-context-contributor", "blade-test", "com.test", "-DclassName=BladeTest", "-Dpackage=blade.test", "-DliferayVersion=7.1");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateTemplateContextContributorInWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("template-context-contributor", "blade-test", "build/libs/blade.test-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateTemplateContextContributorWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("template-context-contributor", "context-contributor-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateTheme70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("theme", "theme-test", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "name: \"com.liferay.gradle.plugins.theme.builder\"", "apply plugin: \"com.liferay.portal.tools.theme.builder\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties", "name=theme-test");
        File mavenProjectDir = _buildTemplateWithMaven("theme", "theme-test", "com.test", "-DliferayVersion=7.0");
        ProjectTemplatesTest._testContains(mavenProjectDir, "pom.xml", "com.liferay.portal.tools.theme.builder");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateTheme71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("theme", "theme-test", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "name: \"com.liferay.gradle.plugins.theme.builder\"", "apply plugin: \"com.liferay.portal.tools.theme.builder\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties", "name=theme-test");
        File mavenProjectDir = _buildTemplateWithMaven("theme", "theme-test", "com.test", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "pom.xml", "com.liferay.portal.tools.theme.builder");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateThemeContributorCustom() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("theme-contributor", "my-contributor-custom", "--contributor-type", "foo-bar");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Liferay-Theme-Contributor-Type: foo-bar", "Web-ContextPath: /foo-bar-theme-contributor");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "bnd.bnd", "-plugin.sass: com.liferay.ant.bnd.sass.SassAnalyzerPlugin");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/resources/META-INF/resources/css/foo-bar.scss");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/resources/META-INF/resources/js/foo-bar.js");
        File mavenProjectDir = _buildTemplateWithMaven("theme-contributor", "my-contributor-custom", "com.test", "-DcontributorType=foo-bar", "-Dpackage=my.contributor.custom");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-plugin.sass: com.liferay.ant.bnd.sass.SassAnalyzerPlugin");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateThemeContributorCustom71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("theme-contributor", "my-contributor-custom", "--contributor-type", "foo-bar", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Liferay-Theme-Contributor-Type: foo-bar", "Web-ContextPath: /foo-bar-theme-contributor");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "bnd.bnd", "-plugin.sass: com.liferay.ant.bnd.sass.SassAnalyzerPlugin");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/resources/META-INF/resources/css/foo-bar.scss");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/resources/META-INF/resources/js/foo-bar.js");
        File mavenProjectDir = _buildTemplateWithMaven("theme-contributor", "my-contributor-custom", "com.test", "-DcontributorType=foo-bar", "-Dpackage=my.contributor.custom", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenProjectDir, "bnd.bnd", "-plugin.sass: com.liferay.ant.bnd.sass.SassAnalyzerPlugin");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateThemeContributorDefaults() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("theme-contributor", "my-contributor-default");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Liferay-Theme-Contributor-Type: my-contributor-default", "Web-ContextPath: /my-contributor-default-theme-contributor");
    }

    @Test
    public void testBuildTemplateThemeContributorinWorkspace() throws Exception {
        _testBuildTemplateWithWorkspace("theme-contributor", "my-contributor", "build/libs/my.contributor-1.0.0.jar");
    }

    @Test
    public void testBuildTemplateThemeInWorkspace() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("theme", "theme-test");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "buildscript {", "apply plugin: \"com.liferay.portal.tools.theme.builder\"", "repositories {");
        File workspaceDir = _buildWorkspace();
        File warsDir = new File(workspaceDir, "wars");
        File workspaceProjectDir = ProjectTemplatesTest._buildTemplateWithGradle(warsDir, "theme", "theme-test");
        ProjectTemplatesTest._testNotContains(workspaceProjectDir, "build.gradle", true, "^repositories \\{.*");
        if (ProjectTemplatesTest._isBuildProjects()) {
            ProjectTemplatesTest._executeGradle(gradleProjectDir, ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD);
            File gradleWarFile = ProjectTemplatesTest._testExists(gradleProjectDir, "build/libs/theme-test.war");
            ProjectTemplatesTest._executeGradle(workspaceDir, ":wars:theme-test:build");
            File workspaceWarFile = ProjectTemplatesTest._testExists(workspaceProjectDir, "build/libs/theme-test.war");
            ProjectTemplatesTest._testWarsDiff(gradleWarFile, workspaceWarFile);
        }
    }

    @Test
    public void testBuildTemplateWarHook70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-hook", "WarHook", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/resources/portal.properties");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0\""));
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/warhook/WarHookLoginPostAction.java", "public class WarHookLoginPostAction extends Action");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/java/warhook/WarHookStartupAction.java", "public class WarHookStartupAction extends SimpleAction");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties", "name=WarHook");
        File mavenProjectDir = _buildTemplateWithMaven("war-hook", "WarHook", "warhook", "-DclassName=WarHook", "-Dpackage=warhook", "-DliferayVersion=7.0");
        ProjectTemplatesTest._testContains(mavenProjectDir, "pom.xml");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateWarHook71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-hook", "WarHook", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("war-hook", "WarHook", "warhook", "-DclassName=WarHook", "-Dpackage=warhook", "-DliferayVersion=7.1");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateWarHookWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-hook", "war-hook-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateWarMVCPortlet70() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-mvc-portlet", "WarMVCPortlet", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"2.0.0\""), "apply plugin: \"com.liferay.css.builder\"", "apply plugin: \"war\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties", "name=WarMVCPortlet");
        File mavenProjectDir = _buildTemplateWithMaven("war-mvc-portlet", "WarMVCPortlet", "warmvcportlet", "-DclassName=WarMVCPortlet", "-Dpackage=WarMVCPortlet", "-DliferayVersion=7.0");
        ProjectTemplatesTest._testContains(mavenProjectDir, "pom.xml", "maven-war-plugin", "com.liferay.css.builder");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateWarMVCPortlet71() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-mvc-portlet", "WarMVCPortlet", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + ", version: \"3.0.0"));
        File mavenProjectDir = _buildTemplateWithMaven("war-mvc-portlet", "WarMVCPortlet", "warmvcportlet", "-DclassName=WarMVCPortlet", "-Dpackage=WarMVCPortlet", "-DliferayVersion=7.1");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateWarMVCPortletInWorkspace() throws Exception {
        _testBuildTemplateProjectWarInWorkspace("war-mvc-portlet", "WarMVCPortlet", "WarMVCPortlet");
    }

    @Test
    public void testBuildTemplateWarMVCPortletWithPackage() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-mvc-portlet", "WarMVCPortlet", "--package-name", "com.liferay.test");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.css.builder\"", "apply plugin: \"war\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties", "name=WarMVCPortlet");
        File mavenProjectDir = _buildTemplateWithMaven("war-mvc-portlet", "WarMVCPortlet", "com.liferay.test", "-DclassName=WarMVCPortlet", "-Dpackage=com.liferay.test");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateWarMVCPortletWithPortletName() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-mvc-portlet", "WarMVCPortlet");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.css.builder\"", "apply plugin: \"war\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties", "name=WarMVCPortlet");
        File mavenProjectDir = _buildTemplateWithMaven("war-mvc-portlet", "WarMVCPortlet", "warmvcportlet", "-DclassName=WarMVCPortlet", "-Dpackage=WarMVCPortlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateWarMVCPortletWithPortletSuffix() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-mvc-portlet", "WarMVC-portlet");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/webapp/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.css.builder\"", "apply plugin: \"war\"");
        ProjectTemplatesTest._testContains(gradleProjectDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties", "name=WarMVC-portlet");
        File mavenProjectDir = _buildTemplateWithMaven("war-mvc-portlet", "WarMVC-portlet", "warmvc.portlet", "-DclassName=WarMVCPortlet", "-Dpackage=WarMVC.portlet");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateWarMvcWithBOM() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("war-mvc-portlet", "war-mvc-dependency-management", "--dependency-management-enabled");
        ProjectTemplatesTest._testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", ((ProjectTemplatesTest._DEPENDENCY_PORTAL_KERNEL) + "\n"));
    }

    @Test
    public void testBuildTemplateWithGradle() throws Exception {
        ProjectTemplatesTest._buildTemplateWithGradle(temporaryFolder.newFolder(), null, "foo-portlet", false, false);
        ProjectTemplatesTest._buildTemplateWithGradle(temporaryFolder.newFolder(), null, "foo-portlet", false, true);
        ProjectTemplatesTest._buildTemplateWithGradle(temporaryFolder.newFolder(), null, "foo-portlet", true, false);
        ProjectTemplatesTest._buildTemplateWithGradle(temporaryFolder.newFolder(), null, "foo-portlet", true, true);
    }

    @Test
    public void testBuildTemplateWithPackageName() throws Exception {
        File gradleProjectDir = _buildTemplateWithGradle("", "barfoo", "--package-name", "foo.bar");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/resources/META-INF/resources/init.jsp");
        ProjectTemplatesTest._testExists(gradleProjectDir, "src/main/resources/META-INF/resources/view.jsp");
        ProjectTemplatesTest._testContains(gradleProjectDir, "bnd.bnd", "Bundle-SymbolicName: foo.bar");
        ProjectTemplatesTest._testContains(gradleProjectDir, "build.gradle", "apply plugin: \"com.liferay.plugin\"");
        File mavenProjectDir = _buildTemplateWithMaven("mvc-portlet", "barfoo", "com.test", "-DclassName=Barfoo", "-Dpackage=foo.bar");
        ProjectTemplatesTest._buildProjects(gradleProjectDir, mavenProjectDir);
    }

    @Test
    public void testBuildTemplateWorkspace() throws Exception {
        File workspaceProjectDir = _buildTemplateWithGradle(WORKSPACE, "foows");
        ProjectTemplatesTest._testExists(workspaceProjectDir, "configs/dev/portal-ext.properties");
        ProjectTemplatesTest._testExists(workspaceProjectDir, "gradle.properties");
        ProjectTemplatesTest._testExists(workspaceProjectDir, "modules");
        ProjectTemplatesTest._testExists(workspaceProjectDir, "themes");
        ProjectTemplatesTest._testExists(workspaceProjectDir, "wars");
        ProjectTemplatesTest._testNotExists(workspaceProjectDir, "modules/pom.xml");
        ProjectTemplatesTest._testNotExists(workspaceProjectDir, "themes/pom.xml");
        ProjectTemplatesTest._testNotExists(workspaceProjectDir, "wars/pom.xml");
        File moduleProjectDir = ProjectTemplatesTest._buildTemplateWithGradle(new File(workspaceProjectDir, "modules"), "", "foo-portlet");
        ProjectTemplatesTest._testNotContains(moduleProjectDir, "build.gradle", "buildscript", "repositories");
        ProjectTemplatesTest._executeGradle(workspaceProjectDir, (":modules:foo-portlet" + (ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD)));
        ProjectTemplatesTest._testExists(moduleProjectDir, "build/libs/foo.portlet-1.0.0.jar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildTemplateWorkspaceExistingFile() throws Exception {
        File destinationDir = temporaryFolder.newFolder("existing-file");
        ProjectTemplatesTest._createNewFiles("foo", destinationDir);
        ProjectTemplatesTest._buildTemplateWithGradle(destinationDir, WORKSPACE, "foo");
    }

    @Test
    public void testBuildTemplateWorkspaceForce() throws Exception {
        File destinationDir = temporaryFolder.newFolder("existing-file");
        ProjectTemplatesTest._createNewFiles("foo", destinationDir);
        ProjectTemplatesTest._buildTemplateWithGradle(destinationDir, WORKSPACE, "forced", "--force");
    }

    @Test
    public void testBuildTemplateWorkspaceLocalProperties() throws Exception {
        File workspaceProjectDir = _buildTemplateWithGradle(WORKSPACE, "foo");
        ProjectTemplatesTest._testExists(workspaceProjectDir, "gradle-local.properties");
        Properties gradleLocalProperties = new Properties();
        String homeDirName = "foo/bar/baz";
        String modulesDirName = "qux/quux";
        gradleLocalProperties.put("liferay.workspace.home.dir", homeDirName);
        gradleLocalProperties.put("liferay.workspace.modules.dir", modulesDirName);
        File gradleLocalPropertiesFile = new File(workspaceProjectDir, "gradle-local.properties");
        try (FileOutputStream fileOutputStream = new FileOutputStream(gradleLocalPropertiesFile)) {
            gradleLocalProperties.store(fileOutputStream, null);
        }
        ProjectTemplatesTest._buildTemplateWithGradle(new File(workspaceProjectDir, modulesDirName), "", "foo-portlet");
        ProjectTemplatesTest._executeGradle(workspaceProjectDir, (((":" + (modulesDirName.replace('/', ':'))) + ":foo-portlet") + (ProjectTemplatesTest._GRADLE_TASK_PATH_DEPLOY)));
        ProjectTemplatesTest._testExists(workspaceProjectDir, (homeDirName + "/osgi/modules/foo.portlet.jar"));
    }

    @Test
    public void testBuildTemplateWorkspaceWith70() throws Exception {
        File gradleWorkspaceProjectDir = ProjectTemplatesTest._buildTemplateWithGradle(WORKSPACE, "withportlet", "--liferayVersion", "7.0");
        ProjectTemplatesTest._testContains(gradleWorkspaceProjectDir, "gradle.properties", true, ".*liferay.workspace.bundle.url=.*liferay.com/portal/7.0.*");
        File gradlePropertiesFile = new File(gradleWorkspaceProjectDir, "gradle.properties");
        ProjectTemplatesTest._testPropertyKeyExists(gradlePropertiesFile, "liferay.workspace.bundle.url");
        File mavenWorkspaceProjectDir = _buildTemplateWithMaven(WORKSPACE, "withportlet", "com.test", "-DliferayVersion=7.0");
        ProjectTemplatesTest._testContains(mavenWorkspaceProjectDir, "pom.xml", "<liferay.workspace.bundle.url>", "liferay.com/portal/7.0.");
    }

    @Test
    public void testBuildTemplateWorkspaceWith71() throws Exception {
        File gradleWorkspaceProjectDir = ProjectTemplatesTest._buildTemplateWithGradle(WORKSPACE, "withportlet", "--liferayVersion", "7.1");
        ProjectTemplatesTest._testContains(gradleWorkspaceProjectDir, "gradle.properties", true, ".*liferay.workspace.bundle.url=.*liferay.com/portal/7.1.2-.*");
        File gradlePropertiesFile = new File(gradleWorkspaceProjectDir, "gradle.properties");
        ProjectTemplatesTest._testPropertyKeyExists(gradlePropertiesFile, "liferay.workspace.bundle.url");
        File mavenWorkspaceProjectDir = _buildTemplateWithMaven(WORKSPACE, "withportlet", "com.test", "-DliferayVersion=7.1");
        ProjectTemplatesTest._testContains(mavenWorkspaceProjectDir, "pom.xml", "<liferay.workspace.bundle.url>", "liferay.com/portal/7.1.2-");
    }

    @Test
    public void testBuildTemplateWorkspaceWithPortlet() throws Exception {
        File gradleWorkspaceProjectDir = _buildTemplateWithGradle(WORKSPACE, "withportlet");
        File gradleModulesDir = new File(gradleWorkspaceProjectDir, "modules");
        ProjectTemplatesTest._buildTemplateWithGradle(gradleModulesDir, "mvc-portlet", "foo-portlet");
        File mavenWorkspaceProjectDir = _buildTemplateWithMaven(WORKSPACE, "withportlet", "com.test");
        File mavenModulesDir = new File(mavenWorkspaceProjectDir, "modules");
        ProjectTemplatesTest._buildTemplateWithMaven(mavenWorkspaceProjectDir.getParentFile(), mavenModulesDir, "mvc-portlet", "foo-portlet", "com.test", "-DclassName=Foo", "-Dpackage=foo.portlet", "-DprojectType=workspace");
        ProjectTemplatesTest._executeGradle(gradleWorkspaceProjectDir, (":modules:foo-portlet" + (ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD)));
        ProjectTemplatesTest._testExists(gradleModulesDir, "foo-portlet/build/libs/foo.portlet-1.0.0.jar");
        ProjectTemplatesTest._executeMaven(mavenModulesDir, ProjectTemplatesTest._MAVEN_GOAL_PACKAGE);
        ProjectTemplatesTest._testExists(mavenModulesDir, "foo-portlet/target/foo-portlet-1.0.0.jar");
    }

    @Test
    public void testCompareGradlePluginVersions() throws Exception {
        String template = "mvc-portlet";
        String name = "foo";
        File gradleProjectDir = _buildTemplateWithGradle(template, name);
        File workspaceDir = _buildWorkspace();
        File modulesDir = new File(workspaceDir, "modules");
        ProjectTemplatesTest._buildTemplateWithGradle(modulesDir, template, name);
        Optional<String> result = ProjectTemplatesTest._executeGradle(gradleProjectDir, true, ProjectTemplatesTest._GRADLE_TASK_PATH_BUILD);
        Matcher matcher = ProjectTemplatesTest._gradlePluginVersionPattern.matcher(result.get());
        String standaloneGradlePluginVersion = null;
        if (matcher.matches()) {
            standaloneGradlePluginVersion = matcher.group(1);
        }
        result = ProjectTemplatesTest._executeGradle(workspaceDir, true, ((":modules:" + name) + ":clean"));
        matcher = ProjectTemplatesTest._gradlePluginVersionPattern.matcher(result.get());
        String workspaceGradlePluginVersion = null;
        if (matcher.matches()) {
            workspaceGradlePluginVersion = matcher.group(1);
        }
        Assert.assertEquals("com.liferay.plugin versions do not match", standaloneGradlePluginVersion, workspaceGradlePluginVersion);
    }

    @Test
    public void testCompareServiceBuilderPluginVersions() throws Exception {
        String name = "sample";
        String packageName = "com.test.sample";
        String serviceProjectName = name + "-service";
        File gradleProjectDir = _buildTemplateWithGradle("service-builder", name, "--package-name", packageName);
        Optional<String> gradleResult = ProjectTemplatesTest._executeGradle(gradleProjectDir, true, ((":" + serviceProjectName) + ":dependencies"));
        String gradleServiceBuilderVersion = null;
        Matcher matcher = ProjectTemplatesTest._serviceBuilderVersionPattern.matcher(gradleResult.get());
        if (matcher.matches()) {
            gradleServiceBuilderVersion = matcher.group(1);
        }
        File mavenProjectDir = _buildTemplateWithMaven("service-builder", name, "com.test", ("-Dpackage=" + packageName));
        String mavenResult = ProjectTemplatesTest._executeMaven(new File(mavenProjectDir, serviceProjectName), ProjectTemplatesTest._MAVEN_GOAL_BUILD_SERVICE);
        matcher = ProjectTemplatesTest._serviceBuilderVersionPattern.matcher(mavenResult);
        String mavenServiceBuilderVersion = null;
        if (matcher.matches()) {
            mavenServiceBuilderVersion = matcher.group(1);
        }
        Assert.assertEquals("com.liferay.portal.tools.service.builder versions do not match", gradleServiceBuilderVersion, mavenServiceBuilderVersion);
    }

    @Test
    public void testListTemplates() throws Exception {
        final Map<String, String> expectedTemplates = new TreeMap<>();
        try (DirectoryStream<Path> directoryStream = FileTestUtil.getProjectTemplatesDirectoryStream()) {
            for (Path path : directoryStream) {
                String fileName = String.valueOf(path.getFileName());
                String template = fileName.substring(FileTestUtil.PROJECT_TEMPLATE_DIR_PREFIX.length());
                if (!(template.equals(WORKSPACE))) {
                    Properties properties = FileUtil.readProperties(path.resolve("bnd.bnd"));
                    String bundleDescription = properties.getProperty("Bundle-Description");
                    expectedTemplates.put(template, bundleDescription);
                }
            }
        }
        Assert.assertEquals(expectedTemplates, ProjectTemplates.getTemplates());
    }

    @Test
    public void testListTemplatesWithCustomArchetypesDir() throws Exception {
        Properties archetypesProperties = ProjectTemplatesUtil.getProjectTemplateJarVersionsProperties();
        Set<String> artifactIds = archetypesProperties.stringPropertyNames();
        Iterator<String> artifactIdIterator = artifactIds.iterator();
        String artifactId = artifactIdIterator.next();
        File templateFile = ProjectTemplatesUtil.getArchetypeFile(artifactId);
        Path templateFilePath = templateFile.toPath();
        File customArchetypesDir = temporaryFolder.newFolder();
        Path customArchetypesDirPath = customArchetypesDir.toPath();
        Files.copy(templateFilePath, customArchetypesDirPath.resolve("custom.name.project.templates.foo.bar-1.2.3.jar"));
        List<File> customArchetypesDirs = new ArrayList<>();
        customArchetypesDirs.add(customArchetypesDir);
        Map<String, String> customTemplatesMap = ProjectTemplates.getTemplates(customArchetypesDirs);
        Map<String, String> templatesMap = ProjectTemplates.getTemplates();
        Assert.assertEquals(customTemplatesMap.size(), ((templatesMap.size()) + 1));
    }

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String _BUILD_PROJECTS = System.getProperty("project.templates.test.builds");

    private static final String _BUNDLES_DIFF_IGNORES = StringTestUtil.merge(Arrays.asList("*.js.map", "*manifest.json", "*pom.properties", "*pom.xml", "*package.json", "Archiver-Version", "Build-Jdk", "Built-By", "Javac-Debug", "Javac-Deprecation", "Javac-Encoding"), ',');

    private static final String _DEPENDENCY_MODULES_EXTENDER_API = "compileOnly group: \"com.liferay\", name: " + "\"com.liferay.frontend.js.loader.modules.extender.api\"";

    private static final String _DEPENDENCY_OSGI_CORE = "compileOnly group: \"org.osgi\", name: \"org.osgi.core\"";

    private static final String _DEPENDENCY_PORTAL_KERNEL = "compileOnly group: \"com.liferay.portal\", name: " + "\"com.liferay.portal.kernel\"";

    private static final String _FREEMARKER_PORTLET_VIEW_FTL_PREFIX = "<#include \"init.ftl\">";

    private static final String _GRADLE_TASK_PATH_BUILD = ":build";

    private static final String _GRADLE_TASK_PATH_BUILD_SERVICE = ":buildService";

    private static final String _GRADLE_TASK_PATH_DEPLOY = ":deploy";

    private static final String[] _GRADLE_WRAPPER_FILE_NAMES = new String[]{ "gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.jar", "gradle/wrapper/gradle-wrapper.properties" };

    private static final String _GRADLE_WRAPPER_VERSION = "4.10.2";

    private static final String _MAVEN_GOAL_BUILD_SERVICE = "service-builder:build";

    private static final String _MAVEN_GOAL_PACKAGE = "package";

    private static final String[] _MAVEN_WRAPPER_FILE_NAMES = new String[]{ "mvnw", "mvnw.cmd", ".mvn/wrapper/maven-wrapper.jar", ".mvn/wrapper/maven-wrapper.properties" };

    private static final String _NODEJS_NPM_CI_REGISTRY = System.getProperty("nodejs.npm.ci.registry");

    private static final String _NODEJS_NPM_CI_SASS_BINARY_SITE = System.getProperty("nodejs.npm.ci.sass.binary.site");

    private static final String _OUTPUT_FILENAME_GLOB_REGEX = "*.{jar,war}";

    private static final String _REPOSITORY_CDN_URL = "https://repository-cdn.liferay.com/nexus/content/groups/public";

    private static final String[] _SPRING_MVC_PORTLET_JAR_NAMES = new String[]{ "aop", "beans", "context", "core", "expression", "web", "webmvc", "webmvc-portlet" };

    private static final String _SPRING_MVC_PORTLET_VERSION = "4.1.9.RELEASE";

    private static final boolean _TEST_DEBUG_BUNDLE_DIFFS = Boolean.getBoolean("test.debug.bundle.diffs");

    private static URI _gradleDistribution;

    private static final Pattern _gradlePluginVersionPattern = Pattern.compile(".*com\\.liferay\\.gradle\\.plugins:([0-9]+\\.[0-9]+\\.[0-9]+).*", ((Pattern.DOTALL) | (Pattern.MULTILINE)));

    private static XPathExpression _pomXmlNpmInstallXPathExpression;

    private static final Pattern _serviceBuilderVersionPattern = Pattern.compile(".*service\\.builder:([0-9]+\\.[0-9]+\\.[0-9]+).*", ((Pattern.DOTALL) | (Pattern.MULTILINE)));
}

