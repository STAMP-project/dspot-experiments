

package org.traccar.reports;


public class AmplTripsTest extends org.traccar.BaseTest {
    private java.util.Date date(java.lang.String time) throws java.text.ParseException {
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return dateFormat.parse(time);
    }

    private org.traccar.model.Position position(java.lang.String time, double speed, double totalDistance) throws java.text.ParseException {
        org.traccar.model.Position position = new org.traccar.model.Position();
        if (time != null) {
            position.setTime(date(time));
        }
        position.setValid(true);
        position.setSpeed(speed);
        position.set(org.traccar.model.Position.KEY_TOTAL_DISTANCE, totalDistance);
        return position;
    }

    @org.junit.Test
    public void testDetectTripsSimple() throws java.text.ParseException {
        java.util.Collection<org.traccar.model.Position> data = java.util.Arrays.asList(position("2016-01-01 00:00:00.000", 0, 0), position("2016-01-01 00:01:00.000", 0, 0), position("2016-01-01 00:02:00.000", 10, 0), position("2016-01-01 00:03:00.000", 10, 1000), position("2016-01-01 00:04:00.000", 10, 2000), position("2016-01-01 00:05:00.000", 0, 3000), position("2016-01-01 00:06:00.000", 0, 3000));
        java.util.Collection<org.traccar.reports.model.TripReport> result = org.traccar.reports.Trips.detectTrips(0.01, 500, 300000, 300000, false, false, data);
        org.junit.Assert.assertNotNull(result);
        org.junit.Assert.assertFalse(result.isEmpty());
        org.traccar.reports.model.TripReport item = result.iterator().next();
        org.junit.Assert.assertEquals(date("2016-01-01 00:02:00.000"), item.getStartTime());
        org.junit.Assert.assertEquals(date("2016-01-01 00:05:00.000"), item.getEndTime());
        org.junit.Assert.assertEquals(180000, item.getDuration());
        org.junit.Assert.assertEquals(10, item.getAverageSpeed(), 0.01);
        org.junit.Assert.assertEquals(10, item.getMaxSpeed(), 0.01);
        org.junit.Assert.assertEquals(3000, item.getDistance(), 0.01);
    }
}

