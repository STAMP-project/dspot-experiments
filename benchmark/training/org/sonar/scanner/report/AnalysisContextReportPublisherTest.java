/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.report;


import LoggerLevel.DEBUG;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.AnalysisMode;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.fs.internal.DefaultInputModule;
import org.sonar.api.batch.fs.internal.InputModuleHierarchy;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.log.LogTester;
import org.sonar.core.platform.PluginInfo;
import org.sonar.scanner.bootstrap.GlobalServerSettings;
import org.sonar.scanner.bootstrap.ScannerPluginRepository;
import org.sonar.scanner.protocol.output.ScannerReportWriter;
import org.sonar.scanner.scan.ProjectServerSettings;
import org.sonar.scanner.scan.filesystem.InputComponentStore;
import org.sonar.updatecenter.common.Version;


public class AnalysisContextReportPublisherTest {
    private static final String BIZ = "BIZ";

    private static final String FOO = "FOO";

    private static final String SONAR_SKIP = "sonar.skip";

    private static final String COM_FOO = "com.foo";

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private ScannerPluginRepository pluginRepo = Mockito.mock(ScannerPluginRepository.class);

    private AnalysisContextReportPublisher publisher;

    private AnalysisMode analysisMode = Mockito.mock(AnalysisMode.class);

    private System2 system2;

    private GlobalServerSettings globalServerSettings;

    private InputModuleHierarchy hierarchy;

    private InputComponentStore store;

    private ProjectServerSettings projectServerSettings;

