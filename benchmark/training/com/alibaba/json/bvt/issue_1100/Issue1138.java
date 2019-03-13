package com.alibaba.json.bvt.issue_1100;


import Feature.SupportArrayToBean;
import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/04/2017.
 */
public class Issue1138 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1138.Model model = new Issue1138.Model();
        model.id = 1001;
        model.name = "gaotie";
        // {"id":1001,"name":"gaotie"}
        String text_normal = JSON.toJSONString(model);
        System.out.println(text_normal);
        // [1001,"gaotie"]
        String text_beanToArray = JSON.toJSONString(model, BeanToArray);
        System.out.println(text_beanToArray);
        // support beanToArray & normal mode
        System.out.println(JSON.parseObject(text_beanToArray, Issue1138.Model.class, SupportArrayToBean));
    }

    static class Model {
        public int id;

        public String name;
    }
}

