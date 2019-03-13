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
package uk.gov.gchq.gaffer.accumulostore.key.core;


import AccumuloStoreConstants.ACCUMULO_ELEMENT_CONVERTER_CLASS;
import AccumuloStoreConstants.ELEMENT_POST_AGGREGATION_FILTER_ITERATOR_NAME;
import AccumuloStoreConstants.ELEMENT_POST_AGGREGATION_FILTER_ITERATOR_PRIORITY;
import AccumuloStoreConstants.ELEMENT_PRE_AGGREGATION_FILTER_ITERATOR_NAME;
import AccumuloStoreConstants.ELEMENT_PRE_AGGREGATION_FILTER_ITERATOR_PRIORITY;
import AccumuloStoreConstants.SCHEMA;
import AccumuloStoreConstants.VALIDATOR_ITERATOR_NAME;
import AccumuloStoreConstants.VALIDATOR_ITERATOR_PRIORITY;
import AccumuloStoreConstants.VIEW;
import TestGroups.EDGE;
import TestPropertyNames.PROP_1;
import org.apache.accumulo.core.client.IteratorSetting;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.key.AccumuloElementConverter;
import uk.gov.gchq.gaffer.accumulostore.key.AccumuloKeyPackage;
import uk.gov.gchq.gaffer.accumulostore.key.impl.ElementPostAggregationFilter;
import uk.gov.gchq.gaffer.accumulostore.key.impl.ElementPreAggregationFilter;
import uk.gov.gchq.gaffer.accumulostore.key.impl.ValidatorFilter;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.koryphe.impl.predicate.Exists;


public abstract class AbstractCoreKeyIteratorSettingsFactoryTest {
    private final AbstractCoreKeyIteratorSettingsFactory factory;

    protected AbstractCoreKeyIteratorSettingsFactoryTest(final AbstractCoreKeyIteratorSettingsFactory factory) {
        this.factory = factory;
    }

    @Test
    public void shouldReturnNullValidatorIteratorIfNoSchemaValidation() throws Exception {
        // Given
        final AccumuloStore store = Mockito.mock(AccumuloStore.class);
        final Schema schema = createSchema();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        // When
        final IteratorSetting iterator = factory.getValidatorIteratorSetting(store);
        // Then
        Assert.assertNull(iterator);
    }

