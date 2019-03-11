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
package uk.gov.gchq.gaffer.accumulostore.utils;


import AccumuloStoreConstants.SCHEMA;
import AccumuloStoreConstants.VIEW;
import org.apache.accumulo.core.client.IteratorSetting;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.store.schema.Schema;


public class IteratorSettingBuilderTest {
    @Test
    public void shouldAddCompactSchemaToIteratorSetting() throws Exception {
        // Given
        final IteratorSetting setting = Mockito.mock(IteratorSetting.class);
        final Schema schema = Mockito.mock(Schema.class);
        final String compactSchemaJson = "CompactSchema";
        BDDMockito.given(schema.toCompactJson()).willReturn(compactSchemaJson.getBytes());
        // When
        new IteratorSettingBuilder(setting).schema(schema);
        // Then
        Mockito.verify(setting).addOption(SCHEMA, compactSchemaJson);
    }

    @Test
    public void shouldAddCompactViewToIteratorSetting() throws Exception {
        // Given
        final IteratorSetting setting = Mockito.mock(IteratorSetting.class);
        final View view = Mockito.mock(View.class);
        final String compactSchemaJson = "CompactSchema";
        BDDMockito.given(view.toCompactJson()).willReturn(compactSchemaJson.getBytes());
        // When
        new IteratorSettingBuilder(setting).view(view);
        // Then
        Mockito.verify(setting).addOption(VIEW, compactSchemaJson);
    }
}

