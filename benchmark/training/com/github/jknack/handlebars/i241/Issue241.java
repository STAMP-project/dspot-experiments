package com.github.jknack.handlebars.i241;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import java.util.Calendar;
import org.junit.Test;


public class Issue241 extends AbstractTest {
    @Test
    public void formatAsHashInDateFormat() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1999);
        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.DATE, 16);
        shouldCompileTo("{{dateFormat date format=\"dd-MM-yyyy\"}}", AbstractTest.$("date", calendar.getTime()), "16-07-1999");
    }
}