    @Test
    public void shouldOnlyDumpPluginsByDefault() throws Exception {
        Mockito.when(pluginRepo.getPluginInfos()).thenReturn(Collections.singletonList(new PluginInfo("xoo").setName("Xoo").setVersion(Version.create("1.0"))));
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        DefaultInputModule rootModule = new DefaultInputModule(ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()));
        Mockito.when(store.allModules()).thenReturn(Collections.singletonList(rootModule));
        Mockito.when(hierarchy.root()).thenReturn(rootModule);
        publisher.init(writer);
        assertThat(writer.getFileStructure().analysisLog()).exists();
        assertThat(FileUtils.readFileToString(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8)).contains("Xoo 1.0 (xoo)");
        Mockito.verifyZeroInteractions(system2);
    }

    @Test
    public void shouldNotDumpInIssuesMode() throws Exception {
        Mockito.when(analysisMode.isIssues()).thenReturn(true);
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        publisher.init(writer);
        assertThat(writer.getFileStructure().analysisLog()).doesNotExist();
    }

    @Test
    public void dumpServerSideGlobalProps() throws Exception {
        logTester.setLevel(DEBUG);
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        Mockito.when(globalServerSettings.properties()).thenReturn(ImmutableMap.of(AnalysisContextReportPublisherTest.COM_FOO, "bar", AnalysisContextReportPublisherTest.SONAR_SKIP, "true"));
        DefaultInputModule rootModule = new DefaultInputModule(ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()).setProperty("sonar.projectKey", "foo"));
        Mockito.when(store.allModules()).thenReturn(Collections.singletonList(rootModule));
        Mockito.when(hierarchy.root()).thenReturn(rootModule);
        publisher.init(writer);
        String content = FileUtils.readFileToString(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8);
        assertThat(content).containsOnlyOnce(AnalysisContextReportPublisherTest.COM_FOO);
        assertThat(content).containsOnlyOnce(AnalysisContextReportPublisherTest.SONAR_SKIP);
    }

    @Test
    public void dumpServerSideProjectProps() throws Exception {
        logTester.setLevel(DEBUG);
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        DefaultInputModule rootModule = new DefaultInputModule(ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()).setProperty("sonar.projectKey", "foo"));
        Mockito.when(store.allModules()).thenReturn(Collections.singletonList(rootModule));
        Mockito.when(hierarchy.root()).thenReturn(rootModule);
        Mockito.when(projectServerSettings.properties()).thenReturn(ImmutableMap.of(AnalysisContextReportPublisherTest.COM_FOO, "bar", AnalysisContextReportPublisherTest.SONAR_SKIP, "true"));
        publisher.init(writer);
        List<String> lines = FileUtils.readLines(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8);
        assertThat(lines).containsExactly("Environment variables:", "System properties:", "SonarQube plugins:", "Global server settings:", "Project server settings:", "  - com.foo=bar", "  - sonar.skip=true", "Project scanner properties:", "  - sonar.projectKey=foo");
    }

    @Test
    public void shouldNotDumpSQPropsInSystemProps() throws Exception {
        logTester.setLevel(DEBUG);
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        Properties props = new Properties();
        props.setProperty(AnalysisContextReportPublisherTest.COM_FOO, "bar");
        props.setProperty(AnalysisContextReportPublisherTest.SONAR_SKIP, "true");
        Mockito.when(system2.properties()).thenReturn(props);
        DefaultInputModule rootModule = new DefaultInputModule(ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()).setProperty("sonar.projectKey", "foo").setProperty(AnalysisContextReportPublisherTest.COM_FOO, "bar").setProperty(AnalysisContextReportPublisherTest.SONAR_SKIP, "true"));
        Mockito.when(store.allModules()).thenReturn(Collections.singletonList(rootModule));
        Mockito.when(hierarchy.root()).thenReturn(rootModule);
        publisher.init(writer);
        List<String> lines = FileUtils.readLines(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8);
        assertThat(lines).containsExactly("Environment variables:", "System properties:", "  - com.foo=bar", "SonarQube plugins:", "Global server settings:", "Project server settings:", "Project scanner properties:", "  - sonar.projectKey=foo", "  - sonar.skip=true");
    }

    @Test
    public void shouldNotDumpEnvTwice() throws Exception {
        logTester.setLevel(DEBUG);
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        Map<String, String> env = new HashMap<>();
        env.put(AnalysisContextReportPublisherTest.FOO, "BAR");
        env.put(AnalysisContextReportPublisherTest.BIZ, "BAZ");
        Mockito.when(system2.envVariables()).thenReturn(env);
        DefaultInputModule rootModule = new DefaultInputModule(ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()).setProperty("sonar.projectKey", "foo").setProperty(("env." + (AnalysisContextReportPublisherTest.FOO)), "BAR"));
        Mockito.when(store.allModules()).thenReturn(Collections.singletonList(rootModule));
        Mockito.when(hierarchy.root()).thenReturn(rootModule);
        publisher.init(writer);
        String content = FileUtils.readFileToString(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8);
        assertThat(content).containsOnlyOnce(AnalysisContextReportPublisherTest.FOO);
        assertThat(content).containsOnlyOnce(AnalysisContextReportPublisherTest.BIZ);
        assertThat(content).containsSubsequence(AnalysisContextReportPublisherTest.BIZ, AnalysisContextReportPublisherTest.FOO);
        content = FileUtils.readFileToString(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8);
        assertThat(content).containsOnlyOnce(AnalysisContextReportPublisherTest.FOO);
        assertThat(content).containsOnlyOnce(AnalysisContextReportPublisherTest.BIZ);
        assertThat(content).doesNotContain(("env." + (AnalysisContextReportPublisherTest.FOO)));
    }

    @Test
    public void shouldNotDumpSensitiveModuleProperties() throws Exception {
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        DefaultInputModule rootModule = new DefaultInputModule(ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()).setProperty("sonar.projectKey", "foo").setProperty("sonar.projectKey", "foo").setProperty("sonar.login", "my_token").setProperty("sonar.password", "azerty").setProperty("sonar.cpp.license.secured", "AZERTY"));
        Mockito.when(store.allModules()).thenReturn(Collections.singletonList(rootModule));
        Mockito.when(hierarchy.root()).thenReturn(rootModule);
        publisher.init(writer);
        assertThat(writer.getFileStructure().analysisLog()).exists();
        assertThat(FileUtils.readFileToString(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8)).containsSubsequence("sonar.cpp.license.secured=******", "sonar.login=******", "sonar.password=******", "sonar.projectKey=foo");
    }

    @Test
    public void shouldShortenModuleProperties() throws Exception {
        File baseDir = temp.newFolder();
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        DefaultInputModule rootModule = new DefaultInputModule(ProjectDefinition.create().setBaseDir(baseDir).setWorkDir(temp.newFolder()).setProperty("sonar.projectKey", "foo").setProperty("sonar.projectBaseDir", baseDir.toString()).setProperty("sonar.aVeryLongProp", StringUtils.repeat("abcde", 1000)));
        Mockito.when(store.allModules()).thenReturn(Collections.singletonList(rootModule));
        Mockito.when(hierarchy.root()).thenReturn(rootModule);
        publisher.init(writer);
        assertThat(writer.getFileStructure().analysisLog()).exists();
        assertThat(FileUtils.readFileToString(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8)).containsSubsequence((("sonar.aVeryLongProp=" + (StringUtils.repeat("abcde", 199))) + "ab..."), ("sonar.projectBaseDir=" + (baseDir.toString())), "sonar.projectKey=foo");
    }

    // SONAR-7598
    @Test
    public void shouldNotDumpSensitiveGlobalProperties() throws Exception {
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        Mockito.when(globalServerSettings.properties()).thenReturn(ImmutableMap.of("sonar.login", "my_token", "sonar.password", "azerty", "sonar.cpp.license.secured", "AZERTY"));
        DefaultInputModule rootModule = new DefaultInputModule(ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()).setProperty("sonar.projectKey", "foo"));
        Mockito.when(store.allModules()).thenReturn(Collections.singletonList(rootModule));
        Mockito.when(hierarchy.root()).thenReturn(rootModule);
        publisher.init(writer);
        assertThat(FileUtils.readFileToString(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8)).containsSubsequence("sonar.cpp.license.secured=******", "sonar.login=******", "sonar.password=******");
    }

    // SONAR-7371
    @Test
    public void dontDumpParentProps() throws Exception {
        logTester.setLevel(DEBUG);
        ScannerReportWriter writer = new ScannerReportWriter(temp.newFolder());
        DefaultInputModule module = new DefaultInputModule(ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()).setProperty("sonar.projectKey", "foo").setProperty(AnalysisContextReportPublisherTest.SONAR_SKIP, "true"));
        DefaultInputModule parent = new DefaultInputModule(ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()).setProperty("sonar.projectKey", "parent").setProperty(AnalysisContextReportPublisherTest.SONAR_SKIP, "true"));
        Mockito.when(hierarchy.parent(module)).thenReturn(parent);
        Mockito.when(store.allModules()).thenReturn(Arrays.asList(parent, module));
        Mockito.when(hierarchy.root()).thenReturn(parent);
        publisher.init(writer);
        List<String> lines = FileUtils.readLines(writer.getFileStructure().analysisLog(), StandardCharsets.UTF_8);
        assertThat(lines).containsExactly("Environment variables:", "System properties:", "SonarQube plugins:", "Global server settings:", "Project server settings:", "Project scanner properties:", "  - sonar.projectKey=parent", "  - sonar.skip=true", "Scanner properties of module: foo", "  - sonar.projectKey=foo");
    }
}

