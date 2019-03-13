/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.varargs;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class VarargsAndAnyObjectPicksUpExtraInvocationsTest extends TestBase {
    public interface TableBuilder {
        void newRow(String trAttributes, String... cells);
    }

    @Mock
    VarargsAndAnyObjectPicksUpExtraInvocationsTest.TableBuilder table;

    @Test
    public void shouldVerifyCorrectlyWithAnyVarargs() {
        // when
        table.newRow("qux", "foo", "bar", "baz");
        table.newRow("abc", "def");
        // then
        Mockito.verify(table, Mockito.times(2)).newRow(ArgumentMatchers.anyString(), ((String[]) (ArgumentMatchers.anyVararg())));
    }

    @Test
    public void shouldVerifyCorrectlyNumberOfInvocationsUsingAnyVarargAndEqualArgument() {
        // when
        table.newRow("x", "foo", "bar", "baz");
        table.newRow("x", "def");
        // then
        Mockito.verify(table, Mockito.times(2)).newRow(ArgumentMatchers.eq("x"), ((String[]) (ArgumentMatchers.anyVararg())));
    }

    @Test
    public void shouldVerifyCorrectlyNumberOfInvocationsWithVarargs() {
        // when
        table.newRow("qux", "foo", "bar", "baz");
        table.newRow("abc", "def");
        // then
        Mockito.verify(table).newRow(ArgumentMatchers.anyString(), ArgumentMatchers.eq("foo"), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(table).newRow(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
}

