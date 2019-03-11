package net.bytebuddy.asm;


import OpenedClassReader.ASM_API;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.pool.TypePool.Empty.INSTANCE;


public class TypeReferenceAdjustmentTest {
    private static final String FOO = "foo";

    @Test
    public void testSuperClass() {
        new ByteBuddy().subclass(TypeReferenceAdjustmentTest.Foo.Bar.class).visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Foo.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test
    public void testInterface() {
        new ByteBuddy().subclass(TypeReferenceAdjustmentTest.Qux.Baz.class).visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Qux.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test
    public void testAnnotation() {
        new ByteBuddy().subclass(Object.class).annotateType(ofType(TypeReferenceAdjustmentTest.Qux.class).build()).visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Qux.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test
    public void testFieldType() {
        new ByteBuddy().subclass(Object.class).defineField(TypeReferenceAdjustmentTest.FOO, TypeReferenceAdjustmentTest.Foo.class).visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Foo.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test
    public void testFieldAnnotationType() {
        new ByteBuddy().subclass(Object.class).modifiers(TypeManifestation.ABSTRACT).defineField(TypeReferenceAdjustmentTest.FOO, TypeReferenceAdjustmentTest.Foo.class).annotateField(ofType(TypeReferenceAdjustmentTest.Qux.class).build()).visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Qux.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test
    public void testMethodReturnType() {
        new ByteBuddy().subclass(Object.class).modifiers(TypeManifestation.ABSTRACT).defineMethod(TypeReferenceAdjustmentTest.FOO, TypeReferenceAdjustmentTest.Foo.class).withoutCode().visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Foo.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test
    public void testMethodParameterType() {
        new ByteBuddy().subclass(Object.class).modifiers(TypeManifestation.ABSTRACT).defineMethod(TypeReferenceAdjustmentTest.FOO, void.class).withParameters(TypeReferenceAdjustmentTest.Foo.class).withoutCode().visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Foo.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test
    public void testMethodAnnotationType() {
        new ByteBuddy().subclass(Object.class).modifiers(TypeManifestation.ABSTRACT).defineMethod(TypeReferenceAdjustmentTest.FOO, void.class).withoutCode().annotateMethod(ofType(TypeReferenceAdjustmentTest.Qux.class).build()).visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Qux.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test
    public void testMethodParameterAnnotationType() {
        new ByteBuddy().subclass(Object.class).modifiers(TypeManifestation.ABSTRACT).defineMethod(TypeReferenceAdjustmentTest.FOO, void.class).withParameter(Object.class).annotateParameter(ofType(TypeReferenceAdjustmentTest.Qux.class).build()).withoutCode().visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Qux.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test
    public void testConstantType() {
        new ByteBuddy().subclass(Object.class).modifiers(TypeManifestation.ABSTRACT).defineMethod(TypeReferenceAdjustmentTest.FOO, void.class).intercept(FixedValue.value(TypeReferenceAdjustmentTest.Foo.class)).visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(false, TypeReferenceAdjustmentTest.Foo.class)).visit(TypeReferenceAdjustment.strict()).make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStrictCannotFindType() {
        new ByteBuddy().subclass(TypeReferenceAdjustmentTest.Foo.Bar.class).visit(TypeReferenceAdjustment.strict()).make(INSTANCE);
    }

    @Test
    public void testRelaxedCannotFindType() {
        new ByteBuddy().subclass(TypeReferenceAdjustmentTest.Foo.Bar.class).visit(TypeReferenceAdjustment.relaxed()).make(INSTANCE);
    }

    @Test
    public void testFilter() {
        new ByteBuddy().subclass(TypeReferenceAdjustmentTest.Foo.Bar.class).visit(new TypeReferenceAdjustmentTest.AssertionVisitorWrapper(true, TypeReferenceAdjustmentTest.Foo.class)).visit(TypeReferenceAdjustment.strict().filter(ElementMatchers.is(TypeReferenceAdjustmentTest.Foo.class))).make();
    }

    public static class Foo {
        /* empty */
        public static class Bar {}
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Qux {
        /* empty */
        interface Baz {}
    }

    private static class AssertionVisitorWrapper extends AsmVisitorWrapper.AbstractBase {
        private final boolean inverted;

        private final Set<String> internalNames;

        private AssertionVisitorWrapper(boolean inverted, Class<?>... types) {
            this.inverted = inverted;
            internalNames = new HashSet<String>();
            for (Class<?> type : types) {
                internalNames.add(Type.getInternalName(type));
            }
        }

        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            return new TypeReferenceAdjustmentTest.AssertionVisitorWrapper.AssertionClassVisitor(classVisitor);
        }

        private class AssertionClassVisitor extends ClassVisitor {
            private final Set<String> visited = new HashSet<String>();

            private AssertionClassVisitor(ClassVisitor classVisitor) {
                super(ASM_API, classVisitor);
            }

            @Override
            public void visitInnerClass(String internalName, String outerName, String innerName, int modifiers) {
                visited.add(internalName);
            }

            @Override
            public void visitEnd() {
                if (inverted ? Collections.disjoint(internalNames, visited) : !(visited.containsAll(internalNames))) {
                    Set<String> missing = new HashSet<String>(internalNames);
                    missing.removeAll(visited);
                    throw new AssertionError(("Missing internal type references: " + missing));
                }
            }
        }
    }
}

