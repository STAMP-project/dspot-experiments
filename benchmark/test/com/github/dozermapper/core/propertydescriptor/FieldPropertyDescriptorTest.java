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
package com.github.dozermapper.core.propertydescriptor;


import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FieldPropertyDescriptorTest extends AbstractDozerTest {
    private DestBeanCreator destBeanCreator = new DestBeanCreator(new BeanContainer());

    @Test(expected = MappingException.class)
    public void testNoSuchField() {
        new FieldPropertyDescriptor(String.class, "nosuchfield", false, 0, null, null, destBeanCreator);
        Assert.fail();
    }

    @Test
    public void testConstructor() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "hidden", false, 0, null, null, destBeanCreator);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(Integer.TYPE, descriptor.getPropertyType());
        Assert.assertNotNull(descriptor.getPropertyValue(new FieldPropertyDescriptorTest.Container()));
    }

    @Test
    public void getPropertyType() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "hidden", false, 0, null, null, destBeanCreator);
        Assert.assertEquals(Integer.TYPE, descriptor.getPropertyType());
    }

    @Test
    public void getPropertyTypeChained() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "container.value", false, 0, null, null, destBeanCreator);
        Assert.assertEquals(String.class, descriptor.getPropertyType());
    }

    @Test
    public void getPropertyValue() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "hidden", false, 0, null, null, destBeanCreator);
        Object result = descriptor.getPropertyValue(new FieldPropertyDescriptorTest.Container());
        Assert.assertEquals(42, result);
    }

    @Test
    public void getPropertyValueChained() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "container.hidden", false, 0, null, null, destBeanCreator);
        Object result = descriptor.getPropertyValue(new FieldPropertyDescriptorTest.Container("A"));
        Assert.assertEquals(42, result);
    }

    @Test
    public void setPropertyValue() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "value", false, 0, null, null, destBeanCreator);
        FieldPropertyDescriptorTest.Container bean = new FieldPropertyDescriptorTest.Container("A");
        descriptor.setPropertyValue(bean, "B", Mockito.mock(FieldMap.class));
        Assert.assertEquals("B", bean.value);
    }

    @Test
    public void setPropertyValueChained() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "container.value", false, 0, null, null, destBeanCreator);
        FieldPropertyDescriptorTest.Container bean = new FieldPropertyDescriptorTest.Container("");
        bean.container = new FieldPropertyDescriptorTest.Container("");
        descriptor.setPropertyValue(bean, "A", Mockito.mock(FieldMap.class));
        Assert.assertEquals("A", bean.container.value);
    }

    @Test
    public void setPropertyValueChained_ThirdLevel() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "container.container.value", false, 0, null, null, destBeanCreator);
        FieldPropertyDescriptorTest.Container bean = new FieldPropertyDescriptorTest.Container("X");
        bean.container = new FieldPropertyDescriptorTest.Container("X");
        bean.container.container = new FieldPropertyDescriptorTest.Container("X");
        descriptor.setPropertyValue(bean, "Y", Mockito.mock(FieldMap.class));
        Assert.assertEquals("Y", bean.container.container.value);
    }

    @Test
    public void setPropertyValueChained_IntermediateNull() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "container.value", false, 0, null, null, destBeanCreator);
        FieldPropertyDescriptorTest.Container bean = new FieldPropertyDescriptorTest.Container("");
        descriptor.setPropertyValue(bean, "A", Mockito.mock(FieldMap.class));
        Assert.assertEquals("A", bean.container.value);
    }

    @Test
    public void getPropertyValueChained_IntermediateNull() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "container.value", false, 0, null, null, destBeanCreator);
        FieldPropertyDescriptorTest.Container bean = new FieldPropertyDescriptorTest.Container("");
        Object value = descriptor.getPropertyValue(bean);
        Assert.assertEquals("", value);
    }

    @Test
    public void getPropertyValueIndexed() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "values", true, 0, null, null, destBeanCreator);
        FieldPropertyDescriptorTest.Container container = new FieldPropertyDescriptorTest.Container("");
        container.values.add("A");
        Object value = descriptor.getPropertyValue(container);
        Assert.assertEquals("A", value);
    }

    @Test
    public void setPropertyValueIndexed() {
        FieldPropertyDescriptor descriptor = new FieldPropertyDescriptor(FieldPropertyDescriptorTest.Container.class, "values", true, 0, null, null, destBeanCreator);
        FieldPropertyDescriptorTest.Container container = new FieldPropertyDescriptorTest.Container("");
        descriptor.setPropertyValue(container, "A", Mockito.mock(FieldMap.class));
        Assert.assertEquals(1, container.values.size());
        Assert.assertEquals("A", container.values.get(0));
    }

    public static class Container {
        public Container() {
        }

        public Container(String value) {
            this.value = value;
        }

        private int hidden = 42;

        String value;

        FieldPropertyDescriptorTest.Container container = this;

        List<String> values = new ArrayList<>();
    }
}

