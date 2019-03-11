package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest9 extends TestCase {
    public void test_bug_for_wanglin() throws Exception {
        RefTest9.VO vo = new RefTest9.VO();
        RefTest9.A a = new RefTest9.A();
        vo.setA(a);
        vo.getValues().add(a);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"a\":{},\"values\":[{\"$ref\":\"$.a\"}]}", text);
        RefTest9.VO vo2 = JSON.parseObject(text, RefTest9.VO.class);
    }

    public static class VO {
        private RefTest9.A a;

        private Set<RefTest9.A> values = new HashSet<RefTest9.A>();

        public RefTest9.A getA() {
            return a;
        }

        public void setA(RefTest9.A a) {
            this.a = a;
        }

        public Set<RefTest9.A> getValues() {
            return values;
        }

        public void setValues(Set<RefTest9.A> values) {
            this.values = values;
        }
    }

    public static class A {}
}

