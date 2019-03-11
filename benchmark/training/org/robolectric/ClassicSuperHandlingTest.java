package org.robolectric;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.annotation.internal.Instrument;
import org.robolectric.internal.SandboxTestRunner;
import org.robolectric.internal.bytecode.SandboxConfig;


@RunWith(SandboxTestRunner.class)
public class ClassicSuperHandlingTest {
    @Test
    @SandboxConfig(shadows = { ClassicSuperHandlingTest.ChildShadow.class, ClassicSuperHandlingTest.ParentShadow.class, ClassicSuperHandlingTest.GrandparentShadow.class })
    public void uninstrumentedSubclassesShouldBeAbleToCallSuperWithoutLooping() throws Exception {
        Assert.assertEquals("4-3s-2s-1s-boof", new ClassicSuperHandlingTest.BabiesHavingBabies().method("boof"));
    }

    @Test
    @SandboxConfig(shadows = { ClassicSuperHandlingTest.ChildShadow.class, ClassicSuperHandlingTest.ParentShadow.class, ClassicSuperHandlingTest.GrandparentShadow.class })
    public void shadowInvocationWhenAllAreShadowed() throws Exception {
        Assert.assertEquals("3s-2s-1s-boof", new ClassicSuperHandlingTest.Child().method("boof"));
        Assert.assertEquals("2s-1s-boof", new ClassicSuperHandlingTest.Parent().method("boof"));
        Assert.assertEquals("1s-boof", new ClassicSuperHandlingTest.Grandparent().method("boof"));
    }

    @Implements(ClassicSuperHandlingTest.Child.class)
    public static class ChildShadow extends ClassicSuperHandlingTest.ParentShadow {
        @RealObject
        private ClassicSuperHandlingTest.Child realObject;

        @Override
        public String method(String value) {
            return "3s-" + (super.method(value));
        }
    }

    @Implements(ClassicSuperHandlingTest.Parent.class)
    public static class ParentShadow extends ClassicSuperHandlingTest.GrandparentShadow {
        @RealObject
        private ClassicSuperHandlingTest.Parent realObject;

        @Override
        public String method(String value) {
            return "2s-" + (super.method(value));
        }
    }

    @Implements(ClassicSuperHandlingTest.Grandparent.class)
    public static class GrandparentShadow {
        @RealObject
        private ClassicSuperHandlingTest.Grandparent realObject;

        public String method(String value) {
            return "1s-" + value;
        }
    }

    private static class BabiesHavingBabies extends ClassicSuperHandlingTest.Child {
        @Override
        public String method(String value) {
            return "4-" + (super.method(value));
        }
    }

    @Instrument
    public static class Child extends ClassicSuperHandlingTest.Parent {
        @Override
        public String method(String value) {
            throw new RuntimeException("Stub!");
        }
    }

    @Instrument
    public static class Parent extends ClassicSuperHandlingTest.Grandparent {
        @Override
        public String method(String value) {
            throw new RuntimeException("Stub!");
        }
    }

    @Instrument
    private static class Grandparent {
        public String method(String value) {
            throw new RuntimeException("Stub!");
        }
    }
}

