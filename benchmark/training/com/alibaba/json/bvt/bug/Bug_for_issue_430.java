package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_430 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "[{\"@type\": \"com.alibaba.json.bvt.bug.Bug_for_issue_430$FooModel\", \"fooCollection\": null}, {\"@type\": \"com.alibaba.json.bvt.bug.Bug_for_issue_430$FooModel\", \"fooCollection\": null}]";
        JSONArray array = JSON.parseArray(text);
        Assert.assertEquals(Bug_for_issue_430.FooModel.class, array.get(0).getClass());
        Assert.assertEquals(Bug_for_issue_430.FooModel.class, array.get(1).getClass());
        Assert.assertNull(((Bug_for_issue_430.FooModel) (array.get(0))).fooCollection);
        Assert.assertNull(((Bug_for_issue_430.FooModel) (array.get(1))).fooCollection);
    }

    public void test_for_issue_1() throws Exception {
        String text = "[{\"@type\": \"com.alibaba.json.bvt.bug.Bug_for_issue_430$FooModel\", \"fooCollection\": null}, {\"@type\": \"com.alibaba.json.bvt.bug.Bug_for_issue_430$FooModel\", \"fooCollection\": null}]";
        JSONArray array = ((JSONArray) (JSON.parse(text)));
        Assert.assertEquals(Bug_for_issue_430.FooModel.class, array.get(0).getClass());
        Assert.assertEquals(Bug_for_issue_430.FooModel.class, array.get(1).getClass());
        Assert.assertNull(((Bug_for_issue_430.FooModel) (array.get(0))).fooCollection);
        Assert.assertNull(((Bug_for_issue_430.FooModel) (array.get(1))).fooCollection);
    }

    public static class FooModel {
        public ArrayList<Bug_for_issue_430.FooCollectionModel> fooCollection;
    }

    public static class FooCollectionModel {}
}

