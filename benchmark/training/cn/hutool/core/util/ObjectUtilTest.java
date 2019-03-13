package cn.hutool.core.util;


import cn.hutool.core.clone.CloneSupport;
import cn.hutool.core.collection.CollUtil;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class ObjectUtilTest {
    @Test
    public void cloneTest() {
        ObjectUtilTest.Obj obj = new ObjectUtilTest.Obj();
        ObjectUtilTest.Obj obj2 = ObjectUtil.clone(obj);
        Assert.assertEquals("OK", obj2.doSomeThing());
    }

    static class Obj extends CloneSupport<ObjectUtilTest.Obj> {
        public String doSomeThing() {
            return "OK";
        }
    }

    @Test
    public void toStringTest() {
        ArrayList<String> strings = CollUtil.newArrayList("1", "2");
        String result = ObjectUtil.toString(strings);
        Assert.assertEquals("[1, 2]", result);
    }
}

