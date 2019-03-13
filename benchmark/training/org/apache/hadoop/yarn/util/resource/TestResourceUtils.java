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
package org.apache.hadoop.yarn.util.resource;


import ResourceInformation.GPU_URI;
import ResourceTypes.COUNTABLE;
import YarnConfiguration.RESOURCE_TYPES;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.api.records.ResourceTypeInfo;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class to verify all resource utility methods.
 */
public class TestResourceUtils {
    private File nodeResourcesFile;

    private File resourceTypesFile;

    static class ResourceFileInformation {
        String filename;

        int resourceCount;

        Map<String, String> resourceNameUnitsMap;

        public ResourceFileInformation(String name, int count) {
            filename = name;
            resourceCount = count;
            resourceNameUnitsMap = new HashMap<>();
        }
    }

    @Test
    public void testGetResourceTypes() throws Exception {
        Map<String, ResourceInformation> res = ResourceUtils.getResourceTypes();
        Assert.assertEquals(2, res.size());
        testMemoryAndVcores(res);
    }

    @Test
    public void testGetResourceTypesConfigs() throws Exception {
        Configuration conf = new YarnConfiguration();
        TestResourceUtils.ResourceFileInformation testFile1 = new TestResourceUtils.ResourceFileInformation("resource-types-1.xml", 2);
        TestResourceUtils.ResourceFileInformation testFile2 = new TestResourceUtils.ResourceFileInformation("resource-types-2.xml", 3);
        testFile2.resourceNameUnitsMap.put("resource1", "G");
        TestResourceUtils.ResourceFileInformation testFile3 = new TestResourceUtils.ResourceFileInformation("resource-types-3.xml", 3);
        testFile3.resourceNameUnitsMap.put("resource2", "");
        TestResourceUtils.ResourceFileInformation testFile4 = new TestResourceUtils.ResourceFileInformation("resource-types-4.xml", 5);
        testFile4.resourceNameUnitsMap.put("resource1", "G");
        testFile4.resourceNameUnitsMap.put("resource2", "m");
        testFile4.resourceNameUnitsMap.put("yarn.io/gpu", "");
        TestResourceUtils.ResourceFileInformation[] tests = new TestResourceUtils.ResourceFileInformation[]{ testFile1, testFile2, testFile3, testFile4 };
        Map<String, ResourceInformation> res;
        for (TestResourceUtils.ResourceFileInformation testInformation : tests) {
            ResourceUtils.resetResourceTypes();
            File source = new File(conf.getClassLoader().getResource(testInformation.filename).getFile());
            resourceTypesFile = new File(source.getParent(), "resource-types.xml");
            FileUtils.copyFile(source, resourceTypesFile);
            res = ResourceUtils.getResourceTypes();
            testMemoryAndVcores(res);
            Assert.assertEquals(testInformation.resourceCount, res.size());
            for (Map.Entry<String, String> entry : testInformation.resourceNameUnitsMap.entrySet()) {
                String resourceName = entry.getKey();
                Assert.assertTrue(("Missing key " + resourceName), res.containsKey(resourceName));
                Assert.assertEquals(entry.getValue(), res.get(resourceName).getUnits());
            }
        }
    }

    @Test
    public void testGetResourceTypesConfigErrors() throws Exception {
        Configuration conf = new YarnConfiguration();
        String[] resourceFiles = new String[]{ "resource-types-error-1.xml", "resource-types-error-2.xml", "resource-types-error-3.xml", "resource-types-error-4.xml" };
        for (String resourceFile : resourceFiles) {
            ResourceUtils.resetResourceTypes();
            try {
                File source = new File(conf.getClassLoader().getResource(resourceFile).getFile());
                resourceTypesFile = new File(source.getParent(), "resource-types.xml");
                FileUtils.copyFile(source, resourceTypesFile);
                ResourceUtils.getResourceTypes();
                Assert.fail(("Expected error with file " + resourceFile));
            } catch (NullPointerException ne) {
                throw ne;
            } catch (Exception e) {
                // Test passed
            }
        }
    }

