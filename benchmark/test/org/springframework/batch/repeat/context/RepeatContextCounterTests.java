/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.repeat.context;


import junit.framework.TestCase;
import org.springframework.batch.repeat.RepeatContext;


public class RepeatContextCounterTests extends TestCase {
    RepeatContext parent = new RepeatContextSupport(null);

    RepeatContext context = new RepeatContextSupport(parent);

    public void testAttributeCreated() {
        new RepeatContextCounter(context, "FOO");
        TestCase.assertTrue(context.hasAttribute("FOO"));
    }

    public void testAttributeCreatedWithNullParent() {
        new RepeatContextCounter(parent, "FOO", true);
        TestCase.assertTrue(parent.hasAttribute("FOO"));
    }

    public void testVanillaIncrement() throws Exception {
        RepeatContextCounter counter = new RepeatContextCounter(context, "FOO");
        TestCase.assertEquals(0, counter.getCount());
        counter.increment(1);
        TestCase.assertEquals(1, counter.getCount());
        counter.increment(2);
        TestCase.assertEquals(3, counter.getCount());
    }

    public void testAttributeCreatedInParent() throws Exception {
        new RepeatContextCounter(context, "FOO", true);
        TestCase.assertFalse(context.hasAttribute("FOO"));
        TestCase.assertTrue(parent.hasAttribute("FOO"));
    }

    public void testParentIncrement() throws Exception {
        RepeatContextCounter counter = new RepeatContextCounter(context, "FOO", true);
        TestCase.assertEquals(0, counter.getCount());
        counter.increment(1);
        // now get new context with same parent
        counter = new RepeatContextCounter(new RepeatContextSupport(parent), "FOO", true);
        TestCase.assertEquals(1, counter.getCount());
        counter.increment(2);
        TestCase.assertEquals(3, counter.getCount());
    }
}

