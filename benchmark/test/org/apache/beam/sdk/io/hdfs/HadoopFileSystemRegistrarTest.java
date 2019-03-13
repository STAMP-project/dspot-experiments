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


import java.net.URI;
import java.util.ServiceLoader;
import org.apache.beam.sdk.io.FileSystem;
import org.apache.beam.sdk.io.FileSystemRegistrar;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link HadoopFileSystemRegistrar}.
 */
@RunWith(JUnit4.class)
public class HadoopFileSystemRegistrarTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private Configuration configuration;

    private MiniDFSCluster hdfsCluster;

    private URI hdfsClusterBaseUri;

    @Test
    public void testServiceLoader() {
        HadoopFileSystemOptions options = PipelineOptionsFactory.as(HadoopFileSystemOptions.class);
        options.setHdfsConfiguration(ImmutableList.of(configuration));
        for (FileSystemRegistrar registrar : Lists.newArrayList(ServiceLoader.load(FileSystemRegistrar.class).iterator())) {
            if (registrar instanceof HadoopFileSystemRegistrar) {
                Iterable<FileSystem> fileSystems = registrar.fromOptions(options);
                Assert.assertEquals(hdfsClusterBaseUri.getScheme(), getScheme());
                return;
            }
        }
        Assert.fail(("Expected to find " + (HadoopFileSystemRegistrar.class)));
    }
}

