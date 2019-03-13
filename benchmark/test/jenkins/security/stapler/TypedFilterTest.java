package jenkins.security.stapler;


import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;


@Issue("SECURITY-400")
public class TypedFilterTest extends StaplerAbstractTest {
    @TestExtension
    public static class GetTarget1 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public StaplerAbstractTest.Renderable getTarget() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getTarget_withoutArg_isNotRoutableDirectly() throws Exception {
        assertNotReachable("getTarget1/target/");
    }

    @TestExtension
    public static class GetTarget2 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        @StaplerDispatchable
        public StaplerAbstractTest.Renderable getTarget() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getTarget_withoutArg_isRoutableWithAnnotation() throws Exception {
        assertReachable("getTarget2/target/");
    }

    @TestExtension
    public static class GetTarget3 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable getTarget() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getTarget_withArg_isNotRoutableWithStaplerNotDispatchable() throws Exception {
        assertNotReachable("getTarget3/target/");
    }

    @TestExtension
    public static class GetTarget4 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public StaplerAbstractTest.Renderable getTarget(StaplerRequest req) {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getTarget_withArg_isRoutable() throws Exception {
        assertReachable("getTarget4/target/");
    }

    @TestExtension
    public static class GetStaplerFallback1 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public StaplerAbstractTest.Renderable getStaplerFallback() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getStaplerFallback_withoutArg_isNotRoutableDirectly() throws Exception {
        assertNotReachable("getStaplerFallback1/staplerFallback/");
    }

    @TestExtension
    public static class GetStaplerFallback2 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        @StaplerDispatchable
        public StaplerAbstractTest.Renderable getStaplerFallback() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getStaplerFallback_withoutArg_isRoutableWithAnnotation() throws Exception {
        assertReachable("getStaplerFallback2/staplerFallback/");
    }

    @TestExtension
    public static class GetStaplerFallback3 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable getStaplerFallback() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getStaplerFallback_withArg_isNotRoutableWithStaplerNotDispatchable() throws Exception {
        assertNotReachable("getStaplerFallback3/staplerFallback/");
    }

    @TestExtension
    public static class GetStaplerFallback4 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public StaplerAbstractTest.Renderable getStaplerFallback(StaplerRequest req) {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getStaplerFallback_withArg_isRoutable() throws Exception {
        assertReachable("getStaplerFallback4/staplerFallback/");
    }

    public static class TypeImplementingStaplerProxy implements StaplerProxy {
        @Override
        public Object getTarget() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    public static class TypeExtendingTypeImplementingStaplerProxy extends TypedFilterTest.TypeImplementingStaplerProxy {}

    // FIXME @StaplerNotDispatchable
    public static class TypeImplementingStaplerProxy2 implements StaplerProxy {
        @Override
        public Object getTarget() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    public static class TypeExtendingTypeImplementingStaplerProxy2 extends TypedFilterTest.TypeImplementingStaplerProxy2 {}

    @TestExtension
    public static class GetTypeImplementingStaplerProxy extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public TypedFilterTest.TypeImplementingStaplerProxy getTypeImplementingStaplerProxy() {
            return new TypedFilterTest.TypeImplementingStaplerProxy();
        }

        public TypedFilterTest.TypeExtendingTypeImplementingStaplerProxy getTypeExtendingTypeImplementingStaplerProxy() {
            return new TypedFilterTest.TypeExtendingTypeImplementingStaplerProxy();
        }

        public TypedFilterTest.TypeImplementingStaplerProxy2 getTypeImplementingStaplerProxy2() {
            return new TypedFilterTest.TypeImplementingStaplerProxy2();
        }

        public TypedFilterTest.TypeExtendingTypeImplementingStaplerProxy2 getTypeExtendingTypeImplementingStaplerProxy2() {
            return new TypedFilterTest.TypeExtendingTypeImplementingStaplerProxy2();
        }
    }

    @Test
    public void typeImplementingStaplerProxy_isRoutableByDefault() throws Exception {
        assertReachable("getTypeImplementingStaplerProxy/typeImplementingStaplerProxy/");
        assertReachable("getTypeImplementingStaplerProxy/typeImplementingStaplerProxy/valid");
    }

    @Test
    public void typeExtendingParentImplementingStaplerProxy_isRoutableByDefault() throws Exception {
        assertReachable("getTypeImplementingStaplerProxy/typeExtendingTypeImplementingStaplerProxy/");
        assertReachable("getTypeImplementingStaplerProxy/typeExtendingTypeImplementingStaplerProxy/valid/");
    }

    @Test
    public void typeImplementingStaplerProxy_isNotRoutableWithNonroutable() throws Exception {
        // TODO no way to avoid routability if implementing StaplerProxy
        // assertNotReachable("getTypeImplementingStaplerProxy/typeImplementingStaplerProxy2/");
        // assertNotReachable("getTypeImplementingStaplerProxy/typeImplementingStaplerProxy2/valid/");
    }

    @Test
    public void typeExtendingParentImplementingStaplerProxy_isNotRoutableWithNonroutable() throws Exception {
        // TODO no way to avoid routability if super type implementing StaplerProxy
        // assertNotReachable("getTypeImplementingStaplerProxy/typeExtendingTypeImplementingStaplerProxy2/");
        // assertNotReachable("getTypeImplementingStaplerProxy/typeExtendingTypeImplementingStaplerProxy2/valid/");
    }

    @TestExtension
    public static class GetDynamic1 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public StaplerAbstractTest.Renderable getDynamic() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getDynamic_withoutArg_isRoutable() throws Exception {
        assertReachable("getDynamic1/dynamic/");
        assertNotReachable("getDynamic1/<anyString>/");
    }

    @TestExtension
    public static class GetDynamic2 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public StaplerAbstractTest.Renderable getDynamic(String someArgs) {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getDynamic_withArgStartingWithString_isRoutable() throws Exception {
        // dynamic is "just" a subcase of regular getDynamic usage
        assertReachable("getDynamic2/dynamic/");
        assertReachable("getDynamic2/<anyString>/");
    }

    @TestExtension
    public static class GetDynamic3 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public StaplerAbstractTest.Renderable getDynamic(StaplerRequest req, String someArgs) {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getDynamic_withArgNotStartingWithString_isNotRoutable() throws Exception {
        assertNotReachable("getDynamic3/dynamic/");
        assertNotReachable("getDynamic3/<anyString>/");
    }

    @TestExtension
    public static class GetDynamic4 extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public StaplerAbstractTest.Renderable getDynamic(StaplerRequest req) {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void getDynamic_withArgNotIncludingString_isRoutable() throws Exception {
        assertReachable("getDynamic4/dynamic/");
        // there is no magic here, as the string argument is missing, just a regular getter
        assertNotReachable("getDynamic4/<anyString>/");
    }
}

