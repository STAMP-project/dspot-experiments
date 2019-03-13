/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util;


import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class MockNameImplTest extends TestBase {
    @Test
    public void shouldProvideTheNameForClass() throws Exception {
        // when
        String name = new MockNameImpl(null, MockNameImplTest.SomeClass.class).toString();
        // then
        Assert.assertEquals("someClass", name);
    }

    @Test
    public void shouldProvideTheNameForAnonymousClass() throws Exception {
        // given
        MockNameImplTest.SomeInterface anonymousInstance = new MockNameImplTest.SomeInterface() {};
        // when
        String name = new MockNameImpl(null, anonymousInstance.getClass()).toString();
        // then
        Assert.assertEquals("someInterface", name);
    }

    @Test
    public void shouldProvideTheGivenName() throws Exception {
        // when
        String name = new MockNameImpl("The Hulk", MockNameImplTest.SomeClass.class).toString();
        // then
        Assert.assertEquals("The Hulk", name);
    }

    private class SomeClass {}

    private class SomeInterface {}
}

