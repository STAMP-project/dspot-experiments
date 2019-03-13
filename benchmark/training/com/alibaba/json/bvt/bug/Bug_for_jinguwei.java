package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_jinguwei extends TestCase {
    public void test_null() throws Exception {
        Bug_for_jinguwei.VO vo = new Bug_for_jinguwei.VO();
        vo.setList(new ArrayList<String>());
        vo.getList().add(null);
        vo.getList().add(null);
        Assert.assertEquals("{\"list\":[null,null]}", JSON.toJSONString(vo));
    }

    public static class VO {
        private List<String> list;

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }
}

