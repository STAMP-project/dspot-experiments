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
package org.apache.hadoop.yarn.service.client;


import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.yarn.client.cli.ApplicationCLI;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.service.conf.ExampleAppJson;
import org.apache.hadoop.yarn.service.utils.SliderFileSystem;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestServiceCLI {
    private static final Logger LOG = LoggerFactory.getLogger(TestServiceCLI.class);

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private Configuration conf = new YarnConfiguration();

    private SliderFileSystem fs;

    private ApplicationCLI cli;

    private File basedir;

    private String basedirProp;

    private File dependencyTarGzBaseDir;

    private Path dependencyTarGz;

    private String dependencyTarGzProp;

    private String yarnAdminNoneAclProp;

    private String dfsAdminAclProp;

    @Test(timeout = 180000)
    public void testFlexComponents() throws Throwable {
        // currently can only test building apps, since that is the only
        // operation that doesn't require an RM
        // TODO: expand CLI test to try other commands
        String serviceName = "app-1";
        buildApp(serviceName, ExampleAppJson.APP_JSON);
        checkApp(serviceName, "master", 1L, 3600L, null);
        serviceName = "app-2";
        buildApp(serviceName, ExampleAppJson.APP_JSON, "1000", "qname");
        checkApp(serviceName, "master", 1L, 1000L, "qname");
    }

    @Test
    public void testInitiateServiceUpgrade() throws Exception {
        String[] args = new String[]{ "app", "-upgrade", "app-1", "-initiate", ExampleAppJson.resourceName(ExampleAppJson.APP_JSON), "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        int result = cli.run(ApplicationCLI.preProcessArgs(args));
        Assert.assertEquals(result, 0);
    }

    @Test(timeout = 180000)
    public void testInitiateAutoFinalizeServiceUpgrade() throws Exception {
        String[] args = new String[]{ "app", "-upgrade", "app-1", "-initiate", ExampleAppJson.resourceName(ExampleAppJson.APP_JSON), "-autoFinalize", "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        int result = cli.run(ApplicationCLI.preProcessArgs(args));
        Assert.assertEquals(result, 0);
    }

    @Test
    public void testUpgradeInstances() throws Exception {
        conf.set(((YARN_APP_ADMIN_CLIENT_PREFIX) + (TestServiceCLI.DUMMY_APP_TYPE)), TestServiceCLI.DummyServiceClient.class.getName());
        cli.setConf(conf);
        String[] args = new String[]{ "app", "-upgrade", "app-1", "-instances", "comp1-0,comp1-1", "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        int result = cli.run(ApplicationCLI.preProcessArgs(args));
        Assert.assertEquals(result, 0);
    }

    @Test
    public void testUpgradeComponents() throws Exception {
        conf.set(((YARN_APP_ADMIN_CLIENT_PREFIX) + (TestServiceCLI.DUMMY_APP_TYPE)), TestServiceCLI.DummyServiceClient.class.getName());
        cli.setConf(conf);
        String[] args = new String[]{ "app", "-upgrade", "app-1", "-components", "comp1,comp2", "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        int result = cli.run(ApplicationCLI.preProcessArgs(args));
        Assert.assertEquals(result, 0);
    }

    @Test
    public void testGetInstances() throws Exception {
        conf.set(((YARN_APP_ADMIN_CLIENT_PREFIX) + (TestServiceCLI.DUMMY_APP_TYPE)), TestServiceCLI.DummyServiceClient.class.getName());
        cli.setConf(conf);
        String[] args = new String[]{ "container", "-list", "app-1", "-components", "comp1,comp2", "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        int result = cli.run(ApplicationCLI.preProcessArgs(args));
        Assert.assertEquals(result, 0);
    }

    @Test
    public void testCancelUpgrade() throws Exception {
        conf.set(((YARN_APP_ADMIN_CLIENT_PREFIX) + (TestServiceCLI.DUMMY_APP_TYPE)), TestServiceCLI.DummyServiceClient.class.getName());
        cli.setConf(conf);
        String[] args = new String[]{ "app", "-upgrade", "app-1", "-cancel", "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        int result = cli.run(ApplicationCLI.preProcessArgs(args));
        Assert.assertEquals(result, 0);
    }

    @Test(timeout = 180000)
    public void testEnableFastLaunch() throws Exception {
        fs.getFileSystem().create(new Path(basedir.getAbsolutePath(), "test.jar")).close();
        Path defaultPath = new Path(dependencyTarGz.toString());
        Assert.assertFalse("Dependency tarball should not exist before the test", fs.isFile(defaultPath));
        String[] args = new String[]{ "app", "-D", dependencyTarGzProp, "-enableFastLaunch", "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        Assert.assertEquals(EXIT_SUCCESS, runCLI(args));
        Assert.assertTrue("Dependency tarball did not exist after the test", fs.isFile(defaultPath));
        File secondBaseDir = new File(dependencyTarGzBaseDir, "2");
        Path secondTarGz = TestServiceCLI.getDependencyTarGz(secondBaseDir);
        Assert.assertFalse("Dependency tarball should not exist before the test", fs.isFile(secondTarGz));
        String[] args2 = new String[]{ "app", "-D", yarnAdminNoneAclProp, "-D", dfsAdminAclProp, "-D", dependencyTarGzProp, "-enableFastLaunch", secondBaseDir.getAbsolutePath(), "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        Assert.assertEquals(EXIT_SUCCESS, runCLI(args2));
        Assert.assertTrue("Dependency tarball did not exist after the test", fs.isFile(secondTarGz));
    }

    @Test(timeout = 180000)
    public void testEnableFastLaunchUserPermissions() throws Exception {
        String[] args = new String[]{ "app", "-D", yarnAdminNoneAclProp, "-D", dependencyTarGzProp, "-enableFastLaunch", "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        Assert.assertEquals(EXIT_UNAUTHORIZED, runCLI(args));
    }

    @Test(timeout = 180000)
    public void testEnableFastLaunchFilePermissions() throws Exception {
        File badDir = new File(dependencyTarGzBaseDir, "bad");
        badDir.mkdir();
        fs.getFileSystem().setPermission(new Path(badDir.getAbsolutePath()), new FsPermission("751"));
        String[] args = new String[]{ "app", "-D", dependencyTarGzProp, "-enableFastLaunch", badDir.getAbsolutePath(), "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        Assert.assertEquals(EXIT_UNAUTHORIZED, runCLI(args));
        badDir = new File(badDir, "child");
        badDir.mkdir();
        fs.getFileSystem().setPermission(new Path(badDir.getAbsolutePath()), new FsPermission("755"));
        String[] args2 = new String[]{ "app", "-D", dependencyTarGzProp, "-enableFastLaunch", badDir.getAbsolutePath(), "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        Assert.assertEquals(EXIT_UNAUTHORIZED, runCLI(args2));
        badDir = new File(dependencyTarGzBaseDir, "badx");
        badDir.mkdir();
        fs.getFileSystem().setPermission(new Path(badDir.getAbsolutePath()), new FsPermission("754"));
        String[] args3 = new String[]{ "app", "-D", dependencyTarGzProp, "-enableFastLaunch", badDir.getAbsolutePath(), "-appTypes", TestServiceCLI.DUMMY_APP_TYPE };
        Assert.assertEquals(EXIT_UNAUTHORIZED, runCLI(args3));
    }

    private static final String DUMMY_APP_TYPE = "dummy";

    /**
     * Dummy service client for test purpose.
     */
    public static class DummyServiceClient extends ServiceClient {
        @Override
        public int initiateUpgrade(String appName, String fileName, boolean autoFinalize) throws IOException, YarnException {
            return 0;
        }

        @Override
        public int actionUpgradeInstances(String appName, List<String> componentInstances) throws IOException, YarnException {
            return 0;
        }

        @Override
        public int actionUpgradeComponents(String appName, List<String> components) throws IOException, YarnException {
            return 0;
        }

        @Override
        public String getInstances(String appName, List<String> components, String version, List<String> containerStates) throws IOException, YarnException {
            return "";
        }

        @Override
        public int actionCancelUpgrade(String appName) throws IOException, YarnException {
            return 0;
        }
    }
}