    @Test
    public void testInitializeResourcesMap() throws Exception {
        String[] empty = new String[]{ "", "" };
        String[] res1 = new String[]{ "resource1", "m" };
        String[] res2 = new String[]{ "resource2", "G" };
        String[][] test1 = new String[][]{ empty };
        String[][] test2 = new String[][]{ res1 };
        String[][] test3 = new String[][]{ res2 };
        String[][] test4 = new String[][]{ res1, res2 };
        String[][][] allTests = new String[][][]{ test1, test2, test3, test4 };
        for (String[][] test : allTests) {
            Configuration conf = new YarnConfiguration();
            String resSt = "";
            for (String[] resources : test) {
                resSt += (resources[0]) + ",";
            }
            resSt = resSt.substring(0, ((resSt.length()) - 1));
            conf.set(RESOURCE_TYPES, resSt);
            for (String[] resources : test) {
                String name = (((YarnConfiguration.RESOURCE_TYPES) + ".") + (resources[0])) + ".units";
                conf.set(name, resources[1]);
            }
            Map<String, ResourceInformation> ret = ResourceUtils.resetResourceTypes(conf);
            // for test1, 4 - length will be 1, 4
            // for the others, len will be 3
            int len = 3;
            if (test == test1) {
                len = 2;
            } else
                if (test == test4) {
                    len = 4;
                }

            Assert.assertEquals(len, ret.size());
            for (String[] resources : test) {
                if ((resources[0].length()) == 0) {
                    continue;
                }
                Assert.assertTrue(ret.containsKey(resources[0]));
                ResourceInformation resInfo = ret.get(resources[0]);
                Assert.assertEquals(resources[1], resInfo.getUnits());
                Assert.assertEquals(COUNTABLE, resInfo.getResourceType());
            }
            // we must always have memory and vcores with their fixed units
            Assert.assertTrue(ret.containsKey("memory-mb"));
            ResourceInformation memInfo = ret.get("memory-mb");
            Assert.assertEquals("Mi", memInfo.getUnits());
            Assert.assertEquals(COUNTABLE, memInfo.getResourceType());
            Assert.assertTrue(ret.containsKey("vcores"));
            ResourceInformation vcoresInfo = ret.get("vcores");
            Assert.assertEquals("", vcoresInfo.getUnits());
            Assert.assertEquals(COUNTABLE, vcoresInfo.getResourceType());
        }
    }

    @Test
    public void testInitializeResourcesMapErrors() throws Exception {
        String[] mem1 = new String[]{ "memory-mb", "" };
        String[] vcores1 = new String[]{ "vcores", "M" };
        String[] mem2 = new String[]{ "memory-mb", "m" };
        String[] vcores2 = new String[]{ "vcores", "G" };
        String[] mem3 = new String[]{ "memory", "" };
        String[][] test1 = new String[][]{ mem1, vcores1 };
        String[][] test2 = new String[][]{ mem2, vcores2 };
        String[][] test3 = new String[][]{ mem3 };
        String[][][] allTests = new String[][][]{ test1, test2, test3 };
        for (String[][] test : allTests) {
            Configuration conf = new YarnConfiguration();
            String resSt = "";
            for (String[] resources : test) {
                resSt += (resources[0]) + ",";
            }
            resSt = resSt.substring(0, ((resSt.length()) - 1));
            conf.set(RESOURCE_TYPES, resSt);
            for (String[] resources : test) {
                String name = (((YarnConfiguration.RESOURCE_TYPES) + ".") + (resources[0])) + ".units";
                conf.set(name, resources[1]);
            }
            try {
                ResourceUtils.initializeResourcesMap(conf);
                Assert.fail("resource map initialization should fail");
            } catch (Exception e) {
                // Test passed
            }
        }
    }

    @Test
    public void testGetResourceInformation() throws Exception {
        Configuration conf = new YarnConfiguration();
        Map<String, Resource> testRun = new HashMap<>();
        TestResourceUtils.setupResourceTypes(conf, "resource-types-4.xml");
        // testRun.put("node-resources-1.xml", Resource.newInstance(1024, 1));
        Resource test3Resources = Resource.newInstance(0, 0);
        test3Resources.setResourceInformation("resource1", ResourceInformation.newInstance("resource1", "Gi", 5L));
        test3Resources.setResourceInformation("resource2", ResourceInformation.newInstance("resource2", "m", 2L));
        test3Resources.setResourceInformation("yarn.io/gpu", ResourceInformation.newInstance("yarn.io/gpu", "", 1));
        testRun.put("node-resources-2.xml", test3Resources);
        for (Map.Entry<String, Resource> entry : testRun.entrySet()) {
            String resourceFile = entry.getKey();
            ResourceUtils.resetNodeResources();
            File source = new File(conf.getClassLoader().getResource(resourceFile).getFile());
            nodeResourcesFile = new File(source.getParent(), "node-resources.xml");
            FileUtils.copyFile(source, nodeResourcesFile);
            Map<String, ResourceInformation> actual = ResourceUtils.getNodeResourceInformation(conf);
            Assert.assertEquals(actual.size(), entry.getValue().getResources().length);
            for (ResourceInformation resInfo : entry.getValue().getResources()) {
                Assert.assertEquals(resInfo, actual.get(resInfo.getName()));
            }
        }
    }

