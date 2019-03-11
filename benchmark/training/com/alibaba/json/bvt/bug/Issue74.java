package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;


public class Issue74 extends TestCase {
    public void test_for_issue() throws Exception {
        InputStream is = Issue72.class.getClassLoader().getResourceAsStream("issue74.json");
        String text = IOUtils.toString(is);
        is.close();
        JSONArray json = ((JSONArray) (JSON.parse(text)));
        Assert.assertNotNull(json.getJSONObject(0).getJSONObject("dataType").getJSONObject("categoryType").getJSONArray("dataTypes").get(0));
        Assert.assertSame(json.getJSONObject(0).getJSONObject("dataType"), json.getJSONObject(0).getJSONObject("dataType").getJSONObject("categoryType").getJSONArray("dataTypes").get(0));
    }
}

