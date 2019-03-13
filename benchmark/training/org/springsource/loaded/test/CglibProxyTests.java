/**
 * Copyright 2010-2012 VMware and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springsource.loaded.test;


import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.ReloadableType;
import org.springsource.loaded.TypeRegistry;


/**
 * Test dealing with CGLIB proxies and auto-regenerating them. This covers the EnhancerByCGLIB types and the
 * FastClassByCGLIB types.
 *
 * @author Andy Clement
 * @since 0.8.3
 */
public class CglibProxyTests extends SpringLoadedTests {
    URLClassLoader cglibLoader;

    @Test
    public void stringReplacement() {
        String s2 = null;
        s2 = CglibProxyTests.removeSpecificCodes("example.Simple$$EnhancerByCGLIB$$12345678$$FastClassByCGLIB$$12345678").toString();
        Assert.assertEquals("example.Simple$$EnhancerByCGLIB$$........$$FastClassByCGLIB$$........", s2);
        s2 = CglibProxyTests.removeSpecificCodes("example.Simple$$EnhancerByCGLIB$$1234568$$FastClassByCGLIB$$1234568").toString();
        Assert.assertEquals("example.Simple$$EnhancerByCGLIB$$........$$FastClassByCGLIB$$........", s2);
        s2 = CglibProxyTests.removeSpecificCodes("example.Simple$$EnhancerByCGLIB$$1234568$$FastClassByCGLIB$$12345678").toString();
        Assert.assertEquals("example.Simple$$EnhancerByCGLIB$$........$$FastClassByCGLIB$$........", s2);
        s2 = CglibProxyTests.removeSpecificCodes("example.Simple$$EnhancerByCGLIB$$12345678$$FastClassByCGLIB$$1234568").toString();
        Assert.assertEquals("example.Simple$$EnhancerByCGLIB$$........$$FastClassByCGLIB$$........", s2);
    }

    // Possibly now failing due to quick fix to copy method/field for grails (~build 113)
    /**
     * This test is quite basic. It is testing interactions with classes through CGLIB generated proxies. The
     * ProxyTestcase creates a proxy for a type (@see ProxyBuilder). A proxy knows about the type which it is standing
     * in for and knows about a method interceptor that will be called when methods on the proxy are invoked. It is up
     * to the interceptor whether the 'original' method runs (by calling super or the MethodProxy passed into the
     * interceptor). This first test does *not* call the super methods. No FastClass objects involved in this test.
     */
    @Test
    public void testSimpleProxyNoSuperCallsNoFastClass() throws Exception {
        String t = "example.ProxyTestcase";
        Class<?> clazz = binLoader.loadClass(t);
        runMethodAndCollectOutput(clazz, "configureTest1");
        String output = runMethodAndCollectOutput(clazz, "run");
        // interception should have occurred and original should not have been run
        assertContains("[void example.Simple.moo()]", output);
        assertDoesNotContain("Simple.moo() running", output);
        // Check we loaded it as reloadable
        ReloadableType rtype = TypeRegistry.getTypeRegistryFor(binLoader).getReloadableType(SpringLoadedTests.toSlash(t), false);
        Assert.assertNotNull(rtype);
        // Check the incidental types were loaded as reloadable
        ReloadableType rtype2 = TypeRegistry.getTypeRegistryFor(binLoader).getReloadableType(SpringLoadedTests.toSlash("example.Simple"), false);
        Assert.assertNotNull(rtype2);
        rtype.loadNewVersion(retrieveRename(t, (t + "2"), "example.Simple2:example.Simple"));
        rtype2.loadNewVersion(retrieveRename("example.Simple", "example.Simple2"));
        // Now running 'boo()' which did not exist in the original. Remember this is invoked via proxy and so will only work
        // if the proxy was autoregenerated and reloaded!
        output = runMethodAndCollectOutput(clazz, "run");
        assertContains("[void example.Simple.boo()]", output);
        assertDoesNotContain("Simple2.boo running()", output);
    }

