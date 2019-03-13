package net.bytebuddy.benchmark;


import ClassByImplementationBenchmark.BASE_CLASS;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import net.bytebuddy.benchmark.specimen.ExampleInterface;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ClassByImplementationBenchmarkTest {
    private static final boolean BOOLEAN_VALUE = true;

    private static final byte BYTE_VALUE = 42;

    private static final short SHORT_VALUE = 42;

    private static final char CHAR_VALUE = '@';

    private static final int INT_VALUE = 42;

    private static final long LONG_VALUE = 42L;

    private static final float FLOAT_VALUE = 42.0F;

    private static final double DOUBLE_VALUE = 42.0;

    private static final Object REFERENCE_VALUE = "foo";

    private ClassByImplementationBenchmark classByImplementationBenchmark;

    @Test
    public void testBaseline() throws Exception {
        ExampleInterface instance = classByImplementationBenchmark.baseline();
        MatcherAssert.assertThat(Arrays.asList(instance.getClass().getInterfaces()), CoreMatchers.hasItem(BASE_CLASS));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(classByImplementationBenchmark.benchmarkByteBuddy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByImplementationBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyClassCreation() throws Exception {
        ExampleInterface instance = classByImplementationBenchmark.benchmarkByteBuddy();
        MatcherAssert.assertThat(Arrays.asList(instance.getClass().getInterfaces()), CoreMatchers.hasItem(BASE_CLASS));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(classByImplementationBenchmark.benchmarkByteBuddy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByImplementationBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyClassCreationWithTypePool() throws Exception {
        ExampleInterface instance = classByImplementationBenchmark.benchmarkByteBuddyWithTypePool();
        MatcherAssert.assertThat(Arrays.asList(instance.getClass().getInterfaces()), CoreMatchers.hasItem(BASE_CLASS));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(classByImplementationBenchmark.benchmarkByteBuddy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByImplementationBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testCglibClassCreation() throws Exception {
        ExampleInterface instance = classByImplementationBenchmark.benchmarkCglib();
        MatcherAssert.assertThat(Arrays.asList(instance.getClass().getInterfaces()), CoreMatchers.hasItem(BASE_CLASS));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(classByImplementationBenchmark.benchmarkCglib().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByImplementationBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testJavassistClassCreation() throws Exception {
        ExampleInterface instance = classByImplementationBenchmark.benchmarkJavassist();
        MatcherAssert.assertThat(Arrays.asList(instance.getClass().getInterfaces()), CoreMatchers.hasItem(BASE_CLASS));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(classByImplementationBenchmark.benchmarkJavassist().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByImplementationBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testJdkProxyClassCreation() throws Exception {
        ExampleInterface instance = classByImplementationBenchmark.benchmarkJdkProxy();
        MatcherAssert.assertThat(Arrays.asList(instance.getClass().getInterfaces()), CoreMatchers.hasItem(BASE_CLASS));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(Proxy.class));
        MatcherAssert.assertThat(classByImplementationBenchmark.benchmarkByteBuddy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByImplementationBenchmarkTest.assertReturnValues(instance);
    }
}

