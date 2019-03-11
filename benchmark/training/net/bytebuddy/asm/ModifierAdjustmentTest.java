package net.bytebuddy.asm;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


public class ModifierAdjustmentTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testTypeModifier() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withTypeModifiers(ElementMatchers.named(ModifierAdjustmentTest.Sample.class.getName()), Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getModifiers(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_STATIC))));
    }

    @Test
    public void testTypeModifierNotApplied() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withTypeModifiers(ElementMatchers.named(ModifierAdjustmentTest.FOO), Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getModifiers(), CoreMatchers.is(ModifierAdjustmentTest.Sample.class.getModifiers()));
    }

    @Test
    public void testTypeModifierUnqualified() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withTypeModifiers(Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getModifiers(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_STATIC))));
    }

    @Test
    public void testFieldModifier() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withFieldModifiers(ElementMatchers.named(ModifierAdjustmentTest.FOO), Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getDeclaredField(ModifierAdjustmentTest.FOO).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredField(ModifierAdjustmentTest.BAR).getModifiers(), CoreMatchers.is(0));
    }

    @Test
    public void testFieldModifierUnqualified() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withFieldModifiers(Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getDeclaredField(ModifierAdjustmentTest.FOO).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredField(ModifierAdjustmentTest.BAR).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
    }

    @Test
    public void testMethodModifier() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withMethodModifiers(ElementMatchers.named(ModifierAdjustmentTest.FOO), Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getDeclaredMethod(ModifierAdjustmentTest.FOO).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredMethod(ModifierAdjustmentTest.BAR).getModifiers(), CoreMatchers.is(0));
    }

    @Test
    public void testMethodModifierUnqualified() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withMethodModifiers(Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getDeclaredMethod(ModifierAdjustmentTest.FOO).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredMethod(ModifierAdjustmentTest.BAR).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
    }

    @Test
    public void testConstructorModifier() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withConstructorModifiers(ElementMatchers.takesArgument(0, Void.class), Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getDeclaredConstructor(Void.class).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredConstructor().getModifiers(), CoreMatchers.is(0));
    }

    @Test
    public void testConstructorModifierUnqualified() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withConstructorModifiers(Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getDeclaredConstructor(Void.class).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredConstructor().getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
    }

    @Test
    public void testInvokableModifier() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withInvokableModifiers(ElementMatchers.named(ModifierAdjustmentTest.FOO).or(ElementMatchers.takesArgument(0, Void.class)), Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getDeclaredMethod(ModifierAdjustmentTest.FOO).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredMethod(ModifierAdjustmentTest.BAR).getModifiers(), CoreMatchers.is(0));
        Assert.assertThat(type.getDeclaredConstructor(Void.class).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredConstructor().getModifiers(), CoreMatchers.is(0));
    }

    @Test
    public void testInvokableModifierUnqualified() throws Exception {
        Class<?> type = new ByteBuddy().redefine(ModifierAdjustmentTest.Sample.class).visit(new ModifierAdjustment().withInvokableModifiers(Visibility.PUBLIC)).make().load(ClassLoadingStrategy.BOOTSTRAP_LOADER, WRAPPER).getLoaded();
        Assert.assertThat(type.getDeclaredMethod(ModifierAdjustmentTest.FOO).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredMethod(ModifierAdjustmentTest.BAR).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredConstructor(Void.class).getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        Assert.assertThat(type.getDeclaredConstructor().getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
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

