/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.util;


import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;
import junit.framework.TestCase;


public class ObservableTest extends TestCase {
    static class TestObserver implements Observer {
        public Vector objv = new Vector();

        int updateCount = 0;

        public void update(Observable observed, Object arg) {
            ++(updateCount);
            objv.add(arg);
        }

        public int updateCount() {
            return updateCount;
        }
    }

    static class DeleteTestObserver implements Observer {
        int updateCount = 0;

        boolean deleteAll = false;

        public DeleteTestObserver(boolean all) {
            deleteAll = all;
        }

        public void update(Observable observed, Object arg) {
            ++(updateCount);
            if (deleteAll)
                observed.deleteObservers();
            else
                observed.deleteObserver(this);

        }

        public int updateCount() {
            return updateCount;
        }
    }

    static class TestObservable extends Observable {
        public void doChange() {
            setChanged();
        }

        public void clearChange() {
            clearChanged();
        }
    }

    Observer observer;

    ObservableTest.TestObservable observable;

    /**
     * java.util.Observable#Observable()
     */
    public void test_Constructor() {
        // Test for method java.util.Observable()
        try {
            Observable ov = new Observable();
            TestCase.assertTrue("Wrong initial values.", (!(ov.hasChanged())));
            TestCase.assertEquals("Wrong initial values.", 0, ov.countObservers());
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
    }

    /**
     * java.util.Observable#addObserver(java.util.Observer)
     */
    public void test_addObserverLjava_util_Observer() {
        // Test for method void
        // java.util.Observable.addObserver(java.util.Observer)
        ObservableTest.TestObserver test = new ObservableTest.TestObserver();
        observable.addObserver(test);
        TestCase.assertEquals("Failed to add observer", 1, observable.countObservers());
        observable.addObserver(test);
        TestCase.assertEquals("Duplicate observer", 1, observable.countObservers());
        Observable o = new Observable();
        try {
            o.addObserver(null);
            TestCase.fail("Expected adding a null observer to throw a NPE.");
        } catch (NullPointerException ex) {
            // expected;
        } catch (Throwable ex) {
            TestCase.fail(("Did not expect adding a new observer to throw a " + (ex.getClass().getName())));
        }
    }

    /**
     * java.util.Observable#countObservers()
     */
    public void test_countObservers() {
        // Test for method int java.util.Observable.countObservers()
        TestCase.assertEquals("New observable had > 0 observers", 0, observable.countObservers());
        observable.addObserver(new ObservableTest.TestObserver());
        TestCase.assertEquals("Observable with observer returned other than 1", 1, observable.countObservers());
    }

    /**
     * java.util.Observable#deleteObserver(java.util.Observer)
     */
    public void test_deleteObserverLjava_util_Observer() {
        // Test for method void
        // java.util.Observable.deleteObserver(java.util.Observer)
        observable.addObserver((observer = new ObservableTest.TestObserver()));
        observable.deleteObserver(observer);
        TestCase.assertEquals("Failed to delete observer", 0, observable.countObservers());
        observable.deleteObserver(observer);
        observable.deleteObserver(null);
    }

    /**
     * java.util.Observable#deleteObservers()
     */
    public void test_deleteObservers() {
        // Test for method void java.util.Observable.deleteObservers()
        observable.addObserver(new ObservableTest.TestObserver());
        observable.addObserver(new ObservableTest.TestObserver());
        observable.addObserver(new ObservableTest.TestObserver());
        observable.addObserver(new ObservableTest.TestObserver());
        observable.addObserver(new ObservableTest.TestObserver());
        observable.addObserver(new ObservableTest.TestObserver());
        observable.addObserver(new ObservableTest.TestObserver());
        observable.addObserver(new ObservableTest.TestObserver());
        observable.deleteObservers();
        TestCase.assertEquals("Failed to delete observers", 0, observable.countObservers());
    }

    /**
     * java.util.Observable#hasChanged()
     */
    public void test_hasChanged() {
        TestCase.assertFalse(observable.hasChanged());
        observable.addObserver((observer = new ObservableTest.TestObserver()));
        observable.doChange();
        TestCase.assertTrue(observable.hasChanged());
    }

    public void test_clearChanged() {
        TestCase.assertFalse(observable.hasChanged());
        observable.addObserver((observer = new ObservableTest.TestObserver()));
        observable.doChange();
        TestCase.assertTrue(observable.hasChanged());
        observable.clearChange();
        TestCase.assertFalse(observable.hasChanged());
    }

    /**
     * java.util.Observable#notifyObservers()
     */
    public void test_notifyObservers() {
        // Test for method void java.util.Observable.notifyObservers()
        observable.addObserver((observer = new ObservableTest.TestObserver()));
        observable.notifyObservers();
        TestCase.assertEquals("Notified when unchnaged", 0, ((ObservableTest.TestObserver) (observer)).updateCount());
        ((ObservableTest.TestObservable) (observable)).doChange();
        observable.notifyObservers();
        TestCase.assertEquals("Failed to notify", 1, ((ObservableTest.TestObserver) (observer)).updateCount());
        ObservableTest.DeleteTestObserver observer1;
        ObservableTest.DeleteTestObserver observer2;
        observable.deleteObservers();
        observable.addObserver((observer1 = new ObservableTest.DeleteTestObserver(false)));
        observable.addObserver((observer2 = new ObservableTest.DeleteTestObserver(false)));
        observable.doChange();
        observable.notifyObservers();
        TestCase.assertTrue("Failed to notify all", (((observer1.updateCount()) == 1) && ((observer2.updateCount()) == 1)));
        TestCase.assertEquals("Failed to delete all", 0, observable.countObservers());
        observable.addObserver((observer1 = new ObservableTest.DeleteTestObserver(false)));
        observable.addObserver((observer2 = new ObservableTest.DeleteTestObserver(false)));
        observable.doChange();
        observable.notifyObservers();
        TestCase.assertTrue("Failed to notify all 2", (((observer1.updateCount()) == 1) && ((observer2.updateCount()) == 1)));
        TestCase.assertEquals("Failed to delete all 2", 0, observable.countObservers());
    }

    /**
     * java.util.Observable#notifyObservers(java.lang.Object)
     */
    public void test_notifyObserversLjava_lang_Object() {
        // Test for method void
        // java.util.Observable.notifyObservers(java.lang.Object)
        Object obj;
        observable.addObserver((observer = new ObservableTest.TestObserver()));
        observable.notifyObservers();
        TestCase.assertEquals("Notified when unchanged", 0, ((ObservableTest.TestObserver) (observer)).updateCount());
        ((ObservableTest.TestObservable) (observable)).doChange();
        observable.notifyObservers((obj = new Object()));
        TestCase.assertEquals("Failed to notify", 1, ((ObservableTest.TestObserver) (observer)).updateCount());
        TestCase.assertTrue("Failed to pass Object arg", ((ObservableTest.TestObserver) (observer)).objv.elementAt(0).equals(obj));
    }

    static final class AlwaysChangedObservable extends Observable {
        @Override
        public boolean hasChanged() {
            return true;
        }
    }

    // http://b/28797950
    public void test_observableWithOverridenHasChanged() throws Exception {
        final AtomicReference<Observable> updated = new AtomicReference<>();
        final Observer observer = ( observable1, data) -> updated.set(observable1);
        Observable alwaysChanging = new ObservableTest.AlwaysChangedObservable();
        alwaysChanging.addObserver(observer);
        alwaysChanging.notifyObservers(null);
        TestCase.assertSame(alwaysChanging, updated.get());
    }
}

