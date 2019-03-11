package jenkins.security.stapler;


import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.TestExtension;

import static TypedFilter.PROHIBIT_STATIC_ACCESS;
import static TypedFilter.SKIP_TYPE_CHECK;


@Issue("SECURITY-595")
@For({ StaplerDispatchable.class, StaplerNotDispatchable.class, TypedFilter.class })
public class StaplerRoutableFieldTest extends StaplerAbstractTest {
    @TestExtension
    public static class TestRootAction extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        @Override
        public String getUrlName() {
            return "test";
        }

        public StaplerAbstractTest.Renderable renderableNotAnnotated = new StaplerAbstractTest.Renderable();

        public StaplerAbstractTest.ParentRenderable parentRenderableNotAnnotated = new StaplerAbstractTest.ParentRenderable();

        public Object objectNotAnnotated = new StaplerAbstractTest.Renderable();

        @StaplerDispatchable
        public StaplerAbstractTest.Renderable renderableAnnotatedOk = new StaplerAbstractTest.Renderable();

        @StaplerDispatchable
        public StaplerAbstractTest.ParentRenderable parentRenderableAnnotatedOk = new StaplerAbstractTest.ParentRenderable();

        @StaplerDispatchable
        public Object objectAnnotatedOk = new StaplerAbstractTest.Renderable();

        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable renderableAnnotatedKo = new StaplerAbstractTest.Renderable();

        @StaplerNotDispatchable
        public Object objectAnnotatedKo = new StaplerAbstractTest.Renderable();

        @StaplerDispatchable
        @StaplerNotDispatchable
        public StaplerAbstractTest.Renderable renderableDoubleAnnotated = new StaplerAbstractTest.Renderable();

        @StaplerDispatchable
        @StaplerNotDispatchable
        public Object objectDoubleAnnotated = new StaplerAbstractTest.Renderable();

        public static StaplerAbstractTest.Renderable staticRenderableNotAnnotated = new StaplerAbstractTest.Renderable();

        public static Object staticObjectNotAnnotated = new StaplerAbstractTest.Renderable();

        @StaplerDispatchable
        public static StaplerAbstractTest.Renderable staticRenderableAnnotatedOk = new StaplerAbstractTest.Renderable();

        @StaplerDispatchable
        public static Object staticObjectAnnotatedOk = new StaplerAbstractTest.Renderable();
    }

    @Test
    public void testFieldNotAnnotated() throws Exception {
        assertReachable("test/renderableNotAnnotated/");
        assertReachable("test/renderableNotAnnotated/valid/");
        assertNotReachable("test/parentRenderableNotAnnotated/");
        assertNotReachable("test/parentRenderableNotAnnotated/renderable/");
        assertNotReachable("test/parentRenderableNotAnnotated/renderable/valid/");
        assertNotReachable("test/objectNotAnnotated/");
        assertNotReachable("test/objectNotAnnotated/valid/");
    }

    @Test
    public void testFieldNotAnnotated_escapeHatch() throws Exception {
        boolean currentValue = SKIP_TYPE_CHECK;
        try {
            SKIP_TYPE_CHECK = true;
            // to apply the new configuration
            webApp.clearMetaClassCache();
            assertReachable("test/renderableNotAnnotated/");
            assertReachable("test/renderableNotAnnotated/valid/");
            assertNotReachable("test/parentRenderableNotAnnotated/");
            assertReachable("test/parentRenderableNotAnnotated/renderable/");
            assertReachable("test/parentRenderableNotAnnotated/renderable/valid/");
        } finally {
            SKIP_TYPE_CHECK = currentValue;
            // to reset the configuration
            webApp.clearMetaClassCache();
        }
    }

    @Test
    public void testFieldAnnotatedOk() throws Exception {
        assertReachable("test/renderableAnnotatedOk/");
        assertReachable("test/renderableAnnotatedOk/valid/");
        assertReachable("test/objectAnnotatedOk/");
        assertReachable("test/objectAnnotatedOk/valid/");
    }

    @Test
    public void testFieldAnnotatedKo() throws Exception {
        assertNotReachable("test/renderableAnnotatedKo/");
        assertNotReachable("test/renderableAnnotatedKo/valid/");
        assertNotReachable("test/objectAnnotatedKo/");
        assertNotReachable("test/objectAnnotatedKo/valid/");
    }

    @Test
    public void testFieldDoubleAnnotated() throws Exception {
        assertNotReachable("test/renderableDoubleAnnotated/");
        assertNotReachable("test/renderableDoubleAnnotated/valid/");
        assertNotReachable("test/objectDoubleAnnotated/");
        assertNotReachable("test/objectDoubleAnnotated/valid/");
    }

    @Test
    public void testStaticFieldNotAnnotated() throws Exception {
        assertNotReachable("test/staticRenderableNotAnnotated/");
        assertNotReachable("test/staticRenderableNotAnnotated/valid/");
        assertNotReachable("test/staticObjectNotAnnotated/");
        assertNotReachable("test/staticObjectNotAnnotated/valid/");
    }

    @Test
    public void testStaticFieldNotAnnotated_escapeHatch() throws Exception {
        boolean currentValue = PROHIBIT_STATIC_ACCESS;
        try {
            PROHIBIT_STATIC_ACCESS = false;
            // to apply the new configuration
            webApp.clearMetaClassCache();
            assertReachable("test/staticRenderableNotAnnotated/");
            assertReachable("test/staticRenderableNotAnnotated/valid/");
            assertNotReachable("test/staticObjectNotAnnotated/");
            assertNotReachable("test/staticObjectNotAnnotated/valid/");
        } finally {
            PROHIBIT_STATIC_ACCESS = currentValue;
            // to reset the configuration
            webApp.clearMetaClassCache();
        }
    }

    @Test
    public void testStaticFieldAnnotatedOk() throws Exception {
        assertReachable("test/staticRenderableAnnotatedOk/");
        assertReachable("test/staticRenderableAnnotatedOk/valid/");
        assertReachable("test/staticObjectAnnotatedOk/");
        assertReachable("test/staticObjectAnnotatedOk/valid/");
    }
}

