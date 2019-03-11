package net.bytebuddy;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.TypeResolutionStrategy;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class ByteBuddyTest {
    @Test(expected = IllegalArgumentException.class)
    public void testEnumWithoutValuesIsIllegal() throws Exception {
        new ByteBuddy().makeEnumeration();
    }

    @Test
    public void testEnumeration() throws Exception {
        Class<?> type = new ByteBuddy().makeEnumeration("foo").make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(Modifier.isPublic(type.getModifiers()), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isEnum(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(false));
    }

    @Test
    public void testInterface() throws Exception {
        Class<?> type = new ByteBuddy().makeInterface().make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(Modifier.isPublic(type.getModifiers()), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isEnum(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(false));
    }

    @Test
    public void testAnnotation() throws Exception {
        Class<?> type = new ByteBuddy().makeAnnotation().make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(Modifier.isPublic(type.getModifiers()), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isEnum(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(true));
    }

    @Test
    public void testTypeInitializerInstrumentation() throws Exception {
        ByteBuddyTest.Recorder recorder = new ByteBuddyTest.Recorder();
        Class<?> type = new ByteBuddy().subclass(Object.class).invokable(ElementMatchers.isTypeInitializer()).intercept(MethodDelegation.to(recorder)).make(new TypeResolutionStrategy.Active()).load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.instanceOf(type));
        MatcherAssert.assertThat(recorder.counter, CoreMatchers.is(1));
    }

    @Test
    public void testImplicitStrategyBootstrap() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER).getLoaded();
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.notNullValue(ClassLoader.class));
    }

    @Test
    public void testImplicitStrategyNonBootstrap() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0], ClassLoadingStrategy.BOOTSTRAP_LOADER);
        Class<?> type = new ByteBuddy().subclass(Object.class).make().load(classLoader).getLoaded();
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.not(classLoader));
    }

    @Test
    public void testImplicitStrategyInjectable() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, false, Collections.<String, byte[]>emptyMap());
        Class<?> type = new ByteBuddy().subclass(Object.class).make().load(classLoader).getLoaded();
        MatcherAssert.assertThat(type.getClassLoader(), CoreMatchers.is(classLoader));
    }

    @Test
    public void testClassWithManyMethods() throws Exception {
        DynamicType.Builder<?> builder = new ByteBuddy().subclass(Object.class);
        for (int index = 0; index < 1000; index++) {
            builder = builder.defineMethod(("method" + index), void.class, Visibility.PUBLIC).intercept(StubMethod.INSTANCE);
        }
        Class<?> type = builder.make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(1000));
        DynamicType.Builder<?> subclassBuilder = new ByteBuddy().subclass(type);
        for (Method method : type.getDeclaredMethods()) {
            subclassBuilder = subclassBuilder.method(ElementMatchers.is(method)).intercept(StubMethod.INSTANCE);
        }
        Class<?> subclass = subclassBuilder.make().load(type.getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(subclass.getDeclaredMethods().length, CoreMatchers.is(1000));
    }

    public static class Recorder {
        public int counter;

        public void instrument() {
            (counter)++;
        }
    }
}

