/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.plugin.api.response;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ResultTest {
    @Test
    public void shouldCreateResponseWithErrorMessages() throws Exception {
        Result result = new Result().withErrorMessages("Error 1", "Error 2");
        MatcherAssert.assertThat(result.isSuccessful(), Matchers.is(false));
        MatcherAssert.assertThat(result.getMessages(), Matchers.contains("Error 1", "Error 2"));
    }

    @Test
    public void shouldDefaultResponseAsSuccess() throws Exception {
        Result result = new Result();
        MatcherAssert.assertThat(result.isSuccessful(), Matchers.is(true));
    }

    @Test
    public void shouldResponseWithSuccessMessages() throws Exception {
        Result result = new Result().withSuccessMessages("Success", "Pass");
        MatcherAssert.assertThat(result.isSuccessful(), Matchers.is(true));
        MatcherAssert.assertThat(result.getMessages(), Matchers.contains("Success", "Pass"));
    }

    @Test
    public void shouldReturnMessagesForDisplay() throws Exception {
        Result result = new Result().withSuccessMessages("Success", "Pass", "Firstpass");
        String messagesForDisplay = result.getMessagesForDisplay();
        MatcherAssert.assertThat(messagesForDisplay, Matchers.is("Success\nPass\nFirstpass"));
    }

    @Test
    public void shouldReturnMessagesForDisplayWithEmptyMessages() throws Exception {
        Result result = new Result().withSuccessMessages();
        String messagesForDisplay = result.getMessagesForDisplay();
        MatcherAssert.assertThat(messagesForDisplay, Matchers.is(""));
    }
}

