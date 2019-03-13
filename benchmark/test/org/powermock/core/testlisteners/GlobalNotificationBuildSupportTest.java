/**
 * Copyright 2013 the original author or authors.
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
package org.powermock.core.testlisteners;


import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.testlisteners.GlobalNotificationBuildSupport.Callback;


/**
 * The design of this test-class does only allow it to be run once per JVM
 * (or more accurately "per classloader", in case the test-class is redefined
 * by different classloaders in some sort of test suite),
 * because it will test some class instantiation, which can only occur one per
 * class.
 */
@SuppressWarnings({ "SameParameterValue", "ResultOfMethodCallIgnored" })
public class GlobalNotificationBuildSupportTest {
    static boolean initiationOfNormalClassIsUnderWay;

    static final Callback mockCallback = Mockito.mock(Callback.class);

    @SuppressWarnings("SameParameterValue")
    static class NormalClass {
        static {
            GlobalNotificationBuildSupportTest.initiationOfNormalClassIsUnderWay = true;
            GlobalNotificationBuildSupport.testClassInitiated(GlobalNotificationBuildSupportTest.NormalClass.class);
        }

        NormalClass(String dummy) {
            GlobalNotificationBuildSupport.testInstanceCreated(this);
        }

        public NormalClass() {
            this("dummy");
            GlobalNotificationBuildSupport.testInstanceCreated(this);
        }
    }

    @SuppressWarnings("SameParameterValue")
    static class SubClass extends GlobalNotificationBuildSupportTest.NormalClass {
        public SubClass() {
            super("dummy");
        }

        public SubClass(String dummy) {
        }
    }

    @Test
    public void normalClassCreation() {
        // Given
        Assert.assertFalse("Initiation of NormalClass must not yet have commenced", GlobalNotificationBuildSupportTest.initiationOfNormalClassIsUnderWay);
        GlobalNotificationBuildSupport.prepareTestSuite(nestedClassName("NormalClass"), GlobalNotificationBuildSupportTest.mockCallback);
        /* Nothing must have happened so far ... */
        Mockito.verifyNoMoreInteractions(GlobalNotificationBuildSupportTest.mockCallback);
        // When
        final GlobalNotificationBuildSupportTest.NormalClass normalInstance = new GlobalNotificationBuildSupportTest.NormalClass();
        // Then verify life-cycle callbacks on NormalClass
        Mockito.verify(GlobalNotificationBuildSupportTest.mockCallback).suiteClassInitiated(GlobalNotificationBuildSupportTest.NormalClass.class);
        // Then notifications of created instances are expected ...
        assertNotificationOf(normalInstance);
        assertNotificationOf(new GlobalNotificationBuildSupportTest.NormalClass());
        assertNotificationOf(new GlobalNotificationBuildSupportTest.NormalClass());
        assertNotificationOf(new GlobalNotificationBuildSupportTest.NormalClass("dummy"));
        assertNotificationOf(new GlobalNotificationBuildSupportTest.SubClass("dummy"));
        assertNotificationOf(new GlobalNotificationBuildSupportTest.NormalClass("dummy"));
        assertNotificationOf(new GlobalNotificationBuildSupportTest.SubClass("dummy"));
        assertNotificationOf(new GlobalNotificationBuildSupportTest.NormalClass());
        // Tear-down
        GlobalNotificationBuildSupport.closeTestSuite(GlobalNotificationBuildSupportTest.NormalClass.class);
        new GlobalNotificationBuildSupportTest.NormalClass("dummy").toString();
        new GlobalNotificationBuildSupportTest.SubClass().hashCode();
        Mockito.verifyNoMoreInteractions(GlobalNotificationBuildSupportTest.mockCallback);// Creation should no longer have any affect

    }

    /**
     * Tests some ConcurrentHashMap functionality that
     * {@link GlobalNotificationBuildSupport#closePendingTestSuites(Callback)}
     * depends on.
     */
    @Test
    public void removeAllFromConcurrentHashMap() {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
        final Object value = new Object();
        map.put("foo", value);
        map.put("bar", value);
        Assert.assertEquals("Size of concurrent hashmap", 2, map.size());
        Collection<?> valueToRemove = Collections.singleton(value);
        map.values().removeAll(valueToRemove);
        Assert.assertEquals("Size of concurrent hashmap after removal of values", 0, map.size());
    }
}

