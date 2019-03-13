package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/03/2017.
 */
public class Issue1023 extends TestCase {
    public void test_for_issue() throws Exception {
        Date now = new Date();
        GregorianCalendar gregorianCalendar = ((GregorianCalendar) (GregorianCalendar.getInstance()));
        gregorianCalendar.setTime(now);
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        String jsonString = JSON.toJSONString(calendar);
        // success
        calendar = JSON.parseObject(jsonString, XMLGregorianCalendar.class);
        Object toJSON1 = JSON.toJSON(calendar);// debug??? Long ??

        // ????????
        // error: java.lang.ClassCastException: java.lang.Long cannot be cast to com.alibaba.fastjson.JSONObject
        // JSONObject jsonObject = (JSONObject) JSON.toJSON(calendar);
        // ?? ???????? ?? jsonObject ??JSONObject??
        // calendar = jsonObject.toJavaObject(XMLGregorianCalendar.class);
        List<XMLGregorianCalendar> calendarList = new ArrayList<XMLGregorianCalendar>();
        calendarList.add(calendar);
        calendarList.add(calendar);
        calendarList.add(calendar);
        Object toJSON2 = JSON.toJSON(calendarList);// debug ??? JSONArray ??

        // success? ?? JSONArray.parseArray ????????
        JSONArray jsonArray = ((JSONArray) (JSON.toJSON(calendarList)));
        jsonString = jsonArray.toJSONString();
        List<XMLGregorianCalendar> calendarList1 = JSONArray.parseArray(jsonString, XMLGregorianCalendar.class);
        // ?? jsonArray.toJavaList ????
        // error: com.alibaba.fastjson.JSONException: can not cast to : javax.xml.datatype.XMLGregorianCalendar
        List<XMLGregorianCalendar> calendarList2 = jsonArray.toJavaList(XMLGregorianCalendar.class);
        TestCase.assertNotNull(calendarList2);
        TestCase.assertEquals(3, calendarList2.size());
    }
}

