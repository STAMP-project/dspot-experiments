package com.alibaba.json.bvt.serializer.date;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;


public class DateTest5_iso8601 extends TestCase {
    public void test_date() throws Exception {
        Date date1 = JSON.parseObject("{\"gmtCreate\":\"2018-09-12\"}", DateTest5_iso8601.VO.class).getGmtCreate();
        TestCase.assertNotNull(date1);
        Date date2 = JSON.parseObject("{\"gmtCreate\":\"2018-09-12T15:10:19+00:00\"}", DateTest5_iso8601.VO.class).getGmtCreate();
        Date date3 = JSON.parseObject("{\"gmtCreate\":\"2018-09-12T15:10:19Z\"}", DateTest5_iso8601.VO.class).getGmtCreate();
        Date date4 = JSON.parseObject("{\"gmtCreate\":\"20180912T151019Z\"}", DateTest5_iso8601.VO.class).getGmtCreate();
        Date date5 = JSON.parseObject("{\"gmtCreate\":\"2018-09-12T15:10:19Z\"}", DateTest5_iso8601.VO.class).getGmtCreate();
        Date date6 = JSON.parseObject("{\"gmtCreate\":\"20180912\"}", DateTest5_iso8601.VO.class).getGmtCreate();
        long delta_2_1 = (date2.getTime()) - (date1.getTime());
        TestCase.assertEquals(83419000, delta_2_1);
        long delta_3_1 = (date3.getTime()) - (date1.getTime());
        TestCase.assertEquals(83419000, delta_3_1);
        long delta_4_3 = (date4.getTime()) - (date3.getTime());
        TestCase.assertEquals(0, delta_4_3);
        long delta_5_4 = (date5.getTime()) - (date4.getTime());
        TestCase.assertEquals(0, delta_5_4);
        long delta_6_1 = (date6.getTime()) - (date1.getTime());
        TestCase.assertEquals(0, delta_6_1);
    }

    public static class VO {
        private Date gmtCreate;

        public Date getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(Date gmtCreate) {
            this.gmtCreate = gmtCreate;
        }
    }
}

