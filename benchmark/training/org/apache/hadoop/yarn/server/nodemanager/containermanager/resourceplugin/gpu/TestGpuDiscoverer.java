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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu;


import YarnConfiguration.NM_GPU_PATH_TO_EXEC;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.nodemanager.webapp.dao.gpu.GpuDeviceInformation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestGpuDiscoverer {
    private static final Logger LOG = LoggerFactory.getLogger(TestGpuDiscoverer.class);

    private static final String PATH = "PATH";

    private static final String NVIDIA = "nvidia";

    private static final String EXEC_PERMISSION = "u+x";

    private static final String BASH_SHEBANG = "#!/bin/bash\n\n";

    private static final String TEST_PARENT_DIR = new File(("target/temp/" + (TestGpuDiscoverer.class.getName()))).getAbsolutePath();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testLinuxGpuResourceDiscoverPluginConfig() throws Exception {
        // Only run this on demand.
        Assume.assumeTrue(Boolean.valueOf(System.getProperty("RunLinuxGpuResourceDiscoverPluginConfigTest")));
        // test case 1, check default setting.
        Configuration conf = new Configuration(false);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        Assert.assertEquals(GpuDiscoverer.DEFAULT_BINARY_NAME, discoverer.getPathOfGpuBinary());
        assertNvidiaIsOnPath(discoverer);
        // test case 2, check mandatory set path.
        File fakeBinary = setupFakeBinary(conf);
        discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        Assert.assertEquals(fakeBinary.getAbsolutePath(), discoverer.getPathOfGpuBinary());
        Assert.assertNull(discoverer.getEnvironmentToRunCommand().get(TestGpuDiscoverer.PATH));
        // test case 3, check mandatory set path,
        // but binary doesn't exist so default path will be used.
        fakeBinary.delete();
        discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        Assert.assertEquals(GpuDiscoverer.DEFAULT_BINARY_NAME, discoverer.getPathOfGpuBinary());
        assertNvidiaIsOnPath(discoverer);
    }

    @Test
    public void testGetGpuDeviceInformationValidNvidiaSmiScript() throws IOException, YarnException {
        Configuration conf = new Configuration(false);
        File fakeBinary = createFakeNvidiaSmiScriptAsRunnableFile(this::createNvidiaSmiScript);
        GpuDiscoverer discoverer = creatediscovererWithGpuPathDefined(conf);
        Assert.assertEquals(fakeBinary.getAbsolutePath(), discoverer.getPathOfGpuBinary());
        Assert.assertNull(discoverer.getEnvironmentToRunCommand().get(TestGpuDiscoverer.PATH));
        GpuDeviceInformation result = discoverer.getGpuDeviceInformation();
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetGpuDeviceInformationFakeNvidiaSmiScriptConsecutiveRun() throws IOException, YarnException {
        Configuration conf = new Configuration(false);
        File fakeBinary = createFakeNvidiaSmiScriptAsRunnableFile(this::createNvidiaSmiScript);
        GpuDiscoverer discoverer = creatediscovererWithGpuPathDefined(conf);
        Assert.assertEquals(fakeBinary.getAbsolutePath(), discoverer.getPathOfGpuBinary());
        Assert.assertNull(discoverer.getEnvironmentToRunCommand().get(TestGpuDiscoverer.PATH));
        for (int i = 0; i < 5; i++) {
            GpuDeviceInformation result = discoverer.getGpuDeviceInformation();
            Assert.assertNotNull(result);
        }
    }

    @Test
    public void testGetGpuDeviceInformationFaultyNvidiaSmiScript() throws IOException, YarnException {
        Configuration conf = new Configuration(false);
        File fakeBinary = createFakeNvidiaSmiScriptAsRunnableFile(this::createFaultyNvidiaSmiScript);
        GpuDiscoverer discoverer = creatediscovererWithGpuPathDefined(conf);
        Assert.assertEquals(fakeBinary.getAbsolutePath(), discoverer.getPathOfGpuBinary());
        Assert.assertNull(discoverer.getEnvironmentToRunCommand().get(TestGpuDiscoverer.PATH));
        exception.expect(YarnException.class);
        exception.expectMessage("Failed to execute GPU device detection script");
        discoverer.getGpuDeviceInformation();
    }

    @Test
    public void testGetGpuDeviceInformationFaultyNvidiaSmiScriptConsecutiveRun() throws IOException, YarnException {
        Configuration conf = new Configuration(false);
        File fakeBinary = createFakeNvidiaSmiScriptAsRunnableFile(this::createNvidiaSmiScript);
        GpuDiscoverer discoverer = creatediscovererWithGpuPathDefined(conf);
        Assert.assertEquals(fakeBinary.getAbsolutePath(), discoverer.getPathOfGpuBinary());
        Assert.assertNull(discoverer.getEnvironmentToRunCommand().get(TestGpuDiscoverer.PATH));
        TestGpuDiscoverer.LOG.debug("Querying nvidia-smi correctly, once...");
        discoverer.getGpuDeviceInformation();
        TestGpuDiscoverer.LOG.debug("Replacing script with faulty version!");
        createFaultyNvidiaSmiScript(fakeBinary);
        final String terminateMsg = (("Failed to execute GPU device " + "detection script (") + (fakeBinary.getAbsolutePath())) + ") for 10 times";
        final String msg = "Failed to execute GPU device detection script";
        for (int i = 0; i < 10; i++) {
            try {
                TestGpuDiscoverer.LOG.debug("Executing faulty nvidia-smi script...");
                discoverer.getGpuDeviceInformation();
                Assert.fail((("Query of GPU device info via nvidia-smi should fail as " + "script should be faulty: ") + fakeBinary));
            } catch (YarnException e) {
                Assert.assertThat(e.getMessage(), CoreMatchers.containsString(msg));
                Assert.assertThat(e.getMessage(), CoreMatchers.not(CoreMatchers.containsString(terminateMsg)));
            }
        }
        try {
            TestGpuDiscoverer.LOG.debug(("Executing faulty nvidia-smi script again..." + "We should reach the error threshold now!"));
            discoverer.getGpuDeviceInformation();
            Assert.fail((("Query of GPU device info via nvidia-smi should fail as " + "script should be faulty: ") + fakeBinary));
        } catch (YarnException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString(terminateMsg));
        }
        TestGpuDiscoverer.LOG.debug(("Verifying if GPUs are still hold the value of " + "first successful query"));
        Assert.assertNotNull(discoverer.getGpusUsableByYarn());
    }

    @Test
    public void testGetGpuDeviceInformationNvidiaSmiScriptWithInvalidXml() throws IOException, YarnException {
        Configuration conf = new Configuration(false);
        File fakeBinary = createFakeNvidiaSmiScriptAsRunnableFile(this::createNvidiaSmiScriptWithInvalidXml);
        GpuDiscoverer discoverer = creatediscovererWithGpuPathDefined(conf);
        Assert.assertEquals(fakeBinary.getAbsolutePath(), discoverer.getPathOfGpuBinary());
        Assert.assertNull(discoverer.getEnvironmentToRunCommand().get(TestGpuDiscoverer.PATH));
        exception.expect(YarnException.class);
        exception.expectMessage(("Failed to parse XML output of " + "GPU device detection script"));
        discoverer.getGpuDeviceInformation();
    }

    @Test
    public void testGpuDiscover() throws YarnException {
        // Since this is more of a performance unit test, only run if
        // RunUserLimitThroughput is set (-DRunUserLimitThroughput=true)
        Assume.assumeTrue(Boolean.valueOf(System.getProperty("runGpuDiscoverUnitTest")));
        Configuration conf = new Configuration(false);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        GpuDeviceInformation info = discoverer.getGpuDeviceInformation();
        Assert.assertTrue(((info.getGpus().size()) > 0));
        Assert.assertEquals(discoverer.getGpusUsableByYarn().size(), info.getGpus().size());
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigSingleDevice() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("1:2");
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        List<GpuDevice> usableGpuDevices = discoverer.getGpusUsableByYarn();
        Assert.assertEquals(1, usableGpuDevices.size());
        Assert.assertEquals(1, usableGpuDevices.get(0).getIndex());
        Assert.assertEquals(2, usableGpuDevices.get(0).getMinorNumber());
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigIllegalFormat() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("0:0,1:1,2:2,3");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfig() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("0:0,1:1,2:2,3:4");
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        List<GpuDevice> usableGpuDevices = discoverer.getGpusUsableByYarn();
        Assert.assertEquals(4, usableGpuDevices.size());
        Assert.assertEquals(0, usableGpuDevices.get(0).getIndex());
        Assert.assertEquals(0, usableGpuDevices.get(0).getMinorNumber());
        Assert.assertEquals(1, usableGpuDevices.get(1).getIndex());
        Assert.assertEquals(1, usableGpuDevices.get(1).getMinorNumber());
        Assert.assertEquals(2, usableGpuDevices.get(2).getIndex());
        Assert.assertEquals(2, usableGpuDevices.get(2).getMinorNumber());
        Assert.assertEquals(3, usableGpuDevices.get(3).getIndex());
        Assert.assertEquals(4, usableGpuDevices.get(3).getMinorNumber());
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigDuplicateValues() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("0:0,1:1,2:2,1:1");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigDuplicateValues2() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("0:0,1:1,2:2,1:1,2:2");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigIncludingSpaces() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("0 : 0,1 : 1");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigIncludingGibberish() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("0:@$1,1:1");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigIncludingLetters() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("x:0, 1:y");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigWithoutIndexNumber() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices(":0, :1");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigEmptyString() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigValueWithoutComma() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("0:0 0:1");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigValueWithoutComma2() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("0.1 0.2");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGetNumberOfUsableGpusFromConfigValueWithoutColonSeparator() throws YarnException {
        Configuration conf = createConfigWithAllowedDevices("0.1,0.2");
        exception.expect(GpuDeviceSpecificationException.class);
        GpuDiscoverer discoverer = new GpuDiscoverer();
        discoverer.initialize(conf);
        discoverer.getGpusUsableByYarn();
    }

    @Test
    public void testGpuBinaryIsANotExistingFile() {
        Configuration conf = new Configuration(false);
        conf.set(NM_GPU_PATH_TO_EXEC, "/blabla");
        GpuDiscoverer plugin = new GpuDiscoverer();
        try {
            plugin.initialize(conf);
            plugin.getGpusUsableByYarn();
            Assert.fail("Illegal format, should fail.");
        } catch (YarnException e) {
            String message = e.getMessage();
            Assert.assertTrue(message.startsWith(("Failed to find GPU discovery " + "executable, please double check")));
            Assert.assertTrue(message.contains(("Also tried to find the " + "executable in the default directories:")));
        }
    }
}

