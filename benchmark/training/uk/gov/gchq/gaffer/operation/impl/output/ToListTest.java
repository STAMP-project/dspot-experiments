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
package uk.gov.gchq.gaffer.operation.impl.output;


import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class ToListTest extends OperationTest<ToList> {
    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // Given
        final ToList<String> toList = new ToList.Builder<String>().input("1", "2").build();
        // Then
        Assert.assertThat(toList.getInput(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(toList.getInput(), Matchers.iterableWithSize(2));
        Assert.assertThat(toList.getInput(), Matchers.containsInAnyOrder("1", "2"));
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(List.class, outputClass);
    }
}

