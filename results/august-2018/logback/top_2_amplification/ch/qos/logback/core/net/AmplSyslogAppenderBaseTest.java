package ch.qos.logback.core.net;


import org.junit.Assert;
import org.junit.Test;


public class AmplSyslogAppenderBaseTest {
    @Test(timeout = 10000)
    public void testFacilityStringTointlitString13_failAssert58() throws Exception, InterruptedException {
        try {
            SyslogAppenderBase.facilityStringToint("KERN");
            SyslogAppenderBase.facilityStringToint("*zH_");
            SyslogAppenderBase.facilityStringToint("MAIL");
            SyslogAppenderBase.facilityStringToint("DAEMON");
            SyslogAppenderBase.facilityStringToint("AUTH");
            SyslogAppenderBase.facilityStringToint("SYSLOG");
            SyslogAppenderBase.facilityStringToint("LPR");
            SyslogAppenderBase.facilityStringToint("NEWS");
            SyslogAppenderBase.facilityStringToint("UUCP");
            SyslogAppenderBase.facilityStringToint("CRON");
            SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            SyslogAppenderBase.facilityStringToint("FTP");
            SyslogAppenderBase.facilityStringToint("NTP");
            SyslogAppenderBase.facilityStringToint("AUDIT");
            SyslogAppenderBase.facilityStringToint("ALERT");
            SyslogAppenderBase.facilityStringToint("CLOCK");
            SyslogAppenderBase.facilityStringToint("LOCAL0");
            SyslogAppenderBase.facilityStringToint("LOCAL1");
            SyslogAppenderBase.facilityStringToint("LOCAL2");
            SyslogAppenderBase.facilityStringToint("LOCAL3");
            SyslogAppenderBase.facilityStringToint("LOCAL4");
            SyslogAppenderBase.facilityStringToint("LOCAL5");
            SyslogAppenderBase.facilityStringToint("LOCAL6");
            SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringTointlitString13 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("*zH_ is not a valid syslog facility string", expected.getMessage());
        }
    }
}

