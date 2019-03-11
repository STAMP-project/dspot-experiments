package net.bytebuddy;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.DefaultMethodCall;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Pipe;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.primitive.PrimitiveTypeAwareAssigner;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.AgentAttachmentRule;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default.NO_CONSTRUCTORS;
import static net.bytebuddy.implementation.bind.annotation.Pipe.Binder.install;
import static net.bytebuddy.implementation.bytecode.StackManipulation.Illegal.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class ByteBuddyTutorialExamplesTest {
    private static final String DEFAULT_METHOD_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    private static final String CONFLICTING_DEFAULT_METHOD_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodConflictingInterface";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Rule
    public MethodRule agentAttachmentRule = new AgentAttachmentRule();

    @Test
    public void testHelloWorld() throws Exception {
        Class<?> dynamicType = new ByteBuddy().subclass(Object.class).method(ElementMatchers.named("toString")).intercept(FixedValue.value("Hello World!")).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredConstructor().newInstance().toString(), CoreMatchers.is("Hello World!"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExtensiveExample() throws Exception {
        Class<? extends ByteBuddyTutorialExamplesTest.Function> dynamicType = new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.Function.class).method(ElementMatchers.named("apply")).intercept(MethodDelegation.to(new ByteBuddyTutorialExamplesTest.GreetingInterceptor())).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredConstructor().newInstance().apply("Byte Buddy"), CoreMatchers.is(((Object) ("Hello from Byte Buddy"))));
    }

    @Test
    public void testTutorialGettingStartedUnnamed() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().subclass(Object.class).make();
        MatcherAssert.assertThat(dynamicType, CoreMatchers.notNullValue());
    }

    @Test
    public void testTutorialGettingStartedNamed() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().subclass(Object.class).name("example.Type").make();
        MatcherAssert.assertThat(dynamicType, CoreMatchers.notNullValue());
    }

    @Test
    public void testTutorialGettingStartedNamingStrategy() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy().with(new ByteBuddyTutorialExamplesTest.GettingStartedNamingStrategy()).subclass(Object.class).make();
        MatcherAssert.assertThat(dynamicType, CoreMatchers.notNullValue());
        Class<?> type = dynamicType.load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is("i.love.ByteBuddy.Object"));
    }

    @Test
    public void testTutorialGettingStartedClassLoading() throws Exception {
        Class<?> dynamicType = new ByteBuddy().subclass(Object.class).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType, CoreMatchers.notNullValue());
    }

    @Test
    @AgentAttachmentRule.Enforce(redefinesClasses = true)
    public void testTutorialGettingStartedClassReloading() throws Exception {
        ByteBuddyAgent.install();
        ByteBuddyTutorialExamplesTest.FooReloading foo = new ByteBuddyTutorialExamplesTest.FooReloading();
        try {
            new ByteBuddy().redefine(ByteBuddyTutorialExamplesTest.BarReloading.class).name(ByteBuddyTutorialExamplesTest.FooReloading.class.getName()).make().load(ByteBuddyTutorialExamplesTest.FooReloading.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            MatcherAssert.assertThat(foo.m(), CoreMatchers.is("bar"));
        } finally {
            ClassReloadingStrategy.fromInstalledAgent().reset(ByteBuddyTutorialExamplesTest.FooReloading.class);// Assure repeatability.

        }
        MatcherAssert.assertThat(foo.m(), CoreMatchers.is("foo"));
    }

    @Test
    public void testTutorialGettingStartedTypePool() throws Exception {
        TypePool typePool = ofSystemLoader();
        ClassLoader classLoader = new URLClassLoader(new URL[0], null);// Assure repeatability.

        Class<?> type = new ByteBuddy().redefine(typePool.describe(ByteBuddyTutorialExamplesTest.UnloadedBar.class.getName()).resolve(), net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.ofSystemLoader()).defineField("qux", String.class).make().load(classLoader, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredField("qux"), CoreMatchers.notNullValue(Field.class));
    }

    @Test
    public void testTutorialGettingStartedJavaAgent() throws Exception {
        new AgentBuilder.Default().type(ElementMatchers.isAnnotatedWith(ByteBuddyTutorialExamplesTest.Rebase.class)).transform(new AgentBuilder.Transformer() {
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                return builder.method(ElementMatchers.named("toString")).intercept(FixedValue.value("transformed"));
            }
        }).installOn(Mockito.mock(Instrumentation.class));
    }

    @Test
    public void testFieldsAndMethodsToString() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(Object.class).name("example.Type").make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().toString(), CoreMatchers.startsWith("example.Type"));
    }

    @Test
    public void testFieldsAndMethodsDetailedMatcher() throws Exception {
        MatcherAssert.assertThat(TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.named("toString").and(ElementMatchers.returns(String.class)).and(ElementMatchers.takesArguments(0))).size(), CoreMatchers.is(1));
    }

    @Test
    public void testFieldsAndMethodsMatcherStack() throws Exception {
        ByteBuddyTutorialExamplesTest.Foo foo = new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.Foo.class).method(ElementMatchers.isDeclaredBy(ByteBuddyTutorialExamplesTest.Foo.class)).intercept(FixedValue.value("Hello World!")).method(ElementMatchers.named("foo")).intercept(FixedValue.value("Hello Foo!")).method(ElementMatchers.named("foo").and(ElementMatchers.takesArguments(1))).intercept(FixedValue.value("...")).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(foo.bar(), CoreMatchers.is("Hello World!"));
        MatcherAssert.assertThat(foo.foo(), CoreMatchers.is("Hello Foo!"));
        MatcherAssert.assertThat(foo.foo(null), CoreMatchers.is("..."));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFieldsAndMethodsIllegalAssignment() throws Exception {
        new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.Foo.class).method(ElementMatchers.isDeclaredBy(ByteBuddyTutorialExamplesTest.Foo.class)).intercept(FixedValue.value(0)).make();
    }

    @Test
    public void testFieldsAndMethodsMethodDelegation() throws Exception {
        String helloWorld = new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.Source.class).method(ElementMatchers.named("hello")).intercept(MethodDelegation.to(ByteBuddyTutorialExamplesTest.Target.class)).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().hello("World");
        MatcherAssert.assertThat(helloWorld, CoreMatchers.is("Hello World!"));
    }

    @Test
    public void testFieldsAndMethodsMethodDelegationAlternatives() throws Exception {
        String helloWorld = new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.Source.class).method(ElementMatchers.named("hello")).intercept(MethodDelegation.to(ByteBuddyTutorialExamplesTest.Target2.class)).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().hello("World");
        MatcherAssert.assertThat(helloWorld, CoreMatchers.is("Hello World!"));
    }

    @Test
    public void testFieldsAndMethodsMethodSuperCall() throws Exception {
        ByteBuddyTutorialExamplesTest.MemoryDatabase loggingDatabase = new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.MemoryDatabase.class).method(ElementMatchers.named("load")).intercept(MethodDelegation.to(ByteBuddyTutorialExamplesTest.LoggerInterceptor.class)).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(loggingDatabase.load("qux"), CoreMatchers.is(Arrays.asList("qux: foo", "qux: bar")));
    }

    @Test
    public void testFieldsAndMethodsMethodSuperCallExplicit() throws Exception {
        MatcherAssert.assertThat(new ByteBuddyTutorialExamplesTest.LoggingMemoryDatabase().load("qux"), CoreMatchers.is(Arrays.asList("qux: foo", "qux: bar")));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testFieldsAndMethodsMethodDefaultCall() throws Exception {
        // This test differs from the tutorial by only conditionally expressing the Java 8 types.
        Object instance = new ByteBuddy(ClassFileVersion.JAVA_V8).subclass(Object.class).implement(Class.forName(ByteBuddyTutorialExamplesTest.DEFAULT_METHOD_INTERFACE)).implement(Class.forName(ByteBuddyTutorialExamplesTest.CONFLICTING_DEFAULT_METHOD_INTERFACE)).method(ElementMatchers.named("foo")).intercept(DefaultMethodCall.prioritize(Class.forName(ByteBuddyTutorialExamplesTest.DEFAULT_METHOD_INTERFACE))).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance();
        Method method = instance.getClass().getMethod("foo");
        MatcherAssert.assertThat(method.invoke(instance), CoreMatchers.is(((Object) ("foo"))));
    }

    @Test
    public void testFieldsAndMethodsExplicitMethodCall() throws Exception {
        Object object = new ByteBuddy().subclass(Object.class, NO_CONSTRUCTORS).defineConstructor(Visibility.PUBLIC).withParameters(int.class).intercept(MethodCall.invoke(Object.class.getDeclaredConstructor())).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor(int.class).newInstance(42);
        MatcherAssert.assertThat(object.getClass(), CoreMatchers.not(CoreMatchers.<Class<?>>is(Object.class)));
    }

    @Test
    public void testFieldsAndMethodsSuper() throws Exception {
        ByteBuddyTutorialExamplesTest.MemoryDatabase loggingDatabase = new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.MemoryDatabase.class).method(ElementMatchers.named("load")).intercept(MethodDelegation.to(ByteBuddyTutorialExamplesTest.ChangingLoggerInterceptor.class)).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(loggingDatabase.load("qux"), CoreMatchers.is(Arrays.asList("qux (logged access): foo", "qux (logged access): bar")));
    }

    @Test
    public void testFieldsAndMethodsRuntimeType() throws Exception {
        ByteBuddyTutorialExamplesTest.Loop trivialGetterBean = new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.Loop.class).method(ElementMatchers.isDeclaredBy(ByteBuddyTutorialExamplesTest.Loop.class)).intercept(MethodDelegation.to(ByteBuddyTutorialExamplesTest.Interceptor.class)).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(trivialGetterBean.loop(42), CoreMatchers.is(42));
        MatcherAssert.assertThat(trivialGetterBean.loop("foo"), CoreMatchers.is("foo"));
    }

    @Test
    public void testFieldsAndMethodsForwarding() throws Exception {
        ByteBuddyTutorialExamplesTest.MemoryDatabase memoryDatabase = new ByteBuddyTutorialExamplesTest.MemoryDatabase();
        ByteBuddyTutorialExamplesTest.MemoryDatabase loggingDatabase = new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.MemoryDatabase.class).method(ElementMatchers.named("load")).intercept(MethodDelegation.withDefaultConfiguration().withBinders(install(ByteBuddyTutorialExamplesTest.Forwarder.class)).to(new ByteBuddyTutorialExamplesTest.ForwardingLoggerInterceptor(memoryDatabase))).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance();
        MatcherAssert.assertThat(loggingDatabase.load("qux"), CoreMatchers.is(Arrays.asList("qux: foo", "qux: bar")));
    }

    @Test
    public void testFieldsAndMethodsFieldAccess() throws Exception {
        ByteBuddy byteBuddy = new ByteBuddy();
        Class<? extends ByteBuddyTutorialExamplesTest.UserType> dynamicUserType = byteBuddy.subclass(ByteBuddyTutorialExamplesTest.UserType.class).defineField("interceptor", ByteBuddyTutorialExamplesTest.Interceptor2.class, Visibility.PRIVATE).method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))).intercept(MethodDelegation.toField("interceptor")).implement(ByteBuddyTutorialExamplesTest.InterceptionAccessor.class).intercept(FieldAccessor.ofBeanProperty()).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        ByteBuddyTutorialExamplesTest.InstanceCreator factory = byteBuddy.subclass(ByteBuddyTutorialExamplesTest.InstanceCreator.class).method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class))).intercept(MethodDelegation.toConstructor(dynamicUserType)).make().load(dynamicUserType.getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance();
        ByteBuddyTutorialExamplesTest.UserType userType = ((ByteBuddyTutorialExamplesTest.UserType) (factory.makeInstance()));
        ((ByteBuddyTutorialExamplesTest.InterceptionAccessor) (userType)).setInterceptor(new ByteBuddyTutorialExamplesTest.HelloWorldInterceptor());
        MatcherAssert.assertThat(userType.doSomething(), CoreMatchers.is("Hello World!"));
    }

    @Test
    public void testAttributesAndAnnotationForClass() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(Object.class).annotateType(new ByteBuddyTutorialExamplesTest.RuntimeDefinitionImpl()).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().isAnnotationPresent(ByteBuddyTutorialExamplesTest.RuntimeDefinition.class), CoreMatchers.is(true));
    }

    @Test
    public void testAttributesAndAnnotationForMethodAndField() throws Exception {
        Class<?> dynamicType = new ByteBuddy().subclass(Object.class).method(ElementMatchers.named("toString")).intercept(SuperMethodCall.INSTANCE).annotateMethod(new ByteBuddyTutorialExamplesTest.RuntimeDefinitionImpl()).defineField("foo", Object.class).annotateField(new ByteBuddyTutorialExamplesTest.RuntimeDefinitionImpl()).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        MatcherAssert.assertThat(dynamicType.getDeclaredMethod("toString").isAnnotationPresent(ByteBuddyTutorialExamplesTest.RuntimeDefinition.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(dynamicType.getDeclaredField("foo").isAnnotationPresent(ByteBuddyTutorialExamplesTest.RuntimeDefinition.class), CoreMatchers.is(true));
    }

    @Test
    public void testCustomImplementationMethodImplementation() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(ByteBuddyTutorialExamplesTest.SumExample.class).method(ElementMatchers.named("calculate")).intercept(ByteBuddyTutorialExamplesTest.SumImplementation.INSTANCE).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().calculate(), CoreMatchers.is(60));
    }

    @Test
    public void testCustomImplementationAssigner() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(Object.class).method(ElementMatchers.named("toString")).intercept(FixedValue.value(42).withAssigner(new PrimitiveTypeAwareAssigner(ByteBuddyTutorialExamplesTest.ToStringAssigner.INSTANCE), STATIC)).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().toString(), CoreMatchers.is("42"));
    }

    @Test
    public void testCustomImplementationDelegationAnnotation() throws Exception {
        MatcherAssert.assertThat(new ByteBuddy().subclass(Object.class).method(ElementMatchers.named("toString")).intercept(MethodDelegation.withDefaultConfiguration().withBinders(ByteBuddyTutorialExamplesTest.StringValueBinder.INSTANCE).to(ByteBuddyTutorialExamplesTest.ToStringInterceptor.class)).make().load(getClass().getClassLoader(), WRAPPER).getLoaded().getDeclaredConstructor().newInstance().toString(), CoreMatchers.is("Hello!"));
    }

    public enum IntegerSum implements StackManipulation {

        INSTANCE;
        public boolean isValid() {
            return true;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitInsn(Opcodes.IADD);
            return new StackManipulation.Size((-1), 0);
        }
    }

    public enum SumMethod implements ByteCodeAppender {

        INSTANCE;
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            if (!(instrumentedMethod.getReturnType().asErasure().represents(int.class))) {
                throw new IllegalArgumentException((instrumentedMethod + " must return int"));
            }
            StackManipulation.Size operandStackSize = new StackManipulation.Compound(IntegerConstant.forValue(10), IntegerConstant.forValue(50), ByteBuddyTutorialExamplesTest.IntegerSum.INSTANCE, MethodReturn.INTEGER).apply(methodVisitor, implementationContext);
            return new ByteCodeAppender.Size(operandStackSize.getMaximalSize(), instrumentedMethod.getStackSize());
        }
    }

    public enum SumImplementation implements Implementation {

        INSTANCE;
        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return ByteBuddyTutorialExamplesTest.SumMethod.INSTANCE;
        }
    }

    public enum ToStringAssigner implements Assigner {

        INSTANCE;
        public StackManipulation assign(TypeDescription.Generic source, TypeDescription.Generic target, Assigner.Typing typing) {
            if ((!(source.isPrimitive())) && (target.represents(String.class))) {
                MethodDescription toStringMethod = TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.named("toString")).getOnly();
                return MethodInvocation.invoke(toStringMethod).virtual(source.asErasure());
            } else {
                return INSTANCE;
            }
        }
    }

    public enum StringValueBinder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<ByteBuddyTutorialExamplesTest.StringValue> {

        INSTANCE;
        public Class<ByteBuddyTutorialExamplesTest.StringValue> getHandledType() {
            return ByteBuddyTutorialExamplesTest.StringValue.class;
        }

        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<ByteBuddyTutorialExamplesTest.StringValue> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            if (!(target.getType().asErasure().represents(String.class))) {
                throw new IllegalStateException((target + " makes wrong use of StringValue"));
            }
            StackManipulation constant = new TextConstant(annotation.loadSilent().value());
            return new MethodDelegationBinder.ParameterBinding.Anonymous(constant);
        }
    }

    public interface Forwarder<T, S> {
        T to(S target);
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface Unsafe {}

    @Retention(RetentionPolicy.RUNTIME)
    private @interface Secured {}

    @SuppressWarnings("unused")
    public interface Interceptor2 {
        String doSomethingElse();
    }

    @SuppressWarnings("unused")
    public interface InterceptionAccessor {
        ByteBuddyTutorialExamplesTest.Interceptor2 getInterceptor();

        void setInterceptor(ByteBuddyTutorialExamplesTest.Interceptor2 interceptor);
    }

    @SuppressWarnings("unused")
    public interface InstanceCreator {
        Object makeInstance();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface RuntimeDefinition {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface StringValue {
        String value();
    }

    private @interface Rebase {}

    public interface Function {
        Object apply(Object arg);
    }

    public static class GreetingInterceptor {
        public Object greet(Object argument) {
            return "Hello from " + argument;
        }
    }

    @SuppressWarnings("unchecked")
    public static class FooReloading {
        public String m() {
            return "foo";
        }
    }

    @SuppressWarnings("unchecked")
    public static class BarReloading {
        public String m() {
            return "bar";
        }
    }

    @SuppressWarnings("unused")
    public static class Account {
        private int amount = 100;

        @ByteBuddyTutorialExamplesTest.Unsafe
        public String transfer(int amount, String recipient) {
            this.amount -= amount;
            return (("transferred $" + amount) + " to ") + recipient;
        }
    }

    @SuppressWarnings("unused")
    public static class Bank {
        public static String obfuscate(@Argument(1)
        String recipient, @Argument(0)
        Integer amount, @Super
        ByteBuddyTutorialExamplesTest.Account zuper) {
            // System.out.println("Transfer " + amount + " to " + recipient);
            return (zuper.transfer(amount, ((recipient.substring(0, 3)) + "XXX"))) + " (obfuscated)";
        }
    }

    private static class GettingStartedNamingStrategy extends NamingStrategy.AbstractBase {
        @Override
        protected String name(TypeDescription superClass) {
            return "i.love.ByteBuddy." + (superClass.getSimpleName());
        }
    }

    @SuppressWarnings("unused")
    public static class Foo {
        public String foo() {
            return null;
        }

        public String foo(Object o) {
            return null;
        }

        public String bar() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class Source {
        public String hello(String name) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class Target {
        public static String hello(String name) {
            return ("Hello " + name) + "!";
        }
    }

    @SuppressWarnings("unused")
    public static class Target2 {
        public static String intercept(String name) {
            return ("Hello " + name) + "!";
        }

        public static String intercept(int i) {
            return Integer.toString(i);
        }

        public static String intercept(Object o) {
            return o.toString();
        }
    }

    public static class MemoryDatabase {
        public List<String> load(String info) {
            return Arrays.asList((info + ": foo"), (info + ": bar"));
        }
    }

    public static class LoggerInterceptor {
        public static List<String> log(@SuperCall
        Callable<List<String>> zuper) throws Exception {
            ByteBuddyTutorialExamplesTest.println("Calling database");
            try {
                return zuper.call();
            } finally {
                ByteBuddyTutorialExamplesTest.println("Returned from database");
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ChangingLoggerInterceptor {
        public static List<String> log(String info, @Super
        ByteBuddyTutorialExamplesTest.MemoryDatabase zuper) {
            ByteBuddyTutorialExamplesTest.println("Calling database");
            try {
                return zuper.load((info + " (logged access)"));
            } finally {
                ByteBuddyTutorialExamplesTest.println("Returned from database");
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Loop {
        public String loop(String value) {
            return value;
        }

        public int loop(int value) {
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class Interceptor {
        @RuntimeType
        public static Object intercept(@RuntimeType
        Object value) {
            ByteBuddyTutorialExamplesTest.println(("Invoked method with: " + value));
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class UserType {
        public String doSomething() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class HelloWorldInterceptor implements ByteBuddyTutorialExamplesTest.Interceptor2 {
        public String doSomethingElse() {
            return "Hello World!";
        }
    }

    private static class RuntimeDefinitionImpl implements ByteBuddyTutorialExamplesTest.RuntimeDefinition {
        public Class<? extends Annotation> annotationType() {
            return ByteBuddyTutorialExamplesTest.RuntimeDefinition.class;
        }
    }

    public abstract static class SumExample {
        public abstract int calculate();
    }

    public static class ToStringInterceptor {
        public static String makeString(@ByteBuddyTutorialExamplesTest.StringValue("Hello!")
        String value) {
            return value;
        }
    }

    /* empty */
    public static class UnloadedBar {}

    public class ForwardingLoggerInterceptor {
        private final ByteBuddyTutorialExamplesTest.MemoryDatabase memoryDatabase;

        public ForwardingLoggerInterceptor(ByteBuddyTutorialExamplesTest.MemoryDatabase memoryDatabase) {
            this.memoryDatabase = memoryDatabase;
        }

        public List<String> log(@Pipe
        ByteBuddyTutorialExamplesTest.Forwarder<List<String>, ByteBuddyTutorialExamplesTest.MemoryDatabase> pipe) {
            ByteBuddyTutorialExamplesTest.println("Calling database");
            try {
                return pipe.to(memoryDatabase);
            } finally {
                ByteBuddyTutorialExamplesTest.println("Returned from database");
            }
        }
    }

    @SuppressWarnings("unchecked")
    class LoggingMemoryDatabase extends ByteBuddyTutorialExamplesTest.MemoryDatabase {
        public List<String> load(String info) {
            try {
                return ByteBuddyTutorialExamplesTest.LoggerInterceptor.log(new ByteBuddyTutorialExamplesTest.LoggingMemoryDatabase.LoadMethodSuperCall(info));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        private class LoadMethodSuperCall implements Callable {
            private final String info;

            private LoadMethodSuperCall(String info) {
                this.info = info;
            }

            public Object call() throws Exception {
                return ByteBuddyTutorialExamplesTest.LoggingMemoryDatabase.super.load(info);
            }
        }
    }
}

