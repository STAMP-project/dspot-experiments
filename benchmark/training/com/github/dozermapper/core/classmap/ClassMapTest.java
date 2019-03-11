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
package com.github.dozermapper.core.classmap;


import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.fieldmap.GenericFieldMap;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ClassMapTest extends AbstractDozerTest {
    private ClassMap classMap;

    private BeanContainer beanContainer;

    private DestBeanCreator destBeanCreator;

    private PropertyDescriptorFactory propertyDescriptorFactory;

    @Test
    public void testAddFieldMappings() {
        ClassMap cm = new ClassMap(null);
        GenericFieldMap fm = new GenericFieldMap(cm, beanContainer, destBeanCreator, propertyDescriptorFactory);
        cm.addFieldMapping(fm);
        Assert.assertNotNull(cm.getFieldMaps());
        Assert.assertTrue(((cm.getFieldMaps().size()) == 1));
        Assert.assertEquals(cm.getFieldMaps().get(0), fm);
    }

    @Test
    public void testSetFieldMappings() {
        ClassMap cm = new ClassMap(null);
        GenericFieldMap fm = new GenericFieldMap(cm, beanContainer, destBeanCreator, propertyDescriptorFactory);
        List<FieldMap> fmList = new ArrayList<>();
        fmList.add(fm);
        cm.setFieldMaps(fmList);
        Assert.assertNotNull(cm.getFieldMaps());
        Assert.assertTrue(((cm.getFieldMaps().size()) == (fmList.size())));
        Assert.assertEquals(cm.getFieldMaps().get(0), fmList.get(0));
    }

    @Test
    public void testGetFieldMapUsingDest() {
        Assert.assertNull(classMap.getFieldMapUsingDest("", true));
    }

    @Test
    public void testProvideAlternateName() {
        Assert.assertEquals("field1", classMap.provideAlternateName("Field1"));
    }
}

