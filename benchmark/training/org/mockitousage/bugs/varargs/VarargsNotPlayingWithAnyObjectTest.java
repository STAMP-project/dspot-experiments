/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.varargs;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


// see issue 62
public class VarargsNotPlayingWithAnyObjectTest extends TestBase {
    interface VarargMethod {
        Object run(String... args);
    }

    @Mock
    VarargsNotPlayingWithAnyObjectTest.VarargMethod mock;

    @Test
    public void shouldMatchAnyVararg() {
        mock.run("a", "b");
        Mockito.verify(mock).run(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(mock).run(((String) (ArgumentMatchers.anyObject())), ((String) (ArgumentMatchers.anyObject())));
        Mockito.verify(mock).run(((String[]) (ArgumentMatchers.anyVararg())));
        Mockito.verify(mock, Mockito.never()).run();
        Mockito.verify(mock, Mockito.never()).run(ArgumentMatchers.anyString(), ArgumentMatchers.eq("f"));
    }

    @Test
    public void shouldAllowUsingAnyObjectForVarArgs() {
        mock.run("a", "b");
        Mockito.verify(mock).run(((String[]) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void shouldStubUsingAnyVarargs() {
        Mockito.when(mock.run(((String[]) (ArgumentMatchers.anyVararg())))).thenReturn("foo");
        Assert.assertEquals("foo", mock.run("a", "b"));
    }
}

