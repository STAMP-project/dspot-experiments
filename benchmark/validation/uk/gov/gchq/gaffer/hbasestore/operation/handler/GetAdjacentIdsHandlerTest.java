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


import DirectedType.DIRECTED;
import GetAdjacentIdsHandler.ExtractDestinationEntityId;
import SeededGraphFilters.IncludeIncomingOutgoingType.INCOMING;
import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.hbasestore.HBaseStore;
import uk.gov.gchq.gaffer.hbasestore.retriever.HBaseRetriever;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.user.User;


public class GetAdjacentIdsHandlerTest {
    @Test
    public void shouldReturnHBaseRetriever() throws OperationException, StoreException {
        // Given
        final Iterable<EntityId> ids = Mockito.mock(Iterable.class);
        final Context context = Mockito.mock(Context.class);
        final User user = Mockito.mock(User.class);
        final HBaseStore store = Mockito.mock(HBaseStore.class);
        final HBaseRetriever<GetElements> hbaseRetriever = Mockito.mock(HBaseRetriever.class);
        final GetAdjacentIdsHandler handler = new GetAdjacentIdsHandler();
        final GetAdjacentIds getAdjacentIds = new GetAdjacentIds.Builder().inputIds(ids).option("option1", "optionValue").inOutType(INCOMING).directedType(DIRECTED).view(new View()).build();
        BDDMockito.given(context.getUser()).willReturn(user);
        final ArgumentCaptor<GetElements> getElementsCaptor = ArgumentCaptor.forClass(GetElements.class);
        BDDMockito.given(store.createRetriever(getElementsCaptor.capture(), ArgumentMatchers.eq(user), ArgumentMatchers.eq(ids), ArgumentMatchers.eq(true))).willReturn(hbaseRetriever);
        // When
        final GetAdjacentIdsHandler.ExtractDestinationEntityId result = ((GetAdjacentIdsHandler.ExtractDestinationEntityId) (handler.doOperation(getAdjacentIds, context, store)));
        // Then
        Assert.assertSame(hbaseRetriever, result.getInput());
        final GetElements getElements = getElementsCaptor.getValue();
        Assert.assertSame(ids, getElements.getInput());
        Assert.assertTrue(getElements.getView().getEntities().isEmpty());
        Assert.assertEquals(getAdjacentIds.getDirectedType(), getElements.getDirectedType());
        Assert.assertEquals(getAdjacentIds.getIncludeIncomingOutGoing(), getElements.getIncludeIncomingOutGoing());
        Assert.assertEquals("optionValue", getElements.getOption("option1"));
    }

    @Test
    public void shouldDoNothingIfNoSeedsProvided() throws OperationException {
        // Given
        final GetAdjacentIdsHandler handler = new GetAdjacentIdsHandler();
        final GetAdjacentIds getAdjacentIds = new GetAdjacentIds();
        final Context context = Mockito.mock(Context.class);
        final HBaseStore store = Mockito.mock(HBaseStore.class);
        // When
        final CloseableIterable<? extends EntityId> result = handler.doOperation(getAdjacentIds, context, store);
        // Then
        Assert.assertEquals(0, Iterables.size(result));
    }
}

