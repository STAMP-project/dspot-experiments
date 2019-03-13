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
package com.liferay.registry.dependency;


import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael C. Han
 */
public class ClassServiceDependencyVerifierTest {
    @Test
    public void testVerifyImplementations() {
        Registry registry = RegistryUtil.getRegistry();
        registry.registerService(ClassServiceDependencyVerifierTest.TestInstance1.class, new ClassServiceDependencyVerifierTest.TestInstance1());
        registry.registerService(ClassServiceDependencyVerifierTest.TestInterface2.class, new ClassServiceDependencyVerifierTest.TestInstance2());
        ServiceReference<?> serviceReference = registry.getServiceReference(ClassServiceDependencyVerifierTest.TestInstance1.class);
        ClassServiceDependencyVerifier classServiceDependencyVerifier1 = new ClassServiceDependencyVerifier(ClassServiceDependencyVerifierTest.TestInstance1.class);
        Assert.assertTrue(classServiceDependencyVerifier1.verify(serviceReference));
        ClassServiceDependencyVerifier classServiceDependencyVerifier2 = new ClassServiceDependencyVerifier(ClassServiceDependencyVerifierTest.TestInterface1.class);
        Assert.assertTrue(classServiceDependencyVerifier2.verify(serviceReference));
    }

    @Test
    public void testVerifyInterfaces() {
        Registry registry = RegistryUtil.getRegistry();
        registry.registerService(ClassServiceDependencyVerifierTest.TestInterface1.class, new ClassServiceDependencyVerifierTest.TestInstance1());
        registry.registerService(ClassServiceDependencyVerifierTest.TestInterface2.class, new ClassServiceDependencyVerifierTest.TestInstance2());
        ServiceReference<?> serviceReference1 = registry.getServiceReference(ClassServiceDependencyVerifierTest.TestInterface1.class);
        ServiceReference<?> serviceReference2 = registry.getServiceReference(ClassServiceDependencyVerifierTest.TestInterface2.class);
        ServiceReference<?> serviceReference3 = registry.getServiceReference(ClassServiceDependencyVerifierTest.TestInterface2.class);
        ClassServiceDependencyVerifier classServiceDependencyVerifier = new ClassServiceDependencyVerifier(ClassServiceDependencyVerifierTest.TestInstance1.class);
        Assert.assertTrue(classServiceDependencyVerifier.verify(serviceReference1));
        Assert.assertFalse(classServiceDependencyVerifier.verify(serviceReference2));
        ClassServiceDependencyVerifier classServiceDependencyVerifier2 = new ClassServiceDependencyVerifier(ClassServiceDependencyVerifierTest.TestInstance2.class);
        Assert.assertTrue(classServiceDependencyVerifier2.verify(serviceReference2));
        ClassServiceDependencyVerifier classServiceDependencyVerifier3 = new ClassServiceDependencyVerifier(ClassServiceDependencyVerifierTest.TestInstance3.class);
        Assert.assertFalse(classServiceDependencyVerifier3.verify(serviceReference3));
    }

    private static class TestInstance1 implements ClassServiceDependencyVerifierTest.TestInterface1 {}

    private static class TestInstance2 implements ClassServiceDependencyVerifierTest.TestInterface2 {}

    private static class TestInstance3 implements ClassServiceDependencyVerifierTest.TestInterface1 , ClassServiceDependencyVerifierTest.TestInterface2 {}

    private interface TestInterface1 {}

    private interface TestInterface2 {}
}

