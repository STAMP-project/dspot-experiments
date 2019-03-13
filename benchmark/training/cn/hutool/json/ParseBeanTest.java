package cn.hutool.json;


import cn.hutool.core.collection.CollUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??Bean???List??????????<br>
 * ??????class?????????
 *
 * @author looly
 */
public class ParseBeanTest {
    @Test
    public void parseBeanTest() {
        C c1 = new C();
        c1.setTest("test1");
        C c2 = new C();
        c2.setTest("test2");
        B b1 = new B();
        b1.setCs(CollUtil.newArrayList(c1, c2));
        B b2 = new B();
        b2.setCs(CollUtil.newArrayList(c1, c2));
        A a = new A();
        a.setBs(CollUtil.newArrayList(b1, b2));
        JSONObject json = JSONUtil.parseObj(a);
        A a1 = JSONUtil.toBean(json, A.class);
        Assert.assertEquals(json.toString(), JSONUtil.toJsonStr(a1));
    }
}

