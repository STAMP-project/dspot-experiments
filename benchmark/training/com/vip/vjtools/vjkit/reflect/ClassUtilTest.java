package com.vip.vjtools.vjkit.reflect;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Assert;
import org.junit.Test;


public class ClassUtilTest {
    @Test
    public void getMessage() {
        Assert.assertThat(ClassUtil.getShortClassName(ClassUtilTest.class)).isEqualTo("ClassUtilTest");
        Assert.assertThat(ClassUtil.getShortClassName(ClassUtilTest.BClass.class)).isEqualTo("ClassUtilTest.BClass");
        Assert.assertThat(ClassUtil.getShortClassName(ClassUtilTest.class.getName())).isEqualTo("ClassUtilTest");
        Assert.assertThat(ClassUtil.getShortClassName(ClassUtilTest.BClass.class.getName())).isEqualTo("ClassUtilTest.BClass");
        Assert.assertThat(ClassUtil.getPackageName(ClassUtilTest.class)).isEqualTo("com.vip.vjtools.vjkit.reflect");
        Assert.assertThat(ClassUtil.getPackageName(ClassUtilTest.BClass.class)).isEqualTo("com.vip.vjtools.vjkit.reflect");
        Assert.assertThat(ClassUtil.getPackageName(ClassUtilTest.class.getName())).isEqualTo("com.vip.vjtools.vjkit.reflect");
        Assert.assertThat(ClassUtil.getPackageName(ClassUtilTest.BClass.class.getName())).isEqualTo("com.vip.vjtools.vjkit.reflect");
    }

    @Test
    public void getAllClass() {
        Assert.assertThat(ClassUtil.getAllInterfaces(ClassUtilTest.BClass.class)).hasSize(4).contains(ClassUtilTest.AInterface.class, ClassUtilTest.BInterface.class, ClassUtilTest.CInterface.class, ClassUtilTest.DInterface.class);
        Assert.assertThat(ClassUtil.getAllSuperclasses(ClassUtilTest.BClass.class)).hasSize(2).contains(ClassUtilTest.AClass.class, Object.class);
        Assert.assertThat(AnnotationUtil.getAllAnnotations(ClassUtilTest.BClass.class)).hasSize(4);
        Assert.assertThat(AnnotationUtil.getAnnotatedPublicFields(ClassUtilTest.BClass.class, ClassUtilTest.AAnnotation.class)).hasSize(2).contains(ReflectionUtil.getField(ClassUtilTest.BClass.class, "sfield"), ReflectionUtil.getField(ClassUtilTest.BClass.class, "tfield"));
        Assert.assertThat(AnnotationUtil.getAnnotatedFields(ClassUtilTest.BClass.class, ClassUtilTest.EAnnotation.class)).hasSize(3).contains(ReflectionUtil.getField(ClassUtilTest.BClass.class, "bfield"), ReflectionUtil.getField(ClassUtilTest.BClass.class, "efield"), ReflectionUtil.getField(ClassUtilTest.AClass.class, "afield"));
        Assert.assertThat(AnnotationUtil.getAnnotatedFields(ClassUtilTest.BClass.class, ClassUtilTest.FAnnotation.class)).hasSize(1).contains(ReflectionUtil.getField(ClassUtilTest.AClass.class, "dfield"));
        Assert.assertThat(AnnotationUtil.getAnnotatedPublicMethods(ClassUtilTest.BClass.class, ClassUtilTest.FAnnotation.class)).hasSize(3).contains(ReflectionUtil.getAccessibleMethodByName(ClassUtilTest.BClass.class, "hello"), ReflectionUtil.getAccessibleMethodByName(ClassUtilTest.BClass.class, "hello3"), ReflectionUtil.getAccessibleMethodByName(ClassUtilTest.AClass.class, "hello4"));
    }

