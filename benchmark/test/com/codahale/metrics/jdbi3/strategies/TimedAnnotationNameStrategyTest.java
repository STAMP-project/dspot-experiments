package com.codahale.metrics.jdbi3.strategies;


import com.codahale.metrics.annotation.Timed;
import org.jdbi.v3.core.extension.ExtensionMethod;
import org.junit.Test;
import org.mockito.Mockito;


public class TimedAnnotationNameStrategyTest extends AbstractStrategyTest {
    private TimedAnnotationNameStrategy timedAnnotationNameStrategy = new TimedAnnotationNameStrategy();

    public interface Foo {
        @Timed
        void update();

        @Timed(name = "custom-update")
        void customUpdate();

        @Timed(name = "absolute-update", absolute = true)
        void absoluteUpdate();
    }

    @Timed
    public interface Bar {
        void update();
    }

    @Timed(name = "custom-bar")
    public interface CustomBar {
        @Timed(name = "find-by-id")
        int find(String name);
    }

    public interface Dummy {
        void show();
    }

    @Test
    public void testAnnotationOnMethod() throws Exception {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(TimedAnnotationNameStrategyTest.Foo.class, TimedAnnotationNameStrategyTest.Foo.class.getMethod("update")));
        assertThat(timedAnnotationNameStrategy.getStatementName(ctx)).isEqualTo("com.codahale.metrics.jdbi3.strategies.TimedAnnotationNameStrategyTest$Foo.update");
    }

    @Test
    public void testAnnotationOnMethodWithCustomName() throws Exception {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(TimedAnnotationNameStrategyTest.Foo.class, TimedAnnotationNameStrategyTest.Foo.class.getMethod("customUpdate")));
        assertThat(timedAnnotationNameStrategy.getStatementName(ctx)).isEqualTo("com.codahale.metrics.jdbi3.strategies.TimedAnnotationNameStrategyTest$Foo.custom-update");
    }

    @Test
    public void testAnnotationOnMethodWithCustomAbsoluteName() throws Exception {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(TimedAnnotationNameStrategyTest.Foo.class, TimedAnnotationNameStrategyTest.Foo.class.getMethod("absoluteUpdate")));
        assertThat(timedAnnotationNameStrategy.getStatementName(ctx)).isEqualTo("absolute-update");
    }

    @Test
    public void testAnnotationOnClass() throws Exception {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(TimedAnnotationNameStrategyTest.Bar.class, TimedAnnotationNameStrategyTest.Bar.class.getMethod("update")));
        assertThat(timedAnnotationNameStrategy.getStatementName(ctx)).isEqualTo("com.codahale.metrics.jdbi3.strategies.TimedAnnotationNameStrategyTest$Bar.update");
    }

    @Test
    public void testAnnotationOnMethodAndClassWithCustomNames() throws Exception {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(TimedAnnotationNameStrategyTest.CustomBar.class, TimedAnnotationNameStrategyTest.CustomBar.class.getMethod("find", String.class)));
        assertThat(timedAnnotationNameStrategy.getStatementName(ctx)).isEqualTo("custom-bar.find-by-id");
    }

    @Test
    public void testNoAnnotations() throws Exception {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(TimedAnnotationNameStrategyTest.Dummy.class, TimedAnnotationNameStrategyTest.Dummy.class.getMethod("show")));
        assertThat(timedAnnotationNameStrategy.getStatementName(ctx)).isNull();
    }

    @Test
    public void testNoMethod() {
        assertThat(timedAnnotationNameStrategy.getStatementName(ctx)).isNull();
    }
}

