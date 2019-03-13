package com.alibaba.json.bvt.jdk8;


import SerializerFeature.UseISO8601DateFormat;
import SerializerFeature.WriteDateUseDateFormat;
import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalDateTimeTest extends TestCase {
    public void test_for_issue() throws Exception {
        LocalDateTimeTest.VO vo = new LocalDateTimeTest.VO();
        vo.setDate(LocalDateTime.now().minusNanos(10L));
        String text = JSON.toJSONString(vo);
        LocalDateTimeTest.VO vo1 = JSON.parseObject(text, LocalDateTimeTest.VO.class);
        Assert.assertEquals(JSON.toJSONString(vo.getDate()), JSON.toJSONString(vo1.getDate()));
    }

    /**
     * ????: ??LocalDateTime ?????? ??
     *  ???1? LocalDateTime ???????????,??????????????
     *  ???2? ???? SerializerFeature.WriteDateUseDateFormat ??? "yyyy-MM-dd HH:mm:ss" ?????
     *  ???3?  ???? SerializerFeature.UseISO8601DateFormat ???ISO8601??? "yyyy-MM-dd'T'HH:mm:ss"?????
     *  ???4:
     *      1)???LocalDateTime?? ????? ??????
     *      2)???WriteDateUseDateFormat ? "yyyy-MM-dd HH:mm:ss" ?????
     *      3)???UseISO8601DateFormat ?ISO8601??? "yyyy-MM-dd'T'HH:mm:ss"?????
     *      4)???WriteDateUseDateFormat?UseISO8601DateFormat ????,??ISO8601??? "yyyy-MM-dd'T'HH:mm:ss"?????
     *
     * @author wuqiong  2017/11/22 15:08
     */
    public void test_toJsonString_ofLong() throws Exception {
        LocalDateTimeTest.VO vo = new LocalDateTimeTest.VO();
        vo.setDate(LocalDateTime.now());
        LocalDateTimeTest.VO vo1 = JSON.parseObject("{\"date\":1511334591189}", LocalDateTimeTest.VO.class);
        String text2 = JSON.toJSONString(vo, WriteDateUseDateFormat);
        System.out.println(text2);// {"date":"2017-11-22 15:09:51"}

        LocalDateTimeTest.VO vo2 = JSON.parseObject(text2, LocalDateTimeTest.VO.class);
        String text3 = JSON.toJSONString(vo, UseISO8601DateFormat);
        System.out.println(text3);// {"date":"2017-11-22T15:09:51"}

        LocalDateTimeTest.VO vo3 = JSON.parseObject(text3, LocalDateTimeTest.VO.class);
        String text4 = JSON.toJSONString(vo, UseISO8601DateFormat, WriteDateUseDateFormat);
        System.out.println(text4);// {"date":"2017-11-22T15:09:51"}

        LocalDateTimeTest.VO vo4 = JSON.parseObject(text4, LocalDateTimeTest.VO.class);
    }

    public void test_for_issue_1() throws Exception {
        String text = "{\"date\":\"2018-08-03 22:38:33.145\"}";
        LocalDateTimeTest.VO vo1 = JSON.parseObject(text, LocalDateTimeTest.VO.class);
        TestCase.assertNotNull(vo1.date);
    }

    public static class VO {
        private LocalDateTime date;

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }
    }
}

