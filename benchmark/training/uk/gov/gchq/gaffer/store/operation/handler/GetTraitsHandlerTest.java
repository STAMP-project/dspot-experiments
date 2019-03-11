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
package uk.gov.gchq.gaffer.store.operation.handler;


import StoreTrait.INGEST_AGGREGATION;
import StoreTrait.QUERY_AGGREGATION;
import StoreTrait.STORE_VALIDATION;
import StoreTrait.VISIBILITY;
import com.google.common.collect.Sets;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.function.ElementAggregator;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.store.operation.GetTraits;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEntityDefinition;
import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;
import uk.gov.gchq.koryphe.impl.predicate.Exists;


public class GetTraitsHandlerTest {
    public static final String STORE_ID = "StoreId";

    public static final String STRING = "string";

    private Store store;

    private Set<StoreTrait> expectedTraits;

    private Schema string;

    @Test
    public void shouldGetTraitsForSchemaEmpty() throws Exception {
        // When
        final Set<StoreTrait> actual = getStoreTraits(new Schema());
        // Then
        final Set<StoreTrait> expected = Sets.newHashSet(this.expectedTraits);
        expected.remove(QUERY_AGGREGATION);
        expected.remove(STORE_VALIDATION);
        expected.remove(VISIBILITY);
        expected.remove(INGEST_AGGREGATION);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTraitsForSchemaWithGroupBy() throws Exception {
        // When
        final Set<StoreTrait> actual = getStoreTraits(new Schema.Builder().entity("e1", new SchemaEntityDefinition.Builder().groupBy("gb").vertex(GetTraitsHandlerTest.STRING).build()).merge(string).build());
        final Set<StoreTrait> expected = Sets.newHashSet(this.expectedTraits);
        expected.remove(STORE_VALIDATION);
        expected.remove(VISIBILITY);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTraitsForSchemaWithValidator() throws Exception {
        // When
        final Set<StoreTrait> actual = getStoreTraits(new Schema.Builder().entity("e1", new SchemaEntityDefinition.Builder().property("p1", GetTraitsHandlerTest.STRING).validator(new ElementFilter.Builder().select("p1").execute(new Exists()).build()).aggregate(false).vertex(GetTraitsHandlerTest.STRING).build()).merge(string).build());
        // Then
        final Set<StoreTrait> expected = Sets.newHashSet(this.expectedTraits);
        expected.remove(QUERY_AGGREGATION);
        expected.remove(VISIBILITY);
        expected.remove(INGEST_AGGREGATION);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTraitsForSchemaWithVisibility() throws Exception {
        // When
        final Set<StoreTrait> actual = getStoreTraits(new Schema.Builder().visibilityProperty(GetTraitsHandlerTest.STRING).build());
        // Then
        final Set<StoreTrait> expected = Sets.newHashSet(this.expectedTraits);
        expected.remove(QUERY_AGGREGATION);
        expected.remove(STORE_VALIDATION);
        expected.remove(INGEST_AGGREGATION);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTraitsForSchemaWithAggregatorAndGroupBy() throws Exception {
        // When
        final Set<StoreTrait> actual = getStoreTraits(new Schema.Builder().entity("e1", new SchemaEntityDefinition.Builder().property("p1", GetTraitsHandlerTest.STRING).vertex(GetTraitsHandlerTest.STRING).groupBy("p1").aggregator(new ElementAggregator.Builder().select("p1").execute(new StringConcat()).build()).build()).merge(string).build());
        // Then
        final Set<StoreTrait> expected = Sets.newHashSet(this.expectedTraits);
        expected.remove(STORE_VALIDATION);
        expected.remove(VISIBILITY);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldHaveAllTraitsForSupported() throws Exception {
        // Given
        store.initialise(GetTraitsHandlerTest.STORE_ID, new Schema(), new StoreProperties());
        // When
        Set<StoreTrait> traits = store.execute(new GetTraits.Builder().currentTraits(false).build(), new uk.gov.gchq.gaffer.store.Context(testUser()));
        // Then
        Assert.assertEquals(expectedTraits, traits);
    }
}

