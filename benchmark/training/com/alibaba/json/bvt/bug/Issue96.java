package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.lang.reflect.Type;
import junit.framework.TestCase;


public class Issue96 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue96.Page<Issue96.Sub> page = new Issue96.Page<Issue96.Sub>(new Issue96.Sub(1));
        Type type = getType();
        // this is ok
        Issue96.Page<Issue96.Sub> page1 = JSON.parseObject(JSON.toJSONString(page), type);
        System.out.println(page1.sub.getClass());
    }

    static class Page<T> {
        public Page() {
            super();
        }

        public Page(T sub) {
            super();
            this.sub = sub;
        }

        T sub;

        public T getSub() {
            return sub;
        }

        public void setSub(T sub) {
            this.sub = sub;
        }
    }

    static class Sub {
        public Sub() {
            super();
        }

        public Sub(int id) {
            super();
            this.id = id;
        }

        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

