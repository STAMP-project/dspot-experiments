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
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;


public class GetElementsBetweenSetsHandlerTest {
    // Query for all edges between the set {A0} and the set {A23}
    private final List<EntityId> inputA = Collections.singletonList(new EntitySeed("A0"));

    private final List<EntityId> inputB = Collections.singletonList(new EntitySeed("A23"));

    private static View defaultView;

    private static AccumuloStore byteEntityStore;

    private static AccumuloStore gaffer1KeyStore;

    private static final Schema schema = Schema.fromJson(StreamUtil.schemas(GetElementsBetweenSetsHandlerTest.class));

    private static final AccumuloProperties PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.storeProps(GetElementsBetweenSetsHandlerTest.class));

    private static final AccumuloProperties CLASSIC_PROPERTIES = AccumuloProperties.loadStoreProperties(StreamUtil.openStream(GetElementsBetweenSetsHandlerTest.class, "/accumuloStoreClassicKeys.properties"));

    private static final Element expectedEdge1 = new Edge.Builder().group(EDGE).source("A0").dest("A23").directed(true).build();

    private static final Element expectedEdge2 = new Edge.Builder().group(EDGE).source("A0").dest("A23").directed(true).build();

    private static final Element expectedEdge3 = new Edge.Builder().group(EDGE).source("A0").dest("A23").directed(true).build();

    private static final Element expectedEntity1 = new Entity.Builder().group(ENTITY).vertex("A0").build();

    private static final Element expectedEntity1B = new Entity.Builder().group(ENTITY).vertex("A23").build();

    private static final Element expectedSummarisedEdge = new Edge.Builder().group(EDGE).source("A0").dest("A23").directed(true).build();

    private User user = new User();

    @Test
    public void shouldReturnElementsNoSummarisationByteEntityStore() throws OperationException {
        shouldReturnElementsNoSummarisation(GetElementsBetweenSetsHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldReturnElementsNoSummarisationGaffer1Store() throws OperationException {
        shouldReturnElementsNoSummarisation(GetElementsBetweenSetsHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnElementsNoSummarisationByteEntityStoreMatchedAsDestination() throws OperationException {
        shouldReturnElementsNoSummarisationMatchedAsDestination(GetElementsBetweenSetsHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldReturnElementsNoSummarisationGaffer1StoreMatchedAsDestination() throws OperationException {
        shouldReturnElementsNoSummarisationMatchedAsDestination(GetElementsBetweenSetsHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnSummarisedElementsByteEntityStore() throws OperationException {
        shouldReturnSummarisedElements(GetElementsBetweenSetsHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldReturnSummarisedElementsGaffer1Store() throws OperationException {
        shouldReturnSummarisedElements(GetElementsBetweenSetsHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnOnlyEdgesWhenOptionSetByteEntityStore() throws OperationException {
        shouldReturnOnlyEdgesWhenViewContainsNoEntities(GetElementsBetweenSetsHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldReturnOnlyEdgesWhenOptionSetGaffer1Store() throws OperationException {
        shouldReturnOnlyEdgesWhenViewContainsNoEntities(GetElementsBetweenSetsHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldReturnOnlyEntitiesWhenOptionSetByteEntityStore() throws OperationException {
        shouldReturnOnlyEntitiesWhenViewContainsNoEdges(GetElementsBetweenSetsHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldReturnOnlyEntitiesWhenOptionSetGaffer1Store() throws OperationException {
        shouldReturnOnlyEntitiesWhenViewContainsNoEdges(GetElementsBetweenSetsHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldSummariseOutGoingEdgesOnlyByteEntityStore() throws OperationException {
        shouldSummariseOutGoingEdgesOnly(GetElementsBetweenSetsHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldSummariseOutGoingEdgesOnlyGaffer1Store() throws OperationException {
        shouldSummariseOutGoingEdgesOnly(GetElementsBetweenSetsHandlerTest.gaffer1KeyStore);
    }

    @Test
    public void shouldHaveNoIncomingEdgesByteEntityStore() throws OperationException {
        shouldHaveNoIncomingEdges(GetElementsBetweenSetsHandlerTest.byteEntityStore);
    }

    @Test
    public void shouldHaveNoIncomingEdgesGaffer1Store() throws OperationException {
        shouldHaveNoIncomingEdges(GetElementsBetweenSetsHandlerTest.gaffer1KeyStore);
    }
}