    @Test
    public void testGetNodeResourcesConfigErrors() throws Exception {
        Configuration conf = new YarnConfiguration();
        Map<String, Resource> testRun = new HashMap<>();
        TestResourceUtils.setupResourceTypes(conf, "resource-types-4.xml");
        String[] invalidNodeResFiles = new String[]{ "node-resources-error-1.xml" };
        for (String resourceFile : invalidNodeResFiles) {
            ResourceUtils.resetNodeResources();
            try {
                File source = new File(conf.getClassLoader().getResource(resourceFile).getFile());
                nodeResourcesFile = new File(source.getParent(), "node-resources.xml");
                FileUtils.copyFile(source, nodeResourcesFile);
                Map<String, ResourceInformation> actual = ResourceUtils.getNodeResourceInformation(conf);
                Assert.fail(("Expected error with file " + resourceFile));
            } catch (NullPointerException ne) {
                throw ne;
            } catch (Exception e) {
                // Test passed
            }
        }
    }

    @Test
    public void testResourceNameFormatValidation() throws Exception {
        String[] validNames = new String[]{ "yarn.io/gpu", "gpu", "g_1_2", "123.io/gpu", "prefix/resource_1", "a___-3", "a....b" };
        String[] invalidNames = new String[]{ "asd/resource/-name", "prefix/-resource_1", "prefix/0123resource", "0123resource", "-resource_1", "........abc" };
        for (String validName : validNames) {
            ResourceUtils.validateNameOfResourceNameAndThrowException(validName);
        }
        for (String invalidName : invalidNames) {
            try {
                ResourceUtils.validateNameOfResourceNameAndThrowException(invalidName);
                Assert.fail((("Expected to fail name check, the name=" + invalidName) + " is illegal."));
            } catch (YarnRuntimeException e) {
                // Expected
            }
        }
    }

    @Test
    public void testGetResourceInformationWithDiffUnits() throws Exception {
        Configuration conf = new YarnConfiguration();
        Map<String, Resource> testRun = new HashMap<>();
        TestResourceUtils.setupResourceTypes(conf, "resource-types-4.xml");
        Resource test3Resources = Resource.newInstance(0, 0);
        // Resource 'resource1' has been passed as 5T
        // 5T should be converted to 5000G
        test3Resources.setResourceInformation("resource1", ResourceInformation.newInstance("resource1", "T", 5L));
        // Resource 'resource2' has been passed as 2M
        // 2M should be converted to 2000000000m
        test3Resources.setResourceInformation("resource2", ResourceInformation.newInstance("resource2", "M", 2L));
        test3Resources.setResourceInformation("yarn.io/gpu", ResourceInformation.newInstance("yarn.io/gpu", "", 1));
        testRun.put("node-resources-3.xml", test3Resources);
        for (Map.Entry<String, Resource> entry : testRun.entrySet()) {
            String resourceFile = entry.getKey();
            ResourceUtils.resetNodeResources();
            File source = new File(conf.getClassLoader().getResource(resourceFile).getFile());
            nodeResourcesFile = new File(source.getParent(), "node-resources.xml");
            FileUtils.copyFile(source, nodeResourcesFile);
            Map<String, ResourceInformation> actual = ResourceUtils.getNodeResourceInformation(conf);
            Assert.assertEquals(actual.size(), entry.getValue().getResources().length);
            for (ResourceInformation resInfo : entry.getValue().getResources()) {
                Assert.assertEquals(resInfo, actual.get(resInfo.getName()));
            }
        }
    }

