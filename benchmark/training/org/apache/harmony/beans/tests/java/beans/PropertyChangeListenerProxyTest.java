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


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import junit.framework.TestCase;


/**
 * Test for PropertyChangeListenerProxy
 */
public class PropertyChangeListenerProxyTest extends TestCase {
    PropertyChangeListenerProxy proxy;

    PropertyChangeListener listener = new PropertyChangeListenerProxyTest.MockPropertyChangeListener();

    String name = "mock";

    static PropertyChangeEvent event = null;

    public void testPropertyChangeListenerProxy() {
        proxy = new PropertyChangeListenerProxy(null, listener);
        TestCase.assertSame(listener, proxy.getListener());
        TestCase.assertNull(proxy.getPropertyName());
        PropertyChangeEvent newevent = new PropertyChangeEvent(new Object(), "name", new Object(), new Object());
        proxy.propertyChange(newevent);
        TestCase.assertSame(newevent, PropertyChangeListenerProxyTest.event);
        proxy = new PropertyChangeListenerProxy(name, null);
        TestCase.assertSame(name, proxy.getPropertyName());
        TestCase.assertNull(proxy.getListener());
        try {
            proxy.propertyChange(new PropertyChangeEvent(new Object(), "name", new Object(), new Object()));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
        }
        proxy = new PropertyChangeListenerProxy(name, listener);
        TestCase.assertSame(listener, proxy.getListener());
        TestCase.assertSame(name, proxy.getPropertyName());
        newevent = new PropertyChangeEvent(new Object(), "name", new Object(), new Object());
        TestCase.assertSame(name, proxy.getPropertyName());
        proxy.propertyChange(newevent);
        TestCase.assertSame(newevent, PropertyChangeListenerProxyTest.event);
    }

    public void testPropertyChange() {
        proxy.propertyChange(null);
        TestCase.assertNull(PropertyChangeListenerProxyTest.event);
    }

    /**
     * Regression for HARMONY-407
     */
    public void testPropertyChange_PropertyChangeEvent() {
        PropertyChangeListenerProxy proxy = new PropertyChangeListenerProxy("harmony", null);
        try {
            proxy.propertyChange(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
        }
    }

    public static class MockPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent newevent) {
            PropertyChangeListenerProxyTest.event = newevent;
        }
    }
}

