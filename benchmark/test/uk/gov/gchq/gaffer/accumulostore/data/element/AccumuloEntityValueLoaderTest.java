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
package uk.gov.gchq.gaffer.accumulostore.data.element;


import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_3;
import TestPropertyNames.TIMESTAMP;
import TestPropertyNames.VISIBILITY;
import org.apache.accumulo.core.data.ByteSequence;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.accumulostore.key.AccumuloElementConverter;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.LazyProperties;
import uk.gov.gchq.gaffer.data.element.Properties;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class AccumuloEntityValueLoaderTest {
    @Test
    public void shouldLoadAllIdentifiers() throws SerialisationException {
        // Given
        final String group = TestGroups.ENTITY;
        final Key key = Mockito.mock(Key.class);
        final Value value = Mockito.mock(Value.class);
        final AccumuloElementConverter converter = Mockito.mock(AccumuloElementConverter.class);
        final Schema schema = createSchema();
        final AccumuloEntityValueLoader loader = new AccumuloEntityValueLoader(group, key, value, converter, schema);
        final Entity entity = Mockito.mock(Entity.class);
        final EntityId elementId = new EntitySeed("vertex");
        BDDMockito.given(converter.getElementId(key, false)).willReturn(elementId);
        // When
        loader.loadIdentifiers(entity);
        // Then
        Mockito.verify(converter, Mockito.never()).getPropertiesFromColumnQualifier(Mockito.eq(group), Mockito.any(byte[].class));
        Mockito.verify(converter, Mockito.never()).getPropertiesFromColumnVisibility(Mockito.eq(group), Mockito.any(byte[].class));
        Mockito.verify(converter, Mockito.never()).getPropertiesFromTimestamp(Mockito.eq(group), Mockito.anyLong());
        Mockito.verify(converter, Mockito.never()).getPropertiesFromValue(Mockito.eq(group), Mockito.any(Value.class));
    }

    @Test
    public void shouldLoadAllColumnQualifierPropertiesWhenGetGroupByProperty() throws SerialisationException {
        // Given
        final String group = TestGroups.ENTITY;
        final Key key = Mockito.mock(Key.class);
        final Value value = Mockito.mock(Value.class);
        final AccumuloElementConverter converter = Mockito.mock(AccumuloElementConverter.class);
        final Schema schema = createSchema();
        final AccumuloEntityValueLoader loader = new AccumuloEntityValueLoader(group, key, value, converter, schema);
        final LazyProperties lazyProperties = Mockito.mock(LazyProperties.class);
        final Properties properties = Mockito.mock(Properties.class);
        final ByteSequence cqData = Mockito.mock(ByteSequence.class);
        BDDMockito.given(key.getColumnQualifierData()).willReturn(cqData);
        final byte[] cqBytes = new byte[]{ 0, 1, 2, 3, 4 };
        BDDMockito.given(cqData.getBackingArray()).willReturn(cqBytes);
        BDDMockito.given(converter.getPropertiesFromColumnQualifier(group, cqBytes)).willReturn(properties);
        BDDMockito.given(properties.get(PROP_1)).willReturn("propValue1");
        // When
        final Object property = loader.getProperty(PROP_1, lazyProperties);
        // Then
        Assert.assertEquals("propValue1", property);
        Mockito.verify(lazyProperties).putAll(properties);
        Mockito.verify(converter, Mockito.never()).getElementId(key, false);
        Mockito.verify(converter, Mockito.never()).getPropertiesFromColumnVisibility(Mockito.eq(group), Mockito.any(byte[].class));
        Mockito.verify(converter, Mockito.never()).getPropertiesFromTimestamp(Mockito.eq(group), Mockito.anyLong());
        Mockito.verify(converter, Mockito.never()).getPropertiesFromValue(Mockito.eq(group), Mockito.any(Value.class));
    }

    @Test
    public void shouldLoadAllValuePropertiesWhenGetProperty() throws SerialisationException {
        // Given
        final String group = TestGroups.ENTITY;
        final Key key = Mockito.mock(Key.class);
        final Value value = Mockito.mock(Value.class);
        final AccumuloElementConverter converter = Mockito.mock(AccumuloElementConverter.class);
        final Schema schema = createSchema();
        final AccumuloEntityValueLoader loader = new AccumuloEntityValueLoader(group, key, value, converter, schema);
        final LazyProperties lazyProperties = Mockito.mock(LazyProperties.class);
        final Properties properties = Mockito.mock(Properties.class);
        BDDMockito.given(converter.getPropertiesFromValue(group, value)).willReturn(properties);
        BDDMockito.given(properties.get(PROP_3)).willReturn("propValue3");
        // When
        final Object property = loader.getProperty(PROP_3, lazyProperties);
        // Then
        Assert.assertEquals("propValue3", property);
        Mockito.verify(lazyProperties).putAll(properties);
        Mockito.verify(converter, Mockito.never()).getElementId(key, false);
        Mockito.verify(converter, Mockito.never()).getPropertiesFromColumnVisibility(Mockito.eq(group), Mockito.any(byte[].class));
        Mockito.verify(converter, Mockito.never()).getPropertiesFromTimestamp(Mockito.eq(group), Mockito.anyLong());
        Mockito.verify(converter, Mockito.never()).getPropertiesFromColumnQualifier(Mockito.eq(group), Mockito.any(byte[].class));
    }

    @Test
    public void shouldLoadAllVisibilityPropertiesWhenGetVisProperty() throws SerialisationException {
        // Given
        final String group = TestGroups.ENTITY;
        final Key key = Mockito.mock(Key.class);
        final Value value = Mockito.mock(Value.class);
        final AccumuloElementConverter converter = Mockito.mock(AccumuloElementConverter.class);
        final Schema schema = createSchema();
        final AccumuloEntityValueLoader loader = new AccumuloEntityValueLoader(group, key, value, converter, schema);
        final LazyProperties lazyProperties = Mockito.mock(LazyProperties.class);
        final Properties properties = Mockito.mock(Properties.class);
        final ByteSequence cvData = Mockito.mock(ByteSequence.class);
        BDDMockito.given(key.getColumnVisibilityData()).willReturn(cvData);
        final byte[] cvBytes = new byte[]{ 0, 1, 2, 3, 4 };
        BDDMockito.given(cvData.getBackingArray()).willReturn(cvBytes);
        BDDMockito.given(converter.getPropertiesFromColumnVisibility(group, cvBytes)).willReturn(properties);
        BDDMockito.given(properties.get(VISIBILITY)).willReturn("vis1");
        // When
        final Object property = loader.getProperty(VISIBILITY, lazyProperties);
        // Then
        Assert.assertEquals("vis1", property);
        Mockito.verify(lazyProperties).putAll(properties);
        Mockito.verify(converter, Mockito.never()).getElementId(key, false);
        Mockito.verify(converter, Mockito.never()).getPropertiesFromColumnQualifier(Mockito.eq(group), Mockito.any(byte[].class));
        Mockito.verify(converter, Mockito.never()).getPropertiesFromTimestamp(Mockito.eq(group), Mockito.anyLong());
        Mockito.verify(converter, Mockito.never()).getPropertiesFromValue(Mockito.eq(group), Mockito.any(Value.class));
    }

    @Test
    public void shouldLoadAllTimestampPropertiesWhenGetTimestampProperty() throws SerialisationException {
        // Given
        final String group = TestGroups.ENTITY;
        final Key key = Mockito.mock(Key.class);
        final Value value = Mockito.mock(Value.class);
        final AccumuloElementConverter converter = Mockito.mock(AccumuloElementConverter.class);
        final Schema schema = createSchema();
        final AccumuloEntityValueLoader loader = new AccumuloEntityValueLoader(group, key, value, converter, schema);
        final LazyProperties lazyProperties = Mockito.mock(LazyProperties.class);
        final Properties properties = Mockito.mock(Properties.class);
        final Long timestamp = 10L;
        BDDMockito.given(key.getTimestamp()).willReturn(timestamp);
        BDDMockito.given(converter.getPropertiesFromTimestamp(group, timestamp)).willReturn(properties);
        BDDMockito.given(properties.get(TIMESTAMP)).willReturn(timestamp);
        // When
        final Object property = loader.getProperty(TIMESTAMP, lazyProperties);
        // Then
        Assert.assertEquals(timestamp, property);
        Mockito.verify(lazyProperties).putAll(properties);
        Mockito.verify(converter, Mockito.never()).getElementId(key, false);
        Mockito.verify(converter, Mockito.never()).getPropertiesFromColumnQualifier(Mockito.eq(group), Mockito.any(byte[].class));
        Mockito.verify(converter, Mockito.never()).getPropertiesFromColumnVisibility(Mockito.eq(group), Mockito.any(byte[].class));
        Mockito.verify(converter, Mockito.never()).getPropertiesFromValue(Mockito.eq(group), Mockito.any(Value.class));
    }
}

