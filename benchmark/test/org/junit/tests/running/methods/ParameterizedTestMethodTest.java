package org.junit.tests.running.methods;


import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ParameterizedTestMethodTest {
    @SuppressWarnings("all")
    public static class EverythingWrong {
        private EverythingWrong() {
        }

        @BeforeClass
        public void notStaticBC() {
        }

        @BeforeClass
        static void notPublicBC() {
        }

        @BeforeClass
        public static int nonVoidBC() {
            return 0;
        }

        @BeforeClass
        public static void argumentsBC(int i) {
        }

        @BeforeClass
        public static void fineBC() {
        }

        @AfterClass
        public void notStaticAC() {
        }

        @AfterClass
        static void notPublicAC() {
        }

        @AfterClass
        public static int nonVoidAC() {
            return 0;
        }

        @AfterClass
        public static void argumentsAC(int i) {
        }

        @AfterClass
        public static void fineAC() {
        }

        @After
        public static void staticA() {
        }

        @After
        void notPublicA() {
        }

        @After
        public int nonVoidA() {
            return 0;
        }

        @After
        public void argumentsA(int i) {
        }

        @After
        public void fineA() {
        }

        @Before
        public static void staticB() {
        }

        @Before
        void notPublicB() {
        }

        @Before
        public int nonVoidB() {
            return 0;
        }

        @Before
        public void argumentsB(int i) {
        }

        @Before
        public void fineB() {
        }

        @Test
        public static void staticT() {
        }

        @Test
        void notPublicT() {
        }

        @Test
        public int nonVoidT() {
            return 0;
        }

        @Test
        public void argumentsT(int i) {
        }

        @Test
        public void fineT() {
        }
    }

    private Class<?> fClass;

    private int fErrorCount;

    public static class SuperWrong {
        @Test
        void notPublic() {
        }
    }

    public static class SubWrong extends ParameterizedTestMethodTest.SuperWrong {
        @Test
        public void justFine() {
        }
    }

    public static class SubShadows extends ParameterizedTestMethodTest.SuperWrong {
        @Override
        @Test
        public void notPublic() {
        }
    }

    public ParameterizedTestMethodTest(Class<?> class1, int errorCount) {
        fClass = class1;
        fErrorCount = errorCount;
    }

    @Test
    public void testFailures() throws Exception {
        List<Throwable> problems = validateAllMethods(fClass);
        Assert.assertEquals(fErrorCount, problems.size());
    }
}

