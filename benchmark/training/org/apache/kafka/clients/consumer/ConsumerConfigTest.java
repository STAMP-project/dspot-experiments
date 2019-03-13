/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients.consumer;


import ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
import org.junit.Test;


public class ConsumerConfigTest {
    private final Deserializer keyDeserializer = new ByteArrayDeserializer();

    private final Deserializer valueDeserializer = new StringDeserializer();

    private final String keyDeserializerClassName = keyDeserializer.getClass().getName();

    private final String valueDeserializerClassName = valueDeserializer.getClass().getName();

    private final Object keyDeserializerClass = keyDeserializer.getClass();

    private final Object valueDeserializerClass = valueDeserializer.getClass();

    @Test
    public void testDeserializerToPropertyConfig() {
        Properties properties = new Properties();
        properties.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClassName);
        properties.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClassName);
        Properties newProperties = ConsumerConfig.addDeserializerToConfig(properties, null, null);
        Assert.assertEquals(newProperties.get(KEY_DESERIALIZER_CLASS_CONFIG), keyDeserializerClassName);
        Assert.assertEquals(newProperties.get(VALUE_DESERIALIZER_CLASS_CONFIG), valueDeserializerClassName);
        properties.clear();
        properties.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClassName);
        newProperties = ConsumerConfig.addDeserializerToConfig(properties, keyDeserializer, null);
        Assert.assertEquals(newProperties.get(KEY_DESERIALIZER_CLASS_CONFIG), keyDeserializerClassName);
        Assert.assertEquals(newProperties.get(VALUE_DESERIALIZER_CLASS_CONFIG), valueDeserializerClassName);
        properties.clear();
        properties.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClassName);
        newProperties = ConsumerConfig.addDeserializerToConfig(properties, null, valueDeserializer);
        Assert.assertEquals(newProperties.get(KEY_DESERIALIZER_CLASS_CONFIG), keyDeserializerClassName);
        Assert.assertEquals(newProperties.get(VALUE_DESERIALIZER_CLASS_CONFIG), valueDeserializerClassName);
        properties.clear();
        newProperties = ConsumerConfig.addDeserializerToConfig(properties, keyDeserializer, valueDeserializer);
        Assert.assertEquals(newProperties.get(KEY_DESERIALIZER_CLASS_CONFIG), keyDeserializerClassName);
        Assert.assertEquals(newProperties.get(VALUE_DESERIALIZER_CLASS_CONFIG), valueDeserializerClassName);
    }

    @Test
    public void testDeserializerToMapConfig() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
        configs.put(VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
        Map<String, Object> newConfigs = ConsumerConfig.addDeserializerToConfig(configs, null, null);
        Assert.assertEquals(newConfigs.get(KEY_DESERIALIZER_CLASS_CONFIG), keyDeserializerClass);
        Assert.assertEquals(newConfigs.get(VALUE_DESERIALIZER_CLASS_CONFIG), valueDeserializerClass);
        configs.clear();
        configs.put(VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
        newConfigs = ConsumerConfig.addDeserializerToConfig(configs, keyDeserializer, null);
        Assert.assertEquals(newConfigs.get(KEY_DESERIALIZER_CLASS_CONFIG), keyDeserializerClass);
        Assert.assertEquals(newConfigs.get(VALUE_DESERIALIZER_CLASS_CONFIG), valueDeserializerClass);
        configs.clear();
        configs.put(KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
        newConfigs = ConsumerConfig.addDeserializerToConfig(configs, null, valueDeserializer);
        Assert.assertEquals(newConfigs.get(KEY_DESERIALIZER_CLASS_CONFIG), keyDeserializerClass);
        Assert.assertEquals(newConfigs.get(VALUE_DESERIALIZER_CLASS_CONFIG), valueDeserializerClass);
        configs.clear();
        newConfigs = ConsumerConfig.addDeserializerToConfig(configs, keyDeserializer, valueDeserializer);
        Assert.assertEquals(newConfigs.get(KEY_DESERIALIZER_CLASS_CONFIG), keyDeserializerClass);
        Assert.assertEquals(newConfigs.get(VALUE_DESERIALIZER_CLASS_CONFIG), valueDeserializerClass);
    }
}

