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
package uk.gov.gchq.gaffer.operation.util;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.JSONSerialisationTest;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;


public class ConditionalTest extends JSONSerialisationTest<Conditional> {
    @Test
    public void shouldCloneTransformInShallowClone() {
        // Given
        final Operation transform = Mockito.mock(Operation.class);
        final Operation transformClone = Mockito.mock(Operation.class);
        BDDMockito.given(transform.shallowClone()).willReturn(transformClone);
        final Conditional conditional = new Conditional(new IsMoreThan(1), transform);
        // When
        final Conditional clone = conditional.shallowClone();
        // Then
        Assert.assertNotSame(conditional, clone);
        Assert.assertSame(transformClone, clone.getTransform());
        Mockito.verify(transform).shallowClone();
    }

    @Test
    public void shouldNotFailToShallowCloneWhenTransformIsNull() {
        // Given
        final Operation transform = null;
        final Conditional conditional = new Conditional(new IsMoreThan(1), transform);
        // When
        final Conditional clone = conditional.shallowClone();
        // Then
        Assert.assertNotSame(conditional, clone);
        Assert.assertNull(clone.getTransform());
    }
}

