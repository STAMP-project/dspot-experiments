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
package uk.gov.gchq.gaffer.federatedstore.operation.handler.impl;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.operation.handler.FederatedTransformHandler;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.function.Transform;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.operation.handler.function.TransformHandler;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class FederatedTransformHandlerTest {
    @Test
    public void shouldDelegateToHandler() throws OperationException {
        // Given
        final FederatedStore store = Mockito.mock(FederatedStore.class);
        final TransformHandler handler = Mockito.mock(TransformHandler.class);
        final Transform op = Mockito.mock(Transform.class);
        final Context context = Mockito.mock(Context.class);
        final Iterable expectedResult = Mockito.mock(Iterable.class);
        final Schema schema = Mockito.mock(Schema.class);
        BDDMockito.given(store.getSchema(op, context)).willReturn(schema);
        BDDMockito.given(handler.doOperation(op, schema)).willReturn(expectedResult);
        final FederatedTransformHandler federatedHandler = new FederatedTransformHandler(handler);
        // When
        final Object result = federatedHandler.doOperation(op, context, store);
        // Then
        Assert.assertSame(expectedResult, result);
        Mockito.verify(handler).doOperation(op, schema);
    }
}

