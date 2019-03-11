package com.baeldung.bytebuddy;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class ByteBuddyUnitTest {
    @Test
    public void givenObject_whenToString_thenReturnHelloWorldString() throws IllegalAccessException, InstantiationException {
        DynamicType.Unloaded unloadedType = new ByteBuddy().subclass(Object.class).method(ElementMatchers.isToString()).intercept(FixedValue.value("Hello World ByteBuddy!")).make();
        Class<?> dynamicType = unloadedType.load(getClass().getClassLoader()).getLoaded();
        Assert.assertEquals(dynamicType.newInstance().toString(), "Hello World ByteBuddy!");
    }

    @Test
    public void givenFoo_whenRedefined_thenReturnFooRedefined() throws Exception {
        ByteBuddyAgent.install();
        new ByteBuddy().redefine(Foo.class).method(ElementMatchers.named("sayHelloFoo")).intercept(FixedValue.value("Hello Foo Redefined")).make().load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        Foo f = new Foo();
        Assert.assertEquals(f.sayHelloFoo(), "Hello Foo Redefined");
    }

    @Test
    public void givenSayHelloFoo_whenMethodDelegation_thenSayHelloBar() throws IllegalAccessException, InstantiationException {
        String r = new ByteBuddy().subclass(Foo.class).method(ElementMatchers.named("sayHelloFoo").and(ElementMatchers.isDeclaredBy(Foo.class).and(ElementMatchers.returns(String.class)))).intercept(MethodDelegation.to(Bar.class)).make().load(getClass().getClassLoader()).getLoaded().newInstance().sayHelloFoo();
        Assert.assertEquals(r, Bar.sayHelloBar());
    }

    @Test
    public void givenMethodName_whenDefineMethod_thenCreateMethod() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).name("MyClassName").defineMethod("custom", String.class, Modifier.PUBLIC).intercept(MethodDelegation.to(Bar.class)).defineField("x", String.class, Modifier.PUBLIC).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        Method m = type.getDeclaredMethod("custom", null);
        Assert.assertEquals(m.invoke(type.newInstance()), Bar.sayHelloBar());
        Assert.assertNotNull(type.getDeclaredField("x"));
    }
}

