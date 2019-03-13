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
package org.apache.flink.formats.sequencefile;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link SerializableHadoopConfiguration}.
 */
public class SerializableHadoopConfigurationTest {
    private static final String TEST_KEY = "test-key";

    private static final String TEST_VALUE = "test-value";

    private Configuration configuration;

    @Test
    public void customPropertiesSurviveSerializationDeserialization() throws IOException, ClassNotFoundException {
        final SerializableHadoopConfiguration serializableConfigUnderTest = new SerializableHadoopConfiguration(configuration);
        final byte[] serializedConfigUnderTest = serializeAndGetBytes(serializableConfigUnderTest);
        final SerializableHadoopConfiguration deserializableConfigUnderTest = deserializeAndGetConfiguration(serializedConfigUnderTest);
        Assert.assertThat(deserializableConfigUnderTest.get(), SerializableHadoopConfigurationTest.hasTheSamePropertiesAs(configuration));
    }
}

