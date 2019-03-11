package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_448 extends TestCase {
    // skip
    public void test_for_issue() throws Exception {
        final int value_size = 1024 * 16;
        List<Bug_for_issue_448.Model> list = new ArrayList<Bug_for_issue_448.Model>();
        for (int i = 0; i < 10; ++i) {
            Bug_for_issue_448.Model model = new Bug_for_issue_448.Model();
            char[] buf = new char[value_size];
            for (int j = 0; j < (buf.length); ++j) {
                buf[j] = ((char) ('a' + j));
            }
            model.value = new String(buf);
            list.add(model);
        }
        String text = JSON.toJSONString(list);
        JSONReader reader = new JSONReader(new StringReader(text));
        reader.startArray();
        while (reader.hasNext()) {
            Bug_for_issue_448.Model model = reader.readObject(Bug_for_issue_448.Model.class);
            String value = model.value;
            Assert.assertEquals(value_size, value.length());
            for (int i = 0; i < (value.length()); ++i) {
                char ch = value.charAt(i);
                Assert.assertEquals(("error : index_" + i), ((char) ('a' + i)), ch);
            }
        } 
        reader.endArray();
        reader.close();
    }

    public static class Model {
        public String value;
    }
}

