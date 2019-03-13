package net.bytebuddy.utility;


import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.utility.JavaConstant.MethodHandle.Dispatcher.ForLegacyVm.INSTANCE;


public class JavaConstantMethodHandleDispatcherTest {
    @Test(expected = UnsupportedOperationException.class)
    public void testLegacyVmInitialization() throws Exception {
        INSTANCE.initialize();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLegacyVmPublicLookup() throws Exception {
        INSTANCE.publicLookup();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLegacyVmLookupType() throws Exception {
        INSTANCE.lookupType(Mockito.mock(Object.class));
    }

    @SuppressWarnings("unused")
    private abstract class Foo {
        private Foo(Void v) {
            /* empty */
        }

        private Foo(String s) {
            /* empty */
        }

        abstract void a1();

        abstract void a2();

        abstract void a3();

        abstract void a4();

        abstract void a5();

        abstract void a6();

        abstract void a7();

        abstract void a8();

        abstract void a9();

        abstract void a10();

        abstract void a11();

        abstract void a12();

        abstract void a13();

        abstract void a14();

        abstract void a15();

        abstract void a16();

        abstract void a17();

        abstract void a18();

        abstract void a19();

        abstract void a20();

        abstract void a21();

        abstract void a22();

        abstract void a23();
    }
}

