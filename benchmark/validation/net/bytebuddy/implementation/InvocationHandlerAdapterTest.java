package net.bytebuddy.implementation;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.CallTraceable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class InvocationHandlerAdapterTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final int BAZ = 42;

    @Test
    public void testStaticAdapterWithoutCache() throws Exception {
        InvocationHandlerAdapterTest.Foo foo = new InvocationHandlerAdapterTest.Foo();
        DynamicType.Loaded<InvocationHandlerAdapterTest.Bar> loaded = new ByteBuddy().subclass(InvocationHandlerAdapterTest.Bar.class).method(ElementMatchers.isDeclaredBy(InvocationHandlerAdapterTest.Bar.class)).intercept(InvocationHandlerAdapter.of(foo).withoutMethodCache()).make().load(InvocationHandlerAdapterTest.Bar.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        InvocationHandlerAdapterTest.Bar instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(foo.methods.get(0), CoreMatchers.not(CoreMatchers.sameInstance(foo.methods.get(1))));
        instance.assertZeroCalls();
    }

    @Test
    public void testStaticAdapterPrivileged() throws Exception {
        InvocationHandlerAdapterTest.Foo foo = new InvocationHandlerAdapterTest.Foo();
        DynamicType.Loaded<InvocationHandlerAdapterTest.Bar> loaded = new ByteBuddy().subclass(InvocationHandlerAdapterTest.Bar.class).method(ElementMatchers.isDeclaredBy(InvocationHandlerAdapterTest.Bar.class)).intercept(withPrivilegedLookup()).make().load(InvocationHandlerAdapterTest.Bar.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        InvocationHandlerAdapterTest.Bar instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(foo.methods.get(0), CoreMatchers.not(CoreMatchers.sameInstance(foo.methods.get(1))));
        instance.assertZeroCalls();
    }

    @Test
    public void testStaticAdapterWithoutCacheForPrimitiveValue() throws Exception {
        InvocationHandlerAdapterTest.Qux qux = new InvocationHandlerAdapterTest.Qux();
        DynamicType.Loaded<InvocationHandlerAdapterTest.Baz> loaded = new ByteBuddy().subclass(InvocationHandlerAdapterTest.Baz.class).method(ElementMatchers.isDeclaredBy(InvocationHandlerAdapterTest.Baz.class)).intercept(InvocationHandlerAdapter.of(qux).withoutMethodCache()).make().load(InvocationHandlerAdapterTest.Bar.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        InvocationHandlerAdapterTest.Baz instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.BAZ), CoreMatchers.is(((InvocationHandlerAdapterTest.BAZ) * 2L)));
        instance.assertZeroCalls();
    }

    @Test
    public void testStaticAdapterWithMethodCache() throws Exception {
        InvocationHandlerAdapterTest.Foo foo = new InvocationHandlerAdapterTest.Foo();
        DynamicType.Loaded<InvocationHandlerAdapterTest.Bar> loaded = new ByteBuddy().subclass(InvocationHandlerAdapterTest.Bar.class).method(ElementMatchers.isDeclaredBy(InvocationHandlerAdapterTest.Bar.class)).intercept(InvocationHandlerAdapter.of(foo)).make().load(InvocationHandlerAdapterTest.Bar.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(2));
        InvocationHandlerAdapterTest.Bar instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(foo.methods.get(0), CoreMatchers.sameInstance(foo.methods.get(1)));
        instance.assertZeroCalls();
    }

    @Test
    public void testInstanceAdapterWithoutCache() throws Exception {
        DynamicType.Loaded<InvocationHandlerAdapterTest.Bar> loaded = new ByteBuddy().subclass(InvocationHandlerAdapterTest.Bar.class).defineField(InvocationHandlerAdapterTest.QUX, InvocationHandler.class, Visibility.PUBLIC).method(ElementMatchers.isDeclaredBy(InvocationHandlerAdapterTest.Bar.class)).intercept(InvocationHandlerAdapter.toField(InvocationHandlerAdapterTest.QUX).withoutMethodCache()).make().load(InvocationHandlerAdapterTest.Bar.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(1));
        Field field = loaded.getLoaded().getDeclaredField(InvocationHandlerAdapterTest.QUX);
        MatcherAssert.assertThat(field.getModifiers(), CoreMatchers.is(Modifier.PUBLIC));
        field.setAccessible(true);
        InvocationHandlerAdapterTest.Bar instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        InvocationHandlerAdapterTest.Foo foo = new InvocationHandlerAdapterTest.Foo();
        field.set(instance, foo);
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(foo.methods.get(0), CoreMatchers.not(CoreMatchers.sameInstance(foo.methods.get(1))));
        instance.assertZeroCalls();
    }

    @Test
    public void testInstanceAdapterWithMethodCache() throws Exception {
        DynamicType.Loaded<InvocationHandlerAdapterTest.Bar> loaded = new ByteBuddy().subclass(InvocationHandlerAdapterTest.Bar.class).defineField(InvocationHandlerAdapterTest.QUX, InvocationHandler.class, Visibility.PUBLIC).method(ElementMatchers.isDeclaredBy(InvocationHandlerAdapterTest.Bar.class)).intercept(InvocationHandlerAdapter.toField(InvocationHandlerAdapterTest.QUX)).make().load(InvocationHandlerAdapterTest.Bar.class.getClassLoader(), WRAPPER);
        MatcherAssert.assertThat(loaded.getLoadedAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(loaded.getLoaded().getDeclaredFields().length, CoreMatchers.is(2));
        Field field = loaded.getLoaded().getDeclaredField(InvocationHandlerAdapterTest.QUX);
        MatcherAssert.assertThat(field.getModifiers(), CoreMatchers.is(Modifier.PUBLIC));
        field.setAccessible(true);
        InvocationHandlerAdapterTest.Bar instance = loaded.getLoaded().getDeclaredConstructor().newInstance();
        InvocationHandlerAdapterTest.Foo foo = new InvocationHandlerAdapterTest.Foo();
        field.set(instance, foo);
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(instance.bar(InvocationHandlerAdapterTest.FOO), CoreMatchers.is(((Object) (instance))));
        MatcherAssert.assertThat(foo.methods.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(foo.methods.get(0), CoreMatchers.sameInstance(foo.methods.get(1)));
        instance.assertZeroCalls();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticMethod() throws Exception {
        new ByteBuddy().subclass(Object.class).defineField(InvocationHandlerAdapterTest.QUX, InvocationHandler.class).defineMethod(InvocationHandlerAdapterTest.FOO, void.class, Ownership.STATIC).intercept(InvocationHandlerAdapter.toField(InvocationHandlerAdapterTest.QUX)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonExistentField() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(InvocationHandlerAdapterTest.FOO, void.class).intercept(InvocationHandlerAdapter.toField(InvocationHandlerAdapterTest.QUX)).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testIncompatibleFieldType() throws Exception {
        new ByteBuddy().subclass(Object.class).defineField(InvocationHandlerAdapterTest.QUX, Object.class).defineMethod(InvocationHandlerAdapterTest.FOO, void.class).intercept(InvocationHandlerAdapter.toField(InvocationHandlerAdapterTest.QUX)).make();
    }

    private static class Foo implements InvocationHandler {
        private final String marker;

        public List<Method> methods;

        private Foo() {
            marker = InvocationHandlerAdapterTest.FOO;
            methods = new ArrayList<Method>();
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            methods.add(method);
            MatcherAssert.assertThat(args.length, CoreMatchers.is(1));
            MatcherAssert.assertThat(args[0], CoreMatchers.is(((Object) (InvocationHandlerAdapterTest.FOO))));
            MatcherAssert.assertThat(method.getName(), CoreMatchers.is(InvocationHandlerAdapterTest.BAR));
            MatcherAssert.assertThat(proxy, CoreMatchers.instanceOf(InvocationHandlerAdapterTest.Bar.class));
            return proxy;
        }

        @Override
        public int hashCode() {
            return marker.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return ((this) == other) || ((!((other == null) || ((getClass()) != (other.getClass())))) && (marker.equals(((InvocationHandlerAdapterTest.Foo) (other)).marker)));
        }
    }

    public static class Bar extends CallTraceable {
        public Object bar(Object o) {
            register(InvocationHandlerAdapterTest.BAR);
            return o;
        }
    }

    private static class Qux implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return ((Integer) (args[0])) * 2L;
        }
    }

    public static class Baz extends CallTraceable {
        public long bar(int o) {
            register(InvocationHandlerAdapterTest.BAR);
            return o;
        }
    }
}

