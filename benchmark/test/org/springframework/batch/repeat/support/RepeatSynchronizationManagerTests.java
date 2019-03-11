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
package org.springframework.batch.repeat.support;


import junit.framework.TestCase;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.context.RepeatContextSupport;


public class RepeatSynchronizationManagerTests extends TestCase {
    private RepeatContext context = new RepeatContextSupport(null);

    public void testGetContext() {
        RepeatSynchronizationManager.register(context);
        TestCase.assertEquals(context, RepeatSynchronizationManager.getContext());
    }

    public void testSetSessionCompleteOnly() {
        TestCase.assertNull(RepeatSynchronizationManager.getContext());
        RepeatSynchronizationManager.register(context);
        TestCase.assertFalse(RepeatSynchronizationManager.getContext().isCompleteOnly());
        RepeatSynchronizationManager.setCompleteOnly();
        TestCase.assertTrue(RepeatSynchronizationManager.getContext().isCompleteOnly());
    }

    public void testSetSessionCompleteOnlyWithParent() {
        TestCase.assertNull(RepeatSynchronizationManager.getContext());
        RepeatContext child = new RepeatContextSupport(context);
        RepeatSynchronizationManager.register(child);
        TestCase.assertFalse(child.isCompleteOnly());
        RepeatSynchronizationManager.setAncestorsCompleteOnly();
        TestCase.assertTrue(child.isCompleteOnly());
        TestCase.assertTrue(context.isCompleteOnly());
    }

    public void testClear() {
        RepeatSynchronizationManager.register(context);
        TestCase.assertEquals(context, RepeatSynchronizationManager.getContext());
        RepeatSynchronizationManager.clear();
        TestCase.assertEquals(null, RepeatSynchronizationManager.getContext());
    }
}

