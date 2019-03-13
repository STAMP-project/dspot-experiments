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
package uk.gov.gchq.gaffer.hbasestore.operation.handler;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.hbasestore.HBaseStore;
import uk.gov.gchq.gaffer.hbasestore.coprocessor.processor.ElementDedupeFilterProcessor;
import uk.gov.gchq.gaffer.hbasestore.retriever.HBaseRetriever;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.user.User;


public class GetAllElementsHandlerTest {
    @Test
    public void shouldReturnHBaseRetriever() throws OperationException, StoreException {
        // Given
        final Context context = Mockito.mock(Context.class);
        final User user = Mockito.mock(User.class);
        final HBaseStore store = Mockito.mock(HBaseStore.class);
        final HBaseRetriever<GetAllElements> hbaseRetriever = Mockito.mock(HBaseRetriever.class);
        final GetAllElementsHandler handler = new GetAllElementsHandler();
        final GetAllElements getElements = new GetAllElements();
        BDDMockito.given(context.getUser()).willReturn(user);
        BDDMockito.given(store.createRetriever(getElements, user, null, false, ElementDedupeFilterProcessor.class)).willReturn(hbaseRetriever);
        // When
        final HBaseRetriever<GetAllElements> result = ((HBaseRetriever<GetAllElements>) (handler.doOperation(getElements, context, store)));
        // Then
        Assert.assertSame(hbaseRetriever, result);
    }
}

