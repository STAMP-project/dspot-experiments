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


import SystemUtils.JAVA_VERSION_FLOAT;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import org.apache.commons.lang.SystemUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@SuppressWarnings("Convert2MethodRef")
public class InterfaceStaticMethodsTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void static_methods_on_interfaces() {
        MatcherAssert.assertThat(InterfaceStaticMethodsTest.Interface.staticMethod(), is(42));
    }

    @Test
    public void static_methods_on_interfaces_taking_arguments() {
        MatcherAssert.assertThat(InterfaceStaticMethodsTest.Interface.staticMethodWithArgs("a", 1, 2L), is("a12"));
    }

    @Test
    public void static_methods_on_interfaces_containing_lambdas() throws Exception {
        MatcherAssert.assertThat(InterfaceStaticMethodsTest.Interface.staticMethodContainingLambdas(), is(123));
    }

    @Test
    public void calling_static_methods_on_interfaces_from_default_methods() {
        InterfaceStaticMethodsTest.Interface obj = new InterfaceStaticMethodsTest.Interface() {};
        MatcherAssert.assertThat(obj.callStaticMethod(), is(42));
    }

    @Test
    public void calling_static_methods_on_interfaces_from_lambdas() throws Exception {
        Callable<Integer> c = () -> InterfaceStaticMethodsTest.Interface.staticMethod();
        MatcherAssert.assertThat(c.call(), is(42));
    }

    @Test
    public void calling_static_methods_on_interfaces_from_method_references() throws Exception {
        Callable<Integer> c = InterfaceStaticMethodsTest.Interface::staticMethod;
        MatcherAssert.assertThat(c.call(), is(42));
    }

    private interface Interface {
        default int callStaticMethod() {
            return InterfaceStaticMethodsTest.Interface.staticMethod();
        }

        static int staticMethod() {
            return 42;
        }

        // arguments of just a couple of different types because we're lazy
        static String staticMethodWithArgs(String s, int a, long b) {
            return (s + a) + b;
        }

        static int staticMethodContainingLambdas() throws Exception {
            Callable<Integer> lambda = () -> 123;
            return lambda.call();
        }
    }

    /**
     * Calling a {@code InterfaceMethodref} constant pool entry with {@code invokestatic}
     * is not allowed in Java 7 bytecode. It'll fail at class loading time with
     * "VerifyError: Illegal type at constant pool entry"
     */
    @Test
    public void calling_static_methods_of_library_interfaces__new_interface() {
        Assume.assumeThat(JAVA_VERSION_FLOAT, is(lessThan(1.8F)));
        thrown.expect(NoClassDefFoundError.class);
        thrown.expectMessage("java/util/stream/Stream");
        // We don't want this call to prevent loading this whole test class,
        // it should only fail when this line is executed
        Stream.of(1, 2, 3);
    }

    @Test
    public void calling_static_methods_of_library_interfaces__new_method_on_old_interface() {
        Assume.assumeThat(JAVA_VERSION_FLOAT, is(lessThan(1.8F)));
        thrown.expect(IncompatibleClassChangeError.class);
        thrown.expectMessage((SystemUtils.isJavaVersionAtLeast(1.6F) ? equalTo("Found interface java.util.Comparator, but class was expected") : nullValue(String.class)));// on Java 5 there is no message

        // We don't want this call to prevent loading this whole test class,
        // it should only fail when this line is executed
        Comparator.naturalOrder();
    }

    @Test
    public void will_not_generate_a_companion_class_when_the_interface_has_just_constant_fields() {
        MatcherAssert.assertThat(InterfaceStaticMethodsTest.InterfaceWithConstants.FOO, is(3));
        TestUtil.assertClassDoesNotExist(TestUtil.companionNameOf(InterfaceStaticMethodsTest.InterfaceWithConstants.class));
    }

    private interface InterfaceWithConstants {
        int FOO = ((int) (Math.sqrt(9)));// a constant which needs to be calculated in a static initialization block

    }
}

