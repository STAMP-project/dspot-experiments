package org.stagemonitor.tracing.freemarker;


import org.junit.Test;
import org.stagemonitor.tracing.profiler.CallStackElement;
import org.stagemonitor.tracing.profiler.Profiler;


public class FreemarkerProfilingTransformerTest {
    @Test
    public void testFreemarkerProfiling() throws Exception {
        final CallStackElement callTree = Profiler.activateProfiling("testFreemarkerProfiling");
        final String renderedTemplate = processTemplate("test.ftl", "${templateModel.foo}", new FreemarkerProfilingTransformerTest.TemplateModel());
        Profiler.stop();
        Profiler.deactivateProfiling();
        assertThat(renderedTemplate).isEqualTo("foo");
        System.out.println(callTree);
        assertThat(callTree.getChildren()).hasSize(1);
        final CallStackElement freemarkerNode = callTree.getChildren().get(0);
        assertThat(freemarkerNode.getSignature()).isEqualTo("test.ftl:1#templateModel.foo");
        assertThat(freemarkerNode.getChildren()).hasSize(1);
        final CallStackElement templateModelNode = freemarkerNode.getChildren().get(0);
        assertThat(templateModelNode.getSignature()).isEqualTo("String org.stagemonitor.tracing.freemarker.FreemarkerProfilingTransformerTest$TemplateModel.getFoo()");
    }

    @Test
    public void testFreemarkerProfilingMethodCall() throws Exception {
        final CallStackElement callTree = Profiler.activateProfiling("testFreemarkerProfilingMethodCall");
        final String renderedTemplate = processTemplate("test.ftl", "${templateModel.getFoo()}", new FreemarkerProfilingTransformerTest.TemplateModel());
        Profiler.stop();
        Profiler.deactivateProfiling();
        assertThat(renderedTemplate).isEqualTo("foo");
        System.out.println(callTree);
        assertThat(callTree.getChildren()).hasSize(1);
        final CallStackElement freemarkerNode = callTree.getChildren().get(0);
        assertThat(freemarkerNode.getSignature()).isEqualTo("test.ftl:1#templateModel.getFoo()");
        assertThat(freemarkerNode.getChildren()).hasSize(1);
        final CallStackElement templateModelNode = freemarkerNode.getChildren().get(0);
        assertThat(templateModelNode.getSignature()).isEqualTo("String org.stagemonitor.tracing.freemarker.FreemarkerProfilingTransformerTest$TemplateModel.getFoo()");
    }

    @Test
    public void testFreemarkerWorksIfNotProfiling() throws Exception {
        final String renderedTemplate = processTemplate("test.ftl", "${templateModel.getFoo()}", new FreemarkerProfilingTransformerTest.TemplateModel());
        assertThat(renderedTemplate).isEqualTo("foo");
    }

    @Test
    public void testShortSignature() {
        final String signature = "foobar.ftl:123#foo.getBar('123').baz";
        // don't try to shorten ftl signatures
        assertThat(CallStackElement.createRoot(signature).getShortSignature()).isNull();
    }

    public static class TemplateModel {
        public String getFoo() {
            Profiler.start("String org.stagemonitor.tracing.freemarker.FreemarkerProfilingTransformerTest$TemplateModel.getFoo()");
            try {
                return "foo";
            } finally {
                Profiler.stop();
            }
        }
    }
}

