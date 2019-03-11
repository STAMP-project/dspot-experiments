package org.junit.internal;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


public class MethodSorterTest {
    private static final String ALPHA = "java.lang.Object alpha(int,double,java.lang.Thread)";

    private static final String BETA = "void beta(int[][])";

    private static final String GAMMA_VOID = "int gamma()";

    private static final String GAMMA_BOOLEAN = "void gamma(boolean)";

    private static final String DELTA = "void delta()";

    private static final String EPSILON = "void epsilon()";

    private static final String SUPER_METHOD = "void superMario()";

    private static final String SUB_METHOD = "void subBowser()";

    static class DummySortWithoutAnnotation {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    static class Super {
        void superMario() {
        }
    }

    static class Sub extends MethodSorterTest.Super {
        void subBowser() {
        }
    }

    @Test
    public void testMethodsNullSorterSelf() {
        List<String> expected = Arrays.asList(MethodSorterTest.EPSILON, MethodSorterTest.BETA, MethodSorterTest.ALPHA, MethodSorterTest.DELTA, MethodSorterTest.GAMMA_VOID, MethodSorterTest.GAMMA_BOOLEAN);
        List<String> actual = getDeclaredMethodNames(MethodSorterTest.DummySortWithoutAnnotation.class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMethodsNullSorterSuper() {
        List<String> expected = Arrays.asList(MethodSorterTest.SUPER_METHOD);
        List<String> actual = getDeclaredMethodNames(MethodSorterTest.Super.class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testMethodsNullSorterSub() {
        List<String> expected = Arrays.asList(MethodSorterTest.SUB_METHOD);
        List<String> actual = getDeclaredMethodNames(MethodSorterTest.Sub.class);
        Assert.assertEquals(expected, actual);
    }

    @FixMethodOrder(MethodSorters.DEFAULT)
    static class DummySortWithDefault {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    @Test
    public void testDefaultMethodSorter() {
        List<String> expected = Arrays.asList(MethodSorterTest.EPSILON, MethodSorterTest.BETA, MethodSorterTest.ALPHA, MethodSorterTest.DELTA, MethodSorterTest.GAMMA_VOID, MethodSorterTest.GAMMA_BOOLEAN);
        List<String> actual = getDeclaredMethodNames(MethodSorterTest.DummySortWithDefault.class);
        Assert.assertEquals(expected, actual);
    }

    @FixMethodOrder(MethodSorters.JVM)
    static class DummySortJvm {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    @Test
    public void testJvmMethodSorter() {
        Method[] fromJvmWithSynthetics = MethodSorterTest.DummySortJvm.class.getDeclaredMethods();
        Method[] sorted = MethodSorter.getDeclaredMethods(MethodSorterTest.DummySortJvm.class);
        Assert.assertArrayEquals(fromJvmWithSynthetics, sorted);
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    static class DummySortWithNameAsc {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    @Test
    public void testAscendingMethodSorter() {
        List<String> expected = Arrays.asList(MethodSorterTest.ALPHA, MethodSorterTest.BETA, MethodSorterTest.DELTA, MethodSorterTest.EPSILON, MethodSorterTest.GAMMA_VOID, MethodSorterTest.GAMMA_BOOLEAN);
        List<String> actual = getDeclaredMethodNames(MethodSorterTest.DummySortWithNameAsc.class);
        Assert.assertEquals(expected, actual);
    }
}

