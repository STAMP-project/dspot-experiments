package dev.morphia.issue173;


import WriteConcern.ACKNOWLEDGED;
import dev.morphia.InsertOptions;
import dev.morphia.TestBase;
import dev.morphia.annotations.Converters;
import dev.morphia.query.FindOptions;
import dev.morphia.testutil.TestEntity;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;


public class TestCalendar extends TestBase {
    @Test
    public final void testCalendar() {
        getMorphia().map(TestCalendar.A.class);
        final TestCalendar.A a = new TestCalendar.A();
        a.c = Calendar.getInstance();
        getDs().save(a, new InsertOptions().writeConcern(ACKNOWLEDGED));
        // occasionally failed, so i suspected a race cond.
        final TestCalendar.A loaded = getDs().find(TestCalendar.A.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(loaded.c);
        Assert.assertEquals(a.c, loaded.c);
    }

    @Converters(CalendarConverter.class)
    private static class A extends TestEntity {
        private Calendar c;
    }
}

