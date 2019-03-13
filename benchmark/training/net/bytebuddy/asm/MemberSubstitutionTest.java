package net.bytebuddy.asm;


import java.util.concurrent.Callable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.packaging.MemberSubstitutionTestHelper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;
import static net.bytebuddy.pool.TypePool.Empty.INSTANCE;


public class MemberSubstitutionTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String RUN = "run";

    @Test
    public void testFieldReadStub() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testStaticFieldReadStub() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testFieldReadWithFieldSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.FieldAccessSample.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
    }

    @Test
    public void testStaticFieldReadWithFieldSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.StaticFieldAccessSample.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
    }

    @Test
    public void testFieldReadWithMethodSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.FieldAccessSample.class.getDeclaredMethod(MemberSubstitutionTest.BAZ)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
    }

    @Test
    public void testStaticFieldReadWithMethodSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.StaticFieldAccessSample.class.getDeclaredMethod(MemberSubstitutionTest.BAZ)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
    }

    @Test
    public void testFieldWriteStub() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
    }

    @Test
    public void testStaticFieldWriteStub() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
    }

    @Test
    public void testFieldWriteWithFieldSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.FieldAccessSample.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testStaticFieldWriteWithFieldSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.StaticFieldAccessSample.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testFieldWriteWithMethodSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.FieldAccessSample.class.getDeclaredMethod(MemberSubstitutionTest.BAZ, String.class)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testStaticFieldWriteWithMethodSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.StaticFieldAccessSample.class.getDeclaredMethod(MemberSubstitutionTest.BAZ, String.class)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testFieldReadStubWithMatchedConstraint() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).onRead().stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testFieldReadStubWithNonMatchedConstraint() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).onWrite().stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testFieldWriteStubWithMatchedConstraint() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).onWrite().stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
    }

    @Test
    public void testFieldWriteStubWithNonMatchedConstraint() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.FieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).onRead().stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testMethodReadStub() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.MethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testStaticMethodReadStub() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticMethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testMethodReadWithFieldSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.MethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.MethodInvokeSample.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
    }

    @Test
    public void testStaticMethodReadWithFieldSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticMethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.StaticMethodInvokeSample.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
    }

    @Test
    public void testMethodReadWithMethodSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.MethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.MethodInvokeSample.class.getDeclaredMethod(MemberSubstitutionTest.BAZ)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
    }

    @Test
    public void testStaticMethodReadWithMethodSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticMethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.StaticMethodInvokeSample.class.getDeclaredMethod(MemberSubstitutionTest.BAZ)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
    }

    @Test
    public void testMethodWriteStub() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.MethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.BAR)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
    }

    @Test
    public void testStaticMethodWriteStub() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticMethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.BAR)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
    }

    @Test
    public void testMethodWriteWithFieldSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.MethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.MethodInvokeSample.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testStaticMethodWriteWithFieldSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticMethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.StaticMethodInvokeSample.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.QUX))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.QUX).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testMethodWriteWithMethodSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.MethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.MethodInvokeSample.class.getDeclaredMethod(MemberSubstitutionTest.BAZ, String.class)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(instance), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testStaticMethodWriteWithMethodSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.StaticMethodInvokeSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.StaticMethodInvokeSample.class.getDeclaredMethod(MemberSubstitutionTest.BAZ, String.class)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAZ))));
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.BAR))));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAZ).get(null), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testConstructorSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.ConstructorSubstitutionSample.class).visit(MemberSubstitution.strict().constructor(ElementMatchers.isDeclaredBy(Object.class)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testVirtualMethodSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.VirtualMethodSubstitutionSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.nullValue(Object.class));
    }

    @Test
    public void testVirtualMethodVirtualCallSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.VirtualMethodCallSubstitutionSample.Extension.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).onVirtualCall().stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(MemberSubstitutionTest.VirtualMethodCallSubstitutionSample.class)), WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void testVirtualMethodSuperCallSubstitution() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.VirtualMethodCallSubstitutionSample.Extension.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).onSuperCall().stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make().load(new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, readToNames(MemberSubstitutionTest.VirtualMethodCallSubstitutionSample.class)), WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.RUN).invoke(instance), CoreMatchers.is(((Object) (2))));
    }

    @Test
    public void testVirtualMethodSuperCallSubstitutionExternal() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.VirtualMethodCallSubstitutionExternalSample.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(Callable.class.getDeclaredMethod("call")).on(ElementMatchers.named(MemberSubstitutionTest.BAR))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getDeclaredConstructor().newInstance();
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.BAR, Callable.class).invoke(instance, new Callable<String>() {
            public String call() {
                return MemberSubstitutionTest.FOO;
            }
        }), CoreMatchers.is(((Object) (MemberSubstitutionTest.FOO))));
    }

    @Test
    public void testFieldMatched() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.MatcherSample.class).visit(replaceWithField(ElementMatchers.named(MemberSubstitutionTest.BAR)).on(ElementMatchers.named(MemberSubstitutionTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getConstructor().newInstance();
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.FOO).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).getInt(instance), CoreMatchers.is(0));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).getInt(instance), CoreMatchers.is(1));
    }

    @Test
    public void testMethodMatched() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.MatcherSample.class).visit(replaceWithMethod(ElementMatchers.named(MemberSubstitutionTest.BAR)).on(ElementMatchers.named(MemberSubstitutionTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getConstructor().newInstance();
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.FOO).invoke(instance), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).getInt(instance), CoreMatchers.is(0));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.BAR).getInt(instance), CoreMatchers.is(1));
    }

    @Test
    public void testMethodSelfDelegationSample() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberSubstitutionTest.MatcherSelfInvokeSample.class).visit(replaceWithInstrumentedMethod().on(ElementMatchers.named(MemberSubstitutionTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Object instance = type.getConstructor().newInstance();
        Assert.assertThat(type.getDeclaredMethod(MemberSubstitutionTest.FOO, int.class).invoke(instance, 0), CoreMatchers.nullValue(Object.class));
        Assert.assertThat(type.getDeclaredField(MemberSubstitutionTest.FOO).getInt(instance), CoreMatchers.is(1));
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldNotAccessible() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTestHelper.class.getDeclaredField(MemberSubstitutionTest.FOO)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldReadNotAssignable() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.ValidationTarget.class.getDeclaredField(MemberSubstitutionTest.BAR)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldWriteNotAssignable() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.ValidationTarget.class.getDeclaredField(MemberSubstitutionTest.BAR)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldReadNotCompatible() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.ValidationTarget.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldWriteNotCompatible() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.ValidationTarget.class.getDeclaredField(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodNotAccessible() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.ValidationTarget.class.getDeclaredMethod(MemberSubstitutionTest.FOO)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodArgumentsNotAssignable() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.FOO)).replaceWith(MemberSubstitutionTest.ValidationTarget.class.getDeclaredMethod(MemberSubstitutionTest.BAR, Void.class)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodReturnNotAssignable() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.ValidationTarget.class.getDeclaredMethod(MemberSubstitutionTest.BAR)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodNotCompatible() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.StaticFieldAccessSample.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).replaceWith(MemberSubstitutionTest.ValidationTarget.class.getDeclaredMethod(MemberSubstitutionTest.QUX)).on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorReplacement() throws Exception {
        MemberSubstitution.strict().field(ElementMatchers.any()).replaceWith(new MethodDescription.ForLoadedConstructor(Object.class.getDeclaredConstructor()));
    }

    @Test(expected = IllegalStateException.class)
    public void testOptionalField() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.OptionalTarget.class).visit(MemberSubstitution.strict().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make(INSTANCE);
    }

    @Test(expected = IllegalStateException.class)
    public void testOptionalMethod() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.OptionalTarget.class).visit(MemberSubstitution.strict().method(ElementMatchers.named(MemberSubstitutionTest.BAR)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make(INSTANCE);
    }

    @Test
    public void testOptionalFieldRelaxed() throws Exception {
        Assert.assertThat(new ByteBuddy().redefine(MemberSubstitutionTest.OptionalTarget.class).visit(MemberSubstitution.relaxed().field(ElementMatchers.named(MemberSubstitutionTest.BAR)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make(INSTANCE), CoreMatchers.notNullValue(DynamicType.class));
    }

    @Test
    public void testOptionalMethodRelaxed() throws Exception {
        Assert.assertThat(new ByteBuddy().redefine(MemberSubstitutionTest.OptionalTarget.class).visit(MemberSubstitution.relaxed().method(ElementMatchers.named(MemberSubstitutionTest.BAR)).stub().on(ElementMatchers.named(MemberSubstitutionTest.RUN))).make(INSTANCE), CoreMatchers.notNullValue(DynamicType.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testNoParametersNoMemberFieldMatch() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.ValidationTarget.class).visit(replaceWithField(ElementMatchers.any()).on(ElementMatchers.named(MemberSubstitutionTest.BAZ))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNoParametersNoMemberMethodMatch() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.ValidationTarget.class).visit(replaceWithMethod(ElementMatchers.any()).on(ElementMatchers.named(MemberSubstitutionTest.BAZ))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPrimitiveParametersNoMemberFieldMatch() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.ValidationTarget.class).visit(replaceWithField(ElementMatchers.any()).on(ElementMatchers.named(MemberSubstitutionTest.BAZ))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPrimitiveParametersNoMemberMethodMatch() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.ValidationTarget.class).visit(replaceWithMethod(ElementMatchers.any()).on(ElementMatchers.named(MemberSubstitutionTest.BAZ))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testArrayParametersNoMemberFieldMatch() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.ValidationTarget.class).visit(replaceWithField(ElementMatchers.any()).on(ElementMatchers.named(MemberSubstitutionTest.BAZ))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testArrayParametersNoMemberMethodMatch() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.ValidationTarget.class).visit(replaceWithMethod(ElementMatchers.any()).on(ElementMatchers.named(MemberSubstitutionTest.BAZ))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldMatchedNoTarget() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.MatcherSample.class).visit(replaceWithField(ElementMatchers.none()).on(ElementMatchers.named(MemberSubstitutionTest.FOO))).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodMatchedNoTarget() throws Exception {
        new ByteBuddy().redefine(MemberSubstitutionTest.MatcherSample.class).visit(replaceWithMethod(ElementMatchers.none()).on(ElementMatchers.named(MemberSubstitutionTest.FOO))).make();
    }

    public static class FieldAccessSample {
        public String foo = MemberSubstitutionTest.FOO;

        public String bar = MemberSubstitutionTest.BAR;

        public String qux = MemberSubstitutionTest.QUX;

        public String baz = MemberSubstitutionTest.BAZ;

        public void run() {
            bar = foo;
        }

        @SuppressWarnings("unused")
        private String baz() {
            return baz;
        }

        @SuppressWarnings("unused")
        private void baz(String baz) {
            this.baz = baz;
        }
    }

    public static class StaticFieldAccessSample {
        public static String foo = MemberSubstitutionTest.FOO;

        public static String bar = MemberSubstitutionTest.BAR;

        public static String qux = MemberSubstitutionTest.QUX;

        public static String baz = MemberSubstitutionTest.BAZ;

        public void run() {
            MemberSubstitutionTest.StaticFieldAccessSample.bar = MemberSubstitutionTest.StaticFieldAccessSample.foo;
        }

        @SuppressWarnings("unused")
        private static String baz() {
            return MemberSubstitutionTest.StaticFieldAccessSample.baz;
        }

        @SuppressWarnings("unused")
        private static void baz(String baz) {
            MemberSubstitutionTest.StaticFieldAccessSample.baz = baz;
        }
    }

    public static class MethodInvokeSample {
        public String foo = MemberSubstitutionTest.FOO;

        public String bar = MemberSubstitutionTest.BAR;

        public String qux = MemberSubstitutionTest.QUX;

        public String baz = MemberSubstitutionTest.BAZ;

        public void run() {
            bar(foo());
        }

        @SuppressWarnings("unused")
        private String foo() {
            return foo;
        }

        @SuppressWarnings("unused")
        private void bar(String bar) {
            this.bar = bar;
        }

        @SuppressWarnings("unused")
        private String baz() {
            return baz;
        }

        @SuppressWarnings("unused")
        private void baz(String baz) {
            this.baz = baz;
        }
    }

    public static class StaticMethodInvokeSample {
        public static String foo = MemberSubstitutionTest.FOO;

        public static String bar = MemberSubstitutionTest.BAR;

        public static String qux = MemberSubstitutionTest.QUX;

        public static String baz = MemberSubstitutionTest.BAZ;

        public void run() {
            MemberSubstitutionTest.StaticMethodInvokeSample.bar(MemberSubstitutionTest.StaticMethodInvokeSample.foo());
        }

        @SuppressWarnings("unused")
        private static String foo() {
            return MemberSubstitutionTest.StaticMethodInvokeSample.foo;
        }

        @SuppressWarnings("unused")
        private static void bar(String bar) {
            MemberSubstitutionTest.StaticMethodInvokeSample.bar = bar;
        }

        @SuppressWarnings("unused")
        private static String baz() {
            return MemberSubstitutionTest.StaticMethodInvokeSample.baz;
        }

        @SuppressWarnings("unused")
        private static void baz(String baz) {
            MemberSubstitutionTest.StaticMethodInvokeSample.baz = baz;
        }
    }

    public static class ConstructorSubstitutionSample {
        public Object run() {
            return new Object();
        }
    }

    public static class VirtualMethodSubstitutionSample {
        public Object run() {
            return foo();
        }

        public Object foo() {
            return MemberSubstitutionTest.FOO;
        }
    }

    public static class VirtualMethodCallSubstitutionSample {
        public int foo() {
            return 1;
        }

        public static class Extension extends MemberSubstitutionTest.VirtualMethodCallSubstitutionSample {
            public int foo() {
                return 2;
            }

            public int run() {
                return (foo()) + (super.foo());
            }
        }
    }

    public static class VirtualMethodCallSubstitutionExternalSample {
        public Object bar(Callable<String> argument) {
            return MemberSubstitutionTest.VirtualMethodCallSubstitutionExternalSample.foo(argument);
        }

        private static Object foo(Callable<String> argument) {
            throw new AssertionError();
        }
    }

    public static class MatcherSample {
        public int foo;

        public int bar;

        public void foo() {
            foo = 1;
        }

        public void bar(int value) {
            bar = value;
        }
    }

    public static class MatcherSelfInvokeSample {
        public int foo;

        public void foo(int value) {
            if (value == 0) {
                bar((value + 1));
            } else {
                foo = value;
            }
        }

        public void bar(int value) {
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unused")
    public static class ValidationTarget {
        private static String foo;

        public static Void bar;

        public String qux;

        private static String foo() {
            return null;
        }

        public static Void bar() {
            return null;
        }

        public static void bar(Void bar) {
            /* empty */
        }

        public String qux() {
            return null;
        }

        public static void foobar(int value) {
            /* empty */
        }

        public static void quxbaz(Object[] value) {
            /* empty */
        }

        public static String baz() {
            MemberSubstitutionTest.ValidationTarget.foobar(0);
            MemberSubstitutionTest.ValidationTarget.quxbaz(null);
            return MemberSubstitutionTest.ValidationTarget.foo();
        }
    }

    public static class OptionalTarget {
        public static void run() {
            MemberSubstitutionTest.ValidationTarget.bar = null;
            MemberSubstitutionTest.ValidationTarget.bar();
        }
    }
}

