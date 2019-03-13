package org.springside.modules.utils.reflect;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.junit.Test;
import org.springside.modules.utils.base.type.UncheckedException;
import org.springside.modules.utils.collection.ListUtil;


public class ReflectionUtilTest {
    @Test
    public void getAndSetFieldValue() {
        ReflectionUtilTest.TestBean bean = new ReflectionUtilTest.TestBean();
        // ??getter??, ????privateField
        assertThat(ReflectionUtil.getFieldValue(bean, "privateField")).isEqualTo(1);
        // ???getter??, ??????privateField
        assertThat(ReflectionUtil.getProperty(bean, "privateField")).isEqualTo(1);
        // ???publicField+1?getter??,????publicField????
        assertThat(ReflectionUtil.getFieldValue(bean, "publicField")).isEqualTo(1);
        // ???getter??, ?????????publicField
        assertThat(ReflectionUtil.getProperty(bean, "publicField")).isEqualTo(2);
        bean = new ReflectionUtilTest.TestBean();
        // ??setter??, ????privateField
        ReflectionUtil.setFieldValue(bean, "privateField", 2);
        assertThat(bean.inspectPrivateField()).isEqualTo(2);
        ReflectionUtil.setProperty(bean, "privateField", 3);
        assertThat(bean.inspectPrivateField()).isEqualTo(3);
        // ???publicField+1?setter??,????publicField????
        ReflectionUtil.setFieldValue(bean, "publicField", 2);
        assertThat(bean.inspectPublicField()).isEqualTo(2);
        // ?????publicField+1?setter??
        ReflectionUtil.setProperty(bean, "publicField", 3);
        assertThat(bean.inspectPublicField()).isEqualTo(4);
        try {
            ReflectionUtil.getFieldValue(bean, "notExist");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // NOSONAR
        }
        try {
            ReflectionUtil.setFieldValue(bean, "notExist", 2);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // NOSONAR
        }
    }

    @Test
    public void invokeGetterAndSetter() {
        ReflectionUtilTest.TestBean bean = new ReflectionUtilTest.TestBean();
        assertThat(ReflectionUtil.invokeGetter(bean, "publicField")).isEqualTo(((bean.inspectPublicField()) + 1));
        bean = new ReflectionUtilTest.TestBean();
        // ??setter????+1
        ReflectionUtil.invokeSetter(bean, "publicField", 10);
        assertThat(bean.inspectPublicField()).isEqualTo((10 + 1));
    }

    @Test
    public void invokeMethod() {
        ReflectionUtilTest.TestBean bean = new ReflectionUtilTest.TestBean();
        // ?????+???????, ?????
        assertThat(ReflectionUtil.invokeMethod(bean, "privateMethod", new Object[]{ "calvin" })).isEqualTo("hello calvin");
        // ?????+???????
        assertThat(ReflectionUtil.invokeMethod(bean, "privateMethod", new Object[]{ "calvin" }, new Class[]{ String.class })).isEqualTo("hello calvin");
        // ??????
        assertThat(ReflectionUtil.invokeMethodByName(bean, "privateMethod", new Object[]{ "calvin" })).isEqualTo("hello calvin");
        // ????
        assertThat(ReflectionUtil.invokeMethod(bean, "intType", new Object[]{ 1 }, new Class[]{ int.class })).isEqualTo(1);
        assertThat(ReflectionUtil.invokeMethod(bean, "integerType", new Object[]{ 1 }, new Class[]{ Integer.class })).isEqualTo(1);
        assertThat(ReflectionUtil.invokeMethod(bean, "listType", new Object[]{ ListUtil.newArrayList("1", "2") }, new Class[]{ List.class })).isEqualTo(2);
        assertThat(ReflectionUtil.invokeMethod(bean, "intType", 1)).isEqualTo(1);
        assertThat(ReflectionUtil.invokeMethod(bean, "integerType", 1)).isEqualTo(1);
        assertThat(ReflectionUtil.invokeMethod(bean, "listType", ListUtil.newArrayList("1", "2"))).isEqualTo(2);
        // ????
        try {
            ReflectionUtil.invokeMethod(bean, "notExistMethod", new Object[]{ "calvin" }, new Class[]{ String.class });
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
        }
        // ?????
        try {
            ReflectionUtil.invokeMethod(bean, "privateMethod", new Object[]{ "calvin" }, new Class[]{ Integer.class });
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (RuntimeException e) {
        }
        // ????
        try {
            ReflectionUtil.invokeMethodByName(bean, "notExistMethod", new Object[]{ "calvin" });
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void invokeConstructor() {
        ReflectionUtilTest.TestBean bean = ReflectionUtil.invokeConstructor(ReflectionUtilTest.TestBean.class);
        assertThat(bean.getPublicField()).isEqualTo(2);
        ReflectionUtilTest.TestBean3 bean3 = ReflectionUtil.invokeConstructor(ReflectionUtilTest.TestBean3.class, 4);
        assertThat(bean3.getId()).isEqualTo(4);
    }

    @Test
    public void convertReflectionExceptionToUnchecked() {
        IllegalArgumentException iae = new IllegalArgumentException();
        // ReflectionException,normal
        RuntimeException e = ReflectionUtil.convertReflectionExceptionToUnchecked(iae);
        assertThat(e).isEqualTo(iae);
        // InvocationTargetException,extract it's target exception.
        Exception ex = new Exception();
        e = ReflectionUtil.convertReflectionExceptionToUnchecked(new InvocationTargetException(ex));
        assertThat(e.getCause()).isEqualTo(ex);
        // UncheckedException, ignore it.
        RuntimeException re = new RuntimeException("abc");
        e = ReflectionUtil.convertReflectionExceptionToUnchecked(re);
        assertThat(e).hasMessage("abc");
        // Unexcepted Checked exception.
        e = ReflectionUtil.convertReflectionExceptionToUnchecked(ex);
        assertThat(e).isInstanceOf(UncheckedException.class);
    }

    public static class ParentBean<T, ID> {}

    public static class TestBean extends ReflectionUtilTest.ParentBean<String, Long> {
        /**
         * ??getter/setter?field
         */
        private int privateField = 1;

        /**
         * ?getter/setter?field
         */
        private int publicField = 1;

        // ??getter???????+1
        public int getPublicField() {
            return (publicField) + 1;
        }

        // ??setter?????????1
        public void setPublicField(int publicField) {
            this.publicField = publicField + 1;
        }

        public int inspectPrivateField() {
            return privateField;
        }

        public int inspectPublicField() {
            return publicField;
        }

        private String privateMethod(String text) {
            return "hello " + text;
        }

        // ????????
        public Integer integerType(Integer i) {
            return i;
        }

        // ????????
        public int intType(int i) {
            return i;
        }

        // ???????
        public int listType(List<?> list) {
            return list.size();
        }
    }

    public static class TestBean2 extends ReflectionUtilTest.ParentBean {}

    public static class TestBean3 {
        public TestBean3() {
        }

        public TestBean3(int id) {
            super();
            this.id = id;
        }

        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

