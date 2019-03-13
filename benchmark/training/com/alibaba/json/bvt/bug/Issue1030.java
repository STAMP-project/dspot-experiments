package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 13/03/2017.
 */
public class Issue1030 extends TestCase {
    public void test_for_issue() throws Exception {
        String DOC = "{\"books\":[{\"pageWords\":[{\"num\":10},{\"num\":15}]},{\"pageWords\":[{\"num\":20}]}]}";
        // fastjson?
        JSONObject result = JSONObject.parseObject(DOC);
        List array = ((List) (JSONPath.eval(result, "$.books[0:].pageWords[0:]")));
        TestCase.assertEquals(3, array.size());
    }
}

