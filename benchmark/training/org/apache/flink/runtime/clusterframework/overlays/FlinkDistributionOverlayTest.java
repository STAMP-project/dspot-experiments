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


import FlinkDistributionOverlay.Builder;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.runtime.clusterframework.ContainerSpecification;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FlinkDistributionOverlayTest extends ContainerOverlayTestBase {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testConfigure() throws Exception {
        File binFolder = tempFolder.newFolder("bin");
        File libFolder = tempFolder.newFolder("lib");
        File confFolder = tempFolder.newFolder("conf");
        Path[] files = ContainerOverlayTestBase.createPaths(tempFolder.getRoot(), "bin/config.sh", "bin/taskmanager.sh", "lib/foo.jar", "lib/A/foo.jar", "lib/B/foo.jar", "lib/B/bar.jar");
        ContainerSpecification containerSpecification = new ContainerSpecification();
        FlinkDistributionOverlay overlay = new FlinkDistributionOverlay(binFolder, confFolder, libFolder);
        overlay.configure(containerSpecification);
        for (Path file : files) {
            ContainerOverlayTestBase.checkArtifact(containerSpecification, new Path(FlinkDistributionOverlay.TARGET_ROOT, file.toString()));
        }
    }

    @Test
    public void testBuilderFromEnvironment() throws Exception {
        Configuration conf = new Configuration();
        File binFolder = tempFolder.newFolder("bin");
        File libFolder = tempFolder.newFolder("lib");
        File confFolder = tempFolder.newFolder("conf");
        // adjust the test environment for the purposes of this test
        Map<String, String> map = new HashMap<String, String>(System.getenv());
        map.put(ENV_FLINK_BIN_DIR, binFolder.getAbsolutePath());
        map.put(ENV_FLINK_LIB_DIR, libFolder.getAbsolutePath());
        map.put(ENV_FLINK_CONF_DIR, confFolder.getAbsolutePath());
        CommonTestUtils.setEnv(map);
        FlinkDistributionOverlay.Builder builder = FlinkDistributionOverlay.newBuilder().fromEnvironment(conf);
        Assert.assertEquals(binFolder.getAbsolutePath(), builder.flinkBinPath.getAbsolutePath());
        Assert.assertEquals(libFolder.getAbsolutePath(), builder.flinkLibPath.getAbsolutePath());
        Assert.assertEquals(confFolder.getAbsolutePath(), builder.flinkConfPath.getAbsolutePath());
    }

    @Test
    public void testBuilderFromEnvironmentBad() throws Exception {
        Configuration conf = new Configuration();
        // adjust the test environment for the purposes of this test
        Map<String, String> map = new HashMap<>(System.getenv());
        map.remove(ENV_FLINK_BIN_DIR);
        map.remove(ENV_FLINK_LIB_DIR);
        map.remove(ENV_FLINK_CONF_DIR);
        CommonTestUtils.setEnv(map);
        try {
            FlinkDistributionOverlay.Builder builder = FlinkDistributionOverlay.newBuilder().fromEnvironment(conf);
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }
}

