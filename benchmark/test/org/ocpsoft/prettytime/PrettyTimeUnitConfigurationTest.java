/**
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.prettytime;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Minute;


public class PrettyTimeUnitConfigurationTest {
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    // Stores current locale so that it can be restored
    private Locale locale;

    @Test
    public void testRightNow() throws Exception {
        Date ref = new Date(0);
        Date then = new Date(2);
        PrettyTime t = new PrettyTime(ref);
        TimeFormat format = t.removeUnit(JustNow.class);
        Assert.assertNotNull(format);
        Assert.assertEquals("2 milliseconds from now", t.format(then));
    }

    @Test
    public void testMinutesFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        TimeFormat format = t.removeUnit(Minute.class);
        Assert.assertNotNull(format);
        Assert.assertEquals("720 seconds from now", t.format(new Date(((1000 * 60) * 12))));
    }

    @Test
    public void testHoursFromNow() throws Exception {
        PrettyTime t = new PrettyTime(new Date(0));
        TimeFormat format = t.removeUnit(Hour.class);
        Assert.assertNotNull(format);
        Assert.assertEquals("180 minutes from now", t.format(new Date((((1000 * 60) * 60) * 3))));
    }
}

