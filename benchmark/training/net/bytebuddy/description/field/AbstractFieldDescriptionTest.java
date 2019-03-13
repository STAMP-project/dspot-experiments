package net.bytebuddy.description.field;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.packaging.FieldDescriptionTestHelper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;


public abstract class AbstractFieldDescriptionTest {
    private Field first;

    private Field second;

    private Field genericField;

    @Test
    public void testPrecondition() throws Exception {
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(describe(second)));
        MatcherAssert.assertThat(describe(first), CoreMatchers.is(describe(first)));
        MatcherAssert.assertThat(describe(second), CoreMatchers.is(describe(second)));
        MatcherAssert.assertThat(describe(first), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(first)))));
        MatcherAssert.assertThat(describe(second), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(second)))));
    }

    @Test
    public void testFieldType() throws Exception {
        MatcherAssert.assertThat(describe(first).getType(), CoreMatchers.is(((TypeDefinition) (of(first.getType())))));
        MatcherAssert.assertThat(describe(second).getType(), CoreMatchers.is(((TypeDefinition) (of(second.getType())))));
    }

    @Test
    public void testFieldName() throws Exception {
        MatcherAssert.assertThat(describe(first).getName(), CoreMatchers.is(first.getName()));
        MatcherAssert.assertThat(describe(second).getName(), CoreMatchers.is(second.getName()));
        MatcherAssert.assertThat(describe(first).getInternalName(), CoreMatchers.is(first.getName()));
        MatcherAssert.assertThat(describe(second).getInternalName(), CoreMatchers.is(second.getName()));
    }

    @Test
    public void testDescriptor() throws Exception {
        MatcherAssert.assertThat(describe(first).getDescriptor(), CoreMatchers.is(Type.getDescriptor(first.getType())));
        MatcherAssert.assertThat(describe(second).getDescriptor(), CoreMatchers.is(Type.getDescriptor(second.getType())));
    }

    @Test
    public void testFieldModifier() throws Exception {
        MatcherAssert.assertThat(describe(first).getModifiers(), CoreMatchers.is(first.getModifiers()));
        MatcherAssert.assertThat(describe(second).getModifiers(), CoreMatchers.is(second.getModifiers()));
    }

    @Test
    public void testFieldDeclaringType() throws Exception {
        MatcherAssert.assertThat(describe(first).getDeclaringType(), CoreMatchers.is(((TypeDescription) (of(first.getDeclaringClass())))));
        MatcherAssert.assertThat(describe(second).getDeclaringType(), CoreMatchers.is(((TypeDescription) (of(second.getDeclaringClass())))));
    }

    @Test
    public void testHashCode() throws Exception {
        MatcherAssert.assertThat(describe(first).hashCode(), CoreMatchers.is(((of(AbstractFieldDescriptionTest.FirstSample.class).hashCode()) + (31 * (17 + (first.getName().hashCode()))))));
        MatcherAssert.assertThat(describe(second).hashCode(), CoreMatchers.is(((of(AbstractFieldDescriptionTest.SecondSample.class).hashCode()) + (31 * (17 + (second.getName().hashCode()))))));
        MatcherAssert.assertThat(describe(first).hashCode(), CoreMatchers.is(describe(first).hashCode()));
        MatcherAssert.assertThat(describe(second).hashCode(), CoreMatchers.is(describe(second).hashCode()));
        MatcherAssert.assertThat(describe(first).hashCode(), CoreMatchers.not(describe(second).hashCode()));
    }

    @Test
    public void testEquals() throws Exception {
        FieldDescription identical = describe(first);
        MatcherAssert.assertThat(identical, CoreMatchers.is(identical));
        FieldDescription equalFirst = Mockito.mock(FieldDescription.class);
        Mockito.when(equalFirst.getName()).thenReturn(first.getName());
        Mockito.when(equalFirst.getDeclaringType()).thenReturn(of(AbstractFieldDescriptionTest.FirstSample.class));
        MatcherAssert.assertThat(describe(first), CoreMatchers.is(equalFirst));
        FieldDescription equalSecond = Mockito.mock(FieldDescription.class);
        Mockito.when(equalSecond.getName()).thenReturn(second.getName());
        Mockito.when(equalSecond.getDeclaringType()).thenReturn(of(AbstractFieldDescriptionTest.SecondSample.class));
        MatcherAssert.assertThat(describe(second), CoreMatchers.is(equalSecond));
        FieldDescription equalFirstTypeOnly = Mockito.mock(FieldDescription.class);
        Mockito.when(equalFirstTypeOnly.getName()).thenReturn(second.getName());
        Mockito.when(equalFirstTypeOnly.getDeclaringType()).thenReturn(of(AbstractFieldDescriptionTest.FirstSample.class));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(equalFirstTypeOnly));
        FieldDescription equalFirstNameOnly = Mockito.mock(FieldDescription.class);
        Mockito.when(equalFirstNameOnly.getName()).thenReturn(first.getName());
        Mockito.when(equalFirstNameOnly.getDeclaringType()).thenReturn(of(AbstractFieldDescriptionTest.SecondSample.class));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(equalFirstNameOnly));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(equalSecond));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(describe(first), CoreMatchers.not(CoreMatchers.equalTo(null)));
    }

    @Test
    public void testToString() throws Exception {
        MatcherAssert.assertThat(describe(first).toString(), CoreMatchers.is(first.toString()));
        MatcherAssert.assertThat(describe(second).toString(), CoreMatchers.is(second.toString()));
    }

    @Test
    public void testSynthetic() throws Exception {
        MatcherAssert.assertThat(describe(first).isSynthetic(), CoreMatchers.is(first.isSynthetic()));
        MatcherAssert.assertThat(describe(second).isSynthetic(), CoreMatchers.is(second.isSynthetic()));
    }

    @Test
    public void testTransient() throws Exception {
        MatcherAssert.assertThat(describe(first).isTransient(), CoreMatchers.is(Modifier.isTransient(first.getModifiers())));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.TransientSample.class.getDeclaredField("foo")).isTransient(), CoreMatchers.is(Modifier.isTransient(AbstractFieldDescriptionTest.TransientSample.class.getDeclaredField("foo").getModifiers())));
    }

    @Test
    public void testIsVisibleTo() throws Exception {
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("publicField")).isVisibleTo(of(AbstractFieldDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("protectedField")).isVisibleTo(of(AbstractFieldDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("packagePrivateField")).isVisibleTo(of(AbstractFieldDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("privateField")).isVisibleTo(of(AbstractFieldDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("publicField")).isVisibleTo(of(AbstractFieldDescriptionTest.FirstSample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("protectedField")).isVisibleTo(of(AbstractFieldDescriptionTest.FirstSample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("packagePrivateField")).isVisibleTo(of(AbstractFieldDescriptionTest.FirstSample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("privateField")).isVisibleTo(of(AbstractFieldDescriptionTest.FirstSample.class)), CoreMatchers.is(ClassFileVersion.of(AbstractFieldDescriptionTest.FirstSample.class).isAtLeast(JAVA_V11)));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("publicField")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("protectedField")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("packagePrivateField")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("privateField")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("publicField")).isVisibleTo(of(FieldDescriptionTestHelper.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("protectedField")).isVisibleTo(of(FieldDescriptionTestHelper.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("packagePrivateField")).isVisibleTo(of(FieldDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PublicType.class.getDeclaredField("privateField")).isVisibleTo(of(FieldDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PackagePrivateType.class.getDeclaredField("publicField")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PackagePrivateType.class.getDeclaredField("protectedField")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PackagePrivateType.class.getDeclaredField("packagePrivateField")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PackagePrivateType.class.getDeclaredField("privateField")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PackagePrivateFieldType.class.getDeclaredField("packagePrivateType")).isVisibleTo(of(AbstractFieldDescriptionTest.PackagePrivateFieldType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.PackagePrivateFieldType.class.getDeclaredField("packagePrivateType")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
    }

    @Test
    public void testAnnotations() throws Exception {
        MatcherAssert.assertThat(describe(first).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
        MatcherAssert.assertThat(describe(second).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.ForLoadedAnnotations(second.getDeclaredAnnotations())))));
    }

    @Test
    public void testGenericTypes() throws Exception {
        MatcherAssert.assertThat(describe(genericField).getType(), CoreMatchers.is(describe(genericField.getGenericType())));
        MatcherAssert.assertThat(describe(genericField).getType().asErasure(), CoreMatchers.is(((TypeDescription) (of(genericField.getType())))));
    }

    @Test
    public void testToGenericString() throws Exception {
        MatcherAssert.assertThat(describe(genericField).toGenericString(), CoreMatchers.is(genericField.toGenericString()));
    }

    @Test
    public void testGetActualModifiers() throws Exception {
        MatcherAssert.assertThat(describe(first).getActualModifiers(), CoreMatchers.is(first.getModifiers()));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.DeprecationSample.class.getDeclaredField("foo")).getActualModifiers(), CoreMatchers.is(((Opcodes.ACC_DEPRECATED) | (Opcodes.ACC_PRIVATE))));
    }

    @Test
    public void testSyntheticField() throws Exception {
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.SyntheticField.class.getDeclaredFields()[0]).getModifiers(), CoreMatchers.is(AbstractFieldDescriptionTest.SyntheticField.class.getDeclaredFields()[0].getModifiers()));
        MatcherAssert.assertThat(describe(AbstractFieldDescriptionTest.SyntheticField.class.getDeclaredFields()[0]).isSynthetic(), CoreMatchers.is(AbstractFieldDescriptionTest.SyntheticField.class.getDeclaredFields()[0].isSynthetic()));
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface SampleAnnotation {}

    @SuppressWarnings("unused")
    protected static class FirstSample {
        private Void first;
    }

    @SuppressWarnings("unused")
    protected static class SecondSample {
        @AbstractFieldDescriptionTest.SampleAnnotation
        int second;
    }

    @SuppressWarnings("unused")
    public static class PublicType {
        public Void publicField;

        protected Void protectedField;

        Void packagePrivateField;

        private Void privateField;
    }

    @SuppressWarnings("unused")
    static class PackagePrivateType {
        public Void publicField;

        protected Void protectedField;

        Void packagePrivateField;

        private Void privateField;
    }

    @SuppressWarnings("unused")
    static class GenericField {
        List<String> foo;
    }

    public static class PackagePrivateFieldType {
        public AbstractFieldDescriptionTest.PackagePrivateType packagePrivateType;
    }

    private static class DeprecationSample {
        @Deprecated
        private Void foo;
    }

    /* empty */
    private class SyntheticField {}

    private static class TransientSample {
        public transient Void foo;
    }
}

