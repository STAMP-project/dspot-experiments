/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config.parts;


import com.thoughtworks.go.config.ConfigCache;
import com.thoughtworks.go.config.MagicalGoConfigXmlLoader;
import com.thoughtworks.go.config.MagicalGoConfigXmlWriter;
import com.thoughtworks.go.config.remote.PartialConfig;
import com.thoughtworks.go.domain.PipelineGroups;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.helper.EnvironmentConfigMother;
import com.thoughtworks.go.helper.GoConfigMother;
import java.io.File;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class XmlPartialConfigProviderTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ConfigCache configCache = new ConfigCache();

    private MagicalGoConfigXmlLoader xmlLoader;

    private XmlPartialConfigProvider xmlPartialProvider;

    private MagicalGoConfigXmlWriter xmlWriter;

    private PartialConfigHelper helper;

    private File tmpFolder;

    @Test
    public void shouldParseFileWithOnePipeline() throws Exception {
        GoConfigMother mother = new GoConfigMother();
        PipelineConfig pipe1 = mother.cruiseConfigWithOnePipelineGroup().getAllPipelineConfigs().get(0);
        File file = helper.addFileWithPipeline("pipe1.gocd.xml", pipe1);
        PartialConfig part = xmlPartialProvider.parseFile(file);
        PipelineConfig pipeRead = part.getGroups().get(0).get(0);
        Assert.assertThat(pipeRead, Matchers.is(pipe1));
    }

    @Test
    public void shouldParseFileWithOnePipelineGroup() throws Exception {
        GoConfigMother mother = new GoConfigMother();
        PipelineConfigs group1 = mother.cruiseConfigWithOnePipelineGroup().getGroups().get(0);
        File file = helper.addFileWithPipelineGroup("group1.gocd.xml", group1);
        PartialConfig part = xmlPartialProvider.parseFile(file);
        PipelineConfigs groupRead = part.getGroups().get(0);
        Assert.assertThat(groupRead, Matchers.is(group1));
        Assert.assertThat(groupRead.size(), Matchers.is(group1.size()));
        Assert.assertThat(groupRead.get(0), Matchers.is(group1.get(0)));
    }

    @Test
    public void shouldParseFileWithOneEnvironment() throws Exception {
        EnvironmentConfig env = EnvironmentConfigMother.environment("dev");
        File file = helper.addFileWithEnvironment("dev-env.gocd.xml", env);
        PartialConfig part = xmlPartialProvider.parseFile(file);
        EnvironmentsConfig loadedEnvs = part.getEnvironments();
        Assert.assertThat(loadedEnvs.size(), Matchers.is(1));
        Assert.assertThat(loadedEnvs.get(0), Matchers.is(env));
    }

    @Test
    public void shouldLoadDirectoryWithOnePipeline() throws Exception {
        GoConfigMother mother = new GoConfigMother();
        PipelineConfig pipe1 = mother.cruiseConfigWithOnePipelineGroup().getAllPipelineConfigs().get(0);
        helper.addFileWithPipeline("pipe1.gocd.xml", pipe1);
        PartialConfig part = xmlPartialProvider.load(tmpFolder, Mockito.mock(PartialConfigLoadContext.class));
        PipelineConfig pipeRead = part.getGroups().get(0).get(0);
        Assert.assertThat(pipeRead, Matchers.is(pipe1));
    }

    @Test
    public void shouldLoadDirectoryWithOnePipelineGroup() throws Exception {
        GoConfigMother mother = new GoConfigMother();
        PipelineConfigs group1 = mother.cruiseConfigWithOnePipelineGroup().getGroups().get(0);
        helper.addFileWithPipelineGroup("group1.gocd.xml", group1);
        PartialConfig part = xmlPartialProvider.load(tmpFolder, Mockito.mock(PartialConfigLoadContext.class));
        PipelineConfigs groupRead = part.getGroups().get(0);
        Assert.assertThat(groupRead, Matchers.is(group1));
        Assert.assertThat(groupRead.size(), Matchers.is(group1.size()));
        Assert.assertThat(groupRead.get(0), Matchers.is(group1.get(0)));
    }

    @Test
    public void shouldLoadDirectoryWithTwoPipelineGroupsAndEnvironment() throws Exception {
        GoConfigMother mother = new GoConfigMother();
        PipelineGroups groups = mother.cruiseConfigWithTwoPipelineGroups().getGroups();
        EnvironmentConfig env = EnvironmentConfigMother.environment("dev");
        helper.addFileWithPipelineGroup("group1.gocd.xml", groups.get(0));
        helper.addFileWithPipelineGroup("group2.gocd.xml", groups.get(1));
        helper.addFileWithEnvironment("dev-env.gocd.xml", env);
        PartialConfig part = xmlPartialProvider.load(tmpFolder, Mockito.mock(PartialConfigLoadContext.class));
        PipelineGroups groupsRead = part.getGroups();
        Assert.assertThat(groupsRead.size(), Matchers.is(2));
        EnvironmentsConfig loadedEnvs = part.getEnvironments();
        Assert.assertThat(loadedEnvs.size(), Matchers.is(1));
        Assert.assertThat(loadedEnvs.get(0), Matchers.is(env));
    }

    @Test
    public void shouldGetFilesToLoadMatchingPattern() throws Exception {
        GoConfigMother mother = new GoConfigMother();
        PipelineConfig pipe1 = mother.cruiseConfigWithOnePipelineGroup().getAllPipelineConfigs().get(0);
        File file1 = helper.addFileWithPipeline("pipe1.gocd.xml", pipe1);
        File file2 = helper.addFileWithPipeline("pipe1.gcd.xml", pipe1);
        File file3 = helper.addFileWithPipeline("subdir/pipe1.gocd.xml", pipe1);
        File file4 = helper.addFileWithPipeline("subdir/sub/pipe1.gocd.xml", pipe1);
        File[] matchingFiles = xmlPartialProvider.getFiles(tmpFolder, Mockito.mock(PartialConfigLoadContext.class));
        File[] expected = new File[]{ file1, file3, file4 };
        Assert.assertArrayEquals(("Matched files are: " + (Arrays.asList(matchingFiles).toString())), expected, matchingFiles);
    }

    @Test
    public void shouldUseExplicitPatternWhenProvided() throws Exception {
        GoConfigMother mother = new GoConfigMother();
        PipelineConfig pipe1 = mother.cruiseConfigWithOnePipelineGroup().getAllPipelineConfigs().get(0);
        File file1 = helper.addFileWithPipeline("pipe1.myextension", pipe1);
        File file2 = helper.addFileWithPipeline("pipe1.gcd.xml", pipe1);
        File file3 = helper.addFileWithPipeline("subdir/pipe1.gocd.xml", pipe1);
        File file4 = helper.addFileWithPipeline("subdir/sub/pipe1.gocd.xml", pipe1);
        PartialConfigLoadContext context = Mockito.mock(PartialConfigLoadContext.class);
        Configuration configs = new Configuration();
        configs.addNewConfigurationWithValue("pattern", "*.myextension", false);
        Mockito.when(context.configuration()).thenReturn(configs);
        File[] matchingFiles = xmlPartialProvider.getFiles(tmpFolder, context);
        File[] expected = new File[1];
        expected[0] = file1;
        Assert.assertArrayEquals(expected, matchingFiles);
    }

    @Test
    public void shouldFailToLoadDirectoryWithDuplicatedPipeline() throws Exception {
        GoConfigMother mother = new GoConfigMother();
        PipelineConfig pipe1 = mother.cruiseConfigWithOnePipelineGroup().getAllPipelineConfigs().get(0);
        helper.addFileWithPipeline("pipe1.gocd.xml", pipe1);
        helper.addFileWithPipeline("pipedup.gocd.xml", pipe1);
        try {
            PartialConfig part = xmlPartialProvider.load(tmpFolder, Mockito.mock(PartialConfigLoadContext.class));
        } catch (Exception ex) {
            Assert.assertThat(ex.getMessage(), Matchers.is("You have defined multiple pipelines called 'pipeline1'. Pipeline names must be unique."));
            return;
        }
        Assert.fail("should have thrown");
    }

    @Test
    public void shouldFailToLoadDirectoryWithNonXmlFormat() throws Exception {
        String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + ("<cruise xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"cruise-config.xsd\" schemaVersion=\"38\">\n" + "/cruise>");// missing '<'

        helper.writeFileWithContent("bad.gocd.xml", content);
        try {
            PartialConfig part = xmlPartialProvider.load(tmpFolder, Mockito.mock(PartialConfigLoadContext.class));
        } catch (RuntimeException ex) {
            return;
        }
        Assert.fail("should have thrown");
    }
}

