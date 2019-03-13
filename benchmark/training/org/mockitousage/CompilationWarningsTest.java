/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage;


import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class CompilationWarningsTest {
    @Test
    public void no_warnings_for_most_common_api() throws Exception {
        Mockito.doReturn(null).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doReturn("a", 12).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doReturn(1000).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doThrow(new NullPointerException()).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doThrow(new NullPointerException(), new IllegalArgumentException()).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doThrow(NullPointerException.class).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doAnswer(CompilationWarningsTest.ignore()).doReturn(null).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doAnswer(CompilationWarningsTest.ignore()).doReturn("a", 12).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doAnswer(CompilationWarningsTest.ignore()).doReturn(1000).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doAnswer(CompilationWarningsTest.ignore()).doThrow(new NullPointerException()).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doAnswer(CompilationWarningsTest.ignore()).doThrow(new NullPointerException(), new IllegalArgumentException()).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doAnswer(CompilationWarningsTest.ignore()).doThrow(NullPointerException.class).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).thenReturn(null);
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).thenReturn("a", 12L);
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).thenReturn(1000);
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).thenThrow(new NullPointerException());
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).thenThrow(new NullPointerException(), new IllegalArgumentException());
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).thenThrow(NullPointerException.class);
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).then(CompilationWarningsTest.ignore()).thenReturn(null);
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).then(CompilationWarningsTest.ignore()).thenReturn("a", 12L);
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).then(CompilationWarningsTest.ignore()).thenReturn(1000);
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).then(CompilationWarningsTest.ignore()).thenThrow(new NullPointerException());
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).then(CompilationWarningsTest.ignore()).thenThrow(new NullPointerException(), new IllegalArgumentException());
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).then(CompilationWarningsTest.ignore()).thenThrow(NullPointerException.class);
        BDDMockito.willReturn(null).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willReturn("a", 12).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willReturn(1000).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willThrow(new NullPointerException()).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willThrow(new NullPointerException(), new IllegalArgumentException()).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willThrow(NullPointerException.class).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willAnswer(CompilationWarningsTest.ignore()).willReturn(null).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willAnswer(CompilationWarningsTest.ignore()).willReturn("a", 12).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willAnswer(CompilationWarningsTest.ignore()).willReturn(1000).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willAnswer(CompilationWarningsTest.ignore()).willThrow(new NullPointerException()).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willAnswer(CompilationWarningsTest.ignore()).willThrow(new NullPointerException(), new IllegalArgumentException()).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willAnswer(CompilationWarningsTest.ignore()).willThrow(NullPointerException.class).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).willReturn(null);
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).willReturn("a", 12L);
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).willReturn(1000);
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).willThrow(new NullPointerException());
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).willThrow(new NullPointerException(), new IllegalArgumentException());
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).willThrow(NullPointerException.class);
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).will(CompilationWarningsTest.ignore()).willReturn(null);
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).will(CompilationWarningsTest.ignore()).willReturn("a", 12L);
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).will(CompilationWarningsTest.ignore()).willReturn(1000);
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).will(CompilationWarningsTest.ignore()).willThrow(new NullPointerException());
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).will(CompilationWarningsTest.ignore()).willThrow(new NullPointerException(), new IllegalArgumentException());
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).will(CompilationWarningsTest.ignore()).willThrow(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void heap_pollution_JDK7plus_warning_avoided_BUT_now_unchecked_generic_array_creation_warnings_ON_JDK5plus_environment() throws Exception {
        Mockito.doThrow(NullPointerException.class, IllegalArgumentException.class).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).thenThrow(NullPointerException.class, IllegalArgumentException.class);
        Mockito.doAnswer(CompilationWarningsTest.ignore()).doThrow(NullPointerException.class, IllegalArgumentException.class).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.willThrow(NullPointerException.class, IllegalArgumentException.class).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).willThrow(NullPointerException.class, IllegalArgumentException.class);
        BDDMockito.willAnswer(CompilationWarningsTest.ignore()).willThrow(NullPointerException.class, IllegalArgumentException.class).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
    }

    @Test
    public void unchecked_confusing_null_argument_warnings() throws Exception {
        Mockito.doReturn(null, ((Object[]) (null))).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.doAnswer(CompilationWarningsTest.ignore()).doReturn(null, ((Object[]) (null))).when(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).thenReturn(null, ((Object[]) (null)));
        Mockito.when(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).then(CompilationWarningsTest.ignore()).thenReturn(null, ((Object[]) (null)));
        BDDMockito.willReturn(null, ((Object[]) (null))).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).willReturn(null, ((Object[]) (null)));
        BDDMockito.willAnswer(CompilationWarningsTest.ignore()).willReturn(null, ((Object[]) (null))).given(Mockito.mock(IMethods.class)).objectReturningMethodNoArgs();
        BDDMockito.given(Mockito.mock(IMethods.class).objectReturningMethodNoArgs()).will(CompilationWarningsTest.ignore()).willReturn(null, ((Object[]) (null)));
    }
}

