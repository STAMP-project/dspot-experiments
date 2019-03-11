/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.store.operation;


import TestGroups.ENTITY;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.comparison.ElementPropertyComparator;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.DiscardOutput;
import uk.gov.gchq.gaffer.operation.impl.compare.Max;
import uk.gov.gchq.gaffer.operation.impl.export.GetExports;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.output.ToVertices;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.ViewValidator;
import uk.gov.gchq.koryphe.ValidationResult;


public class OperationChainValidatorTest {
    @Test
    public void shouldValidateValidOperationChain() {
        validateOperationChain(new OperationChain(Arrays.asList(new GetElements.Builder().input(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build(), new GetElements.Builder().input(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build(), new ToVertices(), new GetAdjacentIds())), true);
    }

    @Test
    public void shouldInValidateNullElementDef() {
        // Given
        final ViewValidator viewValidator = Mockito.mock(ViewValidator.class);
        final OperationChainValidator validator = new OperationChainValidator(viewValidator);
        final Store store = Mockito.mock(Store.class);
        Schema schema = Mockito.mock(Schema.class);
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(schema.getElement(Mockito.anyString())).willReturn(null);
        Max max = new Max();
        max.setComparators(Lists.newArrayList(new ElementPropertyComparator.Builder().groups(ENTITY).property("property").build()));
        ValidationResult validationResult = new ValidationResult();
        // When
        validator.validateComparables(max, null, store, validationResult);
        // Then
        Assert.assertEquals(false, validationResult.isValid());
        Set<String> errors = validationResult.getErrors();
        Assert.assertEquals(1, errors.size());
        errors.contains(((Max.class.getName()) + " references BasicEntity group that does not exist in the schema"));
    }

    @Test
    public void shouldValidateOperationChainThatCouldBeValidBasedOnGenerics() {
        validateOperationChain(new OperationChain(Arrays.asList(new GetElements.Builder().input(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build(), new GetElements.Builder().input(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build(), new ToVertices(), new GenerateObjects.Builder<>().generator(( e) -> e).build(), new GetAdjacentIds())), true);
    }

    @Test
    public void shouldValidateExportOperationChain() {
        validateOperationChain(new OperationChain(Arrays.asList(new GetElements.Builder().input(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build(), new uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet(), new DiscardOutput(), new GetElements.Builder().input(new uk.gov.gchq.gaffer.commonutil.iterable.EmptyClosableIterable()).build(), new uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet(), new DiscardOutput(), new GetExports())), true);
    }

    @Test
    public void shouldValidateInvalidExportOperationChainWithoutDiscardOperation() {
        validateOperationChain(new OperationChain(// new DiscardOutput(),
        // new DiscardOutput(),
        // No input
        Arrays.asList(new GetElements(), new uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet(), new GetElements(), new uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet(), new GetExports())), false);
    }

    @Test
    public void shouldValidateInvalidOperationChainIterableNotAssignableFromMap() {
        validateOperationChain(new OperationChain(// Output is a map
        // Input is an iterable
        Arrays.asList(new GetElements(), new uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet(), new DiscardOutput(), new GetElements(), new uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet(), new DiscardOutput(), new GetExports(), new GetElements())), false);
    }

    @Test
    public void shouldValidateInvalidOperationChain() {
        validateOperationChain(new OperationChain(// Output is an Element
        // Input is an Iterable
        Arrays.asList(new GetElements(), new GetElements(), new ToVertices(), new GetElements(), new Max(), new GetElements())), false);
    }

    @Test
    public void shouldNotValidateInvalidOperationChain() {
        // Given
        Operation operation = Mockito.mock(Operation.class);
        BDDMockito.given(operation.validate()).willReturn(new ValidationResult("SparkContext is required"));
        OperationChain opChain = new OperationChain(operation);
        // When
        validateOperationChain(opChain, false);
        // Then
        Mockito.verify(operation).validate();
    }
}

