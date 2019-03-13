package org.stagemonitor.tracing.ejb;


import javax.ejb.Remote;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.tracing.SpanCapturingReporter;
import org.stagemonitor.tracing.SpanContextInformation;


public class RemoteEjbMonitorTransformerTest {
    private RemoteEjbMonitorTransformerTest.RemoteInterface remote = new RemoteEjbMonitorTransformerTest.RemoteInterfaceImpl();

    private RemoteEjbMonitorTransformerTest.RemoteInterfaceWithRemoteAnnotation remoteAlt = new RemoteEjbMonitorTransformerTest.RemoteInterfaceWithRemoteAnnotationImpl();

    private SpanCapturingReporter spanCapturingReporter;

    @Test
    public void testMonitorRemoteCalls() throws Exception {
        remote.foo();
        final SpanContextInformation spanContext = spanCapturingReporter.get();
        Assert.assertNotNull(spanContext);
        Assert.assertEquals("RemoteEjbMonitorTransformerTest$RemoteInterfaceImpl#foo", spanContext.getOperationName());
        Assert.assertFalse(spanContext.getCallTree().toString(), spanContext.getCallTree().getChildren().isEmpty());
        final String signature = spanContext.getCallTree().getChildren().get(0).getSignature();
        Assert.assertTrue(signature, signature.contains("org.stagemonitor.tracing.ejb.RemoteEjbMonitorTransformerTest$RemoteInterfaceImpl"));
    }

    @Test
    public void testMonitorRemoteCallsAlternateHierarchy() throws Exception {
        remoteAlt.bar();
        final SpanContextInformation spanContext = spanCapturingReporter.get();
        Assert.assertNotNull(spanContext);
        Assert.assertEquals("RemoteEjbMonitorTransformerTest$RemoteInterfaceWithRemoteAnnotationImpl#bar", spanContext.getOperationName());
        Assert.assertFalse(spanContext.getCallTree().toString(), spanContext.getCallTree().getChildren().isEmpty());
        final String signature = spanContext.getCallTree().getChildren().get(0).getSignature();
        Assert.assertTrue(signature, signature.contains("org.stagemonitor.tracing.ejb.RemoteEjbMonitorTransformerTest$RemoteInterfaceWithRemoteAnnotationImpl"));
    }

    @Test
    public void testMonitorRemoteCallsSuperInterface() throws Exception {
        remoteAlt.foo();
        final SpanContextInformation spanContext = spanCapturingReporter.get();
        Assert.assertNotNull(spanContext);
        Assert.assertEquals("RemoteEjbMonitorTransformerTest$RemoteInterfaceWithRemoteAnnotationImpl#foo", spanContext.getOperationName());
        Assert.assertFalse(spanContext.getCallTree().toString(), spanContext.getCallTree().getChildren().isEmpty());
        final String signature = spanContext.getCallTree().getChildren().get(0).getSignature();
        Assert.assertTrue(signature, signature.contains("org.stagemonitor.tracing.ejb.RemoteEjbMonitorTransformerTest$RemoteInterfaceWithRemoteAnnotationImpl"));
    }

    @Test
    public void testExcludeGeneratedClasses() throws Exception {
        // classes which contain $$ are usually generated classes
        new RemoteEjbMonitorTransformerTest.$$ExcludeGeneratedClasses().bar();
        Assert.assertNull(spanCapturingReporter.get());
    }

    @Test
    public void testDontMonitorToString() throws Exception {
        remote.toString();
        Assert.assertNull(spanCapturingReporter.get());
    }

    @Test
    public void testDontMonitorNonRemoteEjb() throws Exception {
        new RemoteEjbMonitorTransformerTest.NoRemoteEJB().foo();
        Assert.assertNull(spanCapturingReporter.get());
    }

    private interface SuperInterface {
        void foo();
    }

    interface RemoteInterface extends RemoteEjbMonitorTransformerTest.SuperInterface {}

    @Remote(RemoteEjbMonitorTransformerTest.RemoteInterface.class)
    public class RemoteInterfaceImpl implements RemoteEjbMonitorTransformerTest.RemoteInterface {
        @Override
        public void foo() {
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    @Remote
    private interface RemoteInterfaceWithRemoteAnnotation extends RemoteEjbMonitorTransformerTest.RemoteInterface {
        void bar();
    }

    public class RemoteInterfaceWithRemoteAnnotationImpl implements RemoteEjbMonitorTransformerTest.RemoteInterfaceWithRemoteAnnotation {
        @Override
        public void bar() {
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public void foo() {
        }
    }

    public class NoRemoteEJB implements RemoteEjbMonitorTransformerTest.SuperInterface {
        @Override
        public void foo() {
        }
    }

    public class $$ExcludeGeneratedClasses implements RemoteEjbMonitorTransformerTest.RemoteInterfaceWithRemoteAnnotation {
        @Override
        public void bar() {
        }

        @Override
        public void foo() {
        }
    }
}

