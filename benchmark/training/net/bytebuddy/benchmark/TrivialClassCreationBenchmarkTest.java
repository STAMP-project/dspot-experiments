package net.bytebuddy.benchmark;


import TrivialClassCreationBenchmark.BASE_CLASS;
import java.lang.reflect.Proxy;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class TrivialClassCreationBenchmarkTest {
    private TrivialClassCreationBenchmark trivialClassCreationBenchmark;

    @Test
    public void testBaseline() throws Exception {
        Class<?> type = trivialClassCreationBenchmark.baseline();
        MatcherAssert.assertThat(type, CoreMatchers.<Class<?>>is(BASE_CLASS));
    }

    @Test
    public void testByteBuddyClassCreation() throws Exception {
        Class<?> type = trivialClassCreationBenchmark.benchmarkByteBuddy();
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(type.getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(trivialClassCreationBenchmark.benchmarkByteBuddy(), CoreMatchers.not(CoreMatchers.<Class<?>>is(type)));
    }

    @Test
    public void testCglibClassCreation() throws Exception {
        Class<?> type = trivialClassCreationBenchmark.benchmarkCglib();
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(type.getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(trivialClassCreationBenchmark.benchmarkCglib(), CoreMatchers.not(CoreMatchers.<Class<?>>is(type)));
    }

    @Test
    public void testJavassistClassCreation() throws Exception {
        Class<?> type = trivialClassCreationBenchmark.benchmarkJavassist();
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(type.getSuperclass(), CoreMatchers.<Class<?>>is(BASE_CLASS));
        MatcherAssert.assertThat(trivialClassCreationBenchmark.benchmarkJavassist(), CoreMatchers.not(CoreMatchers.<Class<?>>is(type)));
    }

    @Test
    public void testJdkProxyClassCreation() throws Exception {
        Class<?> type = trivialClassCreationBenchmark.benchmarkJdkProxy();
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.<Class<?>>is(BASE_CLASS)));
        MatcherAssert.assertThat(type.getSuperclass(), CoreMatchers.<Class<?>>is(Proxy.class));
        MatcherAssert.assertThat(trivialClassCreationBenchmark.benchmarkJdkProxy(), CoreMatchers.not(CoreMatchers.<Class<?>>is(type)));
    }
}

