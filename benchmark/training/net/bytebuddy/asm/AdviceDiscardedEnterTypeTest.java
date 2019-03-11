package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;


@RunWith(Parameterized.class)
public class AdviceDiscardedEnterTypeTest {
    private static final String FOO = "foo";

    private final Class<?> advice;

    public AdviceDiscardedEnterTypeTest(Class<?> advice) {
        this.advice = advice;
    }

    @Test
    public void testEnterTypeDiscarding() throws Exception {
        Class<? extends AdviceDiscardedEnterTypeTest.Sample> type = new ByteBuddy().redefine(AdviceDiscardedEnterTypeTest.Sample.class).visit(Advice.to(advice).on(ElementMatchers.named(AdviceDiscardedEnterTypeTest.FOO))).make().load(advice.getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceDiscardedEnterTypeTest.FOO).invoke(null), CoreMatchers.nullValue(Object.class));
    }

    public static class Sample {
        public static void foo() {
            /* do nothing */
        }
    }

    public static class BooleanAdvice {
        @Advice.OnMethodEnter
        public static boolean enter() {
            return false;
        }
    }

    public static class ByteAdvice {
        @Advice.OnMethodEnter
        public static byte enter() {
            return 0;
        }
    }

    public static class ShortAdvice {
        @Advice.OnMethodEnter
        public static short enter() {
            return 0;
        }
    }

    public static class CharacterAdvice {
        @Advice.OnMethodEnter
        public static char enter() {
            return 0;
        }
    }

    public static class IntegerAdvice {
        @Advice.OnMethodEnter
        public static int enter() {
            return 0;
        }
    }

    public static class LongAdvice {
        @Advice.OnMethodEnter
        public static long enter() {
            return 0L;
        }
    }

    public static class FloatAdvice {
        @Advice.OnMethodEnter
        public static float enter() {
            return 0.0F;
        }
    }

    public static class DoubleAdvice {
        @Advice.OnMethodEnter
        public static double enter() {
            return 0.0;
        }
    }

    public static class ReferenceAdvice {
        @Advice.OnMethodEnter
        public static Object enter() {
            return null;
        }
    }

    public static class DelegatingBooleanAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static boolean enter() {
            return false;
        }
    }

    public static class DelegatingByteAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static byte enter() {
            return 0;
        }
    }

    public static class DelegatingShortAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static short enter() {
            return 0;
        }
    }

    public static class DelegatingCharacterAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static char enter() {
            return 0;
        }
    }

    public static class DelegatingIntegerAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static int enter() {
            return 0;
        }
    }

    public static class DelegatingLongAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static long enter() {
            return 0L;
        }
    }

    public static class DelegatingFloatAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static float enter() {
            return 0.0F;
        }
    }

    public static class DelegatingDoubleAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static double enter() {
            return 0.0;
        }
    }

    public static class DelegatingReferenceAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static Object enter() {
            return null;
        }
    }
}

