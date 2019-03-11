/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.importer;


import java.util.Calendar;
import org.junit.Test;


public class DatesTest {
    @Test
    public void testParse() {
        doTestParse(date(2012, Calendar.FEBRUARY, 6, 13, 12, 59, 123), "2012-02-06T13:12:59.123Z");
        doTestParse(date(2012, Calendar.FEBRUARY, 6, 13, 12, 59, 0), "2012-02-06T13:12:59Z");
        doTestParse(date(2012, Calendar.FEBRUARY, 6, 13, 12, 123, 0), "2012-02-06T13:12:123Z");
        doTestParse(date(2012, Calendar.FEBRUARY, 6, 13, 12, 0, 0), "2012-02-06T13:12Z");
        doTestParse(date(2012, Calendar.FEBRUARY, 6, 13, 0, 0, 0), "2012-02-06T13Z");
        doTestParse(date(2012, Calendar.FEBRUARY, 6, 0, 0, 0, 0), "2012-02-06");
        doTestParse(date(2012, Calendar.FEBRUARY, 1, 0, 0, 0, 0), "2012-02");
        doTestParse(date(2012, Calendar.JANUARY, 1, 0, 0, 0, 0), "2012");
    }
}

