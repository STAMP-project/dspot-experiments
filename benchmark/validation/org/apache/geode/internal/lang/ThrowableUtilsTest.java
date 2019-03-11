/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.lang;


import org.junit.Test;


/**
 * Unit tests for {@link ThrowableUtils}
 */
public class ThrowableUtilsTest {
    @Test
    public void getRootCauseOfNullShouldThrowNullPointerException() {
        Throwable thrown = catchThrowable(() -> getRootCause(null));
        assertThat(thrown).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    public void getRootCauseOfLeafExceptionShouldReturnSameInstance() {
        Throwable exception = new Exception();
        Throwable rootCause = ThrowableUtils.getRootCause(exception);
        assertThat(rootCause).isSameAs(exception);
    }

    @Test
    public void getRootCauseOfExceptionShouldReturnCause() {
        Throwable cause = new Exception();
        Throwable rootCause = ThrowableUtils.getRootCause(new Exception(cause));
        assertThat(rootCause).isSameAs(cause);
    }

    @Test
    public void getRootCauseOfExceptionTreeShouldReturnCause() {
        Throwable cause = new Exception();
        Throwable rootCause = ThrowableUtils.getRootCause(new Exception(new Exception(cause)));
        assertThat(rootCause).isSameAs(cause);
    }

    @Test
    public void getRootCauseOfErrorTreeShouldReturnCause() {
        Throwable cause = new Error();
        Throwable rootCause = ThrowableUtils.getRootCause(new Error(new Error(cause)));
        assertThat(rootCause).isSameAs(cause);
    }

    @Test
    public void hasCauseTypeOfNullClassShouldThrowNullPointerException() {
        Throwable thrown = catchThrowable(() -> hasCauseType(new Exception(), null));
        assertThat(thrown).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    public void hasCauseTypeOfNullThrowableShouldThrowNullPointerException() {
        Throwable thrown = catchThrowable(() -> hasCauseType(null, .class));
        assertThat(thrown).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    public void hasCauseTypeOfNonMatchingShouldReturnFalse() {
        assertThat(ThrowableUtils.hasCauseType(new ThrowableUtilsTest.OneException(), ThrowableUtilsTest.OtherException.class)).isFalse();
    }

    @Test
    public void hasCauseTypeOfSameClassShouldReturnTrue() {
        assertThat(ThrowableUtils.hasCauseType(new ThrowableUtilsTest.OneException(), ThrowableUtilsTest.OneException.class)).isTrue();
    }

    @Test
    public void hasCauseTypeOfSuperClassShouldReturnFalse() {
        assertThat(ThrowableUtils.hasCauseType(new ThrowableUtilsTest.OneException(), ThrowableUtilsTest.SubException.class)).isFalse();
    }

    @Test
    public void hasCauseTypeOfSubClassShouldReturnTrue() {
        assertThat(ThrowableUtils.hasCauseType(new ThrowableUtilsTest.SubException(), ThrowableUtilsTest.OneException.class)).isTrue();
    }

    @Test
    public void hasCauseTypeOfWrappedClassShouldReturnTrue() {
        assertThat(ThrowableUtils.hasCauseType(new ThrowableUtilsTest.OneException(new ThrowableUtilsTest.TwoException()), ThrowableUtilsTest.TwoException.class)).isTrue();
    }

    @Test
    public void hasCauseTypeOfWrappingClassShouldReturnTrue() {
        assertThat(ThrowableUtils.hasCauseType(new ThrowableUtilsTest.OneException(new ThrowableUtilsTest.TwoException()), ThrowableUtilsTest.OneException.class)).isTrue();
    }

    @Test
    public void hasCauseTypeOfNestedClassShouldReturnTrue() {
        assertThat(ThrowableUtils.hasCauseType(new ThrowableUtilsTest.OneException(new ThrowableUtilsTest.TwoException(new ThrowableUtilsTest.OtherException())), ThrowableUtilsTest.OtherException.class)).isTrue();
    }

    @Test
    public void hasCauseMessageForNullShouldThrowNullPointerException() {
        Throwable thrown = catchThrowable(() -> hasCauseMessage(null, "message"));
        assertThat(thrown).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    public void hasCauseMessageOfNullShouldThrowNullPointerException() {
        Throwable thrown = catchThrowable(() -> hasCauseMessage(new org.apache.geode.internal.lang.OneException(), null));
        assertThat(thrown).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    public void hasCauseMessageForNullMessageShouldThrowNullPointerException() {
        Throwable thrown = catchThrowable(() -> hasCauseMessage(new org.apache.geode.internal.lang.OneException(((String) (null))), null));
        assertThat(thrown).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    public void hasCauseMessageOfNonMatchingNullMessageShouldThrowNullPointerException() {
        Throwable thrown = catchThrowable(() -> hasCauseMessage(new org.apache.geode.internal.lang.OneException("message"), null));
        assertThat(thrown).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    public void hasCauseMessageOfEmptyMessageShouldReturnTrue() {
        assertThat(ThrowableUtils.hasCauseMessage(new ThrowableUtilsTest.OneException(""), "")).isTrue();
    }

    @Test
    public void hasCauseMessageOfMatchingMessageShouldReturnTrue() {
        assertThat(ThrowableUtils.hasCauseMessage(new ThrowableUtilsTest.OneException("message"), "message")).isTrue();
    }

    @Test
    public void hasCauseMessageOfNonMatchingMessageShouldReturnFalse() {
        assertThat(ThrowableUtils.hasCauseMessage(new ThrowableUtilsTest.OneException("non-matching"), "message")).isFalse();
    }

    @Test
    public void hasCauseMessageOfContainedMessageShouldReturnTrue() {
        assertThat(ThrowableUtils.hasCauseMessage(new ThrowableUtilsTest.OneException("this is the message"), "message")).isTrue();
    }

    @Test
    public void hasCauseMessageOfPartialMatchingMessageShouldReturnFalse() {
        assertThat(ThrowableUtils.hasCauseMessage(new ThrowableUtilsTest.OneException("message"), "this is the message")).isFalse();
    }

    private static class OneException extends Exception {
        public OneException() {
            // nothing
        }

        public OneException(String message) {
            super(message);
        }

        public OneException(Throwable cause) {
            super(cause);
        }

        public OneException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class SubException extends ThrowableUtilsTest.OneException {
        public SubException() {
            // nothing
        }

        public SubException(String message) {
            super(message);
        }

        public SubException(Throwable cause) {
            super(cause);
        }

        public SubException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class TwoException extends Exception {
        public TwoException() {
            // nothing
        }

        public TwoException(String message) {
            super(message);
        }

        public TwoException(Throwable cause) {
            super(cause);
        }

        public TwoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static class OtherException extends Exception {
        public OtherException() {
            // nothing
        }

        public OtherException(String message) {
            super(message);
        }

        public OtherException(Throwable cause) {
            super(cause);
        }

        public OtherException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

