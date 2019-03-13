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
package com.twitter.distributedlog.config;


import DistributedLogConfiguration.BKDL_RETENTION_PERIOD_IN_HOURS;
import DistributedLogConfiguration.BKDL_RETENTION_PERIOD_IN_HOURS_DEFAULT;
import com.google.common.base.Optional;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDynamicConfigurationFactory {
    static final Logger LOG = LoggerFactory.getLogger(TestDynamicConfigurationFactory.class);

    @Test(timeout = 60000)
    public void testGetDynamicConfigBasics() throws Exception {
        PropertiesWriter writer = new PropertiesWriter();
        DynamicConfigurationFactory factory = getConfigFactory(writer.getFile());
        Optional<DynamicDistributedLogConfiguration> conf = factory.getDynamicConfiguration(writer.getFile().getPath());
        Assert.assertEquals(BKDL_RETENTION_PERIOD_IN_HOURS_DEFAULT, conf.get().getRetentionPeriodHours());
        writer.setProperty(BKDL_RETENTION_PERIOD_IN_HOURS, "1");
        writer.save();
        waitForConfig(conf.get(), 1);
        Assert.assertEquals(1, conf.get().getRetentionPeriodHours());
    }

    @Test(timeout = 60000)
    public void testGetDynamicConfigIsSingleton() throws Exception {
        PropertiesWriter writer = new PropertiesWriter();
        DynamicConfigurationFactory factory = getConfigFactory(writer.getFile());
        String configPath = writer.getFile().getPath();
        Optional<DynamicDistributedLogConfiguration> conf1 = factory.getDynamicConfiguration(configPath);
        Optional<DynamicDistributedLogConfiguration> conf2 = factory.getDynamicConfiguration(configPath);
        Assert.assertEquals(conf1, conf2);
    }

    /**
     * If the file is missing, get-config should not fail, and the file should be picked up if its added.
     * If the file is removed externally same should apply.
     */
    @Test(timeout = 60000)
    public void testMissingConfig() throws Exception {
        PropertiesWriter writer = new PropertiesWriter();
        DynamicConfigurationFactory factory = getConfigFactory(writer.getFile());
        Optional<DynamicDistributedLogConfiguration> conf = factory.getDynamicConfiguration(writer.getFile().getPath());
        writer.setProperty(BKDL_RETENTION_PERIOD_IN_HOURS, "1");
        writer.save();
        waitForConfig(conf.get(), 1);
        File configFile = writer.getFile();
        configFile.delete();
        Thread.sleep(1000);
        PropertiesWriter writer2 = new PropertiesWriter(writer.getFile());
        writer2.setProperty(BKDL_RETENTION_PERIOD_IN_HOURS, "2");
        writer2.save();
        waitForConfig(conf.get(), 2);
    }
}

