package net.bytebuddy.dynamic.scaffold;


import OpenedClassReader.ASM_API;
import java.util.HashSet;
import java.util.Set;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.OpenedClassReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.ClassVisitor;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;


@RunWith(Parameterized.class)
public class TypeWriterDeclarationPreservationTest {
    private final Class<?> type;

    public TypeWriterDeclarationPreservationTest(Class<?> type) {
        this.type = type;
    }

    @Test
    public void testRedefinition() throws Exception {
        TypeWriterDeclarationPreservationTest.TypeModifierExtractor typeModifierExtractor = new TypeWriterDeclarationPreservationTest.TypeModifierExtractor();
        OpenedClassReader.of(read(type)).accept(typeModifierExtractor, 0);
        new ByteBuddy().redefine(type).visit(new TypeWriterDeclarationPreservationTest.TypeValidator.Wrapper(typeModifierExtractor)).make();
    }

    @Test
    public void testRebasing() throws Exception {
        TypeWriterDeclarationPreservationTest.TypeModifierExtractor typeModifierExtractor = new TypeWriterDeclarationPreservationTest.TypeModifierExtractor();
        OpenedClassReader.of(read(type)).accept(typeModifierExtractor, 0);
        new ByteBuddy().rebase(type).visit(new TypeWriterDeclarationPreservationTest.TypeValidator.Wrapper(typeModifierExtractor)).make();
    }

    @Test
    public void testDecoration() throws Exception {
        TypeWriterDeclarationPreservationTest.TypeModifierExtractor typeModifierExtractor = new TypeWriterDeclarationPreservationTest.TypeModifierExtractor();
        OpenedClassReader.of(read(type)).accept(typeModifierExtractor, 0);
        decorate(type).visit(new TypeWriterDeclarationPreservationTest.TypeValidator.Wrapper(typeModifierExtractor)).make();
    }

    private static class InnerClassAttribute {
        private final String name;

        private final String outerName;

        private final String innerName;

        private final int modifiers;

        private InnerClassAttribute(String name, String outerName, String innerName, int modifiers) {
            this.name = name;
            this.outerName = outerName;
            this.innerName = innerName;
            this.modifiers = modifiers;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = (31 * result) + ((outerName) != null ? outerName.hashCode() : 0);
            result = (31 * result) + ((innerName) != null ? innerName.hashCode() : 0);
            result = (31 * result) + (modifiers);
            return result;
        }

        @Override
        public boolean equals(Object object) {
            if ((this) == object)
                return true;

            if ((object == null) || ((getClass()) != (object.getClass())))
                return false;

            TypeWriterDeclarationPreservationTest.InnerClassAttribute that = ((TypeWriterDeclarationPreservationTest.InnerClassAttribute) (object));
            if ((modifiers) != (that.modifiers))
                return false;

            if (!(name.equals(that.name)))
                return false;

            if ((outerName) != null ? !(outerName.equals(that.outerName)) : (that.outerName) != null)
                return false;

            return (innerName) != null ? innerName.equals(that.innerName) : (that.innerName) == null;
        }

        @Override
        public String toString() {
            return ((((((((((("InnerClassAttribute{" + "name='") + (name)) + '\'') + ", outerName='") + (outerName)) + '\'') + ", innerName='") + (innerName)) + '\'') + ", modifiers=") + (modifiers)) + '}';
        }
    }

    private static class OuterClassAttribute {
        private final String type;

        private final String methodName;

        private final String methodDescriptor;

