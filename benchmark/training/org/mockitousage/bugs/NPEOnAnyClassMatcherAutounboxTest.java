/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


// see issue 221
public class NPEOnAnyClassMatcherAutounboxTest extends TestBase {
    interface Foo {
        void bar(long id);
    }

    @Test
    public void shouldNotThrowNPE() {
        NPEOnAnyClassMatcherAutounboxTest.Foo f = Mockito.mock(NPEOnAnyClassMatcherAutounboxTest.Foo.class);
        f.bar(1);
        Mockito.verify(f).bar(ArgumentMatchers.any(Long.class));
    }
}

