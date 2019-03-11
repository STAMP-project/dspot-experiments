package net.bytebuddy.implementation;


import java.io.Serializable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bind.annotation.FieldProxy.Binder.install;


public class MethodDelegationFieldProxyTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Test
    public void testExplicitFieldAccessSimplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.Explicit> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.Explicit.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.Explicit.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.Swap.class)).make().load(MethodDelegationFieldProxyTest.Explicit.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.Explicit explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(explicit.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        explicit.swap();
        MatcherAssert.assertThat(explicit.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testExplicitFieldAccessDuplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.Explicit> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.Explicit.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.Explicit.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationFieldProxyTest.GetSet.class)).to(MethodDelegationFieldProxyTest.SwapDuplex.class)).make().load(MethodDelegationFieldProxyTest.Explicit.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.Explicit explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(explicit.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        explicit.swap();
        MatcherAssert.assertThat(explicit.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testExplicitFieldAccessSerializableSimplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.Explicit> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.Explicit.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.Explicit.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.SwapSerializable.class)).make().load(MethodDelegationFieldProxyTest.Explicit.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.Explicit explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(explicit.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        explicit.swap();
        MatcherAssert.assertThat(explicit.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testExplicitFieldAccessSerializableDuplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.Explicit> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.Explicit.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.Explicit.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationFieldProxyTest.GetSet.class)).to(MethodDelegationFieldProxyTest.SwapSerializableDuplex.class)).make().load(MethodDelegationFieldProxyTest.Explicit.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.Explicit explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(explicit.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        explicit.swap();
        MatcherAssert.assertThat(explicit.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testExplicitFieldAccessStatic() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.ExplicitStatic> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.ExplicitStatic.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.ExplicitStatic.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.Swap.class)).make().load(MethodDelegationFieldProxyTest.ExplicitStatic.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.ExplicitStatic explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(MethodDelegationFieldProxyTest.ExplicitStatic.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        explicit.swap();
        MatcherAssert.assertThat(MethodDelegationFieldProxyTest.ExplicitStatic.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testExplicitFieldAccessStaticDuplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.ExplicitStatic> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.ExplicitStatic.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.ExplicitStatic.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationFieldProxyTest.GetSet.class)).to(MethodDelegationFieldProxyTest.SwapDuplex.class)).make().load(MethodDelegationFieldProxyTest.ExplicitStatic.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.ExplicitStatic explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(MethodDelegationFieldProxyTest.ExplicitStatic.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        explicit.swap();
        MatcherAssert.assertThat(MethodDelegationFieldProxyTest.ExplicitStatic.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testImplicitFieldGetterAccess() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.ImplicitGetter> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.ImplicitGetter.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.ImplicitGetter.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.GetInterceptor.class)).make().load(MethodDelegationFieldProxyTest.ImplicitGetter.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.ImplicitGetter implicitGetter = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(implicitGetter.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        MatcherAssert.assertThat(implicitGetter.getFoo(), CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
        MatcherAssert.assertThat(implicitGetter.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testImplicitFieldSetterAccess() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.ImplicitSetter> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.ImplicitSetter.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.ImplicitSetter.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.SetInterceptor.class)).make().load(MethodDelegationFieldProxyTest.ImplicitSetter.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.ImplicitSetter implicitSetter = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(implicitSetter.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        implicitSetter.setFoo(MethodDelegationFieldProxyTest.BAR);
        MatcherAssert.assertThat(implicitSetter.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testImplicitFieldGetterAccessDuplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.ImplicitGetter> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.ImplicitGetter.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.ImplicitGetter.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationFieldProxyTest.GetSet.class)).to(MethodDelegationFieldProxyTest.GetInterceptorDuplex.class)).make().load(MethodDelegationFieldProxyTest.ImplicitGetter.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.ImplicitGetter implicitGetter = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(implicitGetter.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        MatcherAssert.assertThat(implicitGetter.getFoo(), CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
        MatcherAssert.assertThat(implicitGetter.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testImplicitFieldSetterAccessDuplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.ImplicitSetter> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.ImplicitSetter.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.ImplicitSetter.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationFieldProxyTest.GetSet.class)).to(MethodDelegationFieldProxyTest.SetInterceptorDuplex.class)).make().load(MethodDelegationFieldProxyTest.ImplicitSetter.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.ImplicitSetter implicitSetter = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(implicitSetter.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        implicitSetter.setFoo(MethodDelegationFieldProxyTest.BAR);
        MatcherAssert.assertThat(implicitSetter.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testExplicitFieldAccessImplicitTarget() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.ExplicitInherited> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.ExplicitInherited.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.ExplicitInherited.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.Swap.class)).make().load(MethodDelegationFieldProxyTest.ExplicitInherited.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.ExplicitInherited explicitInherited = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(((MethodDelegationFieldProxyTest.Explicit) (explicitInherited)).foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        MatcherAssert.assertThat(explicitInherited.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.QUX));
        explicitInherited.swap();
        MatcherAssert.assertThat(((MethodDelegationFieldProxyTest.Explicit) (explicitInherited)).foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        MatcherAssert.assertThat(explicitInherited.foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.QUX) + (MethodDelegationFieldProxyTest.BAR))));
    }

    @Test
    public void testExplicitFieldAccessExplicitTarget() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.ExplicitInherited> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.ExplicitInherited.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.ExplicitInherited.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.SwapInherited.class)).make().load(MethodDelegationFieldProxyTest.ExplicitInherited.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.ExplicitInherited explicitInherited = loaded.getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(((MethodDelegationFieldProxyTest.Explicit) (explicitInherited)).foo, CoreMatchers.is(MethodDelegationFieldProxyTest.FOO));
        MatcherAssert.assertThat(explicitInherited.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.QUX));
        explicitInherited.swap();
        MatcherAssert.assertThat(((MethodDelegationFieldProxyTest.Explicit) (explicitInherited)).foo, CoreMatchers.is(((MethodDelegationFieldProxyTest.FOO) + (MethodDelegationFieldProxyTest.BAR))));
        MatcherAssert.assertThat(explicitInherited.foo, CoreMatchers.is(MethodDelegationFieldProxyTest.QUX));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFinalFieldSimplex() throws Exception {
        new ByteBuddy().subclass(MethodDelegationFieldProxyTest.FinalField.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.FinalField.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.Swap.class)).make();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFinalFieldDuplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.FinalField> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.FinalField.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.FinalField.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationFieldProxyTest.GetSet.class)).to(MethodDelegationFieldProxyTest.SwapDuplex.class)).make().load(MethodDelegationFieldProxyTest.FinalField.class.getClassLoader(), WRAPPER);
        loaded.getLoaded().getDeclaredConstructor().newInstance().method();
    }

    @Test(expected = ClassCastException.class)
    public void testIncompatibleGetterTypeThrowsException() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.Explicit> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.Explicit.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.Explicit.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.GetterIncompatible.class)).make().load(MethodDelegationFieldProxyTest.Explicit.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.Explicit explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        explicit.swap();
    }

    @Test(expected = ClassCastException.class)
    public void testIncompatibleSetterTypeThrowsException() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.Explicit> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.Explicit.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.Explicit.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.Set.class)).to(MethodDelegationFieldProxyTest.SetterIncompatible.class)).make().load(MethodDelegationFieldProxyTest.Explicit.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.Explicit explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        explicit.swap();
    }

    @Test(expected = ClassCastException.class)
    public void testIncompatibleTypeThrowsExceptionGetDuplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.Explicit> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.Explicit.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.Explicit.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationFieldProxyTest.GetSet.class)).to(MethodDelegationFieldProxyTest.GetterIncompatibleDuplex.class)).make().load(MethodDelegationFieldProxyTest.Explicit.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.Explicit explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        explicit.swap();
    }

    @Test(expected = ClassCastException.class)
    public void testIncompatibleTypeThrowsExceptionSetDuplex() throws Exception {
        DynamicType.Loaded<MethodDelegationFieldProxyTest.Explicit> loaded = new ByteBuddy().subclass(MethodDelegationFieldProxyTest.Explicit.class).method(ElementMatchers.isDeclaredBy(MethodDelegationFieldProxyTest.Explicit.class)).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(MethodDelegationFieldProxyTest.GetSet.class)).to(MethodDelegationFieldProxyTest.SetterIncompatibleDuplex.class)).make().load(MethodDelegationFieldProxyTest.Explicit.class.getClassLoader(), WRAPPER);
        MethodDelegationFieldProxyTest.Explicit explicit = loaded.getLoaded().getDeclaredConstructor().newInstance();
        explicit.swap();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetterTypeDoesNotDeclareCorrectMethodThrowsException() throws Exception {
        FieldProxy.Binder.install(Serializable.class, MethodDelegationFieldProxyTest.Set.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetterTypeDoesNotDeclareCorrectMethodThrowsException() throws Exception {
        FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, Serializable.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetterTypeDoesInheritFromOtherTypeThrowsException() throws Exception {
        FieldProxy.Binder.install(MethodDelegationFieldProxyTest.GetInherited.class, MethodDelegationFieldProxyTest.Set.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetterTypeDoesInheritFromOtherTypeThrowsException() throws Exception {
        FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.SetInherited.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetterTypeNonPublicThrowsException() throws Exception {
        FieldProxy.Binder.install(MethodDelegationFieldProxyTest.GetPrivate.class, MethodDelegationFieldProxyTest.Set.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetterTypeNonPublicThrowsException() throws Exception {
        FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.SetPrivate.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetterTypeIncorrectSignatureThrowsException() throws Exception {
        FieldProxy.Binder.install(MethodDelegationFieldProxyTest.GetIncorrect.class, MethodDelegationFieldProxyTest.Set.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetterTypeIncorrectSignatureThrowsException() throws Exception {
        FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, MethodDelegationFieldProxyTest.SetIncorrect.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetterTypeNotInterfaceThrowsException() throws Exception {
        FieldProxy.Binder.install(Object.class, MethodDelegationFieldProxyTest.Set.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetterTypeNotInterfaceThrowsException() throws Exception {
        FieldProxy.Binder.install(MethodDelegationFieldProxyTest.Get.class, Object.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplexTooManyMethodsThrowsException() throws Exception {
        install(MethodDelegationFieldProxyTest.GetSetTooManyMethods.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplexNonPublicThrowsException() throws Exception {
        install(MethodDelegationFieldProxyTest.GetSetNonPublic.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplexInheritedThrowsException() throws Exception {
        install(MethodDelegationFieldProxyTest.GetSetInherited.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetterTypeIncorrectSignatureDuplexThrowsException() throws Exception {
        install(MethodDelegationFieldProxyTest.GetSetSetIncorrect.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetterTypeIncorrectSignatureDuplexThrowsException() throws Exception {
        install(MethodDelegationFieldProxyTest.GetSetGetIncorrect.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTypeNotInterfaceDuplexThrowsException() throws Exception {
        install(Object.class);
    }

    public interface Get<T> {
        T get();
    }

    public interface Set<T> {
        void set(T value);
    }

    /* empty */
    public interface GetInherited<T> extends MethodDelegationFieldProxyTest.Get<T> {}

    /* empty */
    public interface SetInherited<T> extends MethodDelegationFieldProxyTest.Set<T> {}

    private interface GetPrivate<T> {
        T get();
    }

    private interface SetPrivate<T> {
        void set(T value);
    }

    public interface GetIncorrect<T> {
        T get(Object value);
    }

    public interface SetIncorrect<T> {
        Object set(T value);
    }

    public interface GetSet<T> {
        T get();

        void set(T value);
    }

    public interface GetSetGetIncorrect<T> {
        String get();

        void set(T value);
    }

    public interface GetSetSetIncorrect<T> {
        T get();

        void set(String value);
    }

    interface GetSetNonPublic<T> {
        T get();

        void set(T value);
    }

    public interface GetSetTooManyMethods<T> {
        T get();

        void set(String value);

        void set(T value);
    }

    /* empty */
    public interface GetSetInherited<T> extends MethodDelegationFieldProxyTest.GetSet<T> {}

    public static class Swap {
        public static void swap(@FieldProxy(MethodDelegationFieldProxyTest.FOO)
        MethodDelegationFieldProxyTest.Get<String> getter, @FieldProxy(MethodDelegationFieldProxyTest.FOO)
        MethodDelegationFieldProxyTest.Set<String> setter) {
            MatcherAssert.assertThat(getter, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            MatcherAssert.assertThat(setter, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            setter.set(((getter.get()) + (MethodDelegationFieldProxyTest.BAR)));
        }
    }

    public static class SwapDuplex {
        public static void swap(@FieldProxy(MethodDelegationFieldProxyTest.FOO)
        MethodDelegationFieldProxyTest.GetSet<String> accessor) {
            MatcherAssert.assertThat(accessor, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            accessor.set(((accessor.get()) + (MethodDelegationFieldProxyTest.BAR)));
        }
    }

    public static class Explicit {
        protected String foo = MethodDelegationFieldProxyTest.FOO;

        public void swap() {
            /* do nothing */
        }
    }

    public static class ExplicitInherited extends MethodDelegationFieldProxyTest.Explicit {
        protected String foo = MethodDelegationFieldProxyTest.QUX;

        public void swap() {
            /* do nothing */
        }
    }

    public static class SwapInherited {
        public static void swap(@FieldProxy(value = MethodDelegationFieldProxyTest.FOO, declaringType = MethodDelegationFieldProxyTest.Explicit.class)
        MethodDelegationFieldProxyTest.Get<String> getter, @FieldProxy(value = MethodDelegationFieldProxyTest.FOO, declaringType = MethodDelegationFieldProxyTest.Explicit.class)
        MethodDelegationFieldProxyTest.Set<String> setter) {
            MatcherAssert.assertThat(getter, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            MatcherAssert.assertThat(setter, CoreMatchers.not(CoreMatchers.instanceOf(Serializable.class)));
            setter.set(((getter.get()) + (MethodDelegationFieldProxyTest.BAR)));
        }
    }

    public static class ExplicitStatic {
        protected static String foo;

        public void swap() {
            /* do nothing */
        }
    }

    public static class ImplicitGetter {
        protected String foo = MethodDelegationFieldProxyTest.FOO;

        public String getFoo() {
            return null;
        }
    }

    public static class GetInterceptor {
        public static String get(@FieldProxy
        MethodDelegationFieldProxyTest.Get<String> getter, @FieldProxy
        MethodDelegationFieldProxyTest.Set<String> setter) {
            setter.set(((getter.get()) + (MethodDelegationFieldProxyTest.BAR)));
            return getter.get();
        }
    }

    public static class SetInterceptor {
        public static void set(@Argument(0)
        String value, @FieldProxy
        MethodDelegationFieldProxyTest.Get<String> getter, @FieldProxy
        MethodDelegationFieldProxyTest.Set<String> setter) {
            setter.set(((getter.get()) + value));
        }
    }

    public static class GetInterceptorDuplex {
        public static String get(@FieldProxy
        MethodDelegationFieldProxyTest.GetSet<String> accessor) {
            accessor.set(((accessor.get()) + (MethodDelegationFieldProxyTest.BAR)));
            return accessor.get();
        }
    }

    public static class SetInterceptorDuplex {
        public static void set(@Argument(0)
        String value, @FieldProxy
        MethodDelegationFieldProxyTest.GetSet<String> accessor) {
            accessor.set(((accessor.get()) + value));
        }
    }

    public static class ImplicitSetter {
        protected String foo = MethodDelegationFieldProxyTest.FOO;

        public void setFoo(String value) {
            /* do nothing */
        }
    }

    public static class FinalField {
        protected final String foo = MethodDelegationFieldProxyTest.FOO;

        public void method() {
            /* do nothing */
        }
    }

    public static class SwapSerializable {
        public static void swap(@FieldProxy(value = MethodDelegationFieldProxyTest.FOO, serializableProxy = true)
        MethodDelegationFieldProxyTest.Get<String> getter, @FieldProxy(value = MethodDelegationFieldProxyTest.FOO, serializableProxy = true)
        MethodDelegationFieldProxyTest.Set<String> setter) {
            MatcherAssert.assertThat(getter, CoreMatchers.instanceOf(Serializable.class));
            MatcherAssert.assertThat(setter, CoreMatchers.instanceOf(Serializable.class));
            setter.set(((getter.get()) + (MethodDelegationFieldProxyTest.BAR)));
        }
    }

    public static class SwapSerializableDuplex {
        public static void swap(@FieldProxy(value = MethodDelegationFieldProxyTest.FOO, serializableProxy = true)
        MethodDelegationFieldProxyTest.GetSet<String> accessor) {
            MatcherAssert.assertThat(accessor, CoreMatchers.instanceOf(Serializable.class));
            accessor.set(((accessor.get()) + (MethodDelegationFieldProxyTest.BAR)));
        }
    }

    public static class GetterIncompatible {
        public static void swap(@FieldProxy(MethodDelegationFieldProxyTest.FOO)
        MethodDelegationFieldProxyTest.Get<Integer> getter, @FieldProxy(MethodDelegationFieldProxyTest.FOO)
        MethodDelegationFieldProxyTest.Set<String> setter) {
            Integer value = getter.get();
        }
    }

    public static class SetterIncompatible {
        public static void swap(@FieldProxy(MethodDelegationFieldProxyTest.FOO)
        MethodDelegationFieldProxyTest.Get<String> getter, @FieldProxy(MethodDelegationFieldProxyTest.FOO)
        MethodDelegationFieldProxyTest.Set<Integer> setter) {
            setter.set(0);
        }
    }

    public static class GetterIncompatibleDuplex {
        public static void swap(@FieldProxy(MethodDelegationFieldProxyTest.FOO)
        MethodDelegationFieldProxyTest.GetSet<Integer> accessor) {
            Integer value = accessor.get();
        }
    }

    public static class SetterIncompatibleDuplex {
        public static void swap(@FieldProxy(MethodDelegationFieldProxyTest.FOO)
        MethodDelegationFieldProxyTest.GetSet<Integer> accessor) {
            accessor.set(0);
        }
    }
}

