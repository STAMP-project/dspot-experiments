/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.jmx.export;


import java.text.SimpleDateFormat;
import java.util.Date;
import javax.management.ObjectName;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jmx.AbstractJmxTests;


/**
 *
 *
 * @author Rob Harrop
 */
public class CustomEditorConfigurerTests extends AbstractJmxTests {
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

    @Test
    public void testDatesInJmx() throws Exception {
        // System.out.println(getServer().getClass().getName());
        ObjectName oname = new ObjectName("bean:name=dateRange");
        Date startJmx = ((Date) (getServer().getAttribute(oname, "StartDate")));
        Date endJmx = ((Date) (getServer().getAttribute(oname, "EndDate")));
        Assert.assertEquals("startDate ", getStartDate(), startJmx);
        Assert.assertEquals("endDate ", getEndDate(), endJmx);
    }

    @Test
    public void testGetDates() throws Exception {
        DateRange dr = ((DateRange) (getContext().getBean("dateRange")));
        Assert.assertEquals("startDate ", getStartDate(), dr.getStartDate());
        Assert.assertEquals("endDate ", getEndDate(), dr.getEndDate());
    }
}

