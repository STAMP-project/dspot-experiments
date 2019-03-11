/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util.collections;


import org.junit.Assert;
import org.junit.Test;


public class IdentitySetTest {
    IdentitySet set = new IdentitySet();

    @Test
    public void shouldWork() throws Exception {
        // when
        Object o = new Object();
        set.add(o);
        // then
        Assert.assertTrue(set.contains(o));
        Assert.assertFalse(set.contains(new Object()));
    }

    class Fake {
        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }

    @Test
    public void shouldWorkEvenIfEqualsTheSame() throws Exception {
        // given
        Assert.assertEquals(new IdentitySetTest.Fake(), new IdentitySetTest.Fake());
        IdentitySetTest.Fake fake = new IdentitySetTest.Fake();
        // when
        set.add(fake);
        // then
        Assert.assertTrue(set.contains(fake));
        Assert.assertFalse(set.contains(new IdentitySetTest.Fake()));
    }
}

