package io.dropwizard.jdbi.timestamps;


import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;


/**
 * Test for handling translation between DateTime to SQL TIMESTAMP
 * in a different time zone
 */
public class JodaDateTimeSqlTimestampTest {
    private static final DateTimeFormatter ISO_FMT = ISODateTimeFormat.dateTimeNoMillis();

    private static DateTimeZone dbTimeZone = DateTimeZone.UTC;

    private static DBIClient dbiClient = new DBIClient(TimeZone.getDefault());

    private static DatabaseInTimeZone databaseInTimeZone = new DatabaseInTimeZone(TimeZone.getDefault());

    static {
        boolean done = false;
        while (!done) {
            try {
                final TimeZone timeZone = JodaDateTimeSqlTimestampTest.getRandomTimeZone();
                JodaDateTimeSqlTimestampTest.dbTimeZone = DateTimeZone.forTimeZone(timeZone);
                JodaDateTimeSqlTimestampTest.dbiClient = new DBIClient(timeZone);
                JodaDateTimeSqlTimestampTest.databaseInTimeZone = new DatabaseInTimeZone(timeZone);
                done = true;
            } catch (IllegalArgumentException e) {
                if (((e.getMessage()) == null) || (!(e.getMessage().contains("is not recognised")))) {
                    throw e;
                }
            }
        } 
    }

    private Handle handle;

    private JodaDateTimeSqlTimestampTest.FlightDao flightDao;

    @Test
    public void testInsertTimestamp() {
        final DateTime departureTime = JodaDateTimeSqlTimestampTest.ISO_FMT.parseDateTime("2015-04-01T06:00:00-05:00");
        final DateTime arrivalTime = JodaDateTimeSqlTimestampTest.ISO_FMT.parseDateTime("2015-04-01T21:00:00+02:00");
        final int result = flightDao.insert("C1671", "ORD", "DUS", departureTime, arrivalTime);
        assertThat(result).isGreaterThan(0);
        final Integer serverDepartureHour = ((Integer) (handle.select(("SELECT EXTRACT(HOUR FROM departure_time) departure_hour " + "FROM flights WHERE flight_id=?"), "C1671").get(0).get("departure_hour")));
        final Integer serverArrivalHour = ((Integer) (handle.select(("SELECT EXTRACT(HOUR FROM arrival_time) arrival_hour " + "FROM flights WHERE flight_id=?"), "C1671").get(0).get("arrival_hour")));
        assertThat(serverDepartureHour).isEqualTo(departureTime.withZone(JodaDateTimeSqlTimestampTest.dbTimeZone).getHourOfDay());
        assertThat(serverArrivalHour).isEqualTo(arrivalTime.withZone(JodaDateTimeSqlTimestampTest.dbTimeZone).getHourOfDay());
    }

    @Test
    public void testReadTimestamp() {
        int result = handle.insert(("INSERT INTO flights(flight_id, departure_airport, arrival_airport, departure_time, arrival_time) " + "VALUES ('C1671','ORD','DUS','2015-04-01T06:00:00-05:00','2015-04-01T21:00:00+02:00')"));
        assertThat(result).isGreaterThan(0);
        final DateTime departureTime = flightDao.getDepartureTime("C1671");
        final DateTime arrivalTime = flightDao.getArrivalTime("C1671");
        assertThat(departureTime).isEqualTo(JodaDateTimeSqlTimestampTest.ISO_FMT.parseDateTime("2015-04-01T06:00:00-05:00"));
        assertThat(arrivalTime).isEqualTo(JodaDateTimeSqlTimestampTest.ISO_FMT.parseDateTime("2015-04-01T21:00:00+02:00"));
    }

    public interface FlightDao {
        @SqlUpdate("INSERT INTO flights(flight_id, departure_airport, arrival_airport,departure_time, arrival_time) " + "VALUES (:flight_id, :departure_airport, :arrival_airport, :departure_time, :arrival_time)")
        int insert(@Bind("flight_id")
        String flightId, @Bind("departure_airport")
        String departureAirport, @Bind("arrival_airport")
        String arrivalAirport, @Bind("departure_time")
        DateTime departureTime, @Bind("arrival_time")
        DateTime arrivalTime);

        @SqlQuery("SELECT arrival_time FROM flights WHERE flight_id=:flight_id")
        DateTime getArrivalTime(@Bind("flight_id")
        String flightId);

        @SqlQuery("SELECT departure_time FROM flights WHERE flight_id=:flight_id")
        DateTime getDepartureTime(@Bind("flight_id")
        String flightId);
    }
}