    @Test
    public void getSuperClassGenericType() {
        // ???1?2?????
        Assert.assertThat(ClassUtil.getClassGenericType(ClassUtilTest.TestBean.class)).isEqualTo(String.class);
        Assert.assertThat(ClassUtil.getClassGenericType(ClassUtilTest.TestBean.class, 1)).isEqualTo(Long.class);
        // ??????????
        Assert.assertThat(ClassUtil.getClassGenericType(ClassUtilTest.TestBean2.class)).isEqualTo(Object.class);
        // ?????
        Assert.assertThat(ClassUtil.getClassGenericType(ClassUtilTest.TestBean3.class)).isEqualTo(Object.class);
    }

    /**
     * Unit test case of {@link com.vip.vjtools.vjkit.reflect.ClassUtil#isSubClassOrInterfaceOf(Class, Class)}
     */
    @Test
    public void testIsSubClassOrInterfaceOf() {
        Assert.assertTrue("TestBean should be subclass of ParentBean", ClassUtil.isSubClassOrInterfaceOf(ClassUtilTest.BClass.class, ClassUtilTest.AClass.class));
        Assert.assertTrue("BInterface should be subinterface of AInterface", ClassUtil.isSubClassOrInterfaceOf(ClassUtilTest.BInterface.class, ClassUtilTest.AInterface.class));
        Assert.assertTrue("BClass should be an implementation of BInterface", ClassUtil.isSubClassOrInterfaceOf(ClassUtilTest.BClass.class, ClassUtilTest.BInterface.class));
        Assert.assertTrue("BClass should be an implementation of AInterface", ClassUtil.isSubClassOrInterfaceOf(ClassUtilTest.BClass.class, ClassUtilTest.AInterface.class));
    }

    public static class ParentBean<T, ID> {}

    public static class TestBean extends ClassUtilTest.ParentBean<String, Long> {}

    public static class TestBean2 extends ClassUtilTest.ParentBean {}

    public static class TestBean3 {}

    public interface AInterface {}

    @ClassUtilTest.CAnnotation
    public interface BInterface extends ClassUtilTest.AInterface {
        @ClassUtilTest.FAnnotation
        void hello();
    }

    public interface CInterface {}

    public interface DInterface {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    @ClassUtilTest.AAnnotation
    public @interface BAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface CAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface DAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface EAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface FAnnotation {}

    @ClassUtilTest.DAnnotation
    public static class AClass implements ClassUtilTest.DInterface {
        @ClassUtilTest.EAnnotation
        private int afield;

        private int cfield;

        @ClassUtilTest.FAnnotation
        private int dfield;

        @ClassUtilTest.AAnnotation
        public int tfield;

        @ClassUtilTest.AAnnotation
        protected int vfield;

        // not counted as public annotated method
        public void hello2(int i) {
        }

        // counted as public annotated method
        @ClassUtilTest.FAnnotation
        public void hello4(int i) {
        }

        // not counted as public annotated method
        @ClassUtilTest.FAnnotation
        protected void hello5(int i) {
        }

        // not counted as public annotated method
        @ClassUtilTest.FAnnotation
        private void hello6(int i) {
        }

        // not counted as public annotated method, because the child override it
        @ClassUtilTest.FAnnotation
        public void hello7(int i) {
        }
    }

    @ClassUtilTest.BAnnotation
    public static class BClass extends ClassUtilTest.AClass implements ClassUtilTest.BInterface , ClassUtilTest.CInterface {
        @ClassUtilTest.EAnnotation
        private int bfield;

        @ClassUtilTest.EAnnotation
        private int efield;

        @ClassUtilTest.AAnnotation
        public int sfield;

        @ClassUtilTest.AAnnotation
        protected int ufield;

        // counted as public annotated method, BInterface
        @Override
        @ClassUtilTest.EAnnotation
        public void hello() {
            // TODO Auto-generated method stub
        }

        public void hello2(int i) {
        }

        // counted as public annotated method
        @ClassUtilTest.FAnnotation
        public void hello3(int i) {
        }

        // not counted as public annotated method
        @Override
        public void hello7(int i) {
        }
    }
}

