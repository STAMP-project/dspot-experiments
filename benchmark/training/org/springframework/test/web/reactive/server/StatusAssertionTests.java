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
package org.springframework.test.web.reactive.server;


import HttpStatus.BAD_REQUEST;
import HttpStatus.CONFLICT;
import HttpStatus.CONTINUE;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import HttpStatus.PERMANENT_REDIRECT;
import HttpStatus.REQUEST_TIMEOUT;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link StatusAssertions}.
 *
 * @author Rossen Stoyanchev
 */
public class StatusAssertionTests {
    @Test
    public void isEqualTo() {
        StatusAssertions assertions = statusAssertions(CONFLICT);
        // Success
        assertions.isEqualTo(CONFLICT);
        assertions.isEqualTo(409);
        try {
            assertions.isEqualTo(REQUEST_TIMEOUT);
            Assert.fail("Wrong status expected");
        } catch (AssertionError error) {
            // Expected
        }
        try {
            assertions.isEqualTo(408);
            Assert.fail("Wrong status value expected");
        } catch (AssertionError error) {
            // Expected
        }
    }

    @Test
    public void reasonEquals() {
        StatusAssertions assertions = statusAssertions(CONFLICT);
        // Success
        assertions.reasonEquals("Conflict");
        try {
            assertions.reasonEquals("Request Timeout");
            Assert.fail("Wrong reason expected");
        } catch (AssertionError error) {
            // Expected
        }
    }

    @Test
    public void statusSerius1xx() {
        StatusAssertions assertions = statusAssertions(CONTINUE);
        // Success
        assertions.is1xxInformational();
        try {
            assertions.is2xxSuccessful();
            Assert.fail("Wrong series expected");
        } catch (AssertionError error) {
            // Expected
        }
    }

    @Test
    public void statusSerius2xx() {
        StatusAssertions assertions = statusAssertions(OK);
        // Success
        assertions.is2xxSuccessful();
        try {
            assertions.is5xxServerError();
            Assert.fail("Wrong series expected");
        } catch (AssertionError error) {
            // Expected
        }
    }

    @Test
    public void statusSerius3xx() {
        StatusAssertions assertions = statusAssertions(PERMANENT_REDIRECT);
        // Success
        assertions.is3xxRedirection();
        try {
            assertions.is2xxSuccessful();
            Assert.fail("Wrong series expected");
        } catch (AssertionError error) {
            // Expected
        }
    }

    @Test
    public void statusSerius4xx() {
        StatusAssertions assertions = statusAssertions(BAD_REQUEST);
        // Success
        assertions.is4xxClientError();
        try {
            assertions.is2xxSuccessful();
            Assert.fail("Wrong series expected");
        } catch (AssertionError error) {
            // Expected
        }
    }

    @Test
    public void statusSerius5xx() {
        StatusAssertions assertions = statusAssertions(INTERNAL_SERVER_ERROR);
        // Success
        assertions.is5xxServerError();
        try {
            assertions.is2xxSuccessful();
            Assert.fail("Wrong series expected");
        } catch (AssertionError error) {
            // Expected
        }
    }

    @Test
    public void matches() {
        StatusAssertions assertions = statusAssertions(CONFLICT);
        // Success
        assertions.value(equalTo(409));
        assertions.value(greaterThan(400));
        try {
            assertions.value(equalTo(200));
            Assert.fail("Wrong status expected");
        } catch (AssertionError error) {
            // Expected
        }
    }
}

