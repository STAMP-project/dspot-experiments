/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.reteoo;


import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DisconnectedWorkingMemoryEntryPoint;
import org.junit.Assert;
import org.junit.Test;


public class FactHandleTest {
    /* Class under test for void FactHandleImpl(long) */
    @Test
    public void testFactHandleImpllong() {
        final DefaultFactHandle f0 = new DefaultFactHandle(134, "cheese");
        Assert.assertEquals(134, f0.getId());
        Assert.assertEquals(134, f0.getRecency());
    }

    /* Class under test for void FactHandleImpl(long, long) */
    @Test
    public void testFactHandleImpllonglong() {
        final DefaultFactHandle f0 = new DefaultFactHandle(134, "cheese", 678, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        Assert.assertEquals(134, f0.getId());
        Assert.assertEquals(678, f0.getRecency());
    }

    /* Class under test for boolean equals(Object) */
    @Test
    public void testEqualsObject() {
        final DefaultFactHandle f0 = new DefaultFactHandle(134, "cheese");
        final DefaultFactHandle f1 = new DefaultFactHandle(96, "cheese");
        final DefaultFactHandle f3 = new DefaultFactHandle(96, "cheese");
        Assert.assertFalse("f0 should not equal f1", f0.equals(f1));
        Assert.assertEquals(f1, f3);
        Assert.assertNotSame(f1, f3);
    }

    @Test
    public void testHashCode() {
        final DefaultFactHandle f0 = new DefaultFactHandle(234, "cheese");
        Assert.assertEquals("cheese".hashCode(), f0.getObjectHashCode());
        Assert.assertEquals(234, f0.hashCode());
    }

    @Test
    public void testInvalidate() {
        final DefaultFactHandle f0 = new DefaultFactHandle(134, "cheese");
        Assert.assertEquals(134, f0.getId());
        f0.invalidate();
        // invalidate no longer sets the id to -1
        Assert.assertEquals(134, f0.getId());
    }
}

