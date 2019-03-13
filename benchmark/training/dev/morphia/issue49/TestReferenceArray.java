package dev.morphia.issue49;


import dev.morphia.TestBase;
import dev.morphia.annotations.Reference;
import dev.morphia.testutil.TestEntity;
import java.util.Arrays;
import org.junit.Test;


public class TestReferenceArray extends TestBase {
    @Test
    public final void testArrayPersistence() {
        TestReferenceArray.A a = new TestReferenceArray.A();
        final TestReferenceArray.B b1 = new TestReferenceArray.B();
        final TestReferenceArray.B b2 = new TestReferenceArray.B();
        a.bs[0] = b1;
        a.bs[1] = b2;
        getDs().save(Arrays.asList(b2, b1, a));
        getDs().get(a);
    }

    public static class A extends TestEntity {
        @Reference
        private final TestReferenceArray.B[] bs = new TestReferenceArray.B[2];
    }

    public static class B extends TestEntity {
        private String foo;
    }
}

