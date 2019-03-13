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
package libcore.java.lang;


import junit.framework.TestCase;

import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.NEW;
import static java.lang.Thread.State.RUNNABLE;
import static java.lang.Thread.State.TERMINATED;
import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;
import static java.lang.Thread.State.valueOf;
import static java.lang.Thread.State.values;


public class OldThreadStateTest extends TestCase {
    Thread.State[] exStates = new Thread.State[]{ NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED };

    public void test_valueOfLString() {
        String[] spNames = new String[]{ "NEW", "RUNNABLE", "BLOCKED", "WAITING", "TIMED_WAITING", "TERMINATED" };
        for (int i = 0; i < (exStates.length); i++) {
            TestCase.assertEquals(exStates[i], valueOf(spNames[i]));
        }
        String[] illegalNames = new String[]{ "New", "new", "", "NAME", "TIME" };
        for (String s : illegalNames) {
            try {
                valueOf(s);
                TestCase.fail(("IllegalArgumentException was not thrown for string: " + s));
            } catch (IllegalArgumentException iae) {
                // expected
            }
        }
    }

    public void test_values() {
        Thread.State[] thStates = values();
        TestCase.assertEquals(exStates.length, thStates.length);
        for (Thread.State ts : thStates) {
            TestCase.assertTrue(isContain(ts));
        }
    }
}

