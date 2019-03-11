package com.alibaba.json.bvt.compatible;


import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 20/03/2017.
 */
public class TypeUtilsComputeGettersTest extends TestCase {
    public void test_for_computeGetters() {
        List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(TypeUtilsComputeGettersTest.Model.class, null);
        TestCase.assertEquals(1, fieldInfoList.size());
        TestCase.assertEquals("id", fieldInfoList.get(0).name);
    }

    public static class Model {
        private int id;

        public int getId() {
            return id;
        }
    }
}

