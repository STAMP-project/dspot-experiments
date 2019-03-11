/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.flink.integration.operation.handler;


import kafka.server.KafkaServer;
import org.apache.curator.test.TestingServer;
import org.apache.flink.testutils.junit.RetryRule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.flink.operation.FlinkTest;
import uk.gov.gchq.gaffer.generator.TestBytesGeneratorImpl;
import uk.gov.gchq.gaffer.generator.TestGeneratorImpl;


public class AddElementsFromKafkaHandlerIT extends FlinkTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    @Rule
    public final RetryRule rule = new RetryRule();

    private KafkaProducer<Integer, String> producer;

    private KafkaServer kafkaServer;

    private TestingServer zkServer;

    private String bootstrapServers;

    @Test
    public void shouldAddElementsWithStringConsumer() throws Exception {
        shouldAddElements(String.class, TestGeneratorImpl.class);
    }

    @Test
    public void shouldAddElementsWithByteArrayConsumer() throws Exception {
        shouldAddElements(byte[].class, TestBytesGeneratorImpl.class);
    }
}

