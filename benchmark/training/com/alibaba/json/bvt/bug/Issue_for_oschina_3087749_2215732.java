package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 29/12/2016.
 */
public class Issue_for_oschina_3087749_2215732 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"datas\":[\"a\",\"b\"]}";
        JSONObject o = JSON.parseObject(json);
        o.toJavaObject(Issue_for_oschina_3087749_2215732.JsonBean.class);
    }

    public static class JsonBean {
        private List<String> datas = new ArrayList<String>();

        public List<String> getDatas() {
            return datas;
        }
    }
}

