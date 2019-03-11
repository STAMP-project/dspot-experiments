package net.bytebuddy.dynamic.loading;


import java.lang.reflect.Constructor;
import java.util.Collections;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.JavaType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.UsingLookup.of;


public class ClassInjectorUsingLookupTest {
    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private Class<?> type;

    @Test
    @JavaVersionRule.Enforce(9)
    public void testIsAvailable() {
        MatcherAssert.assertThat(isAvailable(), CoreMatchers.is(true));
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testLookupType() throws Exception {
        MatcherAssert.assertThat(ClassInjector.UsingLookup.of(type.getMethod("lookup").invoke(null)).lookupType(), CoreMatchers.is(((Object) (type))));
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testLookupInjection() throws Exception {
        ClassInjector injector = ClassInjector.UsingLookup.of(type.getMethod("lookup").invoke(null));
        DynamicType dynamicType = new ByteBuddy().subclass(Object.class).name("net.bytebuddy.test.Bar").make();
        MatcherAssert.assertThat(injector.inject(Collections.singletonMap(dynamicType.getTypeDescription(), dynamicType.getBytes())).get(dynamicType.getTypeDescription()).getName(), CoreMatchers.is("net.bytebuddy.test.Bar"));
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testLookupInjectionPropagate() throws Exception {
        ClassInjector injector = in(type);
        DynamicType dynamicType = new ByteBuddy().subclass(Object.class).name("net.bytebuddy.test.Bar").make();
        MatcherAssert.assertThat(injector.inject(Collections.singletonMap(dynamicType.getTypeDescription(), dynamicType.getBytes())).get(dynamicType.getTypeDescription()).getName(), CoreMatchers.is("net.bytebuddy.test.Bar"));
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testSubclassUsingLookup() throws Exception {
        Class<?> defaultPackageType = new ByteBuddy().subclass(Object.class).name("DefaultPackageType").make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object privateLookup = METHOD_HANDLES.load().getMethod("privateLookupIn", Class.class, JavaType.METHOD_HANDLES_LOOKUP.load()).invoke(null, defaultPackageType, METHOD_HANDLES.load().getMethod("lookup").invoke(null));
        Class<?> type = new ByteBuddy().subclass(defaultPackageType).make().load(defaultPackageType.getClassLoader(), of(privateLookup)).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(0));
        MatcherAssert.assertThat(type.getDeclaredConstructors().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredConstructor(), CoreMatchers.notNullValue(Constructor.class));
        MatcherAssert.assertThat(defaultPackageType.isAssignableFrom(type), CoreMatchers.is(true));
        MatcherAssert.assertThat(type, CoreMatchers.not(CoreMatchers.is(((Object) (defaultPackageType)))));
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.instanceOf(defaultPackageType));
        Assert.assertTrue(defaultPackageType.isInstance(type.getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(type.isInterface(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isAnnotation(), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testAvailable() throws Exception {
        MatcherAssert.assertThat(isAvailable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(isAlive(), CoreMatchers.is(((Object) (true))));
    }
}

