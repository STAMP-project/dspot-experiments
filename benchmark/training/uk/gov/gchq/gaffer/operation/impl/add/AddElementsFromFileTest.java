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


public class AddElementsFromFileTest extends OperationTest<AddElementsFromFile> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws JsonProcessingException, SerialisationException {
        // Given
        final boolean validate = true;
        final boolean skipInvalid = false;
        final String filename = "filename";
        final Integer parallelism = 2;
        final Class<TestGeneratorImpl> generator = TestGeneratorImpl.class;
        final AddElementsFromFile op = new AddElementsFromFile.Builder().filename(filename).generator(generator).parallelism(parallelism).validate(validate).skipInvalidElements(skipInvalid).build();
        // When
        final byte[] json = JSONSerialiser.serialise(op, true);
        final AddElementsFromFile deserialisedOp = JSONSerialiser.deserialise(json, AddElementsFromFile.class);
        // Then
        JsonAssert.assertEquals(String.format(("{%n" + (((((("  \"class\" : \"uk.gov.gchq.gaffer.operation.impl.add.AddElementsFromFile\",%n" + "  \"filename\" : \"filename\",%n") + "  \"parallelism\" : 2,%n") + "  \"elementGenerator\" : \"uk.gov.gchq.gaffer.generator.TestGeneratorImpl\",%n") + "  \"validate\" : true,%n") + "  \"skipInvalidElements\" : false%n") + "}"))).getBytes(), json);
        Assert.assertEquals(filename, deserialisedOp.getFilename());
        Assert.assertEquals(generator, deserialisedOp.getElementGenerator());
        Assert.assertEquals(parallelism, deserialisedOp.getParallelism());
        Assert.assertEquals(validate, deserialisedOp.isValidate());
        Assert.assertEquals(skipInvalid, deserialisedOp.isSkipInvalidElements());
    }
}

