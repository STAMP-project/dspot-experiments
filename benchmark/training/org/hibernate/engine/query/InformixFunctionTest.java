package org.hibernate.engine.query;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.dialect.InformixDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(InformixDialect.class)
public class InformixFunctionTest extends BaseCoreFunctionalTestCase {
    private InformixFunctionTest.Event event;

    @Test
    @TestForIssue(jiraKey = "HHH-10846")
    public void testConcat() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String location = ((String) (session.createQuery(("select concat(e.country, ' - ',  e.city) " + ("from Event e " + "where e.id = :id"))).setParameter("id", event.id).getSingleResult()));
            assertEquals("Romania - Cluj-Napoca", location);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10846")
    public void testSubstring() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String location = ((String) (session.createQuery(("select substring(e.city, 0, 5) " + ("from Event e " + "where e.id = :id"))).setParameter("id", event.id).getSingleResult()));
            assertEquals("Cluj", location);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10846")
    public void testSubstr() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String location = ((String) (session.createQuery(("select substr(e.city, 0, 4) " + ("from Event e " + "where e.id = :id"))).setParameter("id", event.id).getSingleResult()));
            assertEquals("Cluj", location);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10846")
    public void testCoalesceAndNvl() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String location = ((String) (session.createQuery(("select coalesce(e.district, 'N/A') " + ("from Event e " + "where e.id = :id"))).setParameter("id", event.id).getSingleResult()));
            assertEquals("N/A", location);
            location = ((String) (session.createQuery(("select nvl(e.district, 'N/A') " + ("from Event e " + "where e.id = :id"))).setParameter("id", event.id).getSingleResult()));
            assertEquals("N/A", location);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10800")
    public void testCurrentDate() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Date date = ((Date) (session.createQuery(("select current_date() " + ("from Event e " + "where e.id = :id"))).setParameter("id", event.id).getSingleResult()));
            assertNotNull(date);
            assertTrue(((date.getTime()) > 0));
            Calendar resultCalendar = Calendar.getInstance();
            resultCalendar.setTime(date);
            assertEquals(0, todayCalendar().compareTo(resultCalendar));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10800")
    public void testCurrentTimestamp() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            int tries = 2;
            while ((tries--) > 0) {
                Timestamp timestamp = ((Timestamp) (session.createQuery(("select current_timestamp() " + ("from Event e " + "where e.id = :id"))).setParameter("id", event.id).getSingleResult()));
                assertNotNull(timestamp);
                assertTrue(((timestamp != null) && ((timestamp.getTime()) > 0)));
                Calendar resultCalendar = Calendar.getInstance();
                resultCalendar.setTime(timestamp);
                long millis = (resultCalendar.getTime().getTime()) - (todayCalendar().getTime().getTime());
                if (millis == 0) {
                    // What are the odds that ou've run this test exactly at midnight?
                    try {
                        Thread.sleep(1000);
                    } catch ( ignore) {
                    }
                    continue;
                }
                assertTrue((millis > 0));
            } 
        });
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        @GeneratedValue
        private Long id;

        @Column
        private String country;

        private String city;

        private String district;
    }
}

