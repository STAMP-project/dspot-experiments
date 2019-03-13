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
package uk.gov.gchq.gaffer.operation.function;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;


public class ToEntityIdTest {
    @Test
    public void shouldReturnNullIfTheInputIsNull() {
        // Given
        final Object input = null;
        final ToEntityId function = new ToEntityId();
        // When
        final EntityId output = function.apply(input);
        // Then
        Assert.assertNull(output);
    }

    @Test
    public void shouldReturnEntitySeedIfInputIsAnObject() {
        // Given
        final Object input = "item";
        final ToEntityId function = new ToEntityId();
        // When
        final EntityId output = function.apply(input);
        // Then
        Assert.assertEquals(new EntitySeed(input), output);
    }

    @Test
    public void shouldReturnOriginalValueIfInputIsAnEntitySeed() {
        // Given
        final EntitySeed input = new EntitySeed("item");
        final ToEntityId function = new ToEntityId();
        // When
        final EntityId output = function.apply(input);
        // Then
        Assert.assertSame(input, output);
    }

    @Test
    public void shouldReturnOriginalValueIfInputIsAnEntity() {
        // Given
        final Entity input = new Entity("group", "item");
        final ToEntityId function = new ToEntityId();
        // When
        final EntityId output = function.apply(input);
        // Then
        Assert.assertSame(input, output);
    }
}

