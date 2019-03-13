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
package uk.gov.gchq.gaffer.federatedstore.operation;


import FederatedGraphStorage.GRAPH_IDS_NOT_VISIBLE;
import FederatedStoreConstants.KEY_OPERATION_OPTIONS_GRAPH_IDS;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.federatedstore.FederatedStore;
import uk.gov.gchq.gaffer.federatedstore.FederatedStoreProperties;
import uk.gov.gchq.gaffer.federatedstore.schema.FederatedViewValidator;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.ViewValidator;
import uk.gov.gchq.gaffer.user.User;


public class FederatedOperationChainValidatorTest {
    @Test
    public void shouldGetFederatedSchema() {
        // Given
        final ViewValidator viewValidator = Mockito.mock(FederatedViewValidator.class);
        final FederatedOperationChainValidator validator = new FederatedOperationChainValidator(viewValidator);
        final FederatedStore store = Mockito.mock(FederatedStore.class);
        final User user = Mockito.mock(User.class);
        final Operation op = Mockito.mock(Operation.class);
        final Schema schema = Mockito.mock(Schema.class);
        BDDMockito.given(store.getSchema(op, user)).willReturn(schema);
        // When
        final Schema actualSchema = validator.getSchema(op, user, store);
        // Then
        Assert.assertSame(schema, actualSchema);
    }

    @Test
    public void shouldNotErrorWithInvalidViewFromMissingGraph() throws OperationException {
        // given
        String missingGraph = "missingGraph";
        final Graph graph = new Graph.Builder().addStoreProperties(new FederatedStoreProperties()).config(new GraphConfig.Builder().graphId("testFedGraph").build()).build();
        try {
            // when
            graph.execute(new GetAllElements.Builder().view(new View.Builder().entity("missingEntity").build()).option(KEY_OPERATION_OPTIONS_GRAPH_IDS, missingGraph).build(), new Context());
            Assert.fail("exception expected");
        } catch (final IllegalArgumentException e) {
            // then
            Assert.assertEquals(String.format(GRAPH_IDS_NOT_VISIBLE, Lists.newArrayList(missingGraph)), e.getMessage());
        }
    }
}

