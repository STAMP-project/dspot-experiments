/**
 * Created on  13-09-09 15:46
 */
package com.alicp.jetcache.anno.method;


import java.io.Serializable;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class ClassUtilTest {
    interface I1 extends Serializable {}

    interface I2 {}

    interface I3 extends ClassUtilTest.I1 , ClassUtilTest.I2 {}

    class C1 {
        public void foo() {
        }

        public String foo(ClassUtilTest.I1 p) {
            return null;
        }

        public String foo2(ClassUtilTest.I1 p) {
            return null;
        }

        public void foo3(byte p2, short p3, char p4, int p5, long p6, float p7, double p8, boolean p9) {
        }
    }

    @Test
    public void testGetAllInterfaces() throws Exception {
        class CI1 implements ClassUtilTest.I3 {}
        class CI2 extends CI1 implements ClassUtilTest.I1 , Cloneable {}
        Object obj = new CI2();
        Class<?>[] is = ClassUtil.getAllInterfaces(obj);
        Assertions.assertEquals(3, is.length);
    }

    @Test
    public void getShortClassNameTest() {
        Assertions.assertNull(ClassUtil.getShortClassName(null));
        Assertions.assertEquals("j.l.String", ClassUtil.getShortClassName("java.lang.String"));
        Assertions.assertEquals("String", ClassUtil.getShortClassName("String"));
    }

    @Test
    public void testGetMethodSig() throws Exception {
        Method m1 = ClassUtilTest.C1.class.getMethod("foo");
        Method m2 = ClassUtilTest.C1.class.getMethod("foo", ClassUtilTest.I1.class);
        Method m3 = ClassUtilTest.C1.class.getMethod("foo2", ClassUtilTest.I1.class);
        String s1 = (m1.getName()) + "()V";
        String s2 = ClassUtil.getMethodSig(m1);
        Assertions.assertEquals(s1, s2);
        s1 = (((m2.getName()) + "(L") + (ClassUtilTest.I1.class.getName().replace('.', '/'))) + ";)Ljava/lang/String;";
        s2 = ClassUtil.getMethodSig(m2);
        Assertions.assertEquals(s1, s2);
        s1 = (((m3.getName()) + "(L") + (ClassUtilTest.I1.class.getName().replace('.', '/'))) + ";)Ljava/lang/String;";
        s2 = ClassUtil.getMethodSig(m3);
        Assertions.assertEquals(s1, s2);
    }
}

