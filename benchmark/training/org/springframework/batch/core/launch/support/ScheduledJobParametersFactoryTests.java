/**
 * Copyright 2006-2013 the original author or authors.
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
package org.springframework.batch.core.launch.support;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import junit.framework.TestCase;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.util.StringUtils;


/**
 *
 *
 * @author Lucas Ward
 */
public class ScheduledJobParametersFactoryTests extends TestCase {
    ScheduledJobParametersFactory factory;

    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public void testGetParameters() throws Exception {
        String jobKey = "job.key=myKey";
        String scheduleDate = "schedule.date=2008/01/23";
        String vendorId = "vendor.id=33243243";
        String[] args = new String[]{ jobKey, scheduleDate, vendorId };
        JobParameters props = factory.getJobParameters(StringUtils.splitArrayElementsIntoProperties(args, "="));
        TestCase.assertNotNull(props);
        TestCase.assertEquals("myKey", props.getString("job.key"));
        TestCase.assertEquals("33243243", props.getString("vendor.id"));
        Date date = dateFormat.parse("01/23/2008");
        TestCase.assertEquals(date, props.getDate("schedule.date"));
    }

    public void testGetProperties() throws Exception {
        JobParameters parameters = new JobParametersBuilder().addDate("schedule.date", dateFormat.parse("01/23/2008")).addString("job.key", "myKey").addString("vendor.id", "33243243").toJobParameters();
        Properties props = factory.getProperties(parameters);
        TestCase.assertNotNull(props);
        TestCase.assertEquals("myKey", props.getProperty("job.key"));
        TestCase.assertEquals("33243243", props.getProperty("vendor.id"));
        TestCase.assertEquals("2008/01/23", props.getProperty("schedule.date"));
    }

    public void testEmptyArgs() {
        JobParameters props = factory.getJobParameters(new Properties());
        TestCase.assertTrue(props.getParameters().isEmpty());
    }

    public void testNullArgs() {
        TestCase.assertEquals(new JobParameters(), factory.getJobParameters(null));
        TestCase.assertEquals(new Properties(), factory.getProperties(null));
    }

    public void testGetParametersWithDateFormat() throws Exception {
        String[] args = new String[]{ "schedule.date=2008/23/01" };
        factory.setDateFormat(new SimpleDateFormat("yyyy/dd/MM"));
        JobParameters props = factory.getJobParameters(StringUtils.splitArrayElementsIntoProperties(args, "="));
        TestCase.assertNotNull(props);
        Date date = dateFormat.parse("01/23/2008");
        TestCase.assertEquals(date, props.getDate("schedule.date"));
    }

    public void testGetParametersWithBogusDate() throws Exception {
        String[] args = new String[]{ "schedule.date=20080123" };
        try {
            factory.getJobParameters(StringUtils.splitArrayElementsIntoProperties(args, "="));
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            TestCase.assertTrue(("Message should contain wrong date: " + message), contains(message, "20080123"));
        }
    }
}

