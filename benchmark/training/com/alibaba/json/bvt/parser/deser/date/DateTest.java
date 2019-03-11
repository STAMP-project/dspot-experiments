package com.alibaba.json.bvt.parser.deser.date;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import com.alibaba.fastjson.parser.ParserConfig;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateTest extends TestCase {
    public void test() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{\"date\":\"2012/04-01\"}", ParserConfig.getGlobalInstance(), 0);
        parser.setDateFormat("yyyy/MM-dd");
        DateTest.VO vo = parser.parseObject(DateTest.VO.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM-dd", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        Assert.assertEquals(dateFormat.parse("2012/04-01"), vo.getDate());
        parser.close();
    }

    public void test_reader() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser(new JSONReaderScanner("{\"date\":\"2012/04-01\"}", 0));
        parser.setDateFormat("yyyy/MM-dd");
        DateTest.VO vo = parser.parseObject(DateTest.VO.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM-dd", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        Assert.assertEquals(dateFormat.parse("2012/04-01"), vo.getDate());
        parser.close();
    }

    public static class VO {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}

