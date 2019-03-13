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
package uk.gov.gchq.gaffer.data.generator;


import java.util.Arrays;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.TransformIterable;
import uk.gov.gchq.gaffer.data.element.Element;


public class OneToOneElementGeneratorTest {
    private Element elm1;

    private Element elm2;

    private final String obj1 = "object 1";

    private final String obj2 = "object 2";

    @Test
    public void getElementShouldReturnGeneratedElement() {
        // Given
        final OneToOneElementGenerator<String> generator = new OneToOneElementGeneratorTest.OneToOneElementGeneratorImpl();
        // When
        final Element result = generator._apply(obj1);
        // Then
        Assert.assertSame(elm1, result);
    }

    @Test
    public void getObjectShouldReturnGeneratedObject() {
        // Given
        final OneToOneObjectGenerator<String> generator = new OneToOneElementGeneratorTest.OneToOneObjectGeneratorImpl();
        // When
        final String result = generator._apply(elm1);
        // Then
        Assert.assertSame(obj1, result);
    }

    @Test
    public void getObjectsShouldReturnGeneratedObjectTransformIterable() {
        // Given
        final OneToOneObjectGenerator<String> generator = new OneToOneElementGeneratorTest.OneToOneObjectGeneratorImpl();
        // When
        final TransformIterable<Element, String> result = ((TransformIterable<Element, String>) (generator.apply(Arrays.asList(elm1, elm2))));
        // Then
        final Iterator<String> itr = result.iterator();
        Assert.assertSame(obj1, itr.next());
        Assert.assertSame(obj2, itr.next());
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void getElementsShouldReturnGeneratedElementTransformIterable() {
        // Given
        final OneToOneElementGenerator<String> generator = new OneToOneElementGeneratorTest.OneToOneElementGeneratorImpl();
        // When
        final TransformIterable<String, Element> result = ((TransformIterable<String, Element>) (generator.apply(Arrays.asList(obj1, obj2))));
        // Then
        final Iterator<Element> itr = result.iterator();
        Assert.assertSame(elm1, itr.next());
        Assert.assertSame(elm2, itr.next());
        Assert.assertFalse(itr.hasNext());
    }

    private class OneToOneElementGeneratorImpl implements OneToOneElementGenerator<String> {
        @Override
        public Element _apply(final String item) {
            if (obj1.equals(item)) {
                return elm1;
            }
            if (obj2.equals(item)) {
                return elm2;
            }
            return null;
        }
    }

    private class OneToOneObjectGeneratorImpl implements OneToOneObjectGenerator<String> {
        @Override
        public String _apply(final Element element) {
            if ((elm1) == element) {
                return obj1;
            }
            if ((elm2) == element) {
                return obj2;
            }
            return null;
        }
    }
}

