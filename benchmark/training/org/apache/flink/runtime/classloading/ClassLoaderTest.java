/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.classloading;


import java.net.URL;
import java.net.URLClassLoader;
import org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders;
import org.apache.flink.runtime.rpc.messages.RemoteRpcInvocation;
import org.apache.flink.testutils.ClassLoaderUtils;
import org.apache.flink.util.SerializedValue;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for classloading and class loader utilities.
 */
public class ClassLoaderTest extends TestLogger {
    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testMessageDecodingWithUnavailableClass() throws Exception {
        final ClassLoader systemClassLoader = getClass().getClassLoader();
        final String className = "UserClass";
        final URLClassLoader userClassLoader = ClassLoaderUtils.compileAndLoadJava(ClassLoaderTest.temporaryFolder.newFolder(), (className + ".java"), ((("import java.io.Serializable;\n" + "public class ") + className) + " implements Serializable {}"));
        RemoteRpcInvocation method = new RemoteRpcInvocation("test", new Class<?>[]{ int.class, Class.forName(className, false, userClassLoader) }, new Object[]{ 1, Class.forName(className, false, userClassLoader).newInstance() });
        SerializedValue<RemoteRpcInvocation> serializedMethod = new SerializedValue(method);
        expectedException.expect(ClassNotFoundException.class);
        expectedException.expect(CoreMatchers.allOf(CoreMatchers.isA(ClassNotFoundException.class), Matchers.hasProperty("suppressed", Matchers.hasItemInArray(CoreMatchers.allOf(CoreMatchers.isA(ClassNotFoundException.class), Matchers.hasProperty("message", CoreMatchers.containsString("Could not deserialize 1th parameter type of method test(int, ...).")))))));
        RemoteRpcInvocation deserializedMethod = serializedMethod.deserializeValue(systemClassLoader);
        deserializedMethod.getMethodName();
        userClassLoader.close();
    }

    @Test
    public void testParentFirstClassLoading() throws Exception {
        final ClassLoader parentClassLoader = getClass().getClassLoader();
        // collect the libraries / class folders with RocksDB related code: the state backend and RocksDB itself
        final URL childCodePath = getClass().getProtectionDomain().getCodeSource().getLocation();
        final URLClassLoader childClassLoader1 = FlinkUserCodeClassLoaders.parentFirst(new URL[]{ childCodePath }, parentClassLoader);
        final URLClassLoader childClassLoader2 = FlinkUserCodeClassLoaders.parentFirst(new URL[]{ childCodePath }, parentClassLoader);
        final String className = ClassLoaderTest.class.getName();
        final Class<?> clazz1 = Class.forName(className, false, parentClassLoader);
        final Class<?> clazz2 = Class.forName(className, false, childClassLoader1);
        final Class<?> clazz3 = Class.forName(className, false, childClassLoader2);
        Assert.assertEquals(clazz1, clazz2);
        Assert.assertEquals(clazz1, clazz3);
        childClassLoader1.close();
        childClassLoader2.close();
    }

    @Test
    public void testChildFirstClassLoading() throws Exception {
        final ClassLoader parentClassLoader = getClass().getClassLoader();
        // collect the libraries / class folders with RocksDB related code: the state backend and RocksDB itself
        final URL childCodePath = getClass().getProtectionDomain().getCodeSource().getLocation();
        final URLClassLoader childClassLoader1 = FlinkUserCodeClassLoaders.childFirst(new URL[]{ childCodePath }, parentClassLoader, new String[0]);
        final URLClassLoader childClassLoader2 = FlinkUserCodeClassLoaders.childFirst(new URL[]{ childCodePath }, parentClassLoader, new String[0]);
        final String className = ClassLoaderTest.class.getName();
        final Class<?> clazz1 = Class.forName(className, false, parentClassLoader);
        final Class<?> clazz2 = Class.forName(className, false, childClassLoader1);
        final Class<?> clazz3 = Class.forName(className, false, childClassLoader2);
        Assert.assertNotEquals(clazz1, clazz2);
        Assert.assertNotEquals(clazz1, clazz3);
        Assert.assertNotEquals(clazz2, clazz3);
        childClassLoader1.close();
        childClassLoader2.close();
    }

    @Test
    public void testRepeatedChildFirstClassLoading() throws Exception {
        final ClassLoader parentClassLoader = getClass().getClassLoader();
        // collect the libraries / class folders with RocksDB related code: the state backend and RocksDB itself
        final URL childCodePath = getClass().getProtectionDomain().getCodeSource().getLocation();
        final URLClassLoader childClassLoader = FlinkUserCodeClassLoaders.childFirst(new URL[]{ childCodePath }, parentClassLoader, new String[0]);
        final String className = ClassLoaderTest.class.getName();
        final Class<?> clazz1 = Class.forName(className, false, parentClassLoader);
        final Class<?> clazz2 = Class.forName(className, false, childClassLoader);
        final Class<?> clazz3 = Class.forName(className, false, childClassLoader);
        final Class<?> clazz4 = Class.forName(className, false, childClassLoader);
        Assert.assertNotEquals(clazz1, clazz2);
        Assert.assertEquals(clazz2, clazz3);
        Assert.assertEquals(clazz2, clazz4);
        childClassLoader.close();
    }

    @Test
    public void testRepeatedParentFirstPatternClass() throws Exception {
        final String className = ClassLoaderTest.class.getName();
        final String parentFirstPattern = className.substring(0, className.lastIndexOf('.'));
        final ClassLoader parentClassLoader = getClass().getClassLoader();
        // collect the libraries / class folders with RocksDB related code: the state backend and RocksDB itself
        final URL childCodePath = getClass().getProtectionDomain().getCodeSource().getLocation();
        final URLClassLoader childClassLoader = FlinkUserCodeClassLoaders.childFirst(new URL[]{ childCodePath }, parentClassLoader, new String[]{ parentFirstPattern });
        final Class<?> clazz1 = Class.forName(className, false, parentClassLoader);
        final Class<?> clazz2 = Class.forName(className, false, childClassLoader);
        final Class<?> clazz3 = Class.forName(className, false, childClassLoader);
        final Class<?> clazz4 = Class.forName(className, false, childClassLoader);
        Assert.assertEquals(clazz1, clazz2);
        Assert.assertEquals(clazz1, clazz3);
        Assert.assertEquals(clazz1, clazz4);
        childClassLoader.close();
    }
}

