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
package org.apache.flink.runtime.clusterframework.overlays;


import ConfigConstants.PATH_HADOOP_CONFIG;
import HadoopConfOverlay.Builder;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.runtime.clusterframework.ContainerSpecification;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class HadoopConfOverlayTest extends ContainerOverlayTestBase {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testConfigure() throws Exception {
        File confDir = tempFolder.newFolder();
        initConfDir(confDir);
        HadoopConfOverlay overlay = new HadoopConfOverlay(confDir);
        ContainerSpecification spec = new ContainerSpecification();
        overlay.configure(spec);
        Assert.assertEquals(HadoopConfOverlay.TARGET_CONF_DIR.getPath(), spec.getEnvironmentVariables().get("HADOOP_CONF_DIR"));
        Assert.assertEquals(HadoopConfOverlay.TARGET_CONF_DIR.getPath(), spec.getDynamicConfiguration().getString(PATH_HADOOP_CONFIG, null));
        ContainerOverlayTestBase.checkArtifact(spec, new org.apache.flink.core.fs.Path(HadoopConfOverlay.TARGET_CONF_DIR, "core-site.xml"));
        ContainerOverlayTestBase.checkArtifact(spec, new org.apache.flink.core.fs.Path(HadoopConfOverlay.TARGET_CONF_DIR, "hdfs-site.xml"));
    }

    @Test
    public void testNoConf() throws Exception {
        HadoopConfOverlay overlay = new HadoopConfOverlay(null);
        ContainerSpecification containerSpecification = new ContainerSpecification();
        overlay.configure(containerSpecification);
    }

    @Test
    public void testBuilderFromEnvironment() throws Exception {
        // verify that the builder picks up various environment locations
        HadoopConfOverlay.Builder builder;
        Map<String, String> env;
        // fs.hdfs.hadoopconf
        File confDir = tempFolder.newFolder();
        initConfDir(confDir);
        Configuration conf = new Configuration();
        conf.setString(PATH_HADOOP_CONFIG, confDir.getAbsolutePath());
        builder = HadoopConfOverlay.newBuilder().fromEnvironment(conf);
        Assert.assertEquals(confDir, builder.hadoopConfDir);
        // HADOOP_CONF_DIR
        env = new HashMap<String, String>(System.getenv());
        env.remove("HADOOP_HOME");
        env.put("HADOOP_CONF_DIR", confDir.getAbsolutePath());
        CommonTestUtils.setEnv(env);
        builder = HadoopConfOverlay.newBuilder().fromEnvironment(new Configuration());
        Assert.assertEquals(confDir, builder.hadoopConfDir);
        // HADOOP_HOME/conf
        File homeDir = tempFolder.newFolder();
        confDir = initConfDir(new File(homeDir, "conf"));
        env = new HashMap<String, String>(System.getenv());
        env.remove("HADOOP_CONF_DIR");
        env.put("HADOOP_HOME", homeDir.getAbsolutePath());
        CommonTestUtils.setEnv(env);
        builder = HadoopConfOverlay.newBuilder().fromEnvironment(new Configuration());
        Assert.assertEquals(confDir, builder.hadoopConfDir);
        // HADOOP_HOME/etc/hadoop
        homeDir = tempFolder.newFolder();
        confDir = initConfDir(new File(homeDir, "etc/hadoop"));
        env = new HashMap<String, String>(System.getenv());
        env.remove("HADOOP_CONF_DIR");
        env.put("HADOOP_HOME", homeDir.getAbsolutePath());
        CommonTestUtils.setEnv(env);
        builder = HadoopConfOverlay.newBuilder().fromEnvironment(new Configuration());
        Assert.assertEquals(confDir, builder.hadoopConfDir);
    }
}

