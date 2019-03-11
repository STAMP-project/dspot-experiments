package com.alibaba.json.bvt.serializer;


import SerializeConfig.globalInstance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.FieldSerializer;
import com.alibaba.fastjson.serializer.JavaBeanSerializer;
import com.alibaba.json.bvtVO.alipay.PlatformDepartmentVO;
import java.lang.reflect.Field;
import junit.framework.TestCase;


public class Bug_for_yegaofei extends TestCase {
    public void test_0() throws Exception {
        PlatformDepartmentVO vo = new PlatformDepartmentVO();
        vo.setId("xx");
        JSON.toJSONString(vo);
        JavaBeanSerializer serializer = ((JavaBeanSerializer) (globalInstance.getObjectWriter(PlatformDepartmentVO.class)));
        Field field = JavaBeanSerializer.class.getDeclaredField("getters");
        field.setAccessible(true);
        FieldSerializer[] getters = ((FieldSerializer[]) (field.get(serializer)));
        for (FieldSerializer getter : getters) {
            TestCase.assertNotNull(getter);
        }
    }
}

