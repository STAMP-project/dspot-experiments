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
package org.apache.hadoop.yarn.service.conf;


import ConfigFile.TypeEnum.PROPERTIES;
import ConfigFile.TypeEnum.XML;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.service.ServiceTestUtils;
import org.apache.hadoop.yarn.service.api.records.Component;
import org.apache.hadoop.yarn.service.api.records.ConfigFile;
import org.apache.hadoop.yarn.service.api.records.Configuration;
import org.apache.hadoop.yarn.service.api.records.Resource;
import org.apache.hadoop.yarn.service.api.records.ResourceInformation;
import org.apache.hadoop.yarn.service.api.records.Service;
import org.apache.hadoop.yarn.service.utils.ServiceApiUtil;
import org.apache.hadoop.yarn.service.utils.SliderFileSystem;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test global configuration resolution.
 */
public class TestAppJsonResolve extends Assert {
    protected static final Logger LOG = LoggerFactory.getLogger(TestAppJsonResolve.class);

    @Test
    public void testOverride() throws Throwable {
        Service orig = ExampleAppJson.loadResource(ExampleAppJson.OVERRIDE_JSON);
        Configuration global = orig.getConfiguration();
        Assert.assertEquals("a", global.getProperty("g1"));
        Assert.assertEquals("b", global.getProperty("g2"));
        Assert.assertEquals(2, global.getFiles().size());
        Configuration simple = orig.getComponent("simple").getConfiguration();
        Assert.assertEquals(0, simple.getProperties().size());
        Assert.assertEquals(1, simple.getFiles().size());
        Configuration master = orig.getComponent("master").getConfiguration();
        Assert.assertEquals("m", master.getProperty("name"));
        Assert.assertEquals("overridden", master.getProperty("g1"));
        Assert.assertEquals(0, master.getFiles().size());
        Configuration worker = orig.getComponent("worker").getConfiguration();
        TestAppJsonResolve.LOG.info("worker = {}", worker);
        Assert.assertEquals(3, worker.getProperties().size());
        Assert.assertEquals(0, worker.getFiles().size());
        Assert.assertEquals("worker", worker.getProperty("name"));
        Assert.assertEquals("overridden-by-worker", worker.getProperty("g1"));
        Assert.assertNull(worker.getProperty("g2"));
        Assert.assertEquals("1000", worker.getProperty("timeout"));
        // here is the resolution
        SliderFileSystem sfs = ServiceTestUtils.initMockFs();
        ServiceApiUtil.validateAndResolveService(orig, sfs, new YarnConfiguration());
        global = orig.getConfiguration();
        TestAppJsonResolve.LOG.info("global = {}", global);
        Assert.assertEquals("a", global.getProperty("g1"));
        Assert.assertEquals("b", global.getProperty("g2"));
        Assert.assertEquals(2, global.getFiles().size());
        simple = orig.getComponent("simple").getConfiguration();
        Assert.assertEquals(2, simple.getProperties().size());
        Assert.assertEquals("a", simple.getProperty("g1"));
        Assert.assertEquals("b", simple.getProperty("g2"));
        Assert.assertEquals(2, simple.getFiles().size());
        Set<ConfigFile> files = new HashSet<>();
        Map<String, String> props = new HashMap<>();
        props.put("k1", "overridden");
        props.put("k2", "v2");
        files.add(new ConfigFile().destFile("file1").type(PROPERTIES).properties(props));
        files.add(new ConfigFile().destFile("file2").type(XML).properties(Collections.singletonMap("k3", "v3")));
        Assert.assertTrue(files.contains(simple.getFiles().get(0)));
        Assert.assertTrue(files.contains(simple.getFiles().get(1)));
        master = orig.getComponent("master").getConfiguration();
        TestAppJsonResolve.LOG.info("master = {}", master);
        Assert.assertEquals(3, master.getProperties().size());
        Assert.assertEquals("m", master.getProperty("name"));
        Assert.assertEquals("overridden", master.getProperty("g1"));
        Assert.assertEquals("b", master.getProperty("g2"));
        Assert.assertEquals(2, master.getFiles().size());
        props.put("k1", "v1");
        files.clear();
        files.add(new ConfigFile().destFile("file1").type(PROPERTIES).properties(props));
        files.add(new ConfigFile().destFile("file2").type(XML).properties(Collections.singletonMap("k3", "v3")));
        Assert.assertTrue(files.contains(master.getFiles().get(0)));
        Assert.assertTrue(files.contains(master.getFiles().get(1)));
        worker = orig.getComponent("worker").getConfiguration();
        TestAppJsonResolve.LOG.info("worker = {}", worker);
        Assert.assertEquals(4, worker.getProperties().size());
        Assert.assertEquals("worker", worker.getProperty("name"));
        Assert.assertEquals("overridden-by-worker", worker.getProperty("g1"));
        Assert.assertEquals("b", worker.getProperty("g2"));
        Assert.assertEquals("1000", worker.getProperty("timeout"));
        Assert.assertEquals(2, worker.getFiles().size());
        Assert.assertTrue(files.contains(worker.getFiles().get(0)));
        Assert.assertTrue(files.contains(worker.getFiles().get(1)));
    }

