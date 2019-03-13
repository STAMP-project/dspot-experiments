package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.AfterFilter;
import junit.framework.TestCase;
import org.junit.Assert;


public class AfterFilterTest2 extends TestCase {
    public void test_afterFilter() throws Exception {
        AfterFilter filter = new AfterFilter() {
            @Override
            public void writeAfter(Object object) {
                this.writeKeyValue("id", 123);
            }
        };
        Assert.assertEquals("{\"id\":123}", JSON.toJSONString(new AfterFilterTest2.VO(), filter));
    }

    public void test_afterFilter2() throws Exception {
        AfterFilter filter = new AfterFilter() {
            @Override
            public void writeAfter(Object object) {
                this.writeKeyValue("id", 123);
                writeKeyValue("name", "wenshao");
            }
        };
        Assert.assertEquals("{\"id\":123,\"name\":\"wenshao\"}", JSON.toJSONString(new AfterFilterTest2.VO(), filter));
    }

    public static class VO {}
}

