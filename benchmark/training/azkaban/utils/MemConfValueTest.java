package azkaban.utils;


import Constants.JobProperties.JOB_MAX_XMS;
import Constants.JobProperties.JOB_MAX_XMX;
import Constants.JobProperties.MAX_XMS_DEFAULT;
import Constants.JobProperties.MAX_XMX_DEFAULT;
import org.junit.Test;


public class MemConfValueTest {
    @Test
    public void parseMaxXmX() {
        MemConfValueTest.assertXmx(Props.of(JOB_MAX_XMX, "1K"), "1K", 1L);
    }

    @Test
    public void parseMaxXmXDefault() {
        MemConfValueTest.assertXmx(new Props(), MAX_XMX_DEFAULT, 2097152L);
    }

    @Test
    public void parseMaxXms() {
        MemConfValueTest.assertXms(Props.of(JOB_MAX_XMS, "1K"), "1K", 1L);
    }

    @Test
    public void parseMaxXmsDefault() {
        MemConfValueTest.assertXms(new Props(), MAX_XMS_DEFAULT, 1048576L);
    }

    @Test
    public void parseEmptyThrows() {
        final Throwable thrown = catchThrowable(() -> MemConfValue.parseMaxXmx(Props.of(Constants.JobProperties.JOB_MAX_XMX, "")));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertThat(thrown).hasMessage(("job.max.Xmx must not have an empty value. " + "Remove the property to use default or specify a valid value."));
    }
}

