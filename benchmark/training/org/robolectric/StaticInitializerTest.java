package org.robolectric;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.internal.Instrument;
import org.robolectric.internal.SandboxTestRunner;
import org.robolectric.internal.bytecode.RobolectricInternals;
import org.robolectric.internal.bytecode.SandboxConfig;


@RunWith(SandboxTestRunner.class)
public class StaticInitializerTest {
    @Test
    public void whenClassIsUnshadowed_shouldPerformStaticInitialization() throws Exception {
        Assert.assertEquals("Floyd", StaticInitializerTest.ClassWithStaticInitializerA.name);
    }

    @Instrument
    public static class ClassWithStaticInitializerA {
        static String name = "Floyd";
    }

    @Test
    @SandboxConfig(shadows = { StaticInitializerTest.ShadowClassWithoutStaticInitializerOverride.class })
    public void whenClassHasShadowWithoutOverrideMethod_shouldPerformStaticInitialization() throws Exception {
        Assert.assertEquals("Floyd", StaticInitializerTest.ClassWithStaticInitializerB.name);
        RobolectricInternals.performStaticInitialization(StaticInitializerTest.ClassWithStaticInitializerB.class);
        Assert.assertEquals("Floyd", StaticInitializerTest.ClassWithStaticInitializerB.name);
    }

    @Instrument
    public static class ClassWithStaticInitializerB {
        public static String name = "Floyd";
    }

    @Implements(StaticInitializerTest.ClassWithStaticInitializerB.class)
    public static class ShadowClassWithoutStaticInitializerOverride {}

    @Test
    @SandboxConfig(shadows = { StaticInitializerTest.ShadowClassWithStaticInitializerOverride.class })
    public void whenClassHasShadowWithOverrideMethod_shouldDeferStaticInitialization() throws Exception {
        Assert.assertFalse(StaticInitializerTest.ShadowClassWithStaticInitializerOverride.initialized);
        Assert.assertEquals(null, StaticInitializerTest.ClassWithStaticInitializerC.name);
        Assert.assertTrue(StaticInitializerTest.ShadowClassWithStaticInitializerOverride.initialized);
        RobolectricInternals.performStaticInitialization(StaticInitializerTest.ClassWithStaticInitializerC.class);
        Assert.assertEquals("Floyd", StaticInitializerTest.ClassWithStaticInitializerC.name);
    }

    @Instrument
    public static class ClassWithStaticInitializerC {
        public static String name = "Floyd";
    }

    @Implements(StaticInitializerTest.ClassWithStaticInitializerC.class)
    public static class ShadowClassWithStaticInitializerOverride {
        public static boolean initialized = false;

        @Implementation
        protected static void __staticInitializer__() {
            StaticInitializerTest.ShadowClassWithStaticInitializerOverride.initialized = true;
        }
    }
}

