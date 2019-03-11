/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.internal.inputstreams;


import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveDigest;
import org.assertj.core.internal.DigestDiff;
import org.assertj.core.internal.Digests;
import org.assertj.core.internal.InputStreamsBaseTest;
import org.assertj.core.internal.InputStreamsException;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link InputStreams#assertHasDigest(AssertionInfo, InputStream, String, byte[])}</code>
 *
 * @author Valeriy Vyrva
 */
public class InputStreams_assertHasDigest_AlgorithmBytes_Test extends InputStreamsBaseTest {
    private static final String MD5 = "MD5";

    private final byte[] expected = new byte[0];

    private static final String RED_PNG_DIGEST = "3AC1AFA2A89B7E4F1866502877BF1DC5";

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> inputStreams.assertHasDigest(InputStreamsBaseTest.INFO, null, MD5, expected)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_error_if_digest_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> inputStreams.assertHasDigest(InputStreamsBaseTest.INFO, null, ((MessageDigest) (null)), expected)).withMessage("The message digest algorithm should not be null");
    }

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> inputStreams.assertHasDigest(InputStreamsBaseTest.INFO, null, MD5, ((byte[]) (null)))).withMessage("The binary representation of digest to compare to should not be null");
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        // GIVEN
        IOException cause = new IOException();
        InputStreamsBaseTest.actual = Mockito.mock(InputStream.class);
        BDDMockito.given(InputStreamsBaseTest.actual.read(ArgumentMatchers.any())).willThrow(cause);
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> inputStreams.assertHasDigest(InputStreamsBaseTest.INFO, InputStreamsBaseTest.actual, MD5, expected));
        // THEN
        Assertions.assertThat(error).isInstanceOf(InputStreamsException.class).hasCause(cause);
    }

    @Test
    public void should_fail_if_actual_does_not_have_expected_digest() throws NoSuchAlgorithmException {
        // GIVEN
        InputStreamsBaseTest.actual = getClass().getResourceAsStream("/red.png");
        // WHEN
        Assertions.catchThrowable(() -> inputStreams.assertHasDigest(InputStreamsBaseTest.INFO, InputStreamsBaseTest.actual, MD5, expected));
        // THEN
        Mockito.verify(failures).failure(InputStreamsBaseTest.INFO, ShouldHaveDigest.shouldHaveDigest(InputStreamsBaseTest.actual, new DigestDiff(InputStreams_assertHasDigest_AlgorithmBytes_Test.RED_PNG_DIGEST, "", MessageDigest.getInstance(InputStreams_assertHasDigest_AlgorithmBytes_Test.MD5))));
    }

    @Test
    public void should_pass_if_actual_has_expected_digest() {
        // GIVEN
        InputStreamsBaseTest.actual = getClass().getResourceAsStream("/red.png");
        // THEN
        inputStreams.assertHasDigest(InputStreamsBaseTest.INFO, InputStreamsBaseTest.actual, InputStreams_assertHasDigest_AlgorithmBytes_Test.MD5, Digests.fromHex(InputStreams_assertHasDigest_AlgorithmBytes_Test.RED_PNG_DIGEST));
    }
}

