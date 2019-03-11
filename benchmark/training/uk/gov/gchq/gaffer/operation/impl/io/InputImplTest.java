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
package uk.gov.gchq.gaffer.operation.impl.io;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.data.CustomVertex;
import uk.gov.gchq.koryphe.ValidationResult;


public class InputImplTest extends OperationTest<InputImpl> {
    @Test
    public void shouldSerialiseAndDeserialiseOperation() throws SerialisationException {
        // Given
        final String requiredField1 = "value1";
        final CustomVertex requiredField2 = new CustomVertex("type1", "value1");
        final Date optionalField1 = new Date(1L);
        final CustomVertex optionalField2 = new CustomVertex("type2", "value2");
        final List<String> input = Arrays.asList("1", "2", "3", "4");
        final InputImpl op = input(input).build();
        // When
        byte[] json = toJson(op);
        final InputImpl deserialisedOp = fromJson(json);
        // Then
        Assert.assertEquals(requiredField1, deserialisedOp.getRequiredField1());
        Assert.assertEquals(requiredField2, deserialisedOp.getRequiredField2());
        Assert.assertEquals(optionalField1, deserialisedOp.getOptionalField1());
        Assert.assertEquals(optionalField2, deserialisedOp.getOptionalField2());
        Assert.assertEquals(input, deserialisedOp.getInput());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // Given / When
        final String requiredField1 = "value1";
        final CustomVertex requiredField2 = new CustomVertex("type1", "value1");
        final Date optionalField1 = new Date(1L);
        final CustomVertex optionalField2 = new CustomVertex("type2", "value2");
        final List<String> input = Arrays.asList("1", "2", "3", "4");
        final InputImpl op = input(input).build();
        // Then
        Assert.assertEquals(requiredField1, op.getRequiredField1());
        Assert.assertEquals(requiredField2, op.getRequiredField2());
        Assert.assertEquals(optionalField1, op.getOptionalField1());
        Assert.assertEquals(optionalField2, op.getOptionalField2());
        Assert.assertEquals(input, op.getInput());
    }

    @Test
    public void shouldValidateASingleMissingRequiredField() throws SerialisationException {
        // Given
        final String requiredField1 = "value1";
        final Date optionalField1 = new Date(1L);
        final CustomVertex optionalField2 = new CustomVertex("type2", "value2");
        final List<String> input = Arrays.asList("1", "2", "3", "4");
        final InputImpl op = input(input).build();
        // When
        final ValidationResult validationResult = validate();
        // Then
        Assert.assertTrue(validationResult.getErrorString().contains("requiredField2 is required"));
    }

    @Test
    public void shouldShallowCloneOperation() {
        // Given
        final String requiredField1 = "value1";
        final CustomVertex requiredField2 = new CustomVertex("type1", "value1");
        final Date optionalField1 = new Date(1L);
        final CustomVertex optionalField2 = new CustomVertex("type2", "value2");
        final List<String> input = Arrays.asList("1", "2", "3", "4");
        final InputImpl op = input(input).build();
        // When
        final InputImpl clone = ((InputImpl) (op.shallowClone()));
        // Then
        Assert.assertEquals(requiredField1, clone.getRequiredField1());
        Assert.assertEquals(requiredField2, clone.getRequiredField2());
        Assert.assertEquals(optionalField1, clone.getOptionalField1());
        Assert.assertEquals(optionalField2, clone.getOptionalField2());
        Assert.assertSame(input, clone.getInput());
    }
}

