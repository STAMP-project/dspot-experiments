/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.core.repository.dao;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test case showing some weirdnesses in date formatting. Looks like a bug in
 * SimpleDateFormat / GregorianCalendar, and it affects the JSON deserialization
 * that we use in the ExecutionContext around daylight savings.
 *
 * @author Dave Syer
 */
@RunWith(Parameterized.class)
public class DateFormatTests {
    private final SimpleDateFormat format;

    private final String input;

    private final int hour;

    private final String output;

    /**
     *
     */
    public DateFormatTests(String pattern, String input, String output, int hour) {
        this.output = output;
        this.format = new SimpleDateFormat(pattern, Locale.UK);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.input = input;
        this.hour = hour;
    }

    @Test
    public void testDateFormat() throws Exception {
        Date date = format.parse(input);
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"), Locale.UK);
        calendar.setTime(date);
        // System.err.println(format.toPattern() + " + " + input + " --> " +
        // calendar.getTime());
        // This assertion is true...
        Assert.assertEquals(hour, calendar.get(Calendar.HOUR_OF_DAY));
        // ...but the toString() does not match in 1970 and 1971
        Assert.assertEquals(output, format.format(date));
    }
}

