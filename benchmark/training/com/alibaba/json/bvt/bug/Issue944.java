package com.alibaba.json.bvt.bug;


import SerializerFeature.SkipTransientField;
import com.alibaba.fastjson.JSON;
import java.beans.Transient;
import junit.framework.TestCase;


/**
 * Created by wenshao on 19/12/2016.
 */
public class Issue944 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue944.Model model = new Issue944.Model();
        model.id = 1001;
        String text = JSON.toJSONString(model, SkipTransientField);
        TestCase.assertEquals("{}", text);
    }

    public static class Model {
        private int id;

        @Transient
        public int getId() {
            return id;
        }
    }
}

