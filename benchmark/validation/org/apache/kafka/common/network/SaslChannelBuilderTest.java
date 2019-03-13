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
package org.apache.kafka.common.network;


import SecurityProtocol.SASL_PLAINTEXT;
import SecurityProtocol.SASL_SSL;
import SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG;
import java.util.Collections;
import java.util.HashMap;
import org.apache.kafka.common.KafkaException;
import org.junit.Assert;
import org.junit.Test;


public class SaslChannelBuilderTest {
    @Test
    public void testCloseBeforeConfigureIsIdempotent() {
        SaslChannelBuilder builder = createChannelBuilder(SASL_PLAINTEXT);
        builder.close();
        Assert.assertTrue(builder.loginManagers().isEmpty());
        builder.close();
        Assert.assertTrue(builder.loginManagers().isEmpty());
    }

    @Test
    public void testCloseAfterConfigIsIdempotent() {
        SaslChannelBuilder builder = createChannelBuilder(SASL_PLAINTEXT);
        builder.configure(new HashMap<String, Object>());
        Assert.assertNotNull(builder.loginManagers().get("PLAIN"));
        builder.close();
        Assert.assertTrue(builder.loginManagers().isEmpty());
        builder.close();
        Assert.assertTrue(builder.loginManagers().isEmpty());
    }

    @Test
    public void testLoginManagerReleasedIfConfigureThrowsException() {
        SaslChannelBuilder builder = createChannelBuilder(SASL_SSL);
        try {
            // Use invalid config so that an exception is thrown
            builder.configure(Collections.singletonMap(SSL_ENABLED_PROTOCOLS_CONFIG, "1"));
            Assert.fail("Exception should have been thrown");
        } catch (KafkaException e) {
            Assert.assertTrue(builder.loginManagers().isEmpty());
        }
        builder.close();
        Assert.assertTrue(builder.loginManagers().isEmpty());
    }
}

