package net.bytebuddy.pool;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.AbstractTypeDescriptionTest;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.scaffold.InstrumentedType.Factory.Default.FROZEN;
import static net.bytebuddy.dynamic.scaffold.InstrumentedType.Factory.Default.MODIFIABLE;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Empty.INSTANCE;


public class TypePoolDefaultWithLazyResolutionTypeDescriptionTest extends AbstractTypeDescriptionTest {
    @Test
    public void testTypeIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        TypePool typePool = TypePool.Default.WithLazyResolution.of(classFileLocator);
        TypePool.Resolution resolution = typePool.describe(Object.class.getName());
        MatcherAssert.assertThat(resolution.resolve().getName(), CoreMatchers.is(TypeDescription.OBJECT.getName()));
        Mockito.verifyZeroInteractions(classFileLocator);
    }

    @Test
    public void testReferencedTypeIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        TypePool typePool = TypePool.Default.WithLazyResolution.of(classFileLocator);
        TypePool.Resolution resolution = typePool.describe(String.class.getName());
        MatcherAssert.assertThat(resolution.resolve().getName(), CoreMatchers.is(TypeDescription.STRING.getName()));
        MatcherAssert.assertThat(resolution.resolve().getSuperClass().asErasure().getName(), CoreMatchers.is(TypeDescription.OBJECT.getName()));
        Mockito.verify(classFileLocator).locate(String.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testTypeIsCached() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        TypePool typePool = TypePool.Default.WithLazyResolution.of(classFileLocator);
        TypePool.Resolution resolution = typePool.describe(Object.class.getName());
        MatcherAssert.assertThat(resolution.resolve().getModifiers(), CoreMatchers.is(TypeDescription.OBJECT.getModifiers()));
        MatcherAssert.assertThat(resolution.resolve().getInterfaces(), CoreMatchers.is(TypeDescription.OBJECT.getInterfaces()));
        MatcherAssert.assertThat(typePool.describe(Object.class.getName()).resolve(), CoreMatchers.is(resolution.resolve()));
        Mockito.verify(classFileLocator).locate(Object.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testReferencedTypeIsCached() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        TypePool typePool = TypePool.Default.WithLazyResolution.of(classFileLocator);
        TypePool.Resolution resolution = typePool.describe(String.class.getName());
        MatcherAssert.assertThat(resolution.resolve().getModifiers(), CoreMatchers.is(TypeDescription.STRING.getModifiers()));
        TypeDescription superClass = resolution.resolve().getSuperClass().asErasure();
        MatcherAssert.assertThat(superClass, CoreMatchers.is(TypeDescription.OBJECT));
        MatcherAssert.assertThat(superClass.getModifiers(), CoreMatchers.is(TypeDescription.OBJECT.getModifiers()));
        MatcherAssert.assertThat(superClass.getInterfaces(), CoreMatchers.is(TypeDescription.OBJECT.getInterfaces()));
        MatcherAssert.assertThat(typePool.describe(String.class.getName()).resolve(), CoreMatchers.is(resolution.resolve()));
        Mockito.verify(classFileLocator).locate(String.class.getName());
        Mockito.verify(classFileLocator).locate(Object.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testNonGenericResolutionIsLazyForSimpleCreationNonFrozen() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        new ByteBuddy().with(TypeValidation.DISABLED).with(INSTANCE).with(MODIFIABLE).redefine(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()), classFileLocator).make();
        Mockito.verify(classFileLocator, Mockito.times(2)).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testNonGenericResolutionIsLazyForSimpleCreation() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        new ByteBuddy().with(TypeValidation.DISABLED).with(INSTANCE).with(FROZEN).redefine(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()), classFileLocator).make();
        Mockito.verify(classFileLocator, Mockito.times(2)).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testGenericResolutionIsLazyForSimpleCreation() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        new ByteBuddy().with(TypeValidation.DISABLED).with(INSTANCE).with(FROZEN).redefine(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()), classFileLocator).make();
        Mockito.verify(classFileLocator, Mockito.times(2)).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testNonGenericSuperClassHierarchyResolutionIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        MatcherAssert.assertThat(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()).getSuperClass().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleClass.class)))));
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testNonGenericSuperClassNavigatedHierarchyResolutionIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        MatcherAssert.assertThat(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()).getSuperClass().getSuperClass().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SuperClass.class)))));
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class.getName());
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleClass.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testNonGenericSuperInterfaceHierarchyResolutionIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        MatcherAssert.assertThat(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()).getInterfaces().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleInterface.class)))));
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testNonGenericSuperInterfaceNavigatedHierarchyResolutionIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        MatcherAssert.assertThat(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()).getInterfaces().getOnly().getInterfaces().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SuperInterface.class)))));
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.NonGenericType.class.getName());
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleInterface.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testGenericSuperClassHierarchyResolutionIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        MatcherAssert.assertThat(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()).getSuperClass().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleGenericClass.class)))));
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testGenericSuperClassNavigatedHierarchyResolutionIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        MatcherAssert.assertThat(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()).getSuperClass().getSuperClass().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SuperClass.class)))));
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class.getName());
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleGenericClass.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testGenericSuperInterfaceHierarchyResolutionIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        MatcherAssert.assertThat(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()).getInterfaces().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleGenericInterface.class)))));
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    @Test
    public void testGenericSuperInterfaceNavigatedHierarchyResolutionIsLazy() throws Exception {
        ClassFileLocator classFileLocator = Mockito.spy(ofSystemLoader());
        MatcherAssert.assertThat(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.describe(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class, classFileLocator, new TypePool.CacheProvider.Simple()).getInterfaces().getOnly().getInterfaces().getOnly().asErasure(), CoreMatchers.is(((TypeDescription) (of(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SuperInterface.class)))));
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.GenericType.class.getName());
        Mockito.verify(classFileLocator).locate(TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleGenericInterface.class.getName());
        Mockito.verifyNoMoreInteractions(classFileLocator);
    }

    /* empty */
    private static class SuperClass {}

    /* empty */
    private interface SuperInterface {}

    /* empty */
    private static class SampleClass extends TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SuperClass {}

    /* empty */
    private interface SampleInterface extends TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SuperInterface {}

    private static class NonGenericType extends TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleClass implements TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleInterface {
        Object foo;

        Object foo(Object argument) throws Exception {
            return argument;
        }
    }

    /* empty */
    private static class SampleGenericClass<T> extends TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SuperClass {}

    /* empty */
    private interface SampleGenericInterface<T> extends TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SuperInterface {}

    private static class GenericType<T extends Exception> extends TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleGenericClass<T> implements TypePoolDefaultWithLazyResolutionTypeDescriptionTest.SampleGenericInterface<T> {
        T foo;

        T foo(T argument) throws T {
            return argument;
        }
    }
}

