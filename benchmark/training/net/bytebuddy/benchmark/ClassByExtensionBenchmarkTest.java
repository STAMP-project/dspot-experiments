package net.bytebuddy.benchmark;


import ClassByExtensionBenchmark.BASE_CLASS;
import net.bytebuddy.benchmark.specimen.ExampleClass;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ClassByExtensionBenchmarkTest {
    private static final boolean BOOLEAN_VALUE = true;

    private static final byte BYTE_VALUE = 42;

    private static final short SHORT_VALUE = 42;

    private static final char CHAR_VALUE = '@';

    private static final int INT_VALUE = 42;

    private static final long LONG_VALUE = 42L;

    private static final float FLOAT_VALUE = 42.0F;

    private static final double DOUBLE_VALUE = 42.0;

    private static final Object REFERENCE_VALUE = "foo";

    private ClassByExtensionBenchmark classByExtensionBenchmark;

    @Test
    public void testBaseline() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.baseline();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithProxiesClassCreation() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithProxy();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithProxiesClassCreationCached() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithProxyAndReusedDelegator();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithProxiesClassCreationWithTypePool() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithProxyWithTypePool();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithProxiesClassCreationCachedWithTypePool() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithProxyAndReusedDelegatorWithTypePool();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithAccessorClassCreation() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithAccessor();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithAccessorClassCreationCached() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithAccessorAndReusedDelegator();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithAccessorClassCreationWithTypePool() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithAccessorWithTypePool();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithAccessorClassCreationCachedWithTypePool() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithAccessorAndReusedDelegatorWithTypePool();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithPrefixClassCreation() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithPrefix();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithPrefixClassCreationCached() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithPrefixAndReusedDelegator();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithPrefixClassCreationWithTypePool() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithPrefixWithTypePool();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddyWithPrefixClassCreationCachedWithTypePool() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddyWithPrefixAndReusedDelegatorWithTypePool();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddyWithProxy().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testByteBuddySpecializedClassCreation() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkByteBuddySpecialized();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkByteBuddySpecialized().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testCglibClassCreation() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkCglib();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkCglib().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }

    @Test
    public void testJavassistClassCreation() throws Exception {
        ExampleClass instance = classByExtensionBenchmark.benchmarkJavassist();
        MatcherAssert.assertThat(instance.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(instance.getClass().getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(classByExtensionBenchmark.benchmarkJavassist().getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(instance.getClass())));
        ClassByExtensionBenchmarkTest.assertReturnValues(instance);
    }
}

