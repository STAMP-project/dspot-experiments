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
package uk.gov.gchq.gaffer.integration.impl;


import EdgeId.MatchedVertex.DESTINATION;
import TestGroups.EDGE;
import TestPropertyNames.COUNT;
import TestPropertyNames.INT;
import TestPropertyNames.SET;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.CollectionUtil;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.util.ElementUtil;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.domain.DomainObject;
import uk.gov.gchq.gaffer.integration.domain.EdgeDomainObject;
import uk.gov.gchq.gaffer.integration.domain.EntityDomainObject;
import uk.gov.gchq.gaffer.integration.generators.BasicElementGenerator;
import uk.gov.gchq.gaffer.integration.generators.BasicObjectGenerator;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EdgeSeed;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateElements;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;


public class GeneratorsIT extends AbstractStoreIT {
    private static final String NEW_SOURCE = "newSource";

    private static final String NEW_DEST = "newDest";

    private static final String NEW_VERTEX = "newVertex";

    @Test
    public void shouldConvertToDomainObjects() throws OperationException {
        // Given
        final OperationChain<Iterable<? extends DomainObject>> opChain = new OperationChain.Builder().first(new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_1)).build()).then(new GenerateObjects.Builder<DomainObject>().generator(new BasicObjectGenerator()).build()).build();
        // When
        final List<DomainObject> results = Lists.newArrayList(AbstractStoreIT.graph.execute(opChain, getUser()));
        final EntityDomainObject entityDomainObject = new EntityDomainObject(AbstractStoreIT.SOURCE_1, "3", null);
        final EdgeDomainObject edgeDomainObject = new EdgeDomainObject(AbstractStoreIT.SOURCE_1, AbstractStoreIT.DEST_1, false, 1, 1L);
        // Then
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        Assert.assertThat(results, IsCollectionContaining.hasItems(entityDomainObject, edgeDomainObject));
    }

    @Test
    public void shouldConvertFromDomainObjects() throws OperationException {
        // Given
        final OperationChain<Void> opChain = new OperationChain.Builder().first(new GenerateElements.Builder<DomainObject>().generator(new BasicElementGenerator()).input(new EntityDomainObject(GeneratorsIT.NEW_VERTEX, "1", null), new EdgeDomainObject(GeneratorsIT.NEW_SOURCE, GeneratorsIT.NEW_DEST, false, 1, 1L)).build()).then(new AddElements()).build();
        // When - add
        AbstractStoreIT.graph.execute(opChain, getUser());
        // Then - check they were added correctly
        final List<Element> results = Lists.newArrayList(AbstractStoreIT.graph.execute(new GetElements.Builder().input(new EntitySeed(GeneratorsIT.NEW_VERTEX), new EdgeSeed(GeneratorsIT.NEW_SOURCE, GeneratorsIT.NEW_DEST, false)).build(), getUser()));
        final Edge expectedEdge = new Edge.Builder().group(EDGE).source(GeneratorsIT.NEW_SOURCE).dest(GeneratorsIT.NEW_DEST).directed(false).matchedVertex(DESTINATION).build();
        expectedEdge.putProperty(INT, 1);
        expectedEdge.putProperty(COUNT, 1L);
        final Entity expectedEntity = new Entity(TestGroups.ENTITY, GeneratorsIT.NEW_VERTEX);
        expectedEntity.putProperty(SET, CollectionUtil.treeSet("1"));
        ElementUtil.assertElementEquals(Arrays.asList(expectedEntity, expectedEdge), results);
    }
}

