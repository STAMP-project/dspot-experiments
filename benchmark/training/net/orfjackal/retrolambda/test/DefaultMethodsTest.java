/**
 * Copyright ? 2013-2015 Esko Luontola <www.orfjackal.net>
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.test;


import InMainSources.Implementer;
import InMainSources.Interface;
import InMainSources.Overrider;
import SystemUtils.JAVA_VERSION_FLOAT;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.Callable;
import net.orfjackal.retrolambda.test.anotherpackage.UsesLambdasInAnotherPackage;
import org.hamcrest.MatcherAssert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


@SuppressWarnings({ "Convert2Lambda", "Anonymous2MethodRef", "RedundantCast", "UnusedDeclaration" })
public class DefaultMethodsTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    // Inheriting & Overriding
    @Test
    public void default_method_inherited_from_interface() {
        DefaultMethodsTest.DefaultMethods obj = new DefaultMethodsTest.DefaultMethods() {};
        MatcherAssert.assertThat(obj.foo(), is("original"));
    }

    @Test
    public void default_method_overridden_in_current_class() {
        MatcherAssert.assertThat(new DefaultMethodsTest.DefaultMethodOverridingClass().foo(), is("overridden"));
    }

    @Test
    public void default_method_overridden_in_parent_class() {
        class C extends DefaultMethodsTest.DefaultMethodOverridingClass {}
        MatcherAssert.assertThat(new C().foo(), is("overridden"));
    }

    @Test
    public void default_method_overridden_in_parent_class_and_implements_interface_explicitly() {
        class C extends DefaultMethodsTest.DefaultMethodOverridingClass implements DefaultMethodsTest.DefaultMethods {}
        MatcherAssert.assertThat(new C().foo(), is("overridden"));
    }

    private interface DefaultMethods {
        default String foo() {
            return "original";
        }
    }

    private class DefaultMethodOverridingClass implements DefaultMethodsTest.DefaultMethods {
        @Override
        public String foo() {
            return "overridden";
        }
    }

    @Test
    public void default_method_overridden_in_child_interface() {
        DefaultMethodsTest.OverrideChild child = new DefaultMethodsTest.OverrideChild() {};
        MatcherAssert.assertThat(child.foo(), is("overridden"));
    }

    private interface OverrideParent {
        default String foo() {
            return "original";
        }
    }

    private interface OverrideChild extends DefaultMethodsTest.OverrideParent {
        @Override
        default String foo() {
            return "overridden";
        }
    }

    /**
     * Based on the example in <a href="http://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.4.1">JLS ?9.4.1</a>
     * (Interfaces - Inheritance and Overriding)
     */
    @Test
    public void inheriting_same_default_methods_through_many_parent_interfaces() {
        MatcherAssert.assertThat(new DefaultMethodsTest.InheritsOriginal() {}.foo(), is("original"));
        MatcherAssert.assertThat(new DefaultMethodsTest.InheritsOverridden() {}.foo(), is("overridden"));
        MatcherAssert.assertThat(new DefaultMethodsTest.InheritsOverriddenAndOriginal() {}.foo(), is("overridden"));
        MatcherAssert.assertThat(new DefaultMethodsTest.InheritsOriginalAndOverridden() {}.foo(), is("overridden"));
    }

    private interface SuperOriginal {
        default String foo() {
            return "original";
        }
    }

    private interface SuperOverridden extends DefaultMethodsTest.SuperOriginal {
        @Override
        default String foo() {
            return "overridden";
        }
    }

    private interface InheritsOriginal extends DefaultMethodsTest.SuperOriginal {}

    private interface InheritsOverridden extends DefaultMethodsTest.SuperOverridden {}

    private interface InheritsOverriddenAndOriginal extends DefaultMethodsTest.InheritsOriginal , DefaultMethodsTest.SuperOverridden {}

    private interface InheritsOriginalAndOverridden extends DefaultMethodsTest.InheritsOriginal , DefaultMethodsTest.SuperOverridden {}

    @Test
    public void implements_original_and_overridden_default_method() {
        MatcherAssert.assertThat(new DefaultMethodsTest.ImplementsOriginal().foo(), is("original"));
        MatcherAssert.assertThat(new DefaultMethodsTest.ImplementsOriginalAndOverriddenDefault().foo(), is("overridden"));
        MatcherAssert.assertThat(new DefaultMethodsTest.ImplementsOverriddenAndOriginalDefault().foo(), is("overridden"));
        MatcherAssert.assertThat(new DefaultMethodsTest.ExtendsImplementsOriginalAndImplementsOverriddenDefault().foo(), is("overridden"));
    }

    private interface OriginalDefault {
        default String foo() {
            return "original";
        }
    }

    private interface OverriddenDefault extends DefaultMethodsTest.OriginalDefault {
        @Override
        default String foo() {
            return "overridden";
        }
    }

    private class ImplementsOriginal implements DefaultMethodsTest.OriginalDefault {}

    private class ImplementsOriginalAndOverriddenDefault implements DefaultMethodsTest.OriginalDefault , DefaultMethodsTest.OverriddenDefault {}

    private class ImplementsOverriddenAndOriginalDefault implements DefaultMethodsTest.OriginalDefault , DefaultMethodsTest.OverriddenDefault {}

    private class ExtendsImplementsOriginalAndImplementsOverriddenDefault extends DefaultMethodsTest.ImplementsOriginal implements DefaultMethodsTest.OverriddenDefault {}

    // Bridge Methods
    @Test
    public void default_method_type_refined_in_child_interface() {
        DefaultMethodsTest.RefineChild child = new DefaultMethodsTest.RefineChild() {
            @Override
            public String foo() {
                return "refined";
            }
        };
        MatcherAssert.assertThat("direct call", child.foo(), is("refined"));
        MatcherAssert.assertThat("bridged call", ((DefaultMethodsTest.RefineParent) (child)).foo(), is(((Object) ("refined"))));
    }

    @Test
    public void default_method_type_refined_in_implementing_class() {
        class C implements DefaultMethodsTest.RefineParent {
            @Override
            public String foo() {
                return "refined";
            }
        }
        C obj = new C();
        MatcherAssert.assertThat("direct call", obj.foo(), is("refined"));
        MatcherAssert.assertThat("bridged call", ((DefaultMethodsTest.RefineParent) (obj)).foo(), is(((Object) ("refined"))));
    }

    private interface RefineParent {
        default Object foo() {
            return "original";
        }
    }

    private interface RefineChild extends DefaultMethodsTest.RefineParent {
        @Override
        String foo();
    }

    @Test
    public void default_method_argument_type_refined_in_child_interface() {
        DefaultMethodsTest.RefineArgChild child = new DefaultMethodsTest.RefineArgChild() {};
        MatcherAssert.assertThat("direct call", child.foo("42"), is("refined 42"));
        MatcherAssert.assertThat("bridged call", ((DefaultMethodsTest.RefineArgParent<String>) (child)).foo("42"), is(((Object) ("refined 42"))));
    }

    @Test
    public void default_method_argument_type_refined_in_implementing_class() {
        class C implements DefaultMethodsTest.RefineArgParent<String> {
            @Override
            public String foo(String arg) {
                return "refined " + arg;
            }
        }
        C obj = new C();
        MatcherAssert.assertThat("direct call", obj.foo("42"), is("refined 42"));
        MatcherAssert.assertThat("bridged call", ((DefaultMethodsTest.RefineArgParent<String>) (obj)).foo("42"), is(((Object) ("refined 42"))));
    }

    private interface RefineArgParent<T> {
        default String foo(T arg) {
            return "original " + arg;
        }
    }

    private interface RefineArgChild extends DefaultMethodsTest.RefineArgParent<String> {
        @Override
        default String foo(String arg) {
            return "refined " + arg;
        }
    }

    @Test
    public void default_method_type_refined_and_overridden_in_child_interface() {
        DefaultMethodsTest.OverrideRefineChild child = new DefaultMethodsTest.OverrideRefineChild() {};
        MatcherAssert.assertThat("direct call", child.foo(), is("overridden and refined"));
        MatcherAssert.assertThat("bridged call", ((DefaultMethodsTest.OverrideRefineParent) (child)).foo(), is(((Object) ("overridden and refined"))));
    }

    private interface OverrideRefineParent {
        default Object foo() {
            return "original";
        }
    }

    private interface OverrideRefineChild extends DefaultMethodsTest.OverrideRefineParent {
        @Override
        default String foo() {
            return "overridden and refined";
        }
    }

    // Primitive Types & Void
    @Test
    public void default_methods_of_primitive_type() {
        DefaultMethodsTest.Primitives p = new DefaultMethodsTest.Primitives() {};
        MatcherAssert.assertThat("boolean", p.getBoolean(), is(true));
        MatcherAssert.assertThat("byte", p.getByte(), is(((byte) (2))));
        MatcherAssert.assertThat("short", p.getShort(), is(((short) (3))));
        MatcherAssert.assertThat("int", p.getInt(), is(4));
        MatcherAssert.assertThat("long", p.getLong(), is(5L));
        MatcherAssert.assertThat("float", p.getFloat(), is(6.0F));
        MatcherAssert.assertThat("double", p.getDouble(), is(7.0));
        MatcherAssert.assertThat("char", p.getChar(), is('a'));
    }

    private interface Primitives {
        default boolean getBoolean() {
            return true;
        }

        default byte getByte() {
            return 2;
        }

        default short getShort() {
            return 3;
        }

        default int getInt() {
            return 4;
        }

        default long getLong() {
            return 5L;
        }

        default float getFloat() {
            return 6.0F;
        }

        default double getDouble() {
            return 7.0;
        }

        default char getChar() {
            return 'a';
        }
    }

    @Test
    public void default_methods_of_void_type() {
        DefaultMethodsTest.modifiedByVoidMethod = 1;
        DefaultMethodsTest.Voids v = new DefaultMethodsTest.Voids() {};
        v.run();
        MatcherAssert.assertThat(DefaultMethodsTest.modifiedByVoidMethod, is(2));
    }

    private static int modifiedByVoidMethod;

    private interface Voids {
        default void run() {
            (DefaultMethodsTest.modifiedByVoidMethod)++;
        }
    }

    @Test
    public void default_methods_with_primitive_arguments() {
        DefaultMethodsTest.PrimitiveArgs p = new DefaultMethodsTest.PrimitiveArgs() {};
        MatcherAssert.assertThat(p.sum(true, ((byte) (2)), ((short) (3)), 4, 5, 6, 7, ((char) (8))), is(36));
    }

    private interface PrimitiveArgs {
        default int sum(boolean bool, byte b, short s, int i, long l, float f, double d, char c) {
            return ((int) ((((((((bool ? 1 : 0) + b) + s) + i) + l) + f) + d) + c));
        }
    }

    // Calling Super
    @Test
    public void default_methods_calling_super() {
        DefaultMethodsTest.SuperCallChild child = new DefaultMethodsTest.SuperCallChild() {};
        MatcherAssert.assertThat(child.callSuper(), is(11));
    }

    @Test
    public void default_methods_called_with_super() {
        class C implements DefaultMethodsTest.SuperCallChild {
            @Override
            public int callSuper() {
                return 100 + (DefaultMethodsTest.SuperCallChild.super.callSuper());
            }

            public int siblingCallingSuper() {
                return 1000 + (DefaultMethodsTest.SuperCallChild.super.callSuper());
            }
        }
        MatcherAssert.assertThat(new C().callSuper(), is(111));
        MatcherAssert.assertThat(new C().siblingCallingSuper(), is(1011));
    }

    private interface SuperCallParent {
        default int callSuper() {
            return 1;
        }
    }

    private interface SuperCallChild extends DefaultMethodsTest.SuperCallParent {
        @Override
        default int callSuper() {
            return 10 + (DefaultMethodsTest.SuperCallParent.super.callSuper());
        }
    }

    @Test
    public void inheriting_unrelated_default_methods() {
        class C implements DefaultMethodsTest.Conflict1 , DefaultMethodsTest.Conflict2 {
            @Override
            public String conflict() {
                return (DefaultMethodsTest.Conflict1.super.conflict()) + (DefaultMethodsTest.Conflict2.super.conflict());
            }
        }
        MatcherAssert.assertThat(new C().conflict(), is("ab"));
    }

    private interface Conflict1 {
        default String conflict() {
            return "a";
        }
    }

    private interface Conflict2 {
        default String conflict() {
            return "b";
        }
    }

    // Misc
    @Test
    public void default_methods_calling_other_interface_methods() {
        DefaultMethodsTest.CallOtherMethods obj = new DefaultMethodsTest.CallOtherMethods() {
            @Override
            public int foo() {
                return 2;
            }
        };
        MatcherAssert.assertThat(obj.callsFoo(), is(12));
    }

    private interface CallOtherMethods {
        int foo();

        default int callsFoo() {
            return (foo()) + 10;
        }
    }

    /**
     * Backporting default methods should not interact badly with backporting lambdas.
     */
    @Test
    public void lambdas_with_default_methods() {
        DefaultMethodsTest.CallOtherMethods lambda = () -> 2;
        MatcherAssert.assertThat(lambda.foo(), is(2));
        MatcherAssert.assertThat(lambda.callsFoo(), is(12));
    }

    @Test
    public void default_methods_with_lambdas() throws Exception {
        DefaultMethodsTest.UsesLambdas obj = new DefaultMethodsTest.UsesLambdas() {};
        MatcherAssert.assertThat(obj.stateless().call(), is("foo"));
    }

    @Test
    public void default_methods_with_lambdas_that_capture_this() throws Exception {
        DefaultMethodsTest.UsesLambdas obj = new DefaultMethodsTest.UsesLambdas() {};
        MatcherAssert.assertThat(obj.captureThis().call(), is("foo"));
    }

    private interface UsesLambdas {
        default Callable<String> stateless() {
            return () -> "foo";
        }

        default Callable<String> captureThis() {
            return () -> stateless().call();
        }
    }

    /**
     * Lambdas which capture this in default methods will generate the lambda implementation
     * method as a private <em>instance</em> method. We must avoid copying those methods to
     * the interface implementers as if they were default methods.
     */
    @Test
    public void default_methods_with_lambdas_in_another_package() throws Exception {
        Assume.assumeThat(JAVA_VERSION_FLOAT, is(lessThan(1.8F)));
        UsesLambdasInAnotherPackage obj = new UsesLambdasInAnotherPackage() {};
        MatcherAssert.assertThat(obj.stateless().call(), is("foo"));
        MatcherAssert.assertThat(obj.captureThis().call(), is("foo"));
        MatcherAssert.assertThat("should contain only delegates to the two default methods", obj.getClass().getDeclaredMethods(), arrayWithSize(2));
    }

    /**
     * Though we use {@link InMainSources}, because the Retrolambda Maven plugin
     * processes the main sources separately from the test sources, the effect is
     * the same as if they were in another module.
     */
    @Test
    public void calling_default_methods_from_another_module_through_interface() {
        InMainSources.Interface implementer = new InMainSources.Implementer();
        MatcherAssert.assertThat(implementer.defaultMethod(), is("default"));
        InMainSources.Interface overrider = new InMainSources.Overrider();
        MatcherAssert.assertThat(overrider.defaultMethod(), is("overridden"));
    }

    /**
     * Fixes issue of the generated delegate methods being marked as synthetic,
     * in which case the Java compiler causes "error: cannot find symbol"
     * for direct calls to those methods.
     */
    @Test
    public void calling_default_methods_from_another_module_through_class() {
        InMainSources.Implementer implementer = new InMainSources.Implementer();
        MatcherAssert.assertThat(implementer.defaultMethod(), is("default"));
        InMainSources.Overrider overrider = new InMainSources.Overrider();
        MatcherAssert.assertThat(overrider.defaultMethod(), is("overridden"));
    }

    /**
     * We're unable to backport default methods if we cannot modify the interface,
     * e.g. if it's part of the standard library or a third-party library.
     */
    @Test
    public void default_methods_of_library_interfaces_are_ignored_silently() throws Exception {
        @SuppressWarnings("unchecked")
        Iterator<String> dummy = Mockito.mock(Iterator.class);
        // the Iterable interface has default methods in Java 8, but that
        // should not prevent us from using it in previous Java versions
        Iterable<String> it = new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return dummy;
            }
        };
        MatcherAssert.assertThat("interface should work as usual", it.iterator(), is(dummy));
        MatcherAssert.assertThat("should not copy default methods from library interfaces", it.getClass().getDeclaredMethods(), arrayWithSize(1));
    }

    @Test
    public void trying_to_use_default_methods_of_library_interfaces_causes_NoSuchMethodError() {
        Assume.assumeThat(JAVA_VERSION_FLOAT, is(lessThan(1.8F)));
        class C implements Iterable<String> {
            @Override
            public Iterator<String> iterator() {
                return Collections.emptyIterator();
            }
        }
        thrown.expect(NoSuchMethodError.class);
        thrown.expectMessage("spliterator");
        // to make sure that no method was inserted to the class (in which case this call would not fail)
        new C().spliterator();
    }

    /**
     * A naive method for removing method bodies would easily also remove their annotations,
     * because in ASM method annotations are expressed as calls on the MethodVisitor.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void keeps_annotations_on_interface_methods() throws Exception {
        MatcherAssert.assertThat("interface", DefaultMethodsTest.AnnotatedInterface.class.getAnnotations(), arrayContaining(DefaultMethodsTest.someAnnotation(1)));
        MatcherAssert.assertThat("abstract method", DefaultMethodsTest.AnnotatedInterface.class.getMethod("annotatedAbstractMethod").getAnnotations(), arrayContaining(DefaultMethodsTest.someAnnotation(2)));
        MatcherAssert.assertThat("default method", DefaultMethodsTest.AnnotatedInterface.class.getMethod("annotatedDefaultMethod").getAnnotations(), arrayContaining(DefaultMethodsTest.someAnnotation(3)));
        Assume.assumeThat(JAVA_VERSION_FLOAT, is(lessThan(1.8F)));
        MatcherAssert.assertThat("static method", TestUtil.companionOf(DefaultMethodsTest.AnnotatedInterface.class).getMethod("annotatedStaticMethod").getAnnotations(), arrayContaining(DefaultMethodsTest.someAnnotation(4)));
    }

    @DefaultMethodsTest.SomeAnnotation(1)
    private interface AnnotatedInterface {
        @DefaultMethodsTest.SomeAnnotation(2)
        void annotatedAbstractMethod();

        @DefaultMethodsTest.SomeAnnotation(3)
        default void annotatedDefaultMethod() {
        }

        @DefaultMethodsTest.SomeAnnotation(4)
        static void annotatedStaticMethod() {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface SomeAnnotation {
        int value();
    }
}

