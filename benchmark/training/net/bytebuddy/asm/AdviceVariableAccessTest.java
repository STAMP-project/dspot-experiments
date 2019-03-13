package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@RunWith(Parameterized.class)
public class AdviceVariableAccessTest {
    private static final String READ = "read";

    private static final String WRITE = "write";

    private final Class<?> sample;

    private final Class<?> type;

    private final Object value;

    public AdviceVariableAccessTest(Class<?> sample, Object value, Class<?> type) {
        this.sample = sample;
        this.value = value;
        this.type = type;
    }

    @Test
    public void testArray() throws Exception {
        Class<?> dynamicType = new ByteBuddy().redefine(sample).visit(Advice.to(AdviceVariableAccessTest.class).on(ElementMatchers.named(AdviceVariableAccessTest.READ).or(ElementMatchers.named(AdviceVariableAccessTest.WRITE)))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = dynamicType.getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AdviceVariableAccessTest.WRITE, type).invoke(instance, value), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(AdviceVariableAccessTest.READ).invoke(instance), CoreMatchers.is(value));
    }

    public static class BooleanSample {
        private final boolean[] array = new boolean[1];

        public void write(boolean value) {
            array[0] = value;
        }

        public boolean read() {
            return array[0];
        }
    }

    public static class ByteSample {
        private final byte[] array = new byte[1];

        public void write(byte value) {
            array[0] = value;
        }

        public byte read() {
            return array[0];
        }
    }

    public static class ShortSample {
        private final short[] array = new short[1];

        public void write(short value) {
            array[0] = value;
        }

        public short read() {
            return array[0];
        }
    }

    public static class CharacterSample {
        private final char[] array = new char[1];

        public void write(char value) {
            array[0] = value;
        }

        public char read() {
            return array[0];
        }
    }

    public static class IntegerSample {
        private final int[] array = new int[1];

        public void write(int value) {
            array[0] = value;
        }

        public int read() {
            return array[0];
        }
    }

    public static class LongSample {
        private final long[] array = new long[1];

        public void write(long value) {
            array[0] = value;
        }

        public long read() {
            return array[0];
        }
    }

    public static class FloatSample {
        private final float[] array = new float[1];

        public void write(float value) {
            array[0] = value;
        }

        public float read() {
            return array[0];
        }
    }

    public static class DoubleSample {
        private final double[] array = new double[1];

        public void write(double value) {
            array[0] = value;
        }

        public double read() {
            return array[0];
        }
    }

    public static class ReferenceSample {
        private final Object[] array = new Object[1];

        public void write(Object value) {
            array[0] = value;
        }

        public Object read() {
            return array[0];
        }
    }
}

