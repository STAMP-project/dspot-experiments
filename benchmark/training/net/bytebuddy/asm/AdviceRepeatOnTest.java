package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class AdviceRepeatOnTest {
    private static final String FOO = "foo";

    @Test
    public void testInstanceOfRepeat() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceRepeatOnTest.InstanceOfRepeat.class).visit(Advice.to(AdviceRepeatOnTest.InstanceOfRepeat.class).on(ElementMatchers.named(AdviceRepeatOnTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceRepeatOnTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void testInstanceOfNoRepeat() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceRepeatOnTest.InstanceOfNoRepeat.class).visit(Advice.to(AdviceRepeatOnTest.InstanceOfNoRepeat.class).on(ElementMatchers.named(AdviceRepeatOnTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceRepeatOnTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (1))));
    }

    @Test(expected = IllegalStateException.class)
    public void testInstanceOfRepeatOnConstructor() throws Exception {
        new ByteBuddy().redefine(AdviceRepeatOnTest.Sample.class).visit(Advice.to(AdviceRepeatOnTest.InstanceOfRepeat.class).on(ElementMatchers.isConstructor())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testValueRepeatOnConstructor() throws Exception {
        new ByteBuddy().redefine(AdviceRepeatOnTest.Sample.class).visit(Advice.to(AdviceRepeatOnTest.DefaultValueIllegalPrimitiveRepeat.class).on(ElementMatchers.isConstructor())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testInstanceOfPrimitiveRepeat() throws Exception {
        Advice.to(AdviceRepeatOnTest.InstanceOfIllegalPrimitiveRepeat.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testInstanceOfPrimitiveInstanceOfRepeat() throws Exception {
        Advice.to(AdviceRepeatOnTest.InstanceOfIllegalPrimitiveInstanceOfRepeat.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testDefaultValuePrimitiveRepeat() throws Exception {
        Advice.to(AdviceRepeatOnTest.DefaultValueIllegalPrimitiveRepeat.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonDefaultValuePrimitiveRepeat() throws Exception {
        Advice.to(AdviceRepeatOnTest.NonDefaultValueIllegalPrimitiveRepeat.class);
    }

    public static class Sample {
        public String foo() {
            return AdviceRepeatOnTest.FOO;
        }
    }

    public static class InstanceOfRepeat {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = AdviceRepeatOnTest.InstanceOfRepeat.class)
        private static Object enter(@Advice.Exit
        Object exit) {
            return exit == null ? new AdviceRepeatOnTest.InstanceOfRepeat() : null;
        }
    }

    public static class InstanceOfNoRepeat {
        private int count;

        public int foo() {
            return ++(count);
        }

        @Advice.OnMethodExit(repeatOn = AdviceRepeatOnTest.InstanceOfRepeat.class)
        private static Object exit() {
            return null;
        }
    }

    public static class InstanceOfIllegalPrimitiveRepeat {
        @Advice.OnMethodExit(repeatOn = AdviceRepeatOnTest.InstanceOfRepeat.class)
        private static void exit() {
            /* empty */
        }
    }

    public static class DefaultValueIllegalPrimitiveRepeat {
        @Advice.OnMethodExit(repeatOn = Advice.OnDefaultValue.class)
        private static void exit() {
            /* empty */
        }
    }

    public static class NonDefaultValueIllegalPrimitiveRepeat {
        @Advice.OnMethodExit(repeatOn = Advice.OnNonDefaultValue.class)
        private static void exit() {
            /* empty */
        }
    }

    public static class InstanceOfIllegalPrimitiveInstanceOfRepeat {
        @Advice.OnMethodExit(repeatOn = int.class)
        private static void exit() {
            /* empty */
        }
    }
}

