package com.alibaba.json.bvt.bug;


import SerializerFeature.BrowserCompatible;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_bbl extends TestCase {
    public void test_bug() throws Exception {
        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("msg", "<img class=\"em\" src=\"http://ab.com/12/33.jpg\" />");
        params.put("uid", "22034343");
        String s001 = JSON.toJSONString(params, BrowserCompatible);
        System.out.println(s001);
        Map<Object, Object> params2 = ((Map<Object, Object>) (JSON.parse(s001)));
        Assert.assertEquals(params.size(), params2.size());
        Assert.assertEquals(params.get("uid"), params2.get("uid"));
        Assert.assertEquals(params.get("msg"), params2.get("msg"));
        Assert.assertEquals(params, params2);
    }
}

