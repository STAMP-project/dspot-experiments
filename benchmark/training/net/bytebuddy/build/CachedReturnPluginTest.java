package net.bytebuddy.build;


import CachedReturnPlugin.Enhance;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import junit.framework.TestCase;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.of;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class CachedReturnPluginTest {
    private static final String FOO = "foo";

    private final Class<?> type;

    private final Object value;

    private final Class<?> adviceArgument;

    public CachedReturnPluginTest(Class<?> type, Object value, Class<?> adviceArgument) {
        this.type = type;
        this.value = value;
        this.adviceArgument = adviceArgument;
    }

    private Plugin plugin;

    @Test
    public void testMatches() throws Exception {
        MatcherAssert.assertThat(plugin.matches(net.bytebuddy.description.type.TypeDescription.ForLoadedType.of(type)), CoreMatchers.is(true));
    }

    @Test
    public void testCachedValue() throws Exception {
        Class<?> transformed = plugin.apply(new ByteBuddy().redefine(type), net.bytebuddy.description.type.TypeDescription.ForLoadedType.of(type), of(type.getClassLoader())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(transformed.getDeclaredFields().length, CoreMatchers.is(2));
        Object instance = transformed.getConstructor().newInstance();
        MatcherAssert.assertThat(transformed.getMethod(CachedReturnPluginTest.FOO).invoke(instance), CoreMatchers.is(value));
        MatcherAssert.assertThat(transformed.getMethod(CachedReturnPluginTest.FOO).invoke(instance), CoreMatchers.is(value));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCannotConstructAdvice() throws Exception {
        Constructor<?> constructor = Class.forName((((CachedReturnPlugin.class.getName()) + "$") + (adviceArgument.getSimpleName())), true, CachedReturnPlugin.class.getClassLoader()).getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            TestCase.fail();
        } catch (InvocationTargetException e) {
            throw ((Exception) (e.getCause()));
        }
    }

    public static class BooleanSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance
        public boolean foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return true;
        }
    }

    public static class ByteSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance
        public byte foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return 42;
        }
    }

    public static class ShortSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance
        public short foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return 42;
        }
    }

    public static class CharacterSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance
        public char foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return 42;
        }
    }

    public static class IntegerSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance
        public int foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return 42;
        }
    }

    public static class LongSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance
        public long foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return 42;
        }
    }

    public static class FloatSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance
        public float foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return 42.0F;
        }
    }

    public static class DoubleSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance
        public double foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return 42.0;
        }
    }

    public static class ReferenceSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance
        public String foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return CachedReturnPluginTest.FOO;
        }
    }

    public static class ReferenceStaticSample {
        private static boolean executed;

        @CachedReturnPlugin.Enhance
        public static String foo() {
            if (CachedReturnPluginTest.ReferenceStaticSample.executed) {
                throw new AssertionError();
            }
            CachedReturnPluginTest.ReferenceStaticSample.executed = true;
            return CachedReturnPluginTest.FOO;
        }
    }

    public static class ReferenceNamedSample {
        private boolean executed;

        @CachedReturnPlugin.Enhance(CachedReturnPluginTest.FOO)
        public String foo() {
            if (executed) {
                throw new AssertionError();
            }
            executed = true;
            return CachedReturnPluginTest.FOO;
        }
    }
}

