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


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.GroupCounts;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.CountGroups;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;


public class CountGroupsHandlerTest {
    private static final String GROUP1 = "GROUP1";

    private static final String GROUP2 = "GROUP2";

    @Test
    public void shouldReturnNoCountsIfElementsAreNull() throws IOException, OperationException {
        // Given
        final CountGroupsHandler handler = new CountGroupsHandler();
        final Store store = Mockito.mock(Store.class);
        final CountGroups countGroups = Mockito.mock(CountGroups.class);
        final Context context = new Context();
        BDDMockito.given(countGroups.getInput()).willReturn(null);
        // When
        final GroupCounts counts = handler.doOperation(countGroups, context, store);
        // Then
        Assert.assertFalse(counts.isLimitHit());
        Assert.assertEquals(0, counts.getEntityGroups().size());
        Assert.assertEquals(0, counts.getEdgeGroups().size());
        Mockito.verify(countGroups).close();
    }

    @Test
    public void shouldReturnGroupCountsWithoutLimit() throws IOException, OperationException {
        // Given
        final CountGroupsHandler handler = new CountGroupsHandler();
        final Store store = Mockito.mock(Store.class);
        final CountGroups countGroups = Mockito.mock(CountGroups.class);
        final CloseableIterable elements = CountGroupsHandlerTest.getElements();
        final Context context = new Context();
        BDDMockito.given(countGroups.getLimit()).willReturn(null);
        BDDMockito.given(countGroups.getInput()).willReturn(elements);
        // When
        final GroupCounts counts = handler.doOperation(countGroups, context, store);
        // Then
        Assert.assertFalse(counts.isLimitHit());
        Assert.assertEquals(2, counts.getEntityGroups().size());
        Assert.assertEquals(3, ((int) (counts.getEntityGroups().get(CountGroupsHandlerTest.GROUP1))));
        Assert.assertEquals(1, ((int) (counts.getEntityGroups().get(CountGroupsHandlerTest.GROUP2))));
        Assert.assertEquals(2, counts.getEdgeGroups().size());
        Assert.assertEquals(1, ((int) (counts.getEdgeGroups().get(CountGroupsHandlerTest.GROUP1))));
        Assert.assertEquals(3, ((int) (counts.getEdgeGroups().get(CountGroupsHandlerTest.GROUP2))));
        Mockito.verify(countGroups).close();
    }

    @Test
    public void shouldReturnAllGroupCountsWhenLessThanLimit() throws IOException, OperationException {
        // Given
        final CountGroupsHandler handler = new CountGroupsHandler();
        final Store store = Mockito.mock(Store.class);
        final CountGroups countGroups = Mockito.mock(CountGroups.class);
        final CloseableIterable elements = CountGroupsHandlerTest.getElements();
        final Integer limit = 10;
        final Context context = new Context();
        BDDMockito.given(countGroups.getLimit()).willReturn(limit);
        BDDMockito.given(countGroups.getInput()).willReturn(elements);
        // When
        final GroupCounts counts = handler.doOperation(countGroups, context, store);
        // Then
        Assert.assertFalse(counts.isLimitHit());
        Assert.assertEquals(2, counts.getEntityGroups().size());
        Assert.assertEquals(3, ((int) (counts.getEntityGroups().get(CountGroupsHandlerTest.GROUP1))));
        Assert.assertEquals(1, ((int) (counts.getEntityGroups().get(CountGroupsHandlerTest.GROUP2))));
        Assert.assertEquals(2, counts.getEdgeGroups().size());
        Assert.assertEquals(1, ((int) (counts.getEdgeGroups().get(CountGroupsHandlerTest.GROUP1))));
        Assert.assertEquals(3, ((int) (counts.getEdgeGroups().get(CountGroupsHandlerTest.GROUP2))));
        Mockito.verify(countGroups).close();
    }

    @Test
    public void shouldReturnGroupCountsUpToLimit() throws IOException, OperationException {
        // Given
        final CountGroupsHandler handler = new CountGroupsHandler();
        final Store store = Mockito.mock(Store.class);
        final CountGroups countGroups = Mockito.mock(CountGroups.class);
        final CloseableIterable elements = CountGroupsHandlerTest.getElements();
        final Integer limit = 3;
        final Context context = new Context();
        BDDMockito.given(countGroups.getLimit()).willReturn(limit);
        BDDMockito.given(countGroups.getInput()).willReturn(elements);
        // When
        final GroupCounts counts = handler.doOperation(countGroups, context, store);
        // Then
        Assert.assertTrue(counts.isLimitHit());
        Assert.assertEquals(2, counts.getEntityGroups().size());
        Assert.assertEquals(2, ((int) (counts.getEntityGroups().get(CountGroupsHandlerTest.GROUP1))));
        Assert.assertEquals(1, ((int) (counts.getEntityGroups().get(CountGroupsHandlerTest.GROUP2))));
        Mockito.verify(countGroups).close();
    }
}

