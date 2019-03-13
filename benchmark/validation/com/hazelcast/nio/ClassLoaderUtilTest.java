/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.nio;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClassLoaderUtilTest extends HazelcastTestSupport {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(ClassLoaderUtil.class);
    }

    @Test
    public void testImplementsInterfaceWithSameName_whenInterfaceIsDirectlyImplemented() {
        Assert.assertTrue(ClassLoaderUtil.implementsInterfaceWithSameName(ClassLoaderUtilTest.DirectlyImplementingInterface.class, ClassLoaderUtilTest.MyInterface.class));
    }

    @Test
    public void testDoNotImplementInterface() {
        Assert.assertFalse(ClassLoaderUtil.implementsInterfaceWithSameName(Object.class, ClassLoaderUtilTest.MyInterface.class));
    }

    @Test
    public void testImplementsInterfaceWithSameName_whenInterfaceIsImplementedBySuperClass() {
        Assert.assertTrue(ClassLoaderUtil.implementsInterfaceWithSameName(ClassLoaderUtilTest.ExtendingClassImplementingInterface.class, ClassLoaderUtilTest.MyInterface.class));
    }

    @Test
    public void testImplementsInterfaceWithSameName_whenDirectlyImplementingSubInterface() {
        Assert.assertTrue(ClassLoaderUtil.implementsInterfaceWithSameName(ClassLoaderUtilTest.DirectlyImplementingSubInterfaceInterface.class, ClassLoaderUtilTest.MyInterface.class));
    }

    @Test
    public void testImplementsInterfaceWithSameName_whenExtendingClassImplementingSubinterface() {
        Assert.assertTrue(ClassLoaderUtil.implementsInterfaceWithSameName(ClassLoaderUtilTest.ExtendingClassImplementingSubInterface.class, ClassLoaderUtilTest.MyInterface.class));
    }

    @Test
    public void testIssue13509() throws Exception {
        // see https://github.com/hazelcast/hazelcast/issues/13509
        ClassLoader testCL = new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if (name.equals("mock.Class")) {
                    try {
                        byte[] classData = IOUtil.toByteArray(getClass().getResourceAsStream("mock-class-data.dat"));
                        return defineClass(name, classData, 0, classData.length);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                throw new ClassNotFoundException(name);
            }
        };
        ClassLoader previousCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(testCL);
        try {
            Thread.currentThread().setContextClassLoader(testCL);
            Object o = ClassLoaderUtil.newInstance(null, "mock.Class");
            Assert.assertNotNull("no object created", o);
        } finally {
            Thread.currentThread().setContextClassLoader(previousCL);
        }
        // now the context class loader is reset back, new instance should fail
        try {
            ClassLoaderUtil.newInstance(null, "mock.Class");
            Assert.fail("call did not fail, class probably incorrectly returned from CONSTRUCTOR_CACHE");
        } catch (ClassNotFoundException expected) {
        }
    }

    private static class ExtendingClassImplementingSubInterface extends ClassLoaderUtilTest.DirectlyImplementingSubInterfaceInterface {}

    private static class ExtendingClassImplementingInterface extends ClassLoaderUtilTest.DirectlyImplementingInterface {}

    private static class DirectlyImplementingInterface implements ClassLoaderUtilTest.MyInterface {}

    private static class DirectlyImplementingSubInterfaceInterface implements ClassLoaderUtilTest.SubInterface {}

    private interface SubInterface extends ClassLoaderUtilTest.MyInterface {}

    private interface MyInterface {}
}

