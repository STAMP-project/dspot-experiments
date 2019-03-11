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


import com.github.dozermapper.core.vo.jaxb.employee.EmployeeType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JAXBBeansMapping_WithExceptionLoggedTest extends AbstractFunctionalTest {
    private final Logger LOG = LoggerFactory.getLogger(JAXBBeansMapping_WithExceptionLoggedTest.class);

    @Test
    public void testJAXBListWithNoSetter() {
        LOG.error(("WithExceptionsLoggedTest; 'NoSuchMethodException: Unable to determine write method for Field: 'ids' in " + "Class: class com.github.dozermapper.core.vo.jaxb.employee.EmployeeType'"));
        JAXBBeansMappingTest.ListContainer source = new JAXBBeansMappingTest.ListContainer();
        source.getList().add(1);
        source.getList().add(2);
        source.getSubordinates().add(new JAXBBeansMappingTest.StringContainer("John"));
        EmployeeType result = mapper.map(source, EmployeeType.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getIds().size());
        Assert.assertTrue(result.getIds().contains(1));
        Assert.assertTrue(result.getIds().contains(2));
        Assert.assertEquals(1, result.getSubordinates().size());
        Assert.assertEquals("John", result.getSubordinates().get(0).getFirstName());
    }
}