        private OuterClassAttribute(String type, String methodName, String methodDescriptor) {
            this.type = type;
            this.methodName = methodName;
            this.methodDescriptor = methodDescriptor;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = (31 * result) + ((methodName) != null ? methodName.hashCode() : 0);
            result = (31 * result) + ((methodDescriptor) != null ? methodDescriptor.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(Object object) {
            if ((this) == object)
                return true;

            if ((object == null) || ((getClass()) != (object.getClass())))
                return false;

            TypeWriterDeclarationPreservationTest.OuterClassAttribute that = ((TypeWriterDeclarationPreservationTest.OuterClassAttribute) (object));
            if (!(type.equals(that.type)))
                return false;

            if ((methodName) != null ? !(methodName.equals(that.methodName)) : (that.methodName) != null)
                return false;

            return (methodDescriptor) != null ? methodDescriptor.equals(that.methodDescriptor) : (that.methodDescriptor) == null;
        }

        @Override
        public String toString() {
            return ((((((((("OuterClassAttribute{" + "type='") + (type)) + '\'') + ", methodName='") + (methodName)) + '\'') + ", methodDescriptor='") + (methodDescriptor)) + '\'') + '}';
        }
    }

    private static class TypeModifierExtractor extends ClassVisitor {
        public int modifiers;

        private final Set<TypeWriterDeclarationPreservationTest.InnerClassAttribute> innerClassAttributes = new HashSet<TypeWriterDeclarationPreservationTest.InnerClassAttribute>();

        private TypeWriterDeclarationPreservationTest.OuterClassAttribute outerClassAttribute;

        private TypeModifierExtractor() {
            super(ASM_API);
        }

        @Override
        public void visit(int version, int modifiers, String name, String signature, String superName, String[] interfaceName) {
            this.modifiers = modifiers;
        }

        @Override
        public void visitOuterClass(String owner, String name, String descriptor) {
            outerClassAttribute = new TypeWriterDeclarationPreservationTest.OuterClassAttribute(owner, name, descriptor);
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int modifiers) {
            innerClassAttributes.add(new TypeWriterDeclarationPreservationTest.InnerClassAttribute(name, outerName, innerName, modifiers));
        }
    }

    private static class TypeValidator extends ClassVisitor {
        public final int modifiers;

        private final Set<TypeWriterDeclarationPreservationTest.InnerClassAttribute> innerClassAttributes;

        private TypeWriterDeclarationPreservationTest.OuterClassAttribute outerClassAttribute;

        private TypeValidator(ClassVisitor classVisitor, int modifiers, Set<TypeWriterDeclarationPreservationTest.InnerClassAttribute> innerClassAttributes, TypeWriterDeclarationPreservationTest.OuterClassAttribute outerClassAttribute) {
            super(ASM_API, classVisitor);
            this.modifiers = modifiers;
            this.innerClassAttributes = innerClassAttributes;
            this.outerClassAttribute = outerClassAttribute;
        }

        @Override
        public void visit(int version, int modifiers, String name, String signature, String superName, String[] interfaceName) {
            if (modifiers != (this.modifiers)) {
                throw new AssertionError(((("Unexpected modifiers: Observed " + modifiers) + " instead of ") + (this.modifiers)));
            }
            super.visit(version, modifiers, name, signature, superName, interfaceName);
        }

        @Override
        public void visitOuterClass(String owner, String name, String descriptor) {
            if (((outerClassAttribute) == null) || (!(outerClassAttribute.equals(new TypeWriterDeclarationPreservationTest.OuterClassAttribute(owner, name, descriptor))))) {
                throw new AssertionError(((((("Unexpected outer class: " + owner) + ", ") + name) + ", ") + descriptor));
            }
            outerClassAttribute = null;
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int modifiers) {
            if (!(innerClassAttributes.remove(new TypeWriterDeclarationPreservationTest.InnerClassAttribute(name, outerName, innerName, modifiers)))) {
                throw new AssertionError(((((((("Unexpected inner class attribute for " + name) + ", ") + outerName) + ", ") + innerName) + ", ") + modifiers));
            }
        }

        @Override
        public void visitEnd() {
            if (!(innerClassAttributes.isEmpty())) {
                throw new AssertionError(("Did not visit all inner class attributes: " + (innerClassAttributes)));
            } else
                if ((outerClassAttribute) != null) {
                    throw new AssertionError(("Did not visit outer class: " + (outerClassAttribute)));
                }

        }

        private static class Wrapper extends AsmVisitorWrapper.AbstractBase {
            public final int modifiers;

            private final Set<TypeWriterDeclarationPreservationTest.InnerClassAttribute> innerClassAttributes;

            private final TypeWriterDeclarationPreservationTest.OuterClassAttribute outerClassAttribute;

            private Wrapper(TypeWriterDeclarationPreservationTest.TypeModifierExtractor typeModifierExtractor) {
                modifiers = typeModifierExtractor.modifiers;
                innerClassAttributes = typeModifierExtractor.innerClassAttributes;
                outerClassAttribute = typeModifierExtractor.outerClassAttribute;
            }

            public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
                return new TypeWriterDeclarationPreservationTest.TypeValidator(classVisitor, modifiers, innerClassAttributes, outerClassAttribute);
            }
        }
    }
}

