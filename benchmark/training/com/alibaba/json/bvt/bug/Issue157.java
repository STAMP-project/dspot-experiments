package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class Issue157 extends TestCase {
    public void test_for_issue() throws Exception {
        String m = "2\u300195\u5f00\u5934\u9753\u53f7\uff0c\u547c\u51fa\u663e\u53f7\uff0c\u5bf9\u65b9\u53ef\u4ee5\u6253\u56de\uff0c\u5373\u4f7f\u4e0d\u5728\u7ebf\u4ea6\u53ef\u8bbe\u7f6e\u547c\u8f6c\u624b\u673a\uff0c\u4e0d\u9519\u8fc7\u4efb\u4f55\u91cd\u8981\u7535\u8bdd\uff0c\u4e0d\u66b4\u9732\u771f\u5b9e\u8eab\u4efd\u3002\r\n3\u3001\u5e94\u7528\u5185\u5b8c\u5168\u514d\u8d39\u53d1\u9001\u6587\u5b57\u6d88\u606f\u3001\u8bed\u97f3\u5bf9\u8bb2\u3002\r\n4\u3001\u5efa\u8baeWIFI \u6216 3G \u73af\u5883\u4e0b\u4f7f\u7528\u4ee5\u83b7\u5f97\u6700\u4f73\u901a\u8bdd\u4f53\u9a8c";
        JSONObject json = new JSONObject();
        json.put("??", m);
        String content = json.toJSONString();
        System.out.println(content);
    }
}

