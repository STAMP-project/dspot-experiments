package net.bytebuddy.dynamic.scaffold;


import java.io.File;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.MethodManifestation;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.OpenedClassReader;
import org.hamcrest.CoreMatchers;
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

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.dynamic.loading.ByteArrayClassLoader.PersistenceHandler.MANIFEST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.loading.InjectionClassLoader.Strategy.INSTANCE;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.NO_CONSTRUCTORS;
import static net.bytebuddy.utility.JavaConstant.MethodHandle.of;


public class TypeWriterDefaultTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String LEGACY_INTERFACE = "net.bytebuddy.test.precompiled.LegacyInterface";

    private static final String JAVA_8_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test(expected = IllegalStateException.class)
    public void testConstructorOnInterfaceAssertion() throws Exception {
        new ByteBuddy().makeInterface().defineConstructor(Visibility.PUBLIC).intercept(SuperMethodCall.INSTANCE).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorOnAnnotationAssertion() throws Exception {
        new ByteBuddy().makeAnnotation().defineConstructor(Visibility.PUBLIC).intercept(SuperMethodCall.INSTANCE).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAbstractConstructorAssertion() throws Exception {
        new ByteBuddy().subclass(Object.class, NO_CONSTRUCTORS).defineConstructor(Visibility.PUBLIC).withoutCode().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticAbstractMethodAssertion() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, void.class, Ownership.STATIC).withoutCode().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPrivateAbstractMethodAssertion() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, void.class, Visibility.PRIVATE).withoutCode().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAbstractMethodOnNonAbstractClassAssertion() throws Exception {
        new ByteBuddy().subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, String.class).withoutCode().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonPublicFieldOnInterfaceAssertion() throws Exception {
        new ByteBuddy().makeInterface().defineField(TypeWriterDefaultTest.FOO, String.class, Ownership.STATIC, FieldManifestation.FINAL).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonPublicFieldOnAnnotationAssertion() throws Exception {
        new ByteBuddy().makeAnnotation().defineField(TypeWriterDefaultTest.FOO, String.class, Ownership.STATIC, FieldManifestation.FINAL).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonStaticFieldOnInterfaceAssertion() throws Exception {
        new ByteBuddy().makeInterface().defineField(TypeWriterDefaultTest.FOO, String.class, Visibility.PUBLIC, FieldManifestation.FINAL).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonStaticFieldOnAnnotationAssertion() throws Exception {
        new ByteBuddy().makeAnnotation().defineField(TypeWriterDefaultTest.FOO, String.class, Visibility.PUBLIC, FieldManifestation.FINAL).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonFinalFieldOnInterfaceAssertion() throws Exception {
        new ByteBuddy().makeInterface().defineField(TypeWriterDefaultTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonFinalFieldOnAnnotationAssertion() throws Exception {
        new ByteBuddy().makeAnnotation().defineField(TypeWriterDefaultTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticFieldWithIncompatibleConstantValue() throws Exception {
        new ByteBuddy().subclass(Object.class).defineField(TypeWriterDefaultTest.FOO, String.class, Ownership.STATIC).value(0).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStaticFieldWithNullConstantValue() throws Exception {
        new ByteBuddy().subclass(Object.class).defineField(TypeWriterDefaultTest.FOO, String.class, Ownership.STATIC).value(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticNumericFieldWithIncompatibleConstantValue() throws Exception {
        new ByteBuddy().subclass(Object.class).defineField(TypeWriterDefaultTest.FOO, boolean.class, Ownership.STATIC).value(Integer.MAX_VALUE).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticFieldWithNonDefinableConstantValue() throws Exception {
        new ByteBuddy().subclass(Object.class).defineField(TypeWriterDefaultTest.FOO, Object.class, Ownership.STATIC).value(TypeWriterDefaultTest.FOO).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonPublicMethodOnInterfaceAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V6).makeInterface().defineMethod(TypeWriterDefaultTest.FOO, void.class).withoutCode().make();
    }

    @Test
    public void testNonPublicMethodOnInterfaceAssertionJava8() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V8).makeInterface().defineMethod(TypeWriterDefaultTest.FOO, void.class).withoutCode().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonPublicMethodOnAnnotationAssertion() throws Exception {
        new ByteBuddy().makeAnnotation().defineMethod(TypeWriterDefaultTest.FOO, void.class).withoutCode().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticMethodOnInterfaceAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V6).makeInterface().defineMethod(TypeWriterDefaultTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).withoutCode().make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testStaticMethodOnAnnotationAssertionJava8() throws Exception {
        new ByteBuddy().makeInterface().defineMethod(TypeWriterDefaultTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).intercept(StubMethod.INSTANCE).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticMethodOnAnnotationAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V6).makeAnnotation().defineMethod(TypeWriterDefaultTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).intercept(StubMethod.INSTANCE).make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testStaticMethodOnInterfaceAssertionJava8() throws Exception {
        new ByteBuddy().makeAnnotation().defineMethod(TypeWriterDefaultTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).intercept(StubMethod.INSTANCE).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationDefaultValueOnClassAssertion() throws Exception {
        new ByteBuddy().subclass(Object.class).merge(TypeManifestation.ABSTRACT).defineMethod(TypeWriterDefaultTest.FOO, String.class).defaultValue(TypeWriterDefaultTest.BAR, String.class).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationDefaultValueOnInterfaceClassAssertion() throws Exception {
        new ByteBuddy().makeInterface().defineMethod(TypeWriterDefaultTest.FOO, String.class).defaultValue(TypeWriterDefaultTest.BAR, String.class).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationPropertyWithVoidReturnAssertion() throws Exception {
        new ByteBuddy().makeAnnotation().defineMethod(TypeWriterDefaultTest.FOO, void.class, Visibility.PUBLIC).withoutCode().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationPropertyWithParametersAssertion() throws Exception {
        new ByteBuddy().makeAnnotation().defineMethod(TypeWriterDefaultTest.FOO, String.class, Visibility.PUBLIC).withParameters(Void.class).withoutCode().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageDescriptionWithModifiers() throws Exception {
        new ByteBuddy().makePackage(TypeWriterDefaultTest.FOO).modifiers(Visibility.PRIVATE).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageDescriptionWithInterfaces() throws Exception {
        new ByteBuddy().makePackage(TypeWriterDefaultTest.FOO).implement(Serializable.class).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageDescriptionWithField() throws Exception {
        new ByteBuddy().makePackage(TypeWriterDefaultTest.FOO).defineField(TypeWriterDefaultTest.FOO, Void.class).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageDescriptionWithMethod() throws Exception {
        new ByteBuddy().makePackage(TypeWriterDefaultTest.FOO).defineMethod(TypeWriterDefaultTest.FOO, void.class).withoutCode().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationPreJava5TypeAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4).makeAnnotation().make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationOnTypePreJava5TypeAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4).subclass(Object.class).annotateType(ofType(TypeWriterDefaultTest.Foo.class).build()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationOnFieldPreJava5TypeAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4).subclass(Object.class).defineField(TypeWriterDefaultTest.FOO, Void.class).annotateField(ofType(TypeWriterDefaultTest.Foo.class).build()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationOnMethodPreJava5TypeAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4).subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, void.class).intercept(StubMethod.INSTANCE).annotateMethod(ofType(TypeWriterDefaultTest.Foo.class).build()).make();
    }

    @Test
    public void testTypeInitializerOnInterface() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().makeInterface().invokable(ElementMatchers.isTypeInitializer()).intercept(StubMethod.INSTANCE).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded(), CoreMatchers.notNullValue(Class.class));
    }

    @Test
    public void testTypeInitializerOnAnnotation() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().makeAnnotation().invokable(ElementMatchers.isTypeInitializer()).intercept(StubMethod.INSTANCE).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded(), CoreMatchers.notNullValue(Class.class));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testTypeInitializerOnRebasedModernInterface() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().rebase(Class.forName(TypeWriterDefaultTest.JAVA_8_INTERFACE)).invokable(ElementMatchers.isTypeInitializer()).intercept(StubMethod.INSTANCE).make(), CoreMatchers.notNullValue(DynamicType.class));
    }

    @Test
    public void testTypeInitializerOnRebasedLegacyInterface() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().rebase(Class.forName(TypeWriterDefaultTest.LEGACY_INTERFACE)).invokable(ElementMatchers.isTypeInitializer()).intercept(StubMethod.INSTANCE).make(), CoreMatchers.notNullValue(DynamicType.class));
    }

    @Test
    public void testTypeInitializerOnRebasedInterfaceWithFrameComputation() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().makeInterface().visit(new AsmVisitorWrapper.ForDeclaredMethods().writerFlags(ClassWriter.COMPUTE_FRAMES)).invokable(ElementMatchers.isTypeInitializer()).intercept(StubMethod.INSTANCE).make(), CoreMatchers.notNullValue(DynamicType.class));
    }

    @Test
    public void testTypeInitializerOnRebasedInterfaceWithFrameExpansion() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().makeInterface().visit(new AsmVisitorWrapper.ForDeclaredMethods().readerFlags(ClassReader.EXPAND_FRAMES)).invokable(ElementMatchers.isTypeInitializer()).intercept(StubMethod.INSTANCE).make(), CoreMatchers.notNullValue(DynamicType.class));
    }

    @Test
    public void testTypeInitializerOnRebasedInterfaceWithInitializer() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().makeInterface().initializer(new ByteCodeAppender.Simple()).invokable(ElementMatchers.isTypeInitializer()).intercept(StubMethod.INSTANCE).make(), CoreMatchers.notNullValue(DynamicType.class));
    }

    @Test
    public void testTypeInLegacyConstantPoolRemapped() throws Exception {
        Class<?> dynamicType = new ByteBuddy(ClassFileVersion.JAVA_V4).with(TypeValidation.DISABLED).subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, Object.class, Visibility.PUBLIC).intercept(FixedValue.value(Object.class)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(TypeWriterDefaultTest.FOO).invoke(dynamicType.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (Object.class))));
    }

    @Test
    public void testArrayTypeInLegacyConstantPoolRemapped() throws Exception {
        Class<?> dynamicType = new ByteBuddy(ClassFileVersion.JAVA_V4).with(TypeValidation.DISABLED).subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, Object.class, Visibility.PUBLIC).intercept(FixedValue.value(Object[].class)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(TypeWriterDefaultTest.FOO).invoke(dynamicType.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (Object[].class))));
    }

    @Test
    public void testPrimitiveTypeInLegacyConstantPoolRemapped() throws Exception {
        Class<?> dynamicType = new ByteBuddy(ClassFileVersion.JAVA_V4).with(TypeValidation.DISABLED).subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, Object.class, Visibility.PUBLIC).intercept(FixedValue.value(int.class)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(TypeWriterDefaultTest.FOO).invoke(dynamicType.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (int.class))));
    }

    @Test
    public void testLegacyTypeRedefinitionIsDiscovered() throws Exception {
        Class<?> dynamicType = new ByteBuddy().with(TypeValidation.DISABLED).redefine(Class.forName("net.bytebuddy.test.precompiled.TypeConstantSample")).method(ElementMatchers.named(TypeWriterDefaultTest.BAR)).intercept(FixedValue.value(int.class)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod(TypeWriterDefaultTest.BAR).invoke(null), CoreMatchers.is(((Object) (int.class))));
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodTypeInLegacyConstantPool() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4).subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, Object.class).intercept(FixedValue.value(JavaConstant.MethodType.of(Object.class, Object.class))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testDynamicConstantInPre11ConstantPool() throws Exception {
        new ByteBuddy(JAVA_V10).subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, Object.class).intercept(FixedValue.value(Dynamic.ofNullConstant())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodHandleInLegacyConstantPool() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4).subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, Object.class).intercept(FixedValue.value(of(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))))).make();
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(8)
    public void testDefaultMethodCallFromLegacyType() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V7).subclass(Class.forName("net.bytebuddy.test.precompiled.SingleDefaultMethodInterface")).method(ElementMatchers.isDefaultMethod()).intercept(SuperMethodCall.INSTANCE).make();
    }

    @Test
    public void testBridgeNonLegacyType() throws Exception {
        Class<?> base = new ByteBuddy(ClassFileVersion.JAVA_V5).subclass(Object.class).modifiers(Visibility.PACKAGE_PRIVATE).defineMethod("foo", void.class, Visibility.PUBLIC).intercept(StubMethod.INSTANCE).defineMethod("bar", Object.class).intercept(StubMethod.INSTANCE).defineMethod("bar", String.class).intercept(StubMethod.INSTANCE).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, opened()).getLoaded();
        Class<?> subclass = new ByteBuddy(ClassFileVersion.JAVA_V5).subclass(base).modifiers(Visibility.PUBLIC).method(ElementMatchers.named("bar")).intercept(StubMethod.INSTANCE).make().load(((InjectionClassLoader) (base.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(subclass.getDeclaredMethods().length, CoreMatchers.is(3));
        MatcherAssert.assertThat(subclass.getDeclaredMethod("foo").isBridge(), CoreMatchers.is(true));
        MatcherAssert.assertThat(subclass.getDeclaredMethod("bar").isBridge(), CoreMatchers.is(false));
        MatcherAssert.assertThat(subclass.getDeclaredMethod("bar").getReturnType(), CoreMatchers.is(((Object) (String.class))));
    }

    @Test
    public void testNoBridgeLegacyType() throws Exception {
        Class<?> base = new ByteBuddy(ClassFileVersion.JAVA_V4).subclass(Object.class, NO_CONSTRUCTORS).modifiers(Visibility.PACKAGE_PRIVATE).defineConstructor(Visibility.PUBLIC).intercept(SuperMethodCall.INSTANCE).defineMethod(TypeWriterDefaultTest.FOO, void.class, Visibility.PUBLIC).intercept(StubMethod.INSTANCE).defineMethod(TypeWriterDefaultTest.BAR, Object.class).intercept(StubMethod.INSTANCE).defineMethod(TypeWriterDefaultTest.BAR, String.class).intercept(StubMethod.INSTANCE).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, opened()).getLoaded();
        Class<?> subclass = new ByteBuddy(ClassFileVersion.JAVA_V4).subclass(base).modifiers(Visibility.PUBLIC).method(ElementMatchers.named(TypeWriterDefaultTest.BAR)).intercept(StubMethod.INSTANCE).make().load(((InjectionClassLoader) (base.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(subclass.getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(subclass.getDeclaredMethod(TypeWriterDefaultTest.BAR).isBridge(), CoreMatchers.is(false));
        MatcherAssert.assertThat(subclass.getDeclaredMethod(TypeWriterDefaultTest.BAR).getReturnType(), CoreMatchers.is(((Object) (String.class))));
    }

    @Test
    public void testIncompatibleBridgeMethodIsFiltered() throws Exception {
        Class<?> base = new ByteBuddy().subclass(Object.class).defineMethod(TypeWriterDefaultTest.FOO, Object.class, Visibility.PUBLIC).intercept(StubMethod.INSTANCE).defineMethod(TypeWriterDefaultTest.FOO, void.class, Visibility.PUBLIC, MethodManifestation.BRIDGE).intercept(StubMethod.INSTANCE).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, opened()).getLoaded();
        Class<?> subclass = new ByteBuddy().subclass(base).method(ElementMatchers.named(TypeWriterDefaultTest.FOO)).intercept(StubMethod.INSTANCE).make().load(((InjectionClassLoader) (base.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(subclass.getDeclaredMethods().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(subclass.getDeclaredMethod(TypeWriterDefaultTest.FOO).isBridge(), CoreMatchers.is(false));
        MatcherAssert.assertThat(subclass.getDeclaredMethod(TypeWriterDefaultTest.FOO).getReturnType(), CoreMatchers.is(((Object) (Object.class))));
    }

    @Test
    public void testClassDump() throws Exception {
        TypeDescription instrumentedType = Mockito.mock(TypeDescription.class);
        byte[] binaryRepresentation = new byte[]{ 1, 2, 3 };
        File file = File.createTempFile(TypeWriterDefaultTest.FOO, TypeWriterDefaultTest.BAR);
        MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
        file = new File(file.getParentFile(), ("temp" + (System.currentTimeMillis())));
        MatcherAssert.assertThat(file.mkdir(), CoreMatchers.is(true));
        Mockito.when(instrumentedType.getName()).thenReturn((((TypeWriterDefaultTest.FOO) + ".") + (TypeWriterDefaultTest.BAR)));
        dump(file.getAbsolutePath(), instrumentedType, false, binaryRepresentation);
        File[] child = file.listFiles();
        MatcherAssert.assertThat(child, CoreMatchers.notNullValue(File[].class));
        MatcherAssert.assertThat(child.length, CoreMatchers.is(1));
        MatcherAssert.assertThat(child[0].length(), CoreMatchers.is(3L));
        MatcherAssert.assertThat(child[0].delete(), CoreMatchers.is(true));
        MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
    }

    @Test
    public void testClassDumpOriginal() throws Exception {
        TypeDescription instrumentedType = Mockito.mock(TypeDescription.class);
        byte[] binaryRepresentation = new byte[]{ 1, 2, 3 };
        File file = File.createTempFile(TypeWriterDefaultTest.FOO, TypeWriterDefaultTest.BAR);
        MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
        file = new File(file.getParentFile(), ("temp" + (System.currentTimeMillis())));
        MatcherAssert.assertThat(file.mkdir(), CoreMatchers.is(true));
        Mockito.when(instrumentedType.getName()).thenReturn((((TypeWriterDefaultTest.FOO) + ".") + (TypeWriterDefaultTest.BAR)));
        dump(file.getAbsolutePath(), instrumentedType, true, binaryRepresentation);
        File[] child = file.listFiles();
        MatcherAssert.assertThat(child, CoreMatchers.notNullValue(File[].class));
        MatcherAssert.assertThat(child.length, CoreMatchers.is(1));
        MatcherAssert.assertThat(child[0].length(), CoreMatchers.is(3L));
        MatcherAssert.assertThat(child[0].delete(), CoreMatchers.is(true));
        MatcherAssert.assertThat(file.delete(), CoreMatchers.is(true));
    }

    @Test
    public void testPropertyDefinition() throws Exception {
        Class<?> bean = new ByteBuddy().subclass(Object.class).defineProperty(TypeWriterDefaultTest.FOO, Object.class).defineProperty(TypeWriterDefaultTest.BAR, boolean.class).defineProperty(((TypeWriterDefaultTest.FOO) + (TypeWriterDefaultTest.BAR)), String.class, true).make().load(null, WRAPPER).getLoaded();
        MatcherAssert.assertThat(bean.getDeclaredMethods().length, CoreMatchers.is(5));
        MatcherAssert.assertThat(bean.getMethod("getFoo").getReturnType(), CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(bean.getMethod("setFoo", Object.class).getReturnType(), CoreMatchers.is(((Object) (void.class))));
        MatcherAssert.assertThat(bean.getMethod("isBar").getReturnType(), CoreMatchers.is(((Object) (boolean.class))));
        MatcherAssert.assertThat(bean.getMethod("setBar", boolean.class).getReturnType(), CoreMatchers.is(((Object) (void.class))));
        MatcherAssert.assertThat(bean.getMethod("getFoobar").getReturnType(), CoreMatchers.is(((Object) (String.class))));
        MatcherAssert.assertThat(bean.getDeclaredFields().length, CoreMatchers.is(3));
        MatcherAssert.assertThat(bean.getDeclaredField(TypeWriterDefaultTest.FOO).getType(), CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(bean.getDeclaredField(TypeWriterDefaultTest.BAR).getType(), CoreMatchers.is(((Object) (boolean.class))));
        MatcherAssert.assertThat(bean.getDeclaredField(((TypeWriterDefaultTest.FOO) + (TypeWriterDefaultTest.BAR))).getType(), CoreMatchers.is(((Object) (String.class))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyDefinitionVoidType() throws Exception {
        new ByteBuddy().subclass(Object.class).defineProperty(TypeWriterDefaultTest.FOO, void.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPropertyDefinitionEmptyName() throws Exception {
        new ByteBuddy().subclass(Object.class).defineProperty("", Object.class);
    }

    @Test
    public void testOldJavaClassFileDeprecation() {
        ClassWriter classWriter = new ClassWriter(0);
        classWriter.visit(Opcodes.V1_4, ((Opcodes.ACC_DEPRECATED) | (Opcodes.ACC_ABSTRACT)), "foo/Bar", null, "java/lang/Object", null);
        classWriter.visitField(Opcodes.ACC_DEPRECATED, "qux", "Ljava/lang/Object;", null, null).visitEnd();
        classWriter.visitMethod(((Opcodes.ACC_DEPRECATED) | (Opcodes.ACC_ABSTRACT)), "baz", "()V", null, null).visitEnd();
        classWriter.visitEnd();
        TypeDescription typeDescription = new TypeDescription.Latent("foo.Bar", 0, OBJECT);
        Class<?> type = ByteArrayClassLoader.load(ClassLoadingStrategy.BOOTSTRAP_LOADER, Collections.singletonMap(typeDescription, classWriter.toByteArray()), ClassLoadingStrategy.NO_PROTECTION_DOMAIN, MANIFEST, PackageDefinitionStrategy.Trivial.INSTANCE, false, true).get(typeDescription);
        byte[] binaryRepresentation = new ByteBuddy().redefine(type).field(ElementMatchers.isDeclaredBy(type)).annotateField(new Annotation[0]).method(ElementMatchers.isDeclaredBy(type)).withoutCode().make().getBytes();
        ClassReader classReader = new ClassReader(binaryRepresentation);
        classReader.accept(new ClassVisitor(OpenedClassReader.ASM_API) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                if ((access & (Opcodes.ACC_DEPRECATED)) == 0) {
                    throw new AssertionError();
                }
                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                if ((access & (Opcodes.ACC_DEPRECATED)) == 0) {
                    throw new AssertionError();
                }
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if ((access & (Opcodes.ACC_DEPRECATED)) == 0) {
                    throw new AssertionError();
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        }, 0);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Foo {}
}

