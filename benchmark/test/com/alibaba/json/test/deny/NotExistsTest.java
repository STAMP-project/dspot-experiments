package com.alibaba.json.test.deny;


import com.alibaba.fastjson.util.TypeUtils;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;
import junit.framework.TestCase;


/**
 * Created by wenshao on 28/01/2017.
 */
public class NotExistsTest extends TestCase {
    public void test_0() throws Exception {
        Field field = TypeUtils.class.getDeclaredField("mappings");
        field.setAccessible(true);
        ConcurrentMap<String, Class<?>> mappings = ((ConcurrentMap<String, Class<?>>) (field.get(null)));
        System.out.println(mappings.size());
        // ParserConfig.global.setAutoTypeSupport(true);
        for (int i = 0; i < 10; ++i) {
            long start = System.currentTimeMillis();
            perf();
            long millis = (System.currentTimeMillis()) - start;
            System.out.println(("millis : " + millis));
        }
    }
}

