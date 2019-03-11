package org.jivesoftware.openfire.handler;


import IQ.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.bind.DatatypeConverter;
import org.dom4j.Element;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jivesoftware.util.XMPPDateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.xmpp.packet.IQ;


/**
 *
 *
 * @author csh
 */
public class IQEntityTimeHandlerTest {
    @Test
    public void testIQInfo() {
        IQEntityTimeHandler iqEntityTimeHandler = new IQEntityTimeHandler();
        Assert.assertTrue(iqEntityTimeHandler.getFeatures().hasNext());
        Assert.assertEquals(iqEntityTimeHandler.getFeatures().next(), "urn:xmpp:time");
        Assert.assertEquals(iqEntityTimeHandler.getInfo().getNamespace(), "urn:xmpp:time");
        Assert.assertEquals(getName(), "time");
    }

    @Test
    public void testTimeZone() {
        IQEntityTimeHandler iqEntityTimeHandler = new IQEntityTimeHandler();
        Assert.assertEquals(iqEntityTimeHandler.formatsTimeZone(TimeZone.getTimeZone("GMT-8:00")), "-08:00");
    }

    @Test
    public void testUtcDate() {
        IQEntityTimeHandler iqEntityTimeHandler = new IQEntityTimeHandler();
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Assert.assertEquals(iqEntityTimeHandler.getUtcDate(date), DatatypeConverter.printDateTime(calendar));
    }

    @Test
    public void testPerformanceDatatypeConvertVsXMPPDateFormat() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            DatatypeConverter.printDateTime(calendar);
        }
        MatcherAssert.assertThat(((System.currentTimeMillis()) - start), Matchers.is(Matchers.lessThan(2000L)));
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            XMPPDateTimeFormat.format(date);
        }
        MatcherAssert.assertThat(((System.currentTimeMillis()) - start), Matchers.is(Matchers.lessThan(4000L)));
    }

    @Test
    public void testIQ() {
        IQEntityTimeHandler iqEntityTimeHandler = new IQEntityTimeHandler();
        IQ input = new IQ(Type.get, "1");
        IQ result = iqEntityTimeHandler.handleIQ(input);
        Assert.assertEquals(getName(), "time");
        Assert.assertEquals(result.getChildElement().getNamespace().getText(), "urn:xmpp:time");
        Assert.assertEquals(result.getChildElement().content().size(), 2);
        Assert.assertTrue(((result.getChildElement().content().get(0)) instanceof Element));
        Assert.assertTrue(((result.getChildElement().content().get(1)) instanceof Element));
        Assert.assertEquals(getName(), "tzo");
        Assert.assertEquals(getName(), "utc");
    }
}

