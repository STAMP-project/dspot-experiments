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
package uk.gov.gchq.gaffer.accumulostore.integration.delete;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestTypes.DIRECTED_EITHER;
import TestTypes.ID_STRING;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.Entity.Builder;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.io.Output;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.gaffer.user.User;


public abstract class AbstractDeletedElementsIT<OP extends Output<O>, O> {
    protected static final String[] VERTICES = new String[]{ "1", "2", "3" };

    @Test
    public void shouldNotReturnDeletedElements() throws Exception {
        // Given
        final AccumuloStore accStore = ((AccumuloStore) (AccumuloStore.createStore("graph1", new Schema.Builder().entity(ENTITY, new SchemaEntityDefinition.Builder().vertex(ID_STRING).build()).edge(EDGE, new SchemaEdgeDefinition.Builder().source(ID_STRING).destination(ID_STRING).directed(DIRECTED_EITHER).build()).type(ID_STRING, String.class).type(DIRECTED_EITHER, Boolean.class).build(), AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(getClass())))));
        final Graph graph = new Graph.Builder().store(accStore).build();
        final Entity entityToDelete = new Builder().group(ENTITY).vertex("1").build();
        final Edge edgeToDelete = new Edge.Builder().group(EDGE).source("1").dest("2").directed(true).build();
        final Entity entityToKeep = new Builder().group(ENTITY).vertex("2").build();
        final Edge edgeToKeep = new Edge.Builder().group(EDGE).source("2").dest("3").directed(true).build();
        final List<Element> elements = Arrays.asList(entityToDelete, entityToKeep, edgeToDelete, edgeToKeep);
        graph.execute(new AddElements.Builder().input(elements).build(), new User());
        final O resultBefore = graph.execute(createGetOperation(), new User());
        assertElements(((Iterable) (elements)), resultBefore);
        // When
        deleteElement(entityToDelete, accStore);
        deleteElement(edgeToDelete, accStore);
        // Then
        final O resultAfter = graph.execute(createGetOperation(), new User());
        assertElements(Arrays.asList(entityToKeep, edgeToKeep), resultAfter);
    }
}

