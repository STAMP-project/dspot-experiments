/**
 * Copyright ? 2013-2015 Esko Luontola <www.orfjackal.net>
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda;


import java.util.Optional;
import java.util.concurrent.Callable;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


@SuppressWarnings("UnusedDeclaration")
public class ClassAnalyzerTest {
    private final ClassAnalyzer analyzer = new ClassAnalyzer();

    @Test
    public void lists_interfaces_and_classes_separately() {
        analyze(ClassAnalyzerTest.Interface.class, ClassAnalyzerTest.InterfaceImplementer.class);
        MatcherAssert.assertThat("interfaces", getInterfaces(), is(ClassAnalyzerTest.classList(ClassAnalyzerTest.Interface.class)));
        MatcherAssert.assertThat("classes", getClasses(), is(ClassAnalyzerTest.classList(ClassAnalyzerTest.InterfaceImplementer.class)));
    }

    // Method inheritance
    @Test
    public void abstract_interface_method_inherited_and_implemented() {
        analyze(ClassAnalyzerTest.Interface.class, ClassAnalyzerTest.ChildInterface.class, ClassAnalyzerTest.InterfaceImplementer.class);
        MatcherAssert.assertThat("original", analyzer.getMethods(Type.getType(ClassAnalyzerTest.Interface.class)), hasItem(new MethodInfo("abstractMethod", "()V", ClassAnalyzerTest.Interface.class, new MethodKind.Abstract())));
        MatcherAssert.assertThat("inherits unchanged", analyzer.getMethods(Type.getType(ClassAnalyzerTest.ChildInterface.class)), hasItem(new MethodInfo("abstractMethod", "()V", ClassAnalyzerTest.Interface.class, new MethodKind.Abstract())));
        MatcherAssert.assertThat("implements", analyzer.getMethods(Type.getType(ClassAnalyzerTest.InterfaceImplementer.class)), hasItem(new MethodInfo("abstractMethod", "()V", ClassAnalyzerTest.InterfaceImplementer.class, new MethodKind.Implemented())));
    }

    private interface Interface {
        void abstractMethod();
    }

    private interface ChildInterface extends ClassAnalyzerTest.Interface {}

    private class InterfaceImplementer implements ClassAnalyzerTest.Interface {
        @Override
        public void abstractMethod() {
        }
    }

    @Test
    public void interface_method_types() {
        analyze(ClassAnalyzerTest.InterfaceMethodTypes.class);
        MatcherAssert.assertThat(analyzer.getMethods(Type.getType(ClassAnalyzerTest.InterfaceMethodTypes.class)), hasItems(new MethodInfo("abstractMethod", "()V", ClassAnalyzerTest.InterfaceMethodTypes.class, new MethodKind.Abstract()), new MethodInfo("defaultMethod", "()V", ClassAnalyzerTest.InterfaceMethodTypes.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.InterfaceMethodTypes$.class, "defaultMethod", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$InterfaceMethodTypes;)V")))));
    }

    @Test
    public void class_method_types() {
        analyze(ClassAnalyzerTest.ClassMethodTypes.class);
        // An abstract instance method takes precedence over a default method,
        // so we handle abstract instance methods the same way as concrete instance methods.
        MatcherAssert.assertThat(analyzer.getMethods(Type.getType(ClassAnalyzerTest.ClassMethodTypes.class)), hasItems(new MethodInfo("abstractMethod", "()V", ClassAnalyzerTest.ClassMethodTypes.class, new MethodKind.Implemented()), new MethodInfo("instanceMethod", "()V", ClassAnalyzerTest.ClassMethodTypes.class, new MethodKind.Implemented())));
    }

    private interface InterfaceMethodTypes {
        void abstractMethod();

        default void defaultMethod() {
        }

        static void staticMethod() {
        }
    }

    private interface InterfaceMethodTypes$ {}

    private abstract static class ClassMethodTypes {
        public abstract void abstractMethod();

        public void instanceMethod() {
        }

        public static void staticMethod() {
        }
    }

    @Test
    public void default_method_overridden_and_abstracted() {
        analyze(ClassAnalyzerTest.HasDefaultMethods.class, ClassAnalyzerTest.DoesNotOverrideDefaultMethods.class, ClassAnalyzerTest.OverridesDefaultMethods.class, ClassAnalyzerTest.AbstractsDefaultMethods.class);
        MatcherAssert.assertThat("original", analyzer.getMethods(Type.getType(ClassAnalyzerTest.HasDefaultMethods.class)), containsInAnyOrder(new MethodInfo("abstractMethod", "()V", ClassAnalyzerTest.HasDefaultMethods.class, new MethodKind.Abstract()), new MethodInfo("defaultMethod", "()V", ClassAnalyzerTest.HasDefaultMethods.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.HasDefaultMethods$.class, "defaultMethod", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$HasDefaultMethods;)V")))));
        MatcherAssert.assertThat("inherits unchanged", analyzer.getMethods(Type.getType(ClassAnalyzerTest.DoesNotOverrideDefaultMethods.class)), containsInAnyOrder(new MethodInfo("abstractMethod", "()V", ClassAnalyzerTest.HasDefaultMethods.class, new MethodKind.Abstract()), new MethodInfo("defaultMethod", "()V", ClassAnalyzerTest.HasDefaultMethods.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.HasDefaultMethods$.class, "defaultMethod", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$HasDefaultMethods;)V")))));
        MatcherAssert.assertThat("changes default impl", analyzer.getMethods(Type.getType(ClassAnalyzerTest.OverridesDefaultMethods.class)), containsInAnyOrder(new MethodInfo("abstractMethod", "()V", ClassAnalyzerTest.HasDefaultMethods.class, new MethodKind.Abstract()), new MethodInfo("defaultMethod", "()V", ClassAnalyzerTest.OverridesDefaultMethods.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.OverridesDefaultMethods$.class, "defaultMethod", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$OverridesDefaultMethods;)V")))));
        MatcherAssert.assertThat("makes abstract", analyzer.getMethods(Type.getType(ClassAnalyzerTest.AbstractsDefaultMethods.class)), containsInAnyOrder(new MethodInfo("abstractMethod", "()V", ClassAnalyzerTest.HasDefaultMethods.class, new MethodKind.Abstract()), new MethodInfo("defaultMethod", "()V", ClassAnalyzerTest.AbstractsDefaultMethods.class, new MethodKind.Abstract())));
    }

    private interface HasDefaultMethods {
        void abstractMethod();

        default void defaultMethod() {
        }
    }

    private interface HasDefaultMethods$ {}

    private interface DoesNotOverrideDefaultMethods extends ClassAnalyzerTest.HasDefaultMethods {}

    private interface OverridesDefaultMethods extends ClassAnalyzerTest.HasDefaultMethods {
        @Override
        default void defaultMethod() {
        }
    }

    private interface OverridesDefaultMethods$ {}

    private interface AbstractsDefaultMethods extends ClassAnalyzerTest.HasDefaultMethods {
        @Override
        void defaultMethod();
    }

    @Test
    public void superclass_inheritance() {
        analyze(ClassAnalyzerTest.BaseClass.class, ClassAnalyzerTest.ChildClass.class);
        MatcherAssert.assertThat("original", analyzer.getMethods(Type.getType(ClassAnalyzerTest.BaseClass.class)), hasItem(new MethodInfo("baseMethod", "()V", ClassAnalyzerTest.BaseClass.class, new MethodKind.Implemented())));
        MatcherAssert.assertThat("inherits unchanged", analyzer.getMethods(Type.getType(ClassAnalyzerTest.ChildClass.class)), hasItem(new MethodInfo("baseMethod", "()V", ClassAnalyzerTest.BaseClass.class, new MethodKind.Implemented())));
    }

    private class BaseClass {
        void baseMethod() {
        }
    }

    private class ChildClass extends ClassAnalyzerTest.BaseClass {}

    @Test
    public void overriding_default_methods() {
        analyze(ClassAnalyzerTest.DefaultMethods.class, ClassAnalyzerTest.InheritsDefault.class, ClassAnalyzerTest.OverridesDefault.class, ClassAnalyzerTest.InheritsOverridesDefault.class, ClassAnalyzerTest.InheritsOverridesDefaultAndDirectlyImplements.class);
        MatcherAssert.assertThat("original", analyzer.getMethods(Type.getType(ClassAnalyzerTest.DefaultMethods.class)), hasItem(new MethodInfo("foo", "()V", ClassAnalyzerTest.DefaultMethods.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.DefaultMethods$.class, "foo", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$DefaultMethods;)V")))));
        MatcherAssert.assertThat("inherits unchanged", analyzer.getMethods(Type.getType(ClassAnalyzerTest.InheritsDefault.class)), hasItem(new MethodInfo("foo", "()V", ClassAnalyzerTest.DefaultMethods.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.DefaultMethods$.class, "foo", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$DefaultMethods;)V")))));
        MatcherAssert.assertThat("overrides", analyzer.getMethods(Type.getType(ClassAnalyzerTest.OverridesDefault.class)), hasItem(new MethodInfo("foo", "()V", ClassAnalyzerTest.OverridesDefault.class, new MethodKind.Implemented())));
        MatcherAssert.assertThat("inherits overridden", analyzer.getMethods(Type.getType(ClassAnalyzerTest.InheritsOverridesDefault.class)), hasItem(new MethodInfo("foo", "()V", ClassAnalyzerTest.OverridesDefault.class, new MethodKind.Implemented())));
        MatcherAssert.assertThat("inherits overridden", analyzer.getMethods(Type.getType(ClassAnalyzerTest.InheritsOverridesDefaultAndDirectlyImplements.class)), hasItem(new MethodInfo("foo", "()V", ClassAnalyzerTest.OverridesDefault.class, new MethodKind.Implemented())));
    }

    private interface DefaultMethods {
        default void foo() {
        }
    }

    private interface DefaultMethods$ {}

    private class InheritsDefault implements ClassAnalyzerTest.DefaultMethods {}

    private class OverridesDefault implements ClassAnalyzerTest.DefaultMethods {
        @Override
        public void foo() {
        }
    }

    class InheritsOverridesDefault extends ClassAnalyzerTest.OverridesDefault {}

    class InheritsOverridesDefaultAndDirectlyImplements extends ClassAnalyzerTest.OverridesDefault implements ClassAnalyzerTest.DefaultMethods {}

    @Test
    public void inheriting_same_default_methods_through_many_parent_interfaces() {
        analyze(ClassAnalyzerTest.SuperOriginal.class, ClassAnalyzerTest.SuperOverridden.class, ClassAnalyzerTest.InheritsOriginal.class, ClassAnalyzerTest.InheritsOverridden.class, ClassAnalyzerTest.InheritsOverriddenAndOriginal.class, ClassAnalyzerTest.InheritsOriginalAndOverridden.class);
        MethodInfo original = new MethodInfo("foo", "()V", ClassAnalyzerTest.SuperOriginal.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.SuperOriginal$.class, "foo", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$SuperOriginal;)V")));
        MethodInfo overridden = new MethodInfo("foo", "()V", ClassAnalyzerTest.SuperOverridden.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.SuperOverridden$.class, "foo", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$SuperOverridden;)V")));
        MatcherAssert.assertThat("inherits original", analyzer.getMethods(Type.getType(ClassAnalyzerTest.InheritsOriginal.class)), containsInAnyOrder(original));
        MatcherAssert.assertThat("inherits overridden", analyzer.getMethods(Type.getType(ClassAnalyzerTest.InheritsOverridden.class)), containsInAnyOrder(overridden));
        MatcherAssert.assertThat("inherits overridden and original", analyzer.getMethods(Type.getType(ClassAnalyzerTest.InheritsOverriddenAndOriginal.class)), containsInAnyOrder(overridden));
        MatcherAssert.assertThat("inherits original and overridden", analyzer.getMethods(Type.getType(ClassAnalyzerTest.InheritsOriginalAndOverridden.class)), containsInAnyOrder(overridden));
    }

    private interface SuperOriginal {
        default void foo() {
        }
    }

    private interface SuperOriginal$ {}

    private interface SuperOverridden extends ClassAnalyzerTest.SuperOriginal {
        @Override
        default void foo() {
        }
    }

    private interface SuperOverridden$ {}

    private interface InheritsOriginal extends ClassAnalyzerTest.SuperOriginal {}

    private interface InheritsOverridden extends ClassAnalyzerTest.SuperOverridden {}

    private interface InheritsOverriddenAndOriginal extends ClassAnalyzerTest.InheritsOriginal , ClassAnalyzerTest.SuperOverridden {}

    private interface InheritsOriginalAndOverridden extends ClassAnalyzerTest.InheritsOriginal , ClassAnalyzerTest.SuperOverridden {}

    @Test
    public void implements_original_and_overridden_default_method() {
        analyze(ClassAnalyzerTest.OriginalDefault.class, ClassAnalyzerTest.OverriddenDefault.class, ClassAnalyzerTest.ImplementsOriginal.class, ClassAnalyzerTest.ImplementsOriginalAndOverriddenDefault.class, ClassAnalyzerTest.ImplementsOverriddenAndOriginalDefault.class, ClassAnalyzerTest.ExtendsImplementsOriginalAndImplementsOverriddenDefault.class);
        MethodInfo original = new MethodInfo("foo", "()V", ClassAnalyzerTest.OriginalDefault.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.OriginalDefault$.class, "foo", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$OriginalDefault;)V")));
        MethodInfo overridden = new MethodInfo("foo", "()V", ClassAnalyzerTest.OverriddenDefault.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.OverriddenDefault$.class, "foo", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$OverriddenDefault;)V")));
        MatcherAssert.assertThat("implements original", analyzer.getMethods(Type.getType(ClassAnalyzerTest.ImplementsOriginal.class)), hasItem(original));
        MatcherAssert.assertThat("implements original and overridden", analyzer.getMethods(Type.getType(ClassAnalyzerTest.ImplementsOriginalAndOverriddenDefault.class)), hasItem(overridden));
        MatcherAssert.assertThat("implements overridden and original", analyzer.getMethods(Type.getType(ClassAnalyzerTest.ImplementsOverriddenAndOriginalDefault.class)), hasItem(overridden));
        MatcherAssert.assertThat("extends implementor of original and implements overridden", analyzer.getMethods(Type.getType(ClassAnalyzerTest.ExtendsImplementsOriginalAndImplementsOverriddenDefault.class)), hasItem(overridden));
    }

    private interface OriginalDefault {
        default void foo() {
        }
    }

    private interface OriginalDefault$ {}

    private interface OverriddenDefault extends ClassAnalyzerTest.OriginalDefault {
        @Override
        default void foo() {
        }
    }

    private interface OverriddenDefault$ {}

    private class ImplementsOriginal implements ClassAnalyzerTest.OriginalDefault {}

    private class ImplementsOriginalAndOverriddenDefault implements ClassAnalyzerTest.OriginalDefault , ClassAnalyzerTest.OverriddenDefault {}

    private class ImplementsOverriddenAndOriginalDefault implements ClassAnalyzerTest.OriginalDefault , ClassAnalyzerTest.OverriddenDefault {}

    private class ExtendsImplementsOriginalAndImplementsOverriddenDefault extends ClassAnalyzerTest.ImplementsOriginal implements ClassAnalyzerTest.OverriddenDefault {}

    @Test
    public void default_methods_with_lambdas() {
        analyze(ClassAnalyzerTest.UsesLambdas.class, ClassAnalyzerTest.ImplementsUsesLambdas.class);
        MethodInfo stateless = new MethodInfo("stateless", "()Ljava/util/concurrent/Callable;", ClassAnalyzerTest.UsesLambdas.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.UsesLambdas$.class, "stateless", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$UsesLambdas;)Ljava/util/concurrent/Callable;")));
        MethodInfo captureThis = new MethodInfo("captureThis", "()Ljava/util/concurrent/Callable;", ClassAnalyzerTest.UsesLambdas.class, new MethodKind.Default(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.UsesLambdas$.class, "captureThis", "(Lnet/orfjackal/retrolambda/ClassAnalyzerTest$UsesLambdas;)Ljava/util/concurrent/Callable;")));
        MatcherAssert.assertThat("does not copy instance lambda impl methods to implementers", analyzer.getMethods(Type.getType(ClassAnalyzerTest.ImplementsUsesLambdas.class)), hasItems(stateless, captureThis));
    }

    private interface UsesLambdas {
        default Callable<String> stateless() {
            return () -> "foo";
        }

        default Callable<String> captureThis() {
            return () -> stateless().call();
        }
    }

    private interface UsesLambdas$ {}

    private class ImplementsUsesLambdas implements ClassAnalyzerTest.UsesLambdas {}

    // Method relocations
    @Test
    public void abstract_methods_on_interfaces_are_not_relocated() {
        analyze(ClassAnalyzerTest.InterfaceMethodTypes.class);
        MethodRef source = new MethodRef(Opcodes.H_INVOKEINTERFACE, ClassAnalyzerTest.InterfaceMethodTypes.class, "abstractMethod", ClassAnalyzerTest.voidMethod());
        MethodRef target = analyzer.getMethodCallTarget(source);
        MatcherAssert.assertThat(target, is(source));
    }

    @Test
    public void default_methods_on_interfaces_are_not_relocated() {
        analyze(ClassAnalyzerTest.InterfaceMethodTypes.class);
        MethodRef source = new MethodRef(Opcodes.H_INVOKEINTERFACE, ClassAnalyzerTest.InterfaceMethodTypes.class, "defaultMethod", ClassAnalyzerTest.voidMethod());
        MethodRef target = analyzer.getMethodCallTarget(source);
        MatcherAssert.assertThat(target, is(source));
    }

    @Test
    public void static_methods_on_interfaces_are_relocated_to_companion_classes() {
        analyze(ClassAnalyzerTest.InterfaceMethodTypes.class);
        MethodRef source = new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.InterfaceMethodTypes.class, "staticMethod", ClassAnalyzerTest.voidMethod());
        MethodRef target = analyzer.getMethodCallTarget(source);
        MatcherAssert.assertThat(target, is(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.InterfaceMethodTypes$.class, "staticMethod", ClassAnalyzerTest.voidMethod())));
    }

    @Test
    public void static_methods_on_classes_are_not_relocated() {
        analyze(ClassAnalyzerTest.ClassMethodTypes.class);
        MethodRef source = new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.ClassMethodTypes.class, "staticMethod", ClassAnalyzerTest.voidMethod());
        MethodRef target = analyzer.getMethodCallTarget(source);
        MatcherAssert.assertThat(target, is(source));
    }

    // Default method implementations
    @Test
    public void abstract_methods_have_no_implementation() {
        analyze(ClassAnalyzerTest.HasDefaultMethods.class);
        MethodRef method = new MethodRef(Opcodes.H_INVOKEINTERFACE, ClassAnalyzerTest.HasDefaultMethods.class, "abstractMethod", ClassAnalyzerTest.voidMethod());
        MethodRef impl = analyzer.getMethodDefaultImplementation(method);
        MatcherAssert.assertThat(impl, is(nullValue()));
    }

    @Test
    public void default_method_implementation_is_moved_to_companion_class() {
        analyze(ClassAnalyzerTest.HasDefaultMethods.class);
        MethodRef method = new MethodRef(Opcodes.H_INVOKEINTERFACE, ClassAnalyzerTest.HasDefaultMethods.class, "defaultMethod", ClassAnalyzerTest.voidMethod());
        MethodRef impl = analyzer.getMethodDefaultImplementation(method);
        MatcherAssert.assertThat(impl, is(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.HasDefaultMethods$.class, "defaultMethod", ClassAnalyzerTest.voidMethod(ClassAnalyzerTest.HasDefaultMethods.class))));
    }

    @Test
    public void default_method_implementations_are_inherited_from_parent_interface() {
        analyze(ClassAnalyzerTest.HasDefaultMethods.class, ClassAnalyzerTest.DoesNotOverrideDefaultMethods.class);
        MethodRef method = new MethodRef(Opcodes.H_INVOKEINTERFACE, ClassAnalyzerTest.DoesNotOverrideDefaultMethods.class, "defaultMethod", ClassAnalyzerTest.voidMethod());
        MethodRef impl = analyzer.getMethodDefaultImplementation(method);
        MatcherAssert.assertThat(impl, is(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.HasDefaultMethods$.class, "defaultMethod", ClassAnalyzerTest.voidMethod(ClassAnalyzerTest.HasDefaultMethods.class))));
    }

    @Test
    public void overridden_default_method_implementation_is_moved_to_companion_class() {
        analyze(ClassAnalyzerTest.HasDefaultMethods.class, ClassAnalyzerTest.OverridesDefaultMethods.class);
        MethodRef method = new MethodRef(Opcodes.H_INVOKEINTERFACE, ClassAnalyzerTest.OverridesDefaultMethods.class, "defaultMethod", ClassAnalyzerTest.voidMethod());
        MethodRef impl = analyzer.getMethodDefaultImplementation(method);
        MatcherAssert.assertThat(impl, is(new MethodRef(Opcodes.H_INVOKESTATIC, ClassAnalyzerTest.OverridesDefaultMethods$.class, "defaultMethod", ClassAnalyzerTest.voidMethod(ClassAnalyzerTest.OverridesDefaultMethods.class))));
    }

    @Test
    public void abstracted_default_method_implementations_are_again_abstract() {
        analyze(ClassAnalyzerTest.HasDefaultMethods.class, ClassAnalyzerTest.AbstractsDefaultMethods.class);
        MethodRef method = new MethodRef(Opcodes.H_INVOKEINTERFACE, ClassAnalyzerTest.AbstractsDefaultMethods.class, "defaultMethod", ClassAnalyzerTest.voidMethod());
        MethodRef impl = analyzer.getMethodDefaultImplementation(method);
        MatcherAssert.assertThat(impl, is(nullValue()));
    }

    // Companion class
    @Test
    public void companion_class_is_needed_when_methods_are_moved_there() {
        analyze(ClassAnalyzerTest.Interface.class, ClassAnalyzerTest.InterfaceMethodTypes.class, ClassAnalyzerTest.HasDefaultMethods.class, ClassAnalyzerTest.ClassMethodTypes.class);
        MatcherAssert.assertThat("Interface", analyzer.getCompanionClass(Type.getType(ClassAnalyzerTest.Interface.class)), is(Optional.empty()));
        MatcherAssert.assertThat("InterfaceMethodTypes", analyzer.getCompanionClass(Type.getType(ClassAnalyzerTest.InterfaceMethodTypes.class)), is(Optional.of(Type.getType(ClassAnalyzerTest.InterfaceMethodTypes$.class))));
        MatcherAssert.assertThat("HasDefaultMethods", analyzer.getCompanionClass(Type.getType(ClassAnalyzerTest.HasDefaultMethods.class)), is(Optional.of(Type.getType(ClassAnalyzerTest.HasDefaultMethods$.class))));
        MatcherAssert.assertThat("ClassMethodTypes", analyzer.getCompanionClass(Type.getType(ClassAnalyzerTest.ClassMethodTypes.class)), is(Optional.empty()));
    }
}

