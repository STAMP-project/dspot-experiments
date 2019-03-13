/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.quartz;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;


public class TriggerComparatorTest extends TestCase {
    public void testTriggerSort() {
        // build trigger in expected sort order
        Trigger t1 = TriggerBuilder.newTrigger().withIdentity("a").build();
        Trigger t2 = TriggerBuilder.newTrigger().withIdentity("b").build();
        Trigger t3 = TriggerBuilder.newTrigger().withIdentity("c").build();
        Trigger t4 = TriggerBuilder.newTrigger().withIdentity("a", "a").build();
        Trigger t5 = TriggerBuilder.newTrigger().withIdentity("a", "b").build();
        Trigger t6 = TriggerBuilder.newTrigger().withIdentity("a", "c").build();
        List<Trigger> ts = new LinkedList<Trigger>();
        // add triggers to list in somewhat randomized order
        ts.add(t5);
        ts.add(t6);
        ts.add(t4);
        ts.add(t3);
        ts.add(t1);
        ts.add(t2);
        // sort the list
        Collections.sort(ts);
        // check the order of the list
        TestCase.assertEquals(t1, ts.get(0));
        TestCase.assertEquals(t2, ts.get(1));
        TestCase.assertEquals(t3, ts.get(2));
        TestCase.assertEquals(t4, ts.get(3));
        TestCase.assertEquals(t5, ts.get(4));
        TestCase.assertEquals(t6, ts.get(5));
    }

    public void testTriggerTimeSort() {
        // build trigger in expected sort order
        Trigger t1 = TriggerBuilder.newTrigger().withIdentity("a").startAt(DateBuilder.futureDate(1, IntervalUnit.MINUTE)).build();
        computeFirstFireTime(null);
        Trigger t2 = TriggerBuilder.newTrigger().withIdentity("b").startAt(DateBuilder.futureDate(2, IntervalUnit.MINUTE)).build();
        computeFirstFireTime(null);
        Trigger t3 = TriggerBuilder.newTrigger().withIdentity("c").startAt(DateBuilder.futureDate(3, IntervalUnit.MINUTE)).build();
        computeFirstFireTime(null);
        Trigger t4 = TriggerBuilder.newTrigger().withIdentity("d").startAt(DateBuilder.futureDate(5, IntervalUnit.MINUTE)).withPriority(7).build();
        computeFirstFireTime(null);
        Trigger t5 = TriggerBuilder.newTrigger().withIdentity("e").startAt(DateBuilder.futureDate(5, IntervalUnit.MINUTE)).build();
        computeFirstFireTime(null);
        Trigger t6 = TriggerBuilder.newTrigger().withIdentity("g").startAt(DateBuilder.futureDate(5, IntervalUnit.MINUTE)).build();
        computeFirstFireTime(null);
        Trigger t7 = TriggerBuilder.newTrigger().withIdentity("h").startAt(DateBuilder.futureDate(5, IntervalUnit.MINUTE)).withPriority(2).build();
        computeFirstFireTime(null);
        Trigger t8 = TriggerBuilder.newTrigger().withIdentity("i").startAt(DateBuilder.futureDate(6, IntervalUnit.MINUTE)).build();
        computeFirstFireTime(null);
        Trigger t9 = TriggerBuilder.newTrigger().withIdentity("j").startAt(DateBuilder.futureDate(7, IntervalUnit.MINUTE)).build();
        computeFirstFireTime(null);
        List<Trigger> ts = new LinkedList<Trigger>();
        // add triggers to list in somewhat randomized order
        ts.add(t5);
        ts.add(t9);
        ts.add(t6);
        ts.add(t8);
        ts.add(t4);
        ts.add(t3);
        ts.add(t1);
        ts.add(t7);
        ts.add(t2);
        // sort the list
        Collections.sort(ts);
        // check the order of the list
        TestCase.assertEquals(t1, ts.get(0));
        TestCase.assertEquals(t2, ts.get(1));
        TestCase.assertEquals(t3, ts.get(2));
        TestCase.assertEquals(t4, ts.get(3));
        TestCase.assertEquals(t5, ts.get(4));
        TestCase.assertEquals(t6, ts.get(5));
        TestCase.assertEquals(t7, ts.get(6));
        TestCase.assertEquals(t8, ts.get(7));
        TestCase.assertEquals(t9, ts.get(8));
    }
}