    // Possibly now failing due to quick fix to copy method/field for grails (~build 113)
    /**
     * Variation of the test above, but now the super calls are allowed to occur. This means FastClass objects will be
     * created by CGLIB, these also need auto regenerating and reloading.
     */
    @Test
    public void testSimpleProxyWithSuperCallsWithFastClass() throws Exception {
        // binLoader = new TestClassloaderWithRewriting(true, true, true);
        String t = "example.ProxyTestcase";
        Class<?> clazz = binLoader.loadClass(t);
        String output = runMethodAndCollectOutput(clazz, "run");
        // interception should have occurred and original should have been run
        assertContains("[void example.Simple.moo()]", output);
        assertContains("Simple.moo() running", output);
        // Check we loaded it as reloadable
        ReloadableType rtype = TypeRegistry.getTypeRegistryFor(binLoader).getReloadableType(SpringLoadedTests.toSlash(t), false);
        Assert.assertNotNull(rtype);
        Set<String> rtypesForFastClass = getFastClasses(TypeRegistry.getTypeRegistryFor(binLoader).getReloadableTypes());
        Assert.assertEquals(2, rtypesForFastClass.size());
        assertContains("example.Simple$$FastClassByCGLIB$$........", rtypesForFastClass.toString());
        assertContains("example.Simple$$EnhancerByCGLIB$$........$$FastClassByCGLIB$$........", rtypesForFastClass.toString());
        // Check the incidental types were loaded as reloadable
        ReloadableType rtype2 = TypeRegistry.getTypeRegistryFor(binLoader).getReloadableType(SpringLoadedTests.toSlash("example.Simple"), false);
        Assert.assertNotNull(rtype2);
        rtype.loadNewVersion(retrieveRename(t, (t + "2"), "example.Simple2:example.Simple"));
        rtype2.loadNewVersion(retrieveRename("example.Simple", "example.Simple2"));
        output = runMethodAndCollectOutput(clazz, "run");
        assertContains("Simple2.boo() running", output);
        assertContains("[void example.Simple.boo()]", output);
        output = runMethodAndCollectOutput(clazz, "runMoo");
        // new version should run: note the 2 on the classname
        assertContains("Simple2.moo() running", output);
        assertContains("[void example.Simple.moo()]", output);
        // try a method with parameters
        output = runMethodAndCollectOutput(clazz, "runBar");
        assertContains("Simple2.bar(1,abc,3) running", output);
        assertContains("[public void example.Simple.bar(int,java.lang.String,long)]", output);
    }

    // Possibly now failing due to quick fix to copy method/field for grails (~build 113)
    @Test
    public void testSimpleProxyWithSuperCallsWithFastClass2() throws Exception {
        // binLoader = new TestClassloaderWithRewriting(true, true, true);
        String t = "example.ProxyTestcase";
        Class<?> clazz = binLoader.loadClass(t);
        String output = runMethodAndCollectOutput(clazz, "run");
        // interception should have occurred and original should have been run
        assertContains("[void example.Simple.moo()]", output);
        assertContains("Simple.moo() running", output);
        // Check we loaded it as reloadable
        ReloadableType rtype = TypeRegistry.getTypeRegistryFor(binLoader).getReloadableType(SpringLoadedTests.toSlash(t), false);
        Assert.assertNotNull(rtype);
        Set<String> rtypesForFastClass = getFastClasses(TypeRegistry.getTypeRegistryFor(binLoader).getReloadableTypes());
        Assert.assertEquals(2, rtypesForFastClass.size());
        assertContains("example.Simple$$FastClassByCGLIB$$........", rtypesForFastClass.toString());
        assertContains("example.Simple$$EnhancerByCGLIB$$........$$FastClassByCGLIB$$........", rtypesForFastClass.toString());
        // Check the incidental types were loaded as reloadable
        ReloadableType rtype2 = TypeRegistry.getTypeRegistryFor(binLoader).getReloadableType(SpringLoadedTests.toSlash("example.Simple"), false);
        Assert.assertNotNull(rtype2);
        rtype.loadNewVersion(retrieveRename(t, (t + "2"), "example.Simple2:example.Simple"));
        rtype2.loadNewVersion(retrieveRename("example.Simple", "example.Simple2"));
        output = runMethodAndCollectOutput(clazz, "run");
        assertContains("Simple2.boo() running", output);
        assertContains("[void example.Simple.boo()]", output);
        output = runMethodAndCollectOutput(clazz, "runMoo");
        // new version should run: note the 2 on the classname
        assertContains("Simple2.moo() running", output);
        assertContains("[void example.Simple.moo()]", output);
        // try a method with parameters
        output = runMethodAndCollectOutput(clazz, "runBar");
        assertContains("Simple2.bar(1,abc,3) running", output);
        assertContains("[public void example.Simple.bar(int,java.lang.String,long)]", output);
        clazz = binLoader.loadClass("example.Simple");
        Class<?> fastClazz = binLoader.loadClass("net.sf.cglib.reflect.FastClass");
        Method createMethod = fastClazz.getDeclaredMethod("create", Class.class);
        Object fastClassForSimple = createMethod.invoke(null, clazz);
        Method getJavaClassMethod = fastClazz.getDeclaredMethod("getJavaClass");
        Class<?> simpleClazz = ((Class<?>) (getJavaClassMethod.invoke(fastClassForSimple)));
        Assert.assertNotNull(simpleClazz);
        Assert.assertEquals("example.Simple", simpleClazz.getName());
    }
}

