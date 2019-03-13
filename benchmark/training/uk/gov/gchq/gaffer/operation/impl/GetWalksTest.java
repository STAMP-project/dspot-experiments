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
package uk.gov.gchq.gaffer.operation.impl;


import TestGroups.EDGE;
import com.google.common.collect.Sets;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.id.ElementId;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.koryphe.ValidationResult;


public class GetWalksTest extends OperationTest<GetWalks> {
    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // Given
        final GetWalks getWalks = new GetWalks.Builder().input(new EntitySeed("1"), new EntitySeed("2")).operations(new GetElements()).resultsLimit(100).build();
        // Then
        Assert.assertThat(getWalks.getInput(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(getWalks.getInput(), Matchers.iterableWithSize(2));
        Assert.assertThat(getWalks.getResultsLimit(), Matchers.is(CoreMatchers.equalTo(100)));
        Assert.assertThat(getWalks.getOperations(), Matchers.iterableWithSize(1));
        Assert.assertThat(getWalks.getInput(), Matchers.containsInAnyOrder(new EntitySeed("1"), new EntitySeed("2")));
    }

    @Test
    public void shouldFailValidationWithNoHops() {
        // Given
        final GetWalks operation = getTestObject();
        // When
        ValidationResult result = operation.validate();
        Set<String> expectedErrors = Sets.newHashSet(("No hops were provided. " + (GetWalks.HOP_DEFINITION)));
        // Then
        Assert.assertEquals(result.getErrors(), expectedErrors);
    }

    @Test
    public void shouldValidateOperationWhenNoOperationsProvided() {
        // Given
        final GetWalks getWalks = new GetWalks.Builder().input(new EntitySeed("1"), new EntitySeed("2")).build();
        // Then
        Assert.assertFalse(getWalks.validate().isValid());
    }

    @Test
    public void shouldValidateOperationWhenSecondOperationContainsNonNullInput() {
        // Given
        final GetWalks getWalks = new GetWalks.Builder().input(new EntitySeed("1"), new EntitySeed("2")).addOperations(new GetElements.Builder().view(new View.Builder().edge(EDGE).build()).build(), new GetElements.Builder().input(new EntitySeed("seed")).view(new View.Builder().edge(EDGE).build()).build()).build();
        // Then
        Assert.assertFalse(getWalks.validate().isValid());
    }

    @Test
    public void shouldValidateOperationWhenFirstOperationContainsNonNullInput() {
        // Given
        final GetWalks getWalks = new GetWalks.Builder().input(new EntitySeed("1"), new EntitySeed("2")).operations(new GetElements.Builder().input(new EntitySeed("some value")).view(new View.Builder().edge(EDGE).build()).build()).build();
        // Then
        Assert.assertFalse(getWalks.validate().isValid());
    }

    @Test
    public void shouldValidateWhenPreFiltersContainsAnOperationWhichDoesNotAllowAnInput() {
        // Given
        final GetWalks getWalks = new GetWalks.Builder().input(new EntitySeed("1"), new EntitySeed("2")).operations(new OperationChain.Builder().first(new ScoreOperationChain()).then(new DiscardOutput()).then(new GetElements.Builder().input().view(new View.Builder().edge(EDGE).build()).build()).build()).build();
        // Then
        final ValidationResult result = getWalks.validate();
        Assert.assertFalse(result.isValid());
        Assert.assertTrue(result.getErrorString(), result.getErrorString().contains((("The first operation in operation chain 0: " + (ScoreOperationChain.class.getName())) + " is not be able to accept the input seeds.")));
    }

    @Test
    public void shouldValidateWhenOperationListContainsAnEmptyOperationChain() {
        // Given
        final GetWalks getWalks = new GetWalks.Builder().input(new EntitySeed("1"), new EntitySeed("2")).operations(new GetElements.Builder().input(((Iterable<? extends ElementId>) (null))).view(new View.Builder().edge(EDGE).build()).build(), new OperationChain()).build();
        // Then
        final ValidationResult result = getWalks.validate();
        Assert.assertFalse(result.isValid());
        Assert.assertTrue(result.getErrorString(), result.getErrorString().contains("Operation chain 1 contains no operations"));
    }

    @Test
    public void shouldValidateWhenOperationListDoesNotContainAGetElementsOperation() {
        // Given
        final GetWalks getWalks = new GetWalks.Builder().input(new EntitySeed("1"), new EntitySeed("2")).operations(new GetElements.Builder().input(((Iterable<? extends ElementId>) (null))).view(new View.Builder().edge(EDGE).build()).build(), new OperationChain.Builder().first(new AddElements()).build(), new GetElements.Builder().input(((Iterable<? extends ElementId>) (null))).view(new View.Builder().edge(EDGE).build()).build()).build();
        // Then
        final ValidationResult result = getWalks.validate();
        Assert.assertFalse(result.isValid());
        Assert.assertTrue(result.getErrorString(), result.getErrorString().contains("All operations must contain a single hop. Operation 1 does not contain a hop."));
    }

    @Test
    public void shouldValidateWhenOperationContainsMultipleHops() {
        // Given
        final GetWalks getWalks = new GetWalks.Builder().input(new EntitySeed("1"), new EntitySeed("2")).operations(new OperationChain.Builder().first(new GetElements.Builder().input(((Iterable<? extends ElementId>) (null))).view(new View.Builder().edge(EDGE).build()).build()).then(new GetElements.Builder().input(((Iterable<? extends ElementId>) (null))).view(new View.Builder().edge(EDGE).build()).build()).build()).build();
        // Then
        final ValidationResult result = getWalks.validate();
        Assert.assertFalse(result.isValid());
        Assert.assertTrue(result.getErrorString(), ((result.getErrorString().contains("All operations must contain a single hop. Operation ")) && (result.getErrorString().contains(" contains multiple hops"))));
    }
}

