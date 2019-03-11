package net.bytebuddy.description.type;


import java.util.ArrayList;
import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class GenericSignatureResolutionTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testGenericType() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.GenericType.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.GenericType.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.is(originalType.getSuperClass()));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    @Test
    public void testGenericField() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.GenericField.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        FieldDescription createdField = new FieldDescription.ForLoadedField(type.getDeclaredField(GenericSignatureResolutionTest.FOO));
        FieldDescription originalField = new FieldDescription.ForLoadedField(GenericSignatureResolutionTest.GenericField.class.getDeclaredField(GenericSignatureResolutionTest.FOO));
        MatcherAssert.assertThat(createdField.getType(), CoreMatchers.is(originalField.getType()));
    }

    @Test
    public void testGenericMethod() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.GenericMethod.class).method(ElementMatchers.named(GenericSignatureResolutionTest.FOO)).intercept(FixedValue.nullValue()).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MethodDescription createdMethod = new MethodDescription.ForLoadedMethod(type.getDeclaredMethod(GenericSignatureResolutionTest.FOO, Exception.class));
        MethodDescription originalMethod = new MethodDescription.ForLoadedMethod(GenericSignatureResolutionTest.GenericMethod.class.getDeclaredMethod(GenericSignatureResolutionTest.FOO, Exception.class));
        MatcherAssert.assertThat(createdMethod.getTypeVariables(), CoreMatchers.is(originalMethod.getTypeVariables()));
        MatcherAssert.assertThat(createdMethod.getReturnType(), CoreMatchers.is(originalMethod.getReturnType()));
        MatcherAssert.assertThat(createdMethod.getParameters().getOnly().getType(), CoreMatchers.is(originalMethod.getParameters().getOnly().getType()));
        MatcherAssert.assertThat(createdMethod.getExceptionTypes().getOnly(), CoreMatchers.is(originalMethod.getExceptionTypes().getOnly()));
    }

    @Test
    public void testGenericMethodWithoutGenericExceptionTypes() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.GenericMethod.class).method(ElementMatchers.named(GenericSignatureResolutionTest.BAR)).intercept(FixedValue.nullValue()).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MethodDescription createdMethod = new MethodDescription.ForLoadedMethod(type.getDeclaredMethod(GenericSignatureResolutionTest.BAR, Object.class));
        MethodDescription originalMethod = new MethodDescription.ForLoadedMethod(GenericSignatureResolutionTest.GenericMethod.class.getDeclaredMethod(GenericSignatureResolutionTest.BAR, Object.class));
        MatcherAssert.assertThat(createdMethod.getTypeVariables(), CoreMatchers.is(originalMethod.getTypeVariables()));
        MatcherAssert.assertThat(createdMethod.getReturnType(), CoreMatchers.is(originalMethod.getReturnType()));
        MatcherAssert.assertThat(createdMethod.getParameters().getOnly().getType(), CoreMatchers.is(originalMethod.getParameters().getOnly().getType()));
        MatcherAssert.assertThat(createdMethod.getExceptionTypes().getOnly(), CoreMatchers.is(originalMethod.getExceptionTypes().getOnly()));
    }

    @Test
    public void testNoSuperClass() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().redefine(Object.class).make(), CoreMatchers.notNullValue(DynamicType.class));
    }

    @Test
    public void testTypeVariableClassBound() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.TypeVariableClassBound.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.TypeVariableClassBound.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.is(originalType.getSuperClass()));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    @Test
    public void testTypeVariableInterfaceBound() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.TypeVariableInterfaceBound.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.TypeVariableInterfaceBound.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.is(originalType.getSuperClass()));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    @Test
    public void testTypeVariableClassAndInterfaceBound() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.TypeVariableClassAndInterfaceBound.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.TypeVariableClassAndInterfaceBound.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.is(originalType.getSuperClass()));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    @Test
    public void testTypeVariableWildcardNoBound() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.TypeVariableWildcardNoBound.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.TypeVariableWildcardNoBound.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.is(originalType.getSuperClass()));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    @Test
    public void testTypeVariableWildcardUpperClassBound() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.TypeVariableWildcardUpperClassBound.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.TypeVariableWildcardUpperClassBound.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.is(originalType.getSuperClass()));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    @Test
    public void testTypeVariableWildcardUpperInterfaceBound() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.TypeVariableWildcardUpperInterfaceBound.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.TypeVariableWildcardUpperInterfaceBound.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.is(originalType.getSuperClass()));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    @Test
    public void testTypeVariableWildcardLowerClassBound() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.TypeVariableWildcardLowerClassBound.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.TypeVariableWildcardLowerClassBound.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.is(originalType.getSuperClass()));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    @Test
    public void testTypeVariableWildcardLowerInterfaceBound() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.TypeVariableWildcardLowerInterfaceBound.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.TypeVariableWildcardLowerInterfaceBound.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.is(originalType.getSuperClass()));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    @Test
    public void testInterfaceType() throws Exception {
        DynamicType.Unloaded<?> unloaded = new ByteBuddy().redefine(GenericSignatureResolutionTest.InterfaceType.class).make();
        Class<?> type = unloaded.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        TypeDescription createdType = of(type);
        TypeDescription originalType = of(GenericSignatureResolutionTest.InterfaceType.class);
        MatcherAssert.assertThat(createdType.getTypeVariables(), CoreMatchers.is(originalType.getTypeVariables()));
        MatcherAssert.assertThat(createdType.getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(createdType.getInterfaces(), CoreMatchers.is(originalType.getInterfaces()));
    }

    public abstract static class GenericType<T extends ArrayList<T> & Callable<T>, S extends Callable<?>, U extends Callable<? extends Callable<U>>, V extends ArrayList<? super ArrayList<V>>, W extends Callable<W[]>> extends ArrayList<T> implements Callable<T> {}

    @SuppressWarnings("unused")
    public static class GenericMethod {
        <T extends Exception & Callable<T>> T foo(T arg) throws T {
            return null;
        }

        <T> T bar(T arg) throws Exception {
            return null;
        }
    }

    public static class GenericField<T> {
        T foo;
    }

    /* empty */
    public static class TypeVariableClassBound<T extends ArrayList<T>> {}

    /* empty */
    public abstract static class TypeVariableInterfaceBound<T extends Callable<T>> {}

    /* empty */
    public abstract static class TypeVariableClassAndInterfaceBound<T extends ArrayList<T> & Callable<T>> {}

    /* empty */
    public static class TypeVariableWildcardNoBound<T extends ArrayList<?>> {}

    /* empty */
    public static class TypeVariableWildcardUpperClassBound<T extends ArrayList<? extends ArrayList<T>>> {}

    /* empty */
    public static class TypeVariableWildcardUpperInterfaceBound<T extends ArrayList<? extends Callable<T>>> {}

    /* empty */
    public static class TypeVariableWildcardLowerClassBound<T extends ArrayList<? super ArrayList<T>>> {}

    /* empty */
    public static class TypeVariableWildcardLowerInterfaceBound<T extends ArrayList<? super Callable<T>>> {}

    /* empty */
    public interface InterfaceType<T> extends Callable<T> {}
}

