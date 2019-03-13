/**
 * Copyright (C) 2010 Google Inc.
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
package libcore.java.util.beans;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import libcore.util.SerializationTester;


public final class PropertyChangeSupportTest extends TestCase {
    public void testOldAndNewAreBothNull() {
        Object bean = new Object();
        PropertyChangeSupport support = new PropertyChangeSupport(bean);
        PropertyChangeSupportTest.EventLog listener = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(listener);
        PropertyChangeEvent nullToNull = new PropertyChangeEvent(bean, "a", null, null);
        support.firePropertyChange(nullToNull);
        TestCase.assertEquals(Arrays.<PropertyChangeEvent>asList(nullToNull), listener.log);
    }

    public void testOldAndNewAreTheSame() {
        Object bean = new Object();
        PropertyChangeSupport support = new PropertyChangeSupport(bean);
        PropertyChangeSupportTest.EventLog listener = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(listener);
        PropertyChangeEvent xToX = new PropertyChangeEvent(bean, "a", "x", new String("x"));
        support.firePropertyChange(xToX);
        TestCase.assertEquals(Arrays.<PropertyChangeEvent>asList(), listener.log);
    }

    public void testEventsFilteredByProxies() {
        Object bean = new Object();
        PropertyChangeEvent eventA = new PropertyChangeEvent(bean, "a", false, true);
        PropertyChangeEvent eventB = new PropertyChangeEvent(bean, "b", false, true);
        PropertyChangeEvent eventC = new PropertyChangeEvent(bean, "c", false, true);
        PropertyChangeSupport support = new PropertyChangeSupport(bean);
        PropertyChangeSupportTest.EventLog all = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(all);
        PropertyChangeSupportTest.EventLog proxiedA = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(new PropertyChangeListenerProxy("a", proxiedA));
        PropertyChangeSupportTest.EventLog addA = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener("a", addA);
        PropertyChangeSupportTest.EventLog addAProxiedB = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener("a", new PropertyChangeListenerProxy("b", addAProxiedB));
        PropertyChangeSupportTest.EventLog proxiedAB = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(new PropertyChangeListenerProxy("a", new PropertyChangeListenerProxy("b", proxiedAB)));
        PropertyChangeSupportTest.EventLog proxiedAA = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(new PropertyChangeListenerProxy("a", new PropertyChangeListenerProxy("a", proxiedAA)));
        PropertyChangeSupportTest.EventLog proxiedAAC = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(new PropertyChangeListenerProxy("a", new PropertyChangeListenerProxy("a", new PropertyChangeListenerProxy("c", proxiedAAC))));
        support.firePropertyChange(eventA);
        support.firePropertyChange(eventB);
        support.firePropertyChange(eventC);
        TestCase.assertEquals(Arrays.asList(eventA, eventB, eventC), all.log);
        TestCase.assertEquals(Arrays.asList(eventA), proxiedA.log);
        TestCase.assertEquals(Arrays.asList(eventA), addA.log);
        TestCase.assertEquals(Arrays.<PropertyChangeEvent>asList(), addAProxiedB.log);
        TestCase.assertEquals(Arrays.<PropertyChangeEvent>asList(), proxiedAB.log);
        TestCase.assertEquals(Arrays.<PropertyChangeEvent>asList(eventA), proxiedAA.log);
        TestCase.assertEquals(Arrays.<PropertyChangeEvent>asList(), proxiedAAC.log);
    }

    /**
     * Test that we need to do our own equals() work to manually unwrap an
     * arbitrary number of proxies.
     */
    public void testRemoveWithProxies() {
        Object bean = new Object();
        PropertyChangeSupport support = new PropertyChangeSupport(bean);
        PropertyChangeSupportTest.EventLog all = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(all);
        TestCase.assertEquals(1, support.getPropertyChangeListeners().length);
        PropertyChangeSupportTest.EventLog proxiedA = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(new PropertyChangeListenerProxy("a", proxiedA));
        TestCase.assertEquals(2, support.getPropertyChangeListeners().length);
        PropertyChangeSupportTest.EventLog addA = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener("a", addA);
        TestCase.assertEquals(3, support.getPropertyChangeListeners().length);
        PropertyChangeSupportTest.EventLog addAProxiedB = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener("a", new PropertyChangeListenerProxy("b", addAProxiedB));
        TestCase.assertEquals(4, support.getPropertyChangeListeners().length);
        PropertyChangeSupportTest.EventLog proxiedAB = new PropertyChangeSupportTest.EventLog();
        PropertyChangeListenerProxy proxyAB = new PropertyChangeListenerProxy("a", new PropertyChangeListenerProxy("b", proxiedAB));
        support.addPropertyChangeListener(proxyAB);
        TestCase.assertEquals(5, support.getPropertyChangeListeners().length);
        PropertyChangeSupportTest.EventLog proxiedAAC = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener(new PropertyChangeListenerProxy("a", new PropertyChangeListenerProxy("a", new PropertyChangeListenerProxy("c", proxiedAAC))));
        TestCase.assertEquals(6, support.getPropertyChangeListeners().length);
        support.removePropertyChangeListener(all);
        TestCase.assertEquals(5, support.getPropertyChangeListeners().length);
        support.removePropertyChangeListener("a", proxiedA);
        TestCase.assertEquals(4, support.getPropertyChangeListeners().length);
        support.removePropertyChangeListener(new PropertyChangeListenerProxy("a", addA));
        TestCase.assertEquals(3, support.getPropertyChangeListeners().length);
        support.removePropertyChangeListener("a", new PropertyChangeListenerProxy("b", addAProxiedB));
        TestCase.assertEquals(2, support.getPropertyChangeListeners().length);
        support.removePropertyChangeListener(proxyAB);
        TestCase.assertEquals(1, support.getPropertyChangeListeners().length);
        support.removePropertyChangeListener(proxiedAAC);
        support.removePropertyChangeListener(new PropertyChangeListenerProxy("a", proxiedAAC));
        support.removePropertyChangeListener("a", new PropertyChangeListenerProxy("c", proxiedAAC));
        support.removePropertyChangeListener("a", new PropertyChangeListenerProxy("c", new PropertyChangeListenerProxy("a", proxiedAAC)));
        TestCase.assertEquals(1, support.getPropertyChangeListeners().length);
        support.removePropertyChangeListener("a", new PropertyChangeListenerProxy("a", new PropertyChangeListenerProxy("c", proxiedAAC)));
        TestCase.assertEquals(0, support.getPropertyChangeListeners().length);
    }

    public void testAddingOneListenerTwice() {
        Object bean = new Object();
        PropertyChangeSupport support = new PropertyChangeSupport(bean);
        PropertyChangeSupportTest.EventLog log = new PropertyChangeSupportTest.EventLog();
        support.addPropertyChangeListener("a", log);
        support.addPropertyChangeListener(log);
        support.addPropertyChangeListener(log);
        support.addPropertyChangeListener("a", log);
        PropertyChangeEvent eventA = new PropertyChangeEvent(bean, "a", false, true);
        PropertyChangeEvent eventB = new PropertyChangeEvent(bean, "b", false, true);
        support.firePropertyChange(eventA);
        support.firePropertyChange(eventB);
        TestCase.assertEquals(Arrays.asList(eventA, eventA, eventA, eventA, eventB, eventB), log.log);
    }

    public void testAddingAListenerActuallyAddsAProxy() {
        Object bean = new Object();
        PropertyChangeSupport support = new PropertyChangeSupport(bean);
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
            }
        };
        support.addPropertyChangeListener("a", listener);
        PropertyChangeListenerProxy p1 = ((PropertyChangeListenerProxy) (support.getPropertyChangeListeners()[0]));
        TestCase.assertEquals(PropertyChangeListenerProxy.class, p1.getClass());
        TestCase.assertTrue((p1 != listener));// weird but consistent with the RI

        TestCase.assertEquals("a", p1.getPropertyName());
        TestCase.assertEquals(listener, p1.getListener());
    }

    public void testAddingAProxy() {
        Object bean = new Object();
        PropertyChangeSupport support = new PropertyChangeSupport(bean);
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
            }
        };
        PropertyChangeListenerProxy proxy = new PropertyChangeListenerProxy("a", listener);
        support.addPropertyChangeListener("b", proxy);
        // this proxy sets us up to receive 'b' events
        PropertyChangeListenerProxy p1 = ((PropertyChangeListenerProxy) (support.getPropertyChangeListeners()[0]));
        TestCase.assertEquals(PropertyChangeListenerProxy.class, p1.getClass());
        TestCase.assertEquals("b", p1.getPropertyName());
        // this proxy sets us up to receive 'a' events
        PropertyChangeListenerProxy p2 = ((PropertyChangeListenerProxy) (p1.getListener()));
        TestCase.assertEquals(PropertyChangeListenerProxy.class, p2.getClass());
        TestCase.assertEquals("a", p2.getPropertyName());
        TestCase.assertEquals(listener, p2.getListener());
    }

    public void testSerialize() {
        String s = "aced0005737200206a6176612e6265616e732e50726f70657274794368616e67" + (((((((((((((("65537570706f727458d5d264574860bb03000349002a70726f706572747943686" + "16e6765537570706f727453657269616c697a65644461746156657273696f6e4c") + "00086368696c6472656e7400154c6a6176612f7574696c2f486173687461626c6") + "53b4c0006736f757263657400124c6a6176612f6c616e672f4f626a6563743b78") + "7000000002737200136a6176612e7574696c2e486173687461626c6513bb0f252") + "14ae4b803000246000a6c6f6164466163746f724900097468726573686f6c6478") + "703f4000000000000877080000000b00000001740001617371007e00000000000") + "2707400046265616e7372003a6c6962636f72652e6a6176612e7574696c2e6265") + "616e732e50726f70657274794368616e6765537570706f7274546573742445766") + "56e744c6f67b92667637d0b6f450200024c00036c6f677400104c6a6176612f75") + "74696c2f4c6973743b4c00046e616d657400124c6a6176612f6c616e672f53747") + "2696e673b7870737200136a6176612e7574696c2e41727261794c6973747881d2") + "1d99c7619d03000149000473697a6578700000000077040000000a7874000b6c6") + "97374656e6572546f4171007e000c70787871007e00087371007e00097371007e") + "000d0000000077040000000a7874000d6c697374656e6572546f416c6c7078");
        Object bean = "bean";
        PropertyChangeSupport support = new PropertyChangeSupport(bean);
        PropertyChangeSupportTest.EventLog listenerToAll = new PropertyChangeSupportTest.EventLog();
        listenerToAll.name = "listenerToAll";
        PropertyChangeSupportTest.EventLog listenerToA = new PropertyChangeSupportTest.EventLog();
        listenerToA.name = "listenerToA";
        support.addPropertyChangeListener(listenerToAll);
        support.addPropertyChangeListener("a", listenerToA);
        support.addPropertyChangeListener("a", listenerToA);
        new SerializationTester<PropertyChangeSupport>(support, s) {
            @Override
            protected boolean equals(PropertyChangeSupport a, PropertyChangeSupport b) {
                return describe(a.getPropertyChangeListeners()).equals(describe(b.getPropertyChangeListeners()));
            }

            @Override
            protected void verify(PropertyChangeSupport deserialized) {
                TestCase.assertEquals("[a to listenerToA, a to listenerToA, listenerToAll]", describe(deserialized.getPropertyChangeListeners()));
            }
        }.test();
    }

    static class EventLog implements PropertyChangeListener , Serializable {
        String name = "EventLog";

        List<PropertyChangeEvent> log = new ArrayList<PropertyChangeEvent>();

        public void propertyChange(PropertyChangeEvent event) {
            log.add(event);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

