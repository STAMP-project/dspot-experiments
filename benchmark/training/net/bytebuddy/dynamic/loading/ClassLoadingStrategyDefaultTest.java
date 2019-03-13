package net.bytebuddy.dynamic.loading;


import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.ClassReflectionInjectionAvailableRule;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST_PERSISTENT;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.INJECTION;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER_PERSISTENT;


public class ClassLoadingStrategyDefaultTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Rule
    public MethodRule classInjectionAvailableRule = new ClassReflectionInjectionAvailableRule();

    private ClassLoader classLoader;

    private TypeDescription typeDescription;

    private Map<TypeDescription, byte[]> binaryRepresentations;

    private ProtectionDomain protectionDomain;

    @Mock
    private PackageDefinitionStrategy packageDefinitionStrategy;

    @Test
    public void testWrapper() throws Exception {
        Map<TypeDescription, Class<?>> loaded = WRAPPER.load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testWrapperPersistent() throws Exception {
        Map<TypeDescription, Class<?>> loaded = WRAPPER_PERSISTENT.load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testChildFirst() throws Exception {
        Map<TypeDescription, Class<?>> loaded = CHILD_FIRST.load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testChildFirstPersistent() throws Exception {
        Map<TypeDescription, Class<?>> loaded = CHILD_FIRST_PERSISTENT.load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testInjection() throws Exception {
        Map<TypeDescription, Class<?>> loaded = INJECTION.load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testWrapperWithProtectionDomain() throws Exception {
        Map<TypeDescription, Class<?>> loaded = WRAPPER.with(protectionDomain).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testWrapperPersistentWithProtectionDomain() throws Exception {
        Map<TypeDescription, Class<?>> loaded = WRAPPER_PERSISTENT.with(protectionDomain).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testChildFirstWithProtectionDomain() throws Exception {
        Map<TypeDescription, Class<?>> loaded = CHILD_FIRST.with(protectionDomain).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testChildFirstPersistentWithProtectionDomain() throws Exception {
        Map<TypeDescription, Class<?>> loaded = CHILD_FIRST_PERSISTENT.with(protectionDomain).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testInjectionWithProtectionDomain() throws Exception {
        Map<TypeDescription, Class<?>> loaded = INJECTION.with(protectionDomain).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testWrapperWithPackageDefiner() throws Exception {
        Map<TypeDescription, Class<?>> loaded = WRAPPER.with(packageDefinitionStrategy).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
        Mockito.verify(packageDefinitionStrategy).define(ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getPackage().getName()), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testWrapperPersistentWithPackageDefinitionStrategy() throws Exception {
        Map<TypeDescription, Class<?>> loaded = WRAPPER_PERSISTENT.with(packageDefinitionStrategy).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
        Mockito.verify(packageDefinitionStrategy).define(ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getPackage().getName()), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testChildFirstWithPackageDefinitionStrategy() throws Exception {
        Map<TypeDescription, Class<?>> loaded = CHILD_FIRST.with(packageDefinitionStrategy).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
        Mockito.verify(packageDefinitionStrategy).define(ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getPackage().getName()), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    public void testChildFirstPersistentWithPackageDefinitionStrategy() throws Exception {
        Map<TypeDescription, Class<?>> loaded = CHILD_FIRST_PERSISTENT.with(packageDefinitionStrategy).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader().getParent(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
        Mockito.verify(packageDefinitionStrategy).define(ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getPackage().getName()), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test
    @ClassReflectionInjectionAvailableRule.Enforce
    public void testInjectionWithPackageDefinitionStrategy() throws Exception {
        Map<TypeDescription, Class<?>> loaded = INJECTION.with(packageDefinitionStrategy).load(classLoader, binaryRepresentations);
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.is(classLoader));
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
        Mockito.verify(packageDefinitionStrategy).define(ArgumentMatchers.any(ClassLoader.class), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getPackage().getName()), ArgumentMatchers.eq(ClassLoadingStrategyDefaultTest.Foo.class.getName()));
    }

    @Test(expected = IllegalStateException.class)
    public void testWrapperThrowsExceptionOnExistingClass() throws Exception {
        WRAPPER.load(ClassLoader.getSystemClassLoader(), Collections.singletonMap(TypeDescription.STRING, new byte[0]));
    }

    @Test(expected = IllegalStateException.class)
    public void testWrapperPersistentThrowsExceptionOnExistingClass() throws Exception {
        WRAPPER_PERSISTENT.load(ClassLoader.getSystemClassLoader(), Collections.singletonMap(TypeDescription.STRING, new byte[0]));
    }

    @Test(expected = IllegalStateException.class)
    public void testInjectionThrowsExceptionOnExistingClass() throws Exception {
        INJECTION.load(ClassLoader.getSystemClassLoader(), Collections.singletonMap(TypeDescription.STRING, new byte[0]));
    }

    @Test
    public void testWrapperDoesNotThrowExceptionOnExistingClassWhenSupressed() throws Exception {
        Map<TypeDescription, Class<?>> types = WRAPPER.allowExistingTypes().load(ClassLoader.getSystemClassLoader(), Collections.singletonMap(TypeDescription.STRING, new byte[0]));
        MatcherAssert.assertThat(types.size(), CoreMatchers.is(1));
        TestCase.assertEquals(String.class, types.get(TypeDescription.STRING));
    }

    @Test
    public void testWrapperPersistentDoesNotThrowExceptionOnExistingClassWhenSupressed() throws Exception {
        Map<TypeDescription, Class<?>> types = WRAPPER_PERSISTENT.allowExistingTypes().load(ClassLoader.getSystemClassLoader(), Collections.singletonMap(TypeDescription.STRING, new byte[0]));
        MatcherAssert.assertThat(types.size(), CoreMatchers.is(1));
        TestCase.assertEquals(String.class, types.get(TypeDescription.STRING));
    }

    @Test
    public void testInjectionDoesNotThrowExceptionOnExistingClassWhenSupressed() throws Exception {
        Map<TypeDescription, Class<?>> types = INJECTION.allowExistingTypes().load(ClassLoader.getSystemClassLoader(), Collections.singletonMap(TypeDescription.STRING, new byte[0]));
        MatcherAssert.assertThat(types.size(), CoreMatchers.is(1));
        TestCase.assertEquals(String.class, types.get(TypeDescription.STRING));
    }

    /* empty */
    private static class Foo {}
}

