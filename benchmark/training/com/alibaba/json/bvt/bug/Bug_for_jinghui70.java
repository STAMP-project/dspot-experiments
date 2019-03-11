package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_jinghui70 extends TestCase {
    public abstract static class IdObject<I> {
        private I id;

        public I getId() {
            return id;
        }

        public void setId(I id) {
            this.id = id;
        }
    }

    public static class Child extends Bug_for_jinghui70.IdObject<Long> {}

    public void test_generic() throws Exception {
        String str = "{\"id\":0}";
        Bug_for_jinghui70.Child child = JSON.parseObject(str, Bug_for_jinghui70.Child.class);
        Assert.assertEquals(Long.class, child.getId().getClass());
    }
}

