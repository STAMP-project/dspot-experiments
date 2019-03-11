/**
 * Copyright 2018-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.federatedstore.operation.handler;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.operation.handler.impl.FederatedAddGraphHandler;
import uk.gov.gchq.gaffer.federatedstore.operation.handler.impl.FederatedOperationIterableHandler;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;


public class AddGenericHandlerTest {
    private FederatedStore store;

    private Graph graph;

    @Test
    public void shouldHandleGetAllElements() throws Exception {
        BDDMockito.given(store.isSupported(ArgumentMatchers.any())).willReturn(true);
        BDDMockito.given(store.isSupported(GetAllElements.class)).willReturn(false);
        FederatedAddGraphHandler federatedAddGraphHandler = new FederatedAddGraphHandler();
        federatedAddGraphHandler.addGenericHandler(store, graph);
        Mockito.verify(store, Mockito.times(1)).addOperationHandler(ArgumentMatchers.eq(GetAllElements.class), ArgumentMatchers.any(FederatedOperationIterableHandler.class));
    }

    @Test
    public void shouldNotHandleAnything() throws Exception {
        BDDMockito.given(store.isSupported(ArgumentMatchers.any())).willReturn(true);
        FederatedAddGraphHandler federatedAddGraphHandler = new FederatedAddGraphHandler();
        federatedAddGraphHandler.addGenericHandler(store, graph);
        Mockito.verify(store, Mockito.never()).addOperationHandler(ArgumentMatchers.any(), ArgumentMatchers.any(FederatedOperationIterableHandler.class));
    }
}

