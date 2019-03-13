/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.yarn.core;


import DrillOnYarnConfig.APP_NAME;
import DrillOnYarnConfig.CLUSTER_ID;
import DrillOnYarnConfig.DFS_APP_DIR;
import DrillOnYarnConfig.DRILL_ARCHIVE_KEY;
import DrillOnYarnConfig.DRILL_ARCHIVE_PATH;
import DrillOnYarnConfig.DRILL_HOME_ENV_VAR;
import DrillOnYarnConfig.DRILL_SITE_ENV_VAR;
import DrillOnYarnConfig.LOCALIZE_DRILL;
import DrillOnYarnConfig.SITE_ARCHIVE_KEY;
import DrillOnYarnConfig.ZK_ROOT;
import com.typesafe.config.Config;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TestConfig {
    /**
     * Mock config that lets us tinker with loading and environment access for
     * testing.
     */
    private static class DoYTestConfig extends DrillOnYarnConfig {
        protected Map<String, String> mockEnv = new HashMap<>();

        protected File configDir;

        public DoYTestConfig(TestConfig.TestClassLoader cl, File configDir) throws DoyConfigException {
            doLoad(cl);
            instance = this;
            this.configDir = configDir;
        }

        @Override
        protected String getEnv(String key) {
            return mockEnv.get(key);
        }
    }

    /**
     * Mock class loader to let us add config files after the JVM starts. (In
     * production code, the config file directories are added to the class path.)
     */
    private static class TestClassLoader extends ClassLoader {
        private File configDir;

        public TestClassLoader(ClassLoader parent, File configDir) {
            super(parent);
            this.configDir = configDir;
        }

        @Override
        protected URL findResource(String name) {
            File file = new File(configDir, name);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    // noop
                }
            }
            return null;
        }
    }

    /**
     * Creates a stack of settings to test overrides.
     * <table>
     * <tr>
     * <th>property</th>
     * <th>default</th>
     * <th>distrib</th>
     * <th>user</th>
     * <th>system</th>
     * </tr>
     * <tr>
     * <td>drill-key</td>
     * <td>"drill"</td>
     * <td></td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>app-dir</td>
     * <td>"/user/drill"</td>
     * <td>"/opt/drill"</td>
     * <td></td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>app-name</td>
     * <td>"Drill-on-YARN"</td>
     * <td>"config-app-name"</td>
     * <td>"My-App"</td>
     * <td></td>
     * </tr>
     * <tr>
     * <td>queue</td>
     * <td>"default"</td>
     * <td>"distrib-queue"</td>
     * <td>"my-queue"</td>
     * <td>"sys-queue"</td>
     * </table>
     * <p>
     * Full property names:
     * <ul>
     * <li>drill.yarn.drill-install.drill-key</li>
     * <li>drill.yarn.dfs.app-dir</li>
     * <li>drill.yarn.app-name</li>
     * <li>drill.yarn.zk.connect</li>
     * </ul>
     *
     * @throws IOException
     * 		
     * @throws DoyConfigException
     * 		
     */
    @Test
    public void testLoad() throws IOException, DoyConfigException {
        TestConfig.DoYTestConfig doyConfig = initConfig("test-doy-config.conf");
        Config config = DrillOnYarnConfig.config();
        Assert.assertEquals("drill", config.getString(DRILL_ARCHIVE_KEY));
        Assert.assertEquals("/opt/drill", config.getString(DFS_APP_DIR));
        Assert.assertEquals("My-App", config.getString(APP_NAME));
        // Commenting out for now, fails on VM.
        // assertEquals("sys-queue", config.getString(DrillOnYarnConfig.YARN_QUEUE));
        // Should also have access to Drill options.
        // Does not test Drill's override mechanism because have not found a good
        // way to add drill-override.conf to the class path in this test.
        // assertEquals( "org.apache.drill.exec.opt.IdentityOptimizer",
        // config.getString( "drill.exec.optimizer" ) );
        Assert.assertEquals("drillbits1", config.getString(CLUSTER_ID));
        // Drill home: with and without an env var.
        // Must set the site env var. Class path testing can't be done here.
        // No DRILL_HOME: will only occur during testing. In that case, we use
        // the setting from the config file. Explicit site dir.
        Assert.assertNull(doyConfig.mockEnv.get(DRILL_HOME_ENV_VAR));
        doyConfig.mockEnv.put(DRILL_SITE_ENV_VAR, "/drill/site");
        setClientPaths();
        Assert.assertEquals("/config/drill/home", getLocalDrillHome().getAbsolutePath());
        Assert.assertTrue(hasSiteDir());
        Assert.assertEquals("/drill/site", getLocalSiteDir().getAbsolutePath());
        // Home set in an env var
        doyConfig.mockEnv.put(DRILL_HOME_ENV_VAR, "/drill/home");
        setClientPaths();
        Assert.assertEquals("/drill/home", getLocalDrillHome().getAbsolutePath());
        // Remote site: localized case
        Assert.assertTrue(config.getBoolean(LOCALIZE_DRILL));
        Assert.assertEquals("/foo/bar/drill-archive.tar.gz", config.getString(DRILL_ARCHIVE_PATH));
        Assert.assertEquals("$PWD/drill/drill-archive", getRemoteDrillHome());
        Assert.assertEquals("site", config.getString(SITE_ARCHIVE_KEY));
        Assert.assertEquals("$PWD/site", getRemoteSiteDir());
        // Localized, but no separate site directory
        doyConfig.mockEnv.put(DRILL_SITE_ENV_VAR, "/drill/home/conf");
        setClientPaths();
        // If $DRILL_HOME/conf is used, we still treat id as a site dir.
        // assertFalse(doyConfig.hasSiteDir());
        // assertNull(doyConfig.getRemoteSiteDir());
        // Local app id file: composed from Drill home, ZK root and cluster id.
        // (Turns out that there can be two different clusters sharing the same
        // root...)
        // With no site dir, app id is in parent of the drill directory.
        Assert.assertEquals("/drill/home", getLocalDrillHome().getAbsolutePath());
        Assert.assertEquals("drill", config.getString(ZK_ROOT));
        Assert.assertEquals("drillbits1", config.getString(CLUSTER_ID));
        Assert.assertEquals("/drill/home/drill-drillbits1.appid", getLocalAppIdFile().getAbsolutePath());
        // Again, but with a site directory. App id is in parent of the site
        // directory.
        doyConfig.mockEnv.put(DRILL_SITE_ENV_VAR, "/var/drill/site");
        setClientPaths();
        Assert.assertEquals("/var/drill/drill-drillbits1.appid", getLocalAppIdFile().getAbsolutePath());
    }

    @Test
    public void testNonLocalized() throws IOException, DoyConfigException {
        TestConfig.DoYTestConfig doyConfig = initConfig("second-test-config.conf");
        // Test the non-localized case
        doyConfig.mockEnv.put(DRILL_SITE_ENV_VAR, "/drill/site");
        setClientPaths();
        Assert.assertEquals("/config/drill/home", getRemoteDrillHome());
        Assert.assertEquals("/config/drill/site", getRemoteSiteDir());
    }

    @Test
    public void testNonLocalizedNonSite() throws IOException, DoyConfigException {
        TestConfig.DoYTestConfig doyConfig = initConfig("third-test-config.conf");
        // Test the non-localized case
        Assert.assertEquals("/config/drill/home", getRemoteDrillHome());
        Assert.assertNull(getRemoteSiteDir());
    }
}

