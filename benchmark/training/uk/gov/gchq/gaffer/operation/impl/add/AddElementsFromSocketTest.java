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


public class AddElementsFromSocketTest extends OperationTest<AddElementsFromSocket> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws JsonProcessingException, SerialisationException {
        // Given
        final boolean validate = true;
        final boolean skipInvalid = false;
        final Integer parallelism = 2;
        final int port = 6874;
        final String hostname = "hostname";
        final String delimiter = ",";
        final Class<TestGeneratorImpl> generator = TestGeneratorImpl.class;
        final AddElementsFromSocket op = new AddElementsFromSocket.Builder().generator(generator).parallelism(parallelism).validate(validate).skipInvalidElements(skipInvalid).hostname(hostname).port(port).delimiter(delimiter).build();
        // When
        final byte[] json = JSONSerialiser.serialise(op, true);
        final AddElementsFromSocket deserialisedOp = JSONSerialiser.deserialise(json, AddElementsFromSocket.class);
        // Then
        JsonAssert.assertEquals(String.format(("{%n" + (((((((("  \"class\" : \"uk.gov.gchq.gaffer.operation.impl.add.AddElementsFromSocket\",%n" + "  \"hostname\" : \"hostname\",%n") + "  \"port\" : 6874,%n") + "  \"elementGenerator\" : \"uk.gov.gchq.gaffer.generator.TestGeneratorImpl\",%n") + "  \"parallelism\" : 2,%n") + "  \"validate\" : true,%n") + "  \"skipInvalidElements\" : false,%n") + "  \"delimiter\" : \",\"%n") + "}"))).getBytes(), json);
        Assert.assertEquals(generator, deserialisedOp.getElementGenerator());
        Assert.assertEquals(parallelism, deserialisedOp.getParallelism());
        Assert.assertEquals(validate, deserialisedOp.isValidate());
        Assert.assertEquals(skipInvalid, deserialisedOp.isSkipInvalidElements());
        Assert.assertEquals(hostname, deserialisedOp.getHostname());
        Assert.assertEquals(port, deserialisedOp.getPort());
        Assert.assertEquals(delimiter, deserialisedOp.getDelimiter());
    }
}

