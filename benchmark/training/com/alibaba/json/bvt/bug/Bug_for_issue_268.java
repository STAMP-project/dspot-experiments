package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_268 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_268.V1 vo = new Bug_for_issue_268.V1();
        vo.units = EnumSet.of(TimeUnit.DAYS, TimeUnit.HOURS);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"units\":[\"HOURS\",\"DAYS\"]}", text);
        Bug_for_issue_268.V1 vo1 = JSON.parseObject(text, Bug_for_issue_268.V1.class);
        Assert.assertNotNull(vo1);
        Assert.assertEquals(vo.units, vo1.units);
    }

    public void test_for_issue_private() throws Exception {
        Bug_for_issue_268.VO vo = new Bug_for_issue_268.VO();
        vo.units = EnumSet.of(TimeUnit.DAYS, TimeUnit.HOURS);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"units\":[\"HOURS\",\"DAYS\"]}", text);
        Bug_for_issue_268.VO vo1 = JSON.parseObject(text, Bug_for_issue_268.VO.class);
        Assert.assertNotNull(vo1);
        Assert.assertEquals(vo.units, vo1.units);
    }

    private static class VO {
        private EnumSet<TimeUnit> units;

        public EnumSet<TimeUnit> getUnits() {
            return units;
        }

        public void setUnits(EnumSet<TimeUnit> units) {
            this.units = units;
        }
    }

    public static class V1 {
        private EnumSet<TimeUnit> units;

        public EnumSet<TimeUnit> getUnits() {
            return units;
        }

        public void setUnits(EnumSet<TimeUnit> units) {
            this.units = units;
        }
    }
}

