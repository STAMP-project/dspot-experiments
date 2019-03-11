package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.time.LocalDate;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/03/2017.
 */
public class Issue1020 extends TestCase {
    public void test_null() throws Exception {
        Issue1020.Vo vo = JSON.parseObject("{\"ld\":null}", Issue1020.Vo.class);
        TestCase.assertNull(vo.ld);
    }

    public void test_empty() throws Exception {
        Issue1020.Vo vo = JSON.parseObject("{\"ld\":\"\"}", Issue1020.Vo.class);
        TestCase.assertNull(vo.ld);
    }

    public static class Vo {
        public LocalDate ld;
    }
}

