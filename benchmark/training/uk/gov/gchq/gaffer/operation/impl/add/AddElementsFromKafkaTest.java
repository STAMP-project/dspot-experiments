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
package uk.gov.gchq.gaffer.operation.impl.add;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.generator.TestGeneratorImpl;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class AddElementsFromKafkaTest extends OperationTest<AddElementsFromKafka> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws JsonProcessingException, SerialisationException {
        // Given
        final boolean validate = true;
        final boolean skipInvalid = false;
        final Integer parallelism = 2;
        final Class<TestGeneratorImpl> generator = TestGeneratorImpl.class;
        final String groupId = "groupId";
        final String topic = "topic";
        final String[] servers = new String[]{ "server1", "server2" };
        final AddElementsFromKafka op = new AddElementsFromKafka.Builder().generator(generator).parallelism(parallelism).validate(validate).skipInvalidElements(skipInvalid).groupId(groupId).topic(topic).bootstrapServers(servers).build();
        // When
        final byte[] json = JSONSerialiser.serialise(op, true);
        final AddElementsFromKafka deserialisedOp = JSONSerialiser.deserialise(json, AddElementsFromKafka.class);
        // Then
        JsonAssert.assertEquals(String.format(("{%n" + (((((("  \"class\" : \"uk.gov.gchq.gaffer.operation.impl.add.AddElementsFromKafka\",%n" + "  \"topic\" : \"topic\",%n") + "  \"groupId\" : \"groupId\",%n") + "  \"bootstrapServers\" : [ \"server1\", \"server2\" ],%n") + "  \"parallelism\" : 2,%n") + "  \"elementGenerator\" : \"uk.gov.gchq.gaffer.generator.TestGeneratorImpl\"%n") + "}"))).getBytes(), json);
        Assert.assertEquals(generator, deserialisedOp.getElementGenerator());
        Assert.assertEquals(parallelism, deserialisedOp.getParallelism());
        Assert.assertEquals(validate, deserialisedOp.isValidate());
        Assert.assertEquals(skipInvalid, deserialisedOp.isSkipInvalidElements());
        Assert.assertEquals(groupId, deserialisedOp.getGroupId());
        Assert.assertEquals(topic, deserialisedOp.getTopic());
        Assert.assertArrayEquals(servers, deserialisedOp.getBootstrapServers());
    }
}

