/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.servicescommon;


import GooglePubSubSender.HealthChecker;
import com.google.cloud.pubsub.Message;
import com.google.cloud.pubsub.PubSub;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GooglePubSubSenderTest {
    private final PubSub pubsub = Mockito.mock(PubSub.class);

    private final String prefix = "prefix.";

    private final HealthChecker healthchecker = Mockito.mock(HealthChecker.class);

    private final GooglePubSubSender sender = GooglePubSubSender.create(pubsub, prefix, healthchecker);

    @Test
    public void testSendWhenHealthy() throws Exception {
        Mockito.when(healthchecker.isHealthy()).thenReturn(true);
        final String topic = "Event";
        sender.send(topic, new byte['x']);
        Mockito.verify(pubsub).publishAsync(ArgumentMatchers.eq(((prefix) + topic)), ArgumentMatchers.any(Message.class));
    }

    @Test
    public void testSendWhenUnhealthy() throws Exception {
        Mockito.when(healthchecker.isHealthy()).thenReturn(false);
        final String topic = "Event";
        sender.send(topic, new byte['x']);
        Mockito.verify(pubsub, Mockito.never()).publishAsync(ArgumentMatchers.eq(((prefix) + topic)), ArgumentMatchers.any(Message.class));
    }
}

