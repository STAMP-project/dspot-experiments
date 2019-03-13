/**
 * Copyright (c) 2015 Google, Inc.
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
package com.google.common.truth.extensions.re2j;


import com.google.common.truth.Truth;
import com.google.re2j.Pattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link Re2jSubjects}.
 */
@RunWith(JUnit4.class)
public class Re2jSubjectsTest {
    private static final String PATTERN_STR = "(?:hello )+world";

    private static final Pattern PATTERN = Pattern.compile(Re2jSubjectsTest.PATTERN_STR);

    @Test
    public void matches_string_succeeds() {
        Truth.assertAbout(Re2jSubjects.re2jString()).that("hello world").matches(Re2jSubjectsTest.PATTERN_STR);
    }

    @Test
    public void matches_pattern_succeeds() {
        Truth.assertAbout(Re2jSubjects.re2jString()).that("hello world").matches(Re2jSubjectsTest.PATTERN);
    }

    @Test
    public void doesNotMatch_string_succeeds() {
        Truth.assertAbout(Re2jSubjects.re2jString()).that("world").doesNotMatch(Re2jSubjectsTest.PATTERN_STR);
    }

    @Test
    public void doesNotMatch_pattern_succeeds() {
        Truth.assertAbout(Re2jSubjects.re2jString()).that("world").doesNotMatch(Re2jSubjectsTest.PATTERN);
    }

    @Test
    public void containsMatch_string_succeeds() {
        Truth.assertAbout(Re2jSubjects.re2jString()).that("this is a hello world").containsMatch(Re2jSubjectsTest.PATTERN_STR);
    }

    @Test
    public void containsMatch_pattern_succeeds() {
        Truth.assertAbout(Re2jSubjects.re2jString()).that("this is a hello world").containsMatch(Re2jSubjectsTest.PATTERN);
    }

    @Test
    public void doesNotContainMatch_string_succeeds() {
        Truth.assertAbout(Re2jSubjects.re2jString()).that("hello cruel world").doesNotContainMatch(Re2jSubjectsTest.PATTERN_STR);
    }

    @Test
    public void doesNotContainMatch_pattern_succeeds() {
        Truth.assertAbout(Re2jSubjects.re2jString()).that("hello cruel world").doesNotContainMatch(Re2jSubjectsTest.PATTERN);
    }
}

