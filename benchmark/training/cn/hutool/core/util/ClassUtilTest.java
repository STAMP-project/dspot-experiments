package cn.hutool.core.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link ClassUtil} ????
 *
 * @author Looly
 */
public class ClassUtilTest {
    @Test
    public void getClassNameTest() {
        String className = ClassUtil.getClassName(ClassUtil.class, false);
        Assert.assertEquals("cn.hutool.core.util.ClassUtil", className);
        String simpleClassName = ClassUtil.getClassName(ClassUtil.class, true);
        Assert.assertEquals("ClassUtil", simpleClassName);
    }

    @SuppressWarnings("unused")
    class TestClass {
        private String privateField;

        protected String field;

        private void privateMethod() {
        }

        public void publicMethod() {
        }
    }

    @SuppressWarnings("unused")
    class TestSubClass extends ClassUtilTest.TestClass {
        private String subField;

        private void privateSubMethod() {
        }

        public void publicSubMethod() {
        }
    }

    @Test
    public void getPublicMethod() {
        Method superPublicMethod = ClassUtil.getPublicMethod(ClassUtilTest.TestSubClass.class, "publicMethod");
        Assert.assertNotNull(superPublicMethod);
        Method superPrivateMethod = ClassUtil.getPublicMethod(ClassUtilTest.TestSubClass.class, "privateMethod");
        Assert.assertNull(superPrivateMethod);
        Method publicMethod = ClassUtil.getPublicMethod(ClassUtilTest.TestSubClass.class, "publicSubMethod");
        Assert.assertNotNull(publicMethod);
        Method privateMethod = ClassUtil.getPublicMethod(ClassUtilTest.TestSubClass.class, "privateSubMethod");
        Assert.assertNull(privateMethod);
    }

    @Test
    public void getDeclaredMethod() throws Exception {
        Method noMethod = ClassUtil.getDeclaredMethod(ClassUtilTest.TestSubClass.class, "noMethod");
        Assert.assertNull(noMethod);
        Method privateMethod = ClassUtil.getDeclaredMethod(ClassUtilTest.TestSubClass.class, "privateMethod");
        Assert.assertNotNull(privateMethod);
        Method publicMethod = ClassUtil.getDeclaredMethod(ClassUtilTest.TestSubClass.class, "publicMethod");
        Assert.assertNotNull(publicMethod);
        Method publicSubMethod = ClassUtil.getDeclaredMethod(ClassUtilTest.TestSubClass.class, "publicSubMethod");
        Assert.assertNotNull(publicSubMethod);
        Method privateSubMethod = ClassUtil.getDeclaredMethod(ClassUtilTest.TestSubClass.class, "privateSubMethod");
        Assert.assertNotNull(privateSubMethod);
    }

    @Test
    public void getDeclaredField() {
        Field noField = ClassUtil.getDeclaredField(ClassUtilTest.TestSubClass.class, "noField");
        Assert.assertNull(noField);
        // ????????
        Field field = ClassUtil.getDeclaredField(ClassUtilTest.TestSubClass.class, "field");
        Assert.assertNull(field);
        Field subField = ClassUtil.getDeclaredField(ClassUtilTest.TestSubClass.class, "subField");
        Assert.assertNotNull(subField);
    }

    @Test
    public void getClassPathTest() {
        String classPath = ClassUtil.getClassPath();
        Assert.assertNotNull(classPath);
    }

    @Test
    public void getShortClassNameTest() {
        String className = "cn.hutool.core.util.StrUtil";
        String result = ClassUtil.getShortClassName(className);
        Assert.assertEquals("c.h.c.u.StrUtil", result);
    }
}

