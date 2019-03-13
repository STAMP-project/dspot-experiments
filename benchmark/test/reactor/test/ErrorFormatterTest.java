/**
 * Copyright (c) 2011-2018 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.test;


import DefaultStepVerifierBuilder.Event;
import java.time.Duration;
import org.junit.Test;


public class ErrorFormatterTest {
    @Test
    public void noScenarioEmpty() {
        assertThat(new ErrorFormatter("").scenarioPrefix).isNotNull().isEmpty();
    }

    @Test
    public void nullScenarioEmpty() {
        assertThat(new ErrorFormatter(null).scenarioPrefix).isNotNull().isEmpty();
    }

    @Test
    public void givenScenarioWrapped() {
        assertThat(new ErrorFormatter("foo").scenarioPrefix).isEqualTo("[foo] ");
    }

    // === Tests with an empty scenario name ===
    static final ErrorFormatter noScenario = new ErrorFormatter("");

    @Test
    public void noScenarioFailNullEventNoArgs() {
        assertThat(ErrorFormatterTest.noScenario.fail(null, "details")).hasMessage("expectation failed (details)");
    }

    @Test
    public void noScenarioFailNoDescriptionNoArgs() {
        DefaultStepVerifierBuilder.Event event = new DefaultStepVerifierBuilder.NoEvent(Duration.ofMillis(5), "");
        assertThat(ErrorFormatterTest.noScenario.fail(event, "details")).hasMessage("expectation failed (details)");
    }

    @Test
    public void noScenarioFailDescriptionNoArgs() {
        DefaultStepVerifierBuilder.Event event = new DefaultStepVerifierBuilder.NoEvent(Duration.ofMillis(5), "eventDescription");
        assertThat(ErrorFormatterTest.noScenario.fail(event, "details")).hasMessage("expectation \"eventDescription\" failed (details)");
    }

    @Test
    public void noScenarioFailNullEventHasArgs() {
        assertThat(ErrorFormatterTest.noScenario.fail(null, "details = %s", "bar")).hasMessage("expectation failed (details = bar)");
    }

    @Test
    public void noScenarioFailNoDescriptionHasArgs() {
        DefaultStepVerifierBuilder.Event event = new DefaultStepVerifierBuilder.NoEvent(Duration.ofMillis(5), "");
        assertThat(ErrorFormatterTest.noScenario.fail(event, "details = %s", "bar")).hasMessage("expectation failed (details = bar)");
    }

    @Test
    public void noScenarioFailDescriptionHasArgs() {
        DefaultStepVerifierBuilder.Event event = new DefaultStepVerifierBuilder.NoEvent(Duration.ofMillis(5), "eventDescription");
        assertThat(ErrorFormatterTest.noScenario.fail(event, "details = %s", "bar")).hasMessage("expectation \"eventDescription\" failed (details = bar)");
    }

    @Test
    public void noScenarioFailOptional() {
        assertThat(ErrorFormatterTest.noScenario.failOptional(null, "foo")).hasValueSatisfying(( ae) -> assertThat(ae).hasMessage("expectation failed (foo)"));
    }

    @Test
    public void noScenarioFailPrefixNoArgs() {
        assertThat(ErrorFormatterTest.noScenario.failPrefix("firstPart", "secondPart")).hasMessage("firstPartsecondPart)");// note the prefix doesn't have an opening parenthesis

    }

    @Test
    public void noScenarioFailPrefixHasArgs() {
        assertThat(ErrorFormatterTest.noScenario.failPrefix("firstPart(", "secondPart = %s", "foo")).hasMessage("firstPart(secondPart = foo)");
    }

    @Test
    public void noScenarioAssertionError() {
        assertThat(ErrorFormatterTest.noScenario.assertionError("plain")).hasMessage("plain").hasNoCause();
    }

    @Test
    public void noScenarioAssertionErrorWithCause() {
        Throwable cause = new IllegalArgumentException("boom");
        assertThat(ErrorFormatterTest.noScenario.assertionError("plain", cause)).hasMessage("plain").hasCause(cause);
    }

    @Test
    public void noScenarioAssertionErrorWithNullCause() {
        assertThat(ErrorFormatterTest.noScenario.assertionError("plain", null)).hasMessage("plain").hasNoCause();
    }

    @Test
    public void noScenarioIllegalStateException() {
        assertThat(ErrorFormatterTest.noScenario.<Throwable>error(IllegalStateException::new, "plain")).isInstanceOf(IllegalStateException.class).hasMessage("plain");
    }

    // === Tests with a scenario name ===
    static final ErrorFormatter withScenario = new ErrorFormatter("ErrorFormatterTest");

    @Test
    public void withScenarioFailNullEventNoArgs() {
        assertThat(ErrorFormatterTest.withScenario.fail(null, "details")).hasMessage("[ErrorFormatterTest] expectation failed (details)");
    }

    @Test
    public void withScenarioFailNoDescriptionNoArgs() {
        DefaultStepVerifierBuilder.Event event = new DefaultStepVerifierBuilder.NoEvent(Duration.ofMillis(5), "");
        assertThat(ErrorFormatterTest.withScenario.fail(event, "details")).hasMessage("[ErrorFormatterTest] expectation failed (details)");
    }

    @Test
    public void withScenarioFailDescriptionNoArgs() {
        DefaultStepVerifierBuilder.Event event = new DefaultStepVerifierBuilder.NoEvent(Duration.ofMillis(5), "eventDescription");
        assertThat(ErrorFormatterTest.withScenario.fail(event, "details")).hasMessage("[ErrorFormatterTest] expectation \"eventDescription\" failed (details)");
    }

    @Test
    public void withScenarioFailNullEventHasArgs() {
        assertThat(ErrorFormatterTest.withScenario.fail(null, "details = %s", "bar")).hasMessage("[ErrorFormatterTest] expectation failed (details = bar)");
    }

    @Test
    public void withScenarioFailNoDescriptionHasArgs() {
        DefaultStepVerifierBuilder.Event event = new DefaultStepVerifierBuilder.NoEvent(Duration.ofMillis(5), "");
        assertThat(ErrorFormatterTest.withScenario.fail(event, "details = %s", "bar")).hasMessage("[ErrorFormatterTest] expectation failed (details = bar)");
    }

    @Test
    public void withScenarioFailDescriptionHasArgs() {
        DefaultStepVerifierBuilder.Event event = new DefaultStepVerifierBuilder.NoEvent(Duration.ofMillis(5), "eventDescription");
        assertThat(ErrorFormatterTest.withScenario.fail(event, "details = %s", "bar")).hasMessage("[ErrorFormatterTest] expectation \"eventDescription\" failed (details = bar)");
    }

    @Test
    public void withScenarioFailOptional() {
        assertThat(ErrorFormatterTest.withScenario.failOptional(null, "foo")).hasValueSatisfying(( ae) -> assertThat(ae).hasMessage("[ErrorFormatterTest] expectation failed (foo)"));
    }

    @Test
    public void withScenarioFailPrefixNoArgs() {
        assertThat(ErrorFormatterTest.withScenario.failPrefix("firstPart", "secondPart")).hasMessage("[ErrorFormatterTest] firstPartsecondPart)");// note the prefix doesn't have an opening parenthesis

    }

    @Test
    public void withScenarioFailPrefixHasArgs() {
        assertThat(ErrorFormatterTest.withScenario.failPrefix("firstPart(", "secondPart = %s", "foo")).hasMessage("[ErrorFormatterTest] firstPart(secondPart = foo)");
    }

    @Test
    public void withScenarioAssertionError() {
        assertThat(ErrorFormatterTest.withScenario.assertionError("plain")).hasMessage("[ErrorFormatterTest] plain").hasNoCause();
    }

    @Test
    public void withScenarioAssertionErrorWithCause() {
        Throwable cause = new IllegalArgumentException("boom");
        assertThat(ErrorFormatterTest.withScenario.assertionError("plain", cause)).hasMessage("[ErrorFormatterTest] plain").hasCause(cause);
    }

    @Test
    public void withScenarioAssertionErrorWithNullCause() {
        assertThat(ErrorFormatterTest.withScenario.assertionError("plain", null)).hasMessage("[ErrorFormatterTest] plain").hasNoCause();
    }

    @Test
    public void withScenarioIllegalStateException() {
        assertThat(ErrorFormatterTest.withScenario.<Throwable>error(IllegalStateException::new, "plain")).isInstanceOf(IllegalStateException.class).hasMessage("[ErrorFormatterTest] plain");
    }
}

