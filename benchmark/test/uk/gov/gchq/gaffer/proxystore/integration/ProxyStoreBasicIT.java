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
package uk.gov.gchq.gaffer.proxystore.integration;


import JobStatus.RUNNING;
import Status.INTERNAL_SERVER_ERROR;
import StoreTrait.VISIBILITY;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestPropertyNames.COUNT;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import TestPropertyNames.PROP_3;
import TestPropertyNames.PROP_4;
import com.google.common.collect.Iterables;
import java.util.Set;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.core.exception.Error;
import uk.gov.gchq.gaffer.core.exception.GafferWrappedErrorRuntimeException;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.jobtracker.JobDetail;
import uk.gov.gchq.gaffer.mapstore.MapStore;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobDetails;
import uk.gov.gchq.gaffer.rest.RestApiTestClient;
import uk.gov.gchq.gaffer.rest.service.v2.RestApiV2TestClient;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.user.User;


public class ProxyStoreBasicIT {
    private Graph graph;

    private static final RestApiTestClient client = new RestApiV2TestClient();

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    public static final User USER = new User();

    public static final Element[] DEFAULT_ELEMENTS = new Element[]{ new Entity.Builder().group(ENTITY).vertex("1").property(PROP_1, 1).property(PROP_2, 2).property(PROP_3, 3).property(PROP_4, 4).property(COUNT, 1).build(), new Entity.Builder().group(ENTITY).vertex("2").property(PROP_1, 1).property(PROP_2, 2).property(PROP_3, 3).property(PROP_4, 4).property(COUNT, 1).build(), new Edge.Builder().group(EDGE).source("1").dest("2").directed(true).property(PROP_1, 1).property(PROP_2, 2).property(PROP_3, 3).property(PROP_4, 4).property(COUNT, 1).build() };

    @Test
    public void shouldAddElementsAndGetAllElements() throws Exception {
        // Given
        addDefaultElements();
        // When - Get
        final CloseableIterable<? extends Element> results = graph.execute(new GetAllElements(), ProxyStoreBasicIT.USER);
        // Then
        Assert.assertEquals(ProxyStoreBasicIT.DEFAULT_ELEMENTS.length, Iterables.size(results));
        Assert.assertThat(((CloseableIterable<Element>) (results)), Matchers.hasItems(ProxyStoreBasicIT.DEFAULT_ELEMENTS));
    }

    @Test
    public void shouldAddElementsAndGetRelatedElements() throws Exception {
        // Given
        addDefaultElements();
        // When
        final GetElements getElements = new GetElements.Builder().view(new View.Builder().entity(ENTITY).build()).input(new EntitySeed("1")).build();
        CloseableIterable<? extends Element> results = graph.execute(getElements, ProxyStoreBasicIT.USER);
        // Then
        Assert.assertEquals(1, Iterables.size(results));
        Assert.assertThat(((CloseableIterable<Element>) (results)), IsCollectionContaining.hasItem(ProxyStoreBasicIT.DEFAULT_ELEMENTS[0]));
    }

    @Test
    public void shouldAddElementsViaAJob() throws Exception {
        // Add elements
        final AddElements add = new AddElements.Builder().input(ProxyStoreBasicIT.DEFAULT_ELEMENTS).build();
        JobDetail jobDetail = graph.executeJob(new OperationChain(add), ProxyStoreBasicIT.USER);
        // Wait until the job status is not RUNNING
        while (RUNNING.equals(jobDetail.getStatus())) {
            jobDetail = graph.execute(new GetJobDetails.Builder().jobId(jobDetail.getJobId()).build(), ProxyStoreBasicIT.USER);
            Thread.sleep(100);
        } 
        // Get elements
        final GetElements getElements = new GetElements.Builder().view(new View.Builder().entity(ENTITY).edge(EDGE).build()).input(new EntitySeed("1")).build();
        CloseableIterable<? extends Element> results = graph.execute(getElements, ProxyStoreBasicIT.USER);
        // Then
        Assert.assertEquals(2, Iterables.size(results));
        Assert.assertThat(((CloseableIterable<Element>) (results)), IsCollectionContaining.hasItem(ProxyStoreBasicIT.DEFAULT_ELEMENTS[0]));
        Assert.assertThat(((CloseableIterable<Element>) (results)), IsCollectionContaining.hasItem(ProxyStoreBasicIT.DEFAULT_ELEMENTS[2]));
    }

    @Test
    public void shouldCatchAndThrowUsefulErrorMessages() throws Exception {
        // Given
        addDefaultElements();
        // When / Then
        try {
            graph.execute(new OperationChain.Builder().first(new GetAllElements()).then(new uk.gov.gchq.gaffer.operation.impl.Limit(1, false)).then(new uk.gov.gchq.gaffer.operation.impl.output.ToList()).build(), ProxyStoreBasicIT.USER);
            Assert.fail("Exception expected");
        } catch (final GafferWrappedErrorRuntimeException e) {
            Assert.assertEquals(new Error.ErrorBuilder().simpleMessage("Limit of 1 exceeded.").status(INTERNAL_SERVER_ERROR).build(), e.getError());
        }
    }

    @Test
    public void shouldHaveAllOfDelegateStoreTraitsApartFromVisibility() {
        // Given
        final Set<StoreTrait> expectedTraits = new java.util.HashSet(MapStore.TRAITS);
        expectedTraits.remove(VISIBILITY);
        // When
        final Set<StoreTrait> storeTraits = graph.getStoreTraits();
        // Then
        Assert.assertEquals(expectedTraits, storeTraits);
    }

    @Test
    public void shouldNotErrorWithNonNullOptionsMapAndNullHandlerOption() throws Exception {
        final AddElements add = // any value to create a optionsMap
        new AddElements.Builder().input(ProxyStoreBasicIT.DEFAULT_ELEMENTS).option("Anything", "Value").build();
        graph.execute(add, ProxyStoreBasicIT.USER);
    }
}

