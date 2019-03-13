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
package org.assertj.core.api.date;


import java.text.SimpleDateFormat;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.DateAssert;
import org.assertj.core.api.DateAssertBaseTest;
import org.junit.jupiter.api.Test;


/**
 * Abstract class that factorize DateAssert tests with a date arg (either Date or String based).
 * <p/>
 * For the most part, date assertion tests are (whatever the concrete date assertion method invoked is) :
 * <ul>
 * <li>successful assertion test with a date</li>
 * <li>successful assertion test with a date as string following default date format</li>
 * <li>successful assertion test with a date as string following custom date format</li>
 * <li>failed assertion test when date as string does not follow the expected date format</li>
 * <li>checking that DateAssert instance used for assertions is returned to allow fluent assertions chaining</li>
 * </ul>
 * <p/>
 * Subclasses are expected to define the invoked assertion method.
 *
 * @author Joel Costigliola
 */
public abstract class AbstractDateAssertWithDateArg_Test extends DateAssertBaseTest {
    protected Date otherDate;

    protected String dateAsStringWithDefaultFormat;

    protected String dateAsStringWithCustomFormat;

    protected String dateAsStringWithBadFormat;

    protected SimpleDateFormat customDateFormat;

    @Test
    public void should_verify_assertion_with_date_arg() {
        assertionInvocationWithDateArg();
        verifyAssertionInvocation(otherDate);
    }

    @Test
    public void should_verify_assertion_with_date_arg_string_with_default_format() {
        assertionInvocationWithStringArg(dateAsStringWithDefaultFormat);
        verifyAssertionInvocation(parse(dateAsStringWithDefaultFormat));
    }

    @Test
    public void should_verify_assertion_with_date_arg_string_following_custom_format() {
        assertions.withDateFormat(customDateFormat);
        assertionInvocationWithStringArg(dateAsStringWithCustomFormat);
        verifyAssertionInvocation(parse(dateAsStringWithCustomFormat));
        assertions.withDefaultDateFormatsOnly();
    }

    @Test
    public void should_fail_because_date_string_representation_does_not_follow_expected_format() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertionInvocationWithStringArg(dateAsStringWithBadFormat)).withMessage(String.format(((((((("Failed to parse " + (dateAsStringWithBadFormat)) + " with any of these date formats:%n") + "   [yyyy-MM-dd'T'HH:mm:ss.SSS,%n") + "    yyyy-MM-dd HH:mm:ss.SSS,%n") + "    yyyy-MM-dd'T'HH:mm:ssX,%n") + "    yyyy-MM-dd'T'HH:mm:ss,%n") + "    yyyy-MM-dd]")));
    }

    @Test
    public void should_return_this() {
        DateAssert returned = assertionInvocationWithDateArg();
        Assertions.assertThat(returned).isSameAs(assertions);
    }
}

