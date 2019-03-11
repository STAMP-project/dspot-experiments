package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;


public class Bug_for_issue_184 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_184.TUser user = new Bug_for_issue_184.TUser();
        user.id = 1001;
        // ??asm(?android???)???asm?????
        SerializeConfig.getGlobalInstance().setAsmEnable(false);
        String json = JSON.toJSONString(user, WriteClassName);
        // ??{"@type":"xx.TUser","id":0L}
        System.out.println(json);
        // ?????????com.alibaba.fastjson.JSONException: unclosed.str
        // ???id?L??
        user = ((Bug_for_issue_184.TUser) (JSON.parse(json)));
    }

    public static class TUser {
        public long id;
    }
}

