/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.reflect;


import NewEnv.Type;
import com.liferay.portal.kernel.test.SwappableSecurityManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Permission;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Preston Crary
 */
public class ReflectionUtilTest {
    @ClassRule
    @Rule
    public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(CodeCoverageAssertor.INSTANCE, NewEnvTestRule.INSTANCE);

    @Test
    public void testArrayClone() throws Exception {
        Object object = new Object();
        try {
            ReflectionUtil.arrayClone(object);
            Assert.fail();
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals(("Input object is not an array: " + object), iae.getMessage());
        }
        object = new long[]{ 1, 2, 3 };
        Object clone = ReflectionUtil.arrayClone(object);
        Assert.assertNotSame(object, clone);
        Assert.assertArrayEquals(((long[]) (object)), ((long[]) (clone)));
        Field field = ReflectionUtil.getDeclaredField(ReflectionUtil.class, "_cloneMethod");
        field.set(null, null);
        try {
            ReflectionUtil.arrayClone(object);
            Assert.fail();
        } catch (NullPointerException npe) {
            Assert.assertNull(npe.getCause());
        }
    }

    @Test
    public void testConstructor() {
        new ReflectionUtil();
    }

    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testExceptionInInitializerError() throws ClassNotFoundException {
        SecurityException securityException = new SecurityException();
        try (SwappableSecurityManager swappableSecurityManager = new SwappableSecurityManager() {
            @Override
            public void checkPermission(Permission permission) {
                String name = permission.getName();
                if (name.equals("suppressAccessChecks")) {
                    throw securityException;
                }
            }
        }) {
            swappableSecurityManager.install();
            Class.forName(ReflectionUtil.class.getName());
            Assert.fail();
        } catch (ExceptionInInitializerError eiie) {
            Assert.assertSame(securityException, eiie.getCause());
        }
    }

    @Test
    public void testGetDeclaredField() throws Exception {
        Field field = ReflectionUtil.getDeclaredField(ReflectionUtilTest.TestClass.class, "_privateStaticFinalObject");
        Assert.assertTrue(field.isAccessible());
        Assert.assertFalse(Modifier.isFinal(field.getModifiers()));
        Assert.assertSame(ReflectionUtilTest.TestClass._privateStaticFinalObject, field.get(null));
    }

    @Test
    public void testGetDeclaredFields() throws Exception {
        Field[] fields = ReflectionUtil.getDeclaredFields(ReflectionUtilTest.TestClass.class);
        for (Field field : fields) {
            Assert.assertTrue(field.isAccessible());
            Assert.assertFalse(Modifier.isFinal(field.getModifiers()));
            String name = field.getName();
            if (name.equals("_privateStaticFinalObject")) {
                Assert.assertSame(ReflectionUtilTest.TestClass._privateStaticFinalObject, field.get(null));
            } else
                if (name.equals("_privateStaticObject")) {
                    Assert.assertSame(ReflectionUtilTest.TestClass._privateStaticObject, field.get(null));
                }

        }
    }

    @Test
    public void testGetDeclaredMethod() throws Exception {
        Method method = ReflectionUtil.getDeclaredMethod(ReflectionUtilTest.TestClass.class, "_getPrivateStaticObject");
        Assert.assertTrue(method.isAccessible());
        Assert.assertSame(ReflectionUtilTest.TestClass._privateStaticObject, method.invoke(null));
    }

    @Test
    public void testGetInterfaces() {
        Class<?>[] interfaces = ReflectionUtil.getInterfaces(new ReflectionUtilTest.TestClass());
        Assert.assertEquals(Arrays.toString(interfaces), 1, interfaces.length);
        Assert.assertSame(ReflectionUtilTest.TestInterface.class, interfaces[0]);
        AtomicReference<ClassNotFoundException> atomicReference = new AtomicReference<>();
        interfaces = ReflectionUtil.getInterfaces(new ReflectionUtilTest.TestClass(), new URLClassLoader(new URL[0], null));
        Assert.assertEquals(Arrays.toString(interfaces), 0, interfaces.length);
        interfaces = ReflectionUtil.getInterfaces(new ReflectionUtilTest.TestClass(), new URLClassLoader(new URL[0], null), atomicReference::set);
        Assert.assertEquals(Arrays.toString(interfaces), 0, interfaces.length);
        Assert.assertNotNull(atomicReference.get());
    }

    @Test
    public void testThrowException() {
        Exception exception = new Exception();
        try {
            ReflectionUtil.throwException(exception);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertSame(exception, e);
        }
    }

    @Test
    public void testUnfinalField() throws Exception {
        Field field = ReflectionUtilTest.TestClass.class.getDeclaredField("_privateStaticFinalObject");
        Assert.assertTrue(Modifier.isFinal(field.getModifiers()));
        Assert.assertTrue(Modifier.isPrivate(field.getModifiers()));
        Assert.assertTrue(Modifier.isStatic(field.getModifiers()));
        ReflectionUtil.unfinalField(field);
        Assert.assertFalse(Modifier.isFinal(field.getModifiers()));
        Assert.assertTrue(Modifier.isPrivate(field.getModifiers()));
        Assert.assertTrue(Modifier.isStatic(field.getModifiers()));
        ReflectionUtil.unfinalField(field);
        Assert.assertFalse(Modifier.isFinal(field.getModifiers()));
        Assert.assertTrue(Modifier.isPrivate(field.getModifiers()));
        Assert.assertTrue(Modifier.isStatic(field.getModifiers()));
    }

    private static class TestClass implements ReflectionUtilTest.TestInterface {
        public static void setPrivateStaticObject(Object privateStaticObject) {
            ReflectionUtilTest.TestClass._privateStaticObject = privateStaticObject;
        }

        @SuppressWarnings("unused")
        private static Object _getPrivateStaticObject() {
            return ReflectionUtilTest.TestClass._privateStaticObject;
        }

        private static final Object _privateStaticFinalObject = new Object();

        private static Object _privateStaticObject = new Object();
    }

    private interface TestInterface {}
}

