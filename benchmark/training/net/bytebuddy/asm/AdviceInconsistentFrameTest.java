package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER_PERSISTENT;


@RunWith(Parameterized.class)
public class AdviceInconsistentFrameTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private final Class<?> trivial;

    private final Class<?> discarding;

    private final Class<?> copying;

    private final Class<?> retaining;

    public AdviceInconsistentFrameTest(Class<?> trivial, Class<?> discarding, Class<?> copying, Class<?> retaining) {
        this.trivial = trivial;
        this.discarding = discarding;
        this.copying = copying;
        this.retaining = retaining;
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameTooShortTrivial() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.TooShortMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(trivial).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameDropImplicitTrivial() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.DropImplicitMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(trivial).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameInconsistentThisParameterTrivial() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.InconsistentThisReferenceMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(trivial).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameInconsistentParameterTrivial() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).withParameters(Void.class).intercept(new AdviceInconsistentFrameTest.InconsistentParameterReferenceMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO, Void.class).invoke(null, ((Object) (null))), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(trivial).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameTooShortTrivialDiscarding() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.TooShortMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(discarding).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameDropImplicitTrivialDiscarding() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.DropImplicitMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(discarding).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameInconsistentThisParameterTrivialDiscarding() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.InconsistentThisReferenceMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(discarding).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameInconsistentParameterTrivialDiscarding() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).withParameters(Void.class).intercept(new AdviceInconsistentFrameTest.InconsistentParameterReferenceMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO, Void.class).invoke(null, ((Object) (null))), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(discarding).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameTooShortTrivialCopying() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.TooShortMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(copying).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameDropImplicitTrivialCopying() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.DropImplicitMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(copying).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameInconsistentThisParameterTrivialCopying() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.InconsistentThisReferenceMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(copying).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testFrameInconsistentParameterTrivialCopying() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).withParameters(Void.class).intercept(new AdviceInconsistentFrameTest.InconsistentParameterReferenceMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO, Void.class).invoke(null, ((Object) (null))), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(copying).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(7)
    public void testFrameTooShort() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.TooShortMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(retaining).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(7)
    public void testFrameDropImplicit() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.DropImplicitMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(retaining).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(7)
    public void testFrameInconsistentThisParameter() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC).intercept(new AdviceInconsistentFrameTest.InconsistentThisReferenceMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(retaining).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    @JavaVersionRule.Enforce(7)
    public void testFrameInconsistentParameter() throws Exception {
        Class<?> type = new ByteBuddy().subclass(Object.class).defineMethod(AdviceInconsistentFrameTest.FOO, String.class, Visibility.PUBLIC, Ownership.STATIC).withParameters(Void.class).intercept(new AdviceInconsistentFrameTest.InconsistentParameterReferenceMethod()).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER_PERSISTENT).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceInconsistentFrameTest.FOO, Void.class).invoke(null, ((Object) (null))), CoreMatchers.is(((Object) (AdviceInconsistentFrameTest.BAR))));
        new ByteBuddy().redefine(type).visit(Advice.to(retaining).on(ElementMatchers.named(AdviceInconsistentFrameTest.FOO))).make();
    }

    @SuppressWarnings("all")
    public static class TrivialAdvice {
        @Advice.OnMethodEnter
        public static void enter() {
            /* empty */
        }
    }

    @SuppressWarnings("all")
    public static class DiscardingAdvice {
        @Advice.OnMethodEnter
        public static boolean enter() {
            return false;
        }
    }

    @SuppressWarnings("all")
    public static class CopyingAdvice {
        @Advice.OnMethodEnter
        public static boolean enter() {
            return false;
        }

        @Advice.OnMethodExit
        public static void exit() {
            /* do nothing */
        }
    }

    @SuppressWarnings("all")
    public static class RetainingAdvice {
        @Advice.OnMethodEnter
        public static boolean enter() {
            return false;
        }

        @Advice.OnMethodExit(backupArguments = false)
        public static void exit() {
            /* do nothing */
        }
    }

    @SuppressWarnings("all")
    public static class DelegatingTrivialAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static void enter() {
            /* empty */
        }
    }

    @SuppressWarnings("all")
    public static class DelegatingDiscardingAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static boolean enter() {
            return false;
        }
    }

    @SuppressWarnings("all")
    public static class DelegatingCopyingAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static boolean enter() {
            return false;
        }

        @Advice.OnMethodExit(inline = false)
        public static void exit() {
            /* do nothing */
        }
    }

    @SuppressWarnings("all")
    public static class DelegatingRetainingAdvice {
        @Advice.OnMethodEnter(inline = false)
        public static boolean enter() {
            return false;
        }

        @Advice.OnMethodExit(backupArguments = false, inline = false)
        public static void exit() {
            /* do nothing */
        }
    }

    public static class TooShortMethod implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitFrame(Opcodes.F_FULL, 0, new Object[0], 0, new Object[0]);
            methodVisitor.visitLdcInsn(AdviceInconsistentFrameTest.BAR);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            return new ByteCodeAppender.Size(1, 2);
        }
    }

    public static class DropImplicitMethod implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitFrame(Opcodes.F_CHOP, 1, new Object[0], 0, null);
            methodVisitor.visitLdcInsn(AdviceInconsistentFrameTest.BAR);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            return new ByteCodeAppender.Size(1, 2);
        }
    }

    public static class InconsistentThisReferenceMethod implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitFrame(Opcodes.F_FULL, 1, new Object[]{ TypeDescription.OBJECT.getInternalName() }, 0, new Object[0]);
            methodVisitor.visitLdcInsn(AdviceInconsistentFrameTest.BAR);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            return new ByteCodeAppender.Size(1, 2);
        }
    }

    public static class InconsistentParameterReferenceMethod implements Implementation , ByteCodeAppender {
        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return this;
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return instrumentedType;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            methodVisitor.visitFrame(Opcodes.F_FULL, 1, new Object[]{ TypeDescription.OBJECT.getInternalName() }, 0, new Object[0]);
            methodVisitor.visitLdcInsn(AdviceInconsistentFrameTest.BAR);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            return new ByteCodeAppender.Size(1, 2);
        }
    }
}

