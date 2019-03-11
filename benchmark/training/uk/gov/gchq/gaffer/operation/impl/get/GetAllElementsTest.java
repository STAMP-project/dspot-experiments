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
package uk.gov.gchq.gaffer.operation.impl.get;


import DirectedType.EITHER;
import TestGroups.EDGE;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class GetAllElementsTest extends OperationTest<GetAllElements> {
    @Test
    public void shouldSetDirectedTypeToBoth() {
        // When
        final GetAllElements op = new GetAllElements.Builder().directedType(EITHER).build();
        // Then
        Assert.assertEquals(EITHER, op.getDirectedType());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(CloseableIterable.class, outputClass);
    }

    @Test
    public void shouldSetOptionToValue() {
        // When
        final GetAllElements op = new GetAllElements.Builder().option("key", "value").build();
        // Then
        Assert.assertThat(op.getOptions(), Is.is(IsNull.notNullValue()));
        Assert.assertThat(op.getOptions().get("key"), Is.is("value"));
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        GetAllElements getAllElements = new GetAllElements.Builder().view(new View.Builder().edge(EDGE).build()).build();
        Assert.assertNotNull(getAllElements.getView().getEdge(EDGE));
    }
}

