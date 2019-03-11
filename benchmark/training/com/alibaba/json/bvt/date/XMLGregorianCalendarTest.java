package com.alibaba.json.bvt.date;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import junit.framework.TestCase;
import org.junit.Assert;


public class XMLGregorianCalendarTest extends TestCase {
    public void test_for_issue() throws Exception {
        GregorianCalendar gregorianCalendar = ((GregorianCalendar) (GregorianCalendar.getInstance()));
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        String text = JSON.toJSONString(calendar);
        Assert.assertEquals(Long.toString(gregorianCalendar.getTimeInMillis()), text);
        XMLGregorianCalendar calendar1 = JSON.parseObject(text, XMLGregorianCalendar.class);
        TestCase.assertEquals(calendar.toGregorianCalendar().getTimeInMillis(), calendar1.toGregorianCalendar().getTimeInMillis());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("calendar", calendar);
        String json = JSON.toJSONString(jsonObject);
        XMLGregorianCalendarTest.Model model = JSON.parseObject(json).toJavaObject(XMLGregorianCalendarTest.Model.class);
        TestCase.assertEquals(calendar.toGregorianCalendar().getTimeInMillis(), model.calendar.toGregorianCalendar().getTimeInMillis());
    }

    public static class Model {
        public XMLGregorianCalendar calendar;
    }
}

