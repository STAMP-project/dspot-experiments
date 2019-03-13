/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.hdfs;


import HadoopFileSystemOptions.ConfigurationLocator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Maps;
import org.apache.beam.vendor.guava.v20_0.com.google.common.io.Files;
import org.apache.hadoop.conf.Configuration;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link HadoopFileSystemOptions}.
 */
@RunWith(JUnit4.class)
public class HadoopFileSystemOptionsTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @SuppressWarnings("unchecked")
    @Test
    public void testParsingHdfsConfiguration() {
        HadoopFileSystemOptions options = PipelineOptionsFactory.fromArgs(("--hdfsConfiguration=[" + ("{\"propertyA\": \"A\"}," + "{\"propertyB\": \"B\"}]"))).as(HadoopFileSystemOptions.class);
        Assert.assertEquals(2, options.getHdfsConfiguration().size());
        Assert.assertThat(options.getHdfsConfiguration().get(0), Matchers.<Map.Entry<String, String>>contains(new AbstractMap.SimpleEntry("propertyA", "A")));
        Assert.assertThat(options.getHdfsConfiguration().get(1), Matchers.<Map.Entry<String, String>>contains(new AbstractMap.SimpleEntry("propertyB", "B")));
    }

    @Test
    public void testDefaultUnsetEnvHdfsConfiguration() {
        HadoopFileSystemOptions.ConfigurationLocator projectFactory = Mockito.spy(new HadoopFileSystemOptions.ConfigurationLocator());
        Mockito.when(projectFactory.getEnvironment()).thenReturn(ImmutableMap.of());
        Assert.assertNull(projectFactory.create(PipelineOptionsFactory.create()));
    }

    @Test
    public void testDefaultJustSetHadoopConfDirConfiguration() throws IOException {
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("A"), tmpFolder.newFile("core-site.xml"), StandardCharsets.UTF_8);
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("B"), tmpFolder.newFile("hdfs-site.xml"), StandardCharsets.UTF_8);
        HadoopFileSystemOptions.ConfigurationLocator configurationLocator = Mockito.spy(new HadoopFileSystemOptions.ConfigurationLocator());
        Map<String, String> environment = Maps.newHashMap();
        environment.put("HADOOP_CONF_DIR", tmpFolder.getRoot().getAbsolutePath());
        Mockito.when(configurationLocator.getEnvironment()).thenReturn(environment);
        List<Configuration> configurationList = configurationLocator.create(PipelineOptionsFactory.create());
        Assert.assertEquals(1, configurationList.size());
        Assert.assertThat(configurationList.get(0).get("propertyA"), Matchers.equalTo("A"));
        Assert.assertThat(configurationList.get(0).get("propertyB"), Matchers.equalTo("B"));
    }

    @Test
    public void testDefaultJustSetYarnConfDirConfiguration() throws IOException {
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("A"), tmpFolder.newFile("core-site.xml"), StandardCharsets.UTF_8);
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("B"), tmpFolder.newFile("hdfs-site.xml"), StandardCharsets.UTF_8);
        HadoopFileSystemOptions.ConfigurationLocator configurationLocator = Mockito.spy(new HadoopFileSystemOptions.ConfigurationLocator());
        Map<String, String> environment = Maps.newHashMap();
        environment.put("YARN_CONF_DIR", tmpFolder.getRoot().getAbsolutePath());
        Mockito.when(configurationLocator.getEnvironment()).thenReturn(environment);
        List<Configuration> configurationList = configurationLocator.create(PipelineOptionsFactory.create());
        Assert.assertEquals(1, configurationList.size());
        Assert.assertThat(configurationList.get(0).get("propertyA"), Matchers.equalTo("A"));
        Assert.assertThat(configurationList.get(0).get("propertyB"), Matchers.equalTo("B"));
    }

    @Test
    public void testDefaultSetYarnConfDirAndHadoopConfDirAndSameConfiguration() throws IOException {
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("A"), tmpFolder.newFile("core-site.xml"), StandardCharsets.UTF_8);
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("B"), tmpFolder.newFile("hdfs-site.xml"), StandardCharsets.UTF_8);
        HadoopFileSystemOptions.ConfigurationLocator configurationLocator = Mockito.spy(new HadoopFileSystemOptions.ConfigurationLocator());
        Map<String, String> environment = Maps.newHashMap();
        environment.put("YARN_CONF_DIR", tmpFolder.getRoot().getAbsolutePath());
        environment.put("HADOOP_CONF_DIR", tmpFolder.getRoot().getAbsolutePath());
        Mockito.when(configurationLocator.getEnvironment()).thenReturn(environment);
        List<Configuration> configurationList = configurationLocator.create(PipelineOptionsFactory.create());
        Assert.assertEquals(1, configurationList.size());
        Assert.assertThat(configurationList.get(0).get("propertyA"), Matchers.equalTo("A"));
        Assert.assertThat(configurationList.get(0).get("propertyB"), Matchers.equalTo("B"));
    }

    @Test
    public void testDefaultSetYarnConfDirAndHadoopConfDirNotSameConfiguration() throws IOException {
        File hadoopConfDir = tmpFolder.newFolder("hadoop");
        File yarnConfDir = tmpFolder.newFolder("yarn");
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("A"), new File(hadoopConfDir, "core-site.xml"), StandardCharsets.UTF_8);
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("B"), new File(hadoopConfDir, "hdfs-site.xml"), StandardCharsets.UTF_8);
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("C"), new File(yarnConfDir, "core-site.xml"), StandardCharsets.UTF_8);
        Files.write(HadoopFileSystemOptionsTest.createPropertyData("D"), new File(yarnConfDir, "hdfs-site.xml"), StandardCharsets.UTF_8);
        HadoopFileSystemOptions.ConfigurationLocator configurationLocator = Mockito.spy(new HadoopFileSystemOptions.ConfigurationLocator());
        Map<String, String> environment = Maps.newHashMap();
        environment.put("YARN_CONF_DIR", hadoopConfDir.getAbsolutePath());
        environment.put("HADOOP_CONF_DIR", yarnConfDir.getAbsolutePath());
        Mockito.when(configurationLocator.getEnvironment()).thenReturn(environment);
        List<Configuration> configurationList = configurationLocator.create(PipelineOptionsFactory.create());
        Assert.assertEquals(2, configurationList.size());
        int hadoopConfIndex = ((configurationList.get(0).get("propertyA")) != null) ? 0 : 1;
        Assert.assertThat(configurationList.get(hadoopConfIndex).get("propertyA"), Matchers.equalTo("A"));
        Assert.assertThat(configurationList.get(hadoopConfIndex).get("propertyB"), Matchers.equalTo("B"));
        Assert.assertThat(configurationList.get((1 - hadoopConfIndex)).get("propertyC"), Matchers.equalTo("C"));
        Assert.assertThat(configurationList.get((1 - hadoopConfIndex)).get("propertyD"), Matchers.equalTo("D"));
    }
}

