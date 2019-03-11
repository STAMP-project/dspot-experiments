package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class AdviceSkipOnTest {
    private static final String FOO = "foo";

    @Test
    public void testInstanceOfSkip() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceSkipOnTest.InstanceOfSkip.class).visit(Advice.to(AdviceSkipOnTest.InstanceOfSkip.class).on(ElementMatchers.named(AdviceSkipOnTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceSkipOnTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testInstanceOfNoSkip() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceSkipOnTest.InstanceOfNoSkip.class).visit(Advice.to(AdviceSkipOnTest.InstanceOfNoSkip.class).on(ElementMatchers.named(AdviceSkipOnTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceSkipOnTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceSkipOnTest.FOO))));
    }

    @Test(expected = IllegalStateException.class)
    public void testInstanceOfSkipOnConstructor() throws Exception {
        new ByteBuddy().redefine(AdviceSkipOnTest.Sample.class).visit(Advice.to(AdviceSkipOnTest.InstanceOfSkip.class).on(ElementMatchers.isConstructor())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testValueSkipOnConstructor() throws Exception {
        new ByteBuddy().redefine(AdviceSkipOnTest.Sample.class).visit(Advice.to(AdviceSkipOnTest.DefaultValueIllegalPrimitiveSkip.class).on(ElementMatchers.isConstructor())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testInstanceOfPrimitiveSkip() throws Exception {
        Advice.to(AdviceSkipOnTest.InstanceOfIllegalPrimitiveSkip.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testInstanceOfPrimitiveInstanceOfSkip() throws Exception {
        Advice.to(AdviceSkipOnTest.InstanceOfIllegalPrimitiveInstanceOfSkip.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testDefaultValuePrimitiveSkip() throws Exception {
        Advice.to(AdviceSkipOnTest.DefaultValueIllegalPrimitiveSkip.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonDefaultValuePrimitiveSkip() throws Exception {
        Advice.to(AdviceSkipOnTest.NonDefaultValueIllegalPrimitiveSkip.class);
    }

    public static class Sample {
        public String foo() {
            return AdviceSkipOnTest.FOO;
        }
    }

    public static class InstanceOfSkip {
        public String foo() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter(skipOn = AdviceSkipOnTest.InstanceOfSkip.class)
        private static Object enter() {
            return new AdviceSkipOnTest.InstanceOfSkip();
        }
    }

    public static class InstanceOfNoSkip {
        public String foo() {
            return AdviceSkipOnTest.FOO;
        }

        @Advice.OnMethodEnter(skipOn = AdviceSkipOnTest.InstanceOfSkip.class)
        private static Object enter() {
            return null;
        }
    }

    public static class InstanceOfIllegalPrimitiveSkip {
        @Advice.OnMethodEnter(skipOn = AdviceSkipOnTest.InstanceOfSkip.class)
        private static void enter() {
            /* empty */
        }
    }

    public static class DefaultValueIllegalPrimitiveSkip {
        @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
        private static void enter() {
            /* empty */
        }
    }

    public static class NonDefaultValueIllegalPrimitiveSkip {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        private static void enter() {
            /* empty */
        }
    }

    public static class InstanceOfIllegalPrimitiveInstanceOfSkip {
        @Advice.OnMethodEnter(skipOn = int.class)
        private static void enter() {
            /* empty */
        }
    }
}

