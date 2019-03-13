package org.hswebframework.web;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class ListsTests {
    @Test
    public void testCreate() {
        Assert.assertEquals(Lists.buildList(2).add(1).get().get(0), ((Integer) (2)));
        Assert.assertEquals(Lists.buildList(new ArrayList()).add(2, 1).get().get(0), 2);
        Assert.assertEquals(Lists.buildList(ArrayList::new).add(2, 1).get().get(0), 2);
    }
}

