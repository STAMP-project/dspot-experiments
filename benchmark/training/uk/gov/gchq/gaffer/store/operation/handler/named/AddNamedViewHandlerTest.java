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


import TestGroups.EDGE;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.elementdefinition.view.NamedView;
import uk.gov.gchq.gaffer.data.elementdefinition.view.NamedViewDetail;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewParameterDetail;
import uk.gov.gchq.gaffer.named.operation.cache.exception.CacheOperationFailedException;
import uk.gov.gchq.gaffer.named.view.AddNamedView;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.operation.handler.named.cache.NamedViewCache;
import uk.gov.gchq.gaffer.user.User;


public class AddNamedViewHandlerTest {
    private final NamedViewCache namedViewCache = new NamedViewCache();

    private final AddNamedViewHandler handler = new AddNamedViewHandler(namedViewCache);

    private final String testNamedViewName = "testNamedViewName";

    private final String testUserId = "testUser";

    private final Map<String, ViewParameterDetail> testParameters = new HashMap<>();

    private static final ViewParameterDetail TEST_PARAM_VALUE = new ViewParameterDetail.Builder().defaultValue(1L).description("Limit param").valueClass(Long.class).build();

    private Context context = new Context(new User.Builder().userId(testUserId).build());

    private Store store = Mockito.mock(Store.class);

    View view;

    AddNamedView addNamedView;

    @Test
    public void shouldAddNamedViewCorrectly() throws CacheOperationFailedException, OperationException {
        handler.doOperation(addNamedView, context, store);
        final NamedViewDetail result = namedViewCache.getNamedView(testNamedViewName);
        Assert.assertTrue(cacheContains(testNamedViewName));
        Assert.assertEquals(addNamedView.getName(), result.getName());
        Assert.assertEquals(new String(addNamedView.getView().toCompactJson()), result.getView());
        Assert.assertEquals(context.getUser().getUserId(), result.getCreatorId());
    }

    @Test
    public void shouldNotAddNamedViewWithNoName() throws OperationException {
        addNamedView.setName(null);
        try {
            handler.doOperation(addNamedView, context, store);
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().equals("NamedView name must be set and not empty"));
        }
    }

    @Test
    public void shouldNotAddNestedNamedView() throws OperationException {
        final NamedView nestedNamedView = new NamedView.Builder().name(((testNamedViewName) + 1)).edge(EDGE).build();
        addNamedView = new AddNamedView.Builder().name(testNamedViewName).view(nestedNamedView).overwrite(false).build();
        try {
            handler.doOperation(addNamedView, context, store);
        } catch (final OperationException e) {
            Assert.assertTrue(e.getMessage().equals("NamedView can not be nested within NamedView"));
        }
    }
}

