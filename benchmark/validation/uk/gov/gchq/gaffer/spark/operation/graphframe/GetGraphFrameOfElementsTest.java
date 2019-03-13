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
package uk.gov.gchq.gaffer.spark.operation.graphframe;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.koryphe.ValidationResult;


public class GetGraphFrameOfElementsTest extends OperationTest<GetGraphFrameOfElements> {
    @Test
    public void shouldValidateOperationIfViewDoesNotContainEdgesOrEntities() {
        // Given
        final GetGraphFrameOfElements opWithEdgesOnly = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).build()).build();
        final GetGraphFrameOfElements opWithEntitiesOnly = new GetGraphFrameOfElements.Builder().view(new View.Builder().entity(ENTITY).build()).build();
        final GetGraphFrameOfElements opWithEmptyView = new GetGraphFrameOfElements.Builder().view(new View.Builder().build()).build();
        // Then
        Assert.assertTrue(opWithEdgesOnly.validate().isValid());
        Assert.assertTrue(opWithEntitiesOnly.validate().isValid());
        Assert.assertFalse(opWithEmptyView.validate().isValid());
    }

    @Test
    public void shouldValidateOperation() {
        // Given
        final Operation op = new GetGraphFrameOfElements.Builder().view(new View.Builder().edge(EDGE).entity(ENTITY).build()).build();
        // When
        final ValidationResult validationResult = op.validate();
        // Then
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void shouldValidateOperationWhenViewContainsElementsWithReservedPropertyNames() {
        // Given
        final Operation op = new GetGraphFrameOfElements.Builder().view(new View.Builder().entity(ENTITY, new ViewElementDefinition.Builder().properties("vertex").build()).edge(EDGE).build()).build();
        // When
        final ValidationResult validationResult = op.validate();
        // Then
        Assert.assertTrue(validationResult.isValid());
    }
}

