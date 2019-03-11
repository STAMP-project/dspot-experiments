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
package uk.gov.gchq.gaffer.store.operation.handler;


import java.util.Collections;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.Validate;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaElementDefinition;


public class ValidateHandlerTest {
    @Test
    public void shouldReturnNullIfElementsAreNull() throws OperationException {
        // Given
        final ValidateHandler handler = new ValidateHandler();
        final Store store = Mockito.mock(Store.class);
        final Validate validate = Mockito.mock(Validate.class);
        BDDMockito.given(validate.getInput()).willReturn(null);
        final Context context = new Context();
        // When
        final Iterable<? extends Element> result = handler.doOperation(validate, context, store);
        // Then
        Assert.assertNull(result);
    }

    @Test
    public void shouldValidatedElements() throws OperationException {
        // Given
        final ValidateHandler handler = new ValidateHandler();
        final Store store = Mockito.mock(Store.class);
        final Validate validate = Mockito.mock(Validate.class);
        final Element elm1 = Mockito.mock(Element.class);
        final Iterable elements = Collections.singletonList(elm1);
        final Schema schema = Mockito.mock(Schema.class);
        final Context context = new Context();
        BDDMockito.given(validate.getInput()).willReturn(elements);
        BDDMockito.given(validate.isSkipInvalidElements()).willReturn(false);
        BDDMockito.given(store.getSchema()).willReturn(schema);
        final String group = "group";
        BDDMockito.given(elm1.getGroup()).willReturn(group);
        final SchemaElementDefinition elementDef = Mockito.mock(SchemaElementDefinition.class);
        final ElementFilter validator = Mockito.mock(ElementFilter.class);
        BDDMockito.given(validator.test(elm1)).willReturn(true);
        BDDMockito.given(elementDef.getValidator(true)).willReturn(validator);
        BDDMockito.given(schema.getElement(group)).willReturn(elementDef);
        // When
        final Iterable<? extends Element> result = handler.doOperation(validate, context, store);
        // Then
        final Iterator<? extends Element> itr = result.iterator();
        final Element elm1Result = itr.next();
        Assert.assertSame(elm1, elm1Result);
        Assert.assertFalse(itr.hasNext());
    }
}

