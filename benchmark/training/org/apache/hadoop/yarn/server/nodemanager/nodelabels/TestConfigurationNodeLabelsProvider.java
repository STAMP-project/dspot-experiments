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
package org.apache.hadoop.yarn.server.nodemanager.nodelabels;


import AbstractNodeDescriptorsProvider.DISABLE_NODE_DESCRIPTORS_PROVIDER_FETCH_TIMER;
import YarnConfiguration.NM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS;
import YarnConfiguration.YARN_SITE_CONFIGURATION_FILE;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimerTask;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.nodelabels.NodeLabelTestBase;
import org.junit.Assert;
import org.junit.Test;


public class TestConfigurationNodeLabelsProvider extends NodeLabelTestBase {
    protected static File testRootDir = new File("target", ((TestConfigurationNodeLabelsProvider.class.getName()) + "-localDir")).getAbsoluteFile();

    static final File nodeLabelsConfigFile = new File(TestConfigurationNodeLabelsProvider.testRootDir, "yarn-site.xml");

    private static TestConfigurationNodeLabelsProvider.XMLPathClassLoader loader;

    private ConfigurationNodeLabelsProvider nodeLabelsProvider;

    private static ClassLoader classContextClassLoader;

    @Test
    public void testNodeLabelsFromConfig() throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        TestConfigurationNodeLabelsProvider.modifyConf("A");
        nodeLabelsProvider.init(conf);
        // test for ensuring labels are set during initialization of the class
        nodeLabelsProvider.start();
        assertNLCollectionEquals(toNodeLabelSet("A"), nodeLabelsProvider.getDescriptors());
        // test for valid Modification
        TimerTask timerTask = nodeLabelsProvider.getTimerTask();
        TestConfigurationNodeLabelsProvider.modifyConf("X");
        timerTask.run();
        assertNLCollectionEquals(toNodeLabelSet("X"), nodeLabelsProvider.getDescriptors());
    }

    @Test
    public void testConfigForNoTimer() throws Exception {
        Configuration conf = new Configuration();
        TestConfigurationNodeLabelsProvider.modifyConf("A");
        conf.setLong(NM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS, DISABLE_NODE_DESCRIPTORS_PROVIDER_FETCH_TIMER);
        nodeLabelsProvider.init(conf);
        nodeLabelsProvider.start();
        Assert.assertNull(("Timer is not expected to be" + " created when interval is configured as -1"), nodeLabelsProvider.getScheduler());
        // Ensure that even though timer is not run, node labels
        // are fetched at least once so that NM registers/updates Labels with RM
        assertNLCollectionEquals(toNodeLabelSet("A"), nodeLabelsProvider.getDescriptors());
    }

    @Test
    public void testConfigTimer() throws Exception {
        Configuration conf = new Configuration();
        TestConfigurationNodeLabelsProvider.modifyConf("A");
        conf.setLong(NM_NODE_LABELS_PROVIDER_FETCH_INTERVAL_MS, 1000);
        nodeLabelsProvider.init(conf);
        nodeLabelsProvider.start();
        // Ensure that even though timer is not run, node labels are fetched at
        // least once so
        // that NM registers/updates Labels with RM
        assertNLCollectionEquals(toNodeLabelSet("A"), nodeLabelsProvider.getDescriptors());
        TestConfigurationNodeLabelsProvider.modifyConf("X");
        Thread.sleep(1500);
        assertNLCollectionEquals(toNodeLabelSet("X"), nodeLabelsProvider.getDescriptors());
    }

    private static class XMLPathClassLoader extends ClassLoader {
        public XMLPathClassLoader(ClassLoader wrapper) {
            super(wrapper);
        }

        public URL getResource(String name) {
            if (name.equals(YARN_SITE_CONFIGURATION_FILE)) {
                try {
                    return TestConfigurationNodeLabelsProvider.nodeLabelsConfigFile.toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Assert.fail();
                }
            }
            return super.getResource(name);
        }
    }
}

