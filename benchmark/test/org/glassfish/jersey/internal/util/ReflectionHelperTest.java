/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.internal.util;


import ReflectionHelper.DeclaringClassInterfacePair;
import java.lang.reflect.Method;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@code ReflectionHelper} unit tests.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@SuppressWarnings("unchecked")
public class ReflectionHelperTest {
    @SuppressWarnings("UnusedDeclaration")
    public static interface I<T> {}

    public static class A<T> implements ReflectionHelperTest.I<T> {}

    public static class TestNoInterface extends ReflectionHelperTest.A<byte[]> {}

    public static class TestInterface extends ReflectionHelperTest.A<byte[]> implements ReflectionHelperTest.I<byte[]> {}

    /**
     * See JERSEY-1598.
     */
    @Test
    public void getParameterizedClassArgumentsTest() {
        ReflectionHelper.DeclaringClassInterfacePair dcip = ReflectionHelper.getClass(ReflectionHelperTest.TestNoInterface.class, ReflectionHelperTest.I.class);
        Class[] arguments = ReflectionHelper.getParameterizedClassArguments(dcip);
        final Class aClass = arguments[0];
        dcip = ReflectionHelper.getClass(ReflectionHelperTest.TestInterface.class, ReflectionHelperTest.I.class);
        arguments = ReflectionHelper.getParameterizedClassArguments(dcip);
        Assert.assertEquals(aClass, arguments[0]);
    }

    @Test(expected = AccessControlException.class)
    public void securityManagerSetContextClassLoader() throws Exception {
        final ClassLoader loader = ReflectionHelper.class.getClassLoader();
        Thread.currentThread().setContextClassLoader(loader);
        Assert.fail("It should not be possible to set context class loader from unprivileged block");
    }

    @Test(expected = AccessControlException.class)
    public void securityManagerSetContextClassLoaderPA() throws Exception {
        final ClassLoader loader = ReflectionHelper.class.getClassLoader();
        ReflectionHelper.setContextClassLoaderPA(loader).run();
        Assert.fail("It should not be possible to set context class loader from unprivileged block even via Jersey ReflectionHelper");
    }

    @Test(expected = AccessControlException.class)
    public void securityManagerSetContextClassLoaderInDoPrivileged() throws Exception {
        final ClassLoader loader = ReflectionHelper.class.getClassLoader();
        AccessController.doPrivileged(ReflectionHelper.setContextClassLoaderPA(loader));
        Assert.fail(("It should not be possible to set context class loader even from privileged block via Jersey ReflectionHelper " + "utility"));
    }

    public static class FromStringClass {
        private final String value;

        public FromStringClass(final String value) {
            this.value = value;
        }

        public static ReflectionHelperTest.FromStringClass valueOf(final String value) {
            return new ReflectionHelperTest.FromStringClass(value);
        }

        public static ReflectionHelperTest.FromStringClass fromString(final String value) {
            return new ReflectionHelperTest.FromStringClass(value);
        }
    }

    public static class InvalidFromStringClass {
        private final String value;

        public InvalidFromStringClass(final String value) {
            this.value = value;
        }

        public static Boolean valueOf(final String value) {
            throw new AssertionError("Should not be invoked");
        }

        public static Boolean fromString(final String value) {
            throw new AssertionError("Should not be invoked");
        }
    }

    /**
     * Reproducer for JERSEY-2801.
     */
    @Test
    public void testGetValueOfStringMethod() throws Exception {
        final PrivilegedAction<Method> methodPA = ReflectionHelper.getValueOfStringMethodPA(ReflectionHelperTest.FromStringClass.class);
        final ReflectionHelperTest.FromStringClass value = ((ReflectionHelperTest.FromStringClass) (methodPA.run().invoke(null, "value")));
        MatcherAssert.assertThat("Incorrect instance of FromStringClass created.", value.value, CoreMatchers.is("value"));
    }

    /**
     * Negative reproducer for JERSEY-2801.
     */
    @Test
    public void testGetValueOfStringMethodNegative() throws Exception {
        final PrivilegedAction<Method> methodPA = ReflectionHelper.getValueOfStringMethodPA(ReflectionHelperTest.InvalidFromStringClass.class);
        MatcherAssert.assertThat("Invalid valueOf method found.", methodPA.run(), CoreMatchers.nullValue());
    }

    /**
     * Reproducer for JERSEY-2801.
     */
    @Test
    public void testGetFromStringStringMethod() throws Exception {
        final PrivilegedAction<Method> methodPA = ReflectionHelper.getFromStringStringMethodPA(ReflectionHelperTest.FromStringClass.class);
        final ReflectionHelperTest.FromStringClass value = ((ReflectionHelperTest.FromStringClass) (methodPA.run().invoke(null, "value")));
        MatcherAssert.assertThat("Incorrect instance of FromStringClass created.", value.value, CoreMatchers.is("value"));
    }

    /**
     * Negative reproducer for JERSEY-2801.
     */
    @Test
    public void testGetFromStringStringMethodNegative() throws Exception {
        final PrivilegedAction<Method> methodPA = ReflectionHelper.getFromStringStringMethodPA(ReflectionHelperTest.InvalidFromStringClass.class);
        MatcherAssert.assertThat("Invalid valueOf method found.", methodPA.run(), CoreMatchers.nullValue());
    }

    public static class IsGetterTester {
        public int get() {
            return 0;
        }

        public boolean is() {
            return true;
        }

        public int getSomething() {
            return 0;
        }

        public boolean isSomething() {
            return true;
        }
    }

    @Test
    public void testIsGetterWithGetOnlyNegative() throws Exception {
        MatcherAssert.assertThat("isGetter should have returned false for method named 'get'", ReflectionHelper.isGetter(ReflectionHelperTest.IsGetterTester.class.getMethod("get")), CoreMatchers.is(false));
    }

    @Test
    public void testIsGetterWithIsOnlyNegative() throws Exception {
        MatcherAssert.assertThat("isGetter should have returned false for method named 'is'", ReflectionHelper.isGetter(ReflectionHelperTest.IsGetterTester.class.getMethod("is")), CoreMatchers.is(false));
    }

    @Test
    public void testIsGetterWithRealGetterPositive() throws Exception {
        MatcherAssert.assertThat("isGetter should have returned true for method named 'getSomething'", ReflectionHelper.isGetter(ReflectionHelperTest.IsGetterTester.class.getMethod("getSomething")), CoreMatchers.is(true));
    }

    @Test
    public void testIsGetterWithRealIsPositive() throws Exception {
        MatcherAssert.assertThat("isGetter should have returned true for method named 'isSomething'", ReflectionHelper.isGetter(ReflectionHelperTest.IsGetterTester.class.getMethod("isSomething")), CoreMatchers.is(true));
    }
}

