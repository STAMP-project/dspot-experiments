package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import junit.framework.TestCase;
import org.junit.Assert;


public class PascalNameFilterTest extends TestCase {
    public void test_0() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        serializer.getNameFilters().add(new PascalNameFilter());
        PascalNameFilterTest.VO vo = new PascalNameFilterTest.VO();
        vo.setId(123);
        vo.setName("wenshao");
        serializer.write(vo);
        Assert.assertEquals("{\"Id\":123,\"Name\":\"wenshao\"}", serializer.toString());
        serializer.close();
    }

    public static class VO {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

