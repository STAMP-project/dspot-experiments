package net.bytebuddy.asm;


import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.packaging.AdviceTestHelper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class AdviceTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String ENTER = "enter";

    private static final String EXIT = "exit";

    private static final String INSIDE = "inside";

    private static final String THROWABLE = "throwable";

    private static final int VALUE = 42;

    private static final int IGNORED = 1;

    @Test
    public void testEmptyAdviceEntryAndExit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdvice.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceEntryAndExitWithEntrySuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceWithEntrySuppression.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceEntryAndExitWithExitSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceWithEntrySuppression.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceEntryAndExitWithSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceWithSuppression.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceEntryAndExitWithExceptionHandling() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceWithExceptionHandling.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceEntryAndExitWithExceptionHandlingAndEntrySuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceWithExceptionHandlingAndEntrySuppression.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceEntryAndExitWithExceptionHandlingAndExitSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceWithExceptionHandlingAndExitSuppression.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceEntryAndExitWithExceptionHandlingAndSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceWithExceptionHandlingAndSuppression.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceEntry() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceEntry.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceEntryWithSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceEntryWithSuppression.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceExit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceExit.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceExitAndSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceExitAndSuppression.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.SKIP_DEBUG)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceExitWithExceptionHandling() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceExitWithExceptionHandling.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testEmptyAdviceExitWithExceptionHandlingAndSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyMethod.class).visit(Advice.to(AdviceTest.EmptyAdviceExitWithExceptionHandlingAndSuppression.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testTrivialDelegation() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.EmptyDelegationAdvice.class).visit(Advice.to(AdviceTest.EmptyDelegationAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testTrivialAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testTrivialAdviceWithDelegation() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.TrivialAdviceDelegation.class).visit(Advice.to(AdviceTest.TrivialAdviceDelegation.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testTrivialAdviceWithSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdviceWithSuppression.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testTrivialAdviceDistributedEnterOnly() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class, AdviceTest.EmptyAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testTrivialAdviceDistributedExitOnly() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.EmptyAdvice.class, AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (0))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testTrivialAdviceWithDelegationEnterOnly() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.TrivialAdviceDelegation.class).visit(Advice.to(AdviceTest.TrivialAdviceDelegation.class, AdviceTest.EmptyAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testTrivialAdviceWithDelegationExitOnly() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.TrivialAdviceDelegation.class).visit(Advice.to(AdviceTest.EmptyAdvice.class, AdviceTest.TrivialAdviceDelegation.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (0))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testTrivialAdviceMultipleMethods() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.isMethod())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAZ).invoke(null), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (2))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void testTrivialAdviceMultipleMethodsWithSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdviceWithSuppression.class).on(ElementMatchers.isMethod())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAZ).invoke(null), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (2))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void testTrivialAdviceNested() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (2))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void testTrivialAdviceNestedWithSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdviceWithSuppression.class).on(ElementMatchers.named(AdviceTest.FOO))).visit(Advice.to(AdviceTest.TrivialAdviceWithSuppression.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (2))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void testTrivialAdviceWithHandler() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.BAZ))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(((AdviceTest.FOO) + (AdviceTest.BAZ))).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testTrivialAdviceWithHandlerAndSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdviceWithSuppression.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.BAZ))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(((AdviceTest.FOO) + (AdviceTest.BAZ))).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceOnConstructor() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdviceSkipException.class).on(ElementMatchers.isConstructor())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.notNullValue(Object.class));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceOnConstructorExitAdviceWithSuppression() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdviceSkipExceptionWithSuppression.class).on(ElementMatchers.isConstructor())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredConstructor().newInstance(), CoreMatchers.notNullValue(Object.class));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testFrameAdviceSimpleShift() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.FrameShiftAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testFrameAdviceSimpleShiftExpanded() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.FrameShiftAdvice.class).on(ElementMatchers.named(AdviceTest.FOO)).readerFlags(ClassReader.EXPAND_FRAMES)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceOnConstructorWithSuppressionNotLegal() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.isConstructor())).make();
    }

    @Test
    public void testAdviceWithImplicitArgument() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ArgumentAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.BAR), CoreMatchers.is(((Object) (AdviceTest.BAR))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithImplicitArgumentDelegation() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.ArgumentAdviceDelegationImplicit.class).visit(Advice.to(AdviceTest.ArgumentAdviceDelegationImplicit.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.BAR), CoreMatchers.is(((Object) (AdviceTest.BAR))));
    }

    @Test
    public void testAdviceWithExplicitArgument() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ArgumentAdviceExplicit.class).on(ElementMatchers.named(AdviceTest.QUX))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.QUX, String.class, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.FOO, AdviceTest.BAR), CoreMatchers.is(((Object) ((AdviceTest.FOO) + (AdviceTest.BAR)))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithIncrement() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.IncrementSample.class).visit(Advice.to(AdviceTest.IncrementAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO, int.class).invoke(type.getDeclaredConstructor().newInstance(), 0), CoreMatchers.is(((Object) (3))));
    }

    @Test
    public void testAdviceWithThisReference() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ThisReferenceAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithOptionalThisReferenceNonOptional() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OptionalThisReferenceAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithOptionalThisReferenceOptional() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OptionalThisReferenceAdvice.class).on(ElementMatchers.named(AdviceTest.BAZ))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAZ).invoke(null), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithReturnValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ReturnValueAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (0))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithExceptionHandler() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ExceptionHandlerAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceWithExceptionHandlerNested() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ExceptionHandlerAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).visit(Advice.to(AdviceTest.ExceptionHandlerAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (2))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void testAdviceNotSkipExceptionImplicit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.BAR))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(((AdviceTest.FOO) + (AdviceTest.BAR))).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceNotSkipExceptionImplicitNested() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.BAR))))).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.BAR))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(((AdviceTest.FOO) + (AdviceTest.BAR))).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (2))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void testAdviceSkipExceptionImplicit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdviceSkipException.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.BAR))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(((AdviceTest.FOO) + (AdviceTest.BAR))).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testAdviceNotSkipExceptionExplicit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(((AdviceTest.BAR) + (AdviceTest.BAZ))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(((AdviceTest.BAR) + (AdviceTest.BAZ))).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(NullPointerException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceNotSkipExceptionExplicitNested() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(((AdviceTest.BAR) + (AdviceTest.BAZ))))).visit(Advice.to(AdviceTest.TrivialAdvice.class).on(ElementMatchers.named(((AdviceTest.BAR) + (AdviceTest.BAZ))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(((AdviceTest.BAR) + (AdviceTest.BAZ))).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(NullPointerException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (2))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void testAdviceSkipExceptionExplicit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdviceSkipException.class).on(ElementMatchers.named(((AdviceTest.BAR) + (AdviceTest.BAZ))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(((AdviceTest.BAR) + (AdviceTest.BAZ))).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(NullPointerException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testAdviceSkipExceptionDoesNotSkipNonException() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.TrivialAdviceSkipException.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testObsoleteReturnValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ObsoleteReturnValueAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testUnusedReturnValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.UnusedReturnValueAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testExceptionWhenNotThrown() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ThrowableAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.THROWABLE).get(null), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testExceptionWhenThrown() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ThrowableAdvice.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.BAR))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(((AdviceTest.FOO) + (AdviceTest.BAR))).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.THROWABLE).get(null), CoreMatchers.instanceOf(RuntimeException.class));
    }

    @Test
    public void testAdviceThrowOnEnter() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.TracableSample.class).visit(Advice.to(AdviceTest.ThrowOnEnter.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.INSIDE).get(null), CoreMatchers.is(((Object) (0))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testAdviceThrowOnExit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.TracableSample.class).visit(Advice.to(AdviceTest.ThrowOnExit.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.INSIDE).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceThrowSuppressed() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.TracableSample.class).visit(Advice.to(AdviceTest.ThrowSuppressed.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.INSIDE).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAdviceThrowNotSuppressedOnEnter() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.TracableSample.class).visit(Advice.to(AdviceTest.ThrowNotSuppressedOnEnter.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(Exception.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.INSIDE).get(null), CoreMatchers.is(((Object) (0))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testAdviceThrowNotSuppressedOnExit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.TracableSample.class).visit(Advice.to(AdviceTest.ThrowNotSuppressedOnExit.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(Exception.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.INSIDE).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testThisValueSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Box.class).visit(Advice.to(AdviceTest.ThisSubstitutionAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor(String.class).newInstance(AdviceTest.FOO)), CoreMatchers.is(((Object) (AdviceTest.BAR))));
    }

    @Test
    public void testThisValueSubstitutionOptional() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Box.class).visit(Advice.to(AdviceTest.ThisOptionalSubstitutionAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor(String.class).newInstance(AdviceTest.FOO)), CoreMatchers.is(((Object) (AdviceTest.BAR))));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalThisValueSubstitution() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Box.class).visit(Advice.to(AdviceTest.IllegalThisSubstitutionAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test
    public void testParameterValueSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Box.class).visit(Advice.to(AdviceTest.ParameterSubstitutionAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAR, String.class).invoke(null, AdviceTest.FOO), CoreMatchers.is(((Object) (AdviceTest.BAR))));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalParameterValueSubstitution() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Box.class).visit(Advice.to(AdviceTest.IllegalParameterSubstitutionAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make();
    }

    @Test
    public void testReturnValueSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ReturnSubstitutionAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.FOO), CoreMatchers.is(((Object) (AdviceTest.BAR))));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalReturnValueSubstitution() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.IllegalReturnSubstitutionAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test
    public void testFieldAdviceImplicit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.FieldSample.class).visit(Advice.to(AdviceTest.FieldAdviceImplicit.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testFieldAdviceExplicit() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.FieldSample.class).visit(Advice.to(AdviceTest.FieldAdviceExplicit.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testAllArgumentsStackSizeAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.BoxedArgumentsStackSizeAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.FOO), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testAllArgumentsObjectTypeAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.BoxedArgumentsObjectTypeAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.FOO), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testAllArgumentsStackSizeEmptyAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.BoxedArgumentsStackSizeAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testOriginAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OriginAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testOriginCustomAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OriginCustomAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testOriginMethodStackSizeAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OriginMethodStackSizeAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.FOO), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testOriginMethodStackSizeEmptyAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OriginMethodStackSizeAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testOriginConstructorStackSizeAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OriginConstructorStackSizeAdvice.class).on(ElementMatchers.isConstructor())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testOriginMethodAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OriginMethodAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.FOO), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test(expected = IllegalStateException.class)
    public void testOriginMethodNonAssignableAdvice() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OriginMethodAdvice.class).on(ElementMatchers.isConstructor())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testOriginConstructorNonAssignableAdvice() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OriginConstructorAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make();
    }

    @Test
    public void testOriginConstructorAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.OriginConstructorAdvice.class).on(ElementMatchers.isConstructor())).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testExceptionSuppressionAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ExceptionSuppressionAdvice.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.BAR))))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(((AdviceTest.FOO) + (AdviceTest.BAR))).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testExceptionTypeAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.ExceptionTypeAdvice.class).visit(Advice.to(AdviceTest.ExceptionTypeAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(IllegalStateException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testExceptionNotCatchedAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.ExceptionNotCatchedAdvice.class).visit(Advice.to(AdviceTest.ExceptionNotCatchedAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(Exception.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (0))));
    }

    @Test
    public void testExceptionCatchedAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.ExceptionCatchedAdvice.class).visit(Advice.to(AdviceTest.ExceptionCatchedAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(RuntimeException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testExceptionCatchedWithExchangeAdvice() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.ExceptionCatchedWithExchangeAdvice.class).visit(Advice.to(AdviceTest.ExceptionCatchedWithExchangeAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(IOException.class));
        }
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.ENTER).get(null), CoreMatchers.is(((Object) (1))));
        MatcherAssert.assertThat(type.getDeclaredField(AdviceTest.EXIT).get(null), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testNonAssignableCasting() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.NonAssignableReturnTypeAdvice.class).visit(Advice.to(AdviceTest.NonAssignableReturnTypeAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(ClassCastException.class));
        }
    }

    @Test
    public void testTrivialAssignableCasting() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.TrivialReturnTypeAdvice.class).visit(Advice.to(AdviceTest.TrivialReturnTypeAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.BAR))));
    }

    @Test
    public void testPrimitiveNonAssignableCasting() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.NonAssignablePrimitiveReturnTypeAdvice.class).visit(Advice.to(AdviceTest.NonAssignablePrimitiveReturnTypeAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance());
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(ClassCastException.class));
        }
    }

    @Test
    public void testUserNullValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, ((Object) (null))).to(AdviceTest.CustomAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testUserTypeValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, Object.class).to(AdviceTest.CustomAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testUserEnumValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, RetentionPolicy.CLASS).to(AdviceTest.CustomAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testUserSerializableTypeValue() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bindSerialized(AdviceTest.Custom.class, ((Serializable) (Collections.singletonMap(AdviceTest.FOO, AdviceTest.BAR)))).to(AdviceTest.CustomSerializableAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testUserStackManipulation() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, ClassConstant.of(TypeDescription.OBJECT), Object.class).to(AdviceTest.CustomAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.BAR), CoreMatchers.is(((Object) (AdviceTest.BAR))));
    }

    @Test
    public void testUserOffsetMapping() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, new Advice.OffsetMapping.ForStackManipulation(ClassConstant.of(TypeDescription.OBJECT), TypeDescription.STRING.asGenericType(), TypeDescription.STRING.asGenericType(), STATIC)).to(AdviceTest.CustomAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.BAR, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.BAR), CoreMatchers.is(((Object) (AdviceTest.BAR))));
    }

    @Test
    public void testLineNumberPrepend() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.LineNumberAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testLineNumberNoPrepend() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.NoLineNumberAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testLineNumberPrependDelegation() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.LineNumberDelegatingAdvice.class).visit(Advice.to(AdviceTest.LineNumberDelegatingAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testLineNumberNoPrependDelegation() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.NoLineNumberDelegatingAdvice.class).visit(Advice.to(AdviceTest.NoLineNumberDelegatingAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testExceptionPrinting() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ExceptionWriterTest.class).withExceptionPrinting().on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        PrintStream printStream = Mockito.mock(PrintStream.class);
        PrintStream err = System.err;
        synchronized(System.err) {
            System.setErr(printStream);
            try {
                MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
            } finally {
                System.setErr(err);
            }
        }
        Mockito.verify(printStream, Mockito.times(2)).println(Mockito.any(RuntimeException.class));
    }

    @Test
    public void testOptionalArgument() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.OptionalArgumentAdvice.class).visit(Advice.to(AdviceTest.OptionalArgumentAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO).invoke(type.getDeclaredConstructor().newInstance()), CoreMatchers.is(((Object) (AdviceTest.FOO))));
    }

    @Test
    public void testParameterAnnotations() throws Exception {
        Class<?> type = new ByteBuddy().redefine(AdviceTest.ParameterAnnotationSample.class).visit(Advice.to(AdviceTest.ParameterAnnotationSample.class).on(ElementMatchers.named(AdviceTest.FOO))).make().load(AdviceTest.ParameterAnnotationSample.SampleParameter.class.getClassLoader(), CHILD_FIRST).getLoaded();
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO, String.class).invoke(type.getDeclaredConstructor().newInstance(), AdviceTest.FOO), CoreMatchers.is(((Object) (AdviceTest.FOO))));
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO, String.class).getParameterAnnotations().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO, String.class).getParameterAnnotations()[0].length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getDeclaredMethod(AdviceTest.FOO, String.class).getParameterAnnotations()[0][0], CoreMatchers.instanceOf(AdviceTest.ParameterAnnotationSample.SampleParameter.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testUserSerializableTypeValueNonAssignable() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, Collections.singletonList(AdviceTest.FOO)).to(AdviceTest.CustomSerializableAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalUserValue() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, new Object()).to(AdviceTest.CustomAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonAssignableStringValue() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.FOO).to(AdviceTest.CustomPrimitiveAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonAssignableTypeValue() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, Object.class).to(AdviceTest.CustomPrimitiveAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonAssignableTypeDescriptionValue() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, TypeDescription.OBJECT).to(AdviceTest.CustomPrimitiveAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonAssignableSerializableValue() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, new ArrayList<String>()).to(AdviceTest.CustomPrimitiveAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testInvisibleField() throws Exception {
        new ByteBuddy().redefine(AdviceTest.SampleExtension.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTestHelper.class.getDeclaredField("object")).to(AdviceTest.CustomAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonRelatedField() throws Exception {
        new ByteBuddy().redefine(AdviceTest.TracableSample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.Sample.class.getDeclaredField("object")).to(AdviceTest.CustomAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonAssignableField() throws Exception {
        new ByteBuddy().redefine(AdviceTest.SampleExtension.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.SampleExtension.class.getDeclaredField(AdviceTest.FOO)).to(AdviceTest.CustomPrimitiveAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodNegativeIndex() throws Exception {
        Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.Sample.class.getDeclaredMethod(AdviceTest.BAR, String.class), (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodOverflowIndex() throws Exception {
        Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.Sample.class.getDeclaredMethod(AdviceTest.BAR, String.class), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstrcutorNegativeIndex() throws Exception {
        Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.Sample.class.getDeclaredConstructor(), (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorOverflowIndex() throws Exception {
        Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.Sample.class.getDeclaredConstructor(), 0);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonAssignableParameter() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.Sample.class.getDeclaredMethod(AdviceTest.BAR, String.class), 0).to(AdviceTest.CustomPrimitiveAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnrelatedMethodParameter() throws Exception {
        new ByteBuddy().redefine(AdviceTest.SampleExtension.class).visit(Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.Sample.class.getDeclaredMethod(AdviceTest.BAR, String.class), 0).to(AdviceTest.CustomPrimitiveAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithThisReferenceOnConstructor() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ThisReferenceAdvice.class).on(ElementMatchers.isConstructor())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithFieldOnConstructor() throws Exception {
        new ByteBuddy().redefine(AdviceTest.FieldSample.class).visit(Advice.to(AdviceTest.FieldAdviceExplicit.class).on(ElementMatchers.isConstructor())).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithExceptionCatchOnConstructor() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.EmptyAdviceExitWithExceptionHandling.class).on(ElementMatchers.isConstructor())).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdviceWithoutAnnotations() throws Exception {
        Advice.to(Object.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testDuplicateAdvice() throws Exception {
        Advice.to(AdviceTest.DuplicateAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testIOExceptionOnRead() throws Exception {
        ClassFileLocator classFileLocator = Mockito.mock(ClassFileLocator.class);
        Mockito.when(classFileLocator.locate(AdviceTest.TrivialAdvice.class.getName())).thenThrow(new IOException());
        Advice.to(AdviceTest.TrivialAdvice.class, classFileLocator);
    }

    @Test(expected = IllegalStateException.class)
    public void testNonStaticAdvice() throws Exception {
        Advice.to(AdviceTest.NonStaticAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAmbiguousAdvice() throws Exception {
        Advice.to(AdviceTest.AmbiguousAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAmbiguousAdviceDelegation() throws Exception {
        Advice.to(AdviceTest.AmbiguousAdviceDelegation.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotBindEnterToReturn() throws Exception {
        Advice.to(AdviceTest.EnterToReturnAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonExistentArgument() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.IllegalArgumentAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonAssignableParameterImplicit() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.IllegalArgumentAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonAssignableParameter() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.IllegalArgumentWritableAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonEqualParameter() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.IllegalArgumentReadOnlyAdvice.class).on(ElementMatchers.named(AdviceTest.BAR))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceThisReferenceNonExistent() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.ThisReferenceAdvice.class).on(ElementMatchers.named(AdviceTest.BAZ))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonAssignableThisReference() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.IllegalThisReferenceAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonEqualThisReference() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.IllegalThisReferenceWritableAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonAssignableReturnValue() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.NonAssignableReturnAdvice.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.QUX))))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAdviceWithNonAssignableReturnValueWritable() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.NonEqualReturnWritableAdvice.class).on(ElementMatchers.named(((AdviceTest.FOO) + (AdviceTest.QUX))))).make();
    }

    @Test
    public void testAdviceAbstractMethodIsSkipped() throws Exception {
        Advice.Dispatcher.Resolved.ForMethodEnter methodEnter = Mockito.mock(Advice.Dispatcher.Resolved.ForMethodEnter.class);
        Advice.Dispatcher.Resolved.ForMethodExit methodExit = Mockito.mock(Advice.Dispatcher.Resolved.ForMethodExit.class);
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(methodDescription.isAbstract()).thenReturn(true);
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        MatcherAssert.assertThat(new Advice(methodEnter, methodExit).wrap(Mockito.mock(TypeDescription.class), methodDescription, methodVisitor, Mockito.mock(Implementation.Context.class), Mockito.mock(TypePool.class), AdviceTest.IGNORED, AdviceTest.IGNORED), CoreMatchers.sameInstance(methodVisitor));
    }

    @Test
    public void testAdviceNativeMethodIsSkipped() throws Exception {
        Advice.Dispatcher.Resolved.ForMethodEnter methodEnter = Mockito.mock(Advice.Dispatcher.Resolved.ForMethodEnter.class);
        Advice.Dispatcher.Resolved.ForMethodExit methodExit = Mockito.mock(Advice.Dispatcher.Resolved.ForMethodExit.class);
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(methodDescription.isNative()).thenReturn(true);
        MethodVisitor methodVisitor = Mockito.mock(MethodVisitor.class);
        MatcherAssert.assertThat(new Advice(methodEnter, methodExit).wrap(Mockito.mock(TypeDescription.class), methodDescription, methodVisitor, Mockito.mock(Implementation.Context.class), Mockito.mock(TypePool.class), AdviceTest.IGNORED, AdviceTest.IGNORED), CoreMatchers.sameInstance(methodVisitor));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalThrowableRequest() throws Exception {
        Advice.to(AdviceTest.IllegalThrowableRequestAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalThrowableType() throws Exception {
        Advice.to(AdviceTest.IllegalThrowableTypeAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldIllegalExplicit() throws Exception {
        new ByteBuddy().redefine(AdviceTest.FieldSample.class).visit(Advice.to(AdviceTest.FieldAdviceIllegalExplicit.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldNonExistent() throws Exception {
        new ByteBuddy().redefine(AdviceTest.FieldSample.class).visit(Advice.to(AdviceTest.FieldAdviceNonExistent.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldNonAssignable() throws Exception {
        new ByteBuddy().redefine(AdviceTest.FieldSample.class).visit(Advice.to(AdviceTest.FieldAdviceNonAssignable.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldWrite() throws Exception {
        new ByteBuddy().redefine(AdviceTest.FieldSample.class).visit(Advice.to(AdviceTest.FieldAdviceWrite.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalOriginType() throws Exception {
        Advice.to(AdviceTest.IllegalOriginType.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalOriginPattern() throws Exception {
        Advice.to(AdviceTest.IllegalOriginPattern.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalOriginPatternEnd() throws Exception {
        Advice.to(AdviceTest.IllegalOriginPatternEnd.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotWriteOrigin() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.IllegalOriginWriteAdvice.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateRegistration() throws Exception {
        Advice.withCustomMapping().bind(AdviceTest.Custom.class, AdviceTest.FOO).bind(AdviceTest.Custom.class, AdviceTest.FOO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAnnotationType() throws Exception {
        Advice.withCustomMapping().bind(Annotation.class, ((Serializable) (null)));
    }

    @Test(expected = IllegalStateException.class)
    public void testInlineAdviceCannotWriteParameter() throws Exception {
        Advice.to(AdviceTest.IllegalArgumentWritableInlineAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testInlineAdviceCannotWriteThis() throws Exception {
        Advice.to(AdviceTest.IllegalThisWritableInlineAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testInlineAdviceCannotWriteField() throws Exception {
        Advice.to(AdviceTest.IllegalFieldWritableInlineAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testInlineAdviceCannotWriteThrow() throws Exception {
        Advice.to(AdviceTest.IllegalThrowWritableInlineAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testInlineAdviceCannotWriteReturn() throws Exception {
        Advice.to(AdviceTest.IllegalThrowWritableInlineAdvice.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testInvisibleDelegationAdvice() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTestHelper.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonResolvedAdvice() throws Exception {
        Advice.to(of(AdviceTest.TrivialAdvice.class));
    }

    @Test
    public void testCannotInstantiateSuppressionMarker() throws Exception {
        Class<?> type = Class.forName(((Advice.class.getName()) + "$NoExceptionHandler"));
        MatcherAssert.assertThat(Modifier.isPrivate(type.getModifiers()), CoreMatchers.is(true));
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            MatcherAssert.assertThat(Modifier.isPrivate(constructor.getModifiers()), CoreMatchers.is(true));
            constructor.setAccessible(true);
            constructor.newInstance();
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testCannotInstantiateSkipDefaultValueMarker() throws Exception {
        try {
            Constructor<?> constructor = Advice.OnDefaultValue.class.getDeclaredConstructor();
            MatcherAssert.assertThat(Modifier.isPrivate(constructor.getModifiers()), CoreMatchers.is(true));
            constructor.setAccessible(true);
            constructor.newInstance();
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testCannotInstantiateSkipNonDefaultValueMarker() throws Exception {
        try {
            Constructor<?> constructor = Advice.OnNonDefaultValue.class.getDeclaredConstructor();
            MatcherAssert.assertThat(Modifier.isPrivate(constructor.getModifiers()), CoreMatchers.is(true));
            constructor.setAccessible(true);
            constructor.newInstance();
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            MatcherAssert.assertThat(exception.getCause(), CoreMatchers.instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDistributedAdviceNoEnterAdvice() throws Exception {
        Advice.to(Object.class, AdviceTest.EmptyAdvice.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDistributedAdviceNoExitAdvice() throws Exception {
        Advice.to(AdviceTest.EmptyAdvice.class, Object.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalBoxedReturn() throws Exception {
        Advice.to(AdviceTest.IllegalBoxedReturnType.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testBoxedArgumentsWriteDelegateEntry() throws Exception {
        Advice.to(AdviceTest.BoxedArgumentsWriteDelegateEntry.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testBoxedArgumentsWriteDelegateExit() throws Exception {
        Advice.to(AdviceTest.BoxedArgumentsWriteDelegateExit.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testBoxedArgumentsCannotWrite() throws Exception {
        new ByteBuddy().redefine(AdviceTest.Sample.class).visit(Advice.to(AdviceTest.BoxedArgumentsCannotWrite.class).on(ElementMatchers.named(AdviceTest.FOO))).make();
    }

    @SuppressWarnings("unused")
    public static class Sample {
        private Object object;

        public static int enter;

        public static int exit;

        public static Throwable throwable;

        public String foo() {
            return AdviceTest.FOO;
        }

        public String foobar() {
            throw new RuntimeException();
        }

        public String bar(String argument) {
            return argument;
        }

        public String qux(String first, String second) {
            return first + second;
        }

        public static String baz() {
            return AdviceTest.FOO;
        }

        public String quxbaz() {
            String foo = AdviceTest.FOO;
            String bar = AdviceTest.BAR;
            String qux = AdviceTest.QUX;
            String baz = AdviceTest.BAZ;
            return ((foo + bar) + qux) + baz;
        }

        public void fooqux() {
            /* do nothing */
        }

        public void barbaz() {
            object.getClass();// implicit null pointer

        }

        public String foobaz() {
            try {
                throw new Exception();
            } catch (Exception ignored) {
                return AdviceTest.FOO;
            }
        }
    }

    public static class SampleExtension extends AdviceTest.Sample {
        public Object foo;

        public String foo() {
            return null;
        }
    }

    public static class TracableSample {
        public static int enter;

        public static int exit;

        public static int inside;

        public void foo() {
            (AdviceTest.TracableSample.inside)++;
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyDelegationAdvice {
        public void foo() {
            /* empty */
        }

        @Advice.OnMethodEnter
        private static void enter() {
            /* empty */
        }

        @Advice.OnMethodExit
        private static void exit() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class TrivialAdvice {
        @Advice.OnMethodEnter
        private static void enter() {
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        private static void exit() {
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class TrivialAdviceWithSuppression {
        @Advice.OnMethodEnter(suppress = Exception.class)
        private static void enter() {
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit(suppress = Exception.class)
        private static void exit() {
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class TrivialAdviceDelegation {
        public static int enter;

        public static int exit;

        public String foo() {
            return AdviceTest.FOO;
        }

        @Advice.OnMethodEnter(inline = false)
        private static void enter() {
            (AdviceTest.TrivialAdviceDelegation.enter)++;
        }

        @Advice.OnMethodExit(inline = false, onThrowable = Exception.class)
        private static void exit() {
            (AdviceTest.TrivialAdviceDelegation.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyMethod {
        public void foo() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdvice {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceDelegation {
        public void foo() {
            /* do nothing */
        }

        @Advice.OnMethodEnter(inline = false)
        @Advice.OnMethodExit(inline = false)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceWithEntrySuppression {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        @Advice.OnMethodExit
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceWithExitSuppression {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit(suppress = Throwable.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceWithSuppression {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit(suppress = Throwable.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceWithExceptionHandling {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit(onThrowable = Exception.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceWithExceptionHandlingAndEntrySuppression {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        @Advice.OnMethodExit(onThrowable = Exception.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceWithExceptionHandlingAndExitSuppression {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Exception.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceWithExceptionHandlingAndSuppression {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Exception.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceEntry {
        @Advice.OnMethodEnter
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceEntryWithSuppression {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceExit {
        @Advice.OnMethodExit
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceExitAndSuppression {
        @Advice.OnMethodExit(suppress = Throwable.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceExitWithExceptionHandling {
        @Advice.OnMethodExit(onThrowable = Exception.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class EmptyAdviceExitWithExceptionHandlingAndSuppression {
        @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Exception.class)
        private static void advice() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class TrivialAdviceSkipException {
        @Advice.OnMethodEnter
        private static void enter() {
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit() {
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class TrivialAdviceSkipExceptionWithSuppression {
        @Advice.OnMethodEnter(suppress = Exception.class)
        private static void enter() {
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit(suppress = Exception.class)
        private static void exit() {
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class ArgumentAdvice {
        public static int enter;

        public static int exit;

        @Advice.OnMethodEnter
        private static void enter(String argument) {
            if (!(argument.equals(AdviceTest.BAR))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(String argument) {
            if (!(argument.equals(AdviceTest.BAR))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class ArgumentAdviceExplicit {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Argument(1)
        String argument) {
            if (!(argument.equals(AdviceTest.BAR))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Argument(1)
        String argument) {
            if (!(argument.equals(AdviceTest.BAR))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class ArgumentAdviceDelegationImplicit {
        public String foo(String value) {
            return value;
        }

        @Advice.OnMethodEnter(inline = false)
        private static void enter(String argument) {
            if (!(argument.equals(AdviceTest.BAR))) {
                throw new AssertionError();
            }
        }

        @Advice.OnMethodExit(inline = false)
        private static void exit(String argument) {
            if (!(argument.equals(AdviceTest.BAR))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class IncrementSample {
        public int foo(int value) {
            return ++value;
        }
    }

    @SuppressWarnings("unused")
    public static class IncrementAdvice {
        @Advice.OnMethodEnter
        private static int enter(@Advice.Argument(value = 0, readOnly = false)
        int argument, @Advice.Unused
        int ignored) {
            if ((++argument) != 1) {
                throw new AssertionError();
            }
            if ((++ignored) != 0) {
                throw new AssertionError();
            }
            int value = 0;
            if ((++value) != 1) {
                throw new AssertionError();
            }
            return value;
        }

        @Advice.OnMethodExit
        private static int exit(@Advice.Return(readOnly = false)
        int argument, @Advice.Unused
        int ignored) {
            if ((++argument) != 3) {
                throw new AssertionError();
            }
            if ((++ignored) != 0) {
                throw new AssertionError();
            }
            int value = 0;
            if ((++value) != 1) {
                throw new AssertionError();
            }
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class ThisReferenceAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.This
        AdviceTest.Sample thiz) {
            if (thiz == null) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.This
        AdviceTest.Sample thiz) {
            if (thiz == null) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class OptionalThisReferenceAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.This(optional = true)
        AdviceTest.Sample thiz) {
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.This(optional = true)
        AdviceTest.Sample thiz) {
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class ReturnValueAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        String value) {
            if (!(value.equals(AdviceTest.FOO))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class ExceptionHandlerAdvice {
        @Advice.OnMethodEnter(suppress = Throwable.class)
        private static void enter() {
            try {
                throw new Exception();
            } catch (Exception ignored) {
                (AdviceTest.Sample.enter)++;
            }
        }

        @Advice.OnMethodExit(suppress = Throwable.class)
        private static void exit() {
            try {
                throw new Exception();
            } catch (Exception ignored) {
                (AdviceTest.Sample.exit)++;
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ObsoleteReturnValueAdvice {
        @Advice.OnMethodEnter
        private static int enter() {
            (AdviceTest.Sample.enter)++;
            return AdviceTest.VALUE;
        }
    }

    @SuppressWarnings("unused")
    public static class UnusedReturnValueAdvice {
        @Advice.OnMethodEnter
        private static int enter() {
            (AdviceTest.Sample.enter)++;
            return AdviceTest.VALUE;
        }

        @Advice.OnMethodExit
        private static void exit() {
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class ThrowableAdvice {
        @Advice.OnMethodExit(onThrowable = Exception.class)
        private static void exit(@Advice.Thrown
        Throwable throwable) {
            AdviceTest.Sample.throwable = throwable;
        }
    }

    @SuppressWarnings("unused")
    public static class ThrowOnEnter {
        @Advice.OnMethodEnter
        private static void enter() {
            (AdviceTest.TracableSample.enter)++;
            throw new RuntimeException();
        }

        @Advice.OnMethodExit
        private static void exit() {
            (AdviceTest.TracableSample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class ThrowOnExit {
        @Advice.OnMethodEnter
        private static void enter() {
            (AdviceTest.TracableSample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit() {
            (AdviceTest.TracableSample.exit)++;
            throw new RuntimeException();
        }
    }

    @SuppressWarnings("unused")
    public static class ThrowSuppressed {
        @Advice.OnMethodEnter(suppress = RuntimeException.class)
        private static void enter() {
            (AdviceTest.TracableSample.enter)++;
            throw new RuntimeException();
        }

        @Advice.OnMethodExit(suppress = RuntimeException.class)
        private static void exit() {
            (AdviceTest.TracableSample.exit)++;
            throw new RuntimeException();
        }
    }

    @SuppressWarnings("unused")
    public static class ThrowNotSuppressedOnEnter {
        @Advice.OnMethodEnter(suppress = RuntimeException.class)
        private static void enter() throws Exception {
            (AdviceTest.TracableSample.enter)++;
            throw new Exception();
        }
    }

    @SuppressWarnings("unused")
    public static class ThrowNotSuppressedOnExit {
        @Advice.OnMethodEnter(suppress = RuntimeException.class)
        private static void enter() {
            (AdviceTest.TracableSample.enter)++;
            throw new RuntimeException();
        }

        @Advice.OnMethodExit(suppress = RuntimeException.class)
        private static void exit() throws Exception {
            (AdviceTest.TracableSample.exit)++;
            throw new Exception();
        }
    }

    @SuppressWarnings("unused")
    public static class ThisSubstitutionAdvice {
        @Advice.OnMethodEnter
        @SuppressWarnings("all")
        private static void enter(@Advice.This(readOnly = false)
        AdviceTest.Box box) {
            box = new AdviceTest.Box(AdviceTest.BAR);
        }
    }

    @SuppressWarnings("unused")
    public static class ThisOptionalSubstitutionAdvice {
        @Advice.OnMethodEnter
        @SuppressWarnings("all")
        private static void enter(@Advice.This(readOnly = false, optional = true)
        AdviceTest.Box box) {
            box = new AdviceTest.Box(AdviceTest.BAR);
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalThisSubstitutionAdvice {
        @Advice.OnMethodEnter
        @SuppressWarnings("all")
        private static void enter(@Advice.This
        AdviceTest.Box box) {
            box = new AdviceTest.Box(AdviceTest.BAR);
        }
    }

    @SuppressWarnings("unused")
    public static class ParameterSubstitutionAdvice {
        @Advice.OnMethodEnter
        @SuppressWarnings("all")
        private static void enter(@Advice.Argument(value = 0, readOnly = false)
        String value) {
            value = AdviceTest.BAR;
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalParameterSubstitutionAdvice {
        @Advice.OnMethodEnter
        @SuppressWarnings("all")
        private static void enter(@Advice.Argument(0)
        String value) {
            value = AdviceTest.BAR;
        }
    }

    @SuppressWarnings("unused")
    public static class ReturnSubstitutionAdvice {
        @Advice.OnMethodExit
        @SuppressWarnings("all")
        private static void exit(@Advice.Return(readOnly = false)
        String value) {
            value = AdviceTest.BAR;
            if (!(value.equals(AdviceTest.BAR))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalReturnSubstitutionAdvice {
        @Advice.OnMethodExit
        @SuppressWarnings("all")
        private static void exit(@Advice.Return
        String value) {
            value = AdviceTest.BAR;
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class Box {
        public final String value;

        public Box(String value) {
            this.value = value;
        }

        public String foo() {
            return value;
        }

        public static String bar(String value) {
            return value;
        }
    }

    @SuppressWarnings("unused")
    public static class FieldSample {
        public static int enter;

        public static int exit;

        private String foo = AdviceTest.FOO;

        public String foo() {
            return foo;
        }

        public static String bar() {
            return AdviceTest.BAR;
        }
    }

    @SuppressWarnings("unused")
    public static class FieldAdviceImplicit {
        @Advice.OnMethodEnter
        private static void enter(@Advice.FieldValue("foo")
        String foo) {
            (AdviceTest.FieldSample.enter)++;
            if (!(foo.equals(AdviceTest.FOO))) {
                throw new AssertionError();
            }
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.FieldValue("foo")
        String foo) {
            (AdviceTest.FieldSample.exit)++;
            if (!(foo.equals(AdviceTest.FOO))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class FieldAdviceExplicit {
        @Advice.OnMethodEnter
        private static void enter(@Advice.FieldValue(value = "foo", declaringType = AdviceTest.FieldSample.class)
        String foo) {
            (AdviceTest.FieldSample.enter)++;
            if (!(foo.equals(AdviceTest.FOO))) {
                throw new AssertionError();
            }
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.FieldValue(value = "foo", declaringType = AdviceTest.FieldSample.class)
        String foo) {
            (AdviceTest.FieldSample.exit)++;
            if (!(foo.equals(AdviceTest.FOO))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class OriginAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Origin
        String origin) throws Exception {
            if (!(origin.equals(AdviceTest.Sample.class.getDeclaredMethod(AdviceTest.FOO).toString()))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Origin
        String origin) throws Exception {
            if (!(origin.equals(AdviceTest.Sample.class.getDeclaredMethod(AdviceTest.FOO).toString()))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class OriginCustomAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Origin("#t #m #d #r #s")
        String origin, @Advice.Origin
        Class<?> type) throws Exception {
            if (!(origin.equals(((((((((AdviceTest.Sample.class.getName()) + " ") + (AdviceTest.FOO)) + " ()L") + (String.class.getName().replace('.', '/'))) + "; ") + (String.class.getName())) + " ()")))) {
                throw new AssertionError();
            }
            if (type != (AdviceTest.Sample.class)) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Origin("\\#\\#\\\\#m")
        String origin, @Advice.Origin
        Class<?> type) throws Exception {
            if (!(origin.equals(("##\\" + (AdviceTest.FOO))))) {
                throw new AssertionError();
            }
            if (type != (AdviceTest.Sample.class)) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class OriginMethodStackSizeAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Origin
        Method origin) throws Exception {
            Object ignored = origin;
        }
    }

    @SuppressWarnings("unused")
    public static class OriginConstructorStackSizeAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Origin
        Constructor<?> origin) throws Exception {
            Object ignored = origin;
        }
    }

    @SuppressWarnings("unused")
    public static class OriginMethodAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Origin
        Method origin) throws Exception {
            if (!(origin.equals(AdviceTest.Sample.class.getDeclaredMethod(AdviceTest.BAR, String.class)))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Origin
        Method origin) throws Exception {
            if (!(origin.equals(AdviceTest.Sample.class.getDeclaredMethod(AdviceTest.BAR, String.class)))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class OriginConstructorAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Origin
        Constructor<?> origin) throws Exception {
            if (!(origin.equals(AdviceTest.Sample.class.getDeclaredConstructor()))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.enter)++;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Origin
        Constructor<?> origin) throws Exception {
            if (!(origin.equals(AdviceTest.Sample.class.getDeclaredConstructor()))) {
                throw new AssertionError();
            }
            (AdviceTest.Sample.exit)++;
        }
    }

    @SuppressWarnings("all")
    public static class FrameShiftAdvice {
        @Advice.OnMethodEnter
        private static String enter() {
            int v0 = 0;
            if (v0 != 0) {
                // do nothing
            }
            return AdviceTest.BAR;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Enter
        String value) {
            if (!(value.equals(AdviceTest.BAR))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ExceptionTypeAdvice {
        public static int enter;

        public static int exit;

        public void foo() {
            throw new IllegalStateException();
        }

        @Advice.OnMethodEnter(suppress = UnsupportedOperationException.class)
        private static void enter() {
            (AdviceTest.ExceptionTypeAdvice.enter)++;
            throw new UnsupportedOperationException();
        }

        @Advice.OnMethodExit(suppress = ArrayIndexOutOfBoundsException.class, onThrowable = IllegalStateException.class)
        private static void exit() {
            (AdviceTest.ExceptionTypeAdvice.exit)++;
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    @SuppressWarnings("unused")
    public static class ExceptionNotCatchedAdvice {
        public static int enter;

        public static int exit;

        public void foo() throws Exception {
            throw new Exception();
        }

        @Advice.OnMethodEnter
        private static void enter() {
            (AdviceTest.ExceptionNotCatchedAdvice.enter)++;
        }

        @Advice.OnMethodExit(onThrowable = RuntimeException.class)
        private static void exit() {
            (AdviceTest.ExceptionNotCatchedAdvice.exit)++;
        }
    }

    @SuppressWarnings("unused")
    public static class ExceptionCatchedAdvice {
        public static int enter;

        public static int exit;

        public void foo() {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        private static void enter() {
            (AdviceTest.ExceptionCatchedAdvice.enter)++;
        }

        @Advice.OnMethodExit(onThrowable = Exception.class)
        private static void exit() {
            (AdviceTest.ExceptionCatchedAdvice.exit)++;
        }
    }

    @SuppressWarnings("all")
    public static class ExceptionCatchedWithExchangeAdvice {
        public static int enter;

        public static int exit;

        public void foo() {
            throw new RuntimeException();
        }

        @Advice.OnMethodEnter
        private static void enter() {
            (AdviceTest.ExceptionCatchedWithExchangeAdvice.enter)++;
        }

        @Advice.OnMethodExit(onThrowable = RuntimeException.class)
        private static void exit(@Advice.Thrown(readOnly = false)
        Throwable throwable) {
            (AdviceTest.ExceptionCatchedWithExchangeAdvice.exit)++;
            throwable = new IOException();
        }
    }

    @SuppressWarnings("all")
    public static class NonAssignableReturnTypeAdvice {
        public String foo() {
            return AdviceTest.FOO;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = new Object();
        }
    }

    @SuppressWarnings("all")
    public static class TrivialReturnTypeAdvice {
        public Object foo() {
            return AdviceTest.FOO;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = AdviceTest.BAR;
        }
    }

    @SuppressWarnings("all")
    public static class NonAssignablePrimitiveReturnTypeAdvice {
        public int foo() {
            return 0;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false, typing = DYNAMIC)
        Object value) {
            value = new Object();
        }
    }

    @SuppressWarnings("unused")
    public static class OptionalArgumentAdvice {
        public String foo() {
            return AdviceTest.FOO;
        }

        @Advice.OnMethodEnter
        private static void enter(@Advice.Argument(value = 0, optional = true)
        String value) {
            if (value != null) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ParameterAnnotationSample {
        public String foo(@AdviceTest.ParameterAnnotationSample.SampleParameter
        String value) {
            return value;
        }

        @Advice.OnMethodExit
        private static void exit(@Advice.Unused
        Void ignored1, @Advice.Unused
        Void ignored2) {
            if ((ignored1 != null) || (ignored2 != null)) {
                throw new AssertionError();
            }
        }

        @Retention(RetentionPolicy.RUNTIME)
        public @interface SampleParameter {}
    }

    @SuppressWarnings("unused")
    public static class FieldAdviceIllegalExplicit {
        @Advice.OnMethodEnter
        private static void enter(@Advice.FieldValue(value = "bar", declaringType = Void.class)
        String bar) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class FieldAdviceNonExistent {
        @Advice.OnMethodEnter
        private static void enter(@Advice.FieldValue("bar")
        String bar) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class FieldAdviceNonAssignable {
        @Advice.OnMethodEnter
        private static void enter(@Advice.FieldValue("foo")
        Void foo) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("all")
    public static class FieldAdviceWrite {
        @Advice.OnMethodEnter
        private static void enter(@Advice.FieldValue("foo")
        String foo) {
            foo = AdviceTest.BAR;
        }
    }

    @SuppressWarnings("all")
    public static class ExceptionSuppressionAdvice {
        @Advice.OnMethodExit(onThrowable = Exception.class)
        private static void exit(@Advice.Thrown(readOnly = false)
        Throwable throwable) {
            throwable = null;
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalArgumentAdvice {
        @Advice.OnMethodEnter
        private static void enter(Void argument) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalArgumentReadOnlyAdvice {
        @SuppressWarnings("unused")
        @Advice.OnMethodEnter
        private static void enter(@Advice.Argument(0)
        Void argument) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalArgumentWritableAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Argument(value = 0, readOnly = false)
        Object argument) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class DuplicateAdvice {
        @Advice.OnMethodEnter
        private static void enter1() {
            throw new AssertionError();
        }

        @Advice.OnMethodEnter
        private static void enter2() {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class NonStaticAdvice {
        @Advice.OnMethodEnter
        private void enter() {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalThisReferenceAdvice {
        @Advice.OnMethodExit
        private static void enter(@Advice.This
        Void thiz) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalThisReferenceWritableAdvice {
        @Advice.OnMethodExit
        private static void enter(@Advice.This(readOnly = false)
        Object thiz) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class AmbiguousAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Argument(0)
        @Advice.This
        Object thiz) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class AmbiguousAdviceDelegation {
        @Advice.OnMethodEnter(inline = false)
        private static void enter(@Advice.Argument(0)
        @Advice.This
        Object thiz) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class EnterToReturnAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.Return
        Object value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class NonAssignableReturnAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return
        Void value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class NonEqualReturnWritableAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Return(readOnly = false)
        Object value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalThrowableRequestAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Thrown
        Throwable value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalThrowableTypeAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Thrown
        Object value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalOriginType {
        @Advice.OnMethodExit
        private static void exit(@Advice.Origin
        Void value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalOriginPattern {
        @Advice.OnMethodExit
        private static void exit(@Advice.Origin("#x")
        String value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalOriginPatternEnd {
        @Advice.OnMethodExit
        private static void exit(@Advice.Origin("#")
        String value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("all")
    public static class IllegalOriginWriteAdvice {
        @Advice.OnMethodExit
        private static void exit(@Advice.Origin
        String value) {
            value = null;
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalArgumentWritableInlineAdvice {
        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Argument(value = 0, readOnly = false)
        Object argument) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalThisWritableInlineAdvice {
        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.This(readOnly = false)
        Object argument) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalThrowWritableInlineAdvice {
        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Thrown(readOnly = false)
        Object argument) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalReturnWritableInlineAdvice {
        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Return(readOnly = false)
        Object argument) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class IllegalFieldWritableInlineAdvice {
        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.Argument(value = 0, readOnly = false)
        Object argument) {
            throw new AssertionError();
        }
    }

    public static class IllegalBoxedReturnType {
        @Advice.OnMethodEnter
        private static void advice(@Advice.StubValue
        int value) {
            throw new AssertionError();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Custom {}

    @SuppressWarnings("unused")
    public static class CustomAdvice {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit
        private static void advice(@AdviceTest.Custom
        Object value) {
            if (((value != (Object.class)) && (value != (RetentionPolicy.CLASS))) && (value != null)) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class CustomPrimitiveAdvice {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit
        private static void advice(@AdviceTest.Custom
        int value) {
            if (value == 0) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class CustomSerializableAdvice {
        @Advice.OnMethodEnter
        @Advice.OnMethodExit
        private static void advice(@AdviceTest.Custom
        Map<String, String> value) {
            if (((value.size()) != 1) && (!(value.get(AdviceTest.FOO).equals(AdviceTest.BAR)))) {
                throw new AssertionError();
            }
        }
    }

    @SuppressWarnings("unused")
    public static class NonVisibleAdvice {
        @Advice.OnMethodEnter(inline = false)
        private static void enter() {
            /* empty */
        }
    }

    public static class LineNumberAdvice {
        @Advice.OnMethodEnter
        private static void enter() {
            StackTraceElement top = new Throwable().getStackTrace()[0];
            if ((top.getLineNumber()) < 0) {
                throw new AssertionError();
            }
        }
    }

    public static class NoLineNumberAdvice {
        @Advice.OnMethodEnter(prependLineNumber = false)
        private static void enter() {
            StackTraceElement top = new Throwable().getStackTrace()[0];
            if ((top.getLineNumber()) >= 0) {
                throw new AssertionError();
            }
        }
    }

    public static class LineNumberDelegatingAdvice {
        public String foo() {
            return AdviceTest.FOO;
        }

        @Advice.OnMethodEnter(inline = false)
        private static void enter() {
            StackTraceElement top = new Throwable().getStackTrace()[1];
            if ((top.getLineNumber()) < 0) {
                throw new AssertionError();
            }
        }
    }

    public static class NoLineNumberDelegatingAdvice {
        public String foo() {
            return AdviceTest.FOO;
        }

        @Advice.OnMethodEnter(inline = false, prependLineNumber = false)
        private static void enter() {
            StackTraceElement top = new Throwable().getStackTrace()[1];
            if ((top.getLineNumber()) >= 0) {
                throw new AssertionError();
            }
        }
    }

    public static class BoxedArgumentsStackSizeAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments
        Object[] value) {
            Object ignored = value;
        }
    }

    public static class BoxedArgumentsObjectTypeAdvice {
        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments
        Object value) {
            Object ignored = value;
        }
    }

    public static class BoxedArgumentsWriteDelegateEntry {
        @Advice.OnMethodEnter(inline = false)
        private static void enter(@Advice.AllArguments(readOnly = false)
        Object[] value) {
            throw new AssertionError();
        }
    }

    public static class BoxedArgumentsWriteDelegateExit {
        @Advice.OnMethodExit(inline = false)
        private static void exit(@Advice.AllArguments(readOnly = false)
        Object[] value) {
            throw new AssertionError();
        }
    }

    public static class BoxedArgumentsCannotWrite {
        @Advice.OnMethodEnter
        private static void enter(@Advice.AllArguments
        Object[] value) {
            value = new Object[0];
        }
    }

    public static class ExceptionWriterTest {
        @Advice.OnMethodEnter(suppress = RuntimeException.class)
        @Advice.OnMethodExit(suppress = RuntimeException.class)
        private static void advice() {
            RuntimeException exception = new RuntimeException();
            exception.setStackTrace(new StackTraceElement[0]);
            throw exception;
        }
    }
}

