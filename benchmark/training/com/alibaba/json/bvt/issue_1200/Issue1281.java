package com.alibaba.json.bvt.issue_1200;


import java.lang.reflect.Type;
import junit.framework.TestCase;


/**
 * Created by wenshao on 24/06/2017.
 */
public class Issue1281 extends TestCase {
    public void test_for_issue() throws Exception {
        Type type1 = getType();
        Type type2 = getType();
        TestCase.assertSame(type1, type2);
    }

    public static class Result<T> {
        public T value;
    }
}

