package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_list extends TestCase {
    public void test_list_map() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("val", new Object());
        List list = new ArrayList();
        list.add(map);
        Assert.assertSame(map.get("val"), new JSONPath("$[0].val").eval(list));
        Assert.assertSame(map.get("val"), new JSONPath("$[-1].val").eval(list));
    }
}

