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
package uk.gov.gchq.gaffer.mapstore.impl;


import MapImpl.EDGE_ID_TO_ELEMENTS;
import MapImpl.ENTITY_ID_TO_ELEMENTS;
import TestGroups.EDGE;
import com.google.common.collect.Sets;
import java.util.Map;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.GroupedProperties;
import uk.gov.gchq.gaffer.data.element.id.EdgeId;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.mapstore.MapStoreProperties;
import uk.gov.gchq.gaffer.mapstore.factory.MapFactory;
import uk.gov.gchq.gaffer.mapstore.multimap.MultiMap;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;

import static MapImpl.AGG_ELEMENTS;
import static MapImpl.NON_AGG_ELEMENTS;


public class MapImplTest {
    private static MapFactory mockMapFactory;

    @Test
    public void shouldCreateMapsUsingMapFactory() throws StoreException {
        // Given
        final Schema schema = Mockito.mock(Schema.class);
        final MapStoreProperties properties = Mockito.mock(MapStoreProperties.class);
        final Map aggElements = Mockito.mock(Map.class);
        final Map nonAggElements = Mockito.mock(Map.class);
        final MultiMap entityIdToElements = Mockito.mock(MultiMap.class);
        final MultiMap edgeIdToElements = Mockito.mock(MultiMap.class);
        BDDMockito.given(schema.getGroups()).willReturn(Sets.newHashSet(EDGE));
        BDDMockito.given(properties.getMapFactory()).willReturn(MapImplTest.TestMapFactory.class.getName());
        BDDMockito.given(properties.getCreateIndex()).willReturn(true);
        BDDMockito.given(MapImplTest.mockMapFactory.getMap((((TestGroups.EDGE) + "|") + (AGG_ELEMENTS)), Element.class, GroupedProperties.class)).willReturn(aggElements);
        BDDMockito.given(MapImplTest.mockMapFactory.getMap((((TestGroups.EDGE) + "|") + (NON_AGG_ELEMENTS)), Element.class, Integer.class)).willReturn(nonAggElements);
        BDDMockito.given(MapImplTest.mockMapFactory.getMultiMap(ENTITY_ID_TO_ELEMENTS, EntityId.class, Element.class)).willReturn(entityIdToElements);
        BDDMockito.given(MapImplTest.mockMapFactory.getMultiMap(EDGE_ID_TO_ELEMENTS, EdgeId.class, Element.class)).willReturn(edgeIdToElements);
        // When
        new MapImpl(schema, properties);
        // Then
        Mockito.verify(MapImplTest.mockMapFactory).getMap((((TestGroups.EDGE) + "|") + (AGG_ELEMENTS)), Element.class, GroupedProperties.class);
        Mockito.verify(MapImplTest.mockMapFactory).getMap((((TestGroups.EDGE) + "|") + (NON_AGG_ELEMENTS)), Element.class, Long.class);
        Mockito.verify(MapImplTest.mockMapFactory).getMultiMap(ENTITY_ID_TO_ELEMENTS, EntityId.class, Element.class);
        Mockito.verify(MapImplTest.mockMapFactory).getMultiMap(EDGE_ID_TO_ELEMENTS, EdgeId.class, Element.class);
    }

    @Test
    public void shouldNotCreateIndexesIfNotRequired() throws StoreException {
        // Given
        final Schema schema = Mockito.mock(Schema.class);
        final MapStoreProperties properties = Mockito.mock(MapStoreProperties.class);
        final Map aggElements = Mockito.mock(Map.class);
        final Map nonAggElements = Mockito.mock(Map.class);
        BDDMockito.given(schema.getGroups()).willReturn(Sets.newHashSet(EDGE));
        BDDMockito.given(properties.getMapFactory()).willReturn(MapImplTest.TestMapFactory.class.getName());
        BDDMockito.given(properties.getCreateIndex()).willReturn(false);
        BDDMockito.given(MapImplTest.mockMapFactory.getMap((((TestGroups.EDGE) + "|") + (AGG_ELEMENTS)), Element.class, GroupedProperties.class)).willReturn(aggElements);
        BDDMockito.given(MapImplTest.mockMapFactory.getMap((((TestGroups.EDGE) + "|") + (NON_AGG_ELEMENTS)), Element.class, Integer.class)).willReturn(nonAggElements);
        // When
        new MapImpl(schema, properties);
        // Then
        Mockito.verify(MapImplTest.mockMapFactory).getMap((((TestGroups.EDGE) + "|") + (AGG_ELEMENTS)), Element.class, GroupedProperties.class);
        Mockito.verify(MapImplTest.mockMapFactory).getMap((((TestGroups.EDGE) + "|") + (NON_AGG_ELEMENTS)), Element.class, Long.class);
        Mockito.verify(MapImplTest.mockMapFactory, Mockito.never()).getMultiMap(ENTITY_ID_TO_ELEMENTS, EntityId.class, Element.class);
        Mockito.verify(MapImplTest.mockMapFactory, Mockito.never()).getMultiMap(EDGE_ID_TO_ELEMENTS, EdgeId.class, Element.class);
    }

    public static final class TestMapFactory implements MapFactory {
        @Override
        public void initialise(final Schema schema, final MapStoreProperties properties) {
            MapImplTest.mockMapFactory.initialise(schema, properties);
        }

        @Override
        public <K, V> Map<K, V> getMap(final String mapName, final Class<K> keyClass, final Class<V> valueClass) {
            return MapImplTest.mockMapFactory.getMap(mapName, keyClass, valueClass);
        }

        @Override
        public <K, V> MultiMap<K, V> getMultiMap(final String mapName, final Class<K> keyClass, final Class<V> valueClass) {
            return MapImplTest.mockMapFactory.getMultiMap(mapName, keyClass, valueClass);
        }

        @Override
        public void clear() {
            MapImplTest.mockMapFactory.clear();
        }

        @Override
        public Element cloneElement(final Element element, final Schema schema) {
            return MapImplTest.mockMapFactory.cloneElement(element, schema);
        }
    }
}

