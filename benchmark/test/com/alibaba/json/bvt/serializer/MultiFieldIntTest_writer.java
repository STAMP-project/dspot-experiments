package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class MultiFieldIntTest_writer extends TestCase {
    public void test_for_big_writer() throws Exception {
        List<MultiFieldIntTest_writer.Model> list = new ArrayList<MultiFieldIntTest_writer.Model>();
        for (int i = 0; i < (1024 * 10); ++i) {
            MultiFieldIntTest_writer.Model model = new MultiFieldIntTest_writer.Model();
            model.id = 10000000 + i;
            list.add(model);
        }
        StringWriter out = new StringWriter();
        JSONWriter writer = new JSONWriter(out);
        writer.writeObject(list);
        writer.close();
        String text = out.toString();
        System.out.println(text);
        List<MultiFieldIntTest_writer.Model> results = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<List<MultiFieldIntTest_writer.Model>>() {});
        Assert.assertEquals(list.size(), results.size());
        for (int i = 0; i < (results.size()); ++i) {
            Assert.assertEquals(list.get(i).id, results.get(i).id);
        }
    }

    public static class Model {
        public int id;
    }
}

