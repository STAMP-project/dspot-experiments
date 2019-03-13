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
package com.github.dozermapper.core.factory;


import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import org.junit.Assert;
import org.junit.Test;


public class JAXBBeanFactoryTest extends AbstractDozerTest {
    private JAXBBeanFactory factory;

    private BeanContainer beanContainer;

    @Test
    public void testCreateBeanForSimpleJaxbClass() {
        Object obj = factory.createBean(null, null, "com.github.dozermapper.core.vo.jaxb.employee.EmployeeType", beanContainer);
        Assert.assertNotNull("Object can not be null", obj);
        Assert.assertEquals("com.github.dozermapper.core.vo.jaxb.employee.EmployeeType", obj.getClass().getName());
    }

    @Test(expected = MappingException.class)
    public void testCreateBeanClassNotFoundException() {
        factory.createBean(null, null, "ve.ve.DE", beanContainer);
    }

    @Test
    public void testCreateBeanForInnerJaxbClass() {
        Object obj = factory.createBean(null, null, "com.github.dozermapper.core.vo.jaxb.employee.EmployeeWithInnerClass$Address", beanContainer);
        Assert.assertNotNull(obj);
        Assert.assertEquals("com.github.dozermapper.core.vo.jaxb.employee.EmployeeWithInnerClass$Address", obj.getClass().getName());
    }

    @Test
    public void testCreateBeanForNestedInnerJaxbClass() {
        Object obj = factory.createBean(null, null, "com.github.dozermapper.core.vo.jaxb.employee.EmployeeWithInnerClass$Address$State", beanContainer);
        Assert.assertNotNull(obj);
        Assert.assertEquals("com.github.dozermapper.core.vo.jaxb.employee.EmployeeWithInnerClass$Address$State", obj.getClass().getName());
    }
}

