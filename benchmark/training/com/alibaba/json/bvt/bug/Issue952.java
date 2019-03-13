package com.alibaba.json.bvt.bug;


import SerializerFeature.UseISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import junit.framework.TestCase;


/**
 * Created by wenshao on 19/12/2016.
 */
public class Issue952 extends TestCase {
    public void test_for_issue() throws Exception {
        final String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String text = JSON.toJSONString(dateTime, UseISO8601DateFormat);
        TestCase.assertEquals(JSON.toJSONString(formatter.format(dateTime)), text);
    }
}

