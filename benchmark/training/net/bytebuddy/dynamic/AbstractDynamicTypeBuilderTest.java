package net.bytebuddy.dynamic;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.MethodManifestation;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.ProvisioningState;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.ExceptionMethod;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.CallTraceable;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.utility.OpenedClassReader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;
import static net.bytebuddy.description.type.TypeDescription.Generic.AnnotationReader.DISPATCHER;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.parameterizedType;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.rawType;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.unboundWildcard;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.UNDEFINED;
import static net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional.Valuable.withHashCodeEquals;
import static net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional.Valuable.withToString;
import static net.bytebuddy.dynamic.DynamicType.Builder.declaredTypes;
import static net.bytebuddy.dynamic.DynamicType.Builder.innerTypeOf;
import static net.bytebuddy.dynamic.DynamicType.Builder.nestHost;
import static net.bytebuddy.dynamic.DynamicType.Builder.nestMembers;
import static net.bytebuddy.dynamic.DynamicType.Builder.require;
import static net.bytebuddy.dynamic.DynamicType.Builder.topLevelType;
import static net.bytebuddy.dynamic.Transformer.ForField.withModifiers;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.loading.InjectionClassLoader.Strategy.INSTANCE;


public abstract class AbstractDynamicTypeBuilderTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String TO_STRING = "toString";

    private static final String TYPE_VARIABLE_NAME = "net.bytebuddy.test.precompiled.TypeAnnotation";

    private static final String VALUE = "value";

    private static final int MODIFIERS = Opcodes.ACC_PUBLIC;

    private static final boolean BOOLEAN_VALUE = true;

    private static final int INTEGER_VALUE = 42;

    private static final long LONG_VALUE = 42L;

    private static final float FLOAT_VALUE = 42.0F;

    private static final double DOUBLE_VALUE = 42.0;

    private static final String BOOLEAN_FIELD = "booleanField";

    private static final String BYTE_FIELD = "byteField";

    private static final String CHARACTER_FIELD = "characterField";

    private static final String SHORT_FIELD = "shortField";

    private static final String INTEGER_FIELD = "integerField";

    private static final String LONG_FIELD = "longField";

    private static final String FLOAT_FIELD = "floatField";

    private static final String DOUBLE_FIELD = "doubleField";

    private static final String STRING_FIELD = "stringField";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    private Type list;

    private Type fooVariable;

    @Test
    public void testMethodDefinition() throws Exception {
        Class<?> type = createPlain().defineMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class, Visibility.PUBLIC).throwing(Exception.class).intercept(new Implementation.Simple(new TextConstant(AbstractDynamicTypeBuilderTest.FOO), MethodReturn.REFERENCE)).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        Method method = type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(method.getReturnType(), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(method.getExceptionTypes(), CoreMatchers.is(new Class<?>[]{ Exception.class }));
        MatcherAssert.assertThat(method.getModifiers(), CoreMatchers.is(Modifier.PUBLIC));
        MatcherAssert.assertThat(method.invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testAbstractMethodDefinition() throws Exception {
        Class<?> type = createPlain().modifiers(Visibility.PUBLIC, TypeManifestation.ABSTRACT).defineMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class, Visibility.PUBLIC).throwing(Exception.class).withoutCode().make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        Method method = type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(method.getReturnType(), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(method.getExceptionTypes(), CoreMatchers.is(new Class<?>[]{ Exception.class }));
        MatcherAssert.assertThat(method.getModifiers(), CoreMatchers.is(((Modifier.PUBLIC) | (Modifier.ABSTRACT))));
    }

    @Test
    public void testConstructorDefinition() throws Exception {
        Class<?> type = createPlain().defineConstructor(Visibility.PUBLIC).withParameters(Void.class).throwing(Exception.class).intercept(MethodCall.invoke(Object.class.getDeclaredConstructor())).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        Constructor<?> constructor = type.getDeclaredConstructor(Void.class);
        MatcherAssert.assertThat(constructor.getExceptionTypes(), CoreMatchers.is(new Class<?>[]{ Exception.class }));
        MatcherAssert.assertThat(constructor.getModifiers(), CoreMatchers.is(Modifier.PUBLIC));
        MatcherAssert.assertThat(constructor.newInstance(((Object) (null))), CoreMatchers.notNullValue(Object.class));
    }

    @Test
    public void testFieldDefinition() throws Exception {
        Class<?> type = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, Void.class, Visibility.PUBLIC).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        Field field = type.getDeclaredField(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(field.getType(), CoreMatchers.<Class<?>>is(Void.class));
        MatcherAssert.assertThat(field.getModifiers(), CoreMatchers.is(Modifier.PUBLIC));
    }

    @Test
    public void testFieldDefaultValueDefinition() throws Exception {
        Class<?> type = createPlain().defineField(AbstractDynamicTypeBuilderTest.BOOLEAN_FIELD, boolean.class, Visibility.PUBLIC, Ownership.STATIC).value(AbstractDynamicTypeBuilderTest.BOOLEAN_VALUE).defineField(AbstractDynamicTypeBuilderTest.BYTE_FIELD, byte.class, Visibility.PUBLIC, Ownership.STATIC).value(AbstractDynamicTypeBuilderTest.INTEGER_VALUE).defineField(AbstractDynamicTypeBuilderTest.SHORT_FIELD, short.class, Visibility.PUBLIC, Ownership.STATIC).value(AbstractDynamicTypeBuilderTest.INTEGER_VALUE).defineField(AbstractDynamicTypeBuilderTest.CHARACTER_FIELD, char.class, Visibility.PUBLIC, Ownership.STATIC).value(AbstractDynamicTypeBuilderTest.INTEGER_VALUE).defineField(AbstractDynamicTypeBuilderTest.INTEGER_FIELD, int.class, Visibility.PUBLIC, Ownership.STATIC).value(AbstractDynamicTypeBuilderTest.INTEGER_VALUE).defineField(AbstractDynamicTypeBuilderTest.LONG_FIELD, long.class, Visibility.PUBLIC, Ownership.STATIC).value(AbstractDynamicTypeBuilderTest.LONG_VALUE).defineField(AbstractDynamicTypeBuilderTest.FLOAT_FIELD, float.class, Visibility.PUBLIC, Ownership.STATIC).value(AbstractDynamicTypeBuilderTest.FLOAT_VALUE).defineField(AbstractDynamicTypeBuilderTest.DOUBLE_FIELD, double.class, Visibility.PUBLIC, Ownership.STATIC).value(AbstractDynamicTypeBuilderTest.DOUBLE_VALUE).defineField(AbstractDynamicTypeBuilderTest.STRING_FIELD, String.class, Visibility.PUBLIC, Ownership.STATIC).value(AbstractDynamicTypeBuilderTest.FOO).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.BOOLEAN_FIELD).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.BOOLEAN_VALUE))));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.BYTE_FIELD).get(null), CoreMatchers.is(((Object) ((byte) (AbstractDynamicTypeBuilderTest.INTEGER_VALUE)))));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.SHORT_FIELD).get(null), CoreMatchers.is(((Object) ((short) (AbstractDynamicTypeBuilderTest.INTEGER_VALUE)))));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.CHARACTER_FIELD).get(null), CoreMatchers.is(((Object) ((char) (AbstractDynamicTypeBuilderTest.INTEGER_VALUE)))));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.INTEGER_FIELD).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.INTEGER_VALUE))));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.LONG_FIELD).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.LONG_VALUE))));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.FLOAT_FIELD).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FLOAT_VALUE))));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.DOUBLE_FIELD).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.DOUBLE_VALUE))));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.STRING_FIELD).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testApplicationOrder() throws Exception {
        MatcherAssert.assertThat(createPlain().method(ElementMatchers.named(AbstractDynamicTypeBuilderTest.TO_STRING)).intercept(new Implementation.Simple(new TextConstant(AbstractDynamicTypeBuilderTest.FOO), MethodReturn.REFERENCE)).method(ElementMatchers.named(AbstractDynamicTypeBuilderTest.TO_STRING)).intercept(new Implementation.Simple(new TextConstant(AbstractDynamicTypeBuilderTest.BAR), MethodReturn.REFERENCE)).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().toString(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.BAR));
    }

    @Test
    public void testTypeInitializer() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(AbstractDynamicTypeBuilderTest.Bar.class));
        Class<?> type = createPlain().invokable(ElementMatchers.isTypeInitializer()).intercept(MethodCall.invoke(AbstractDynamicTypeBuilderTest.Bar.class.getDeclaredMethod("invoke"))).make().load(classLoader, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.notNullValue(Object.class));
        Class<?> foo = classLoader.loadClass(AbstractDynamicTypeBuilderTest.Bar.class.getName());
        MatcherAssert.assertThat(foo.getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testConstructorInvokingMethod() throws Exception {
        Class<?> type = createPlain().defineMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class, Visibility.PUBLIC).intercept(new Implementation.Simple(new TextConstant(AbstractDynamicTypeBuilderTest.FOO), MethodReturn.REFERENCE)).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        Method method = type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(method.invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testMethodTransformation() throws Exception {
        Class<?> type = createPlain().method(ElementMatchers.named(AbstractDynamicTypeBuilderTest.TO_STRING)).intercept(new Implementation.Simple(new TextConstant(AbstractDynamicTypeBuilderTest.FOO), MethodReturn.REFERENCE)).transform(Transformer.ForMethod.withModifiers(MethodManifestation.FINAL)).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance().toString(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.TO_STRING).getModifiers(), CoreMatchers.is(((Opcodes.ACC_FINAL) | (Opcodes.ACC_PUBLIC))));
    }

    @Test
    public void testFieldTransformation() throws Exception {
        Class<?> type = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, Void.class).field(ElementMatchers.named(AbstractDynamicTypeBuilderTest.FOO)).transform(withModifiers(Visibility.PUBLIC)).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
    }

    @Test
    public void testIgnoredMethod() throws Exception {
        Class<?> type = createPlain().ignoreAlso(ElementMatchers.named(AbstractDynamicTypeBuilderTest.TO_STRING)).method(ElementMatchers.named(AbstractDynamicTypeBuilderTest.TO_STRING)).intercept(new Implementation.Simple(new TextConstant(AbstractDynamicTypeBuilderTest.FOO), MethodReturn.REFERENCE)).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance().toString(), CoreMatchers.not(AbstractDynamicTypeBuilderTest.FOO));
    }

    @Test
    public void testIgnoredMethodDoesNotApplyForDefined() throws Exception {
        Class<?> type = createPlain().ignoreAlso(ElementMatchers.named(AbstractDynamicTypeBuilderTest.FOO)).defineMethod(AbstractDynamicTypeBuilderTest.FOO, String.class, Visibility.PUBLIC).intercept(new Implementation.Simple(new TextConstant(AbstractDynamicTypeBuilderTest.FOO), MethodReturn.REFERENCE)).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testPreparedField() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(AbstractDynamicTypeBuilderTest.SampleAnnotation.class));
        Class<?> type = createPlain().defineMethod(AbstractDynamicTypeBuilderTest.BAR, String.class, Visibility.PUBLIC).intercept(new AbstractDynamicTypeBuilderTest.PreparedField()).make().load(classLoader, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredFields().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).getType(), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).getModifiers(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.MODIFIERS));
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).getAnnotations().length, CoreMatchers.is(1));
        Annotation annotation = type.getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).getAnnotations()[0];
        MatcherAssert.assertThat(annotation.annotationType().getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.SampleAnnotation.class.getName()));
        Method foo = annotation.annotationType().getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(foo.invoke(annotation), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.BAR))));
    }

    @Test
    public void testPreparedMethod() throws Exception {
        ClassLoader classLoader = new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(AbstractDynamicTypeBuilderTest.SampleAnnotation.class));
        Class<?> type = createPlain().defineMethod(AbstractDynamicTypeBuilderTest.BAR, String.class, Visibility.PUBLIC).intercept(new AbstractDynamicTypeBuilderTest.PreparedMethod()).make().load(classLoader, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethods().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class).getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class).getReturnType(), CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class).getParameterTypes().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class).getParameterTypes()[0], CoreMatchers.<Class<?>>is(Object.class));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class).getModifiers(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.MODIFIERS));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class).getAnnotations().length, CoreMatchers.is(1));
        Annotation methodAnnotation = type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class).getAnnotations()[0];
        MatcherAssert.assertThat(methodAnnotation.annotationType().getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.SampleAnnotation.class.getName()));
        Method methodMethod = methodAnnotation.annotationType().getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(methodMethod.invoke(methodAnnotation), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.BAR))));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class).getParameterAnnotations()[0].length, CoreMatchers.is(1));
        Annotation parameterAnnotation = type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class).getParameterAnnotations()[0][0];
        MatcherAssert.assertThat(parameterAnnotation.annotationType().getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.SampleAnnotation.class.getName()));
        Method parameterMethod = parameterAnnotation.annotationType().getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(parameterMethod.invoke(parameterAnnotation), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.QUX))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWriterHint() throws Exception {
        AsmVisitorWrapper asmVisitorWrapper = Mockito.mock(AsmVisitorWrapper.class);
        Mockito.when(asmVisitorWrapper.wrap(ArgumentMatchers.any(TypeDescription.class), ArgumentMatchers.any(ClassVisitor.class), ArgumentMatchers.any(Implementation.Context.class), ArgumentMatchers.any(TypePool.class), ArgumentMatchers.any(FieldList.class), ArgumentMatchers.any(MethodList.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).then(new Answer<ClassVisitor>() {
            public ClassVisitor answer(InvocationOnMock invocationOnMock) throws Throwable {
                return new ClassVisitor(OpenedClassReader.ASM_API, ((ClassVisitor) (invocationOnMock.getArguments()[1]))) {
                    @Override
                    public void visitEnd() {
                        MethodVisitor mv = visitMethod(Opcodes.ACC_PUBLIC, AbstractDynamicTypeBuilderTest.FOO, "()Ljava/lang/String;", null, null);
                        mv.visitCode();
                        mv.visitLdcInsn(AbstractDynamicTypeBuilderTest.FOO);
                        mv.visitInsn(Opcodes.ARETURN);
                        mv.visitMaxs((-1), (-1));
                        mv.visitEnd();
                    }
                };
            }
        });
        Mockito.when(asmVisitorWrapper.mergeWriter(0)).thenReturn(ClassWriter.COMPUTE_MAXS);
        Class<?> type = createPlain().visit(asmVisitorWrapper).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FOO))));
        Mockito.verify(asmVisitorWrapper).mergeWriter(0);
        Mockito.verify(asmVisitorWrapper, Mockito.atMost(1)).mergeReader(0);
        Mockito.verify(asmVisitorWrapper).wrap(ArgumentMatchers.any(TypeDescription.class), ArgumentMatchers.any(ClassVisitor.class), ArgumentMatchers.any(Implementation.Context.class), ArgumentMatchers.any(TypePool.class), ArgumentMatchers.any(FieldList.class), ArgumentMatchers.any(MethodList.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verifyNoMoreInteractions(asmVisitorWrapper);
    }

    @Test
    public void testExplicitTypeInitializer() throws Exception {
        MatcherAssert.assertThat(createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, String.class, Ownership.STATIC, Visibility.PUBLIC).initializer(new ByteCodeAppender() {
            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                return new ByteCodeAppender.Size(new StackManipulation.Compound(new TextConstant(AbstractDynamicTypeBuilderTest.FOO), FieldAccess.forField(instrumentedMethod.getDeclaringType().getDeclaredFields().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.FOO)).getOnly()).write()).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
            }
        }).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FOO))));
    }

    @Test
    public void testSerialVersionUid() throws Exception {
        Class<?> type = createPlain().serialVersionUid(42L).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Field field = type.getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        MatcherAssert.assertThat(((Long) (field.get(null))), CoreMatchers.is(42L));
        MatcherAssert.assertThat(field.getType(), CoreMatchers.is(((Object) (long.class))));
        MatcherAssert.assertThat(field.getModifiers(), CoreMatchers.is((((Opcodes.ACC_PRIVATE) | (Opcodes.ACC_STATIC)) | (Opcodes.ACC_FINAL))));
    }

    @Test
    public void testTypeVariable() throws Exception {
        Class<?> type = createPlain().typeVariable(AbstractDynamicTypeBuilderTest.FOO).typeVariable(AbstractDynamicTypeBuilderTest.BAR, String.class).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getTypeParameters().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(type.getTypeParameters()[1].getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.BAR));
        MatcherAssert.assertThat(type.getTypeParameters()[1].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[1].getBounds()[0], CoreMatchers.is(((Object) (String.class))));
    }

    @Test
    public void testTypeVariableTransformation() throws Exception {
        Class<?> type = createPlain().typeVariable(AbstractDynamicTypeBuilderTest.FOO).typeVariable(AbstractDynamicTypeBuilderTest.BAR, String.class).transform(ElementMatchers.named(AbstractDynamicTypeBuilderTest.BAR), new Transformer<TypeVariableToken>() {
            public TypeVariableToken transform(TypeDescription instrumentedType, TypeVariableToken target) {
                return new TypeVariableToken(target.getSymbol(), Collections.singletonList(of(Integer.class)));
            }
        }).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getTypeParameters().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(type.getTypeParameters()[1].getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.BAR));
        MatcherAssert.assertThat(type.getTypeParameters()[1].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[1].getBounds()[0], CoreMatchers.is(((Object) (Integer.class))));
    }

    @Test
    public void testGenericFieldDefinition() throws Exception {
        Class<?> type = createPlain().defineField(AbstractDynamicTypeBuilderTest.QUX, list).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredField(AbstractDynamicTypeBuilderTest.QUX).getGenericType(), CoreMatchers.is(list));
    }

    @Test
    public void testGenericMethodDefinition() throws Exception {
        Class<?> type = createPlain().defineMethod(AbstractDynamicTypeBuilderTest.QUX, list).withParameter(list, AbstractDynamicTypeBuilderTest.BAR, ProvisioningState.MANDATED).throwing(fooVariable).typeVariable(AbstractDynamicTypeBuilderTest.FOO, Exception.class).intercept(StubMethod.INSTANCE).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.QUX, List.class).getTypeParameters().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.QUX, List.class).getTypeParameters()[0].getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.QUX, List.class).getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.QUX, List.class).getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Object) (Exception.class))));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.QUX, List.class).getGenericReturnType(), CoreMatchers.is(list));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.QUX, List.class).getGenericExceptionTypes()[0], CoreMatchers.is(((Type) (type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.QUX, List.class).getTypeParameters()[0]))));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.QUX, List.class).getGenericParameterTypes().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethod(AbstractDynamicTypeBuilderTest.QUX, List.class).getGenericParameterTypes()[0], CoreMatchers.is(list));
    }

    @Test
    public void testHashCodeMethod() throws Exception {
        Class<?> type = withHashCodeEquals().make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        Object left = type.getDeclaredConstructor().newInstance();
        Object right = type.getDeclaredConstructor().newInstance();
        left.getClass().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).set(left, AbstractDynamicTypeBuilderTest.FOO);
        right.getClass().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).set(right, AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(left.hashCode(), CoreMatchers.is(right.hashCode()));
        MatcherAssert.assertThat(left, CoreMatchers.is(right));
    }

    @Test
    public void testToString() throws Exception {
        Class<?> type = withToString().make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        instance.getClass().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO).set(instance, AbstractDynamicTypeBuilderTest.BAR);
        MatcherAssert.assertThat(instance.toString(), CoreMatchers.endsWith("{foo=bar}"));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testGenericMethodDefinitionMetaDataParameter() throws Exception {
        Class<?> type = createPlain().defineMethod(AbstractDynamicTypeBuilderTest.QUX, list).withParameter(list, AbstractDynamicTypeBuilderTest.BAR, ProvisioningState.MANDATED).throwing(fooVariable).typeVariable(AbstractDynamicTypeBuilderTest.FOO, Exception.class).intercept(StubMethod.INSTANCE).make().load(new URLClassLoader(new URL[0], null), WRAPPER).getLoaded();
        MatcherAssert.assertThat(describe(type).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.QUX)).getOnly().getParameters().getOnly().getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.BAR));
        MatcherAssert.assertThat(describe(type).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.QUX)).getOnly().getParameters().getOnly().getModifiers(), CoreMatchers.is(ProvisioningState.MANDATED.getMask()));
    }

    @Test(expected = ClassFormatError.class)
    public void testUnvalidated() throws Exception {
        createPlainWithoutValidation().defineField(AbstractDynamicTypeBuilderTest.FOO, void.class).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER);
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableOnTypeAnnotationClassBound() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Class<?> type = createPlain().typeVariable(AbstractDynamicTypeBuilderTest.FOO, rawType(Object.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, ((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)).build())).annotateTypeVariable(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getTypeParameters().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).ofTypeVariableBoundType(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).ofTypeVariableBoundType(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableOnTypeAnnotationInterfaceBound() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Class<?> type = createPlain().typeVariable(AbstractDynamicTypeBuilderTest.FOO, rawType(Runnable.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, ((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)).build())).annotateTypeVariable(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getTypeParameters().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Object) (Runnable.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).ofTypeVariableBoundType(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).ofTypeVariableBoundType(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableOnTypeAnnotationTypeVariableBound() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Class<?> type = createPlain().typeVariable(AbstractDynamicTypeBuilderTest.FOO).annotateTypeVariable(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).typeVariable(AbstractDynamicTypeBuilderTest.BAR, rawType(Object.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, ((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 3)).build())).annotateTypeVariable(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, ((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)).build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getTypeParameters().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(type.getTypeParameters()[1].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getTypeParameters()[1].getBounds()[0], CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[0]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[1]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[1]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[1]).ofTypeVariableBoundType(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(type.getTypeParameters()[1]).ofTypeVariableBoundType(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 3)));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationOnInterfaceType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Class<?> type = createPlain().merge(TypeManifestation.ABSTRACT).implement(rawType(Runnable.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build())).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getInterfaces().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getInterfaces()[0], CoreMatchers.is(((Object) (Runnable.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveInterfaceType(type, 0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveInterfaceType(type, 0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnFieldType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Field field = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, rawType(Object.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build())).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(field.getType(), CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableOnMethodAnnotationClassBound() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Method method = createPlain().merge(TypeManifestation.ABSTRACT).defineMethod(AbstractDynamicTypeBuilderTest.FOO, void.class).typeVariable(AbstractDynamicTypeBuilderTest.FOO, rawType(Object.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, ((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)).build())).annotateTypeVariable(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).withoutCode().make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(method.getTypeParameters().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(method.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(method.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).ofTypeVariableBoundType(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).ofTypeVariableBoundType(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableOnMethodAnnotationInterfaceBound() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Method method = createPlain().merge(TypeManifestation.ABSTRACT).defineMethod(AbstractDynamicTypeBuilderTest.FOO, void.class).typeVariable(AbstractDynamicTypeBuilderTest.FOO, rawType(Runnable.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, ((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)).build())).annotateTypeVariable(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).withoutCode().make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(method.getTypeParameters().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(method.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(method.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Object) (Runnable.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).ofTypeVariableBoundType(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).ofTypeVariableBoundType(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableOnMethodAnnotationTypeVariableBound() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Method method = createPlain().merge(TypeManifestation.ABSTRACT).defineMethod(AbstractDynamicTypeBuilderTest.FOO, void.class).typeVariable(AbstractDynamicTypeBuilderTest.FOO).annotateTypeVariable(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).typeVariable(AbstractDynamicTypeBuilderTest.BAR, rawType(Object.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, ((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 3)).build())).annotateTypeVariable(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, ((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)).build()).withoutCode().make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(method.getTypeParameters().length, CoreMatchers.is(2));
        MatcherAssert.assertThat(method.getTypeParameters()[0].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(method.getTypeParameters()[0].getBounds()[0], CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(method.getTypeParameters()[1].getBounds().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(method.getTypeParameters()[1].getBounds()[0], CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[0]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[1]).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[1]).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 2)));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[1]).ofTypeVariableBoundType(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveTypeVariable(method.getTypeParameters()[1]).ofTypeVariableBoundType(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(((AbstractDynamicTypeBuilderTest.INTEGER_VALUE) * 3)));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnMethodReturnType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Method method = createPlain().merge(TypeManifestation.ABSTRACT).defineMethod(AbstractDynamicTypeBuilderTest.FOO, rawType(Object.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build())).withoutCode().make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(method.getReturnType(), CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveReturnType(method).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveReturnType(method).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnMethodParameterType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Method method = createPlain().merge(TypeManifestation.ABSTRACT).defineMethod(AbstractDynamicTypeBuilderTest.FOO, void.class).withParameters(rawType(Object.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build())).withoutCode().make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO, Object.class);
        MatcherAssert.assertThat(method.getParameterTypes().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(method.getParameterTypes()[0], CoreMatchers.is(((Object) (Object.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveParameterType(method, 0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveParameterType(method, 0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnMethodExceptionType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Method method = createPlain().merge(TypeManifestation.ABSTRACT).defineMethod(AbstractDynamicTypeBuilderTest.FOO, void.class).throwing(rawType(Exception.class).build(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build())).withoutCode().make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredMethod(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(method.getExceptionTypes().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(method.getExceptionTypes()[0], CoreMatchers.is(((Object) (Exception.class))));
        MatcherAssert.assertThat(DISPATCHER.resolveExceptionType(method, 0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveExceptionType(method, 0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnWildcardWithoutBound() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Field field = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, parameterizedType(of(Collection.class), unboundWildcard(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build())).build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofTypeArgument(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofTypeArgument(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnWildcardUpperBoundBound() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Field field = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, parameterizedType(of(Collection.class), rawType(Object.class).annotate(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).asWildcardUpperBound()).build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofTypeArgument(0).ofWildcardUpperBoundType(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofTypeArgument(0).ofWildcardUpperBoundType(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnWildcardLowerBoundBound() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Field field = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, parameterizedType(of(Collection.class), rawType(Object.class).annotate(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).asWildcardLowerBound()).build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofTypeArgument(0).ofWildcardLowerBoundType(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofTypeArgument(0).ofWildcardLowerBoundType(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnGenericComponentType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Field field = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, parameterizedType(of(Collection.class), TypeDescription.Generic.Builder.unboundWildcard()).annotate(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).asArray().build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofComponentType().asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofComponentType().asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnNonGenericComponentType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Field field = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, rawType(Object.class).annotate(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).asArray().build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofComponentType().asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofComponentType().asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnParameterizedType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Field field = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, parameterizedType(of(Collection.class), unboundWildcard(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build())).build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofTypeArgument(0).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).ofTypeArgument(0).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testAnnotationTypeOnNestedType() throws Exception {
        Class<? extends Annotation> typeAnnotationType = ((Class<? extends Annotation>) (Class.forName(AbstractDynamicTypeBuilderTest.TYPE_VARIABLE_NAME)));
        MethodDescription.InDefinedShape value = of(typeAnnotationType).getDeclaredMethods().filter(ElementMatchers.named(AbstractDynamicTypeBuilderTest.VALUE)).getOnly();
        Field field = createPlain().defineField(AbstractDynamicTypeBuilderTest.FOO, TypeDescription.Generic.Builder.rawType(of(AbstractDynamicTypeBuilderTest.Nested.Inner.class), rawType(AbstractDynamicTypeBuilderTest.Nested.class).build()).annotate(ofType(typeAnnotationType).define(AbstractDynamicTypeBuilderTest.VALUE, AbstractDynamicTypeBuilderTest.INTEGER_VALUE).build()).build()).make().load(typeAnnotationType.getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredField(AbstractDynamicTypeBuilderTest.FOO);
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(DISPATCHER.resolveFieldType(field).asList().ofType(typeAnnotationType).getValue(value).resolve(Integer.class), CoreMatchers.is(AbstractDynamicTypeBuilderTest.INTEGER_VALUE));
    }

    @Test
    public void testBridgeResolutionAmbiguous() throws Exception {
        Class<?> type = createPlain().defineMethod(AbstractDynamicTypeBuilderTest.QUX, String.class, Visibility.PUBLIC).intercept(FixedValue.value(AbstractDynamicTypeBuilderTest.FOO)).defineMethod(AbstractDynamicTypeBuilderTest.QUX, Object.class, Visibility.PUBLIC).intercept(FixedValue.value(AbstractDynamicTypeBuilderTest.BAR)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        for (Method method : type.getDeclaredMethods()) {
            if ((method.getReturnType()) == (String.class)) {
                MatcherAssert.assertThat(method.getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.QUX));
                MatcherAssert.assertThat(method.getParameterTypes().length, CoreMatchers.is(0));
                MatcherAssert.assertThat(method.invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.BAR))));
            } else
                if ((method.getReturnType()) == (Object.class)) {
                    MatcherAssert.assertThat(method.getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.QUX));
                    MatcherAssert.assertThat(method.getParameterTypes().length, CoreMatchers.is(0));
                    MatcherAssert.assertThat(method.invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.BAR))));
                } else {
                    throw new AssertionError();
                }

        }
    }

    @Test
    public void testCanOverloadMethodByReturnType() throws Exception {
        Class<?> type = // Is static to avoid method graph compiler.
        createPlain().defineMethod(AbstractDynamicTypeBuilderTest.QUX, String.class, Visibility.PUBLIC).intercept(FixedValue.value(AbstractDynamicTypeBuilderTest.FOO)).defineMethod(AbstractDynamicTypeBuilderTest.QUX, Object.class, Ownership.STATIC, Visibility.PUBLIC).intercept(FixedValue.value(AbstractDynamicTypeBuilderTest.BAR)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        for (Method method : type.getDeclaredMethods()) {
            if ((method.getReturnType()) == (String.class)) {
                MatcherAssert.assertThat(method.getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.QUX));
                MatcherAssert.assertThat(method.getParameterTypes().length, CoreMatchers.is(0));
                MatcherAssert.assertThat(method.invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FOO))));
            } else
                if ((method.getReturnType()) == (Object.class)) {
                    MatcherAssert.assertThat(method.getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.QUX));
                    MatcherAssert.assertThat(method.getParameterTypes().length, CoreMatchers.is(0));
                    MatcherAssert.assertThat(method.invoke(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.BAR))));
                } else {
                    throw new AssertionError();
                }

        }
    }

    @Test
    public void testCanOverloadFieldByType() throws Exception {
        Class<?> type = createPlain().defineField(AbstractDynamicTypeBuilderTest.QUX, String.class, Ownership.STATIC, Visibility.PUBLIC).value(AbstractDynamicTypeBuilderTest.FOO).defineField(AbstractDynamicTypeBuilderTest.QUX, long.class, Ownership.STATIC, Visibility.PUBLIC).value(42L).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        for (Field field : type.getDeclaredFields()) {
            if ((field.getType()) == (String.class)) {
                MatcherAssert.assertThat(field.getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.QUX));
                MatcherAssert.assertThat(field.get(null), CoreMatchers.is(((Object) (AbstractDynamicTypeBuilderTest.FOO))));
            } else
                if ((field.getType()) == (long.class)) {
                    MatcherAssert.assertThat(field.getName(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.QUX));
                    MatcherAssert.assertThat(field.get(null), CoreMatchers.is(((Object) (42L))));
                } else {
                    throw new AssertionError();
                }

        }
    }

    @Test
    public void testInterfaceInterception() throws Exception {
        MatcherAssert.assertThat(((AbstractDynamicTypeBuilderTest.SampleInterface) (createPlain().implement(AbstractDynamicTypeBuilderTest.SampleInterface.class).intercept(FixedValue.value(AbstractDynamicTypeBuilderTest.FOO)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredConstructor().newInstance())).foo(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
    }

    @Test
    public void testInterfaceInterceptionPreviousSuperseeded() throws Exception {
        MatcherAssert.assertThat(((AbstractDynamicTypeBuilderTest.SampleInterface) (createPlain().method(ElementMatchers.named(AbstractDynamicTypeBuilderTest.FOO)).intercept(ExceptionMethod.throwing(AssertionError.class)).implement(AbstractDynamicTypeBuilderTest.SampleInterface.class).intercept(FixedValue.value(AbstractDynamicTypeBuilderTest.FOO)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredConstructor().newInstance())).foo(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
    }

    @Test
    public void testInterfaceInterceptionLaterSuperseeding() throws Exception {
        MatcherAssert.assertThat(((AbstractDynamicTypeBuilderTest.SampleInterface) (createPlain().implement(AbstractDynamicTypeBuilderTest.SampleInterface.class).intercept(ExceptionMethod.throwing(AssertionError.class)).method(ElementMatchers.named(AbstractDynamicTypeBuilderTest.FOO)).intercept(FixedValue.value(AbstractDynamicTypeBuilderTest.FOO)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredConstructor().newInstance())).foo(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
    }

    @Test
    public void testInterfaceInterceptionSubClass() throws Exception {
        MatcherAssert.assertThat(((AbstractDynamicTypeBuilderTest.SampleInterface) (createPlain().implement(AbstractDynamicTypeBuilderTest.SampleInterface.SubInterface.class).intercept(FixedValue.value(AbstractDynamicTypeBuilderTest.FOO)).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded().getDeclaredConstructor().newInstance())).foo(), CoreMatchers.is(AbstractDynamicTypeBuilderTest.FOO));
    }

    @Test
    public void testInterfaceMakesClassMethodPublic() throws Exception {
        Class<?> type = createPlain().implement(AbstractDynamicTypeBuilderTest.Cloneable.class).method(ElementMatchers.named("clone")).intercept(FixedValue.self()).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        AbstractDynamicTypeBuilderTest.Cloneable cloneable = ((AbstractDynamicTypeBuilderTest.Cloneable) (type.getDeclaredConstructor().newInstance()));
        MatcherAssert.assertThat(cloneable.clone(), CoreMatchers.sameInstance(((Object) (cloneable))));
    }

    @Test
    public void testTopLevelType() throws Exception {
        Class<?> type = topLevelType().make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getEnclosingClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getEnclosingMethod(), CoreMatchers.nullValue(Method.class));
        MatcherAssert.assertThat(type.getEnclosingConstructor(), CoreMatchers.nullValue(Constructor.class));
        MatcherAssert.assertThat(type.isAnonymousClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isLocalClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isMemberClass(), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaredAsMemberType() throws Exception {
        TypeDescription sample = new TypeDescription.Latent("foo.Bar$Qux", Opcodes.ACC_PUBLIC, OBJECT) {
            @Override
            public String getSimpleName() {
                return "Qux";
            }

            @Override
            public boolean isAnonymousType() {
                return false;
            }

            @Override
            public boolean isMemberType() {
                return true;
            }
        };
        Class<?> outer = declaredTypes(sample).make().load(getClass().getClassLoader(), opened()).getLoaded();
        Class<?> type = createPlainWithoutValidation().name(sample.getName()).innerTypeOf(outer).asMemberType().make().load(((InjectionClassLoader) (outer.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(type.getEnclosingClass(), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(type.getEnclosingMethod(), CoreMatchers.nullValue(Method.class));
        MatcherAssert.assertThat(type.getEnclosingConstructor(), CoreMatchers.nullValue(Constructor.class));
        MatcherAssert.assertThat(type.isAnonymousClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isLocalClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isMemberClass(), CoreMatchers.is(true));
    }

    @Test
    public void testDeclaredAsAnonymousType() throws Exception {
        // Older JVMs derive the anonymous class property from a naming convention.
        TypeDescription sample = new TypeDescription.Latent("foo.Bar$1", Opcodes.ACC_PUBLIC, OBJECT) {
            @Override
            public String getSimpleName() {
                return "";
            }

            @Override
            public boolean isAnonymousType() {
                return true;
            }

            @Override
            public boolean isMemberType() {
                return false;
            }
        };
        Class<?> outer = declaredTypes(sample).make().load(getClass().getClassLoader(), opened()).getLoaded();
        Class<?> type = createPlainWithoutValidation().name(sample.getName()).innerTypeOf(outer).asAnonymousType().make().load(((InjectionClassLoader) (outer.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getEnclosingClass(), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(type.getEnclosingMethod(), CoreMatchers.nullValue(Method.class));
        MatcherAssert.assertThat(type.getEnclosingConstructor(), CoreMatchers.nullValue(Constructor.class));
        MatcherAssert.assertThat(type.isAnonymousClass(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isLocalClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isMemberClass(), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaredAsLocalType() throws Exception {
        TypeDescription sample = new TypeDescription.Latent("foo.Bar$Qux", Opcodes.ACC_PUBLIC, OBJECT) {
            @Override
            public String getSimpleName() {
                return "Qux";
            }

            @Override
            public boolean isAnonymousType() {
                return false;
            }

            @Override
            public boolean isMemberType() {
                return false;
            }
        };
        Class<?> outer = declaredTypes(sample).make().load(getClass().getClassLoader(), opened()).getLoaded();
        Class<?> type = createPlainWithoutValidation().name(sample.getName()).innerTypeOf(outer).make().load(((InjectionClassLoader) (outer.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getEnclosingClass(), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(type.getEnclosingMethod(), CoreMatchers.nullValue(Method.class));
        MatcherAssert.assertThat(type.getEnclosingConstructor(), CoreMatchers.nullValue(Constructor.class));
        MatcherAssert.assertThat(type.isAnonymousClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isLocalClass(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isMemberClass(), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaredAsAnonymousTypeInMethod() throws Exception {
        // Older JVMs derive the anonymous class property from a naming convention.
        TypeDescription sample = new TypeDescription.Latent("foo.Bar$1", Opcodes.ACC_PUBLIC, OBJECT) {
            @Override
            public String getSimpleName() {
                return "";
            }

            @Override
            public boolean isAnonymousType() {
                return true;
            }

            @Override
            public boolean isMemberType() {
                return false;
            }
        };
        Class<?> outer = declaredTypes(sample).make().load(getClass().getClassLoader(), opened()).getLoaded();
        Class<?> type = innerTypeOf(outer.getConstructor()).asAnonymousType().make().load(((InjectionClassLoader) (outer.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getEnclosingClass(), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(type.getEnclosingMethod(), CoreMatchers.nullValue(Method.class));
        MatcherAssert.assertThat(type.getEnclosingConstructor(), CoreMatchers.is(((Object) (outer.getConstructor()))));
        MatcherAssert.assertThat(type.isAnonymousClass(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isLocalClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isMemberClass(), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaredAsLocalTypeInInitializer() throws Exception {
        // Older JVMs derive the anonymous class property from a naming convention.
        TypeDescription sample = new TypeDescription.Latent("foo.Bar$Qux", Opcodes.ACC_PUBLIC, OBJECT) {
            @Override
            public String getSimpleName() {
                return "Qux";
            }

            @Override
            public boolean isAnonymousType() {
                return false;
            }

            @Override
            public boolean isMemberType() {
                return false;
            }
        };
        Class<?> outer = declaredTypes(sample).make().load(getClass().getClassLoader(), opened()).getLoaded();
        Class<?> type = createPlainWithoutValidation().name(sample.getName()).innerTypeOf(new MethodDescription.Latent.TypeInitializer(of(outer))).make().load(((InjectionClassLoader) (outer.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getEnclosingClass(), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(type.getEnclosingMethod(), CoreMatchers.nullValue(Method.class));
        MatcherAssert.assertThat(type.getEnclosingConstructor(), CoreMatchers.nullValue(Constructor.class));
        MatcherAssert.assertThat(type.isAnonymousClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isLocalClass(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isMemberClass(), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaredAsAnonymousTypeInInitializer() throws Exception {
        // Older JVMs derive the anonymous class property from a naming convention.
        TypeDescription sample = new TypeDescription.Latent("foo.Bar$1", Opcodes.ACC_PUBLIC, OBJECT) {
            @Override
            public String getSimpleName() {
                return "";
            }

            @Override
            public boolean isAnonymousType() {
                return true;
            }

            @Override
            public boolean isMemberType() {
                return false;
            }
        };
        Class<?> outer = declaredTypes(sample).make().load(getClass().getClassLoader(), opened()).getLoaded();
        Class<?> type = createPlainWithoutValidation().name(sample.getName()).innerTypeOf(new MethodDescription.Latent.TypeInitializer(of(outer))).asAnonymousType().make().load(((InjectionClassLoader) (outer.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getEnclosingClass(), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(type.getEnclosingMethod(), CoreMatchers.nullValue(Method.class));
        MatcherAssert.assertThat(type.getEnclosingConstructor(), CoreMatchers.nullValue(Constructor.class));
        MatcherAssert.assertThat(type.isAnonymousClass(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isLocalClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isMemberClass(), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaredAsLocalTypeInMethod() throws Exception {
        TypeDescription sample = new TypeDescription.Latent("foo.Bar$Qux", Opcodes.ACC_PUBLIC, OBJECT) {
            @Override
            public String getSimpleName() {
                return "Qux";
            }

            @Override
            public boolean isAnonymousType() {
                return false;
            }

            @Override
            public boolean isMemberType() {
                return false;
            }
        };
        Class<?> outer = new net.bytebuddy.ByteBuddy().subclass(Object.class).name("foo.Bar").defineMethod("foo", void.class, Visibility.PUBLIC).intercept(StubMethod.INSTANCE).declaredTypes(sample).make().load(getClass().getClassLoader(), opened()).getLoaded();
        Class<?> type = createPlainWithoutValidation().name(sample.getName()).innerTypeOf(outer.getMethod(AbstractDynamicTypeBuilderTest.FOO)).make().load(((InjectionClassLoader) (outer.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getEnclosingClass(), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(type.getEnclosingMethod(), CoreMatchers.is(outer.getMethod(AbstractDynamicTypeBuilderTest.FOO)));
        MatcherAssert.assertThat(type.getEnclosingConstructor(), CoreMatchers.nullValue(Constructor.class));
        MatcherAssert.assertThat(type.isAnonymousClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isLocalClass(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isMemberClass(), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaredAsLocalTypeInConstructor() throws Exception {
        TypeDescription sample = new TypeDescription.Latent("foo.Bar$Qux", Opcodes.ACC_PUBLIC, OBJECT) {
            @Override
            public String getSimpleName() {
                return "Qux";
            }

            @Override
            public boolean isAnonymousType() {
                return false;
            }

            @Override
            public boolean isMemberType() {
                return false;
            }
        };
        Class<?> outer = declaredTypes(sample).make().load(getClass().getClassLoader(), opened()).getLoaded();
        Class<?> type = innerTypeOf(outer.getConstructor()).make().load(((InjectionClassLoader) (outer.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(type.getDeclaringClass(), CoreMatchers.nullValue(Class.class));
        MatcherAssert.assertThat(type.getEnclosingClass(), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(type.getEnclosingMethod(), CoreMatchers.nullValue(Method.class));
        MatcherAssert.assertThat(type.getEnclosingConstructor(), CoreMatchers.is(((Object) (outer.getConstructor()))));
        MatcherAssert.assertThat(type.isAnonymousClass(), CoreMatchers.is(false));
        MatcherAssert.assertThat(type.isLocalClass(), CoreMatchers.is(true));
        MatcherAssert.assertThat(type.isMemberClass(), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(11)
    public void testNestMates() throws Exception {
        TypeDescription sample = new TypeDescription.Latent("foo.Bar", Opcodes.ACC_PUBLIC, OBJECT);
        Class<?> outer = nestMembers(sample).make().load(getClass().getClassLoader(), opened()).getLoaded();
        Class<?> type = nestHost(outer).make().load(((InjectionClassLoader) (outer.getClassLoader())), INSTANCE).getLoaded();
        MatcherAssert.assertThat(Class.class.getMethod("getNestHost").invoke(outer), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(Class.class.getMethod("getNestMembers").invoke(outer), CoreMatchers.is(((Object) (new Class<?>[]{ outer, type }))));
        MatcherAssert.assertThat(Class.class.getMethod("getNestHost").invoke(type), CoreMatchers.is(((Object) (outer))));
        MatcherAssert.assertThat(Class.class.getMethod("getNestMembers").invoke(type), CoreMatchers.is(((Object) (new Class<?>[]{ outer, type }))));
    }

    @Test
    public void testAuxiliaryTypes() throws Exception {
        Map<TypeDescription, byte[]> auxiliaryTypes = require(TypeDescription.VOID, new byte[]{ 1, 2, 3 }).make().getAuxiliaryTypes();
        MatcherAssert.assertThat(auxiliaryTypes.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(auxiliaryTypes.get(TypeDescription.VOID).length, CoreMatchers.is(3));
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SampleAnnotation {
        String foo();
    }

    /* empty */
    public static class Foo {}

    public static class Bar {
        public static String foo;

        public static void invoke() {
            AbstractDynamicTypeBuilderTest.Bar.foo = AbstractDynamicTypeBuilderTest.FOO;
        }
    }

    public static class BridgeRetention<T> extends CallTraceable {
        public T foo() {
            register(AbstractDynamicTypeBuilderTest.FOO);
            return null;
        }

        /* empty */
        public static class Inner extends AbstractDynamicTypeBuilderTest.BridgeRetention<String> {}
    }

    public static class CallSuperMethod<T> extends CallTraceable {
        public T foo(T value) {
            register(AbstractDynamicTypeBuilderTest.FOO);
            return value;
        }

        /* empty */
        public static class Inner extends AbstractDynamicTypeBuilderTest.CallSuperMethod<String> {}
    }

    private static class PreparedField implements Implementation {
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType.withField(new FieldDescription.Token(AbstractDynamicTypeBuilderTest.FOO, AbstractDynamicTypeBuilderTest.MODIFIERS, OBJECT, Collections.singletonList(ofType(AbstractDynamicTypeBuilderTest.SampleAnnotation.class).define(AbstractDynamicTypeBuilderTest.FOO, AbstractDynamicTypeBuilderTest.BAR).build())));
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new ByteCodeAppender.Simple(NullConstant.INSTANCE, MethodReturn.REFERENCE);
        }
    }

    private static class PreparedMethod implements Implementation {
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType.withMethod(new MethodDescription.Token(AbstractDynamicTypeBuilderTest.FOO, AbstractDynamicTypeBuilderTest.MODIFIERS, Collections.<TypeVariableToken>emptyList(), OBJECT, Collections.singletonList(new ParameterDescription.Token(OBJECT, Collections.singletonList(ofType(AbstractDynamicTypeBuilderTest.SampleAnnotation.class).define(AbstractDynamicTypeBuilderTest.FOO, AbstractDynamicTypeBuilderTest.QUX).build()))), Collections.singletonList(of(Exception.class)), Collections.singletonList(ofType(AbstractDynamicTypeBuilderTest.SampleAnnotation.class).define(AbstractDynamicTypeBuilderTest.FOO, AbstractDynamicTypeBuilderTest.BAR).build()), AnnotationValue.UNDEFINED, UNDEFINED));
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new ByteCodeAppender.Simple(NullConstant.INSTANCE, MethodReturn.REFERENCE);
        }
    }

    public static class InterfaceOverrideInterceptor {
        public static String intercept(@SuperCall
        Callable<String> zuper) throws Exception {
            return (zuper.call()) + (AbstractDynamicTypeBuilderTest.BAR);
        }
    }

    @SuppressWarnings("unused")
    private static class Holder<foo> {
        List<?> list;

        List<foo> fooList;
    }

    @SuppressWarnings("unused")
    public static class Nested {
        /* empty */
        public class Inner {}
    }

    @SuppressWarnings("unused")
    public static class GenericNested<T> {
        /* empty */
        public class Inner {}
    }

    public interface Cloneable {
        Object clone();
    }

    public interface SampleInterface {
        String foo();

        interface SubInterface extends AbstractDynamicTypeBuilderTest.SampleInterface {}
    }
}

