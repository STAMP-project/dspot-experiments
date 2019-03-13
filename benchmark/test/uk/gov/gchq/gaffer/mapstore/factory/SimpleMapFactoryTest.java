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
package uk.gov.gchq.gaffer.mapstore.factory;


import SimpleMapFactory.MAP_CLASS;
import SimpleMapFactory.MAP_CLASS_DEFAULT;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.mapstore.MapStoreProperties;
import uk.gov.gchq.gaffer.mapstore.multimap.MapOfSets;
import uk.gov.gchq.gaffer.mapstore.utils.ElementCloner;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class SimpleMapFactoryTest {
    @Test
    public void shouldThrowExceptionIfMapClassIsInvalid() throws StoreException {
        // Given
        final Class mapClass = String.class;
        final Schema schema = Mockito.mock(Schema.class);
        final MapStoreProperties properties = Mockito.mock(MapStoreProperties.class);
        final SimpleMapFactory factory = new SimpleMapFactory();
        BDDMockito.given(properties.get(MAP_CLASS, MAP_CLASS_DEFAULT)).willReturn(mapClass.getName());
        // When / Then
        try {
            factory.initialise(schema, properties);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldExtractMapClassFromPropertiesWhenInitialised() throws StoreException {
        // Given
        final Class<? extends Map> mapClass = LinkedHashMap.class;
        final Schema schema = Mockito.mock(Schema.class);
        final MapStoreProperties properties = Mockito.mock(MapStoreProperties.class);
        final SimpleMapFactory factory = new SimpleMapFactory();
        BDDMockito.given(properties.get(MAP_CLASS, MAP_CLASS_DEFAULT)).willReturn(mapClass.getName());
        // When
        factory.initialise(schema, properties);
        // Then
        Assert.assertEquals(mapClass, factory.getMapClass());
    }

    @Test
    public void shouldCreateNewMapUsingMapClass() throws StoreException {
        // Given
        final Class<? extends Map> mapClass = LinkedHashMap.class;
        final Schema schema = Mockito.mock(Schema.class);
        final MapStoreProperties properties = Mockito.mock(MapStoreProperties.class);
        final SimpleMapFactory factory = new SimpleMapFactory();
        BDDMockito.given(properties.get(MAP_CLASS, MAP_CLASS_DEFAULT)).willReturn(mapClass.getName());
        factory.initialise(schema, properties);
        // When
        final Map<Object, Object> map1 = factory.getMap("mapName1", Object.class, Object.class);
        final Map<Object, Object> map2 = factory.getMap("mapName2", Object.class, Object.class);
        // Then
        Assert.assertTrue(map1.isEmpty());
        Assert.assertTrue((map1 instanceof LinkedHashMap));
        Assert.assertTrue(map2.isEmpty());
        Assert.assertTrue((map2 instanceof LinkedHashMap));
        Assert.assertNotSame(map1, map2);
    }

    @Test
    public void shouldThrowExceptionIfMapClassCannotBeInstantiated() throws StoreException {
        // Given
        final Class<? extends Map> mapClass = Map.class;
        final Schema schema = Mockito.mock(Schema.class);
        final MapStoreProperties properties = Mockito.mock(MapStoreProperties.class);
        final SimpleMapFactory factory = new SimpleMapFactory();
        BDDMockito.given(properties.get(MAP_CLASS, MAP_CLASS_DEFAULT)).willReturn(mapClass.getName());
        factory.initialise(schema, properties);
        // When / Then
        try {
            factory.getMap("mapName1", Object.class, Object.class);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldCreateNewMultiMap() throws StoreException {
        // Given
        final Class<? extends Map> mapClass = LinkedHashMap.class;
        final Schema schema = Mockito.mock(Schema.class);
        final MapStoreProperties properties = Mockito.mock(MapStoreProperties.class);
        final SimpleMapFactory factory = new SimpleMapFactory();
        BDDMockito.given(properties.get(MAP_CLASS, MAP_CLASS_DEFAULT)).willReturn(mapClass.getName());
        factory.initialise(schema, properties);
        // When
        final MapOfSets<Object, Object> map1 = ((MapOfSets) (factory.getMultiMap("mapName1", Object.class, Object.class)));
        final MapOfSets<Object, Object> map2 = ((MapOfSets) (factory.getMultiMap("mapName2", Object.class, Object.class)));
        // Then
        Assert.assertTrue(map1.getWrappedMap().isEmpty());
        Assert.assertTrue(map1.getWrappedMap().isEmpty());
        Assert.assertTrue(((map2.getWrappedMap()) instanceof LinkedHashMap));
        Assert.assertTrue(((map2.getWrappedMap()) instanceof LinkedHashMap));
        Assert.assertNotSame(map1, map2);
    }

    @Test
    public void shouldCloneElementUsingCloner() throws StoreException {
        // Given
        final ElementCloner elementCloner = Mockito.mock(ElementCloner.class);
        final Element element = Mockito.mock(Element.class);
        final Element expectedClonedElement = Mockito.mock(Element.class);
        final Schema schema = Mockito.mock(Schema.class);
        final SimpleMapFactory factory = new SimpleMapFactory(elementCloner);
        BDDMockito.given(elementCloner.cloneElement(element, schema)).willReturn(expectedClonedElement);
        // When
        final Element clonedElement = factory.cloneElement(element, schema);
        // Then
        Mockito.verify(elementCloner).cloneElement(element, schema);
        Assert.assertSame(expectedClonedElement, clonedElement);
        Assert.assertNotSame(element, clonedElement);
    }
}

