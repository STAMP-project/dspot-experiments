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


import DirectedType.DIRECTED;
import DirectedType.UNDIRECTED;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import static IdentifierType.DESTINATION;
import static IdentifierType.SOURCE;


public class LazyEdgeTest {
    @Test
    public void shouldLoadPropertyFromLazyProperties() {
        // Given
        final Edge edge = new Edge.Builder().build();
        final ElementValueLoader edgeLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEdge lazyEdge = new LazyEdge(edge, edgeLoader);
        final String propertyName = "property name";
        final String exceptedPropertyValue = "property value";
        BDDMockito.given(edgeLoader.getProperty(propertyName, lazyEdge.getProperties())).willReturn(exceptedPropertyValue);
        // When
        Object propertyValue = lazyEdge.getProperty(propertyName);
        // Then
        Assert.assertEquals(exceptedPropertyValue, propertyValue);
    }

    @Test
    public void shouldLoadIdentifierWhenNotLoaded() {
        // Given
        final Edge edge = Mockito.mock(Edge.class);
        BDDMockito.given(edge.getProperties()).willReturn(Mockito.mock(Properties.class));
        final ElementValueLoader edgeLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEdge lazyEdge = new LazyEdge(edge, edgeLoader);
        final IdentifierType identifierType = DESTINATION;
        final String exceptedIdentifierValue = "identifier value";
        BDDMockito.given(edge.getDestination()).willReturn(exceptedIdentifierValue);
        // When
        Object identifierValue = lazyEdge.getIdentifier(identifierType);
        // Then
        Assert.assertEquals(exceptedIdentifierValue, identifierValue);
        Assert.assertEquals(identifierValue, edge.getDestination());
        Mockito.verify(edgeLoader).loadIdentifiers(edge);
    }

    @Test
    public void shouldNotLoadIdentifierWhenLoaded() {
        // Given
        final Edge edge = new Edge.Builder().build();
        final ElementValueLoader edgeLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEdge lazyEdge = new LazyEdge(edge, edgeLoader);
        final IdentifierType identifierType = SOURCE;
        final String exceptedIdentifierValue = "identifier value";
        lazyEdge.setIdentifiers(exceptedIdentifierValue, "dest", DIRECTED);
        // When - should use the loaded value
        Object identifierValue = lazyEdge.getIdentifier(identifierType);
        Object identifierValue2 = lazyEdge.getIdentifier(identifierType);
        // Then
        Assert.assertEquals(exceptedIdentifierValue, identifierValue);
        Assert.assertEquals(exceptedIdentifierValue, identifierValue2);
        Assert.assertEquals(exceptedIdentifierValue, edge.getSource());
        Mockito.verify(edgeLoader, Mockito.never()).loadIdentifiers(edge);
    }

    @Test
    public void shouldNotLoadIsDirectedWhenLoaded() {
        // Given
        final Edge edge = new Edge.Builder().build();
        final ElementValueLoader edgeLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEdge lazyEdge = new LazyEdge(edge, edgeLoader);
        lazyEdge.setIdentifiers(null, null, DIRECTED);// call it to load the value.

        // When
        boolean isDirected = lazyEdge.isDirected();
        boolean isDirected2 = lazyEdge.isDirected();
        // Then
        Assert.assertTrue(isDirected);
        Assert.assertTrue(isDirected2);
        Mockito.verify(edgeLoader, Mockito.never()).loadIdentifiers(edge);
    }

    @Test
    public void shouldDelegatePutPropertyToLazyProperties() {
        // Given
        final Edge edge = new Edge.Builder().build();
        final ElementValueLoader edgeLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEdge lazyEdge = new LazyEdge(edge, edgeLoader);
        final String propertyName = "property name";
        final String propertyValue = "property value";
        // When
        lazyEdge.putProperty(propertyName, propertyValue);
        // Then
        Assert.assertEquals(propertyValue, edge.getProperty(propertyName));
        Assert.assertEquals(propertyValue, lazyEdge.getProperty(propertyName));
    }

    @Test
    public void shouldDelegateSetIdentifiersToEdge() {
        // Given
        final Edge edge = Mockito.mock(Edge.class);
        BDDMockito.given(edge.getProperties()).willReturn(Mockito.mock(Properties.class));
        final ElementValueLoader edgeLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEdge lazyEdge = new LazyEdge(edge, edgeLoader);
        // When
        lazyEdge.setIdentifiers("src", "dest", UNDIRECTED);
        // Then
        Mockito.verify(edgeLoader, Mockito.never()).loadIdentifiers(edge);
        Mockito.verify(edge).setIdentifiers("src", "dest", UNDIRECTED);
    }

    @Test
    public void shouldDelegateGetGroupToEdge() {
        // Given
        final ElementValueLoader edgeLoader = Mockito.mock(ElementValueLoader.class);
        final String group = "group";
        final Edge edge = new Edge.Builder().group(group).build();
        final LazyEdge lazyEdge = new LazyEdge(edge, edgeLoader);
        // When
        final String groupResult = lazyEdge.getGroup();
        // Then
        Assert.assertEquals(group, groupResult);
    }

    @Test
    public void shouldGetLazyProperties() {
        // Given
        final Edge edge = new Edge.Builder().build();
        final ElementValueLoader edgeLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEdge lazyEdge = new LazyEdge(edge, edgeLoader);
        // When
        final LazyProperties result = lazyEdge.getProperties();
        // Then
        Assert.assertNotNull(result);
        Assert.assertNotSame(edge.getProperties(), result);
    }

    @Test
    public void shouldUnwrapEdge() {
        // Given
        final Edge edge = new Edge.Builder().build();
        final ElementValueLoader edgeLoader = Mockito.mock(ElementValueLoader.class);
        final LazyEdge lazyEdge = new LazyEdge(edge, edgeLoader);
        // When
        final Edge result = lazyEdge.getElement();
        // Then
        Assert.assertSame(edge, result);
    }
}

