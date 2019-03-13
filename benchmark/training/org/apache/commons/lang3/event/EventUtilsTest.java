/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.event;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.naming.event.ObjectChangeListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @since 3.0
 */
public class EventUtilsTest {
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new EventUtils());
        final Constructor<?>[] cons = EventUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(EventUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(EventUtils.class.getModifiers()));
    }

    @Test
    public void testAddEventListener() {
        final EventUtilsTest.PropertyChangeSource src = new EventUtilsTest.PropertyChangeSource();
        final EventUtilsTest.EventCountingInvociationHandler handler = new EventUtilsTest.EventCountingInvociationHandler();
        final PropertyChangeListener listener = handler.createListener(PropertyChangeListener.class);
        Assertions.assertEquals(0, handler.getEventCount("propertyChange"));
        EventUtils.addEventListener(src, PropertyChangeListener.class, listener);
        Assertions.assertEquals(0, handler.getEventCount("propertyChange"));
        src.setProperty("newValue");
        Assertions.assertEquals(1, handler.getEventCount("propertyChange"));
    }

    @Test
    public void testAddEventListenerWithNoAddMethod() {
        final EventUtilsTest.PropertyChangeSource src = new EventUtilsTest.PropertyChangeSource();
        final EventUtilsTest.EventCountingInvociationHandler handler = new EventUtilsTest.EventCountingInvociationHandler();
        final ObjectChangeListener listener = handler.createListener(ObjectChangeListener.class);
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> EventUtils.addEventListener(src, ObjectChangeListener.class, listener));
        Assertions.assertEquals((((((("Class " + (src.getClass().getName())) + " does not have a public add") + (ObjectChangeListener.class.getSimpleName())) + " method which takes a parameter of type ") + (ObjectChangeListener.class.getName())) + "."), e.getMessage());
    }

    @Test
    public void testAddEventListenerThrowsException() {
        final EventUtilsTest.ExceptionEventSource src = new EventUtilsTest.ExceptionEventSource();
        Assertions.assertThrows(RuntimeException.class, () -> EventUtils.addEventListener(src, PropertyChangeListener.class, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent e) {
                // Do nothing!
            }
        }));
    }

    @Test
    public void testAddEventListenerWithPrivateAddMethod() {
        final EventUtilsTest.PropertyChangeSource src = new EventUtilsTest.PropertyChangeSource();
        final EventUtilsTest.EventCountingInvociationHandler handler = new EventUtilsTest.EventCountingInvociationHandler();
        final VetoableChangeListener listener = handler.createListener(VetoableChangeListener.class);
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> EventUtils.addEventListener(src, VetoableChangeListener.class, listener));
        Assertions.assertEquals((((((("Class " + (src.getClass().getName())) + " does not have a public add") + (VetoableChangeListener.class.getSimpleName())) + " method which takes a parameter of type ") + (VetoableChangeListener.class.getName())) + "."), e.getMessage());
    }

    @Test
    public void testBindEventsToMethod() {
        final EventUtilsTest.PropertyChangeSource src = new EventUtilsTest.PropertyChangeSource();
        final EventUtilsTest.EventCounter counter = new EventUtilsTest.EventCounter();
        EventUtils.bindEventsToMethod(counter, "eventOccurred", src, PropertyChangeListener.class);
        Assertions.assertEquals(0, counter.getCount());
        src.setProperty("newValue");
        Assertions.assertEquals(1, counter.getCount());
    }

    @Test
    public void testBindEventsToMethodWithEvent() {
        final EventUtilsTest.PropertyChangeSource src = new EventUtilsTest.PropertyChangeSource();
        final EventUtilsTest.EventCounterWithEvent counter = new EventUtilsTest.EventCounterWithEvent();
        EventUtils.bindEventsToMethod(counter, "eventOccurred", src, PropertyChangeListener.class);
        Assertions.assertEquals(0, counter.getCount());
        src.setProperty("newValue");
        Assertions.assertEquals(1, counter.getCount());
    }

    @Test
    public void testBindFilteredEventsToMethod() {
        final EventUtilsTest.MultipleEventSource src = new EventUtilsTest.MultipleEventSource();
        final EventUtilsTest.EventCounter counter = new EventUtilsTest.EventCounter();
        EventUtils.bindEventsToMethod(counter, "eventOccurred", src, EventUtilsTest.MultipleEventListener.class, "event1");
        Assertions.assertEquals(0, counter.getCount());
        src.listeners.fire().event1(new PropertyChangeEvent(new Date(), "Day", Integer.valueOf(0), Integer.valueOf(1)));
        Assertions.assertEquals(1, counter.getCount());
        src.listeners.fire().event2(new PropertyChangeEvent(new Date(), "Day", Integer.valueOf(1), Integer.valueOf(2)));
        Assertions.assertEquals(1, counter.getCount());
    }

    public interface MultipleEventListener {
        void event1(PropertyChangeEvent e);

        void event2(PropertyChangeEvent e);
    }

    public static class EventCounter {
        private int count;

        public void eventOccurred() {
            (count)++;
        }

        public int getCount() {
            return count;
        }
    }

    public static class EventCounterWithEvent {
        private int count;

        public void eventOccurred(final PropertyChangeEvent e) {
            (count)++;
        }

        public int getCount() {
            return count;
        }
    }

    private static class EventCountingInvociationHandler implements InvocationHandler {
        private final Map<String, Integer> eventCounts = new TreeMap<>();

        public <L> L createListener(final Class<L> listenerType) {
            return listenerType.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ listenerType }, this));
        }

        public int getEventCount(final String eventName) {
            final Integer count = eventCounts.get(eventName);
            return count == null ? 0 : count.intValue();
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) {
            final Integer count = eventCounts.get(method.getName());
            if (count == null) {
                eventCounts.put(method.getName(), Integer.valueOf(1));
            } else {
                eventCounts.put(method.getName(), Integer.valueOf(((count.intValue()) + 1)));
            }
            return null;
        }
    }

    public static class MultipleEventSource {
        private final EventListenerSupport<EventUtilsTest.MultipleEventListener> listeners = EventListenerSupport.create(EventUtilsTest.MultipleEventListener.class);

        public void addMultipleEventListener(final EventUtilsTest.MultipleEventListener listener) {
            listeners.addListener(listener);
        }
    }

    public static class ExceptionEventSource {
        public void addPropertyChangeListener(final PropertyChangeListener listener) {
            throw new RuntimeException();
        }
    }

    public static class PropertyChangeSource {
        private final EventListenerSupport<PropertyChangeListener> listeners = EventListenerSupport.create(PropertyChangeListener.class);

        private String property;

        public void setProperty(final String property) {
            final String oldValue = this.property;
            this.property = property;
            listeners.fire().propertyChange(new PropertyChangeEvent(this, "property", oldValue, property));
        }

        protected void addVetoableChangeListener(final VetoableChangeListener listener) {
            // Do nothing!
        }

        public void addPropertyChangeListener(final PropertyChangeListener listener) {
            listeners.addListener(listener);
        }

        public void removePropertyChangeListener(final PropertyChangeListener listener) {
            listeners.removeListener(listener);
        }
    }
}

