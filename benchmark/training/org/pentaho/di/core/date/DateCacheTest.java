/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.date;


import java.util.Calendar;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class DateCacheTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    @SuppressWarnings("deprecation")
    @Test
    public void testDateCache() {
        DateCache cache = new DateCache();
        cache.populate("yyyy-MM-dd", 2016, 2016);
        Assert.assertEquals(366, cache.getSize());// Leap year

        Assert.assertEquals(Calendar.FEBRUARY, cache.lookupDate("2016-02-29").getMonth());
        Assert.assertEquals(29, cache.lookupDate("2016-02-29").getDate());
        Assert.assertEquals((2016 - 1900), cache.lookupDate("2016-02-29").getYear());
    }
}

