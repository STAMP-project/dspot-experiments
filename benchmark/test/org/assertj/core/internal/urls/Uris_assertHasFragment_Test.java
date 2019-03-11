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
package org.assertj.core.internal.urls;


import java.net.URI;
import java.net.URISyntaxException;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.uri.ShouldHaveFragment;
import org.assertj.core.internal.UrisBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Uris#assertHasFragment(org.assertj.core.api.AssertionInfo, java.net.URI, String)}  </code>
 * .
 *
 * @author Alexander Bischof
 */
public class Uris_assertHasFragment_Test extends UrisBaseTest {
    @Test
    public void should_pass_if_actual_uri_has_the_given_fragment() throws URISyntaxException {
        uris.assertHasFragment(info, new URI("http://www.helloworld.org/pages/index.html#print"), "print");
        uris.assertHasFragment(info, new URI("http://www.helloworld.org/index.html#print"), "print");
    }

    @Test
    public void should_pass_if_actual_uri_has_no_fragment_and_given_is_null() throws URISyntaxException {
        uris.assertHasFragment(info, new URI("http://www.helloworld.org/index.html"), null);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> uris.assertHasFragment(info, null, "http://www.helloworld.org/index.html#print")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_URI_has_not_the_expected_fragment() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://example.com/index.html#print");
        String expectedFragment = "foo";
        try {
            uris.assertHasFragment(info, uri, expectedFragment);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveFragment.shouldHaveFragment(uri, expectedFragment));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_URI_has_no_fragment_and_expected_fragment_is_not_null() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://example.com/index.html");
        String expectedFragment = "print";
        try {
            uris.assertHasFragment(info, uri, expectedFragment);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveFragment.shouldHaveFragment(uri, expectedFragment));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_URI_has_fragment_and_expected_fragment_is_null() throws URISyntaxException {
        AssertionInfo info = TestData.someInfo();
        URI uri = new URI("http://example.com/index.html#print");
        String expectedFragment = null;
        try {
            uris.assertHasFragment(info, uri, expectedFragment);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveFragment.shouldHaveFragment(uri, expectedFragment));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_actual_uri_has_no_fragment() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> uris.assertHasFragment(info, new URI("http://www.helloworld.org/index.html"), "print"));
    }
}

