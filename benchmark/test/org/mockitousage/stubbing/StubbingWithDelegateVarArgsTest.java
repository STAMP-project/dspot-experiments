/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.stubbing;


import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;


public class StubbingWithDelegateVarArgsTest {
    public interface Foo {
        int bar(String baz, Object... args);
    }

    private static final class FooImpl implements StubbingWithDelegateVarArgsTest.Foo {
        @Override
        public int bar(String baz, Object... args) {
            return args != null ? args.length : -1;// simple return argument count

        }
    }

    @Test
    public void should_not_fail_when_calling_varargs_method() {
        StubbingWithDelegateVarArgsTest.Foo foo = Mockito.mock(StubbingWithDelegateVarArgsTest.Foo.class, Mockito.withSettings().defaultAnswer(AdditionalAnswers.delegatesTo(new StubbingWithDelegateVarArgsTest.FooImpl())));
        assertThat(foo.bar("baz", 12, "45", 67.8)).isEqualTo(3);
    }

    @Test
    public void should_not_fail_when_calling_varargs_method_without_arguments() {
        StubbingWithDelegateVarArgsTest.Foo foo = Mockito.mock(StubbingWithDelegateVarArgsTest.Foo.class, Mockito.withSettings().defaultAnswer(AdditionalAnswers.delegatesTo(new StubbingWithDelegateVarArgsTest.FooImpl())));
        assertThat(foo.bar("baz")).isEqualTo(0);
        assertThat(foo.bar("baz", new Object[0])).isEqualTo(0);
    }

    @Test
    public void should_not_fail_when_calling_varargs_method_with_null_argument() {
        StubbingWithDelegateVarArgsTest.Foo foo = Mockito.mock(StubbingWithDelegateVarArgsTest.Foo.class, Mockito.withSettings().defaultAnswer(AdditionalAnswers.delegatesTo(new StubbingWithDelegateVarArgsTest.FooImpl())));
        assertThat(foo.bar("baz", ((Object[]) (null)))).isEqualTo((-1));
    }
}

