package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_BlankRain_Issue_502 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_BlankRain_Issue_502.People a1 = new Bug_for_BlankRain_Issue_502.People();
        a1.set??("A");
        a1.set??("B");
        a1.set??("C");
        a1.set???("D");
        a1.set??("E");
        a1.set??("F");
        String text = JSON.toJSONString(a1);
        Assert.assertEquals("{\"\u59d3\u540d\":\"A\",\"\u65f6\u95f4\":\"F\",\"\u6ee1\u610f\u5ea6\":\"D\",\"\u72b6\u6001\":\"C\",\"\u7c7b\u578b\":\"B\",\"\u7edf\u8ba1\":\"E\"}", text);
        System.out.println(text);
        Bug_for_BlankRain_Issue_502.People a2 = JSON.parseObject(text, Bug_for_BlankRain_Issue_502.People.class);
        Assert.assertEquals(a1.get??(), a2.get??());
        Assert.assertEquals(a1.get??(), a2.get??());
        Assert.assertEquals(a1.get??(), a2.get??());
        Assert.assertEquals(a1.get???(), a2.get???());
        Assert.assertEquals(a1.get??(), a2.get??());
        Assert.assertEquals(a1.get??(), a2.get??());
    }

    public static class People {
        private String ??;

        private String ??;

        private String ??;

        private String ???;

        private String ??;

        private String ??;

        static List<String> head() {
            List<String> h = new ArrayList<String>();
            h.add("??");
            h.add("??");
            h.add("??");
            h.add("???");
            h.add("??");
            h.add("??");
            return h;
        }

        public String get??() {
            return ??;
        }

        public void set??(String ??) {
            this.?? = ??;
        }

        public String get??() {
            return ??;
        }

        public void set??(String ??) {
            this.?? = ??;
        }

        public String get??() {
            return ??;
        }

        public void set??(String ??) {
            this.?? = ??;
        }

        public String get???() {
            return ???;
        }

        public void set???(String ???) {
            this.??? = ???;
        }

        public String get??() {
            return ??;
        }

        public void set??(String ??) {
            this.?? = ??;
        }

        public String get??() {
            return ??;
        }

        public void set??(String ??) {
            this.?? = ??;
        }
    }
}

