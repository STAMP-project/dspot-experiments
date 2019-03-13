/**
 * Copyright 2018-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.operation.impl;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.koryphe.ValidationResult;


public class ValidateOperationChainTest extends OperationTest<ValidateOperationChain> {
    final OperationChain operationChain = new OperationChain.Builder().first(new AddElements()).then(new GetElements()).build();

    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final ValidateOperationChain validateOperationChain = new ValidateOperationChain.Builder().operationChain(operationChain).build();
        // When
        byte[] json = JSONSerialiser.serialise(validateOperationChain, true);
        final ValidateOperationChain deserialisedOp = JSONSerialiser.deserialise(json, ValidateOperationChain.class);
        // Then
        Assert.assertEquals(validateOperationChain.getOperationChain(), deserialisedOp.getOperationChain());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(ValidationResult.class, outputClass);
    }
}

