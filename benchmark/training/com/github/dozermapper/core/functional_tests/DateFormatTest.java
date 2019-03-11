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


import com.github.dozermapper.core.vo.DateContainer;
import com.github.dozermapper.core.vo.DateObjectDest;
import com.github.dozermapper.core.vo.DateObjectSource;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Assert;
import org.junit.Test;


public class DateFormatTest extends AbstractFunctionalTest {
    @Test
    public void testConversion() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2009, 6, 3, 12, 20, 10);
        DateContainer result = mapper.map(calendar.getTime(), DateContainer.class);
        Assert.assertNotNull(result);
        Assert.assertEquals("2009-07-03 12:20:10", result.getDate());
        Assert.assertEquals("2009-07-03 12:20:10", result.getSet().iterator().next());
    }

    @Test
    public void testConversion_Calendar() {
        GregorianCalendar cal = new GregorianCalendar(2009, 2, 3);
        Date sourceValue = cal.getTime();
        Calendar result = mapper.map(sourceValue, Calendar.class);
        Assert.assertEquals(cal.getTime(), result.getTime());
    }

    @Test
    public void testConversion_CalendarInsideBean() {
        GregorianCalendar cal = new GregorianCalendar(2009, 2, 3);
        Date sourceValue = cal.getTime();
        DateFormatTest.Source source = new DateFormatTest.Source();
        source.setDate(sourceValue);
        DateFormatTest.Destination result = mapper.map(source, DateFormatTest.Destination.class);
        Assert.assertEquals(cal.getTime(), result.getDate().getTime());
    }

    @Test
    public void testGregorianCalendar() throws Exception {
        DateObjectSource source = new DateObjectSource();
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar();
        xmlGregorianCalendar.setTimezone((2 * 60));
        source.setRecalledDate(xmlGregorianCalendar);
        DateObjectDest dest = mapper.map(source, DateObjectDest.class);
        Assert.assertEquals(source.getRecalledDate().getYear(), dest.getRecDate().getYear());
        Assert.assertEquals(source.getRecalledDate().getMonth(), dest.getRecDate().getMonth());
        Assert.assertEquals(source.getRecalledDate().getDay(), dest.getRecDate().getDay());
        Assert.assertEquals(source.getRecalledDate().getHour(), dest.getRecDate().getHour());
        Assert.assertEquals(source.getRecalledDate().getMinute(), dest.getRecDate().getMinute());
        Assert.assertEquals(source.getRecalledDate().getSecond(), dest.getRecDate().getSecond());
        Assert.assertEquals(source.getRecalledDate().getTimezone(), dest.getRecDate().getTimezone());
        // Test when instance exist
        dest = new DateObjectDest();
        dest.setRecDate(DatatypeFactory.newInstance().newXMLGregorianCalendar());
        mapper.map(source, dest);
        Assert.assertEquals(source.getRecalledDate(), dest.getRecDate());
    }

    public static class Source {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public static class Destination {
        private Calendar calendar;

        public Calendar getDate() {
            return calendar;
        }

        public void setDate(Calendar calendar) {
            this.calendar = calendar;
        }
    }
}

