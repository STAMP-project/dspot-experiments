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


import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.metadata.ClassMappingMetadata;
import com.github.dozermapper.core.metadata.MappingMetadata;
import com.github.dozermapper.core.metadata.MetadataLookupException;
import com.github.dozermapper.core.vo.metadata.ClassA;
import com.github.dozermapper.core.vo.metadata.ClassB;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the new metadata interfaces.
 */
public class MetadataTest extends AbstractFunctionalTest {
    private static final String MAPPING_FILE = "mappings/metadataTest.xml";

    private static final String CLASS_A = "com.github.dozermapper.core.vo.metadata.ClassA";

    private static final String CLASS_B = "com.github.dozermapper.core.vo.metadata.ClassB";

    private static final String CLASS_NONEXISTENT = "com.github.dozermapper.core.vo.metadata.ClassNonExistent";

    private MappingMetadata mapMetadata;

    @Test
    public void testGetClassMappings() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappings();
        Assert.assertTrue(((metadata.size()) == 4));
    }

    @Test
    public void testGetClassMappingBySource() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappingsBySource(ClassA.class);
        Assert.assertTrue(((metadata.size()) == 1));
    }

    @Test
    public void testGetClassMappingByDestination() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappingsByDestination(ClassB.class);
        Assert.assertTrue(((metadata.size()) == 1));
    }

    @Test
    public void testGetClassMapping() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMapping(ClassA.class, ClassB.class);
        Assert.assertNotNull(classMetadata);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetClassMappingBySourceNull() {
        mapMetadata.getClassMappingsBySource(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetClassMappingByDestinationNull() {
        mapMetadata.getClassMappingsByDestination(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetClassMappingByNull1() {
        mapMetadata.getClassMapping(ClassA.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetClassMappingByNull2() {
        mapMetadata.getClassMapping(null, ClassB.class);
    }

    @Test(expected = MetadataLookupException.class)
    public void testGetClassMappingByNonExistent() {
        mapMetadata.getClassMapping(DozerBeanMapper.class, ClassB.class);
    }

    @Test
    public void testGetClassMappingBySourceName() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappingsBySourceName(MetadataTest.CLASS_A);
        Assert.assertTrue(((metadata.size()) == 1));
    }

    @Test
    public void testGetClassMappingByDestinationName() {
        List<ClassMappingMetadata> metadata = mapMetadata.getClassMappingsByDestinationName(MetadataTest.CLASS_B);
        Assert.assertTrue(((metadata.size()) == 1));
    }

    @Test
    public void testGetClassMappingByName() {
        ClassMappingMetadata classMetadata = mapMetadata.getClassMappingByName(MetadataTest.CLASS_A, MetadataTest.CLASS_B);
        Assert.assertNotNull(classMetadata);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetClassMappingBySourceNameNull() {
        mapMetadata.getClassMappingsBySourceName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetClassMappingByDestinationNameNull() {
        mapMetadata.getClassMappingsByDestinationName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetClassMappingByNameNull1() {
        mapMetadata.getClassMappingByName(MetadataTest.CLASS_A, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetClassMappingByNameNull2() {
        mapMetadata.getClassMappingByName(null, MetadataTest.CLASS_B);
    }

    @Test(expected = MetadataLookupException.class)
    public void testGetClassMappingByNameNonExistent() {
        mapMetadata.getClassMappingByName(MetadataTest.CLASS_NONEXISTENT, MetadataTest.CLASS_B);
    }
}

