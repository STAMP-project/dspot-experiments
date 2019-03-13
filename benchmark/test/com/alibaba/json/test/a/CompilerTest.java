package com.alibaba.json.test.a;


import java.io.Serializable;
import junit.framework.TestCase;


/**
 * Created by wenshao on 04/02/2017.
 */
public class CompilerTest extends TestCase {
    public void test_for_compiler() throws Exception {
        byte[] bytes;
        {
            CompilerTest.Model model = new CompilerTest.Model();
            model.id = 123;
            bytes = toBytes(model);
        }
        perf(bytes);
        for (int i = 0; i < 10; ++i) {
            long start = System.currentTimeMillis();
            perf(bytes);
            long millis = (System.currentTimeMillis()) - start;
            System.out.println(("millis : " + millis));
        }
    }

    public static class Model implements Serializable {
        public int id;
    }
}

