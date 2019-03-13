package com.vaadin.tests.serialization;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class SerializerTestTest extends MultiBrowserTest {
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", new Locale("en", "fi"));

    @Test
    public void testSerialization() {
        // Set up formatting with browsers timezone
        FORMAT.setTimeZone(getBrowserTimeZone());
        openTestURL();
        int logRow = 0;
        Assert.assertEquals("sendJson: {\"b\":false,\"s\":\"JSON\"}, null, \"value\"", getLogRow((logRow++)));
        Assert.assertEquals("sendDateArray: January 31, 2013 12:00:00 AM UTC January 31, 2012 12:00:00 AM UTC", getLogRow((logRow++)));
        Assert.assertEquals("sendDate: May 1, 2013 11:12:13 AM UTC", getLogRow((logRow++)));
        Assert.assertEquals("sendDate: January 1, 1970 12:00:00 AM UTC", getLogRow((logRow++)));
        Assert.assertEquals("sendEnum: PREFORMATTED, [HTML, TEXT], [PREFORMATTED, TEXT]", getLogRow((logRow++)));
        Assert.assertEquals("sendWrappedGenerics: {[SimpleTestBean(1)]={1=[SimpleTestBean(42)]}}", getLogRow((logRow++)));
        Assert.assertEquals("sendMap: {a=SimpleTestBean(1)}, [com.vaadin.tests.widgetset.server.SerializerTestExtension=SimpleTestBean(4)], [2=com.vaadin.tests.widgetset.server.SerializerTestExtension], {SimpleTestBean(4)=SimpleTestBean(-4), SimpleTestBean(-5)=SimpleTestBean(5)}", getLogRow((logRow++)));
        Assert.assertEquals("sendSet: [-12, -7, -4], class com.vaadin.tests.serialization.SerializerTest, [SimpleTestBean(2), SimpleTestBean(3)]", getLogRow((logRow++)));
        Assert.assertEquals("sendArrayList: [[2], [2]], [[2, 1], [2, 3]], [[SimpleTestBean(7)]]", getLogRow((logRow++)));
        Assert.assertEquals("sendList: [-234, 5, 8], class com.vaadin.tests.widgetset.server.SerializerTestExtension, class com.vaadin.tests.serialization.SerializerTest, [SimpleTestBean(-568), SimpleTestBean(234)]", getLogRow((logRow++)));
        Assert.assertEquals("sendNestedArray: [[7, 5]], [[SimpleTestBean(2)], [SimpleTestBean(4)]]", getLogRow((logRow++)));
        Assert.assertEquals("sendNull: null, Not null", getLogRow((logRow++)));
        Assert.assertEquals("sendBean: ComplexTestBean [innerBean1=SimpleTestBean(1), innerBean2=SimpleTestBean(3), innerBeanCollection=[SimpleTestBean(6), SimpleTestBean(0)], privimite=6], SimpleTestBean(0), [SimpleTestBean(7)]", getLogRow((logRow++)));
        Assert.assertEquals("sendConnector: com.vaadin.tests.widgetset.server.SerializerTestExtension", getLogRow((logRow++)));
        Assert.assertEquals("sendString: Taegghiiiinnrsssstt?, [null, ?]", getLogRow((logRow++)));
        Assert.assertEquals("sendDouble: 0.423310825130748, 5.859874482048838, [2.0, 1.7976931348623157E308, 4.9E-324]", getLogRow((logRow++)));
        Assert.assertEquals("sendFloat: 1.0000001, 3.14159, [-12.0, 0.0, 57.0]", getLogRow((logRow++)));
        Assert.assertEquals("sendLong: -57841235865, 577431841358, [57, 0]", getLogRow((logRow++)));
        Assert.assertEquals("sendInt: 2, 5, [2147483647, 0]", getLogRow((logRow++)));
        Assert.assertEquals("sendChar: ?, ?, [a, b, c, d]", getLogRow((logRow++)));
        Assert.assertEquals("sendByte: 5, -12, [3, 1, 2]", getLogRow((logRow++)));
        Assert.assertEquals("sendBoolean: false, false, [false, false, true, false, true, true]", getLogRow((logRow++)));
        Assert.assertEquals("sendBeanSubclass: 43", getLogRow((logRow++)));
        // Dates from state
        Date date1 = new Date(1);
        Date date2 = new Date(Date.UTC((2013 - 1900), 4, 1, 11, 12, 13));
        Date[] dateArray = new Date[]{ new Date(1), new Date(2) };
        Assert.assertEquals(("state.dateArray: " + (Arrays.stream(dateArray).map(this::formatDate).collect(Collectors.joining(" ")))), getLogRow((logRow++)));
        Assert.assertEquals(("state.date2: " + (formatDate(date2))), getLogRow((logRow++)));
        Assert.assertEquals(("state.date1: " + (formatDate(date1))), getLogRow((logRow++)));
        Assert.assertEquals("state.jsonBoolean: false", getLogRow((logRow++)));
        Assert.assertEquals("state.jsonString: a string", getLogRow((logRow++)));
        Assert.assertEquals("state.jsonNull: NULL", getLogRow((logRow++)));
        Assert.assertEquals("state.stringArray: [null, ?]", getLogRow((logRow++)));
        Assert.assertEquals("state.string: This is a tesing string ?", getLogRow((logRow++)));
        Assert.assertEquals("state.doubleArray: [1.7976931348623157e+308, 5e-324]", getLogRow((logRow++)));
        Assert.assertEquals("state.doubleObjectValue: -2.718281828459045", getLogRow((logRow++)));
        Assert.assertEquals("state.doubleValue: 3.141592653589793", getLogRow((logRow++)));
        Assert.assertEquals("state.floatArray: [57, 0, -12]", getLogRow((logRow++)));
        Assert.assertTrue(getLogRow((logRow++)).startsWith("state.floatObjectValue: 1.0000001"));
        Assert.assertTrue(getLogRow((logRow++)).startsWith("state.floatValue: 3.14159"));
        Assert.assertEquals("state.longArray: [-57841235865, 57]", getLogRow((logRow++)));
        Assert.assertEquals("state.longObjectValue: 577431841360", getLogRow((logRow++)));
        Assert.assertEquals("state.longValue: 577431841359", getLogRow((logRow++)));
        Assert.assertEquals("state.intArray: [5, 7]", getLogRow((logRow++)));
        Assert.assertEquals("state.intObjectValue: 42", getLogRow((logRow++)));
        Assert.assertEquals("state.intValue: 2147483647", getLogRow((logRow++)));
        Assert.assertEquals("state.charArray: aBcD", getLogRow((logRow++)));
        Assert.assertEquals("state.charObjectValue: ?", getLogRow((logRow++)));
        Assert.assertEquals("state.charValue: ?", getLogRow((logRow++)));
        Assert.assertEquals("state.byteArray: [3, 1, 2]", getLogRow((logRow++)));
        Assert.assertEquals("state.byteObjectValue: -12", getLogRow((logRow++)));
        Assert.assertEquals("state.byteValue: 5", getLogRow((logRow++)));
        Assert.assertEquals("state.booleanArray: [true, true, false, true, false, false]", getLogRow((logRow++)));
    }
}

