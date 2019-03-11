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
package uk.gov.gchq.gaffer.operation.impl.compare;


import TestGroups.ENTITY;
import java.util.stream.Collectors;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.stream.Streams;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.comparison.ElementPropertyComparator;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.impl.compare.Min.Builder;


public class MinTest extends OperationTest<Min> {
    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // Given
        final Min min = new Builder().input(new Entity.Builder().group(ENTITY).property("property", 1).build(), new Entity.Builder().group(ENTITY).property("property", 2).build()).comparators(new ElementPropertyComparator() {
            @Override
            public int compare(final Element e1, final Element e2) {
                return 0;
            }
        }).build();
        // Then
        Assert.assertThat(min.getInput(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(min.getInput(), Matchers.iterableWithSize(2));
        Assert.assertThat(Streams.toStream(min.getInput()).map(( e) -> e.getProperty("property")).collect(Collectors.toList()), Matchers.containsInAnyOrder(1, 2));
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(Element.class, outputClass);
    }
}

