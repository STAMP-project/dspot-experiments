/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
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
package org.terracotta.quartz.upgradability.serialization;


import java.io.IOException;
import java.text.ParseException;
import java.util.Comparator;
import org.junit.Test;
import org.quartz.impl.calendar.CronCalendar;
import org.quartz.impl.calendar.HolidayCalendar;


/**
 *
 *
 * @author cdennis
 */
public class CronCalendarSerializationTest {
    private static final Comparator<CronCalendar> COMPARATOR = new Comparator<CronCalendar>() {
        @Override
        public int compare(CronCalendar o1, CronCalendar o2) {
            return (((o1.getCronExpression().getCronExpression().equals(o2.getCronExpression().getCronExpression())) && (o1.getTimeZone().equals(o2.getTimeZone()))) && (nullSafeEquals(o1.getDescription(), o2.getDescription()))) && (((o1.getBaseCalendar()) == null) == ((o2.getBaseCalendar()) == null)) ? 0 : -1;
        }
    };

    @Test
    public void testWithoutBase() throws IOException, ClassNotFoundException, ParseException {
        CronCalendar cc = new CronCalendar("0 0 12 * * ?");
        validateSerializedForm(cc, CronCalendarSerializationTest.COMPARATOR, "serializedforms/CronCalendarSerializationTest.testWithoutBase.ser");
    }

    @Test
    public void testWithBase() throws IOException, ClassNotFoundException, ParseException {
        CronCalendar cc = new CronCalendar(new HolidayCalendar(), "0 0 12 * * ?");
        validateSerializedForm(cc, CronCalendarSerializationTest.COMPARATOR, "serializedforms/CronCalendarSerializationTest.testWithBase.ser");
    }

    @Test
    public void testWithTimezone() throws IOException, ClassNotFoundException, ParseException {
        CronCalendar cc = new CronCalendar(new HolidayCalendar(), "0 0 12 * * ?", new SimplisticTimeZone("Terra Australis"));
        validateSerializedForm(cc, CronCalendarSerializationTest.COMPARATOR, "serializedforms/CronCalendarSerializationTest.testWithTimezone.ser");
    }
}

