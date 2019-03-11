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


import EmployeeWithInnerClass.Address.State;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.jaxb.employee.EmployeeWithInnerClass;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.Assert;
import org.junit.Test;


public class JAXBBeansMappingTest extends AbstractFunctionalTest {
    @Test
    public void testTrivial() {
        Class<?> type = MappingUtils.loadClass("com.github.dozermapper.core.vo.jaxb.employee.EmployeeType", new BeanContainer());
        Assert.assertNotNull(type);
    }

    @Test
    public void testSimple() {
        TestObject source = new TestObject();
        source.setOne("ABC");
        EmployeeWithInnerClass result = mapper.map(source, EmployeeWithInnerClass.class);
        Assert.assertNotNull(result);
        Assert.assertEquals("ABC", result.getFirstName());
    }

    @Test
    public void testNestedInnerClass() {
        TestObject source = new TestObject();
        source.setOne("Name");
        EmployeeWithInnerClass.Address.State result = mapper.map(source, State.class);
        Assert.assertNotNull(result);
        Assert.assertEquals("Name", result.getName());
    }

    @Test
    public void testDateToXMLGregorianCalendar() {
        TestObject source = new TestObject();
        Date now = new Date();
        source.setDate(now);
        EmployeeWithInnerClass result = mapper.map(source, EmployeeWithInnerClass.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(now.getTime(), result.getBirthDate().toGregorianCalendar().getTimeInMillis());
    }

    @Test
    public void testXMLGregorianCalendarToDate() throws DatatypeConfigurationException {
        Calendar cal = GregorianCalendar.getInstance();
        EmployeeWithInnerClass source = new EmployeeWithInnerClass();
        source.setBirthDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(((GregorianCalendar) (cal))));
        TestObject result = mapper.map(source, TestObject.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(cal.getTimeInMillis(), result.getDate().getTime());
    }

    public static class ListContainer {
        private List<Integer> list = new ArrayList<>();

        private List<JAXBBeansMappingTest.StringContainer> subordinates = new ArrayList<>();

        public List<Integer> getList() {
            return list;
        }

        public List<JAXBBeansMappingTest.StringContainer> getSubordinates() {
            return subordinates;
        }
    }

    public static class StringContainer {
        private String value;

        public StringContainer() {
        }

        public StringContainer(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

