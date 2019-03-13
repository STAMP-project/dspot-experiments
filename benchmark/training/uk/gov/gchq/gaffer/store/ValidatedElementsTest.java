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
package uk.gov.gchq.gaffer.store;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.store.schema.Schema;


@RunWith(MockitoJUnitRunner.class)
public class ValidatedElementsTest {
    private List<Element> elements;

    private List<ElementFilter> filters;

    private Schema schema;

    @Test
    public void shouldCreateIteratorThatReturnsOnlyValidElements() {
        // Given
        final boolean skipInvalidElements = true;
        final ValidatedElements validElements = new ValidatedElements(elements, schema, skipInvalidElements);
        final Iterator<Element> itr = validElements.iterator();
        // When 1a
        final boolean hasNext1 = itr.hasNext();
        // Then 1a
        Assert.assertTrue(hasNext1);
        // When 1b
        final Element next1 = itr.next();
        // Then 1b
        Assert.assertSame(elements.get(0), next1);
        // When 2a / Then 2a
        final boolean hasNext2 = itr.hasNext();
        // Then 2a
        Assert.assertTrue(hasNext2);
        // When 2b
        final Element next2 = itr.next();
        // Then 2b
        Assert.assertSame(elements.get(2), next2);
    }

    @Test
    public void shouldCreateIteratorThatThrowsExceptionOnInvalidElement() {
        // Given
        final boolean skipInvalidElements = false;
        final ValidatedElements validElements = new ValidatedElements(elements, schema, skipInvalidElements);
        final Iterator<Element> itr = validElements.iterator();
        // When 1a
        final boolean hasNext1 = itr.hasNext();
        // Then 1a
        Assert.assertTrue(hasNext1);
        // When 1b
        final Element next1 = itr.next();
        // Then 1b
        Assert.assertSame(elements.get(0), next1);
        // When 2a / Then 2a
        try {
            itr.hasNext();
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Some error"));
        }
        Mockito.verify(filters.get(2), Mockito.never()).test(elements.get(2));
    }

    @Test
    public void shouldThrowExceptionIfNextCalledWhenNoNextElement() {
        // Given
        final boolean skipInvalidElements = true;
        final ValidatedElements validElements = new ValidatedElements(elements, schema, skipInvalidElements);
        final Iterator<Element> itr = validElements.iterator();
        // When 1
        final Element next0 = itr.next();
        final Element next1 = itr.next();
        // Then 1
        Assert.assertSame(elements.get(0), next0);
        Assert.assertSame(elements.get(2), next1);
        // When 2 / Then 2
        try {
            itr.next();
            Assert.fail("Exception expected");
        } catch (final NoSuchElementException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void shouldThrowExceptionIfRemoveCalled() {
        // Given
        final boolean skipInvalidElements = true;
        final ValidatedElements validElements = new ValidatedElements(elements, schema, skipInvalidElements);
        final Iterator<Element> itr = validElements.iterator();
        // When / Then
        try {
            itr.remove();
            Assert.fail("Exception expected");
        } catch (final UnsupportedOperationException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void shouldNotThrowStackOverflowExceptionOnSkipInvalid() {
        final Set<Element> x = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            x.add(new Entity("G", ("" + i)));
        }
        final ValidatedElements ve = new ValidatedElements(x, new View.Builder().build(), true);
        Iterator<Element> it = ve.iterator();
        try {
            while (it.hasNext()) {
                it.next();
            } 
        } catch (final StackOverflowError ex) {
            Assert.fail("Unexpected StackOverflowError.");
        }
    }
}

