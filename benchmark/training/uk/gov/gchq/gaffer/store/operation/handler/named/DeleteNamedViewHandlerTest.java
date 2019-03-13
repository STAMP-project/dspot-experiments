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
package uk.gov.gchq.gaffer.store.operation.handler.named;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewParameterDetail;
import uk.gov.gchq.gaffer.named.operation.cache.exception.CacheOperationFailedException;
import uk.gov.gchq.gaffer.named.view.AddNamedView;
import uk.gov.gchq.gaffer.named.view.DeleteNamedView;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.operation.handler.named.cache.NamedViewCache;
import uk.gov.gchq.gaffer.user.User;


public class DeleteNamedViewHandlerTest {
    private static final String WRITE_ACCESS_ROLE = "writeRole";

    private final NamedViewCache namedViewCache = new NamedViewCache();

    private final AddNamedViewHandler addNamedViewHandler = new AddNamedViewHandler(namedViewCache);

    private final DeleteNamedViewHandler deleteNamedViewHandler = new DeleteNamedViewHandler(namedViewCache);

    private final String testNamedViewName = "testNamedViewName";

    private final String invalidNamedViewName = "invalidNamedViewName";

    private final String testUserId = "testUser";

    private final Map<String, ViewParameterDetail> testParameters = new HashMap<>();

    private final StoreProperties properties = new StoreProperties();

    private final Context context = new Context(new User.Builder().userId(testUserId).opAuth(DeleteNamedViewHandlerTest.WRITE_ACCESS_ROLE).build());

    private final Store store = Mockito.mock(Store.class);

    private View view;

    private AddNamedView addNamedView;

    @Test
    public void shouldDeleteNamedViewCorrectly() throws CacheOperationFailedException, OperationException {
        Assert.assertTrue(cacheContains(testNamedViewName));
        // Given
        final DeleteNamedView deleteNamedView = new DeleteNamedView.Builder().name(testNamedViewName).build();
        // When
        deleteNamedViewHandler.doOperation(deleteNamedView, context, store);
        // Then
        Assert.assertFalse(cacheContains(testNamedViewName));
    }

    @Test
    public void shouldNotThrowExceptionWhenNoNamedViewToDelete() throws CacheOperationFailedException, OperationException {
        Assert.assertTrue(cacheContains(testNamedViewName));
        // Given
        final DeleteNamedView deleteInvalidNamedView = new DeleteNamedView.Builder().name(invalidNamedViewName).build();
        // When
        deleteNamedViewHandler.doOperation(deleteInvalidNamedView, context, store);
        // Then
        Assert.assertTrue(cacheContains(testNamedViewName));
    }
}