    @Test
    public void testResourceUnitParsing() throws Exception {
        Resource res = ResourceUtils.createResourceFromString("memory=20g,vcores=3", ResourceUtils.getResourcesTypeInfo());
        Assert.assertEquals(Resources.createResource((20 * 1024), 3), res);
        res = ResourceUtils.createResourceFromString("memory=20G,vcores=3", ResourceUtils.getResourcesTypeInfo());
        Assert.assertEquals(Resources.createResource((20 * 1024), 3), res);
        res = ResourceUtils.createResourceFromString("memory=20M,vcores=3", ResourceUtils.getResourcesTypeInfo());
        Assert.assertEquals(Resources.createResource(20, 3), res);
        res = ResourceUtils.createResourceFromString("memory=20m,vcores=3", ResourceUtils.getResourcesTypeInfo());
        Assert.assertEquals(Resources.createResource(20, 3), res);
        res = ResourceUtils.createResourceFromString("memory-mb=20,vcores=3", ResourceUtils.getResourcesTypeInfo());
        Assert.assertEquals(Resources.createResource(20, 3), res);
        res = ResourceUtils.createResourceFromString("memory-mb=20m,vcores=3", ResourceUtils.getResourcesTypeInfo());
        Assert.assertEquals(Resources.createResource(20, 3), res);
        res = ResourceUtils.createResourceFromString("memory-mb=20G,vcores=3", ResourceUtils.getResourcesTypeInfo());
        Assert.assertEquals(Resources.createResource((20 * 1024), 3), res);
        // W/o unit for memory means bits, and 20 bits will be rounded to 0
        res = ResourceUtils.createResourceFromString("memory=20,vcores=3", ResourceUtils.getResourcesTypeInfo());
        Assert.assertEquals(Resources.createResource(0, 3), res);
        // Test multiple resources
        List<ResourceTypeInfo> resTypes = new java.util.ArrayList(ResourceUtils.getResourcesTypeInfo());
        resTypes.add(ResourceTypeInfo.newInstance(GPU_URI, ""));
        ResourceUtils.reinitializeResources(resTypes);
        res = ResourceUtils.createResourceFromString("memory=2G,vcores=3,gpu=0", resTypes);
        Assert.assertEquals((2 * 1024), res.getMemorySize());
        Assert.assertEquals(0, res.getResourceValue(GPU_URI));
        res = ResourceUtils.createResourceFromString("memory=2G,vcores=3,gpu=3", resTypes);
        Assert.assertEquals((2 * 1024), res.getMemorySize());
        Assert.assertEquals(3, res.getResourceValue(GPU_URI));
        res = ResourceUtils.createResourceFromString("memory=2G,vcores=3", resTypes);
        Assert.assertEquals((2 * 1024), res.getMemorySize());
        Assert.assertEquals(0, res.getResourceValue(GPU_URI));
        res = ResourceUtils.createResourceFromString("memory=2G,vcores=3,yarn.io/gpu=0", resTypes);
        Assert.assertEquals((2 * 1024), res.getMemorySize());
        Assert.assertEquals(0, res.getResourceValue(GPU_URI));
        res = ResourceUtils.createResourceFromString("memory=2G,vcores=3,yarn.io/gpu=3", resTypes);
        Assert.assertEquals((2 * 1024), res.getMemorySize());
        Assert.assertEquals(3, res.getResourceValue(GPU_URI));
        // TODO, add more negative tests.
    }

    @Test
    public void testMultipleOpsForResourcesWithTags() throws Exception {
        Configuration conf = new YarnConfiguration();
        TestResourceUtils.setupResourceTypes(conf, "resource-types-6.xml");
        Resource resourceA = Resource.newInstance(2, 4);
        Resource resourceB = Resource.newInstance(3, 6);
        resourceA.setResourceInformation("resource1", ResourceInformation.newInstance("resource1", "T", 5L));
        resourceA.setResourceInformation("resource2", ResourceInformation.newInstance("resource2", "M", 2L));
        resourceA.setResourceInformation("yarn.io/gpu", ResourceInformation.newInstance("yarn.io/gpu", "", 1));
        resourceA.setResourceInformation("yarn.io/test-volume", ResourceInformation.newInstance("yarn.io/test-volume", "", 2));
        resourceB.setResourceInformation("resource1", ResourceInformation.newInstance("resource1", "T", 3L));
        resourceB.setResourceInformation("resource2", ResourceInformation.newInstance("resource2", "M", 4L));
        resourceB.setResourceInformation("yarn.io/gpu", ResourceInformation.newInstance("yarn.io/gpu", "", 2));
        resourceB.setResourceInformation("yarn.io/test-volume", ResourceInformation.newInstance("yarn.io/test-volume", "", 3));
        Resource addedResource = Resources.add(resourceA, resourceB);
        Assert.assertEquals(addedResource.getMemorySize(), 5);
        Assert.assertEquals(addedResource.getVirtualCores(), 10);
        Assert.assertEquals(addedResource.getResourceInformation("resource1").getValue(), 8);
        // Verify that value of resourceA and resourceB is not added up for
        // "yarn.io/test-volume".
        Assert.assertEquals(addedResource.getResourceInformation("yarn.io/test-volume").getValue(), 2);
        Resource mulResource = Resources.multiplyAndRoundDown(resourceA, 3);
        Assert.assertEquals(mulResource.getMemorySize(), 6);
        Assert.assertEquals(mulResource.getVirtualCores(), 12);
        Assert.assertEquals(mulResource.getResourceInformation("resource1").getValue(), 15);
        // Verify that value of resourceA is not multiplied up for
        // "yarn.io/test-volume".
        Assert.assertEquals(mulResource.getResourceInformation("yarn.io/test-volume").getValue(), 2);
    }
}

