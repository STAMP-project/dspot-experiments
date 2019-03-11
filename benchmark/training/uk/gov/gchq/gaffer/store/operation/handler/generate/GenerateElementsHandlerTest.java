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
package uk.gov.gchq.gaffer.store.operation.handler.generate;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterator;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.generator.ElementGenerator;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;


public class GenerateElementsHandlerTest {
    @Test
    public void shouldReturnElements() throws OperationException {
        // Given
        final GenerateElementsHandler<String> handler = new GenerateElementsHandler();
        final Store store = Mockito.mock(Store.class);
        final GenerateElements<String> operation = Mockito.mock(GenerateElements.class);
        final CloseableIterable<Element> elements = Mockito.mock(CloseableIterable.class);
        final ElementGenerator<String> elementGenerator = Mockito.mock(ElementGenerator.class);
        final CloseableIterable objs = Mockito.mock(CloseableIterable.class);
        final Context context = new Context();
        final CloseableIterator<Element> elementsIter = Mockito.mock(CloseableIterator.class);
        BDDMockito.given(elements.iterator()).willReturn(elementsIter);
        BDDMockito.given(elementGenerator.apply(objs)).willReturn(elements);
        BDDMockito.given(operation.getInput()).willReturn(objs);
        BDDMockito.given(operation.getElementGenerator()).willReturn(elementGenerator);
        // When
        final Iterable<? extends Element> result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertSame(elementsIter, result.iterator());
    }
}

