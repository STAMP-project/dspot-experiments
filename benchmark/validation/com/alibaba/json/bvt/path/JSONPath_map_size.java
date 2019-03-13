package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_map_size extends TestCase {
    public void test_list_size() throws Exception {
        Assert.assertEquals(0, JSONPath.eval(Collections.emptyMap(), "$.size"));
    }

    public void test_list_size1() throws Exception {
        Assert.assertEquals(0, JSONPath.eval(Collections.emptyMap(), "$.size()"));
    }
}

