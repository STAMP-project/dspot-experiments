/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.accumulostore.operation.handler;


import TestGroups.EDGE;
import TestGroups.ENTITY;
import java.util.Arrays;
import java.util.Set;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class GetElementsWithinSetHandlerTest {
    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(GetElementsWithinSetHandlerTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(GetElementsWithinSetHandlerTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(GetElementsWithinSetHandlerTest.class, "/accumuloStoreClassicKeys.properties"));

    private static View defaultView;

    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    private static Edge expectedEdge1 = new Edge.Builder().group(EDGE).source("A0").dest("A23").directed(true).build();

    private static Edge expectedEdge2 = new Edge.Builder().group(EDGE).source("A0").dest("A23").directed(true).build();

    private static Edge expectedEdge3 = new Edge.Builder().group(EDGE).source("A0").dest("A23").directed(true).build();

    private static Entity expectedEntity1 = new Entity.Builder().group(ENTITY).vertex("A0").build();

    private static Entity expectedEntity2 = new Entity.Builder().group(ENTITY).vertex("A23").build();

    private static Edge expectedSummarisedEdge = new Edge.Builder().group(EDGE).source("A0").dest("A23").directed(true).build();

    final Set<EntityId> seeds = new java.util.HashSet(Arrays.asList(new EntitySeed("A0"), new EntitySeed("A23")));

    private User user = new User();

    @Test
    public void shouldReturnElementsNoSummarisationByteEntityStore() throws OperationException {
        shouldReturnElementsNoSummarisation(GetElementsWithinSetHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldReturnElementsNoSummarisationGaffer1Store() throws OperationException {
        shouldReturnElementsNoSummarisation(GetElementsWithinSetHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldSummariseByteEntityStore() throws OperationException {
        shouldSummarise(GetElementsWithinSetHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldSummariseGaffer1Store() throws OperationException {
        shouldSummarise(GetElementsWithinSetHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnOnlyEdgesWhenViewContainsNoEntitiesByteEntityStore() throws OperationException {
        shouldReturnOnlyEdgesWhenViewContainsNoEntities(GetElementsWithinSetHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldReturnOnlyEdgesWhenViewContainsNoEntitiesGaffer1Store() throws OperationException {
        shouldReturnOnlyEdgesWhenViewContainsNoEntities(GetElementsWithinSetHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnOnlyEntitiesWhenViewContainsNoEdgesByteEntityStore() throws OperationException {
        shouldReturnOnlyEntitiesWhenViewContainsNoEdges(GetElementsWithinSetHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldReturnOnlyEntitiesWhenViewContainsNoEdgesGaffer1Store() throws OperationException {
        shouldReturnOnlyEntitiesWhenViewContainsNoEdges(GetElementsWithinSetHandlerTest.gaffer1KeyStore);
    }
}

