/**
 * Copyright (c) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.truth;


import Subject.Factory;
import com.google.common.truth.ExpectFailure.SimpleSubjectBuilderCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of {@link ExpectFailure}'s Java 8 support.
 */
@RunWith(JUnit4.class)
public final class ExpectFailure8Test {
    @Test
    public void testExpectFailure() throws Exception {
        AssertionError failure1 = ExpectFailure.expectFailure(( whenTesting) -> whenTesting.that(4).isEqualTo(5));
        ExpectFailure.assertThat(failure1).factValue("expected").isEqualTo("5");
        // verify multiple independent failures can be caught in the same test
        AssertionError failure2 = ExpectFailure.expectFailure(( whenTesting) -> whenTesting.that(5).isEqualTo(4));
        ExpectFailure.assertThat(failure2).factValue("expected").isEqualTo("4");
    }

    @Test
    public void testExpectFailureAbout() {
        AssertionError unused = ExpectFailure.expectFailureAbout(com.google.common.truth.STRINGS, ((SimpleSubjectBuilderCallback<StringSubject, String>) (( whenTesting) -> whenTesting.that("foo").contains("bar"))));
    }

    private static final Factory<StringSubject, String> STRINGS = StringSubject::new;
}

