/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.beans.tests.java.beans;


import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.apache.harmony.beans.tests.support.OtherBean;
import org.apache.harmony.beans.tests.support.SampleListener;
import org.apache.harmony.beans.tests.support.mock.MockFakeListener;
import org.apache.harmony.beans.tests.support.mock.MockPropertyChangeEvent;
import org.apache.harmony.beans.tests.support.mock.MockPropertyChangeListener;
import org.apache.harmony.beans.tests.support.mock.MockPropertyChangeListener2;
import org.apache.harmony.beans.tests.support.mock.MockPropertyChangeValidListener;


/**
 * Unit test for EventSetDescriptor
 */
public class EventSetDescriptorTest extends TestCase {
    public EventSetDescriptorTest() {
    }

    public EventSetDescriptorTest(String s) {
        super(s);
    }

    /* Class under test for void EventSetDescriptor(Class, String, Class,
    String)
     */
    public void testEventSetDescriptorClassStringClassString() throws IntrospectionException, IOException, ClassNotFoundException, NoSuchMethodException, SecurityException {
        String eventSetName = "mockPropertyChange";
        String listenerMethodName = eventSetName;
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = MockPropertyChangeListener.class;
        EventSetDescriptor esd = null;
        esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodName);
        String listenerName = getUnQualifiedClassName(listenerType);
        Method addMethod = sourceClass.getMethod(("add" + listenerName), new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod(("remove" + listenerName), new Class[]{ listenerType });
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertEquals(1, esd.getListenerMethods().length);
        TestCase.assertEquals(listenerMethodName, esd.getListenerMethods()[0].getName());
        TestCase.assertEquals(1, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethodName, esd.getListenerMethodDescriptors()[0].getMethod().getName());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
        esd = new EventSetDescriptor(EventSetDescriptorTest.AnObject.class, "something", EventSetDescriptorTest.AnObjectListener.class, "aMethod");
    }

    public void testEventSetDescriptorClassStringClassString2() throws IntrospectionException, IOException, ClassNotFoundException, NoSuchMethodException, SecurityException {
        String eventSetName = "mockPropertyChange";
        String listenerMethodName = eventSetName;
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = MockPropertyChangeListener.class;
        try {
            new EventSetDescriptor(sourceClass, "FFF", listenerType, listenerMethodName);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
            // valid
        }
    }

    /* Sourceclass==null */
    public void testEventSetDescriptorClassStringClassString_sourceClassNull() throws IntrospectionException {
        String eventSetName = "mockPropertyChange";
        String listenerMethodName = eventSetName;
        Class<?> sourceClass = null;
        Class<?> listenerType = MockPropertyChangeListener.class;
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodName);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    /* Event is null */
    public void testEventSetDescriptorClassStringClassString_EventNull() throws IntrospectionException {
        String eventSetName = "mockPropertyChange";
        String listenerMethodName = eventSetName;
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = MockPropertyChangeListener.class;
        try {
            new EventSetDescriptor(sourceClass, null, listenerType, listenerMethodName);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    /* Eventsetname="" */
    public void testEventSetDescriptorClassStringClassString_EventEmpty() throws IntrospectionException {
        String eventSetName = "mockPropertyChange";
        String listenerMethodName = eventSetName;
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = MockPropertyChangeListener.class;
        try {
            // RI doesn't throw exception here but this doesn't really make
            // much sense. Moreover, it is against the java.beans
            // package description: null values or empty Strings are not
            // valid parameters unless explicitly stated
            new EventSetDescriptor(sourceClass, "", listenerType, listenerMethodName);
        } catch (IntrospectionException e) {
            // valid
        }
    }

    /* Event is not a subclass of java.util.EventObject. */
    public void testEventSetDescriptorClassStringClassString_EventInvalid() throws IntrospectionException {
        String eventSetName = "MockFake";
        String listenerMethodName = "mockNotAEventObject";
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = MockPropertyChangeListener.class;
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodName);
        TestCase.assertEquals(listenerMethodName, esd.getListenerMethods()[0].getName());
    }

    public void testEventSetDescriptorClassStringClassString_AmbiguousEvent() throws IntrospectionException, IOException, ClassNotFoundException, NoSuchMethodException, SecurityException {
        String eventSetName = "mockPropertyChange";
        String listenerMethodName = eventSetName;
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = MockPropertyChangeListener2.class;
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodName);
        String listenerName = getUnQualifiedClassName(listenerType);
        Method addMethod = sourceClass.getMethod(("add" + listenerName), new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod(("remove" + listenerName), new Class[]{ listenerType });
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertEquals(1, esd.getListenerMethods().length);
        TestCase.assertEquals(listenerMethodName, esd.getListenerMethods()[0].getName());
        TestCase.assertEquals(1, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethodName, esd.getListenerMethodDescriptors()[0].getMethod().getName());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
    }

    /* ListenerType=null */
    public void testEventSetDescriptorClassStringClassString_ListenerNull() throws IntrospectionException {
        String eventSetName = "mockPropertyChange";
        String listenerMethodName = eventSetName;
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = null;
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodName);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    /* ListenerType does not implement any EventListener */
    public void testEventSetDescriptorClassStringClassString_ListenerInvalid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        String listenerMethodName = "mockPropertyChange";
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = MockFakeListener.class;
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodName);
        String listenerName = getUnQualifiedClassName(listenerType);
        Method addMethod = sourceClass.getMethod(("add" + listenerName), new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod(("remove" + listenerName), new Class[]{ listenerType });
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertEquals(1, esd.getListenerMethods().length);
        TestCase.assertEquals(listenerMethodName, esd.getListenerMethods()[0].getName());
        TestCase.assertEquals(1, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethodName, esd.getListenerMethodDescriptors()[0].getMethod().getName());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
    }

