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
package uk.gov.gchq.gaffer.rest.service.v2;


import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Properties;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.impl.GetWalks;
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
public class GraphConfigurationServiceV2Test {
    private static final String GRAPH_ID = "graphId";

    @InjectMocks
    private GraphConfigurationServiceV2 service;

    @Mock
    private GraphFactory graphFactory;

    @Mock
    private UserFactory userFactory;

    @Mock
    private Store store;

    @Test
    public void shouldReturnDescription() {
        // When
        final int status = service.getDescription().getStatus();
        final String description = service.getDescription().getEntity().toString();
        // Then
        Assert.assertEquals(200, status);
        Assert.assertEquals("test graph", description);
    }

    @Test
    public void shouldGetFilterFunctions() {
        // When
        final Set<Class> classes = ((Set<Class>) (service.getFilterFunction(null).getEntity()));
        // Then
        Assert.assertThat(classes, IsCollectionContaining.hasItem(IsA.class));
    }

    @Test
    public void shouldGetFilterFunctionsWithInputClass() {
        // When
        final Set<Class> classes = ((Set<Class>) (service.getFilterFunction(Long.class.getName()).getEntity()));
        // Then
        Assert.assertThat(classes, IsCollectionContaining.hasItem(IsLessThan.class));
        Assert.assertThat(classes, IsCollectionContaining.hasItem(IsMoreThan.class));
        Assert.assertThat(classes, IsCollectionContaining.hasItem(Not.class));
    }

    @Test
    public void shouldThrowExceptionWhenGetFilterFunctionsWithUnknownClassName() {
        // When / Then
        try {
            service.getFilterFunction("unknown className");
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Input class was not recognised:"));
        }
    }

    @Test
    public void shouldGetSerialisedFields() {
        // When
        final Set<String> fields = ((Set<String>) (service.getSerialisedFields(IsA.class.getName()).getEntity()));
        // Then
        Assert.assertEquals(1, fields.size());
        Assert.assertTrue(fields.contains("type"));
    }

    @Test
    public void shouldGetSerialisedFieldsForGetElementsClass() {
        // When
        final Set<String> fields = ((Set<String>) (service.getSerialisedFields(GetElements.class.getName()).getEntity()));
        final Set<String> expectedFields = new HashSet<>();
        expectedFields.add("input");
        expectedFields.add("view");
        expectedFields.add("includeIncomingOutGoing");
        expectedFields.add("seedMatching");
        expectedFields.add("options");
        expectedFields.add("directedType");
        expectedFields.add("views");
        // Then
        Assert.assertEquals(expectedFields, fields);
    }

    @Test
    public void shouldGetCorrectSerialisedFieldsForEdgeClass() {
        // When
        final Map<String, String> fields = ((Map<String, String>) (service.getSerialisedFieldClasses(Edge.class.getName()).getEntity()));
        final Map<String, String> expectedFields = new HashMap<>();
        expectedFields.put("class", Class.class.getName());
        expectedFields.put("source", Object.class.getName());
        expectedFields.put("destination", Object.class.getName());
        expectedFields.put("matchedVertex", String.class.getName());
        expectedFields.put("group", String.class.getName());
        expectedFields.put("properties", Properties.class.getName());
        expectedFields.put("directed", Boolean.class.getName());
        expectedFields.put("directedType", String.class.getName());
        // Then
        Assert.assertEquals(expectedFields, fields);
    }

    @Test
    public void shouldGetCorrectSerialisedFieldsForGetWalksClass() {
        // When
        final Map<String, String> fields = ((Map<String, String>) (service.getSerialisedFieldClasses(GetWalks.class.getName()).getEntity()));
        final Map<String, String> expectedFields = new HashMap<>();
        expectedFields.put("operations", "java.util.List<uk.gov.gchq.gaffer.operation.io.Output<java.lang.Iterable<uk.gov.gchq.gaffer.data.element.Element>>>");
        expectedFields.put("input", "java.lang.Object[]");
        expectedFields.put("options", "java.util.Map<java.lang.String,java.lang.String>");
        expectedFields.put("resultsLimit", Integer.class.getName());
        // Then
        Assert.assertEquals(expectedFields, fields);
    }

    @Test
    public void shouldThrowExceptionWhenGetSerialisedFieldsWithUnknownClassName() {
        // When / Then
        try {
            service.getSerialisedFields("unknown className");
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
            Assert.assertTrue(e.getMessage().contains("Class name was not recognised:"));
        }
    }

    @Test
    public void shouldGetStoreTraits() {
        // When
        final Set<StoreTrait> traits = ((Set<StoreTrait>) (service.getStoreTraits().getEntity()));
        // Then
        Assert.assertNotNull(traits);
        Assert.assertEquals("Collection size should be 6", 6, traits.size());
        Assert.assertTrue("Collection should contain INGEST_AGGREGATION trait", traits.contains(INGEST_AGGREGATION));
        Assert.assertTrue("Collection should contain PRE_AGGREGATION_FILTERING trait", traits.contains(PRE_AGGREGATION_FILTERING));
        Assert.assertTrue("Collection should contain POST_AGGREGATION_FILTERING trait", traits.contains(POST_AGGREGATION_FILTERING));
        Assert.assertTrue("Collection should contain POST_TRANSFORMATION_FILTERING trait", traits.contains(POST_TRANSFORMATION_FILTERING));
        Assert.assertTrue("Collection should contain TRANSFORMATION trait", traits.contains(TRANSFORMATION));
        Assert.assertTrue("Collection should contain STORE_VALIDATION trait", traits.contains(STORE_VALIDATION));
    }

    @Test
    public void shouldGetTransformFunctions() {
        // When
        final Set<Class> classes = ((Set<Class>) (service.getTransformFunctions().getEntity()));
        // Then
        Assert.assertFalse(classes.isEmpty());
    }

    @Test
    public void shouldGetElementGenerators() {
        // When
        final Set<Class> classes = ((Set<Class>) (service.getElementGenerators().getEntity()));
        // Then
        Assert.assertFalse(classes.isEmpty());
    }

    @Test
    public void shouldGetObjectGenerators() {
        // When
        final Set<Class> classes = ((Set<Class>) (service.getObjectGenerators().getEntity()));
        // Then
        Assert.assertFalse(classes.isEmpty());
    }

    @Test
    public void shouldSerialiseAndDeserialiseGetStoreTraits() throws SerialisationException {
        // When
        byte[] bytes = JSONSerialiser.serialise(service.getStoreTraits().getEntity());
        final Set<String> traits = JSONSerialiser.deserialise(bytes, Set.class);
        // Then
        Assert.assertEquals(Sets.newHashSet(INGEST_AGGREGATION.name(), PRE_AGGREGATION_FILTERING.name(), POST_AGGREGATION_FILTERING.name(), POST_TRANSFORMATION_FILTERING.name(), TRANSFORMATION.name(), STORE_VALIDATION.name()), traits);
    }
}

