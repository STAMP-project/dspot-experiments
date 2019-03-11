/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class GenericMatchersTest extends TestBase {
    private interface Foo {
        List<String> sort(List<String> otherList);

        String convertDate(Date date);
    }

    @Mock
    GenericMatchersTest.Foo sorter;

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCompile() {
        Mockito.when(sorter.convertDate(new Date())).thenReturn("one");
        Mockito.when(sorter.convertDate(((Date) (ArgumentMatchers.anyObject())))).thenReturn("two");
        // following requires warning suppression but allows setting anyList()
        Mockito.when(sorter.sort(ArgumentMatchers.<String>anyList())).thenReturn(null);
    }
}

