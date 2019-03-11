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
package com.github.dozermapper.core.loader.xml;


import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.classmap.ClassMap;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.loader.MappingsSource;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.ResourceLoader;
import java.net.URL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


public class XMLParserTest extends AbstractDozerTest {
    MappingsSource<Document> parser;

    ResourceLoader loader;

    BeanContainer beanContainer;

    DestBeanCreator destBeanCreator;

    PropertyDescriptorFactory propertyDescriptorFactory;

    @Test
    public void testParse() throws Exception {
        URL url = loader.getResource("mappings/testDozerBeanMapping.xml");
        Document document = createParser().parse(url.openStream());
        parser = new XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory);
        MappingFileData mappings = parser.read(document);
        Assert.assertNotNull(mappings);
    }

    /**
     * This tests checks that the customconverterparam reaches the
     * fieldmapping.
     */
    @Test
    public void testParseCustomConverterParam() throws Exception {
        URL url = loader.getResource("mappings/fieldCustomConverterParam.xml");
        Document document = createParser().parse(url.openStream());
        parser = new XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory);
        MappingFileData mappings = parser.read(document);
        Assert.assertNotNull("The mappings should not be null", mappings);
        List<ClassMap> mapping = mappings.getClassMaps();
        Assert.assertNotNull("The list of mappings should not be null", mapping);
        Assert.assertEquals("There should be one mapping", 3, mapping.size());
        ClassMap classMap = mapping.get(0);
        Assert.assertNotNull("The classmap should not be null", classMap);
        List<FieldMap> fieldMaps = classMap.getFieldMaps();
        Assert.assertNotNull("The fieldmaps should not be null", fieldMaps);
        Assert.assertEquals("The fieldmap should have one mapping", 1, fieldMaps.size());
        FieldMap fieldMap = fieldMaps.get(0);
        Assert.assertNotNull("The fieldmap should not be null", fieldMap);
        Assert.assertEquals("The customconverterparam should be correct", "CustomConverterParamTest", fieldMap.getCustomConverterParam());
    }
}

