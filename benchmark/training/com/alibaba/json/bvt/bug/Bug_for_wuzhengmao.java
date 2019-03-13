package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_wuzhengmao extends TestCase {
    public void test_0() throws Exception {
        Bug_for_wuzhengmao.Node node1 = new Bug_for_wuzhengmao.Node();
        node1.setId(1);
        Bug_for_wuzhengmao.Node node2 = new Bug_for_wuzhengmao.Node();
        node2.setId(2);
        node1.setParent(node2);
        List<Bug_for_wuzhengmao.Node> list = Arrays.asList(new Bug_for_wuzhengmao.Node[]{ node1, node2 });
        String json = JSON.toJSONString(list, true);
        System.out.println(json);
        List<Bug_for_wuzhengmao.Node> result = JSON.parseArray(json, Bug_for_wuzhengmao.Node.class);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(1, result.get(0).getId());
        Assert.assertEquals(2, result.get(1).getId());
        Assert.assertEquals(result.get(0).getParent(), result.get(1));
    }

    static class Node {
        int id;

        Bug_for_wuzhengmao.Node parent;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Bug_for_wuzhengmao.Node getParent() {
            return parent;
        }

        public void setParent(Bug_for_wuzhengmao.Node parent) {
            this.parent = parent;
        }
    }
}

