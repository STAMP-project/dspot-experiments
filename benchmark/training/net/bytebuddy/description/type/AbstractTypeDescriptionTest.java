package net.bytebuddy.description.type;


import OpenedClassReader.ASM_API;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.GenericSignatureFormatError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.packaging.SimpleType;
import net.bytebuddy.test.scope.EnclosingType;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.visibility.Sample;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.VARIABLE;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.dynamic.loading.ByteArrayClassLoader.PersistenceHandler.MANIFEST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER_PERSISTENT;
import static net.bytebuddy.dynamic.loading.PackageDefinitionStrategy.NoOp.INSTANCE;


public abstract class AbstractTypeDescriptionTest extends AbstractTypeDescriptionGenericVariableDefiningTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private final List<Class<?>> standardTypes;

    @SuppressWarnings({ "unchecked", "deprecation" })
    protected AbstractTypeDescriptionTest() {
        standardTypes = Arrays.asList(Object.class, Object[].class, AbstractTypeDescriptionTest.SampleClass.class, AbstractTypeDescriptionTest.SampleClass[].class, AbstractTypeDescriptionTest.SampleInterface.class, AbstractTypeDescriptionTest.SampleInterface[].class, AbstractTypeDescriptionTest.SampleAnnotation.class, AbstractTypeDescriptionTest.SampleAnnotation[].class, void.class, byte.class, byte[].class, short.class, short[].class, char.class, char[].class, int.class, int[].class, long.class, long[].class, float.class, float[].class, double.class, double[].class, new EnclosingType().localMethod, Array.newInstance(new EnclosingType().localConstructor, 1).getClass(), new EnclosingType().anonymousMethod, Array.newInstance(new EnclosingType().anonymousMethod, 1).getClass(), new EnclosingType().localConstructor, Array.newInstance(new EnclosingType().localConstructor, 1).getClass(), new EnclosingType().anonymousConstructor, Array.newInstance(new EnclosingType().anonymousConstructor, 1).getClass(), EnclosingType.LOCAL_INITIALIZER, Array.newInstance(EnclosingType.LOCAL_INITIALIZER.getClass(), 1).getClass(), EnclosingType.ANONYMOUS_INITIALIZER, Array.newInstance(EnclosingType.ANONYMOUS_INITIALIZER, 1).getClass(), EnclosingType.LOCAL_METHOD, Array.newInstance(EnclosingType.LOCAL_METHOD.getClass(), 1).getClass(), EnclosingType.ANONYMOUS_METHOD, Array.newInstance(EnclosingType.ANONYMOUS_METHOD, 1).getClass(), EnclosingType.INNER, Array.newInstance(EnclosingType.INNER, 1).getClass(), EnclosingType.NESTED, Array.newInstance(EnclosingType.NESTED, 1).getClass(), EnclosingType.PRIVATE_INNER, Array.newInstance(EnclosingType.PRIVATE_INNER, 1).getClass(), EnclosingType.PRIVATE_NESTED, Array.newInstance(EnclosingType.PRIVATE_NESTED, 1).getClass(), EnclosingType.PROTECTED_INNER, Array.newInstance(EnclosingType.PROTECTED_INNER, 1).getClass(), EnclosingType.PROTECTED_NESTED, Array.newInstance(EnclosingType.PROTECTED_NESTED, 1).getClass(), EnclosingType.PACKAGE_INNER, Array.newInstance(EnclosingType.PACKAGE_INNER, 1).getClass(), EnclosingType.PACKAGE_NESTED, Array.newInstance(EnclosingType.PACKAGE_NESTED, 1).getClass(), EnclosingType.FINAL_NESTED, Array.newInstance(EnclosingType.FINAL_NESTED, 1).getClass(), EnclosingType.FINAL_INNER, Array.newInstance(EnclosingType.FINAL_INNER, 1).getClass(), EnclosingType.DEPRECATED, Array.newInstance(EnclosingType.DEPRECATED, 1).getClass(), AbstractTypeDescriptionTest.Type$With$Dollar.class, new ByteBuddy().subclass(Object.class).name("sample.WithoutDefinedPackage").make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT.with(INSTANCE)).getLoaded(), new ByteBuddy().subclass(Object.class).name("WithoutPackage").make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded());
    }

    @Test
    public void testPrecondition() throws Exception {
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.not(describe(AbstractTypeDescriptionTest.SampleInterface.class)));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.not(describe(AbstractTypeDescriptionTest.SampleAnnotation.class)));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.is(describe(AbstractTypeDescriptionTest.SampleClass.class)));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class), CoreMatchers.is(describe(AbstractTypeDescriptionTest.SampleInterface.class)));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleAnnotation.class), CoreMatchers.is(describe(AbstractTypeDescriptionTest.SampleAnnotation.class)));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionTest.SampleClass.class)))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionTest.SampleInterface.class)))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleAnnotation.class), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionTest.SampleAnnotation.class)))));
    }

    @Test
    public void testStackSize() throws Exception {
        MatcherAssert.assertThat(describe(void.class).getStackSize(), CoreMatchers.is(StackSize.ZERO));
        MatcherAssert.assertThat(describe(boolean.class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(describe(byte.class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(describe(short.class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(describe(char.class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(describe(int.class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(describe(long.class).getStackSize(), CoreMatchers.is(StackSize.DOUBLE));
        MatcherAssert.assertThat(describe(float.class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(describe(double.class).getStackSize(), CoreMatchers.is(StackSize.DOUBLE));
        MatcherAssert.assertThat(describe(Object.class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(describe(Object[].class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(describe(long[].class).getStackSize(), CoreMatchers.is(StackSize.SINGLE));
    }

    @Test
    public void testDefaultValue() throws Exception {
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.is(((Object) (false))));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.is(((Object) ((byte) (0)))));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.is(((Object) ((short) (0)))));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.is(((Object) ((char) (0)))));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.is(((Object) (0))));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.is(((Object) (0L))));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.is(((Object) (0.0F))));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.is(((Object) (0.0))));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(getDefaultValue(), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testName() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(describe(type).getName(), CoreMatchers.is(type.getName()));
        }
    }

    @Test
    public void testSourceName() throws Exception {
        for (Class<?> type : standardTypes) {
            if (type.isArray()) {
                MatcherAssert.assertThat(describe(type).getActualName(), CoreMatchers.is(((type.getComponentType().getName()) + "[]")));
            } else {
                MatcherAssert.assertThat(describe(type).getActualName(), CoreMatchers.is(type.getName()));
            }
        }
    }

    @Test
    public void testInternalName() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(describe(type).getInternalName(), CoreMatchers.is(Type.getInternalName(type)));
        }
    }

    @Test
    public void testCanonicalName() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(describe(type).getCanonicalName(), CoreMatchers.is(type.getCanonicalName()));
        }
    }

    @Test
    public void testSimpleName() throws Exception {
        for (Class<?> type : standardTypes) {
            if (type.getName().equals("net.bytebuddy.test.scope.EnclosingType$1Foo"))
                MatcherAssert.assertThat(describe(type).getSimpleName(), CoreMatchers.is(type.getSimpleName()));

        }
    }

    @Test
    public void testIsMemberType() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(isMemberType(), CoreMatchers.is(type.isMemberClass()));
        }
    }

    @Test
    public void testIsAnonymousType() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(isAnonymousType(), CoreMatchers.is(type.isAnonymousClass()));
        }
    }

    @Test
    public void testIsLocalType() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(isLocalType(), CoreMatchers.is(type.isLocalClass()));
        }
    }

    @Test
    public void testJavaName() throws Exception {
        MatcherAssert.assertThat(describe(Object.class).getActualName(), CoreMatchers.is(Object.class.getName()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).getActualName(), CoreMatchers.is(AbstractTypeDescriptionTest.SampleClass.class.getName()));
        MatcherAssert.assertThat(describe(void.class).getActualName(), CoreMatchers.is(void.class.getName()));
        MatcherAssert.assertThat(describe(boolean.class).getActualName(), CoreMatchers.is(boolean.class.getName()));
        MatcherAssert.assertThat(describe(byte.class).getActualName(), CoreMatchers.is(byte.class.getName()));
        MatcherAssert.assertThat(describe(short.class).getActualName(), CoreMatchers.is(short.class.getName()));
        MatcherAssert.assertThat(describe(char.class).getActualName(), CoreMatchers.is(char.class.getName()));
        MatcherAssert.assertThat(describe(int.class).getActualName(), CoreMatchers.is(int.class.getName()));
        MatcherAssert.assertThat(describe(long.class).getActualName(), CoreMatchers.is(long.class.getName()));
        MatcherAssert.assertThat(describe(float.class).getActualName(), CoreMatchers.is(float.class.getName()));
        MatcherAssert.assertThat(describe(double.class).getActualName(), CoreMatchers.is(double.class.getName()));
        MatcherAssert.assertThat(describe(Object[].class).getActualName(), CoreMatchers.is(((Object.class.getName()) + "[]")));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass[].class).getActualName(), CoreMatchers.is(((AbstractTypeDescriptionTest.SampleClass.class.getName()) + "[]")));
        MatcherAssert.assertThat(describe(Object[][].class).getActualName(), CoreMatchers.is(((Object.class.getName()) + "[][]")));
        MatcherAssert.assertThat(describe(boolean[].class).getActualName(), CoreMatchers.is(((boolean.class.getName()) + "[]")));
        MatcherAssert.assertThat(describe(byte[].class).getActualName(), CoreMatchers.is(((byte.class.getName()) + "[]")));
        MatcherAssert.assertThat(describe(short[].class).getActualName(), CoreMatchers.is(((short.class.getName()) + "[]")));
        MatcherAssert.assertThat(describe(char[].class).getActualName(), CoreMatchers.is(((char.class.getName()) + "[]")));
        MatcherAssert.assertThat(describe(int[].class).getActualName(), CoreMatchers.is(((int.class.getName()) + "[]")));
        MatcherAssert.assertThat(describe(long[].class).getActualName(), CoreMatchers.is(((long.class.getName()) + "[]")));
        MatcherAssert.assertThat(describe(float[].class).getActualName(), CoreMatchers.is(((float.class.getName()) + "[]")));
        MatcherAssert.assertThat(describe(double[].class).getActualName(), CoreMatchers.is(((double.class.getName()) + "[]")));
    }

    @Test
    public void testDescriptor() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(describe(type).getDescriptor(), CoreMatchers.is(Type.getDescriptor(type)));
        }
    }

    @Test
    public void testModifiers() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(describe(type).getModifiers(), CoreMatchers.is(type.getModifiers()));
        }
    }

    @Test
    public void testDeclaringType() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(describe(type).getDeclaringType(), ((type.getDeclaringClass()) == null ? CoreMatchers.nullValue(TypeDescription.class) : CoreMatchers.is(((TypeDescription) (of(type.getDeclaringClass()))))));
        }
    }

    @Test
    public void testDeclaredTypes() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(describe(type).getDeclaredTypes(), CoreMatchers.is(((TypeList) (new TypeList.ForLoadedTypes(type.getDeclaredClasses())))));
        }
    }

    @Test
    public void testEnclosingMethod() throws Exception {
        for (Class<?> type : standardTypes) {
            Matcher<MethodDescription> matcher;
            if ((type.getEnclosingMethod()) != null) {
                matcher = CoreMatchers.<MethodDescription>is(new MethodDescription.ForLoadedMethod(type.getEnclosingMethod()));
            } else
                if ((type.getEnclosingConstructor()) != null) {
                    matcher = CoreMatchers.<MethodDescription>is(new MethodDescription.ForLoadedConstructor(type.getEnclosingConstructor()));
                } else {
                    matcher = CoreMatchers.nullValue(MethodDescription.class);
                }

            MatcherAssert.assertThat(describe(type).getEnclosingMethod(), matcher);
        }
    }

    @Test
    public void testEnclosingType() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(describe(type).getEnclosingType(), ((type.getEnclosingClass()) == null ? CoreMatchers.nullValue(TypeDescription.class) : CoreMatchers.is(((TypeDescription) (of(type.getEnclosingClass()))))));
        }
    }

    @Test
    public void testHashCode() throws Exception {
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).hashCode(), CoreMatchers.is(AbstractTypeDescriptionTest.SampleClass.class.getName().hashCode()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).hashCode(), CoreMatchers.is(AbstractTypeDescriptionTest.SampleInterface.class.getName().hashCode()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleAnnotation.class).hashCode(), CoreMatchers.is(AbstractTypeDescriptionTest.SampleAnnotation.class.getName().hashCode()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).hashCode(), CoreMatchers.is(describe(AbstractTypeDescriptionTest.SampleClass.class).hashCode()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).hashCode(), CoreMatchers.not(describe(AbstractTypeDescriptionTest.SampleInterface.class).hashCode()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).hashCode(), CoreMatchers.not(describe(AbstractTypeDescriptionTest.SampleAnnotation.class).hashCode()));
        MatcherAssert.assertThat(describe(Object[].class).hashCode(), CoreMatchers.is(describe(Object[].class).hashCode()));
        MatcherAssert.assertThat(describe(Object[].class).hashCode(), CoreMatchers.not(describe(Object.class).hashCode()));
        MatcherAssert.assertThat(describe(void.class).hashCode(), CoreMatchers.is(void.class.getName().hashCode()));
    }

    @Test
    public void testEquals() throws Exception {
        TypeDescription identical = describe(AbstractTypeDescriptionTest.SampleClass.class);
        MatcherAssert.assertThat(identical, CoreMatchers.is(identical));
        TypeDescription equalFirst = Mockito.mock(TypeDescription.class);
        Mockito.when(equalFirst.getSort()).thenReturn(NON_GENERIC);
        Mockito.when(equalFirst.asErasure()).thenReturn(equalFirst);
        Mockito.when(equalFirst.getName()).thenReturn(AbstractTypeDescriptionTest.SampleClass.class.getName());
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.is(equalFirst));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.not(describe(AbstractTypeDescriptionTest.SampleInterface.class)));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.not(((TypeDescription) (of(AbstractTypeDescriptionTest.SampleInterface.class)))));
        TypeDefinition nonRawType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(nonRawType.getSort()).thenReturn(VARIABLE);
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.not(nonRawType));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.not(CoreMatchers.equalTo(null)));
        MatcherAssert.assertThat(describe(Object[].class), CoreMatchers.is(((TypeDescription) (of(Object[].class)))));
        MatcherAssert.assertThat(describe(Object[].class), CoreMatchers.not(TypeDescription.OBJECT));
    }

    @Test
    public void testIsInstance() throws Exception {
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).isInstance(new AbstractTypeDescriptionTest.SampleClass()), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).isInstance(new Object()), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).isInstance(new AbstractTypeDescriptionTest.SampleInterfaceImplementation()), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object[].class).isInstance(new Object[0]), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object[].class).isInstance(new Object()), CoreMatchers.is(false));
    }

    @Test
    public void testPackage() throws Exception {
        for (Class<?> type : standardTypes) {
            if ((type.isArray()) || (type.isPrimitive())) {
                MatcherAssert.assertThat(describe(type).getPackage(), CoreMatchers.nullValue(PackageDescription.class));
            } else {
                String packageName = type.getName();
                int packageIndex = packageName.lastIndexOf('.');
                MatcherAssert.assertThat(describe(type).getPackage().getName(), CoreMatchers.is((packageIndex == (-1) ? "" : packageName.substring(0, packageIndex))));
            }
        }
    }

    @Test
    public void testActualModifiers() throws Exception {
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).getActualModifiers(true), CoreMatchers.is(((AbstractTypeDescriptionTest.SampleClass.class.getModifiers()) | (Opcodes.ACC_SUPER))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).getActualModifiers(false), CoreMatchers.is(AbstractTypeDescriptionTest.SampleClass.class.getModifiers()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).getActualModifiers(true), CoreMatchers.is(((((AbstractTypeDescriptionTest.SampleInterface.class.getModifiers()) & (~((Opcodes.ACC_PROTECTED) | (Opcodes.ACC_STATIC)))) | (Opcodes.ACC_PUBLIC)) | (Opcodes.ACC_SUPER))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).getActualModifiers(false), CoreMatchers.is((((AbstractTypeDescriptionTest.SampleInterface.class.getModifiers()) & (~((Opcodes.ACC_PROTECTED) | (Opcodes.ACC_STATIC)))) | (Opcodes.ACC_PUBLIC))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleAnnotation.class).getActualModifiers(true), CoreMatchers.is((((AbstractTypeDescriptionTest.SampleAnnotation.class.getModifiers()) & (~((Opcodes.ACC_PRIVATE) | (Opcodes.ACC_STATIC)))) | (Opcodes.ACC_SUPER))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleAnnotation.class).getActualModifiers(false), CoreMatchers.is(((AbstractTypeDescriptionTest.SampleAnnotation.class.getModifiers()) & (~((Opcodes.ACC_PRIVATE) | (Opcodes.ACC_STATIC))))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SamplePackagePrivate.class).getActualModifiers(true), CoreMatchers.is((((AbstractTypeDescriptionTest.SamplePackagePrivate.class.getModifiers()) & (~(Opcodes.ACC_STATIC))) | (Opcodes.ACC_SUPER))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SamplePackagePrivate.class).getActualModifiers(false), CoreMatchers.is(((AbstractTypeDescriptionTest.SamplePackagePrivate.class.getModifiers()) & (~(Opcodes.ACC_STATIC)))));
    }

    @Test
    public void testActualModifiersDeprecation() throws Exception {
        MatcherAssert.assertThat(describe(EnclosingType.DEPRECATED).getActualModifiers(false), CoreMatchers.is(Opcodes.ACC_DEPRECATED));
        MatcherAssert.assertThat(describe(EnclosingType.DEPRECATED).getActualModifiers(true), CoreMatchers.is(((Opcodes.ACC_DEPRECATED) | (Opcodes.ACC_SUPER))));
    }

    @Test
    public void testSuperClass() throws Exception {
        MatcherAssert.assertThat(describe(Object.class).getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleAnnotation.class).getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(describe(void.class).getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleIndirectInterfaceImplementation.class).getSuperClass(), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionTest.SampleInterfaceImplementation.class)))));
        MatcherAssert.assertThat(describe(Object[].class).getSuperClass(), CoreMatchers.is(OBJECT));
    }

    @Test
    public void testInterfaces() throws Exception {
        MatcherAssert.assertThat(describe(Object.class).getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleAnnotation.class).getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(Annotation.class)))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterfaceImplementation.class).getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(AbstractTypeDescriptionTest.SampleInterface.class)))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleIndirectInterfaceImplementation.class).getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.Empty()))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleTransitiveInterfaceImplementation.class).getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(AbstractTypeDescriptionTest.SampleTransitiveInterface.class)))));
        MatcherAssert.assertThat(describe(Object[].class).getInterfaces(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(Cloneable.class, Serializable.class)))));
    }

    @Test
    public void testToString() throws Exception {
        for (Class<?> type : standardTypes) {
            MatcherAssert.assertThat(describe(type).toString(), CoreMatchers.is(type.toString()));
        }
    }

    @Test
    public void testIsAssignable() throws Exception {
        MatcherAssert.assertThat(describe(Object.class).isAssignableTo(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object.class).isAssignableFrom(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object[].class).isAssignableTo(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object.class).isAssignableFrom(Object[].class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object[].class).isAssignableTo(Serializable.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Serializable.class).isAssignableFrom(Object[].class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object[].class).isAssignableTo(Cloneable.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Cloneable.class).isAssignableFrom(Object[].class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(String[].class).isAssignableTo(Object[].class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object[].class).isAssignableFrom(String[].class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object[].class).isAssignableFrom(String[][].class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(String[][].class).isAssignableTo(Object[].class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(String[].class).isAssignableFrom(String[][].class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(String[][].class).isAssignableTo(String[].class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(Cloneable[].class).isAssignableFrom(String[].class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(String[].class).isAssignableTo(Cloneable[].class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionGenericTest.Foo[].class).isAssignableFrom(String[].class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(String[].class).isAssignableTo(AbstractTypeDescriptionGenericTest.Foo[].class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(int.class).isAssignableTo(int.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(int.class).isAssignableFrom(int.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(void.class).isAssignableTo(void.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(void.class).isAssignableFrom(void.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterfaceImplementation.class).isAssignableTo(AbstractTypeDescriptionTest.SampleInterface.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).isAssignableFrom(AbstractTypeDescriptionTest.SampleInterfaceImplementation.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleTransitiveInterfaceImplementation.class).isAssignableTo(AbstractTypeDescriptionTest.SampleInterface.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).isAssignableFrom(AbstractTypeDescriptionTest.SampleTransitiveInterfaceImplementation.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).isAssignableTo(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object.class).isAssignableFrom(AbstractTypeDescriptionTest.SampleInterface.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterfaceImplementation.class).isAssignableTo(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).isAssignableFrom(AbstractTypeDescriptionTest.SampleInterfaceImplementation.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterfaceImplementation.class).isAssignableTo(boolean.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(boolean.class).isAssignableFrom(AbstractTypeDescriptionTest.SampleInterfaceImplementation.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(boolean.class).isAssignableTo(Object.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(Object.class).isAssignableFrom(boolean.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(boolean[].class).isAssignableTo(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object.class).isAssignableFrom(boolean[].class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(boolean[].class).isAssignableTo(Object[].class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(Object[].class).isAssignableFrom(boolean[].class), CoreMatchers.is(false));
    }

    @Test
    public void testIsAssignableClassLoader() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(SimpleType.class), MANIFEST);
        Class<?> otherSimpleType = classLoader.loadClass(SimpleType.class.getName());
        MatcherAssert.assertThat(describe(SimpleType.class).isAssignableFrom(describe(otherSimpleType)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(SimpleType.class).isAssignableTo(describe(otherSimpleType)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object.class).isAssignableFrom(describe(otherSimpleType)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(otherSimpleType).isAssignableTo(describe(Object.class)), CoreMatchers.is(true));
    }

    @Test
    public void testIsInHierarchyWith() throws Exception {
        MatcherAssert.assertThat(isInHierarchyWith(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(isInHierarchyWith(String.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(isInHierarchyWith(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(isInHierarchyWith(Long.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(isInHierarchyWith(int.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(isInHierarchyWith(int.class), CoreMatchers.is(false));
    }

    @Test
    public void testIsVisible() throws Exception {
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).isVisibleTo(of(AbstractTypeDescriptionTest.SampleInterface.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SamplePackagePrivate.class).isVisibleTo(of(AbstractTypeDescriptionTest.SampleClass.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).isVisibleTo(of(AbstractTypeDescriptionTest.SampleClass.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleAnnotation.class).isVisibleTo(of(AbstractTypeDescriptionTest.SampleClass.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SamplePackagePrivate.class).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface.class).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleAnnotation.class).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(int.class).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleInterface[].class).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SamplePackagePrivate[].class).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testAnnotations() throws Exception {
        assertAnnotations(AbstractTypeDescriptionTest.SampleClass.class);
        assertAnnotations(AbstractTypeDescriptionTest.SampleInterface.class);
        assertAnnotations(AbstractTypeDescriptionTest.SampleClassInherited.class);
        assertAnnotations(AbstractTypeDescriptionTest.SampleClassInheritedOverride.class);
    }

    @Test
    public void testComponentType() throws Exception {
        MatcherAssert.assertThat(describe(Object.class).getComponentType(), CoreMatchers.nullValue(Object.class));
        MatcherAssert.assertThat(describe(Object[].class).getComponentType(), CoreMatchers.is(describe(Object.class)));
    }

    @Test
    public void testWrapperType() throws Exception {
        MatcherAssert.assertThat(describe(Object.class).isPrimitiveWrapper(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(Boolean.class).isPrimitiveWrapper(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Byte.class).isPrimitiveWrapper(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Short.class).isPrimitiveWrapper(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Character.class).isPrimitiveWrapper(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Integer.class).isPrimitiveWrapper(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Long.class).isPrimitiveWrapper(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Float.class).isPrimitiveWrapper(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Double.class).isPrimitiveWrapper(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Void.class).isPrimitiveWrapper(), CoreMatchers.is(false));
    }

    @Test
    public void testGenericType() throws Exception {
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleGenericType.class).getTypeVariables(), CoreMatchers.is(of(AbstractTypeDescriptionTest.SampleGenericType.class).getTypeVariables()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleGenericType.class).getSuperClass(), CoreMatchers.is(of(AbstractTypeDescriptionTest.SampleGenericType.class).getSuperClass()));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleGenericType.class).getInterfaces(), CoreMatchers.is(of(AbstractTypeDescriptionTest.SampleGenericType.class).getInterfaces()));
    }

    @Test
    public void testHierarchyIteration() throws Exception {
        Iterator<TypeDefinition> iterator = describe(AbstractTypeDescriptionTest.Traversal.class).iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionTest.Traversal.class)))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (TypeDescription.OBJECT))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void testHierarchyEnds() throws Exception {
        Iterator<TypeDefinition> iterator = describe(Object.class).iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (TypeDescription.OBJECT))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
        iterator.next();
    }

    @Test(expected = GenericSignatureFormatError.class)
    public void testMalformedTypeSignature() throws Exception {
        TypeDescription typeDescription = describe(AbstractTypeDescriptionTest.SignatureMalformer.malform(AbstractTypeDescriptionTest.MalformedBase.class));
        MatcherAssert.assertThat(typeDescription.getInterfaces().size(), CoreMatchers.is(1));
        typeDescription.getInterfaces().getOnly().getSort();
    }

    @Test(expected = GenericSignatureFormatError.class)
    public void testMalformedFieldSignature() throws Exception {
        TypeDescription typeDescription = describe(AbstractTypeDescriptionTest.SignatureMalformer.malform(AbstractTypeDescriptionTest.MalformedBase.class));
        MatcherAssert.assertThat(typeDescription.getDeclaredFields().size(), CoreMatchers.is(1));
        typeDescription.getDeclaredFields().getOnly().getType().getSort();
    }

    @Test(expected = GenericSignatureFormatError.class)
    public void testMalformedMethodSignature() throws Exception {
        TypeDescription typeDescription = describe(AbstractTypeDescriptionTest.SignatureMalformer.malform(AbstractTypeDescriptionTest.MalformedBase.class));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod()).size(), CoreMatchers.is(1));
        typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getReturnType().getSort();
    }

    @Test
    public void testRepresents() throws Exception {
        MatcherAssert.assertThat(describe(Object.class).represents(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object.class).represents(Serializable.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(List.class).represents(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionTest.FOO).getGenericType()), CoreMatchers.is(false));
    }

    @Test
    public void testNonAvailableAnnotations() throws Exception {
        TypeDescription typeDescription = describe(new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(AbstractTypeDescriptionTest.MissingAnnotations.class), MANIFEST).loadClass(AbstractTypeDescriptionTest.MissingAnnotations.class.getName()));
        MatcherAssert.assertThat(typeDescription.getDeclaredAnnotations().isAnnotationPresent(AbstractTypeDescriptionTest.SampleAnnotation.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getDeclaredFields().getOnly().getDeclaredAnnotations().isAnnotationPresent(AbstractTypeDescriptionTest.SampleAnnotation.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getDeclaredAnnotations().isAnnotationPresent(AbstractTypeDescriptionTest.SampleAnnotation.class), CoreMatchers.is(false));
    }

    @Test
    public void testIsPackageDescription() throws Exception {
        MatcherAssert.assertThat(describe(Class.forName((((Sample.class.getPackage().getName()) + ".") + (PackageDescription.PACKAGE_CLASS_NAME)))).isPackageType(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object.class).isPackageType(), CoreMatchers.is(false));
    }

    @Test
    public void testEnclosingSource() throws Exception {
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).getEnclosingSource(), CoreMatchers.is(((TypeVariableSource) (describe(AbstractTypeDescriptionTest.class)))));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.Traversal.class).getEnclosingSource(), CoreMatchers.nullValue(TypeVariableSource.class));
    }

    @Test
    public void testInMethodType() throws Exception {
        MatcherAssert.assertThat(describe(inMethodClass()).getEnclosingMethod(), CoreMatchers.is(((TypeVariableSource) (new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionTest.class.getDeclaredMethod("inMethodClass"))))));
        MatcherAssert.assertThat(describe(inMethodClass()).getEnclosingSource(), CoreMatchers.is(((TypeVariableSource) (new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionTest.class.getDeclaredMethod("inMethodClass"))))));
    }

    @Test
    public void testEnclosingAndDeclaringType() throws Exception {
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).getEnclosingType(), CoreMatchers.is(describe(AbstractTypeDescriptionTest.class)));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.SampleClass.class).getDeclaringType(), CoreMatchers.is(describe(AbstractTypeDescriptionTest.class)));
        Class<?> anonymousType = new Object() {}.getClass();
        MatcherAssert.assertThat(describe(anonymousType).getEnclosingType(), CoreMatchers.is(describe(AbstractTypeDescriptionTest.class)));
        MatcherAssert.assertThat(describe(anonymousType).getDeclaringType(), CoreMatchers.nullValue(TypeDescription.class));
    }

    @Test
    public void testIsGenerified() throws Exception {
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.GenericSample.class).isGenerified(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.GenericSample.Inner.class).isGenerified(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.GenericSample.Nested.class).isGenerified(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractTypeDescriptionTest.GenericSample.NestedInterface.class).isGenerified(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(Object.class).isGenerified(), CoreMatchers.is(false));
    }

    @Test
    public void testInnerClass() throws Exception {
        MatcherAssert.assertThat(isInnerClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(isInnerClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(isInnerClass(), CoreMatchers.is(true));
    }

    @Test
    public void testNestedClass() throws Exception {
        MatcherAssert.assertThat(isNestedClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(isNestedClass(), CoreMatchers.is(true));
        MatcherAssert.assertThat(isNestedClass(), CoreMatchers.is(true));
    }

    @Test
    public void testGetSegmentCount() throws Exception {
        MatcherAssert.assertThat(getInnerClassCount(), CoreMatchers.is(0));
        MatcherAssert.assertThat(getInnerClassCount(), CoreMatchers.is(1));
        MatcherAssert.assertThat(getInnerClassCount(), CoreMatchers.is(0));
        MatcherAssert.assertThat(getInnerClassCount(), CoreMatchers.is(0));
        MatcherAssert.assertThat(getInnerClassCount(), CoreMatchers.is(0));
    }

    @Test
    public void testBoxed() throws Exception {
        MatcherAssert.assertThat(describe(boolean.class).asBoxed(), CoreMatchers.is(describe(Boolean.class)));
        MatcherAssert.assertThat(describe(byte.class).asBoxed(), CoreMatchers.is(describe(Byte.class)));
        MatcherAssert.assertThat(describe(short.class).asBoxed(), CoreMatchers.is(describe(Short.class)));
        MatcherAssert.assertThat(describe(char.class).asBoxed(), CoreMatchers.is(describe(Character.class)));
        MatcherAssert.assertThat(describe(int.class).asBoxed(), CoreMatchers.is(describe(Integer.class)));
        MatcherAssert.assertThat(describe(long.class).asBoxed(), CoreMatchers.is(describe(Long.class)));
        MatcherAssert.assertThat(describe(float.class).asBoxed(), CoreMatchers.is(describe(Float.class)));
        MatcherAssert.assertThat(describe(double.class).asBoxed(), CoreMatchers.is(describe(Double.class)));
        MatcherAssert.assertThat(describe(void.class).asBoxed(), CoreMatchers.is(describe(void.class)));
        MatcherAssert.assertThat(describe(Object.class).asBoxed(), CoreMatchers.is(describe(Object.class)));
    }

    @Test
    public void testUnboxed() throws Exception {
        MatcherAssert.assertThat(describe(Boolean.class).asUnboxed(), CoreMatchers.is(describe(boolean.class)));
        MatcherAssert.assertThat(describe(Byte.class).asUnboxed(), CoreMatchers.is(describe(byte.class)));
        MatcherAssert.assertThat(describe(Short.class).asUnboxed(), CoreMatchers.is(describe(short.class)));
        MatcherAssert.assertThat(describe(Character.class).asUnboxed(), CoreMatchers.is(describe(char.class)));
        MatcherAssert.assertThat(describe(Integer.class).asUnboxed(), CoreMatchers.is(describe(int.class)));
        MatcherAssert.assertThat(describe(Long.class).asUnboxed(), CoreMatchers.is(describe(long.class)));
        MatcherAssert.assertThat(describe(Float.class).asUnboxed(), CoreMatchers.is(describe(float.class)));
        MatcherAssert.assertThat(describe(Double.class).asUnboxed(), CoreMatchers.is(describe(double.class)));
        MatcherAssert.assertThat(describe(Void.class).asUnboxed(), CoreMatchers.is(describe(Void.class)));
        MatcherAssert.assertThat(describe(Object.class).asUnboxed(), CoreMatchers.is(describe(Object.class)));
    }

    @Test
    public void testNestMatesTrivial() throws Exception {
        MatcherAssert.assertThat(getNestHost(), CoreMatchers.is(TypeDescription.OBJECT));
        MatcherAssert.assertThat(getNestMembers().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(getNestMembers(), CoreMatchers.hasItem(TypeDescription.OBJECT));
        MatcherAssert.assertThat(isNestMateOf(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(Object.class).isNestMateOf(TypeDescription.STRING), CoreMatchers.is(false));
        MatcherAssert.assertThat(isNestHost(), CoreMatchers.is(true));
    }

    @Test
    @JavaVersionRule.Enforce(value = 11, target = AbstractTypeDescriptionTest.SampleClass.class)
    public void testNestMatesSupported() throws Exception {
        MatcherAssert.assertThat(getNestHost(), CoreMatchers.is(describe(AbstractTypeDescriptionTest.class)));
        MatcherAssert.assertThat(getNestMembers(), CoreMatchers.hasItems(describe(AbstractTypeDescriptionTest.SampleClass.class), describe(AbstractTypeDescriptionTest.class)));
        MatcherAssert.assertThat(isNestHost(), CoreMatchers.is(false));
        MatcherAssert.assertThat(getNestHost(), CoreMatchers.is(describe(AbstractTypeDescriptionTest.class)));
        MatcherAssert.assertThat(getNestMembers(), CoreMatchers.hasItems(describe(AbstractTypeDescriptionTest.SampleClass.class), describe(AbstractTypeDescriptionTest.class)));
        MatcherAssert.assertThat(isNestHost(), CoreMatchers.is(true));
        MatcherAssert.assertThat(isNestMateOf(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(isNestMateOf(AbstractTypeDescriptionTest.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(isNestMateOf(AbstractTypeDescriptionTest.SampleClass.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(isNestMateOf(Object.class), CoreMatchers.is(false));
        MatcherAssert.assertThat(isNestMateOf(AbstractTypeDescriptionTest.class), CoreMatchers.is(false));
    }

    @Test
    public void testNonEnclosedAnonymousType() throws Exception {
        ClassWriter classWriter = new ClassWriter(0);
        classWriter.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, "foo/Bar", null, "java/lang/Object", null);
        classWriter.visitInnerClass("foo/Bar", null, null, Opcodes.ACC_PUBLIC);
        classWriter.visitEnd();
        ClassLoader classLoader = new ByteArrayClassLoader(null, Collections.singletonMap("foo.Bar", classWriter.toByteArray()), MANIFEST);
        Class<?> type = classLoader.loadClass("foo.Bar");
        MatcherAssert.assertThat(isAnonymousType(), CoreMatchers.is(type.isAnonymousClass()));
        MatcherAssert.assertThat(isLocalType(), CoreMatchers.is(type.isLocalClass()));
        MatcherAssert.assertThat(isMemberType(), CoreMatchers.is(type.isMemberClass()));
    }

    /* empty */
    protected interface SampleInterface {}

    @Retention(RetentionPolicy.RUNTIME)
    private @interface SampleAnnotation {}

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    private @interface OtherAnnotation {
        String value();
    }

    /* empty */
    public interface SampleTransitiveInterface extends AbstractTypeDescriptionTest.SampleInterface {}

    private static class SignatureMalformer extends ClassVisitor {
        private static final String FOO = "foo";

        public SignatureMalformer(ClassVisitor classVisitor) {
            super(ASM_API, classVisitor);
        }

        public static Class<?> malform(Class<?> type) throws Exception {
            ClassReader classReader = new ClassReader(type.getName());
            ClassWriter classWriter = new ClassWriter(classReader, 0);
            classReader.accept(new AbstractTypeDescriptionTest.SignatureMalformer(classWriter), 0);
            ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, Collections.singletonMap(type.getName(), classWriter.toByteArray()), MANIFEST);
            return classLoader.loadClass(type.getName());
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, AbstractTypeDescriptionTest.SignatureMalformer.FOO, superName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return super.visitField(access, name, desc, AbstractTypeDescriptionTest.SignatureMalformer.FOO, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return super.visitMethod(access, name, desc, AbstractTypeDescriptionTest.SignatureMalformer.FOO, exceptions);
        }
    }

    @SuppressWarnings("unused")
    public abstract static class MalformedBase<T> implements Callable<T> {
        Callable<T> foo;

        abstract Callable<T> foo();
    }

    /* empty */
    static class SamplePackagePrivate {}

    /* empty */
    public static class SampleInterfaceImplementation implements AbstractTypeDescriptionTest.SampleInterface {}

    /* empty */
    public static class SampleIndirectInterfaceImplementation extends AbstractTypeDescriptionTest.SampleInterfaceImplementation {}

    /* empty */
    public static class SampleTransitiveInterfaceImplementation implements AbstractTypeDescriptionTest.SampleTransitiveInterface {}

    public static class SampleGenericType<T extends ArrayList<T> & Callable<T>, S extends Callable<?>, U extends Callable<? extends Callable<U>>, V extends ArrayList<? super ArrayList<V>>, W extends Callable<W[]>> extends ArrayList<T> implements Callable<T> {
        public T call() throws Exception {
            return null;
        }
    }

    /* empty */
    public static class Traversal {}

    /* empty */
    @AbstractTypeDescriptionTest.SampleAnnotation
    @AbstractTypeDescriptionTest.OtherAnnotation(AbstractTypeDescriptionTest.FOO)
    public class SampleClass {}

    /* empty */
    public class SampleClassInherited extends AbstractTypeDescriptionTest.SampleClass {}

    /* empty */
    @AbstractTypeDescriptionTest.OtherAnnotation(AbstractTypeDescriptionTest.BAR)
    public class SampleClassInheritedOverride extends AbstractTypeDescriptionTest.SampleClass {}

    @SuppressWarnings("unused")
    public static class InnerInnerClass {
        /* empty */
        public static class Foo {}
    }

    @AbstractTypeDescriptionTest.SampleAnnotation
    public static class MissingAnnotations {
        @AbstractTypeDescriptionTest.SampleAnnotation
        Void foo;

        @AbstractTypeDescriptionTest.SampleAnnotation
        void foo(@AbstractTypeDescriptionTest.SampleAnnotation
        Void foo) {
            /* empty */
        }
    }

    private static class GenericSample<T> {
        /* empty */
        static class Nested {}

        /* empty */
        class Inner {}

        /* empty */
        interface NestedInterface {}
    }

    /*  */
    private static class Type$With$Dollar {}
}

