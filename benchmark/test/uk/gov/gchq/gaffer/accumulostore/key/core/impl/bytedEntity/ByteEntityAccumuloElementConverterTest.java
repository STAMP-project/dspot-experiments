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
package uk.gov.gchq.gaffer.accumulostore.key.core.impl.bytedEntity;


import TestGroups.EDGE;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.key.AbstractAccumuloElementConverterTest;
import uk.gov.gchq.gaffer.accumulostore.key.core.AbstractCoreKeyAccumuloElementConverterTest;
import uk.gov.gchq.gaffer.accumulostore.utils.AccumuloPropertyNames;
import uk.gov.gchq.gaffer.accumulostore.utils.ByteUtils;
import uk.gov.gchq.gaffer.accumulostore.utils.BytesAndRange;
import uk.gov.gchq.gaffer.data.element.Properties;


/**
 * Tests are inherited from AbstractAccumuloElementConverterTest.
 */
public class ByteEntityAccumuloElementConverterTest extends AbstractCoreKeyAccumuloElementConverterTest {
    @Test
    public void shouldSerialiseWithHistoricPropertiesAsBytesFromColumnQualifier() throws Exception {
        // Given
        final Properties properties = new Properties() {
            {
                put(AccumuloPropertyNames.COLUMN_QUALIFIER, 1);
                put(AccumuloPropertyNames.COLUMN_QUALIFIER_2, 2);
                put(AccumuloPropertyNames.COLUMN_QUALIFIER_3, 3);
                put(AccumuloPropertyNames.COLUMN_QUALIFIER_4, 4);
            }
        };
        byte[] historicPropertyBytes = new byte[]{ 4, 1, 0, 0, 0, 4, 2, 0, 0, 0 };
        final byte[] columnQualifierBytes = converter.buildColumnQualifier(EDGE, properties);
        // When
        final BytesAndRange propertiesBytes = converter.getPropertiesAsBytesFromColumnQualifier(EDGE, columnQualifierBytes, 2);
        // Then
        Assert.assertTrue(ByteUtils.areKeyBytesEqual(new BytesAndRange(historicPropertyBytes, 0, historicPropertyBytes.length), propertiesBytes));
    }

    @Test
    public void shouldSerialiseWithHistoricColumnQualifier() throws Exception {
        // Given
        final Properties properties = new Properties() {
            {
                put(AccumuloPropertyNames.COLUMN_QUALIFIER, 1);
                put(AccumuloPropertyNames.COLUMN_QUALIFIER_2, Integer.MAX_VALUE);
                put(AccumuloPropertyNames.COLUMN_QUALIFIER_3, 3);
                put(AccumuloPropertyNames.COLUMN_QUALIFIER_4, Integer.MIN_VALUE);
            }
        };
        byte[] historicColumnQualifierBytes = new byte[]{ 4, 1, 0, 0, 0, 4, -1, -1, -1, 127, 4, 3, 0, 0, 0, 4, 0, 0, 0, -128 };
        // When
        final byte[] columnQualifier = converter.buildColumnQualifier(EDGE, properties);
        Properties propertiesFromHistoric = converter.getPropertiesFromColumnQualifier(EDGE, historicColumnQualifierBytes);
        // Then
        Assert.assertArrayEquals(historicColumnQualifierBytes, columnQualifier);
        Assert.assertEquals(propertiesFromHistoric, properties);
    }
}

