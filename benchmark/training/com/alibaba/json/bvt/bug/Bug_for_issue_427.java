package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_issue_427 extends TestCase {
    public void test_for_issue() throws Exception {
        String value = "================================================" + (("\n\u670d\u52a1\u5668\u540d\u79f0\uff1a[FFFF00]N23-\u7269\u534e\u5929\u5b9d\u00a0[-]" + "\n\u5f00\u670d\u65f6\u95f4\uff1a[FFFF00]2015\u5e7410\u670816\u65e511\uff1a00\uff08\u5468\u4e94\uff09[-]") + "\n================================================");
        Bug_for_issue_427.Model model = new Bug_for_issue_427.Model();
        model.value = value;
        JSON.toJSONString(model);
    }

    public static class Model {
        public String value;
    }
}

