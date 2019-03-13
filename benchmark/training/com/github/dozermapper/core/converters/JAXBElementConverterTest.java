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
package com.github.dozermapper.core.converters;


import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.vo.jaxb.employee.EmployeeType;
import com.github.dozermapper.core.vo.jaxb.employee.EmployeeWithInnerClass;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Assert;
import org.junit.Test;


public class JAXBElementConverterTest extends AbstractDozerTest {
    public static final int YEAR = 101;

    public static final int MONTH = 1;

    public static final int DAY = 1;

    private JAXBElementConverter converter;

    private BeanContainer beanContainer;

    @Test
    public void stringToJAXBElementConversion() {
        converter = new JAXBElementConverter(EmployeeWithInnerClass.class.getCanonicalName(), "parentName", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        Object conversion = converter.convert(JAXBElement.class, "dummy");
        Assert.assertNotNull(conversion);
        Assert.assertEquals("javax.xml.bind.JAXBElement", conversion.getClass().getCanonicalName());
        Assert.assertEquals("java.lang.String", JAXBElement.class.cast(conversion).getDeclaredType().getCanonicalName());
        Assert.assertEquals("dummy", JAXBElement.class.cast(conversion).getValue());
    }

    @Test
    public void xmlgregoriancalendarToJAXBElementConversion() throws Exception {
        converter = new JAXBElementConverter(EmployeeType.class.getCanonicalName(), "birthDate", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        DatatypeFactory instance = DatatypeFactory.newInstance();
        XMLGregorianCalendar calendar = instance.newXMLGregorianCalendar(new GregorianCalendar(2001, 1, 1));
        Object conversion = converter.convert(JAXBElement.class, calendar);
        Assert.assertNotNull(conversion);
        Assert.assertEquals("javax.xml.bind.JAXBElement", conversion.getClass().getCanonicalName());
        Assert.assertEquals("javax.xml.datatype.XMLGregorianCalendar", JAXBElement.class.cast(conversion).getDeclaredType().getCanonicalName());
        Assert.assertEquals(calendar.toString(), JAXBElement.class.cast(conversion).getValue().toString());
    }

    @Test
    public void calendarToJAXBElementConversion() throws Exception {
        converter = new JAXBElementConverter(EmployeeType.class.getCanonicalName(), "birthDate", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        Calendar calendar = new GregorianCalendar(2001, 1, 1);
        Object conversion = converter.convert(JAXBElement.class, calendar);
        Assert.assertNotNull(conversion);
        Assert.assertEquals("javax.xml.bind.JAXBElement", conversion.getClass().getCanonicalName());
        Assert.assertEquals("javax.xml.datatype.XMLGregorianCalendar", JAXBElement.class.cast(conversion).getDeclaredType().getCanonicalName());
        DatatypeFactory instance = DatatypeFactory.newInstance();
        XMLGregorianCalendar expected = instance.newXMLGregorianCalendar(new GregorianCalendar(2001, 1, 1));
        Assert.assertEquals(expected.toString(), JAXBElement.class.cast(conversion).getValue().toString());
    }

    @Test
    public void dateToJAXBElementConversion() throws Exception {
        converter = new JAXBElementConverter(EmployeeType.class.getCanonicalName(), "birthDate", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        Date calendar = new Date(JAXBElementConverterTest.YEAR, JAXBElementConverterTest.MONTH, JAXBElementConverterTest.DAY);
        Object conversion = converter.convert(JAXBElement.class, calendar);
        Assert.assertNotNull(conversion);
        Assert.assertEquals("javax.xml.bind.JAXBElement", conversion.getClass().getCanonicalName());
        Assert.assertEquals("javax.xml.datatype.XMLGregorianCalendar", JAXBElement.class.cast(conversion).getDeclaredType().getCanonicalName());
        DatatypeFactory instance = DatatypeFactory.newInstance();
        XMLGregorianCalendar expected = instance.newXMLGregorianCalendar(new GregorianCalendar(2001, 1, 1));
        Assert.assertEquals(expected.toString(), JAXBElement.class.cast(conversion).getValue().toString());
    }

    @Test
    public void dateToJAXBElementStringConversion() {
        converter = new JAXBElementConverter(EmployeeWithInnerClass.class.getCanonicalName(), "parentName", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        Date calendar = new Date(JAXBElementConverterTest.YEAR, JAXBElementConverterTest.MONTH, JAXBElementConverterTest.DAY);
        Object conversion = converter.convert(JAXBElement.class, calendar);
        Assert.assertNotNull(conversion);
        Assert.assertEquals("javax.xml.bind.JAXBElement", conversion.getClass().getCanonicalName());
        Assert.assertEquals("java.lang.String", JAXBElement.class.cast(conversion).getDeclaredType().getCanonicalName());
        Assert.assertEquals("01.02.2001", JAXBElement.class.cast(conversion).getValue());
    }

    @Test
    public void calendarToJAXBElementStringConversion() {
        converter = new JAXBElementConverter(EmployeeWithInnerClass.class.getCanonicalName(), "parentName", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        Calendar calendar = new GregorianCalendar(2001, 1, 1);
        Object conversion = converter.convert(JAXBElement.class, calendar);
        Assert.assertNotNull(conversion);
        Assert.assertEquals("javax.xml.bind.JAXBElement", conversion.getClass().getCanonicalName());
        Assert.assertEquals("java.lang.String", JAXBElement.class.cast(conversion).getDeclaredType().getCanonicalName());
        Assert.assertEquals("01.02.2001", JAXBElement.class.cast(conversion).getValue());
    }

    @Test(expected = MappingException.class)
    public void createFieldNotFoundException() {
        converter = new JAXBElementConverter(EmployeeWithInnerClass.class.getCanonicalName(), "vds", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        converter.convert(JAXBElement.class, "dummy");
    }

    @Test
    public void stringTypeBeanId() {
        converter = new JAXBElementConverter(EmployeeWithInnerClass.class.getCanonicalName(), "parentName", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        String beanId = converter.getBeanId();
        Assert.assertNotNull(beanId);
        Assert.assertEquals("java.lang.String", beanId);
    }

    @Test
    public void xmlgregoriancalendarTypeBeanId() {
        converter = new JAXBElementConverter(EmployeeType.class.getCanonicalName(), "birthDate", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        String beanId = converter.getBeanId();
        Assert.assertNotNull(beanId);
        Assert.assertEquals("javax.xml.datatype.XMLGregorianCalendar", beanId);
    }

    @Test(expected = MappingException.class)
    public void beanIdFieldNotFoundException() {
        converter = new JAXBElementConverter(EmployeeWithInnerClass.class.getCanonicalName(), "vds", new SimpleDateFormat("dd.MM.yyyy"), beanContainer);
        converter.getBeanId();
    }
}

