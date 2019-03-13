package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.time.LocalDate;
import java.util.Locale;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocalDateTest5 extends TestCase {
    private Locale origin;

    public void test_for_tw() throws Exception {
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"2016/05/06\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_jp() throws Exception {
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"2016\u5e745\u67086\u65e5\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_kr() throws Exception {
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"2016\ub1445\uc6d46\uc77c\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_us() throws Exception {
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"05/26/2016\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(26, vo.date.getDayOfMonth());
    }

    public void test_for_eur() throws Exception {
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"26/05/2016\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(26, vo.date.getDayOfMonth());
    }

    public void test_for_us_1() throws Exception {
        Locale.setDefault(Locale.US);
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"05/06/2016\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_br() throws Exception {
        Locale.setDefault(new Locale("pt", "BR"));
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"06/05/2016\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_au() throws Exception {
        Locale.setDefault(new Locale("en", "AU"));
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"06/05/2016\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_de() throws Exception {
        Locale.setDefault(new Locale("pt", "BR"));
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"06.05.2016\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public void test_for_in() throws Exception {
        LocalDateTest5.VO vo = JSON.parseObject("{\"date\":\"06-05-2016\"}", LocalDateTest5.VO.class);
        Assert.assertEquals(2016, vo.date.getYear());
        Assert.assertEquals(5, vo.date.getMonthValue());
        Assert.assertEquals(6, vo.date.getDayOfMonth());
    }

    public static class VO {
        public LocalDate date;
    }
}

