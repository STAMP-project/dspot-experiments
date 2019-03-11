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


import com.github.dozermapper.core.Mapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Assert;
import org.junit.Test;


public class ConstructorTest extends AbstractFunctionalTest {
    private Mapper beanMapper;

    @Test
    public void dateFormat() {
        DateFormat instance = SimpleDateFormat.getInstance();
        GregorianCalendar calendar = new GregorianCalendar(1, 1, 1);
        instance.setCalendar(calendar);
        ConstructorTest.Container source = new ConstructorTest.Container();
        source.setDateFormat(instance);
        ConstructorTest.Container target = new ConstructorTest.Container();
        beanMapper.map(source, target);
        Assert.assertNotSame(source.getDateFormat(), target.getDateFormat());
        Assert.assertNotNull(target.getDateFormat());
        Assert.assertEquals(calendar, target.getDateFormat().getCalendar());
    }

    @Test
    public void xmlCalendar() throws DatatypeConfigurationException {
        ConstructorTest.Container source = new ConstructorTest.Container();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Date.from(Instant.EPOCH));
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        source.setCalendar(calendar);
        ConstructorTest.Container target = new ConstructorTest.Container();
        beanMapper.map(source, target);
        Assert.assertNotSame(source.getCalendar(), target.getCalendar());
        Assert.assertEquals(calendar, target.getCalendar());
    }

    public static class Container {
        DateFormat dateFormat;

        XMLGregorianCalendar calendar;

        public DateFormat getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(DateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }

        public XMLGregorianCalendar getCalendar() {
            return calendar;
        }

        public void setCalendar(XMLGregorianCalendar calendar) {
            this.calendar = calendar;
        }
    }
}

