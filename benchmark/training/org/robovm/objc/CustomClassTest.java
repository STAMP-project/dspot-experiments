/**
 * Copyright (C) 2015 RoboVM AB
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
package org.robovm.objc;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSObjectProtocol;
import org.robovm.apple.foundation.NSString;
import org.robovm.objc.annotation.BindSelector;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.objc.annotation.NotImplemented;
import org.robovm.rt.bro.annotation.Callback;


/**
 * Tests that the {@code ObjCMemberPlugin} generates the expected
 * {@link Callback} methods for classes subclassing native ObjC classes.
 */
public class CustomClassTest {
    public static class SubClass1 extends NSObject {
        @Override
        public String description() {
            return "(overridden) " + (super.description());
        }
    }

    @CustomClass("SubClass2")
    public static class SubClass2 extends NSObject {}

    public static class SubClass3 extends CustomClassTest.SubClass1 {
        @Override
        public String description() {
            return "(overridden again) " + (super.description());
        }
    }

    public interface Protocol extends NSObjectProtocol {
        @Method(selector = "foo:")
        NSObject foo(NSObject obj);

        @Method(selector = "bar:")
        NSObject bar(NSObject obj);
    }

    public static class ProtocolAdapter extends NSObject implements CustomClassTest.Protocol {
        @NotImplemented("foo:")
        public NSObject foo(NSObject obj) {
            return null;
        }

        @NotImplemented("bar:")
        public NSObject bar(NSObject obj) {
            return null;
        }
    }

    public static class ProtocolImpl extends CustomClassTest.ProtocolAdapter {
        @Override
        public NSObject foo(NSObject obj) {
            return new NSString(("foo" + obj));
        }
    }

    public abstract static class AbstractBaseClass extends NSObject {
        @Override
        public String description() {
            return "(abstract base) " + (super.description());
        }
    }

    public static class ConcreteSubClass extends CustomClassTest.AbstractBaseClass {
        @Override
        public String description() {
            return "(concrete) " + (super.description());
        }
    }

    public interface NSObjectProxyProtocol extends NSObjectProtocol {
        @Method(selector = "description")
        public String description();
    }

    public static class NSObjectProxyProtocolReturner extends NSObject {
        @Method(selector = "performSelector:")
        public native final CustomClassTest.NSObjectProxyProtocol performSelector2(Selector aSelector);
    }

    @Test
    public void testCustomClassName() {
        CustomClassTest.SubClass1 o1 = new CustomClassTest.SubClass1();
        Assert.assertEquals(("j_" + (CustomClassTest.SubClass1.class.getName().replace('.', '_'))), getObjCClass().getName());
        CustomClassTest.SubClass2 o2 = new CustomClassTest.SubClass2();
        Assert.assertEquals("SubClass2", getObjCClass().getName());
    }

    @Test
    public void testCallOverridenMethodFromJava() {
        CustomClassTest.SubClass1 o = new CustomClassTest.SubClass1();
        Assert.assertTrue(o.description().startsWith(("(overridden) " + "<j_org_robovm_objc_CustomClassTest$SubClass1: 0x")));
    }

    @Test
    public void testCallOverridenMethodFromObjC() {
        CustomClassTest.SubClass1 o = new CustomClassTest.SubClass1();
        NSString description = ((NSString) (o.performSelector(Selector.register("description"))));
        Assert.assertEquals(o.description(), description.toString());
    }

    @Test
    public void testCallOverridenOverriddenMethodFromObjC() {
        CustomClassTest.SubClass3 o = new CustomClassTest.SubClass3();
        NSString description = ((NSString) (o.performSelector(Selector.register("description"))));
        Assert.assertEquals(o.description(), description.toString());
    }

    @Test
    public void testOnlySingleCallbackInHierarchy() throws Exception {
        Method method1 = CustomClassTest.SubClass1.class.getDeclaredMethod("$cb$description", CustomClassTest.SubClass1.class, Selector.class);
        Assert.assertNotNull(method1.getAnnotation(Callback.class));
        Assert.assertNotNull(method1.getAnnotation(BindSelector.class));
        Assert.assertEquals("description", method1.getAnnotation(BindSelector.class).value());
        try {
            CustomClassTest.SubClass3.class.getDeclaredMethod("$cb$description", CustomClassTest.SubClass2.class, Selector.class);
            Assert.fail("NoSuchMethodException expected");
        } catch (NoSuchMethodException e) {
        }
    }

    @Test
    public void testNotImplemented() throws Exception {
        Assert.assertFalse(getMethodNames(CustomClassTest.ProtocolAdapter.class).contains("$cb$foo$"));
    }

    @Test
    public void testCallProtocolMethodFromObjC() throws Exception {
        Assert.assertTrue(getMethodNames(CustomClassTest.ProtocolImpl.class).contains("$cb$foo$"));
        Assert.assertFalse(getMethodNames(CustomClassTest.ProtocolImpl.class).contains("$cb$bar$"));
        CustomClassTest.ProtocolImpl p = new CustomClassTest.ProtocolImpl();
        NSObject o = p.performSelector(Selector.register("foo:"), new NSString("bar"));
        Assert.assertEquals("foobar", o.toString());
    }

    @Test
    public void testAbstractBaseClass() throws Exception {
        Assert.assertTrue(getMethodNames(CustomClassTest.AbstractBaseClass.class).contains("$cb$description"));
        Assert.assertFalse(getMethodNames(CustomClassTest.ConcreteSubClass.class).contains("$cb$description"));
        CustomClassTest.AbstractBaseClass o = new CustomClassTest.ConcreteSubClass();
        NSString description = ((NSString) (o.performSelector(Selector.register("description"))));
        Assert.assertEquals(o.description(), description.toString());
    }

    @Test
    public void testObjCProxy() throws Exception {
        Class<?> objcProxyCls = Class.forName(((CustomClassTest.NSObjectProxyProtocol.class.getName()) + "$ObjCProxy"));
        Assert.assertTrue(getMethodNames(objcProxyCls).contains("$cb$description"));
        CustomClassTest.NSObjectProxyProtocolReturner pr = new CustomClassTest.NSObjectProxyProtocolReturner();
        CustomClassTest.NSObjectProxyProtocol description = pr.performSelector2(Selector.register("description"));
        Assert.assertSame(objcProxyCls, description.getClass());
        Assert.assertEquals(as(NSObject.class).description(), description.toString());
        // Make sure the proxy class isn't treated as a custom class
        @SuppressWarnings("unchecked")
        ObjCClass objcClass = ObjCClass.getByType(((Class<? extends ObjCObject>) (objcProxyCls)));
        Assert.assertFalse(objcClass.isCustom());
    }
}

