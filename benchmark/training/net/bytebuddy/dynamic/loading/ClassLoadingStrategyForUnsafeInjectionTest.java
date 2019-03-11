package net.bytebuddy.dynamic.loading;


import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.ClassUnsafeInjectionAvailableRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;


public class ClassLoadingStrategyForUnsafeInjectionTest {
    @Rule
    public MethodRule classUnsafeInjectionAvailableRule = new ClassUnsafeInjectionAvailableRule();

    private ClassLoader classLoader;

    private TypeDescription typeDescription;

    private Map<TypeDescription, byte[]> binaryRepresentations;

    private ProtectionDomain protectionDomain;

    @Test
    @ClassUnsafeInjectionAvailableRule.Enforce
    public void testInjection() throws Exception {
        Map<TypeDescription, Class<?>> loaded = new ClassLoadingStrategy.ForUnsafeInjection().load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyForUnsafeInjectionTest.Foo.class.getName()));
    }

    @Test
    @ClassUnsafeInjectionAvailableRule.Enforce
    public void testInjectionWithProtectionDomain() throws Exception {
        Map<TypeDescription, Class<?>> loaded = new ClassLoadingStrategy.ForUnsafeInjection(protectionDomain).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyForUnsafeInjectionTest.Foo.class.getName()));
    }

    @Test
    @ClassUnsafeInjectionAvailableRule.Enforce
    public void testInjectionDoesNotThrowExceptionOnExistingClass() throws Exception {
        Map<TypeDescription, Class<?>> types = new ClassLoadingStrategy.ForUnsafeInjection(protectionDomain).load(ClassLoader.getSystemClassLoader(), Collections.singletonMap(TypeDescription.STRING, new byte[0]));
        MatcherAssert.assertThat(types.size(), CoreMatchers.is(1));
        TestCase.assertEquals(String.class, types.get(TypeDescription.STRING));
    }

    /* empty */
    private static class Foo {}
}

