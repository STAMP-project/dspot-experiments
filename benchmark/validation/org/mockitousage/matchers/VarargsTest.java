/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockitousage.IMethods;


public class VarargsTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Captor
    private ArgumentCaptor<String> captor;

    @Mock
    private IMethods mock;

    private static final Condition<Object> NULL = new Condition<Object>() {
        @Override
        public boolean matches(Object value) {
            return value == null;
        }
    };

    @Test
    public void shouldMatchVarArgs_noArgs() {
        mock.varargs();
        Mockito.verify(mock).varargs();
    }

    @Test
    public void shouldMatchVarArgs_oneNullArg_eqNull() {
        Object arg = null;
        mock.varargs(arg);
        Mockito.verify(mock).varargs(ArgumentMatchers.eq(null));
    }

    @Test
    public void shouldMatchVarArgs_oneNullArg_isNull() {
        Object arg = null;
        mock.varargs(arg);
        Mockito.verify(mock).varargs(ArgumentMatchers.isNull());
    }

    @Test
    public void shouldMatchVarArgs_nullArrayArg() {
        Object[] argArray = null;
        mock.varargs(argArray);
        Mockito.verify(mock).varargs(ArgumentMatchers.isNull());
    }

    @Test
    public void shouldnotMatchVarArgs_twoArgsOneMatcher() {
        mock.varargs("1", "1");
        exception.expectMessage("Argument(s) are different");
        Mockito.verify(mock).varargs(ArgumentMatchers.eq("1"));
    }

    @Test
    public void shouldMatchVarArgs_emptyVarArgsOneAnyMatcher() {
        mock.varargs();
        Mockito.verify(mock).varargs(((String[]) (ArgumentMatchers.any())));// any() -> VarargMatcher

    }

    @Test
    public void shouldMatchVarArgs_oneArgsOneAnyMatcher() {
        mock.varargs(1);
        Mockito.verify(mock).varargs(ArgumentMatchers.any());// any() -> VarargMatcher

    }

    @Test
    public void shouldMatchVarArgs_twoArgsOneAnyMatcher() {
        mock.varargs(1, 2);
        Mockito.verify(mock).varargs(ArgumentMatchers.any());// any() -> VarargMatcher

    }

    @Test
    public void shouldMatchVarArgs_twoArgsTwoAnyMatcher() {
        mock.varargs(1, 2);
        Mockito.verify(mock).varargs(ArgumentMatchers.any(), ArgumentMatchers.any());// any() -> VarargMatcher

    }

    @Test
    public void shouldMatchVarArgs_twoArgsThreeAnyMatcher() {
        mock.varargs(1, 2);
        exception.expectMessage("Argument(s) are different");
        Mockito.verify(mock).varargs(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());// any() -> VarargMatcher

    }

    @Test
    public void shouldMatchVarArgs_oneNullArgument() {
        mock.varargs("1", null);
        Mockito.verify(mock).varargs(ArgumentMatchers.eq("1"), ((String) (ArgumentMatchers.isNull())));
    }

    @Test
    public void shouldMatchVarArgs_onebyte() {
        mock.varargsbyte(((byte) (1)));
        Mockito.verify(mock).varargsbyte(ArgumentMatchers.eq(((byte) (1))));
    }

    @Test
    public void shouldMatchVarArgs_nullByteArray() {
        mock.varargsbyte(null);
        Mockito.verify(mock).varargsbyte(((byte[]) (ArgumentMatchers.isNull())));
    }

    @Test
    public void shouldMatchVarArgs_emptyByteArray() {
        mock.varargsbyte();
        Mockito.verify(mock).varargsbyte();
    }

    @Test
    public void shouldMatchVarArgs_oneArgIsNotNull() {
        mock.varargsbyte(((byte) (1)));
        Mockito.verify(mock).varargsbyte(((byte[]) (ArgumentMatchers.isNotNull())));
    }

    @Test
    public void shouldCaptureVarArgs_noArgs() {
        mock.varargs();
        Mockito.verify(mock).varargs(captor.capture());
        VarargsTest.assertThat(captor).isEmpty();
    }

    @Test
    public void shouldCaptureVarArgs_oneNullArg_eqNull() {
        String arg = null;
        mock.varargs(arg);
        Mockito.verify(mock).varargs(captor.capture());
        VarargsTest.assertThat(captor).areExactly(1, VarargsTest.NULL);
    }

    /**
     * Relates to Github issue #583 "ArgumentCaptor: NPE when an null array is
     * passed to a varargs method"
     */
    @Test
    public void shouldCaptureVarArgs_nullArrayArg() {
        String[] argArray = null;
        mock.varargs(argArray);
        Mockito.verify(mock).varargs(captor.capture());
        VarargsTest.assertThat(captor).areExactly(1, VarargsTest.NULL);
    }

    @Test
    public void shouldCaptureVarArgs_twoArgsOneCapture() {
        mock.varargs("1", "2");
        Mockito.verify(mock).varargs(captor.capture());
        VarargsTest.assertThat(captor).contains("1", "2");
    }

    @Test
    public void shouldCaptureVarArgs_twoArgsTwoCaptures() {
        mock.varargs("1", "2");
        Mockito.verify(mock).varargs(captor.capture(), captor.capture());
        VarargsTest.assertThat(captor).contains("1", "2");
    }

    @Test
    public void shouldCaptureVarArgs_oneNullArgument() {
        mock.varargs("1", null);
        Mockito.verify(mock).varargs(captor.capture());
        VarargsTest.assertThat(captor).contains("1", ((String) (null)));
    }

    @Test
    public void shouldCaptureVarArgs_oneNullArgument2() {
        mock.varargs("1", null);
        Mockito.verify(mock).varargs(captor.capture(), captor.capture());
        VarargsTest.assertThat(captor).contains("1", ((String) (null)));
    }

    @Test
    public void shouldNotCaptureVarArgs_3args2captures() {
        mock.varargs("1", "2", "3");
        exception.expect(ArgumentsAreDifferent.class);
        Mockito.verify(mock).varargs(captor.capture(), captor.capture());
    }

    @Test
    public void shouldCaptureVarArgs_3argsCaptorMatcherMix() {
        mock.varargs("1", "2", "3");
        Mockito.verify(mock).varargs(captor.capture(), ArgumentMatchers.eq("2"), captor.capture());
        VarargsTest.assertThat(captor).containsExactly("1", "3");
    }

    @Test
    public void shouldNotCaptureVarArgs_3argsCaptorMatcherMix() {
        mock.varargs("1", "2", "3");
        try {
            Mockito.verify(mock).varargs(captor.capture(), ArgumentMatchers.eq("X"), captor.capture());
            Assert.fail("The verification must fail, cause the second arg was not 'X' as expected!");
        } catch (ArgumentsAreDifferent expected) {
        }
        VarargsTest.assertThat(captor).isEmpty();
    }

    @Test
    public void shouldNotCaptureVarArgs_1args2captures() {
        mock.varargs("1");
        exception.expect(ArgumentsAreDifferent.class);
        Mockito.verify(mock).varargs(captor.capture(), captor.capture());
    }

    @Test
    public void shouldNotMatchRegualrAndVaraArgs() {
        mock.varargsString(1, "a", "b");
        exception.expect(ArgumentsAreDifferent.class);
        Mockito.verify(mock).varargsString(1);
    }

    @Test
    public void shouldNotMatchVaraArgs() {
        Mockito.when(mock.varargsObject(1, "a", "b")).thenReturn("OK");
        Assertions.assertThat(mock.varargsObject(1)).isNull();
    }
}

