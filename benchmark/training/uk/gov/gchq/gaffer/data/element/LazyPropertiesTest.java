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


import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LazyPropertiesTest {
    @Test
    public void shouldLoadPropertyWhenNotLoaded() {
        // Given
        final ElementValueLoader elementLoader = Mockito.mock(ElementValueLoader.class);
        final Properties properties = new Properties();
        final String propertyName = "property name";
        final String exceptedPropertyValue = "property value";
        final LazyProperties lazyProperties = new LazyProperties(properties, elementLoader);
        BDDMockito.given(elementLoader.getProperty(propertyName, lazyProperties)).willReturn(exceptedPropertyValue);
        // When
        final Object propertyValue = lazyProperties.get(propertyName);
        // Then
        Assert.assertEquals(exceptedPropertyValue, propertyValue);
        Assert.assertEquals(propertyValue, properties.get(propertyName));
    }

    @Test
    public void shouldNotLoadPropertyWhenLoaded() {
        // Given
        final ElementValueLoader elementLoader = Mockito.mock(ElementValueLoader.class);
        final String propertyName = "property name";
        final String exceptedPropertyValue = "property value";
        final Properties properties = new Properties(propertyName, exceptedPropertyValue);
        final LazyProperties lazyProperties = new LazyProperties(properties, elementLoader);
        BDDMockito.given(elementLoader.getProperty(propertyName, lazyProperties)).willReturn(exceptedPropertyValue);
        // When
        final Object propertyValue = lazyProperties.get(propertyName);
        // Then
        Assert.assertEquals(exceptedPropertyValue, propertyValue);
        Mockito.verify(elementLoader, Mockito.never()).getProperty(propertyName, lazyProperties);
    }

    @Test
    public void shouldAddPropertyToMapWhenAddingProperty() {
        // Given
        final ElementValueLoader elementLoader = Mockito.mock(ElementValueLoader.class);
        final Properties properties = new Properties();
        final String propertyName = "property name";
        final String propertyValue = "property value";
        final LazyProperties lazyProperties = new LazyProperties(properties, elementLoader);
        BDDMockito.given(elementLoader.getProperty(propertyName, lazyProperties)).willReturn(propertyValue);
        // When
        lazyProperties.put(propertyName, propertyValue);
        // Then
        Mockito.verify(elementLoader, Mockito.never()).getProperty(propertyName, lazyProperties);
        Assert.assertEquals(propertyValue, properties.get(propertyName));
    }

    @Test
    public void shouldClearLoadedPropertiesAndMapWhenClearIsCalled() {
        // Given
        final ElementValueLoader elementLoader = Mockito.mock(ElementValueLoader.class);
        final Properties properties = new Properties();
        final String propertyName = "property name";
        final String propertyValue = "property value";
        final LazyProperties lazyProperties = new LazyProperties(properties, elementLoader);
        BDDMockito.given(elementLoader.getProperty(propertyName, lazyProperties)).willReturn(propertyValue);
        lazyProperties.get(propertyName);// call it to load the value.

        // When
        lazyProperties.clear();
        lazyProperties.get(propertyName);
        // Then
        Mockito.verify(elementLoader, Mockito.times(2)).getProperty(propertyName, lazyProperties);// should be called twice before and after clear()

        Assert.assertEquals(propertyValue, properties.get(propertyName));
    }

    @Test
    public void shouldRemovePropertyNameFromLoadedPropertiesAndMapWhenRemoveIsCalled() {
        // Given
        final ElementValueLoader elementLoader = Mockito.mock(ElementValueLoader.class);
        final Properties properties = new Properties();
        final String propertyName = "property name";
        final String propertyValue = "property value";
        final LazyProperties lazyProperties = new LazyProperties(properties, elementLoader);
        BDDMockito.given(elementLoader.getProperty(propertyName, lazyProperties)).willReturn(propertyValue);
        lazyProperties.get(propertyName);// call it to load the value.

        // When
        lazyProperties.remove(propertyName);
        lazyProperties.get(propertyName);
        // Then
        Mockito.verify(elementLoader, Mockito.times(2)).getProperty(propertyName, lazyProperties);// should be called twice before and after removeProperty()

        Assert.assertEquals(propertyValue, properties.get(propertyName));
    }

    @Test
    public void shouldRemovePropertyNameFromLoadedPropertiesAndMapWhenKeepOnlyThesePropertiesIsCalled() {
        // Given
        final ElementValueLoader elementLoader = Mockito.mock(ElementValueLoader.class);
        final Properties properties = new Properties();
        final String propertyName1 = "property name1";
        final String propertyName2 = "property name2";
        final String propertyValue1 = "property value1";
        final String propertyValue2 = "property value2";
        final LazyProperties lazyProperties = new LazyProperties(properties, elementLoader);
        BDDMockito.given(elementLoader.getProperty(propertyName1, lazyProperties)).willReturn(propertyValue1);
        BDDMockito.given(elementLoader.getProperty(propertyName2, lazyProperties)).willReturn(propertyValue2);
        lazyProperties.get(propertyName1);// call it to load value 1.

        lazyProperties.get(propertyName2);// call it to load value 2.

        // When
        lazyProperties.keepOnly(Sets.newSet(propertyName2));
        lazyProperties.get(propertyName1);
        lazyProperties.get(propertyName2);
        // Then
        Mockito.verify(elementLoader, Mockito.times(2)).getProperty(propertyName1, lazyProperties);
        Mockito.verify(elementLoader, Mockito.times(1)).getProperty(propertyName2, lazyProperties);
        Assert.assertEquals(propertyValue1, properties.get(propertyName1));
        Assert.assertEquals(propertyValue2, properties.get(propertyName2));
    }

    @Test
    public void shouldRemovePropertyNamesFromLoadedPropertiesAndMapWhenRemovePropertiesIsCalled() {
        // Given
        final ElementValueLoader elementLoader = Mockito.mock(ElementValueLoader.class);
        final Properties properties = new Properties();
        final String propertyName1 = "property name1";
        final String propertyName2 = "property name2";
        final String propertyValue1 = "property value1";
        final String propertyValue2 = "property value2";
        final LazyProperties lazyProperties = new LazyProperties(properties, elementLoader);
        BDDMockito.given(elementLoader.getProperty(propertyName1, lazyProperties)).willReturn(propertyValue1);
        BDDMockito.given(elementLoader.getProperty(propertyName2, lazyProperties)).willReturn(propertyValue2);
        lazyProperties.get(propertyName1);// call it to load value 1.

        lazyProperties.get(propertyName2);// call it to load value 2.

        // When
        lazyProperties.remove(Sets.newSet(propertyName1));
        lazyProperties.get(propertyName1);
        lazyProperties.get(propertyName2);
        // Then
        Mockito.verify(elementLoader, Mockito.times(2)).getProperty(propertyName1, lazyProperties);
        Mockito.verify(elementLoader, Mockito.times(1)).getProperty(propertyName2, lazyProperties);
        Assert.assertEquals(propertyValue1, properties.get(propertyName1));
        Assert.assertEquals(propertyValue2, properties.get(propertyName2));
    }

    @Test
    public void shouldDelegateEntrySetMethodToPropertiesInstance() {
        // Given
        final Properties properties = Mockito.mock(Properties.class);
        final LazyProperties lazyProperties = new LazyProperties(properties, null);
        final Set<Map.Entry<String, Object>> expectedEntrySet = Mockito.mock(Set.class);
        BDDMockito.given(properties.entrySet()).willReturn(expectedEntrySet);
        // When
        final Set<Map.Entry<String, Object>> entrySet = lazyProperties.entrySet();
        // Then
        Assert.assertSame(expectedEntrySet, entrySet);
    }
}

