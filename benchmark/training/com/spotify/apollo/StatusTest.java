/**
 * -\-\-
 * Spotify Apollo API Interfaces
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo;


import Status.ACCEPTED;
import Status.OK;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class StatusTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldAllowSettingReasonPhrase() throws Exception {
        Assert.assertThat(OK.withReasonPhrase("this is toe-dally OK").reasonPhrase(), CoreMatchers.equalTo("this is toe-dally OK"));
    }

    @Test
    public void shouldReplaceNewlineInReasonPhrase() throws Exception {
        StatusType statusType = ACCEPTED.withReasonPhrase("with\nnewline");
        Assert.assertThat(statusType.reasonPhrase(), CoreMatchers.is("with newline"));
    }

    @Test
    public void shouldReplaceCRInReasonPhrase() throws Exception {
        StatusType statusType = ACCEPTED.withReasonPhrase("with\rcarriage return");
        Assert.assertThat(statusType.reasonPhrase(), CoreMatchers.is("with carriage return"));
    }

    @Test
    public void shouldReplaceControlCharsInReasonPhrase() throws Exception {
        StatusType statusType = ACCEPTED.withReasonPhrase("with control\u0007");
        Assert.assertThat(statusType.reasonPhrase(), CoreMatchers.is("with control "));
    }
}

