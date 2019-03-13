package net.bytebuddy.asm;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class MemberRemovalTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testFieldRemoval() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberRemovalTest.Sample.class).visit(new MemberRemoval().stripFields(ElementMatchers.named(MemberRemovalTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredField(MemberRemovalTest.FOO);
            Assert.fail();
        } catch (NoSuchFieldException ignored) {
        }
        Assert.assertThat(type.getDeclaredField(MemberRemovalTest.BAR), CoreMatchers.notNullValue(Field.class));
    }

    @Test
    public void testMethodRemoval() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberRemovalTest.Sample.class).visit(new MemberRemoval().stripMethods(ElementMatchers.named(MemberRemovalTest.FOO))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredMethod(MemberRemovalTest.FOO);
            Assert.fail();
        } catch (NoSuchMethodException ignored) {
        }
        Assert.assertThat(type.getDeclaredMethod(MemberRemovalTest.BAR), CoreMatchers.notNullValue(Method.class));
    }

    @Test
    public void testConstructorRemoval() throws Exception {
        Class<?> type = new ByteBuddy().redefine(MemberRemovalTest.Sample.class).visit(new MemberRemoval().stripConstructors(ElementMatchers.takesArguments(0))).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        try {
            type.getDeclaredConstructor();
            Assert.fail();
        } catch (NoSuchMethodException ignored) {
        }
        Assert.assertThat(type.getDeclaredConstructor(Void.class), CoreMatchers.notNullValue(Constructor.class));
    }

    private static class Sample {
        Void foo;

        Void bar;

        Sample() {
            /* empty */
        }

        Sample(Void ignored) {
            /* empty */
        }

        void foo() {
            /* empty */
        }

        void bar() {
            /* empty */
        }
    }
}

