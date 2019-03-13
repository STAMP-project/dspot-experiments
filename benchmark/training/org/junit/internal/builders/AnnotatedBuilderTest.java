package org.junit.internal.builders;


import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.RunnerSpy;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerBuilderStub;


public class AnnotatedBuilderTest {
    private AnnotatedBuilder builder = new AnnotatedBuilder(new RunnerBuilderStub());

    @Test
    public void topLevelTestClassWithoutAnnotation_isRunWithDefaultRunner() throws Exception {
        Runner runner = builder.runnerForClass(Object.class);
        Assert.assertThat(runner, Is.is(IsNull.nullValue()));
    }

    @Test
    public void topLevelTestClassWithAnnotation_isRunWithAnnotatedRunner() throws Exception {
        Runner runner = builder.runnerForClass(AnnotatedBuilderTest.OuterClass.class);
        Assert.assertThat(runner, Is.is(IsInstanceOf.instanceOf(RunnerSpy.class)));
        RunnerSpy runnerSpy = ((RunnerSpy) (runner));
        Assert.assertThat(runnerSpy.getInvokedTestClass(), Is.is(((Object) (AnnotatedBuilderTest.OuterClass.class))));
    }

    @Test
    public void memberClassInsideAnnotatedTopLevelClass_isRunWithTopLevelRunner() throws Exception {
        Runner runner = builder.runnerForClass(AnnotatedBuilderTest.OuterClass.InnerClassWithoutOwnRunWith.class);
        Assert.assertThat(runner, Is.is(IsInstanceOf.instanceOf(RunnerSpy.class)));
        RunnerSpy runnerSpy = ((RunnerSpy) (runner));
        Assert.assertThat(runnerSpy.getInvokedTestClass(), Is.is(((Object) (AnnotatedBuilderTest.OuterClass.InnerClassWithoutOwnRunWith.class))));
    }

    @Test
    public void memberClassDeepInsideAnnotatedTopLevelClass_isRunWithTopLevelRunner() throws Exception {
        Runner runner = builder.runnerForClass(AnnotatedBuilderTest.OuterClass.InnerClassWithoutOwnRunWith.MostInnerClass.class);
        Assert.assertThat(runner, Is.is(IsInstanceOf.instanceOf(RunnerSpy.class)));
        RunnerSpy runnerSpy = ((RunnerSpy) (runner));
        Assert.assertThat(runnerSpy.getInvokedTestClass(), Is.is(((Object) (AnnotatedBuilderTest.OuterClass.InnerClassWithoutOwnRunWith.MostInnerClass.class))));
    }

    @Test
    public void annotatedMemberClassInsideAnnotatedTopLevelClass_isRunWithOwnRunner() throws Exception {
        Runner runner = builder.runnerForClass(AnnotatedBuilderTest.OuterClass.InnerClassWithOwnRunWith.class);
        Assert.assertThat(runner, Is.is(IsInstanceOf.instanceOf(AnnotatedBuilderTest.InnerRunner.class)));
        RunnerSpy runnerSpy = ((RunnerSpy) (runner));
        Assert.assertThat(runnerSpy.getInvokedTestClass(), Is.is(((Object) (AnnotatedBuilderTest.OuterClass.InnerClassWithOwnRunWith.class))));
    }

    @Test
    public void memberClassDeepInsideAnnotatedMemberClass_isRunWithParentMemberClassRunner() throws Exception {
        Runner runner = builder.runnerForClass(AnnotatedBuilderTest.OuterClass.InnerClassWithOwnRunWith.MostInnerClass.class);
        Assert.assertThat(runner, Is.is(IsInstanceOf.instanceOf(AnnotatedBuilderTest.InnerRunner.class)));
        RunnerSpy runnerSpy = ((RunnerSpy) (runner));
        Assert.assertThat(runnerSpy.getInvokedTestClass(), Is.is(((Object) (AnnotatedBuilderTest.OuterClass.InnerClassWithOwnRunWith.MostInnerClass.class))));
    }

    @RunWith(RunnerSpy.class)
    public static class OuterClass {
        public class InnerClassWithoutOwnRunWith {
            @Test
            public void test() {
            }

            public class MostInnerClass {
                @Test
                public void test() {
                }
            }
        }

        @RunWith(AnnotatedBuilderTest.InnerRunner.class)
        public class InnerClassWithOwnRunWith {
            @Test
            public void test() {
            }

            public class MostInnerClass {
                @Test
                public void test() {
                }
            }
        }
    }

    public static class InnerRunner extends RunnerSpy {
        public InnerRunner(Class<?> testClass) {
            super(testClass);
        }

        public InnerRunner(Class<?> testClass, RunnerBuilder runnerBuilder) {
            super(testClass, runnerBuilder);
        }
    }
}

