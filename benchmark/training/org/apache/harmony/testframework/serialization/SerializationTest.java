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
/**
 *
 *
 * @author Alexey V. Varlamov
 * @version $Revision$
 */
package org.apache.harmony.testframework.serialization;


import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import junit.framework.TestCase;


/**
 * Framework for serialization testing. Subclasses only need to override
 * getData() method and, optionally, assertDeserialized() method. The first one
 * returns array of objects to be de/serialized in tests, and the second
 * compares reference and deserialized objects (needed only if tested objects do
 * not provide specific method equals()). <br>
 * There are two modes of test run: <b>reference generation mode </b> and
 * <b>testing mode </b>. The actual mode is selected via
 * <b>&quot;test.mode&quot; </b> system property. The <b>testing mode </b> is
 * the default mode. <br>
 * To turn on the <b>reference generation mode </b>, the test.mode property
 * should be set to value &quot;serial.reference&quot;. In this mode, no testing
 * is performed but golden files are produced, which contain reference
 * serialized objects. This mode should be run on a pure
 * Implementation classes, which are targeted for compartibility. <br>
 * The location of golden files (in both modes) is controlled via
 * <b>&quot;RESOURCE_DIR&quot; </b> system property.
 */
public abstract class SerializationTest extends TestCase {
    /**
     * Key to a system property defining root location of golden files.
     */
    public static final String GOLDEN_PATH = "RESOURCE_DIR";

    private static final String outputPath = System.getProperty(SerializationTest.GOLDEN_PATH, "src/test/resources/serialization");

    /**
     * Tests that data objects can be serialized and deserialized without
     * exceptions, and that deserialization really produces deeply cloned
     * objects.
     */
    public void testSelf() throws Throwable {
        if ((this) instanceof SerializationTest.SerializableAssert) {
            SerializationTest.verifySelf(getData(), ((SerializationTest.SerializableAssert) (this)));
        } else {
            SerializationTest.verifySelf(getData());
        }
    }

    /**
     * Tests that data objects can be deserialized from golden files, to verify
     * compatibility with Reference Implementation.
     */
    public void testGolden() throws Throwable {
        SerializationTest.verifyGolden(this, getData());
    }

    /**
     * Interface to compare (de)serialized objects
     *
     * Should be implemented if a class under test does not provide specific
     * equals() method and it's instances should to be compared manually.
     */
    public interface SerializableAssert {
        /**
         * Compares deserialized and reference objects.
         *
         * @param initial
         * 		- initial object used for creating serialized form
         * @param deserialized
         * 		- deserialized object
         */
        void assertDeserialized(Serializable initial, Serializable deserialized);
    }

    // default comparator for a class that has equals(Object) method
    private static final SerializationTest.SerializableAssert DEFAULT_COMPARATOR = new SerializationTest.SerializableAssert() {
        public void assertDeserialized(Serializable initial, Serializable deserialized) {
            TestCase.assertEquals(initial, deserialized);
        }
    };

    /**
     * Comparator for verifying that deserialized object is the same as initial.
     */
    public static final SerializationTest.SerializableAssert SAME_COMPARATOR = new SerializationTest.SerializableAssert() {
        public void assertDeserialized(Serializable initial, Serializable deserialized) {
            TestCase.assertSame(initial, deserialized);
        }
    };

    /**
     * Comparator for Throwable objects
     */
    public static final SerializationTest.SerializableAssert THROWABLE_COMPARATOR = new SerializationTest.SerializableAssert() {
        public void assertDeserialized(Serializable initial, Serializable deserialized) {
            Throwable initThr = ((Throwable) (initial));
            Throwable dserThr = ((Throwable) (deserialized));
            // verify class
            TestCase.assertEquals(initThr.getClass(), dserThr.getClass());
            // verify message
            TestCase.assertEquals(initThr.getMessage(), dserThr.getMessage());
            // verify cause
            if ((initThr.getCause()) == null) {
                TestCase.assertNull(dserThr.getCause());
            } else {
                TestCase.assertNotNull(dserThr.getCause());
                SerializationTest.THROWABLE_COMPARATOR.assertDeserialized(initThr.getCause(), dserThr.getCause());
            }
        }
    };

    /**
     * Comparator for PermissionCollection objects
     */
    public static final SerializationTest.SerializableAssert PERMISSION_COLLECTION_COMPARATOR = new SerializationTest.SerializableAssert() {
        public void assertDeserialized(Serializable initial, Serializable deserialized) {
            PermissionCollection initPC = ((PermissionCollection) (initial));
            PermissionCollection dserPC = ((PermissionCollection) (deserialized));
            // verify class
            TestCase.assertEquals(initPC.getClass(), dserPC.getClass());
            // verify 'readOnly' field
            TestCase.assertEquals(initPC.isReadOnly(), dserPC.isReadOnly());
            // verify collection of permissions
            Collection<Permission> refCollection = new HashSet<Permission>(Collections.list(initPC.elements()));
            Collection<Permission> tstCollection = new HashSet<Permission>(Collections.list(dserPC.elements()));
            TestCase.assertEquals(refCollection, tstCollection);
        }
    };
}

