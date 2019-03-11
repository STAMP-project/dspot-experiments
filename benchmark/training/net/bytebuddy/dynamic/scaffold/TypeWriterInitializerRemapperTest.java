package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.Implementation.Context.Disabled.Factory.INSTANCE;


@RunWith(Parameterized.class)
public class TypeWriterInitializerRemapperTest {
    private final Class<?> type;

    public TypeWriterInitializerRemapperTest(Class<?> type) {
        this.type = type;
    }

    @Test
    public void testNoInitializerWithEnabledContext() throws Exception {
        Class<?> type = new ByteBuddy().redefine(this.type).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Class.forName(type.getName(), true, type.getClassLoader());
    }

    @Test
    public void testNoInitializerWithDisabledContext() throws Exception {
        Class<?> type = new ByteBuddy().with(INSTANCE).redefine(this.type).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Class.forName(type.getName(), true, type.getClassLoader());
    }

    @Test
    public void testInitializerWithEnabledContext() throws Exception {
        Class<?> type = new ByteBuddy().redefine(this.type).invokable(ElementMatchers.isTypeInitializer()).intercept(StubMethod.INSTANCE).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Class.forName(type.getName(), true, type.getClassLoader());
    }

    @Test
    public void testInitializerWithDisabledContext() throws Exception {
        Class<?> type = new ByteBuddy().with(INSTANCE).redefine(this.type).invokable(ElementMatchers.isTypeInitializer()).intercept(StubMethod.INSTANCE).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Class.forName(type.getName(), true, type.getClassLoader());
    }

    /* empty */
    public static class NoInitializer {}

    public static class BranchingInitializer {
        static {
            int ignored = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                long v3 = 3L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if (((v1 + v2) + v3) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            long v7 = 7L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v4 + v5) + v6) + v7) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v8 = 8L;
            } catch (Exception exception) {
                long v9 = 9L;
            }
        }
    }
}

