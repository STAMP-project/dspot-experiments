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
package uk.gov.gchq.gaffer.store.operation.handler;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.operation.GetSchema;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class GetSchemaHandlerTest {
    private GetSchemaHandler handler;

    private Schema schema;

    private Store store;

    private Context context;

    private User user;

    private StoreProperties properties;

    private byte[] compactSchemaBytes;

    @Test
    public void shouldReturnCompactSchema() throws OperationException {
        BDDMockito.given(store.getProperties()).willReturn(properties);
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(context.getUser()).willReturn(user);
        final GetSchema operation = new GetSchema.Builder().compact(true).build();
        // When
        final Schema result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertNotNull(result);
        JsonAssert.assertNotEqual(schema.toJson(true), result.toJson(true));
        JsonAssert.assertEquals(compactSchemaBytes, result.toJson(true));
    }

    @Test
    public void shouldReturnFullSchema() throws OperationException {
        BDDMockito.given(store.getProperties()).willReturn(properties);
        BDDMockito.given(store.getOriginalSchema()).willReturn(schema);
        BDDMockito.given(context.getUser()).willReturn(user);
        final GetSchema operation = new GetSchema();
        // When
        final Schema result = handler.doOperation(operation, context, store);
        // Then
        Assert.assertNotNull(result);
        JsonAssert.assertEquals(schema.toJson(true), result.toJson(true));
    }

    @Test
    public void shouldThrowExceptionForNullOperation() throws OperationException {
        final GetSchema operation = null;
        // When / Then
        try {
            handler.doOperation(operation, context, store);
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().contains("Operation cannot be null"));
        }
    }
}

