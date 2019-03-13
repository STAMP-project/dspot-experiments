package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.serializer.JSONSerializer;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_Date extends TestCase {
    public void test_date() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        serializer.write(new TestASM_Date.V0());
        Assert.assertEquals("{}", serializer.getWriter().toString());
    }

    public static class V0 {
        private Date d;

        public V0() {
        }

        public V0(long value) {
            super();
            this.d = new Date(value);
        }

        public Date getD() {
            return d;
        }

        public void setD(Date d) {
            this.d = d;
        }
    }
}

