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
package uk.gov.gchq.gaffer.data.element;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import static IdentifierType.VERTEX;


public class LazyEntityTest {
    @Test
    public void shouldLoadPropertyFromLoader() {
        // Given
        final Entity entity = new Entity("group");
        final ElementValueLoader entityLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEntity lazyEntity = new LazyEntity(entity, entityLoader);
        final String propertyName = "property name";
        final String exceptedPropertyValue = "property value";
        BDDMockito.given(entityLoader.getProperty(propertyName, lazyEntity.getProperties())).willReturn(exceptedPropertyValue);
        // When
        Object propertyValue = lazyEntity.getProperty(propertyName);
        // Then
        Assert.assertEquals(exceptedPropertyValue, propertyValue);
    }

    @Test
    public void shouldLoadIdentifierWhenNotLoaded() {
        // Given
        final Entity entity = Mockito.mock(Entity.class);
        BDDMockito.given(entity.getProperties()).willReturn(Mockito.mock(Properties.class));
        final ElementValueLoader entityLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEntity lazyEntity = new LazyEntity(entity, entityLoader);
        final IdentifierType identifierType = VERTEX;
        final String exceptedIdentifierValue = "identifier value";
        BDDMockito.given(entity.getVertex()).willReturn(exceptedIdentifierValue);
        // When
        Object identifierValue = lazyEntity.getIdentifier(identifierType);
        // Then
        Assert.assertEquals(exceptedIdentifierValue, identifierValue);
        Assert.assertEquals(identifierValue, entity.getVertex());
        Mockito.verify(entityLoader).loadIdentifiers(entity);
    }

    @Test
    public void shouldNotLoadIdentifierWhenLoaded() {
        // Given
        final Entity entity = new Entity("group");
        final ElementValueLoader entityLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEntity lazyEntity = new LazyEntity(entity, entityLoader);
        final IdentifierType identifierType = VERTEX;
        final String exceptedIdentifierValue = "identifier value";
        lazyEntity.setVertex(exceptedIdentifierValue);
        // When - should use the loaded value
        Object identifierValue = lazyEntity.getIdentifier(identifierType);
        Object identifierValue2 = lazyEntity.getIdentifier(identifierType);
        // Then
        Assert.assertEquals(exceptedIdentifierValue, identifierValue);
        Assert.assertEquals(exceptedIdentifierValue, identifierValue2);
        Assert.assertEquals(exceptedIdentifierValue, entity.getVertex());
        Mockito.verify(entityLoader, Mockito.never()).loadIdentifiers(entity);
    }

    @Test
    public void shouldDelegatePutPropertyToLazyProperties() {
        // Given
        final Entity entity = new Entity("group");
        final ElementValueLoader entityLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEntity lazyEntity = new LazyEntity(entity, entityLoader);
        final String propertyName = "property name";
        final String propertyValue = "property value";
        // When
        lazyEntity.putProperty(propertyName, propertyValue);
        // Then
        Mockito.verify(entityLoader, Mockito.never()).getProperty(propertyName, lazyEntity.getProperties());
        Assert.assertEquals(propertyValue, entity.getProperty(propertyName));
        Assert.assertEquals(propertyValue, lazyEntity.getProperty(propertyName));
    }

    @Test
    public void shouldDelegateSetIdentifierToEntity() {
        // Given
        final Entity entity = new Entity("group");
        final ElementValueLoader entityLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEntity lazyEntity = new LazyEntity(entity, entityLoader);
        final String vertex = "vertex";
        // When
        lazyEntity.setVertex(vertex);
        // Then
        Mockito.verify(entityLoader, Mockito.never()).loadIdentifiers(entity);
        Assert.assertEquals(vertex, entity.getVertex());
    }

    @Test
    public void shouldDelegateGetGroupToEntity() {
        // Given
        final String group = "group";
        final Entity entity = new Entity(group);
        final ElementValueLoader entityLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEntity lazyEntity = new LazyEntity(entity, entityLoader);
        // When
        final String groupResult = lazyEntity.getGroup();
        // Then
        Assert.assertEquals(group, groupResult);
    }

    @Test
    public void shouldGetLazyProperties() {
        // Given
        final Entity entity = new Entity("group");
        final ElementValueLoader entityLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEntity lazyEntity = new LazyEntity(entity, entityLoader);
        // When
        final LazyProperties result = lazyEntity.getProperties();
        // Then
        Assert.assertNotNull(result);
        Assert.assertNotSame(entity.getProperties(), result);
    }

    @Test
    public void shouldUnwrapEntity() {
        // Given
        final Entity entity = new Entity("group");
        final ElementValueLoader entityLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEntity lazyEntity = new LazyEntity(entity, entityLoader);
        // When
        final Entity result = lazyEntity.getElement();
        // Then
        Assert.assertSame(entity, result);
    }
}