    @Test
    public void shouldReturnIteratorValidatorIterator() throws Exception {
        // Given
        final AccumuloStore store = Mockito.mock(AccumuloStore.class);
        final Schema schema = new Schema.Builder().merge(createSchema()).type("str", new TypeDefinition.Builder().validateFunctions(new Exists()).build()).build();
        final AccumuloKeyPackage keyPackage = Mockito.mock(AccumuloKeyPackage.class);
        final AccumuloElementConverter converter = Mockito.mock(AccumuloElementConverter.class);
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getKeyPackage()).willReturn(keyPackage);
        BDDMockito.given(keyPackage.getKeyConverter()).willReturn(converter);
        // When
        final IteratorSetting iterator = factory.getValidatorIteratorSetting(store);
        // Then
        Assert.assertEquals(VALIDATOR_ITERATOR_NAME, iterator.getName());
        Assert.assertEquals(VALIDATOR_ITERATOR_PRIORITY, iterator.getPriority());
        Assert.assertEquals(ValidatorFilter.class.getName(), iterator.getIteratorClass());
        JsonAssert.assertEquals(schema.toCompactJson(), iterator.getOptions().get(SCHEMA).getBytes());
        Assert.assertEquals(converter.getClass().getName(), iterator.getOptions().get(ACCUMULO_ELEMENT_CONVERTER_CLASS));
    }

    @Test
    public void shouldReturnNullPreAggFilterIfNoPreAggFilters() throws Exception {
        // Given
        final AccumuloStore store = Mockito.mock(AccumuloStore.class);
        final Schema schema = createSchema();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().postAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).build();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        // When
        final IteratorSetting iterator = factory.getElementPreAggregationFilterIteratorSetting(view, store);
        // Then
        Assert.assertNull(iterator);
    }

    @Test
    public void shouldReturnPreAggFilterIterator() throws Exception {
        // Given
        final AccumuloStore store = Mockito.mock(AccumuloStore.class);
        final Schema schema = createSchema();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).build();
        final AccumuloKeyPackage keyPackage = Mockito.mock(AccumuloKeyPackage.class);
        final AccumuloElementConverter converter = Mockito.mock(AccumuloElementConverter.class);
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getKeyPackage()).willReturn(keyPackage);
        BDDMockito.given(keyPackage.getKeyConverter()).willReturn(converter);
        // When
        final IteratorSetting iterator = factory.getElementPreAggregationFilterIteratorSetting(view, store);
        // Then
        Assert.assertEquals(ELEMENT_PRE_AGGREGATION_FILTER_ITERATOR_NAME, iterator.getName());
        Assert.assertEquals(ELEMENT_PRE_AGGREGATION_FILTER_ITERATOR_PRIORITY, iterator.getPriority());
        Assert.assertEquals(ElementPreAggregationFilter.class.getName(), iterator.getIteratorClass());
        JsonAssert.assertEquals(schema.toCompactJson(), iterator.getOptions().get(SCHEMA).getBytes());
        JsonAssert.assertEquals(view.toCompactJson(), iterator.getOptions().get(VIEW).getBytes());
        Assert.assertEquals(converter.getClass().getName(), iterator.getOptions().get(ACCUMULO_ELEMENT_CONVERTER_CLASS));
    }

    @Test
    public void shouldReturnNullPostAggFilterIfNoPreAggFilters() throws Exception {
        // Given
        final AccumuloStore store = Mockito.mock(AccumuloStore.class);
        final Schema schema = createSchema();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).build();
        BDDMockito.given(store.getSchema()).willReturn(schema);
        // When
        final IteratorSetting iterator = factory.getElementPostAggregationFilterIteratorSetting(view, store);
        // Then
        Assert.assertNull(iterator);
    }

    @Test
    public void shouldReturnPostAggFilterIterator() throws Exception {
        // Given
        final AccumuloStore store = Mockito.mock(AccumuloStore.class);
        final Schema schema = createSchema();
        final View view = new View.Builder().edge(EDGE, new ViewElementDefinition.Builder().postAggregationFilter(new ElementFilter.Builder().select(PROP_1).execute(new Exists()).build()).build()).build();
        final AccumuloKeyPackage keyPackage = Mockito.mock(AccumuloKeyPackage.class);
        final AccumuloElementConverter converter = Mockito.mock(AccumuloElementConverter.class);
        BDDMockito.given(store.getSchema()).willReturn(schema);
        BDDMockito.given(store.getKeyPackage()).willReturn(keyPackage);
        BDDMockito.given(keyPackage.getKeyConverter()).willReturn(converter);
        // When
        final IteratorSetting iterator = factory.getElementPostAggregationFilterIteratorSetting(view, store);
        // Then
        Assert.assertEquals(ELEMENT_POST_AGGREGATION_FILTER_ITERATOR_NAME, iterator.getName());
        Assert.assertEquals(ELEMENT_POST_AGGREGATION_FILTER_ITERATOR_PRIORITY, iterator.getPriority());
        Assert.assertEquals(ElementPostAggregationFilter.class.getName(), iterator.getIteratorClass());
        JsonAssert.assertEquals(schema.toCompactJson(), iterator.getOptions().get(SCHEMA).getBytes());
        JsonAssert.assertEquals(view.toCompactJson(), iterator.getOptions().get(VIEW).getBytes());
        Assert.assertEquals(converter.getClass().getName(), iterator.getOptions().get(ACCUMULO_ELEMENT_CONVERTER_CLASS));
    }
}

