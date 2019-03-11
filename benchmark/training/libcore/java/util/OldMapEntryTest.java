/**
 * Copyright (C) 2008 The Android Open Source Project
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
package libcore.java.util;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import junit.framework.TestCase;


public class OldMapEntryTest extends TestCase {
    Map.Entry me = null;

    HashMap hm = null;

    Iterator i = null;

    public void testGetKey() {
        TestCase.assertTrue(hm.containsKey(me.getKey()));
        hm.clear();
        try {
            me.getKey();
            // expected
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testGetValue() {
        TestCase.assertTrue(hm.containsValue(me.getValue()));
        hm.clear();
        try {
            me.getValue();
            // expected
        } catch (IllegalStateException e) {
            // expected
        }
    }

    class Mock_HashMap extends HashMap {
        @Override
        public Object put(Object key, Object val) {
            if (val == null)
                throw new NullPointerException();

            if ((val.getClass()) == (Double.class))
                throw new ClassCastException();

            if (((String) (val)).equals("Wrong element"))
                throw new IllegalArgumentException();

            throw new UnsupportedOperationException();
        }

        public Object fakePut(Object key, Object val) {
            return super.put(key, val);
        }
    }

    public void testSetValue() {
        OldMapEntryTest.Mock_HashMap mhm = new OldMapEntryTest.Mock_HashMap();
        mhm.fakePut(new Integer(1), "One");
        mhm.fakePut(new Integer(2), "Two");
        i = mhm.entrySet().iterator();
        me = ((Map.Entry) (i.next()));
        me.setValue("Wrong element");
        hm.clear();
        try {
            me.setValue("");
            // expected
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testEquals() {
        Map.Entry me1 = ((Map.Entry) (i.next()));
        TestCase.assertFalse(me.equals(me1));
        TestCase.assertFalse(me.equals(this));
        me1 = me;
        TestCase.assertTrue(me.equals(me1));
    }

    public void testHashCode() {
        Map.Entry me1 = ((Map.Entry) (i.next()));
        TestCase.assertTrue(((me.hashCode()) != (me1.hashCode())));
    }
}

