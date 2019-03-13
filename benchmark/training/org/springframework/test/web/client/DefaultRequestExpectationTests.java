/**
 * Copyright 2002-2018 the original author or authors.
 *
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
 */
package org.springframework.test.web.client;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;


/**
 * Unit tests for {@link DefaultRequestExpectation}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultRequestExpectationTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void match() throws Exception {
        RequestExpectation expectation = new DefaultRequestExpectation(ExpectedCount.once(), MockRestRequestMatchers.requestTo("/foo"));
        expectation.match(createRequest(GET, "/foo"));
    }

    @Test
    public void matchWithFailedExpectation() throws Exception {
        RequestExpectation expectation = new DefaultRequestExpectation(ExpectedCount.once(), MockRestRequestMatchers.requestTo("/foo"));
        expectation.andExpect(MockRestRequestMatchers.method(POST));
        this.thrown.expectMessage("Unexpected HttpMethod expected:<POST> but was:<GET>");
        expectation.match(createRequest(GET, "/foo"));
    }

    @Test
    public void hasRemainingCount() {
        RequestExpectation expectation = new DefaultRequestExpectation(ExpectedCount.twice(), MockRestRequestMatchers.requestTo("/foo"));
        expectation.andRespond(MockRestResponseCreators.withSuccess());
        expectation.incrementAndValidate();
        Assert.assertTrue(expectation.hasRemainingCount());
        expectation.incrementAndValidate();
        TestCase.assertFalse(expectation.hasRemainingCount());
    }

    @Test
    public void isSatisfied() {
        RequestExpectation expectation = new DefaultRequestExpectation(ExpectedCount.twice(), MockRestRequestMatchers.requestTo("/foo"));
        expectation.andRespond(MockRestResponseCreators.withSuccess());
        expectation.incrementAndValidate();
        TestCase.assertFalse(expectation.isSatisfied());
        expectation.incrementAndValidate();
        Assert.assertTrue(expectation.isSatisfied());
    }
}

