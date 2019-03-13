/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.kafka.util;


import com.cloudera.oryx.api.KeyMessage;
import com.cloudera.oryx.common.OryxTest;
import com.cloudera.oryx.common.collection.CloseableIterator;
import com.cloudera.oryx.common.collection.Pair;
import com.cloudera.oryx.common.io.IOUtils;
import com.cloudera.oryx.common.lang.LoggingCallable;
import com.cloudera.oryx.common.settings.ConfigUtils;
import java.util.List;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class LargeMessageIT extends OryxTest {
    private static final Logger log = LoggerFactory.getLogger(LargeMessageIT.class);

    private static final String TOPIC = (LargeMessageIT.class.getSimpleName()) + "Topic";

    private static final int NUM_DATA = 1;

    @Test
    public void testProduceConsume() throws Exception {
        int zkPort = IOUtils.chooseFreePort();
        int kafkaBrokerPort = IOUtils.chooseFreePort();
        try (LocalZKServer localZKServer = new LocalZKServer(zkPort);LocalKafkaBroker localKafkaBroker = new LocalKafkaBroker(kafkaBrokerPort, zkPort)) {
            localZKServer.start();
            localKafkaBroker.start();
            int maxMessageSize = ConfigUtils.getDefault().getInt("oryx.update-topic.message.max-size");
            String zkHostPort = "localhost:" + zkPort;
            KafkaUtils.deleteTopic(zkHostPort, LargeMessageIT.TOPIC);
            KafkaUtils.maybeCreateTopic(zkHostPort, LargeMessageIT.TOPIC, 1, ConfigUtils.keyValueToProperties("max.message.bytes", maxMessageSize));
            int largeMessageSize = maxMessageSize - (1 << 14);// slack for message metadata

            ProduceData produce = new ProduceData(new LargeMessageIT.BigDatumGenerator(largeMessageSize), localKafkaBroker.getPort(), LargeMessageIT.TOPIC, LargeMessageIT.NUM_DATA, 0);
            List<KeyMessage<String, String>> keyMessages;
            try (CloseableIterator<KeyMessage<String, String>> data = new ConsumeData(LargeMessageIT.TOPIC, maxMessageSize, kafkaBrokerPort).iterator()) {
                LargeMessageIT.log.info("Starting consumer thread");
                ConsumeTopicRunnable consumeTopic = new ConsumeTopicRunnable(data, LargeMessageIT.NUM_DATA);
                new Thread(LoggingCallable.log(consumeTopic).asRunnable(), "ConsumeTopicThread").start();
                consumeTopic.awaitRun();
                LargeMessageIT.log.info("Producing data");
                produce.start();
                consumeTopic.awaitMessages();
                keyMessages = consumeTopic.getKeyMessages();
            } finally {
                KafkaUtils.deleteTopic(zkHostPort, LargeMessageIT.TOPIC);
            }
            assertEquals(1, keyMessages.size());
            assertEquals(LargeMessageIT.BigDatumGenerator.LARGE_MESSAGE, keyMessages.get(0).getMessage());
        }
    }

    private static final class BigDatumGenerator implements DatumGenerator<String, String> {
        private static String LARGE_MESSAGE;

        private final int largeMessageSize;

        private BigDatumGenerator(int largeMessageSize) {
            this.largeMessageSize = largeMessageSize;
        }

        @Override
        public Pair<String, String> generate(int id, RandomGenerator random) {
            if ((LargeMessageIT.BigDatumGenerator.LARGE_MESSAGE) == null) {
                StringBuilder builder = new StringBuilder(largeMessageSize);
                for (int i = 0; i < (largeMessageSize); i++) {
                    builder.append(((char) (' ' + (random.nextInt((('~' - ' ') + 1))))));
                }
                LargeMessageIT.BigDatumGenerator.LARGE_MESSAGE = builder.toString();
            }
            return new Pair(null, LargeMessageIT.BigDatumGenerator.LARGE_MESSAGE);
        }
    }
}

