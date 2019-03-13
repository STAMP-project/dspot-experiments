package hudson.scheduler;


import antlr.ANTLRException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;


@RunWith(Parameterized.class)
@For({ CronTab.class, Hash.class })
public class CronTabEventualityTest {
    private String name;

    private Hash hash;

    public CronTabEventualityTest(String name, Hash hash) {
        this.name = name;
        this.hash = hash;
    }

    @Test
    @Issue("JENKINS-12388")
    public void testYearlyWillBeEventuallyTriggeredWithinOneYear() throws ANTLRException {
        Calendar start = new GregorianCalendar(2012, 0, 11, 22, 33);// Jan 11th 2012 22:33

        Calendar limit = createLimit(start, Calendar.YEAR, 1);
        checkEventuality(start, "@yearly", limit);
    }

    @Test
    @Issue("JENKINS-12388")
    public void testAnnuallyWillBeEventuallyTriggeredWithinOneYear() throws ANTLRException {
        Calendar start = new GregorianCalendar(2012, 0, 11, 22, 33);// Jan 11th 2012 22:33

        Calendar limit = createLimit(start, Calendar.YEAR, 1);
        checkEventuality(start, "@annually", limit);
    }

    @Test
    public void testMonthlyWillBeEventuallyTriggeredWithinOneMonth() throws ANTLRException {
        Calendar start = new GregorianCalendar(2012, 0, 11, 22, 33);// Jan 11th 2012 22:33

        Calendar limit = createLimit(start, Calendar.MONTH, 1);
        checkEventuality(start, "@monthly", limit);
    }

    @Test
    public void testWeeklyWillBeEventuallyTriggeredWithinOneWeek() throws ANTLRException {
        Calendar start = new GregorianCalendar(2012, 0, 11, 22, 33);// Jan 11th 2012 22:33

        Calendar limit = createLimit(start, Calendar.WEEK_OF_YEAR, 1);
        checkEventuality(start, "@weekly", limit);
    }

    @Test
    public void testDailyWillBeEventuallyTriggeredWithinOneDay() throws ANTLRException {
        Calendar start = new GregorianCalendar(2012, 0, 11, 22, 33);// Jan 11th 2012 22:33

        Calendar limit = createLimit(start, Calendar.DAY_OF_MONTH, 1);
        checkEventuality(start, "@daily", limit);
    }

    @Test
    public void testMidnightWillBeEventuallyTriggeredWithinOneDay() throws ANTLRException {
        Calendar start = new GregorianCalendar(2012, 0, 11, 22, 33);// Jan 11th 2012 22:33

        Calendar limit = createLimit(start, Calendar.DAY_OF_MONTH, 1);
        checkEventuality(start, "@midnight", limit);
    }

    @Test
    public void testHourlyWillBeEventuallyTriggeredWithinOneHour() throws ANTLRException {
        Calendar start = new GregorianCalendar(2012, 0, 11, 22, 33);// Jan 11th 2012 22:33

        Calendar limit = createLimit(start, Calendar.HOUR, 1);
        checkEventuality(start, "@hourly", limit);
    }

    @Test
    public void testFirstDayOfMonthWillBeEventuallyTriggeredWithinOneMonth() throws ANTLRException {
        Calendar start = new GregorianCalendar(2012, 0, 11, 22, 33);// Jan 11th 2012 22:33

        Calendar limit = createLimit(start, Calendar.MONTH, 1);
        checkEventuality(start, "H H 1 * *", limit);
    }

    @Test
    public void testFirstSundayOfMonthWillBeEventuallyTriggeredWithinOneMonthAndOneWeek() throws ANTLRException {
        Calendar start = new GregorianCalendar(2012, 0, 11, 22, 33);// Jan 11th 2012 22:33

        Calendar limit = createLimit(start, Calendar.DAY_OF_MONTH, (31 + 7));
        // If both day of month and day of week are specified:
        // UNIX: triggered when either matches
        // Jenkins: triggered when both match
        checkEventuality(start, "H H 1-7 * 0", limit);
    }
}

