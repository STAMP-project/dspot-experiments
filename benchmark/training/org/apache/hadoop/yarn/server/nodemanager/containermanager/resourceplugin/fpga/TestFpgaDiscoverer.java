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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.fpga;


import YarnConfiguration.NM_FPGA_AVAILABLE_DEVICES;
import YarnConfiguration.NM_FPGA_DEVICE_DISCOVERY_SCRIPT;
import YarnConfiguration.NM_FPGA_PATH_TO_EXEC;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.ResourceHandlerException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.fpga.FpgaResourceAllocator;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.fpga.FpgaResourceAllocator.FpgaDevice;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestFpgaDiscoverer {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testLinuxFpgaResourceDiscoverPluginConfig() throws Exception {
        Configuration conf = new Configuration(false);
        FpgaDiscoverer discoverer = FpgaDiscoverer.getInstance();
        IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
        // because FPGA discoverer is a singleton, we use setPlugin to make
        // FpgaDiscoverer.getInstance().diagnose() work in openclPlugin.initPlugin()
        discoverer.setResourceHanderPlugin(openclPlugin);
        openclPlugin.initPlugin(conf);
        openclPlugin.setShell(mockPuginShell());
        discoverer.initialize(conf);
        // Case 1. No configuration set for binary(no environment "ALTERAOCLSDKROOT" set)
        Assert.assertEquals(("No configuration(no environment ALTERAOCLSDKROOT set)" + "should return just a single binary name"), "aocl", openclPlugin.getPathToExecutable());
        // Case 2. With correct configuration and file exists
        File fakeBinary = new File(((getTestParentFolder()) + "/aocl"));
        conf.set(NM_FPGA_PATH_TO_EXEC, ((getTestParentFolder()) + "/aocl"));
        touchFile(fakeBinary);
        discoverer.initialize(conf);
        Assert.assertEquals("Correct configuration should return user setting", ((getTestParentFolder()) + "/aocl"), openclPlugin.getPathToExecutable());
        // Case 3. With correct configuration but file doesn't exists. Use default
        fakeBinary.delete();
        discoverer.initialize(conf);
        Assert.assertEquals("Should return just a single binary name", "aocl", openclPlugin.getPathToExecutable());
        // Case 4. Set a empty value
        conf.set(NM_FPGA_PATH_TO_EXEC, "");
        discoverer.initialize(conf);
        Assert.assertEquals("configuration with empty string value, should use aocl", "aocl", openclPlugin.getPathToExecutable());
        // Case 5. No configuration set for binary, but set environment "ALTERAOCLSDKROOT"
        // we load the default configuration to start with
        conf = new Configuration(true);
        fakeBinary = new File(((getTestParentFolder()) + "/bin/aocl"));
        fakeBinary.getParentFile().mkdirs();
        touchFile(fakeBinary);
        Map<String, String> newEnv = new HashMap<String, String>();
        newEnv.put("ALTERAOCLSDKROOT", getTestParentFolder());
        TestFpgaDiscoverer.setNewEnvironmentHack(newEnv);
        discoverer.initialize(conf);
        Assert.assertEquals("No configuration but with environment ALTERAOCLSDKROOT set", ((getTestParentFolder()) + "/bin/aocl"), openclPlugin.getPathToExecutable());
    }

    @Test
    public void testDiscoverPluginParser() throws YarnException {
        String output = "------------------------- acl0 -------------------------\n" + (((((((("Vendor: Nallatech ltd\n" + "Phys Dev Name  Status   Information\n") + "aclnalla_pcie0Passed   nalla_pcie (aclnalla_pcie0)\n") + "                       PCIe dev_id = 2494, bus:slot.func = 02:00.00, Gen3 x8\n") + "                       FPGA temperature = 53.1 degrees C.\n") + "                       Total Card Power Usage = 31.7 Watts.\n") + "                       Device Power Usage = 0.0 Watts.\n") + "DIAGNOSTIC_PASSED") + "---------------------------------------------------------\n");
        output = (((((((((output + "------------------------- acl1 -------------------------\n") + "Vendor: Nallatech ltd\n") + "Phys Dev Name  Status   Information\n") + "aclnalla_pcie1Passed   nalla_pcie (aclnalla_pcie1)\n") + "                       PCIe dev_id = 2495, bus:slot.func = 03:00.00, Gen3 x8\n") + "                       FPGA temperature = 43.1 degrees C.\n") + "                       Total Card Power Usage = 11.7 Watts.\n") + "                       Device Power Usage = 0.0 Watts.\n") + "DIAGNOSTIC_PASSED") + "---------------------------------------------------------\n";
        output = ((((((((((output + "------------------------- acl2 -------------------------\n") + "Vendor: Intel(R) Corporation\n") + "\n") + "Phys Dev Name  Status   Information\n") + "\n") + "acla10_ref0   Passed   Arria 10 Reference Platform (acla10_ref0)\n") + "                       PCIe dev_id = 2494, bus:slot.func = 09:00.00, Gen2 x8\n") + "                       FPGA temperature = 50.5781 degrees C.\n") + "\n") + "DIAGNOSTIC_PASSED\n") + "---------------------------------------------------------\n";
        Configuration conf = new Configuration(false);
        IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
        FpgaDiscoverer.getInstance().setResourceHanderPlugin(openclPlugin);
        openclPlugin.initPlugin(conf);
        openclPlugin.setShell(mockPuginShell());
        FpgaDiscoverer.getInstance().initialize(conf);
        List<FpgaResourceAllocator.FpgaDevice> list = new LinkedList<>();
        // Case 1. core parsing
        openclPlugin.parseDiagnoseInfo(output, list);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("IntelOpenCL", list.get(0).getType());
        Assert.assertEquals("247", list.get(0).getMajor().toString());
        Assert.assertEquals("0", list.get(0).getMinor().toString());
        Assert.assertEquals("acl0", list.get(0).getAliasDevName());
        Assert.assertEquals("aclnalla_pcie0", list.get(0).getDevName());
        Assert.assertEquals("02:00.00", list.get(0).getBusNum());
        Assert.assertEquals("53.1 degrees C", list.get(0).getTemperature());
        Assert.assertEquals("31.7 Watts", list.get(0).getCardPowerUsage());
        Assert.assertEquals("IntelOpenCL", list.get(1).getType());
        Assert.assertEquals("247", list.get(1).getMajor().toString());
        Assert.assertEquals("1", list.get(1).getMinor().toString());
        Assert.assertEquals("acl1", list.get(1).getAliasDevName());
        Assert.assertEquals("aclnalla_pcie1", list.get(1).getDevName());
        Assert.assertEquals("03:00.00", list.get(1).getBusNum());
        Assert.assertEquals("43.1 degrees C", list.get(1).getTemperature());
        Assert.assertEquals("11.7 Watts", list.get(1).getCardPowerUsage());
        Assert.assertEquals("IntelOpenCL", list.get(2).getType());
        Assert.assertEquals("246", list.get(2).getMajor().toString());
        Assert.assertEquals("0", list.get(2).getMinor().toString());
        Assert.assertEquals("acl2", list.get(2).getAliasDevName());
        Assert.assertEquals("acla10_ref0", list.get(2).getDevName());
        Assert.assertEquals("09:00.00", list.get(2).getBusNum());
        Assert.assertEquals("50.5781 degrees C", list.get(2).getTemperature());
        Assert.assertEquals("", list.get(2).getCardPowerUsage());
        // Case 2. check alias map
        Map<String, String> aliasMap = openclPlugin.getAliasMap();
        Assert.assertEquals("acl0", aliasMap.get("247:0"));
        Assert.assertEquals("acl1", aliasMap.get("247:1"));
        Assert.assertEquals("acl2", aliasMap.get("246:0"));
    }

    @Test
    public void testDiscoveryWhenAvailableDevicesDefined() throws YarnException {
        Configuration conf = new Configuration(false);
        conf.set(NM_FPGA_AVAILABLE_DEVICES, "acl0/243:0,acl1/244:1");
        FpgaDiscoverer discoverer = FpgaDiscoverer.getInstance();
        IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
        discoverer.setResourceHanderPlugin(openclPlugin);
        openclPlugin.initPlugin(conf);
        openclPlugin.setShell(mockPuginShell());
        discoverer.initialize(conf);
        List<FpgaDevice> devices = discoverer.discover();
        Assert.assertEquals("Number of devices", 2, devices.size());
        FpgaDevice device0 = devices.get(0);
        FpgaDevice device1 = devices.get(1);
        Assert.assertEquals("Device id", "acl0", device0.getAliasDevName());
        Assert.assertEquals("Minor number", new Integer(0), device0.getMinor());
        Assert.assertEquals("Major", new Integer(243), device0.getMajor());
        Assert.assertEquals("Device id", "acl1", device1.getAliasDevName());
        Assert.assertEquals("Minor number", new Integer(1), device1.getMinor());
        Assert.assertEquals("Major", new Integer(244), device1.getMajor());
    }

    @Test
    public void testDiscoveryWhenAvailableDevicesEmpty() throws YarnException {
        expected.expect(ResourceHandlerException.class);
        expected.expectMessage("No FPGA devices were specified");
        Configuration conf = new Configuration(false);
        conf.set(NM_FPGA_AVAILABLE_DEVICES, "");
        FpgaDiscoverer discoverer = FpgaDiscoverer.getInstance();
        IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
        discoverer.setResourceHanderPlugin(openclPlugin);
        openclPlugin.initPlugin(conf);
        openclPlugin.setShell(mockPuginShell());
        discoverer.initialize(conf);
        discoverer.discover();
    }

    @Test
    public void testDiscoveryWhenAvailableDevicesAreIllegalString() throws YarnException {
        expected.expect(ResourceHandlerException.class);
        expected.expectMessage("Illegal device specification string");
        Configuration conf = new Configuration(false);
        conf.set(NM_FPGA_AVAILABLE_DEVICES, "illegal/243:0,acl1/244=1");
        FpgaDiscoverer discoverer = FpgaDiscoverer.getInstance();
        IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
        discoverer.setResourceHanderPlugin(openclPlugin);
        openclPlugin.initPlugin(conf);
        openclPlugin.setShell(mockPuginShell());
        discoverer.initialize(conf);
        discoverer.discover();
    }

    @Test
    public void testDiscoveryWhenExternalScriptDefined() throws YarnException {
        Configuration conf = new Configuration(false);
        conf.set(NM_FPGA_DEVICE_DISCOVERY_SCRIPT, "/dummy/script");
        FpgaDiscoverer discoverer = FpgaDiscoverer.getInstance();
        IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
        discoverer.setResourceHanderPlugin(openclPlugin);
        openclPlugin.initPlugin(conf);
        openclPlugin.setShell(mockPuginShell());
        discoverer.setScriptRunner(( s) -> {
            return Optional.of("acl0/243:0,acl1/244:1");
        });
        discoverer.initialize(conf);
        List<FpgaDevice> devices = discoverer.discover();
        Assert.assertEquals("Number of devices", 2, devices.size());
        FpgaDevice device0 = devices.get(0);
        FpgaDevice device1 = devices.get(1);
        Assert.assertEquals("Device id", "acl0", device0.getAliasDevName());
        Assert.assertEquals("Minor number", new Integer(0), device0.getMinor());
        Assert.assertEquals("Major", new Integer(243), device0.getMajor());
        Assert.assertEquals("Device id", "acl1", device1.getAliasDevName());
        Assert.assertEquals("Minor number", new Integer(1), device1.getMinor());
        Assert.assertEquals("Major", new Integer(244), device1.getMajor());
    }

    @Test
    public void testDiscoveryWhenExternalScriptReturnsEmptyString() throws YarnException {
        expected.expect(ResourceHandlerException.class);
        expected.expectMessage("No FPGA devices were specified");
        Configuration conf = new Configuration(false);
        conf.set(NM_FPGA_DEVICE_DISCOVERY_SCRIPT, "/dummy/script");
        FpgaDiscoverer discoverer = FpgaDiscoverer.getInstance();
        IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
        discoverer.setResourceHanderPlugin(openclPlugin);
        openclPlugin.initPlugin(conf);
        openclPlugin.setShell(mockPuginShell());
        discoverer.setScriptRunner(( s) -> {
            return Optional.of("");
        });
        discoverer.initialize(conf);
        discoverer.discover();
    }

    @Test
    public void testDiscoveryWhenExternalScriptFails() throws YarnException {
        expected.expect(ResourceHandlerException.class);
        expected.expectMessage("Unable to run external script");
        Configuration conf = new Configuration(false);
        conf.set(NM_FPGA_DEVICE_DISCOVERY_SCRIPT, "/dummy/script");
        FpgaDiscoverer discoverer = FpgaDiscoverer.getInstance();
        IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
        discoverer.setResourceHanderPlugin(openclPlugin);
        openclPlugin.initPlugin(conf);
        openclPlugin.setShell(mockPuginShell());
        discoverer.setScriptRunner(( s) -> {
            return Optional.empty();
        });
        discoverer.initialize(conf);
        discoverer.discover();
    }

    @Test
    public void testDiscoveryWhenExternalScriptUndefined() throws YarnException {
        expected.expect(ResourceHandlerException.class);
        expected.expectMessage("Unable to run external script");
        Configuration conf = new Configuration(false);
        conf.set(NM_FPGA_DEVICE_DISCOVERY_SCRIPT, "");
        FpgaDiscoverer discoverer = FpgaDiscoverer.getInstance();
        IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
        discoverer.setResourceHanderPlugin(openclPlugin);
        openclPlugin.initPlugin(conf);
        openclPlugin.setShell(mockPuginShell());
        discoverer.initialize(conf);
        discoverer.discover();
    }

    @Test
    public void testDiscoveryWhenExternalScriptCannotBeExecuted() throws IOException, YarnException {
        File fakeScript = new File(((getTestParentFolder()) + "/fakeScript"));
        try {
            expected.expect(ResourceHandlerException.class);
            expected.expectMessage("Unable to run external script");
            Configuration conf = new Configuration(false);
            fakeScript = new File(((getTestParentFolder()) + "/fakeScript"));
            touchFile(fakeScript);
            fakeScript.setExecutable(false);
            conf.set(NM_FPGA_DEVICE_DISCOVERY_SCRIPT, fakeScript.getAbsolutePath());
            FpgaDiscoverer discoverer = FpgaDiscoverer.getInstance();
            IntelFpgaOpenclPlugin openclPlugin = new IntelFpgaOpenclPlugin();
            discoverer.setResourceHanderPlugin(openclPlugin);
            openclPlugin.initPlugin(conf);
            openclPlugin.setShell(mockPuginShell());
            discoverer.initialize(conf);
            discoverer.discover();
        } finally {
            fakeScript.delete();
        }
    }
}

