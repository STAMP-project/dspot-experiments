/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.varargs;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class VarargsErrorWhenCallingRealMethodTest extends TestBase {
    class Foo {
        int blah(String a, String b, Object... c) {
            return 1;
        }
    }

    @Test
    public void shouldNotThrowAnyException() throws Exception {
        VarargsErrorWhenCallingRealMethodTest.Foo foo = Mockito.mock(VarargsErrorWhenCallingRealMethodTest.Foo.class);
        Mockito.when(foo.blah(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenCallRealMethod();
        Assert.assertEquals(1, foo.blah("foo", "bar"));
    }
}