    @Test
    public void testOverrideExternalConfiguration() throws IOException {
        Service orig = ExampleAppJson.loadResource(ExampleAppJson.EXTERNAL_JSON_1);
        Configuration global = orig.getConfiguration();
        Assert.assertEquals(0, global.getProperties().size());
        Assert.assertEquals(3, orig.getComponents().size());
        Configuration simple = orig.getComponent("simple").getConfiguration();
        Assert.assertEquals(0, simple.getProperties().size());
        Configuration master = orig.getComponent("master").getConfiguration();
        Assert.assertEquals(1, master.getProperties().size());
        Assert.assertEquals("is-overridden", master.getProperty("g3"));
        Configuration other = orig.getComponent("other").getConfiguration();
        Assert.assertEquals(0, other.getProperties().size());
        // load the external service
        SliderFileSystem sfs = ServiceTestUtils.initMockFs();
        Service ext = ExampleAppJson.loadResource(ExampleAppJson.APP_JSON);
        ServiceApiUtil.validateAndResolveService(ext, sfs, new YarnConfiguration());
        // perform the resolution on original service
        sfs = ServiceTestUtils.initMockFs(ext);
        ServiceApiUtil.validateAndResolveService(orig, sfs, new YarnConfiguration());
        global = orig.getConfiguration();
        Assert.assertEquals(0, global.getProperties().size());
        Assert.assertEquals(4, orig.getComponents().size());
        simple = orig.getComponent("simple").getConfiguration();
        Assert.assertEquals(3, simple.getProperties().size());
        Assert.assertEquals("a", simple.getProperty("g1"));
        Assert.assertEquals("b", simple.getProperty("g2"));
        Assert.assertEquals("60", simple.getProperty("yarn.service.failure-count-reset.window"));
        master = orig.getComponent("master").getConfiguration();
        Assert.assertEquals(5, master.getProperties().size());
        Assert.assertEquals("512M", master.getProperty("jvm.heapsize"));
        Assert.assertEquals("overridden", master.getProperty("g1"));
        Assert.assertEquals("b", master.getProperty("g2"));
        Assert.assertEquals("is-overridden", master.getProperty("g3"));
        Assert.assertEquals("60", simple.getProperty("yarn.service.failure-count-reset.window"));
        Configuration worker = orig.getComponent("worker").getConfiguration();
        TestAppJsonResolve.LOG.info("worker = {}", worker);
        Assert.assertEquals(4, worker.getProperties().size());
        Assert.assertEquals("512M", worker.getProperty("jvm.heapsize"));
        Assert.assertEquals("overridden-by-worker", worker.getProperty("g1"));
        Assert.assertEquals("b", worker.getProperty("g2"));
        Assert.assertEquals("60", worker.getProperty("yarn.service.failure-count-reset.window"));
        // Validate worker's resources
        Resource workerResource = orig.getComponent("worker").getResource();
        Assert.assertEquals(1, workerResource.getCpus().intValue());
        Assert.assertEquals(1024, workerResource.calcMemoryMB());
        Assert.assertNotNull(workerResource.getAdditional());
        Assert.assertEquals(2, workerResource.getAdditional().size());
        Assert.assertEquals(3333, workerResource.getAdditional().get("resource-1").getValue().longValue());
        Assert.assertEquals("Gi", workerResource.getAdditional().get("resource-1").getUnit());
        Assert.assertEquals(5, workerResource.getAdditional().get("yarn.io/gpu").getValue().longValue());
        Assert.assertEquals("", workerResource.getAdditional().get("yarn.io/gpu").getUnit());
        other = orig.getComponent("other").getConfiguration();
        Assert.assertEquals(0, other.getProperties().size());
    }

    @Test
    public void testSetResourceAttributes() throws IOException {
        Service orig = ExampleAppJson.loadResource(ExampleAppJson.EXTERNAL_JSON_3);
        Component component = orig.getComponent("volume-service");
        Assert.assertNotNull(component);
        Map<String, ResourceInformation> adResource = component.getResource().getAdditional();
        Assert.assertNotNull(adResource);
        Assert.assertEquals(1, adResource.size());
        Map.Entry<String, ResourceInformation> volume = adResource.entrySet().iterator().next();
        Assert.assertEquals("yarn.io/csi-volume", volume.getKey());
        Assert.assertEquals(100L, volume.getValue().getValue().longValue());
        Assert.assertEquals(2, volume.getValue().getAttributes().size());
        Assert.assertEquals(1, volume.getValue().getTags().size());
    }
}

