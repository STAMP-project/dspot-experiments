package com.alibaba.json.bvt.bug;


import Feature.UseObjectArray;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_423 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "[[],{\"value\":[]}]";
        Object root = JSON.parse(text, UseObjectArray);
        Assert.assertEquals(Object[].class, root.getClass());
        Object[] rootArray = ((Object[]) (root));
        Assert.assertEquals(Object[].class, rootArray[0].getClass());
        Assert.assertEquals(Object[].class, get("value").getClass());
    }
}

