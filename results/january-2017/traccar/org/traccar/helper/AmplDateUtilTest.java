

package org.traccar.helper;


public class AmplDateUtilTest {
    @org.junit.Test
    public void testCorrectDate() throws java.text.ParseException {
        java.text.DateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        org.junit.Assert.assertEquals(f.parse("2015-12-31 23:59:59"), org.traccar.helper.DateUtil.correctDate(f.parse("2016-01-01 00:00:01"), f.parse("2016-01-01 23:59:59"), java.util.Calendar.DAY_OF_MONTH));
        org.junit.Assert.assertEquals(f.parse("2016-01-01 00:00:02"), org.traccar.helper.DateUtil.correctDate(f.parse("2016-01-01 00:00:01"), f.parse("2016-01-01 00:00:02"), java.util.Calendar.DAY_OF_MONTH));
        org.junit.Assert.assertEquals(f.parse("2016-01-01 00:00:02"), org.traccar.helper.DateUtil.correctDate(f.parse("2016-01-01 00:00:01"), f.parse("2015-12-31 00:00:02"), java.util.Calendar.DAY_OF_MONTH));
    }
}

