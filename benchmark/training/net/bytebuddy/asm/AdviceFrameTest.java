package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class AdviceFrameTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String COUNT = "count";

    private final Class<?> advice;

    private final int count;

    public AdviceFrameTest(Class<?> advice, int count) {
        this.advice = advice;
        this.count = count;
    }

    @Test
    public void testFrameAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceFrameTest.FrameSample.class).visit(Advice.to(advice).on(ElementMatchers.named(AdviceFrameTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceFrameTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceFrameTest.FOO), CoreMatchers.is(((Object) (AdviceFrameTest.FOO))));
        MatcherAssert.assertThat(type.getField(AdviceFrameTest.COUNT).getInt(null), CoreMatchers.is(((Object) (count))));
    }

    @Test
    public void testFrameAdviceStaticMethod() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceFrameTest.FrameSample.class).visit(Advice.to(advice).on(ElementMatchers.named(AdviceFrameTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceFrameTest.BAR, String.class).invoke(null, AdviceFrameTest.FOO), CoreMatchers.is(((Object) (AdviceFrameTest.FOO))));
        MatcherAssert.assertThat(type.getField(AdviceFrameTest.COUNT).getInt(null), CoreMatchers.is(((Object) (count))));
    }

    @Test
    public void testFrameAdviceExpanded() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceFrameTest.FrameSample.class).visit(Advice.to(advice).on(ElementMatchers.named(AdviceFrameTest.FOO)).readerFlags(ClassReader.EXPAND_FRAMES)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceFrameTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceFrameTest.FOO), CoreMatchers.is(((Object) (AdviceFrameTest.FOO))));
        MatcherAssert.assertThat(type.getField(AdviceFrameTest.COUNT).getInt(null), CoreMatchers.is(((Object) (count))));
    }

    @Test
    public void testFrameAdviceStaticMethodExpanded() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceFrameTest.FrameSample.class).visit(Advice.to(advice).on(ElementMatchers.named(AdviceFrameTest.BAR)).readerFlags(ClassReader.EXPAND_FRAMES)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceFrameTest.BAR, String.class).invoke(null, AdviceFrameTest.FOO), CoreMatchers.is(((Object) (AdviceFrameTest.FOO))));
        MatcherAssert.assertThat(type.getField(AdviceFrameTest.COUNT).getInt(null), CoreMatchers.is(((Object) (count))));
    }

    @Test
    public void testFrameAdviceComputedMaxima() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceFrameTest.FrameSample.class).visit(Advice.to(advice).on(ElementMatchers.named(AdviceFrameTest.FOO)).writerFlags(ClassWriter.COMPUTE_MAXS)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceFrameTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceFrameTest.FOO), CoreMatchers.is(((Object) (AdviceFrameTest.FOO))));
        MatcherAssert.assertThat(type.getField(AdviceFrameTest.COUNT).getInt(null), CoreMatchers.is(((Object) (count))));
    }

    @Test
    public void testFrameAdviceStaticMethodComputedMaxima() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceFrameTest.FrameSample.class).visit(Advice.to(advice).on(ElementMatchers.named(AdviceFrameTest.BAR)).writerFlags(ClassWriter.COMPUTE_MAXS)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceFrameTest.BAR, String.class).invoke(null, AdviceFrameTest.FOO), CoreMatchers.is(((Object) (AdviceFrameTest.FOO))));
        MatcherAssert.assertThat(type.getField(AdviceFrameTest.COUNT).getInt(null), CoreMatchers.is(((Object) (count))));
    }

    @Test
    public void testFrameAdviceComputedFrames() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceFrameTest.FrameSample.class).visit(Advice.to(advice).on(ElementMatchers.named(AdviceFrameTest.FOO)).writerFlags(ClassWriter.COMPUTE_FRAMES)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceFrameTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceFrameTest.FOO), CoreMatchers.is(((Object) (AdviceFrameTest.FOO))));
        MatcherAssert.assertThat(type.getField(AdviceFrameTest.COUNT).getInt(null), CoreMatchers.is(((Object) (count))));
    }

    @Test
    public void testFrameAdviceStaticMethodComputedFrames() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceFrameTest.FrameSample.class).visit(Advice.to(advice).on(ElementMatchers.named(AdviceFrameTest.BAR)).writerFlags(ClassWriter.COMPUTE_FRAMES)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceFrameTest.BAR, String.class).invoke(null, AdviceFrameTest.FOO), CoreMatchers.is(((Object) (AdviceFrameTest.FOO))));
        MatcherAssert.assertThat(type.getField(AdviceFrameTest.COUNT).getInt(null), CoreMatchers.is(((Object) (count))));
    }

    @SuppressWarnings("all")
    public static class FrameSample {
        public static int count;

        public String foo(String value) {
            int ignored = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            return value;
        }

        public static String bar(String value) {
            int ignored = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdvice {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit(onThrowable = Exception.class)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceWithoutThrowable {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceWithSuppression {
        @Advice.OnMethodEnter(suppress = Exception.class)
        @Advice.OnMethodExit(suppress = Exception.class, onThrowable = Exception.class)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceEntryOnly {
        @Advice.OnMethodEnter
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceEntryOnlyWithSuppression {
        @Advice.OnMethodEnter(suppress = Exception.class)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceExitOnly {
        @Advice.OnMethodExit(onThrowable = Exception.class)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceExitOnlyWithSuppression {
        @Advice.OnMethodExit(suppress = Exception.class, onThrowable = Exception.class)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceExitOnlyWithSuppressionAndNonExceptionHandling {
        @Advice.OnMethodExit(suppress = Exception.class, onThrowable = Exception.class)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("all")
    public static class FrameReturnAdvice {
        @Advice.OnMethodEnter(suppress = RuntimeException.class)
        @Advice.OnMethodExit(suppress = RuntimeException.class)
        private static String advice() {
            try {
                int ignored = 0;
                if (ignored != 0) {
                    return AdviceFrameTest.BAR;
                }
            } catch (Exception e) {
                int ignored = 0;
                if (ignored != 0) {
                    return AdviceFrameTest.QUX;
                }
            }
            (AdviceFrameTest.FrameSample.count)++;
            return AdviceFrameTest.FOO;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceRetainedArguments {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit(onThrowable = Exception.class, backupArguments = false)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceWithoutThrowableRetainedArguments {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit(backupArguments = false)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceWithSuppressionRetainedArguments {
        @Advice.OnMethodEnter(suppress = Exception.class)
        @Advice.OnMethodExit(suppress = Exception.class, onThrowable = Exception.class, backupArguments = false)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceExitOnlyRetainedArguments {
        @Advice.OnMethodExit(onThrowable = Exception.class, backupArguments = false)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceExitOnlyWithSuppressionRetainedArguments {
        @Advice.OnMethodExit(suppress = Exception.class, onThrowable = Exception.class, backupArguments = false)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FrameAdviceExitOnlyWithSuppressionAndNonExceptionHandlingRetainedArguments {
        @Advice.OnMethodExit(suppress = Exception.class, onThrowable = Exception.class, backupArguments = false)
        private static String advice(@Advice.Unused
        int ignored, @Advice.Argument(0)
        String value) {
            int v0 = 0;
            {
                long v1 = 1L;
                long v2 = 2L;
                if (ignored == 1) {
                    throw new AssertionError();
                } else
                    if (ignored == 2) {
                        if ((v1 + v2) == 0L) {
                            throw new AssertionError();
                        }
                    }

            }
            long v3 = 3L;
            long v4 = 4L;
            long v5 = 5L;
            long v6 = 6L;
            if (ignored == 3) {
                throw new AssertionError();
            } else
                if (ignored == 4) {
                    if ((((v3 + v4) + v5) + v6) == 0L) {
                        throw new AssertionError();
                    }
                }

            try {
                long v7 = 7L;
            } catch (Exception exception) {
                long v8 = 8L;
            }
            (AdviceFrameTest.FrameSample.count)++;
            return value;
        }
    }

    @SuppressWarnings("all")
    public static class FrameReturnAdviceRetainedArguments {
        @Advice.OnMethodEnter(suppress = RuntimeException.class)
        @Advice.OnMethodExit(suppress = RuntimeException.class, backupArguments = false)
        private static String advice() {
            try {
                int ignored = 0;
                if (ignored != 0) {
                    return AdviceFrameTest.BAR;
                }
            } catch (Exception e) {
                int ignored = 0;
                if (ignored != 0) {
                    return AdviceFrameTest.QUX;
                }
            }
            (AdviceFrameTest.FrameSample.count)++;
            return AdviceFrameTest.FOO;
        }
    }
}

