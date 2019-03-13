/**
 * Copyright 2018 NAVER Corp.
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
package com.navercorp.pinpoint.plugin.kafka;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class KafkaConfigTest {
    @Test
    public void configTest1() throws Exception {
        KafkaConfig config = createConfig("true", "true", "entryPoint");
        Assert.assertTrue(config.isProducerEnable());
        Assert.assertTrue(config.isConsumerEnable());
        Assert.assertEquals("entryPoint", config.getKafkaEntryPoint());
    }

    @Test
    public void configTest2() throws Exception {
        KafkaConfig config = createConfig("true", "false");
        Assert.assertTrue(config.isProducerEnable());
        Assert.assertFalse(config.isConsumerEnable());
        Assert.assertEquals("", config.getKafkaEntryPoint());
    }

    @Test
    public void configTest3() throws Exception {
        KafkaConfig config = createConfig("false", "true");
        Assert.assertFalse(config.isProducerEnable());
        Assert.assertTrue(config.isConsumerEnable());
        Assert.assertEquals("", config.getKafkaEntryPoint());
    }

    @Test
    public void configTest4() throws Exception {
        KafkaConfig config = createConfig("false", "false");
        Assert.assertFalse(config.isProducerEnable());
        Assert.assertFalse(config.isConsumerEnable());
        Assert.assertEquals("", config.getKafkaEntryPoint());
    }
}

