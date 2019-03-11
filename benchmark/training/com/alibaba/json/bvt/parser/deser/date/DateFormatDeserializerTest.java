package com.alibaba.json.bvt.parser.deser.date;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateFormatDeserializerTest extends TestCase {
    public void test_dateFormat_empty() throws Exception {
        DateFormatDeserializerTest.VO vo = JSON.parseObject("{\"format\":\"\"}", DateFormatDeserializerTest.VO.class);
        Assert.assertEquals(null, vo.getFormat());
    }

    public void test_dateFormat_array() throws Exception {
        List<SimpleDateFormat> list = JSON.parseArray("[\"\",null,\"yyyy\"]", SimpleDateFormat.class);
        Assert.assertEquals(null, list.get(0));
        Assert.assertEquals(null, list.get(1));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        Assert.assertEquals(dateFormat, list.get(2));
    }

    public void test_dateFormat_null() throws Exception {
        DateFormatDeserializerTest.VO vo = JSON.parseObject("{\"format\":null}", DateFormatDeserializerTest.VO.class);
        Assert.assertEquals(null, vo.getFormat());
    }

    public void test_dateFormat_yyyy() throws Exception {
        DateFormatDeserializerTest.VO vo = JSON.parseObject("{\"format\":\"yyyy\"}", DateFormatDeserializerTest.VO.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        Assert.assertEquals(dateFormat, vo.getFormat());
    }

    public void test_dateFormat_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"format\":123}", DateFormatDeserializerTest.VO.class);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        private SimpleDateFormat format;

        public VO() {
        }

        public VO(SimpleDateFormat format) {
            this.format = format;
        }

        public SimpleDateFormat getFormat() {
            return format;
        }

        public void setFormat(SimpleDateFormat format) {
            this.format = format;
        }
    }
}

