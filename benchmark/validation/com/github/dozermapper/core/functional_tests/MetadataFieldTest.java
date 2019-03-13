/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.metadata.ClassMappingMetadata;
import com.github.dozermapper.core.metadata.FieldMappingMetadata;
import com.github.dozermapper.core.metadata.MappingMetadata;
import com.github.dozermapper.core.metadata.MetadataLookupException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the new mapping catalogue interfaces.
 */
public class MetadataFieldTest extends AbstractFunctionalTest {
    private static final String MAPPING_FILE = "mappings/metadataTest.xml";

    private static final String CLASS_A = "com.github.dozermapper.core.vo.metadata.ClassA";

    private static final String CLASS_B = "com.github.dozermapper.core.vo.metadata.ClassB";

    private static final String NONEXISTENTFIELD = "noField";

    private static final String AUTOFIELD = "autoField";

    private static final String CUSTOM_FIELD_A = "customFieldA";

    private static final String CUSTOM_FIELD_B = "customFieldB";

    private MappingMetadata mapMetadata;

    @Test
    public void testFieldMaps() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(MetadataFieldTest.CLASS_A, MetadataFieldTest.CLASS_B);
        List<FieldMappingMetadata> fieldMetadata = classMetadata.getFieldMappings();
        Assert.assertTrue(MetadataFieldTest.AUTOFIELD, ((fieldMetadata.size()) == 3));
    }

    @Test
    public void testAutoFieldMap() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(MetadataFieldTest.CLASS_A, MetadataFieldTest.CLASS_B);
        FieldMappingMetadata fieldMetadata = classMetadata.getFieldMappingBySource(MetadataFieldTest.AUTOFIELD);
        Assert.assertEquals(MetadataFieldTest.AUTOFIELD, fieldMetadata.getDestinationName());
    }

    @Test
    public void testCustomFieldMap() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(MetadataFieldTest.CLASS_A, MetadataFieldTest.CLASS_B);
        FieldMappingMetadata fieldMetadata = classMetadata.getFieldMappingBySource(MetadataFieldTest.CUSTOM_FIELD_A);
        Assert.assertEquals(MetadataFieldTest.CUSTOM_FIELD_B, fieldMetadata.getDestinationName());
    }

    @Test
    public void testFieldMapByDestination() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(MetadataFieldTest.CLASS_A, MetadataFieldTest.CLASS_B);
        classMetadata.getFieldMappingByDestination(MetadataFieldTest.CUSTOM_FIELD_B);
    }

    @Test
    public void testCustomFieldMapBack() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(MetadataFieldTest.CLASS_A, MetadataFieldTest.CLASS_B);
        FieldMappingMetadata fieldMetadata = classMetadata.getFieldMappingByDestination(MetadataFieldTest.CUSTOM_FIELD_B);
        Assert.assertEquals(MetadataFieldTest.CUSTOM_FIELD_A, fieldMetadata.getSourceName());
    }

    @Test(expected = MetadataLookupException.class)
    public void testFieldMapNonExistantSource() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(MetadataFieldTest.CLASS_A, MetadataFieldTest.CLASS_B);
        classMetadata.getFieldMappingBySource(MetadataFieldTest.NONEXISTENTFIELD);
    }

    @Test(expected = MetadataLookupException.class)
    public void testFieldMapNonExistantDestination() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(MetadataFieldTest.CLASS_A, MetadataFieldTest.CLASS_B);
        classMetadata.getFieldMappingByDestination(MetadataFieldTest.NONEXISTENTFIELD);
    }
}

