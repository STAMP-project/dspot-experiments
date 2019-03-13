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


import SeedMatching.SeedMatchingType.EQUAL;
import SeedMatching.SeedMatchingType.RELATED;
import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.hbasestore.HBaseStore;
import uk.gov.gchq.gaffer.hbasestore.retriever.HBaseRetriever;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.user.User;


public class GetElementsHandlerTest {
    @Test
    public void shouldThrowExceptionIfAnOldOperationOptionIsUsed() throws OperationException, StoreException {
        // Given
        final Iterable<EntityId> ids = Mockito.mock(Iterable.class);
        final GetElementsHandler handler = new GetElementsHandler();
        final GetElements getElements = new GetElements.Builder().input(ids).option("hbasestore.operation.return_matched_id_as_edge_source", "true").build();
        // When / Then
        try {
            handler.doOperation(getElements, new Context(), null);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("return_matched_id_as_edge_source"));
        }
    }

    @Test
    public void shouldReturnHBaseRetrieverWithIncludeMatchedVertex() throws OperationException, StoreException {
        // Given
        final Iterable<EntityId> ids = Mockito.mock(Iterable.class);
        final Context context = Mockito.mock(Context.class);
        final User user = Mockito.mock(User.class);
        final HBaseStore store = Mockito.mock(HBaseStore.class);
        final HBaseRetriever<GetElements> hbaseRetriever = Mockito.mock(HBaseRetriever.class);
        final GetElementsHandler handler = new GetElementsHandler();
        final GetElements getElements = new GetElements.Builder().inputIds(ids).seedMatching(RELATED).build();
        BDDMockito.given(context.getUser()).willReturn(user);
        BDDMockito.given(store.createRetriever(getElements, user, ids, true)).willReturn(hbaseRetriever);
        // When
        final HBaseRetriever<GetElements> result = ((HBaseRetriever<GetElements>) (handler.doOperation(getElements, context, store)));
        // Then
        Assert.assertSame(hbaseRetriever, result);
    }

    @Test
    public void shouldReturnHBaseRetrieverWithIncludeMatchedVertexWhenSeedMatchingIsNull() throws OperationException, StoreException {
        // Given
        final Iterable<EntityId> ids = Mockito.mock(Iterable.class);
        final Context context = Mockito.mock(Context.class);
        final User user = Mockito.mock(User.class);
        final HBaseStore store = Mockito.mock(HBaseStore.class);
        final HBaseRetriever<GetElements> hbaseRetriever = Mockito.mock(HBaseRetriever.class);
        final GetElementsHandler handler = new GetElementsHandler();
        final GetElements getElements = new GetElements.Builder().inputIds(ids).seedMatching(null).build();
        BDDMockito.given(context.getUser()).willReturn(user);
        BDDMockito.given(store.createRetriever(getElements, user, ids, true)).willReturn(hbaseRetriever);
        // When
        final HBaseRetriever<GetElements> result = ((HBaseRetriever<GetElements>) (handler.doOperation(getElements, context, store)));
        // Then
        Assert.assertSame(hbaseRetriever, result);
    }

    @Test
    public void shouldReturnHBaseRetrieverWithoutIncludeMatchdxVertex() throws OperationException, StoreException {
        // Given
        final Iterable<EntityId> ids = Mockito.mock(Iterable.class);
        final Context context = Mockito.mock(Context.class);
        final User user = Mockito.mock(User.class);
        final HBaseStore store = Mockito.mock(HBaseStore.class);
        final HBaseRetriever<GetElements> hbaseRetriever = Mockito.mock(HBaseRetriever.class);
        final GetElementsHandler handler = new GetElementsHandler();
        final GetElements getElements = new GetElements.Builder().inputIds(ids).seedMatching(EQUAL).build();
        BDDMockito.given(context.getUser()).willReturn(user);
        BDDMockito.given(store.createRetriever(getElements, user, ids, false)).willReturn(hbaseRetriever);
        // When
        final HBaseRetriever<GetElements> result = ((HBaseRetriever<GetElements>) (handler.doOperation(getElements, context, store)));
        // Then
        Assert.assertSame(hbaseRetriever, result);
    }

    @Test
    public void shouldDoNothingIfNoSeedsProvided() throws OperationException {
        // Given
        final GetElementsHandler handler = new GetElementsHandler();
        final GetElements getElements = new GetElements();
        final Context context = Mockito.mock(Context.class);
        final HBaseStore store = Mockito.mock(HBaseStore.class);
        // When
        final CloseableIterable<? extends Element> result = handler.doOperation(getElements, context, store);
        // Then
        Assert.assertEquals(0, Iterables.size(result));
    }
}

