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


import com.liferay.project.templates.util.FileTestUtil;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Di Giorgi
 */
public class ProjectTemplateFilesTest {
    @Test
    public void testProjectTemplateFiles() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        try (DirectoryStream<Path> directoryStream = FileTestUtil.getProjectTemplatesDirectoryStream()) {
            for (Path path : directoryStream) {
                _testProjectTemplateFiles(path, documentBuilder);
            }
        }
    }

    private static final String[] _SOURCESET_NAMES = new String[]{ "main", "test", "testIntegration" };

    private static final List<String> _archetypeMetadataXmlDefaultPropertyNames = Arrays.asList("artifactId", "groupId", "package", "project", "version");

    private static final Pattern _archetypeMetadataXmlIncludePattern = Pattern.compile("<include>([^\\*]+?)<\\/include>");

    private static final Pattern _archetypeMetadataXmlRequiredPropertyPattern = Pattern.compile("<requiredProperty key=\"(\\w+)\">");

    private static final Pattern _archetypeResourceExpressionPattern = Pattern.compile("\\$\\{([^\\}]*)\\}");

    private static final Pattern _buildGradleDependencyPattern = Pattern.compile(("(compile(?:Only)?) group: \"(.+)\", name: \"(.+)\", " + "(?:transitive: (?:true|false), )?version: \"(.+)\""));

    private static final Pattern _buildGradleWorkspaceVariantPattern = Pattern.compile(".*^#if \\(\\$\\{projectType\\} != \"workspace\"\\).*", ((Pattern.DOTALL) | (Pattern.MULTILINE)));

    private static final Pattern _bundleDescriptionPattern = Pattern.compile("Creates a .+\\.");

    private static final List<String> _gitIgnoreLines = Arrays.asList(".gradle/", "build/", "target/");

    private static final Comparator<String> _languagePropertiesKeyComparator = new Comparator<String>() {
        @Override
        public int compare(String key1, String key2) {
            boolean key1StartsWithLetter = Character.isLetter(key1.charAt(0));
            boolean key2StartsWithLetter = Character.isLetter(key2.charAt(0));
            if (key1StartsWithLetter && (!key2StartsWithLetter)) {
                return -1;
            }
            if ((!key1StartsWithLetter) && key2StartsWithLetter) {
                return 1;
            }
            return key1.compareTo(key2);
        }
    };

    private static final Pattern _pomXmlExecutionIdPattern = Pattern.compile("[a-z]+(?:-[a-z]+)*");

    private static final Pattern _projectTemplateDirNameSeparatorPattern = Pattern.compile("(?:^|-)(\\w)");

    private static final Set<String> _textFileExtensions = new HashSet<>(Arrays.asList("bnd", "gradle", "java", "js", "json", "jsp", "jspf", "properties", "vm", "xml"));

    private static final Pattern _velocityDirectivePattern = Pattern.compile("#(if|set)\\s*\\(\\s*(.+)\\s*\\)");

    private static final Pattern _velocitySetDirectivePattern = Pattern.compile("#set\\s*\\(\\s*\\$(\\S+)\\s*=");

    private static final Map<String, String> _xmlDeclarations = new HashMap<>();

    static {
        try {
            ProjectTemplateFilesTest._addXmlDeclaration(null, "xml_declaration.tmpl");
            ProjectTemplateFilesTest._addXmlDeclaration("liferay-display.xml", "liferay_display_xml_declaration.tmpl");
            ProjectTemplateFilesTest._addXmlDeclaration("liferay-hook.xml", "liferay_hook_xml_declaration.tmpl");
            ProjectTemplateFilesTest._addXmlDeclaration("liferay-layout-templates.xml", "liferay_layout_templates_xml_declaration.tmpl");
            ProjectTemplateFilesTest._addXmlDeclaration("liferay-portlet.xml", "liferay_portlet_xml_declaration.tmpl");
            ProjectTemplateFilesTest._addXmlDeclaration("pom.xml", "pom_xml_declaration.tmpl");
            ProjectTemplateFilesTest._addXmlDeclaration("portlet.xml", "portlet_xml_declaration.tmpl");
            ProjectTemplateFilesTest._addXmlDeclaration("service.xml", "service_xml_declaration.tmpl");
            ProjectTemplateFilesTest._addXmlDeclaration("web.xml", "web_xml_declaration.tmpl");
        } catch (IOException ioe) {
            throw new ExceptionInInitializerError(ioe);
        }
    }

    private static class BuildGradleDependency {
        public BuildGradleDependency(String group, String name, String version, boolean provided) {
            this.group = group;
            this.name = name;
            this.version = version;
            this.provided = provided;
        }

        public final String group;

        public final String name;

        public final boolean provided;

        public final String version;
    }
}

