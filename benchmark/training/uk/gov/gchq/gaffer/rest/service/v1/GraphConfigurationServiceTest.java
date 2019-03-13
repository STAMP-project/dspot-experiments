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
package uk.gov.gchq.gaffer.rest.service.v1;


import java.io.IOException;
import java.util.Set;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.rest.factory.GraphFactory;
import uk.gov.gchq.gaffer.rest.factory.UserFactory;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.koryphe.impl.predicate.IsA;
import uk.gov.gchq.koryphe.impl.predicate.IsLessThan;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;
import uk.gov.gchq.koryphe.impl.predicate.Not;


@RunWith(MockitoJUnitRunner.class)
public class GraphConfigurationServiceTest {
    private static final String GRAPH_ID = "graphId";

    @InjectMocks
    private GraphConfigurationService service;

    @Mock
    private GraphFactory graphFactory;

    @Mock
    private UserFactory userFactory;

    @Mock
    private Store store;

    @Test
    public void shouldGetFilterFunctions() throws IOException {
        // When
        final Set<Class> classes = service.getFilterFunctions(null);
        // Then
        Assert.assertThat(classes, IsCollectionContaining.hasItem(IsA.class));
    }

    @Test
    public void shouldGetFilterFunctionsWithInputClass() throws IOException {
        // When
        final Set<Class> classes = service.getFilterFunctions(Long.class.getName());
        // Then
        Assert.assertThat(classes, IsCollectionContaining.hasItem(IsLessThan.class));
        Assert.assertThat(classes, IsCollectionContaining.hasItem(IsMoreThan.class));
        Assert.assertThat(classes, IsCollectionContaining.hasItem(Not.class));
    }

    @Test
    public void shouldThrowExceptionWhenGetFilterFunctionsWithUnknownClassName() throws IOException {
        // When / Then
        try {
            service.getFilterFunctions("an unknown class name");
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldGetSerialisedFields() throws IOException {
        // When
        final Set<String> fields = service.getSerialisedFields(IsA.class.getName());
        // Then
        Assert.assertEquals(1, fields.size());
        Assert.assertTrue(fields.contains("type"));
    }

    @Test
    public void shouldThrowExceptionWhenGetSerialisedFieldsWithUnknownClassName() throws IOException {
        // When / Then
        try {
            service.getSerialisedFields("an unknown class name");
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldGetNextOperations() throws IOException {
        // Given
        final Set<Class<? extends Operation>> expectedNextOperations = Mockito.mock(Set.class);
        BDDMockito.given(store.getNextOperations(GetElements.class)).willReturn(expectedNextOperations);
        // When
        final Set<Class> nextOperations = service.getNextOperations(GetElements.class.getName());
        // Then
        Assert.assertSame(expectedNextOperations, nextOperations);
    }

    @Test
    public void shouldThrowExceptionWhenGetNextOperationsWithUnknownClassName() throws IOException {
        // When / Then
        try {
            service.getNextOperations("an unknown class name");
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Operation class was not found"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenGetNextOperationsWithNonOperationClassName() throws IOException {
        // When / Then
        try {
            service.getNextOperations(String.class.getName());
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("does not extend Operation"));
        }
    }

    @Test
    public void shouldGetStoreTraits() throws IOException {
        // When
        final Set<StoreTrait> traits = service.getStoreTraits();
        // Then
        Assert.assertNotNull(traits);
        Assert.assertTrue("Collection size should be 6", ((traits.size()) == 6));
        Assert.assertTrue("Collection should contain INGEST_AGGREGATION trait", traits.contains(INGEST_AGGREGATION));
        Assert.assertTrue("Collection should contain PRE_AGGREGATION_FILTERING trait", traits.contains(PRE_AGGREGATION_FILTERING));
        Assert.assertTrue("Collection should contain POST_AGGREGATION_FILTERING trait", traits.contains(POST_AGGREGATION_FILTERING));
        Assert.assertTrue("Collection should contain POST_TRANSFORMATION_FILTERING trait", traits.contains(POST_TRANSFORMATION_FILTERING));
        Assert.assertTrue("Collection should contain TRANSFORMATION trait", traits.contains(TRANSFORMATION));
        Assert.assertTrue("Collection should contain STORE_VALIDATION trait", traits.contains(STORE_VALIDATION));
    }

    @Test
    public void shouldGetTransformFunctions() throws IOException {
        // When
        final Set<Class> classes = service.getTransformFunctions();
        // Then
        Assert.assertTrue((!(classes.isEmpty())));
    }

    @Test
    public void shouldGetElementGenerators() throws IOException {
        // When
        final Set<Class> classes = service.getElementGenerators();
        // Then
        Assert.assertTrue((!(classes.isEmpty())));
    }

    @Test
    public void shouldGetObjectGenerators() throws IOException {
        // When
        final Set<Class> classes = service.getObjectGenerators();
        // Then
        Assert.assertTrue((!(classes.isEmpty())));
    }

    @Test
    public void shouldGetAllAvailableOperations() throws IOException {
        // When
        final Set<Class> supportedOperations = service.getOperations();
        // Then
        Assert.assertTrue((!(supportedOperations.isEmpty())));
        Assert.assertEquals(1, supportedOperations.size());
    }

    @Test
    public void shouldValidateWhetherOperationIsSupported() throws IOException {
        // When
        final Set<Class> supportedOperations = service.getOperations();
        for (final Class<? extends Operation> operationClass : supportedOperations) {
            // Then
            Assert.assertTrue(service.isOperationSupported(operationClass));
        }
    }

    @Test
    public void shouldSerialiseAndDeserialiseGetStoreTraits() throws IOException {
        // When
        byte[] bytes = JSONSerialiser.serialise(service.getStoreTraits());
        final Set<StoreTrait> traits = JSONSerialiser.deserialise(bytes, Set.class);
        // Then
        Assert.assertNotNull(traits);
        Assert.assertTrue("Collection size should be 6", ((traits.size()) == 6));
        Assert.assertTrue("Collection should contain INGEST_AGGREGATION trait", traits.contains(INGEST_AGGREGATION.name()));
        Assert.assertTrue("Collection should contain PRE_AGGREGATION_FILTERING trait", traits.contains(PRE_AGGREGATION_FILTERING.name()));
        Assert.assertTrue("Collection should contain POST_AGGREGATION_FILTERING trait", traits.contains(POST_AGGREGATION_FILTERING.name()));
        Assert.assertTrue("Collection should contain POST_TRANSFORMATION_FILTERING trait", traits.contains(POST_TRANSFORMATION_FILTERING.name()));
        Assert.assertTrue("Collection should contain TRANSFORMATION trait", traits.contains(TRANSFORMATION.name()));
        Assert.assertTrue("Collection should contain STORE_VALIDATION trait", traits.contains(STORE_VALIDATION.name()));
    }
}

