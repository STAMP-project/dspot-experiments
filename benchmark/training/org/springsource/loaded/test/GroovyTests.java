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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.ReloadableType;
import org.springsource.loaded.TypeRegistry;
import org.springsource.loaded.test.infra.ClassPrinter;
import org.springsource.loaded.test.infra.Result;
import org.springsource.loaded.test.infra.TestClassloaderWithRewriting;


public class GroovyTests extends SpringLoadedTests {
    // Accessing a field from another type, which just turns into property
    // access via a method
    @Test
    public void fields() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String a = "simple.Front";
        String b = "simple.Back";
        TypeRegistry r = getTypeRegistry(((a + ",") + b));
        ReloadableType rtypea = r.addType(a, loadBytesForClass(a));
        ReloadableType rtypeb = r.addType(b, loadBytesForClass(b));
        result = runUnguarded(rtypea.getClazz(), "run");
        Assert.assertEquals(35, result.returnValue);
        try {
            result = runUnguarded(rtypea.getClazz(), "run2");
            Assert.fail();
        } catch (InvocationTargetException ite) {
            // success - var2 doesn't exist yet
        }
        rtypeb.loadNewVersion("2", retrieveRename(b, (b + "2")));
        result = runUnguarded(rtypea.getClazz(), "run2");
        Assert.assertEquals(3355, result.returnValue);
    }

    // TODO why doesn't this need swapInit? Is that only required for static
    // field constants?
    @Test
    public void localVariables() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String a = "simple.LFront";
        TypeRegistry r = getTypeRegistry(a);
        ReloadableType rtypea = r.addType(a, loadBytesForClass(a));
        result = runUnguarded(rtypea.getClazz(), "run");
        Assert.assertEquals("abc", result.returnValue);
        result = runUnguarded(rtypea.getClazz(), "run2");
        Assert.assertEquals(99, result.returnValue);
        rtypea.loadNewVersion("2", retrieveRename(a, (a + "2")));
        result = runUnguarded(rtypea.getClazz(), "run");
        Assert.assertEquals("xxx", result.returnValue);
        result = runUnguarded(rtypea.getClazz(), "run2");
        Assert.assertEquals(88, result.returnValue);
    }

    @Test
    public void fieldsOnInstance() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String a = "simple.Front";
        String b = "simple.Back";
        TypeRegistry r = getTypeRegistry(((a + ",") + b));
        ReloadableType rtypea = r.addType(a, loadBytesForClass(a));
        ReloadableType rtypeb = r.addType(b, loadBytesForClass(b));
        Object instance = rtypea.getClazz().newInstance();
        result = SpringLoadedTests.runOnInstance(rtypea.getClazz(), instance, "run");
        Assert.assertEquals(35, result.returnValue);
        try {
            result = SpringLoadedTests.runOnInstance(rtypea.getClazz(), instance, "run2");
            Assert.fail();
        } catch (Exception e) {
            // success - var2 doesn't exist yet
        }
        // rtypea.fixupGroovyType();
        rtypeb.loadNewVersion("2", retrieveRename(b, (b + "2")));
        result = SpringLoadedTests.runOnInstance(rtypea.getClazz(), instance, "run2");
        // The field will not be initialized, so will contain 0
        Assert.assertEquals(0, result.returnValue);
    }

    // Changing the return value within a method
    @Test
    public void basic() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String t = "simple.Basic";
        TypeRegistry r = getTypeRegistry(t);
        ReloadableType rtype = r.addType(t, loadBytesForClass(t));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("hello", result.returnValue);
        rtype.loadNewVersion("2", retrieveRename(t, (t + "2")));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("goodbye", result.returnValue);
    }

    @Test
    public void basicInstance() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String t = "simple.Basic";
        TypeRegistry r = getTypeRegistry(t);
        ReloadableType rtype = r.addType(t, loadBytesForClass(t));
        Object instance = null;
        instance = rtype.getClazz().newInstance();
        // First method call to 'run' should return "hello"
        result = SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "run");
        Assert.assertEquals("hello", result.returnValue);
        // Version 3 makes run() call another local method to get the string
        // "abc"
        rtype.loadNewVersion("3", retrieveRename(t, (t + "3")));
        // Field f = rtype.getClazz().getDeclaredField("metaClass");
        // f.setAccessible(true);
        // Object mc = f.get(instance);
        // System.out.println("Metaclass is currently " + mc);
        // 
        // f.set(instance, null);
        // 
        // f =
        // rtype.getClazz().getDeclaredField("$class$groovy$lang$MetaClass");
        // f.setAccessible(true);
        // f.set(instance, null);
        // 
        // Method m = rtype.getClazz().getDeclaredMethod("getMetaClass");
        // m.setAccessible(true);
        // m.invoke(instance);
        // f.setAccessible(true);
        // mc = f.get(instance);
        // 9: invokevirtual #23; //Method
        // $getStaticMetaClass:()Lgroovy/lang/MetaClass;
        // 12: dup
        // 13: invokestatic #27; //Method
        // $get$$class$groovy$lang$MetaClass:()Ljava/lang/Class;
        // 16: invokestatic #33; //Method
        // org/codehaus/groovy/runtime/ScriptBytecodeAdapter.castToType:(Ljava/lang/Object;Ljav
        // a/lang/Class;)Ljava/lang/Object;
        // 19: checkcast #35; //class groovy/lang/MetaClass
        // 22: aload_0
        // 23: swap
        // 24: putfield #37; //Field metaClass:Lgroovy/lang/MetaClass;
        // 27: pop
        // 28: return
        // Method m = rtype.getClazz().getDeclaredMethod("$getStaticMetaClass");
        // m.setAccessible(true);
        // Object o = m.invoke(instance);
        // m =
        // rtype.getClazz().getDeclaredMethod("$get$$class$groovy$lang$MetaClass");
        // m.setAccessible(true);
        // Object p = m.invoke(null);
        // m =
        // rtype.getClazz().getClassLoader().loadClass("org.codehaus.groovy.runtime.ScriptBytecodeAdapter")
        // .getDeclaredMethod("castToType", Object.class, Class.class);
        // m.setAccessible(true);
        // 
        // Object mc = m.invoke(null, o, p);
        // Field f = rtype.getClazz().getDeclaredField("metaClass");
        // f.setAccessible(true);
        // f.set(instance, null);
        // instance = rtype.getClazz().newInstance();
        // System.out.println("Metaclass is currently " + mc);
        // Let's reinitialize the instance meta class by duplicating
        result = SpringLoadedTests.runOnInstance(rtype.getClazz(), instance, "run");
        // result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("abc", result.returnValue);
    }

    // The method calls another method to get the return string, test
    // that when the method we are calling changes, we do call the new
    // one (simply checking the callsite cache is reset)
    @Test
    public void basic2() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String t = "simple.BasicB";
        TypeRegistry r = getTypeRegistry(t);
        ReloadableType rtype = r.addType(t, loadBytesForClass(t));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("hello", result.returnValue);
        rtype.loadNewVersion("2", retrieveRename(t, (t + "2")));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("goodbye", result.returnValue);
    }

    // Similar to BasicB but now using non-static methods
    @Test
    public void basic3() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String t = "simple.BasicC";
        TypeRegistry r = getTypeRegistry(t);
        ReloadableType rtype = r.addType(t, loadBytesForClass(t));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("hello", result.returnValue);
        rtype.loadNewVersion("2", retrieveRename(t, (t + "2")));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("goodbye", result.returnValue);
    }

    // Now calling between two different types to check if we have
    // to clear more than 'our' state on a reload. In this scenario
    // the method being called is static
    @Test
    public void basic4() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String t = "simple.BasicD";
        String target = "simple.BasicDTarget";
        TypeRegistry r = getTypeRegistry(((t + ",") + target));
        ReloadableType rtype = r.addType(t, loadBytesForClass(t));
        ReloadableType rtypeTarget = r.addType(target, loadBytesForClass(target));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("hello", result.returnValue);
        rtype.loadNewVersion("2", retrieveRename(t, (t + "2")));
        rtypeTarget.loadNewVersion("2", retrieveRename(target, (target + "2")));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("abc", result.returnValue);
    }

    // Calling from one type to another, now the methods are non-static
    @Test
    public void basic5() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String t = "simple.BasicE";
        String target = "simple.BasicETarget";
        TypeRegistry r = getTypeRegistry(((t + ",") + target));
        ReloadableType rtype = r.addType(t, loadBytesForClass(t));
        ReloadableType rtypeTarget = r.addType(target, loadBytesForClass(target));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("hello", result.returnValue);
        rtype.loadNewVersion("2", retrieveRename(t, (t + "2")));
        rtypeTarget.loadNewVersion("2", retrieveRename(target, (target + "2")));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("foobar", result.returnValue);
    }

    // Now call a method on the target, then load a version where it is gone,
    // what happens?
    // I'm looking to determine what in the caller needs clearing out based on
    // what it has
    // cached about the target
    @Test
    public void basic6() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String t = "simple.BasicF";
        String target = "simple.BasicFTarget";
        // GlobalConfiguration.logging = true;
        // GlobalConfiguration.isRuntimeLogging = true;
        TypeRegistry r = getTypeRegistry(((t + ",") + target));
        ReloadableType rtype = r.addType(t, loadBytesForClass(t));
        ReloadableType rtypeTarget = r.addType(target, loadBytesForClass(target));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("123", result.returnValue);
        // rtype.loadNewVersion("2", retrieveRename(t, t + "2"));
        rtypeTarget.loadNewVersion("2", retrieveRename(target, (target + "2")));
        result = null;
        // The target method has been removed, should now fail to call it
        try {
            runUnguarded(rtype.getClazz(), "run");
            Assert.fail();
        } catch (InvocationTargetException ite) {
            // success
        }
        // Load the original back in, should work again
        rtypeTarget.loadNewVersion("3", retrieveRename(target, target));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("123", result.returnValue);
        // rtype.loadNewVersion("2", rtype.bytesInitial); //reload yourself
        // Load a new version that now returns an int
        rtypeTarget.loadNewVersion("4", retrieveRename(target, (target + "4")));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("4456", result.returnValue);
    }

    // Needs groovy 1.7.8
    @Test
    public void simpleValues() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String t = "simple.Values";
        String target = "simple.BasicFTarget";
        // GlobalConfiguration.logging = true;
        // GlobalConfiguration.isRuntimeLogging = true;
        TypeRegistry r = getTypeRegistry(((t + ",") + target));
        ReloadableType rtype = r.addType(t, loadBytesForClass(t));
        // ReloadableType rtypeTarget = r.addType(target,
        // loadBytesForClass(target));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals(new Integer(123), result.returnValue);
        rtype.loadNewVersion("2", retrieveRename(t, (t + "2")));
        // rtypeTarget.loadNewVersion("2", retrieveRename(target, target +
        // "2"));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals(new Integer(456), result.returnValue);
        // 
        // result = null;
        // 
        // // The target method has been removed, should now fail to call it
        // try {
        // runUnguarded(rtype.getClazz(), "run");
        // fail();
        // } catch (InvocationTargetException ite) {
        // // success
        // }
        // 
        // // Load the original back in, should work again
        // rtypeTarget.loadNewVersion("3", retrieveRename(target, target));
        // result = runUnguarded(rtype.getClazz(), "run");
        // assertEquals("123", result.returnValue);
        // 
        // // rtype.loadNewVersion("2", rtype.bytesInitial); //reload yourself
        // 
        // // Load a new version that now returns an int
        // rtypeTarget.loadNewVersion("4", retrieveRename(target, target +
        // "4"));
        // result = runUnguarded(rtype.getClazz(), "run");
        // assertEquals("4456", result.returnValue);
    }

    @Test
    public void staticInitializerReloading1() throws Exception {
        String t = "clinitg.One";
        TypeRegistry typeRegistry = getTypeRegistry(t);
        ReloadableType rtype = typeRegistry.addType(t, loadBytesForClass(t));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("5", result.returnValue);
        rtype.loadNewVersion("39", retrieveRename(t, (t + "2")));
        rtype.runStaticInitializer();// call is made on reloadable type

        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("7", result.returnValue);
    }

    @Test
    public void staticInitializerReloading2() throws Exception {
        String t = "clinitg.One";
        TypeRegistry typeRegistry = getTypeRegistry(t);
        ReloadableType rtype = typeRegistry.addType(t, loadBytesForClass(t));
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("5", result.returnValue);
        rtype.loadNewVersion("39", retrieveRename(t, (t + "2")));
        // use the 'new' ___clinit___ method to drive the static initializer
        Method staticInitializer = rtype.getClazz().getMethod("___clinit___");
        Assert.assertNotNull(staticInitializer);
        staticInitializer.invoke(null);
        result = runUnguarded(rtype.getClazz(), "run");
        Assert.assertEquals("7", result.returnValue);
    }

    /**
     * Reloading enums written in groovy - very simple enum
     */
    @Test
    public void enums() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String enumtype = "enums.WhatAnEnum";
        String intface = "enums.ExtensibleEnum";
        String runner = "enums.RunnerA";
        TypeRegistry typeRegistry = getTypeRegistry(((((enumtype + ",") + intface) + ",") + runner));
        // ReloadableType rtypeIntface =
        typeRegistry.addType(intface, loadBytesForClass(intface));
        ReloadableType rtypeEnum = typeRegistry.addType(enumtype, loadBytesForClass(enumtype));
        ReloadableType rtypeRunner = typeRegistry.addType(runner, loadBytesForClass(runner));
        result = runUnguarded(rtypeRunner.getClazz(), "run");
        // ClassPrinter.print(rtypeEnum.bytesInitial);
        assertContains("[RED GREEN BLUE]", result.stdout);
        System.out.println(result);
        byte[] bs = retrieveRename(enumtype, (enumtype + "2"), "enums.WhatAnEnum2$__clinit__closure1:enums.WhatAnEnum$__clinit__closure1", "[Lenums/WhatAnEnum2;:[Lenums/WhatAnEnum;", "Lenums/WhatAnEnum2;:Lenums/WhatAnEnum;", "enums/WhatAnEnum2:enums/WhatAnEnum");
        ClassPrinter.print(bs);
        rtypeEnum.loadNewVersion(bs);
        result = runUnguarded(rtypeRunner.getClazz(), "run");
        System.out.println(result);
        assertContains("[RED GREEN BLUE YELLOW]", result.stdout);
        // assertEquals("55", result.returnValue);
        // rtype.loadNewVersion("39", retrieveRename(t, t + "2"));
        // rtype.runStaticInitializer();
        // result = runUnguarded(rtype.getClazz(), "run");
        // assertEquals("99", result.returnValue);
    }

    /**
     * Reloading enums - more complex enum (grails-7776)
     */
    @Test
    public void enums2() throws Exception {
        binLoader = new TestClassloaderWithRewriting();
        String enumtype = "enums.WhatAnEnumB";
        String intface = "enums.ExtensibleEnumB";
        String runner = "enums.RunnerB";
        String closure = "enums.WhatAnEnumB$__clinit__closure1";
        TypeRegistry typeRegistry = getTypeRegistry(((((((enumtype + ",") + intface) + ",") + runner) + ",") + closure));
        // ReloadableType rtypeIntface =
        typeRegistry.addType(intface, loadBytesForClass(intface));
        ReloadableType rtypeClosure = typeRegistry.addType(closure, loadBytesForClass(closure));
        ReloadableType rtypeEnum = typeRegistry.addType(enumtype, loadBytesForClass(enumtype));
        ReloadableType rtypeRunner = typeRegistry.addType(runner, loadBytesForClass(runner));
        result = runUnguarded(rtypeRunner.getClazz(), "run");
        assertContains("[PETS_AT_THE_DISCO 1 JUMPING_INTO_A_HOOP 2 HAVING_A_NICE_TIME 3 LIVING_ON_A_LOG 4 WHAT_DID_YOU_DO 5 UNKNOWN 0]", result.stdout);
        byte[] cs = retrieveRename(closure, "enums.WhatAnEnumB2$__clinit__closure1", "enums.WhatAnEnumB2:enums.WhatAnEnumB");
        rtypeClosure.loadNewVersion(cs);
        byte[] bs = retrieveRename(enumtype, (enumtype + "2"), "enums.WhatAnEnumB2$__clinit__closure1:enums.WhatAnEnumB$__clinit__closure1", "[Lenums/WhatAnEnumB2;:[Lenums/WhatAnEnumB;", "enums/WhatAnEnumB2:enums/WhatAnEnumB");
        rtypeEnum.loadNewVersion(bs);
        result = runUnguarded(rtypeRunner.getClazz(), "run");
        System.out.println(result);
        assertContains("[PETS_AT_THE_DISCO 1 JUMPING_INTO_A_HOOP 2 HAVING_A_NICE_TIME 3 LIVING_ON_A_LOG 4 WHAT_DID_YOU_DO 5 WOBBLE 6 UNKNOWN 0]", result.stdout);
    }
}

