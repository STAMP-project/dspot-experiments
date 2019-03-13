/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.config.sources;


import com.google.common.collect.ImmutableMap;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PollResult;
import java.util.Map;
import org.jclouds.blobstore.BlobStoreContext;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple test which uses the im-memory ({@code transient}) {@link BlobStore}.
 *
 * @author Adrian Cole
 */
public class BlobStoreBackedConfigurationTest {
    private static final String DEFAULT_CONTAINER = "archaius-blobstore";

    private static BlobStoreContext ctx;

    private static Map<String, String> INITIAL = ImmutableMap.<String, String>builder().put("test1", "val1").put("test2", "val2").put("test3", "val3").build();

    private static Map<String, String> UPDATE = ImmutableMap.<String, String>builder().put("test1", "vala").put("test2", "valb").put("test3", "valc").build();

    @Test
    public void testPropertyChange() throws Exception {
        BlobStoreConfigurationSource source = new BlobStoreConfigurationSource(BlobStoreBackedConfigurationTest.ctx);
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 1000, false);
        DynamicConfiguration dynamicConfig = new DynamicConfiguration(source, scheduler);
        ConfigurationManager.loadPropertiesFromConfiguration(dynamicConfig);
        DynamicStringProperty test1 = DynamicPropertyFactory.getInstance().getStringProperty("test1", "");
        DynamicStringProperty test2 = DynamicPropertyFactory.getInstance().getStringProperty("test2", "");
        DynamicStringProperty test3 = DynamicPropertyFactory.getInstance().getStringProperty("test3", "");
        Assert.assertEquals("val1", test1.get());
        Assert.assertEquals("val2", test2.get());
        Assert.assertEquals("val3", test3.get());
        update();
        Thread.sleep(1250);
        Assert.assertEquals("vala", test1.get());
        Assert.assertEquals("valb", test2.get());
        Assert.assertEquals("valc", test3.get());
    }

    @Test
    public void testPoll() throws Exception {
        BlobStoreConfigurationSource testConfigSource = new BlobStoreConfigurationSource(BlobStoreBackedConfigurationTest.ctx);
        PollResult result = testConfigSource.poll(true, null);
        Assert.assertEquals(3, result.getComplete().size());
        Assert.assertEquals("val1", result.getComplete().get("test1"));
        Assert.assertEquals("val2", result.getComplete().get("test2"));
        Assert.assertEquals("val3", result.getComplete().get("test3"));
    }

    @Test
    public void testUpdate() throws Exception {
        BlobStoreConfigurationSource testConfigSource = new BlobStoreConfigurationSource(BlobStoreBackedConfigurationTest.ctx);
        PollResult result = testConfigSource.poll(true, null);
        Assert.assertEquals(3, result.getComplete().size());
        Assert.assertEquals("val1", result.getComplete().get("test1"));
        Assert.assertEquals("val2", result.getComplete().get("test2"));
        Assert.assertEquals("val3", result.getComplete().get("test3"));
        update();
        result = testConfigSource.poll(true, null);
        Assert.assertEquals(3, result.getComplete().size());
        Assert.assertEquals("vala", result.getComplete().get("test1"));
        Assert.assertEquals("valb", result.getComplete().get("test2"));
        Assert.assertEquals("valc", result.getComplete().get("test3"));
    }
}

