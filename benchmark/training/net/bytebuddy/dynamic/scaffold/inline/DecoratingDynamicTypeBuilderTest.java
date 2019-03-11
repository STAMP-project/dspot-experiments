package net.bytebuddy.dynamic.scaffold.inline;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.OpenedClassReader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.CHILD_FIRST;


public class DecoratingDynamicTypeBuilderTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testDecoration() throws Exception {
        Object instance = decorate(DecoratingDynamicTypeBuilderTest.Foo.class).annotateType(ofType(DecoratingDynamicTypeBuilderTest.Qux.class).build()).ignoreAlso(new LatentMatcher.Resolved<MethodDescription>(ElementMatchers.none())).visit(new AsmVisitorWrapper.ForDeclaredMethods().method(ElementMatchers.named(DecoratingDynamicTypeBuilderTest.FOO), new AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper() {
            public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
                return new MethodVisitor(OpenedClassReader.ASM_API, methodVisitor) {
                    public void visitLdcInsn(Object value) {
                        if (DecoratingDynamicTypeBuilderTest.FOO.equals(value)) {
                            value = DecoratingDynamicTypeBuilderTest.BAR;
                        }
                        super.visitLdcInsn(value);
                    }
                };
            }
        })).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded().getConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass().getMethod(DecoratingDynamicTypeBuilderTest.FOO).invoke(instance), CoreMatchers.is(((Object) (DecoratingDynamicTypeBuilderTest.BAR))));
        MatcherAssert.assertThat(instance.getClass().isAnnotationPresent(DecoratingDynamicTypeBuilderTest.Bar.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.getClass().isAnnotationPresent(DecoratingDynamicTypeBuilderTest.Qux.class), CoreMatchers.is(true));
    }

    @Test
    public void testDecorationNonVirtualMember() throws Exception {
        Object instance = decorate(DecoratingDynamicTypeBuilderTest.Foo.class).annotateType(ofType(DecoratingDynamicTypeBuilderTest.Qux.class).build()).ignoreAlso(new LatentMatcher.Resolved<MethodDescription>(ElementMatchers.none())).visit(new AsmVisitorWrapper.ForDeclaredMethods().method(ElementMatchers.named(DecoratingDynamicTypeBuilderTest.BAR), new AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper() {
            public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
                return new MethodVisitor(OpenedClassReader.ASM_API, methodVisitor) {
                    @Override
                    public void visitLdcInsn(Object value) {
                        if (DecoratingDynamicTypeBuilderTest.FOO.equals(value)) {
                            value = DecoratingDynamicTypeBuilderTest.BAR;
                        }
                        super.visitLdcInsn(value);
                    }
                };
            }
        })).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded().getConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass().getMethod(DecoratingDynamicTypeBuilderTest.BAR).invoke(null), CoreMatchers.is(((Object) (DecoratingDynamicTypeBuilderTest.BAR))));
        MatcherAssert.assertThat(instance.getClass().isAnnotationPresent(DecoratingDynamicTypeBuilderTest.Bar.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.getClass().isAnnotationPresent(DecoratingDynamicTypeBuilderTest.Qux.class), CoreMatchers.is(true));
    }

    @Test
    public void testDecorationWithoutAnnotationRetention() throws Exception {
        Object instance = decorate(DecoratingDynamicTypeBuilderTest.Foo.class).annotateType(ofType(DecoratingDynamicTypeBuilderTest.Qux.class).build()).ignoreAlso(new LatentMatcher.Resolved<MethodDescription>(ElementMatchers.none())).visit(new AsmVisitorWrapper.ForDeclaredMethods().method(ElementMatchers.named(DecoratingDynamicTypeBuilderTest.FOO), new AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper() {
            public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor, Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {
                return new MethodVisitor(OpenedClassReader.ASM_API, methodVisitor) {
                    @Override
                    public void visitLdcInsn(Object value) {
                        if (DecoratingDynamicTypeBuilderTest.FOO.equals(value)) {
                            value = DecoratingDynamicTypeBuilderTest.BAR;
                        }
                        super.visitLdcInsn(value);
                    }
                };
            }
        })).make().load(getClass().getClassLoader(), CHILD_FIRST).getLoaded().getConstructor().newInstance();
        MatcherAssert.assertThat(instance.getClass().getMethod(DecoratingDynamicTypeBuilderTest.FOO).invoke(instance), CoreMatchers.is(((Object) (DecoratingDynamicTypeBuilderTest.BAR))));
        MatcherAssert.assertThat(instance.getClass().isAnnotationPresent(DecoratingDynamicTypeBuilderTest.Bar.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(instance.getClass().isAnnotationPresent(DecoratingDynamicTypeBuilderTest.Qux.class), CoreMatchers.is(true));
    }

    @Test
    public void testAuxiliaryTypes() throws Exception {
        Map<TypeDescription, byte[]> auxiliaryTypes = decorate(DecoratingDynamicTypeBuilderTest.Foo.class).require(TypeDescription.VOID, new byte[]{ 1, 2, 3 }).make().getAuxiliaryTypes();
        MatcherAssert.assertThat(auxiliaryTypes.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(auxiliaryTypes.get(TypeDescription.VOID).length, CoreMatchers.is(3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDecorationChangeName() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).name(DecoratingDynamicTypeBuilderTest.FOO);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDecorationChangeModifiers() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).modifiers(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDecorationChangeModifiersMerge() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).merge();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDecorationChangeInterface() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).implement(Runnable.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDecorationChange() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).implement(Runnable.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInnerClassChangeForTopLevel() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).topLevelType();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInnerClassChangeForType() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).innerTypeOf(Object.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInnerClassChangeForMethod() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).innerTypeOf(Object.class.getMethod("toString"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInnerClassChangeForConstructor() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).innerTypeOf(Object.class.getConstructor());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNestHost() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).nestHost(Object.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNestMember() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).nestMembers(Object.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDefineField() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).defineField(DecoratingDynamicTypeBuilderTest.FOO, Void.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInterceptField() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).field(ElementMatchers.any());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDefineMethod() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).defineMethod(DecoratingDynamicTypeBuilderTest.FOO, void.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDefineConstructor() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).defineConstructor();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInterceptInvokable() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).invokable(ElementMatchers.any());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTypeVariable() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).typeVariable(DecoratingDynamicTypeBuilderTest.FOO);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInitializer() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).initializer(Mockito.mock(ByteCodeAppender.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLoadedInitializer() throws Exception {
        decorate(DecoratingDynamicTypeBuilderTest.Foo.class).initializer(Mockito.mock(LoadedTypeInitializer.class));
    }

    @DecoratingDynamicTypeBuilderTest.Bar
    public static class Foo {
        public String foo() {
            return DecoratingDynamicTypeBuilderTest.FOO;
        }

        public static String bar() {
            return DecoratingDynamicTypeBuilderTest.FOO;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Bar {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Qux {}
}