    /* listenerMethodName is null */
    public void testEventSetDescriptorClassStringClassString_listenerMethodNameNull() throws IntrospectionException {
        String eventSetName = "mockPropertyChange";
        String listenerMethodName = null;
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = MockPropertyChangeListener.class;
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodName);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /* No this method specified by listenerMethodName */
    public void testEventSetDescriptorClassStringClassString_listenerMethodNameInvalid() {
        String eventSetName = "mockPropertyChange";
        String listenerMethodName = "";
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Class<?> listenerType = MockPropertyChangeListener.class;
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodName);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /* Class under test for void EventSetDescriptor(Class, String, Class,
    String[], String, String)
     */
    public void testEventSetDescriptorClassStringClassStringArrayStringString() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod().getName());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod().getName());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertEquals(2, esd.getListenerMethods().length);
        TestCase.assertEquals(listenerMethodNames[0], esd.getListenerMethods()[0].getName());
        TestCase.assertEquals(listenerMethodNames[1], esd.getListenerMethods()[1].getName());
        TestCase.assertEquals(MockPropertyChangeEvent.class, esd.getListenerMethods()[1].getParameterTypes()[0]);
        TestCase.assertEquals(2, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethodNames[0], esd.getListenerMethodDescriptors()[0].getMethod().getName());
        TestCase.assertEquals(listenerMethodNames[1], esd.getListenerMethodDescriptors()[1].getMethod().getName());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
    }

    /* sourceClass is null */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_sourceClassNull() throws IntrospectionException {
        Class<?> sourceClass = null;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    /* Event is null */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_eventNull() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod().getName());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod().getName());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertEquals(2, esd.getListenerMethods().length);
        TestCase.assertEquals(listenerMethodNames[0], esd.getListenerMethods()[0].getName());
        TestCase.assertEquals(listenerMethodNames[1], esd.getListenerMethods()[1].getName());
        TestCase.assertEquals(2, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethodNames[0], esd.getListenerMethodDescriptors()[0].getMethod().getName());
        TestCase.assertEquals(listenerMethodNames[1], esd.getListenerMethodDescriptors()[1].getMethod().getName());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
        // Regression for HARMONY-1504
        try {
            new EventSetDescriptor(sourceClass, null, listenerType, listenerMethodNames, addMethod, removeMethod);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /* Eventsetname="" */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_eventEmpty() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        try {
            // RI doesn't throw exception here but this
            // is against the java.beans package description:
            // null values or empty Strings are not
            // valid parameters unless explicitly stated
            EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
        } catch (IntrospectionException e) {
            // valid
        }
    }

    /* listenerType=null */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_ListenerNull() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = null;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    /* listenerMethodNames=null */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_listenerMethodNamesNull() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = null;
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
            // expected
        }
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, new String[]{ null }, addMethod, removeMethod);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /* contain invalid method. */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_listenerMethodNamesInvalid() {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange_Invalid", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testEventSetDescriptorClassStringClassStringArrayStringString_listenerMethodNamesValid() throws Exception {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeValidListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange_Valid", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        EventSetDescriptor eventSetDescriptor = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
        TestCase.assertEquals(2, eventSetDescriptor.getListenerMethods().length);
    }

    public void testEventSetDescriptorClassStringClassStringArrayStringString_listenerMethodNamesEmpty() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{  };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
        TestCase.assertEquals(0, esd.getListenerMethods().length);
    }

    /* addListenerMethodName==null */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_addListenerMethodNameNull() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = null;
        String removeMethod = "removeMockPropertyChangeListener";
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
        TestCase.assertNull(esd.getAddListenerMethod());
    }

    /* addListenerMethodName is invalid (args) */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_addListenerMethodNameInvalid() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener_Invalid";
        String removeMethod = "removeMockPropertyChangeListener";
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /* removeListenerMethodName==null */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_removeListenerMethodNameNull() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "removeMockPropertyChangeListener";
        String removeMethod = null;
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
        TestCase.assertNull(esd.getRemoveListenerMethod());
    }

    /* removeListenerMethodName is invalid */
    public void testEventSetDescriptorClassStringClassStringArrayStringString_removeListenerMethodNameInvalid() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "removeMockPropertyChangeListener";
        String removeMethod = "addMockPropertyChangeListener_Invalid";
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /* Class under test for void EventSetDescriptor(Class, String, Class,
    String[], String, String, String)
     */
    public void testEventSetDescriptorClassStringClassStringArrayStringStringString() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        String getMethod = "getMockPropertyChangeListener";
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod, getMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod().getName());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod().getName());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertEquals(2, esd.getListenerMethods().length);
        TestCase.assertEquals(listenerMethodNames[0], esd.getListenerMethods()[0].getName());
        TestCase.assertEquals(listenerMethodNames[1], esd.getListenerMethods()[1].getName());
        TestCase.assertEquals(2, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethodNames[0], esd.getListenerMethodDescriptors()[0].getMethod().getName());
        TestCase.assertEquals(listenerMethodNames[1], esd.getListenerMethodDescriptors()[1].getMethod().getName());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
        // Regression for HARMONY-1237
        try {
            new EventSetDescriptor(Thread.class, "0xABCD", Thread.class, new String[]{  }, "aaa", null, "bbb");
            TestCase.fail("IntrospectionException expected");
        } catch (IntrospectionException e) {
            // expected
        }
    }

    /* getListenerMethodName is null */
    public void testEventSetDescriptorClassStringClassStringArrayStringStringString_getListenerMethodNameNull() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        String getMethod = null;
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod, getMethod);
        TestCase.assertNull(esd.getGetListenerMethod());
        // Regression for Harmony-1504
        try {
            new EventSetDescriptor(sourceClass, eventSetName, listenerType, null, addMethod, removeMethod, getMethod);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /* getListenerMethodName is invalid (return void) */
    public void testEventSetDescriptorClassStringClassStringArrayStringStringString_getListenerMethodNameInvalid() throws IntrospectionException {
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        String[] listenerMethodNames = new String[]{ "mockPropertyChange", "mockPropertyChange2" };
        String addMethod = "addMockPropertyChangeListener";
        String removeMethod = "removeMockPropertyChangeListener";
        String getMethod = addMethod;
        EventSetDescriptor esd = new EventSetDescriptor(sourceClass, eventSetName, listenerType, listenerMethodNames, addMethod, removeMethod, getMethod);
        TestCase.assertNull(esd.getGetListenerMethod());
    }

    /* Class under test for void EventSetDescriptor(String, Class, Method[],
    Method, Method)
     */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, addMethod, removeMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
        TestCase.assertNull(esd.getGetListenerMethod());
        // RI reports true in the following assertion, so it returns exactly
        // the same array as it was specified in the EventSetDescriptor
        // constructor.
        TestCase.assertEquals(listenerMethods, esd.getListenerMethods());
        TestCase.assertEquals(2, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethods[0], esd.getListenerMethodDescriptors()[0].getMethod());
        TestCase.assertEquals(listenerMethods[1], esd.getListenerMethodDescriptors()[1].getMethod());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
    }

    /* eventSetName=null */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod_EventNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = null;
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, addMethod, removeMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertEquals(2, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethods[0], esd.getListenerMethodDescriptors()[0].getMethod());
        TestCase.assertEquals(listenerMethods[1], esd.getListenerMethodDescriptors()[1].getMethod());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
    }

    /* eventSetName="" */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod_EventEmpty() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", MockPropertyChangeEvent.class), listenerType.getMethod("mockPropertyChange2", MockPropertyChangeEvent.class) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", listenerType);
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", listenerType);
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, addMethod, removeMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
        TestCase.assertNull(esd.getGetListenerMethod());
        // RI asserts to true here
        TestCase.assertEquals(listenerMethods, esd.getListenerMethods());
        TestCase.assertEquals(2, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethods[0], esd.getListenerMethodDescriptors()[0].getMethod());
        TestCase.assertEquals(listenerMethods[1], esd.getListenerMethodDescriptors()[1].getMethod());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
    }

    /* listenerType=null */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod_ListenerTypeNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", MockPropertyChangeEvent.class), listenerType.getMethod("mockPropertyChange2", MockPropertyChangeEvent.class) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", listenerType);
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", listenerType);
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, null, listenerMethods, addMethod, removeMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertEquals(listenerMethods, esd.getListenerMethods());
        TestCase.assertEquals(2, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethods[0], esd.getListenerMethodDescriptors()[0].getMethod());
        TestCase.assertEquals(listenerMethods[1], esd.getListenerMethodDescriptors()[1].getMethod());
        TestCase.assertNull(esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
    }

    /* listenerMethods=null */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod_listenerMethodsNull() throws IntrospectionException, NoSuchMethodException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", listenerType);
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", listenerType);
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, ((Method[]) (null)), addMethod, removeMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertNull(esd.getListenerMethods());
        TestCase.assertNull(esd.getListenerMethodDescriptors());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
    }

    /* listenerMethods is invalid */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod_listenerMethodsInvalid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange_Invalid", ((Class[]) (null))) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, addMethod, removeMethod);
        TestCase.assertEquals(listenerMethods, esd.getListenerMethods());
    }

    /* addListenerMethod = null */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod_addListenerMethodNull() throws IntrospectionException, NoSuchMethodException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, null, removeMethod);
        TestCase.assertNull(esd.getAddListenerMethod());
    }

    /* addListenerMethod is invalid */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod_addListenerMethodInvalid() throws IntrospectionException, NoSuchMethodException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener_Invalid", ((Class[]) (null)));
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, addMethod, removeMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
    }

    /* removeListenerMethod = null */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod_remveListenerMethodNull() throws IntrospectionException, NoSuchMethodException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, null, null);
        TestCase.assertNull(esd.getRemoveListenerMethod());
    }

    /* removeListenerMethod is invalid */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethod_removeListenerMethodInvalid() throws IntrospectionException, NoSuchMethodException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod("addMockPropertyChangeListener_Invalid", ((Class[]) (null)));
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, addMethod, removeMethod);
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
    }

    /* Class under test for void EventSetDescriptor(String, Class, Method[],
    Method, Method, Method)
     */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethodMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        Method getMethod = sourceClass.getMethod("getMockPropertyChangeListener", new Class[]{ listenerType });
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, addMethod, removeMethod, getMethod);
        TestCase.assertEquals(getMethod, esd.getGetListenerMethod());
    }

    /* getListenerMethod is null */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethodMethod_getListenerMethodNull() throws IntrospectionException, NoSuchMethodException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, addMethod, removeMethod, null);
        TestCase.assertNull(esd.getGetListenerMethod());
    }

    /* getListenerMethod is invalid */
    public void testEventSetDescriptorStringClassMethodArrayMethodMethodMethod_getListenerMethodInvalid() throws IntrospectionException, NoSuchMethodException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        Method getMethod = sourceClass.getMethod("addMockPropertyChangeListener_Invalid", ((Class[]) (null)));
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethods, addMethod, removeMethod, getMethod);
        TestCase.assertEquals(getMethod, esd.getGetListenerMethod());
    }

    /* Class under test for void EventSetDescriptor(String, Class,
    MethodDescriptor[], Method, Method)
     */
    public void testEventSetDescriptorStringClassMethodDescriptorArrayMethodMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", MockPropertyChangeEvent.class), listenerType.getMethod("mockPropertyChange2", MockPropertyChangeEvent.class) };
        MethodDescriptor[] listenerMethodDescriptors = new MethodDescriptor[]{ new MethodDescriptor(listenerMethods[0]), new MethodDescriptor(listenerMethods[1]) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", listenerType);
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", listenerType);
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethodDescriptors, addMethod, removeMethod);
        TestCase.assertEquals(addMethod, esd.getAddListenerMethod());
        TestCase.assertEquals(removeMethod, esd.getRemoveListenerMethod());
        TestCase.assertNull(esd.getGetListenerMethod());
        TestCase.assertEquals(listenerMethods[0], esd.getListenerMethods()[0]);
        TestCase.assertEquals(listenerMethods[1], esd.getListenerMethods()[1]);
        TestCase.assertEquals(2, esd.getListenerMethodDescriptors().length);
        TestCase.assertEquals(listenerMethods[0], esd.getListenerMethodDescriptors()[0].getMethod());
        TestCase.assertEquals(listenerMethods[1], esd.getListenerMethodDescriptors()[1].getMethod());
        TestCase.assertEquals(listenerType, esd.getListenerType());
        TestCase.assertTrue(esd.isInDefaultEventSet());
        TestCase.assertFalse(esd.isUnicast());
    }

    /* listenerMethodDescriptors is null */
    public void testEventSetDescriptorStringClassMethodDescriptorArrayMethodMethod_ListenerMDNull() throws IntrospectionException, NoSuchMethodException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", listenerType);
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", listenerType);
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, ((MethodDescriptor[]) (null)), addMethod, removeMethod);
        TestCase.assertNull(esd.getListenerMethodDescriptors());
        TestCase.assertNull(esd.getListenerMethods());
    }

    /* listenerMethodDescriptors is invalid */
    public void testEventSetDescriptorStringClassMethodDescriptorArrayMethodMethod_ListenerMDInvalid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", MockPropertyChangeEvent.class), listenerType.getMethod("mockPropertyChange_Invalid") };
        MethodDescriptor[] listenerMethodDescriptors = new MethodDescriptor[]{ new MethodDescriptor(listenerMethods[0]), new MethodDescriptor(listenerMethods[1]) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", listenerType);
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", listenerType);
        // RI doesn't check parameters of listener methods
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethodDescriptors, addMethod, removeMethod);
        TestCase.assertEquals(0, esd.getListenerMethods()[1].getParameterTypes().length);
        TestCase.assertEquals(listenerMethodDescriptors[1], esd.getListenerMethodDescriptors()[1]);
    }

    public void testSetInDefaultEventSet() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", MockPropertyChangeEvent.class), listenerType.getMethod("mockPropertyChange2", MockPropertyChangeEvent.class) };
        MethodDescriptor[] listenerMethodDescriptors = new MethodDescriptor[]{ new MethodDescriptor(listenerMethods[0]), new MethodDescriptor(listenerMethods[1]) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", listenerType);
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", listenerType);
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethodDescriptors, addMethod, removeMethod);
        esd.setInDefaultEventSet(true);
        TestCase.assertTrue(esd.isInDefaultEventSet());
    }

    public void testSetInDefaultEventSet_false() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        MethodDescriptor[] listenerMethodDescriptors = new MethodDescriptor[]{ new MethodDescriptor(listenerMethods[0]), new MethodDescriptor(listenerMethods[1]) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethodDescriptors, addMethod, removeMethod);
        TestCase.assertTrue(esd.isInDefaultEventSet());
        esd.setInDefaultEventSet(false);
        TestCase.assertFalse(esd.isInDefaultEventSet());
    }

    public void testSetUnicast() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", new Class[]{ MockPropertyChangeEvent.class }), listenerType.getMethod("mockPropertyChange2", new Class[]{ MockPropertyChangeEvent.class }) };
        MethodDescriptor[] listenerMethodDescriptors = new MethodDescriptor[]{ new MethodDescriptor(listenerMethods[0]), new MethodDescriptor(listenerMethods[1]) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", new Class[]{ listenerType });
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", new Class[]{ listenerType });
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethodDescriptors, addMethod, removeMethod);
        TestCase.assertFalse(esd.isUnicast());
        esd.setInDefaultEventSet(true);
        TestCase.assertTrue(esd.isInDefaultEventSet());
    }

    public void testSetUnicast_false() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String eventSetName = "MockPropertyChange";
        Class<?> listenerType = MockPropertyChangeListener.class;
        Method[] listenerMethods = new Method[]{ listenerType.getMethod("mockPropertyChange", MockPropertyChangeEvent.class), listenerType.getMethod("mockPropertyChange2", MockPropertyChangeEvent.class) };
        MethodDescriptor[] listenerMethodDescriptors = new MethodDescriptor[]{ new MethodDescriptor(listenerMethods[0]), new MethodDescriptor(listenerMethods[1]) };
        Class<EventSetDescriptorTest.MockSourceClass> sourceClass = EventSetDescriptorTest.MockSourceClass.class;
        Method addMethod = sourceClass.getMethod("addMockPropertyChangeListener", listenerType);
        Method removeMethod = sourceClass.getMethod("removeMockPropertyChangeListener", listenerType);
        EventSetDescriptor esd = new EventSetDescriptor(eventSetName, listenerType, listenerMethodDescriptors, addMethod, removeMethod);
        TestCase.assertFalse(esd.isUnicast());
        esd.setInDefaultEventSet(false);
        TestCase.assertFalse(esd.isInDefaultEventSet());
    }

    /**
     * The test checks the constructor
     */
    public void testEventSetDescriptorConstructor() throws Exception {
        new EventSetDescriptor(OtherBean.class, "sample", SampleListener.class, "fireSampleEvent");
    }

    public void test_EventSetDescriptor_Constructor() throws Exception {
        EventSetDescriptor eventSetDescriptor = new EventSetDescriptor(((String) (null)), ((Class<?>) (null)), new MethodDescriptor[]{ null, null }, ((Method) (null)), ((Method) (null)));
        TestCase.assertNull(eventSetDescriptor.getName());
        TestCase.assertNull(eventSetDescriptor.getListenerType());
        TestCase.assertNull(eventSetDescriptor.getAddListenerMethod());
        TestCase.assertNull(eventSetDescriptor.getRemoveListenerMethod());
        try {
            eventSetDescriptor.getListenerMethods();
            TestCase.fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testConstructor_withAnotherListener() throws Exception {
        Method[] listenermethods = EventSetDescriptorTest.AnotherObjectListener.class.getDeclaredMethods();
        Method add = EventSetDescriptorTest.AnObject.class.getDeclaredMethod("addEventSetDescriptorTest$AnObjectListener", EventSetDescriptorTest.AnObjectListener.class);
        Method remove = EventSetDescriptorTest.AnObject.class.getDeclaredMethod("removeEventSetDescriptorTest$AnObjectListener", EventSetDescriptorTest.AnObjectListener.class);
        EventSetDescriptor esd = new EventSetDescriptor("something", EventSetDescriptorTest.AnObjectListener.class, listenermethods, add, remove);
        TestCase.assertNotNull(esd);
    }

    class MockSourceClass implements Serializable {
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 1L;

        public void addMockPropertyChangeListener(MockPropertyChangeListener listener) {
        }

        public void addMockPropertyChangeListener_Invalid() {
        }

        public void removeMockPropertyChangeListener(MockPropertyChangeListener listener) {
        }

        public MockPropertyChangeListener[] getMockPropertyChangeListener(MockPropertyChangeListener listeners) {
            return null;
        }

        public void addMockPropertyChangeListener2(MockPropertyChangeListener2 listener) {
        }

        public void removeMockPropertyChangeListener2(MockPropertyChangeListener2 listener) {
        }

        public void addMockFakeListener(MockFakeListener listener) {
        }

        public void removeMockFakeListener(MockFakeListener listener) {
        }
    }

    private interface AnObjectListener {
        public void aMethod(EventSetDescriptorTest.SomethingEvent s);
    }

    private static class AnObject {
        public void addEventSetDescriptorTest$AnObjectListener(EventSetDescriptorTest.AnObjectListener l) {
        }

        public void removeEventSetDescriptorTest$AnObjectListener(EventSetDescriptorTest.AnObjectListener l) {
        }
    }

    private static class SomethingEvent {}

    private interface AnotherObjectListener {
        public void anotherMethod(EventSetDescriptorTest.SomethingEvent s);
    }
}

