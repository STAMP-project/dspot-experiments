package org.hswebframework.web.bean;


import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author zhouhao
 * @since 3.0
 */
public class FastBeanCopierTest {
    @Test
    public void test() throws IllegalAccessException, InvocationTargetException {
        Source source = new Source();
        setAge(100);
        setName("??");
        setIds(new String[]{ "1", "2", "3" });
        setAge2(2);
        setBoy2(true);
        setColor(Color.RED);
        setNestObject2(Collections.singletonMap("name", "mapTest"));
        NestObject nestObject = new NestObject();
        setAge(10);
        setPassword("1234567");
        setName("??2");
        setNestObject(nestObject);
        setNestObject3(nestObject);
        Target target = new Target();
        FastBeanCopier.copy(source, target);
        long t = System.currentTimeMillis();
        // for (int i = 10_0000; i > 0; i--) {
        // FastBeanCopier.copy(source, target);
        // }
        System.out.println(((System.currentTimeMillis()) - t));
        System.out.println(source);
        System.out.println(target);
        System.out.println(((getNestObject()) == (getNestObject())));
        // Source source1=new Source();
        // FastBeanCopier.copy(source,source1);
        // System.out.println(source1);
        // 
        // t = System.currentTimeMillis();
        // 
        // for (int i = 100_0000; i > 0; i--) {
        // try {
        // BeanUtils.copyProperties(source, target);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // System.out.println(System.currentTimeMillis() - t);
        // System.out.println(target);
        // System.out.println(target.getNestObject() == source.getNestObject());
    }

    @Test
    public void testCopyMap() {
        Source source = new Source();
        setAge(100);
        setName("??");
        // source.setIds(new String[]{"1", "2", "3"});
        NestObject nestObject = new NestObject();
        setAge(10);
        setName("??2");
        // source.setNestObject(nestObject);
        Map<String, Object> target = new HashMap<>();
        System.out.println(FastBeanCopier.copy(source, target, FastBeanCopier.include("age")));
        System.out.println(target);
        System.out.println(FastBeanCopier.copy(target, new Source()));
    }
}

