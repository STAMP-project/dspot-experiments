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
package com.github.dozermapper.core.loader;


import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.classmap.ClassMappings;
import com.github.dozermapper.core.classmap.Configuration;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.loader.xml.MappingFileReader;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import org.junit.Assert;
import org.junit.Test;


public class MappingsParserTest extends AbstractDozerTest {
    private MappingsParser parser;

    private BeanContainer beanContainer;

    private DestBeanCreator destBeanCreator;

    private PropertyDescriptorFactory propertyDescriptorFactory;

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateMapIds() {
        MappingFileReader fileReader = new MappingFileReader(new com.github.dozermapper.core.loader.xml.XMLParserFactory(beanContainer), new com.github.dozermapper.core.loader.xml.XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory), beanContainer);
        MappingFileData mappingFileData = fileReader.read("mappings/duplicateMapIdsMapping.xml");
        parser.processMappings(mappingFileData.getClassMaps(), new Configuration());
        Assert.fail("should have thrown exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDetectDuplicateMapping() {
        MappingFileReader fileReader = new MappingFileReader(new com.github.dozermapper.core.loader.xml.XMLParserFactory(beanContainer), new com.github.dozermapper.core.loader.xml.XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory), beanContainer);
        MappingFileData mappingFileData = fileReader.read("mappings/duplicateMapping.xml");
        parser.processMappings(mappingFileData.getClassMaps(), new Configuration());
        Assert.fail("should have thrown exception");
    }

    @Test
    public void testEmptyMappings() {
        MappingFileData mappingFileData = new MappingFileData();
        ClassMappings result = parser.processMappings(mappingFileData.getClassMaps(), new Configuration());
        Assert.assertNotNull("result should not be null", result);
        Assert.assertEquals("result should be empty", 0, result.size());
    }
}

