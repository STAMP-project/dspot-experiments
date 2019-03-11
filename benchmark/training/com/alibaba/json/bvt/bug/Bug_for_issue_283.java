package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_283 extends TestCase {
    public void test_for_issue() throws Exception {
        String jsons = "[[1,1,1,2,3],[2,3,12,3,4],[1],[2]]";
        // 
        Collection<Collection<Integer>> collections = JSON.parseObject(jsons, new com.alibaba.fastjson.TypeReference<Collection<Collection<Integer>>>() {});
        Assert.assertEquals(4, collections.size());
        Assert.assertEquals(ArrayList.class, collections.getClass());
        Collection<Integer> firstItemCollection = collections.iterator().next();
        Assert.assertEquals(5, firstItemCollection.size());
        Assert.assertEquals(ArrayList.class, firstItemCollection.getClass());
    }
}

