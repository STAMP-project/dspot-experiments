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


import Element.DEFAULT_GROUP;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.gchq.gaffer.commonutil.StringUtil;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;


@RunWith(MockitoJUnitRunner.class)
public abstract class ElementTest {
    @Test
    public void shouldSetAndGetFields() {
        // Given
        final String group = "group";
        final Properties properties = new Properties();
        final Element element = newElement();
        // When
        element.setGroup(group);
        element.setProperties(properties);
        // Then
        Assert.assertEquals(group, element.getGroup());
        Assert.assertSame(properties, element.getProperties());
    }

    @Test
    public void shouldCreateElementWithUnknownGroup() {
        // Given
        // When
        final Element element = newElement();
        // Then
        Assert.assertEquals(DEFAULT_GROUP, element.getGroup());
    }

    @Test
    public void shouldCreateElementWithGroup() {
        // Given
        final String group = "group";
        // When
        final Element element = newElement(group);
        // Then
        Assert.assertEquals("group", element.getGroup());
    }

    @Test
    public void shouldReturnTrueForEqualsWithTheSameInstance() {
        // Given
        final Element element = newElement("group");
        // When
        boolean isEqual = element.equals(element);
        // Then
        Assert.assertTrue(isEqual);
        Assert.assertEquals(element.hashCode(), element.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenGroupIsDifferent() {
        // Given
        final Element element1 = newElement("group");
        final Object element2 = newElement("a different group");
        // When
        boolean isEqual = element1.equals(element2);
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertFalse(((element1.hashCode()) == (element2.hashCode())));
    }

    @Test
    public void shouldReturnFalseForEqualsWithNullObject() {
        // Given
        final Element element1 = newElement("group");
        // When
        boolean isEqual = element1.equals(((Object) (null)));
        // Then
        Assert.assertFalse(isEqual);
    }

    @Test
    public void shouldReturnFalseForEqualsWithNullElement() {
        // Given
        final Element element1 = newElement("group");
        // When
        boolean isEqual = element1.equals(null);
        // Then
        Assert.assertFalse(isEqual);
    }

    @Test
    public void shouldReturnItselfForGetElement() {
        // Given
        final Element element = newElement("group");
        // When
        final Element result = element.getElement();
        // Then
        Assert.assertSame(element, result);
    }

    @Test
    public void shouldCopyProperties() {
        // Given
        final Element element1 = newElement("group");
        final Properties newProperties = new Properties("property1", "propertyValue1");
        // When
        element1.copyProperties(newProperties);
        // Then
        Assert.assertEquals(1, element1.getProperties().size());
        Assert.assertEquals("propertyValue1", element1.getProperty("property1"));
    }

    @Test
    public void shouldRemoveProperty() {
        // Given
        final Element element1 = newElement("group");
        element1.putProperty("property1", "propertyValue1");
        element1.putProperty("property2", "propertyValue2");
        // When
        element1.removeProperty("property1");
        // Then
        Assert.assertEquals(1, element1.getProperties().size());
        Assert.assertEquals(null, element1.getProperty("property1"));
        Assert.assertEquals("propertyValue2", element1.getProperty("property2"));
    }

    @Test
    public void shouldSerialiseAndDeserialiseProperties() throws SerialisationException {
        // Given
        final Element element = newElement("group");
        final Properties properties = new Properties();
        properties.put("property1", 1L);
        properties.put("property2", 2);
        properties.put("property3", ((double) (3)));
        properties.put("property4", "4");
        properties.put("property5", new Date(5L));
        element.setProperties(properties);
        // When
        final byte[] serialisedElement = JSONSerialiser.serialise(element);
        final Element deserialisedElement = JSONSerialiser.deserialise(serialisedElement, element.getClass());
        // Then
        Assert.assertTrue(StringUtil.toString(serialisedElement).contains("{\"java.util.Date\":5}"));
        Assert.assertEquals(element, deserialisedElement);
    }
}

